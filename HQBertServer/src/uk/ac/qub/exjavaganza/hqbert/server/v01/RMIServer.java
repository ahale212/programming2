package uk.ac.qub.exjavaganza.hqbert.server.v01;

import java.math.BigInteger;
import java.rmi.RemoteException;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.UnicastRemoteObject;
import java.security.SecureRandom;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.naming.AuthenticationException;

/**
 * Server that implements the methods defined in the RemoteServer interface, and makes them 
 * accessible to clients via RMI (Remote Method Invocation).
 * 
 * @author Tom Couchman
 */
public class RMIServer extends UnicastRemoteObject implements RemoteServer {

	/**
	 * Serial version UID of the class - necessary for determining compatibility of 
	 * serialised classes. Should be incremented when breaking changes mean the class will no 
	 * longer be compatible with previous versions of the class.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * The number of times an attempt to communicate with the client before the client
	 * is removed from the client list.
	 */
	private static final int MAX_FAILED_CONNECTION_ATTEMPTS = 3;
	
	/**
	 * A map of ClientCallback objects, with clientIDs used as keys.
	 */
	private HashMap<String, ClientDetails> clients;

	/**
	 * Constructor with args for the RMI Server.
	 * 
	 * @param port		The port number on which the remote object receives calls 
	 * @throws RemoteException Exception thrown when an communication issue occurs during RMI
	 */
	public RMIServer(int port) throws RemoteException {
		super(port);
		
		init();
	}
	
	/**
	 * Constructor with args for the RMI Server.
	 * 
	 * @param port		The port number on which the remote object receives calls 
	 * @param csf		The client Socket factory
	 * @param ssf		The server socket factory
	 * @throws RemoteException
	 */
	public RMIServer(int port, RMIClientSocketFactory csf, RMIServerSocketFactory ssf) throws RemoteException {
		super(port, csf, ssf);
		
		init();
		
	}
	
	/**
	 * Initialise the server
	 */
	private void init() {
		// Initialises the clients list
		clients = new HashMap<String, ClientDetails>();
	}

	/**
	 * Performs the callback that informs clients that the queue has been
	 * updated
	 */
	public synchronized void updateClients() {
		System.out.println("Sending updated queue to clients");

		// Get the key set from the list of clients
		Set<String> keys = clients.keySet();
		// Get the iterator for the list of keys
		Iterator<String> keyIterator = keys.iterator();

		// Loops through each of the clients in the clients list
		while (keyIterator.hasNext()) {
			String key = keyIterator.next();

			// Get the client associated with the current key
			ClientDetails client = clients.get(key);

			try {
				
				// calls the update method with the current state of the
				// patients queue
				client.getCallback().udpate(Supervisor.INSTANCE.getHQueue().getPQ(),
						Supervisor.INSTANCE.getTreatmentFacilities(), Supervisor.INSTANCE.getOnCallStaffList(), Supervisor.INSTANCE.getActiveStaff(Job.DOCTOR));
			} catch (RemoteException e) {
				System.err
						.println("RemoteException occurred while calling 'update' callback method. "
								+ "Removing client from clients list.");

				client.incrementFailedConnectionAttempts();
				if (client.getFailedConnectionAttempts() > MAX_FAILED_CONNECTION_ATTEMPTS) {
					// Add the failed send to the log
					Supervisor.INSTANCE.logToFile("Client connection issue: " + client.getUsername() + " removed from queue.");
					// Remove the client from the list.
					deregister(key);
				}
			}
		}
	}

	public synchronized void broadcastLog(String log) {

		// Get the key set from the list of clients
		Set<String> keys = clients.keySet();

		// Loops through each of the clients in the clients list
		for (String key : keys) {
			ClientDetails client = clients.get(key);
			try {
				client.getCallback().log(log);
			} catch (RemoteException e) {
				client.incrementFailedConnectionAttempts();
				if (client.getFailedConnectionAttempts() > MAX_FAILED_CONNECTION_ATTEMPTS) {
					// Add the failed send to the log
					Supervisor.INSTANCE.logToFile("Client connection issue: " + client.getUsername() + " removed from queue.");
					// Remove the client from the list.
					deregister(key);
				}
			}

		}

	}
	

	/**
	 * Inform the client that the next patient should be called to a room
	 */
	public synchronized void broadcastNextPatientCall(String message) {

		// Get the key set from the list of clients
		Set<String> keys = clients.keySet();

		// Loops through each of the clients in the clients list
		for (String key : keys) {
			ClientDetails client = clients.get(key);
			try {
				// Alert the client that the queue is full
				client.getCallback().notifyNextPatientToRoom(message);
			} catch (RemoteException e) {
				e.printStackTrace();
				
				client.incrementFailedConnectionAttempts();
				if (client.getFailedConnectionAttempts() > MAX_FAILED_CONNECTION_ATTEMPTS) {
					// Remove the client from the list.
					deregister(key);
				}
			}
		}
	}
	
