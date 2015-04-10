package uk.ac.qub.exjavaganza.hqbert.server.v01;

public interface ITreatmentFacility {
	
	public void setTimeToAvailable(int tta);
	
	public void assignStaff(StaffMember sm);
	
	public void update(int deltaTime);
}
