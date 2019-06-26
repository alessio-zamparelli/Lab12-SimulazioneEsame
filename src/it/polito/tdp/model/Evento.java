package it.polito.tdp.model;

import java.time.LocalDateTime;

public class Evento implements Comparable<Evento> {
	public enum TipoEvento {
		COP_ARRIVATO, DELITTO, COP_ALLONTANATO
	}

	private LocalDateTime data;
	private TipoEvento tipoEvento;
	private Event delitto;
	private District distretto;

	public Evento(LocalDateTime data, TipoEvento tipoEvento, Event delitto, District distretto) {
		this.data = data;
		this.tipoEvento = tipoEvento;
		this.delitto = delitto;
		this.distretto = distretto;
	}

	public LocalDateTime getData() {
		return data;
	}

	public TipoEvento getTipoEvento() {
		return tipoEvento;
	}

	public Event getDelitto() {
		return delitto;
	}

	public District getDistretto() {
		return distretto;
	}

	@Override
	public int compareTo(Evento o) {
		return this.data.compareTo(o.data);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Evento [data=");
		builder.append(data);
		builder.append(", tipoEvento=");
		builder.append(tipoEvento);
		builder.append(", distretto=");
		builder.append(distretto);
		builder.append("]");
		return builder.toString();
	}

	
}
