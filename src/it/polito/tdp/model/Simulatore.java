package it.polito.tdp.model;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.stream.Collectors;

import com.javadocmd.simplelatlng.LatLng;
import com.javadocmd.simplelatlng.LatLngTool;
import com.javadocmd.simplelatlng.util.LengthUnit;

import it.polito.tdp.db.EventsDao;
import it.polito.tdp.model.Evento.TipoEvento;

public class Simulatore {

	private int giorno;
	private int mese;
	private int anno;
	private int N;
	private int malGestiti;

	private Model model;
	private Random rSeed;

	private PriorityQueue<Evento> queue;
	private Map<District, Integer> mappaCop;

	public Simulatore() {

	}

	public void init(int giorno, int mese, int anno, int N, Model model) {
		this.giorno = giorno;
		this.mese = mese;
		this.anno = anno;
		this.N = N;
		this.model = model;
		this.malGestiti = 0;
		this.rSeed = new Random();
		this.queue = new PriorityQueue<>();
		this.mappaCop = new HashMap<>();
		District distrettoDiPartenza = this.model.getDistrettoMinCriminalita(anno);
		Map<Integer, District> disIdMap = this.model.getAllDistricts().stream()
				.collect(Collectors.toMap(District::getDistrictId, a -> a));
		for (Event crime : model.getAllEventsFiltered(giorno, mese, anno)) {
			District dCrimine = disIdMap.get(crime.getDistrict_id());
			mappaCop.put(dCrimine, 0);
			System.out.println("Carico i crimini per " + dCrimine);
			queue.add(new Evento(crime.getReported_date(), TipoEvento.DELITTO, crime, dCrimine));
		}
		// Metto i poliziotti iniziali
		mappaCop.put(distrettoDiPartenza, N);

//		for (District dis : model.getAllDistricts()) {
//			mappaCop.put(dis, 0);
//			System.out.println("Carico i crimini per " + dis);
//			for (Event e : dis.getAllEvents())
//				queue.add(new Evento(e.getReported_date(), TipoEvento.DELITTO, e, dis));
//		}
		// Metto i poliziotti iniziali
//		mappaCop.put(distrettoDiPartenza, N);

	}

	public void run() {

		Evento e;
		while ((e = this.queue.poll()) != null) {
			switch (e.getTipoEvento()) {

				case DELITTO:
					System.out.println("DELITTO " + e + "\n");
					RunningCop rc = this.calcolaPoliziottoVicino(e);
					if (rc == null) {
						// Non ho poliziotti disponibili
						malGestiti++;
					} else {
						// tolgo il poliziotto dalla centrale
						this.mappaCop.put(rc.getDPartenza(), this.mappaCop.get(rc.getDPartenza()) - 1);
						queue.add(new Evento(e.getData().plusSeconds(rc.ETA()), TipoEvento.COP_ARRIVATO, e.getDelitto(),
								e.getDistretto()));
					}
					break;

				case COP_ARRIVATO:
					System.out.println("COP_ARRIVATO " + e + "\n");
					if (e.getData().isAfter(e.getDelitto().getReported_date().plusMinutes(15))) {
						System.out.println("  MalGestito");
						// evento mal gestito, poliziotto in ritardo
						this.malGestiti++;
						// se ne torna in centrale
						this.mappaCop.put(e.getDistretto(), this.mappaCop.get(e.getDistretto()) + 1);
					} else {
						System.out.println("  BenGestito");
						// cop in tempo
						int durataIntervento;
						if (e.getDelitto().getOffense_category_id().equals("all-other-crimes")) {
							durataIntervento = rSeed.nextInt(2) + 1;
						} else {
							durataIntervento = 2;
						}
						System.out.println("    Durata Intervento-> " + durataIntervento + "\n");
						queue.add(new Evento(e.getData().plusHours(durataIntervento), TipoEvento.COP_ALLONTANATO,
								e.getDelitto(), e.getDistretto()));
					}
					break;

				case COP_ALLONTANATO:
					System.out.println("COP_ALLONTANATO " + e + "\n");
					// torna nella centrale vicino al crimine
					this.mappaCop.put(e.getDistretto(), this.mappaCop.get(e.getDistretto()) + 1);
					break;

			}
		}

	}

	private RunningCop calcolaPoliziottoVicino(Evento e) {
		double bestDistance = Double.MAX_VALUE;
		District nearestDis = null;
		for (District d : this.mappaCop.keySet()) {
			double currDistance = LatLngTool.distance(e.getDelitto().getLatLng(), d.getLatLng(), LengthUnit.METER);
			if (currDistance < bestDistance && this.mappaCop.get(d) > 0) {
				bestDistance = currDistance;
				nearestDis = d;
			}
		}
		if (nearestDis == null)
			// non ho trovato il polizziotto
			return null;
		return new RunningCop(nearestDis, nearestDis.getLatLng(), e.getDelitto().getLatLng());
	}

	private class RunningCop {
		private District dPartenza;
//		private District dArrivo;
		private LatLng partenza;
		private LatLng arrivo;

//		public RunningCop(District dPartenza, District dArrivo, LatLng partenza, LatLng arrivo) {
		public RunningCop(District dPartenza, LatLng partenza, LatLng arrivo) {
			this.dPartenza = dPartenza;
//			this.dArrivo = dArrivo;
			this.partenza = partenza;
			this.arrivo = arrivo;
		}

//		public District getDArrivo() {
//			return this.dArrivo;
//		}

		public District getDPartenza() {
			return this.dPartenza;
		}

		public long ETA() { // in secondi
			return (long) (LatLngTool.distance(partenza, arrivo, LengthUnit.METER) / (60 / 3.6));
		}

	}

	public int getMalGestiti() {
		return this.malGestiti;
	}

}
