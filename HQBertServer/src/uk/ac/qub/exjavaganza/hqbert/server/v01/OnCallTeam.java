package uk.ac.qub.exjavaganza.hqbert.server.v01;

public class OnCallTeam extends TreatmentFacility {

	StaffMember doctor0;
	StaffMember nurse0;
	
	Patient patient;
	
	public OnCallTeam() {
		super();
		this.baseOccupancyTime = Supervisor.INSTANCE.ONCALL_ENGAGEMENT_TIME;
		this.extensionTime = baseOccupancyTime;
	}
	
	
	@Override
	public void update(int deltaTime) {
		super.update(deltaTime);
		
		//Maybe onCall team should go away
		if(patient == null){
			
		}
	}
	
	public void goAway(){
		
	}

	@Override
	public void showFacilityInConsole() {
		// TODO Auto-generated method stub

	}

}
