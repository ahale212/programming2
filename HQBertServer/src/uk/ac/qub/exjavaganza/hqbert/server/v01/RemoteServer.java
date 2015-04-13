package uk.ac.qub.exjavaganza.hqbert.server.v01;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface that defines the methods that can be called from the 
 * client. The methods will be called using RMI so the interface
 * extends java.rmi.Remote
 * 
 * @author Tom Couchman
 */
public interface RemoteServer extends Remote {

	/**
	 * Registers clients for callbacks. This allows the server to communicate with clients
	 * that with to register for the times updates.
	 * @param client	The client that wishes to receive the update.
	 * @throws RemoteException	Exception thrown when an communication issue occurs during RMI
	 */
	public void registerForUpdates(ClientCallback client) throws RemoteException;
	
	/**
	 * Unregisters clients for callbacks. No more updates will be sent to the client passed in.
	 * @param client	The client that wishes to be unregistered
	 * @throws RemoteException	Exception thrown when an communication issue occurs during RMI
	 */
	public void unregisterForUpdates(ClientCallback client) throws RemoteException;
	
	/**
	 * Searches for a person by their firstName and lastName.
	 * @param firstName	The first name of the person
	 * @param lastName	The last name of the person
	 * @return	The Person that matches the passed in search terms.
	 * @throws RemoteException	Exception thrown when an communication issue occurs during RMI
	 */
	public Person searchPersonByName(String firstName, String lastName) throws RemoteException;
	
	/**
	 * Search for a person by their NHS number 
	 * @param nhsNumber		The NHS number of the person being searched for
	 * @return	The Person that matches the passed in search terms.
	 * @throws RemoteException	Exception thrown when an communication issue occurs during RMI
	 */
	public Person searchPersonByNhsNumber(int nhsNumber) throws RemoteException;
	
	/**
	 * Retrieves the current state of the queue
	 * @return	The patient queue
	 * @throws RemoteException	Exception thrown when an communication issue occurs during RMI
	 */
	public HQueue getQeue() throws RemoteException;
	
}
