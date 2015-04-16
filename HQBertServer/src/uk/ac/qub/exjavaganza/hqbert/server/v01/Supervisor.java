package uk.ac.qub.exjavaganza.hqbert.server.v01;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

public enum Supervisor {

	INSTANCE;

	public final int MAX_WAIT_TIME = 12;
	public final int MAX_OVERDUE_PATIENTS = 3;
	public final int BASE_UPDATE_INTERVAL = 1;
	public final int BASE_ROOM_OCCUPANCY_TIME = 10;
	public final int ROOM_OCCUPANCY_EXTENSION_TIME = 5;
	public final int ONCALL_ENGAGEMENT_TIME = 15;
	public final int MAX_TREATMENT_ROOMS = 4;

	private final int serverPort = 1099;

	private HQueue hQueue;
	private Clock clock;
	private boolean exit;
	private ArrayList<TreatmentFacility> treatmentFacilities;
	private RMIServer server;
	private Logger logger;

	private int testPatientNo;
	private Urgency[] testUrgencies;
	
	// URL for the database connection
	private String url = "jdbc:mysql://web2.eeecs.qub.ac.uk/40058483";
	// Connection that holds the session with the database
	private Connection con;
	// The data accessor for the person table. Allows for searching of the 
	// Person table in the database.
	private PersonDataAccessor dataAccessor;

	private Supervisor() {
	}

