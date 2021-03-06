package comms;

import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class OnCallTeamAlert {

	/**
	 * Method to send automated sms to the On Call Team when when all the
	 * treatment rooms are engaged with emergency priorities and a new emergency
	 * patient enters the system.
	 */
	public static void onCallEmergencyPriority() {

		String username = "awhitten02";
		String password = "71Great7";
		String smtphost = "ipipi.com";
		String compression = "None";
		String from = "awhitten02@ipipi.com";
		String to = "+447821063144@sms.ipipi.com";
		String to1 = "+447759351906@sms.ipipi.com";
		String body = "All treatment rooms engaged! New emergency priority!";
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
			InternetAddress[] address = { new InternetAddress(to), new InternetAddress(to1) };
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
	 * Method to send automated sms to the on call team when the hospital list
	 * reaches 10 patients.
	 */
	public static void onCallTeamQueueCapacity() {

		String username = "awhitten02";
		String password = "71Great7";
		String smtphost = "ipipi.com";
		String compression = "None";
		String from = "awhitten02@ipipi.com";
		String to = "+447821063144@sms.ipipi.com";
		String body = "The A&E Queue is at capacity!";
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
