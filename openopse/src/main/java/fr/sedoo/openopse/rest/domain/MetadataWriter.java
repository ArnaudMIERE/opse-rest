package fr.sedoo.openopse.rest.domain;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.keycloak.representations.AccessTokenResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.aeris.commons.metadata.domain.identification.data.dataset.CollectionMetadata;
import fr.aeris.commons.metadata.domain.identification.data.dataset.ParameterList;
import fr.sedoo.openopse.rest.config.SsoTokenService;

@Component
public class MetadataWriter {
	
	private static final Logger LOG = LoggerFactory.getLogger(MetadataWriter.class);
	
	@Autowired
	private SsoTokenService ssoTokenService;
	
	private String url;
	
	@Autowired
	public MetadataWriter(SsoTokenService ssoTokenService, @Value("${metadataService.url}") String url) {
		this.ssoTokenService = ssoTokenService;
		this.url = url;
	}
	
	/**
	 * Patch metadata with the given parameter list
	 * @param id metadata uuid
	 * @param paramList
	 * @return http return code
	 */
	public String patchParameterList(String id, ParameterList paramList) {
		String result = null;
        try {
    		CollectionMetadata metadata = new CollectionMetadata();
            metadata.setParameters(paramList);
            metadata.setStatus(null);
            metadata.setEditInformations(null);
            metadata.setContacts(null);
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(Feature.AUTO_CLOSE_SOURCE, true);
            objectMapper.setSerializationInclusion(Include.NON_DEFAULT);
			String json = objectMapper.writeValueAsString(metadata);
			LOG.debug(json);
			result = patchMetadata(id, json);
		} catch (JsonProcessingException e) {
			 LOG.error("Error while serializing parameterList for uuid {}",id, e);
			 throw new RuntimeException();
		}
        return result;
	}
	
	/**
	 * Patch the metadata with the given json
	 * @param id metadata uuid
	 * @param json as string
	 * @return http return code
	 */
	private String patchMetadata(String id, String json) {
		try {
			
			AccessTokenResponse accessToken = ssoTokenService.getToken();
			
			HttpPatch request = new HttpPatch(url.concat(id));
			request.addHeader("Content-Type", "application/json;charset=UTF-8");
			request.addHeader("Authorization", "Bearer ".concat(accessToken.getToken()));
			
			HttpEntity httpEntity = new ByteArrayEntity(json.getBytes("UTF-8"));
			request.setEntity(httpEntity);
			
	        CloseableHttpClient client = HttpClientBuilder.create().build();
	        HttpResponse response = client.execute(request);  
	        
			if (response.getStatusLine().getStatusCode() != HttpStatus.OK.value()) {
				LOG.error("status : {}", response.getStatusLine().getStatusCode());
                String content = getContent(response.getEntity());
                LOG.error("Error message : {}", content);
                throw new IOException("Bad status: " + response.getStatusLine().getStatusCode() + " response: " + json);
			} else {
				return "ok";
			}
		} catch (IOException e) {
			throw new RuntimeException();
		}
	}
	
	private static String getContent(HttpEntity entity) throws IOException {
        if (entity == null) return null;
        InputStream is = entity.getContent();
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            int c;
            while ((c = is.read()) != -1) {
                os.write(c);
            }
            byte[] bytes = os.toByteArray();
            String data = new String(bytes);
            return data;
        } finally {
            try {
                is.close();
            } catch (IOException ignored) {

            }
        }
    }

}
