package it.mathchallenger.server.controls;

import java.util.ArrayList;
import java.util.Random;

import de.congrace.exp4j.Calculable;
import de.congrace.exp4j.ExpressionBuilder;
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

	public void risolvi(Domanda d) throws UnknownFunctionException, UnparsableExpressionException{
		Calculable calc = new ExpressionBuilder(d.getDomanda()).build();
		float res = (float) calc.calculate();
		d.setRispostaEsatta(res);
	}
	protected String generaAddizione(){
		int op1=rand.nextInt(20)+1;
		int op2=rand.nextInt(20)+1;
		if(rand.nextInt(2)<=2)
			op1*=-1;
		return op1+"+"+op2;
	}
	protected String generaSottrazione(){
		int op1=rand.nextInt(20)+1;
		int op2=rand.nextInt(op1)+1;
		return op1+"-"+op2;
	}
	protected String generaMoltiplicazione(){
		int op1=rand.nextInt(11)+1;
		int op2=rand.nextInt(op1);
		if(rand.nextInt(10)<=2)
			op1*=-1;
		return op1+"*"+op2;
	}
	protected String generaDivisione(){
		int op1=rand.nextInt(50)+1;
		ArrayList<Integer> divisori=new ArrayList<Integer>();
		int op2=op1/2;
		while(op2>1){
			if(op1%op2==0)
				divisori.add(op2);
			op2--;
		}
		if(divisori.size()==0){
			divisori.add(1);
			if(op1!=0)
				divisori.add(op1);
		}
		op2=divisori.get(rand.nextInt(divisori.size()));
		if(rand.nextInt(10)<=2)
			op1*=-1;
		return op1+"/"+op2;
	}
	protected boolean isPresenteRisposta(Domanda d, float r){
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
	protected float getRandomRisposta(Domanda d){
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
	protected String generaEspressioneSempliceRandom(){
		int r2=rand.nextInt(12);
		if(r2<3)
			return generaAddizione();
		else if(r2<6)
			return generaSottrazione();
		else if(r2<9)
			return generaMoltiplicazione();
		else
			return generaDivisione();
	}
}
