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
    
    private final StringProperty NHSNum = new SimpleStringProperty(this, "NHSNum");
    public StringProperty NHSNumProperty() {
        return NHSNum ;
    }
    public final String getNHSNum() {
        return NHSNumProperty().get();
    }
    public final void setNHSNum(String NHSNum) {
        NHSNumProperty().set(NHSNum);
    }
    
    private final StringProperty title = new SimpleStringProperty(this, "title");
    public StringProperty titleProperty() {
        return title ;
    }
    public final String getTitle() {
        return titleProperty().get();
    }
    public final void setTitle(String title) {
        titleProperty().set(title);
    }
    private final StringProperty firstName = new SimpleStringProperty(this, "firstName");
    public StringProperty firstNameProperty() {
        return firstName ;
    }
    public final String getFirstName() {
        return firstNameProperty().get();
    }
    public final void setFirstName(String firstName) {
        firstNameProperty().set(firstName);
    }

    private final StringProperty lastName = new SimpleStringProperty(this, "lastName");
    public StringProperty lastNameProperty() {
        return lastName ;
    }
    public final String getLastName() {
        return lastNameProperty().get();
    }
    public final void setLastName(String lastName) {
        lastNameProperty().set(lastName);
    }

    private final StringProperty DOB = new SimpleStringProperty(this, "dob");
    public StringProperty DOBProperty() {
        return DOB ;
    }
    public final String getDOB() {
        return DOBProperty().get();
    }
    public final void setDOB(String DOB) {
        DOBProperty().set(DOB);
    }
    
    private final StringProperty address = new SimpleStringProperty(this, "address");
    public StringProperty addressProperty() {
        return address ;
    }
    public final String getAddress() {
        return addressProperty().get();
    }
    public final void setAddress(String address) {
        addressProperty().set(address);
    }
    
    private final StringProperty city = new SimpleStringProperty(this, "city");
    public StringProperty cityProperty() {
        return city ;
    }
    public final String getCity() {
        return cityProperty().get();
    }
    public final void setCity(String city) {
        cityProperty().set(city); 
    }
    
    private final StringProperty country = new SimpleStringProperty(this, "country");
    public StringProperty countryProperty() {
        return country ;
    }
    public final String getCountry() {
        return countryProperty().get();
    }
    public final void setCountry(String country) {
        countryProperty().set(country); 
        
    
    }
    
    private final StringProperty postcode = new SimpleStringProperty(this, "postcode");
    public StringProperty postcodeProperty() {
        return postcode ;
    }
    public final String getPostcode() {
        return postcodeProperty().get();
    }
    public final void setPostcode(String postcode) {
        postcodeProperty().set(postcode); 
    }
    private final StringProperty telephone = new SimpleStringProperty(this, "telephone");
    public StringProperty telephoneProperty() {
        return telephone;
    }
    public final String getTelephone() {
        return telephoneProperty().get();
    }
    public final void setTelephone(String telephone) {
        telephoneProperty().set(telephone); 
    }
    private final StringProperty  allergies= new SimpleStringProperty(this, "alergies");
    public StringProperty allergiesProperty() {
        return allergies ;
    }
    public final String getAllergies() {
        return allergiesProperty().get();
    }
    public final void setAllergies(String allergies) {
        allergiesProperty().set(allergies); 
    }
    
    private final StringProperty bloodGroup = new SimpleStringProperty(this, "blood group");
    public StringProperty bloodGroupProperty() {
        return bloodGroup ;
    }
    public final String getBloodGroup() {
        return bloodGroupProperty().get();
    }
    public final void setBloodGroup(String bloodGroup) {
        bloodGroupProperty().set(bloodGroup); 
    }
   
    
    

    
}
