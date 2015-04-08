package uk.ac.qub.exjavaganza.hqbert.server.v01;

import java.util.Calendar;
import java.util.Date;

/**
 * Provides a central shared reference for time related functions
 *
 */
public class Clock {
	
	Calendar cal = Calendar.getInstance();
	
	Date startTime;
	Date currentTime;
	Date lastTriggerTime;
	
	int interval; 
	
	long difference;
	
	public Clock(int baseInterval) {
		try{
			interval = baseInterval * 1000;
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void initClock(){
		startTime = cal.getTime();
		lastTriggerTime = startTime;
	}
	
	public void update(){
		
		currentTime = Calendar.getInstance().getTime();
		difference = currentTime.getTime() - lastTriggerTime.getTime();
		
		if(difference  >= interval){
			lastTriggerTime = currentTime;
			Supervisor.INSTANCE.update(interval);
		}
	}
	
	public Date getCurrentTime(){
		return currentTime;
	}
}
	

