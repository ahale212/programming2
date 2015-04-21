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

	Staff staff = new Staff();
	

	// establish connection to mySQl
	String url = "jdbc:mysql://web2.eeecs.qub.ac.uk/40058483";
	Connection con;
	PreparedStatement findStaff;
	// declare var
	String findStaffString = "";

	public StaffDataAccessor(String dbURL, String user, String password)
			throws SQLException, ClassNotFoundException {
		// driver name
		Class.forName("com.mysql.jdbc.Driver");
		// connection to database using login name and password
		con = DriverManager.getConnection(url, "40058483", "VPK7789");

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
	}

	
	List<Staff> staffList(String username,  String password) throws SQLException {
/*
		try (Statement findPatients = con.prepareStatement(findStaffString);
				// execute query
				ResultSet rs = findStaff
						.executeQuery("SELECT Employee_Number FROM staff WHERE '"+ username + "' AND '"+ password +"'");) {
			List<Staff> staffList = new ArrayList<>();
			//"SELECT Employee_Number FROM staff WHERE '"+ username + "' AND '"+ password +"'");
			while (rs.next()) {
				String EmployeeNumber = rs.getString("Employee_Number");
				String FirstName = rs.getString("Employee_first_name");
				String LastName = rs.getString("Employee_last_name");
				String Username = rs.getString("Employee_username");
				String Password = rs.getString("Employee_Password");
				String Email = rs.getString("Employee_Email");

				Staff staff = new Staff(EmployeeNumber, FirstName, LastName,
						Username, Password, Email);
				staffList.add(staff);
			}
			return staffList;
		}

		
	}*/

	
		// TODO Auto-generated method stub
		try (Statement findStaff = con.prepareStatement(findStaffString);
				// execute query
			ResultSet rs1 = findStaff.executeQuery("SELECT * FROM staff WHERE Employee_username = '" + username + "' AND Employee_Password = '" + password + "'");) {
			List<Staff> staffList = new ArrayList<>();
			//"SELECT Employee_Number FROM staff WHERE '"+ username + "' AND '"+ password +"'");
			while (rs1.next()) {
				//String EmployeeNumber = rs.getString("Employee_Number");
				//String FirstName = rs.getString("Employee_first_name");
				//String LastName = rs.getString("Employee_last_name");
				String Username = rs1.getString("Employee_username");
				String Password = rs1.getString("Employee_Password");
				//String Email = rs.getString("Employee_Email");

				Staff staff = new Staff(Username, Password);
				staffList.add(staff);
			}
			return staffList;
					
	}
}
}
