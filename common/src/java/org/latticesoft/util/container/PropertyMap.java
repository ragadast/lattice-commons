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

import java.util.*;
import java.io.*;
import org.latticesoft.util.common.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This PropertyMap is an extension of the java.util.Properties.
 * It provides the following new features:
 * <ul>
 * <li>Property indirection</li>
 * <li>Cyclic reference</li>
 * <li>Direct conversion in native types</li>
 * </ul>
 * <p>
 * <h1>Property Indirection</h1>
 * <pre>
 * tempPath=C:/Temp
 * myPath=${tempPath}/folder1
 * myPath2=${tempPath}/folder2
 * </pre>
 * When myPath is quried normal properties will return
 * the exact string value of "${tempPath}/folder1". Using PropertyMap,
 * however, will return the desired value of "C:/Temp/folder1"
 * This allows for standardization of path and especially when the
 * path is changed no search and replace of all the instance of occurence
 * of the changed path is required.
 * </p>
 * <p>
 * <h1>Cyclic Reference</h1>
 * When a cyclic reference is set up the PropertyMap will return the
 * last value before the cyclic reference. For example
 * <pre>
 * a=${b}
 * b=${c}
 * c=${a}
 * </pre>
 * In this case, a refers to b and b refers to c. C again refers back to
 * a; this is where the cyclic reference occurs and the PropertyMap will
 * return the direct value of a: "${b}".
 * </p>
 * <p>
 * <h1>Direct Conversion of Native types</h1>
 * If the intend values is a boolean flag, the boolean value can
 * be obtain by invoking the corresponding method of getBoolean(keyName).
 * For more information, please refer to the individual methods' explanation.
 * Note that redirection will be applicable to all getXxxx methods.
 * If the direct value is desired, please use the get(name) method instead.
 * </p>
 * <p>
 * In additional, the PropertyMap convieniently provides a singleton
 * implementation for applications like a system wide properties.
 * <code>
 * PropertyMap.singletonize();
 * PropertyMap.getInstance().read("files...");
 * </code>
 * </p>
 */
public class PropertyMap implements Map, Data, Serializable {

	public static final long serialVersionUID = 20050524151705L;
	private static final Log log = LogFactory.getLog(PropertyMap.class);
	private Map map = new OrderedMap();
	private String name;
	private String paramStart = "${";
	private String paramEnd = "}";
	private boolean useJexl = true;
	private boolean parseObjectFirst = true;
	private static PropertyMap instance;
	public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";

	/**
	 * Default Constructor
	 */
	public PropertyMap() {
		this((String)null, "${", "}", false, false);
	}

	/**
	 * Constructor
	 * @param name name of the property file to load
	 */
	public PropertyMap(String name) {
		this(name, "${", "}", false, false);
	}
	/**
	 * Constructor
	 * @param is inputstream to load from
	 */
	public PropertyMap(InputStream is) {
		this(is, "${", "}", false, false);
	}
	
	/**
	 * Constructor
	 * @param name name of the property file to load
	 * @param paramStart starting string of the parameter redirection
	 * @param paramEnd ending string of param
	 */
	public PropertyMap(String name, String paramStart, String paramEnd) {
		this(name, paramStart, paramEnd, false, false);
	}
	/**
	 * Constructor
	 * @param is inputstream
	 * @param paramStart starting string of the parameter redirection
	 * @param paramEnd ending string of param
	 */
	public PropertyMap(InputStream is, String paramStart, String paramEnd) {
		this(is, paramStart, paramEnd, false, false);
	}

	/**
	 * Constructor
	 * @param name name of the property file to load
	 * @param paramStart starting string of the parameter redirection
	 * @param paramEnd ending string of param
	 * @param parseObjectFirst resolve object before normal string
	 * @param useJexl use JEXL to resolve redirection
	 */
	public PropertyMap(String name, String paramStart, String paramEnd, boolean parseObjectFirst, boolean useJexl) {
		this.paramEnd = paramEnd;
		this.paramStart = paramStart;
		this.parseObjectFirst = parseObjectFirst;
		this.useJexl = useJexl;
		if (name != null && !name.trim().equals("")) { read(name); }
if (log.isDebugEnabled()) { log.debug(this.map); }
	}
	
