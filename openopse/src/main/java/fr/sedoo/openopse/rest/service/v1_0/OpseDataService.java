package fr.sedoo.openopse.rest.service.v1_0;

import java.awt.Desktop;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.util.StreamUtils;

import javax.ws.rs.core.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.zeroturnaround.zip.ZipUtil;

import com.google.common.base.Strings;

import fr.sedoo.openopse.rest.domain.DomainFilter;
import fr.sedoo.commons.util.StringUtil;
import fr.sedoo.openopse.rest.config.ApplicationConfig;
import fr.sedoo.openopse.rest.dao.FileUtils;
import fr.sedoo.openopse.rest.domain.FileInfo;
import fr.sedoo.openopse.rest.domain.OSEntry;
import fr.sedoo.openopse.rest.domain.OSResponse;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import tech.tablesaw.api.Table;

@RestController
@RequestMapping(value = "/data/v1_0")
public class OpseDataService {

	@Autowired
	ApplicationConfig config;

	private static final String SEPARATOR = ";";

	private static final Logger LOG = LoggerFactory.getLogger(OpseDataService.class);
	private static final String DEFAULT_PATH_CSV = "/data/csv";
	private static final String DEFAULT_PATH_NETCDF = "/data/netcdf";
	
	private static final String FILE_PATH = "/data/conf/";

	private static final String COMMA_DELIMITER = ",";

	@RequestMapping(value = "/isalive", method = RequestMethod.GET)
	public String isalive() {
		return "yes";
	}

	@RequestMapping(value = "/toscript", method = RequestMethod.GET)
	@Produces(MediaType.TEXT_PLAIN)
	@ApiOperation(value = "Return the download command line")
	public String toScript(HttpServletRequest request,
			@ApiParam(name = "collectionId", value = "id of the collection to be scripted", required = true) @RequestParam("collectionId") String collectionId,
			@ApiParam(name = "filter", value = "filter that indicates the selected items", required = false) @RequestParam(required = false) String filter) {
		String baseUrl = "https://" + request.getServerName() + request.getContextPath();
		LOG.debug("baseurl: " + baseUrl);
		String command = "";
		if (!Strings.isNullOrEmpty(filter)) {
			command = "curl -X GET " + baseUrl + "/data/download?collectionId=" + collectionId + "/&filter=" + filter
					+ " --output dataset" + collectionId + ".zip";
		} else {
			command = "curl -X GET " + baseUrl + "/data/download?collectionId=" + collectionId + " --output dataset"
					+ collectionId + ".zip";
		}

		return command;
	}

	private String getZipFileNameFromId(String id) {
		return "dataset-" + id + ".zip";
	}

	private List<String> getFileNameFromUuidAndYear(String collection, String filter, String folder)
			throws IOException {
		DomainFilter domain = new DomainFilter(filter);
		LOG.info("YEARS " + domain.getYears());
		String folderName = config.getOpenOpseFolderName();
		File workDirectory = new File(folderName);

		File resource = new File(workDirectory, collection + folder);
		LOG.info("Path " + resource.getAbsolutePath());
		List<String> currentFile = new ArrayList<>();
		File[] listOfFiles = resource.listFiles();
		List<File> listOfFiles_copy = new ArrayList<>();
		for (File file : listOfFiles) {
			for (String year : domain.getYears()) {
				if (file.getName().contains(year)) {
					String destinationFilePath = resource.getAbsolutePath().concat("/").concat(file.getName());

					currentFile.add(destinationFilePath);
					listOfFiles_copy.add(file);
				}
			}
		}
		if (!folder.equalsIgnoreCase(DEFAULT_PATH_CSV))
			return currentFile;

		List<String> result = new ArrayList<>();
		for (File file : listOfFiles_copy) {
			Table table = Table.read().file(file);
			for (String p : table.columnNames()) {
				if (!domain.getParameters().contains(p)) {
					table.removeColumns(p);
				}
			}
			//String tmp_folder = resource.getAbsolutePath().concat("/tmp/");
			String  tmp_folder = config.getTemporaryDownloadFolderName();
			File tmp = new File(tmp_folder);
			if (tmp.exists() == false) {
				tmp.mkdirs();
			} else {
				tmp.delete();
				tmp.mkdirs();
			}
			String destinationFilePath = tmp_folder.concat(file.getName());
			table.write().csv(destinationFilePath);
			result.add(destinationFilePath);
		}
		System.out.println("result size " + currentFile);
		return result;
	}

