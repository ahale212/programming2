package uk.ac.qub.exjavaganza.hqbert.server.v01;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 * Class that holds details pertaining to a patient. Including a Person object that holds
 * details such as name their NHS number, name, date of birth and address. The class also holds details
 * about the patients time in the queue, their priority and notes made by doctors.
 * @author James Thompson
 * @author Ciaran Molloy
 * @autior Tom Couchman
 */
public class Patient implements Comparable<Patient>, Serializable {
	/**Urgency assigned by triage*/
	private Urgency urgency;
	
	/**A Patient waiting more than a predetermined limit gets priority
	 * regardless of urgency*/
	private boolean priority;
	
	/**Date (and time) the patient entered the local system*/
	private Date entryDate;
	
	/**How long since the patient entered the queue
	 * stored as seconds for future flexibility*/
	private int waitTime;
	
	/**The patient object has a person object - their identity*/
	private Person person;
	
	/** the concatenated name of the patient */
	private String patientName;
	
	/** The details of the patients incident */
	private String incidentDetails;

	public Patient(){
		waitTime = 0;
		//symptoms = new ArrayList<String>();
	}
	
	public Patient(Person person, Urgency urgency, String incidentDetails){
		this.setPerson(person);
		this.setPatientName(person);
		this.setUrgency(urgency);
		this.setIncidentDetails(incidentDetails);
		waitTime = 0;
		//symptoms = new ArrayList<String>();
	}

	public void incrementWaitTime(int difference){
		this.waitTime += (difference);
		if(waitTime > Supervisor.INSTANCE.PRIORITY_WAIT_TIME){
			setPriority(true);
		}
	}
	
	@Override
	public int compareTo(Patient o) {
		int compareWait = ((Patient) o).getWaitTime(); 
		 
		//ascending order
		//return this.waitTime - compareWait;
		//descending order
		return compareWait;
	}
	
	public int getWaitTime(){
		return waitTime;
	}
	
	public Urgency getUrgency(){
		return this.urgency;
	}
	
	public void setUrgency(Urgency urgency){
		this.urgency = urgency;
	}
	
	public void setPriority(boolean isPriority){
		this.priority = isPriority;
	}
	
	public Person getPerson(){
		return this.person;
	}
	
	public void setPerson(Person person){
		this.person = person;
	}
	
	public boolean getPriority(){
		return priority;
	}
	
	public String getPatientName() {
		return patientName;
	}

	public void setPatientName(Person person) {
		this.patientName = this.person.getLastName()+", "+this.person.getFirstName();
	}

	public Date getEntryDate() {
		return entryDate;
	}

	public void setEntryDate(Date entryDate) {
		this.entryDate = entryDate;
	}
	
	/** Getter for incident details
	 * @return the incident details
	 */
	public String getIncidentDetails() {
		return incidentDetails;
	}
	
	/** Setter for incident details 
	 * @param incidentDetails 	The incident details to be set
	 */
	public void setIncidentDetails(String incidentDetails) {
		this.incidentDetails = incidentDetails;
	}

}
