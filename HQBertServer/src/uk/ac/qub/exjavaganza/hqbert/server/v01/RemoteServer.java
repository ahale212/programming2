package uk.ac.qub.exjavaganza.hqbert.server.v01;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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
	public List<Person> searchPersonByDetails(String nhsNumber, String firstName, String lastName, String dateOfBirth, String postCode, String telephoneNumber) throws RemoteException;
	

	/**
	 * Retrieves the current state of the queue
	 * @return	The patient queue
	 * @throws RemoteException	Exception thrown when an communication issue occurs during RMI
	 */
	public LinkedList<Patient> getQeue() throws RemoteException;
	
	/**
	 * Gets the current state of the treatment rooms.
	 * @return The list of treatment rooms
	 * @throws RemoteException	Exception thrown when an communication issue occurs during RMI
	 */
	public ArrayList<TreatmentFacility> getTreatmentRooms() throws RemoteException;
	
	/**
	 * Adds a newly triaged emergency patient to the backend list along with the details of their current state.
	 * @throws RemoteException	Exception thrown when an communication issue occurs during RMI
	 */
	public void addPrimaryPatient(Person person, boolean airway, boolean breating, boolean spine, boolean circulation, boolean disability, boolean exposure) throws RemoteException;
	
	/**
	 * Adds a newly triaged non-emergency patient to the backend list along with the details of their current state.
	 * @throws RemoteException	Exception thrown when an communication issue occurs during RMI
	 */
	public void addSecondaryPatient(Person person, Urgency urgency, boolean breathingWithoutResusitation, boolean canWalk, int respirationRate, int pulseRate, String underlyingCondition, String prescribedMedication) throws RemoteException;
	
}
