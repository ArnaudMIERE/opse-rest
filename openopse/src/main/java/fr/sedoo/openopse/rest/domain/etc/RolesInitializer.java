package fr.sedoo.openopse.rest.domain.etc;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.sedoo.openopse.rest.config.ApplicationConfig;
import fr.sedoo.sso.utils.TokenProvider;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class RolesInitializer {

	@Autowired
    ApplicationConfig config;

	@PostConstruct
	 public void initClientAndRoles() {
		 
		try {
			String token = TokenProvider.getToken(config.getLogin(), config.getPassword());
		}catch (Exception e) {
			log.error("An error has occured while creating clients and roles: "+ExceptionUtils.getFullStackTrace(e));
		}
	 }
}
