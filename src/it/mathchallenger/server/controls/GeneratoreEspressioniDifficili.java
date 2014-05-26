package it.mathchallenger.server.controls;

import java.util.ArrayList;

import it.mathchallenger.server.entities.Domanda;
import de.congrace.exp4j.Calculable;
import de.congrace.exp4j.ExpressionBuilder;
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
	private float getRandomRisposta(Domanda d){
		if(d.getRispostaErrata(1)==0 && d.getRispostaErrata(2)==0 && d.getRispostaErrata(3)==0)
			return d.getRispostaEsatta();
		else {
			int errate_ok=0;
			if(d.getRispostaErrata(3)!=0)
				errate_ok=3;
			else if(d.getRispostaErrata(2)!=0)
				errate_ok=2;
			else if(d.getRispostaErrata(1)!=0)
				errate_ok=1;
			else
				return d.getRispostaEsatta();
			int rand_n=rand.nextInt(4*(errate_ok+1))/4;
			switch(rand_n){
				case 0:
					return d.getRispostaEsatta();
				case 1:
					return d.getRispostaErrata(1);
				case 2:
					return d.getRispostaErrata(2);
				case 3:
					return d.getRispostaErrata(3);
				default:
					return 0;
			}
		}
	}
	private boolean isPresenteRisposta(Domanda d, float r){
		if(Float.compare(r, d.getRispostaEsatta())==0)
			return true;
		if(Float.compare(r, d.getRispostaErrata(1))==0)
			return true;
		if(Float.compare(r, d.getRispostaErrata(2))==0)
			return true;
		if(Float.compare(r, d.getRispostaErrata(3))==0)
			return true;
		return false;
	}
	@Override
	public void risolvi(Domanda d) throws UnknownFunctionException, UnparsableExpressionException {
		Calculable calc = new ExpressionBuilder(d.getDomanda()).build();
		float res=(float) calc.calculate();
		d.setRispostaEsatta(res);
	}
	private String generaEasy(){
		int[] operatori=new int[2];
		String op=operazioni_easy[rand.nextInt(operazioni_easy.length)];
		switch(op){
			case "+":
			case "-":
				operatori[0]=rand.nextInt(20)+1;
				operatori[1]=rand.nextInt(20)+1;
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
					if(operatori[0]!=0)
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
    		System.out.println(d.getDomanda()+ "\t\t"+d.getRispostaEsatta()+" "+d.getRispostaErrata(1)+" "+d.getRispostaErrata(2)+" "+d.getRispostaErrata(3));
    		i++;
		}
	}
}
