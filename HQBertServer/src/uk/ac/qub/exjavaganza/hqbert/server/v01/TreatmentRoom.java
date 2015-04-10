package uk.ac.qub.exjavaganza.hqbert.server.v01;

import java.util.ArrayList;

public class TreatmentRoom extends TreatmentFacility {

	public TreatmentRoom(){
		super();
	}	
	
	public void setInitialTimeToAvailable(){
		super.setTimeToAvailable(Supervisor.INSTANCE.BASE_ROOM_OCCUPANCY_TIME);
	}
	
	public void receivePatient(){
		//Log the patient's arrival in the room and the staff present at the time
		this.patient = Supervisor.INSTANCE.getHQueue().getNextPatient();
	}
	
	public void emergencyInterruption(Patient emergencyPatient){
		returnPatientToQueue();
		this.patient = emergencyPatient;
	}
	
	public void DischargePatient(){
		//Make appropriate calls to whatever admin class
		//and log patient leaving the system
		patient = null;
	}
	
	/**Patient displaced for an emergency
	 * attempt to re-admit them to the queue,
	 * or redirect them elsewhere
	 */
	public void returnPatientToQueue(){
		//Log that patient leaves treatment room
		
		//log where they went
	}
	
	public void update(int deltaTime){
		
		timeToAvailable = Math.max(0, timeToAvailable - deltaTime);
		
		if(patient != null){
			patient.incrementWaitTime(deltaTime);
			if(timeToAvailable <= 0){
				super.setTimeToAvailable(Supervisor.INSTANCE.ROOM_OCCUPANCY_EXTENSION_TIME);
			}
		}
	}
}
