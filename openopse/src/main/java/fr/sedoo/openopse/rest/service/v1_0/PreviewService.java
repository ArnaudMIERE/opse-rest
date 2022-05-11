package fr.sedoo.openopse.rest.service.v1_0;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import fr.sedoo.openopse.rest.service.v1_0.PreviewService;
import fr.sedoo.commons.util.StringUtil;
import fr.sedoo.openopse.rest.config.ApplicationConfig;
import fr.sedoo.openopse.rest.dao.FileUtils;
import fr.sedoo.openopse.rest.domain.OSEntry;
import fr.sedoo.openopse.rest.domain.OSResponse;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@CrossOrigin
@RequestMapping(value = "/preview/v1_0")
public class PreviewService {
	
	private static final Logger LOG = LoggerFactory.getLogger(PreviewService.class);

	@Autowired
	ApplicationConfig config;
	
	@RequestMapping(value = "/request", method = RequestMethod.GET)
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Return year file data")
	public OSResponse request(
			@ApiParam(name = "collection", value = "id of the collection to be vizualised", required = true) @RequestParam("collection") String collection) {

		OSResponse response = new OSResponse();
		List<OSEntry> entries = new ArrayList<>();
		response.setEntries(entries);

		try {
			Map<String, OSEntry> entriesMap = new HashMap<>();
			File resource = new File(config.getOpenOpseFolderName(), collection+FolderConstants.GEOJSON_FILE);
			File [] listOfFiles = resource.listFiles();
			for (File file : listOfFiles) {
				if (file.isFile()) {
					String year = FileUtils.getYear(file.getName());
					if (StringUtils.isNoneEmpty(year)) {
						OSEntry osEntry = entriesMap.get(year);
						if (osEntry == null) {
							osEntry = new OSEntry();
							osEntry.setDate(new GregorianCalendar(new Integer(year), 0, 1).getTime());
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
	
	@RequestMapping(value = "/geojson", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
	@ResponseBody 
	public byte[] visualizeDataLocation(
			@ApiParam(name = "uuid", value = "id of the collection to be downloaded", required = true) @RequestParam("uuid") String uuid) {
		if (StringUtil.isEmpty(uuid)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "uuid parameter is mandatory");
		}
		byte[] result = null;
		File resource = new File(config.getOpenOpseFolderName(), uuid+FolderConstants.GEOJSON_FILE);
		try {
			String outputFile = file(uuid);

			InputStream in = new FileInputStream(new File(resource, outputFile));
			//result = IOUtils.toByteArray(in);
			result =convertStreamToByteArray(in);
		} catch (Exception e) {
			LOG.error("Could not execute script", e);
		}
		return result;
	}
	
	
	private String file (String collection) {
		String fileGeoJson = null;
		File resource = new File(config.getOpenOpseFolderName(), collection+FolderConstants.GEOJSON_FILE);
		File [] listOfFiles = resource.listFiles();
		for (File file : listOfFiles) {
			if (file.getName().endsWith(".geojson")) {
				fileGeoJson = file.getName();
			}
		}
		return fileGeoJson;
	}
	
	public byte[] convertStreamToByteArray(InputStream is) throws Exception {
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    byte[] buff = new byte[1024];
	    int i = 0;
	    while ((i = is.read(buff, 0, buff.length)) > 0) {
	        baos.write(buff, 0, i);
	    }
	    return baos.toByteArray();
	
	}
	
	
}
