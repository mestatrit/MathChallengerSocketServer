package it.mathchallenger.server.admin;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class AdminServerSocket extends Thread {
	private final static int PORT_ADMIN_SERVER = 60000;
	public void run(){
		ServerSocket server = null;
		try {
			server = new ServerSocket(PORT_ADMIN_SERVER);
			while(true){
				Socket u=server.accept();
				(new AdminSocketService(u)).start();
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			if(server!=null)
				try {
					server.close();
				}
				catch (IOException e) {
					e.printStackTrace();
				}
		}
	}
}
