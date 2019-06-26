/**
 * Sample Skeleton for 'Crimes.fxml' Controller Class
 */

package it.polito.tdp.crimes;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import it.polito.tdp.model.District;
import it.polito.tdp.model.Model;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class CrimesController {

	private Model model;
	private int anno = -1;

	@FXML // ResourceBundle that was given to the FXMLLoader
	private ResourceBundle resources;

	@FXML // URL location of the FXML file that was given to the FXMLLoader
	private URL location;

	@FXML // fx:id="boxAnno"
	private ComboBox<Integer> boxAnno; // Value injected by FXMLLoader

	@FXML // fx:id="boxMese"
	private ComboBox<Integer> boxMese; // Value injected by FXMLLoader

	@FXML // fx:id="boxGiorno"
	private ComboBox<Integer> boxGiorno; // Value injected by FXMLLoader

	@FXML // fx:id="btnCreaReteCittadina"
	private Button btnCreaReteCittadina; // Value injected by FXMLLoader

	@FXML // fx:id="btnSimula"
	private Button btnSimula; // Value injected by FXMLLoader

	@FXML // fx:id="txtN"
	private TextField txtN; // Value injected by FXMLLoader

	@FXML // fx:id="txtResult"
	private TextArea txtResult; // Value injected by FXMLLoader

	@FXML
	void doCreaReteCittadina(ActionEvent event) {
		Integer anno = this.boxAnno.getSelectionModel().getSelectedItem();
		if (anno == null) {
			txtResult.setText("Valore anno non corretto\n");
			return;
		}
		this.anno = anno;
		this.model.creaGrafo(anno);
		List<District> distretti = this.model.getAllDistricts();
		for (District d : distretti) {
			txtResult.appendText(d.toString() + "\nVicini:\n");
			this.model.getAllNeighborDistrict(d)
					.forEach(a -> this.txtResult.appendText(a.getDistretto() + " dista " + a.getDistance() + "\n"));
		}
	}

	@FXML
	void doSimula(ActionEvent event) {
		if(anno == -1) {
			txtResult.setText("Inizializzazione mancante");
			return;
		}
		Integer giorno = this.boxGiorno.getSelectionModel().getSelectedItem();
		Integer mese = this.boxMese.getSelectionModel().getSelectedItem();
		if (giorno == null) {
			txtResult.setText("Immettere un giorno");
			return;
		}
		if (mese == null) {
			txtResult.setText("Immettere un mese");
			return;
		}
		int N = 0;
		try {
			N = Integer.parseInt(this.txtN.getText());
			if (N > 10 || N < 0)
				throw new NumberFormatException();
		} catch (NumberFormatException e) {
			txtResult.setText("Valore di N non ammesso");
			return;
		}
		int malGes = model.doSimula(giorno, mese, anno, N);
		txtResult.setText("Mal gestiti: " + malGes + "\n");
	}

	@FXML // This method is called by the FXMLLoader when initialization is complete
	void initialize() {
		assert boxAnno != null : "fx:id=\"boxAnno\" was not injected: check your FXML file 'Crimes.fxml'.";
		assert boxMese != null : "fx:id=\"boxMese\" was not injected: check your FXML file 'Crimes.fxml'.";
		assert boxGiorno != null : "fx:id=\"boxGiorno\" was not injected: check your FXML file 'Crimes.fxml'.";
		assert btnCreaReteCittadina != null : "fx:id=\"btnCreaReteCittadina\" was not injected: check your FXML file 'Crimes.fxml'.";
		assert btnSimula != null : "fx:id=\"btnSimula\" was not injected: check your FXML file 'Crimes.fxml'.";
		assert txtN != null : "fx:id=\"txtN\" was not injected: check your FXML file 'Crimes.fxml'.";
		assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Crimes.fxml'.";

	}

	public void setModel(Model model) {
		this.model = model;
		this.boxAnno.getItems().setAll(model.getAllYears());
		List<Integer> giorni = new ArrayList<>();
		List<Integer> mesi= new ArrayList<>();;
		for(int i = 1; i<31;i++) {
			giorni.add(i);
			if(i<13)
				mesi.add(i);
		}
		
		this.boxGiorno.getItems().setAll(giorni);
		this.boxMese.getItems().setAll(mesi);
	}
}
