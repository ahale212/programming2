package application;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.time.temporal.IsoFields;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

import org.controlsfx.control.PopOver;

import com.sun.javafx.application.PlatformImpl.FinishListener;
import com.sun.org.apache.bcel.internal.generic.NEWARRAY;
import com.sun.prism.paint.Color;

import uk.ac.qub.exjavaganza.hqbert.server.v01.ClientCallback;
import uk.ac.qub.exjavaganza.hqbert.server.v01.OnCallTeam;
import uk.ac.qub.exjavaganza.hqbert.server.v01.Patient;
import uk.ac.qub.exjavaganza.hqbert.server.v01.Person;
import uk.ac.qub.exjavaganza.hqbert.server.v01.Staff;
import uk.ac.qub.exjavaganza.hqbert.server.v01.TreatmentFacility;
import uk.ac.qub.exjavaganza.hqbert.server.v01.TreatmentRoom;
import uk.ac.qub.exjavaganza.hqbert.server.v01.Urgency;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Slider;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.StringConverter;

public class RevController implements Initializable, ClientCallback {
	
	private RMIClient client;
	
	@FXML
	MenuItem settings;
	
	@FXML
	private Rectangle countdown;
	
	@FXML // comment
	private TextArea outputTextArea;
	
	@FXML
	private ToggleButton tb1, tb2, tb3, tb4, tb5, tb6;

	@FXML
	private ListView queue, trooms, treatment_room_list, on_call_list, on_call;

	@FXML
	private Button login, UPGRADE, search_database, emergency, Q_view,
			TRooms_view, urg, semi_urg, non_urg, extend;

	@FXML
	private Slider respiratory_rate, pulse_rate;

	@FXML
	private ChoiceBox breathing_yes, allergy, patient_finder;

	@FXML
	private ComboBox conditions, medication;

	@FXML
	private CheckBox walk, walk_no;
	
	@FXML
	private Tab triage_tab, tr_tab, stats_tab;

	@FXML
	private TextField search_NHS_No, search_First_Name, search_Surname,
			search_DOB, search_Postcode, search_Telephone_No,
			textfield_NHS_Num, textfield_Postcode, textfield_Title,
			textfield_First_Name, textfield_Surname, textfield_DOB,
			textfield_Address, textfield_Telephone, textfield_Blood_Group;

	PopOver p = new PopOver();
	PopOver p1 = new PopOver();
	PopOver p2 = new PopOver();
	PopOver p3 = new PopOver();
	
	Staff staff = new Staff();
	
	private final ObservableList<Patient> emergency_room = FXCollections.observableArrayList();
	private final ObservableList<Patient> waiting_room = FXCollections.observableArrayList();
	private final ObservableList<String> QList = FXCollections.observableArrayList();
	private final ObservableList<String> trList = FXCollections.observableArrayList();
	private final ObservableList trno = FXCollections.observableArrayList();
	private final ObservableList ocu = FXCollections.observableArrayList();
	private final ObservableList<String> onCallList = FXCollections.observableArrayList();
	private final ObservableList breathingList = FXCollections.observableArrayList();
	private final ObservableList medi_condition = FXCollections.observableArrayList();
	private final ObservableList meds = FXCollections.observableArrayList();
	private final ObservableList allergy_list = FXCollections.observableArrayList();
	private final ObservableList<Person> search_patient_results = FXCollections.observableArrayList();
	List<Person> matchingPeople, matchingPeople1;

	/**
	 * Data Lists:
	 */
	
	/**
	 * The list that holds the Patients in the queue
	 */
	List<Patient> queueList = new LinkedList<Patient>();
	
	/**
	 * The list of Treatment Rooms / On call team
	 */
	List<TreatmentFacility> treatmentFacilities = new ArrayList<TreatmentFacility>();
	
	private String[] array = new String[10];
	private String[] array1 = new String[5];
	private String[] onCall = new String[1];
	private String[] treatmentRoomNum = new String[5];
	private String[] breaths = new String[2];
	
	private String[] condition = { "Neurological", "Respiratory",
			"Dermatological", "Endrocrinal", "Circulatory", "Auto-Immune", "Viral" };
	
	private String[] medicine = { "Pain Killers", "Antibiotics", "Steroids",
			"Beta-Blockers", "Anti-Depressants", "Anticoagulants" };
	
