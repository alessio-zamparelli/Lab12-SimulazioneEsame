package it.polito.tdp.model;

import java.util.Map;

/*
 * TIPI DI EVENTO
 * 1. Evento criminoso
 *   1.1 La centrale seleziona l'agente più vicino
 *     1.1.1 Calcolo il cammino minimo tra tutti i vertici che hanno 
 *       degli agenti liberi ed il vertice in cui è avvenuto il crimine
 *   1.2 Setta l'agente come occupato
 * 2. Arriva l'agente
 *   2.1 Definisco quanto durerà l'evento
 *   2.2 Controlla se l'evento è mal gestito
 * 3. Crimine terminato
 *   3.1 Libero l'agente
 */
public class Simulatore {

	// Strutture dati che servono
	private Integer malGestiti;
	private Integer N;
	private Integer anno;
	private Integer mese;
	private Integer giorno;

	// mappa di distretto-n agenti liberi
	private Map<Integer, Integer> agenti;

	//

}
