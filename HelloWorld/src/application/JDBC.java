package application;

import java.sql*;

public class JDBC {
	/**
	 * main method to query for a database return
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		// establish connection to mySQl
		String url = "jdbc:mysql://web2.eeecs.qub.ac.uk/40058483";
		Connection con;
		PreparedStatement findStaff;
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
			findStaff = con.prepareStatement(findPatientsString);
			// execute query
			ResultSet rs1 = findStaff
					.executeQuery("SELECT * FROM  WHERE  AND  AND  ");
			// display results method - this can be changed/removed as needed -
			// only used this for the purpose of testing it worked
			displayResults(rs1);

			// start of catch block
		} catch (SQLException ex) {
			// print out the error
			System.err.println("SQLException: " + ex.getMessage());

		}// end of catch
	}// end of main method

	

}// end of class