	public PropertyMap(InputStream is, String paramStart, String paramEnd, boolean parseObjectFirst, boolean useJexl) {
		this.paramEnd = paramEnd;
		this.paramStart = paramStart;
		this.parseObjectFirst = parseObjectFirst;
		this.useJexl = useJexl;
		if (is != null) {
			this.read(is);
		}
if (log.isDebugEnabled()) { log.debug(this.map); }
	}
	
	
	/**
	 * Constructs an object based on a Map. A wrapper constructor for Map object
	 */
	public PropertyMap(Map map) {
		this(map, "${", "}");
	}

	/**
	 * Constructs an object based on a Map. A wrapper constructor for Map object
	 */
	public PropertyMap(Map map, String paramStart, String paramEnd) {
		this((String)null, "${", "}");
		this.map.putAll(map);
	}
	
	/** @return Returns the useJexl. */
	public boolean isUseJexl() { return (this.useJexl); }
	/** @param useJexl The useJexl to set. */
	public void setUseJexl(boolean useJexl) { this.useJexl = useJexl; }
	/** @return Returns the parseObjectFirst.*/
	public boolean isParseObjectFirst() { return (this.parseObjectFirst); }
	/** @param parseObjectFirst The parseObjectFirst to set. */
	public void setParseObjectFirst(boolean parseObjectFirst) { this.parseObjectFirst = parseObjectFirst; }

	/**
	 * Creates a singleton. This method loads values into the map by
	 * specifying the files to load. In addition the redirection
	 * parameter start and end string is required.
	 * @param name name of files to load
	 * @param paramStart starting character of parameters
	 * @param paramEnd starting character of parameters
	 * @param parseObjectFirst resolve object 1st before plain string
	 * @param useJexl use JEXL to resolve
	 */
	public synchronized static void singletonize(String name, String paramStart, String paramEnd, boolean parseObjectFirst, boolean useJexl) {
		if (instance != null) return;
		instance = new PropertyMap(name, paramStart, paramEnd, parseObjectFirst, useJexl);
	}

	public synchronized static void singletonize(String name, String paramStart, String paramEnd) {
		if (instance != null) return;
		instance = new PropertyMap(name, paramStart, paramEnd, true, true);
	}

	/**
	 * Creates a singleton. This method loads values into
	 * the map by specifying the files to load.
	 * @param name name of files to load
	 */
	public synchronized static void singletonize(String name) {
		if (instance != null) return;
		instance = new PropertyMap(name);
	}

	/**
	 * Creates a singleton.
	 * This method is meant for programatical loading of
	 * the values into the map.
	 */
	public synchronized static void singletonize() {
		if (instance != null) return;
		instance = new PropertyMap();
	}


	/**
	 * Returns the singleton.
	 */
	public static PropertyMap getInstance() {
		return instance;
	}

	/** Reads the keys and values from files. */
	public void read() {
		InputStream is = FileUtil.getInputStream(this.name);
		try {
			this.read(is, true);
		} catch (Exception e) {
			if (log.isErrorEnabled()) { log.error("Exception in loading map", e); }
		} finally {
			try { is.close(); } catch (Exception e) {}
			is = null;
		}
	}

	/**
	 * Reads the keys and values from files.
	 */
	public void read(String filename) {
		this.name = filename;
		this.read();
	}
	
	/** Reads the keys from the inputstream */
	public void read(InputStream is) {
		this.read(is, false);
	}

	/** Reads the keys from the inputstream */
	public void read(InputStream is, boolean closeMap) {
		try {
			FileUtil.loadMap(is, this.map, closeMap);
			// after loading the map we need to decrypt certain keys
			this.init();

		} catch (Exception e) {
			if (log.isErrorEnabled()) { log.error(e); }
		} finally {
		}
	}

	/**
	 * Writes the property into the file. If the name
	 * is wild card, then the PropertyMap will not write
	 * to any file.
	 */
	public void write() {
		if (this.name == null) { return; }
		if (this.map == null) { return; }
		StringBuffer sb = new StringBuffer();
		Iterator iter = this.map.keySet().iterator();
		List l = new ArrayList();
		while (iter.hasNext()) {
			sb.setLength(0);
			Object key = iter.next();
			Object value = map.get(key);
			if (key != null && value != null) {
				String key1 = StringUtil.replace(key.toString(), "=", "\\=", true);
				String val1 = StringUtil.replace(value.toString(), "=", "\\=", true);
				sb.append(key1);
				sb.append("=");
				sb.append(val1);
				l.add(sb.toString());
			}
		}
		String s = ConvertUtil.convertListToString(l);
		FileUtil.writeToFile(this.name, s);
	}

