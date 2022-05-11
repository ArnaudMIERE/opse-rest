package fr.sedoo.openopse.rest.domain;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import fr.sedoo.openopse.rest.config.Profiles;

@Component
@Profile("!" + Profiles.PRODUCTION_PROFILE)
public class FakeGeoJsonGenerator implements IGeoJsonGenerator {

	@Override
	public void refresh() {
	}

	@Override
	public String generateGeoJsonFromUuidAndFilter(String uuid, String filter) {
		return "";
	}

	@Override
	public String getCacheFileNameFromUuidAndYear(String uuid, String filter) {
		// TODO Auto-generated method stub
		return "fake.json";
	}
}
