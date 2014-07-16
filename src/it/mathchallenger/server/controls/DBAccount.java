package it.mathchallenger.server.controls;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Logger;

import it.mathchallenger.server.communication.mail.MailSender;
import it.mathchallenger.server.entities.Account;
import it.mathchallenger.server.storage.DBConnectionPool;
import it.mathchallenger.server.storage.LoggerManager;

public class DBAccount {
	private static DBAccount manager;
	private static Logger	logger;

	private final static int AUTHCODE_SIZE = 64;
	private final static int PASSWORD_SIZE = 8;

	public static synchronized DBAccount getInstance() {
		if (manager == null) {
			manager = new DBAccount();
		}
		return manager;
	}

	private DBAccount() {
		logger = LoggerManager.newLogger(getClass().getName());
	}

	public Account login(String username, String password) {
		String password_hash = generaHash(password);
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		Account acc = null;
		try {
			String query = "SELECT * FROM account WHERE username=\"" + username + "\" AND password=\"" + password_hash + "\"";
			con = DBConnectionPool.getConnection();
			st = con.prepareStatement(query);
			rs = st.executeQuery();
			if (rs.next()) {
				acc = new Account();
				acc.setID(rs.getInt("id"));
				acc.setUsername(username);
				acc.setAuthCode(generaAuthCode(AUTHCODE_SIZE));
				acc.setEmail(rs.getString("email"));
				salvaAuthCode(acc.getID(), acc.getAuthCode());
			}
		}
		catch (SQLException e) {
			logger.severe(e.getMessage());
		}
		finally {
			try {
				if (rs != null)
					rs.close();
				if (st != null)
					st.close();
				if (con != null)
					DBConnectionPool.releaseConnection(con);
			}
			catch (SQLException e) {}
		}
		return acc;
	}

	private final static String letters = "0123456789?-_+AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvXxYyWwZz";
	private final static Random random  = new Random(System.currentTimeMillis());

	private String generaAuthCode(int size) {
		StringBuilder str = new StringBuilder(size);
		int size_letters = letters.length();
		for (int i = 0; i < size; i++)
			str.append(letters.charAt(random.nextInt(size_letters)));
		return str.toString();
	}

