package uk.ac.qub.exjavaganza.hqbert.server.v01;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Interface for the callback methods that will be implemented by the client.
 * These methods can then be called from the server when an update to the queue occurs.
 * 
 * @author Tom Couchman
 */
public interface ClientCallback extends Remote {

	/**
	 * The update method that is called (via the callback object) by the client whenever 
	 * the timed update to the queue occurs.
	 * @param queue		The patient queue
	 * @throws RemoteException	Exception thrown when an communication issue occurs during RMI
	 */
	public void udpate(LinkedList<Patient> queue, ArrayList<TreatmentFacility> treatmentFacilities) throws RemoteException;
	
}
