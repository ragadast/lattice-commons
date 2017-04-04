/*
 * Copyright 2004 Senunkan Shinryuu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Created on Mar 8, 2005
 */
package org.latticesoft.util.resource;

import java.util.*;
import javax.mail.*;
import java.io.*;
import javax.mail.internet.*;
import javax.activation.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author clgoh
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MessageUtil {
	private static final Log log = LogFactory.getLog(MessageUtil.class);
	
	public static void sendMessage() {
		
	}
	
	public static void sendEmail(String fromAddr, String toAddr, String subject, String body, Properties mailConfig) {
		try {
			//Here, no Authenticator argument is used (it is null).
			//Authenticators are used to prompt the user for user
			//name and password.
			Session session = Session.getDefaultInstance(mailConfig);
			MimeMessage message = new MimeMessage(session);
			//the "from" address may be set in code, or set in the
			//config file under "mail.from" ; here, the latter style is used
			message.setFrom(new InternetAddress(fromAddr));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(toAddr));
			message.setSubject(subject);
			message.setText(body);
			Transport.send(message);
		} catch (MessagingException me) {
			if (log.isErrorEnabled()) { log.error(me); }
		}
	}

	public static void sendEmail(String fromAddr, String toAddr, String subject, String body, String mailConfigFileName) {
		InputStream is = null;
		try {
			Properties p = new Properties();
			is = new FileInputStream(mailConfigFileName);
			p.load(is);
			is.close();
			sendEmail(fromAddr, toAddr, subject, body, p);
		} catch (FileNotFoundException fnfe) {
			if (log.isErrorEnabled()) { log.error(fnfe); }
		} catch (IOException ioe) {
			if (log.isErrorEnabled()) { log.error(ioe); }
		} finally {
			try { is.close(); } catch (Exception e) {}
			is = null;
		}
	}

	/**
	 * Instantiates a new authenticator for sending email
	 * @param account the email account for authentication
	 * @param password the email password for authentication 
	 */
	public static Authenticator newAuthenticator(String account, String password) {
		return new SMTPAuthenticator(account, password);
	}
	/** @see #sendMail(EmailInfo, Properties, Authenticator) */
	public static void sendMail(EmailInfo info) {
		sendMail(info, null, null);
	}
	
	/** @see #sendMail(EmailInfo, Properties, Authenticator) */
	public static void sendMail(EmailInfo info, Properties p, String account, String password) {
		Authenticator auth = MessageUtil.newAuthenticator(account, password);
		sendMail(info, p, auth);
	}
	/**
	 * Sends the email.
	 * @param info the EmailInfo containing the message and other details
	 * @param p the properties to set in the environment when instantiating the session
	 * @param auth the authenticator
	 */
	public static void sendMail(EmailInfo info, Properties p, Authenticator auth) {
		try {
			if (p == null) {
if (log.isErrorEnabled()) { log.error("Null properties!"); }
				return;
			}
			Session session = Session.getInstance(p, auth);
			session.setDebug(true);
if (log.isInfoEnabled()) {
	log.info(p);
	log.info(session);
}
			MimeMessage mimeMessage = new MimeMessage(session);
if (log.isInfoEnabled()) {
	log.info(mimeMessage);
	log.info(info.getFromAddress());
}
			mimeMessage.setFrom(info.getFromAddress());
			mimeMessage.setSentDate(new Date());
			List l = info.getToList();
			if (l != null) {
				for (int i=0; i<l.size(); i++) {
					String addr = (String)l.get(i);
if (log.isInfoEnabled()) { log.info(addr); }
					mimeMessage.addRecipients(Message.RecipientType.TO, addr);
				}
			}
			l = info.getCcList();
			if (l != null) {
				for (int i=0; i<l.size(); i++) {
					String addr = (String)l.get(i);
					mimeMessage.addRecipients(Message.RecipientType.CC, addr);
				}
			}
			l = info.getBccList();
			if (l != null) {
				for (int i=0; i<l.size(); i++) {
					String addr = (String)l.get(i);
					mimeMessage.addRecipients(Message.RecipientType.BCC, addr);
				}
			}

			if (info.getAttachment().size() == 0) {
				if (info.getCharSet() != null) {
					mimeMessage.setSubject(info.getSubject(), info.getCharSet());
					mimeMessage.setText(info.getContent(), info.getCharSet());
				} else {
					mimeMessage.setSubject(info.getSubject());
					mimeMessage.setText(info.getContent());
				}
				mimeMessage.setContent(info.getContent(), info.getContentType());
			} else {
				if (info.getCharSet() != null) {
					mimeMessage.setSubject(info.getSubject(), info.getCharSet());
				} else {
					mimeMessage.setSubject(info.getSubject());
				}
				Multipart mp = new MimeMultipart();
				MimeBodyPart body = new MimeBodyPart();
				if (info.getCharSet() != null) {
					body.setText(info.getContent() , info.getCharSet());
					body.setContent(info.getContent(), info.getContentType());
				} else {
					body.setText(info.getContent());
					body.setContent(info.getContent(), info.getContentType());
				}
				mp.addBodyPart(body);
				for (int i=0; i<info.getAttachment().size(); i++) {
					String filename = (String)info.getAttachment().get(i);
					MimeBodyPart attachment = new MimeBodyPart();
					FileDataSource fds = new FileDataSource(filename);
					attachment.setDataHandler(new DataHandler(fds));
					attachment.setFileName(MimeUtility.encodeWord(fds.getName()));
					mp.addBodyPart(attachment);
				}
				mimeMessage.setContent(mp);
			}
			Transport.send(mimeMessage);
		} catch (Exception e) {
			if (log.isErrorEnabled()) {
				log.error("Error in sending email", e);
			}
		}
	}

	
	/*
	 * # Configuration file for javax.mail 
# If a value for an item is not provided, then 
# system defaults will be used. These items can 
# also be set in code. 

# Host whose mail services will be used 
# (Default value : localhost) 
mail.host=mail.blah.com 

# Return address to appear on emails 
# (Default value : username@host) 
mail.from=webmaster@blah.net 

# Other possible items include: 
# mail.user= 
# mail.store.protocol= 
# mail.transport.protocol= 
# mail.smtp.host= 
# mail.smtp.user= 
# mail.debug=
	*/
	public static void main(String[] args) {
		Properties p = new Properties();
		p.put("mail.host", "localhost");
		p.put("mail.user", "");
		p.put("mail.smtp.host", "localhost");
		p.put("mail.transport.protocol", "");
	}
	
	
}
