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
	private static int PING_TIMEOUT=60000;
	private static Logger logger=LoggerManager.newLogger("SocketService");
	
	private Account account;
	
	public SocketService(Socket com) {
		comm=com;
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
		int timer_ping=0;
		while(!comm.isClosed() && comm.isBound()){
			try {
				int read=0;
				if((read=input.read(readed))>0){
					String str=new String(readed);
					str=str.substring(0, read).trim();
					System.out.println(str);
					String[] cmd=str.split(" ");
					switch(cmd[0]){
						case "ping":
							if(cmd.length==1){
								timer_ping=0;
								OutputWrite("ping=ok");
								break;
							}
						case "exit":
							if(cmd.length==1){
								if(account==null){
									OutputWrite("exit=error;message=You must be logged in");
									break;
								}
								OutputWrite("exit=OK");
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
								timer_ping=0;
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
								timer_ping=0;
								int id=Integer.parseInt(cmd[1].trim());
								String auth=cmd[2].trim();
								//System.out.println("authcode login: "+auth);
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
							if(cmd.length==3){
								if(account==null){
									OutputWrite("change-psw=error;message=You must be logged in");
									break;
								}
								String oldPass=cmd[1].trim();
								String newPass=cmd[2].trim();
								Account acc=DBAccount.getInstance().login(account.getUsername(), oldPass);
								if(acc==null){
									OutputWrite("change-psw=error;message=La vecchia password è errata");
									break;
								}
								boolean change=DBAccount.getInstance().changePassword(account, newPass);
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
						case "getPartiteInCorso":
							if(cmd.length==1){
								if(account==null){
									OutputWrite("getPartiteInCorso=error;message=You must be logged in");
									break;
								}
								ArrayList<Partita> partite=DBPartita.getInstance().getPartiteByUser(account.getID());
								StringBuilder res=new StringBuilder("getPartiteInCorso=OK");
								if(partite.size()>0){
									for(int i=0;i<partite.size();i++){
										Partita p=partite.get(i);
										Account sfidato=null;
										if(p.getIDUtente2()<=0){
											//System.out.println("account2 null");
											//sfidato=GestionePartite.getInstance().getBotByID(p.getIDUtente2())
											sfidato=GestionePartite.getInstance().getBotRandom();
										}
										else {
											int id_s=account.getID()==p.getIDUtente1()?p.getIDUtente2():p.getIDUtente1();
											sfidato=DBAccount.getInstance().getAccountByID(id_s);
										}
										if(sfidato==null)
											continue;
										res.append(";partita="+p.getIDPartita()+","+sfidato.getID()+","+sfidato.getUsername()+","+p.getStatoPartita());
										/*
										Partita p=partite.get(i);
										Integer id_sfidante=account.getID()==p.getIDUtente1()?p.getIDUtente2():p.getIDUtente1();
										Account acc_sfidante = null;
										if(id_sfidante==null)
											acc_sfidante=GestionePartite.getInstance().getBotRandom();
										else
											acc_sfidante=DBAccount.getInstance().getAccountByID(id_sfidante);
										if(acc_sfidante==null)
											continue;
										res.append(";partita="+p.getIDPartita()+","+id_sfidante+","+acc_sfidante.getUsername()+","+p.getStatoPartita());
										*/
									}
								}
								OutputWrite(res.toString());
								break;
							}
							else {
								OutputWrite("getPartiteInCorso=error;message=Usage: getPartiteInCorso");
							}
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
						case "newgame-random":
							if(cmd.length==1){
								if(account==null){
									OutputWrite("newgame-random=error;message=You must be logged in");
									break;
								}
								Account acc_sfidante=GestionePartite.getInstance().accountRandom(account.getID());
								int id_s=acc_sfidante.getID();
								Partita p=DBPartita.getInstance().creaPartita(account.getID(), id_s<0?0:id_s);
								OutputWrite("newgame-random=OK;id="+p.getIDPartita());
							}
							else 
								OutputWrite("newgame-random=error;message=Usage: newgame-random");
							break;
						case "getDettagliPartita":
							if(cmd.length==2){
								if(account==null){
									OutputWrite("getDettagliPartita=error;message=You must be logged in");
									break;
								}
								int id_partita=Integer.parseInt(cmd[1]);
								Partita p=DBPartita.getInstance().getPartitaByID(id_partita);
								if(p!=null){
									boolean playerOK=p.getIDUtente1()==account.getID() || p.getIDUtente2()==account.getID();
									if(playerOK){
										boolean risposto1=p.hasUtente1Risposto();
										boolean risposto2=p.hasUtente2Risposto();
										int utente_n=p.getIDUtente1()==account.getID()?1:2;
										int stato=p.getStatoPartita();
										StringBuilder r=new StringBuilder();
										switch(utente_n){
											case 1:
												r.append("getDettagliPartita=OK;domande="+p.getNumeroDomande()+";stato_partita="+stato+";utente=1;hai_risposto="+(risposto1?1:0)+";tue_risposte=");
												for(int i=0;i<p.getNumeroDomande();i++){
													r.append(p.getDomanda(i).getUser1Risposto());
													if(i<p.getNumeroDomande()-1)
														r.append(",");	
												}
												r.append(";avversario_risposto="+(risposto2?1:0)+";avversario_risposte=");
												for(int i=0;i<p.getNumeroDomande();i++){
													r.append(p.getDomanda(i).getUser2Risposto());
													if(i<p.getNumeroDomande()-1)
														r.append(",");
												}
												break;
											case 2:
												r.append("getDettagliPartita=OK;domande="+p.getNumeroDomande()+";stato_partita="+stato+";utente=2;hai_risposto="+(risposto2?1:0)+";tue_risposte=");
												for(int i=0;i<p.getNumeroDomande();i++){
													r.append(p.getDomanda(i).getUser2Risposto());
													if(i<p.getNumeroDomande()-1)
														r.append(",");	
												}
												r.append(";avversario_risposto="+(risposto1?1:0)+";avversario_risposte=");
												for(int i=0;i<p.getNumeroDomande();i++){
													r.append(p.getDomanda(i).getUser1Risposto());
													if(i<p.getNumeroDomande()-1)
														r.append(",");
												}
												break;
										}
										OutputWrite(r.toString());
									}
									else {
										OutputWrite("getDettagliPartita=error;message=This is not your match");
									}
								}
								else {
									OutputWrite("getDettagliPartita=error;message=Match not found");
								}
							}
							else
								OutputWrite("getDettagliPartita=error;message=Usage: getDettagliPartita idPartita");
							break;
						case "abandon":
							if(cmd.length==2){
								if(account==null){
									OutputWrite("abandon=error;message=You must be logged in");
									break;
								}
								Integer id_partita=Integer.parseInt(cmd[1]);
								if(DBPartita.getInstance().abbandonaPartita(id_partita, account.getID())){
									OutputWrite("abandon=OK");
								}
								else {
									OutputWrite("abandon=error;message=Retry later");
								}
							}
							else 
								OutputWrite("abandon=error;message=Usage: abandon idpartita");
							break;
						case "answer":
							if(cmd.length==8){
								if(account==null){
									OutputWrite("answer=error;message=You must be logged in");
									break;
								}
								int id=Integer.parseInt(cmd[1]);
								float[] risposte=new float[6];
								for(int i=2,j=0;i<8;i++,j++){
									float f=Float.parseFloat(cmd[i]);
									risposte[j]=f;
								}
								DBPartita.getInstance().rispondiDomande(id, account.getID(), risposte);
								OutputWrite("answer=OK");
								break;
							}
							else {
								OutputWrite("answer=error;message=Usage: answer idpartita r1 r2 r3 r4 r5 r6");
								break;
							}
						case "addfriend":
							if(cmd.length==2){
								if(account==null){
									OutputWrite("addfriend=error;message=You must be logged in");
									break;
								}
								int idAmico=Integer.parseInt(cmd[1]);
								if(idAmico==0 || idAmico==account.getID()){
									OutputWrite("addfriend=error;message=You can't add this account to friends");
									break;
								}
								DBAccount.getInstance().addFriend(account, idAmico);
								OutputWrite("addfriend=OK");
							}
							else
								OutputWrite("addfriend=error;message=Usage: addfriend idamico");
							break;
						case "removefriend":
							if(cmd.length==2){
								if(account==null){
									OutputWrite("removefriend=error;message=You must be logged in");
									break;
								}
								int id_amico=Integer.parseInt(cmd[1]);
								DBAccount.getInstance().removeFriend(account, id_amico);
								OutputWrite("removefriend=OK");
							}
							else
								OutputWrite("removefriend=error;message=Usage: removefriend idamico");
							break;
						case "getMyFriends":
							if(cmd.length==1){
								if(account==null){
									OutputWrite("getMyFriends=error;message=You must be logged in");
									break;
								}
								ArrayList<Account> amici=DBAccount.getInstance().getListaAmici(account);
								StringBuilder res=new StringBuilder("getMyFriends=OK;trovati="+amici.size());
								for(int i=0;i<amici.size();i++){
									Account acc=amici.get(i);
									res.append(";account="+acc.getID()+","+acc.getUsername());
								}
								OutputWrite(res.toString());
							}
							else
								OutputWrite("getMyFriends=error;message=Usage: getMyFriends");
							break;
							
						case "isValidVersion":
							break;
							
						case "search-user":
							if(cmd.length==2){
								if(account==null){
									OutputWrite("search-user=error;messagge=You must be logged in");
									break;
								}
								ArrayList<Account> results=DBAccount.getInstance().searchUser(account,cmd[1]);
								StringBuilder out=new StringBuilder("search-user=OK;trovati="+results.size());
								for(int i=0;i<results.size();i++){
									Account acc=results.get(i);
									out.append(";utente="+acc.getUsername()+","+acc.getID());
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
				else {
					timer_ping+=100;
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
				e.printStackTrace();
				try {
					comm.close();
				}
				catch (IOException e1) {
					e1.printStackTrace();
				}
				break;
			}
			
			if(timer_ping>PING_TIMEOUT){
				if(account!=null){
					break;
				}
			}
		}
		
		if(account!=null){
			System.out.println("Termine thread: "+account.getUsername());
			GestionePartite.getInstance().esceUtente(account);
		}
		else 
			System.out.println("Termine thread connessione");
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