	private String[] allergic = { "None", "Nuts", "Penicillin", "Stings",
			"Seafood", "Hayfever", "Animals", "Latex" };

	private Person displayedPerson;
	private List<Person> search_results;
	
	@Override
	public void initialize(URL fxmlFilelocation, ResourceBundle resources) {

		labelSliders();
		loadArrayLists();
		runValidSearch();
		updateQueue();
		buttonFunction();
		treatmentRoomEggTimer();
		toolTime();		
				
		try {
			client = new RMIClient(this);
			log("Connected to server and registered for updates.");
		} catch (RemoteException | MalformedURLException | NotBoundException e) {
			log("Failed to connect to the server.");
			e.printStackTrace();
		}
	}
	
	/**
	 * performs actions to conduct valid searches
	 */
	private void runValidSearch() {
		
		search_NHS_No.setOnMouseExited(e -> {
			
			if (search_NHS_No.getText().length() == 10) {
			
			search_database.setDisable(false);
			
			} 				
		});	
		
		search_Surname.setOnMouseExited(e -> {
			
			search_database.setDisable(false);
			List<Person> matchingPeople = null;

			try {
				matchingPeople = client.getServer().searchPersonByDetails(search_NHS_No.getText() ,search_First_Name.getText(), search_Surname.getText(), search_DOB.getText(), search_Postcode.getText(), search_Telephone_No.getText());
			} catch (RemoteException ex) {
				System.err.println("Server communication error.");
				ex.printStackTrace();
			}	
			
			if (matchingPeople.size() > 0) {
				
			search_patient_results.addAll(matchingPeople);				
			patient_finder.setItems(search_patient_results);
			
			}});
	}

	/**
	 * sets graphical timer in treatment room view
	 */
	private void treatmentRoomEggTimer() {
		
		int eggtimer = 1;
		switch(eggtimer) {
		case 4: countdown.setHeight(240.0); countdown.setLayoutY(83.0); break;
		case 3: countdown.setHeight(180.0); countdown.setLayoutY(143.0); break;
		case 2: countdown.setHeight(120.0); countdown.setLayoutY(203.0); break;
		case 1: countdown.setHeight(60.0); countdown.setLayoutY(263.0); break;
		}		
	}

	/**
	 * turns off server with control action
	 */
	public void closeButtonAction() {
		if (client != null) {
			client.close();
		}
		Platform.exit();
	}

	/**
	 * adds tags to slider controls
	 */
	private void labelSliders() {

		respiratory_rate.setLabelFormatter(new StringConverter<Double>() {
			@Override
			public String toString(Double n) {
				if (n < 0.5) { return "<10pm"; 	}
				if (n < 50.5) { return "10-30pm"; } 
				else { return ">30pm";	}
				}

			@Override
			public Double fromString(String s) {
				switch (s) {
				case "<10pm": return 0d;
				case "10-30pm":	return 1d;
				case ">30pm": return 2d;
				default: return 1d;
				}
			}
		});

		respiratory_rate.setOnMouseClicked(e -> {
			
			if (respiratory_rate.getValue() < 0.5 || respiratory_rate.getValue() > 50.5  ) {
			urg.setDisable(false);
			urg.setStyle("-fx-base: orange;");
			semi_urg.setDisable(true); semi_urg.setStyle(null);
			non_urg.setDisable(true); non_urg.setStyle(null);
			}});

		pulse_rate.setLabelFormatter(new StringConverter<Double>() {
			@Override
			public String toString(Double n) {
				if (n < 0.5) return "<40bpm";
				if (n < 50.5) return "40-120bpm";
				return ">120bpm";
			}

			@Override
			public Double fromString(String s) {
				switch (s) {
				case "<40bpm": return 0d;
				case "40-120bpm": return 1d;
				case ">120bpm": return 2d;
				default: return 1d;
				}
			}});

		pulse_rate.setOnMouseClicked(e -> {
			
			if (pulse_rate.getValue() < 0.5 || pulse_rate.getValue() > 50.5  ) {
			urg.setDisable(false);
			urg.setStyle("-fx-base: orange;");
			semi_urg.setDisable(true); semi_urg.setStyle(null);
			non_urg.setDisable(true); non_urg.setStyle(null);
			}});
	}

