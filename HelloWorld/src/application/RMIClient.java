package application;

import java.io.Closeable;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;

import uk.ac.qub.exjavaganza.hqbert.server.v01.ClientCallback;
import uk.ac.qub.exjavaganza.hqbert.server.v01.HQueue;
import uk.ac.qub.exjavaganza.hqbert.server.v01.Patient;
import uk.ac.qub.exjavaganza.hqbert.server.v01.RemoteServer;

/**
 * The client calls that interacts with the server using RMI (Remote Method Invocation).
 * It also implements the methods defined in the ClientCallback interface, which can be 
 * called by the server using a callback.
 * 
 * @author Tom Couchman
 */
public class RMIClient extends UnicastRemoteObject implements ClientCallback, AutoCloseable {

	/**
	 * A reference to the remote server
	 */
	private RemoteServer server;
	/**
	 * The URI/IP of the server
	 */
	private String serverAddress = "localhost";
	/**
	 * The port of the server
	 */
	private int serverPort = 1099;
	
	/**
	 * Constructor for RMIClient
	 * @throws RemoteException	Exception thrown when an communication issue occurs during RMI
	 */
	protected RMIClient() throws RemoteException {
		super();
		
		try {
			// Get a reference to the server stub using a RMI URL built comprising of the server address and port 
			server = (RemoteServer)Naming.lookup("rmi://" + serverAddress + ":" + serverPort + "/HQBertServer");
			
			// Register for the update callbacks. This passes a reference
			// of the client to the server so the 'update' method can be called remotely.
			//server.registerForUpdates(this);
			
			System.out.println("Connected to server and registered for updates.");
			
		} catch (MalformedURLException | NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Callback method defined in the ClientCallback interface. This method can be 
	 * invoked remotely from the server as a callback, whenever the queue is updated.
	 * It updates the local state of the queue to match the remote changes.
	 * 
	 * @param queue		The updated patient queue sent from the server
	 * @throws RemoteException	Exception thrown when an communication issue occurs during RMI
	 */
	@Override
	public void udpate(LinkedList<Patient> queue) throws RemoteException {
		System.out.println("Updating");
	}
	
	/**
	 * Informs the server that the client no longer wished to receive updates.
	 */
	@Override
	public void close() {
		try {
			// Unregister for updates
			server.unregisterForUpdates(this);
		} catch (RemoteException e) {
			System.err.println("Failed to unregister from server updates.");
			e.printStackTrace();
		}
	}
	
	public RemoteServer getServer() {
		return this.server;
	}

}
