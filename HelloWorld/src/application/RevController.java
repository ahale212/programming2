package application;

import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;










import org.controlsfx.control.PopOver;

import uk.ac.qub.exjavaganza.hqbert.server.v01.ClientCallback;
import uk.ac.qub.exjavaganza.hqbert.server.v01.Patient;
import uk.ac.qub.exjavaganza.hqbert.server.v01.Person;
import uk.ac.qub.exjavaganza.hqbert.server.v01.TreatmentFacility;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.util.StringConverter;

public class RevController implements Initializable, ClientCallback {

	private RMIClient client;
	
	@FXML
	private ToggleButton tb1, tb2, tb3, tb4, tb5, tb6;

	@FXML
	private ListView queue, trooms, treatment_room_list, on_call_list;

	@FXML
	private Button login, UPGRADE, search_database, emergency, Q_view,
			TRooms_view, urg, semi_urg, non_urg;

	@FXML
	private Slider respiratory_rate, pulse_rate;

	@FXML
	private ChoiceBox breathing_yes, allergy;

	@FXML
	private ComboBox conditions, medication;

	@FXML
	private CheckBox walk, walk_no;

	@FXML
	private TextField search_NHS_No, search_First_Name, search_Surname,
			search_DOB, search_Postcode, search_Telephone_No,
			textfield_NHS_Num, textfield_Postcode, textfield_Title,
			textfield_First_Name, textfield_Surname, textfield_DOB,
			textfield_Address, textfield_Telephone, textfield_Blood_Group;

	PopOver p = new PopOver();

	private final ObservableList QList = FXCollections.observableArrayList();
	private final ObservableList trList = FXCollections.observableArrayList();
	private final ObservableList trno = FXCollections.observableArrayList();
	private final ObservableList ocu = FXCollections.observableArrayList();
	private final ObservableList breathingList = FXCollections.observableArrayList();
	private final ObservableList medi_condition = FXCollections.observableArrayList();
	private final ObservableList meds = FXCollections.observableArrayList();
	private final ObservableList allergy_list = FXCollections.observableArrayList();

	private String[] array = new String[10];
	private String[] array1 = new String[4];
	private String[] onCall = new String[1];
	private String[] treatmentRoomNum = new String[4];
	private String[] breaths = new String[2];
	
	private String[] condition = { "Neurological", "Respiratory",
			"Dermatological", "Endrocrinal", "Circulatory", "Auto-Immune", "Viral" };
	
	private String[] medicine = { "Pain Killers", "Antibiotics", "Steroids",
			"Beta-Blockers", "Anti-Depressants", "Anticoagulants" };
	
	private String[] allergic = { "None", "Nuts", "Penicillin", "Stings",
			"Seafood", "Hayfever", "Animals", "Latex" };

	private Person displayedPerson;
	
	@Override
	public void initialize(URL fxmlFilelocation, ResourceBundle resources) {

		labelSliders();
		loadArrayLists();
		buttonFunction();
		
		try {
			client = new RMIClient(this);
		} catch (RemoteException e) {
			System.err.println("Failed to set up client.");
			e.printStackTrace();
		}

	}

	private void labelSliders() {

		respiratory_rate.setLabelFormatter(new StringConverter<Double>() {
			@Override
			public String toString(Double n) {
				if (n < 0.5)
					return "<10pm";
				if (n < 50.5)
					return "10-30pm";

				return ">30pm";
			}

			@Override
			public Double fromString(String s) {
				switch (s) {
				case "<10pm":
					return 0d;
				case "10-30pm":
					return 1d;
				case ">30pm":
					return 2d;
				default:
					return 1d;
				}
			}
		});

		respiratory_rate.setOnMouseClicked(e -> {
			urg.setDisable(false);
			urg.setStyle("-fx-base: orange;");
		});

		pulse_rate.setLabelFormatter(new StringConverter<Double>() {
			@Override
			public String toString(Double n) {
				if (n < 0.5)
					return "<40bpm";
				if (n < 50.5)
					return "40-120bpm";
				return ">120bpm";
			}

			@Override
			public Double fromString(String s) {
				switch (s) {
				case "<40bpm":
					return 0d;
				case "40-120bpm":
					return 1d;
				case ">120bpm":
					return 2d;
				default:
					return 1d;
				}
			}
		});

		pulse_rate.setOnMouseClicked(e -> {
			urg.setDisable(false);
			urg.setStyle("-fx-base: orange;");
		});

	}

