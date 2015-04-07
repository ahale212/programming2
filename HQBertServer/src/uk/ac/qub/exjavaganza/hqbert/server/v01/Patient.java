package uk.ac.qub.exjavaganza.hqbert.server.v01;

import java.util.ArrayList;
import java.util.Date;


public class Patient implements Comparable<Patient> {
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
	
	
	public Patient(){
		waitTime = 0;
		//symptoms = new ArrayList<String>();
	}	

	public void incrementWaitTime(int difference){
		this.waitTime += difference;
	}
	
	@Override
	public int compareTo(Patient o) {
		int compareWait = ((Patient) o).getWaitTime(); 
		 
		//ascending order
		return this.waitTime - compareWait;
	}
	
	public int getWaitTime(){
		return waitTime;
	}
	
	public void SetUrgency(Urgency urgency){
		this.urgency = urgency;
	}
	
	public void SetPriority(boolean isPriority){
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

	/*
	public ArrayList<String> getSymptoms(){
		return symptoms;
	}
	*/
}