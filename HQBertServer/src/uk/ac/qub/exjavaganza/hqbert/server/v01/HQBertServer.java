package uk.ac.qub.exjavaganza.hqbert.server.v01;

public class HQBertServer {

	public static void main(String[] args) {
		
		System.out.println("Starting");

		Person testPerson = new Person();
		testPerson.firstName = "Bobby";
		testPerson.lastName = "Branson";
		
		Patient test = new Patient();
		test.person = testPerson;
		
		Supervisor.INSTANCE.hQueue.pq.add(test);
		
		Supervisor.INSTANCE.clock.initClock();
		Supervisor.INSTANCE.startLoop();

	}

}
