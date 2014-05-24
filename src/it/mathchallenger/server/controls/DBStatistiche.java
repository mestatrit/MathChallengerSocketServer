package it.mathchallenger.server.controls;

import it.mathchallenger.server.entities.Partita;
import it.mathchallenger.server.entities.Statistiche;

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
		return null;
	}

	public void aggiungiVittoriaAtUser(int id) {

	}

	public void aggiungiSconfittaAtUser(int id) {

	}

	public void aggiungiPareggioAtUser(int id) {

	}

	public void aggiungiAbbandonoAtUser(int id) {

	}

	public void ricalcolaPunteggio(ArrayList<Partita> partite) {

	}
}
