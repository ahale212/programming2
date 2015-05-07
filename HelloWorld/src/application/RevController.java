package application;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.naming.AuthenticationException;

import org.controlsfx.control.Notifications;
import org.controlsfx.control.PopOver;

import application.RMIClient;
import uk.ac.qub.exjavaganza.hqbert.server.v01.ClientCallback;
import uk.ac.qub.exjavaganza.hqbert.server.v01.ExtensionReason;
import uk.ac.qub.exjavaganza.hqbert.server.v01.Job;
import uk.ac.qub.exjavaganza.hqbert.server.v01.OnCallTeam;
import uk.ac.qub.exjavaganza.hqbert.server.v01.Patient;
import uk.ac.qub.exjavaganza.hqbert.server.v01.Person;
import uk.ac.qub.exjavaganza.hqbert.server.v01.Staff;
import uk.ac.qub.exjavaganza.hqbert.server.v01.TreatmentFacility;
import uk.ac.qub.exjavaganza.hqbert.server.v01.TreatmentRoom;
import uk.ac.qub.exjavaganza.hqbert.server.v01.Urgency;
import uk.ac.qub.exjavaganza.hqbert.server.v01.RemoteServer.ConnectionState;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Slider;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.StringConverter;

/**
 * The view class—RevController—carries out event handling. EventHandler 
 * method and HandleOverride method frequently appear in the code. RevController 
 * class controls the view, implementing JavaFX interface Initializable, linking 
 * the FXML file (TRIAGEv4) with the project Java code.
 * 
 * @author Ciaran Molloy
 * @author James Thompson
 * @author Tom Couchman
 * @author Adam Hale
 * @author Alan Whitten
 * @author Adrian Curran
 * @author Jack Ferguson
 */
public class RevController implements Initializable, ClientCallback {

	// local client instance 
	private RMIClient client;

	@FXML // toolbar set in BorderPane left
	private ToolBar toolbar_left;

	@FXML // menuItems in menubar
	private MenuItem settings, close_system;

	@FXML // graphic eggtimer display for patient treatment time remaining
	private Rectangle countdown;

	@FXML
	// comment textfields for logging activity and logging patient treatment note respectively
	private TextArea outputTextArea, tr_treatment_notes;

	@FXML // toggle buttons to signify primary triage and if server is connected
	private ToggleButton tb1, tb2, tb3, tb4, tb5, tb6, server_check;

	@FXML // various lists displayed in BorderPane centre representing queue and treatment rooms
	private ListView queue, trooms, treatment_room_list, on_call_list, on_call,
			doctor_on_duty;

	@FXML // various buttons to set on action events for event listening and handling
	private Button login, re_assign, search_database, emergency, Q_view,

			TRooms_view, urg, semi_urg, non_urg, extend, tr_button_save, cancel_extension, sign, search_queue, BTstat;

	@FXML // sliders deployed in secondary triage
	private Slider respiratory_rate, pulse_rate;

	@FXML // comboboxes for allowing a limited selection from a particular list
	private ComboBox conditions, medication, breathing_yes, allergy, tr_allergy;

	@FXML // comboboxes instantiated to type String

	private PieChart chart;
	 
	@FXML // comboboxes instantiated to type String
	private ComboBox<String> patient_finder, select_tr;

	@FXML // checkboxes deployed in secondary triage
	private CheckBox walk, walk_no;

	@FXML // tabs in tabbedpane, BorderPane bottom
	private Tab triage_tab, tr_tab, stats_tab;
	
	@FXML // TableView of on call staff
	private TableView<Staff> onCallUnitStaff;

	@FXML // various textFields for user input or displaying patient details
	private TextField search_NHS_No, search_First_Name, search_Surname,
			search_DOB, search_Postcode, search_Telephone_No,
			textfield_NHS_Num, textfield_Postcode, textfield_Title,
			textfield_First_Name, textfield_Surname, textfield_DOB,
			textfield_Address, textfield_Telephone, textfield_Blood_Group,
			triage_nurse_on_duty, admin, tr_patient_urgency, tr_incident_details, tr_textfield_NHS_Num,
			tr_textfield_Title, tr_textfield_First_Name, tr_textfield_Surname, tr_textfield_DOB,
			tr_textfield_Address, tr_textfield_Telephone, tr_textfield_Blood_Group, tr_textfield_Postcode, triagePane_triageNurse;

	@FXML // labels set to demonstrate statistics
	private Label current_total, current_in_queue, av_visit_time, av_treatment_time, daily_emergencies, daily_urgent, daily_semi_urgent, daily_non_urgent, daily_tr_extended, daily_avg_wait, patients_rejected, max_wait_time_exceeded, daily_avg_semi_urgent, daily_avg_non_urgent;

	// PopOver custom javafx controls to seamlessly bring the user to a new window
	PopOver login_pop = new PopOver();
	PopOver q_pop = new PopOver();
	PopOver tr_pop = new PopOver();
	PopOver extension_pop = new PopOver();

	Staff staff = new Staff();
	Staff loggedInUser = null;

	List<Person> matchingPeople, matchingPeople1;
	
	/**
	 * JavaFX Collections Framework provides ObservableList to show lists of data on screen
	 */
	private final ObservableList<Staff> on_call_unit_staff = FXCollections.observableArrayList();
	private final ObservableList<Staff> on_call_unit_docs = FXCollections.observableArrayList();
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
	private final ObservableList<String> search_patient_results = FXCollections.observableArrayList();

	/**
	 * The list that holds the Patients in the queue
	 */
	List<Patient> queueList = new LinkedList<Patient>();
	
	/**
	 * The list that holds the staff on duty
	 */
	List<Staff> onCallStaff = new LinkedList<Staff>();
	List<Staff> onCallDoctors = new LinkedList<Staff>();

	/**
	 * The list of Treatment Rooms / On call team
	 */
	List<TreatmentFacility> treatmentFacilities = new ArrayList<TreatmentFacility>();
	/**
	 * private Integer for the original treatment room count by the spec
	 */

	private Integer roomCount = 5;
	
	/**
	 * private Integer for the newly desired room count specified by the user
	 */
	private Integer newRoomCount = 0;

	private String[] array = new String[10];
	private String[] array1 = new String[5];
	private String[] onCall = new String[1];
	List<String> treatmentRoomNum = new ArrayList<String>();
	// private String[] treatmentRoomNum = new String[5];
	private String[] breaths = new String[2];

	// String arrays for populating various observable controls. 
	
	private String[] condition = { "Neurological", "Respiratory",
			"Dermatological", "Endrocrinal", "Circulatory", "Auto-Immune",
			"Viral, none" };

	private String[] medicine = { "Pain Killers", "Antibiotics", "Steroids",
			"Beta-Blockers", "Anti-Depressants", "Anticoagulants, none" };

	private String[] allergic = { "None", "Nuts", "Penicillin", "Stings",
			"Seafood", "Hayfever", "Animals", "Latex, none" };

	// Display Person used to show patient details in tabbed panes.
	private Person displayedPerson;

	/**saved preferences for editable values that should persist between launches*/
	private Preferences prefs;
	private final String PORT_PREF_NAME = "PORT_PREF";
	private final String ADDRESS_PREF_NAME = "ADDRESS_PREF";
	private final String  LOCAL_HOST_PREF_NAME = "LOCAL_HOST_PREF";
	
	/**
	 * @Override implemented method. Interface initialise. 
	 */
	@Override
	protected void finalize() throws Throwable {
		// Implemented for garbage collection.
		if (client != null) {
			client.close();
		}
		super.finalize();
	}

	/**
	 * The main utility method for initialising the controls and the behaviour of 
	 * our view class.
	 */
	@Override
	public void initialize(URL fxmlFilelocation, ResourceBundle resources) {

		labelSliders();
		loadArrayLists();
		runValidSearch();
		updateQueue();
		buttonFunction();
		toolTime();		
		getPrefsFile();

	}

	/**
	 * Sets layout colours and patterns
	 * unimplemented method to allow for future customisation
	 */
	private void colours() {

		triage_tab.setStyle("-fx-base: skyblue;");
		tr_tab.setStyle("-fx-base: lightpink;");
		stats_tab.setStyle("-fx-base: pink;");
		toolbar_left.setStyle("-fx-base: black;");
		login.setStyle("-fx-base: darkblue;");

	}

	/**
	 * load the prefs file and assign the instance var
	 */
	public void getPrefsFile(){
		prefs = Preferences.userRoot().node(this.getClass().getName());
	}
	
	/**
	 * performs actions to conduct valid searches
	 */
	private void runValidSearch() {

		// When the user enters text into the NHS text box
		search_NHS_No.setOnKeyTyped(e -> {
			checkCanPerformAutoSearch();
		});

		// When the user enters text into the first name text box
		search_First_Name.setOnKeyTyped(e -> {
			checkCanPerformAutoSearch();
		});
		
		// When the user enters text into the last name text box
		search_Surname.setOnKeyTyped(e -> {
			checkCanPerformAutoSearch();
		});
		
		// When the user enters text into date of birth text box
		search_DOB.setOnKeyTyped(e -> {
			checkCanPerformAutoSearch();
		});
		
		// When the user enters text into postcode text box
		search_Postcode.setOnKeyTyped(e -> {
			checkCanPerformAutoSearch();
		});
		
		// When the user enters text into search_Telephone_No text box
		search_Telephone_No.setOnKeyTyped(e -> {
			checkCanPerformAutoSearch();
		});
		
		// When a user selects a patient from the matching patient list 
		patient_finder.setOnAction( e-> {
			int selectedIndex = patient_finder.getSelectionModel().getSelectedIndex();
			if (selectedIndex > -1 && selectedIndex < matchingPeople.size()) {
				displayedPerson = matchingPeople.get(selectedIndex);
				displayTriagePerson(displayedPerson);
			}
			enableTriage();

		});

	}
	
