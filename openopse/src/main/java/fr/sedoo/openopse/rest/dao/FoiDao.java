package fr.sedoo.openopse.rest.dao;

import java.util.List;

import fr.sedoo.openopse.rest.domain.FeatureOfInterest;

public interface FoiDao {
	List<FeatureOfInterest> getFoiByUuid(String uuid);

}
