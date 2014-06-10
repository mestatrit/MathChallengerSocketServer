package it.mathchallenger.server.controls;

import it.mathchallenger.server.entities.Domanda;
import it.mathchallenger.server.entities.Partita;
import it.mathchallenger.server.storage.DBConnectionPool;
import it.mathchallenger.server.storage.LoggerManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.logging.Logger;

public class DBPartita {
	private static DBPartita manager;
	private static Logger	logger;
	private Random rand;

	public static synchronized DBPartita getInstance() {
		if (manager == null) {
			manager = new DBPartita();
		}
		return manager;
	}

	private DBPartita() {
		logger = LoggerManager.newLogger(getClass().getName());
		dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		rand=new Random(System.currentTimeMillis());
	}

	public Partita creaPartita(int id1, Integer id2) {
		Partita partita = new Partita();
		partita.setIDUtente1(id1);
		partita.setIDUtente2(id2);
		partita.setInizioPartita(System.currentTimeMillis());
		partita.setStatoPartita(Partita.CREATA);
		for(int i=0;i<6;i++){
			int ra=rand.nextInt(10);
			switch(ra){
				case 0:
				case 1:
				case 2:
				case 3://40%
					partita.aggiungiDomanda(generaDomandaFacile());
					break;
				case 4:
				case 5:
				case 6://30%
					partita.aggiungiDomanda(generaDomandaMedia());
					break;
				case 7:
				case 8://20%
					partita.aggiungiDomanda(generaDomandaDifficile());
					break;
				case 9://10%
					partita.aggiungiDomanda(generaDomandaDifficilissima());
					break;
				default:
					partita.aggiungiDomanda(generaDomandaFacile());
					break;
			}
		}
		inserisciPartita(partita);
		return partita;
	}

