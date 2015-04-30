package uk.ac.qub.exjavaganza.hqbert.server.v01;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
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
public class PersonDataAccessor {
	
	/**
	 * constant for the SQL driver
	 */
	public static final String DRIVER = "com.mysql.jdbc.Driver";
	/**
	 * URL for the SQL database 
	 */
	public static final String URL = "jdbc:mysql://web2.eeecs.qub.ac.uk/40058483";
	/**
	 * constant for the username for the SQL database
	 */
	public static final String USERNAME = "40058483";
	/**
	 * constant for the password for the SQL database 
	 */
	public static final String PASSWORD = "VPK7789";
	

	Person person = new Person();

	// establish connection to mySQl
	
	Connection con;
	PreparedStatement findPatients, findPatientsWithNHSNum,
			findPatientsWithOnlyFirstName;
	// declare vars
	String findPatientsString = "";
	String findPatientWithNHSNumString = "";
	String findPatientWithOnlyFirstNameString = "";

	public PersonDataAccessor(String dbURL, String user, String password)
			throws SQLException, ClassNotFoundException {
		// driver name
		Class.forName(DRIVER);
		// connection to database using login name and password
		con = DriverManager.getConnection(URL, USERNAME, PASSWORD);

	}

	/**
	 * method to shut down connection Throws exception
	 * 
	 * @throws SQLException
	 */
	public void shutdown() throws SQLException {
		if (con != null) {
			con.close();
		}
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

		
		//start of try to initiate query statement
		try (Statement findPatients = con.prepareStatement(findPatientsString);
				// execute query
				ResultSet rs = findPatients
						.executeQuery("SELECT * FROM patients WHERE NHS_number LIKE '%" + nhsNumber + "%' "
										+ "AND first_name LIKE '%" +  firstName + "%' "
										+ "AND date_of_birth LIKE '%" + dateOfBirth + "%' "
										+ "AND postcode LIKE '%" + postCode + "%' "
										+ "AND telephone LIKE '%" + telephoneNumber + "%' "
									);) {
			return resultOfQuery(rs);
		}
		
		/*try(Statement findPatientsWithOnlyFirstName = con.prepareStatement(findPatientWithOnlyFirstNameString);
				ResultSet rs1 = findPatientsWithOnlyFirstName.executeQuery("SELECT * FROM patients WHERE first_name = '" + firstName + "'");){
		
			resultOfQuery(rs1);
		}*/
	
	}

	
	/**
	 * Update the patient notes for a given patient - identified by an NHS number
	 * @param nhsNumber			The NHS number of the person to be updated
	 * @param doctorsNotes		The doctors notes to be stored
	 * @throws SQLException
	 */
	public boolean updateDoctorsNotes(String nhsNumber, String doctorsNotes) throws SQLException {

		//start of try to initiate query statement
		try (Statement findPatients = con.prepareStatement(findPatientsString)) {
			// the number of rows affected by the query
			int updatedRows = 0;
			
			// Execute update to store the new doctors' notes
			updatedRows = findPatients.executeUpdate("UPDATE patients SET doctors_notes = " +  doctorsNotes
													+ " WHERE NHS_number = '"
													+ nhsNumber + "'");
			
			// Return whether the query affected rows in the db
			return (updatedRows > 0);
		}
	
	}

	
	/**
	 * Parses the ResultSet of a query and returns a List of Person objects
	 * @param rs	The RestultSet returend from the database
	 * @return		An List of Person objects
	 * @throws SQLException
	 */
	public List<Person> resultOfQuery(ResultSet rs) throws SQLException {
		List<Person> personList = new ArrayList<>();
		// instantiate the String vars to that of the database entry
		while (rs.next()) {
			String NHSNum = rs.getString("NHS_number");
			String title = rs.getString("title");
			String lfirstName = rs.getString("first_name");
			String llastName = rs.getString("last_name");
			String DOB = rs.getString("date_of_birth");
			String address = rs.getString("address_line_1");
			String city = rs.getString("city");
			String country = rs.getString("country");
			String postcode = rs.getString("postcode");
			String telephone = rs.getString("telephone");
			String allergies = rs.getString("known_allergies");
			String bloodGroup = rs.getString("blood_group");
			String doctorsNotes = rs.getString("doctors_notes");

			// Build a person object from the values in the ResultSet
			Person person = new Person(NHSNum, title, lfirstName, llastName,
					DOB, address, city, country, postcode, telephone,
					allergies, bloodGroup, doctorsNotes);
			personList.add(person);	
			
		}// end of while

		return personList;
		
	}

} // end of class

