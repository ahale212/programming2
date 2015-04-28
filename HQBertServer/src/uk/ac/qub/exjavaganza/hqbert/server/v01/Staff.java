package uk.ac.qub.exjavaganza.hqbert.server.v01;

import java.io.Serializable;

/**
 * class for declaring staff and their details. This will be used mostly for the
 * login whereby staff input their details to successfully gain access to the
 * PAS. This will also include password encryption which has been stored in a
 * database alongside their other details. This means that if access to the
 * database is obtained without consent, the stored encryption of the password
 * is what is recorded
 * 
 * @author adamhale
 *
 */

public class Staff implements Serializable {
	/**
	 * create a private constant for the encryption key
	 */
	private static final int KEY = 4;
	// intialize vars
	private String employeeNumber;
	private String firstName;
	private String lastName;
	private String employeeUsername;
	private String employeePassword;
	private String employeeEmail;

	/**
	 * default constructor
	 */
	public Staff() {
	}// end of constructor

	/**
	 * Staff Constructor Overload with args
	 * 
	 * @param employeeUsername
	 * @param employeePassword
	 *            This is used eventually for staff logging in
	 */
	public Staff(String employeeUsername, String employeePassword) {
		this.employeeUsername = employeeUsername;
		this.employeePassword = employeePassword;
	}// end of constructor

	/**
	 * Staff Constructor with full args
	 * 
	 * @param employeeNumber
	 * @param firstName
	 * @param lastName
	 * @param employeeUsername
	 * @param employeePassword
	 * @param employeeEmail
	 */
	public Staff(String employeeNumber, String firstName, String lastName,
			String employeeUsername, String employeePassword,
			String employeeEmail) {

		setEmployeeNumber(employeeNumber);
		setFirstName(firstName);
		setLastName(lastName);
		setEmployeeUsername(employeeUsername);
		setEmployeePassword(employeePassword);
		setEmployeeEmail(employeeEmail);

	}// end of constructor

	/**
	 * getter for employee number
	 * 
	 * @return
	 */
	public String getEmployeeNumber() {
		return employeeNumber;
	}// end of getter

	/**
	 * setter for employee number
	 * 
	 * @param employeeNumber
	 */
	public void setEmployeeNumber(String employeeNumber) {
		this.employeeNumber = employeeNumber;
	}// end of setter

	/**
	 * getter for first name
	 * 
	 * @return
	 */
	public String getFirstName() {
		return firstName;
	}// end of getter

	/**
	 * setter for first name
	 * 
	 * @param firstName
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}// end of setter

	/**
	 * getter for last name
	 * 
	 * @return
	 */
	public String getLastName() {
		return lastName;
	}// end of getter

	/**
	 * setter for last name
	 * 
	 * @param lastName
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}// end of setter

	/**
	 * getter for employee username
	 * 
	 * @return
	 */
	public String getEmployeeUsername() {
		return employeeUsername;
	}// end of getter

	/**
	 * setter for employee username
	 * 
	 * @param employeeUsername
	 */
	public void setEmployeeUsername(String employeeUsername) {
		this.employeeUsername = employeeUsername;
	}// end of setter

	/**
	 * getter for employee password
	 * 
	 * @return
	 */
	public String getEmployeePassword() {
		return employeePassword;
	}// end of getter

	/**
	 * setter for employee password This contains the encryption of the password
	 * to match that of the database
	 * 
	 * @param employeePassword
	 * @return
	 */
	public String setEmployeePassword(String employeePassword) {
		// initialize var to constant previously set
		int key = KEY;
		// initialize vars
		char ch;
		String encryptedPassword = "";
		// for loop to loop through the password and change each character the
		// specificed number of places set by the constant
		for (int loop = 0; loop < employeePassword.length(); loop++) {
			// obtain next character to be encrypted
			ch = employeePassword.charAt(loop);

			// apply the key to encrypt
			ch = (char) (ch + key);

			// append encrypted character to end of string
			encryptedPassword += ch;
		}// end of for loop
		return this.employeePassword = encryptedPassword;
	}// end of setter

	/**
	 * getter for employee email note that the employee email must include an @
	 * symbol otherwise it throws and exception
	 * 
	 * @return
	 */
	public String getEmployeeEmail() {
		if (employeeEmail.contains("@")) {
			return employeeEmail;
		} else {
			throw new IllegalArgumentException(
					"Error, email is not in the correct format");
		}
	}// end of getter

	/**
	 * setter for employee email
	 * 
	 * @param employeeEmail
	 */
	public void setEmployeeEmail(String employeeEmail) {
		this.employeeEmail = employeeEmail;
	}// end of setter

}// end of class
