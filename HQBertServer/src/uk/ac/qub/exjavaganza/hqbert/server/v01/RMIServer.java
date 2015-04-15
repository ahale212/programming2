package uk.ac.qub.exjavaganza.hqbert.server.v01;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javafx.stage.Stage;

/**
 * Server that implements the methods defined in the RemoteServer interface,
 * so that they can be called by clients via RMI (Remote Method Invocation).
 * 
 * @author Tom Couchman
 */
public class RMIServer extends UnicastRemoteObject implements RemoteServer {

	private List<ClientCallback> clients;

	/**
	 * Constructor for the RMI Server.
	 * @throws RemoteException	Exception thrown when an communication issue occurs during RMI
	 */
	public RMIServer() throws RemoteException {
		super();
		
		// Initialises the clients list
		clients = new ArrayList<ClientCallback>();
	}
	
	/**
	 * Performs the callback that informs clients that the queue has been updated
	 */
	public void updateClients() {
		System.out.println("Sending updated queue to clients");
		
		// Loops through each of the clients in the clients list
		for (int loop = clients.size() - 1; loop >= 0; loop--) {
			ClientCallback client = clients.get(loop);
			
			try {
				// calls the update method with the current state of the patients queue
				client.udpate(Supervisor.INSTANCE.getHQueue().getPQ(), Supervisor.INSTANCE.getTreatmentFacilities());
			} catch (RemoteException e) {
				System.err.println("RemoteException occurred while calling 'update' callback method. "
						+ "Removing client from clients list.");
				// Display the stack trace for the exception
				e.printStackTrace();
				
				// Remove the client from the list.
				unregisterForUpdates(client);
			}
		}
	}
	
	public void broadcastLog(String log) {
		
		// Loops through each of the clients in the clients list
		for (int loop = clients.size() - 1; loop >= 0; loop--) {
			ClientCallback client = clients.get(loop);
			
			try {
				client.log(log);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			
		}
		
	}
	
	/**
	 * The methods defined in the RemoteServer interface, that clients of the server can call
	 */
	
	@Override
	public void registerForUpdates(ClientCallback client)
			throws RemoteException {
		System.out.println("Adding client to the client callback list.");
		
		// Add the passed in client to the list of clients
		this.clients.add(client);
		
	}
	
	@Override
	public void unregisterForUpdates(ClientCallback client) {
		clients.remove(client);
	}

	@Override
	public List<Person> searchPersonByDetails(String nhsNumber, String firstName, String lastName, String dateOfBirth, String postCode, String telephoneNumber)
			throws RemoteException {
		




		try {
			List<Person> people;// = new ArrayList<Person>();

			// Search for the person in the database
			people = Supervisor.INSTANCE.getDataAccessor().personList(nhsNumber, firstName, lastName, dateOfBirth, postCode, telephoneNumber);
			
			// Test
			/*Person testPerson = new Person();
			testPerson.setTitle("Mrs");
			testPerson.setFirstName ("Barbara");
			testPerson.setLastName ("Balmer");
			testPerson.setAddress("12 McDonald Road");
			testPerson.setCity("Bristol");
			testPerson.setCountry("United Kingdom");
			testPerson.setPostcode("TA8 1RE");
			testPerson.setBloodGroup("B");
			testPerson.setNHSNum("242512612781236");
			testPerson.setTelephone("01278 795326");
			
			people.add(testPerson);*/
			
			// Return the matching people found in the database
			return people;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		return null;
	}

	/**
	 * Gets the current state of the queue.
	 * @return The list of treatment rooms
	 * @throws RemoteException	Exception thrown when an communication issue occurs during RMI
	 */
	@Override
	public LinkedList<Patient> getQeue() throws RemoteException {

		return null;
	}
	
	/**
	 * Gets the current state of the treatments rooms.
	 * @return The list of treatment rooms
	 * @throws RemoteException	Exception thrown when an communication issue occurs during RMI
	 */
	public ArrayList<TreatmentFacility> getTreatmentRooms() throws RemoteException {
		 return Supervisor.INSTANCE.getTreatmentFacilities();
	}

	/**
	 * Adds a newly triaged emergency patient to the backend list along with the details of their current state.
	 * @throws RemoteException	Exception thrown when an communication issue occurs during RMI
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
	 * Adds a newly triaged non-emergency patient to the backend list along with the details of their current state.
	 * @throws RemoteException	Exception thrown when an communication issue occurs during RMI
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
	
	
	
	
}
