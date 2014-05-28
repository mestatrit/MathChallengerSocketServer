package it.mathchallenger.server.controls;

import java.util.ArrayList;
import java.util.logging.Logger;

import de.congrace.exp4j.UnknownFunctionException;
import de.congrace.exp4j.UnparsableExpressionException;
import it.mathchallenger.server.entities.Domanda;
import it.mathchallenger.server.storage.LoggerManager;

public class GeneratoreEspressioniFacili extends Risolutore {
	private static GeneratoreEspressioniFacili generatore;
	private static Logger					  logger;

	private GeneratoreEspressioniFacili() {
		super();
		logger = LoggerManager.newLogger(getClass().getName());
	}

	public static GeneratoreEspressioniFacili getInstance() {
		if (generatore == null) {
			generatore = new GeneratoreEspressioniFacili();
		}
		return generatore;
	}

	public Domanda generaDomanda() {
		String op = operazioni_easy[rand.nextInt(operazioni_easy.length)];
		int op1 = 0, op2 = 0;
		try {
			switch (op) {
				case "+":
				case "-":
					op1 = rand.nextInt(101);
					op2 = rand.nextInt(op1 > 1 ? op1 / 2 : 2) + 1;
					break;
				case "*":
					op1 = rand.nextInt(21);
					op2 = rand.nextInt(11);
					break;
				case "/":
					op1 = rand.nextInt(100)+1;
					ArrayList<Integer> divisori = new ArrayList<Integer>();
					op2 = op1 / 2;
					while (op2 > 1) {
						if (op1 % op2 == 0)
							divisori.add(op2);
						op2--;
					}
					if (divisori.size() == 0) {
						divisori.add(1);
						divisori.add(-1);
						if(op1!=0){
    						divisori.add(-op1);
    						divisori.add(op1);
						}
					}
					op2 = divisori.get(rand.nextInt(divisori.size()));
					break;
				default:
					System.out.println("Operatore non valido");
					break;
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			System.out.println("Causa dell'errore: " + op1 + op + op2);
		}
		if (rand.nextInt(10) <= 2)
			op1 *= (-1);
		String domanda = op1 + op + op2;
		System.out.println("Domanda: " + domanda);
		Domanda domanda_d = new Domanda();
		domanda_d.setDomanda(domanda);
		try {
			risolvi(domanda_d);
		}
		catch (UnknownFunctionException | UnparsableExpressionException e) {
			logger.severe(e.getMessage());
			e.printStackTrace();
			return generaDomanda();
		}
		generaRisposteErrate(domanda_d, op, 1);
		return domanda_d;
	}
	
	private void generaRisposteErrate(Domanda d, String operazione, int domanda) {
		if (domanda >= 4)
			return;
		if (domanda == 1) {
			switch (operazione) {
				case "+": {
					String[] operatori = d.getDomanda().split("\\+");
					if ((Integer.parseInt(operatori[0]) + Integer.parseInt(operatori[1])) < d.getRispostaEsatta()) {
						int op1 = Integer.parseInt(operatori[0]) * (-1);
						int op2 = Integer.parseInt(operatori[1]);
						d.setRispostaErrata(domanda, op1 + op2);
					}
					else {
						d.setRispostaErrata(domanda, d.getRispostaEsatta() + rand.nextInt(3) + 1);
					}
				}
				case "-": {
					String[] operatori = d.getDomanda().split("-");
					int[] op = new int[2];
					int cur_op = 0;
					int i = 0;
					while (i < operatori.length) {
						try {
							int oper = Integer.parseInt(operatori[i]);
							op[cur_op] = oper;
							cur_op++;
						}
						catch (NumberFormatException e) {}
						i++;
					}
					if (cur_op == 2 && (op[0] - op[1]) > d.getRispostaEsatta()) {
						d.setRispostaErrata(domanda, op[0] - op[1]);
					}
					else {
						d.setRispostaErrata(domanda, d.getRispostaEsatta() + rand.nextInt(3) + 1);
					}
				}
				case "*":
				case "/":
					if (d.getRispostaEsatta() < 0)
						d.setRispostaErrata(domanda, d.getRispostaEsatta() * (-1));
					else {
						d.setRispostaErrata(domanda, d.getRispostaEsatta() + rand.nextInt(3) + 1);
					}
			}
		}
		else {
			if (domanda < 4) {
				int shift = rand.nextInt(3) + 1;
				if (domanda == 2) {
					if (rand.nextBoolean()) {
						float newErr = d.getRispostaEsatta() + shift;
						if (!isPresenteRisposta(d, newErr))
							d.setRispostaErrata(domanda, newErr);
						else
							generaRisposteErrate(d, operazione, domanda);
					}
					else {
						float newErr = d.getRispostaEsatta() - shift;
						if (!isPresenteRisposta(d, newErr))
							d.setRispostaErrata(domanda, newErr);
						else
							generaRisposteErrate(d, operazione, domanda);
					}
				}
				else if (domanda == 3) {
					if (rand.nextBoolean()) {
						float newErr = d.getRispostaErrata(rand.nextInt(3) + 1) + shift;
						if (!isPresenteRisposta(d, newErr))
							d.setRispostaErrata(domanda, newErr);
						else
							generaRisposteErrate(d, operazione, domanda);
					}
					else {
						float newErr = d.getRispostaErrata(rand.nextInt(3) + 1) - shift;
						if (!isPresenteRisposta(d, newErr))
							d.setRispostaErrata(domanda, newErr);
						else
							generaRisposteErrate(d, operazione, domanda);
					}
				}
			}
		}
		generaRisposteErrate(d, operazione, domanda + 1);
	}
	public void generaRisposteErrate(Domanda d) {}
}
