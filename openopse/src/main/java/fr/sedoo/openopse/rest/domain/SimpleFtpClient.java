package fr.sedoo.openopse.rest.domain;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

public class SimpleFtpClient {

	Logger logger = LoggerFactory.getLogger(SimpleFtpClient.class);

	private FtpConfiguration ftpConfiguration;

	public SimpleFtpClient(FtpConfiguration ftpConfiguration) {
		this.ftpConfiguration = ftpConfiguration;
	}

	public List<FTPFile> getContent() {
		return getContent(null);
	}

	public List<FTPFile> getContent(String folderName) {

		FTPClient client = new FTPClient();

		try {
			client.connect(ftpConfiguration.getHost());
			client.setFileType(FTP.BINARY_FILE_TYPE, FTP.BINARY_FILE_TYPE);
			client.setFileTransferMode(FTP.BINARY_FILE_TYPE);
			client.enterLocalPassiveMode();
			client.login(ftpConfiguration.getLogin(), ftpConfiguration.getPassword());
			if (folderName != null) {
				client.changeWorkingDirectory(folderName);
			}
			FTPFile[] listFiles = client.listFiles();
			client.disconnect();
			return Lists.newArrayList(listFiles);
		} catch (Exception e) {
			return new ArrayList<>();
		}
	}

	public void downloadContent(File requestFolder, String folderName, DomainFilter domainFilter) throws Exception {
		FTPClient client = new FTPClient();

		client.connect(ftpConfiguration.getHost());
		client.setFileType(FTP.BINARY_FILE_TYPE, FTP.BINARY_FILE_TYPE);
		client.setFileTransferMode(FTP.BINARY_FILE_TYPE);
		client.enterLocalPassiveMode();
		client.login(ftpConfiguration.getLogin(), ftpConfiguration.getPassword());
		if (folderName != null) {
			boolean result = client.changeWorkingDirectory(folderName);
			if (result == false) {
				throw new Exception("The corresponding folder doesn't exist");
			}
		}
		FTPFile[] listFiles = client.listFiles();

		for (int i = 0; i < listFiles.length; i++) {
			downloadFile(requestFolder, listFiles[i], client, domainFilter);
		}

		client.disconnect();

	}

	private void downloadFile(File localFolder, FTPFile ftpFile, FTPClient client, DomainFilter domainFilter)
			throws Exception {

		if (ftpFile.isFile()) {
			if (!isDownloadableFile(ftpFile)) {
				return;
			}
			if (domainFilter.isFiltered(ftpFile.getName())) {

				File localFile = new File(localFolder, ftpFile.getName());
				try (FileOutputStream fos = new FileOutputStream(localFile)) {
					client.setFileType(FTP.BINARY_FILE_TYPE, FTP.BINARY_FILE_TYPE);
					client.setFileTransferMode(FTP.BINARY_FILE_TYPE);
					logger.info("Downloading file: " + ftpFile.getName());
					client.retrieveFile(ftpFile.getName(), fos);
					logger.info("Download completed");

				} catch (IOException e) {
					throw new Exception("An error has occured while downloading file: " + ftpFile.getName());
				}

			} else {
				logger.info(ftpFile.getName() + " won't be downloaded because of the filter");
			}

		} else {
			File localSubFolder = new File(localFolder, ftpFile.getName());
			localSubFolder.mkdirs();
			client.changeWorkingDirectory(ftpFile.getName());
			FTPFile[] listFiles = client.listFiles();
			for (int i = 0; i < listFiles.length; i++) {
				downloadFile(localSubFolder, listFiles[i], client, domainFilter);
			}
			client.changeToParentDirectory();

		}

	}

	private boolean isDownloadableFile(FTPFile ftpFile) {
		return true;
	}

	/**
	 * @param source
	 *            fileName
	 * @param destination
	 *            full file path of on local machine
	 * @throws IOException
	 *             {@link IOException}
	 */
	public void downloadFile(String source, String destination, String folderName) throws IOException {
		FTPClient client = new FTPClient();

		client.connect(ftpConfiguration.getHost());
		client.setFileType(FTP.BINARY_FILE_TYPE, FTP.BINARY_FILE_TYPE);
		client.setFileTransferMode(FTP.BINARY_FILE_TYPE);
		client.enterLocalPassiveMode();
		client.login(ftpConfiguration.getLogin(), ftpConfiguration.getPassword());
		if (folderName != null) {
			client.changeWorkingDirectory(folderName);
		}
		if (isFileExist(client, source)) {
			client.setFileType(FTP.BINARY_FILE_TYPE, FTP.BINARY_FILE_TYPE);
			client.setFileTransferMode(FTP.BINARY_FILE_TYPE);
			FileOutputStream out = new FileOutputStream(destination);
			client.retrieveFile(source, out);
			out.close();
		}
		client.disconnect();
	}

	/**
	 * 
	 * @param fileName
	 * @return true if file passed as parameter exist on FTP server
	 * @throws IOException
	 */
	private boolean isFileExist(FTPClient client, String fileName) throws IOException {
		boolean result = false;
		FTPFile[] files = client.listFiles();
		if (files != null && files.length > 0) {
			for (FTPFile file : files) {
				if (StringUtils.equals(file.getName(), fileName)) {
					result = true;
				}
			}
		}
		return result;
	}

	/**
	 * 
	 * @param fileName
	 * @return true if file passed as parameter exist on FTP server
	 * @throws IOException
	 */
	private boolean isFolderExist(FTPClient client, String folderName) throws IOException {
		boolean result = false;
		FTPFile[] dirs = client.listDirectories();
		if (dirs != null && dirs.length > 0) {
			for (FTPFile dir : dirs) {
				if (StringUtils.equals(dir.getName(), folderName)) {
					result = true;
				}
			}
		}
		return result;
	}
	
	
	public void uploadFile(File file, String path) {
		uploadFile(file, path, null);
	}
	
	public void uploadFile(File file, String path, String newFileName) {
		String fileName = file.getName();
		if(newFileName != null) {
			fileName = newFileName;
		}
		
		FTPClient client = new FTPClient();
		FileInputStream fis = null;
		try {
			client.connect(ftpConfiguration.getHost());
			client.login(ftpConfiguration.getLogin(), ftpConfiguration.getPassword());
			client.setBufferSize(16 * 1024);
			client.setFileType(FTP.BINARY_FILE_TYPE, FTP.BINARY_FILE_TYPE);
			client.setFileTransferMode(FTP.BINARY_FILE_TYPE);
			client.enterLocalPassiveMode();
			if (path != null) {
				if (path.contains("/")) {
					String[] folders = path.split("/");
					for (String folder : folders) {
						if (!isFolderExist(client, folder)) {
							client.makeDirectory(folder);
						}
						client.changeWorkingDirectory(folder);
					}
				} else {
					client.changeWorkingDirectory(path);
				}
			}
			// If present on FTP server delete it.
			if (isFileExist(client, fileName)) {
				client.deleteFile(fileName);
			}
			fis = new FileInputStream(file);
			client.storeFile(fileName, fis);
			client.disconnect();
		} catch (Exception e) {
			logger.error("Error while uploading file {}", file.getName(), e);
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					logger.error("FileInputStream counld not be closed");
				}
			}
		}
	}

}
