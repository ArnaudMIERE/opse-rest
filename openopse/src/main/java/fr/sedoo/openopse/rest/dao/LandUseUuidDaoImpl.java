package fr.sedoo.openopse.rest.dao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class LandUseUuidDaoImpl implements LandUseUuidDao {

	private final String LAOS_LAND_USE_UUID = "0f1aea48-2a51-9b42-7688-a774a8f75e7a";
	private final String VIETNAM_LAND_USE_UUID = "c3724992-a043-4bbf-8ac1-bc6f9a608c1c";

	@Override
	public List<String> getUuids() {
		List<String> result = new ArrayList<>();
		result.add(LAOS_LAND_USE_UUID);
		result.add(VIETNAM_LAND_USE_UUID);
		return result;
	}

	@Override
	public boolean isLandUseUuid(String uuid) {
		return getUuids().contains(uuid);
	}

}
