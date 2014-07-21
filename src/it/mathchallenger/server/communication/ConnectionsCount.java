package it.mathchallenger.server.communication;

public class ConnectionsCount {
	private static int connessioni_attive = 0;
	
	public static void connectionEnabled(){
		connessioni_attive++;
	}
	public static void connectionClosed(){
		connessioni_attive--;
	}
	public static int getConnectionsAlive(){
		return connessioni_attive;
	}
}
