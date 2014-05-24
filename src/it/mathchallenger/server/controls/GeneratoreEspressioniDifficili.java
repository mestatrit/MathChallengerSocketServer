package it.mathchallenger.server.controls;

import java.util.ArrayList;

import sun.security.jca.GetInstance;
import it.mathchallenger.server.entities.Domanda;
import de.congrace.exp4j.UnknownFunctionException;
import de.congrace.exp4j.UnparsableExpressionException;

public class GeneratoreEspressioniDifficili extends Risolutore {
	private static GeneratoreEspressioniDifficili ris;
	public static GeneratoreEspressioniDifficili getInstance(){
		if(ris==null)
			ris=new GeneratoreEspressioniDifficili();
		return ris;
	}
	private GeneratoreEspressioniDifficili(){
		super();
	}
	@Override
	public Domanda generaDomanda() {
		StringBuilder domanda=new StringBuilder("(");
		domanda.append(generaEasy());
		domanda.append(")");
		String op=operazioni_easy[rand.nextInt(operazioni_easy.length-1)];
		domanda.append(op);
		domanda.append("(");
		domanda.append(generaEasy());
		domanda.append(")");
		
		Domanda dom=new Domanda();
		dom.setDomanda(domanda.toString());
		return dom;
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
				operatori[0]=rand.nextInt(30)+1;
				operatori[1]=rand.nextInt(30)+1;
				break;
			case "*":
				operatori[0]=rand.nextInt(11);
				operatori[1]=rand.nextInt(11);
				break;
			case "/":
				operatori[0]=rand.nextInt(50)+1;
				ArrayList<Integer> divisori=new ArrayList<Integer>();
				operatori[1]=operatori[0]/2;
				while(operatori[1]>1){
					if(operatori[0]%operatori[1]==0)
						divisori.add(operatori[1]);
					operatori[1]--;
				}
				if(divisori.size()==0){
					divisori.add(1);
					divisori.add(operatori[0]);
				}
				operatori[1]=divisori.get(rand.nextInt(divisori.size()));
				break;
		}
		if(rand.nextInt(10)<=2)
			operatori[0]*=(-1);
		return operatori[0]+op+operatori[1];
	}
	public static void main(String[] args){
		Risolutore ris=getInstance();
		int i=0;
		while(i<1000){
    		Domanda d=ris.generaDomanda();
    		System.out.println(d.getDomanda());
    		i++;
		}
	}
}
