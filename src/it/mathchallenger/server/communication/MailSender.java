package it.mathchallenger.server.communication;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.mail.EmailException;

public class MailSender extends Thread{
	private static Properties   email_prop;
	//private static Queue<Email> codaMessaggi;
	//private static Thread	   thread_invio;
	private static boolean	  instanced = false;

	private static void init() throws IOException {
		if (!instanced) {
			InputStream in = new FileInputStream("mail.properties");
			email_prop = new Properties();
			email_prop.load(in);
			Runtime.getRuntime().addShutdownHook(new Thread() {
				public void run() {
					// TODO salvataggio in database
				}
			});
			//codaMessaggi = new NodeQueue<Email>();
			//thread_invio = new ThreadInvioMessaggi(codaMessaggi);
			//thread_invio.start();

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

	public static void newPasswordMail(String address, String newPass) throws EmailException {
		class MessageToSend extends Thread {
			String body, address;
			public MessageToSend(String message, String to){
				body=message;
				address=to;
			}
			public void run(){
				Properties props = new Properties();
		        Session session = Session.getDefaultInstance(props, null);

		        try {
		            Message msg = new MimeMessage(session);
		            msg.setFrom(new InternetAddress(email_prop.getProperty("email"), email_prop.getProperty("nome-visualizzato")));
		            msg.addRecipient(Message.RecipientType.TO,new InternetAddress(address));
		            msg.setSubject("[MathChallenger] - Reset Password");
		            msg.setText(body);
		            Transport.send(msg, email_prop.getProperty("username"), email_prop.getProperty("password"));

		        } catch (AddressException e) {
		        	e.printStackTrace();
		            // ...
		        } catch (MessagingException e) {
		        	e.printStackTrace();
		            // ...
		        }
		        catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		Thread t=new MessageToSend("Your new password is: " + newPass, address);
		t.start();
		/*
		Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);
        String msgBody = ;

        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(email_prop.getProperty("email"), email_prop.getProperty("nome-visualizzato")));
            msg.addRecipient(Message.RecipientType.TO,new InternetAddress(address));
            msg.setSubject("[MathChallenger] - Reset Password");
            msg.setText(msgBody);
            Transport.send(msg, email_prop.getProperty("username"), email_prop.getProperty("password"));

        } catch (AddressException e) {
        	e.printStackTrace();
            // ...
        } catch (MessagingException e) {
        	e.printStackTrace();
            // ...
        }
        catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		/*
		Email email = new SimpleEmail();
		email.setHostName(email_prop.getProperty("smtp-server"));
		email.setSmtpPort(Integer.parseInt(email_prop.getProperty("smtp-port")));
		email.setAuthenticator(new DefaultAuthenticator(email_prop.getProperty("username"), email_prop.getProperty("password")));
		email.setSSLOnConnect(Boolean.parseBoolean(email_prop.getProperty("ssl")));
		email.setFrom(email_prop.getProperty("email"));
		email.setSubject("[MathChallenger] - Reset Password");
		email.setMsg("Your new password is: " + newPass);
		email.addTo(address);
		email.send();
		//aggiungiInCoda(email);
		
		*/
		
	}

	public static void newUserMail(String address, String username, String pass) throws EmailException {
		class MessageToSend extends Thread {
			String body, address;
			public MessageToSend(String message, String to){
				body=message;
				address=to;
			}
			public void run(){
				Properties props = new Properties();
		        Session session = Session.getDefaultInstance(props, null);

		        try {
		            Message msg = new MimeMessage(session);
		            msg.setFrom(new InternetAddress(email_prop.getProperty("email"), email_prop.getProperty("nome-visualizzato")));
		            msg.addRecipient(Message.RecipientType.TO,new InternetAddress(address));
		            msg.setSubject("[MathChallenger] - Welcome");
		            msg.setText(body);
		            Transport.send(msg, email_prop.getProperty("username"), email_prop.getProperty("password"));

		        } catch (AddressException e) {
		        	e.printStackTrace();
		            // ...
		        } catch (MessagingException e) {
		        	e.printStackTrace();
		            // ...
		        }
		        catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		Thread t=new MessageToSend("Thank you for joining us.\nHere are your account details:\n\nUsername: " + username + "\nPassword: " + pass + "\n\n", address);
		t.start();
		/*
		Email email = new SimpleEmail();
		email.setHostName(email_prop.getProperty("smtp-server"));
		email.setSmtpPort(Integer.parseInt(email_prop.getProperty("smtp-port")));
		email.setAuthenticator(new DefaultAuthenticator(email_prop.getProperty("username"), email_prop.getProperty("password")));
		email.setSSLOnConnect(Boolean.parseBoolean(email_prop.getProperty("ssl")));
		email.setFrom(email_prop.getProperty("email"));
		email.setSubject("[MathChallenger] - Welcome");
		email.setMsg("Thank you for joining us.\nHere are your account details:\n\nUsername: " + username + "\nPassword: " + pass + "\n\n");
		email.addTo(address);
		email.send();
		*/
		//aggiungiInCoda(email);
	}
	
/*
	private static void aggiungiInCoda(Email mail) {
		codaMessaggi.enqueue(mail);
	}
*/
}
