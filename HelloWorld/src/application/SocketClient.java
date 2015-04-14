package application;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import javafx.application.Application;
import uk.ac.qub.exjavaganza.hqbert.server.v01.HQueue;
import uk.ac.qub.exjavaganza.hqbert.server.v01.Patient;

/**
 * Handles the client's connect to the server and allows for communication back and forth
 * between the client and server.
 * @author Tom Couchman
 */
public class SocketClient extends Thread {
	/**
	 * The host name/ip address of the server
	 */
	private final static String SERVER_ADDRESS = "localhost";
	/** 
	 * The port on the server to connect to
	 */
	private final int SERVER_PORT = 6792;
	
	/**
	 * The socket through which the client can communicate with the server
	 */
	private Socket serverConnection;
	/**
	 * The output stream that allows the client to receive objects from the server
	 */
	private ObjectOutputStream output;
	/**
	 * The input stream that allows the client to send objects to the server.
	 */
	private ObjectInputStream input;
	
	/**
	 * Default constructor
	 */
	public SocketClient() {
	}
	
	public void run() {
	
		try {
			
			System.out.println("Client: Setting up connection");
			// Sets up the connection to the server
			serverConnection = new Socket(SERVER_ADDRESS, SERVER_PORT);
			// Sets up the input and output streams for communication with the server
			setUpStreams();

			// Wait for data from the server
			getDataFromServer();
			
		} catch (UnknownHostException ex) {
			System.err.println("Client: Could not connect to the server - host unknown.");
			ex.printStackTrace();
		} catch (IOException ex) {
			System.err.println("Client: Could not connect to the server.");
			ex.printStackTrace();
		} finally {
			// Close the streams and socket
			closeConnections();
		}
	}
	
	/**
	 * Wait for and handle the data received from the server.
	 */
	private void getDataFromServer() {
		// Listen for objects from the server
		while(true) {
			Object recievedObject;
			try {
				// Read the object from the server using the input stream
				recievedObject = input.readObject();
				
				// Check what data has been received and react accordingly
				// If the HQueue has been sent:
				if (recievedObject instanceof HQueue) {
					// Cast the object to a HQeue
					HQueue queue = (HQueue)recievedObject;
					System.out.println("Client: Object recieved");
					// Temp: testing that values can be accessed as expected
					/*for (Patient patient : queue.getHQueue()) {
						System.out.println(patient.getPerson().getFirstName());
						
						// do something with the data
					}*/
				}
				
			} catch (ClassNotFoundException ex) {
				System.err.println("Client: Unknown object recieved from server.");
				ex.printStackTrace();
			} catch (IOException ex) {
				System.err.println("Client: Failure recieveing object from server");
				ex.printStackTrace();
			}
		}

		
	}
	
	/**
	 * Sets up the input and output streams to allow the client to communicate with the server
	 * @throws IOException	Thrown if there is an issue setting up the streams
	 */
	private void setUpStreams() throws IOException {
		System.out.println("Client: Setting up input and output streams.");
		try {

			// Set up the output stream
			output = new ObjectOutputStream(serverConnection.getOutputStream());
			
			// Flush the output stream to ensure no data is left in there
			output.flush();
			
			// Setup the input stream
			input = new ObjectInputStream(serverConnection.getInputStream());
			
			System.out.println("Client: Input and output stream setup completed.");
			
		} catch (IOException ex) {
			System.err.println("Client: Failed to set up streams to server " + serverConnection.getInetAddress().getHostName());
			// Re-throw the error as execution cannot continue
			throw ex;
		}
	}
	
	/**
	 * Close the input and output streams and the connection to the server
	 */
	private void closeConnections() {
		System.out.println("Client: Closing connection to server.");
		try {
			// Before closing the connections, check that they are not null as they
			// may not have been set up correctly if an exception was thrown.
			if (input != null) {
				input.close();
			}
			if (output != null) {
				output.close();
			}
			if (serverConnection != null) {
				serverConnection.close();
			}
			
			System.out.println("Client: Connection to server closed.");
		} catch (IOException e) {
			System.err.println("Client: Failed to close connection to server " + serverConnection.getInetAddress().getHostName());
		}
	}
	
	
}
