package it.polito.tdp.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import com.javadocmd.simplelatlng.LatLng;
import com.javadocmd.simplelatlng.LatLngTool;
import com.javadocmd.simplelatlng.util.LengthUnit;
import com.mysql.cj.xdevapi.Collection;

import it.polito.tdp.db.EventsDao;

public class Model {

	private EventsDao dao;
	private Simulatore sim;
	private Graph<District, DefaultWeightedEdge> grafo;

	public Model() {
		this.dao = new EventsDao();
		this.grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		this.sim = new Simulatore();
	}

	public List<Integer> getAllYears() {
		return this.dao.listAllYears();
	}

	public boolean creaGrafo(int anno) { 
		List<District> distretti = dao.listAllDistrict(anno);
		Graphs.addAllVertices(this.grafo, distretti);
		for (District d1 : this.grafo.vertexSet()) {
			for (District d2 : this.grafo.vertexSet()) {
				if (!d1.equals(d2)) {
					Double distanza;
					LatLng pos1 = new LatLng(d1.getLatMedia(), d1.getLonMedia());
					LatLng pos2 = new LatLng(d2.getLatMedia(), d2.getLonMedia());
					distanza = LatLngTool.distance(pos1, pos2, LengthUnit.KILOMETER);
					Graphs.addEdge(this.grafo, d1, d2, distanza);
				}
			}
		}
		System.out.format("Creato un grafo con %d vertici e %d archi", this.grafo.vertexSet().size(), this.grafo.edgeSet().size());
		return true;
	}

	public List<Vicino> getAllNeighborDistrict(District dis){
		List<District> disVicini = Graphs.neighborListOf(this.grafo, dis);
		List<Vicino> res = new LinkedList<>();
		for(District d : disVicini) 
			res.add(new Vicino(d, this.grafo.getEdgeWeight(this.grafo.getEdge(dis, d))));
		Collections.sort(res);
		return res;
	}
	
	public class Vicino implements Comparable<Vicino>{
		private District distretto;
		private Double distance;
		public Vicino(District distretto, Double distance) {
			super();
			this.distretto = distretto;
			this.distance = distance;
		}
		public District getDistretto() {
			return distretto;
		}
		public Double getDistance() {
			return distance;
		}
		@Override
		public int compareTo(Vicino o) {
			return this.distance.compareTo(o.distance);
		}
		
	}

	public List<District> getAllDistricts() {
		return new ArrayList<>(this.grafo.vertexSet());
	}

	public double getDistrinctsDistance(District d1, District d2) {
		return this.grafo.getEdgeWeight(this.grafo.getEdge(d1, d2));
	}
	
	public District getDistrettoMinCriminalita(int anno) {
		return dao.getDistrettoMinCriminalita(anno, this.grafo.vertexSet());
	}
	
	public List<Event> getAllEventsFiltered(int giorno, int mese, int anno){
		return dao.listAllEventsFiltered(giorno, mese, anno);
	}

	public int doSimula(Integer giorno, Integer mese, int anno, int n) {
		sim.init(giorno, mese, anno, n, this);
		sim.run();
		return sim.getMalGestiti();
	}
}
