package fr.sedoo.openopse.rest.domain;

import java.util.List;

public class OSResponse {
	
	private List<OSEntry> entries;
	private List<OSEntry> parameters;
	private List<OSEntry> pivots;
	private List<OSEntry> urls;

	public List<OSEntry> getUrls() {
		return urls;
	}

	public void setUrls(List<OSEntry> urls) {
		this.urls = urls;
	}

	public List<OSEntry> getPivots() {
		return pivots;
	}

	public void setPivots(List<OSEntry> pivots) {
		this.pivots = pivots;
	}

	public List<OSEntry> getParameters() {
		return parameters;
	}

	public void setParameters(List<OSEntry> parameters) {
		this.parameters = parameters;
	}

	public List<OSEntry> getEntries() {
		return entries;
	}

	public void setEntries(List<OSEntry> entries) {
		this.entries = entries;
	}

}
