package fr.sedoo.openopse.rest.service.v1_0;

import org.apache.commons.mail.SimpleEmail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import fr.sedoo.openopse.rest.config.MailConfig;
import fr.sedoo.openopse.rest.domain.EmailSender;
import fr.sedoo.openopse.rest.domain.User;

@Component
public class EmailConfirmation {

	@Autowired
	EmailSender emailSender;

	@Autowired
	MailConfig mailConfig;
	
	@Value("${mail.confirmUrl}")
	String rootUrl;
	
	@Value("${mail.msecContactEmail}")
	String msecContactEmail;
	
	@Value("${mail.sedooContactEmail}")
	String sedooContactEmail;
	
	public void sendConfirm (User user) {
		String subject = mailConfig.getSubjectPrefix() + " New User";
		String content = "Dear Pi, \n\nUser "+user.getName()+" ask access to database Mtropics. To confirm registration, please click on the following link: "+rootUrl+ "?email=" + user.getEmail() + "\n\n"
				+"Name: "+user.getName()+"\n"
				+"Mail: "+user.getEmail()+"\n"
				+"Category: "+user.getCategory()+"\n"
				+"Organism: "+user.getOrganism()+"\n"
				+"Purpose: "+user.getPurposesList()+"\n"
				+"Data use: "+user.getDataUse()+"\n"
				+"Country: "+user.getCountry()+
				"\n\nThank you very much! \n\nThe Mtropics Team";
		
		SimpleEmail mail = new SimpleEmail();
		mail.setHostName(mailConfig.getHostname());
		try {
			mail.addTo(msecContactEmail);
			String[] sedooContact = sedooContactEmail.split(",");
			for (String t : sedooContact) {
			mail.addCc(t);
			}
			mail.setFrom(mailConfig.getFrom());
			mail.setSubject(subject);
			emailSender.send(mail, content);
			
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void validRegistration(String email) {
		String subject = mailConfig.getSubjectPrefix() + " New User";
		String content = "Your registration is now confirmed. Thank you very much. \n\nThe Mtropics Team";
		
		SimpleEmail mail = new SimpleEmail();
		mail.setHostName(mailConfig.getHostname());
		try {
			mail.addTo(email);
			mail.addTo(msecContactEmail);
			String[] sedooContact = sedooContactEmail.split(",");
			for (String t : sedooContact) {
			mail.addCc(t);
			}
			mail.setFrom(mailConfig.getFrom());
			mail.setSubject(subject);
			emailSender.send(mail, content);
			
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
}
