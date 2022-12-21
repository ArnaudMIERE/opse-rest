package fr.sedoo.openopse.rest.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
public class ApplicationConfig {
	
	
	@Value("${sso.login}")
	private String login;
	
	@Value("${sso.password}")
	private String password;
	
	
	@Value("${mail.hostname}")
	private String hostname;
	
	@Value("${mail.subjectPrefix}")
	private String subjectPrefix;
	
	@Value("${mail.from}")
	private String from;
	
	
	@Value("${openOpse.data}")
	private String openOpseFolderName;

	@Value("${local.baseFolder}")
	private String temporaryDownloadFolderName;
	
	@Value("${local.baseFolder}/geojson")
	private String temporaryGeojsonFolderName;
	
	@Value("${openOpse.depot}")
	private String openOpseDepotFolder;

	public String getOpenOpseFolderName() {
		return openOpseFolderName;
	}

	public void setOpenOpseFolderName(String openOpseFolderName) {
		this.openOpseFolderName = openOpseFolderName;
	}

	public String getTemporaryDownloadFolderName() {
		return temporaryDownloadFolderName;
	}

	public void setTemporaryDownloadFolderName(String temporaryDownloadFolderName) {
		this.temporaryDownloadFolderName = temporaryDownloadFolderName;
	}

	public String getOpenOpseDepotFolder() {
		return openOpseDepotFolder;
	}

	public void setOpenOpseDepotFolder(String openOpseDepotFolder) {
		this.openOpseDepotFolder = openOpseDepotFolder;
	}
	@Value("${etc.python.venvdir}")
	private String virtualEnvironmentFolderName;
	
	@Value("${etc.python.convertMneToPngLauncherScript}")
	private String convertMneToPngLauncherScript;
	
	@Value("${etc.python.spipEtcLauncherScript}")
	private String spipLauncherScript; 
	
	@Value("${etc.python.scriptdir}")
	private String scriptFolderName;
	
	@Value("${etc.python.etcInstallScript}")
	private String installScript;

	

	

	

}
