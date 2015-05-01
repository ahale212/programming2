package application;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NoSuchObjectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.naming.AuthenticationException;

import uk.ac.qub.exjavaganza.hqbert.server.v01.ClientCallback;
import uk.ac.qub.exjavaganza.hqbert.server.v01.Patient;
import uk.ac.qub.exjavaganza.hqbert.server.v01.RemoteServer;
import uk.ac.qub.exjavaganza.hqbert.server.v01.Staff;
import uk.ac.qub.exjavaganza.hqbert.server.v01.RemoteServer.ConnectionState;
import uk.ac.qub.exjavaganza.hqbert.server.v01.TreatmentFacility;

/**
 * The client calls that interacts with the server using RMI (Remote Method Invocation).
 * It also implements the methods defined in the ClientCallback interface, which can be 
 * called by the server using a callback.
 * 
 * @author Tom Couchman
 */
public class RMIClient extends UnicastRemoteObject implements ClientCallback, AutoCloseable {

	/**
	 * The interval in milliseconds between heartbeats
	 */
	private final int HEARTBEAT_INTERVAL = 2000;
	
	/**
	 * A reference to the remote server
	 */
	private RemoteServer server;
	
	/**
	 * The URI/IP of the server
	 */
	private String serverAddress;
	
	/**
	 * The port of the server
	 */
	private int serverPort;

	/**
	 * The controller of the client application
	 */
	private RevController controller;
	
	/**
	 * Boolean to hold the current status of the server.
	 */
	private ConnectionState serverAccessible;

	/**
	 * The ID assigned by the remote server that allows the server to identify the client
	 */
	private String clientID;

	/**
	 * The host name of the local computer
	 */
	private String localHostname;

	/**
	 * Timer that calls the heartbeat messages to ensure the server is running
	 */
	Timer heartBeatTimer; 
	
	/**
	 * Constructor for RMIClient
	 * @throws RemoteException	Exception thrown when an communication issue occurs during RMI
	 * @throws AuthenticationException 
	 */
	protected RMIClient(RevController controller, String serverAddress, int port, String localhost, String username, String password) throws RemoteException, NotBoundException, MalformedURLException, AuthenticationException {
		super();
		
		this.serverAddress = serverAddress;
		this.serverPort = port;
		this.localHostname = localhost;
		this.controller = controller;
		
		try {
			
			// Set up the connection to the server, and register for updates.
			initConnection(username, password);
			
		} catch (RemoteException | MalformedURLException | NotBoundException e) {
			// Change the connectionstate and inform the user
			serverAccessible = ConnectionState.CONNECTION_ERROR;
			controller.serverStatusChanged(serverAccessible);
			
			// End connection to server and remove the RMIClient from the RMI runtime
			close();
			
			// Re-throw the exception to be handled elsewhere
			throw e;
		}
		
		// Create a timer to handle pings
		heartBeatTimer = new Timer();

		// Set the time to repeat every 5 seconds and run the timedPing Timer Task each tick
		heartBeatTimer.scheduleAtFixedRate(new timedPing(), HEARTBEAT_INTERVAL, HEARTBEAT_INTERVAL);
			

	}
	
	/**
	 * Sets up the connection to the server, and register for updates.
	 * @throws RemoteException			Exception thrown when an communication issue occurs during RMI
	 * @throws MalformedURLException	The URL provided for the connection could not be parsed
	 * @throws NotBoundException 		The name looked up in the call to Naming.lookup() has no associated binding
	 * @throws AuthenticationException 	Thrown when the user login details are incorrect.
	 */
	public void initConnection(String username, String password) throws RemoteException, MalformedURLException, NotBoundException, AuthenticationException {
		lookUpServer();
		regsiterWithServer(username, password) ;
	}
	
	public void lookUpServer() throws MalformedURLException, RemoteException, NotBoundException {
		serverAccessible = ConnectionState.CONNECTING;
		controller.serverStatusChanged(serverAccessible);
		
		// If a local host name has been provided
		if (!localHostname.equals("")) {
			// Set it as a system property
			System.setProperty("java.rmi.server.hostname",localHostname);
		}
		
		// Get a reference to the server stub using a RMI URL built comprising of the server address and port 
		server = (RemoteServer)Naming.lookup("rmi://" + serverAddress + ":" + serverPort + "/HQBertServer");
	}
	