	/**
	 * Writes the property into the file.
	 * @param filename the name of the file to write to
	 * @see #write()
	 */
	public void write(String filename) {
		this.name = filename;
		this.write();
	}
	
	/** Initialize the values and encrypt and decrypt the values in the map. */
	public void init() {
		if (this.map.containsKey("~useJexl")) {
			this.setUseJexl(this.getBoolean("~useJexl"));
		}
		if (this.map.containsKey("~parseObjectFirst")) {
			this.setParseObjectFirst(this.getBoolean("~parseObjectFirst"));
		}

		Map keys = this.getSubMap("~");
		String keyWrapped = (String)keys.get("keyWrapped");
		Map keyMap = CryptoUtil.unwrap(keyWrapped);
		
		
		// decryption
		Set processed = new HashSet();
		if (keys.containsKey("decryptStartsWith")) {
			String decryptStartsWith = (String)keys.get("decryptStartsWith");
			if (log.isDebugEnabled()) {
				log.debug("decryptStartsWith: " + decryptStartsWith);
			}
			List l = new ArrayList();
			l.addAll(this.keySet());
			for (int i=0; i<l.size(); i++) {
				String key = (String)l.get(i);
				if (log.isDebugEnabled()) {
					log.debug("key: " + key);
				}
				if (key.startsWith(decryptStartsWith) && !processed.contains(key)) {
					String value = this.getString(key);
					String decryptValue = CryptoUtil.decrypt(keyMap, value, true);
					if (log.isDebugEnabled()) {
						log.debug("Decrypt[" + key + "]: " + value + " ==> " + decryptValue);
					}
					this.remove(key);
					this.setString(key, decryptValue);
					processed.add(key);
				}
			}
		}
		
		int count = NumeralUtil.parseInt((String)keys.get("decryptCount"));
		for (int i=1; i<=count; i++) {
			String key = (String)keys.get("decrypt_" + i);
			if (processed.contains(key)) {
				if (log.isDebugEnabled()) {
					log.debug("Already processed before: [" + key + "] skip decryption...");
				}
				continue;
			}
			String value = this.getString(key);
			String decryptValue = CryptoUtil.decrypt(keyMap, value, true);
			if (log.isDebugEnabled()) {
				log.debug("Decrypt[" + i + "]: " + value + " ==> " + decryptValue);
			}
			this.remove(key);
			this.setString(key, decryptValue);
			processed.add(key);
		}
		
		// encryption
		String encryptStartsWith = (String)keys.get("encryptStartsWith");
		if (log.isDebugEnabled()) {
			log.debug("encryptStartsWith: " + encryptStartsWith);
		}
		
		if (keys.containsKey("encryptStartsWith")) {
			List l = new ArrayList();
			l.addAll(this.keySet());
			for (int i=0; i<l.size(); i++) {
				String key = (String)l.get(i);
				if (key.startsWith(encryptStartsWith) && !processed.contains(key)) {
					String value = this.getString(key);
					String encryptValue = CryptoUtil.encrypt(keyMap, value, true);
					if (log.isDebugEnabled()) {
						log.debug("Encrypt[" + key + "]: " + value + " ==> " + encryptValue);
					}
					this.remove(key);
					this.setString(key, encryptValue);
					processed.add(key);
				}
			}
		}

		count = NumeralUtil.parseInt((String)keys.get("encryptCount"));
		for (int i=1; i<=count; i++) {
			String key = (String)keys.get("encrypt_" + i);
			if (processed.contains(key)) {
				if (log.isDebugEnabled()) {
					log.debug("Already processed before: [" + key + "] skip encryption...");
				}
				continue;
			}
			String value = this.getString(key);
			String encryptValue = CryptoUtil.encrypt(keyMap, value, true);
			if (log.isDebugEnabled()) {
				log.debug("Encrypt[" + i + "]: " + value + " ==> " + encryptValue);
			}
			this.remove(key);
			this.setString(key, encryptValue);
		}

	}

	/** Gets the name of the properties */
	public String getName() { return (this.name); }
	/** Returns the name of the loaded properties */
	public void setName(String name) { this.name = name; }

	/** Gets the starting parameter string */
	public String getParamStart() { return (this.paramStart); }
	/** Returns the starting parameter string */
	public void setParamStart(String paramStart) { this.paramStart = paramStart; }

	/** Get the ending parameter string */
	public String getParamEnd() { return (this.paramEnd); }
	/** Returns the ending parameter string */
	public void setParamEnd(String paramEnd) { this.paramEnd = paramEnd; }

