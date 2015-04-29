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
	
	public static final String DRIVER = "com.mysql.jdbc.Driver";
	public static final String URL = "jdbc:mysql://web2.eeecs.qub.ac.uk/40058483";
	public static final String USERNAME = "40058483";
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

	// create a new list for person
	List<Person> personList(String nhsNumber, String firstName,
			String lastName, String dateOfBirth, String postCode,
			String telephoneNumber) throws SQLException {

		
		//start of try to initiate query statement
		try (Statement findPatients = con.prepareStatement(findPatientsString);
				// execute query
				ResultSet rs = findPatients
						.executeQuery("SELECT * FROM patients WHERE first_name = '" + firstName + "' AND last_name = '" + lastName + "'");) {
			
			resultOfQuery(rs);
		}
		
		try(Statement findPatientsWithOnlyFirstName = con.prepareStatement(findPatientWithOnlyFirstNameString);
				ResultSet rs1 = findPatientsWithOnlyFirstName.executeQuery("SELECT * FROM patients WHERE first_name = '" + firstName + "'");){
		
			resultOfQuery(rs1);
		}
		return personList(nhsNumber, firstName, lastName, dateOfBirth, postCode, telephoneNumber);
	}


	public void resultOfQuery(ResultSet rs) throws SQLException {
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

			Person person = new Person(NHSNum, title, lfirstName, llastName,
					DOB, address, city, country, postcode, telephone,
					allergies, bloodGroup, doctorsNotes);
			personList.add(person);
			
		}// end of while

	}

} // end of class

