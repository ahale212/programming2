package uk.ac.qub.exjavaganza.hqbert.server.v01;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.naming.AuthenticationException;

/**
 * Interface that defines the methods that can be called from the 
 * client. The methods will be called using RMI so the interface
 * extends java.rmi.Remote
 * 
 * @author Tom Couchman
 */
public interface RemoteServer extends Remote {

	/** The various states that the server can be in */
	public enum ConnectionState {
		CONNECTED, CONNECTING, NOT_CONNECTED, CONNECTION_ERROR
	}
	
	public enum PatientAdmittanceState {
		ADMITTED, FAILED_QUEUE_FULL, FAILED_NO_EMERGENCY_CAPACITY
	}
	
	/**
	 * Registers clients for callbacks. This allows the server to communicate with clients
	 * that register for the timed updates.
	 * @param userName	The username of the client connecting to the server
	 * @param password	The encrypted password of the user
	 * @param client	The client that wishes to receive the update.
	 * @throws RemoteException	Exception thrown when a communication issue occurs during RMI
	 */
	public String register(String username, String password, ClientCallback client) throws RemoteException;
	
	/**
	 * Deregisters clients for callbacks. No more updates will be sent to the client passed in.
	 * @param clientID		The clientId of the client that wishes to be unregistered
	 * @throws RemoteException	Exception thrown when a communication issue occurs during RMI
	 */
	public void deregister(String clientID) throws RemoteException;
	
	/**
	 * A ping call that allows the user to ensure that the server is still running.
	 * @param clientID		The clientId of the client sending the heartbeat
	 * @return 				Whether the user is registered or not.
	 */
	public boolean heartbeat(String clientID) throws RemoteException;
	
	/**
	 * Searches for a person by their firstName and lastName.
	 * @param firstName	The first name of the person
	 * @param lastName	The last name of the person
	 * @return	The Person that matches the passed in search terms.
	 * @throws RemoteException	Exception thrown when a communication issue occurs during RMI
	 */
	public List<Person> searchPersonByDetails(String clientID, String nhsNumber, String firstName, String lastName, String dateOfBirth, String postCode, String telephoneNumber) throws RemoteException, AuthenticationException;
	

	/**
	 * Retrieves the current state of the queue
	 * @return	The patient queue
	 * @throws RemoteException	Exception thrown when a communication issue occurs during RMI
	 */
	public LinkedList<Patient> getQeue(String clientID) throws RemoteException, AuthenticationException;
	
	/**
	 * Gets the current state of the treatment rooms.
	 * @return The list of treatment rooms
	 * @throws RemoteException	Exception thrown when a communication issue occurs during RMI
	 */
	public ArrayList<TreatmentFacility> getTreatmentRooms(String clientID) throws RemoteException, AuthenticationException;
	
	/**
	 * Adds a newly triaged emergency patient to the backend list along with the details of their current state. If they 
	 * are added, true is returned. If they cannot be added (e.g. the queue is full), false is returned.
	 * @return Whether the patient was successfully added or not.
	 * @throws RemoteException	Exception thrown when a communication issue occurs during RMI
	 */
	public boolean addPatient(String clientID, Patient patien) throws RemoteException, AuthenticationException;

	/**
	 * Logs a user into the system
	 * @param username	The username of the staff member
	 * @param password	The password of the staff member
	 * @return	The Staff that matches the passed in search terms.
	 * @throws RemoteException	Exception thrown when a communication issue occurs during RMI
	 */
	public List<Staff> searchStaffByDetails(String clientID, String username, String password) throws RemoteException, AuthenticationException;
	
	
	public Staff searchStaffByUsername(String clientID, String username) throws RemoteException, AuthenticationException;
	
	/**
	 * Method to update the doctors notes for a given person.
	 * @throws RemoteException	Exception thrown when a communication issue occurs during RMI
	 */
	public boolean updateDoctorsNotes(String clientID, String nhsNumber, String doctorsNotes) throws RemoteException, AuthenticationException;
	
	/**
	 * Method to extend the treatment time for a given treatment room.
	 * @throws RemoteException	Exception thrown when a communication issue occurs during RMI
	 */
	public void extendTreatmentTime(String clientID, TreatmentFacility facility, ExtensionReason reason) throws RemoteException, AuthenticationException;
}

