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
import javafx.scene.control.PasswordField;
import javafx.scene.control.Slider;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.StringConverter;

public class RevController implements Initializable, ClientCallback {
	
	TableRow trow = new TableRow();

	private RMIClient client;
	
	@FXML
	private TextArea outputTextArea;
	
	@FXML
	private ToggleButton tb1, tb2, tb3, tb4, tb5, tb6;

	@FXML
	private ListView queue, trooms, treatment_room_list, on_call_list, on_call;

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
	PopOver p1 = new PopOver();
	PopOver p2 = new PopOver();
	
	

	private final ObservableList<String> QList = FXCollections.observableArrayList();
	private final ObservableList<String> trList = FXCollections.observableArrayList();
	private final ObservableList trno = FXCollections.observableArrayList();
	private final ObservableList ocu = FXCollections.observableArrayList();
	private final ObservableList<String> onCallList = FXCollections.observableArrayList();
	private final ObservableList breathingList = FXCollections.observableArrayList();
	private final ObservableList medi_condition = FXCollections.observableArrayList();
	private final ObservableList meds = FXCollections.observableArrayList();
	private final ObservableList allergy_list = FXCollections.observableArrayList();

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
		search();
		
		try {
			client = new RMIClient(this);
			log("Connected to server and registered for updates.");
		} catch (RemoteException | MalformedURLException | NotBoundException e) {
			log("Failed to connect to the server.");
			e.printStackTrace();
		}

	}
	
	public void closeButtonAction() {
		if (client != null) {
			client.close();
		}
		Platform.exit();
	}

	private void search() {
		// TODO Auto-generated method stub
		
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
				}
			}

		}
	}
	
	private void loadArrayLists() {

		ocu.clear();

		/*array[0] = "Jim";
		array[1] = "Mary";
		array[2] = "Illy";
		array[3] = "OMM";
		array[4] = "B3B4";
		array[5] = "A";
		array[6] = "JHSjhd";
		array[7] = "0987";
		array[8] = "bn67BNhgg";
		array[9] = "Kim";
		*/
		
		array1[0] = "JimJonJoe";
		array1[1] = "JonJoe";
		array1[2] = "Joe";
		array1[3] = "JJJoe";
		
		onCall[0] = "On Call Unit";
		
		// Link the observable list of patients in the queue, to the queue ListView
		queue.setItems(QList);
		
		// Link the observable list of treatment rooms to the treatment rooms ListView
		trList.addAll(array1);
		trooms.setItems(trList);
		

		treatmentRoomNum[0] = "Treatment Room 1";
		treatmentRoomNum[1] = "Treatment Room 2";
		treatmentRoomNum[2] = "Treatment Room 3";
		treatmentRoomNum[3] = "Treatment Room 4";
		
		trno.addAll(treatmentRoomNum);


		ocu.addAll(onCall);


		treatment_room_list.setItems(trno);
		on_call_list.setItems(ocu);
		on_call.setItems(onCallList);

		
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
		
		login.setOnAction(e -> {

			Label l1 = new Label();
			TextField tf1 = new TextField();
			PasswordField tf2 = new PasswordField();
			AnchorPane ap1 = new AnchorPane();
			Button bt1 = new Button();
			l1.setText("Welcome to Triage!");
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
					List<Staff> matchingPeople1=null;
					
					try{
						matchingPeople1 = client.getServer().searchStaffByDetails(_user, db_pass);
						
						if (matchingPeople1.size() >0) {
							logMeIn = true;
						}
					} catch (RemoteException ex) {
						System.out.println("Server communication error.");
						ex.printStackTrace();
					
					if(logMeIn == true){
						p.hide();
						
					}
				}}});
			
			
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
			
			AnchorPane ap2 = new AnchorPane();
			TableView tv1 = new TableView();
			tv1.setItems(QList);
			ap2.getChildren().add(tv1);
			p1.setContentNode(ap2);
			p1.show(Q_view);
			
		});
		
		TRooms_view.setOnAction(e -> {
			
			AnchorPane ap3 = new AnchorPane();
			TableView<Patient> tv2 = new TableView<Patient>();
			Patient pat = new Patient();
			Person pete = new Person();
			pat.setPerson(pete);
			pat.setUrgency(Urgency.URGENT);
			ObservableList<Object> o = FXCollections.observableArrayList(pat);
			o.add(pete);

			TableColumn person = new TableColumn("Person");
			person.setCellValueFactory(new PropertyValueFactory<Patient, String>("firstName"));
			TableColumn Urgency = new TableColumn("Urgency");
			Urgency.setCellValueFactory(new PropertyValueFactory<Patient, String>("urgency"));
			TableColumn WaitingTime = new TableColumn("Wait Time");
			WaitingTime.setCellValueFactory(new PropertyValueFactory<Patient, String>("waitTime"));
						
			tv2.setItems(o);
			tv2.getColumns().addAll(person, Urgency,WaitingTime);
			ap3.getChildren().add(tv2);
			p2.setContentNode(ap3);
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
			
			outputTextArea.appendText("EMERGENCY!\n"+textfield_Surname.getText()+", "+textfield_First_Name.getText()+" sent to the Treatment room!\n");
			trList.remove(0);
			trList.add(3, textfield_Surname.getText() + ", " + textfield_First_Name.getText());
			TableRow tr1 = new TableRow();
			ListCell lr1 = new ListCell();
			
			try {
				client.getServer().addPrimaryPatient(displayedPerson, tb1.isSelected(), tb2.isSelected(), tb3.isSelected(), tb4.isSelected(), tb5.isSelected(), tb6.isSelected());
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			
			clearTextFields();
			resetTriage();
		});

		urg.setOnAction(e -> {
			outputTextArea.appendText("URGENT!\n"+textfield_Surname.getText()+", "+textfield_First_Name.getText()+" has been added to the Queue!\n");
			QList.remove(0);
			QList.add(0, textfield_Surname.getText() + ", "	+ textfield_First_Name.getText());
			clearTextFields();
			resetTriage();
		});

		semi_urg.setOnAction(e -> {
			outputTextArea.appendText("Semi-Urgent:\n"+textfield_Surname.getText()+", "+textfield_First_Name.getText()+" has been added to the Queue!\n");
			QList.remove(0);
			QList.add(4, textfield_Surname.getText() + ", "	+ textfield_First_Name.getText());
			clearTextFields();
			resetTriage();
		});

		non_urg.setOnAction(e -> {
			outputTextArea.appendText(textfield_Surname.getText()+", "+textfield_First_Name.getText()+" has been added to the Queue!\n");
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
	

}
