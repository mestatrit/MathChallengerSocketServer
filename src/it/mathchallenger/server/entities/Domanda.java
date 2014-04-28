package it.mathchallenger.server.entities;

public class Domanda {
	private String domanda;
	private float risposta_esatta, risposta_errata_1,risposta_errata_2,risposta_errata_3;
	private int user_1_risposto, user_2_risposto;
	private int numero_domanda;
	
	public final static int NON_RISPOSTO=-1, ESATTA=0, SBAGLIATA=1;
	
	public Domanda() {
		user_1_risposto=NON_RISPOSTO;
		user_2_risposto=NON_RISPOSTO;
	}
	public float getRispostaEsatta(){
		return risposta_esatta;
	}
	public void setRispostaEsatta(float f){
		risposta_esatta=f;
	}
	public float getRispostaErrata(int i){
		switch(i){
			case 1:
				return risposta_errata_1;
			case 2:
				return risposta_errata_2;
			case 3:
				return risposta_errata_3;
		}
		throw new RuntimeException();
	}
	public void setRispostaErrata(int domanda, float r){
		switch(domanda){
			case 1:
				risposta_errata_1=r;
				break;
			case 2:
				risposta_errata_2=r;
				break;
			case 3:
				risposta_errata_3=r;
				break;
		}
	}
	public String getDomanda(){
		return domanda;
	}
	public void setDomanda(String domanda){
		this.domanda=domanda;
	}
	public boolean isUser1Corretta(){
		if(user_1_risposto==ESATTA)
			return true;
		return false;
	}
	public boolean isUser1Risposto(){
		return user_1_risposto!=NON_RISPOSTO;
	}
	public void setUser1Risposta(int f){
		user_1_risposto=f;
	}
	public void setUser2Risposta(int f){
		user_2_risposto=f;
	}
	public boolean isUser2Corretta(){
		if(user_2_risposto==ESATTA)
			return true;
		return false;
	}
	public boolean isUser2Risposto(){
		return user_2_risposto!=NON_RISPOSTO;
	}
	public int getNumeroDomanda(){
		return numero_domanda;
	}
	public void setNumeroDomanda(int n){
		numero_domanda=n;
	}
}
