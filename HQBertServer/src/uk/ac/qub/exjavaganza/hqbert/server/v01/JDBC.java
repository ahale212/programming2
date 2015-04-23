/*
package uk.ac.qub.exjavaganza.hqbert.server.v01;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;



public class JDBC {
	/**
	 * main method to query for a database return
	 * 
	 * @param args
	 */
/*
 
 
	public static void runDB() {

		// establish connection to mySQl
		String url = "jdbc:mysql://web2.eeecs.qub.ac.uk/40058483";
		Connection con;
		PreparedStatement findPatients;
		// declare var
		String findPatientsString = "";

		// Loading the driver
		try {
			Class.forName("com.mysql.jdbc.Driver");
			// catch exceptions
		} catch (java.lang.ClassNotFoundException e) {
			System.err.print("ClassNotFoundException: ");
			// get error message
			System.err.println(e.getMessage());
		}

		try {
			// connection to database using login name and password
			con = DriverManager.getConnection(url, "40058483", "VPK7789");
			// initialise prepared statement
			findPatients = con.prepareStatement(findPatientsString);
			// execute query
			ResultSet rs = findPatients.executeQuery("SELECT * FROM 'patients'  ");
			// display results method - this can be changed/removed as needed -
			// only used this for the purpose of testing it worked
			//displayResults(rs1);
			
			System.out.println(rs);

			// start of catch block
		} catch (SQLException ex) {
			// print out the error
			System.err.println("SQLException: " + ex.getMessage());

			
		}// end of catch
		
		
	}// end of method
	//public static void main(String[] args) {
		//runDB();
		
	//}
	

}

*/