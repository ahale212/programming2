package uk.ac.wub.exjavaganza.hqbert.server.v01;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import uk.ac.qub.exjavaganza.hqbert.server.v01.Staff;

/**
 * Unit test for class Staff
 * 
 * @author adamhale
 *
 */
public class StaffTest {

	// vars needed to test Staff class
	String employeeNumber, invalidEmployeeNumber, firstName, invalidFirstName,
			lastName, invalidLastName, employeeUsername,
			invalidEmployeeUsername, employeePassword, invalidEmployeePassword,
			employeeEmail, invalidEmployeeEmail, mobileNumber;

	/**
	 * method to initialize the vars needed to test Staff class
	 * 
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		// vars for each string needed to test Staff class
		employeeNumber = "11111";
		invalidEmployeeNumber = "invalid";
		firstName = "a";
		invalidFirstName = "invalid";
		lastName = "b";
		invalidLastName = "invalid";
		employeeUsername = "tester01";
		invalidEmployeeUsername = "invalid";
		employeePassword = "password";
		invalidEmployeePassword = "invalid";
		employeeEmail = "test@test.com";
		invalidEmployeeEmail = "invalid";
		mobileNumber = "+447938812243";

	}

	/**
	 * method testing the default constructor
	 */
	@Test
	public void testStaff() {
		Staff staff = new Staff();
		assertNotNull(staff);
	}

	/**
	 * method testing the constructor with args, including the encryption for
	 * password
	 */
	@Test
	public void testStaffStringStringStringStringStringString() {
		Staff staff = new Staff(employeeNumber, firstName, lastName,
				employeeUsername, employeePassword, employeeEmail, mobileNumber);
		assertEquals(employeeNumber, staff.getEmployeeNumber());
		assertEquals(firstName, staff.getFirstName());
		assertEquals(lastName, staff.getLastName());
		assertEquals(employeeUsername, staff.getEmployeeUsername());
		assertEquals(mobileNumber, staff.getMobileNumber());
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
		this.employeePassword = encryptedPassword;
		assertEquals(employeePassword, staff.getEmployeePassword());
		assertEquals(employeeEmail, staff.getEmployeeEmail());
	}

	/**
	 * method testing getters and setters for employee number
	 */
	@Test
	public void testGetSetEmployeeNumber() {
		Staff staff = new Staff();
		staff.setEmployeeNumber(employeeNumber);
		assertEquals(employeeNumber, staff.getEmployeeNumber());
	}

	/**
	 * method testing getters and setters for first name
	 */
	@Test
	public void testGetSetFirstName() {
		Staff staff = new Staff();
		staff.setFirstName(firstName);
		assertEquals(firstName, staff.getFirstName());
	}

	/**
	 * method testing getters and setters for last name
	 */
	@Test
	public void testGetSetLastName() {
		Staff staff = new Staff();
		staff.setLastName(lastName);
		assertEquals(lastName, staff.getLastName());
	}

	/**
	 * method testing getters and setters for username
	 */
	@Test
	public void testGetSetEmployeeUserName() {
		Staff staff = new Staff();
		staff.setEmployeeUsername(employeeUsername);
		assertEquals(employeeUsername, staff.getEmployeeUsername());
	}

	/**
	 * method testing getters and setters for password using the encryption
	 */
	@Test
	public void testGetSetEmployeePassword() {
		Staff staff = new Staff();
		staff.setEmployeePassword(employeePassword);

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
		this.employeePassword = encryptedPassword;
		assertEquals(employeePassword, staff.getEmployeePassword());
	}

	/**
	 * method testing getters and setters for email
	 */
	@Test
	public void testGetSetEmployeeEmail() {
		Staff staff = new Staff();
		staff.setEmployeeEmail(employeeEmail);
		assertEquals(employeeEmail, staff.getEmployeeEmail());
	}

	/**
	 * method to test getters and setters for invalid email, expecting exception
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testInvalidGetSetEmployeeEmail() {
		Staff staff = new Staff();
		staff.setEmployeeEmail(invalidEmployeeEmail);
		assertEquals(invalidEmployeeEmail, staff.getEmployeeEmail());
	}

	/**
	 * method testing getters and setters for mobile number
	 */
	@Test
	public void testGetSetMobileNumber() {
		Staff staff = new Staff();
		staff.setMobileNumber(mobileNumber);
		assertEquals(mobileNumber, staff.getMobileNumber());
	}
}
