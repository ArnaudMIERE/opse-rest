package fr.sedoo.openopse.rest.domain;

import java.util.List;

import fr.aeris.commons.metadata.domain.identification.lang.InternationalString;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class Project {

	public String name;
	public InternationalString translation;
	public List<Project> thesaurusItems;
	public String searchConcat;
}
