package it.mathchallenger.server.communication;

import it.mathchallenger.server.controls.DBAccount;
import it.mathchallenger.server.controls.DBPartita;
import it.mathchallenger.server.controls.GestionePartite;
import it.mathchallenger.server.entities.Account;
import it.mathchallenger.server.entities.Partita;
import it.mathchallenger.server.storage.LoggerManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.logging.Logger;

public class SocketService implements Runnable {
	private Socket comm;
	private InputStream input;
	private OutputStream output;
	
	private static Logger logger=LoggerManager.newLogger("SocketService");
	
	private Account account;
	
	public SocketService(Socket com) {
		comm=com;
		try {
			comm.setKeepAlive(true);
		} 
		catch (SocketException e1) {
			e1.printStackTrace();
		}
		try {
			input=com.getInputStream();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		try {
			output=com.getOutputStream();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void run(){
		byte[] readed=new byte[2048];
		svuotaBuffer(readed, 2048);
		while(!comm.isClosed()){
			try {
				int read=0;
				if((read=input.read(readed))>0){
					String str=new String(readed);
					str=str.substring(0, read).trim();
					System.out.println(str);
					String[] cmd=str.split(" ");
					switch(cmd[0]){
						case "exit":
							if(cmd.length==1){
								if(account==null){
									OutputWrite("exit=error;message=You must be logged in");
									break;
								}
								GestionePartite.getInstance().esceUtente(account);
								closeConnection();
								break;
							}
							else 
								OutputWrite("exit=error;message=Usage: exit");
						case "login":
							if(cmd.length==3){
								if(account!=null){
									OutputWrite("login=error;message=You already are logged in");
									break;
								}
								String user=cmd[1].trim();
								String pass=cmd[2].trim();
								account=DBAccount.getInstance().login(user, pass);
								if(account!=null)
									OutputWrite("login=OK;authcode="+account.getAuthCode()+";id="+account.getID());
								else
									OutputWrite("login=error");
							}
							else 
								OutputWrite("login=error;message=Usage: login username password");
							break;
						case "login-authcode":
							if(cmd.length==3){
								if(account!=null){
									OutputWrite("login=error;message=You already are logged in");
									break;
								}
								int id=Integer.parseInt(cmd[1].trim());
								String auth=cmd[2].trim();
								System.out.println("authcode login: "+auth);
								account=DBAccount.getInstance().login(id, auth);
								if(account!=null)
									OutputWrite("login=OK");
								else 
									OutputWrite("login=error;message=invalid authcode");
							}
							else 
								OutputWrite("login=error;message=Usage: login-auth id authcode");
							break;
						case "logout":
							if(cmd.length==1){
								if(account==null){
									OutputWrite("logout=error;message=You must be logged in");
									break;
								}
								GestionePartite.getInstance().esceUtente(account);
								boolean logout=DBAccount.getInstance().logout(account.getUsername(), account.getAuthCode());
								if(logout){
									GestionePartite.getInstance().esceUtente(account);
									OutputWrite("logout=OK");
									closeConnection();
								}
								else
									OutputWrite("logout=error");
							}
							else 
								OutputWrite("logout=error;message=Usage: logout");
							break;
						case "change-psw":
							if(cmd.length==2){
								if(account==null){
									OutputWrite("change-psw=error;message=You must be logged in");
									break;
								}
								String pass=cmd[1].trim();
								boolean change=DBAccount.getInstance().changePassword(account, pass);
								if(change)
									OutputWrite("change-psw=OK");
								else 
									OutputWrite("change-psw=error");
							}
							else 
								OutputWrite("change-psw=error;message=Usage: change-psw newpassword");
							break;
						case "reset-psw":
							if(cmd.length==2){
								boolean reset=DBAccount.getInstance().resetPasswordByUsername(cmd[1]);
								OutputWrite("reset-psw="+(reset==true?"OK":"error"));
							}
							else
								OutputWrite("reset-psw=error;message=Usage: reset-psw username");
							break;
						case "register":
							if(cmd.length==4){
								if(account!=null){
									OutputWrite("register=error;message=You already are logged in");
									break;
								}
								String user=cmd[1];
								String pass=cmd[2];
								String email=cmd[3];
								DBAccount dba=DBAccount.getInstance();
								if(dba.isAccountExist(user))
									OutputWrite("register=error;message=username in uso");
								else {
									account=dba.registra(user, pass, email);
									if(account!=null){
										GestionePartite.getInstance().entraUtente(account);
										OutputWrite("register=OK;authcode="+account.getAuthCode()+";id="+account.getID());
									}
									else
										OutputWrite("register=error");
								}
							}
							else 
								OutputWrite("register=error;message=Usage: register username password email");
							break;
						case "newgame":
							if(cmd.length==2){
								if(account==null){
									OutputWrite("newgame=error;message=You must be logged in");
									break;
								}
								Integer id_utente_sfidato=Integer.parseInt(cmd[1]);
								if(id_utente_sfidato==account.getID()){
									OutputWrite("newgame=error;message=You cant challenge yourself");
									break;
								}
								Partita partita=DBPartita.getInstance().creaPartita(account.getID(), id_utente_sfidato);
								OutputWrite("newgame=OK;id="+partita.getIDPartita());
							}
							else 
								OutputWrite("newgame=error;message=Usage: newgame idutentesfidato");
							break;
						case "abandon":
							
							break;
						case "answer":
							
							break;
						case "addfriend":
							
							break;
						case "removefriend":
							
							break;
						case "listchallenge":
							
							break;
						case "search-user":
							if(cmd.length==2){
								if(account==null){
									OutputWrite("search-user=error;messagge=You must be logged in");
									break;
								}
								ArrayList<Account> results=DBAccount.getInstance().searchUser(cmd[1]);
								StringBuilder out=new StringBuilder("Search-User=OK;trovati="+results.size());
								for(int i=0;i<results.size();i++){
									Account acc=results.get(i);
									out.append(";utente="+acc.getUsername()+",id="+acc.getID());
								}
								results.clear();
								results=null;
								OutputWrite(out.toString());
							}
							else
								OutputWrite("search-user=error;message=Usage: search-user nomeutente");
							break;
						default:
							//logger.severe(str);
							break;
					}
					svuotaBuffer(readed, 2048);
				}
			} 
			catch (SocketException e) {
				logger.severe(e.getMessage());
				e.printStackTrace();
				break;
			} 
			catch (IOException e) {
				logger.severe(e.getMessage());
				e.printStackTrace();
			}
			try {
				Thread.sleep(100L);
			} 
			catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(account!=null)
			GestionePartite.getInstance().esceUtente(account);
	}
	private void svuotaBuffer(byte[] buff, int size){
		for(int i=0;i<size;i++)
			buff[i]=0;
	}
	private boolean closeConnection(){
		try {
			comm.close();
			return true;
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	private void OutputWrite(String s) throws IOException{
		if(!s.endsWith("\n"))
			s+="\n";
		output.write(s.getBytes());
		output.flush();
	}
}
