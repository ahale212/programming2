package uk.ac.qub.exjavaganza.hqbert.server.v01;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.sun.javafx.font.Metrics;

public enum MetricsController {

	INSTANCE;
	
	ArrayList<PatientMetrics> stats;
	ArrayList<MetricTimeExtension> exstentions;
	ArrayList<Object> saveList;
	ArrayList<Long> queTime;
	ArrayList<Long> treatmentTime;
	ArrayList<Long> visitTime;
	int EMERGENCY;
	int URGENT;
	int SEMI_URGENT;
	int NON_URGENT;
	int patientsAddedToQueue;
	int patientsAddedToTreatmentRoom;
	int PatientsDischarged;
	
	boolean emptyFile=true;
	
	ObjectOutputStream oos;
	ObjectInputStream ois;
	FileOutputStream fout;
	FileInputStream fin;
	
	
	public ArrayList<Long> getQueTime() {
		return queTime;
	}

	public ArrayList<Long> getTreatmentTime() {
		return treatmentTime;
	}

	public ArrayList<Long> getVisitTime() {
		return visitTime;
	}
	
	public int getEMERGENCY() {
		return EMERGENCY;
	}
	public int getURGENT() {
		return URGENT;
	}
	public int getSEMI_URGENT() {
		return SEMI_URGENT;
	}
	public int getNON_URGENT() {
		return NON_URGENT;
	}
	
	public int[] getUrgencies(){
		int [] Urgencies = {EMERGENCY,URGENT,SEMI_URGENT,NON_URGENT};
		return  Urgencies;
	}

	private MetricsController(){
		stats = new ArrayList<PatientMetrics>();
		exstentions = new ArrayList<MetricTimeExtension>();
	}
	
	public void AddMetric(PatientMetrics metrics){
		stats.add(metrics);
		WriteToFile();
	}
	
	public void RemoveMetric(PatientMetrics metrics){
		stats.remove(metrics);
	}
	public void AddExtension(MetricTimeExtension metrics){
		exstentions.add(metrics);
	}
	public void RemoveExtension(MetricTimeExtension metrics){
		exstentions.remove(metrics);
	}
	
	private void makeSaveList(){
		ArrayList<Object> saveList = new ArrayList<Object>();
		saveList.add(stats);
		saveList.add(exstentions);
	}
	
	private void loadSaveList(){
		stats = (ArrayList<PatientMetrics>) saveList.get(0);
		exstentions = (ArrayList<MetricTimeExtension>) saveList.get(1);
	}
	
