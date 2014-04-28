package it.mathchallenger.server.controls;

import it.mathchallenger.server.entities.Account;

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
	
	private GestionePartite() {
		utenti_loggati=new ArrayList<Account>(100);
		rand=new Random(System.currentTimeMillis());
	}
	public void entraUtente(Account acc){
		if(acc!=null)
			utenti_loggati.add(acc);
	}
	public void esceUtente(Account acc){
		if(acc!=null)
			utenti_loggati.remove(acc);
	}
	private static Random rand;
	private Account accountRandom(){
		int acc_ind=rand.nextInt(utenti_loggati.size());
		return utenti_loggati.get(acc_ind);
	}
}
