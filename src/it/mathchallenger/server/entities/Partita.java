package it.mathchallenger.server.entities;

import java.util.ArrayList;

public class Partita {
	private int				id_partita;
	private Integer			id_utente_1, id_utente_2;
	private int				stato_partita;
	private long			   inizio, fine;
	private final static long  DURATA_MASSIMA_PARTITA = 172800000L; // 2 giorni
	private ArrayList<Domanda> domande;

	public final static int	CREATA				 = 0, INIZIATA = 1,
			ABBANDONATA_1 = 2, ABBANDONATA_2 = 3, VINCITORE_1 = 4,
			VINCITORE_2 = 5, TEMPO_SCADUTO = 6, PAREGGIATA = 7;

	public Partita() {
		domande = new ArrayList<Domanda>(6);
	}

	public void setIDUtente1(int id) {
		id_utente_1 = id;
	}

	public int getIDUtente1() {
		return id_utente_1;
	}

	public void setIDUtente2(Integer id) {
		id_utente_2 = id;
	}

	public Integer getIDUtente2() {
		return id_utente_2;
	}

	public void setIDPartita(int id) {
		id_partita = id;
	}

	public int getIDPartita() {
		return id_partita;
	}

	public void setStatoPartita(int s) {
		stato_partita = s;
	}

	public int getStatoPartita() {
		return stato_partita;
	}

	public void setInizioPartita(long i) {
		inizio = i;
		setFinePartita(i + DURATA_MASSIMA_PARTITA);
	}

	public long getInizioPartita() {
		return inizio;
	}

	private void setFinePartita(long i) {
		fine = i;
	}

	public long getFinePartita() {
		return fine;
	}

	public void aggiungiDomanda(Domanda d) {
		domande.add(d);
	}

	public Domanda getDomanda(int d) {
		return domande.get(d);
	}

	public boolean isTerminata() {
		switch (getStatoPartita()) {
			case CREATA:
			case INIZIATA:
				return false;
			default:
				return true;
		}
	}

	public boolean isInCorso() {
		switch (getStatoPartita()) {
			case CREATA:
			case INIZIATA:
				return true;
			default:
				return false;
		}
	}

	public String toString() {
		String str = "id: " + getIDPartita() + "\nPlayer1: " + getIDUtente1() + "\nPlayer2: " + getIDUtente2() + "\n";
		str += "Stato partita: " + getStatoPartita() + "\nInizio: " + getInizioPartita() + "\nFine: " + getFinePartita() + "\n\n";
		for (int i = 0; i < domande.size(); i++) {
			Domanda d = getDomanda(i);
			str += "Domanda num." + d.getNumeroDomanda() + "\n";
			str += "Domanda: " + d.getDomanda() + "\n";
			str += "Risposta esatta: " + d.getRispostaEsatta() + "\n";
			str += "Risposta 2: " + d.getRispostaErrata(1) + "\n";
			str += "Risposta 3: " + d.getRispostaErrata(2) + "\n";
			str += "Risposta 4: " + d.getRispostaErrata(3) + "\n\n";
		}
		return str;
	}

	public boolean hasUtente1Risposto() {
		for (int i = 0; i < domande.size(); i++) {
			if (!domande.get(i).isUser1Risposto())
				return false;
		}
		return true;
	}

	public boolean hasUtente2Risposto() {
		for (int i = 0; i < domande.size(); i++) {
			if (!domande.get(i).isUser2Risposto())
				return false;
		}
		return true;
	}

	public int getNumeroDomande() {
		return domande.size();
	}
}
