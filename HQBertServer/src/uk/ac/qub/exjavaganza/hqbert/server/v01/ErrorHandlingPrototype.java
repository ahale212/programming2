/**
 * Alternative log class. This one uses in-house means of error handling. 
 * The log file in this case is Outfile.log. I'll need to have a word with somebody who's dealing
 * with the HQBertServer about how we want to use something like this. Please note that this 
 * class is currently exemplar in form. 
 */
package uk.ac.qub.exjavaganza.hqbert.server.v01;

import java.io.IOException;
import java.util.logging.*;

/**
 * @author Jack Ferguson
 *
 */
public class ErrorHandlingPrototype { //Class begins

	/**
	 * Logger
	 */

	public static Logger logger;

	/**
	 * Setting up the logger.
	 */
	public static void setupLogger() {
		Handler handler = null;
		try {
			handler = new FileHandler("OutFile.log");
		} catch (SecurityException | IOException e) {
			e.printStackTrace();
		}
		Logger.getLogger("").addHandler(handler);
		logger = Logger.getLogger("uk.ac.qub.week");
	}

	/**
	 * Main method
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		setupLogger();
		int number = 0;
		try {
			System.out.println("Answer : " + 10 / number);
		} catch (ArithmeticException arithmeticException) {
			logger.log(Level.WARNING, "Outputs error warning.");
		}

	}

}
