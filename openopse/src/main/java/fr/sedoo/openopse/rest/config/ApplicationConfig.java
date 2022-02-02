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

	

	

	

}
