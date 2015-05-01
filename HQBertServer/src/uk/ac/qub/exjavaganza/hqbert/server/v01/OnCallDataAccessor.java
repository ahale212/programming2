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
public class OnCallDataAccessor {
	
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
	PreparedStatement callOnCall;
	// declare var
	String callOnCallString = "";

	public OnCallDataAccessor(String dbURL, String user, String password)
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
	public List<Staff> getOnCallList() throws SQLException {
		// start of try to initiate query statement
		try (Statement callOnCall = con.prepareStatement(callOnCallString);
				// execute query
				ResultSet rs1 = callOnCall.executeQuery("SELECT * FROM on_call ");) {
			List<Staff> onCallList = new ArrayList<>();
			while (rs1.next()) {
				 String EmployeeNumber = rs1.getString("Employee_Number");
				 String FirstName = rs1.getString("Employee_first_name");
				 String LastName = rs1.getString("Employee_last_name");
				String Username = rs1.getString("Employee_username");
				String Password = rs1.getString("Employee_Password");
				 String Email = rs1.getString("Employee_Email");
				 String MobileNumber = rs1.getString("mobile_number");
				 String employeeJob = rs1.getString("job");

				 
				Staff staff = new Staff(EmployeeNumber, FirstName, LastName, Username, Password, Email, MobileNumber);
				
				if(employeeJob.equalsIgnoreCase("doctor")){
					staff.setJob(Job.DOCTOR);
				}else if(employeeJob.equalsIgnoreCase("nurse")){
					staff.setJob(Job.NURSE);
				}else if(employeeJob.equalsIgnoreCase("admin")){
					staff.setJob(Job.ADMIN);
				}else if(employeeJob.equalsIgnoreCase("manager")){
					staff.setJob(Job.MANAGER);
				}else if(employeeJob.equalsIgnoreCase("triage Nurse")){
					staff.setJob(Job.TRIAGE_NURSE);
				}
				
				onCallList.add(staff);
			}
			// return the stafflist
			return onCallList;

		}
	}
	

	
}// 