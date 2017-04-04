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
package org.latticesoft.util.common;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.util.Map;
import java.util.HashMap;
import java.util.Collection;
import java.util.Iterator;
import java.util.ArrayList;
import java.lang.reflect.Constructor;

/**
 * <p>
 * Parameter is used for storing values. It is used by many command
 * classes, namely those whose attributes are not fixed. An example
 * is the CustomCommand class where the set of parameters are used
 * to initialize the custom command class.
 * </p>
 * <p>
 * One of the main feature of this helper class is in the use of digester.
 * Imagine you want to have a Map class within an xml which you can define
 * the values of a map that is supposed to be part of an input to the 
 * encapsulated class. Currently there is no way of doing that in a digester 
 * way, not especially when you are talking about declaring the values
 * inside an xml.
 * </p>
 * <p>
 * For example
 * <code>
 * <some-enclosing-class>
 * <map>
 *     <entry key="one" value="two"/>
 *     <entry key="one" value="two"/>
 * </map>
 * </some-enclosing-class>
 * </code>
 * </p>
 * <p>
 * In this case there is no way of the digester creating the specific Map
 * and set it into the attribute of the encapsulating class. However the
 * trick is to have an add method inside the encapsulating class to 
 * differentiate the object to be added. However there is no way of specifying
 * what to set it into. That is where the parameter class comes in. It will
 * be created by the digester and contains the necessary info to set into
 * the encapsulating class. The add method inside the encapsulating class will
 * then extract the info and set it accordingly.
 * </p>
 * <p>
 * One of the feature include a nested capability of the Parameter. I.e. It
 * can hold another Parameter object as a child.
 * </p>
 */
public class Parameter {

	private static final Log log = LogFactory.getLog(Parameter.class);

	private String name;
	private String value;
	private String type;
	private String separator;
	private String file;
	private String input;
	private String output;
	private String dbName;
	private String fieldName;
	private String columnName;
	private String rowName;
	private String description;
	private int length;

	private Map paramsMap = new HashMap();
	private Collection paramsList = new ArrayList();
	private Collection formatters = new ArrayList();
	
	public Parameter() {
		if (log.isDebugEnabled()) { log.debug("Created."); }
	}
	public Parameter(String name) {
		this();
		this.setName(name);
	}

	/** Gets the type of the value of the parameter */
	public String getType() { return (this.type); }
	/** Sets the type of the value of the parameter */
	public void setType(String type) { this.type = type; }

	/** Gets the name of the parameter */
	public String getName() { return (this.name); }
	/** Sets the name of the parameter */
	public void setName(String name) { this.name = name; }

	/** Gets the value of the parameter */
	public String getValue() { return (this.value); }
	/** Sets the value of the parameter */
	public void setValue(String value) { this.value = value; }

	public String getSeparator() { return (this.separator); }
	public void setSeparator(String separator) { this.separator = separator; }

	public String getFile() { return (this.file); }
	public void setFile(String file) { this.file = file; }

	public String getInput() { return (this.input); }
	public void setInput(String input) { this.input = input; }

	public String getOutput() { return (this.output); }
	public void setOutput(String output) { this.output = output; }

	public String getDbName() { return (this.dbName); }
	public void setDbName(String dbName) { this.dbName = dbName; }

	public String getFieldName() { return (this.fieldName); }
	public void setFieldName(String fieldName) { this.fieldName = fieldName; }

	public String getColumnName() { return (this.columnName); }
	public void setColumnName(String columnName) { this.columnName = columnName; }

	public String getRowName() { return (this.rowName); }
	public void setRowName(String rowName) { this.rowName = rowName; }

	public int getLength() { return (this.length); }
	public void setLength(int length) { this.length = length; }

	public String getDescription() { return (this.description); }
	public void setDescription(String description) { this.description = description; }

	public void set(String propName, Object o) {
		if (propName != null && propName.equalsIgnoreCase("name") &&
			o instanceof String) {
			this.setName(o.toString());
		}
	}

