package uk.ac.qub.exjavaganza.hqbert.server.v01;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.prefs.Preferences;

import javax.rmi.ssl.SslRMIClientSocketFactory;
import javax.rmi.ssl.SslRMIServerSocketFactory;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
/**
 * Class to handle interaction between the  queue, the treatment rooms,
 * on-call team and the front end. Also manages sending of alerts and messages
 * @author james thompson
 *
 */
public enum Supervisor {

	INSTANCE;

	/**Upper limit of people allowed in the queue at one time before new admissions are sent away*/
	public final int MAX_QUEUE_SIZE = 10;
	/**The longest waiting time considered acceptable by the trust*/
	public final int MAX_WAIT_TIME = 20;
	/**The waiting time threshold at which a patient will be given queue priority regardless of triage category*/
	public final int PRIORITY_WAIT_TIME = 15;
	/**If more than this number of patients are waiting more than the acceptable time, the manager is alerted*/
	public final int MAX_OVERDUE_PATIENTS = 1;
	
	/**How many minutes between automatic queue updates*/
	public final int BASE_UPDATE_INTERVAL = 1;
	/**How long a room is typically expected to be occupied to treat a patient*/
	public final int BASE_ROOM_OCCUPANCY_TIME = 10;
	/**Length of time a room can be extended by in one go to continue treating a patient*/
	public final int ROOM_OCCUPANCY_EXTENSION_TIME = 5;
	/** How long an onCall team is expected to spend treating a patient*/
	public final int ONCALL_ENGAGEMENT_TIME = 15;
	
	/**How many treatment rooms are currently available in the hospital*/
	public int TREATMENT_ROOMS_COUNT = 3;
	/**Number of doctors in the onCall team*/
	public final int ONCALL_TEAM_DOCTORS = 2;
	/**Number of nursees in an on call team*/
	public final int ONCALL_TEAM_NURSES = 3;
	/**Debug flag : whether emails and sms messages should actually be sent*/
	public final boolean ALERTS_ACTIVE = false;

	/**Multiplier to allow time in the system to be sped up / slowed down for testing / demoing*/
	public final float TIME_MULTI = 60;
	/**saved preferences for editable values that should persist between launches*/
	private Preferences prefs;
	
	/**reason for summoning the oncall - used for metrics*/
	private enum ON_CALL_REASON {QUEUE_FULL,EXTRA_EMERGENCY};
	
	/**port on which the server listens for connections*/
	private int serverPort = 1099;

	/**Queue management object*/
	private HQueue hQueue;
	/**Time management object*/
	private Clock clock;
	/**whether the server application should exit*/
	private boolean exit;
	
	/**currently available treatment facilities (including on-call team if active*/
	private ArrayList<TreatmentFacility> treatmentFacilities;
	
	/**All staff available*/
	private ArrayList<Staff> availableStaff;
	/**All staff currently logged into the system*/
	private ArrayList<Staff> loggedInStaff;
	/**on call team made up of staff who are listed as on-call at given time*/
	private OnCallTeam onCallTeam;
	/**the staff available for oncall at a given time*/
	private ArrayList<Staff> staffOnCall;
	/**Staff currently busy, so unavailable for new duties*/
	private ArrayList<Staff> activeStaff;
	
	/**The remote method invocation server*/
	private RMIServer server;
	/**logs relevant queue related incidents as well as exceptions*/
	private Logger logger;

	/**whether too many patients have been waiting too long*/
	private boolean excessiveWaitingAlertSent;
	
	//ad-hoc testing fields
	private int testPatientNo;
	private Urgency[] testUrgencies;
	private int[] extensions;

	/**URL for the database connection*/
	private String url = "jdbc:mysql://web2.eeecs.qub.ac.uk/40058483";
	/**Connection that holds the session with the database*/
	private Connection con;
	/** The data accessor for the person table. Allows for searching of the
	 Person table in the database.*/
	private PersonDataAccessor dataAccessor;
	/**The data accessor for the staff table. Allows for searching of the 
	staff table in the database*/
	private StaffDataAccessor staffAccessor;
	/**The data accessor for the on call staff table. Allows for searching of the 
	staff table in the database*/
	private OnCallDataAccessor onCallAccessor;

