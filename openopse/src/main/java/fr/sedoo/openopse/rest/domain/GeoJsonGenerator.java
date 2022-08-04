package fr.sedoo.openopse.rest.domain;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import fr.sedoo.openopse.rest.config.ApplicationConfig;
import fr.sedoo.openopse.rest.dao.OpseJsonStyleDao;
import fr.sedoo.openopse.rest.service.v1_0.FolderConstants;
@Component

public class GeoJsonGenerator implements IGeoJsonGenerator{
	
	@Autowired
	ApplicationConfig config;
	
	//@Autowired
	//OpseJsonStyleDao styleDao;

	@Override
	public void refresh() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String generateGeoJsonFromUuidAndFilter(String uuid, String filter) {
		
		String shapeFileName = null;
		String shapeXFileName = null;
		String shapeProjFileName = null;
		String shapeDbfFileName = null;
		String shapeCpgFileName = null;
		String shapeQpjFileName = null;
		DomainFilter domain = new DomainFilter(filter);
		File resource = new File (config.getOpenOpseFolderName(), uuid+FolderConstants.SHAPE_FILE);
		File [] listOfFiles = resource.listFiles();
		try {
		for (File file : listOfFiles) {
			//for (String year : domain.getYears()) {
				//if (file.getName().contains(year)) {
					if (file.getName().endsWith("shp")) {
					shapeFileName = file.getName();
					}
					if (file.getName().endsWith("shx")) {
						shapeXFileName = file.getName();
					}
					if (file.getName().endsWith("prj")) {
						shapeProjFileName = file.getName();
					}
					if (file.getName().endsWith("dbf")) {
						shapeDbfFileName = file.getName();
					}
					if (file.getName().endsWith("cpg")) {
						shapeCpgFileName = file.getName();
					}
					if (file.getName().endsWith("qpj")) {
						shapeQpjFileName = file.getName();
					}
				//}
				
			//}
		}
		File shapeFile = new File(resource, shapeFileName);

		return  Shape2GeoJsonConverter.convertShapeFile(shapeFile/*, styleDao*/);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
					"An error has occured: " + e.getMessage());
		}
	}

	@Override
	public String getCacheFileNameFromUuidAndYear(String uuid, String year) {
		return uuid + "_" + year + ".json";
	}
	
	
	

}
