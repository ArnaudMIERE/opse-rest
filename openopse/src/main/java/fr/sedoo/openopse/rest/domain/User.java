package fr.sedoo.openopse.rest.domain;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = User.COLLECTION_NAME, language = "english")
public class User {

	public static final String COLLECTION_NAME = "person";

	public String email;
	public String name;
	public String category;
	public String organism;
	private List<String> purposesList;
	private String dataUse;
	private String country;
	private Boolean dataPolicy;
	private Boolean status;
	
	

	


	public User() {
		super();
	}

	@Id
	public String getEmail() {
		return email;
	}
	public void setMail(String email) {
		this.email = email;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getOrganism() {
		return organism;
	}
	public void setOrganism(String organism) {
		this.organism = organism;
	}
	public List<String> getPurposesList() {
		return purposesList;
	}
	public void setPurposesList(List<String> purposesList) {
		this.purposesList = purposesList;
	}
	public String getDataUse() {
		return dataUse;
	}
	public void setDataUse(String dataUse) {
		this.dataUse = dataUse;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public Boolean getDataPolicy() {
		return dataPolicy;
	}
	public void setDataPolicy(Boolean dataPolicy) {
		this.dataPolicy = dataPolicy;
	}
	public Boolean getStatus() {
		return status;
	}
	public void setStatus(Boolean status) {
		this.status = status;
	}
	



}
