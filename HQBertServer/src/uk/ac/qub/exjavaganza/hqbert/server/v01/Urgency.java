package uk.ac.qub.exjavaganza.hqbert.server.v01;

import java.io.Serializable;

public enum Urgency  implements Serializable {
	EMERGENCY(0),
	URGENT(1),
	SEMI_URGENT(2),
	NON_URGENT(3);
	
	private final int value;
	
	private Urgency(int value){
		this.value = value;
	}
	
	public int getValue(){
		return this.value;
	}
}
