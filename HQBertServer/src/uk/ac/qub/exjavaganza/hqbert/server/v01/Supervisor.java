package uk.ac.qub.exjavaganza.hqbert.server.v01;

import java.util.ArrayList;
import java.util.Date;


public enum Supervisor {

	INSTANCE;
	
	public final int MAX_WAIT_TIME = 12;
	public final int BASE_UPDATE_INTERVAL = 1;
	public final int BASE_ROOM_OCCUPANCY_TIME = 10;
	public final int ROOM_OCCUPANCY_EXTENSION_TIME = 5;
	public final int MAX_TREATMENT_ROOMS = 3;
	
	HQueue hQueue;
	Clock clock;
	boolean exit;
	ArrayList<TreatmentFacility> treatmentFacilities;
	Server server;
	
	private int testPatientNo;
	private Urgency[] testUrgencies;
	
	private Supervisor(){
	}
	
	public void init(){
		hQueue = new HQueue();
		clock = new Clock(BASE_UPDATE_INTERVAL);
		
		//server = new Server();
		//server.start();
		
		treatmentFacilities = new ArrayList<TreatmentFacility>();
		for(int i = 0; i < MAX_TREATMENT_ROOMS; i++){
			treatmentFacilities.add(i, new TreatmentRoom(i));
		}
		
		testPatientNo = 0;
		
		testUrgencies = new Urgency[]{
				Urgency.NON_URGENT,
				Urgency.NON_URGENT,
				Urgency.SEMI_URGENT,
				Urgency.SEMI_URGENT,
				Urgency.NON_URGENT,
				Urgency.URGENT,
				Urgency.SEMI_URGENT,
				Urgency.EMERGENCY,
				Urgency.NON_URGENT,
				Urgency.URGENT,
				Urgency.SEMI_URGENT,
				Urgency.EMERGENCY,
				Urgency.EMERGENCY,
				Urgency.EMERGENCY,
				Urgency.SEMI_URGENT
		};
		
		exit = false;
	}
	
	public void startLoop() {
		while(exit == false){
			clock.update();
		}
	}
	
	
	/**
	 * Update the queue, its subQueues, all the patients in the system, and all the treatment rooms.
	 * @param deltaTime : time (in milliseconds) since the last update
	 */
	public void update(int deltaTime){	
		
		for(int i = 0; i < treatmentFacilities.size(); i++){
			TreatmentFacility tf = treatmentFacilities.get(i);
			tf.update(deltaTime);
			if(tf.getTimeToAvailable() <= 0){
				Patient nextPatient = hQueue.getNextPatient();
				if (nextPatient != null){
					sendToTreatment(nextPatient);
				
				}
			}
			tf.showFacilityInConsole();
		}
		
		hQueue.update(deltaTime);
		
		
		//Testing
		if(testPatientNo < testUrgencies.length){
			insertTestPatient();
		}
		if(treatmentFacilities.get(0).getPatient() != null && treatmentFacilities.get(0).getTimeToAvailable() < BASE_ROOM_OCCUPANCY_TIME / 2){
			treatmentFacilities.get(0).DischargePatient();
		}
		
		
		//server.sendObject(hQueue);
	}
	
	private void insertTestPatient(){
		Person testPerson = new Person();
		testPerson.firstName = "Bobby"+testPatientNo;
		testPerson.lastName = "Branson"+testPatientNo;
		
		Patient test = new Patient();
		test.person = testPerson;
		test.SetUrgency(testUrgencies[testPatientNo]);
		
		admitPatient(test);
		testPatientNo++;
	}
	
	public boolean admitPatient(Patient patient){
		if(hQueue.insert(patient) == true){	
			return true;
		}else{
			System.out.println("Queue at capacity : "+patient.getPerson().getFirstName()+" - "+patient.getUrgency()+" - sent away.");
			return false;
		}
	}
	
	/**Send patient to available treatment room
	 * If incoming patient is an emergency, they will displace a nonemergency occupying a treatment room
	 * @param patient
	 * @return true if patient successfully sent to a treatment room
	 * 			false if no treatment room available
	 */
	public boolean sendToTreatment(Patient patient){
		for(int i = 0; i < treatmentFacilities.size(); i++){
			TreatmentFacility tf = treatmentFacilities.get(i);
			
			//If a room is available and the patient is at the send the new patient straight to a room
			if(tf.getTimeToAvailable() <= 0 && tf.getPatient() == null){
				tf.receivePatient(patient);
				return true;
			}
		}
		//Patient didn't make it to a treatment room - check if they are an emergency
		if(patient.urgency == Urgency.EMERGENCY){
			for(int i = 0; i < treatmentFacilities.size(); i++){
				TreatmentFacility tf = treatmentFacilities.get(i);
				if(tf.getPatient() == null){	//The room is empty, just not "unlocked" yet
					tf.receivePatient(patient);
					return true;	
				}else if(tf.getPatient().getUrgency() != Urgency.EMERGENCY){	//Another patient is in the room, check if they are an emergency
					tf.emergencyInterruption(patient);
					return true;
				}
			}
		}
		return false;
	}
	
	public void removeFromQueue(Patient patient){
		System.out.println("Removing "+patient.getPerson().getFirstName()+" from the queue.");
		hQueue.removePatient(patient);
	}
	
	
	
	/*Getters and setters*/
	public HQueue getHQueue(){
		return hQueue;
	}
	
	public Date getCurrentTime(){
		return clock.getCurrentTime();
	}
	
	
}
