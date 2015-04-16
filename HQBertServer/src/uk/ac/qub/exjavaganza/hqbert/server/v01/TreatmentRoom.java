package uk.ac.qub.exjavaganza.hqbert.server.v01;

import java.util.ArrayList;

public class TreatmentRoom extends TreatmentFacility {

	private int roomNumber;
	
	public TreatmentRoom(int roomNumber){
		super();
		this.roomNumber = roomNumber;
		this.baseOccupancyTime = Supervisor.INSTANCE.BASE_ROOM_OCCUPANCY_TIME;
		this.extensionTime = Supervisor.INSTANCE.ROOM_OCCUPANCY_EXTENSION_TIME;
	}	
	
	
	public void update(int deltaTime){
		timeToAvailable = Math.max(0, timeToAvailable - deltaTime/1000);
		
		if(patient != null){
			patient.incrementWaitTime(deltaTime);
			if(timeToAvailable <= 0){
				super.setTimeToAvailable(Supervisor.INSTANCE.ROOM_OCCUPANCY_EXTENSION_TIME);
			}
		}
	}
	
	public void showFacilityInConsole(){
		int patientWaitTime = (patient != null)? patient.getWaitTime() : 0;
		String patientUrgency = (patient != null)? patient.getUrgency().toString() : "na";
		String patientName = (patient!=null)? patient.getPerson().getFirstName() : "None";
		System.out.println("Room no. : "+roomNumber+"\tPatient : "+patientName+"\tUrgency : "+patientUrgency+"\tTime to available : "+timeToAvailable+"\tPatientWaitTime : "+patientWaitTime);
	}
	
	/**
	 * Getter for the TreatmentRoom's room number
	 * @return	The room number of the TreatmentRoom
	 */
	public int getRoomNumber() {
		return this.roomNumber;
	}
}
