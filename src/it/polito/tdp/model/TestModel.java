package it.polito.tdp.model;

import java.util.List;

public class TestModel {

	public static void main(String[] args) {

		TestModel testModel = new TestModel();
		testModel.run();

	}

	public void run() {
		Model model = new Model();
		model.creaGrafo(2015);
//		List<District> distretti = model.getAllDistricts();
//		for (District d : distretti) {
//			System.out.println(d.toString() + "\nVicini:\n");
//			model.getAllNeighborDistrict(d).forEach(a->System.out.println(a.getDistretto() + " dista " + a.getDistance() + "\n"));
//		}
		
//		System.out.println("Distretto a minor criminalità " + model.getDistrettoMinCriminalita(2015));
		
		model.doSimula(19, 9, 2017, 50);
	}

}
