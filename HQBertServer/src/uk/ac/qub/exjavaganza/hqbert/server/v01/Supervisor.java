package uk.ac.qub.exjavaganza.hqbert.server.v01;

import java.util.ArrayList;
import java.util.Date;


public enum Supervisor {

	INSTANCE;
	
	public final int MAX_WAIT_TIME = 12;
	public final int BASE_UPDATE_INTERVAL = 1;
	public final int BASE_ROOM_OCCUPANCY_TIME = 600;
	public final int ROOM_OCCUPANCY_EXTENSION_TIME = 300;
	public final int MAX_TREATMENT_ROOMS = 5;
	
	HQueue hQueue;
	Clock clock;
	boolean exit;
	ArrayList<TreatmentRoom> rooms;
	Server server;
	
	private int testPatientNo;
	private Urgency[] testUrgencies;
	
	private Supervisor(){
		hQueue = new HQueue();
		
		clock = new Clock(BASE_UPDATE_INTERVAL);
		
		//server = new Server();
		//server.start();
		
		rooms = new ArrayList<TreatmentRoom>(MAX_TREATMENT_ROOMS);
		for(int i = 0; i < rooms.size(); i++){
			rooms.add(i, new TreatmentRoom());
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
				Urgency.EMERGENCY
		};
		
		exit = false;
	}
	
	public void startLoop() {
		
		while(exit == false){
			clock.update();
		}
		
	}
	
	public void update(int deltaTime){
		
		hQueue.update(deltaTime);
		for(int i = 0; i < rooms.size(); i++){
			TreatmentRoom room = rooms.get(i);
			room.update(deltaTime);
		}

		if(testPatientNo < testUrgencies.length){
			insertTestPatient();
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
		
		hQueue.insert(test);
		testPatientNo++;
	}
	
	public HQueue getHQueue(){
		return hQueue;
	}
	
	public Date getCurrentTime(){
		return clock.getCurrentTime();
	}
	
	
}
