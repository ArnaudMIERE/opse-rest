package fr.sedoo.openopse.rest.domain;

import org.springframework.stereotype.Component;

import fr.aeris.commons.metadata.domain.identification.data.dataset.CollectionMetadata;
import fr.aeris.commons.metadata.util.json.JsonUtils;

@Component
public class MetadataParser {

	static JsonUtils jsonUtils;

	static {
		jsonUtils = new JsonUtils("fr.aeris");

	}

	public CollectionMetadata parse(String json) throws Exception {

		return (CollectionMetadata) jsonUtils.parse(json, CollectionMetadata.class);

	}
}
