package uk.ac.qub.exjavaganza.hqbert.server.v01;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.List;
import java.util.ArrayList;

import uk.ac.qub.exjavaganza.hqbert.server.v01.Staff;

/**
 * JDBC class for getting staff from the database
 *
 */
public class StaffDataAccessor {
	
	/**
	 * constant for the SQL driver 
	 */
	public static final String DRIVER = "com.mysql.jdbc.Driver";
	/**
	 * URL for the SQL database
	 */
	public static final String URL = "jdbc:mysql://web2.eeecs.qub.ac.uk/40058483";
	/**
	 * username for the SQL database
	 */
	public static final String USERNAME = "40058483";
	/**
	 * password for the SQL database
	 */
	public static final String PASSWORD = "VPK7789";
	
	Staff staff = new Staff();

	// establish connection to mySQl
;
	Connection con;
	PreparedStatement findStaff;
	// declare var
	String findStaffString = "";

	public StaffDataAccessor(String dbURL, String user, String password)
			throws SQLException, ClassNotFoundException {
		// driver name
		Class.forName(DRIVER);
		// connection to database using login name and password
		con = DriverManager.getConnection(URL, USERNAME, PASSWORD);

	}

	/**
	 * method to shut down connection
	 * 
	 * @throws SQLException
	 */
	public void shutdown() throws SQLException {
		if (con != null) {
			con.close();
		}
	}// end of shut down method

	// create a new list for staff
	List<Staff> staffList(String username, String password) throws SQLException {
		// start of try to initiate query statement
		try (Statement findStaff = con.prepareStatement(findStaffString);
				// execute query
				ResultSet rs1 = findStaff.executeQuery("SELECT * FROM staff WHERE Employee_username = '"+ username+ "' AND Employee_Password = '"+ password + "'");) {
			List<Staff> staffList = new ArrayList<>();
			while (rs1.next()) {
				// String EmployeeNumber = rs.getString("Employee_Number");
				// String FirstName = rs.getString("Employee_first_name");
				// String LastName = rs.getString("Employee_last_name");
				String Username = rs1.getString("Employee_username");
				String Password = rs1.getString("Employee_Password");
				// String Email = rs.getString("Employee_Email");

				Staff staff = new Staff(Username, Password);
				staffList.add(staff);
			}
			// return the stafflist
			return staffList;

		}
	}
}// end of class
