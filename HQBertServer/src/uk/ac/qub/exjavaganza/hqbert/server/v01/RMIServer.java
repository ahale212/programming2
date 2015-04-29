package uk.ac.qub.exjavaganza.hqbert.server.v01;

import java.math.BigInteger;
import java.rmi.RemoteException;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import com.sun.jndi.url.rmi.rmiURLContext;
import com.sun.security.ntlm.Client;

import javafx.stage.Stage;

/**
 * Server that implements the methods defined in the RemoteServer interface, so
 * that they can be called by clients via RMI (Remote Method Invocation).
 * 
 * @author Tom Couchman
 */
public class RMIServer extends UnicastRemoteObject implements RemoteServer {

	private HashMap<String, ClientCallback> clients;

	/**
	 * Constructor for the RMI Server.
	 * 
	 * @throws RemoteException
	 *             Exception thrown when an communication issue occurs during
	 *             RMI
	 */
	public RMIServer() throws RemoteException {
		super();

		// Initialises the clients list
		clients = new HashMap<String, ClientCallback>();
	}

	/**
	 * Performs the callback that informs clients that the queue has been
	 * updated
	 */
	public void updateClients() {
		System.out.println("Sending updated queue to clients");

		// Get the key set from the list of clients
		Set<String> keys = clients.keySet();
		// Get the iterator for the list of keys
		Iterator<String> keyIterator = keys.iterator();

		// Loops through each of the clients in the clients list
		while (keyIterator.hasNext()) {
			String key = keyIterator.next();

			// Get the client associated with the current key
			ClientCallback client = clients.get(key);

			try {
				// calls the update method with the current state of the
				// patients queue
				client.udpate(Supervisor.INSTANCE.getHQueue().getPQ(),
						Supervisor.INSTANCE.getTreatmentFacilities());
			} catch (RemoteException e) {
				System.err
						.println("RemoteException occurred while calling 'update' callback method. "
								+ "Removing client from clients list.");
				// Display the stack trace for the exception
				e.printStackTrace();

				// Remove the client from the list.
				deregister(key);
			}
		}
	}

	public void broadcastLog(String log) {

		// Get the key set from the list of clients
		Set<String> keys = clients.keySet();

		// Loops through each of the clients in the clients list
		for (String key : keys) {
			ClientCallback client = clients.get(key);
			try {
				client.log(log);
			} catch (RemoteException e) {
				e.printStackTrace();
			}

		}

	}

	/**
	 * Inform the clients that the queue is full
	 */
	public void broadcastQueueFullAlert() {

		// Get the key set from the list of clients
		Set<String> keys = clients.keySet();

		// Loops through each of the clients in the clients list
		for (String key : keys) {
			ClientCallback client = clients.get(key);
			try {
				// Alert the client that the queue is full
				client.alertQueueFull();
			} catch (RemoteException e) {
				e.printStackTrace();
				// Remove the client from the list.
				deregister(key);
			}
		}
	}

	/**
	 * Remotely callable method that registers a client for callbacks from the
	 * server and returns a new ID
	 */
	@Override
	public String register(ClientCallback client) throws RemoteException {
		System.out.println("Adding client to the client callback list.");

		// Generate a new client id
		String newClientID = generateClientID();

		// If the Id is not unique, loop until a unique key has been found
		while (clients.containsKey(newClientID)) {
			// Generate a new ID
			newClientID = generateClientID();
		}

		// Add the passed in client to the HashMap of clients, along with their
		// unique id
		this.clients.put(newClientID, client);

		// Return the id back to the client
		return newClientID;

	}

	/**
	 * Remotely accessible method that takes clients out of the clients hashmap,
	 * therefore deregistering them from callbacks from the server.
	 */
	@Override
	public void deregister(String clientID) {
		System.out.println("Removing client from client callback list.");

		clients.remove(clientID);
	}

	@Override
	public List<Person> searchPersonByDetails(String nhsNumber,
			String firstName, String lastName, String dateOfBirth,
			String postCode, String telephoneNumber) throws RemoteException {

		try {
			List<Person> people;// = new ArrayList<Person>();

			// Search for the person in the database
			people = Supervisor.INSTANCE.getDataAccessor().personList(
					nhsNumber, firstName, lastName, dateOfBirth, postCode,
					telephoneNumber);

			// Return the matching people found in the database
			return people;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public List<Staff> searchStaffByDetails(String username, String password)
			throws RemoteException {

		try {
			List<Staff> staff;// = new ArrayList<Staff>();

			// Search for the staff in the database
			staff = Supervisor.INSTANCE.getStaffAccessor().staffList(username,
					password);

			// Return the matching staff found in the database
			return staff;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
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
	public LinkedList<Patient> getQeue() throws RemoteException {

		return null;
	}

	/**
	 * Gets the current state of the treatments rooms.
	 * 
	 * @return The list of treatment rooms
	 * @throws RemoteException
	 *             Exception thrown when an communication issue occurs during
	 *             RMI
	 */
	public ArrayList<TreatmentFacility> getTreatmentRooms()
			throws RemoteException {
		return Supervisor.INSTANCE.getTreatmentFacilities();
	}

	/**
	 * Adds a newly triaged emergency patient to the backend list along with the
	 * details of their current state.
	 * 
	 * @throws RemoteException
	 *             Exception thrown when an communication issue occurs during
	 *             RMI
	 */
	@Override
	public void addPrimaryPatient(Person person, boolean airway,
			boolean breating, boolean spine, boolean circulation,
			boolean disability, boolean exposure) throws RemoteException {
		Patient patient = new Patient();
		patient.setPerson(person);
		patient.setUrgency(Urgency.EMERGENCY);
		patient.setPriority(true);

		Supervisor.INSTANCE.admitPatient(patient);

	}

	/**
	 * Adds a newly triaged non-emergency patient to the backend list along with
	 * the details of their current state.
	 * 
	 * @throws RemoteException
	 *             Exception thrown when an communication issue occurs during
	 *             RMI
	 */
	@Override
	public void addSecondaryPatient(Person person, Urgency urgency,
			boolean breathingWithoutResusitation, boolean canWalk,
			int respirationRate, int pulseRate, String underlyingCondition,
			String prescribedMedication) {

		Patient patient = new Patient();
		patient.setPerson(person);
		patient.setUrgency(urgency);
		patient.setPriority(false);

		Supervisor.INSTANCE.admitPatient(patient);
	}

	/**
	 * Updated the doctors notes, for a particular person, identified by NHS
	 * number.
	 */
	@Override
	public void updateDoctorsNotes(String nhsNumber, String doctorsNotes)
			throws RemoteException {

		// Call the update patient notes method on the supervisor.
		Supervisor.INSTANCE.updatePatientNotes(nhsNumber, doctorsNotes);

	}

	@Override
	public void extendTreatmentTime(TreatmentFacility facility)
			throws RemoteException {

		// Call the extend treatment room method on the supervisor.
		Supervisor.INSTANCE.extendTreatmentRoom(facility);

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
	public boolean ping(String clientID) throws RemoteException {
		// Check to see if the client is in the clients list
		if (clients.containsKey(clientID)) {
			// if the client is registered return true
			return true;
		} else {
			// If the client is not registered return false
			// so they know they must re-register
			return false;
		}
	}

	private String generateClientID() {

		// Use secure random to generate a random id for the client
		SecureRandom random = new SecureRandom();

		return new BigInteger(130, random).toString(32);
	}

}