	/**
	 * returns the internal map.
	 */
	public Map getMap() {
		return this.map;
	}

	/** @see Map#clear */
	public void clear() { this.map.clear(); }
	/** @see Map#containsKey */
	public boolean containsKey(Object key) { return this.map.containsKey(key); }
	/** @see Map#containsValue */
	public boolean containsValue(Object value) { return this.map.containsValue(value); }
	/** @see Map#entrySet */
	public Set entrySet() { return this.map.entrySet(); }
	/** @see Map#equals */
	public boolean equals(Object o) { return this.map.equals(o); }
	/** @see Map#get */
	public Object get(Object key) { return this.map.get(key); }
	/** @see Map#hashCode */
	public int hashCode() { return this.map.hashCode(); }
	/** @see Map#isEmpty */
	public boolean isEmpty() { return this.map.isEmpty(); }
	/** @see Map#keySet */
	public Set keySet() { return this.map.keySet(); }
	/** @see Map#put */
	public Object put(Object key, Object value) { return this.map.put(key, value); }
	/** @see Map#putAll */
	public void putAll(Map t) { this.map.putAll(t); }
	/** @see Map#remove */
	public Object remove(Object key) { return this.map.remove(key); }
	/** @see Map#size */
	public int size() { return this.map.size(); }
	/** @see Map#values */
	public Collection values() { return this.map.values(); }

	/**
	 * Returns a integer based on the key
	 * @param key key of the object stored in the map.
	 */
	public int getInt(Object key) {
		if (key == null) return 0;
		Object o = this.getObject(key);
		return NumeralUtil.parseInt(o);
	}
	/**
	 * Sets an integer value
	 * @param key the key of the attribute
	 * @param the value of the attribute
	 */
	public void setInt(Object key, int value) {
		if (key == null) return;
		Integer object = new Integer(value);
		this.put(key, object);
	}
	
	/**
	 * Returns a long based on the key
	 * @param key key of the object stored in the map.
	 */
	public long getLong(Object key) {
		if (key == null) return 0L;
		Object o = this.getObject(key);
		return NumeralUtil.parseLong(o);
	}

	/**
	 * Sets an long value
	 * @param key the key of the attribute
	 * @param the value of the attribute
	 */
	public void setLong(Object key, long value) {
		if (key == null) return;
		Long object = new Long(value);
		this.put(key, object);
	}

	/**
	 * Returns a short based on the key
	 * @param key key of the object stored in the map.
	 */
	public short getShort(Object key) {
		if (key == null) return 0;
		Object o = this.getObject(key);
		return NumeralUtil.parseShort(o);
	}

	/**
	 * Sets an short value
	 * @param key the key of the attribute
	 * @param the value of the attribute
	 */
	public void setShort(Object key, short value) {
		if (key == null) return;
		Short object = new Short(value);
		this.put(key, object);
	}

	/**
	 * Returns a byte based on the key
	 * @param key key of the object stored in the map.
	 */
	public byte getByte(Object key) {
		if (key == null) return 0;
		Object o = this.getObject(key);
		return NumeralUtil.parseByte(o);
	}

	/**
	 * Sets an byte value
	 * @param key the key of the attribute
	 * @param the value of the attribute
	 */
	public void setByte(Object key, byte value) {
		if (key == null) return;
		Byte object = new Byte(value);
		this.put(key, object);
	}
	
	/**
	 * Returns a float based on the key
	 * @param key key of the object stored in the map.
	 */
	public float getFloat(Object key) {
		if (key == null) return 0.0F;
		Object o = this.getObject(key);
		return (float)NumeralUtil.parseFloat(o);
	}

	/**
	 * Sets an float value
	 * @param key the key of the attribute
	 * @param the value of the attribute
	 */
	public void setFloat(Object key, float value) {
		if (key == null) return;
		Float object = new Float(value);
		this.put(key, object);
	}

	/**
	 * Returns a double based on the key
	 * @param key key of the object stored in the map.
	 */
	public double getDouble(Object key) {
		if (key == null) return 0.0;
		Object o = this.getObject(key);
		return NumeralUtil.parseDouble(o);
	}

	/**
	 * Sets an double value
	 * @param key the key of the attribute
	 * @param the value of the attribute
	 */
	public void setDouble(Object key, double value) {
		if (key == null) return;
		Double object = new Double(value);
		this.put(key, object);
	}

