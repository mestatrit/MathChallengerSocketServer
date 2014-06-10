package it.mathchallenger.server.communication.mail;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.mail.EmailException;

public class MailSender extends Thread{
	private static Properties   email_prop;
	private static ThreadInvioMessaggi	   thread_invio;
	private static boolean	  instanced = false;

	private static void init() throws IOException {
		if (!instanced) {
			readProperties();
			thread_invio=new ThreadInvioMessaggi();
			Runtime.getRuntime().addShutdownHook(new Thread() {
				public void run() {
					// TODO salvataggio in database
				}
			});
			// TODO caricamento email da database
		}
	}
	public void run(){
		try {
			init();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void newPasswordMail(String address, String newPass) {
		try {
			thread_invio.add_nuovaPassword(email_prop, address, newPass);
		} 
		catch (EmailException e) {
			e.printStackTrace();
		}
	}

	public static void newUserMail(String address, String username, String pass) {
		try {
			thread_invio.add_nuovoUtente(email_prop, address, username, pass);
		} 
		catch (EmailException e) {
			e.printStackTrace();
		}
	}
	public static void readProperties() throws IOException{
		if(email_prop==null)
			email_prop=new Properties();
		email_prop.clear();
		InputStream in = new FileInputStream("mail.properties");
		email_prop.load(in);
		in.close();
	}
}
