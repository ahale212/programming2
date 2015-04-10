package uk.ac.qub.exjavaganza.hqbert.server.v01;

public class HQBertServer {

	public static void main(String[] args) {
		
		System.out.println("Starting");
	
		Supervisor.INSTANCE.clock.initClock();
		Supervisor.INSTANCE.startLoop();

	}

}
