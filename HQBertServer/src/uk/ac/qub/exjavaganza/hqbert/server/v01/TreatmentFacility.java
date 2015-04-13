package uk.ac.qub.exjavaganza.hqbert.server.v01;

import java.util.ArrayList;

public abstract class TreatmentFacility implements ITreatmentFacility {
	
	protected ArrayList<StaffMember> staff;
	protected Patient patient;
	protected int timeToAvailable;
	protected int baseOccupancyTime;
	protected int extensionTime;

	public TreatmentFacility(){
		setTimeToAvailable(0);
	}
	
	public void assignStaff(StaffMember sm){
		staff.add(sm);
	}
	
	public void setTimeToAvailable(int tta){
		timeToAvailable = tta;
	}
	
	public void update(int deltaTime){
		timeToAvailable = Math.max(0, timeToAvailable - deltaTime);
		
		if(patient != null){
			patient.incrementWaitTime(deltaTime);
			if(timeToAvailable <= 0){
				setTimeToAvailable(extensionTime);
			}
		}
	}
	
	public void receivePatient(Patient patient){
		//Log the patient's arrival in the room and the staff present at the time
		this.patient = patient;
		setTimeToAvailable(this.getBaseOccupancyTime());

		Supervisor.INSTANCE.removeFromQueue(patient);
	}
	
	public void emergencyInterruption(Patient emergencyPatient){
		returnPatientToQueue();
		receivePatient(emergencyPatient);
	}
	
	public void patientFailedToArrive(){
		//log that assigned patient not here
		//return that patient to queue / possibly a special holding list / discharge them
		//set time to available to ???
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
		patient.SetPriority(true);
		//Log that patient leaves treatment room
		if(Supervisor.INSTANCE.admitPatient(patient)){
			System.out.println(patient.getPerson().getFirstName()+" back in queue");
		}else{
			
		}
		//log where they went
	}
	
	
	public abstract void showFacilityInConsole();
	
	
	/*Getters and Setters*/
	public Patient getPatient(){
		return this.patient;
	}
	
	public int getTimeToAvailable(){
		return this.timeToAvailable;
	}
	
	public int getBaseOccupancyTime(){
		return baseOccupancyTime;
	}
	
	public int getExtensionTime(){
		return extensionTime;
	}
}
