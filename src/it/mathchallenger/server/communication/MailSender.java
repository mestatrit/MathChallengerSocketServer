package it.mathchallenger.server.communication;

import it.mathchallenger.server.tda.NodeQueue;
import it.mathchallenger.server.tda.Queue;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;

public class MailSender {
	private static Properties email_prop;
	private static Queue<Email> codaMessaggi;
	private static Thread thread_invio;
	private static boolean instanced=false;
	
	public static void init() throws IOException{
		if(!instanced){
			InputStream in=new FileInputStream("mail.properties");
			email_prop=new Properties();
			email_prop.load(in);
			Runtime.getRuntime().addShutdownHook(new Thread(){
				public void run(){
					//TODO salvataggio in database
				}
			});
			codaMessaggi=new NodeQueue<Email>();
			thread_invio=new ThreadInvioMessaggi(codaMessaggi);
			thread_invio.start();
			
			//TODO caricamento email da database
		}
	}
	
	public static void newPasswordMail(String address, String newPass) throws EmailException{
		Email email=new SimpleEmail();
		email.setHostName(email_prop.getProperty("smtp-server"));
		email.setSmtpPort(Integer.parseInt(email_prop.getProperty("smtp-port")));
		email.setAuthenticator(new DefaultAuthenticator(email_prop.getProperty("username"), email_prop.getProperty("password")));
		email.setSSLOnConnect(Boolean.parseBoolean(email_prop.getProperty("ssl")));
		email.setFrom(email_prop.getProperty("email"));
		email.setSubject("[MathChallenger] - Reset Password");
		email.setMsg("Your new password is: "+newPass);
		email.addTo(address);
		
		aggiungiInCoda(email);
	}
	public static void newUserMail(String address, String username, String pass) throws EmailException{
		Email email=new SimpleEmail();
		email.setHostName(email_prop.getProperty("smtp-server"));
		email.setSmtpPort(Integer.parseInt(email_prop.getProperty("smtp-port")));
		email.setAuthenticator(new DefaultAuthenticator(email_prop.getProperty("username"), email_prop.getProperty("password")));
		email.setSSLOnConnect(Boolean.parseBoolean(email_prop.getProperty("ssl")));
		email.setFrom(email_prop.getProperty("email"));
		email.setSubject("[MathChallenger] - Welcome");
		email.setMsg("Thank you for joining us.\nHere are your account details:\n\nUsername: "+username+"\nPassword: "+pass+"\n\n");
		email.addTo(address);
		
		aggiungiInCoda(email);
	}
	private static void aggiungiInCoda(Email mail){
		codaMessaggi.enqueue(mail);
	}
	
}