	public void init() {
		hQueue = new HQueue();
		clock = new Clock(BASE_UPDATE_INTERVAL);

		startServer();

		logger = Logger.getLogger(Supervisor.class);
		
		treatmentFacilities = new ArrayList<TreatmentFacility>();
		for (int i = 0; i < MAX_TREATMENT_ROOMS; i++) {
			treatmentFacilities.add(i, new TreatmentRoom(i));
		}

		testPatientNo = 0;

		testUrgencies = new Urgency[] { Urgency.NON_URGENT, Urgency.NON_URGENT,
				Urgency.SEMI_URGENT, Urgency.SEMI_URGENT, Urgency.NON_URGENT,
				Urgency.URGENT, Urgency.SEMI_URGENT, Urgency.EMERGENCY,
				Urgency.NON_URGENT, Urgency.URGENT, Urgency.SEMI_URGENT,
				Urgency.EMERGENCY, Urgency.EMERGENCY, Urgency.EMERGENCY,
				Urgency.SEMI_URGENT };

		exit = false;
		
		//set up connection to database
		try {
			setDataAccessor(new PersonDataAccessor(url, "40058483", "VPK7789"));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void startLoop() {
		while (exit == false) {
			clock.update();
		}
	}

	/**
	 * Starts the RMI server so that clients can connect and communicate with
	 * the back end.
	 */
	public void startServer() {
		try {

			// Creates a registry that accepts requests on the port specified in
			// 'serverPort'
			LocateRegistry.createRegistry(serverPort);

			// Instantiate the RMI server
			server = new RMIServer();
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
	 * Update the queue, its subQueues, all the patients in the system, and all
	 * the treatment rooms.
	 * 
	 * @param deltaTime
	 *            : time (in milliseconds) since the last update
	 */
	public void update(int deltaTime) {

		for (int i = 0; i < treatmentFacilities.size(); i++) {
			TreatmentFacility tf = treatmentFacilities.get(i);
			tf.update(deltaTime);
			if (tf.getTimeToAvailable() <= 0) {
				Patient nextPatient = hQueue.getNextPatient();
				if (nextPatient != null) {
					sendToTreatment(nextPatient);

				}
			}
			tf.showFacilityInConsole();
		}

		hQueue.update(deltaTime);
		
		

		// Testing
		if (testPatientNo < testUrgencies.length) {
			insertTestPatient();
		}
		if (treatmentFacilities.get(0).getPatient() != null
				&& treatmentFacilities.get(0).getTimeToAvailable() < BASE_ROOM_OCCUPANCY_TIME / 2) {
			treatmentFacilities.get(0).DischargePatient();
		}

		// Send the updated queue to clients via the server
		server.updateClients();
		
		log("Update Complete");
		
		//checkCapacity();
		//checkWaitingTime();
	}

	private void insertTestPatient() {
		Person testPerson = new Person();
		testPerson.setFirstName ("Bobby" + testPatientNo);
		testPerson.setLastName ("Branson" + testPatientNo);

		Patient test = new Patient();
		test.person = testPerson;
		test.setUrgency(testUrgencies[testPatientNo]);

		if(testPatientNo >= 7){
			boolean stop;
			stop = true;
		}
		
		admitPatient(test);
		testPatientNo++;
	}

	public boolean admitPatient(Patient patient) {
		try{
			if (hQueue.insert(patient) == true) {
				server.updateClients();
				return true;
			} else {
				System.out.println("Queue at capacity : "
						+ patient.getPerson().getFirstName() + " - "
						+ patient.getUrgency() + " - sent away.");
				return false;
			}
		}catch(StackOverflowError so){
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
		
		for (int i = 0; i < treatmentFacilities.size(); i++) {
			TreatmentFacility tf = treatmentFacilities.get(i);

			// If a room is available and the patient is at the send the new
			// patient straight to a room
			if (tf.getTimeToAvailable() <= 0 && tf.getPatient() == null) {
				tf.receivePatient(patient);
				return true;
			}
		}
		// Patient didn't make it to a treatment room - check if they are an
		// emergency
		if (patient.urgency == Urgency.EMERGENCY) {
			
			for (int i = 0; i < treatmentFacilities.size(); i++) {
				TreatmentFacility tf = treatmentFacilities.get(i);
				if (tf.getPatient() == null) { 
					/* The room is empty, just not "unlocked" yet*/
					tf.receivePatient(patient);
					return true;
				/* Another patient is in the room, check if they are an emergency*/
				} else if (tf.getPatient().getUrgency() != Urgency.EMERGENCY) { 
					/*They aren't - add them to a list of possible to replace patients*/
					hQueue.addToDisplacable(tf.getPatient());
				}
			}
			
			hQueue.sortDisplacable();
			Patient displacablePatient = null;
			displacablePatient = hQueue.getMostDisplacable();
			if(displacablePatient != null){
				for(int i = 0; i < treatmentFacilities.size(); i++){
					TreatmentFacility tf = treatmentFacilities.get(i);
					Patient roomCurrentPatient = tf.getPatient();
					if(roomCurrentPatient.equals(displacablePatient)){
						tf.emergencyInterruption(patient);
						return true;
					}
				}
			} 
		}
		return false;
	}

	public void alertOnCall(){
		
	}
	
	
	private boolean checkRoomsFull() {
		boolean roomsFull = true;

		for (TreatmentFacility facility : treatmentFacilities) {
			if (facility.getPatient() == null) {
				roomsFull = false;
			}
		}
		
		return roomsFull;
		
		/*
		if (roomsFull){
			System.out.println("Sending capacity messages");			
			ManagerAlert.emailCapacityAlert();
			ManagerAlert.smsCapacityAlert();
		}*/
	}
	
	private boolean checkWaitingTimes(){
		int delayedCount = 0;
		
		for (Patient p : hQueue.getPQ()){
			if (p.getWaitTime() >= MAX_WAIT_TIME){
				delayedCount++;
			}
		}
		
		if(delayedCount > MAX_OVERDUE_PATIENTS){
			return true;
		}else{
			return false;
		}
		
		/*
		if (delayedCount>=2){
			System.out.println("Sending wait time messages");
			ManagerAlert.emailWaitingTimeAlert();
			ManagerAlert.smsWaitingTimeAlert();
		}*/
	}
	
	private void checkQueueFull() {
		
		// If the patient queue is full
		if (hQueue.getPQ().size() == HQueue.MAX_QUEUE_SIZE) {
			
			OnCallTeamAlert.onCallTeamQueueCapacity();
		}
		
	}
	

	public void removeFromQueue(Patient patient) {
		System.out.println("Removing " + patient.getPerson().getFirstName()
				+ " from the queue.");
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

	public PersonDataAccessor getDataAccessor() {
		return dataAccessor;
	}

	public void setDataAccessor(PersonDataAccessor dataAccessor) {
		this.dataAccessor = dataAccessor;
	}
	
	public void log(String message) {
		logger.debug(message);
		server.broadcastLog(message);
	}
	
	
}
