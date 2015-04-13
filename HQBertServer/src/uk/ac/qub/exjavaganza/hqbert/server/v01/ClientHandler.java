package uk.ac.qub.exjavaganza.hqbert.server.v01;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/** 
 * Handles the socket connection to a single client
 * @author Tom Couchman
 */
public class ClientHandler extends Thread {

	// The socket that connects the server to the client
	private Socket clientSocket;
	
	// The input and output streams that allow the server 
	// to communicate with the client
	private ObjectOutputStream output;
	private ObjectInputStream input;
	
	private boolean communicating;
	
	/**
	 * Constructor with args for the ClientHandler. 
	 * @param clientSocket	The socket that allows for communication with the client
	 */
	public ClientHandler(Socket clientSocket) {
		System.out.println("ClientHandler: Setting up client handler for connection " + clientSocket.getInetAddress().getHostName());
		// The client connection is passed in via the constructor and stored
		this.clientSocket = clientSocket;
		
		// This enables communication between the server and client, and vice versa.
		communicating = true;
	}
	
	@Override
	public void run() {
		try {
			// Set up the input and output streams so we can communicate with the client
			setUpStreams();
			
			// Listen for objects sent from the client
			while (communicating) {
				try {
					System.out.println("ClientHandler: Listening for input from client " + clientSocket.getInetAddress().getHostName());
					System.out.println((String)input.readObject());
				} catch (ClassNotFoundException e) {
					System.err.println("ClientHandler: Unknown object recieved from client.");
					e.printStackTrace();
				}
				// If the thread is interrupted, set the loop continuation condition to false
				if (Thread.interrupted()) {
					communicating = false;
				}
			}
			
		} catch (IOException ex) {
			System.err.println("IOException thrown. Connection to client terminated");
			ex.printStackTrace();
		} finally {
			// Ensure the connection to the client is closed.
			closeConnections();
		}
	}
	
	/**
	 * Send an object to the client
	 * @param object 	The object to be sent to the client
	 */
	public void sendObject(Object object) {
		System.out.println("ClientHandler: Sending object to client " + clientSocket.getInetAddress().getHostName());
		try {
			// Write the object to the output stream
			output.writeObject(object);
			// Flush the stream to ensure the object is sent.
			output.flush();
		} catch (IOException e) {
			System.err.println("ClientHandler: Object could not be sent to client " + clientSocket.getInetAddress().getHostName());
			e.printStackTrace();
		}
	}
	
	/**
	 * Sets up the input and output streams to allow the server to communicate with the client
	 * @throws IOException	Thrown if there is an issue setting up the streams
	 */
	private void setUpStreams() throws IOException {
		
		System.out.println("ClientHandler: Setting up streams to client " + clientSocket.getInetAddress().getHostName());
		try {
			// Set up the output stream
			output = new ObjectOutputStream(clientSocket.getOutputStream());

			// Flush the output stream to ensure no data is left in there
			output.flush();
			
			// Setup the input stream
			input = new ObjectInputStream(clientSocket.getInputStream());
			
			System.out.println("ClientHandler: Input and output stream setup completed for client " + clientSocket.getInetAddress().getHostName());
		} catch (IOException ex) {
			System.err.println("ClientHandler: Failed to set up streams to client " + clientSocket.getInetAddress().getHostName());
			// Re-throw the error as execution cannot continue
			throw ex;
		}
	}
	
	/**
	 * Close the input and output streams and the connection to the client
	 */
	private void closeConnections() {
		System.out.println("ClientHandler: Closing connection to client.");
		try {
			// In case the input stream failed to set up correctly ensure it is 
			// not null before closing it
			if (input != null) {
				input.close();
			}
			// In case the output stream failed to set up correctly ensure it is 
			// not null before closing it
			if (output != null) {
				output.close();
			}
			// Close the connection
			clientSocket.close();
			
			System.out.println("ClientHandler: Connection to client closed.");
		} catch (IOException e) {
			System.err.println("Failed to close connection to client " + clientSocket.getInetAddress().getHostName());
		}
	}
	
	/** 
	 * Returns whether or not the socket associated with this client handler is closed
	 * @return	Whether or no the connection is closed
	 */
	public boolean isConnectionClosed() {
		return clientSocket.isClosed();
	}
	
}
