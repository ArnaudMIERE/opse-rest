package fr.sedoo.openopse.rest.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FtpConfiguration {

	private String login;
	private String password;
	private String host;
	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}

}
