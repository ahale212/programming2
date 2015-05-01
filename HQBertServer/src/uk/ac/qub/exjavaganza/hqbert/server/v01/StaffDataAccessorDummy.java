package uk.ac.qub.exjavaganza.hqbert.server.v01;

import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

import uk.ac.qub.exjavaganza.hqbert.server.v01.Staff;

/**
 * JDBC class for getting staff from the database
 *
 */
public class StaffDataAccessorDummy {
	

	public StaffDataAccessorDummy(String dbURL, String user, String password) throws ClassNotFoundException, SQLException {
			
	}

	/**
	 * method to shut down connection
	 * 
	 * @throws SQLException
	 */
	public void shutdown() throws SQLException {

	}// end of shut down method

	// create a new list for staff
	List<Staff> staffList(String username, String password) throws SQLException {
		
		List<Staff> staffList = new ArrayList<>();

		String Username;
		String Password;
		// String Email = rs.getString("Employee_Email");

		Staff staff = new Staff(username, password);
		staffList.add(staff);
		// return the stafflist
		return staffList;
		
	}
	
	// create a new list for staff
	Staff getStaffMemeberByUsername(String username) throws SQLException {
			Staff staff = new Staff("123123123123", "Tom", "Couchman", "my_username", "my_password", "tcouchman01@qub.ac.uk", "02302302323");
			return staff;
	}
	
	// create a new list for staff
	List<Staff> getStaffList() throws SQLException {
		List<Staff> staff = new ArrayList<Staff>();
		
		Staff staff1 = new Staff("123123123123", "Tom", "Couchman", "my_username", "my_password", "tcouchman01@qub.ac.uk", "02302302323");
		Staff staff2 = new Staff("12312312234235", "Jon", "Hello", "no_no", "ok", "tcouchman01@qub.ac.uk", "02302302323");
		
		staff.add(staff1);
		staff.add(staff2);
		
		return staff;
		
	}
	
	
}// end of class
