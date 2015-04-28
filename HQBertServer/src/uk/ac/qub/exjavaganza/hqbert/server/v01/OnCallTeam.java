package uk.ac.qub.exjavaganza.hqbert.server.v01;

public class OnCallTeam extends TreatmentFacility {
	
	
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
	
	@Override
	public void receivePatient(Patient patient){
		super.receivePatient(patient);
	}
	
	@Override
	public void DischargePatient(){
		super.DischargePatient();
	}
	
	public void goAway(){
		
	}

	@Override
	public void showFacilityInConsole() {
		
		int patientWaitTime = (patient != null)? patient.getWaitTime() : 0;
		String patientUrgency = (patient != null)? patient.getUrgency().toString() : "na";
		String patientName = (patient!=null)? patient.getPerson().getFirstName() : "None";
		System.out.println("On-call Team : "+"\tPatient : "+patientName+"\tUrgency : "+patientUrgency+"\tTime to available : "+timeToAvailable+"\tPatientWaitTime : "+patientWaitTime);
	}
	

}
