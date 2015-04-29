package uk.ac.qub.exjavaganza.hqbert.server.v01;

public class HQBertServer {

	/**
	 * Begins HQBert server
	 * @param args	Arguments for the server port to use and whether SSL should be used.
	 */
	public static void main(String[] args) {
		
		// The server port to be used (will be parsed from args)
		int serverPort = 0;
		// Whether or no the server will be used (will be parsed from args)
		boolean useSSL = false;
		
		// If there are arguments passed in
		if (args.length > 0) {
			try {
				// parse the
				Integer.parseInt(args[0]);
			} catch (NumberFormatException ex) {
				// Ignore the value
			}
		}
		
		// If there are arguments passed in
		if (args.length > 1) {
			// If value for use SSL is set to true
			if (args[1].equals("true")) {
				useSSL = true;
			}
		}
		
		System.out.println("Starting");

		Supervisor.INSTANCE.init(serverPort, useSSL);
		Supervisor.INSTANCE.startLoop();

	}

}
