
package uk.ac.qub.exjavaganza.hqbert.server.v01;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

public class PatientMetrics implements Serializable{
	
	/**
	 * default serial ID
	 */
	private static final long serialVersionUID = 1L;
	LocalDateTime dateTime;
	Urgency urgency;
	String NHS_number;
	boolean priority;
	
	
	
	public PatientMetrics(LocalDateTime dateTime, Urgency urgency, String NHS_number, boolean priority){
		this.dateTime = dateTime;
		this.urgency = urgency;
		this.NHS_number = NHS_number;
		this.priority = priority;
	}
}