	/**
	 * Returns a string based on the key
	 * @param key key of the object stored in the map.
	 */
	public String getString(Object key) {
		if (key == null) return null;
		Object o = this.getObject(key);
		if (o == null) return null;
		return o.toString();
	}

	/**
	 * Sets an string value
	 * @param key the key of the attribute
	 * @param the value of the attribute
	 */
	public void setString(Object key, String value) {
		if (key == null) return;
		this.put(key, value);
	}
	
	/**
	 * Returns a number
	 * @param the key of the object
	 * @return the number object
	 */
	public Number getNumber(Object key) {
		if (key == null) return null;
		Object o = this.getObject(key);
		if (o instanceof Number) {
			return (Number)o;
		}
		return null;
	}
	/** 
	 * Sets an Number object
	 */

	public void setNumber(Object key, Number value) {
		if (key == null) return;
		this.put(key, value);
	}

	/**
	 * Returns a string based on the key
	 * @param key key of the object stored in the map.
	 */
	public boolean getBoolean(Object key) {
		if (key == null) return false;
		Object o = this.getObject(key);
		if (o == null) return false;
		if (o instanceof Boolean) {
			return ((Boolean)o).booleanValue();
		}
		String s = o.toString().trim();
		boolean retVal = false;
		if (s.equalsIgnoreCase("t") || s.equalsIgnoreCase("true") ||
			s.equalsIgnoreCase("1")) {
			retVal = true;
		}
		return retVal;
	}

	/**
	 * Sets an boolean value
	 * @param key the key of the attribute
	 * @param the value of the attribute
	 */
	public void setBoolean(Object key, boolean value) {
		if (key == null) return;
		Boolean object = new Boolean(value); 
		this.put(key, object);
	}

	/**
	 * Returns an date object
	 * @param key the key of the attribute
	 * @param defaultFormat the default date format of the date if the object
	 * needs to be converted
	 * @return the date object
	 */
	public Date getDate(Object key, String defaultFormat) {
		Date retVal = null;
		if (key == null) { return retVal; }
		Object o = this.getObject(key);
		if (o == null) { return retVal; }
		if (o instanceof Date) {
			retVal = (Date)o;
			return retVal;
		}
		
		// split the string into 2 parts
		String s = o.toString();
		String date = null;
		String format = defaultFormat;
		if (s.indexOf(";") > 0) {
			String[] res = StringUtil.tokenizeIntoStringArray(s, ";");
			if (res != null && res.length > 1 && res[0] != null && res[1] != null) {
				date = res[0];
				format = res[1];
			}
		} else {
			date = s;
		}
		if (date != null && format != null) {
			retVal = DateUtil.parseUtilDate(format, date);
		}
		return retVal;
	}

	/**
	 * Returns an date object. The default date format is "yyyy-MM-dd HH:mm:ss.sss"
	 * @param key the key of the attribute
	 * needs to be converted
	 * @return the date object
	 */
	public Date getDate(Object key) {
		return this.getDate(key, null); 
	}

	public void setDate(Object key, Date date) {
		if (key == null) return;
		this.put(key, date);
	}

	/**
	 * Returns a object based on the key. The main difference
	 * between this method and the get(key) method is that this
	 * method will allow redirection. For more information about
	 * redirection please see MiscUtil.resolve().
	 * <br>
	 * For example, in a map:
	 * <pre>
	 * tempDir=C:/Temp
	 * b=${tempDir}/*.txt
	 * c=${tempDir}/*.pic
	 * </pre>
	 * When b is retrieve using get("b"), "${tempDir}/*.txt" is return.
	 * Conversely, using the getObject("b") we have "C:/Temp/*.txt"
	 * instead.
	 * The behaviour applies to all other getXXX methods.
	 * @param key key of the object stored in the map.
	 * @see MiscUtil#resolve
	 */
	public Object getObject(Object key) {
		if (key == null) return null;
		// change the algor abit.
		// if the key has ${ } then we will resolve
		// if not we just retrieve as if its a normal map
		Object o = null;
		if (key instanceof String) {
			String s = (String)key;
			if (s.indexOf(this.paramStart) > -1) {
				o = MiscUtil.resolve(map, s, null, paramStart, paramEnd, parseObjectFirst, useJexl);
			}
		}
		if (o == null && map.containsKey(key)) {
			o = this.map.get(key);
		}
		return o;
	}