	/**
	 * private constructor - default
	 */
	private Supervisor() {
	}

	/**Initializaion method - sets up all necessary elements of the system
	 * @param serverPort
	 * @param useSSL : whether to use ssl encryption
	 */
	public void init(int serverPort, boolean useSSL) {
		
		// If server port has been passed in set it, else leave it as its default value
		if (serverPort != 0) {
			this.serverPort = serverPort;
		}
		
		getPrefsFile();
		getPreferences();
		
		hQueue = new HQueue();
		clock = new Clock(BASE_UPDATE_INTERVAL);

		logger = Logger.getLogger(Supervisor.class);

		treatmentFacilities = new ArrayList<TreatmentFacility>();
		for (int i = 0; i < TREATMENT_ROOMS_COUNT; i++) {
			treatmentFacilities.add(i, new TreatmentRoom(i));
		}
	
		//Testing
		makeBobbies();
		
		log("Connecting to database");
		
		// set up connection to database
		try {
			setDataAccessor(new PersonDataAccessor(url, "40058483", "VPK7789"));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		// set up connection to database
		try {
			setStaffAccessor(new StaffDataAccessor(url, "40058483", "VPK7789"));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		// set up connection to database
		try {
			setOnCallStaffAccessor(new OnCallDataAccessor(url, "40058483", "VPK7789"));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		log("done fetching dataBase accessors");
		
		availableStaff = new ArrayList<Staff>();
		loggedInStaff = new ArrayList<Staff>();
		staffOnCall = new ArrayList<Staff>();
		activeStaff = new ArrayList<Staff>();
		onCallTeam = null;
		
		getAvailableStaff();
		superFakeOnCallTeam();
		//getOnCallList();
		
		// Start the server to allow clients to connect
		startServer(useSSL);
		
		excessiveWaitingAlertSent = false;
		
		exit = false;
	}

	/**load the prefs file and assign the instance var*/
	public void getPrefsFile(){
		prefs = Preferences.userRoot().node(this.getClass().getName());
	}
	
	/**assign relevant vars from the prefs*/
	public void getPreferences(){
		  String ID_Rooms = "treatment_rooms";
		  TREATMENT_ROOMS_COUNT = prefs.getInt(ID_Rooms, 5);
	}
	
	/**set values in the prefs according to current vars*/
	public void setPreferences(){
		String ID_Rooms = "treatment_rooms";
	    // now set the values
	    prefs.putInt(ID_Rooms, TREATMENT_ROOMS_COUNT);
	}
	
	/**Update the clock every tick to drive timed updates*/
	public void startLoop() {
		while (exit == false) {
			clock.update();
		}
	}
	
	/**Generates a list of dummy patients to quickly test the queue under heavy load*/
	public void makeBobbies(){
		testPatientNo = 0;

		testUrgencies = new Urgency[] { Urgency.EMERGENCY, Urgency.EMERGENCY,
				Urgency.EMERGENCY, Urgency.EMERGENCY, Urgency.NON_URGENT,
				Urgency.URGENT, Urgency.SEMI_URGENT, Urgency.NON_URGENT,
				Urgency.EMERGENCY, Urgency.URGENT, Urgency.SEMI_URGENT,
				Urgency.EMERGENCY, Urgency.EMERGENCY, Urgency.EMERGENCY,
				Urgency.SEMI_URGENT, Urgency.EMERGENCY, Urgency.EMERGENCY,
				Urgency.NON_URGENT, Urgency.NON_URGENT, Urgency.NON_URGENT,
				Urgency.URGENT, Urgency.SEMI_URGENT, Urgency.EMERGENCY,
				Urgency.EMERGENCY, Urgency.URGENT, Urgency.EMERGENCY,
				Urgency.EMERGENCY, Urgency.EMERGENCY, Urgency.EMERGENCY,
				Urgency.EMERGENCY };

		extensions = new int[] { 0, 1, 2 };
	}

	/**Test method to run dummy patients through the queue*/
	public void runBobbyTest(){
		// Testing
		if (testPatientNo < testUrgencies.length) {
			insertTestPatientBobby();
		}
		// automated extension tests
		for (int i = 0; i < treatmentFacilities.size(); i++) {
			TreatmentFacility tf = treatmentFacilities.get(i);
			if (tf.getTimeToAvailable() == 1 && tf.getPatient() != null) {
				if (i > 0 && i < 3) {
					if (extensions[i] > 0) {
						tf.extendTime();
						extensions[i]--;
					}
				}
			}
		}
		
//		if(testPatientNo == 9){
//			Patient sample;
//			for(int i = 0; i < hQueue.getPQ().size(); i++){
//				sample =  hQueue.getPQ().get(i);
//				if(sample.getPerson().getFirstName().equalsIgnoreCase("bobby7")){
//					reAssignTriage(sample, Urgency.EMERGENCY);
//				}
//			}
//		}
		
		if(testPatientNo == 3){
		//	addTreatmentRooms(1);
		}
		
		if(testPatientNo == 13){
		//	removeTreatmentRooms(4);
		}
	}

	
	/**
	 * add additional treatment rooms to the system
	 */	
	public void addTreatmentRooms(int numToAdd){
		log("Adding room(s)");
		if(onCallTeam != null){
			treatmentFacilities.remove(onCallTeam);
		}
		for(int i = 0; i < numToAdd; i++){
			TreatmentRoom tr = new TreatmentRoom(treatmentFacilities.size());
			treatmentFacilities.add(tr);
		}	
		if(onCallTeam != null){
			treatmentFacilities.add(onCallTeam);
		}
	}
	/**
	 * remove treatment rooms from the system
	 */	
	public boolean removeTreatmentRooms(int newNum){
		log("removing room(s)");
		int pqSize = hQueue.getPQ().size();
		if(onCallTeam != null){
			treatmentFacilities.remove(onCallTeam);
		}

		for(int i = treatmentFacilities.size()-1; i >= 0; i--){
			TreatmentFacility tf = treatmentFacilities.get(i);
			if(tf.getPatient()==null){
				treatmentFacilities.remove(tf);
				if(treatmentFacilities.size() <= newNum){
					break;
				}
			}
		}
		
		if(treatmentFacilities.size() > newNum){
			if( (treatmentFacilities.size() - newNum) > (MAX_QUEUE_SIZE - pqSize) ){
				//Not enough space in the queue for the patients in treatment to be displaced to 
				return false;
			}else{
				for(int i = treatmentFacilities.size()-1; i >= newNum; i--){
					TreatmentFacility tf = treatmentFacilities.get(i);
					Patient p = tf.getPatient();
					hQueue.reQueue(p);
					treatmentFacilities.remove(tf);
				}
			}
		}
		
		if(onCallTeam != null){
			treatmentFacilities.add(onCallTeam);
		}
		
		return true;
	}
	
	public void superFakeOnCallTeam(){
		Staff drOctopus = new Staff("docOc", "8ArmsBaby");
		drOctopus.setJob(Job.DOCTOR);
		Staff drDoom = new Staff("drDoom", "hahaha");
		drDoom.setJob(Job.DOCTOR);
		Staff nurseBetty = new Staff("Betty","bettyPass");
		nurseBetty.setJob(Job.NURSE);
		Staff nurseJohn = new Staff("John","johnPass");
		nurseJohn.setJob(Job.NURSE);
		Staff nurseJane = new Staff("Jane","janePass");
		nurseJane.setJob(Job.NURSE);
		
		staffOnCall.add(nurseJane);
		staffOnCall.add(drDoom);
		staffOnCall.add(nurseJohn);
		staffOnCall.add(drOctopus);
		staffOnCall.add(nurseBetty);
	}
	
	/**
	 * Auto-generate simple test patients to push through the system
	 */
	private void insertTestPatientBobby() {
		Person testPerson = new Person();
		testPerson.setFirstName("Bobby" + testPatientNo);
		testPerson.setLastName("Branson" + testPatientNo);

		Patient test = new Patient();
		test.setPerson(testPerson);
		test.setUrgency(testUrgencies[testPatientNo]);

		admitPatient(test);
		testPatientNo++;
	}	
	
	/**
	 * Starts the RMI server so that clients can connect and communicate with
	 * the back end.
	 */
	public void startServer(boolean useSSL) {
		try {

			// Creates a registry that accepts requests on the port specified in
			// 'serverPort'
			LocateRegistry.createRegistry(serverPort);

			// Instantiate the RMI server
			if (useSSL) {
				// If SSL is to be used pass the SSlRMISocketFactory into the constructor
				server = new RMIServer(serverPort, new SslRMIClientSocketFactory(),
		                 new SslRMIServerSocketFactory(null, null, true));
			} else {
				// If SSL is not to be used, call the default with just port as args
				server = new RMIServer(serverPort);
			}
			// Bind the server object to the name it will be accessible by on
			// the client side.
			Naming.rebind("HQBertServer", server);

		} catch (RemoteException | MalformedURLException e) {

			// Inform users that an issue occurred
			System.err.println("Error ocurred while setting up RMI server.");
			e.printStackTrace();
		}
	}
	
	public void getAvailableStaff(){
		try {
			availableStaff = (ArrayList<Staff>)getStaff();
			System.out.println(availableStaff);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void getAvailableOnCall(){
		try {
			staffOnCall = (ArrayList<Staff>)getOnCallStaff();
			System.out.println(staffOnCall);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * @throws SQLException 
	 * 
	 */
	private List<Staff> getStaff() throws SQLException {
		return getStaffAccessor().getStaffList();
	}

	private List<Staff> getOnCallStaff() throws SQLException {
		return getOnCallStaffAccessor().getOnCallList();
	}
	
	/**
	 * This populates staffOnCall with people from the availableStaff list:
	 * in real-life they would come from a more robust rota system
	 */
//	public void getOnCallList(){
//		try{
//			
//		int doctors = 0;
//		for(int i = availableStaff.size()-1; i >= 0; i--){
//			Staff member = availableStaff.get(i);
//			if(member.getJob() == Job.DOCTOR){
//				staffOnCall.add(member);
//				availableStaff.remove(member);
//				doctors++;
//				if(doctors >= 2){
//					break;
//				}
//			}
//		}
//		
//		int nurses = 0;
//		for(int i = availableStaff.size()-1; i >= 0; i--){
//			Staff member = availableStaff.get(i);
//			if(member.getJob() == Job.NURSE){
//				staffOnCall.add(member);
//				availableStaff.remove(member);
//				nurses++;
//				if(nurses >= 3){
//					break;
//				}
//			}
//		}
//		}catch(IndexOutOfBoundsException iobE){
//			
//		}
//	}
	
	
	

	
	/**
	 * Update the queue, its subQueues, all the patients in the system, and all
	 * the treatment rooms.
	 * 
	 * @param deltaTime
	 *            : time (in milliseconds) since the last update
	 */
	public void update(int deltaTime) {
		
		runBobbyTest();
		
		//Check if the oncall team is needed: Do this first so emergencies get assigned to them
		manageOnCallAndAlerts();
		
		//update the treatment facilities (rooms and on-call if on-site)
		for (int i = 0; i < treatmentFacilities.size(); i++) {
			TreatmentFacility tf = treatmentFacilities.get(i);
			tf.update(deltaTime);
			if (tf.getTimeToAvailable() <= 0) {
				Patient nextPatient = hQueue.getNextPatient();
				if (nextPatient != null) {
					if(sendToTreatment(nextPatient) == true){
						//log sending of patient to treatment
					}else{
						//log that the patient didn't make it to treatment
					}
				}
			}
			tf.showFacilityInConsole();
		}

		//update the queue
		hQueue.update(deltaTime);


		// Send the updated queue to clients via the server
		server.updateClients();

		log("Update Complete");

		// checkCapacity();
		// checkWaitingTime();
	}


	/**
	 * 
	 * @param patient
	 * @return
	 */
	public boolean admitPatient(Patient patient) {
		try {
			if (hQueue.insert(patient) == true) {
				server.updateClients();
				return true;
			} else {
				if(patient.getUrgency() == Urgency.EMERGENCY){
					//Alert the manager of the next hoapital
				}
				server.updateClients();
				return false;
			}
		} catch (StackOverflowError so) {
			so.printStackTrace();
			return false;
		}

	}

	/**
	 * Send patient to available treatment room If incoming patient is an
	 * emergency, they will displace a nonemergency occupying a treatment room
	 * 
	 * @param patient
	 * @return true if patient successfully sent to a treatment room false if no
	 *         treatment room available
	 */
	public boolean sendToTreatment(Patient patient) {
		boolean success = false;
		int targetRoomNum = 0;
		String targetRoomName = "";
		
		for (int i = 0; i < treatmentFacilities.size(); i++) {
			TreatmentFacility tf = treatmentFacilities.get(i);

			// If a room is available send the patient
			if (tf.getTimeToAvailable() <= 0 && tf.getPatient() == null) {
				tf.receivePatient(patient);
				targetRoomNum = i+1;
				success = true;
				break;
			}
		}
		// Patient didn't make it to a treatment room - check if they are an
		// emergency
		if (success == false && patient.getUrgency() == Urgency.EMERGENCY) {

			for (int i = 0; i < treatmentFacilities.size(); i++) {
				TreatmentFacility tf = treatmentFacilities.get(i);
				Patient tfPatient = tf.getPatient();
				if (tf.getPatient() == null) {
					/* The room is empty, just not "unlocked" yet 
					 * This should never actually fire as rooms are occupied until unlocked*/
					tf.receivePatient(patient);
					success = true;
					targetRoomNum = i+1;
					break;
				/*
				 * Another patient is in the room, check if they are an
				 * emergency
				 */
				} else if (tfPatient.getUrgency() != Urgency.EMERGENCY) {
					/*
					 * They aren't - add them to a list of possible to replace
					 * patients
					 */
					hQueue.addToDisplacable(tfPatient);
				}
			}
			
			if(success == false){
				Patient displacablePatient = null;
				displacablePatient = hQueue.findMostDisplacable();
				if (displacablePatient != null) {
					for (int i = 0; i < treatmentFacilities.size(); i++) {
						TreatmentFacility tf = treatmentFacilities.get(i);
						Patient roomCurrentPatient = tf.getPatient();
						if (roomCurrentPatient.equals(displacablePatient)) {
							tf.emergencyInterruption(patient);
							success = true;
							targetRoomNum = i+1;
							break;
						}
					}
				}else{
					if(onCallTeam != null){
						//Log Stuff: Full of emergencies - send this emergency away
						log("No emergency treatment available: "+patient.getPerson().getFirstName()+" ("+patient.getUrgency()+") sent away");
					}
				}					
			}
			
			//Still didn't make it - try onCall
			boolean onCallHere = false;
			if(success == false){
				if(onCallTeam == null){
					if(assembleOnCall(ON_CALL_REASON.EXTRA_EMERGENCY) == true){
						onCallHere = true;
					}
				}
				
				if(onCallHere == true){
					onCallTeam.receivePatient(patient);
					targetRoomNum = TREATMENT_ROOMS_COUNT;
					success = true;
				}
				
			}
			
			//if onCall was already here, they would have been checked for displacable patients already
		}

		if(success == true){
			if(targetRoomNum < TREATMENT_ROOMS_COUNT){
				targetRoomName = String.format("Treatment room %2d",targetRoomNum);
			}else{
				targetRoomName = "On-call team";
			}
			String message = patient.getPerson().getFirstName()+" "+patient.getPerson().getLastName()+" to "+targetRoomName;
			log(message);
			server.broadcastNextPatientCall(message);
		}
		
		return success;
	}

	/**
	 * Change a patient's triage category
	 */
	public void reAssignTriage(Patient patient, Urgency newUrgency){
		log("\tRe-assigning triage priority for "+patient.getPatientName());
		hQueue.reAssignTriage(patient, newUrgency);
	}
	
	/**
	 * 
	 */
	public void manageOnCallAndAlerts(){
		if(checkQueueFull() == true && onCallTeam == null){
			assembleOnCall(ON_CALL_REASON.QUEUE_FULL);
			
			// Alert the clients that the queue is full
			// via the RMI server.
			
		}
		
		if(checkTooManyOverduePatients() == true && excessiveWaitingAlertSent == false){
		//	if(ALERTS_ACTIVE == true){
				Staff manager = null;
				for(Staff member : availableStaff){
					if(member.getJob() == Job.MANAGER){
						manager = member;
					}
				}
				if(manager != null){
					ManagerAlert.emailWaitingTimeAlert(manager, ALERTS_ACTIVE);
					ManagerAlert.smsWaitingTimeAlert(manager, ALERTS_ACTIVE);

					log("Alerting Hospital Manager : Too many overdue patients." );
					excessiveWaitingAlertSent = true;
				}
		//	}
		}
	}
	
	/**
	 * Checks if the conditions are acceptable for the onCall team to go offline.
	 * If ok, the oncall team leaves.
	 */
	public void onCallTryLeave(){
		boolean canLeave = false;
		if(checkQueueFull() == false){
			for(int i = 0; i < TREATMENT_ROOMS_COUNT; i++){
				if(treatmentFacilities.get(i).patient == null){
					canLeave = true;
					break;
				}else if(treatmentFacilities.get(i).patient.getUrgency() != Urgency.EMERGENCY){
					canLeave = true;
					break;
				}
			}
		}
		if(canLeave == true){
			onCallGoAway();
		}
	}
	
	/**The on call team leaves and its members become available 
	 * to be called again in future
	 */
	public void onCallGoAway(){
		for(int i = 0; i < onCallTeam.getStaff().size(); i++){
			//activeStaff.add(onCallTeam.getStaff().get(i));
		}
		treatmentFacilities.remove(onCallTeam);
		log("On call team disengaged.");
		onCallTeam = null;
	}
	
	/**
	 * send messages to staff on the onCall list and assemble an onCall team
	 * @return whether the onCall team was successfully assembled
	 */
	public boolean assembleOnCall(ON_CALL_REASON reason){
		if(reason == ON_CALL_REASON.QUEUE_FULL){
			log("On-call team requested: full queue - On-call team ASSEMBLE!");
		}else if(reason == ON_CALL_REASON.EXTRA_EMERGENCY){
			log("On-call team requested: too many emergency - On-call team ASSEMBLE!");
		}
		
		onCallTeam = new OnCallTeam();
		for(int count = 0; count < 2; count++){
			for(int staffMemberIndex = 0; staffMemberIndex < staffOnCall.size(); staffMemberIndex++){
				Staff staffMember = staffOnCall.get(staffMemberIndex);
				if (staffMember.getJob() == Job.DOCTOR && !(activeStaff.contains(staffMember))){
					if(OnCallTeamAlert.onCallEmergencyPriority(staffMember, ALERTS_ACTIVE) == true){
						//activeStaff.add(staffMember);
						onCallTeam.assignStaff(staffMember);
					}
				}
			}
			if(onCallTeam.getStaff().size() < ONCALL_TEAM_DOCTORS){
				//Log that insufficient doctors are available for an oncall team
				log("Not enough doctors for on-call");
				return false;
			}
			for(int staffMemberIndex = 0; staffMemberIndex < staffOnCall.size(); staffMemberIndex++){
				Staff staffMember = staffOnCall.get(staffMemberIndex);
				if (staffMember.getJob() == Job.NURSE && !(activeStaff.contains(staffMember))){
					if(OnCallTeamAlert.onCallEmergencyPriority(staffMember, ALERTS_ACTIVE) == true){
						//activeStaff.add(staffMember);
						onCallTeam.assignStaff(staffMember);
					}
				}
			}
			if(onCallTeam.getStaff().size() < (ONCALL_TEAM_DOCTORS + ONCALL_TEAM_NURSES)){
				//Log that insufficient nurses are available for an oncall team
				log("Not enough nurses for on-call");
				return false;
			}
		}
		treatmentFacilities.add(onCallTeam);
		return true;
	}


	/**
	 * Extend the time a room is expected to be occupied by a pre-set amount, due to patient not finished.
	 * @param roomIndex : which treatment Room to extend the time on.
	 */
	public void extendRoom(int roomIndex, ExtensionReason reason) {
		treatmentFacilities.get(roomIndex).extendTime();
	}

	/**
	 * check if more than the maximum allowed number of patients have been waiting longer than the 
	 * maximum allowed time.
	 * @return  true : too many patients waiting too long
	 * 			false : not reached thresh-hold yet
	 */
	private boolean checkTooManyOverduePatients() {
		int delayedCount = 0;
		for (Patient p : hQueue.getPQ()) {
			if (p.getWaitTime() >= MAX_WAIT_TIME) {
				delayedCount++;
			}
		}

		if (delayedCount > MAX_OVERDUE_PATIENTS) {
			return true;
		} else {
			excessiveWaitingAlertSent = false;
			return false;
		}
	}

	/**
	 * Check if the queue has reached its maximum allowed size
	 * @return true : queue at max size
 	 * 			false : still room to add more people to the queue
	 */
	private boolean checkQueueFull() {
		// If the patient queue is full
		if (hQueue.getPQ().size() == Supervisor.INSTANCE.MAX_QUEUE_SIZE) {
			return true;
		}else{
			return false;
		}

	}
	
	public void removeFromQueue(Patient patient) {
		System.out.println("Removing "+ patient.getPerson().getFirstName() + " from the queue.");	

		hQueue.removePatient(patient);
	}

	/* Getters and setters */
	
	/**
	 * Get the queue management object, which contains getters for the actual queue
	 * and manipulation methods
	 * @return
	 */
	public HQueue getHQueue() {
		return hQueue;
	}

	public Date getCurrentTime() {
		return clock.getCurrentTime();
	}

	/**Get the treatment facilities - including onCall team if its not null*/
	public ArrayList<TreatmentFacility> getTreatmentFacilities() {
		return treatmentFacilities;
	}

	/**
	 * getter for person data accessor
	 * 
	 * @return
	 */
	public PersonDataAccessor getDataAccessor() {
		return dataAccessor;
	}

	/**
	 * setter for person data accessor
	 * 
	 * @param dataAccessor
	 */
	public void setDataAccessor(PersonDataAccessor dataAccessor) {
		this.dataAccessor = dataAccessor;
	}

	/**
	 * Log a message to file and send it to the front end.
	 * @param message
	 */
	public void log(String message) {
		logger.log(Priority.INFO, message);
		if (server != null) {
			server.broadcastLog(message);
		}
	}
	
	/**
	 * Log a message to file
	 * @param message
	 */
	public void logToFile(String message) {
		logger.log(Priority.INFO, message);
	}

	/**
	 * getter for staff data accessor
	 * 
	 * @return
	 */
	public StaffDataAccessor getStaffAccessor() {
		return staffAccessor;
	}

	/**
	 * getter for on call staff data accessor
	 * 
	 * @return
	 */
	public OnCallDataAccessor getOnCallStaffAccessor() {
		return onCallAccessor;
	}

	/**
	 * setter for staff data accessor
	 * 
	 * @param staffAccessor
	 */
	public void setStaffAccessor(StaffDataAccessor staffAccessor) {
		this.staffAccessor = staffAccessor;
	}

	public void setOnCallStaffAccessor(OnCallDataAccessor onCallStaffAccessor){
		this.onCallAccessor = onCallStaffAccessor;
	}
	
	public OnCallTeam getOncallTeam(){
		return this.onCallTeam;
	}

	public ArrayList<Staff> getOnCallStaffList(){
		return staffOnCall;
	}
	
	/**
	 * Find a person by nhsNumber and update their doctors notes.
	 * @param nhsNumber
	 * @param doctorsNotes
	 * @return
	 */
	public boolean updateDoctorsNotes(String nhsNumber, String doctorsNotes) {
		
		// Loop through the various facilities to find the patient
		for (TreatmentFacility facility : treatmentFacilities) {
			Person person = facility.patient.getPerson();
			// If the NHS number matches, update the doctors notes.
			if (person.getNHSNum().equals(nhsNumber)) {
				person.setDoctorsNotes(doctorsNotes);
			}
		}
		
		try {
			// Update the database with the updated doctors notes and pass the success boolean it
			// returns on to front end to inform them that the update was successful
			return dataAccessor.updateDoctorsNotes(nhsNumber, doctorsNotes);
		} catch (SQLException e) {
			// return false to inform the front end that the update failed.
			return false;
		}
	}


	public int getCurrentNumberOfTreatmentRooms(){
		return this.TREATMENT_ROOMS_COUNT;
	}
	
	public boolean setCurrentNumberOfTreatmentRooms(int numRooms){
		int diff = numRooms - TREATMENT_ROOMS_COUNT;
		TREATMENT_ROOMS_COUNT = numRooms;
		if(diff > 0){
			addTreatmentRooms(diff);
			return true;
		}else if(diff < 0){
			if(removeTreatmentRooms(numRooms) == true){
				return true;
			}else{
				return false;
			}
		}
		return true;
	}


}