	private String generaHash(String pass) {
		try {
			MessageDigest msg_dig = MessageDigest.getInstance("SHA-256");
			byte[] hash = msg_dig.digest(pass.getBytes());

			StringBuilder hexString = new StringBuilder();

			for (int i = 0; i < hash.length; i++) {
				String hex = Integer.toHexString(0xff & hash[i]);
				if (hex.length() == 1)
					hexString.append('0');
				hexString.append(hex);
			}
			return hexString.toString();
		}
		catch (NoSuchAlgorithmException e) {
			logger.severe("Algoritmo SHA-256 non trovato");
			logger.severe(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	private boolean salvaAuthCode(int id_utente, String authcode) {
		Connection con = null;
		PreparedStatement st = null;
		try {
			con = DBConnectionPool.getConnection();
			String query = "UPDATE account SET authcode=\"" + authcode + "\" WHERE id=" + id_utente;
			st = con.prepareStatement(query);
			if (st.executeUpdate() > 0) {
				return true;
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (st != null)
					st.close();
				if (con != null)
					DBConnectionPool.releaseConnection(con);
			}
			catch (SQLException e) {}
		}
		return false;
	}

	public Account login(int id_utente, String authcode) {
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		Account acc = null;
		try {
			String query = "SELECT * FROM account WHERE id=" + id_utente + " AND authcode=\"" + authcode + "\"";
			con = DBConnectionPool.getConnection();
			st = con.prepareStatement(query);
			rs = st.executeQuery();
			if (rs.next()) {
				acc = new Account();
				String user = rs.getString("username");
				String email = rs.getString("email");
				acc.setUsername(user);
				acc.setAuthCode(authcode);
				acc.setID(id_utente);
				acc.setEmail(email);
			}
		}
		catch (SQLException e) {
			logger.severe(e.getMessage());
		}
		finally {
			try {
				if (rs != null)
					rs.close();
				if (st != null)
					st.close();
				if (con != null)
					DBConnectionPool.releaseConnection(con);
			}
			catch (SQLException e) {}
		}
		return acc;
	}

	public boolean logout(String username, String authcode) {
		Connection con = null;
		PreparedStatement st = null;
		try {
			con = DBConnectionPool.getConnection();
			String query = "UPDATE account SET authcode='' WHERE username=\"" + username + "\" AND authcode='" + authcode + "'";
			st = con.prepareStatement(query);
			if (st.executeUpdate() > 0) {
				return true;
			}
		}
		catch (SQLException e) {}
		finally {
			try {
				if (st != null)
					st.close();
				if (con != null)
					DBConnectionPool.releaseConnection(con);
			}
			catch (SQLException e) {}
		}
		return false;
	}

	public boolean logout(Account acc) {
		return logout(acc.getID(), acc.getAuthCode());
	}

	public boolean logout(int id_utente, String authcode) {
		Connection con = null;
		PreparedStatement st = null;
		try {
			con = DBConnectionPool.getConnection();
			String query = "UPDATE account SET authcode='' WHERE id=" + id_utente + " AND authcode='" + authcode + "'";
			st = con.prepareStatement(query);
			if (st.executeUpdate() > 0) {
				return true;
			}
		}
		catch (SQLException e) {}
		finally {
			try {
				if (st != null)
					st.close();
				if (con != null)
					DBConnectionPool.releaseConnection(con);
			}
			catch (SQLException e) {}
		}
		return false;
	}

	public Account registra(String username, String password, String email) {
		Connection con = null;
		PreparedStatement st = null;
		Account acc = null;
		try {
			con = DBConnectionPool.getConnection();
			String password_hash = generaHash(password);
			String query = "INSERT INTO account(username, password, email) VALUES(\"" + username + "\",\"" + password_hash + "\",\"" + email + "\")";
			st = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

			if (st.executeUpdate() > 0) {
				ResultSet rs = st.getGeneratedKeys();
				rs.next();
				int id = rs.getInt(1);
				DBStatistiche.getInstance().inserisciStatistica(id);
				rs.close();
				String auth = generaAuthCode(AUTHCODE_SIZE);
				acc = new Account(username, auth);
				acc.setID(id);
				acc.setEmail(email);
				salvaAuthCode(id, auth);
				MailSender.newUserMail(email, username, password);
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
			logger.severe(e.getMessage());
		}
		finally {
			try {
				if (st != null)
					st.close();
				if (con != null)
					DBConnectionPool.releaseConnection(con);
			}
			catch (SQLException e) {}
		}
		return acc;
	}

	public boolean changePassword(Account acc, String newPass) {
		Connection con = null;
		PreparedStatement st = null;
		try {
			con = DBConnectionPool.getConnection();
			String pass = generaHash(newPass);
			String query = "UPDATE account SET password=\"" + pass + "\" WHERE id=" + acc.getID();
			st = con.prepareStatement(query);
			if (st.executeUpdate() > 0)
				return true;
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (st != null)
					st.close();
				if (con != null)
					DBConnectionPool.releaseConnection(con);
			}
			catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	public boolean resetPasswordByUsername(String username) {
		Account acc = getAccountByUsername(username);
		if (acc != null) {
			String pass = generaRandomPass();
			String pass_hash = generaHash(pass);
			if (salvaPassword(acc.getID(), pass_hash)) {
				MailSender.newPasswordMail(acc.getEmail(), pass);
				return true;
			}
			else {
				return false;
			}
		}
		else
			return false;
	}

	private String generaRandomPass() {
		return generaAuthCode(PASSWORD_SIZE);
	}

	public Account getAccountByID(int id) {
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		Account acc = null;
		try {
			con = DBConnectionPool.getConnection();
			String query = "SELECT * FROM account WHERE id=" + id;
			st = con.prepareStatement(query);
			rs = st.executeQuery();
			if (rs.next()) {
				acc = new Account();
				String username = rs.getString("username");
				String auth = rs.getString("authcode");
				String email = rs.getString("email");
				acc.setUsername(username);
				acc.setID(id);
				acc.setEmail(email);
				acc.setAuthCode(auth);
			}
		}
		catch (SQLException e) {}
		finally {
			try {
				if (rs != null)
					rs.close();
				if (st != null)
					st.close();
				if (con != null)
					DBConnectionPool.releaseConnection(con);
			}
			catch (SQLException e) {}
		}
		return acc;
	}

	public ArrayList<Account> searchUser(Account account_cercante, String user) {
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		ArrayList<Account> acc = new ArrayList<Account>();
		try {
			con = DBConnectionPool.getConnection();
			String query = "SELECT * FROM account WHERE username LIKE \"%" + user + "%\" AND id>0 AND id!=" + account_cercante.getID() + " ORDER BY CASE WHEN username LIKE \"" + user + "%\" THEN 1 WHEN username LIKE \"%" + user + "\" THEN 3 ELSE 2 END";
			st = con.prepareStatement(query);
			rs = st.executeQuery();
			while (rs.next()) {
				Account a = new Account();
				String auth = rs.getString("authcode");
				String email = rs.getString("email");
				int id = rs.getInt("id");
				String username = rs.getString("username");
				a.setUsername(username);
				a.setID(id);
				a.setEmail(email);
				a.setAuthCode(auth);
				acc.add(a);
			}
			acc.trimToSize();
			return acc;
		}
		catch (SQLException e) {}
		finally {
			try {
				if (rs != null)
					rs.close();
				if (st != null)
					st.close();
				if (con != null)
					DBConnectionPool.releaseConnection(con);
			}
			catch (SQLException e) {}
		}
		return acc;
	}

	private Account getAccountByUsername(String username) {
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		Account acc = null;
		try {
			con = DBConnectionPool.getConnection();
			String query = "SELECT * FROM account WHERE username=\"" + username + "\"";
			st = con.prepareStatement(query);
			rs = st.executeQuery();
			if (rs.next()) {
				acc = new Account();
				String auth = rs.getString("authcode");
				String email = rs.getString("email");
				int id = rs.getInt("id");
				acc.setUsername(username);
				acc.setID(id);
				acc.setEmail(email);
				acc.setAuthCode(auth);
			}
		}
		catch (SQLException e) {}
		finally {
			try {
				if (rs != null)
					rs.close();
				if (st != null)
					st.close();
				if (con != null)
					DBConnectionPool.releaseConnection(con);
			}
			catch (SQLException e) {}
		}
		return acc;
	}

	public Account getAccount(String username, String authcode) {
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		Account acc = null;
		try {
			con = DBConnectionPool.getConnection();
			String query = "SELECT * FROM account WHERE username=\"" + username + "\" AND authcode=\"" + authcode + "\"";
			st = con.prepareStatement(query);
			rs = st.executeQuery();
			if (rs.next()) {
				acc = new Account();
				String email = rs.getString("email");
				int id = rs.getInt("id");
				acc.setUsername(username);
				acc.setID(id);
				acc.setEmail(email);
				acc.setAuthCode(authcode);
			}
		}
		catch (SQLException e) {}
		finally {
			try {
				if (rs != null)
					rs.close();
				if (st != null)
					st.close();
				if (con != null)
					DBConnectionPool.releaseConnection(con);
			}
			catch (SQLException e) {}
		}
		return acc;
	}

	private boolean salvaPassword(int id, String password) {
		Connection con = null;
		PreparedStatement st = null;
		try {
			con = DBConnectionPool.getConnection();
			String query = "UPDATE account SET password=\"" + password + "\" WHERE id=" + id;
			st = con.prepareStatement(query);
			if (st.executeUpdate() > 0)
				return true;
			return false;
		}
		catch (SQLException e) {}
		finally {
			try {
				if (st != null)
					st.close();
				if (con != null)
					DBConnectionPool.releaseConnection(con);
			}
			catch (SQLException e) {}
		}
		return false;
	}

	public boolean isAccountExist(String username) {
		Account acc = getAccountByUsername(username);
		if (acc == null)
			return false;
		else
			return true;
	}

	public boolean addFriend(Account user, int id_amico) {
		Connection con = null;
		PreparedStatement st = null;
		try {
			con = DBConnectionPool.getConnection();
			String query = "INSERT INTO amico (id_utente, id_amico) VALUES(" + user.getID() + "," + id_amico + ")";
			st = con.prepareStatement(query);
			st.executeUpdate();
			return true;
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (st != null)
					st.close();
				if (con != null)
					DBConnectionPool.releaseConnection(con);
			}
			catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return true;
	}

	public boolean removeFriend(Account user, int id_amico) {
		Connection con = null;
		PreparedStatement st = null;
		try {
			con = DBConnectionPool.getConnection();
			String query = "DELETE FROM amico WHERE id_utente=" + user.getID() + " AND id_amico=" + id_amico;
			st = con.prepareStatement(query);
			st.executeUpdate();
			return true;
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (st != null)
					st.close();
				if (con != null)
					DBConnectionPool.releaseConnection(con);
			}
			catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return true;
	}

	public ArrayList<Account> getListaAmici(Account acc) {
		ArrayList<Account> amici = new ArrayList<Account>();
		Connection con = null;
		ResultSet rs = null;
		PreparedStatement st = null;
		try {
			con = DBConnectionPool.getConnection();
			String query = "SELECT * FROM amico WHERE id_utente=" + acc.getID();
			st = con.prepareStatement(query);
			rs = st.executeQuery();
			while (rs.next()) {
				int id_amico = rs.getInt("id_amico");
				Account a = getAccountByID(id_amico);
				amici.add(a);
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (rs != null)
					rs.close();
				if (st != null)
					st.close();
				if (con != null)
					DBConnectionPool.releaseConnection(con);
			}
			catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return amici;
	}
	
	public boolean cancellaAccount(int id){
		Connection con=null;
		PreparedStatement st=null;
		try {
			con=DBConnectionPool.getConnection();
			String query="DELETE FROM account WHERE id="+id;
			st=con.prepareStatement(query);
			return st.executeUpdate()>0;
		} 
		catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		finally {
			if(st!=null)
				try {
					st.close();
				} 
				catch (SQLException e) {
					e.printStackTrace();
				}
			if(con!=null)
				DBConnectionPool.releaseConnection(con);
		}
	}
	public boolean cancellaAccount(String u){
		Connection con=null;
		PreparedStatement st=null;
		try {
			con=DBConnectionPool.getConnection();
			String query="DELETE FROM account WHERE username="+u;
			st=con.prepareStatement(query);
			return st.executeUpdate()>0;
		} 
		catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		finally {
			if(st!=null)
				try {
					st.close();
				} 
				catch (SQLException e) {
					e.printStackTrace();
				}
			if(con!=null)
				DBConnectionPool.releaseConnection(con);
		}
	}

	public boolean registra(String user, String email) {
		Account a=registra(user, generaRandomPass(), email);
		if(a!=null)
			return true;
		else
			return false;
	}
}
