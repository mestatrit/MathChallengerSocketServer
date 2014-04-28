package it.mathchallenger.server.communication;

import it.mathchallenger.server.storage.DBConnectionPool;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;

public class SocketServer {
	public static ThreadGroup thread_utenti_attivi=new ThreadGroup("t_utenti_attivi");
	public static void main(String[] args) throws IOException {
		try {
			DBConnectionPool.init();
			Runtime.getRuntime().addShutdownHook(new Thread(){
				public void run(){
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
		MailSender.init();
		ServerSocket server = null;
		try {
			server=new ServerSocket(50000);
			while(true){
				Socket socket=server.accept();
				Thread t=new Thread(thread_utenti_attivi, new SocketService(socket));
				t.start();
			}
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			server.close();
		}
	}

}
