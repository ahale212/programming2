package uk.ac.qub.exjavaganza.hqbert.server.v01;

import java.util.Calendar;
import java.util.Date;

/**
 * Provides a central shared reference for time related functions
 *
 */
public class Clock {
	
	Calendar cal = Calendar.getInstance();
	
	float multi = Supervisor.INSTANCE.TIME_MULTI;
	
	Date startTime;
	Date currentTime;
	Date lastTriggerTime;
	
	int interval; 
	
	long difference;
	
	public Clock(int baseInterval) {
		try{
			interval = baseInterval * 1000 * 60;
			startTime = cal.getTime();
			lastTriggerTime = startTime;
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
		
		if(difference * multi >= interval){
			lastTriggerTime = currentTime;
			float dtFloat = ((difference*multi)/1000)/60;
			int dtInt = (int)dtFloat;
			
			Supervisor.INSTANCE.update(dtInt);
		}
		//System.out.println("Curr: "+currentTime+"\tlast: "+lastTriggerTime+"\tDiff: "+difference+"\tinterval: "+interval+"\tmutlti: "+multi);
	}
	
	public Date getCurrentTime(){
		return currentTime;
	}
}
	

