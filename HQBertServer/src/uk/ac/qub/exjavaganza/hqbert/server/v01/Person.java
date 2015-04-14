package uk.ac.qub.exjavaganza.hqbert.server.v01;

import java.io.Serializable;
import java.sql.Date;
import java.util.LinkedList;
import javafx.beans.property.StringProperty ;
import javafx.beans.property.SimpleStringProperty ;

public class Person implements Serializable {
	
	/**
	 * default constructor
	 */
	public Person() {}
/**
 * constructor with args
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
 */
    public Person(String NHSNum,String title, String firstName, String lastName, String DOB, String address, String city, String country, String postcode, String telephone, String allergies, String bloodGroup) {
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
    }
    
    private String NHSNum;

    public String getNHSNum() {
        return NHSNum;
    }
    public void setNHSNum(String NHSNum) {
        this.NHSNum = NHSNum;
    }
    
    private String title;

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
    	this.title = title;
    }
    private String firstName;

    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    private String lastName;

    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    private String DOB;

    public String getDOB() {
        return DOB;
    }
    public void setDOB(String DOB) {
       this.DOB = DOB;
    }
    
    private String address;

    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    
    private String city;

    public String getCity() {
        return city;
    }
    public void setCity(String city) {
    	this.city = city; 
    }
    
    private String country;

    public String getCountry() {
        return country;
    }
    public void setCountry(String country) {
        this.country = country;; 
    }
    
    private String postcode;

    public String getPostcode() {
        return postcode;
    }
    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }
    private String telephone;

    public String getTelephone() {
        return telephone;
    }
    public void setTelephone(String telephone) {
        this.telephone = telephone; 
    }
    
    
    private String allergies;

    public String getAllergies() {
        return allergies;
    }
    public void setAllergies(String allergies) {
        this.allergies = allergies;
    }
    
    private String bloodGroup;

    public String getBloodGroup() {
        return bloodGroup;
    }
    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }
   
    
    

    
}
