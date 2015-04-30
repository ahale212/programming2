package uk.ac.qub.exjavaganza.hqbert.server.v01;

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
import java.util.List;

import javax.rmi.ssl.SslRMIClientSocketFactory;
import javax.rmi.ssl.SslRMIServerSocketFactory;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

public enum Supervisor {

	INSTANCE;

	public final int MAX_QUEUE_SIZE = 5;
	public final int MAX_WAIT_TIME = 30;
	public final int PRIORITY_WAIT_TIME = 25;
	public final int MAX_OVERDUE_PATIENTS = 2
			;
	public final int BASE_UPDATE_INTERVAL = 1;
	public final int BASE_ROOM_OCCUPANCY_TIME = 10;
	public final int ROOM_OCCUPANCY_EXTENSION_TIME = 5;
	public final int ONCALL_ENGAGEMENT_TIME = 15;
	
	public final int MAX_TREATMENT_ROOMS = 3;
	public final int ONCALL_TEAM_DOCTORS = 2;
	public final int ONCALL_TEAM_NURSES = 3;
	public final boolean ALERTS_ACTIVE = false;

	public final float TIME_MULTI = 60;

	private enum ON_CALL_REASON {QUEUE_FULL,EXTRA_EMERGENCY};
	
	private int serverPort = 1099;

	private HQueue hQueue;
	private Clock clock;
	private boolean exit;
	private ArrayList<TreatmentFacility> treatmentFacilities;
	private OnCallTeam onCallTeam;
	private ArrayList<Staff> staffOnCall;
	private ArrayList<Staff> activeOnCallStaff;
	private RMIServer server;
	private Logger logger;

	//whether too many patients have been waiting too long
	private boolean waitTimesUnacceptable;
	
	// Test stuff
	private int testPatientNo;
	private Urgency[] testUrgencies;
	private int[] extensions;

	// URL for the database connection
	private String url = "jdbc:mysql://web2.eeecs.qub.ac.uk/40058483";
	// Connection that holds the session with the database
	private Connection con;
	// The data accessor for the person table. Allows for searching of the
	// Person table in the database.
	private PersonDataAccessor dataAccessor;
	//The data accessor for the staff table. Allows for searching of the 
	//staff table in the database
	private StaffDataAccessor staffAccessor;

	private Supervisor() {
	}

