package uk.ac.wub.exjavaganza.hqbert.server.v01;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import uk.ac.qub.exjavaganza.hqbert.server.v01.Person;

public class PersonTest {

	// declare vars needed in order to test for Strings

		String NHSNum, invalidNHSNum, title, invalidTitle, firstName,
				invalidFirstName, lastName, invalidLastName, DOB, invalidDOB,
				address, invalidAddress, city, invalidCity, country,
				invalidCountry, postcode, invalidPostcode, telephone,
				invalidTelephone, allergies, invalidAllergies, bloodGroup,
				invalidBloodGroup;
		int severity, invalidSeverity;

		@Before
		public void setUp() throws Exception {
			NHSNum = "1010101010";
			invalidNHSNum = "asda";
			title = "mr";
			invalidTitle = "as";
			firstName = "a";
			invalidFirstName = "nope";
			lastName = "b";
			invalidLastName = "nope";
			DOB = "11/11/1111";
			invalidDOB = "no";
			address = "asdasdj ad asd";
			invalidAddress = "nope";
			city = "belfast";
			invalidCity = "nope";
			country = "ni";
			invalidCountry = "no";
			postcode = "bt23 8dy";
			invalidPostcode = "no";
			telephone = "123456789";
			invalidTelephone = "no";
			allergies = "none";
			invalidAllergies = "nope";
			bloodGroup = "a";
			invalidBloodGroup = "no-";
		

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
			Person person = new Person(NHSNum, title, firstName, lastName, DOB, address,
					city, country, postcode, telephone, allergies, bloodGroup);
			// use assert equals to compare expected and actual values
			assertEquals(NHSNum, person.getNHSNum());
			// use assert equals to compare expected and actual values
			assertEquals(title, person.getTitle());
			// use assert equals to compare expected and actual values
			assertEquals(firstName, person.getFirstName());
			// use assert equals to compare expected and actual values
			assertEquals(lastName, person.getLastName());
			
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
		 * method to test the NHSNum with invalid info expecting exception
		 */
		@Test(expected = IllegalArgumentException.class)
		public void GetSetInvalidNHSNum() {
			// create object from EBook
			Person person = new Person();
			// set to invalid upper length
			person.setNHSNum(invalidNHSNum);
			// use assert equals to compare expected and actual values
			assertEquals(invalidNHSNum, person.getNHSNum());
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
		 * method to test the title with invalid info expecting exception
		 */
		@Test(expected = IllegalArgumentException.class)
		public void GetSetInvalidTitle() {
			// create object from EBook
			Person person = new Person();
			// set to invalid upper length
			person.setTitle(invalidTitle);
			// use assert equals to compare expected and actual values
			assertEquals(invalidTitle, person.getTitle());
		}

		/**
		 * method to test get and set firstName using valid info
		 */
		@Test
		public void testGetSetFirstName() {
			// create new object from Person
			Person person = new Person();
			person.setTitle(firstName);
			// use assert equals to compare expected and actual values
			assertEquals(firstName, person.getFirstName());
		}

		/**
		 * method to test the firstName with invalid info expecting exception
		 */
		@Test(expected = IllegalArgumentException.class)
		public void GetSetInvalidFirstName() {
			// create object from EBook
			Person person = new Person();
			// set to invalid upper length
			person.setTitle(invalidFirstName);
			// use assert equals to compare expected and actual values
			assertEquals(invalidFirstName, person.getFirstName());
		}

		/**
		 * method to test get and set lastName using valid info
		 */
		@Test
		public void testGetSetLastName() {
			// create new object from Person
			Person person = new Person();
			person.setTitle(lastName);
			// use assert equals to compare expected and actual values
			assertEquals(lastName, person.getLastName());
		}

		/**
		 * method to test the lastName with invalid info expecting exception
		 */
		@Test(expected = IllegalArgumentException.class)
		public void GetSetInvalidLastName() {
			// create object from EBook
			Person person = new Person();
			// set to invalid upper length
			person.setTitle(invalidLastName);
			// use assert equals to compare expected and actual values
			assertEquals(invalidLastName, person.getLastName());
		}

		/**
		 * method to test get and set address using valid info
		 */
		@Test
		public void testGetSetAddress() {
			// create new object from Person
			Person person = new Person();
			person.setTitle(address);
			// use assert equals to compare expected and actual values
			assertEquals(address, person.getAddress());
		}

		/**
		 * method to test the address with invalid info expecting exception
		 */
		@Test(expected = IllegalArgumentException.class)
		public void GetSetInvalidAddress() {
			// create object from EBook
			Person person = new Person();
			// set to invalid upper length
			person.setTitle(invalidAddress);
			// use assert equals to compare expected and actual values
			assertEquals(invalidAddress, person.getAddress());
		}

		/**
		 * method to test get and set city using valid info
		 */
		@Test
		public void testGetSetCity() {
			// create new object from Person
			Person person = new Person();
			person.setTitle(city);
			// use assert equals to compare expected and actual values
			assertEquals(city, person.getCity());
		}

		/**
		 * method to test the city with invalid info expecting exception
		 */
		@Test(expected = IllegalArgumentException.class)
		public void GetSetInvalidCity() {
			// create object from EBook
			Person person = new Person();
			// set to invalid upper length
			person.setTitle(invalidCity);
			// use assert equals to compare expected and actual values
			assertEquals(invalidCity, person.getCity());
		}

		/**
		 * method to test get and set country using valid info
		 */
		@Test
		public void testGetSetCountry() {
			// create new object from Person
			Person person = new Person();
			person.setTitle(country);
			// use assert equals to compare expected and actual values
			assertEquals(country, person.getCountry());
		}

		/**
		 * method to test the country with invalid info expecting exception
		 */
		@Test(expected = IllegalArgumentException.class)
		public void GetSetCountry() {
			// create object from EBook
			Person person = new Person();
			// set to invalid upper length
			person.setTitle(invalidCountry);
			// use assert equals to compare expected and actual values
			assertEquals(invalidCountry, person.getCountry());
		}

		/**
		 * method to test get and set postcode using valid info
		 */
		@Test
		public void testGetSetPostcode() {
			// create new object from Person
			Person person = new Person();
			person.setTitle(postcode);
			// use assert equals to compare expected and actual values
			assertEquals(postcode, person.getPostcode());
		}

		/**
		 * method to test the postcode with invalid info expecting exception
		 */
		@Test(expected = IllegalArgumentException.class)
		public void GetSetInvalidPostcode() {
			// create object from EBook
			Person person = new Person();
			// set to invalid upper length
			person.setTitle(invalidPostcode);
			// use assert equals to compare expected and actual values
			assertEquals(invalidPostcode, person.getPostcode());
		}

		/**
		 * method to test get and set telephone using valid info
		 */
		@Test
		public void testGetSetTelephone() {
			// create new object from Person
			Person person = new Person();
			person.setTitle(telephone);
			// use assert equals to compare expected and actual values
			assertEquals(telephone, person.getTelephone());
		}

		/**
		 * method to test the telephone with invalid info expecting exception
		 */
		@Test(expected = IllegalArgumentException.class)
		public void GetSetInvalidTelephone() {
			// create object from EBook
			Person person = new Person();
			// set to invalid upper length
			person.setTitle(invalidTelephone);
			// use assert equals to compare expected and actual values
			assertEquals(invalidTelephone, person.getTelephone());
		}

		/**
		 * method to test get and set allergies using valid info
		 */
		@Test
		public void testGetSetAllergies() {
			// create new object from Person
			Person person = new Person();
			person.setTitle(allergies);
			// use assert equals to compare expected and actual values
			assertEquals(allergies, person.getAllergies());
		}

		/**
		 * method to test the allergies with invalid info expecting exception
		 */
		@Test(expected = IllegalArgumentException.class)
		public void GetSetInvalidAllergies() {
			// create object from EBook
			Person person = new Person();
			// set to invalid upper length
			person.setTitle(invalidAllergies);
			// use assert equals to compare expected and actual values
			assertEquals(invalidAllergies, person.getAllergies());
		}

		/**
		 * method to test get and set bloodGroup using valid info
		 */
		@Test
		public void testGetSetBloodGroup() {
			// create new object from Person
			Person person = new Person();
			person.setTitle(bloodGroup);
			// use assert equals to compare expected and actual values
			assertEquals(bloodGroup, person.getBloodGroup());
		}

		/**
		 * method to test the blood group with invalid info expecting exception
		 */
		//@Test(expected = IllegalArgumentException.class)
		public void GetSetInvalidBloodGroup() {
			// create object from EBook
			Person person = new Person();
			// set to invalid upper length
			person.setTitle(invalidBloodGroup);
			// use assert equals to compare expected and actual values
			assertEquals(invalidBloodGroup, person.getBloodGroup());
		}

		

}
