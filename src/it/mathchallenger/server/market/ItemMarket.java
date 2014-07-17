package it.mathchallenger.server.market;

public class ItemMarket {
	private String nome_oggetto;
	private int id_oggetto;
	private float prezzo;
	
	public ItemMarket(String nome, int id, float prezzo){
		nome_oggetto=nome;
		id_oggetto=id;
		this.prezzo=prezzo;
	}

	public String getNomeOggetto() {
		return nome_oggetto;
	}

	public int getIDOggetto() {
		return id_oggetto;
	}

	public float getPrezzo() {
		return prezzo;
	}

	public void setNomeOggetto(String nome_oggetto) {
		this.nome_oggetto = nome_oggetto;
	}

	public void setIDOggetto(int id_oggetto) {
		this.id_oggetto = id_oggetto;
	}

	public void setPrezzo(float prezzo) {
		this.prezzo = prezzo;
	}
	
}
