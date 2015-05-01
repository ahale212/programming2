package uk.ac.qub.exjavaganza.hqbert.server.v01;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;


import uk.ac.qub.exjavaganza.hqbert.server.v01.Staff;

/**
 * JDBC class for getting staff from the database
 *
 */
public class OnCallDataAccessorDummy {


	public OnCallDataAccessorDummy(String dbURL, String user, String password)
			throws SQLException, ClassNotFoundException {

	}

	/**
	 * method to shut down connection
	 * 
	 * @throws SQLException
	 */
	public void shutdown() throws SQLException {

	}// end of shut down method

	// create a new list for staff
	public List<Staff> getOnCallList() throws SQLException {
		List<Staff> staff = new LinkedList<Staff>();
		
		Staff staffmember = new Staff("name", "other");
		
		staffmember.setJob(Job.DOCTOR);
		staff.add(staffmember);
		return staff;
					
	}
	

	
}// 