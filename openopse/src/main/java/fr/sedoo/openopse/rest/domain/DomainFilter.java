package fr.sedoo.openopse.rest.domain;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import fr.sedoo.openopse.rest.dao.FileUtils;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DomainFilter {

	private Set<String> years = new HashSet<>();
	private Set<String> sites = new HashSet<>();
	private Set<String> parameters = new HashSet<>();

	public DomainFilter(String filter) {
		readRequestParam(filter);
	}

	/**
	 * Parse request parameter and initialize the domain filter year and site are
	 * used to filter a file parameter is used to filter the content a file
	 * 
	 * @param filter
	 *            from RequestParam
	 */
	private void readRequestParam(String filter) {
		if (StringUtils.isNotEmpty(filter)) {
			String[] split = filter.split("__");
			for (int i = 0; i < split.length; i++) {
				String currentFilter = split[i];
				String[] split2 = currentFilter.split("_");
				if (split2[0].toLowerCase().equalsIgnoreCase("year")) {
					for (int j = 1; j < split2.length; j++) {
						this.getYears().add(split2[j]);
					}
				} else if (split2[0].toLowerCase().equalsIgnoreCase("site")) {
					for (int j = 1; j < split2.length; j++) {
						this.getSites().add(split2[j]);
					}
				} else if (split2[0].toLowerCase().equalsIgnoreCase("parameter")) {
					for (int j = 1; j < split2.length; j++) {
						this.getParameters().add(split2[j]);
					}
				}
			}
		}
	}

	/**
	 * Used to select a file
	 * 
	 * @param fileName
	 * @return true if the file has to be downloaded
	 */
	public boolean isFiltered(String fileName) {
		return isYearFiltered(fileName) && isSiteFiltered(fileName);
	}

	/**
	 * @return true if the content of the file has to be filtered
	 */
	public boolean hasContentToBeFiltered() {
		return this.getParameters() != null && !this.getParameters().isEmpty();
	}

	/**
	 * Example:
	 * ftp://sedur.sedoo.fr/data/353d7f00-8d6a-2a34-c0a2-5903c64e800b/OMPrawdataLaos2005.xlsx
	 * 
	 * @param fileName
	 * @return true file name contains the requested year
	 */
	private boolean isYearFiltered(String fileName) {
		String year = FileUtils.getYear(fileName);
		if (StringUtils.isEmpty(year)) {
			return true;
		} else {
			if (years.isEmpty()) {
				return true;
			} else {
				return years.contains(year);
			}
		}
	}

	/**
	 * Example:
	 * ftp://sedur.sedoo.fr/data/b359db4b-0106-6a76-2519-45f08e001f59/maddur_river_hydrochemistry.xlsx
	 * 
	 * @param fileName
	 * @return true file name contains the requested site
	 */
	private boolean isSiteFiltered(String fileName) {
		String site = FileUtils.getSite(fileName);
		if (StringUtils.isEmpty(site)) {
			return true;
		} else {
			if (sites.isEmpty()) {
				return true;
			} else {
				return sites.contains(site);
			}
		}
	}

}
