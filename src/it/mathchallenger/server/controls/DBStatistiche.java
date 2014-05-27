package it.mathchallenger.server.controls;

import it.mathchallenger.server.entities.Partita;
import it.mathchallenger.server.entities.Statistiche;
import it.mathchallenger.server.storage.DBConnectionPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class DBStatistiche {
	public final static int	  PUNTI_VITTORIA  = 3;
	public final static int	  PUNTI_PAREGGIO  = 1;
	public final static int	  PUNTI_SCONFITTA = 0;
	public final static int	  PUNTI_ABBANDONO = -2;
	private static DBStatistiche stats;

	private DBStatistiche() {}

	public static DBStatistiche getInstance() {
		if (stats == null)
			stats = new DBStatistiche();
		return stats;
	}

	public Statistiche getStatisticheByID(int id) {
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		Statistiche stat = null;

		try {
			con = DBConnectionPool.getConnection();
			String query = "SELECT * FROM statistiche WHERE id_utente=" + id;
			st = con.prepareStatement(query);
			rs = st.executeQuery();
			if (rs.next()) {
				stat = new Statistiche(id);
				stat.setAbbandonate(rs.getInt("abbandonate"));
				stat.setPareggi(rs.getInt("pareggiate"));
				stat.setPartite_giocate(rs.getInt("partite_giocate"));
				stat.setPunti(rs.getInt("punteggio"));
				stat.setSconfitte(rs.getInt("perse"));
				stat.setVittorie(rs.getInt("vinte"));
			}
			return stat;
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
		return stat;
	}

	public boolean aggiungiVittoriaAtUser(int id) {
		Connection con = null;
		PreparedStatement st = null;
		try {
			con = DBConnectionPool.getConnection();
			String query = "UPDATE statistiche SET vinte=vinte+1, partite_giocate=partite_giocate+1, punteggio=punteggio+" + PUNTI_VITTORIA + " WHERE id_utente=" + id;
			st = con.prepareStatement(query);
			if (st.executeUpdate() > 0)
				return true;
			return false;
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

	public boolean aggiungiSconfittaAtUser(int id) {
		Connection con = null;
		PreparedStatement st = null;
		try {
			con = DBConnectionPool.getConnection();
			String query = "UPDATE statistiche SET perse=perse+1, partite_giocate=partite_giocate+1, punteggio=punteggio+" + PUNTI_SCONFITTA + " WHERE id_utente=" + id;
			st = con.prepareStatement(query);
			if (st.executeUpdate() > 0)
				return true;
			return false;
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

	public boolean aggiungiPareggioAtUser(int id) {
		Connection con = null;
		PreparedStatement st = null;
		try {
			con = DBConnectionPool.getConnection();
			String query = "UPDATE statistiche SET pareggiate=pareggiate+1, partite_giocate=partite_giocate+1, punteggio=punteggio+" + PUNTI_PAREGGIO + " WHERE id_utente=" + id;
			st = con.prepareStatement(query);
			if (st.executeUpdate() > 0)
				return true;
			return false;
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

	public boolean aggiungiAbbandonoAtUser(int id) {
		Connection con = null;
		PreparedStatement st = null;
		try {
			con = DBConnectionPool.getConnection();
			String query = "UPDATE statistiche SET abbandonate=abbandonate+1, partite_giocate=partite_giocate+1, punteggio=punteggio+" + PUNTI_ABBANDONO + " WHERE id_utente=" + id;
			st = con.prepareStatement(query);
			if (st.executeUpdate() > 0)
				return true;
			return false;
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

	public void ricalcolaPunteggio(ArrayList<Partita> partite) {
		//TODO
	}

	public boolean inserisciStatistica(int id) {
		Connection con = null;
		PreparedStatement st = null;
		try {
			con = DBConnectionPool.getConnection();
			String query = "INSERT INTO statistiche (id_utente) VALUES (" + id + ")";
			st = con.prepareStatement(query);
			if (st.executeUpdate() > 0)
				return true;
			return false;
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
}
