package uk.ac.qub.exjavaganza.hqbert.server.v01;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.logging.Level;

import javax.print.attribute.standard.Severity;

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.LocatorEx.Snapshot;

/**
 * Core class dealing with patients moving into,
 * out of, and through the queue according to
 * their waiting-time and urgency
 *
 */
public class HQueue implements Serializable {
	
	/**
	 * Sub queues to hold each triage category of patients in the queue, 
	 * plus a subqueue for patients with special priority due to waiting time or displacement
	 */
	private LinkedList<Patient> emergency;
	private LinkedList<Patient> urgent;
	private LinkedList<Patient> semiUrgent;
	private LinkedList<Patient> nonUrgent;
	private LinkedList<Patient> hiPriQueue;
	
	/**The main, combined queue that is shown to outside classes*/
	private LinkedList<Patient> pq;
	
	/**An array of all the subQueues*/
	private LinkedList<Patient>[] allSubqueues;

	/*Similar set of lists for dealing with who gets replaced when all rooms are full and an emergency arrives*/
	private LinkedList<Patient> displacable;
	private LinkedList<Patient> dNonUrgent; 
	private LinkedList<Patient> dSemiUrgent;
	private LinkedList<Patient> dUrgent; 
	private LinkedList<Patient> dPrority; 
	
	/**
	 * Parameterless constructor
	 * initialise all the lists forming the queue and sub-queues
	 */
	public HQueue(){
		emergency = new LinkedList<Patient>();
		urgent = new LinkedList<Patient>();
		semiUrgent = new LinkedList<Patient>();
		nonUrgent = new LinkedList<Patient>();
		
		//Add the subqueues in order to match the urgency enumeration values
		allSubqueues = new LinkedList[]{emergency, urgent, semiUrgent, nonUrgent};
		hiPriQueue = new LinkedList<Patient>();
		pq = new LinkedList<Patient>();
		
		displacable = new LinkedList<Patient>();
		dNonUrgent = new LinkedList<Patient>(); 
		dSemiUrgent = new LinkedList<Patient>();
		dUrgent = new LinkedList<Patient>();    
		dPrority = new LinkedList<Patient>();   
	}
	
	/**Update the waiting time for everyone in the queue.
	 * Organize the queue: 1st sort the subqueues by time, then combine them according to triage priority, and special wait time priority
	 * @param deltaTime
	 */
	public void update(int deltaTime) {
		Patient p = null;
		//Update all patients in any queue
		for(int i = 0; i < pq.size(); i++){
			p = pq.get(i);
			p.incrementWaitTime(deltaTime);
		}
		
		//Check each non-priority queue for newly prioritized patients and reassign them
		//Start at 1 to ignore the emergency sub queue as its a special case
		for(int npqNum = 1; npqNum < allSubqueues.length; npqNum++){
			LinkedList<Patient> queue = allSubqueues[npqNum];
			for(int patientIndex = 0; patientIndex < queue.size(); patientIndex++){
				p = queue.get(patientIndex);
				if(p.getPriority() == true){
					queue.remove(p);
					hiPriQueue.add(p);
					break;
				}
			}
		}
		sortQueue(hiPriQueue);
		buildPQ();
		showQueueInConsole();
	}
	
	
	/**
	 * Change the triage urgency of a patient already in the queue
	 * @param p : the patient who's urgency you wish to change
	 * @param newUrgency : the required new level of urgency for the patient
	 */
	public void reAssignTriage(Patient p, Urgency newUrgency){
		allSubqueues[p.getUrgency().getValue()].remove(p);
		p.setUrgency(newUrgency); 
		Supervisor.INSTANCE.admitPatient(p);
	}
	
