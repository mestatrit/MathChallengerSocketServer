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
import java.util.logging.Logger;

public class DBPartita {
	private static DBPartita manager;
	private static Logger logger;
	
	public static synchronized DBPartita getInstance() {
		if (manager == null) {
			manager = new DBPartita();
		}
		return manager;
	}
	private DBPartita(){
		logger=LoggerManager.newLogger(getClass().getName());
		dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	}
	public Partita creaPartita(int id1, int id2){
		Partita partita=new Partita();
		partita.setIDUtente1(id1);
		partita.setIDUtente2(id2);
		partita.setInizioPartita(System.currentTimeMillis());
		partita.setStatoPartita(Partita.CREATA);
		Domanda d1=generaDomandaFacile();
		d1.setNumeroDomanda(1);
		//TODO modificare da domanda facile
		Domanda d2=generaDomandaFacile();
		d2.setNumeroDomanda(2);
		Domanda d3=generaDomandaFacile();
		d3.setNumeroDomanda(3);
		Domanda d4=generaDomandaFacile();
		d4.setNumeroDomanda(4);
		Domanda d5=generaDomandaFacile();
		d5.setNumeroDomanda(5);
		Domanda d6=generaDomandaFacile();
		d6.setNumeroDomanda(6);
		//FINE esempio
		
		/*
		Domanda d2=generaDomandaMedia();
		d2.setNumeroDomanda(2);
		Domanda d3=generaDomandaMedia();
		d3.setNumeroDomanda(3);
		Domanda d4=generaDomandaDifficile();
		d4.setNumeroDomanda(4);
		Domanda d5=generaDomandaDifficile();
		d5.setNumeroDomanda(5);
		Domanda d6=generaDomandaDifficilissima();
		d6.setNumeroDomanda(6);
		*/
		partita.aggiungiDomanda(d1);
		partita.aggiungiDomanda(d2);
		partita.aggiungiDomanda(d3);
		partita.aggiungiDomanda(d4);
		partita.aggiungiDomanda(d5);
		partita.aggiungiDomanda(d6);
		inserisciPartita(partita);
		return partita;
	}
	public Partita getPartitaByID(int id){
		Connection con=null;
		PreparedStatement st=null;
		ResultSet rs=null;
		Partita partita=null;
		try {
			con=DBConnectionPool.getConnection();
			String query="SELECT * FROM partite WHERE id="+id;
			st=con.prepareStatement(query);
			rs=st.executeQuery();
			while(rs.next()){
				int id1=rs.getInt("id_utente_1");
				int id2=rs.getInt("id_utente_2");
				int stato_partita=rs.getInt("stato_partita");
				long inizio=rs.getTimestamp("inizio").getTime();
				partita=new Partita();
				partita.setIDPartita(id);
				partita.setIDUtente1(id1);
				partita.setIDUtente2(id2);
				partita.setInizioPartita(inizio);
				partita.setStatoPartita(stato_partita);
				Domanda d1=new Domanda();
				d1.setDomanda(rs.getString("domanda1"));
				d1.setRispostaErrata(1, rs.getFloat("risposta1_alternativa1"));
				d1.setRispostaErrata(2, rs.getFloat("risposta1_alternativa2"));
				d1.setRispostaErrata(3, rs.getFloat("risposta1_alternativa3"));
				d1.setRispostaEsatta(rs.getFloat("risposta1_esatta"));
				d1.setUser1Risposta(rs.getInt("utente1_risposta1"));
				d1.setUser2Risposta(rs.getInt("utente2_risposta1"));
				d1.setNumeroDomanda(1);
				partita.aggiungiDomanda(d1);
				Domanda d2=new Domanda();
				d2.setDomanda(rs.getString("domanda2"));
				d2.setRispostaErrata(1, rs.getFloat("risposta2_alternativa1"));
				d2.setRispostaErrata(2, rs.getFloat("risposta2_alternativa2"));
				d2.setRispostaErrata(3, rs.getFloat("risposta2_alternativa3"));
				d2.setRispostaEsatta(rs.getFloat("risposta2_esatta"));
				d2.setUser1Risposta(rs.getInt("utente1_risposta2"));
				d2.setUser2Risposta(rs.getInt("utente2_risposta2"));
				d2.setNumeroDomanda(2);
				partita.aggiungiDomanda(d2);
				Domanda d3=new Domanda();
				d3.setDomanda(rs.getString("domanda3"));
				d3.setRispostaErrata(1, rs.getFloat("risposta3_alternativa1"));
				d3.setRispostaErrata(2, rs.getFloat("risposta3_alternativa2"));
				d3.setRispostaErrata(3, rs.getFloat("risposta3_alternativa3"));
				d3.setRispostaEsatta(rs.getFloat("risposta3_esatta"));
				d3.setUser1Risposta(rs.getInt("utente1_risposta3"));
				d3.setUser2Risposta(rs.getInt("utente2_risposta3"));
				d3.setNumeroDomanda(3);
				partita.aggiungiDomanda(d3);
				Domanda d4=new Domanda();
				d4.setDomanda(rs.getString("domanda4"));
				d4.setRispostaErrata(1, rs.getFloat("risposta4_alternativa1"));
				d4.setRispostaErrata(2, rs.getFloat("risposta4_alternativa2"));
				d4.setRispostaErrata(3, rs.getFloat("risposta4_alternativa3"));
				d4.setRispostaEsatta(rs.getFloat("risposta4_esatta"));
				d4.setUser1Risposta(rs.getInt("utente1_risposta4"));
				d4.setUser2Risposta(rs.getInt("utente2_risposta4"));
				d4.setNumeroDomanda(4);
				partita.aggiungiDomanda(d4);
				Domanda d5=new Domanda();
				d5.setDomanda(rs.getString("domanda5"));
				d5.setRispostaErrata(1, rs.getFloat("risposta5_alternativa1"));
				d5.setRispostaErrata(2, rs.getFloat("risposta5_alternativa2"));
				d5.setRispostaErrata(3, rs.getFloat("risposta5_alternativa3"));
				d5.setRispostaEsatta(rs.getFloat("risposta5_esatta"));
				d5.setUser1Risposta(rs.getInt("utente1_risposta5"));
				d5.setUser2Risposta(rs.getInt("utente2_risposta5"));
				d5.setNumeroDomanda(5);
				partita.aggiungiDomanda(d5);
				Domanda d6=new Domanda();
				d6.setDomanda(rs.getString("domanda6"));
				d6.setRispostaErrata(1, rs.getFloat("risposta6_alternativa1"));
				d6.setRispostaErrata(2, rs.getFloat("risposta6_alternativa2"));
				d6.setRispostaErrata(3, rs.getFloat("risposta6_alternativa3"));
				d6.setRispostaEsatta(rs.getFloat("risposta6_esatta"));
				d6.setUser1Risposta(rs.getInt("utente1_risposta6"));
				d6.setUser2Risposta(rs.getInt("utente2_risposta6"));
				d6.setNumeroDomanda(6);
				partita.aggiungiDomanda(d6);
			}
		} 
		catch (SQLException e) {
			logger.severe(e.getMessage());
		}
		finally {
			try {
				if(rs!=null)
					rs.close();
				if(st!=null)
					st.close();
				if(con!=null)
					DBConnectionPool.releaseConnection(con);
			} 
			catch (SQLException e) {}
		}
		return partita;
	}
	public ArrayList<Partita> getPartiteByUser(int id_utente){
		
	}
	public void aggiornaPartita(Partita d){
		
	}
	
