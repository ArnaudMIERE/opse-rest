package fr.sedoo.openopse.rest.dao;

import java.util.List;

import fr.sedoo.openopse.rest.domain.Observation;

public interface ObservationDao {

	List<Observation> getObservations(String uuid, String site, String param);

}
