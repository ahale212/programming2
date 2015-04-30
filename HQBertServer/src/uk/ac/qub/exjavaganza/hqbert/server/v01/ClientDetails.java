package uk.ac.qub.exjavaganza.hqbert.server.v01;

/**
 * Details about a client connected to the RMI server
 * @author Tom Couchman
 */
public class ClientDetails {

	/** The callback object of the client */
	private ClientCallback callback;
	
	/** The number of failed connection attempts */
	private int failedConnectionAttempts;
	
	/**
	 * Constructor with args for ClientDetails object 
	 * @param callback	The callback object to be set for the client
	 */
	public ClientDetails(ClientCallback callback) {
		this.callback = callback;
		// Intialise the number of connection attempts to 0
		this.failedConnectionAttempts = 0;
	}
	
	/**
	 * Getter for the callback object
	 * @return 		The callback object of the client
	 */
	public ClientCallback getCallback() {
		return callback;
	}
	
	/**
	 * Setter for the callback object
	 * @param callback   The callback object to be set for the client
	 */
	public void setCallback(ClientCallback callback) {
		this.callback = callback;
	}
	

	/**
	 * Getter for the number of failed connection attempts
	 * @return	Number of failed connection attempts
	 */
	public int getFailedConnectionAttempts() {
		return failedConnectionAttempts;
	}
	
	/**
	 * Increments the number of failed connection attempts
	 */
	public void incrementFailedConnectionAttempts() {
		failedConnectionAttempts++;
	}
	
	/**
	 * Resets the number of connection attempts
	 */
	public void resetFailedConnectionAttempts() {
		failedConnectionAttempts = 0;
	}
	
}
