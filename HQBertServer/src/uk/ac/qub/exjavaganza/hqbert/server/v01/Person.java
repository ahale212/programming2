package uk.ac.qub.exjavaganza.hqbert.server.v01;

import java.io.Serializable;
import java.sql.Date;
import java.util.LinkedList;

import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * class for declaring a person and their details. This will be used to display
 * the details from the database when they are brought from the front end to the
 * back end
 */
public class Person implements Serializable {

	/**
	 * default constructor
	 */
	public Person() {
	}

	/**
	 * constructor with args
	 * 
	 * @param NHSNum
	 * @param title
	 * @param firstName
	 * @param lastName
	 * @param DOB
	 * @param address
	 * @param city
	 * @param country
	 * @param postcode
	 * @param telephone
	 * @param allergies
	 * @param bloodGroup
	 * @param doctorsNotes
	 */
	public Person(String NHSNum, String title, String firstName,
			String lastName, String DOB, String address, String city,
			String country, String postcode, String telephone,
			String allergies, String bloodGroup, String doctorsNotes) {
		
		setFirstName(firstName);
		setLastName(lastName);
		setDOB(DOB);
		setTitle(title);
		setNHSNum(NHSNum);
		setAddress(address);
		setCity(city);
		setCountry(country);
		setPostcode(postcode);
		setTelephone(telephone);
		setAllergies(allergies);
		setBloodGroup(bloodGroup);
		setDoctorsNotes(doctorsNotes);
	}

	/**
	 * private var for nhs number
	 */
	private String NHSNum;
	/**
	 * private var for title
	 */
	private String title;
	/**
	 * private var for first name
	 */
	private String firstName;
	/**
	 * private var for last name
	 */
	private String lastName;
	/**
	 * private var for DOB
	 */
	private String DOB;
	/**
	 * private var for address
	 */
	private String address;
	/**
	 * private var for city
	 */
	private String city;
	/**
	 * private var for country
	 */
	private String country;
	/**
	 * private var for postcode
	 */
	private String postcode;
	/**
	 * private var for telephone
	 */
	private String telephone;
	/**
	 * private var for allergies
	 */
	private String allergies;
	/**
	 * private var for blood group
	 */
	private String bloodGroup;
	/** 
	 * Private bar storing notes that have been made about the patient by doctors 
	 */
	private String doctorsNotes;

	/**
	 * getter for nhs number
	 * 
	 * @return
	 */
	public String getNHSNum() {
		return NHSNum;
	}

	/**
	 * setter for nhs number
	 * 
	 * @param NHSNum
	 */
	public void setNHSNum(String NHSNum) {
		this.NHSNum = NHSNum;
	}

	/**
	 * getter for title
	 * 
	 * @return
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * setter for title
	 * 
	 * @param title
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * getter for first name
	 * 
	 * @return
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * setter for first name
	 * 
	 * @param firstName
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * getter for last name
	 * 
	 * @return
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * settter for last name
	 * 
	 * @param lastName
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * getter for DOB
	 * 
	 * @return
	 */
	public String getDOB() {
		return DOB;
	}

	/**
	 * setter for DOB
	 * 
	 * @param DOB
	 */
	public void setDOB(String DOB) {
		this.DOB = DOB;
	}

	/**
	 * getter for address
	 * 
	 * @return
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * setter for address
	 * 
	 * @param address
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * getter for city
	 * 
	 * @return
	 */
	public String getCity() {
		return city;
	}

	/**
	 * setter for city
	 * 
	 * @param city
	 */
	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * getter for country
	 * 
	 * @return
	 */
	public String getCountry() {
		return country;
	}

	/**
	 * setter for country
	 * 
	 * @param country
	 */
	public void setCountry(String country) {
		this.country = country;
		;
	}

	/**
	 * getter for postcode
	 * 
	 * @return
	 */
	public String getPostcode() {
		return postcode;
	}

	/**
	 * setter for postcode
	 * 
	 * @param postcode
	 */
	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}

	/**
	 * getter for telephone
	 * 
	 * @return
	 */
	public String getTelephone() {
		return telephone;
	}

	/**
	 * setter for telephone
	 * 
	 * @param telephone
	 */
	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	/**
	 * getter for allergies
	 * 
	 * @return
	 */
	public String getAllergies() {
		return allergies;
	}

	/**
	 * setter for allergies
	 * 
	 * @param allergies
	 */
	public void setAllergies(String allergies) {
		this.allergies = allergies;
	}

	/**
	 * getter for blood group
	 * 
	 * @return
	 */
	public String getBloodGroup() {
		return bloodGroup;
	}

	/**
	 * setter for bloodGroup
	 * 
	 * @param bloodGroup
	 */
	public void setBloodGroup(String bloodGroup) {
		this.bloodGroup = bloodGroup;
	}

	public String getDoctorsNotes() {
		return doctorsNotes;
	}

	public void setDoctorsNotes(String doctorsNotes) {
		this.doctorsNotes = doctorsNotes;
	}


}// end of class
