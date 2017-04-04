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
 */
package org.latticesoft.util.container;

import java.io.Serializable;
import java.io.FileInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.Collection;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.Properties;
import java.util.HashSet;
import java.util.MissingResourceException;
import java.sql.Timestamp;

import org.latticesoft.util.common.FileExtensionFilter;
import org.latticesoft.util.common.StringUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @deprecated
 * @see PropertyMap
 */
public class SystemProperties implements Map, Serializable {

	public static final long serialVersionUID = 101385L;
	private final static Log log = LogFactory.getLog(SystemProperties.class);

	private static SystemProperties sp = new SystemProperties();
	private static String START;
	private static String END;

	protected Map map = new HashMap();
	protected Map source = new HashMap();

	/**
	 * Constructor
	 */
	private SystemProperties() {
		ResourceBundle rb = null;
		String conf = null;
		String names[][] = {{"SystemResource", "conf"},
							{"SysResource", "SysConfigDir"}};
		int index = 0;
		do {
			try {
				rb = null;
				rb = ResourceBundle.getBundle(names[index][0]);
				conf = rb.getString(names[index][1]);
			} catch (MissingResourceException mre) {
				rb = null;
			}
			index++;
		} while (rb == null &&  index < names[0].length);
		this.init(conf);
	}

	private void init(String conf) {
if (log.isDebugEnabled()) { log.debug("Initialization"); }

		if (conf == null) {
			if (log.isDebugEnabled()) { log.debug("Null config directory cannot initialize"); }
			return;
		}
		if (log.isDebugEnabled()) { log.debug("Conf:" + conf); }
		char c = conf.charAt(conf.length()-1);
		if (c != '/' && c != '\\') {
			conf += "/";
		}
		File f = new File(conf);
		if (!f.isDirectory()) {
			if (log.isDebugEnabled()) { log.debug("Location is not a directory"); }
			return;
		}
		String[] filelist = f.list(new FileExtensionFilter("properties"));
		for (int i=0; i<filelist.length; i++) {
			InputStream is = null;
			Properties p = new Properties();
			try {
				is = new FileInputStream(conf + filelist[i]);
				p.load(is);
				this.map.putAll(p);
				Iterator iter = p.keySet().iterator();
				while (iter.hasNext()) {
					Object o = iter.next();
					this.source.put(o, filelist[i]);
				}
				p.clear();
			} catch (Exception e) {
				if (log.isErrorEnabled()) { log.error("Exception caught", e); }
			} finally {
				try {is.close();} catch (Exception e) {}
				is = null;
			}
		}
		String classname = this.getClass().getName();
		SystemProperties.START = (String)this.map.get(classname + ".START");
		SystemProperties.END = (String)this.map.get(classname + ".END");
		if (SystemProperties.START == null || SystemProperties.END == null ||
			SystemProperties.END.trim().equals("") ||
			SystemProperties.START.trim().equals("")) {
			SystemProperties.START = "${";
			SystemProperties.END = "}";
		}
	}

	public static synchronized void reset() {
		sp = new SystemProperties();
	}

	/**
	 * Get the instance
	 */
	public static final SystemProperties getInstance() {
		return sp;
	}

	public String getStartChar() {
		return SystemProperties.START;
	}

	public String getEndChar() {
		return SystemProperties.END;
	}

	/**
	 * Convert the object into string format.
	 * @return the class expressed in string format
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		Map.Entry entry = null;
		Iterator iter = this.map.entrySet().iterator();
		sb.append("\n<sysprop>");
		while (iter.hasNext()) {
			entry = (Map.Entry)iter.next();
			sb.append("\n\t<entry name=\"");
			sb.append(entry.getKey());
			sb.append("\" value=\"");
			sb.append(entry.getValue());
			sb.append("\" source=\"");
			sb.append(this.source.get(entry.getKey()));
			sb.append("\"/>");
		}
		sb.append("\n</sysprop>\n");
		return sb.toString();
	}
	public void clear() { map.clear(); }
	public boolean containsKey(Object o) { return map.containsKey(o); }
	public boolean containsValue(Object o) { return map.containsValue(o); }
	public Set entrySet() { return map.entrySet(); }
	public boolean equals(Object o) { return map.equals(o); }
	public Object get(Object o) { return map.get(o); }
	public int hashCode() { return map.hashCode(); }
	public boolean isEmpty() { return map.isEmpty(); }
	public Set keySet() { return map.keySet(); }
	public Object put(Object key, Object value) { return map.put(key, value); }
	public void putAll(Map m) { map.putAll(m); }
	public Object remove(Object o) { return map.remove(o); }
	public int size() { return map.size(); }
	public Collection values() { return map.values(); }

	/**
	 * Get the object with an index.
	 * I.e. <code>event.get("test", 1); </code>
	 * is the same as
	 * <code>event.get("test1");</code>
	 * @param o the key
	 * @param index the index
	 * @return the object retrieved.
	 */
	public Object get(Object o, int index) {
		return map.get("" + o + index);
	}

