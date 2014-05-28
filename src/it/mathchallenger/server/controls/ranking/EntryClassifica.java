package it.mathchallenger.server.controls.ranking;

public class EntryClassifica {
	private String utente;
	private int	punteggio;

	public EntryClassifica(String user, int p) {
		utente = user;
		punteggio = p;
	}

	public String getUtente() {
		return utente;
	}

	public int getPunteggio() {
		return punteggio;
	}
}