	public Partita getPartitaByID(int id) {
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		Partita partita = null;
		try {
			con = DBConnectionPool.getConnection();
			String query = "SELECT * FROM partite WHERE id=" + id;
			st = con.prepareStatement(query);
			rs = st.executeQuery();
			if (rs.next()) {
				partita = getPartitaFromResultSet(rs);
				verificaStatoPartita(partita);
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
		return partita;
	}

	private Partita getPartitaFromResultSet(ResultSet rs) throws SQLException {
		Partita partita = new Partita();
		int id_partita = rs.getInt("id");
		int id1 = rs.getInt("id_utente_1");
		int id2 = rs.getInt("id_utente_2");
		int stato_partita = rs.getInt("stato_partita");
		long inizio = rs.getTimestamp("inizio").getTime();
		partita = new Partita();
		partita.setIDPartita(id_partita);
		partita.setIDUtente1(id1);
		partita.setIDUtente2(id2);
		partita.setInizioPartita(inizio);
		partita.setStatoPartita(stato_partita);
		Domanda d1 = new Domanda();
		d1.setDomanda(rs.getString("domanda1"));
		d1.setRispostaErrata(1, rs.getFloat("risposta1_alternativa1"));
		d1.setRispostaErrata(2, rs.getFloat("risposta1_alternativa2"));
		d1.setRispostaErrata(3, rs.getFloat("risposta1_alternativa3"));
		d1.setRispostaEsatta(rs.getFloat("risposta1_esatta"));
		d1.setUser1Risposta(rs.getInt("utente1_risposta1"));
		d1.setUser2Risposta(rs.getInt("utente2_risposta1"));
		d1.setNumeroDomanda(1);
		partita.aggiungiDomanda(d1);
		Domanda d2 = new Domanda();
		d2.setDomanda(rs.getString("domanda2"));
		d2.setRispostaErrata(1, rs.getFloat("risposta2_alternativa1"));
		d2.setRispostaErrata(2, rs.getFloat("risposta2_alternativa2"));
		d2.setRispostaErrata(3, rs.getFloat("risposta2_alternativa3"));
		d2.setRispostaEsatta(rs.getFloat("risposta2_esatta"));
		d2.setUser1Risposta(rs.getInt("utente1_risposta2"));
		d2.setUser2Risposta(rs.getInt("utente2_risposta2"));
		d2.setNumeroDomanda(2);
		partita.aggiungiDomanda(d2);
		Domanda d3 = new Domanda();
		d3.setDomanda(rs.getString("domanda3"));
		d3.setRispostaErrata(1, rs.getFloat("risposta3_alternativa1"));
		d3.setRispostaErrata(2, rs.getFloat("risposta3_alternativa2"));
		d3.setRispostaErrata(3, rs.getFloat("risposta3_alternativa3"));
		d3.setRispostaEsatta(rs.getFloat("risposta3_esatta"));
		d3.setUser1Risposta(rs.getInt("utente1_risposta3"));
		d3.setUser2Risposta(rs.getInt("utente2_risposta3"));
		d3.setNumeroDomanda(3);
		partita.aggiungiDomanda(d3);
		Domanda d4 = new Domanda();
		d4.setDomanda(rs.getString("domanda4"));
		d4.setRispostaErrata(1, rs.getFloat("risposta4_alternativa1"));
		d4.setRispostaErrata(2, rs.getFloat("risposta4_alternativa2"));
		d4.setRispostaErrata(3, rs.getFloat("risposta4_alternativa3"));
		d4.setRispostaEsatta(rs.getFloat("risposta4_esatta"));
		d4.setUser1Risposta(rs.getInt("utente1_risposta4"));
		d4.setUser2Risposta(rs.getInt("utente2_risposta4"));
		d4.setNumeroDomanda(4);
		partita.aggiungiDomanda(d4);
		Domanda d5 = new Domanda();
		d5.setDomanda(rs.getString("domanda5"));
		d5.setRispostaErrata(1, rs.getFloat("risposta5_alternativa1"));
		d5.setRispostaErrata(2, rs.getFloat("risposta5_alternativa2"));
		d5.setRispostaErrata(3, rs.getFloat("risposta5_alternativa3"));
		d5.setRispostaEsatta(rs.getFloat("risposta5_esatta"));
		d5.setUser1Risposta(rs.getInt("utente1_risposta5"));
		d5.setUser2Risposta(rs.getInt("utente2_risposta5"));
		d5.setNumeroDomanda(5);
		partita.aggiungiDomanda(d5);
		Domanda d6 = new Domanda();
		d6.setDomanda(rs.getString("domanda6"));
		d6.setRispostaErrata(1, rs.getFloat("risposta6_alternativa1"));
		d6.setRispostaErrata(2, rs.getFloat("risposta6_alternativa2"));
		d6.setRispostaErrata(3, rs.getFloat("risposta6_alternativa3"));
		d6.setRispostaEsatta(rs.getFloat("risposta6_esatta"));
		d6.setUser1Risposta(rs.getInt("utente1_risposta6"));
		d6.setUser2Risposta(rs.getInt("utente2_risposta6"));
		d6.setNumeroDomanda(6);
		partita.aggiungiDomanda(d6);
		return partita;
	}

	public ArrayList<Partita> getPartiteByUser(int id_utente) {
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		ArrayList<Partita> partite = new ArrayList<Partita>();
		try {
			con = DBConnectionPool.getConnection();
			String query = "SELECT * FROM partite WHERE (id_utente_1=" + id_utente + " OR id_utente_2=" + id_utente + ") AND (stato_partita=" + Partita.CREATA + " OR stato_partita=" + Partita.INIZIATA + ")";
			st = con.prepareStatement(query);
			rs = st.executeQuery();
			while (rs.next()) {
				Partita p = getPartitaFromResultSet(rs);
				verificaStatoPartita(p);
				partite.add(p);
			}
		}
		catch (SQLException e) {
			logger.severe(e.getMessage());
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
		return partite;
	}

	private void verificaStatoPartita(Partita p) {
		switch (p.getStatoPartita()) { // partite già concluse
			case Partita.ABBANDONATA_1:
			case Partita.ABBANDONATA_2:
			case Partita.PAREGGIATA:
			case Partita.VINCITORE_1:
			case Partita.VINCITORE_2:
			case Partita.TEMPO_SCADUTO:
				return;
		}
		// caso in cui la partita sia in stato iniziata o in corso
		if (p.hasUtente1Risposto() && p.hasUtente2Risposto()) { // se la partita
																// è in corso e
																// entrambi gli
																// utenti hanno
																// risposto
			int newState = getWinner(p);
			if (p.getStatoPartita() != newState) {
				p.setStatoPartita(newState);
				aggiornaStatoPartita(p);
			}
			switch (newState) {
				case Partita.VINCITORE_1:
					DBStatistiche.getInstance().aggiungiVittoriaAtUser(p.getIDUtente1());
					DBStatistiche.getInstance().aggiungiSconfittaAtUser(p.getIDUtente2());
					break;
				case Partita.VINCITORE_2:
					DBStatistiche.getInstance().aggiungiVittoriaAtUser(p.getIDUtente2());
					DBStatistiche.getInstance().aggiungiSconfittaAtUser(p.getIDUtente1());
					break;
				case Partita.PAREGGIATA:
					DBStatistiche.getInstance().aggiungiPareggioAtUser(p.getIDUtente1());
					DBStatistiche.getInstance().aggiungiPareggioAtUser(p.getIDUtente2());
					break;
			}
		}
		else if (p.getFinePartita() <= System.currentTimeMillis()) { // tempo
																	 // scaduto
			//System.out.println("tempo scaduto");
			if (p.hasUtente1Risposto()) { // se soltanto l'utente 1 ha risposto
				p.setStatoPartita(Partita.VINCITORE_1);
				DBStatistiche.getInstance().aggiungiVittoriaAtUser(p.getIDUtente1());
				DBStatistiche.getInstance().aggiungiSconfittaAtUser(p.getIDUtente2());
			}
			else if (p.hasUtente2Risposto()) { // se soltanto l'utente 2 ha
											   // risposto
				p.setStatoPartita(Partita.VINCITORE_2);
				DBStatistiche.getInstance().aggiungiVittoriaAtUser(p.getIDUtente2());
				DBStatistiche.getInstance().aggiungiSconfittaAtUser(p.getIDUtente1());
			}
			else { // se non ha risposto nessuno dei due
				p.setStatoPartita(Partita.TEMPO_SCADUTO);
			}
			aggiornaStatoPartita(p);
		}
	}

	private int getWinner(Partita p) {
		int u1 = 0, u2 = 0;
		for (int i = 0; i < p.getNumeroDomande(); i++) {
			Domanda d = p.getDomanda(i);
			if (d.isUser1Corretta())
				u1++;
			if (d.isUser2Corretta())
				u2++;
		}
		if (u1 == u2)
			return Partita.PAREGGIATA;
		else if (u1 > u2)
			return Partita.VINCITORE_1;
		else
			return Partita.VINCITORE_2;
	}

	private void aggiornaStatoPartita(Partita d) {
		//System.out.println("Aggiornamento stato partita " + d.getIDPartita() + " allo stato " + d.getStatoPartita());
		Connection con = null;
		PreparedStatement st = null;
		try {
			con = DBConnectionPool.getConnection();
			String query = "UPDATE partite SET stato_partita=" + d.getStatoPartita() + " WHERE id=" + d.getIDPartita();
			st = con.prepareStatement(query);
			st.executeUpdate();
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
	}

	private Domanda generaDomandaFacile() {
		Domanda d = GeneratoreEspressioniFacili.getInstance().generaDomanda();
		String domanda = d.getDomanda();
		domanda = domanda.replace('*', 'x').replace('/', ':');
		d.setDomanda(domanda);
		return d;
	}

	private Domanda generaDomandaMedia() {
		Domanda d = GeneratoreEspressioniMedie.getInstance().generaDomanda();
		String dom = d.getDomanda().replace('*', 'x').replace('/', ':');
		d.setDomanda(dom);
		return d;
	}

	private Domanda generaDomandaDifficile() {
		Domanda d = GeneratoreEspressioniDifficili.getInstance().generaDomanda();
		String dom = d.getDomanda().replace('*', 'x').replace('/', ':');
		d.setDomanda(dom);
		return d;
	}

	private Domanda generaDomandaDifficilissima() {
		return generaDomandaDifficile();
	}

	private static DateFormat dateFormat;

	private void inserisciPartita(Partita p) {
		Connection con = null;
		PreparedStatement st = null;
		try {
			con = DBConnectionPool.getConnection();
			p.setInizioPartita(System.currentTimeMillis());
			String query = "INSERT INTO partite (id_utente_1, id_utente_2, stato_partita," + "domanda1, risposta1_esatta, risposta1_alternativa1, risposta1_alternativa2, risposta1_alternativa3, " + "domanda2, risposta2_esatta, risposta2_alternativa1, risposta2_alternativa2, risposta2_alternativa3, " + "domanda3, risposta3_esatta, risposta3_alternativa1, risposta3_alternativa2, risposta3_alternativa3, " + "domanda4, risposta4_esatta, risposta4_alternativa1, risposta4_alternativa2, risposta4_alternativa3, " + "domanda5, risposta5_esatta, risposta5_alternativa1, risposta5_alternativa2, risposta5_alternativa3, " + "domanda6, risposta6_esatta, risposta6_alternativa1, risposta6_alternativa2, risposta6_alternativa3, " + "inizio, fine" + ") VALUES (" + p.getIDUtente1() + "," + p.getIDUtente2() + "," + p.getStatoPartita() + "," + "\"" + p.getDomanda(0).getDomanda() + "\"," + p.getDomanda(0).getRispostaEsatta() + "," + p.getDomanda(0).getRispostaErrata(1) + "," + p.getDomanda(0).getRispostaErrata(2) + "," + p.getDomanda(0).getRispostaErrata(3) + "," + "\"" + p.getDomanda(1).getDomanda() + "\"," + p.getDomanda(1).getRispostaEsatta() + "," + p.getDomanda(1).getRispostaErrata(1) + "," + p.getDomanda(1).getRispostaErrata(2) + "," + p.getDomanda(1).getRispostaErrata(3) + "," + "\"" + p.getDomanda(2).getDomanda() + "\"," + p.getDomanda(2).getRispostaEsatta() + "," + p.getDomanda(2).getRispostaErrata(1) + "," + p.getDomanda(2).getRispostaErrata(2) + "," + p.getDomanda(2).getRispostaErrata(3) + "," + "\"" + p.getDomanda(3).getDomanda() + "\"," + p.getDomanda(3).getRispostaEsatta() + "," + p.getDomanda(3).getRispostaErrata(1) + "," + p.getDomanda(3).getRispostaErrata(2) + "," + p.getDomanda(3).getRispostaErrata(3) + "," + "\"" + p.getDomanda(4).getDomanda() + "\"," + p.getDomanda(4).getRispostaEsatta() + "," + p.getDomanda(4).getRispostaErrata(1) + "," + p.getDomanda(4).getRispostaErrata(2) + "," + p.getDomanda(4).getRispostaErrata(3) + "," + "\"" + p.getDomanda(5).getDomanda() + "\"," + p.getDomanda(5).getRispostaEsatta() + "," + p.getDomanda(5).getRispostaErrata(1) + "," + p.getDomanda(5).getRispostaErrata(2) + "," + p.getDomanda(5).getRispostaErrata(3) + "," + "\"" + dateFormat.format(new Date(p.getInizioPartita())) + "\",\"" + dateFormat.format(new Date(p.getFinePartita())) + "\")";
			st = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			if (st.executeUpdate() > 0) {
				ResultSet rs = st.getGeneratedKeys();
				rs.next();
				int id = rs.getInt(1);
				p.setIDPartita(id);
				rs.close();
			}
		}
		catch (SQLException e) {
			logger.severe(e.getMessage());
			e.printStackTrace();
		}
		finally {
			try {
				if (st != null)
					st.close();
				DBConnectionPool.releaseConnection(con);
			}
			catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void rispondiDomandeBot(Partita partita) {
		Connection con = null;
		PreparedStatement st = null;
		try {
			con = DBConnectionPool.getConnection();
			String query;
			query = "UPDATE partite SET utente2_risposta1=" + partita.getDomanda(0).getUser2Risposto() + "," + "utente2_risposta2=" + partita.getDomanda(1).getUser2Risposto() + "," + "utente2_risposta3=" + partita.getDomanda(2).getUser2Risposto() + "," + "utente2_risposta4=" + partita.getDomanda(3).getUser2Risposto() + "," + "utente2_risposta5=" + partita.getDomanda(4).getUser2Risposto() + "," + "utente2_risposta6=" + partita.getDomanda(5).getUser2Risposto() + " " + "WHERE id=" + partita.getIDPartita();
			st = con.prepareStatement(query);
			st.executeUpdate();
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
	}

	public void rispondiDomande(int id_partita, int id_utente, float[] risposte) {
		Partita partita = getPartitaByID(id_partita);
		int n_utente = partita.getIDUtente1() == id_utente ? 1 : 2;
		for (int i = 0; i < partita.getNumeroDomande(); i++) {
			Domanda d = partita.getDomanda(i);
			boolean esatta = d.isRispostaEsatta(risposte[i]);
			switch (n_utente) {
				case 1:
					d.setUser1Risposta(esatta ? Domanda.ESATTA : Domanda.SBAGLIATA);
					break;
				case 2:
					d.setUser2Risposta(esatta ? Domanda.ESATTA : Domanda.SBAGLIATA);
					break;
			}
		}
		Connection con = null;
		PreparedStatement st = null;
		try {
			con = DBConnectionPool.getConnection();
			String query;
			if (n_utente == 1) {
				query = "UPDATE partite SET utente1_risposta1=" + partita.getDomanda(0).getUser1Risposto() + "," + "utente1_risposta2=" + partita.getDomanda(1).getUser1Risposto() + "," + "utente1_risposta3=" + partita.getDomanda(2).getUser1Risposto() + "," + "utente1_risposta4=" + partita.getDomanda(3).getUser1Risposto() + "," + "utente1_risposta5=" + partita.getDomanda(4).getUser1Risposto() + "," + "utente1_risposta6=" + partita.getDomanda(5).getUser1Risposto() + " " + "WHERE id=" + id_partita;
			}
			else {
				query = "UPDATE partite SET utente2_risposta1=" + partita.getDomanda(0).getUser2Risposto() + "," + "utente2_risposta2=" + partita.getDomanda(1).getUser2Risposto() + "," + "utente2_risposta3=" + partita.getDomanda(2).getUser2Risposto() + "," + "utente2_risposta4=" + partita.getDomanda(3).getUser2Risposto() + "," + "utente2_risposta5=" + partita.getDomanda(4).getUser2Risposto() + "," + "utente2_risposta6=" + partita.getDomanda(5).getUser2Risposto() + " " + "WHERE id=" + id_partita;
			}
			st = con.prepareStatement(query);
			st.executeUpdate();
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
	}

	public boolean abbandonaPartita(int id_partita, int id_account) {
		Partita p = getPartitaByID(id_partita);
		if (p != null) {
			if (p.getStatoPartita() <= Partita.INIZIATA) {
				if (p.getIDUtente1() == id_account) {
					p.setStatoPartita(Partita.VINCITORE_2);
					DBStatistiche.getInstance().aggiungiAbbandonoAtUser(id_account);
					DBStatistiche.getInstance().aggiungiVittoriaAtUser(p.getIDUtente2());
				}
				else if (p.getIDUtente2() == id_account) {
					p.setStatoPartita(Partita.VINCITORE_1);
					DBStatistiche.getInstance().aggiungiAbbandonoAtUser(id_account);
					DBStatistiche.getInstance().aggiungiVittoriaAtUser(p.getIDUtente1());
				}
				else
					return false;

				aggiornaStatoPartita(p);
				return true;
			}
		}
		return false;
	}
}
