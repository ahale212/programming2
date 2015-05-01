package uk.ac.qub.exjavaganza.hqbert.server.v01;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
/**
 * Supertype for TreatmentRoom and OnCallTeam
 * Takes in, discharges, replaces patients etc. 
 * Time until available can be extended if needed
 * @author james_admin
 *
 */
public abstract class TreatmentFacility implements ITreatmentFacility, Serializable {
	
	protected ArrayList<Staff> staff;
	protected Patient patient;
	protected int timeToAvailable;
	protected int baseOccupancyTime;
	protected int extensionTime;

	public TreatmentFacility(){
		setTimeToAvailable(0);
		staff = new ArrayList<Staff>();
	}
	
	public void assignStaff(Staff staffMember){
		staff.add(staffMember);
	}
	
	public ArrayList<Staff> getStaff(){
		return this.staff;
	}
	
	public void setTimeToAvailable(int tta){
		timeToAvailable = tta;
	}
	
	/**
	 * reduce the time until the room is available for the next patient
	 * discharge the current patient if timeToAvailable is 0
	 */
	public void update(int deltaTime){
		timeToAvailable = Math.max(0, timeToAvailable - deltaTime);
		
		if(patient != null){
			//patient.incrementWaitTime(deltaTime);
			if(timeToAvailable <= 0){
				DischargePatient();
			}
		}
	}
	
	/**
	 * Take in a new patient, remove them from the queue, 
	 * record that they went to a treatment facility, set the facility's timeToAvailable to its max
	 * @param patient
	 */
	public void receivePatient(Patient patient){
		//Log the patient's arrival in the room and the staff present at the time
		this.patient = patient;
		
		PatientMetrics metrics = new PatientMetrics(LocalDateTime.now(), patient.getUrgency(), patient.getPerson().getNHSNum(), patient.getPriority());
		MetricsController.INSTANCE.AddMetric(metrics);
		
		setTimeToAvailable(this.getBaseOccupancyTime());

		Supervisor.INSTANCE.removeFromQueue(patient);
	}
	
	/**
	 * Remove a current non-emergency patient
	 * @param emergencyPatient
	 */
	public void emergencyInterruption(Patient emergencyPatient){
		returnPatientToQueue();
		receivePatient(emergencyPatient);
	}
	
	/**
	 * Discharge patient - from the hospital
	 */
	public void DischargePatient(){

		//Make appropriate calls to whatever admin class
		//and log patient leaving the system
		PatientMetrics metrics = new PatientMetrics(LocalDateTime.now(), patient.getUrgency(), patient.getPerson().getNHSNum(), patient.getPriority());
		MetricsController.INSTANCE.AddMetric(metrics);

		logDischarge();
		patient = null;
	}
	
	public void logDischarge(){
		
	}
	
	/**Patient displaced for an emergency
	 * attempt to re-admit them to the queue,
	 * or redirect them elsewhere
	 */
	public void returnPatientToQueue(){
		patient.setPriority(true);
		//Log that patient leaves treatment room
		if(Supervisor.INSTANCE.admitPatient(patient)){
			Supervisor.INSTANCE.log(patient.getPerson().getFirstName()+" back in queue");
		}else{
			//remove least proority person from queues and send them home, re-attempt to readmit
		}
		//log where they went
	}
	
	public void extendTime(){
		setTimeToAvailable(timeToAvailable + extensionTime);
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