	private void loadArrayLists() {

		array[0] = "Jim";
		array[1] = "Mary";
		array[2] = "Illy";
		array[3] = "OMM";
		array[4] = "B3B4";
		array[5] = "A";
		array[6] = "JHSjhd";
		array[7] = "0987";
		array[8] = "bn67BNhgg";
		array[9] = "Kim";
		
		array1[0] = "JimJonJoe";
		array1[1] = "JonJoe";
		array1[2] = "Joe";
		array1[3] = "JJJoe";
		
		onCall[0] = "On Call Unit";

		treatmentRoomNum[0] = "Treatment Room 1";
		treatmentRoomNum[1] = "Treatment Room 2";
		treatmentRoomNum[2] = "Treatment Room 3";
		treatmentRoomNum[3] = "Treatment Room 4";

		trno.addAll(treatmentRoomNum);

		ocu.addAll(onCall);
		QList.addAll(array);
		trList.addAll(array1);
		queue.setItems(QList);
		treatment_room_list.setItems(trno);
		on_call_list.setItems(ocu);

		breaths[0] = "Yes, without resuscitation";
		breaths[1] = "Yes, after opening airway";
		breathingList.addAll(breaths);
		breathing_yes.setItems(breathingList);

		medi_condition.addAll(condition);
		conditions.setItems(medi_condition);

		meds.addAll(medicine);
		medication.setItems(meds);

		allergy_list.addAll(allergic);
		allergy.setItems(allergy_list);

	}

