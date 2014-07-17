package it.mathchallenger.server.market;

import java.util.ArrayList;

import it.mathchallenger.server.entities.Account;

public class MarketPlace {
	private static MarketPlace market;
	private ArrayList<ItemMarket> servizi;//"cambio_username", "cambio_email"
	private ArrayList<ItemMarket> oggettiMercato;
	
	public static MarketPlace getInstance(){
		if(market==null){
			market=new MarketPlace();
		}
		return market;
	}
	private MarketPlace(){
		oggettiMercato=new ArrayList<ItemMarket>();
		servizi=new ArrayList<>();
		caricaServizi();
		caricaLista();
	}
	private void caricaServizi(){
		servizi.add(new ItemMarket("Cambio Username", -1, 0.50f));
		servizi.add(new ItemMarket("Cambio Email", -2, 0.20f));
	}
	private void caricaLista(){
		//TODO
	}
	public void aggiungiOggettoAlMarket(ItemMarket item){
		//TODO aggiungi oggetto a database
		oggettiMercato.add(item);
	}
	public void modificaOggetto(ItemMarket item){
		
	}
	
	public boolean modificaUsername(Account richiedente, String nuovo_username){
		//TODO 
		return false;
	}
	public boolean modificaIndirizzoEmail(Account acc, String nuova_email){
		//TODO
		return false;
	}
}