	/**
	 * Update the UI to display the current state of the queue List
	 */
	private synchronized void updateQueue() {
		// Clear the observable list that contains the patients displayed on the UI
		QList.clear();
		
		// Loop through each of the patients in queueList
		for (Patient patient : queueList) {
			// Concatenate the patients first and second name and add them to the observable queue.
			QList.add(patient.getPerson().getFirstName() + " " + patient.getPerson().getLastName());
		}
		
		queue.setItems(QList);
	}
	
	/**
	 * Update the UI to display the current state of the treatment rooms
	 */
	public synchronized void updateTreatmentRooms() {
		
		// Clear the observable lists that hold the names of patients in the treatment rooms
		// and the patient being treated by the on call team
		trList.clear();
		onCallList.clear();
		
		// Loop through the various facilities help in the treatmentFacilities list
		for (TreatmentFacility facility : treatmentFacilities) {
			
			// Check if there is a patient in the facility
			Patient patient = facility.getPatient();
			
			// Declare a string to hold the patient name
			String patientName = "";
			// If there is a patient concatenate their first and last name, else leave first name as a blank string
			if (patient != null) {
				patientName = patient.getPerson().getFirstName() + 
						" " + patient.getPerson().getLastName();
			}
			
			// If the current facility is the on call team, add their name to the on call team box on the UI
			if (facility instanceof OnCallTeam) {
				onCallList.add(patientName);
			} else if (facility instanceof TreatmentRoom) { // if the facility is a treatment room 

				// Cast the facility to a treament room so the room number can be accessed
				TreatmentRoom room = (TreatmentRoom)facility;
				try {
					// Add the patients name to the observable array in the correct position for the room they're in.
					trList.add(room.getRoomNumber(), patientName);  //array1[room.getRoomNumber()] = patientName;
				} catch (Exception ex) {
					System.err.println("failed!!!!!!!!!!!!!");
				}}
		}
	}
	
