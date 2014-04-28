package it.mathchallenger.server.controls;

import de.congrace.exp4j.UnknownFunctionException;
import de.congrace.exp4j.UnparsableExpressionException;
import it.mathchallenger.server.entities.Domanda;

public interface Risolutore {
	public Domanda generaDomanda();
	public void generaRisposteErrate(Domanda d);
	public void risolvi(Domanda d) throws UnknownFunctionException, UnparsableExpressionException;
}
