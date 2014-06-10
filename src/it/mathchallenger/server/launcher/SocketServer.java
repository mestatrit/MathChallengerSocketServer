package it.mathchallenger.server.launcher;

import it.mathchallenger.server.admin.AdminServerSocket;
import it.mathchallenger.server.communication.SocketService;
import it.mathchallenger.server.communication.mail.MailSender;
import it.mathchallenger.server.controls.GestionePartite;
import it.mathchallenger.server.controls.ranking.Ranking;
import it.mathchallenger.server.storage.DBConnectionPool;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;

public class SocketServer {
	public static ThreadGroup   thread_utenti_attivi = new ThreadGroup("t_utenti_attivi");
	private static ServerSocket server			   = null;
	private static MailSender mailsender = null;
	private static Ranking ranking;

	public static void main(String[] args) throws IOException {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				try {
					System.out.println("Chiusura del ServerSocket");
					server.close();

				}
				catch (IOException e) {
					System.out.println("Chiusura del socket fallita");
					e.printStackTrace();
				}

				try {
					System.out.println("Chiusura delle connessioni al database");
					DBConnectionPool.freeConnections();
				}
				catch (SQLException e) {
					e.printStackTrace();
				}

				System.out.println("Chiusura di tutte le connessioni aperte");
				thread_utenti_attivi.interrupt();
			}
		});
		try {
			System.out.println("Avvio il collegamento con il database...");
			DBConnectionPool.init();
		}
		catch (ClassNotFoundException | SQLException e1) {
			System.out.println("Collegamento con il database fallito...");
			System.exit(0);
		}
		System.out.println("Avvio il controller per inviare le email...");
		mailsender=new MailSender();
		mailsender.start();

		System.out.println("Istanziazione del Gestore delle partite");
		GestionePartite.getInstance();

		System.out.println("Avvio thread che aggiorna la classifica");
		ranking=Ranking.getInstance();
		ranking.start();
		
		System.out.println("Avvio gestore amministratore");
		Thread t_admin = new AdminServerSocket();
		t_admin.start();
		
		System.out.println("Tentativo di mettersi in ascolto per ricevere connessioni in arrivo...");
		try {
			server = new ServerSocket(50000);
			System.out.println("In ascolto in attesa di connessioni...");
			while (true) {
				Socket socket = server.accept();
				System.out.println("Collegamento accettato con: " + socket.getInetAddress().getHostAddress());
				Thread t = new Thread(thread_utenti_attivi, new SocketService(socket));
				t.start();
			}
		}
		catch (IOException e) {
			System.out.println("Per problemi di udito, il server ha fallito l'ascolto...");
			e.printStackTrace();
			System.exit(0);
		}
		finally {
			System.out.println("Chiusura del server...");
			if (server != null)
				server.close();
			System.exit(0);;
		}
	}

}
