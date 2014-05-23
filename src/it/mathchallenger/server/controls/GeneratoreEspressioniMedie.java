package it.mathchallenger.server.controls;

import java.util.ArrayList;

import it.mathchallenger.server.entities.Domanda;
import de.congrace.exp4j.UnknownFunctionException;
import de.congrace.exp4j.UnparsableExpressionException;

public class GeneratoreEspressioniMedie extends Risolutore {

	@Override
	public Domanda generaDomanda() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void generaRisposteErrate(Domanda d) {
		// TODO Auto-generated method stub

	}

	@Override
	public void risolvi(Domanda d) throws UnknownFunctionException, UnparsableExpressionException {
		// TODO Auto-generated method stub

	}
	private String generaEasy(){
		int[] operatori=new int[2];
		String op=operazioni_easy[rand.nextInt(operazioni_easy.length)];
		switch(op){
			case "+":
			case "-":
				operatori[0]=rand.nextInt(101);
				operatori[1]=rand.nextInt(50)+1;
				break;
			case "*":
				operatori[0]=rand.nextInt(21);
				operatori[1]=rand.nextInt(11);
				break;
			case "/":
				operatori[0]=rand.nextInt(101);
				ArrayList<Integer> divisori=new ArrayList<Integer>();
				operatori[1]=operatori[0]/2;
				while(operatori[1]>1){
					if(operatori[0]%operatori[1]==0)
						divisori.add(operatori[1]);
					operatori[1]--;
				}
				if(divisori.size()==0){
					divisori.add(1);
					divisori.add(-operatori[0]);
					divisori.add(-1);
					divisori.add(operatori[0]);
				}
				operatori[1]=divisori.get(rand.nextInt(divisori.size()));
				break;
		}
		if(rand.nextInt(10)<=2)
			operatori[0]*=(-1);
		return operatori[0]+op+operatori[1];
	}
}
