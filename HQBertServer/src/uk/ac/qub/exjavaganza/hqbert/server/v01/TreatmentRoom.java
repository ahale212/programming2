package uk.ac.qub.exjavaganza.hqbert.server.v01;

import java.util.ArrayList;

public class TreatmentRoom {
	
	ArrayList<StaffMember> staff;
	Patient patient;
	int timeToAvailable;
	
	public TreatmentRoom(){
		timeToAvailable = 0;
	}
	
	public void assignStaff(StaffMember sm){
		staff.add(sm);
	}
	
	public void setInitialTimeToAvailable(){
		timeToAvailable = Supervisor.BASE_ROOM_OCCUPANCY_TIME;
	}
	
	public void extendTimeToAvailable(){
		timeToAvailable += Supervisor.ROOM_OCCUPANCY_EXTENSION_TIME;
	}
	
	public void receivePatient(Patient patient){
		//Log the patient's arrival in the room and the staff present at the time
		this.patient = patient;
	}
	
	public void emergencyInterruption(Patient emergencyPatient){
		returnPatientToQueue();
		receivePatient(emergencyPatient);
	}
	
	public void DischargePatient(){
		//Make appropriate calls to whatever admin class
		//and log patient leaving the system
		patient = null;
	}
	
	/**Patient displaced for an emergency
	 * attempt to readmit them to the queue,
	 * or redirect them elsewhere
	 */
	public void returnPatientToQueue(){
		//Log that patient leaves treatmentroom
		if(Supervisor.INSTANCE.hQueue.reQueue(patient) == false){
			//queue full - redirect from hospital
		}
		//log where they went
	}
	
	public void update(int deltaTime){
		if(timeToAvailable > 0){
			timeToAvailable = Math.max(0, timeToAvailable - deltaTime);
		}
		if(patient != null){
			patient.incrementWaitTime(deltaTime);
		}
	}
}
