package uk.ac.qub.exjavaganza.hqbert.server.v01;

import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
import uk.ac.qub.exjavaganza.hqbert.server.v01.Person;

/**
 * JDBC class for getting people from the database to display the search results
 * on the GUI
 * 
 * @author adamhale
 *
 */
public class PersonDataAccessorDummy {


	// establish connection to mySQl


	public PersonDataAccessorDummy(String dbURL, String user, String password)
			throws SQLException, ClassNotFoundException {


	}

	/**
	 * method to shut down connection Throws exception
	 * 
	 * @throws SQLException
	 */
	public void shutdown() throws SQLException {

	}// end of shutdown method

	/**
	 * Search the database for a list of people matching the various criteria passed in.
	 * @param nhsNumber
	 * @param firstName
	 * @param lastName
	 * @param dateOfBirth
	 * @param postCode
	 * @param telephoneNumber
	 * @return A list of Person objects that match the search criteria
	 * @throws SQLException
	 */
	public List<Person> personList(String nhsNumber, String firstName,
			String lastName, String dateOfBirth, String postCode,
			String telephoneNumber) throws SQLException {

		List<Person> people = new ArrayList<Person>();
		people.add(new Person("1010101010", "Brian", "Bobob", "01/06/1991", "Over there" ,  "city", "country", "postcode","..","..","..","..",".."));		
		return people;
	}

	
	/**
	 * Update the patient notes for a given patient - identified by an NHS number
	 * @param nhsNumber			The NHS number of the person to be updated
	 * @param doctorsNotes		The doctors notes to be stored
	 * @throws SQLException
	 */
	public boolean updateDoctorsNotes(String nhsNumber, String doctorsNotes) throws SQLException {

		return true;
	
	}



} // end of class

