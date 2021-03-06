package uk.ac.qub.exjavaganza.hqbert.server.v01;
/**
 * The onCall team extends TreatmentFacility and behaves very similarly to one,
 * taking/discharging/displacing patients etc.
 * Can be extended if there is a real-world interface to do so
 * 
 * @author james_thompson
 */
public class OnCallTeam extends TreatmentFacility {
	
	/**
	 * default constructor, sets default time values
	 */
	public OnCallTeam() {
		super();
		this.baseOccupancyTime = Supervisor.INSTANCE.ONCALL_ENGAGEMENT_TIME;
		this.extensionTime = baseOccupancyTime;
	}
	
	
	@Override
	public void update(int deltaTime) {
		super.update(deltaTime);
	}
	
	@Override
	public void receivePatient(Patient patient){
		super.receivePatient(patient);
	}
	
	@Override
	public void DischargePatient(){
		super.DischargePatient();
		Supervisor.INSTANCE.onCallTryLeave();
	}
	
	@Override
	public void logDischarge(){
		String message = patient.getPerson().getFirstName() + " " + patient.getPerson().getLastName() + " has left the on-call team";
		Supervisor.INSTANCE.log(message);
	}
	
	@Override
	public void showFacilityInConsole() {
		int patientWaitTime = (patient != null)? patient.getWaitTime() : 0;
		String patientUrgency = (patient != null)? patient.getUrgency().toString() : "na";
		String patientName = (patient!=null)? patient.getPerson().getFirstName() : "None";
		System.out.println("On-call Team : "+"\tPatient : "+patientName+"\tUrgency : "+patientUrgency+"\tTime to available : "+timeToAvailable+"\tPatientWaitTime : "+patientWaitTime);
	}
}
