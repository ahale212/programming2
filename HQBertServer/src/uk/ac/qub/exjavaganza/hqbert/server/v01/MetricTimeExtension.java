package uk.ac.qub.exjavaganza.hqbert.server.v01;

import java.io.Serializable;
import java.time.LocalDateTime;

public class MetricTimeExtension implements Serializable{

	private static final long serialVersionUID = 2L;
	LocalDateTime dateTime;
	ExtensionReason reason;
	
	public MetricTimeExtension (LocalDateTime dateTime, ExtensionReason reason){
		this.dateTime = dateTime;
		this.reason = reason;
	}
}