	public void WriteToFile(){
		makeSaveList();
		try{		
		  fout = new FileOutputStream("src"+ File.separator + "systemoutFile.txt", true);
		  oos = new ObjectOutputStream(fout);
		  oos.writeObject(saveList);
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
		        fin = new FileInputStream("src"+ File.separator + "systemoutFile.txt");
		        ois = new ObjectInputStream(fin);
		        try{
		        	saveList =  (ArrayList<Object>) ois.readObject();
		        	loadSaveList();
		        if(saveList!=null){
		        	emptyFile=false;
		        	
		        }
		        } catch (Exception e){
		        	System.out.println(" error in retrieving array list");
		        	emptyFile=true;
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
		
		if (stats==null){
			readFromFile();
		} else {
		ArrayList<LocalDateTime[]> queueWaitTime = new ArrayList<LocalDateTime[]>();
		ArrayList<String> allNHSNumbers = new ArrayList<String>();

		for(PatientMetrics met: stats){
			allNHSNumbers.add(met.NHS_number);
		}

		Set<String> UniqueNHSNumbers = new HashSet<String>(allNHSNumbers);

		//get time data for all 3 occasions (enter queue, enter treatment room, leave hospital)
		LocalDateTime[] LDTA = new LocalDateTime[3];
		
		
		
		for(String uniqueNHSNo: UniqueNHSNumbers){
			int count=0;
			for(PatientMetrics met2: stats){
				
				if(met2.NHS_number.equals(uniqueNHSNo)){
					
					if(count<3){
						LDTA[count]=met2.dateTime;
						count++;
					}
					if (count>=3) {
					queueWaitTime.add(LDTA);
					count=0;
					}
				}
			}
		}
		return queueWaitTime;
		}
		
		return null;
	}
	
	public void getWaitTimeArrays(){
		
		ArrayList<LocalDateTime[]> personTimes = getQueWaitTime();
		
		if (personTimes==null){
			throw new NullPointerException("there is no data");
		}
		LocalDateTime Start = null, Treatment = null, end = null;
		LocalDateTime[] LDT = { Start, Treatment, end};
		
		queTime = new ArrayList<Long>();
		treatmentTime = new ArrayList<Long>();
		visitTime = new ArrayList<Long>();
		
		
		for(LocalDateTime[] times: personTimes){
			for (int i = 0; i<times.length; i++){
				 LDT[i]=times[i];
			}
			queTime.add(LDT[1].toEpochSecond(ZoneOffset.UTC)-LDT[0].toEpochSecond(ZoneOffset.UTC));
			treatmentTime.add(LDT[2].toEpochSecond(ZoneOffset.UTC)-LDT[1].toEpochSecond(ZoneOffset.UTC));
			visitTime.add(LDT[2].toEpochSecond(ZoneOffset.UTC)-LDT[0].toEpochSecond(ZoneOffset.UTC));
		}
	}
	
	public long getAvTimeInQue(){
		getWaitTimeArrays();
		long waitTotal=0;
		for(long waitTime : queTime){
			waitTotal+=waitTime;
		}
		if(queTime.size()>0){
		return waitTotal/queTime.size();
		} else { 
			return 0;
		}
		  
	}
	public long getAvTreatmentTime(){
		getWaitTimeArrays();
		long waitTotal=0;
		for(long waitTime : treatmentTime){
			waitTotal+=waitTime;
		}
		if(treatmentTime.size()>0){
			return waitTotal/treatmentTime.size();
			} else { 
				return 0;
			}
	}
	public long getAvVisitTime(){
		getWaitTimeArrays();
		long waitTotal=0;
		for(long waitTime : visitTime){
			waitTotal+=waitTime;
		}
		if(visitTime.size()>0){
			return waitTotal/visitTime.size();
			} else { 
				return 0;
			}
	}
	
	public void setUrgency(){

		EMERGENCY = 0;
		URGENT = 0;
		SEMI_URGENT = 0;
		NON_URGENT = 0;
		
		if (stats==null){
			readFromFile();
		}
		ArrayList<String> allNHSNumbers = new ArrayList<String>();

		for(PatientMetrics met: stats){
			allNHSNumbers.add(met.NHS_number);
		}

		Set<String> UniqueNHSNumbers = new HashSet<String>(allNHSNumbers);

		for(String uniqueNHSNo: UniqueNHSNumbers){
			
			for(PatientMetrics met2: stats){
				if(met2.NHS_number.equals(uniqueNHSNo)){
					switch(met2.urgency){
					case EMERGENCY: EMERGENCY++;
						break;
					case URGENT: URGENT++;
						break;
					case SEMI_URGENT: SEMI_URGENT++;
						break;
					case NON_URGENT: NON_URGENT++;
						break;
					}
					break;
				}
			}
		}
	}

	public void setCurrentNumberInQue(){
		
		ArrayList<String> allNHSNumbers = new ArrayList<String>();

		for(PatientMetrics met: stats){
			allNHSNumbers.add(met.NHS_number);
		}

		Set<String> UniqueNHSNumbers = new HashSet<String>(allNHSNumbers);

		for(String NHSNumber:UniqueNHSNumbers){
			int count=0;
			for(PatientMetrics met2: stats){
				if(NHSNumber.equals(met2)){
					count++;
				}
			} 
			switch(count%3){
			case 0: patientsAddedToQueue++;
				break;
			case 1: patientsAddedToTreatmentRoom++;
				break;
			case 2: PatientsDischarged++;
				break; 
			}
		}
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
