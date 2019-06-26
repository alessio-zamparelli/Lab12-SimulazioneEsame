package it.polito.tdp.model;

import java.util.ArrayList;
import java.util.List;

import com.javadocmd.simplelatlng.LatLng;

public class District {

	int district_id;
	List<Event> eventList;
	double latMedia;
	double lonMedia;

	public District(int district_id, double latMedia, double lonMedia) {
		this.district_id = district_id;
		this.latMedia = latMedia;
		this.lonMedia = lonMedia;
		this.eventList = new ArrayList<Event>();
	}

	public void addEventToList(Event newEvent) {
		this.eventList.add(newEvent);
	}

	public int getDistrictId() {
		return this.district_id;
	}

	public List<Event> getAllEvents() {
		return this.eventList;
	}

	public double getLatMedia() {
		return latMedia;
	}

	public double getLonMedia() {
		return lonMedia;
	}
	
	public LatLng getLatLng() {
		return new LatLng(this.latMedia, this.lonMedia);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + district_id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		District other = (District) obj;
		if (district_id != other.district_id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Distretto numero: " + String.valueOf(this.district_id);
	}
	
}