	/**
	 * Inform the clients that the queue is full
	 */
	public synchronized void broadcastQueueFullAlert() {

		// Get the key set from the list of clients
		Set<String> keys = clients.keySet();

		// Loops through each of the clients in the clients list
		for (String key : keys) {
			ClientDetails client = clients.get(key);
			try {
				// Alert the client that the queue is full
				client.getCallback().alertQueueFull();
			} catch (RemoteException e) {
				
				client.incrementFailedConnectionAttempts();
				if (client.getFailedConnectionAttempts() > MAX_FAILED_CONNECTION_ATTEMPTS) {
					// Add the failed send to the log
					Supervisor.INSTANCE.logToFile("Client connection issue: " + client.getUsername() + " removed from queue.");
					// Remove the client from the list.
					deregister(key);
				}
			}
		}
	}

	/**
	 * Remotely callable method that registers a client for callbacks from the
	 * server and returns a new ID
	 */
	@Override
	public String register(String username, String password, ClientCallback callbackObject) throws RemoteException {
		System.out.println("Adding client to the client callback list.");
		
		// Search for the staff member in the database to
		Staff staffMember = null;
		try {
			List<Staff> staffMembers = Supervisor.INSTANCE.getStaffAccessor().staffList(username, password);
			if (staffMembers.size() > 0) {
				staffMember = staffMembers.get(0);
			} else {
				return "";
			}
		} catch (SQLException e) {
			// staff member not available
		}
		
		// Generate a new client id
		String newClientID = generateClientID();

		// If the Id is not unique, loop until a unique key has been found
		while (clients.containsKey(newClientID)) {
			// Generate a new ID
			newClientID = generateClientID();
		}
		
		// Add the passed in client and their username to the HashMap of clients, with their client ID as the key
		this.clients.put(newClientID, new ClientDetails(callbackObject, username));
		
		return newClientID;

	}

	/**
	 * Remotely accessible method that takes clients out of the clients hashmap,
	 * therefore deregistering them from callbacks from the server.
	 * @param clientID		The clientID of the client deregistering
	 */
	@Override
	public void deregister(String clientID) {
		System.out.println("Removing client from client callback list.");

		clients.remove(clientID);
	}

	


	
	
	@Override
	public List<Person> searchPersonByDetails(String clientID, String nhsNumber,
			String firstName, String lastName, String dateOfBirth,
			String postCode, String telephoneNumber) throws RemoteException, AuthenticationException {

		// If the client is not authenticated, thrown an authentication error
		if (!authenticate(clientID)) {
			throw new AuthenticationException("Client not registered");
		}
		
		try {
			List<Person> people;// = new ArrayList<Person>();

			// Search for the person in the database
			people = Supervisor.INSTANCE.getDataAccessor().personList(
					nhsNumber, firstName, lastName, dateOfBirth, postCode,
					telephoneNumber);

			// Return the matching people found in the database
			return people;
		} catch (SQLException e) {
			// Inform the user that an error occurred
			throw new RemoteException("Database connection error.");
		}
	}

	@Override
	public List<Staff> searchStaffByDetails(String clientID, String username, String password)
			throws RemoteException, AuthenticationException {

		// If the client is not authenticated, thrown an authentication error
		if (!authenticate(clientID)) {
			throw new AuthenticationException("Client not registered");
		}
		
		try {
			List<Staff> staff;// = new ArrayList<Staff>();

			// Search for the staff in the database
			staff = Supervisor.INSTANCE.getStaffAccessor().staffList(username,
					password);

			// Return the matching staff found in the database
			return staff;

		} catch (SQLException e) {
			// Inform the user that an error occurred
			throw new RemoteException("Database connection error.");
		}
	}
	
	/**
	 * Search for a staff member based on their username
	 */
	@Override
	public Staff searchStaffByUsername(String clientID, String username) throws RemoteException, AuthenticationException {

		// If the client is not authenticated, thrown an authentication error
		if (!authenticate(clientID)) {
			throw new AuthenticationException("Client not registered");
		}
		
		try {
			// Search for the staff in the database
			 Staff staffMember = Supervisor.INSTANCE.getStaffAccessor().getStaffMemeberByUsername(username);

			// Return the matching staff found in the database
			return staffMember;

		} catch (SQLException e) {
			// Inform the user that an error occurred
			throw new RemoteException("Database connection error.");
		}

	}

	/**
	 * Gets the current state of the queue.
	 * 
	 * @return The list of treatment rooms
	 * @throws RemoteException
	 *             Exception thrown when an communication issue occurs during
	 *             RMI
	 */
	@Override
	public LinkedList<Patient> getQueue(String clientID) throws RemoteException {

		return null;
	}

	/**
	 * Gets the current state of the treatments rooms.
	 * 
	 * @return The list of treatment rooms
	 * @throws RemoteException
	 *             Exception thrown when an communication issue occurs during
	 *             RMI
	 * @throws AuthenticationException 
	 */
	public ArrayList<TreatmentFacility> getTreatmentRooms(String clientID)
			throws RemoteException, AuthenticationException {
		
		// If the client is not authenticated, thrown an authentication error
		if (!authenticate(clientID)) {
			throw new AuthenticationException("Client not registered");
		}
		
		return Supervisor.INSTANCE.getTreatmentFacilities();
	}

