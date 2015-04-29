package uk.ac.qub.exjavaganza.hqbert.server.v01;

/**
 * Class to create patients to send to the queue
 * @author jpt
 *
 */
public class IncidentFactory {

	/**testing/demo*/
	//private enum Severity { RANDOM, SEVERE, MODERATE, MINOR; }
	
	public Patient newPatient(boolean fullDetails){
		Patient patient = new Patient();
		Person person = new Person();
		if(fullDetails == true){
			fetchDetailsFull(person);
		}else{
			fetchDetailsPartial(person);
		}
		
		patient.setPerson(person);
		//assignSymptoms(patient, Severity.RANDOM);
		
		return patient;
	}
	
	/**
	 * takes in a person, assignes them 'ALL' details from the nhs db,
	 * then records that db entry as used;
	 * @param person
	 * @return
	 */
	private void fetchDetailsFull(Person person){
		
		
	}
	
	/**
	 * takes in a person, assignes them 'SOME' details from the nhs db,
	 * then records that db entry as used;
	 * @param person
	 * @return
	 */
	private void fetchDetailsPartial(Person person){
		
	}
	
	
	/**
	 * might be useful for testng & demo
	 * @param patient
	 * @param severity
	 *//*
	private void assignSymptoms(Patient patient, Severity severity){
		
	}*/
	
}
