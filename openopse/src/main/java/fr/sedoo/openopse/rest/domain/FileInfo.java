package fr.sedoo.openopse.rest.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileInfo {

	private String name;
	private String url;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
}
