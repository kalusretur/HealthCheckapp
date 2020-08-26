package itms.com.pe.utils;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class HtmlEmailSender {

	public void sendHtmlEmail(String host, String port,
			final String userName, final String password, String toAddress,
			String subject, String message) throws AddressException,
			MessagingException, UnsupportedEncodingException {

		// sets SMTP server properties
		Properties properties = new Properties();
		properties.put("mail.smtp.host", host);
		properties.put("mail.smtp.port", port);
		properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.starttls.enable", "true");

		// creates a new session with an authenticator
		Authenticator auth = new Authenticator() {
			public PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(userName, password);
			}
		};

		Session session = Session.getInstance(properties, auth);

		// creates a new e-mail message
		Message msg = new MimeMessage(session);

		msg.setFrom(new InternetAddress(userName, "Telemedicina de Perú"));
		InternetAddress[] toAddresses = { new InternetAddress(toAddress) };
		msg.setRecipients(Message.RecipientType.TO, toAddresses);
		msg.setSubject(subject);
		msg.setSentDate(new Date());
		msg.addRecipient(RecipientType.BCC, new InternetAddress("soporte@itms.com.pe"));
		// set plain text message
		msg.setContent(message, "text/html");

		// sends the e-mail
		Transport.send(msg);

	}

	/**
	 * Test the send html e-mail method
	 *
	 */
	public static void main(String[] args) {
		// SMTP server information
		String host = "smtp.gmail.com";
		String port = "587";
		String mailFrom = "alertas.itms@gmail.com";
		String password = "Morris741TmsP3ru";

		// outgoing message information
		String mailTo = "soporte@itms.com.pe";
		String subject = "Agendamiento de Paciente ";

		// message contains HTML markups
		String message = "<?php ?>"
				+ "<html>	<body><table border='0' width='566' style='font-size: 15px'>"
				+ "<tr><td  width='100%' colspan='2'><img style='display:block;margin:0 auto 0 auto;' src='http://www.pitperu.com.pe/app/cabezera.png'></td></tr>"
				+ "<tr><td width='100%' colspan='2'><hr style='background-color: #0080ff; height: 3px;' /></td></tr>"
				+ "<tr><td colspan='2'>Estimados,</td></tr>"
				+ "<tr><td colspan='2'>Se comunica que se acaba de programar la cita para la realización del siguiente procedimiento</td></tr>"
				+ "<tr><td><br></td></tr>"
				+ "<tr><td style='height: 4px'>Paciente : </td>	<td style='height: 4px'><b>SALVADOR CALZADA CARLOS EDUARDO </b></td>	</tr>"
				+ "<tr><td>Procedimiento :</td><td><b>MAPA</b></td></tr>"
				+ "<tr><td>Tipo Paciente :</td><td>Farmaindustria</td></tr>"
				+ "<tr><td>Dirección :</td><td>Av. Victor Raúl Haya de la Torre 601</td></tr>"
				+ "<tr><td>Referencia</td><td>Frente a la municipalidad de La Perla</td></tr>"
				+ "<tr><td>Médico Solicitante :</td><td>F.I. - DR. ALFREDO SARAVIA SANCHEZ</td></tr>"
				+ "<tr><td>Fecha Programada :</td><td><font color=red</font> 02/05/2017 10:08 AM</td></tr>"
				+ "<tr><td>Registrado Por :</td><td>KDELGADO</td></tr>"
				+ "<tr><td><br><br></td></tr>"
				+ "<tr><td align='center'>Saludos Cordiales<br> <font color=blue>Sistemas ITMS.</font></td></tr>"
				+ "</table></body></html>";
		
		message += "";

		HtmlEmailSender mailer = new HtmlEmailSender();

		try {
			mailer.sendHtmlEmail(host, port, mailFrom, password, mailTo,
					subject, message);
			System.out.println("Email sent.");
		} catch (Exception ex) {
			System.out.println("Failed to sent email.");
			ex.printStackTrace();
		}
	}
}