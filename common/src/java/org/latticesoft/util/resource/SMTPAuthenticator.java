package org.latticesoft.util.resource;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

public class SMTPAuthenticator extends Authenticator {
	private String account;
	private String password;
	public String getAccount() { return account; }
	public void setAccount(String account) { this.account = account; }
	public String getPassword() { return password; }
	public void setPassword(String password) { this.password = password; }

	public SMTPAuthenticator(){}
	public SMTPAuthenticator(String account, String password) {
		this.setAccount(account);
		this.setPassword(password);
	}
	public PasswordAuthentication getPasswordAuthentication() {
		return new PasswordAuthentication(account, password);
	}
}
