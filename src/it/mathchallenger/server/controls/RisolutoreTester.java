package it.mathchallenger.server.controls;

import it.mathchallenger.server.entities.Domanda;

public class RisolutoreTester {

	public static void main(String[] args) throws InterruptedException {
		while (true) {
			Domanda d = GeneratoreEspressioniFacili.getInstance().generaDomanda();
			// System.out.println(d.getDomanda());
			System.out.println("Risposte: " + d.getRispostaEsatta() + " " + d.getRispostaErrata(1) + " " + d.getRispostaErrata(2) + " " + d.getRispostaErrata(3));
			Thread.sleep(500);
		}
	}
}
