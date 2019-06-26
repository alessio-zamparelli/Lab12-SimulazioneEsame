package it.polito.tdp.db;

import java.util.List;

import it.polito.tdp.model.District;
import it.polito.tdp.model.Event;

public class TestDao {

	public static void main(String[] args) {
		EventsDao dao = new EventsDao();
//		for(Event e : dao.listAllEvents())
//			System.out.println(e);
//		System.out.println(dao.listAllYears());
		List<District> distretti = dao.listAllDistrict(2015);
		for(District d : distretti) {
			System.out.println("Distretto " + d.getDistrictId() + " con " + d.getAllEvents().size() + " eventi\n");
		}
	}

}
