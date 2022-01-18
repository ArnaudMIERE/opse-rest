package fr.sedoo.openopse.rest.route;

import java.io.IOException;

public interface CamelFileDispatcher {
	
	/**
	 * Save camel idempotent repository file on SEDOO FTP
	 * @throws IOException 
	 */
	void saveFileIdempotentRepository() throws IOException;
	
	/**
	 * Download idempotent repository file from SEDOO FTP
	 * @throws IOException
	 */
	void getFileIdempotentRepository() throws IOException;

}
