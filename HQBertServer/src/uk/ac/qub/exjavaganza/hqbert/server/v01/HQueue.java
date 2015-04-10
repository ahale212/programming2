package uk.ac.qub.exjavaganza.hqbert.server.v01;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

import javax.print.attribute.standard.Severity;

/**
 * Core class dealing with patients moving into,
 * out of, and through the queue according to
 * their waiting-time and urgency
 *
 */
public class HQueue implements Serializable {

	public static final int MAX_QUEUE_SIZE = 10;
	
	LinkedList<Patient> emergency;
	LinkedList<Patient> urgent;
	LinkedList<Patient> semiUrgent;
	LinkedList<Patient> nonUrgent;
	LinkedList<Patient> hiPriQueue;
	LinkedList<Patient> pq;
	
	int totalPatients;
	
	public HQueue(){

		urgent = new LinkedList<Patient>();
		semiUrgent = new LinkedList<Patient>();
		nonUrgent = new LinkedList<Patient>();
		
		hiPriQueue = new LinkedList<Patient>();
		pq = new LinkedList<Patient>();
		
		totalPatients = 0;
	}
	
	public void update(int deltaTime) {
		Patient p = null;
		int maxWait = Supervisor.INSTANCE.MAX_WAIT_TIME;
		
		//Update all patients in any queue
		for(int i = 0; i < pq.size(); i++){
			p = pq.get(i);
			p.incrementWaitTime(deltaTime);
		}
		
		//Check each non-priority queue for newly prioritized patients and reassign them
		for(int i = urgent.size()-1; i>=0 ; i--){
			p = urgent.get(i);
			if(p.getWaitTime() > maxWait){
				urgent.remove(i);
				hiPriQueue.add(p);
			}
		}
		for(int i = semiUrgent.size()-1; i>=0 ; i--){
			p = semiUrgent.get(i);
			if(p.getWaitTime() > maxWait){
				semiUrgent.remove(i);
				hiPriQueue.add(p);
			}
		}
		for(int i = nonUrgent.size()-1; i>=0 ; i--){
			p = nonUrgent.get(i);
			if(p.getWaitTime() > maxWait){
				nonUrgent.remove(i);
				hiPriQueue.add(p);
			}
		}

		sortHiPriQueue();
		buildPQ();
		showQueueInConsole();
	}
	
	public boolean insert(Patient patient){
		if(pq.size() >= MAX_QUEUE_SIZE
			|| patient.urgency == null){
			//Log failure
			return false;
		}
		
		Urgency urgency = patient.urgency;
		boolean priority = patient.getPriority();
		
		if(priority == false){
			if(urgency == Urgency.URGENT){
				urgent.add(patient);
			}else if(urgency == Urgency.SEMI_URGENT){
				semiUrgent.add(patient);
			}else if (urgency == Urgency.NON_URGENT){
				nonUrgent.add(patient);
			}
		}else{
			hiPriQueue.add(patient);
			sortHiPriQueue();
		}
		buildPQ();
		return true;
	}
	
	public void reQueue(Patient patient){
		if(pq.size() >= MAX_QUEUE_SIZE){
			pq.removeLast();
		}
		
		patient.SetPriority(true);
		hiPriQueue.add(patient);
		sortHiPriQueue();
		buildPQ();
	}
	
	private void buildPQ(){
		pq.clear();
		pq.addAll(hiPriQueue);
		pq.addAll(urgent);
		pq.addAll(semiUrgent);
		pq.addAll(nonUrgent);
	}
	
	private void sortHiPriQueue() {
		if (hiPriQueue.size() > 0) {
			Collections.sort(hiPriQueue, new Comparator<Patient>() {
				@Override
				public int compare(Patient p1, Patient p2) {
					// TODO Auto-generated method stub
					return p1.compareTo(p2);
				}
			});
		}
	}
	
	
	public Patient getNextPatient(){
		return pq.removeFirst();
	}
	
	public LinkedList<Patient> getHQueue(){
		return pq;
	}
	
	
	public void showQueueInConsole(){
		System.out.println("CurrentTime: "+Supervisor.INSTANCE.getCurrentTime() );
		for(int patientNum = 0; patientNum < pq.size(); patientNum++){
			Patient p = pq.get(patientNum);
			String patientQueueDetails = p.getPerson().firstName + " : "+p.getUrgency()+" : "+p.getPriority()+ " : " +p.getWaitTime();
			System.out.println(patientQueueDetails);
		}
		System.out.println("\n");
	}
}
