package uk.ac.qub.exjavaganza.hqbert.server.v01;

import java.io.Serializable;
import java.sql.Date;
import java.util.LinkedList;


public class Person implements Serializable {
	public enum Title { Mr, Mrs, Miss, Ms, Dr, Fr, Rev, Prof, Sir, Lady, General; }
	public enum BloodGroup { APositive, ANegative , BPositive, BNegative, ABPositive, ABNegative, OPositive, ONegative };
	
	String  NHSNum;
	Title title;
	String firstName;
	String lastName;
	String dateOfBirth;
	String address1;
	String city;
	String country;
	String postcode;
	String telephoneNum;
	LinkedList<String> allergies;
	BloodGroup bloodGroup;
	
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	public String getNHSNum() {
		return NHSNum;
	}
	public void setNHSNum(String nhsNum) {
		this.NHSNum = nhsNum;
	}
}