	/**
	 * Register with the server
	 * @throws RemoteException 
	 * @throws AuthenticationException 
	 */
	public void regsiterWithServer(String username, String password) throws RemoteException, AuthenticationException {
		// Register for the update callbacks. This passes a reference
		// of the client to the server so the 'update' method can be called remotely.
		clientID = server.register(username, password, this);
		
		if (clientID.equals("")) {
			serverAccessible = ConnectionState.NOT_CONNECTED;
			controller.serverStatusChanged(serverAccessible);
			throw new AuthenticationException("Login Failed");
		}
		
		// The user has registered successfully, so set serverAccessible to true
		serverAccessible = ConnectionState.CONNECTED;
		controller.serverStatusChanged(serverAccessible);
	}
	/**
	 * TimerTask that pings the server to ensure that it is still accessible
	 */
	public class timedPing extends TimerTask {
		@Override
		public void run() {
			
			// Boolean to hold whether or not the server is running
			ConnectionState accessible;
			
			// If the server is null can't continue so tell the user to login and return
			if (server == null) {
				controller.alertLoggedOff();
				return;
			}
			
			try {
				// Ping the server
				boolean pingResult = server.heartbeat(clientID);
				// If no error was thrown the server is connected
				accessible = ConnectionState.CONNECTED;
				// A result of false means that the client is not registered
				if (pingResult == false) {
					System.out.println("Server accessible but client not registered.");
					// Set the connection state to not connected
					if (serverAccessible != ConnectionState.NOT_CONNECTED) {
						serverAccessible = ConnectionState.NOT_CONNECTED;
						controller.serverStatusChanged(serverAccessible);
						// tell the user they are logged off
						controller.alertLoggedOff();
						
						heartBeatTimer.cancel();
					}
					return;
				}
			} catch (RemoteException e) {
				System.err.println("Server not accessible.");
				// Communication with the server has failed, so set running to false;
				accessible = ConnectionState.CONNECTION_ERROR;
			}
			
			// If the status has changed since last check, inform the controller
			if (accessible != serverAccessible) {
				controller.serverStatusChanged(accessible);
			}
			// update the value help in serverAccessible
			serverAccessible = accessible;
			
			// I accessible is false, try to look up the server again
			if (accessible == ConnectionState.CONNECTION_ERROR) {
				try {
					lookUpServer();
				} catch (MalformedURLException | RemoteException
						| NotBoundException e) {
					
				}
			}
		}

	}
	
	/**
	 * Callback method defined in the ClientCallback interface. This method can be 
	 * invoked remotely from the server as a callback, whenever the queue is updated.
	 * It updates the local state of the queue to match the remote changes.
	 * 
	 * @param queue					The updated patient queue sent from the server
	 * @throws RemoteException		Exception thrown when an communication issue occurs during RMI
	 */
	@Override
	public void udpate(List<Patient> queue, List<TreatmentFacility> treatmentFacilities, List<Staff> onCallStaff, List<Staff> onCallDoctors) throws RemoteException {

		// Pass the callback call onto the controller
		controller.udpate(queue, treatmentFacilities, onCallStaff, onCallDoctors);
	}
	
	/**
	 * Sends log messages to the client
	 * @param log				The log text
	 * @throws RemoteException	Exception thrown when an communication issue occurs during RMI
	 */
	@Override
	public void log(String message) throws RemoteException {
		controller.log(message);
	};
	
	/**
	 * Display the next patient to be sent to a room
	 */
	@Override
	public void notifyNextPatientToRoom(String message) throws RemoteException {
		
		// Pass on the message to the controller
		controller.notifyNextPatientToRoom(message);
		
	}
	
	/**
	 * Informs the server that the client no longer wished to receive updates and removes the client
	 * from the RMI runtime.
	 */
	@Override
	public void close() {
		
		// If the heartbeat timer has been instantiated
		if (heartBeatTimer != null) {
			// Cancel the heartbeat timer
			heartBeatTimer.cancel();
		}
	

		if (server != null) {
			try {
				// Unregister for updates
				server.deregister(clientID);
				serverAccessible = ConnectionState.NOT_CONNECTED;
				controller.serverStatusChanged(serverAccessible);
			} catch (RemoteException e) {
				System.err.println("Failed to unregister from server updates.");
				serverAccessible = ConnectionState.CONNECTION_ERROR;
				controller.serverStatusChanged(serverAccessible);
			}
		}
		
		// Set the server reference to null
		server = null;
		
		try {
			// Remove the RMIClient from the RMI runtime
			RMIClient.unexportObject(this, true);
		} catch (NoSuchObjectException e) {
			System.err.println("Failed to remove RMIClient from RMI run time.");
		}
		
	}
	
	public RemoteServer getServer() {
		return this.server;
	}

	@Override
	public void alertQueueFull() throws RemoteException {
		
	}
	
	/** Getter for the client ID */
	public String getClientID() {
		return clientID;
	}
	
	/** Getter for server address */
	public String getServerAddress() {
		return serverAddress;
	}

	/** Setter for server address */
	public void setServerAddress(String serverAddress) {
		this.serverAddress = serverAddress;
	}

	/** Getter for server port */
	public int getServerPort() {
		return serverPort;
	}

	/** Setter for server port */
	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}
	
	
}
