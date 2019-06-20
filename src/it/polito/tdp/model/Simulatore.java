package it.polito.tdp.model;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;

import it.polito.tdp.db.EventsDao;
import it.polito.tdp.model.Evento.TipoEvento;

/*
 * TIPI DI EVENTO
 * 1. Evento criminoso
 *   1.1 La centrale seleziona l'agente più vicino
 *     (non necessario, grafo fortemente connesso) 1.1.1 Calcolo il cammino minimo tra tutti i vertici che hanno 
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

	private Graph<Integer, DefaultWeightedEdge> grafo;
	private PriorityQueue<Evento> queue;

	// mappa di distretto-n agenti liberi
	private Map<Integer, Integer> agenti;

	public void init(Integer N, Integer anno, Integer mese, Integer giorno,
			Graph<Integer, DefaultWeightedEdge> grafo) {
		this.N = N;
		this.anno = anno;
		this.mese = mese;
		this.giorno = giorno;
		this.grafo = grafo;

		this.malGestiti = 0;
		this.agenti = new HashMap<>();
		for (Integer d : this.grafo.vertexSet()) {
			this.agenti.put(d, 0);
		}
		// Devo scegliere dove sta la centrale
		// minD è il distretto a minor criminalità
		EventsDao dao = new EventsDao();
		Integer minD = dao.getDistrettoMin(anno);
		this.agenti.put(minD, N);

		// Creo la coda
		this.queue = new PriorityQueue<>();

		for (Event e : dao.listAllEventsByDate(this.anno, this.mese, this.giorno))
			queue.add(new Evento(TipoEvento.CRIMINE, e.getReported_date(), e));

	}

	public void run() {

		Evento e;
		while ((e = queue.poll()) != null) {
			switch (e.getTipo()) {
				case ARRIVA_AGENTE:
					System.out.format("AGENTE ARRIVA PER CRIMINE: %d\n", e.getCrimine().getIncident_id());
					Long duration = getDuration(e.getCrimine().getOffense_category_id());
					this.queue.add(new Evento(TipoEvento.GESTITO, e.getData().plusSeconds(duration), e.getCrimine()));
					// Controllo se il crimine è mal gestito
					if (e.getData().isAfter(e.getCrimine().getReported_date().plusMinutes(15))) {
						System.out.format("CRIMINE %d MAL GESTITO!", e.getCrimine().getIncident_id());
						this.malGestiti++;
					}
					break;

				case CRIMINE:
					System.out.println("NUOVO CRIMINE!! " + e.getCrimine().getIncident_id());
					Integer partenza = null;
					partenza = cercaAgente(e.getCrimine().getDistrict_id());
					if (partenza != null) {
						// C'è un agente libero
						if (partenza.equals(e.getCrimine().getDistrict_id())) {
							// Tempo di arrivo = 0
							System.out.format("AGENTE ARRIVA PER CRIMINE: %d\n", e.getCrimine().getIncident_id());
							Long duration1 = getDuration(e.getCrimine().getOffense_category_id());
							this.queue.add(
									new Evento(TipoEvento.GESTITO, e.getData().plusSeconds(duration1), e.getCrimine()));
						} else {
							// Tempo di arrivo != 0, devo schedulare l'evento
							Double distance = this.grafo
									.getEdgeWeight(this.grafo.getEdge(partenza, e.getCrimine().getDistrict_id()));
							Long seconds = (long) ((distance * 1000) / (60 / 3.6));
							this.queue.add(new Evento(TipoEvento.ARRIVA_AGENTE, e.getData().plusSeconds(seconds),
									e.getCrimine()));
						}
					} else {
						// Non c'è alcun agente libero
						System.out.format("CRIMINE %d MAL GESTITO!\n", e.getCrimine().getIncident_id());
						this.malGestiti++;
					}
					break;

				case GESTITO:
					System.out.format("CRIMINE %d GESTITO!\n", e.getCrimine().getIncident_id());
					Integer distretto = e.getCrimine().getDistrict_id();
					this.agenti.put(distretto, this.agenti.get(distretto) + 1);
					break;

			}
		}
		
		System.out.format("TERMINATO! MAL GESTITI = %d", this.malGestiti);

	}

	private Integer cercaAgente(Integer crime_district_id) {
		Double distanza = Double.MAX_VALUE;
		Integer distretto = null;

		for (Integer d : this.agenti.keySet()) {

			if (this.agenti.get(d) > 0) {
				if (crime_district_id.equals(d)) {
					distanza = Double.valueOf(0);
					distretto = d;
				}
				else if (this.grafo.getEdgeWeight(this.grafo.getEdge(crime_district_id, d)) < distanza) {
					distanza = this.grafo.getEdgeWeight(this.grafo.getEdge(crime_district_id, d));
					distretto = d;
				}
			}
		}

		return distretto;
	}

	private Long getDuration(String offense_category_id) {
		if (offense_category_id.equals("all_other_crimes")) {
			Random r = new Random();
			if (r.nextDouble() > 0.5)
				return Long.valueOf(2 * 3600);
			else
				return Long.valueOf(1 * 3600);
		} else
			return Long.valueOf(2 * 3600);
	}

}