	/**
	 * Check whether the input text will allow for auto search
	 */
	public void checkCanPerformAutoSearch() {
		
		if (search_NHS_No.getText().length() > 9 || 
			search_First_Name.getText().length() > 1 || 
			search_Surname.getText().length() > 1 ||
			search_Postcode.getText().length() > 1 ||
			search_Telephone_No.getText().length() > 1){
	
			displayMatchingPeople();
		} else {
			// Close the patient finder combobox
			patient_finder.hide();
			search_patient_results.clear();
		}
	}
	

	/**
	 * Perform the search based on the user input
	 */
	public void displayMatchingPeople() {
		// Enable the search button
		search_database.setDisable(false);
		// Get a list of people who match the search criteria
		matchingPeople = searchForPerson();

		// If results have been returned display them
		if (matchingPeople.size() > 0) {
			populateMatchingPatientList();
			patient_finder.show();
		}
	}

	/**
	 * sets graphical timer in treatment room view
	 */
	private void treatmentRoomEggTimer(TreatmentRoom room) {
		// Timer decrements on switch statement. 
		// Rectangle graphic shrinks in size at each case statement.
		switch (room.getTimeToAvailable()) {
		case 9:
			countdown.setHeight(270.0);
			countdown.setLayoutY(53.0);
			break;
		case 8:
			countdown.setHeight(240.0);
			countdown.setLayoutY(83.0);
			break;
		case 7:
			countdown.setHeight(210.0);
			countdown.setLayoutY(113.0);
			break;
		case 6:
			countdown.setHeight(180.0);
			countdown.setLayoutY(143.0);
			break;
		case 5:
			countdown.setHeight(150.0);
			countdown.setLayoutY(173.0);
			break;
		case 4:
			countdown.setHeight(120.0);
			countdown.setLayoutY(203.0);
			break;
		case 3:
			countdown.setHeight(90.0);
			countdown.setLayoutY(233.0);
			break;
		case 2:
			countdown.setHeight(60.0);
			countdown.setLayoutY(263.0);
			break;
		case 1:
			countdown.setHeight(30.0);
			countdown.setLayoutY(293.0);
			break;
		default:
			countdown.setHeight(300.0);
			countdown.setLayoutY(23.0);
			break;
		}
	}

	/**
	 * Adds the matching patients to a combo box
	 */
	public void populateMatchingPatientList() {

		// Clear the patient results array, ready to be refilled
		search_patient_results.clear();

		// Loop through each person in the results
		for (Person person : matchingPeople) {
			// Concatenate a string of the person's details
			String personDetails = String.format(
					"%-12s %s, %-10s %-25s %-10s %-10s %-11s",
					person.getNHSNum(), person.getLastName(),
					person.getFirstName(), person.getAddress(),
					person.getPostcode(), person.getCity(), person.getDOB());
			// Add the patient details to the results
			search_patient_results.add(personDetails);
		}

		// Display the results on screen
		patient_finder.setItems(search_patient_results);
	}

	/**
	 * adds tags to slider controls
	 */
	private void labelSliders() {
		// Labels slider for respiratory rate.
		respiratory_rate.setLabelFormatter(new StringConverter<Double>() {
			@Override
			public String toString(Double n) {
				if (n < 0.5) { return "<10pm"; }
				if (n < 50.5) { return "10-30pm"; }
				else { return ">30pm"; }
			}
			// Returns double value from slider.
			@Override
			public Double fromString(String s) {
				switch (s) { case "<10pm": return 0d;
				case "10-30pm": return 1d;
				case ">30pm": return 2d;
				default: return 1d;
				}
			}});
		// If slider deployed, "urgent" button will enable.
		respiratory_rate.setOnMouseClicked(e -> {

			if (respiratory_rate.getValue() < 0.5
					|| respiratory_rate.getValue() > 50.5) {
				urg.setDisable(false);
				urg.setStyle("-fx-base: orange;");
				semi_urg.setDisable(true);
				semi_urg.setStyle(null);
				non_urg.setDisable(true);
				non_urg.setStyle(null);
			}
		});
		// Labels slider for pulse rate.
		pulse_rate.setLabelFormatter(new StringConverter<Double>() {
			@Override
			public String toString(Double n) {
				if (n < 0.5) { return "<40bpm"; }
				if (n < 50.5) { return "40-120bpm"; }
				else { return ">120bpm"; }
			}
			// Returns double value from slider.
			@Override
			public Double fromString(String s) {
				switch (s) { case "<40bpm": return 0d;
				case "40-120bpm": return 1d;
				case ">120bpm": return 2d;
				default: return 1d;
				}
			}});
		// If slider deployed, "urgent" button will enable.
		pulse_rate.setOnMouseClicked(e -> {

			if (pulse_rate.getValue() < 0.5 || pulse_rate.getValue() > 50.5) {
				urg.setDisable(false);
				urg.setStyle("-fx-base: orange;");
				semi_urg.setDisable(true);
				semi_urg.setStyle(null);
				non_urg.setDisable(true);
				non_urg.setStyle(null);
			}});
	} // end labelSliders()

	/**
	 * Update the UI to display the current state of the queue List
	 */
	private synchronized void updateQueue() {
		// Clear the observable list that contains the patients displayed on the
		// UI
		QList.clear();

		// Loop through each of the patients in queueList
		for (Patient patient : queueList) {
			// Concatenate the patients first and second name and add them to
			// the observable queue.
			QList.add(patient.getPerson().getFirstName() + " "
					+ patient.getPerson().getLastName());
		}
		queue.setItems(QList);
		//waitingRoomView(waiting_room);

	}

	/**
	 * Update the UI to display the current state of the treatment rooms
	 */
	public synchronized void updateTreatmentRooms() {

		// Clear the observable lists that hold the names of patients in the
		// treatment rooms
		// and the patient being treated by the on call team
		trList.clear();
		onCallList.clear();

		// Loop through the various facilities help in the treatmentFacilities
		// list
		for (TreatmentFacility facility : treatmentFacilities) {

			// Check if there is a patient in the facility
			Patient patient = facility.getPatient();

			// Declare a string to hold the patient name
			String patientName = "";
			// If there is a patient concatenate their first and last name, else
			// leave first name as a blank string
			if (patient != null) {
				patientName = patient.getPatientName();
			}

			// If the current facility is the on call team, add their name to
			// the on call team box on the UI
			if (facility instanceof OnCallTeam) {
				onCallList.add(patientName);
			} else if (facility instanceof TreatmentRoom) { // if the facility
															// is a treatment
															// room

				// Cast the facility to a treament room so the room number can
				// be accessed
				TreatmentRoom room = (TreatmentRoom) facility;
				try {
					// Add the patients name to the observable array in the correct position for the room they're in.
					trList.add(room.getRoomNumber(), patientName);  //array1[room.getRoomNumber()] = patientName;

				} catch (Exception ex) {
					System.err.println("failed.");
				}
			}
		}
		// trooms is the observable list view: the aspect received in border pane.
		// To view the data we need to add an observable list to the list view control.
		// here we add the treatment room data to the list view for treatment rooms.
		trooms.setItems(trList);
		
		// Here we add the on-call data.
		on_call.setItems(onCallList);
		
		// Ensure that the updated details are being shown in the treatment room tab
		displaySelectedTreatmentRoom();
	}

	/**
	 * Add tags to listviews
	 */
	private void loadArrayLists() {

		ocu.add("On Call Unit");
		on_call_list.setItems(ocu);
		on_call_list.setTooltip(new Tooltip("On Call Unit"));

		// for loop iterates through and creates the treatment rooms
		for (int loop = 0; loop < roomCount; loop++) {
			// adds the treatment room
			treatmentRoomNum.add("Treatment Room" + (loop + 1));
		}
		// adds all the treatment rooms to the array list
		trno.addAll(treatmentRoomNum);

		treatment_room_list.setItems(trno);
		select_tr.setItems(trno);
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
		

		onCallUnitStaff = new TableView<Staff>(); 
		
		TableColumn role = new TableColumn("Role");			
		role.setCellValueFactory(new PropertyValueFactory<Object, String>("job"));
		
		TableColumn firstName_ocu = new TableColumn("Name");			
		firstName_ocu.setCellValueFactory(new PropertyValueFactory<Object, String>("firstName"));
		
		TableColumn lastName_ocu = new TableColumn("Surname");			
		lastName_ocu.setCellValueFactory(new PropertyValueFactory<Object, String>("lastName"));
			
		onCallUnitStaff.getColumns().addAll(role, firstName_ocu, lastName_ocu);
		
		for (Staff s : onCallStaff) {			
			// Check if there is a patient in the facility
			on_call_unit_staff.add(s);
		}		
		onCallUnitStaff.setItems(on_call_unit_staff);

		for (Staff docs : onCallDoctors) {
			on_call_unit_docs.add(docs);
		}
		doctor_on_duty.setItems(on_call_unit_docs);
	}
	
