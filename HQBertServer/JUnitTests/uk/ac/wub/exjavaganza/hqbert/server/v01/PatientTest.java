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

	static Urgency urgency;

	boolean priority;

	Date entryDate;

	int differenceUnder;

	Person person;

	String patientName;
	
	boolean isPriority;
	
	int difference;
	

	@Before
	public void setUp() throws Exception {

		person = new Person("1010101010", "Mr", "Alan", "Simms", "12.06.89",
				"123 Main Street", "Belfast", "UK", "BT56 7TE", "0712345678",
				"Yeast", "A+","Doctors' notes");
		
		patientName = "Simms, Alan";
		
		urgency = Urgency.SEMI_URGENT;
		
		differenceUnder = 10;
		
		difference = 40;
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
	public void testIncrementWaitTimeOver() {
		
		Patient patient = new Patient();
		priority = true;
		patient.incrementWaitTime(difference);
		assertEquals(priority, patient.getPriority());
	}

	@Test
	public void testIncrementWaitTimeUnder() {
		
		Patient patient = new Patient();
		priority = false;
		patient.incrementWaitTime(differenceUnder);
		assertEquals(priority, patient.getPriority());
	}

	@Test
	public void testSetUrgency() {
		Patient patient = new Patient();
		patient.setUrgency(urgency);
		assertEquals(urgency, patient.getUrgency());
	}

	@Test
	public void testSetPriorityTrue() {
		Patient patient = new Patient();
		isPriority = true;
		patient.setPriority(isPriority);
		assertEquals(isPriority, patient.getPriority());
	}
	
	@Test
	public void testSetPriorityFalse() {
		Patient patient = new Patient();
		isPriority = false;
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
