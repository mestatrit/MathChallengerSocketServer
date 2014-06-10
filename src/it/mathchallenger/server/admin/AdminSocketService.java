package it.mathchallenger.server.admin;

import it.mathchallenger.server.controls.GestionePartite;
import it.mathchallenger.server.controls.ranking.Ranking;
import it.mathchallenger.server.controls.version.VersionCheck;
import it.mathchallenger.server.entities.Account;
import it.mathchallenger.server.launcher.SocketServer;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Properties;

public class AdminSocketService extends Thread {
	private Socket comm;
	private BufferedReader in;
	private OutputStream output;
	private final static int TIMEOUT_PING = 60000;
	private boolean logged=false;
	
	public AdminSocketService(Socket s){
		comm=s;
		try {
			in=new BufferedReader(new InputStreamReader(comm.getInputStream()));
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		try {
			output=comm.getOutputStream();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void run(){
		int timer_ping = 0;
		while (!comm.isClosed() && comm.isBound()) {
			try {
				String str;
				if ((str=in.readLine())!=null) {
					String[] cmd=str.trim().split(" ");
					switch(cmd[0]){
						case "logout":
							logout(cmd);
							break;
						case "login":
							login(cmd);
							break;
						case "user_list_online":
							user_list_online(cmd);
							break;
						case "user_add":
							break;
						case "user_kick":
							break;
						case "user_ban":
							break;
						case "user_delete":
							break;
						case "version_get_client_enabled":
							version_getVersioniAbilitate(cmd);
							break;
						case "version_add_client_enabled":
							break;
						case "version_remove_client_enabled":
							break;
						case "version_reload_client_enabled":
							break;
						case "ranking_change_value":
							break;
						case "ranking_change_all_values":
							break;
						case "ranking_reload":
							ranking_reload(cmd);
							break;
						case "ranking_force_update":
							break;
						case "server_restart":
							break;
						case "server_lock_connections":
							break;
						case "server_unlock_connections":
							break;
						case "server_change_admin":
							break;
						case "server_stop_admin_connections":
							break;
						case "email_change_value":
							break;
						case "email_change_all_values":
							break;
						case "email_reload_properties":
							break;
					}
				}
				else {
					timer_ping += 200;
				}
			}
			catch(IOException e){
				
			}
			try {
				sleep(200);
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
			if(timer_ping>TIMEOUT_PING)
				break;
		}
		System.out.println("Thread admin terminato");
	}
	private void OutputWrite(String s) throws IOException {
		if (!s.endsWith("\n"))
			s += "\n";
		output.write(s.getBytes());
		output.flush();
	}
	private void login(String[] cmd) throws IOException{
		if(cmd.length==3){
			String u=cmd[1];
			String p=cmd[2];
			if(login(u,p)){
				logged=true;
				OutputWrite("login=OK");
			}
			else {
				logged=false;
				OutputWrite("login=error;message=Invalid username or password");
			}
		}
		else {
			OutputWrite("login=errror;message=Usage: login username password");
		}
	}
	private boolean login(String u, String p){
		Properties prop=new Properties();
		InputStream in;
		try {
			in = new FileInputStream("admin.properties");
			prop.load(in);
			in.close();
		}
		catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		boolean login;
		if(u.compareTo(prop.getProperty("username"))==0 && p.compareTo(prop.getProperty("password"))==0)
			login=true;
		else
			login = false;
		prop.clear();
		return login;
	}
	private void logout(String[] cmd) throws IOException{
		if(cmd.length==1){
			if(!logged){
				OutputWrite("logout=error;message=Non sei loggato");
			}
			else {
				OutputWrite("logout=OK");
				comm.close();
			}
		}
		else {
			OutputWrite("logout=error;message=Usage: logout");
		}
	}
	private void user_list_online(String[] cmd) throws IOException{
		if(cmd.length==1){
			if(!logged){
				OutputWrite("user_list_online=error;message=Non sei loggato");
			}
			else {
				ArrayList<Account> list=GestionePartite.getInstance().listUsers();
				StringBuilder resp=new StringBuilder("user_list_online=OK;client_attivi="+SocketServer.thread_utenti_attivi.activeCount()+";loggati="+list.size());
				for(int i=0;i<list.size();i++){
					resp.append(";utente="+list.get(i).getUsername()+","+list.get(i).getID());
				}
				OutputWrite(resp.toString());
			}
		}
		else
			OutputWrite("user_list_online=error;message=Usage: list_users");
	}
	private void ranking_reload(String[] cmd) throws IOException{
		if(cmd.length==1){
			if(!logged){
				OutputWrite("ranking_reload=error;message=Non sei loggato");
			}
			else{
				Ranking.readProperties();
				OutputWrite("ranking_reload=OK");
			}
		}
		else {
			OutputWrite("ranking_reload=error;message=Usage: ranking_reload");
		}
	}
	private void version_getVersioniAbilitate(String[] cmd) throws IOException{
		if(cmd.length==1){
			if(logged){
				ArrayList<Integer> v=VersionCheck.getInstance().getValidVersions();
				StringBuilder str=new StringBuilder("version_get_client_enabled=OK");
				for(int i=0;i<v.size();i++){
					str.append(";version="+v.get(i));
				}
				OutputWrite(str.toString());
			}
			else {
				OutputWrite("version_get_client_enabled=error");
			}
		}
		else {
			OutputWrite("version_get_client_enabled=error");
		}
	}
}