	private Domanda generaDomandaFacile(){
		Domanda d=GeneratoreEspressioniFacili.getInstance().generaDomanda();
		String domanda=d.getDomanda();
		domanda=domanda.replace('*', 'x').replace('/', ':');
		d.setDomanda(domanda);
		return d;
	}
	private Domanda generaDomandaMedia(){
		
	}
	private Domanda generaDomandaDifficile(){
		
	}
	private Domanda generaDomandaDifficilissima(){
		
	}
	private static DateFormat dateFormat;
	private void inserisciPartita(Partita p){
		Connection con=null;
		PreparedStatement st=null;
		try {
			con=DBConnectionPool.getConnection();
			p.setInizioPartita(System.currentTimeMillis());
			String query="INSERT INTO partite (id_utente_1, id_utente_2, stato_partita,"+
				"domanda1, risposta1_esatta, risposta1_alternativa1, risposta1_alternativa2, risposta1_alternativa3, "+
				"domanda2, risposta2_esatta, risposta2_alternativa1, risposta2_alternativa2, risposta2_alternativa3, "+
				"domanda3, risposta3_esatta, risposta3_alternativa1, risposta3_alternativa2, risposta3_alternativa3, "+
				"domanda4, risposta4_esatta, risposta4_alternativa1, risposta4_alternativa2, risposta4_alternativa3, "+
				"domanda5, risposta5_esatta, risposta5_alternativa1, risposta5_alternativa2, risposta5_alternativa3, "+
				"domanda6, risposta6_esatta, risposta6_alternativa1, risposta6_alternativa2, risposta6_alternativa3, "+
				"inizio, fine"+
				") VALUES ("+
				p.getIDUtente1()+","+p.getIDUtente2()+","+p.getStatoPartita()+","+
				"\""+p.getDomanda(0).getDomanda()+"\","+p.getDomanda(0).getRispostaEsatta()+","+p.getDomanda(0).getRispostaErrata(1)+","+p.getDomanda(0).getRispostaErrata(2)+","+p.getDomanda(0).getRispostaErrata(3)+","+
				"\""+p.getDomanda(1).getDomanda()+"\","+p.getDomanda(1).getRispostaEsatta()+","+p.getDomanda(1).getRispostaErrata(1)+","+p.getDomanda(1).getRispostaErrata(2)+","+p.getDomanda(1).getRispostaErrata(3)+","+
				"\""+p.getDomanda(2).getDomanda()+"\","+p.getDomanda(2).getRispostaEsatta()+","+p.getDomanda(2).getRispostaErrata(1)+","+p.getDomanda(2).getRispostaErrata(2)+","+p.getDomanda(2).getRispostaErrata(3)+","+
				"\""+p.getDomanda(3).getDomanda()+"\","+p.getDomanda(3).getRispostaEsatta()+","+p.getDomanda(3).getRispostaErrata(1)+","+p.getDomanda(3).getRispostaErrata(2)+","+p.getDomanda(3).getRispostaErrata(3)+","+
				"\""+p.getDomanda(4).getDomanda()+"\","+p.getDomanda(4).getRispostaEsatta()+","+p.getDomanda(4).getRispostaErrata(1)+","+p.getDomanda(4).getRispostaErrata(2)+","+p.getDomanda(4).getRispostaErrata(3)+","+
				"\""+p.getDomanda(5).getDomanda()+"\","+p.getDomanda(5).getRispostaEsatta()+","+p.getDomanda(5).getRispostaErrata(1)+","+p.getDomanda(5).getRispostaErrata(2)+","+p.getDomanda(5).getRispostaErrata(3)+","+
				"\""+dateFormat.format(new Date(p.getInizioPartita()))+"\",\""+dateFormat.format(new Date(p.getFinePartita()))+"\")";
			st=con.prepareStatement(query,Statement.RETURN_GENERATED_KEYS);
			if(st.executeUpdate()>0){
				ResultSet rs=st.getGeneratedKeys();
				rs.next();
				int id=rs.getInt(1);
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
				if(st!=null)
					st.close();
				DBConnectionPool.releaseConnection(con);
			} 
			catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
