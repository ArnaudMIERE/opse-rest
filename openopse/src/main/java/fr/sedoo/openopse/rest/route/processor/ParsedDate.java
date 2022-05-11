package fr.sedoo.openopse.rest.route.processor;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ParsedDate implements Comparable<ParsedDate> {

	private Date date;
	private String stringValue;

	public ParsedDate(String stringValue, Date date) {
		this.stringValue = stringValue;
		this.date = date;
	}

	@Override
	public int compareTo(ParsedDate o) {
		return date.compareTo(o.date);
	}

}
