package fr.sedoo.openopse.rest.service.v1_0;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fr.sedoo.openopse.rest.config.ApplicationConfig;
import fr.sedoo.openopse.rest.config.MailConfig;
import fr.sedoo.openopse.rest.domain.EmailSender;
import lombok.extern.java.Log;

@RestController
@CrossOrigin
@RequestMapping(value = "/contact")
public class ContactService {
	
	@Autowired
	EmailSender emailSender;

	@Autowired
	ApplicationConfig config;
	
		
	private static final Logger LOG = LoggerFactory.getLogger(ContactService.class);
	
	@RequestMapping(value = "/send", method = { RequestMethod.POST })
	public void send(HttpServletRequest request, HttpServletResponse response,
			@RequestHeader("Authorization") String authHeader, 
			@RequestParam String email, /*@RequestParam String name,*/ @RequestParam String resourceTitle) throws  EmailException {
		try {
			sendMessage(email, resourceTitle);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
		
	}
	
	private void sendMessage(String email,  String resourceTitle) throws Exception {

		
		String subject = config.getSubjectPrefix() + " Data download " ;
		
		

		String message = "Dear PI, \n\n  Data corresponding to the dataset '"+ resourceTitle +"' have been downloaded and received by the user"
		+"\n\n Regards, \n\nOPSE Team";

		SimpleEmail spl = new SimpleEmail();
		
		spl.setHostName(config.getHostname());
		try {
			
			String[] sedooContact = email.split(",");
			for (String t : sedooContact) {
				spl.addTo(t);
			}
			spl.setFrom(config.getFrom());
			spl.addBcc("arnaud.miere@obs-mip.fr");
			spl.setSubject(subject);
			emailSender.send(spl, message);
			
			LOG.info("email sent");
			
		}catch (Exception e) {
			e.printStackTrace();
		}

	}

}
