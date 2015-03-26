package uk.ac.qub.exjavaganza.hqbert.server.v01;

import java.sql.Date;
import java.util.LinkedList;


public class Person {
	public enum Title { Mr, Mrs, Miss, Ms, Dr, Fr, Rev, Prof, Sir, Lady; }
	public enum BloodGroup { A, B, Bn, O, On };
	Title title;
	String firstName;
	String lastName;
	String address1;
	String address2;
	String city;
	String country;
	String telephoneNum;
	LinkedList<String> allergies;
	String  nhsNum;
	BloodGroup bloodGroup;
	
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	public String getNhsNum() {
		return nhsNum;
	}
	public void setNhsNum(String nhsNum) {
		this.nhsNum = nhsNum;
	}
}
