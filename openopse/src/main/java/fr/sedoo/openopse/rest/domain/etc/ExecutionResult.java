package fr.sedoo.openopse.rest.domain.etc;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExecutionResult {
	
	private String content;

	public void setContent(String result) {
		// TODO Auto-generated method stub
		this.content = result;
	}

}
