package fr.sedoo.openopse.rest.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
public class ApplicationConfig {
	
	@Value("${openOpse.data}")
	private String openOpseFolderName;

	@Value("${local.baseFolder}")
	private String temporaryDownloadFolderName;
	
	@Value("${openOpse.depot}")
	private String openOpseDepotFolder;

	

	

	

}