	/**
	 * Adds a newly triaged emergency patient to the backend list along with the
	 * details of their current state.
	 * 
	 * @param clientID		The id of the client calling the method
	 * @throws RemoteException Exception thrown when an communication issue occurs duringRMI
	 * @throws AuthenticationException 
	 */
	@Override
	public boolean addPatient(String clientID, Patient patient) throws RemoteException, AuthenticationException {
		
		// If the client is not authenticated, thrown an authentication error
		if (!authenticate(clientID)) {
			throw new AuthenticationException("Client not registered");
		}
		// Admit the patient and return whethe it was successful
		return Supervisor.INSTANCE.admitPatient(patient);
	}

	/**
	 * Updated the doctors notes, for a particular person, identified by NHS
	 * number.
	 */
	@Override
	public boolean updateDoctorsNotes(String clientID, String nhsNumber, String doctorsNotes)
			throws RemoteException, AuthenticationException {

		// If the client is not authenticated, thrown an authentication error
		if (!authenticate(clientID)) {
			throw new AuthenticationException("Client not registered");
		}
		
		// Call the update patient notes method on the supervisor.
		return Supervisor.INSTANCE.updateDoctorsNotes(nhsNumber, doctorsNotes);

	}

	@Override
	public void extendTreatmentTime(String clientID, int facilityIndex, ExtensionReason reason)
			throws RemoteException, AuthenticationException {

		// If the client is not authenticated, thrown an authentication error
		if (!authenticate(clientID)) {
			throw new AuthenticationException("Client not registered");
		}
		
		// Call the extend treatment room method on the supervisor.
		Supervisor.INSTANCE.extendRoom(facilityIndex, reason);

	}

	/**
	 * Ping method to allow clients to check the server is still accessible Also
	 * returns a boolean value indicating whether the client is registered for
	 * updates
	 * 
	 * @param clientID
	 *            The id of the client sending the ping.
	 */
	@Override
	public boolean heartbeat(String clientID) throws RemoteException {
		// Check to see if the client is in the clients list, by searching for their clientID in
		// the clients hashmap keys
		if (clients.containsKey(clientID)) {
			// if the client is registered return true
			return true;
		} else {
			// If the client is not registered return false
			// so they know they must re-register
			return false;
		}
	}
	
	/** 
	 * Generates an ID to identify the client
	 * @return
	 */
	private String generateClientID() {

		// Use secure random to generate a random id for the client
		SecureRandom random = new SecureRandom();

		return new BigInteger(130, random).toString(32);
	}
	
	
	public long getAvTimeInQue(){
		
		return MetricsController.INSTANCE.getAvTimeInQue();
	}
	
	public long getAvTreatmentTime(){
		
		return MetricsController.INSTANCE.getAvTreatmentTime();
	}
	
	public long getAvVisitTime(){
		
		return MetricsController.INSTANCE.getAvVisitTime();
	}
	
	public int[] getUrgencies(){
		MetricsController.INSTANCE.setUrgency();
		return MetricsController.INSTANCE.getUrgencies();
	}
	
	public int getPatientsRejected(){
		return MetricsController.INSTANCE.getPatientsRejected();
	}
	
	public int getCurrentNumberInQueue(){

		return Supervisor.INSTANCE.getHQueue().getPQ().size();
	}
	
	public int getNumberOfExtensions(){
		return MetricsController.INSTANCE.exstentions.size();
	}
	
	public int NumberOfPatientsOverWaitTime(){
		int count = 0;
		for(long waitTime : MetricsController.INSTANCE.queTime){
			if((waitTime/60/60)>=Supervisor.INSTANCE.MAX_WAIT_TIME){
				count++;
			}
		}
		return count;
	}

	
	/**
	 * Authenticates the clientID by checking it exists in the clients list.
	 * If it does the ClientCallback object is returned, else null is returned if the
	 * clientID is not valid
	 * @param clientID		The clientId of the client
	 * @return				Whether or not the clientID is valid
	 */
	private boolean authenticate(String clientID) {
		// If the clients list contains the clientID as a key then the 
		return clients.containsKey(clientID);
	}


	@Override
	public void reAssignTriage(String clientID, int patientIndex,
			Urgency newUrgency) throws RemoteException, AuthenticationException {
		
		Supervisor.INSTANCE.reAssignTriage(Supervisor.INSTANCE.getHQueue().getPQ().get(patientIndex), newUrgency);
		// Send the updated data to the client
		updateClients();
		
	}
	
	
	/**
	 * 
	 * @param clientID			The Id of the client
	 * @param numberOfRooms  	The number of rooms to be set
	 * @return	Whether or not the change was made
	 * @throws RemoteException	
	 * @throws AuthenticationException
	 */
	@Override
	public boolean setTreatmentRoomNumber(String clientID, int numberOfRooms) throws RemoteException, AuthenticationException {
		
		// If the client is not authenticated, thrown an authentication error
		if (!authenticate(clientID)) {
			throw new AuthenticationException("Client not registered");
		}
		
		// Attempt to change the number of 
		Supervisor.INSTANCE.setCurrentNumberOfTreatmentRooms(numberOfRooms);
		
		return true;
	}
	

}
