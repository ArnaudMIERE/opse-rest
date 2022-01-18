package fr.sedoo.openopse.rest.domain;

import org.apache.commons.mail.SimpleEmail;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import fr.sedoo.openopse.rest.config.Profiles;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@Profile(Profiles.PRODUCTION_PROFILE)
public class ProdEmailSender implements EmailSender {

	@Value("${mail.hostname}")
	private String hostname;

	@Value("${mail.from}")
	private String from;

	@Override
	public void send(SimpleEmail email, String content) throws Exception {
		email.setHostName(getHostname());
		email.setFrom(getFrom());
		email.setMsg(content);
		email.send();
	}

}
