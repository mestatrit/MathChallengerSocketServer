package it.mathchallenger.server.communication;

import org.apache.commons.mail.Email;

import it.mathchallenger.server.tda.Queue;

public class ThreadInvioMessaggi extends Thread {
	Queue<Email> codaMessaggi;

	public ThreadInvioMessaggi(Queue<Email> coda) {
		codaMessaggi = coda;
	}

	public void run() {
		while (true) {
			if (codaMessaggi.isEmpty()) {
				try {
					sleep(5000L);
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
}
