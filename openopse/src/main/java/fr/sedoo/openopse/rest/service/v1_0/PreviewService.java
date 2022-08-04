package fr.sedoo.openopse.rest.service.v1_0;


import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
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
import fr.sedoo.openopse.rest.domain.IGeoJsonGenerator;
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
	
	@Autowired
	IGeoJsonGenerator geojsonGenerator;
	
	
	
	
	
	
	@RequestMapping(value = "/geojson", method = RequestMethod.GET, produces = "application/json")
	public String geoJson(@RequestParam("uuid") String uuid, @RequestParam("filter") String filter) {
		if (StringUtil.isEmpty(uuid)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "uuid parameter is mandatory");
		}
		if (StringUtil.isEmpty(filter)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "filter parameter is mandatory");
		}

		
			return geojsonGenerator.generateGeoJsonFromUuidAndFilter(uuid, filter);
		

	}
	
	
	
}