	private void buttonFunction() {

		UPGRADE.setOnAction(e -> {
			String potential = (String) queue.getSelectionModel()
					.getSelectedItem();
			if (potential != null) {
				queue.getSelectionModel().clearSelection();
				QList.remove(potential);

				trList.remove(0);
				trList.add(3, "");
				trList.add(3, potential);
			}
		});
		trooms.setItems(trList);
		Q_view.setOnAction(e -> {
			p.show(Q_view);
		});
		TRooms_view.setOnAction(e -> {
			p.show(TRooms_view);
		});

		search_database.setOnAction(e -> {

			List<Person> matchingPeople = null;

			try {
				matchingPeople = client.getServer().searchPersonByDetails(search_NHS_No.getText() ,search_First_Name.getText(), search_Surname.getText(), search_DOB.getText(), search_Postcode.getText(), search_Telephone_No.getText());
			} catch (RemoteException ex) {
				System.err.println("Server communication error.");
				ex.printStackTrace();
			}	
			
			// If there were people matching the criteria display them to the user
			if (matchingPeople.size() > 0) {
				displayedPerson = matchingPeople.get(0);
				textfield_First_Name.setText(displayedPerson.getFirstName());
				textfield_Surname.setText(displayedPerson.getLastName());
				textfield_NHS_Num.setText(displayedPerson.getNHSNum());
				textfield_Title.setText(displayedPerson.getTitle());
				textfield_DOB.setText(displayedPerson.getDOB());
				textfield_Address.setText(displayedPerson.getAddress());
				textfield_Blood_Group.setText(displayedPerson.getBloodGroup());
				textfield_Postcode.setText(displayedPerson.getPostcode());
				textfield_Telephone.setText(displayedPerson.getTelephone());
				
				// If the returned allergy is "null" set the allergy
				// box to display "None".
				if (displayedPerson.getAllergies().equalsIgnoreCase("null")) {
					allergy.setValue(allergic[0]);
				} else {
					// Else set the allergy box value to the returned alergy
					allergy.setValue(displayedPerson.getAllergies());
				}
			}
			/*textfield_First_Name.setText(search_First_Name.getText());
			textfield_Surname.setText(search_Surname.getText());
			textfield_NHS_Num.setText(search_NHS_No.getText());
			textfield_Title.setText("Dr.");
			textfield_DOB.setText(search_DOB.getText());
			textfield_Address.setText("Breenagh, Letterkenny, Co. Donegal");
			textfield_Blood_Group.setText("Oneg");
			textfield_Postcode.setText(search_Postcode.getText());
			textfield_Telephone.setText(search_Telephone_No.getText());
			allergy.setValue(allergic[0]);*/
			
			enableTriage();
			clearSearchFields();
		});

		tb1.setOnAction(e -> {
			tb1.setText("!");
			tb1.setStyle("-fx-base: salmon;");
			emergency.setDisable(false);
			emergency.setStyle("-fx-base: red;");
		});

		tb2.setOnAction(e -> {
			tb2.setText("!");
			tb2.setStyle("-fx-base: salmon;");
			emergency.setDisable(false);
			emergency.setStyle("-fx-base: red;");
		});

		tb3.setOnAction(e -> {
			tb3.setText("!");
			tb3.setStyle("-fx-base: salmon;");
			emergency.setDisable(false);
			emergency.setStyle("-fx-base: red;");
		});

		tb4.setOnAction(e -> {
			tb4.setText("!");
			tb4.setStyle("-fx-base: salmon;");
			emergency.setDisable(false);
			emergency.setStyle("-fx-base: red;");
		});

		tb5.setOnAction(e -> {
			tb5.setText("!");
			tb5.setStyle("-fx-base: salmon;");
			emergency.setDisable(false);
			emergency.setStyle("-fx-base: red;");
		});

		tb6.setOnAction(e -> {
			tb6.setText("!");
			tb6.setStyle("-fx-base: salmon;");
			emergency.setDisable(false);
			emergency.setStyle("-fx-base: red;");
		});

		emergency.setOnAction(e -> {
			trList.remove(0);
			trList.add(3, textfield_Surname.getText() + ", " + textfield_First_Name.getText());
			
			try {
				client.getServer().addPrimaryPatient(displayedPerson, tb1.isSelected(), tb2.isSelected(), tb3.isSelected(), tb4.isSelected(), tb5.isSelected(), tb6.isSelected());
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			
			clearTextFields();
			resetTriage();
		});

		urg.setOnAction(e -> {
			QList.remove(0);
			QList.add(0, textfield_Surname.getText() + ", "	+ textfield_First_Name.getText());
			clearTextFields();
			resetTriage();
		});

		semi_urg.setOnAction(e -> {
			QList.remove(0);
			QList.add(4, textfield_Surname.getText() + ", "	+ textfield_First_Name.getText());
			clearTextFields();
			resetTriage();
		});

		non_urg.setOnAction(e -> {
			QList.remove(0);
			QList.add(9, textfield_Surname.getText() + ", "	+ textfield_First_Name.getText());
			clearTextFields();
			resetTriage();
		});
		
		walk.setOnAction(e -> {
			non_urg.setDisable(false);
			non_urg.setStyle("-fx-base: green;");
		});

		walk_no.setOnAction(e -> {
			if (breathing_yes.getSelectionModel().getSelectedIndex() == 0) {
				semi_urg.setDisable(false);
				semi_urg.setStyle("-fx-base: yellow;");
				clearTextFields();
			}
		});
	}

	private void clearSearchFields() {

		search_NHS_No.clear();
		search_First_Name.clear();
		search_Surname.clear();
		search_DOB.clear();
		search_Postcode.clear();
		search_Telephone_No.clear();
	}

	private void clearTextFields() {

		textfield_First_Name.clear();
		textfield_Surname.clear();
		textfield_NHS_Num.clear();
		textfield_Title.clear();
		textfield_DOB.clear();
		textfield_Address.clear();
		textfield_Blood_Group.clear();
		textfield_Postcode.clear();
		textfield_Telephone.clear();
		allergy.setValue(null);

	}

	private void resetTriage() {

		tb1.setDisable(true);
		tb1.setSelected(false);
		tb1.setStyle(null);
		tb1.setText("✓");
		tb2.setDisable(true);
		tb2.setSelected(false);
		tb2.setStyle(null);
		tb2.setText("✓");
		tb3.setDisable(true);
		tb3.setSelected(false);
		tb3.setStyle(null);
		tb3.setText("✓");
		tb4.setDisable(true);
		tb4.setSelected(false);
		tb4.setStyle(null);
		tb4.setText("✓");
		tb5.setDisable(true);
		tb5.setSelected(false);
		tb5.setStyle(null);
		tb5.setText("✓");
		tb6.setDisable(true);
		tb6.setSelected(false);
		tb6.setStyle(null);
		tb6.setText("✓");
		emergency.setDisable(true);
		emergency.setStyle(null);
		urg.setDisable(true);
		urg.setStyle(null);
		semi_urg.setDisable(true);
		semi_urg.setStyle(null);
		non_urg.setDisable(true);
		non_urg.setStyle(null);
		walk.setDisable(true);
		walk.setSelected(false);
		walk_no.setDisable(true);
		walk_no.setSelected(false);
		breathing_yes.setDisable(true);
		breathing_yes.setValue(null);
		respiratory_rate.setDisable(true);
		respiratory_rate.setValue(50);
		pulse_rate.setDisable(true);
		pulse_rate.setValue(50);
		medication.setDisable(true);
		medication.setValue(null);
		conditions.setDisable(true);
		conditions.setValue(null);

	}

	private void enableTriage() {

		tb1.setDisable(false);
		tb2.setDisable(false);
		tb3.setDisable(false);
		tb4.setDisable(false);
		tb5.setDisable(false);
		tb6.setDisable(false);
		walk.setDisable(false);
		walk_no.setDisable(false);
		breathing_yes.setDisable(false);
		respiratory_rate.setDisable(false);
		pulse_rate.setDisable(false);
		medication.setDisable(false);
		conditions.setDisable(false);
	}

	@Override
	public void udpate(LinkedList<Patient> queue,
			ArrayList<TreatmentFacility> treatmentFacilities)
			throws RemoteException {
		System.out.println("RevController: Queue recieved");
		
	}

	@Override
	public void log(String log) throws RemoteException {
		System.out.println("RevController: " + log);
	}
}
