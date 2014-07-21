package it.mathchallenger.server.controls;

import it.mathchallenger.server.entities.Account;
import it.mathchallenger.server.storage.DBConnectionPool;
import it.mathchallenger.server.tda.NodeQueue;
import it.mathchallenger.server.tda.Queue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class GestionePartite {
	private static GestionePartite manager;

	public static GestionePartite getInstance() {
		if (manager == null)
			manager = new GestionePartite();
		return manager;
	}

	private HashMap<String, AccountWrapper> user_map;
	private ArrayList<Bot>	 bot_list;
	private ArrayList<Bot>	 b_copy;
	private Queue<Bot>		 ordine_bot;

	private GestionePartite() {
		user_map=new HashMap<String, AccountWrapper>();
		rand = new Random(System.currentTimeMillis());
		
		ordine_bot = new NodeQueue<Bot>();
		bot_list = new ArrayList<Bot>(20);
		loadBot();

		b_copy = new ArrayList<Bot>(bot_list.size());
		for (int i = 0; i < bot_list.size(); i++) {
			b_copy.add(bot_list.get(i));
		}
		avviaBot();
		generaOrdineBot();
	}

	private void generaOrdineBot() {
		while (!b_copy.isEmpty()) {
			Bot b = b_copy.remove(rand.nextInt(b_copy.size()));
			ordine_bot.enqueue(b);
		}
	}

	private synchronized Bot getBot() {
		if (ordine_bot.isEmpty())
			generaOrdineBot();
		Bot b = ordine_bot.dequeue();
		b_copy.add(b);
		return b;
	}

	public synchronized void entraUtente(Account acc) {		
		AccountWrapper aw=user_map.get(acc.getUsername());
		if(aw==null){
			aw=new AccountWrapper(acc);
			user_map.put(acc.getUsername(), aw);
		}
		aw.increaseCount();
	}

	public synchronized void esceUtente(Account acc) {
		AccountWrapper a=user_map.get(acc.getUsername());
		if(a!=null){
			a.decreaseCount();
			if(a.getCount()<=0)
				user_map.remove(acc.getUsername());
		}
	}

	public Bot getBotByID(int id) {
		for (int i = 0; i < bot_list.size(); i++) {
			if (bot_list.get(i).getID() == id)
				return bot_list.get(i);
		}
		return null;
	}

	public Bot getBotRandom() {
		return getBot();
	}

	private static Random rand;

	public Account accountRandom(int id_ricercante, ArrayList<Account> last) {
		if (user_map.size() <= 1) { // l'utente loggato è l'utente che cerca
			return getBotRandom();
		}
		else {
			int prove = 0;
			Object[] accs;
			synchronized (user_map) {
				accs=user_map.values().toArray();
			}
			while (prove < 5) {
				int acc_ind = rand.nextInt(accs.length);
				if(acc_ind<accs.length){
    				Account acc = ((AccountWrapper) accs[acc_ind]).getAccount();
    				if (acc.getID() != id_ricercante && !isInLastUsers(acc.getID(), last)){
    					accs=null;
    					return acc;
    				}
    			}
				prove++;
			}
			accs=null;
			return getBotRandom();
		}
	}
	private boolean isInLastUsers(int id, ArrayList<Account> list){
		if(list==null)
			return false;
		for(int i=0;i<list.size();i++)
			if(list.get(i).getID()==id)
				return true;
		return false;
	}

	private void loadBot() {
		String query = "SELECT * FROM bot";
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			con = DBConnectionPool.getConnection();
			st = con.prepareStatement(query);
			rs = st.executeQuery();
			while (rs.next()) {
				int id = rs.getInt("id");
				String nome = rs.getString("nome");
				int percent = rs.getInt("percentuale_successo");
				Bot b = new Bot(nome, percent, id);
				bot_list.add(b);
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
	}

	private void avviaBot() {
		for (int i = 0; i < bot_list.size(); i++) {
			Bot bot = bot_list.get(i);
			Thread t_bot = new Thread(bot);
			t_bot.start();
		}
	}
	public ArrayList<String> listUsers(){
		ArrayList<String> l=new ArrayList<String>(user_map.size());
		Object[] la;
		synchronized (user_map) {
			la=user_map.values().toArray();
		}
		for(int i=0;i<la.length;i++){
			AccountWrapper a=(AccountWrapper) la[i];
			l.add(a.getAccount().getUsername()+","+a.getAccount().getID()+","+a.getCount());
		}
		la=null;
		
		return l;
	}
	public boolean kickUser(String utente){
		AccountWrapper acc=user_map.remove(utente);
		return acc!=null;
	}
}
class AccountWrapper {
	private Account acc;
	private int count;
	
	public AccountWrapper(Account a){
		acc=a;
		count=0;
	}
	public int getCount(){
		return count;
	}
	public Account getAccount(){
		return acc;
	}
	public void increaseCount(){
		count++;
	}
	public void decreaseCount(){
		count--;
	}
}