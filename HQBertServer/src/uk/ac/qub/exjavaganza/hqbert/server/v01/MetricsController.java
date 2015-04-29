package uk.ac.qub.exjavaganza.hqbert.server.v01;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;

import com.sun.javafx.font.Metrics;

public enum MetricsController {

	INSTANCE;
	
	ArrayList<PatientMetrics> stats;
	ObjectOutputStream oos;
	ObjectInputStream ois;
	FileOutputStream fout;
	FileInputStream fin;
	
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
	
	public int[] getQueWaitTime(){
		String tempNHSnum;
		//for each unique NHSnumber
		for(PatientMetrics met: stats){
			tempNHSnum = met.NHS_number;
			
			//get time data for all 3 occasions (enter queue, enter treatment room, leave hospital)
			LocalDateTime[] LDTA = new LocalDateTime[3];
			int count=0;
			
			for(PatientMetrics met2: stats){
				if(tempNHSnum.equals(met2.NHS_number)){
					if(count!=3){
						LDTA[count]=met2.dateTime;
						++count;
					}
					
					
				}
			}
		}
		return null;
	}
}
