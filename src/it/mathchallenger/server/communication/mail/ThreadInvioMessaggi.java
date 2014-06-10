package it.mathchallenger.server.communication.mail;

import java.util.Properties;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;

import it.mathchallenger.server.tda.NodeQueue;
import it.mathchallenger.server.tda.Queue;

public class ThreadInvioMessaggi extends Thread {
	private Queue<Email> codaMessaggi;

	public ThreadInvioMessaggi() {
		codaMessaggi = new NodeQueue<Email>();
	}

	public void run() {
		while (true) {
			if (codaMessaggi.isEmpty()) {
				try {
					sleep(30000L);
				}
				catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			else {
				Email mail = codaMessaggi.dequeue();
				if (mail != null) {
					try {
						System.out.println("Invio email in corso...");
						mail.send();
					}
					catch (Exception e) {
						e.printStackTrace();
						codaMessaggi.enqueue(mail);
					}
				}
			}
		}
	}
	public void add_nuovaPassword(Properties email_prop, String address, String newPass) throws EmailException {
		Email email = new SimpleEmail();
		email.setHostName(email_prop.getProperty("smtp-server"));
		email.setSmtpPort(Integer.parseInt(email_prop.getProperty("smtp-port")));
		email.setAuthenticator(new DefaultAuthenticator(email_prop.getProperty("username"), email_prop.getProperty("password")));
		email.setSSLOnConnect(Boolean.parseBoolean(email_prop.getProperty("ssl")));
		email.setFrom(email_prop.getProperty("email"));
		email.setSubject("[MathChallenger] - Reset Password");
		email.setMsg("Your new password is: " + newPass);
		email.addTo(address);
		codaMessaggi.enqueue(email);
		//email.send();
	}
	public void add_nuovoUtente(Properties email_prop, String address, String username, String pass) throws EmailException {
		Email email = new SimpleEmail();
		email.setHostName(email_prop.getProperty("smtp-server"));
		email.setSmtpPort(Integer.parseInt(email_prop.getProperty("smtp-port")));
		email.setAuthenticator(new DefaultAuthenticator(email_prop.getProperty("username"), email_prop.getProperty("password")));
		email.setSSLOnConnect(Boolean.parseBoolean(email_prop.getProperty("ssl")));
		email.setFrom(email_prop.getProperty("email"));
		email.setSubject("[MathChallenger] - Welcome");
		email.setMsg("Thank you for joining us.\nHere are your account details:\n\nUsername: " + username + "\nPassword: " + pass + "\n\n");
		email.addTo(address);
		codaMessaggi.enqueue(email);
	}
}
