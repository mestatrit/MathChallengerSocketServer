package it.mathchallenger.server.admin;

import it.mathchallenger.server.communication.mail.MailSender;
import it.mathchallenger.server.controls.DBAccount;
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
							user_add(cmd);
							break;
						case "user_kick":
							user_kick(cmd);
							break;
						case "user_ban":
							break;
						case "user_delete_username":
							user_delete_username(cmd);
							break;
						case "user_delete_id":
							user_delete_id(cmd);
							break;
						case "version_get_client_enabled":
							version_getVersioniAbilitate(cmd);
							break;
						case "version_add_client_enabled":
							version_add_client_enabled(cmd);
							break;
						case "version_remove_client_enabled":
							version_remove_client_enabled(cmd);
							break;
						case "version_reload_client_enabled":
							version_reload_client_enabled(cmd);
							break;
						case "ranking_enable_post":
							break;
						case "ranking_change_all_values":
							ranking_change_all_values(cmd);
							break;
						case "ranking_reload":
							ranking_reload(cmd);
							break;
						case "ranking_force_update":
							ranking_force_update(cmd);
							break;
						case "server_restart":
							server_restart(cmd);
							break;
						case "server_lock_connections":
							break;
						case "server_unlock_connections":
							break;
						case "server_change_admin":
							break;
						case "server_stop_admin_connections":
							break;
						case "email_change_all_values":
							email_change_all_values(cmd);
							break;
						case "email_reload_properties":
							email_reload_properties(cmd);
							break;
						case "email_debug":
							email_debug(cmd);
							break;
					}
				}
				else {
					timer_ping += 500;
				}
			}
			catch(Exception e){
				try {
					OutputWrite("generic=error");
				} 
				catch (IOException e1) {
					e1.printStackTrace();
				}
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
	private void user_kick(String[] cmd) throws IOException {
		if(cmd.length==2 && logged){
			String username=cmd[1];
			boolean b=GestionePartite.getInstance().kickUser(username);
			if(b)
				OutputWrite("user_kick=OK");
			else
				OutputWrite("user_kick=error");
		}
		else
			OutputWrite("user_kick=error");
	}
	private void server_restart(String[] cmd) throws IOException {
		if(cmd.length==1){
			OutputWrite("server_restart=OK");
			System.exit(0);
		}
		else
			OutputWrite("server_restart=error");
	}
	private void user_add(String[] cmd) throws IOException {
		if(cmd.length==3 && logged){
			if(DBAccount.getInstance().isAccountExist(cmd[1]))
				OutputWrite("user_add=error;message=Account esistente");
			else {
				boolean b=DBAccount.getInstance().registra(cmd[1], cmd[2]);
				if(b)
					OutputWrite("user_add=OK");
				else
					OutputWrite("user_add=error;message=Errore durante la registrazione");
			}
		}
		else
			OutputWrite("user_add=error");
	}
	private void email_debug(String[] cmd) throws IOException {
		if(cmd.length==2 && logged){
			boolean b=Boolean.parseBoolean(cmd[1]);
			MailSender.debugStatus(b);
			OutputWrite("email_debug=OK");
		}
		else 
			OutputWrite("email_debug=error");
	}
	private void email_reload_properties(String[] cmd) throws IOException {
		if(cmd.length==1){
			if(logged){
				MailSender.readProperties();
				OutputWrite("email_reload_properties=OK");
			}
			else {
				OutputWrite("email_reload_properties=error");
			}
		}
		else {
			OutputWrite("email_reload_properties=error");
		}
	}
	private void ranking_change_all_values(String[] cmd) throws IOException {
		if(cmd.length%2!=0){
			if(logged){
				boolean okChange=true;
				for(int i=1;i<cmd.length;i=i+2){
					okChange=Ranking.getInstance().changeValue(cmd[i], cmd[i+1]);
					if(!okChange)
						break;
				}
				if(okChange)
					Ranking.getInstance().saveToFile();
				else
					Ranking.getInstance().readProperties();
				OutputWrite("ranking_change_all_values="+(okChange?"OK":"error"));
			}
			else {
				OutputWrite("ranking_change_all_values=error");
			}
		}
		else {
			OutputWrite("ranking_change_all_values=error");
		}
	}
	private void email_change_all_values(String[] cmd) throws IOException {
		if(cmd.length%2!=0){
			if(logged){
				boolean okChange=true;
				for(int i=1;i<cmd.length;i=i+2){
					okChange=MailSender.changeValue(cmd[i], cmd[i+1]);
					if(!okChange)
						break;
				}
				if(okChange)
					MailSender.saveToFile();
				else
					MailSender.readProperties();
				OutputWrite("email_change_all_values="+(okChange?"OK":"error"));
			}
			else {
				OutputWrite("email_change_all_values=error");
			}
		}
		else {
			OutputWrite("email_change_all_values=error");
		}
	}
	/*
	private void ranking_change_value(String[] cmd) throws IOException {
		if(cmd.length==3){
			if(logged){
				boolean b=Ranking.getInstance().changeValue(cmd[1], cmd[2]);
				if(b){
					if(Ranking.getInstance().saveToFile())
						OutputWrite("ranking_change_value=OK");
					else
						OutputWrite("ranking_change_value=error");
				}
				else {
					OutputWrite("ranking_change_value=error");
				}
			}
			else {
				OutputWrite("ranking_change_value=error");
			}
		}
		else {
			OutputWrite("ranking_change_value=error");
		}
	}
	*/
	private void ranking_force_update(String[] cmd) throws IOException {
		if(cmd.length==1){
			if(logged){
				boolean b=Ranking.getInstance().forceUpdate();
				OutputWrite("ranking_force_update="+(b?"OK":"error"));
			}
			else {
				OutputWrite("ranking_force_update=error");
			}
		}
		else {
			OutputWrite("ranking_force_update=error");
		}
	}
	private void version_reload_client_enabled(String[] cmd) throws IOException {
		if(cmd.length==1){
			if(logged){
				VersionCheck.getInstance().loadFile();
				OutputWrite("version_reload_client_enabled=OK");
			}
			else{
				OutputWrite("version_reload_client_enabled=error");
			}
		}
		else {
			OutputWrite("version_reload_client_enabled=error");
		}
	}
	private void version_remove_client_enabled(String[] cmd) throws IOException {
		if(cmd.length==2){
			if(logged){
				try{
					Integer i=Integer.parseInt(cmd[1]);
					boolean b=VersionCheck.getInstance().rimuoviVersione(i);
					OutputWrite("version_remove_client_enabled="+(b?"OK":"error"));
				}
				catch(NumberFormatException e){
					OutputWrite("version_remove_client_enabled=error");
				}
			}
			else {
				OutputWrite("version_remove_client_enabled=error");
			}
		}
		else {
			OutputWrite("version_remove_client_enabled=error");
		}
	}
	private void version_add_client_enabled(String[] cmd) throws IOException {
		if(cmd.length==2){
			if(logged){
				try{
					Integer i=Integer.parseInt(cmd[1]);
					boolean b=VersionCheck.getInstance().aggiungiVersione(i);
					OutputWrite("version_add_client_enabled="+(b?"OK":"error"));
				}
				catch(NumberFormatException e){
					OutputWrite("version_add_client_enabled=error");
				}
			}
			else {
				OutputWrite("version_add_client_enabled=error");
			}
		}
		else {
			OutputWrite("version_add_client_enabled=error");
		}
		
	}
	private void user_delete_id(String[] cmd) throws IOException {
		if(cmd.length==2){
			if(logged){
				try {
					Integer id=Integer.parseInt(cmd[1]);
					boolean del=DBAccount.getInstance().cancellaAccount(id);
					OutputWrite("user_delete_id="+(del?"OK":"error"));
				}
				catch(NumberFormatException e){
					OutputWrite("user_delete_id=error");
				}
			}
			else {
				OutputWrite("user_delete_id=error");
			}
		}
		else {
			OutputWrite("user_delete_id=error");
		}
	}
	private void user_delete_username(String[] cmd) throws IOException {
		if(cmd.length==2){
			if(logged){
				boolean del=DBAccount.getInstance().cancellaAccount(cmd[1]);
				OutputWrite("user_delete_username="+(del?"OK":"error"));
			}
			else {
				OutputWrite("user_delete_username=error");
			}
		}
		else {
			OutputWrite("user_delete_username=error");
		}
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
				Ranking.getInstance().readProperties();
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
