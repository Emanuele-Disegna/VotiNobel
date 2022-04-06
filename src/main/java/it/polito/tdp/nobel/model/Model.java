package it.polito.tdp.nobel.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import it.polito.tdp.nobel.db.EsameDAO;

public class Model {

	private List<Esame> esami;
	private Set<Esame> migliore;
	private double mediaMigliore;
	
	public Model() {
		EsameDAO dao = new EsameDAO();
		esami = new ArrayList<Esame>(dao.getTuttiEsami());
	}
	
	public Set<Esame> calcolaSottoinsiemeEsami(int m) {
		//Pulizia sol migliore e relativa media
		migliore = new HashSet<Esame>();
		mediaMigliore = -1;
		
		Set<Esame> parziale = new HashSet<Esame>();
		//this.cercaRicorsiva(m, 0, parziale);
		this.cercaRicorsivaPiuVeloce(m, 0, parziale);
		return migliore;	
	}
	
	
	/*
	 * Il prolema di questo algoritmo è la sua complessità: N!
	 */
	public void cercaRicorsiva(int m, int livello, Set<Esame> parziale) {
		//Controllo casi terminali, condizione di arresto
		if(this.sommaCrediti(parziale)>m) {
			//Soluzione non valida
			return;
		} else if (this.sommaCrediti(parziale)==m) {
			//Inutile continuare ma possibile soluzione ottima
			//Controlliamo se è la migliore finora
			if(this.calcolaMedia(parziale)>mediaMigliore) {
				migliore = new HashSet<Esame>(parziale); // Attenzione, non 
				//voglio il riferimento ma una fotografia ==> fare una new e non un migliore=parziale
				mediaMigliore = this.calcolaMedia(parziale);
			}
			return;
		}
		
		//Qua i crediti sono  < m e quindi vado avanti
		if(/*Potrei non avere piu esami*/livello == esami.size()) {
			return;
		}
		
		//Abbiamo controllato tutti i casi terminali,
		//ora possiamo iniziare a gestire i sottoproblemi
		for(Esame e : esami) {
			if(/*Non inserisco
			 	un esame che gia è contenuto*/
					!parziale.contains(e)) {
				parziale.add(e);
				this.cercaRicorsiva(m, livello+1, parziale);
				parziale.remove(e/*Attenzione, valido solo per i set fare cosi*/);//Backtracking
				//parziale.remove(parziale.size()-1) per le liste
			}
				
		}
		
	}
	
	/*
	 * Tecnica risolutiva piu veloce:
	 * 
	 * Creiamo un metodo migliore che procede in verticale 
	 * creando un albero di possibilita formato in questo modo:
	 * si parte dal primo esame e si decide se inserirlo o meno, quindi per le due
	 * soluzioni trovate si fa lo stesso ragionamento etc fino alla fine.
	 * 
	 * La complessita di questo algoritmo non è piu N! ora ma bensi 2^N, 
	 * un grande miglioramento
	 */
	
	//Implementazione
	public void cercaRicorsivaPiuVeloce(int m, int livello, Set<Esame> parziale) {
		//I casi terminali sono gli stessi
		if(this.sommaCrediti(parziale)>m) {
			return;
		} else if (this.sommaCrediti(parziale)==m) {
			if(this.calcolaMedia(parziale)>mediaMigliore) {
				migliore = new HashSet<Esame>(parziale);
				mediaMigliore = this.calcolaMedia(parziale);
			}
			return;
		}
		
		if(livello == esami.size()) {
			return;
		}
		
		//Inizio codice nuovo
		
		//Provo ad aggiungere esami al livello L
		parziale.add(esami.get(livello));
		this.cercaRicorsivaPiuVeloce(m, livello+1, parziale);
		
		//provo a non aggiungere esami al livello L
		parziale.remove(esami.get(livello)); //Backtracking
		this.cercaRicorsivaPiuVeloce(m, livello+1, parziale);
		
	}
	
	public double calcolaMedia(Set<Esame> esami) {
		
		int crediti = 0;
		int somma = 0;
		
		for(Esame e : esami){
			crediti += e.getCrediti();
			somma += (e.getVoto() * e.getCrediti());
		}
		
		return somma/crediti;
	}
	
	public int sommaCrediti(Set<Esame> esami) {
		int somma = 0;
		
		for(Esame e : esami)
			somma += e.getCrediti();
		
		return somma;
	}
	
	
	
}
