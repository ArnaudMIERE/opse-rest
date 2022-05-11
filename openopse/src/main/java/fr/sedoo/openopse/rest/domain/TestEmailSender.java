package fr.sedoo.openopse.rest.domain;

import org.apache.commons.mail.SimpleEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import fr.sedoo.openopse.rest.config.Profiles;

@Component
@Profile("!" + Profiles.PRODUCTION_PROFILE)
public class TestEmailSender implements EmailSender {

	private static final Logger LOG = LoggerFactory.getLogger(TestEmailSender.class);

	@Override
	public void send(SimpleEmail email, String content) throws Exception {

		LOG.info("The following message would have been sent");
		LOG.info(content);
		LOG.info("Recipients: " + email.getToAddresses().toString().replace('[', ' ').replace(']', ' ').trim());
		LOG.info("Bcc Recipients: " + email.getBccAddresses().toString().replace('[', ' ').replace(']', ' ').trim());
	}

}
