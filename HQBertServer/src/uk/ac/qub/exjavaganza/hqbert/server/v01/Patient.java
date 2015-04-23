package uk.ac.qub.exjavaganza.hqbert.server.v01;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;


public class Patient implements Comparable<Patient>, Serializable {
	/**Urgency assigned by triage*/
	Urgency urgency;
	
	/**A Patient waiting more than a predetermined limit gets priority
	 * regardless of urgency*/
	boolean priority;
	
	/**Date (and time) the patient entered the local system*/
	Date entryDate;
	
	/**How long since the patient entered the queue
	 * stored as seconds for future flexibility*/
	int waitTime;
	
	/**The patient object has a person object - their identity*/
	Person person;
	
	String patientName;
		
	public Patient(){
		waitTime = 0;
		//symptoms = new ArrayList<String>();
	}
	
	public Patient(Person person, Urgency urgency){
		this.setPerson(person);
		this.setPatientName(person);
		this.setUrgency(urgency);
		waitTime = 0;
		//symptoms = new ArrayList<String>();
	}

	public void incrementWaitTime(int difference){
		this.waitTime += (difference);
		checkPriority();
	}
	
	private void checkPriority(){
		if(waitTime > Supervisor.INSTANCE.MAX_WAIT_TIME){
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

	/*
	public ArrayList<String> getSymptoms(){
		return symptoms;
	}
	*/
}
