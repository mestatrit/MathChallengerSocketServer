package it.mathchallenger.server.controls;

import it.mathchallenger.server.entities.Domanda;
import de.congrace.exp4j.Calculable;
import de.congrace.exp4j.ExpressionBuilder;
import de.congrace.exp4j.UnknownFunctionException;
import de.congrace.exp4j.UnparsableExpressionException;

public class GeneratoreEspressioniMedie extends Risolutore {
	private static GeneratoreEspressioniMedie res;
	
	public static GeneratoreEspressioniMedie getInstance(){
		if(res==null)
			res=new GeneratoreEspressioniMedie();
		return res;
	}
	
	public GeneratoreEspressioniMedie() {
		super();
	}

	@Override
	public Domanda generaDomanda() {
		boolean generaParentesi=rand.nextBoolean();
		boolean parentesiIniziali=rand.nextBoolean();
		StringBuilder domanda=new StringBuilder();
		Domanda dom=new Domanda();
		if(generaParentesi){
			if(parentesiIniziali){
				domanda.append("(");
				domanda.append(generaEspressioneSempliceRandom());
				domanda.append(")");
				String middle_op=operazioni_easy[rand.nextInt(operazioni_easy.length-1)];
				domanda.append(middle_op);
				int op3=rand.nextInt(20)+1;
				domanda.append(op3);
			}
			else {
				int op3=rand.nextInt(20)+1;
				domanda.append(op3);
				String middle_op=operazioni_easy[rand.nextInt(operazioni_easy.length-1)];
				domanda.append(middle_op);
				domanda.append("(");
				domanda.append(generaEspressioneSempliceRandom());
				domanda.append(")");
			}
			dom.setDomanda(domanda.toString());
			try {
				risolvi(dom);
			}
			catch (UnknownFunctionException | UnparsableExpressionException e) {
				e.printStackTrace();
			}
			generaRisposteErrateParentesi(dom);
		}
		else {
			String operazione1=operazioni_easy[rand.nextInt(operazioni_easy.length-1)];
			String operazione2=operazioni_easy[rand.nextInt(operazioni_easy.length-1)];
			int op1=rand.nextInt(20)+1;
			int op2=rand.nextInt(20)+1;
			int op3=rand.nextInt(20)+1;
			domanda.append(op1).append(operazione1).append(op2).append(operazione2).append(op3);
			
			dom.setDomanda(domanda.toString());
			try {
				risolvi(dom);
			}
			catch (UnknownFunctionException | UnparsableExpressionException e) {
				e.printStackTrace();
			}
			generaRisposteErrateSenzaParentesi(dom,op1, op2, op3, operazione1, operazione2);
		}
		
		return dom;
	}
	private void generaRisposteErrateParentesi(Domanda d){
		for(int i=1;i<=3;){
			int shift=rand.nextInt(3)+1;
			float f=getRandomRisposta(d);
			if(rand.nextInt(4)<=1){
				if(!isPresenteRisposta(d, f+shift)){
					d.setRispostaErrata(i, f+shift);
					i++;
				}
			}
			else {
				if(!isPresenteRisposta(d, f-shift)){
					d.setRispostaErrata(i, f-shift);
					i++;
				}
			}
		}
	}
	private void generaRisposteErrateSenzaParentesi(Domanda d, int op1, int op2, int op3,String operazione1, String operazione2){
		int domande_generate=0;
		
		StringBuilder espr1=new StringBuilder().append(op1).append(operazione2).append(op2).append(operazione1).append(op3);
		float r_espr1=risolvi(espr1.toString());
		if(!isPresenteRisposta(d, r_espr1)){
			d.setRispostaErrata(domande_generate+1, r_espr1);
			domande_generate++;
		}
		StringBuilder espr2=new StringBuilder().append(op3).append(operazione1).append(op2).append(operazione2).append(op1);
		float r_espr2=risolvi(espr2.toString());
		if(!isPresenteRisposta(d, r_espr2)){
			d.setRispostaErrata(domande_generate+1, r_espr2);
			domande_generate++;
		}
		StringBuilder espr3=new StringBuilder().append(op2).append(operazione1).append(op1).append(operazione2).append(op3);
		float r_espr3=risolvi(espr3.toString());
		if(!isPresenteRisposta(d, r_espr3)){
			d.setRispostaErrata(domande_generate+1, r_espr3);
			domande_generate++;
		}
		
		if(domande_generate<3){
			StringBuilder espr4=new StringBuilder().append(op3).append(operazione2).append(op1).append(operazione1).append(op2);
			float r_espr4=risolvi(espr4.toString());
			if(!isPresenteRisposta(d, r_espr4)){
				d.setRispostaErrata(domande_generate+1, r_espr4);
				domande_generate++;
			}
		}
		
		while(domande_generate<3){
			int shift=rand.nextInt(3)+1;
			float f=getRandomRisposta(d);
			if(!isPresenteRisposta(d, f+shift)){
				d.setRispostaErrata(domande_generate+1, f+shift);
				domande_generate++;
			}
			else if(!isPresenteRisposta(d, f-shift)){
				d.setRispostaErrata(domande_generate+1, f-shift);
				domande_generate++;
			}
		}
	}
	public float risolvi(String espr) {
		Calculable calc;
		try {
			calc = new ExpressionBuilder(espr).build();
		}
		catch (UnknownFunctionException | UnparsableExpressionException e) {
			e.printStackTrace();
			return 0f;
		}
		float res = (float) calc.calculate();
		return res;
	}
	@Override
	public void generaRisposteErrate(Domanda d) {}

	public static void main(String[] args){
		Risolutore r=getInstance();
		for(int i=0;i<1000;i++){
			Domanda d=r.generaDomanda();
			System.out.println(d.getDomanda()+ "\t\t"+d.getRispostaEsatta()+" "+d.getRispostaErrata(1)+" "+d.getRispostaErrata(2)+" "+d.getRispostaErrata(3));
		}
	}
}
