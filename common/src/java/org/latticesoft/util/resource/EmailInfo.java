package org.latticesoft.util.resource;

import java.util.*;
import javax.mail.internet.*;

public class EmailInfo {

	private List toList = new ArrayList();
	private List ccList = new ArrayList();
	private List bccList = new ArrayList();
	private String from;
	private String fromName;
	private String charSet;
	private String subject;
	private String content;
	private String contentType;

	private List attachment = new ArrayList();
	
	
	public EmailInfo() {}
	public EmailInfo(EmailInfo info) {
		this.copy(info);
	}
	public void copy(EmailInfo that) {
		this.setFrom(that.getFrom());
		this.setFromName(that.getFromName());
		this.setSubject(that.getSubject());
		this.setContent(that.getContent());
		this.setCharSet(that.getCharSet());
		this.setContentType(that.getContentType());
		this.getAttachment().addAll(that.getAttachment());
		this.getToList().addAll(that.getToList());
		this.getCcList().addAll(that.getCcList());
		this.getBccList().addAll(that.getBccList());
	}

	public List getAttachment() { return attachment; }
	public void setAttachment(List attachment) { this.attachment = attachment; }
	public List getBccList() { return bccList; }
	public void setBccList(List bccList) { this.bccList = bccList; }
	public List getCcList() { return ccList; }
	public void setCcList(List ccList) { this.ccList = ccList; }
	public String getContent() { return content; }
	public void setContent(String content) { this.content = content; }
	public String getFrom() { return from; }
	public void setFrom(String from) { this.from = from; }
	public String getSubject() { return subject; }
	public void setSubject(String subject) { this.subject = subject; }
	public List getToList() { return toList; }
	public void setToList(List toList) { this.toList = toList; }
	public String getFromName() { return fromName; }
	public void setFromName(String fromName) { this.fromName = fromName; }
	public String getCharSet() { return charSet; }
	public void setCharSet(String charSet) { this.charSet = charSet; }
	public String getContentType() { return contentType; }
	public void setContentType(String contentType) { this.contentType = contentType; }
	public InternetAddress getFromAddress() {
		InternetAddress retVal = null;
		try {
			if (this.fromName != null && this.from != null && this.charSet != null) {
				retVal = new InternetAddress(this.from, this.fromName, this.charSet);
			} else if (this.from != null && this.charSet != null) {
				retVal = new InternetAddress(this.from, this.charSet);
			} else if (this.from != null) {
				retVal = new InternetAddress(this.from);
			}
		} catch (Exception e) {
		}
		return retVal;
	}
	/**
	 * Converts the class in a string form
	 * @returns the class in a string form.
	 */
	public String toString() {
		return toString(false);
	}
	
	/**
	 * Converts the class in a string form
	 * @param fullInfo display the full info or not
	 * @returns the class in a string form.
	 */
	public String toString(boolean fullInfo) {
		StringBuffer sb = new StringBuffer();
		if (fullInfo == false){
			sb.append("[EmailInfo: To=");
			for (int i=0; i<this.toList.size(); i++) {
				String to = (String)this.toList.get(i);
				sb.append(to).append(",");
			}
			sb.deleteCharAt(sb.length()-1);
			sb.append("|subject=").append(this.getSubject());
			sb.append("|from=").append(this.getFrom());
			sb.append("]");
		} else {
			sb.append("\n===== EmailInfo =====");
			sb.append("\nFrom       : ");
			sb.append(this.getFrom());
			if (this.getFromName() != null) {
				sb.append(" <").append(this.getFromName()).append(">");
			}
			sb.append("\nTo         : ");
			for (int i=0; i<this.getToList().size(); i++) {
				sb.append(this.getToList().get(i)).append(", ");
			}
			if (this.getToList().size() > 0) {
				sb.deleteCharAt(sb.length()-1);
				sb.deleteCharAt(sb.length()-1);
			}
			
			sb.append("\nCc         : ");
			for (int i=0; i<this.getCcList().size(); i++) {
				sb.append(this.getCcList().get(i)).append(", ");
			}
			if (this.getCcList().size() > 0) {
				sb.deleteCharAt(sb.length()-1);
				sb.deleteCharAt(sb.length()-1);
			}
			
			sb.append("\nBcc        : ");
			for (int i=0; i<this.getBccList().size(); i++) {
				sb.append(this.getBccList().get(i)).append(", ");
			}
			if (this.getBccList().size() > 0) {
				sb.deleteCharAt(sb.length()-1);
				sb.deleteCharAt(sb.length()-1);
			}
			sb.append("\nContentType: ");
			sb.append(this.getContentType());

			sb.append("\nAttachment : ");
			for (int i=0; i<this.getAttachment().size(); i++) {
				sb.append(this.getAttachment().get(i)).append(", ");
			}
			if (this.getAttachment().size() > 0) {
				sb.deleteCharAt(sb.length()-1);
				sb.deleteCharAt(sb.length()-1);
			}

			sb.append("\nSubject    : ").append(this.getSubject());
			sb.append("\nContent    :\n").append(this.getContent());
		}
		return sb.toString();
	}
}
