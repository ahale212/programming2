package uk.ac.qub.exjavaganza.hqbert.server.v01;

public enum ExtensionReason {

	CHANGE_IN_PRIOROTY, SITUATION_UNRESOLVED, PATIENT_UNSTABLE, ROOM_UNREADY, STAFF_AVAILABILITY;
	
	public String toString() {
		switch(this) {
		case CHANGE_IN_PRIOROTY:
			return "Change in priority";
		case SITUATION_UNRESOLVED:
			return "Situation unresolved";
		case PATIENT_UNSTABLE:
			return "Patient unstable";
		case ROOM_UNREADY:
			return "Room not ready";
		case STAFF_AVAILABILITY:
			return "Staff availability";
		default:
			return "";
		}
	}
	
}
