package uk.ac.qub.exjavaganza.hqbert.server.v01;

import java.util.ArrayList;



public enum Supervisor {

	INSTANCE;
	
	public static final int MAX_WAIT_TIME = 1800;
	public static final int UPDATE_INTERVAL = 60;
	public static final int BASE_ROOM_OCCUPANCY_TIME = 600;
	public static final int ROOM_OCCUPANCY_EXTENSION_TIME = 300;
	public static final int MAX_TREATMENT_ROOMS = 5;
	
	HQueue hQueue;
	Clock clock;
	boolean exit;
	ArrayList<TreatmentRoom> rooms;
	
	private Supervisor(){
		hQueue = new HQueue();
		clock = new Clock();
		rooms = new ArrayList<TreatmentRoom>(MAX_TREATMENT_ROOMS);
		for(int i = 0; i < rooms.size(); i++){
			rooms.add(i, new TreatmentRoom());
		}
		exit = false;
		
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
	}
	
	public HQueue getHQueue(){
		return hQueue;
	}
}
