package fr.sedoo.openopse.rest.dao;

import java.util.List;

public interface LandUseUuidDao {

	List<String> getUuids();

	boolean isLandUseUuid(String uuid);
}
