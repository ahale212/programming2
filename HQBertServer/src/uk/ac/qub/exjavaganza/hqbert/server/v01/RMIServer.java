package uk.ac.qub.exjavaganza.hqbert.server.v01;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

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
				client.udpate(Supervisor.INSTANCE.hQueue);
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
	public Person searchPersonByName(String firstName, String lastName)
			throws RemoteException {

		return null;
	}

	@Override
	public Person searchPersonByNhsNumber(int nhsNumber) throws RemoteException {

		return null;
	}

	@Override
	public HQueue getQeue() throws RemoteException {

		return null;
	}
	
}
