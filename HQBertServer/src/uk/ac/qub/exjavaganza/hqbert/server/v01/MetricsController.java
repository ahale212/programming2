package uk.ac.qub.exjavaganza.hqbert.server.v01;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.sun.javafx.font.Metrics;

public enum MetricsController {

	INSTANCE;
	
	ArrayList<PatientMetrics> stats;
	ArrayList<Long> queTime;
	ArrayList<Long> treatmentTime;
	ArrayList<Long> visitTime;
	boolean emptyFile=true;
	
	ObjectOutputStream oos;
	ObjectInputStream ois;
	FileOutputStream fout;
	FileInputStream fin;
	
	private MetricsController(){
		stats = new ArrayList<PatientMetrics>();
	}
	
	public void AddMetric(PatientMetrics metrics){
		stats.add(metrics);
	}
	
	public void RemoveMetric(PatientMetrics metrics){
		stats.remove(metrics);
	}
	
	public void WriteToFile(){
		if(stats == null){
		stats= new ArrayList<PatientMetrics>();
	}
		try{		
		  fout = new FileOutputStream("src"+ File.separator + "systemoutFile.txt", true);
		  oos = new ObjectOutputStream(fout);
		  oos.writeObject(stats);
		} catch (Exception ex) {
		        ex.printStackTrace();
		}finally {
		        if(oos  != null){
		            try {
						oos.close();
					} catch (IOException e) {
						System.out.println("unable to close FileOutputStream");
					}
		         } 
		}
	}
	
	public void readFromFile(){
		
		 try {
		        fin = new FileInputStream("G:\\address.ser");
		        ois = new ObjectInputStream(fin);
		        try{
		        stats = (ArrayList<PatientMetrics>) ois.readObject();
		        } catch (Exception e){
		        	System.out.println(" error in retrieving array list");
		        }

		    } catch (Exception e) {

		        e.printStackTrace();
		 }finally {
		        if(ois != null){
		            try {
						ois.close();
					} catch (IOException e) {
						System.out.println("unable to close FileInputStream");
					}
		         } 
		 }
	}
	
	
	private ArrayList<LocalDateTime[]> getQueWaitTime(){
		
		if (stats==null ){
			readFromFile();
		} else if (stats==null){		
		}
		
		ArrayList<LocalDateTime[]> queueWaitTime = new ArrayList<LocalDateTime[]>();

		ArrayList<String> allNHSNumbers = new ArrayList<String>();

		for(PatientMetrics met: stats){
			allNHSNumbers.add(met.NHS_number);
		}

		Set<String> UniqueNHSNumbers = new HashSet<String>(allNHSNumbers);

		//get time data for all 3 occasions (enter queue, enter treatment room, leave hospital)
		LocalDateTime[] LDTA = new LocalDateTime[3];
		int count=0;

		for(String uniqueNHSNo: UniqueNHSNumbers){

			for(PatientMetrics met2: stats){
				if(uniqueNHSNo.equals(met2.NHS_number)){
					if(count<3){
						LDTA[count]=met2.dateTime;
						++count;
					}
					queueWaitTime.add(LDTA);
				}
			}
		}
		return queueWaitTime;
	}
	
	private void getWaitTimeArrays(){
		
		ArrayList<LocalDateTime[]> personTimes = getQueWaitTime();
		
		LocalDateTime Start = null, Treatment = null, end = null;
		LocalDateTime[] LDT = { Start, Treatment, end};
		
		queTime = new ArrayList<Long>();
		treatmentTime = new ArrayList<Long>();
		visitTime = new ArrayList<Long>();
		
		
		for(LocalDateTime[] times: personTimes){
			for (int i = 0; i<times.length; i++){
				times[i] = LDT[i];
			}
			queTime.add(LDT[1].toEpochSecond(null)-LDT[0].toEpochSecond(null));
			treatmentTime.add(LDT[2].toEpochSecond(null)-LDT[1].toEpochSecond(null));
			visitTime.add(LDT[2].toEpochSecond(null)-LDT[0].toEpochSecond(null));
		}
	}
	
	public long getAverageQueTime(ArrayList<Long> timeList){
		
		getWaitTimeArrays();
		
		long waitTotal=0;
		
		for(long waitTime : timeList){
			waitTotal+=waitTime;
		}
		
		return waitTotal/timeList.size();
		
	}
	
	public long getAvTimeInQue(){
		return getAverageQueTime(queTime);
		  
	}
	public long getAvTreatmentTime(){
		return getAverageQueTime(treatmentTime);
	}
	public long getAvVisitTime(){
		return getAverageQueTime(visitTime);
	}
	/**
	 * test main method remove before use
	 * @param args
	 */
	public static void main(String[] args){
		
		ArrayList<String> initialList = new ArrayList<String>();
		
		for(int i=0; i<10 ; i++){
			initialList.add("123456789");
			
		}
		for(int i=0; i<4 ; i++){
		initialList.add("fkjgdfbgr");
		}
		Set<String> hashsetList = new HashSet<String>(initialList);
		System.out.printf("%s",hashsetList);
		hashsetList.toArray();
	}
}
