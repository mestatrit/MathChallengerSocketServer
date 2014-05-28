package it.mathchallenger.server.controls;

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
		String op_middle=operazioni_easy[rand.nextInt(operazioni_easy.length-1)];
		String op1="",op2="";
		switch(op_middle){
			case "+":
			case "-":
			case "*":
				int r1=rand.nextInt(12);
				if(r1<3)
					op1=generaAddizione();
				else if(r1<6)
					op1=generaSottrazione();
				else if(r1<9)
					op1=generaMoltiplicazione();
				else
					op1=generaDivisione();
				
				int r2=rand.nextInt(12);
				if(r2<3)
					op2=generaAddizione();
				else if(r2<6)
					op2=generaSottrazione();
				else if(r2<9)
					op2=generaMoltiplicazione();
				else
					op2=generaDivisione();
		}
		StringBuilder domanda=new StringBuilder("(");
		domanda.append(op1);
		domanda.append(")");
		domanda.append(op_middle);
		domanda.append("(");
		domanda.append(op2);
		domanda.append(")");
		
		Domanda dom=new Domanda();
		dom.setDomanda(domanda.toString());
		try {
			risolvi(dom);
			generaRisposteErrate(dom);
		}
		catch (UnknownFunctionException | UnparsableExpressionException e) {
			e.printStackTrace();
			return generaDomanda();
		}
		return dom;
	}

	@Override
	public void generaRisposteErrate(Domanda d) {
		for(int i=1;i<=3;){
			int shift=rand.nextInt(3)+1;
			float newR=getRandomRisposta(d);
			if(rand.nextInt(4)<=1){
				newR-=shift;
			}
			else
				newR+=shift;
			if(!isPresenteRisposta(d, newR)){
				d.setRispostaErrata(i, newR);
				i++;
			}
		}
	}
	public static void main(String[] args){
		Risolutore ris=getInstance();
		int i=0;
		while(i<1000){
    		Domanda d=ris.generaDomanda();
    		System.out.println(d.getDomanda()+ "\t\t"+d.getRispostaEsatta()+" "+d.getRispostaErrata(1)+" "+d.getRispostaErrata(2)+" "+d.getRispostaErrata(3));
    		i++;
		}
	}
}
