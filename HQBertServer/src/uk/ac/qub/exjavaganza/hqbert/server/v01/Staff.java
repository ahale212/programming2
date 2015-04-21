package uk.ac.qub.exjavaganza.hqbert.server.v01;

import java.io.Serializable;

public class Staff implements Serializable {

	private String employeeNumber;
	private String firstName;
	private String lastName;
	private String employeeUsername;
	private String employeePassword;
	private String employeeEmail;

	public Staff() {

	}
	
	/**
	 * Staff Constructor
	 * Overload with params username and password
	 * @param employeeUsername
	 * @param employeePassword
	 */
	public Staff(String employeeUsername, String employeePassword) { 
		this.employeeUsername = employeeUsername;
		this.employeePassword = employeePassword;
	}

	public Staff(String employeeNumber, String firstName, String lastName,
			String employeeUsername, String employeePassword,
			String employeeEmail) {

		setEmployeeNumber(employeeNumber);
		setFirstName(firstName);
		setLastName(lastName);
		setEmployeeUsername(employeeUsername);
		setEmployeePassword(employeePassword);
		setEmployeeEmail(employeeEmail);

	}

	public String getEmployeeNumber() {
		return employeeNumber;
	}

	public void setEmployeeNumber(String employeeNumber) {
		this.employeeNumber = employeeNumber;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmplolyeeUsername() {
		return employeeUsername;
	}

	public void setEmployeeUsername(String employeeUsername) {
		this.employeeUsername = employeeUsername;
	}

	public String getEmployeePassword() {
		return employeePassword;
	}

	public String setEmployeePassword(String employeePassword) {

		int key = 4;
		char ch;
		String encryptedPassword = "";

		for (int loop = 0; loop < employeePassword.length(); loop++) {
			// obtain next character to be encrypted
			ch = employeePassword.charAt(loop);

			// apply the key to encrypt
			ch = (char) (ch + key);

			// append encrypted character to end of string
			encryptedPassword += ch;
		}
		return this.employeePassword = encryptedPassword;
	}

	public String getEmployeeEmail() {
		if(employeeEmail.contains("@")){
			return employeeEmail;
		} else {
			throw new IllegalArgumentException("Error, email is not in the correct format");
		}
	}

	public void setEmployeeEmail(String employeeEmail) {
		this.employeeEmail = employeeEmail;
	}

}
