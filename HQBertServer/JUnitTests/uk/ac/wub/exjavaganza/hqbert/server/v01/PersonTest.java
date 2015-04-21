package uk.ac.wub.exjavaganza.hqbert.server.v01;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import uk.ac.qub.exjavaganza.hqbert.server.v01.Person;

/**
 * Unit testing for Person class Coverage is 97.1%
 * 
 * @author adamhale
 *
 */
public class PersonTest {

	// declare vars needed in order to test for Strings

	String NHSNum, invalidNHSNum, title, invalidTitle, firstName,
			invalidFirstName, lastName, invalidLastName, DOB, invalidDOB,
			address, invalidAddress, city, invalidCity, country,
			invalidCountry, postcode, invalidPostcode, telephone,
			invalidTelephone, allergies, invalidAllergies, bloodGroup,
			invalidBloodGroup;
	int severity, invalidSeverity;

	/**
	 * Initialize the vars including the invalid data
	 * 
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		NHSNum = "1010101010";
		
		title = "mr";
		firstName = "a";
		lastName = "b";
		DOB = "11/11/1111";
		address = "QUB";
		city = "belfast";
		country = "NI";
		postcode = "bt23 8dy";
		telephone = "123456789";
		allergies = "none";
		bloodGroup = "a+";

	}

	/**
	 * method to test the default constructor
	 */

	@Test
	public void testPerson() {
		// create new object from Person
		Person person = new Person();
		// assert not null used to fill the default constructor with default
		// valurs
		assertNotNull(person);
	}

	/**
	 * method to test the args based constructor
	 */
	@Test
	public void testPersonStringStringStringStringStringStringStringStringStringStringStringString() {
		// create new object from the Person and fill constructor
		Person person = new Person(NHSNum, title, firstName, lastName, DOB,
				address, city, country, postcode, telephone, allergies,
				bloodGroup);
		// use assert equals to compare expected and actual values
		assertEquals(NHSNum, person.getNHSNum());
		// use assert equals to compare expected and actual values
		assertEquals(title, person.getTitle());
		// use assert equals to compare expected and actual values
		assertEquals(firstName, person.getFirstName());
		// use assert equals to compare expected and actual values
		assertEquals(lastName, person.getLastName());
		//use assert equals to compare expected and actual values
		assertEquals(DOB, person.getDOB());
		// use assert equals to compare expected and actual values
		assertEquals(address, person.getAddress());
		// use assert equals to compare expected and actual values
		assertEquals(city, person.getCity());
		// use assert equals to compare expected and actual values
		assertEquals(country, person.getCountry());
		// use assert equals to compare expected and actual values
		assertEquals(postcode, person.getPostcode());
		// use assert equals to compare expected and actual values
		assertEquals(telephone, person.getTelephone());
		// use assert equals to compare expected and actual values
		assertEquals(allergies, person.getAllergies());
		// use assert equals to compare expected and actual values
		assertEquals(bloodGroup, person.getBloodGroup());
		// use assert equals to compare expected and actual values

	}

	/**
	 * method to test get and set NHSNum using valid info
	 */
	@Test
	public void testGetSetNHSNum() {
		// create new object from Person
		Person person = new Person();
		person.setNHSNum(NHSNum);
		// use assert equals to compare expected and actual values
		assertEquals(NHSNum, person.getNHSNum());
	}

	/**
	 * method to test get and set NHSNum using valid info
	 */
	@Test
	public void testGetSetTitle() {
		// create new object from Person
		Person person = new Person();
		person.setTitle(title);
		// use assert equals to compare expected and actual values
		assertEquals(title, person.getTitle());
	}

	

	/**
	 * method to test get and set firstName using valid info
	 */
	@Test
	public void testGetSetFirstName() {
		// create new object from Person
		Person person = new Person();
		person.setFirstName(firstName);
		// use assert equals to compare expected and actual values
		assertEquals(firstName, person.getFirstName());
	}

	

	/**
	 * method to test get and set lastName using valid info
	 */
	@Test
	public void testGetSetLastName() {
		// create new object from Person
		Person person = new Person();
		person.setLastName(lastName);
		// use assert equals to compare expected and actual values
		assertEquals(lastName, person.getLastName());
	}

	

	/**
	 * method to test get and set address using valid info
	 */
	@Test
	public void testGetSetAddress() {
		// create new object from Person
		Person person = new Person();
		person.setAddress(address);
		// use assert equals to compare expected and actual values
		assertEquals(address, person.getAddress());
	}

	

	/**
	 * method to test get and set city using valid info
	 */
	@Test
	public void testGetSetCity() {
		// create new object from Person
		Person person = new Person();
		// set to valid entry
		person.setCity(city);
		// use assert equals to compare expected and actual values
		assertEquals(city, person.getCity());
	}

	

	/**
	 * method to test get and set country using valid info
	 */
	@Test
	public void testGetSetCountry() {
		// create new object from Person
		Person person = new Person();
		person.setCountry(country);
		// use assert equals to compare expected and actual values
		assertEquals(country, person.getCountry());
	}

	

	/**
	 * method to test get and set postcode using valid info
	 */
	@Test
	public void testGetSetPostcode() {
		// create new object from Person
		Person person = new Person();
		person.setPostcode(postcode);
		// use assert equals to compare expected and actual values
		assertEquals(postcode, person.getPostcode());
	}

	
	/**
	 * method to test get and set telephone using valid info
	 */
	@Test
	public void testGetSetTelephone() {
		// create new object from Person
		Person person = new Person();
		person.setTelephone(telephone);
		// use assert equals to compare expected and actual values
		assertEquals(telephone, person.getTelephone());
	}

	

	/**
	 * method to test get and set allergies using valid info
	 */
	@Test
	public void testGetSetAllergies() {
		// create new object from Person
		Person person = new Person();
		person.setAllergies(allergies);
		// use assert equals to compare expected and actual values
		assertEquals(allergies, person.getAllergies());
	}

	
	/**
	 * method to test get and set bloodGroup using valid info
	 */
	@Test
	public void testGetSetBloodGroup() {
		// create new object from Person
		Person person = new Person();
		person.setBloodGroup(bloodGroup);
		// use assert equals to compare expected and actual values
		assertEquals(bloodGroup, person.getBloodGroup());
	}

}
