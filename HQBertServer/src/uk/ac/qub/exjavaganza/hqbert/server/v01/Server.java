package uk.ac.qub.exjavaganza.hqbert.server.v01;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Server that listens for client connections and runs a new ClientServer thread to handle
 * communication with each.
 * @author Tom Couchman
 */
public class Server extends Thread {
	/** The port that the server uses to communicate with clients */
	public final int CONNECTION_PORT = 6792;
	/** The ServerSocket that listens for client connection requests */
	private ServerSocket serverSocket;
	
	private ArrayList<ClientHandler> clients;

	/** 
	 * Default constructor for the server 
	 */
	public Server() {
		// Initialise the array list of clients
		clients = new ArrayList<ClientHandler>();
	}
	
	public void run(){

		try {
			// set up the server socket on the port defined in CONNECTION_PORT.
			serverSocket = new ServerSocket(CONNECTION_PORT);
			System.out.println("Server: Server set up on port " + CONNECTION_PORT);
		} catch (IOException ex) { // Thrown when the initialisation of the ServerSocket fails
			System.err.println("Failed to set up server socket on port: " + CONNECTION_PORT);
			ex.printStackTrace();
		}

		try {
			// Loop indefinitely
			while (true) {
				System.out.println("Server: Waiting for clients...");
				// Accept any clients that try to connect
				ClientHandler clientHandler = new ClientHandler(serverSocket.accept());
				// And the clientHandler array to the clients list so they can be accessed later
				clients.add(clientHandler);
				// Start the client handler on a new thread
				clientHandler.start();
				System.out.println("Server: Client Connected. ClientServer thread started.");
			}
			
		} catch (IOException ex) {
			// Thrown when an IO issue occurs while waiting for a connection
			System.err.println("Server: Could not establish connection to client.");
			ex.printStackTrace();
		} finally {
			// Ensure that the connection is closed before the program exists.
			closeConnection();
		}
	}
	
	/**
	 * Closes the server socket
	 */
	private void closeConnection() {
		System.out.println("Server: Closing server socket.");
		try {
			// Close the server socket
			serverSocket.close();
			System.out.println("Server: Server socket closed.");
		} catch (IOException ex) {
			System.err.println("Server: Failed to close server socket.");
			ex.printStackTrace();
		}
	}
	
	/**
	 * Send an object to each of the clients
	 * @param object	The object to be sent
	 */
	public void sendObject(Object object) {
		// Loop through each of the client
		for (ClientHandler client : clients) {
			if (client.isConnectionClosed()) {
				endConnection(client);
			} else {
				// Send the object to the client
				client.sendObject(object);
			}
		}
	}
	
	/** 
	 * Interrupt the clientHandler thread and remove it from the clients list.
	 * @param client
	 */
	public void endConnection(ClientHandler client) {
		client.interrupt();
		clients.remove(client);
	}

}