	/**Insert a patient in the appropriate sub-queue place and so, correct position in overall queue
	 * Attempt to fast track emergencies
	 * @param patient
	 * @return
	 */
	public boolean insert(Patient patient){
		Urgency urgency = patient.getUrgency();
		
		//First : if queue full and all rooms full - anyone new is sent away,
		//		even emergencies - as per Aidan's email.
		
		if(pq.size() >= Supervisor.INSTANCE.MAX_QUEUE_SIZE){
			Supervisor.INSTANCE.log("\tQueue Full");
			return false;
		}
		
		//If they are an emergency, skip the queue and attempt to send for treatment
		if(urgency == Urgency.EMERGENCY || pq.size() == 0){
			if(Supervisor.INSTANCE.sendToTreatment(patient) == true){
				return true;
			}else{ //Full of emergencies
				if(urgency == Urgency.EMERGENCY){
					Supervisor.INSTANCE.log("\tAt emergency capacity!!!\t"+patient.getPatientName()+" sent away.");
					return false;
				}
			}
		}
		
		if(pq.size() >= Supervisor.INSTANCE.MAX_QUEUE_SIZE
			|| patient.getUrgency() == null){
			//Log failure
			return false;
		}
		
		boolean priority = patient.getPriority();
		if(priority == false){
			allSubqueues[urgency.getValue()].add(patient);
		}else{
			hiPriQueue.add(patient);
			sortQueue(hiPriQueue);
		}
		buildPQ();
		return true;
	}
	
	
	/**
	 * Take all references to the patient out of all queues/subqueues
	 * @param patient
	 */
	public void removePatient(Patient patient){
		for(int i = 0; i < allSubqueues.length; i++){
			if(allSubqueues[i].contains(patient)){
				allSubqueues[i].remove(patient);
			}
		}
		if(hiPriQueue.contains(patient)){
			hiPriQueue.remove(patient);
		}
		if(pq.contains(patient)){
			pq.remove(patient);
		}
	}
	
	
	/**
	 * Put a patient displaced by a new emergency patient, back in the queue, 
	 * at the front, with high priority
	 * @param patient
	 */
	public void reQueue(Patient patient){
//		if(pq.size() >= Supervisor.INSTANCE.MAX_QUEUE_SIZE){
//			//Someone has to be sent home
//			pq.removeLast();
//		}
		patient.setPriority(true);
		if(patient.getUrgency() == Urgency.EMERGENCY){
			emergency.add(patient);
			sortQueue(emergency);

		}else{
			hiPriQueue.add(patient);
			sortQueue(hiPriQueue);
		}
		buildPQ();
	}
	
	
	/**
	 * Construct the outward facing queue from the subqueues
	 */
	private void buildPQ(){
		pq.clear();
		pq.addAll(emergency);
		pq.addAll(hiPriQueue);
		pq.addAll(urgent);
		pq.addAll(semiUrgent);
		pq.addAll(nonUrgent);
	}
	
	
	/**
	 * Arrange the patients in the passed in queue into order of waiting time
	 * @param queue
	 */
	private void sortQueue(LinkedList<Patient> queue) {
		if (queue.size() > 0) {
			Collections.sort(queue, new Comparator<Patient>() {
				@Override
				public int compare(Patient p1, Patient p2) {
					// TODO Auto-generated method stub
					return p1.compareTo(p2);
				}
			});
		}
	}
	
	
	/**
	 * Get the next patient to be treated - an emergency if there is one, 
	 * otherwise whoever is at the front of the queue
	 * @return
	 */
	public Patient getNextPatient(){
		if(emergency.size() > 0){
			return emergency.removeFirst();
		}
		if(pq.size() > 0){
			return pq.removeFirst();
		}else{
			return null;
		}
	}
	
	
	/**
	 * Get a reference to the main queue
	 * @return
	 */
	public LinkedList<Patient> getPQ(){
		return pq;
	}
	
	
	/**
	 * Show the details of the queue in the console 
	 */
	public void showQueueInConsole(){
		Supervisor.INSTANCE.log("CurrentTime: "+Supervisor.INSTANCE.getCurrentTime() );
		for(int patientNum = 0; patientNum < pq.size(); patientNum++){
			Patient p = pq.get(patientNum);
			String patientQueueDetails = p.getPerson().getFirstName() + " : "+p.getUrgency()+" : "+p.getPriority()+ " : " +p.getWaitTime();
			Supervisor.INSTANCE.log(patientQueueDetails);
		}
		Supervisor.INSTANCE.log("\n");
	}
	
	public void initDisplacable(){
		displacable.clear();
		dNonUrgent.clear();
		dSemiUrgent.clear();
		dUrgent.clear();
		dPrority.clear(); 
	}
	
	/**
	 * add a patient to the list of candidates to be displaced from treatment
	 * to accomodate an emergency.
	 * @param patient
	 */
	public void addToDisplacable(Patient patient){
		boolean priority = patient.getPriority();
		Urgency u = patient.getUrgency();
		
		if(priority == true){
			dPrority.add(patient);
		}else{
			if(u == Urgency.NON_URGENT){
				dNonUrgent.add(patient);
			}else if (u == Urgency.SEMI_URGENT){
				dSemiUrgent.add(patient);
			}else if (u == Urgency.URGENT){
				dUrgent.add(patient);
			}
		}
		
	}
	
	/**
	 * Sort the displaceable list as if it was a queue to find the most suitable candidate to displace
	 */
	public void sortDisplacable(){
		sortQueue(dNonUrgent);
		sortQueue(dSemiUrgent);
		sortQueue(dUrgent);
		sortQueue(dPrority);
		displacable.addAll(dPrority);
		displacable.addAll(dUrgent);
		displacable.addAll(dSemiUrgent);
		displacable.addAll(dNonUrgent);
	}
	
	/**
	 * 
	 * @return the most suitable patient to displace from a treatment room to accomodate an emergency
	 */
	public Patient findMostDisplacable(){
		Patient displacablePatient = null;
		
		sortDisplacable();
		try{
			displacablePatient = displacable.removeLast();
		}catch(Exception e){
			Supervisor.INSTANCE.log("No one displaceable");
		}
		initDisplacable();
		
		return displacablePatient;
	}
}
