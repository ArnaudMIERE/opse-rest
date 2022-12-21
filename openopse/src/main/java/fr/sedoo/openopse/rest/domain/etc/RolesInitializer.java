package fr.sedoo.openopse.rest.domain.etc;




import javax.annotation.PostConstruct;


import org.apache.commons.lang.exception.ExceptionUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;



import fr.sedoo.openopse.rest.config.ApplicationConfig;
import fr.sedoo.openopse.rest.domain.Project;
import fr.sedoo.sso.utils.ClientProvider;
import fr.sedoo.sso.utils.RoleProvider;
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
			ClientProvider.createBackendClient("catalogue-opse-services", token);
			
			RestTemplate restTemplate = new RestTemplate();
			String url = "https://api.sedoo.fr/opse-catalogue-prod/metadata/projects?language=en&all=true";
			
			String urlCache = "https://api.sedoo.fr/sedoo-access-request-rest/projects/refreshCache";
			
			//Project[] projects = restTemplate.getForObject(url, Project[].class);
			
			ResponseEntity<Project[]> response =   restTemplate.getForEntity(url ,Project[].class);
			Project[] projects = response.getBody();
			 
			for (Project project : projects) {
                for (int i = 0; i<project.getThesaurusItems().size(); i++) {
                	RoleProvider.createRole(project.getThesaurusItems().get(i).name.toUpperCase()+"_METADATA_EDITOR", "catalogue-opse-services", token);
                }
            } 
			
			ResponseEntity<?> cachedResponse =   restTemplate.getForEntity(urlCache ,Object.class);
			cachedResponse.getBody();
			
			
		}catch (Exception e) {
			log.error("An error has occured while creating clients and roles: "+ExceptionUtils.getFullStackTrace(e));
		}
	 }
	
	
}
