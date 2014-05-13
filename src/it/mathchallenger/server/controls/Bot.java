package it.mathchallenger.server.controls;

import java.util.Random;

import it.mathchallenger.server.entities.Account;
import it.mathchallenger.server.entities.Domanda;
import it.mathchallenger.server.entities.Partita;
import it.mathchallenger.server.tda.NodeQueue;
import it.mathchallenger.server.tda.Queue;

public class Bot extends Account implements Runnable{
	private Queue<Integer> partite;
	private int percentuale;
	public Bot(String botname, int percentuale_successo, int id) {
		super();
		percentuale=percentuale_successo;
		super.setUsername(botname);
		super.setID(id);
		partite=new NodeQueue<Integer>();
	}

	@Override
	public void run() {
		System.out.println("Avvio del bot "+getUsername());
		Random rand=new Random(System.currentTimeMillis());
		long random_sleep=(rand.nextInt(30)+10)*1000L;
		int empty_time=0;
		while(true){
			if(!partite.isEmpty()){
				empty_time=0;
				int id_partita=partite.dequeue();
				Partita p=DBPartita.getInstance().getPartitaByID(id_partita);
				if(p!=null && p.getStatoPartita()<=Partita.INIZIATA){
    				System.out.println("Bot "+getUsername()+" risponde alla partita "+id_partita);
					for(int i=0;i<p.getNumeroDomande();i++){
    					Domanda d=p.getDomanda(i);
    					if(rand.nextInt(100)<=percentuale)
    						d.setUser2Risposta(Domanda.ESATTA);
    					else
    						d.setUser2Risposta(Domanda.SBAGLIATA);
    				}
    				DBPartita.getInstance().rispondiDomandeBot(p);
				}
			}
			else
				empty_time++;
			try {
				//sleep più lunga se il bot non viene impiegato da 5 cicli
				Thread.sleep(empty_time<=5?random_sleep:random_sleep+rand.nextInt(30)*1000L);
			} 
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	public void aggiungiPartita(Integer p){
		partite.enqueue(p);
	}
}
