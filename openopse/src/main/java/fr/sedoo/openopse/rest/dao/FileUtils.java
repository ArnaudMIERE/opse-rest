package fr.sedoo.openopse.rest.dao;

public class FileUtils {

	public static String getYear(String fileName) {
		boolean lastCharacterIsNotNumeric = true;
		StringBuilder result = new StringBuilder();

		for (int i = 0; i < fileName.length(); i++) {
			Character character = new Character(fileName.charAt(i));
			if (Character.isDigit(character)) {
				if (lastCharacterIsNotNumeric) {
					lastCharacterIsNotNumeric = false;
					result = new StringBuilder();
				}
				result.append(character);
			} else {
				lastCharacterIsNotNumeric = true;
			}

		}

		return result.toString();
	}
	
	/**
	 * @param fileName
	 * @return the String before the first _
	 */
	public static String getSite(String fileName) {	
		if(fileName.contains("_")) {
			return fileName.substring(0, fileName.indexOf("_"));
		} else {
			return null;
		}
		
	}

}
