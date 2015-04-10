package uk.ac.qub.exjavaganza.hqbert.server.v01;

import java.util.ArrayList;

public abstract class TreatmentFacility implements ITreatmentFacility {
	protected ArrayList<StaffMember> staff;
	protected Patient patient;
	protected int timeToAvailable;
	
	public TreatmentFacility(){
		timeToAvailable = 0;
	}
	
	public void assignStaff(StaffMember sm){
		staff.add(sm);
	}
	
	public void setTimeToAvailable(int tta){
		timeToAvailable = tta;
	}
	
	public void update(int deltaTime, int extensionTime){
		timeToAvailable = Math.max(0, timeToAvailable - deltaTime);
		
		if(patient != null){
			patient.incrementWaitTime(deltaTime);
			if(timeToAvailable <= 0){
				setTimeToAvailable(extensionTime);
			}
		}
	}
}
