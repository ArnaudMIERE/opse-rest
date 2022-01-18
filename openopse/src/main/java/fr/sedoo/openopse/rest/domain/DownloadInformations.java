package fr.sedoo.openopse.rest.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DownloadInformations {

	private int downloadFileNumber;
	private long downloadSize;
	
		
	public DownloadInformations() {
		super();this.downloadFileNumber = 0;
		this.downloadSize = 0;
	}
	public DownloadInformations(int downloadFileNumber, long downloadSize) {
		super();
		this.downloadFileNumber = downloadFileNumber;
		this.downloadSize = downloadSize;
	}
}
