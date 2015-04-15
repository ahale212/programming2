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
 * JDBC class for getting people from the database to display the searc results on the GUI
 * 
 * @author adamhale
 *
 */
public class PersonDataAccessor {

	Person person = new Person();
	/**
	 * constant linking to the front end to serach db for first name
	 */
	public final String FIRSTNAME = person.getFirstName();

	/**
	 * constant linking to the front end to search db for last name
	 */
	public final String LASTNAME = person.getLastName();
	/**
	 * constant linking to the front end to search db for NHS number
	 */
	public final String NHSNUM = person.getNHSNum();

	
	
	
	// establish connection to mySQl
	String url = "jdbc:mysql://web2.eeecs.qub.ac.uk/40058483";
	Connection con;
	PreparedStatement findPatients, findPatientsWithNHSNum;
	// declare var
	String findPatientsString = "";
	String findPatientWithNHSNumString = "";

	public PersonDataAccessor(String dbURL, String user, String password)
			throws SQLException, ClassNotFoundException {
		// driver name
		Class.forName("com.mysql.jdbc.Driver");
		// connection to database using login name and password
		con = DriverManager.getConnection(url, "40058483", "VPK7789");

	}

	/**
	 * method to shut down connection
	 * Throws exception
	 * @throws SQLException
	 */
	public void shutdown() throws SQLException {
		if (con != null) {
			con.close();
		}
	}

	List<Person> personList(String nhsNumber, String firstName,
			String lastName, String dateOfBirth, String postCode,
			String telephoneNumber) throws SQLException {

		// to be used for searching with nhs number alone, not finished yet
		// try(
		// Statement findPatientsWithNHSNum =
		// con.prepareStatement(findPatientWithNHSNumString);
		// execute query
		// ResultSet rs1 =
		// findPatientsWithNHSNum.executeQuery("SELECT * FROM patients WHERE NHS_number = '"+nhsNumber+"'");
		// ){
		
		
		
		//one currently being used to search by only names
		try (Statement findPatients = con.prepareStatement(findPatientsString);
				// execute query
				ResultSet rs = findPatients
						.executeQuery("SELECT * FROM patients WHERE first_name = '" + firstName + "' AND last_name = '" + lastName + "'");) {

			List<Person> personList = new ArrayList<>();

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

				Person person = new Person(NHSNum, title, lfirstName,
						llastName, DOB, address, city, country, postcode,
						telephone, allergies, bloodGroup);
				personList.add(person);

			}// end of while
			return personList;
		}

	}
}
// end of class

