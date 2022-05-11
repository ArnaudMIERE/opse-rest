package fr.sedoo.openopse.rest.service.v1_0;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fr.sedoo.openopse.rest.config.MailConfig;
import fr.sedoo.openopse.rest.domain.EmailSender;

@RestController
@CrossOrigin
@RequestMapping(value = "/contact")
public class ContactService {
	
	@Autowired
	EmailSender emailSender;

	@Autowired
	MailConfig mailConfig;
	
	@Value("${mail.msecContactEmail}")
	String msecContactEmail;
	
	@Value("${mail.sedooContactEmail}")
	String sedooContactEmail;
	
	@RequestMapping(value = "/send", method = { RequestMethod.POST })
	public void send(HttpServletRequest request, HttpServletResponse response,
			@RequestHeader("Authorization") String authHeader, 
			@RequestParam String email, @RequestParam String name, @RequestParam String message) throws  EmailException {
		try {
			sendMessage(email,name, message);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
		
	}
	
	private void sendMessage(String mail, String name,  String msg) throws Exception {

		String emailAddress = mail;
		String n = name;


		String subject = mailConfig.getSubjectPrefix() + " " ;
		String body = "Name: " + n + "\n" + "Email: " + emailAddress + "\n" 
				+ "\n";

		body += msg;

		SimpleEmail spl = new SimpleEmail();
		spl.setHostName(mailConfig.getHostname());
		try {
			spl.addTo(msecContactEmail);
			String[] sedooContact = sedooContactEmail.split(",");
			for (String t : sedooContact) {
				spl.addCc(t);
			}
			spl.setFrom(mailConfig.getFrom());
			spl.setSubject(subject);
			emailSender.send(spl, body);
			
		}catch (Exception e) {
			e.printStackTrace();
		}

	}

}
