package it.mathchallenger.server.controls;

import java.util.Random;

import de.congrace.exp4j.UnknownFunctionException;
import de.congrace.exp4j.UnparsableExpressionException;
import it.mathchallenger.server.entities.Domanda;

public abstract class Risolutore {
	protected Random				rand;
	protected final static String[] operazioni_easy = { "+", "-", "*", "/" };

	public Risolutore() {
		rand = new Random(System.currentTimeMillis());
	}

	public abstract Domanda generaDomanda();

	public abstract void generaRisposteErrate(Domanda d);

	public abstract void risolvi(Domanda d) throws UnknownFunctionException, UnparsableExpressionException;
}