	public List<String> getPivot(String collection) {
		// File path is passed as parameter
		String folderName = config.getOpenOpseFolderName();
		File workDirectory = new File(folderName);

		File file = new File(workDirectory, collection + FILE_PATH+"pivot.txt");
		//File file = new File("pivot.txt");
		BufferedReader br;
		List<String> result = new ArrayList<>();
		try {
			br = new BufferedReader(new FileReader(file));
			// Declaring a string variable
			String st = br.readLine();
			if ( st!= null) {
				// Print the string
				System.out.println("les pivots "+st);
				for (String s : st.split(SEPARATOR)) {
					result.add(s);
				}
			}
			br.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;

	}

	@RequestMapping(value = "/download", method = RequestMethod.GET)
	@Produces("application/zip")
	@ApiOperation(value = "Return files for the collection id")
	public byte[] download(HttpServletResponse response,
			@ApiParam(name = "collectionId", value = "id of the collection to be downloaded", required = true) @RequestParam("collectionId") String collectionId,
			@ApiParam(name = "filter", value = "filter indicates the selected items", required = false) @RequestParam(required = false) String filter,
			@ApiParam(name = "folder", value = "folder indicates the selected items", required = true) @RequestParam(required = true) String folder)
			throws Exception {
		LOG.info("Starting download collection " + collectionId);

		String FolderName = config.getOpenOpseFolderName();
		String zipFileName = getZipFileNameFromId(collectionId);
		File workDirectory = new File(FolderName);
		if (workDirectory.exists() == false) {
			workDirectory.mkdirs();
		}
		File requestFolder = new File(workDirectory, collectionId + folder.replaceFirst("thumnails", "data"));
		if (requestFolder.exists() == false) {
			requestFolder.mkdirs();
		}
		File workDirectoryTmp = new File(config.getTemporaryDownloadFolderName());
		File zipFile = new File(workDirectoryTmp, zipFileName);

		LOG.info("Request folder  " + requestFolder.getAbsolutePath());

		ZipUtil.pack(requestFolder, zipFile);

		java.nio.file.Path p = zipFile.toPath();
		try {
			InputStream is = Files.newInputStream(p);
			byte[] result = IOUtils.toByteArray(is);

			response.addHeader("Content-Disposition", "attachment; filename=" + zipFileName);
			return result;
		} catch (Exception e) {
			LOG.error(ExceptionUtils.getStackTrace(e));
			throw new WebApplicationException(Response.Status.NOT_FOUND);

		}
	}

	@RequestMapping(value = "/downloadYear", method = RequestMethod.GET)
	@Produces("application/zip")
	@ApiOperation(value = "Return files for the collection id")
	public void downloadFile(HttpServletResponse response,
			@ApiParam(name = "collectionId", value = "id of the collection to be downloaded", required = true) @RequestParam("collectionId") String collectionId,
			@ApiParam(name = "filter", value = "filter indicates the selected items", required = true) @RequestParam(required = true) String filter,
			@ApiParam(name = "folder", value = "folder indicates the selected items", required = true) @RequestParam(required = true) String folder)
			throws Exception {
		LOG.info("Starting download collection " + collectionId);
		String zipFileName = getZipFileNameFromId(collectionId);

		response.setContentType("application/octet-stream");
		response.setHeader("Content-Disposition", "attachment;filename=" + zipFileName);
		response.setStatus(HttpServletResponse.SC_OK);

		List<String> fileNames = getFileNameFromUuidAndYear(collectionId, filter, folder.replaceFirst("thumnails", "data"));

		LOG.info("file size " + fileNames.size());

		try (ZipOutputStream zippedOut = new ZipOutputStream(response.getOutputStream())) {
			for (String file : fileNames) {
				FileSystemResource resource = new FileSystemResource(file);

				ZipEntry e = new ZipEntry(resource.getFilename());
				// Configure the zip entry, the properties of the file
				e.setSize(resource.contentLength());
				e.setTime(System.currentTimeMillis());
				zippedOut.putNextEntry(e);
				// And the content of the resource:
				StreamUtils.copy(resource.getInputStream(), zippedOut);
				zippedOut.closeEntry();
			}
			zippedOut.finish();
		} catch (Exception e) {
			LOG.error(ExceptionUtils.getStackTrace(e));
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
	}

	@RequestMapping(value = "/download", method = RequestMethod.HEAD)
	@Produces("application/zip")
	@ApiOperation(value = "Return files for the collection id")
	public void downloadHead(HttpServletResponse response,
			@ApiParam(name = "collectionId", value = "id of the collection to be downloaded", required = true) @RequestParam("collectionId") String collectionId) {
		response.addHeader("Content-Disposition", "attachment; filename=" + getZipFileNameFromId(collectionId));
	}

	public static long folderSize(File directory) {
		long length = 0;
		for (File file : directory.listFiles()) {
			if (file.isFile())
				length += file.length();
			else
				length += folderSize(file);
		}
		return length;
	}

	/*
	 * @RequestMapping(value = "/request", method = RequestMethod.GET)
	 * 
	 * @Produces(MediaType.APPLICATION_JSON)
	 * 
	 * @ApiOperation(value = "Return statistics on the download") public OSResponse
	 * request(
	 * 
	 * @ApiParam(name = "collection", value =
	 * "id of the collection to be downloaded", required =
	 * true) @RequestParam("collection") String collection) {
	 * 
	 * OSResponse response = new OSResponse(); List<OSEntry> entries = new
	 * ArrayList<>(); response.setEntries(entries);
	 * 
	 * try {
	 * 
	 * File resource = new File(config.getOpenOpseFolderName(), collection); if
	 * (resource.isDirectory()) { int filecount = resource.list().length;
	 * DownloadInformations downloadInformations = new
	 * DownloadInformations(filecount, folderSize(resource)); OSEntry entry = new
	 * OSEntry(); entry.setFileNumber(downloadInformations.getDownloadFileNumber());
	 * entry.setTotalSize(downloadInformations.getDownloadSize());
	 * LOG.info("File number  " + entry.getFileNumber()); LOG.info("Total size  " +
	 * entry.getTotalSize()); entries.add(entry); }
	 * 
	 * 
	 * return response; } catch (Exception e) {
	 * LOG.error(ExceptionUtils.getStackTrace(e)); throw new RuntimeException(); }
	 * 
	 * }
	 */

	@RequestMapping(value = "/request", method = RequestMethod.GET)
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Return year file data")
	public OSResponse request(
			@ApiParam(name = "collection", value = "id of the collection to be downloaded", required = true) @RequestParam("collection") String collection,
			@ApiParam(name = "folder", value = "folder indicates the selected items", required = true) @RequestParam(required = true) String folder) {

		OSResponse response = new OSResponse();
		List<OSEntry> entries = new ArrayList<>();
		response.setEntries(entries);

		try {
			Map<String, OSEntry> entriesMap = new HashMap<>();
			File resource = new File(config.getOpenOpseFolderName(), collection + folder);
			// resource.mkdirs();
			/*
			 * if (!resource.exists()) { resource.mkdir(); }
			 */
			/*
			 * Path filePath = Paths.get(resource.getAbsolutePath()); if
			 * (Files.notExists(filePath)) { throw new
			 * ResponseStatusException(HttpStatus.BAD_REQUEST, "Folder does no exist");
			 * 
			 * }
			 */
			File[] listOfFiles = resource.listFiles();
			for (File file : listOfFiles) {
				if (file.isFile()) {
					String year = FileUtils.getYear(file.getName());
					if (StringUtils.isNoneEmpty(year)) {
						OSEntry osEntry = entriesMap.get(year);
						if (osEntry == null) {
							osEntry = new OSEntry();
							osEntry.setDate(new GregorianCalendar(Integer.parseInt(year), 0, 1).getTime());
							entriesMap.put(year, osEntry);
						}
					}
				}
			}
			entries.addAll(entriesMap.values());
			Collections.sort(entries);

			return response;
		} catch (Exception e) {
			LOG.error(ExceptionUtils.getStackTrace(e));
			throw new RuntimeException();
		}

	}

	@RequestMapping(value = "/request1", method = RequestMethod.GET)
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Return year file data")
	public OSResponse request1(
			@ApiParam(name = "collection", value = "id of the collection to be downloaded", required = true) @RequestParam("collection") String collection,
			@ApiParam(name = "folder", value = "folder indicates the selected items", required = true) @RequestParam(required = true) String folder) {

		OSResponse response = new OSResponse();
		List<OSEntry> entries = new ArrayList<>();
		List<OSEntry> params = new ArrayList<>();
		List<OSEntry> pivots = new ArrayList<>();
		
		response.setParameters(params);
		response.setEntries(entries);
		response.setPivots(pivots);

		try {
			Map<String, OSEntry> entriesMap = new HashMap<>();
			File resource = new File(config.getOpenOpseFolderName(), collection + folder);
			// resource.mkdirs();
			/*
			 * if (!resource.exists()) { resource.mkdir(); }
			 */
			/*
			 * Path filePath = Paths.get(resource.getAbsolutePath()); if
			 * (Files.notExists(filePath)) { throw new
			 * ResponseStatusException(HttpStatus.BAD_REQUEST, "Folder does no exist");
			 * 
			 * }
			 */
			File[] listOfFiles = resource.listFiles();
			for (File file : listOfFiles) {
				if (file.isFile()) {
					String year = FileUtils.getYear(file.getName());
					if (StringUtils.isNoneEmpty(year)) {
						OSEntry osEntry = entriesMap.get(year);
						if (osEntry == null) {
							osEntry = new OSEntry();
							osEntry.setDate(new GregorianCalendar(Integer.parseInt(year), 0, 1).getTime());
							entriesMap.put(year, osEntry);
						}
					}
				}
			}
			entries.addAll(entriesMap.values());
			Collections.sort(entries);
			if (folder.equalsIgnoreCase(DEFAULT_PATH_NETCDF))
				return response;

			Map<String, OSEntry> paramsMap = new HashMap<>();
			try (BufferedReader br = new BufferedReader(new FileReader(listOfFiles[0]))) {
				String line = br.readLine();
				String[] values;
				if (line != null) {
					values = line.split(COMMA_DELIMITER);

					for (String s : values) {
						OSEntry osEntry = paramsMap.get(s);
						if (osEntry == null) {
							osEntry = new OSEntry();
							osEntry.setName(s);
							paramsMap.put(s, osEntry);
						}
					}
				}

			}
			Map<String, OSEntry> pivotMap = new HashMap<>();
			for (String s: getPivot(collection)) {
				OSEntry osEntry = pivotMap.get(s);
				if (osEntry == null) {
					osEntry = new OSEntry();
					osEntry.setName(s);
					pivotMap.put(s, osEntry);
				}
			}

			params.addAll(paramsMap.values());
			pivots.addAll(pivotMap.values());
			// Collections.sort(params);

			return response;
		} catch (Exception e) {
			LOG.error(ExceptionUtils.getStackTrace(e));
			throw new RuntimeException();
		}

	}
	
	@RequestMapping(value = "/thumbnails", method = RequestMethod.GET)
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    @ResponseBody
	public OSResponse getThumbnails(
			@ApiParam(name = "collection", value = "id of the collection to be downloaded", required = true) @RequestParam("collection") String collection,
			@ApiParam(name = "folder", value = "folder indicates the selected items", required = true) @RequestParam(required = true) String folder) {

		OSResponse response = new OSResponse();
		List<OSEntry> entries = new ArrayList<>();
		List<OSEntry> urls = new ArrayList<>();
		
		response.setUrls(urls);
		response.setEntries(entries);

		try {
			Map<String, OSEntry> entriesMap = new HashMap<>();
			Map<String, OSEntry> urlsMap = new HashMap<>();
			File resource = new File(config.getOpenOpseFolderName(), collection + folder);
			// resource.mkdirs();
			File[] listOfFiles = resource.listFiles();
			for (File file : listOfFiles) {
				if (file.isFile()) {
					//String year = FileUtils.getYear(file.getName());
					String year =file.getName().substring(0, 4);
					if (StringUtils.isNoneEmpty(year)) {
						OSEntry osEntry = entriesMap.get(year);
						if (osEntry == null) {
							osEntry = new OSEntry();
							osEntry.setDate(new GregorianCalendar(Integer.parseInt(year), 0, 1).getTime());
							entriesMap.put(year, osEntry);
						}
						osEntry = urlsMap.get(file.getName());
						if (osEntry == null) {
							osEntry = new OSEntry();
							String path = resource.getAbsolutePath()+"/"+file.getName();
							osEntry.setUrl(path);
							BufferedImage bufferedImage = ImageIO.read(file);

							 // get DataBufferBytes from Raster
							InputStream in = new FileInputStream(file);
							osEntry.setImage(IOUtils.toByteArray(in));
							osEntry.setName(file.getName());
							urlsMap.put(file.getName(), osEntry);
							
						}
					}
				}
			}
			entries.addAll(entriesMap.values());
			urls.addAll(urlsMap.values());
			Collections.sort(entries);
			//Collections.sort(urls);
			//System.out.println("liste des url "+urlsMap.keySet());

			return response;
		} catch (Exception e) {
			LOG.error(ExceptionUtils.getStackTrace(e));
			throw new RuntimeException();
		}

	}
	
	@RequestMapping(value = "/photos", method = RequestMethod.GET)
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    @ResponseBody
	public OSResponse getImageFromPhotos(
			@ApiParam(name = "collection", value = "id of the collection to be downloaded", required = true) @RequestParam("collection") String collection) {
		
		
		OSResponse response = new OSResponse();
		List<OSEntry> entries = new ArrayList<>();
		
		response.setEntries(entries);
		
		String folder = "/photos";

		try {
			Map<String, OSEntry> entriesMap = new HashMap<>();
			File resource = new File(config.getOpenOpseFolderName(), collection + folder);
			// resource.mkdirs();
			File[] listOfFiles = resource.listFiles();
			for (File file : listOfFiles) {
				if (file.isFile()) {
					//String year = FileUtils.getYear(file.getName());
					String year =file.getName().substring(0, 4);
					if (StringUtils.isNoneEmpty(year)) {
						OSEntry osEntry = entriesMap.get(year);
						if (osEntry == null) {
							osEntry = new OSEntry();
							osEntry.setDate(new GregorianCalendar(Integer.parseInt(year), 0, 1).getTime());
							String path = resource.getAbsolutePath()+"/"+file.getName();
							osEntry.setUrl(path);
							 // get DataBufferBytes from Raster
							InputStream in = new FileInputStream(file);
							osEntry.setImage(IOUtils.toByteArray(in));
							osEntry.setName(file.getName());
							entriesMap.put(year, osEntry);
						}
					}
				}
			}
			entries.addAll(entriesMap.values());
			Collections.sort(entries);

			return response;
		} catch (Exception e) {
			LOG.error(ExceptionUtils.getStackTrace(e));
			throw new RuntimeException();
		}

	}
	
	@RequestMapping(value = "/getImage", method = RequestMethod.GET )
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    @ResponseBody
    public List<byte[]> getimage(HttpServletResponse response,
                    @ApiParam(name = "collectionId", value = "id of the collection to be downloaded", required = true) @RequestParam("collectionId") String collectionId,
                    @ApiParam(name = "folder", value = "folder indicates the selected items", required = true) @RequestParam(required = true) String folder) throws IOException {
            
            response.setContentType("application/octet-stream");
            List<byte[]> result = new ArrayList();
           
            File resource = new File(config.getOpenOpseFolderName(), collectionId + folder);
			// resource.mkdirs();
			File[] listOfFiles = resource.listFiles();
            //List<String> fileNames = getFileNameFromUuidAndYear(collectionId, filter, folder);
            for (File image : listOfFiles) {
                    InputStream in = new FileInputStream(image);
                    result.add(IOUtils.toByteArray(in));
            }
            return result;
    }

	@RequestMapping(value = "/dataFiles", method = RequestMethod.GET)
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Return year file data")
	public List<FileInfo> dataFiles(
			@ApiParam(name = "collection", value = "id of the collection to be downloaded", required = true) @RequestParam("collection") String collection) {

		List<FileInfo> entries = new ArrayList<>();

		try {
			File resource = new File(config.getOpenOpseDepotFolder(), collection);
			File[] listOfFiles = resource.listFiles();
			for (File file : listOfFiles) {
				FileInfo entry = new FileInfo();
				entry.setName(file.getName());
				entries.add(entry);
			}

			return entries;
		} catch (Exception e) {
			LOG.error(ExceptionUtils.getStackTrace(e));
			throw new RuntimeException();
		}

	}

	@RequestMapping(value = "/delete", method = RequestMethod.DELETE)
	@ResponseBody
	public void deleteFile(@RequestParam("fileName") String fileName, @RequestParam("collection") String collection) {
		File resource = new File(config.getOpenOpseDepotFolder(), collection);
		String file = resource.getAbsolutePath().concat("/").concat(fileName);
		try {
			Boolean result = Files.deleteIfExists(Paths.get(file));
			if (result) {
				System.out.println("File is deleted!");
			} else {
				System.out.println("Unable to delete the file.");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@RequestMapping(value = "/deleteAll", method = RequestMethod.DELETE)
	@ResponseBody
	public void deleteAll(@RequestParam("collection") String collection) throws IOException {
		File resource = new File(config.getOpenOpseDepotFolder(), collection);
		File[] listOfFiles = resource.listFiles();
		if (resource.exists() && listOfFiles != null) {
			for (File f : listOfFiles) {
				boolean result = f.delete();
				if (result) {
					System.out.println(f.getName() + " is deleted!");
				} else {
					System.out.println("Unable to delete the file.");
				}
			}
		}

	}

	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	@ResponseBody
	public List<FileInfo> uploadMultipleFiles(@RequestParam("collection") String collection,
			@RequestParam(value = "file") MultipartFile[] files) {

		List<FileInfo> fileInfos = new ArrayList<>();

		File resource = new File(config.getOpenOpseDepotFolder(), collection);

		if (resource.exists() == false) {
			resource.mkdirs();
		}

		for (MultipartFile multipleFile : files) {
			String filename = multipleFile.getOriginalFilename();

			String path = resource.getAbsolutePath().concat("/").concat(filename);
			File file = new File(path);

			try {
				if (file.createNewFile()) {
					try (FileOutputStream fos = new FileOutputStream(file)) {
						fos.write(multipleFile.getBytes());
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

			FileInfo fileInfo = new FileInfo();
			fileInfo.setName(filename);
			fileInfo.setUrl(path);
			fileInfos.add(fileInfo);
		}
		return fileInfos;
	}

	/*
	 * @RequestMapping(value = "/parameter", method = RequestMethod.GET)
	 * 
	 * @ResponseBody public String parameter (@RequestParam("collection") String
	 * collection) { StringBuilder message = new StringBuilder(); }
	 */

}