	/**
	 * Sets an Object value
	 * @param key the key of the attribute
	 * @param the value of the attribute
	 */
	public void setObject(Object key, Object value) {
		if (key == null) return;
		this.put(key, value);
	}

	/**
	 * Returns a map object. The map object can be set programatically
	 * or from a properties file under the following format: 
	 * mapName={key1=value1,key2=value2}
	 */
	public Map getMap(Object key) {
		Map map = null;
		Object o = this.getObject(key);
		if (o instanceof Map) {
			return (Map)o;
		}
		String s = this.getString(key);
		map = StringUtil.mapFromString(s);
		return map;
	}
	
	/**
	 * Sets a map object
	 */
	public void setMap(Object key, Map value) {
		if (key == null) return;
		this.map.put(key, value);
	}
	
	/**
	 * Returns a list object
	 */
	public List getList(Object key) {
		List retVal = new ArrayList();
		Object o = this.getObject(key);
		if (o instanceof List) {
			return (List)o;
		}
		String s = this.getString(key);
		if (s != null && s.startsWith("[") && s.endsWith("]")) {
			s = s.substring(1, s.length()-1);
			Collection c = StringUtil.tokenize(s, ",");
			Iterator iter = c.iterator();
			while (iter.hasNext()) {
				s = (String)iter.next();
				if (s != null) {
					retVal.add(s.trim());
				}
			}
		}
		return retVal;
	}
	
	/**
	 * Sets a List object
	 */
	public void setList(Object key, List value) {
		if (key == null) return;
		this.map.put(key, value);
	}
	
	
	/**
	 * Returns a collection object
	 */
	public Collection getCollection(Object key) {
		return this.getList(key);
	}

	/**
	 * Sets a Collection object
	 */
	public void setCollection(Object key, Collection value) {
		if (key == null) return;
		this.map.put(key, value);
	}
	
	public Object loadObject(Object key) {
		if (key == null) return null;
		String skey = key.toString();
		if (skey.indexOf(this.paramStart) > -1) {
			int length = skey.length();
			int start = this.paramStart.length();
			int end = length - this.paramEnd.length();
			skey = skey.substring(start, end);
		}
		String classType = this.getString(this.paramStart + skey + "_" + "classType" + this.paramEnd);
		Map classProperties = MiscUtil.getSubMap(skey + "_" + "properties_", this);
		Object o = null;
		try {
			o = ClassUtil.newInstance(classType);
			org.apache.commons.beanutils.BeanUtils.populate(o, classProperties);
		} catch (Exception e) {
			if (log.isErrorEnabled()) { log.error(e); }
		}
		return o;
	}
	
	public Map getSubMap(String key) {
		return MiscUtil.getSubMap(key, this);
	}
	public List getSubList(String key) {
		return MiscUtil.getSubList(key, this);
	}
	
	public static void main(String args[]){
		
		PropertyMap.singletonize("src/java/haha.properties");
		PropertyMap pm = PropertyMap.getInstance();
		System.out.println(pm.getMap());
		System.out.println("Enc:" + pm.getString("encryptTest"));
		System.out.println("Dec:" + pm.getString("decryptTest"));
		
		
		
		/*
		System.out.println("Test: " + pm.getString("test"));
		System.out.println("Test: " + pm.getString("${test}"));
		System.out.println("Hahaha: " + pm.getString("haha.haha.haha"));
		System.out.println("Hahaha: " + pm.getString("${haha.haha.haha}"));
		
		System.out.println("Date: " + pm.getDate("date1"));
		
		
		pm.put("a", "hello there");
		pm.put("test_1", "${a} john");
		System.out.println("1 " + pm.getString("${test_1}"));
		
		pm.put("test.1", "${a} john");
		pm.setParseObjectFirst(false);
		pm.setUseJexl(false);
		System.out.println("2 " + pm.getString("${test.1}"));
		
		System.out.println(pm.getString("test.1"));
		System.out.println(pm.getString("test.1.2"));
		System.out.println(pm.getString("test.1.2.3"));

		Map map = pm.getMap("map");
		System.out.println(map);

		Collection c = pm.getCollection("coll");
		System.out.println(c);
		
		System.out.println(pm.getString("${url}"));
		
		Object o = pm.loadObject("loadObj");
		System.out.println(o);
		if (o instanceof TestBean) {
			TestBean b = (TestBean)o;
			b.sayHello();
		}
		
		
		//*/
		pm.write("bin/haha2.properties");
	}//*/
}