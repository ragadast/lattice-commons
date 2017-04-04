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
 * Created on Mar 20, 2006
 *
 */
package org.latticesoft.util.resource;

import java.io.*;
import java.util.*;

import org.latticesoft.util.common.*;
import org.latticesoft.util.convert.Converter;
import org.latticesoft.command.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Downloads data from a url and parse the data into javabean objects
 * based on certain rules. The rules are much like xml jax parser in which
 * certain event occurs and and the object variable is extracted from the
 * string and populated into the bean's attribute.
 */
public class DataDownloader implements Command {

	private static final Log log = LogFactory.getLog(DataDownloader.class);
	public static final int MODE_SINGLETON_BEAN = 1;
	public static final int MODE_INSTANTIATE_NEW_BEAN = 2;
	
	private ArrayList parsers = new ArrayList();
	private boolean proxy = false;
	private String proxyHost = null;
	private int proxyPort = 0;
	private String url = null;
	private boolean useFile = false;
	private String fileOutput = null;
	
	private int mode = MODE_SINGLETON_BEAN;
	
	private String beanClassName = null;
	private Class beanClass = null;
	private Map defaultBeanAttribute = new HashMap();
	

	public DataDownloader() {}

	/** @return Returns the url. */
	public String getUrl() { return (this.url); }
	/** @param url The url to set. */
	public void setUrl(String url) { this.url = url; }
	/** @return Returns the proxy. */
	public boolean isProxy() { return (this.proxy); }
	/** @param proxy The proxy to set. */
	public void setProxy(boolean proxy) { this.proxy = proxy; }
	/** @return Returns the proxyHost. */
	public String getProxyHost() { return (this.proxyHost); }
	/** @param proxyHost The proxyHost to set. */
	public void setProxyHost(String proxyHost) { this.proxyHost = proxyHost; }
	/** @return Returns the proxyPort. */
	public int getProxyPort() { return (this.proxyPort); }
	/** @param proxyPort The proxyPort to set. */
	public void setProxyPort(int proxyPort) { this.proxyPort = proxyPort; }

	
	/** @return Returns the mode. */
	public int getMode() { return (this.mode); }
	/** @param mode The mode to set. */
	public void setMode(int mode) { this.mode = mode; }
	/** @return Returns the fileOutput.*/
	public String getFileOutput() { return (this.fileOutput); }
	/** @param fileOutput The fileOutput to set. */
	public void setFileOutput(String fileOutput) { this.fileOutput = fileOutput; }
	/** @return Returns the useFile. */
	public boolean isUseFile() { return (this.useFile); }
	/** @param useFile The useFile to set. */
	public void setUseFile(boolean useFile) { this.useFile = useFile; }

	
	/** @return Returns the beanClass. */
	public Class getBeanClass() { return (this.beanClass); }
	/** @param beanClass The beanClass to set. */
	public void setBeanClass(Class beanClass) { this.beanClass = beanClass; }
	/** @return Returns the beanClassName. */
	public String getBeanClassName() { return (this.beanClassName); }
	/** @param beanClassName The beanClassName to set. */
	public void setBeanClassName(String beanClassName) { this.beanClassName = beanClassName; }

	/** @return Returns the defaultBeanAttribute. */
	public Map getDefaultBeanAttribute() { return (this.defaultBeanAttribute); }
	/** @return Returns the parsers. */
	public Collection getParsers() { return (this.parsers); }
	
	public boolean addParser(Converter c) {
		if (c != null) {
			return this.parsers.add(c);
		}
		return false; 
	}
	
	public boolean add(Object o) {
		if (o != null && o instanceof Converter) {
			Converter c = (Converter)o;
			return this.addParser(c);
		}
		return false;
	}
	
	
	
	
	

	// ==================================================
	public Object execute(Object o) throws CommandException {
		Object data = null;
		try {
			this.configureProxy();
			Collection c = this.download();
			data = this.parseData(c);
		} catch (Exception e) {
			if (log.isErrorEnabled()) { log.error(e); }
		}
		return data;
	}

	public void configureProxy() {
		if (proxy && proxyHost != null && proxyPort > 0) {
if (log.isInfoEnabled()) { log.info("Using proxy : " + proxyHost + ":" + proxyPort); }
			System.getProperties().put("proxySet", "true");
			System.getProperties().put("proxyHost", proxyHost);
			System.getProperties().put("proxyPort", "" + proxyPort);
		}
	}

	public Collection download() {
if (log.isInfoEnabled()) { log.info("Download from " + this.url); }
		InputStream is = null;
		Collection c = new ArrayList();
		try {
			if (!this.useFile) {
				c = FileUtil.readLinesFromURL(this.url);
			} else {
				if (this.fileOutput == null) {
					return new ArrayList();
				}
				FileUtil.downloadFromURL(this.url, this.fileOutput);
			}
		} catch (Exception e) {
			if (log.isErrorEnabled()) { log.error(e); }
		} finally {
			try { is.close(); } catch (Exception e) {}
		}
		return c;
	}
	
	public Object parseData(Collection c) {
		if (!this.useFile && c == null) {
			return null;
		}
		if (beanClass == null && beanClassName == null) {
			return null;
		}
		
		Object bean = null;
		Iterator iter = null;
		String line = null;
		ArrayList a = new ArrayList();
		int lineCount = 0;
		try {
			if (beanClass == null) {
				beanClass = Class.forName(this.beanClassName);
			}
			if (beanClass == null) {
				return null;
			}
			if (mode == MODE_SINGLETON_BEAN) {
				bean = beanClass.newInstance();
				MiscUtil.populateObject(bean, this.defaultBeanAttribute);
			}
			if (this.useFile) {
if (log.isDebugEnabled()) { log.debug("Reading from  : " + this.fileOutput); }
				iter = new FileIterator(this.fileOutput);
			} else {
if (log.isDebugEnabled()) { log.debug("Parsing data : " + c.size()); }
				iter = c.iterator();
			}
			while (iter.hasNext()) {
				if (mode == MODE_INSTANTIATE_NEW_BEAN) {
					bean = beanClass.newInstance();
					MiscUtil.populateObject(bean, this.defaultBeanAttribute);
					//a.add(bean);
				}
				lineCount++;
				line = (String)iter.next();
				if (line == null) continue;
				line = line.trim();
				if (line.length() == 0) {
					continue;
				}
				for (int i=0; i<parsers.size(); i++) {
					Object o = parsers.get(i);
if (log.isDebugEnabled()) { log.debug(o); }
					if (o instanceof Converter) {
						Converter conv = (Converter)o;
						Map map = new HashMap();
						map.put("bean", bean);
						MiscUtil.populateObject(conv, map);
						o = conv.convert(line);
						if (o != null && this.mode == MODE_INSTANTIATE_NEW_BEAN) {
							a.add(bean);
						}
					}
				}
			}
		} catch (Exception e) {
			if (log.isErrorEnabled()) { log.error(e); }
		} finally {
			FileUtil.closeFileIterator(iter);
		}
		if (this.mode == MODE_INSTANTIATE_NEW_BEAN) {
			return a;
		} else {
			return bean;
		}
	}

}
