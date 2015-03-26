package uk.ac.qub.exjavaganza.hqbert.server.v01;

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
public class HQueue {

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
		for(int i = urgent.size()-1; i>=0 ; i--){
			p = urgent.get(i);
			p.incrementWaitTime(deltaTime);
			if(p.getWaitTime() > maxWait){
				urgent.remove(i);
				hiPriQueue.add(p);
			}
		}
		
	}
	
	public boolean insert(Patient patient){
		if(pq.size() >= MAX_QUEUE_SIZE){
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
	
	public boolean reQueue(Patient patient){
		if(pq.size() >= MAX_QUEUE_SIZE){
			return false;
		}
		
		patient.SetPriority(true);
		hiPriQueue.add(patient);
		sortHiPriQueue();
		buildPQ();
		
		return true;
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
	
}
