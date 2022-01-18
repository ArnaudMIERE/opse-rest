package fr.sedoo.openopse.rest.domain;

public interface IGeoJsonGenerator {

	void refresh();

	String generateGeoJsonFromUuidAndFilter(String uuid, String filter);

	String getCacheFileNameFromUuidAndYear(String uuid, String filter);

}
