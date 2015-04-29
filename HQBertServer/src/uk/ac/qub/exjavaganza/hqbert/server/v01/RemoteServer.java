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

	public enum ConnectionState {
		CONNECTED, CONNECTING, NOT_CONNECTED
	}
	
	/**
	 * Registers clients for callbacks. This allows the server to communicate with clients
	 * that with to register for the times updates.
	 * @param client	The client that wishes to receive the update.
	 * @throws RemoteException	Exception thrown when a communication issue occurs during RMI
	 */
	public String register(ClientCallback client) throws RemoteException;
	
	/**
	 * Unregisters clients for callbacks. No more updates will be sent to the client passed in.
	 * @param client	The client that wishes to be unregistered
	 * @throws RemoteException	Exception thrown when a communication issue occurs during RMI
	 */
	public void deregister(String clientID) throws RemoteException;
	
	/**
	 * A ping call that allows the user to ensure that the server is still running.
	 */
	public boolean heartbeat(String clientID) throws RemoteException;
	
	/**
	 * Searches for a person by their firstName and lastName.
	 * @param firstName	The first name of the person
	 * @param lastName	The last name of the person
	 * @return	The Person that matches the passed in search terms.
	 * @throws RemoteException	Exception thrown when a communication issue occurs during RMI
	 */
	public List<Person> searchPersonByDetails(String nhsNumber, String firstName, String lastName, String dateOfBirth, String postCode, String telephoneNumber) throws RemoteException;
	

	/**
	 * Retrieves the current state of the queue
	 * @return	The patient queue
	 * @throws RemoteException	Exception thrown when a communication issue occurs during RMI
	 */
	public LinkedList<Patient> getQeue() throws RemoteException;
	
	/**
	 * Gets the current state of the treatment rooms.
	 * @return The list of treatment rooms
	 * @throws RemoteException	Exception thrown when a communication issue occurs during RMI
	 */
	public ArrayList<TreatmentFacility> getTreatmentRooms() throws RemoteException;
	
	/**
	 * Adds a newly triaged emergency patient to the backend list along with the details of their current state. If they 
	 * are added, true is returned. If they cannot be added (e.g. the queue is full), false is returned.
	 * @return Whether the patient was successfully added or not.
	 * @throws RemoteException	Exception thrown when a communication issue occurs during RMI
	 */
	public boolean addPrimaryPatient(Person person, boolean airway, boolean breating, boolean spine, boolean circulation, boolean disability, boolean exposure) throws RemoteException;
	
	/**
	 * Adds a newly triaged non-emergency patient to the backend list along with the details of their current state. If they 
	 * are added, true is returned. If they cannot be added (e.g. the queue is full), false is returned.
	 * @return Whether the patient was successfully added or not.
	 * @throws RemoteException	Exception thrown when a communication issue occurs during RMI
	 */
	public boolean addSecondaryPatient(Person person, Urgency urgency, boolean breathingWithoutResusitation, boolean canWalk, int respirationRate, int pulseRate, String underlyingCondition, String prescribedMedication) throws RemoteException;

	/**
	 * Searches for a staff by their username and password.
	 * @param username	The username of the staff
	 * @param password	The password of the staff
	 * @return	The Staff that matches the passed in search terms.
	 * @throws RemoteException	Exception thrown when a communication issue occurs during RMI
	 */
	public List<Staff> searchStaffByDetails(String username, String password) throws RemoteException;
	
	/**
	 * 
	 * @throws RemoteException	Exception thrown when a communication issue occurs during RMI
	 */
	public void updateDoctorsNotes(String nhsNumber, String doctorsNotes) throws RemoteException;
	
	/**
	 * 
	 * @throws RemoteException	Exception thrown when a communication issue occurs during RMI
	 */
	public void extendTreatmentTime(TreatmentFacility facility, ExtensionReason reason) throws RemoteException;
}