	/**
	 * new method to change the amount of treatment rooms - this is done through
	 * the front end using config - settings - then typing in a valid number
	 */
	private void newArrayList() {
		// remove the current treatment rooms
		trno.removeAll(treatmentRoomNum);
		// declare a difference var between old and new treatment room counts
		Integer difference = (newRoomCount - roomCount);
		// if the difference is greater than 0
		if (difference > 0) {
			// loop through until it reaches the new treatment room count
			for (int loop = roomCount; loop < newRoomCount; loop++) {
				// add the new treatment rooms to the array list
				treatmentRoomNum.add("Treatment Room" + (loop + 1));
				// make the new room count equal to the original one so this can
				// be changed the exact same way at any time
				roomCount = newRoomCount;
			}// end of loop
		}// end of if
			// if the difference is less than 0
		if (difference < 0) {
			// loop through until it reaches the new treatment room count
			for (int loop = roomCount; loop > newRoomCount; loop--) {
				// remove the unwanted treatment rooms from the array list
				treatmentRoomNum.remove("Treatment Room" + (loop));
				// make the new room count equal to the original one so this can
				// be changed the exact same way at any time
				roomCount = newRoomCount;
			}// end of loop
		}// end of if
			// add all the treatment rooms to the arraylist
		trno.addAll(treatmentRoomNum);
	}// end of method

	/**
	 * Controller method to set button functionality uses lambda expression to
	 * handle events (e->)
	 */
	private void buttonFunction() {	
		
		// use search fields to search through queue
		search_queue.setOnAction(e -> {
			
			for (Patient pat: queueList) {
				if (pat.getPerson().getNHSNum().toLowerCase().contains(search_NHS_No.getText()) ||
						pat.getPerson().getFirstName().toLowerCase().contains(search_First_Name.getText()) ||
						pat.getPerson().getLastName().toLowerCase().contains(search_Surname.getText()) ||
						pat.getPerson().getDOB().toLowerCase().contains(search_DOB.getText()) ||
						pat.getPerson().getPostcode().toLowerCase().contains(search_Postcode.getText()) ||
						pat.getPerson().getTelephone().toLowerCase().contains(search_Telephone_No.getText())) {
					if (pat!=null) {
						displayedPerson = pat.getPerson();
						// display the matching person
						displayTriagePerson(displayedPerson);
						// Enable triage as there is a patient being displayed.
						enableTriage();
						
					} else if (pat==null) { 
						// else if there were multiple people show a list
						populateMatchingPatientList();
					} 
					
					clearSearchFields();
				}
			}
			for (TreatmentFacility facility : treatmentFacilities) {

				// Check if there is a patient in the facility
				Patient pat = facility.getPatient();
				
				if (pat.getPerson().getNHSNum().toLowerCase().contains(search_NHS_No.getText()) ||
						pat.getPerson().getFirstName().toLowerCase().contains(search_First_Name.getText()) ||
						pat.getPerson().getLastName().toLowerCase().contains(search_Surname.getText()) ||
						pat.getPerson().getDOB().toLowerCase().contains(search_DOB.getText()) ||
						pat.getPerson().getPostcode().toLowerCase().contains(search_Postcode.getText()) ||
						pat.getPerson().getTelephone().toLowerCase().contains(search_Telephone_No.getText())) {
					if (pat!=null) {
						displayedPerson = pat.getPerson();
						// display the matching person
						displayTriagePerson(displayedPerson);
						// Enable triage as there is a patient being displayed.
						enableTriage();
						
					} else if (pat==null) { 
						// else if there were multiple people show a list
						populateMatchingPatientList();
					} 
					
					clearSearchFields();
				}
			}
		});
		
		// Sign refers to the "signature" button in the Treatment Room pane.
				// Sets the time stamp for recording new patient input or treatment
				// notes. 
		sign.setOnAction(e -> {
			String my_details = null;
			
			if (loggedInUser != null) {
				my_details = loggedInUser.getLastName()+", "+loggedInUser.getLastName();
			}
			tr_treatment_notes.appendText(Calendar.getInstance().getTime().toString()+" - "+my_details);
		});
		
		// Action to be performed when the user clicks the save notes 
		// button on the treatment room tab
		tr_button_save.setOnAction( e -> {
			// Get the index of the currently shown treatment room
			int selectedRoomIndex = select_tr.getSelectionModel().getSelectedIndex();
			
			// If the room is within bounds and the facility at the index is a treatment room
			if (selectedRoomIndex < treatmentFacilities.size() 
					&& treatmentFacilities.get(selectedRoomIndex) instanceof TreatmentRoom) {
				// Cast the selected facility to a treatment room
				TreatmentRoom room = (TreatmentRoom)treatmentFacilities.get(selectedRoomIndex);
				
				// If the room has a patient in it then get their details.
				if (room.getPatient() != null)
				{
					// Update the doctors notes of the patient in the current room
					try {
						client.getServer().updateDoctorsNotes(client.getClientID(), 
								room.getPatient().getPerson().getNHSNum(), tr_treatment_notes.getText());
					} catch (AuthenticationException e1) {
						Notifications.create().title("Save failed").text("You are not logged in.").showWarning();	
						log("Save failed: You are not logged in.");
					} catch (RemoteException e1) {
						Notifications.create().title("Update failed").text("Server communication error.").showWarning();	
						log("Save failed: Server communication error.");
					}
				}
			}
		});
		// Shuts down the window.
		// Point of Note: on this action, only the window will close. Our server 
		// will also require closure separately. 
		close_system.setOnAction( e -> {
			
			// If the RMI client exists, close it
			if (client!=null) {
				client.close();
			}
			
			// Exit the application
			Platform.exit();

		});

		// Settings is the configuration of preferences for the system.
		// Configuration items include resetting amount of treatment rooms
		// and setting a new URL for the server. 
		settings.setOnAction(e -> {
			Stage settings_stage = new Stage();
			AnchorPane root = new AnchorPane();
			Label plus_trs = new Label("Set Amount of Treatment Rooms");
			TextField set_no_trs = new TextField();
			set_no_trs.setPromptText("1-10");
			Button save_no_trs = new Button("Set");
			set_no_trs.setLayoutY(25.0);
			save_no_trs.setLayoutY(52.0);
			
			// Set up the server address label and textbox
			Label server_address_label = new Label("Set the server address");
			TextField set_server_address = new TextField();
			set_server_address.setText(prefs.get(ADDRESS_PREF_NAME, "localhost"));
			
			Button save_server_address = new Button("Set");
			server_address_label.setLayoutY(85.0);
			set_server_address.setLayoutY(105.0);
			save_server_address.setLayoutY(132.0);
			
			// Set up the server port label and textbox
			Label port_label = new Label("Set the server port");
			TextField set_port = new TextField();
			set_port.setText(String.valueOf(prefs.getInt(PORT_PREF_NAME, 1099)));
			
			Button save_port = new Button("Set");
			port_label.setLayoutY(165.0);
			set_port.setLayoutY(190.0);
			save_port.setLayoutY(215.0);
			
			// Set up the server port label and textbox
			Label local_host_name_label = new Label("Set the host name");
			TextField set_local_host_name = new TextField();
			set_local_host_name.setText(prefs.get(LOCAL_HOST_PREF_NAME, "localhost"));
			
			Button save_local_host_name = new Button("Set");
			local_host_name_label.setLayoutY(255.0);
			set_local_host_name.setLayoutY(275.0);
			save_local_host_name.setLayoutY(300.0);
			
			// On set address button pressed
			save_server_address.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent event) {
					// The server address
					String address = set_server_address.getText();
					// Whether or not
					if (!address.equals("")) {
						// Store in preferences
						prefs.put(ADDRESS_PREF_NAME, address);
						Notifications.create()
							.title("Server Address Updated")
							.text("The server address has been changed to " + address + ". "
									+ "\nLog in again to to connect using the new settings.")
							.showConfirm();
					} else {
						Notifications.create().title("Invalid Address").text("Address not changed.").showError();
					}
				}
			});
			