	/** Copy the values of the map into the param map. */
	public void setParamsMap(Map map) {
		this.paramsMap.putAll(map);
	}

	/**
	 * Copy the values of the collection into the param collection.
	 */
	public void setParamsList(Collection c) {
		this.paramsList.addAll(c);
	}

	public Collection getFormatters() {
		return (this.formatters);
	}

	/**
	 * Copy the values of the collection into the formatters collection.
	 */
	public void setFormatters(Collection c) {
		this.formatters.addAll(c);
	}

	/** Adds a parameter to this parameter */
	public boolean add(Object o) {
		if (o != null && o instanceof Parameter) {
			Parameter p = (Parameter)o;
			if (p.getName() != null && !this.paramsMap.containsKey(p.getName())) {
				this.paramsMap.put(p.getName(), p);
				return this.paramsList.add(p);
			}
		}
		return false;
	}

	/** Get the params as a collection */
	public Collection getParamsList() {
		return this.paramsList;
	}

	/** Get the params as a map */
	public Map getParamsMap() {
		return this.paramsMap;
	}

	/**
	 * Return the copied value of the mapped parameters
	 * @return a map of values
	 */
	public Map getAllMap() {
		Map map = new HashMap();
		map.putAll(this.paramsMap);
		return map;
	}

	/**
	 * Return the copied value of the collection list
	 * @return the collection of parameters
	 */
	public Collection getAllCollection() {
		Collection c = new ArrayList();
		c.addAll(this.paramsList);
		return c;
	}

	/**
	 * Get the param collection as a copied value
	 * @return the copied value of the
	 * @deprecated
	 */
	public Collection getParams() {
		return this.paramsList;
	}

	/** Returns the parameter with a designated key. */
	public Parameter getParam(String key) {
		if (key == null) return null;
		if (this.paramsMap.containsKey(key)) {
			return (Parameter)this.paramsMap.get(key);
		}
		return null;
	}

	/** Returns the iterator for looping through the contained parameters*/
	public Iterator iterator() {
		return this.paramsList.iterator();
	}

	/** Returns the size of the parameters contained with the current parameter */
	public int size() {
		return this.paramsList.size();
	}

	/** Clears the internal storage */
	public void clear() {
		this.paramsList.clear();
		this.paramsMap.clear();
	}

	/**
	 * Convert the object into string format.
	 * @return the class expressed in string format
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		if (this.paramsList.size() > 0) {
			String openTag = StringUtil.formatObjectToXmlString(this, StringUtil.MODE_START_TAG, false);
			String endTag = StringUtil.formatObjectToXmlString(this, StringUtil.MODE_END_TAG, false);
			sb.append(openTag);
			int tabCount = ThreadUtil.incrementCount();
			Iterator iter = this.paramsList.iterator();
			while (iter.hasNext()) {
				Object o = iter.next();
				if (o != null) {
					sb.append('\n');
					for (int i=0; i<tabCount; i++) sb.append('\t');
					sb.append(o.toString());
				}
			}
			tabCount = ThreadUtil.decrementCount();
			sb.append('\n');
			for (int i=0; i<tabCount; i++) sb.append('\t');
			sb.append(endTag);
		} else {
			sb.append(StringUtil.formatObjectToXmlString(this, StringUtil.MODE_FULL_STANDARD, false));
		}
		return sb.toString();
	}

	/**
	 * Gets the object based on the type.
	 */
	public Object getObject() {
		Object retVal = null;
		if (type == null) { return retVal; }
		try {
			Constructor[] clazzCon = Class.forName(type).getConstructors();
			Object arg[] = new Object[1];
			arg[0] = this.value;
			for (int i=0; i<clazzCon.length; i++) {
				if (clazzCon[i].getParameterTypes().length == 1) {
					try {
						retVal = clazzCon[i].newInstance(arg);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return retVal;
	}
}
