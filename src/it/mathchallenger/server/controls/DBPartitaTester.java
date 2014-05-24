package it.mathchallenger.server.controls;

import it.mathchallenger.server.entities.Partita;
import it.mathchallenger.server.storage.DBConnectionPool;

import java.io.IOException;
import java.sql.SQLException;

public class DBPartitaTester {

	public static void main(String[] args) throws IOException {
		try {
			DBConnectionPool.init();
			Runtime.getRuntime().addShutdownHook(new Thread() {
				public void run() {
					try {
						DBConnectionPool.freeConnections();
					}
					catch (SQLException e) {
						e.printStackTrace();
					}
				}
			});
		}
		catch (ClassNotFoundException | SQLException e1) {
			System.out.println("Database connection error");
			System.exit(0);
		}
		/*
		 * Partita p=DBPartita.getInstance().getPartitaByID(1); String
		 * query="INSERT INTO partite (id_utente_1, id_utente_2, stato_partita,"
		 * +
		 * "domanda1, risposta1_esatta, risposta1_alternativa1, risposta1_alternativa2, risposta1_alternativa3, "
		 * +
		 * "domanda2, risposta2_esatta, risposta2_alternativa1, risposta2_alternativa2, risposta2_alternativa3, "
		 * +
		 * "domanda3, risposta3_esatta, risposta3_alternativa1, risposta3_alternativa2, risposta3_alternativa3, "
		 * +
		 * "domanda4, risposta4_esatta, risposta4_alternativa1, risposta4_alternativa2, risposta4_alternativa3, "
		 * +
		 * "domanda5, risposta5_esatta, risposta5_alternativa1, risposta5_alternativa2, risposta5_alternativa3, "
		 * +
		 * "domanda6, risposta6_esatta, risposta6_alternativa1, risposta6_alternativa2, risposta6_alternativa3, "
		 * + "fine"+ ") VALUES ("+
		 * p.getIDUtente1()+","+p.getIDUtente2()+","+p.getStatoPartita()+","+
		 * "\""
		 * +p.getDomanda(0).getDomanda()+"\","+p.getDomanda(0).getRispostaEsatta
		 * ()+","+p.getDomanda(0).getRispostaErrata(1)+","+p.getDomanda(0).
		 * getRispostaErrata(2)+","+p.getDomanda(0).getRispostaErrata(3)+","+
		 * "\""
		 * +p.getDomanda(1).getDomanda()+"\","+p.getDomanda(1).getRispostaEsatta
		 * ()+","+p.getDomanda(1).getRispostaErrata(1)+","+p.getDomanda(1).
		 * getRispostaErrata(2)+","+p.getDomanda(1).getRispostaErrata(3)+","+
		 * "\""
		 * +p.getDomanda(2).getDomanda()+"\","+p.getDomanda(2).getRispostaEsatta
		 * ()+","+p.getDomanda(2).getRispostaErrata(1)+","+p.getDomanda(2).
		 * getRispostaErrata(2)+","+p.getDomanda(2).getRispostaErrata(3)+","+
		 * "\""
		 * +p.getDomanda(3).getDomanda()+"\","+p.getDomanda(3).getRispostaEsatta
		 * ()+","+p.getDomanda(3).getRispostaErrata(1)+","+p.getDomanda(3).
		 * getRispostaErrata(2)+","+p.getDomanda(3).getRispostaErrata(3)+","+
		 * "\""
		 * +p.getDomanda(4).getDomanda()+"\","+p.getDomanda(4).getRispostaEsatta
		 * ()+","+p.getDomanda(4).getRispostaErrata(1)+","+p.getDomanda(4).
		 * getRispostaErrata(2)+","+p.getDomanda(4).getRispostaErrata(3)+","+
		 * "\""
		 * +p.getDomanda(5).getDomanda()+"\","+p.getDomanda(5).getRispostaEsatta
		 * ()+","+p.getDomanda(5).getRispostaErrata(1)+","+p.getDomanda(5).
		 * getRispostaErrata(2)+","+p.getDomanda(5).getRispostaErrata(3)+","+
		 * p.getFinePartita()+")"; System.out.println(query);
		 */
		// System.out.println(p);
		Partita p = DBPartita.getInstance().creaPartita(1, 2);
		System.out.println(p);
	}

}
