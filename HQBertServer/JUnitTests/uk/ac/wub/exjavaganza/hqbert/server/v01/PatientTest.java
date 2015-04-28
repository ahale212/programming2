package uk.ac.wub.exjavaganza.hqbert.server.v01;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import uk.ac.qub.exjavaganza.hqbert.server.v01.Patient;
import uk.ac.qub.exjavaganza.hqbert.server.v01.Person;
import uk.ac.qub.exjavaganza.hqbert.server.v01.Supervisor;
import uk.ac.qub.exjavaganza.hqbert.server.v01.Urgency;

public class PatientTest {

	Patient patient;

	static Urgency urgency;

	boolean priority;

	Date entryDate;

	int waitTime;

	Person person;

	String patientName;
	
	boolean isPriority;

	@Before
	public void setUp() throws Exception {

		person = new Person("1010101010", "Mr", "Alan", "Simms", "12.06.89",
				"123 Main Street", "Belfast", "UK", "BT56 7TE", "0712345678",
				"Yeast", "A+","Doctors' notes");
		
		patientName = "Simms, Alan";
		
		urgency = Urgency.SEMI_URGENT;
		
		waitTime = 40;
		
		
		
	}

	@Test
	public void testPatient() {

		Patient patient = new Patient();
		assertNotNull(patient);
	}

	@Test
	public void testPatientConstructorWithArgs() {

		Patient patient = new Patient(person, urgency);
		assertNotNull(patient);

		assertEquals(person, patient.getPerson());
		assertEquals(urgency, patient.getUrgency());
		

	}
	

	@Test
	public void testIncrementWaitTime() {
		fail("Not yet implemented");
	}

	@Test
	public void testCheckPriority() {
		
		Patient patient = new Patient();
		priority = true;
		patient.setPriority(priority);
		assertEquals(priority, patient.getPriority());
		
	}
	
	@Test
	public void testCompareTo() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetUrgency() {
		Patient patient = new Patient();
		patient.setUrgency(urgency);
		assertEquals(urgency, patient.getUrgency());
	}

	@Test
	public void testSetPriority() {
		Patient patient = new Patient();
		isPriority = true;
		patient.setPriority(isPriority);
		assertEquals(isPriority, patient.getPriority());
	}

	@Test
	public void testSetPerson() {
		Patient patient = new Patient();
		patient.setPerson(person);
		assertEquals(person, patient.getPerson());
		
	}

	@Test
	public void testSetPatientName() {
		Patient patient = new Patient(person, null);
		patient.setPatientName(person);
		assertEquals(patientName, patient.getPatientName());
	}

}