	public String getParameter(String param) {
		if (param == null) {
			if (log.isErrorEnabled()) { log.error("Param is null"); }
			return null;
		}

		if (log.isDebugEnabled()) { log.debug("param:" + param); }
		String curr = param;
		String prev = param;
		boolean stop = true;
		Set set = new HashSet();
		int prevSetSize = set.size();

		if (log.isDebugEnabled()) { log.debug("1st rnd determine start from resolve or get."); }

		if (curr.indexOf(SystemProperties.START) > -1) {
			curr = this.resolve(curr);
		} else {
			curr = (String)this.get(curr);
		}

		if (log.isDebugEnabled()) { log.debug("curr:" + curr); }

		do {
			prev = curr;
			curr = this.resolve(curr);
			set.add(curr);
			if (set.size() > prevSetSize) {
				if (log.isDebugEnabled()) { log.debug("CyclicCheck ok: " + prevSetSize + ":" + set.size()); }
				prevSetSize = set.size();

				if (curr.indexOf(SystemProperties.START) >= 0 && !curr.equals(prev)) {
					stop = false;
				} else {
					stop = true;
				}
			} else {
				if (log.isDebugEnabled()) { log.debug("CyclicCheck failed: " + prevSetSize + ":" + set.size()); }
				stop = true;
			}
		} while (!stop);
		if (log.isDebugEnabled()) { log.debug("set:" + set); }
		set.clear();
		prevSetSize = 0;

		String retVal = curr;

		// final test again see can find or not
		String s = (String)this.map.get(retVal);
		if (s != null) {
			retVal = s;
		}

		if (retVal == null) {
			retVal =  prev;
		}
		if (retVal == null) {
			retVal = param;
		}
		if (log.isDebugEnabled()) { log.debug("RetValue:" + retVal); }
		return retVal;
	}

	/**
	 * Get the parameter with and index.
	 * @param param the parameter string
	 * @param index the index
	 * @return the string retrieved.
	 */
	public String getParameter(String param, int index) {
		param = StringUtil.replace(param, "[]", ""+index, true);
		return (String)getParameter(param);
	}

	/**
	 * Resolve the entry
	 */
	public String resolve(String source) {
		return resolve(source, "${", "}");
	}

	/**
	 * Resolves the parameter. Currently the variable is specified
	 * using the curly brace. E.g. {variable}
	 * @param source the source string
	 * @param start starting string "{" in this case.
	 * @param end ending string "}" in this case.
	 * @return the resolved string.
	 */
	private String resolve(String source, String start, String end) {
		if (source == null) {
			if (log.isErrorEnabled()) { log.error("Source string is null"); }
			return null;
		}
		StringBuffer sb = new StringBuffer(source);

		Collection c = StringUtil.extractParameter(source, start, end);
		if (log.isDebugEnabled()) log.debug("Extract parameter..." + c);

		Iterator iter = c.iterator();
		Object key = null;
		Object value = null;
		//Set set = new HashSet();
		while (iter.hasNext()) {
			key = iter.next();
			if (key != null) {
				if (key.toString().equalsIgnoreCase("SYSDATE")) {
					value = this.getCurrentTimestamp().toString();
				} else {
					value = this.map.get((String)key);
				}

				String replaceKey = start + key + end;
				String replaceValue = null;
				if (log.isDebugEnabled()) log.debug("{" + key + ", " + value + "}");
				if (value != null) {
					replaceValue = value.toString();
				}
				if (replaceKey != null && replaceValue != null) {
					StringUtil.replace(sb, replaceKey, replaceValue, true);
				}
			} else {
				if (log.isDebugEnabled()) log.debug("null key");
			}
		}
		return sb.toString();
	}

	public Timestamp getCurrentTimestamp() {
		return new Timestamp(System.currentTimeMillis());
	}

	public java.sql.Date getCurrentSQLDate() {
		return new java.sql.Date(System.currentTimeMillis());
	}

	public java.util.Date getCurrentDate() {
		return new java.util.Date(System.currentTimeMillis());
	}
}