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
	public LinkedList<Patient> getQueue(String clientID) throws RemoteException, AuthenticationException;
	
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
	public void extendTreatmentTime(String clientID, int facilityIndex, ExtensionReason reason) throws RemoteException, AuthenticationException;

	/**
	 * 
	 * @param clientID
	 * @param patient
	 * @param newUrgency
	 * @throws RemoteException
	 * @throws AuthenticationException
	 */
	public void reAssignTriage(String clientID, int patientIndex, Urgency newUrgency) throws RemoteException, AuthenticationException;

	
	/**
	 * 
	 * @param clientID			The Id of the client
	 * @param numberOfRooms  	The number of rooms to be set
	 * @return	Whether or not the change was made
	 * @throws RemoteException	
	 * @throws AuthenticationException
	 */
	public boolean setTreatmentRoomNumber(String clientID, int numberOfRooms) throws RemoteException, AuthenticationException;
	
	/**
	 * Gets the number of treatment rooms
	 * @return the number of treatment rooms
	 * @throws RemoteException
	 * @throws AuthenticationException
	 */
	public int getTreatmentRoomNumber() throws RemoteException;
	
	
	/**
	 * Method to get the ques average wait time
	 * @return time in seconds
	 */
	public long getAvTimeInQue() throws RemoteException;
	
	/**
	 * Method to get the average treatment time
	 * @return time in seconds
	 */
	public long getAvTreatmentTime() throws RemoteException;
	
	/**
	 * Method to get the average overall visit time
	 * @return time in seconds
	 */
	public long getAvVisitTime() throws RemoteException;
	
	/**
	 * Method to get an int[] array with the total counts of EMERGENCY,URGENT,SEMI_URGENT,NON_URGENT patients respectively
	 * @return int[] urgency 'EMERGENCY,URGENT,SEMI_URGENT,NON_URGENT' respectively
	 */
	public int[] getUrgencies() throws RemoteException;
	
	/**
	 * Method to get the current number of rejected patients
	 * @return number of patients rejected from queue
	 * @throws RemoteException
	 */
	public int getPatientsRejected() throws RemoteException;
	/**
	 * Method to get the current number of patients waiting in the queue
	 * @return current number in the queue
	 */
	public int getCurrentNumberInQueue() throws RemoteException;
	
	/**
	 * Method to get the current number of Extensions requested
	 * @return number of extensions
	 */
	public int getNumberOfExtensions() throws RemoteException;
	
	/**
	 * Method to get the total number of patients exceeding the waiting time 
	 * @return number of patients over the wait time limit
	 */
	public int NumberOfPatientsOverWaitTime() throws RemoteException;
}