	public void init(int serverPort, boolean useSSL) {
		
		// If server port has been passed in set it, else leave it as its default value
		if (serverPort != 0) {
			this.serverPort = serverPort;
		}
		
		hQueue = new HQueue();
		clock = new Clock(BASE_UPDATE_INTERVAL);



		logger = Logger.getLogger(Supervisor.class);

		treatmentFacilities = new ArrayList<TreatmentFacility>();
		for (int i = 0; i < MAX_TREATMENT_ROOMS; i++) {
			treatmentFacilities.add(i, new TreatmentRoom(i));
		}

		staffOnCall = new ArrayList<Staff>();
		activeOnCallStaff = new ArrayList<Staff>();
		onCallTeam = null;
		
		//Testing
		runBobbyTest();
		fakeOnCallTeam();

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
		
		waitTimesUnacceptable = false;

		try {
			List<Staff> staff = getStaff();
			System.out.println(staff);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Start the server to allow clients to connect
		startServer(useSSL);
		
		exit = false;
	}

	public void startLoop() {
		while (exit == false) {
			clock.update();
		}
	}
	
	public void runBobbyTest(){
		testPatientNo = 0;

		testUrgencies = new Urgency[] { Urgency.EMERGENCY, Urgency.EMERGENCY,
				Urgency.EMERGENCY, Urgency.EMERGENCY, Urgency.NON_URGENT,
				Urgency.URGENT, Urgency.SEMI_URGENT, Urgency.EMERGENCY,
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

	public void fakeOnCallTeam(){
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
	
	/**
	 * @throws SQLException 
	 * 
	 */
	private List<Staff> getStaff() throws SQLException {
		return getStaffAccessor().getStaffList();
	}

	/**
	 * Update the queue, its subQueues, all the patients in the system, and all
	 * the treatment rooms.
	 * 
	 * @param deltaTime
	 *            : time (in milliseconds) since the last update
	 */
	public void update(int deltaTime) {
		// Testing
		if (testPatientNo < testUrgencies.length) {
			insertTestPatient();
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
	 * Auto-generate simple test patients to push through the system
	 */
	private void insertTestPatient() {
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
				System.out.println("Queue at capacity : "
						+ patient.getPerson().getFirstName() + " - "
						+ patient.getUrgency() + " - sent away.");
				server.updateClients();
				System.out.println("PATIENT REJECTED : "
						+ patient.getPerson().getFirstName());
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
		
		for (int i = 0; i < treatmentFacilities.size(); i++) {
			TreatmentFacility tf = treatmentFacilities.get(i);

			// If a room is available send the patient
			if (tf.getTimeToAvailable() <= 0 && tf.getPatient() == null) {
				tf.receivePatient(patient);
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
							break;
						}
					}
				}else{
					if(onCallTeam != null){
						//Log Stuff: Full of emergencies - send this emergency away
						System.out.println("No emergency treatment available: "+patient.getPerson().getFirstName()+" ("+patient.getUrgency()+") sent away");
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
					success = true;
				}
				
			}
			
			//if onCall was already here, they would have been checked for displacable patients already
		}

		return success;
	}

	/**
	 * 
	 */
	public void manageOnCallAndAlerts(){
		if(checkQueueFull() == true && onCallTeam == null){
			assembleOnCall(ON_CALL_REASON.QUEUE_FULL);
			System.out.println("\tONCALL: full queue - ASSEMBLE!");
			// Alert the clients that the queue is full
			// via the RMI server.
			
		}
		
		
		if(checkWaitingTimes() == true && waitTimesUnacceptable == false){
			waitTimesUnacceptable = false;
			if(ALERTS_ACTIVE == true){
				ManagerAlert.emailWaitingTimeAlert();
				ManagerAlert.smsWaitingTimeAlert();
			}
		}
	}
	
	public void onCallTryLeave(){
		boolean canLeave = false;
		if(checkQueueFull() == false){
			for(int i = 0; i < MAX_TREATMENT_ROOMS; i++){
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
	
	public void onCallGoAway(){
		for(int i = 0; i < onCallTeam.getStaff().size(); i++){
			activeOnCallStaff.add(onCallTeam.getStaff().get(i));
		}
		treatmentFacilities.remove(onCallTeam);
		System.out.println("\n\nOnCALL REMOVED!!!!!!!!!!!!!!!!!!!!!!!!!\n\n");
		onCallTeam = null;
	}
	
	/**
	 * send messages to staff on the onCall list and assemble an onCall team
	 * @return whether the onCall team was successfully assembled
	 */
	public boolean assembleOnCall(ON_CALL_REASON reason){
		onCallTeam = new OnCallTeam();
		for(int count = 0; count < 2; count++){
			for(int staffMemberIndex = 0; staffMemberIndex < staffOnCall.size(); staffMemberIndex++){
				Staff staffMember = staffOnCall.get(staffMemberIndex);
				if (staffMember.getJob() == Job.DOCTOR && !(activeOnCallStaff.contains(staffMember))){
					if(OnCallTeamAlert.onCallEmergencyPriority(staffMember, ALERTS_ACTIVE) == true){
						activeOnCallStaff.add(staffMember);
						onCallTeam.assignStaff(staffMember);
					}
				}
			}
			if(onCallTeam.getStaff().size() < ONCALL_TEAM_DOCTORS){
				//Log that insufficient doctors are available for an oncall team
				return false;
			}
			for(int staffMemberIndex = 0; staffMemberIndex < staffOnCall.size(); staffMemberIndex++){
				Staff staffMember = staffOnCall.get(staffMemberIndex);
				if (staffMember.getJob() == Job.NURSE && !(activeOnCallStaff.contains(staffMember))){
					if(OnCallTeamAlert.onCallEmergencyPriority(staffMember, ALERTS_ACTIVE) == true){
						activeOnCallStaff.add(staffMember);
						onCallTeam.assignStaff(staffMember);
					}
				}
			}
			if(onCallTeam.getStaff().size() < (ONCALL_TEAM_DOCTORS + ONCALL_TEAM_NURSES)){
				//Log that insufficient nurses are available for an oncall team
				return false;
			}
		}
		treatmentFacilities.add(onCallTeam);
		return true;
	}


	public void extendRoom(int roomIndex) {
		treatmentFacilities.get(roomIndex).extendTime();
	}

	private boolean checkWaitingTimes() {
		int delayedCount = 0;

		for (Patient p : hQueue.getPQ()) {
			if (p.getWaitTime() >= MAX_WAIT_TIME) {
				delayedCount++;
			}
		}

		if (delayedCount > MAX_OVERDUE_PATIENTS) {
			return true;
		} else {
			return false;
		}

		/*
		 * if (delayedCount>=2){
		 * System.out.println("Sending wait time messages");
		 * ManagerAlert.emailWaitingTimeAlert();
		 * ManagerAlert.smsWaitingTimeAlert(); }
		 */
	}

	private boolean checkQueueFull() {
		// If the patient queue is full
		if (hQueue.getPQ().size() == Supervisor.INSTANCE.MAX_QUEUE_SIZE) {
			return true;
		}else{
			return false;
		}

	}

	public void removeFromQueue(Patient patient) {
		System.out.println("Removing "		 + patient.getPerson().getFirstName() + " from the queue.");	// Alert the clients that the queue is full
			// via the RMI server.
			server.broadcastQueueFullAlert();

		hQueue.removePatient(patient);
	}

	/* Getters and setters */
	public HQueue getHQueue() {
		return hQueue;
	}

	public Date getCurrentTime() {
		return clock.getCurrentTime();
	}

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

	public void log(String message) {
		logger.debug(message);
		server.broadcastLog(message);
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
	 * setter for staff data accessor
	 * 
	 * @param staffAccessor
	 */
	public void setStaffAccessor(StaffDataAccessor staffAccessor) {
		this.staffAccessor = staffAccessor;
	}

	
	public OnCallTeam getOncallTeam(){
		return this.onCallTeam;
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
	
	/**
	 * Extends the treatment time for a given facility
	 * @param facility		The facility to be updated
	 */
	public void extendTreatmentRoom(TreatmentFacility facility) {
		for (TreatmentFacility loopedFacility : treatmentFacilities) {
			if (facility instanceof OnCallTeam && loopedFacility instanceof OnCallTeam) {
				loopedFacility.extendTime();
			}
		}
	}


}
