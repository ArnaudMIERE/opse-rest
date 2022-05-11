package fr.sedoo.openopse.rest.domain;

import org.apache.commons.mail.SimpleEmail;

public interface EmailSender {

	void send(SimpleEmail email, String content) throws Exception;

}