			// On set port button pressed
			save_port.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent event) {
					// The server port to parse
					int port;
				
					try {
						port = Integer.parseInt(set_port.getText());
					} catch (NumberFormatException ex) {
						port = -1;
					}
					
					// Check whether the port is in a reasonable range
					if (port >= 0 && port <= 65535) {
					
						// Store in preferences
						prefs.putInt(PORT_PREF_NAME, port);
						Notifications.create()
							.title("Server Port Updated").text("The server port has been changed to " + port + ". "
									+ "\nLog in again to to connect using the new settings.").showConfirm();
					} else {
						Notifications.create().title("Invalid Port").text("Port not changed.").showError();
					}
				}
			});

			// On set localhost address button pressed
			save_local_host_name.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent event) {
					// The server address
					String localHostName = set_local_host_name.getText();
					// Whether or not
					if (!localHostName.equals("")) {
						// Store in preferences
						prefs.put(LOCAL_HOST_PREF_NAME, localHostName);
						Notifications.create()
							.title("Server Address Updated")
							.text("The localhost has been changed to " + localHostName + ". "
									+ "\nLog in again to to connect using the new settings.")
							.showConfirm();
					} else {
						Notifications.create().title("Invalid localhost address").text("Localhost address not changed.").showError();
					}
				}
			});
			
			save_no_trs.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent event) {

					Integer i = (Integer.parseInt(set_no_trs.getText()
							.toString()));
					int a = i;
					if (i < 0) {
						Notifications.create()
								.title("Not Enough Treatment Rooms added")
								.text("Please set a number between 1-10")
								.darkStyle().showError();
					} else if (i > 10) {
						Notifications.create()
								.title("Too Many Treatment Rooms added")
								.text("Please set a number between 1-10")
								.darkStyle().showError();
					} else {
						
						// Send the number of treatment rooms to the server
						boolean success;
						try {
							// Send the updated number of rooms to the server
							success = client.getServer().setTreatmentRoomNumber(client.getClientID(), i);
						} catch (AuthenticationException e) {
							success = false;
						} catch (RemoteException e) {
							success = false;
						}
						
						// If the update fails show a message and return
						if (!success) {
							Notifications.create()
								.title("Change Request Rejected")
								.text("The number of treatment rooms could not be changed.")
								.darkStyle().showError();
							return;
						}
						
						// switch statement on the user entry to set a new
						// treatment room number
						switch (i) {
						// for a case, set the new treatment room count as the
						// same as the number entered in the text field
						case 1:
							setNewRoomCount(i);
							// start the method to change the treatment rooms
							newArrayList();
							break;
						default:
							// set the default
							setNewRoomCount(i);
							// start the method to change the treatment rooms
							newArrayList();
						}// end of switch
						
						// once clicking on submit, close the tab
						settings_stage.close();
					}
				}
			});

			// Anchor pane for settings view. Anchor pane is base for housing the
			// controls for the settings pane, which itself is a pop-over control.
			root.getChildren().addAll(plus_trs, set_no_trs, save_no_trs, port_label, 
					set_port, server_address_label, set_server_address,
					save_server_address, save_port, local_host_name_label, set_local_host_name,
					save_local_host_name);
			Scene s = new Scene(root, 200, 340);
			settings_stage.setScene(s);
			settings_stage.show();

		});

		// Login has multiple features. Primarily, the user will be able to login to
		// the system to use accordingly. The initial control pane also includes
		// the option to request forgotten username and password. This leads to 
		// an additional pop-over. The request pop-over requires the user to enter
		// an e-mail address to which the user will be able to retrieve his or her
		// username and password. 
		login.setOnAction(e -> {
			
			TextField tf1 = new TextField();
			PasswordField tf2 = new PasswordField();
			AnchorPane ap1 = new AnchorPane();
			ap1.setPrefSize(200,165);
			Button bt1 = new Button();
			Button bt2 = new Button();
			Button bt3 = new Button();
			Label l1 = new Label("Welcome to Triage!"); l1.setLayoutX(15); l1.setLayoutY(10);
			tf1.setPromptText("Username"); tf1.setLayoutX(10); tf1.setLayoutY(36);			
			tf2.setPromptText("Password"); tf2.setLayoutX(10); tf2.setLayoutY(62);			
			bt1.setText("Login"); bt1.setLayoutX(10); bt1.setLayoutY(90);			
			bt2.setText("Logoff"); bt2.setLayoutX(60); bt2.setLayoutY(90);			
			bt3.setText("Forgot Username or Password"); bt3.setLayoutX(10); bt3.setLayoutY(120);			
			
			bt1.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent event) {
					
					Staff s1 = new Staff();					
					boolean logMeIn = false;					
					String _user = tf1.getText();
					String _pass = tf2.getText();
					String db_pass = s1.setEmployeePassword(_pass);
					String staff_LastName;
					String staff_FirstName;
					List<Staff> matchingPeople1 = null;
						
					try {
						int port = prefs.getInt(PORT_PREF_NAME, 1099);
						String address = prefs.get(ADDRESS_PREF_NAME, "localhost");
						String localhost = prefs.get(LOCAL_HOST_PREF_NAME, "localhost");
						// Create the client
						client = new RMIClient(RevController.this, address, port, localhost, _user, db_pass);
						// Add a message to the log
						log("Logged in as " + _user);

						logMeIn = true;
					} catch (RemoteException | MalformedURLException | NotBoundException ex) {
						Notifications.create().title("Login failed").text("Server communication error.").position(Pos.CENTER).showError();	
						log("Login failed: Server communication error.");
					} catch (AuthenticationException ex) {
						Notifications.create().title("Login failed").text("Invalid username or password.").position(Pos.CENTER).showError();	
						log("Login failed: Invalid username or password.");
					} 
					
					if (logMeIn == true){
					login_pop.hide();			
					Notifications.create().title("Logged in").text("Begin Patient Triage!").showConfirm();		

						Staff loggedInUser = null;
						try {
							loggedInUser = client.getServer().searchStaffByUsername(client.getClientID(), _user);
						} catch (AuthenticationException | RemoteException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} 
					
						if (loggedInUser == null) {
							return;
						}
						
						staff_LastName = loggedInUser.getLastName();
						staff_FirstName = loggedInUser.getFirstName();

						Job jobs = Job.values()[4];
						switch (jobs) {
						case TRIAGE_NURSE:
							triage_nurse_on_duty.setText(staff_LastName + ","
									+ staff_FirstName);

							triagePane_triageNurse.setText(staff_LastName + ","
									+ staff_FirstName);
							break;
						case DOCTOR:
							;
							break;
						case ADMIN:
							admin.setText(staff_LastName + ","
									+ staff_FirstName);
							break;
						case NURSE:
							;
							break;
						default:
							;

						}

						login_pop.hide();

					}
				}
			});
			
			bt2.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent event) {
					
					client.close();
					resetTriage();
					
				}
			});
			
			bt3.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent event) {
					
					PopOver userNameRequest = new PopOver();
					Label emailLabel = new Label("Enter Email");
					TextField EmailRequest = new TextField();
					Button ConfirmRequest = new Button("Send Request");
					Button CancelRequest = new Button("Cancel");
					EmailRequest.setLayoutY(26);
					ConfirmRequest.setLayoutY(60);
					CancelRequest.setLayoutY(60); 
					CancelRequest.setLayoutX(90);

					AnchorPane forgot = new AnchorPane();
					forgot.setPrefSize(100,100);
					forgot.getChildren().addAll(EmailRequest, ConfirmRequest, emailLabel, CancelRequest);
					userNameRequest.setContentNode(forgot);					
					userNameRequest.show(bt3);
					
					ConfirmRequest.setOnAction(new EventHandler<ActionEvent>() {

						@Override
						public void handle(ActionEvent event) {							
							emailNewPassword(EmailRequest);
							userNameRequest.hide();
							login_pop.hide();	
							Notifications.create().title("Username & Password Request").text("Email sent").show();
						}});
					CancelRequest.setOnAction(new EventHandler<ActionEvent>() {

						@Override
						public void handle(ActionEvent event) {	
							login_pop.hide();
						}});
				}});
			
			ap1.setMinWidth(100);
			ap1.getChildren().add(l1);
			ap1.getChildren().add(tf1);
			ap1.getChildren().add(tf2);
			ap1.getChildren().add(bt1);
			ap1.getChildren().add(bt2);
			ap1.getChildren().add(bt3);
			ap1.setCursor(null);
			login_pop.setContentNode(ap1);
			login_pop.show(login);
		});

		re_assign.setOnAction(e -> {

			int selectedIndex = queue.getSelectionModel().getSelectedIndex();
			if (selectedIndex == -1) {
				Notifications.create().title("No patient selected").text("Select a patient in the queue to change their priority.").show();
			}
			
			PopOver reassign_priority = new PopOver();
			CheckBox set_e = new CheckBox("EMERGENCY");
			set_e.setStyle("-fx-base: salmon;");
			set_e.setLayoutY(26);
			set_e.setLayoutX(14);
			CheckBox set_u = new CheckBox("Urgent");
			set_u.setStyle("-fx-base: orange;");
			set_u.setLayoutY(52);
			set_u.setLayoutX(14);
			CheckBox set_semi_u = new CheckBox("Semi-Urgent");
			set_semi_u.setStyle("-fx-base: yellow;");
			set_semi_u.setLayoutY(78);
			set_semi_u.setLayoutX(14);
			CheckBox set_non_u = new CheckBox("Non-Urgent");
			set_non_u.setStyle("-fx-base: lightgreen;");
			set_non_u.setLayoutY(104);
			set_non_u.setLayoutX(14);
			Button confirm_new_priority = new Button("Confirm");
			confirm_new_priority.setLayoutY(132);
			confirm_new_priority.setLayoutX(14);
			Button cancel_new_priority = new Button("Cancel");
			cancel_new_priority.setLayoutY(132);
			cancel_new_priority.setLayoutX(74);
			Label select_new_urgency = new Label(
					"Please Select the appropriate Priority Level:");
			select_new_urgency.setLayoutX(14);
			AnchorPane re_assign_pane = new AnchorPane();
			re_assign_pane.setPrefSize(280, 190);
			re_assign_pane.getChildren().addAll(select_new_urgency, set_e,
					set_u, set_semi_u, set_non_u, confirm_new_priority, cancel_new_priority);
			reassign_priority.setContentNode(re_assign_pane);
			reassign_priority.show(re_assign);

			// Set a new Urgency level for a patient selected in the queue
			confirm_new_priority.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent event){
					
					int index = queue.getSelectionModel().getSelectedIndex();
					
					// If no patient has been selected
					if (index == -1 && selectedIndex == -1) {
						// show a message
						Notifications.create().title("No patient selected").text("Select a patient in the queue to change their priority.").show();
						return;
					} 

					// Use whichever index is not -1
					if (index == -1 && selectedIndex > -1){
						index = selectedIndex;
					}
					

					// If the index is valid, determine the new urgency based on the value
					// selected in the reassign priority pane
					if (index < queueList.size()) {
						Urgency reassigned_urgency = null;
						if (set_e.isSelected()) {
							reassigned_urgency = Urgency.EMERGENCY;
						} else if (set_u.isSelected()) {
							reassigned_urgency = Urgency.URGENT;
						} else if (set_semi_u.isSelected()) {
							reassigned_urgency = Urgency.SEMI_URGENT;
						} else if (set_non_u.isSelected()) {
							reassigned_urgency = Urgency.NON_URGENT;
						}
						
						Patient patient = queueList.get(index);
						
						try {
							client.getServer().reAssignTriage(client.getClientID(), index, reassigned_urgency);
							Notifications.create().title("Updated Successful").text("Priority of patient " + patient.getPatientName() + " has been changed to " + reassigned_urgency).showInformation();
						} catch (AuthenticationException | RemoteException e) {
							Notifications.create().title("Updated Unsuccessful").text("Priority of patient " + patient.getPatientName() + " has not been changed").showInformation();
						}
						
						outputTextArea.appendText(patient.getPatientName() +" is now !\n");
						reassign_priority.hide();
					}

				}
			});
			
			cancel_new_priority.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent event){
					reassign_priority.hide();
				}
			});

			set_e.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent event) {

					set_u.setSelected(false);
					set_semi_u.setSelected(false);
					set_non_u.setSelected(false);

				}
			});

			set_u.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent event) {
					set_e.setSelected(false);
					set_semi_u.setSelected(false);
					set_non_u.setSelected(false);
				}
			});

			set_semi_u.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent event) {
					set_e.setSelected(false);
					set_u.setSelected(false);
					set_non_u.setSelected(false);
				}
			});

			set_non_u.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent event) {

					set_e.setSelected(false);
					set_u.setSelected(false);
					set_semi_u.setSelected(false);
				}
			});

		});
			
		// Allows the user to view patients in the waiting room in detail. 
		// This action calls the class method "waitingRoomView". 
		Q_view.setOnAction(e -> {
			waitingRoomView();
			q_pop.show(Q_view);				
		});
		
		// Allows the user to view the treatment rooms in detail. 
		// This action calls the class method "treatmentRoomView". 
		TRooms_view.setOnAction(e -> {	
			treatmentRoomsView();
			tr_pop.show(TRooms_view);
		});

		// Search button for retrieving patient data from the NHS 
		// database. 
		search_database.setOnAction(e -> {

			matchingPeople = searchForPerson();	
			
			// If there was only a single user that matched the criteria, show them
			if (matchingPeople.size() == 1) {
				displayedPerson = matchingPeople.get(0);
				// display the matching person
				displayTriagePerson(displayedPerson);
				// Enable triage as there is a patient being displayed.
				enableTriage();
				
			} else if (matchingPeople.size() > 1) { 
				// else if there were multiple people show a list
				populateMatchingPatientList();
			} 
			
			clearSearchFields();
		});

		/**
		 * Toggle Button tb1 - tb6. Responsible for eliciting primary 
		 * triage and committing new patient as emergency. 
		 */

		// Refers to airway blocked.
		tb1.setOnAction(e -> {
			outputTextArea.appendText(textfield_Surname.getText() + ", "
					+ textfield_First_Name.getText()
					+ " has a blocked airway!\n");
			tb1.setText("!");
			tb1.setStyle("-fx-base: salmon;");
			emergency.setDisable(false);
			emergency.setStyle("-fx-base: red;");
		});

		// Patient not breathing.
		tb2.setOnAction(e -> {
			outputTextArea.appendText(textfield_Surname.getText() + ", "
					+ textfield_First_Name.getText()
					+ " has difficulty breathing!\n");
			tb2.setText("!");
			tb2.setStyle("-fx-base: salmon;");
			emergency.setDisable(false);
			emergency.setStyle("-fx-base: red;");
		});

		// Spinal trauma suspected.
		tb3.setOnAction(e -> {
			outputTextArea.appendText(textfield_Surname.getText() + ", "
					+ textfield_First_Name.getText()
					+ " has suspected cervia spine trauma!\n");
			tb3.setText("!");
			tb3.setStyle("-fx-base: salmon;");
			emergency.setDisable(false);
			emergency.setStyle("-fx-base: red;");
		});

		// Patient has circulation difficulties. 
		tb4.setOnAction(e -> {
			outputTextArea.appendText(textfield_Surname.getText() + ", "
					+ textfield_First_Name.getText()
					+ " has circulation difficulties!\n");
			tb4.setText("!");
			tb4.setStyle("-fx-base: salmon;");
			emergency.setDisable(false);
			emergency.setStyle("-fx-base: red;");
		});

		// Patient has disability or is physically incapable.
		tb5.setOnAction(e -> {
			outputTextArea.appendText(textfield_Surname.getText() + ", "
					+ textfield_First_Name.getText() + " is incapacitated!\n");
			tb5.setText("!");
			tb5.setStyle("-fx-base: salmon;");
			emergency.setDisable(false);
			emergency.setStyle("-fx-base: red;");
		});

		// Patient exposed to extreme conditions. 
		tb6.setOnAction(e -> {
			outputTextArea.appendText(textfield_Surname.getText() + ", "
					+ textfield_First_Name.getText()
					+ " has been exposed to extreme conditions!\n");
			tb6.setText("!");
			tb6.setStyle("-fx-base: salmon;");
			emergency.setDisable(false);
			emergency.setStyle("-fx-base: red;");
		});

		// Refers to our "add" button.
		// Sends person to the back-end as a patient object. 
		// Sets patient urgency "emergency". This patient will
		// only be visible in the treatment room view.
		emergency.setOnAction(e -> {
			
			// Create an emergency patient based on the displayed person
			Patient emergency_patient = new Patient(displayedPerson, Urgency.EMERGENCY,"");
			
			outputTextArea.appendText("EMERGENCY!\n"+textfield_Surname.getText()+", "+textfield_First_Name.getText()+" sent to the Treatment room!\n");
								
			
			try {
				// Add the emergency patient to the back end
				boolean added = client.getServer().addPatient(client.getClientID(), emergency_patient);
				if (added) {
					Notifications.create().title("Patient Added").text("Patient was successfully added to the queue.").showConfirm();
				} else {
					Notifications.create().title("Patient Not Added").text("Patient could not be added to the queue.").showConfirm();
				}
			} catch (Exception e1) {
				Notifications.create().title("Communication error").text("Patient could not be added to the queue.").showConfirm();
			}
			clearSearchFields();
			clearTriageTextFields();
			
			resetTriage();
		});

		// Refers to our "add" button.
		// Sends person to the back-end as a patient object. 
		// Sets patient urgency "urgent". 
		// Back-end runs a check if there is no space in the treatment rooms,
		// patient added to waiting list queue. 
		urg.setOnAction(e -> {

			// Create an urgent patient based on the displayed person
			Patient urgent_patient = new Patient(displayedPerson, Urgency.URGENT, "");
			outputTextArea.appendText("URGENT!\n"+textfield_Surname.getText()+", "+textfield_First_Name.getText()+" has been added to the Queue!\n");
			
			try {
				// Add the urgent patient to the back end
				boolean added = client.getServer().addPatient(client.getClientID(), urgent_patient);
				if (added) {
					Notifications.create().title("Patient Added").text("Patient was successfully added to the queue.").showConfirm();
				} else {
					Notifications.create().title("Patient Not Added").text("Patient could not be added to the queue.").showConfirm();
				}
			} catch (Exception e1) {
				Notifications.create().title("Communication error").text("Patient could not be added to the queue.").showConfirm();
			}

			clearSearchFields();
			clearTriageTextFields();
			
			resetTriage();
		});

		// Refers to our "add" button.
		// Sends person to the back-end as a patient object. 
		// Sets patient urgency "semi-urgent". 
		// Back-end runs a check if there is no space in the treatment rooms,
		// patient added to waiting list queue. 
		semi_urg.setOnAction(e -> {

			// Create a semi-urgent patient based on the displayed person
			Patient semi_urgent_patient = new Patient(displayedPerson, Urgency.SEMI_URGENT, "");

			outputTextArea.appendText("Semi-Urgent:\n"+textfield_Surname.getText()+", "+textfield_First_Name.getText()+" has been added to the Queue!\n");
			
			try {
				// Add the semi-urgent patient to the back end
				boolean added = client.getServer().addPatient(client.getClientID(), semi_urgent_patient);
				if (added) {
					Notifications.create().title("Patient Added").text("Patient was successfully added to the queue.").showConfirm();
				} else {
					Notifications.create().title("Patient Not Added").text("Patient could not be added to the queue.").showConfirm();
				}
			} catch (Exception e1) {
				Notifications.create().title("Communication error").text("Patient could not be added to the queue.").showConfirm();
			}
			
			clearSearchFields();
			clearTriageTextFields();
			
			resetTriage();
		});

		// Refers to our "add" button.
		// Sends person to the back-end as a patient object. 
		// Sets patient urgency "non-urgent". 
		// Back-end runs a check if there is no space in the treatment rooms,
		// patient added to waiting list queue. 
		non_urg.setOnAction(e -> {
			// Create a non-urgent patient based on the displayed person
			Patient non_urgent_patient = new Patient(displayedPerson, Urgency.NON_URGENT, "");

			outputTextArea.appendText("Non-Urgent:\n"+textfield_Surname.getText()+", "+textfield_First_Name.getText()+" has been added to the Queue!\n");
			
			try {

				// Add the non-urgent patient to the back end
				boolean added = client.getServer().addPatient(client.getClientID(), non_urgent_patient);
				if (added) {
					Notifications.create().title("Patient Added").text("Patient was successfully added to the queue.").showConfirm();
				} else {
					Notifications.create().title("Patient Not Added").text("Patient could not be added to the queue.").showConfirm();
				}
			} catch (Exception e1) {
				Notifications.create().title("Communication error").text("Patient could not be added to the queue.").showConfirm();
			}
			
			clearSearchFields();
			clearTriageTextFields();
			
			resetTriage();
		});
		
		// Check-box in secondary triage pane.
		// If selected, indicates that the patient is mobile and
		// independent. 
		walk.setOnAction(e -> {
			walk_no.setSelected(false);
			if (breathing_yes.getSelectionModel().getSelectedIndex() == 0) {
				non_urg.setDisable(false);
				non_urg.setStyle("-fx-base: green;");
			} else if (breathing_yes.getSelectionModel().getSelectedIndex() == 1) {
				urg.setDisable(false);
				urg.setStyle("-fx-base: orange;");
			}
		});

		// Check-box in secondary triage pane.
		// If selected, indicates that the patient is immobile and requires
		// semi-urgent or urgent attention.
		walk_no.setOnAction(e -> {
			walk.setSelected(false);
			if (breathing_yes.getSelectionModel().getSelectedIndex() == 0) {
				semi_urg.setDisable(false);
				semi_urg.setStyle("-fx-base: yellow;");
			} else if (breathing_yes.getSelectionModel().getSelectedIndex() == 1) {
				emergency.setDisable(false);
				emergency.setStyle("-fx-base: red;");

			}
		});

		// Extend is the button for extending the treatment of a patient
		// in a treatment room. 
		// Calls the back-end to reset the timer and halt the arrival of
		// a new patient in the room. 
		// Upon action, user is required to enter reason for extend request.
		extend.setOnAction(e -> {
			
			AnchorPane ap_ext = new AnchorPane();
			Label extension = new Label("Confirm Extension Request");
			ComboBox extension_request = new ComboBox(); extension_request.setLayoutY(26);
			ObservableList extending_time = FXCollections.observableArrayList();
			extending_time.addAll(ExtensionReason.values());
			extension_request.setItems(extending_time);
			Button extension_confirm = new Button("Confirm"); extension_confirm.setLayoutY(52);

			extension_confirm.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent event) {
					// Boolean to hold whether the update was successful or not
					boolean updateSuccessful = false;
					try {
						// Get the Index of the selected reason so it can be passed to the server
						int reasonIndex =extension_request.getSelectionModel().getSelectedIndex();
						// Request the update via the server and get a result as a boolean
						client.getServer().extendTreatmentTime(client.getClientID(), treatmentFacilities.indexOf(getSelectedTreatmentRoom()), ExtensionReason.values()[reasonIndex]);
						Notifications.create().title("Extension Granted").text("Extended for 5 minutes").darkStyle().showConfirm();
						extension_pop.hide();
					} catch (Exception e1) {
						Notifications.create().title("Extension Failed").text("Server connection issue.").darkStyle().showWarning();
						extension_pop.hide();
					}

				}
			});

			ap_ext.getChildren().addAll(extension, extension_request,
					extension_confirm);
			extension_pop.setContentNode(ap_ext);
			extension_pop.show(extend);
			
			});
		
		
			// Set up the event when the user clicks the treatment room selection
			// combobox on the treatment room tab
			select_tr.getSelectionModel().selectedItemProperty().addListener(e -> {
				// Display the treatment room based on the selected room in select_tr
				displaySelectedTreatmentRoom();

			});

		// Set up the event when the user clicks the treatment room selection
		// combobox on the treatment room tab
		select_tr.setOnAction(e -> {

			// Get the selected room index
				int selectedRoomIndex = select_tr.getSelectionModel()
						.getSelectedIndex();

				// If the room is within bounds and the facility at the index is
				// a treatment room
				if (selectedRoomIndex < treatmentFacilities.size()
						&& treatmentFacilities.get(selectedRoomIndex) instanceof TreatmentRoom) {
					// Cast the selected facility to a treatment room
					TreatmentRoom room = (TreatmentRoom) treatmentFacilities
							.get(selectedRoomIndex);

					// Init the detail strings to empty strings.
					String urgency = "", doctorsNotes = "", incidentDetails = "";

					// If the room has a patient in it then get their details.
					if (room.getPatient() != null) {
						urgency = room.getPatient().getUrgency().toString();
						doctorsNotes = room.getPatient().getPerson()
								.getDoctorsNotes();
						incidentDetails = "Incident Details";
					}

					// Display the details in the text fields, if there was no
					// patient
					// they will be empty.
					tr_patient_urgency.setText(urgency);
					tr_treatment_notes.setText(doctorsNotes);
					tr_incident_details.setText(incidentDetails);
				}
			});
		
		//	TODO check that is sets values
		BTstat.setOnAction(e->{pasStats();});
	}

	/**
	 * Display the details of a treatment room based on the selected room in select_tr
	 */
	public void displaySelectedTreatmentRoom() {

		TreatmentRoom room = getSelectedTreatmentRoom();
		// If there is a room selected and it contains a patient
		if (room != null && room.getPatient() != null) {
			// Enable the button
			tr_button_save.setDisable(false);
			extend.setDisable(false);
			// Display the room
			tr_patient_urgency.setText(room.getPatient().getUrgency().toString());
			tr_incident_details.setText("Incident Details");
			displayTreatmentRoomPerson(room.getPatient().getPerson());
			
			treatmentRoomEggTimer(room);
		} else { // else clear the text boxes and disable the buttons
			// Disable the save notes button
			tr_button_save.setDisable(true);
			extend.setDisable(true);
			// Clear out the text fields
			clearTreatmentRoomTextFields();
		}
		// ensure cancel button is disabled at first
		cancel_extension.setDisable(true);
		

	}
	
	/**
	 * Gets the current selected treatment room on the treatment room tab
	 */
	public TreatmentRoom getSelectedTreatmentRoom(){
		
		// Get the selected room index
		int selectedRoomIndex = select_tr.getSelectionModel().getSelectedIndex();
		
		// If the selected index is within bounds and the facility at that index is a treatment room
		if (selectedRoomIndex < treatmentFacilities.size() 
				&& treatmentFacilities.size() > 0
				&& selectedRoomIndex > -1
				&& treatmentFacilities.get(selectedRoomIndex) instanceof TreatmentRoom) {
			// return the treatment room at the selected index
			return (TreatmentRoom)treatmentFacilities.get(selectedRoomIndex);
		} else {
			// else return null
			return null;
		}
	}
	
	/**
	 * Display a person in the patient triage tab.
	 *
	 * @param displayedPerson
	 */
	public void displayTriagePerson(Person displayedPerson) {

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
	
	/**
	 * Display a person in the patient treatment room tab.
	 *
	 * @param displayedPerson
	 */
	public void displayTreatmentRoomPerson(Person displayedPerson) {

		tr_textfield_First_Name.setText(displayedPerson.getFirstName());
		tr_textfield_Surname.setText(displayedPerson.getLastName());
		tr_textfield_NHS_Num.setText(displayedPerson.getNHSNum());
		tr_textfield_Title.setText(displayedPerson.getTitle());
		tr_textfield_DOB.setText(displayedPerson.getDOB());
		tr_textfield_Address.setText(displayedPerson.getAddress());
		tr_textfield_Blood_Group.setText(displayedPerson.getBloodGroup());
		tr_textfield_Postcode.setText(displayedPerson.getPostcode());
		tr_textfield_Telephone.setText(displayedPerson.getTelephone());
		tr_treatment_notes.setText(displayedPerson.getDoctorsNotes());
		
		// If the returned allergy is "null" set the allergy
		// box to display "None".
		if (displayedPerson.getAllergies().equalsIgnoreCase("null")) {
			allergy.setValue(allergic[0]);
		} else {
			// Else set the allergy box value to the returned alergy
			allergy.setValue(displayedPerson.getAllergies());
		}
	}

	/**
	 * Treatment room view called on action from the tr-view button. This will
	 * display the patient name; the urgency; and the total static waiting time 
	 * of patients. Details represented in table view format. 
	 */
	private void treatmentRoomsView() {
				
		AnchorPane ap3 = new AnchorPane();	
		Label treatment_table = new Label("Current Patients in Treatment"); treatment_table.setLayoutX(10);
		Button close_view1 = new Button("x"); close_view1.setStyle("-fx-base: red;"); close_view1.setLayoutX(224);
		
		for (TreatmentFacility facility : treatmentFacilities) {			
			// Check if there is a patient in the facility
			Patient patient = facility.getPatient();
			emergency_room.add(patient);
		}
		
		TableView<Patient>  treatmentRoomTable = new TableView<Patient>(); treatmentRoomTable.setLayoutY(26);
		 
		TableColumn patName = new TableColumn("Patient");			
		patName.setCellValueFactory(new PropertyValueFactory<Patient, String>("patientName"));
		
		TableColumn Urgency = new TableColumn("Urgency");			
		Urgency.setCellValueFactory(new PropertyValueFactory<Object, String>("urgency"));
		
		TableColumn WaitingTime = new TableColumn("Wait Time");			
		WaitingTime.setCellValueFactory(new PropertyValueFactory<Object, String>("waitTime"));
		
		treatmentRoomTable.getColumns().addAll(patName, Urgency, WaitingTime);
		treatmentRoomTable.setItems(emergency_room);
		ap3.getChildren().addAll(treatment_table, close_view1, treatmentRoomTable);
		tr_pop.setContentNode(ap3);		
		close_view1.setOnAction(e -> {tr_pop.hide();emergency_room.clear();});
	}

	/**
	 * Waiting room view called on action from the qView button. This will
	 * display the patient name; the urgency; and the current waiting time 
	 * per patient. Details represented in table view format. 
	 */
	private void waitingRoomView() {
		
		AnchorPane ap2 = new AnchorPane();	
		Label waitroom_table = new Label("Current Patients in Waiting"); waitroom_table.setLayoutX(10);
		Button close_view2 = new Button("x"); close_view2.setStyle("-fx-base: red;"); close_view2.setLayoutX(224);		
		waiting_room.clear();
		// Loop through each of the patients in queueList
				for (Patient patient : queueList) {
					waiting_room.add(patient);					
				}
		
		TableView<Patient>  waitingRoomTable = new TableView<Patient>(); waitingRoomTable.setLayoutY(26);
		
		TableColumn patName1 = new TableColumn("Patient");			
		patName1.setCellValueFactory(new PropertyValueFactory<Object, String>("patientName"));
		
		TableColumn Urgency1 = new TableColumn("Urgency");			
		Urgency1.setCellValueFactory(new PropertyValueFactory<Object, String>("urgency"));
		
		TableColumn WaitingTime1 = new TableColumn("Wait Time");			
		WaitingTime1.setCellValueFactory(new PropertyValueFactory<Object, String>("waitTime"));
			
		waitingRoomTable.getColumns().addAll(patName1, Urgency1, WaitingTime1);
		waitingRoomTable.setItems(waiting_room);
		ap2.getChildren().addAll(waitroom_table,close_view2, waitingRoomTable);
		q_pop.setContentNode(ap2);
		close_view2.setOnAction(e -> {q_pop.hide();waiting_room.clear();});
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
	private void clearTriageTextFields() {

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
	 * clear patient details after priority set
	 */
	private void clearTreatmentRoomTextFields() {

		tr_textfield_First_Name.clear();
		tr_textfield_Surname.clear();
		tr_textfield_NHS_Num.clear();
		tr_textfield_Title.clear();
		tr_textfield_DOB.clear();
		tr_textfield_Address.clear();
		tr_textfield_Blood_Group.clear();
		tr_textfield_Postcode.clear();
		tr_textfield_Telephone.clear();
		tr_allergy.setValue(null);
		tr_treatment_notes.clear();
		tr_patient_urgency.clear();
		tr_incident_details.clear();
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

	/**
	 * @Override method - update implemented client-callback interface.
	 * Update reaches into the back-end and fetches data from the patient
	 * and treatment facility objects. 
	 * Update data passed to observable lists for instantiation by 
	 * listview and tableview controls. 
	 */
	@Override
	public void udpate(List<Patient> queue, List<TreatmentFacility> treatmentFacilities, List<Staff> onCallStaff, List<Staff> onCallDoctors) {
		// Store the passed in queue and treatment facilities
		this.queueList = queue;
		this.treatmentFacilities = treatmentFacilities;
		this.onCallStaff = onCallStaff;
		this.onCallDoctors = onCallDoctors;
		// Call run later to run updates to the UI on the JavaFX thread
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				// Update the queue on the UI with the updated data
				updateQueue();
				// Update the treatment room list on the UI with the updated
				// data
				updateTreatmentRooms();
			}
		});
	}

	/**
	 * Appends text from action to user-interface log ticker. 
	 */
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

		triage_tab.setTooltip(new Tooltip(
				"Search Patient Database and commence triage"));
		tr_tab.setTooltip(new Tooltip("Display Treatment Room details"));
		stats_tab
				.setTooltip(new Tooltip("Display Management System Statistics"));
		login.setTooltip(new Tooltip("Log in here"));
		re_assign.setTooltip(new Tooltip(
				"Select a Patient in the Queue to Upgrade to EMERGENCY!"));
		Q_view.setTooltip(new Tooltip("View Queued Patient Details"));
		TRooms_view.setTooltip(new Tooltip(
				"View Treatment Room Patient Details"));
		search_database.setTooltip(new Tooltip("Search NHS DBMS"));
		tb1.setTooltip(new Tooltip("Patient has BLOCKED AIRWAY!"));
		tb2.setTooltip(new Tooltip("Patient is NOT BREATHING!"));
		tb3.setTooltip(new Tooltip("Patient has suspected SPINAL TRAUMA!"));
		tb4.setTooltip(new Tooltip("Patient is LOSING BLOOD!"));
		tb5.setTooltip(new Tooltip("Patient is INCAPACITATED!"));
		tb6.setTooltip(new Tooltip(
				"Patient has been EXPOSED TO EXTREME CONDITIONS!"));
		emergency.setTooltip(new Tooltip(
				"Send Patiently directly to Treatment Room"));
		urg.setTooltip(new Tooltip(
				"Send Patient to Waiting Room with URGENT priority"));
		semi_urg.setTooltip(new Tooltip(
				"Send Patient to Waiting Room with SEMI-URGENT priority"));
		non_urg.setTooltip(new Tooltip("Send Patient to Waiting Room"));
		extend.setTooltip(new Tooltip("Extend Patient Treatment time"));
		search_DOB.setTooltip(new Tooltip(
				"Enter D.o.B. in the Format DD/MM/YYYY"));
		search_First_Name.setTooltip(new Tooltip("Search by Name"));
		search_NHS_No.setTooltip(new Tooltip("Search by Patient NHS Number"));
		search_Postcode.setTooltip(new Tooltip("Enter Postcode"));
		search_Surname.setTooltip(new Tooltip("Search by Name"));
		search_Telephone_No.setTooltip(new Tooltip("Enter Telephone Number"));
		respiratory_rate.setTooltip(new Tooltip("Breaths taken per minute"));
		pulse_rate.setTooltip(new Tooltip("Heart beats taken per minute"));
		breathing_yes.setTooltip(new Tooltip("Select option"));
		walk.setTooltip(new Tooltip("The patient is mobile and stable"));
		walk_no.setTooltip(new Tooltip(
				"The patient is immobile and requires assistence"));
		conditions.setTooltip(new Tooltip("Select option"));
		medication.setTooltip(new Tooltip("Select option"));
		server_check.setTooltip(new Tooltip("Server Status"));
	}

	/**
	 * Method called by RMIClient when the remote server reports that the queue
	 * is full
	 */
	@Override
	public void alertQueueFull() throws RemoteException {
		Notifications.create().title("Queue full").text("").showInformation();	
		log("Queue full.");
	}

	/**
	 * Called by RMI client when the server status changes (whether its
	 * accessible or not)
	 */
	public void serverStatusChanged(ConnectionState accessible) {

		// Call run later to run updates to the UI on the JavaFX thread
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				switch (accessible) {
				case CONNECTED:
					outputTextArea.appendText("Server accessible\n");
					server_check.setText("Server Connected");
					server_check.setStyle("-fx-base: green;");
					break;
				case CONNECTION_ERROR:
					outputTextArea.appendText("Server inaccessible\n");
					server_check.setText("Connection Error");
					server_check.setStyle("-fx-base: red;");
					server_check.setSelected(false);
					server_check
							.setTooltip(new Tooltip(
									"Please check connection to Server and re-connect"));
					break;
				case NOT_CONNECTED:
					outputTextArea.appendText("Not connected to server.\n");
					server_check.setText("Not Connected");
					server_check.setStyle("-fx-base: blue;");
					server_check.setSelected(false);
					server_check
							.setTooltip(new Tooltip(
									"Please check connection to Server and re-connect"));
					break;
				case CONNECTING:
					outputTextArea.appendText("Connecting to server...\n");
					server_check.setText("Connecting...");
					server_check.setStyle("-fx-base: yellow;");
					break;
				}
			}
		});
	}

	/**
	 * Calls server method to find people
	 * 
	 * @return A list of people who match the details input into the search
	 *         fields
	 */
	public List<Person> searchForPerson() {
		// Init a list to hold the results
		List<Person> foundPeople = null;

		try {
			// Call the searchPersonByDetails method on the server to get the
			// details from the database
			foundPeople = client.getServer().searchPersonByDetails(
					client.getClientID(), search_NHS_No.getText(),
					search_First_Name.getText(), search_Surname.getText(),
					search_DOB.getText(), search_Postcode.getText(),
					search_Telephone_No.getText());
		} catch (RemoteException ex) {
			System.err.println("Server communication error.");
			ex.printStackTrace();
		} catch (AuthenticationException ex) {
			System.err.println("not authenticated to the server, please login");
			ex.printStackTrace();
		}

		// return the results.
		return foundPeople;
	}

	/**
	 * Method to send an email to the user that has forgotten his username or password.
	 * A reset username and password would be sent by this method.
	 */
	public static void emailNewPassword(TextField emailRequest) {
		// Recipient's email ID needs to be mentioned.
		String to = emailRequest.getText().toString();

		// Sender's email ID needs to be mentioned
		String from = "pashospital@gmail.com";

		// Get system properties
		Properties properties = System.getProperties();

		properties.put("mail.smtp.starttls.enable", "true");
		properties.put("mail.smtp.host", "smtp.gmail.com");

		properties.put("mail.smtp.port", "587");
		properties.put("mail.smtp.auth", "true");
		Authenticator authenticator = new Authenticator() {
			public PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("pashospital@gmail.com",
						"hospitalsystem");// userid and password for "from"
											// email
											// address
			}
		};

		Session session = Session.getDefaultInstance(properties, authenticator);
		try {
			// Create a default MimeMessage object.
			MimeMessage message = new MimeMessage(session);

			// Set From
			message.setFrom(new InternetAddress(from));

			// Set To
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(
					to));

			// Set Subject: header field
			message.setSubject("PAS username and password retrival");

			// Now set the actual message
			message.setText("Your new Username is xxxxxx. Your new Password is xxxxxxxx.");

			// Send message
			Transport.send(message);
			System.out.println("Sent message successfully....");
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Alert the user that they are logged off.
	 */
	public void alertLoggedOff() {
		// Call run later to run updates to the UI on the JavaFX thread
		Platform.runLater(new Runnable() {

			@Override
			public void run() {

				// Show a notification informing the user they have been logged off.
				Notifications.create().title("You have been logged off").text("Please reconnect.").showConfirm();	
				// Leave a message in the log
				log("You have been logged off - please reconnect.");
				// Show the login popup
				login_pop.show(login);

			}
		});
	}


	/**
	 * getter for the original room count
	 * 
	 * @return
	 */
	public Integer getRoomCount() {
		return roomCount;
	}

	/**
	 * setter for the original room count
	 * 
	 * @param roomCount
	 */
	public void setRoomCount(Integer roomCount) {
		this.roomCount = roomCount;
	}

	
	/**
	 * getter for the new room count
	 * 
	 * @return
	 */
	public Integer getNewRoomCount() {
		return newRoomCount;
	}

	/**
	 * setter for the new room count
	 * 
	 * @param newRoomCount
	 */
	public void setNewRoomCount(Integer newRoomCount) {
		this.newRoomCount = newRoomCount;
	}
	
	/**
	 * A method to be passed as a field in a new patient instantiation. 
	 * This method provides a description of the incident details for which
	 * the patient is admitted to the PAS. 
	 * @return String
	 */
	public String incidentDomain() {
		
		String breathing = null;
		String walking= null;
		String shallow_breathing= null;
		String gasping= null;
		String low_pulse= null;
		String rapid_pulse= null;
		String any_medical_conditions= null;
		String any_prescribed_medicines= null;
		String output_incident_report = null;
		
		if (breathing_yes.getSelectionModel().getSelectedIndex() == 1) {
			breathing = "Resuscitation Required";
		}
		if (walk_no.isSelected()) {
			walking = "Patient is incapacitated and immobile";
		}
		if (respiratory_rate.getValue() > 50.5) {
			shallow_breathing = "Patient has shallow breathing";
		}
		if (respiratory_rate.getValue() < 0.5) {
			gasping = "Patient has trouble breathing";
		}
		if (pulse_rate.getValue() > 50.5) {
			rapid_pulse = "Pulse High";
		}
		if (pulse_rate.getValue() < 0.5) {
			low_pulse = "Pulse Low";
		}
		if (conditions.getSelectionModel().getSelectedIndex() > 0) {
			any_medical_conditions = "Patient is receiving treatment for medical conditions";
		}
		if (medication.getSelectionModel().getSelectedIndex() > 0) {
			any_prescribed_medicines = "Patient is taking prescribed medication";
		}
		
		String[] incident_report = {breathing,walking,shallow_breathing,gasping,rapid_pulse,low_pulse,any_medical_conditions,any_medical_conditions};
		for (String string : incident_report) {
			if (string!=null){
				output_incident_report = output_incident_report+string+"; ";
			}
		}
		return output_incident_report;
		
	}

	/** Show the next patient to be added to a room */
	@Override
	public void notifyNextPatientToRoom(String message) throws RemoteException {
		// Call run later to run updates to the UI on the JavaFX thread
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
					Notifications.create().title("Next Patient to Treatment Room ").text(message).showInformation();
			}	
		});
	}
	
	/**
	 * Gets inputs from the server-side and sets them to text-fields in the 
	 * Stat tab on client side. 
	 */
	public void pasStats() {
 
		try{
		//set urgency data to array
		int[] urgencies = client.getServer().getUrgencies();
		
		//
		current_in_queue.setText(""+client.getServer().getCurrentNumberInQueue());
		daily_emergencies.setText(""+urgencies[0]);
		daily_urgent.setText(""+urgencies[1]);
		daily_semi_urgent.setText(""+urgencies[2]);
		daily_non_urgent.setText(""+urgencies[3]);
		daily_tr_extended.setText(""+client.getServer().getNumberOfExtensions());
		daily_avg_wait.setText(""+client.getServer().getAvTimeInQue());
		current_total.setText(""+client.getServer().getPatientsRejected());
		av_treatment_time.setText(""+ client.getServer().getAvTreatmentTime());
		av_visit_time.setText(""+ client.getServer().getAvVisitTime());
		patients_rejected.setText(""+ client.getServer().getPatientsRejected());
		max_wait_time_exceeded.setText("" + client.getServer().NumberOfPatientsOverWaitTime());
		
		//data for pieChart as a % of the total
		int count=0;
		for(int totals:urgencies){
			count+=totals;
		}
		int Emergency = (100/count)*urgencies[0];
		int Urgent = (100/count)*urgencies[1];
		int Semi_Urgent = (100/count)*urgencies[2];
		int Non_Urgent = (100/count)*urgencies[3];
		
		//if the server is not available the catch sets the text fields to unavailable
		} catch (Exception ex){
			
			current_total.setText("unavailable");
			current_in_queue.setText("unavailable");
			av_visit_time.setText("unavailable");
			av_treatment_time.setText("unavailable");
			daily_emergencies.setText("unavailable");
			daily_urgent.setText("unavailable");
			daily_semi_urgent.setText("unavailable");
			daily_non_urgent.setText("unavailable");
			daily_tr_extended.setText("unavailable");
			daily_avg_wait.setText("unavailable");
			patients_rejected.setText("unavailable");
			max_wait_time_exceeded.setText("unavailable");
			daily_avg_semi_urgent.setText("unavailable");
			daily_avg_non_urgent.setText("unavailable");
		}
	}
	
	
	/**
	 * controller for generating pie chart
	 * @author Adrian
	 *
	 */
	/*public class GraphScreenController implements Initializable {

	    @FXML
	    PieChart chart;

	    int Emergency,Urgent,Semi_Urgent,Non_Urgent;
	    
	public GraphScreenController(int Emergency,int Urgent,int Semi_Urgent, int Non_Urgent){
		this.Emergency=Emergency;
		this.Urgent=Urgent;
		this.Semi_Urgent=Semi_Urgent;
		this.Non_Urgent=Non_Urgent;
	}
	@Override
	public void initialize(URL url, ResourceBundle rb) {
	    // TODO
	 ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
	            new PieChart.Data("Emergency", Emergency),
	            new PieChart.Data("Urgent", Urgent),
	            new PieChart.Data("Semi_Urgent", Semi_Urgent),
	            new PieChart.Data("Non_Urgent", Non_Urgent));

	 chart.setData(pieChartData);

	}
	*/
}