	/**
	 * Add tags to listviews
	 */
	private void loadArrayLists() {
			
		ocu.add("On Call Unit");
		on_call_list.setItems(ocu);
		on_call_list.setTooltip(new Tooltip("On Call Unit"));
		
		for (int i = 0; i < treatmentRoomNum.length; i++) {
			treatmentRoomNum[i] = "Treatment Room "+(i+1);
		}		
		trno.addAll(treatmentRoomNum);
		treatment_room_list.setItems(trno);		
		
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

	/**
	 * Controller method to set button functionality
	 * uses lambda expression to handle events (e->)
	 */
	private void buttonFunction() {		
		
		settings.setOnAction(e -> {
			VBox configurations = new VBox();
			
		});
		
		login.setOnAction(e -> {

			Label l1 = new Label("Welcome to Triage!");
			TextField tf1 = new TextField();
			PasswordField tf2 = new PasswordField();
			AnchorPane ap1 = new AnchorPane();
			Button bt1 = new Button();
			l1.setLayoutX(15);
			tf1.setPromptText("Username");
			tf1.setLayoutY(26);
			tf2.setPromptText("Password");
			tf2.setLayoutY(52);
			bt1.setText("Login");
			bt1.setLayoutY(80);
			
			bt1.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent event) {
					
					Staff s1 = new Staff();					
					boolean logMeIn = false;					
					String _user = tf1.getText();
					String _pass = tf2.getText();
					String db_pass = s1.setEmployeePassword(_pass);
					List<Staff> matchingPeople1 = null;
					
					try {
						matchingPeople1 = client.getServer().searchStaffByDetails(_user, db_pass);
						
						if (matchingPeople1.size() > 0) {
							logMeIn = true;
						}
						
					} catch (RemoteException ex) {
						System.err.println("Server communication error.");
						ex.printStackTrace();
					}
					if (logMeIn == true){
					p.hide();					
					}
				}});
			
			ap1.setMinWidth(100);
			ap1.getChildren().add(l1);
			ap1.getChildren().add(tf1);
			ap1.getChildren().add(tf2);
			ap1.getChildren().add(bt1);
			ap1.setCursor(null);
			p.setContentNode(ap1);
			p.show(login);
		});

		UPGRADE.setOnAction(e -> {
			String potential = (String) queue.getSelectionModel()
					.getSelectedItem();
			if (potential != null) {
				queue.getSelectionModel().clearSelection();
				QList.remove(potential);

				trList.remove(0);
				trList.add(3, "");
				trList.add(3, potential);
				
				outputTextArea.appendText(potential+" is now an emergency!\n");
			}
		});
		trooms.setItems(trList);
		
		Q_view.setOnAction(e -> {
			
			p1.show(Q_view);				
		});
		
		TRooms_view.setOnAction(e -> {
			
			p2.show(TRooms_view);						
		});

		search_database.setOnAction(e -> {

			outputTextArea.appendText(search_Surname.getText()+", "+search_First_Name.getText()+" ready for Triage!\n");
			List<Person> matchingPeople = null;

			try {
				matchingPeople = client.getServer().searchPersonByDetails(search_NHS_No.getText() ,search_First_Name.getText(), search_Surname.getText(), search_DOB.getText(), search_Postcode.getText(), search_Telephone_No.getText());
			} catch (RemoteException ex) {
				System.err.println("Server communication error.");
				ex.printStackTrace();
			}	
			
			// If there were people matching the criteria, display them to the user
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
			
			enableTriage();
			clearSearchFields();
		});

		tb1.setOnAction(e -> {
			outputTextArea.appendText(textfield_Surname.getText()+", "+textfield_First_Name.getText()+" has a blocked airway!\n");
			tb1.setText("!");
			tb1.setStyle("-fx-base: salmon;");
			emergency.setDisable(false);
			emergency.setStyle("-fx-base: red;");
		});

		tb2.setOnAction(e -> {
			outputTextArea.appendText(textfield_Surname.getText()+", "+textfield_First_Name.getText()+" has difficulty breathing!\n");
			tb2.setText("!");
			tb2.setStyle("-fx-base: salmon;");
			emergency.setDisable(false);
			emergency.setStyle("-fx-base: red;");
		});

		tb3.setOnAction(e -> {
			outputTextArea.appendText(textfield_Surname.getText()+", "+textfield_First_Name.getText()+" has suspected cervia spine trauma!\n");
			tb3.setText("!");
			tb3.setStyle("-fx-base: salmon;");
			emergency.setDisable(false);
			emergency.setStyle("-fx-base: red;");
		});

		tb4.setOnAction(e -> {
			outputTextArea.appendText(textfield_Surname.getText()+", "+textfield_First_Name.getText()+" has circulation difficulties!\n");
			tb4.setText("!");
			tb4.setStyle("-fx-base: salmon;");
			emergency.setDisable(false);
			emergency.setStyle("-fx-base: red;");
		});

		tb5.setOnAction(e -> {
			outputTextArea.appendText(textfield_Surname.getText()+", "+textfield_First_Name.getText()+" is incapacitated!\n");
			tb5.setText("!");
			tb5.setStyle("-fx-base: salmon;");
			emergency.setDisable(false);
			emergency.setStyle("-fx-base: red;");
		});

		tb6.setOnAction(e -> {
			outputTextArea.appendText(textfield_Surname.getText()+", "+textfield_First_Name.getText()+" has been exposed to extreme conditions!\n");
			tb6.setText("!");
			tb6.setStyle("-fx-base: salmon;");
			emergency.setDisable(false);
			emergency.setStyle("-fx-base: red;");
		});

		emergency.setOnAction(e -> {
			
			Patient emergency_patient = new Patient(new Person(textfield_NHS_Num.getText(), textfield_Title.getText(), textfield_First_Name.getText(), textfield_Surname.getText(), textfield_DOB.getText(), textfield_Address.getText(), textfield_Postcode.getText(), textfield_Telephone.getText(), textfield_Blood_Group.getText(), null, null, null), Urgency.EMERGENCY);
			emergency_room.add(emergency_patient);
			treatmentRoomsView(emergency_room);
			trList.add(emergency_patient.getPatientName());
			trooms.setItems(trList);
						
			outputTextArea.appendText("EMERGENCY!\n"+textfield_Surname.getText()+", "+textfield_First_Name.getText()+" sent to the Treatment room!\n");
									
			try {
				client.getServer().addPrimaryPatient(displayedPerson, tb1.isSelected(), tb2.isSelected(), tb3.isSelected(), tb4.isSelected(), tb5.isSelected(), tb6.isSelected());
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			
			clearTextFields();
			resetTriage();
		});

		urg.setOnAction(e -> {
			
			Patient urgent_patient = new Patient(new Person(textfield_NHS_Num.getText(), textfield_Title.getText(), textfield_First_Name.getText(), textfield_Surname.getText(), textfield_DOB.getText(), textfield_Address.getText(), textfield_Postcode.getText(), textfield_Telephone.getText(), textfield_Blood_Group.getText(), null, null, null), Urgency.URGENT);
			waiting_room.add(urgent_patient);
			waitingRoomView(waiting_room);
			QList.add(urgent_patient.getPatientName());
			queue.setItems(QList);
			
			outputTextArea.appendText("URGENT!\n"+textfield_Surname.getText()+", "+textfield_First_Name.getText()+" has been added to the Queue!\n");
			
			clearTextFields();
			resetTriage();
		});

		semi_urg.setOnAction(e -> {
			
			Patient semi_urgent_patient = new Patient(new Person(textfield_NHS_Num.getText(), textfield_Title.getText(), textfield_First_Name.getText(), textfield_Surname.getText(), textfield_DOB.getText(), textfield_Address.getText(), textfield_Postcode.getText(), textfield_Telephone.getText(), textfield_Blood_Group.getText(), null, null, null), Urgency.SEMI_URGENT);
			waiting_room.add(semi_urgent_patient);
			waitingRoomView(waiting_room);
			QList.add(semi_urgent_patient.getPatientName());
			queue.setItems(QList);
			outputTextArea.appendText("Semi-Urgent:\n"+textfield_Surname.getText()+", "+textfield_First_Name.getText()+" has been added to the Queue!\n");
			
			clearTextFields();
			resetTriage();
		});

		non_urg.setOnAction(e -> {
			
			Patient non_urgent_patient = new Patient(new Person(textfield_NHS_Num.getText(), textfield_Title.getText(), textfield_First_Name.getText(), textfield_Surname.getText(), textfield_DOB.getText(), textfield_Address.getText(), textfield_Postcode.getText(), textfield_Telephone.getText(), textfield_Blood_Group.getText(), null, null, null), Urgency.NON_URGENT);
			waiting_room.add(non_urgent_patient);
			waitingRoomView(waiting_room);
			QList.add(non_urgent_patient.getPatientName());
			queue.setItems(QList);
			outputTextArea.appendText(textfield_Surname.getText()+", "+textfield_First_Name.getText()+" has been added to the Queue!\n");
			
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
		
		extend.setOnAction(e -> {p3.show(extend);});
	}

	/**
	 * View patient details in... de...tail.......
	 * @param ObservableList<Patient> oL
	 */
	private void treatmentRoomsView(ObservableList<Patient> o ) {
				
		AnchorPane ap3 = new AnchorPane();			
		TableView<Patient>  treatmentRoomTable = new TableView<Patient>();
		
		TableColumn patName = new TableColumn("Patient");			
		patName.setCellValueFactory(new PropertyValueFactory<Object, String>("patientName"));
		
		TableColumn Urgency = new TableColumn("Urgency");			
		Urgency.setCellValueFactory(new PropertyValueFactory<Object, String>("urgency"));
		
		TableColumn WaitingTime = new TableColumn("Wait Time");			
		WaitingTime.setCellValueFactory(new PropertyValueFactory<Object, String>("waitTime"));
		
		Patient pat = new Patient(new Person("", null, "Ciaran", "Molloy", null, null, null, null, null, null, null, null), null);
		o.add(pat);
		treatmentRoomTable.setItems(o);
		treatmentRoomTable.getColumns().addAll(patName, Urgency, WaitingTime);
		ap3.getChildren().add(treatmentRoomTable);
		p2.setContentNode(ap3);
		
	}
	
	/**
	 * View patient details in... de...tail.......
	 * @param ObservableList<Patient> oL
	 */
	private void waitingRoomView(ObservableList<Patient> oL ) {
		
		AnchorPane ap2 = new AnchorPane();			
		TableView<Patient>  waitingRoomTable = new TableView<Patient>();
		
		TableColumn patName1 = new TableColumn("Patient");			
		patName1.setCellValueFactory(new PropertyValueFactory<Object, String>("patientName"));
		
		TableColumn Urgency1 = new TableColumn("Urgency");			
		Urgency1.setCellValueFactory(new PropertyValueFactory<Object, String>("urgency"));
		
		TableColumn WaitingTime1 = new TableColumn("Wait Time");			
		WaitingTime1.setCellValueFactory(new PropertyValueFactory<Object, String>("waitTime"));
		
		Patient pat = new Patient(new Person("", null, "Ciaran", "Molloy", null, null, null, null, null, null, null, null), null);
		oL.add(pat);
		waitingRoomTable.setItems(oL);
		waitingRoomTable.getColumns().addAll(patName1, Urgency1, WaitingTime1);
		ap2.getChildren().add(waitingRoomTable);
		p2.setContentNode(ap2);
		p2.show(Q_view);
		
	}

	/**
	 * clear search fields after patient selected
	 */
	private void clearSearchFields() {

		search_NHS_No.clear();
		search_First_Name.clear();
		search_Surname.clear();
		search_DOB.clear();
		search_Postcode.clear();
		search_Telephone_No.clear();
	}

	/**
	 * clear patient details after priority set
	 */
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

	/**
	 * reset controls to preset state
	 */
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

	/**
	 * enable controls to elicit triage 
	 */
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
			ArrayList<TreatmentFacility> treatmentFacilities) {
		// Store the passed in queue and treatment facilities
		this.queueList = queue;
		this.treatmentFacilities = treatmentFacilities;
		
		// Call run later to run updates to the UI on the JavaFX thread
		Platform.runLater(new Runnable() {
			
			@Override
			public void run() {
				// Update the queue on the UI with the updated data
				updateQueue();
				// Update the treatment room list on the UI with the updated data
				updateTreatmentRooms();
			}
		});
	}

	@Override
	public void log(String log) {
		// Call run later to run updates to the UI on the JavaFX thread
		Platform.runLater(new Runnable() {
			
			@Override
			public void run() {
					outputTextArea.appendText(log + "\n");
			}
		});
	}

	/**
	 * Set tooltips for UI controls
	 */
	private void toolTime() {
		
		triage_tab.setTooltip(new Tooltip("Search Patient Database and commence triage"));
		tr_tab.setTooltip(new Tooltip("Display Treatment Room details"));
		stats_tab.setTooltip(new Tooltip("Display Management System Statistics"));
		login.setTooltip(new Tooltip("Log in here"));
		UPGRADE.setTooltip(new Tooltip("Select a Patient in the Queue to Upgrade to EMERGENCY!"));		
		Q_view.setTooltip(new Tooltip("View Queued Patient Details"));
		TRooms_view.setTooltip(new Tooltip("View Treatment Room Patient Details"));
		search_database.setTooltip(new Tooltip("Search NHS DBMS"));
		tb1.setTooltip(new Tooltip("Patient has BLOCKED AIRWAY!"));
		tb2.setTooltip(new Tooltip("Patient is NOT BREATHING!"));
		tb3.setTooltip(new Tooltip("Patient is LOSING BLOOD!"));
		tb4.setTooltip(new Tooltip("Patient is LOSING BLOOD!"));
		tb5.setTooltip(new Tooltip("Patient is INCAPACITATED!"));
		tb6.setTooltip(new Tooltip("Patient has been EXPOSED TO EXTREME CONDITIONS!"));
		emergency.setTooltip(new Tooltip("Send Patiently directly to Treatment Room"));
		urg.setTooltip(new Tooltip("Send Patient to Waiting Room with URGENT priority"));
		semi_urg.setTooltip(new Tooltip("Send Patient to Waiting Room with SEMI-URGENT priority"));
		non_urg.setTooltip(new Tooltip("Send Patient to Waiting Room"));
		extend.setTooltip(new Tooltip("Extend Patient Treatment time"));
		search_DOB.setTooltip(new Tooltip("Enter D.o.B. in the Format DD/MM/YYYY"));
		search_First_Name.setTooltip(new Tooltip("Search by Name"));
		search_NHS_No.setTooltip(new Tooltip("Search by Patient NHS Number"));
		search_Postcode.setTooltip(new Tooltip("Enter Postcode"));
		search_Surname.setTooltip(new Tooltip("Search by Name"));
		search_Telephone_No.setTooltip(new Tooltip("Enter Telephone Number"));	
		respiratory_rate.setTooltip(new Tooltip("Breaths taken per minute"));
		pulse_rate.setTooltip(new Tooltip("Heart beats taken per minute"));
		breathing_yes.setTooltip(new Tooltip("Select option"));
		walk.setTooltip(new Tooltip("The patient is mobile and stable"));
		walk_no.setTooltip(new Tooltip("The patient is immobile and requires assistence"));
		conditions.setTooltip(new Tooltip("Select option"));
		medication.setTooltip(new Tooltip("Select option"));
	}
}
