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
import java.util.Random;

public class GestionePartite {
	private static GestionePartite manager;
	public static GestionePartite getInstance(){
		if(manager==null)
			manager=new GestionePartite();
		return manager;
	}
	
	private ArrayList<Account> utenti_loggati;
	private ArrayList<Bot> bot_list;
	private ArrayList<Bot> b_copy;
	private Queue<Bot> ordine_bot;
	
	private GestionePartite() {
		utenti_loggati=new ArrayList<Account>(100);
		rand=new Random(System.currentTimeMillis());
		ordine_bot=new NodeQueue<Bot>();
		bot_list=new ArrayList<Bot>(20);
		
		loadBot();
		
		b_copy=new ArrayList<Bot>(bot_list.size());
		for(int i=0;i<bot_list.size();i++){
			b_copy.add(bot_list.get(i));
		}
		avviaBot();
		generaOrdineBot();
	}
	private void generaOrdineBot() {
		while(!b_copy.isEmpty()){
			Bot b=b_copy.remove(rand.nextInt(b_copy.size()));
			ordine_bot.enqueue(b);
		}
	}
	private synchronized Bot getBot(){
		if(ordine_bot.isEmpty())
			generaOrdineBot();
		Bot b=ordine_bot.dequeue();
		b_copy.add(b);
		return b;
	}
	public void entraUtente(Account acc){
		if(acc!=null)
			utenti_loggati.add(acc);
	}
	public void esceUtente(Account acc){
		if(acc!=null)
			utenti_loggati.remove(acc);
	}
	public Bot getBotByID(int id){
		for(int i=0;i<bot_list.size();i++){
			if(bot_list.get(i).getID()==id)
				return bot_list.get(i);
		}
		return null;
	}
	public Bot getBotRandom(){
		return getBot();
	}
	private static Random rand;
	public Account accountRandom(int id_ricercante){
		if(utenti_loggati.size()<=1){ //l'utente loggato è l'utente che cerca
			return getBotRandom();
		}
		else {
			int prove=0;
			while(prove<5){
				int acc_ind=rand.nextInt(utenti_loggati.size());
				Account acc=utenti_loggati.get(acc_ind);
				if(acc.getID()!=id_ricercante)
					return acc;
			}
			return getBotRandom();
		}
	}
	private void loadBot(){
		String query="SELECT * FROM bot";
		Connection con=null;
		PreparedStatement st=null;
		ResultSet rs=null;
		try {
			con=DBConnectionPool.getConnection();
			st=con.prepareStatement(query);
			rs=st.executeQuery();
			while(rs.next()){
				int id=rs.getInt("id");
				String nome=rs.getString("nome");
				int percent=rs.getInt("percentuale_successo");
				Bot b=new Bot(nome, percent, id);
				bot_list.add(b);
			}
		} 
		catch (SQLException e) {
			e.printStackTrace();
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
			catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	private void avviaBot() {
		for(int i=0;i<bot_list.size();i++){
			Bot bot=bot_list.get(i);
			Thread t_bot=new Thread(bot);
			t_bot.start();
		}
	}
}
