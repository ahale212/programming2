/**
 * 
 */
package comms;

import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;

/**
 * Class to send automated email messages to the hospital manager when called.
 * 
 * @author Alan
 *
 */
public class ManagerAlert {

	/**
	 * Method to send the automated email when the A&E is at capacity.
	 */
	public static void emailCapacityAlert() {
		// Recipient's email ID needs to be mentioned.
		String to = "hospitalmanager10@gmail.com";

		// Sender's email ID needs to be mentioned
		String from = "pashospital@gmail.com";

		// Get system properties
		Properties properties = System.getProperties();

		properties.put("mail.smtp.starttls.enable", "true");
		properties.put("mail.smtp.host", "smtp.gmail.com");

		properties.put("mail.smtp.port", "587");
		properties.put("mail.smtp.auth", "true");
		Authenticator authenticator = new Authenticator() {
			public PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("pashospital@gmail.com",
						"hospitalsystem");// userid and password for "from"
											// email
											// address
			}
		};

		Session session = Session.getDefaultInstance(properties, authenticator);
		try {
			// Create a default MimeMessage object.
			MimeMessage message = new MimeMessage(session);

			// Set From
			message.setFrom(new InternetAddress(from));

			// Set To
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(
					to));

			// Set Subject: header field
			message.setSubject("A&E ALERT");

			// Now set the actual message
			message.setText("All treatment rooms are full and the on call team is engaged");

			// Send message
			Transport.send(message);
			System.out.println("Sent message successfully....");
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Method to send the automated email when two people have been waiting more
	 * than 30 minutes.
	 */
	public static void emailWaitingTimeAlert() {
		// Recipient's email ID.
		String to = "hospitalmanager10@gmail.com";

		// Sender's email ID.
		String from = "pashospital@gmail.com";

		// Get system properties
		Properties properties = System.getProperties();

		properties.put("mail.smtp.starttls.enable", "true");
		properties.put("mail.smtp.host", "smtp.gmail.com");

		properties.put("mail.smtp.port", "587");
		properties.put("mail.smtp.auth", "true");

		Authenticator authenticator = new Authenticator() {
			public PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("pashospital@gmail.com",
						"hospitalsystem");// user id and password for "from"
											// email
											// address
			}
		};

		Session session = Session.getDefaultInstance(properties, authenticator);
		try {
			// Create a default MimeMessage object.
			MimeMessage message = new MimeMessage(session);

			// Set From
			message.setFrom(new InternetAddress(from));

			// Set To
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(
					to));

			// Set Subject: header field
			message.setSubject("A&E ALERT");

			// Now set the actual message
			message.setText("Two patients have been waiting more than 30 minutes");

			// Send message
			Transport.send(message);
			System.out.println("Sent message successfully....");
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Method to send automated sms to the Hospital Manager when the hospital
	 * list is at capacity.
	 */
	public static void smsCapacityAlert() {
		String username = "awhitten02";
		String password = "71Great7";
		String smtphost = "ipipi.com";
		String compression = "None";
		String from = "awhitten02@ipipi.com";
		String to = "+447821063144@sms.ipipi.com";
		String body = "All treatment rooms are full and the on call team is engaged";
		Transport tr = null;

		try {
			Properties props = System.getProperties();
			props.put("mail.smtp.auth", "true");
			
			// Get a Session object
			Session mailSession = Session.getDefaultInstance(props, null);

			// construct the message
			Message msg = new MimeMessage(mailSession);

			// Set message attributes
			msg.setFrom(new InternetAddress(from));
			InternetAddress[] address = { new InternetAddress(to) };
			msg.setRecipients(Message.RecipientType.TO, address);
			msg.setSubject(compression);
			msg.setText(body);
			msg.setSentDate(new Date());

			tr = mailSession.getTransport("smtp");
			tr.connect(smtphost, username, password);
			msg.saveChanges();
			tr.sendMessage(msg, msg.getAllRecipients());
			tr.close();
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Method to send automated sms to the Hospital Manager when two patients
	 * have exceeded the waiting time.
	 */
	public static void smsWaitingTimeAlert() {
		String username = "awhitten02";
		String password = "71Great7";
		String smtphost = "ipipi.com";
		String compression = "None";
		String from = "awhitten02@ipipi.com";
		String to = "+447821063144@sms.ipipi.com";
		String body = "Two patients have been waiting more than 30 minutes";
		Transport tr = null;

		try {
			Properties props = System.getProperties();
			props.put("mail.smtp.auth", "true");

			// Get a Session object
			Session mailSession = Session.getDefaultInstance(props, null);

			// construct the message
			Message msg = new MimeMessage(mailSession);

			// Set message attributes
			msg.setFrom(new InternetAddress(from));
			InternetAddress[] address = { new InternetAddress(to) };
			msg.setRecipients(Message.RecipientType.TO, address);
			msg.setSubject(compression);
			msg.setText(body);
			msg.setSentDate(new Date());

			tr = mailSession.getTransport("smtp");
			tr.connect(smtphost, username, password);
			msg.saveChanges();
			tr.sendMessage(msg, msg.getAllRecipients());
			tr.close();
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}

}
