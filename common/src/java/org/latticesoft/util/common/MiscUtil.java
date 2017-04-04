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

import java.lang.NoSuchMethodException;
import java.lang.IllegalAccessException;
import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.io.*;
import java.sql.Timestamp;
import java.net.URL;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.beanutils.WrapDynaBean;
import org.apache.commons.digester.Digester;
import org.apache.commons.digester.xmlrules.DigesterLoader;
import org.apache.commons.jexl.JexlHelper;
import org.apache.commons.jexl.JexlContext;
import org.apache.commons.jexl.Expression;
import org.apache.commons.jexl.ExpressionFactory;
import org.apache.commons.beanutils.*;
import org.xml.sax.SAXException;
import org.apache.xml.resolver.tools.CatalogResolver;

public final class MiscUtil {

	private static final Log log = LogFactory.getLog(MiscUtil.class);
	private static final boolean USE_JEXL = false;
	public static final String START = "${";
	public static final String END = "}";

	private MiscUtil() {}

	/**
	 * Return a submap of properties based a key.
	 * @param map the map object to extract from.
	 * @param groupKey the key to extract the sub map.
	 * @return the sub map of values
	 */
	public static Map getSubMap(String groupKey, Map map) {
		return getSubMap(groupKey, map, false);
	}
	
	/**
	 * Return a submap of properties based a key.
	 * @param map the map object to extract from.
	 * @param groupKey the key to extract the sub map.
	 * @param remove it true it will remove from the original map
	 * @return the sub map of values
	 */
	public static Map getSubMap(String groupKey, Map map, boolean remove) {
		Map retVal = new HashMap();
		if (groupKey == null) return retVal;
		if (map == null) return retVal;

		Iterator iter = map.keySet().iterator();
		ArrayList a = new ArrayList();

		while (iter.hasNext()) {
			String key = (String)iter.next();
			if (key == null) continue;

			int length = groupKey.length();
			if (length >= key.length()) {
if (log.isDebugEnabled()) {
	log.debug("key: " + key + " - key length less than GroupKey: continue");
}
				continue;
			} else {
if (log.isDebugEnabled()) { log.debug("key: " + key); }
			}

			if (key.startsWith(groupKey)) {
				String s = "";
				Object value = map.get(key);
				if (value instanceof String) {
					s = (String)value;
					if (s.indexOf(MiscUtil.START) > -1 && s.indexOf(MiscUtil.END) > -1) {
						value = MiscUtil.resolve(map, START + key + END);
					}
					if (value == null) {
						value = map.get(key);
					}
				}
				char c = key.charAt(length);
				if (c == '.' || c == '|' || c == '/' || c == '-' ||
					c == '*' || c == '#' || c == '!' || c == '$' ) {
					s = key.substring(length+1, key.length());
				} else {
					s = key.substring(length, key.length());
				}
				retVal.put(s, value);
			}
			if (remove) { a.add(key); }
		}
if (log.isDebugEnabled()) { log.debug("====="); }
		for (int i=0; i<a.size(); i++) {
if (log.isDebugEnabled()) { log.debug("Removing entry from map:" + a.get(i)); }
			map.remove(a.get(i));
		}
		return retVal;
	}
	
	/**
	 * Get a sublist from a key, the order is in the order of the keys.
	 * @param key the sublist key
	 * @param map the map to extract from
	 * @return the list sort according to the key
	 */
	public static List getSubList(String key, Map map) {
		if (key == null) {
			return new ArrayList();
		}
		List l = new ArrayList();
		Map subMap = MiscUtil.getSubMap(key, map);
		List keyList = new ArrayList();
		keyList.addAll(subMap.keySet());
		Collections.sort(keyList);
		for (int i=0; i<keyList.size(); i++) {
			Object o = subMap.get(keyList.get(i));
			if (o != null) {
				l.add(o);
			}
		}
		return l;
	}
	/**
	 * Reads a String value from map.
	 * @param map the source map to read the value from.
	 * @param key the key to get the value from.
	 * @param defaultValue assign default value if the value is read is null
	 * @return the value read.
	 */
	public static String readString(Map map, String key, String defaultValue) {
		if (map == null) {
if (log.isErrorEnabled()) { log.error("Map is null."); }
			return null;
		}
		if (key == null) {
if (log.isErrorEnabled()) { log.error("Key is null"); }
			return null;
		}
		Object o = map.get(key);
		if (o == null) {

if (log.isDebugEnabled()) {
	log.debug("Assigning default value");
	log.debug("Value is null: " + o);
}

			return defaultValue;
		}
		if (!(o instanceof String)) {
if (log.isErrorEnabled()) { log.error("Value is not a string: " + o); }
if (log.isDebugEnabled()) { log.debug("Assigning default value"); }
			return defaultValue;
		}
		return (String)o;
	}

	/**
	 * Adds the element to the bean. The bean must implement the
	 * addElement(<ClassName> bean) method where <ClassName>
	 * is the name of the class.
	 * @param bean the Bean to add to.
	 * @param map where the objects reside
	 * @param attributeName attribute name to get the objects
	 * @throws java.lang.NoSuchMethodException
	 * @throws java.lang.IllegalAccessException
	 * @throws java.lang.reflect.InvocationTargetException
	 */
	public static void addElement(Object bean, Map map, String attributeName, int startIndex)
		throws NoSuchMethodException,
		IllegalAccessException,
		java.lang.reflect.InvocationTargetException {

		Object o = null;
		if (attributeName == null || map == null || bean == null) {
if (log.isErrorEnabled()) { log.error("Null Input"); }
			return;
		}
		int index = startIndex;
		do {
			o = map.get(attributeName + index);
if (log.isDebugEnabled()) { log.debug("Object:" + o); }
			index++;
			try {
				if (o != null) {
					String methodName = "add";
					MethodUtils.invokeMethod(bean, methodName, o);
				}
			} catch (NoSuchMethodException nsme) {
				if (log.isErrorEnabled()) { log.error("NoSuchMethodException", nsme); }
				throw nsme;
			} catch (IllegalAccessException iae) {
				if (log.isErrorEnabled()) { log.error("IllegalAccessException", iae); }
				throw iae;
			} catch (InvocationTargetException ite) {
				if (log.isErrorEnabled()) { log.error("InvocationTargetException", ite); }
				throw ite;
			} catch (Exception e) {
				if (log.isErrorEnabled()) { log.error("Exception", e); }
			}
		} while (o != null);
	}


	/**
	 * Builds an hierarchy of object(s) from a rule xml file and
	 * a input xml file. The objects are created using digester
	 * library. Default is no catalog resolver
	 * @param rules the digester rules inputstream
	 * @param input the input xml file to be parsed
	 * @return the object created
	 * @see Apache Digester library java doc
	 */
	public static Object buildObjectFromXml(URL rules, InputStream input) {
		return MiscUtil.buildObjectFromXml(rules, input, false);
	}
	
	/**
	 * Builds an hierarchy of object(s) from a rule xml file and
	 * a input xml file. The objects are created using digester
	 * library.
	 * @param rules the digester rules inputstream
	 * @param input the input xml file to be parsed
	 * @param useCatalogResolver whether to use the catalog resolver or not
	 * @return the object created
	 * @see Apache Digester library java doc
	 */
	public static Object buildObjectFromXml(URL rules, InputStream input, boolean useCatalogResolver) {
		Object retVal = null;
		try {
			Digester d = DigesterLoader.createDigester(rules);
			if (useCatalogResolver) { 
				CatalogResolver cr = new CatalogResolver();
				d.setEntityResolver(cr);
			}
			/*d.setValidating(true);
			d.setNamespaceAware(true);
			d.setProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://www.w3.org/2001/XMLSchema");
			d.setErrorHandler(new org.xml.sax.ErrorHandler() {
			});//*/
			retVal = d.parse(input);
		} catch (IOException ioe) {
			if (log.isErrorEnabled()) { log.error("Error!", ioe); }
		} catch (SAXException saxe) {
			if (log.isErrorEnabled()) { log.error("Error!", saxe); }
		} catch (Exception e) {
			if (log.isErrorEnabled()) { log.error("Error!" , e); }
		}
		return retVal;
	}

	/**
	 * Builds an hierarchy of object(s) from a rule xml file and
	 * a input xml file. The objects are created using digester
	 * library.
	 * @param rules the digester rules inputstream
	 * @param input the input xml file to be parsed
	 * @return the object created
	 * @see Apache Digester library java doc
	 */
	public static Object buildObjectFromXml(File rules, File input) {
		return MiscUtil.buildObjectFromXml(rules, input, false);
	}

	/**
	 * Builds an hierarchy of object(s) from a rule xml file and
	 * a input xml file. The objects are created using digester
	 * library.
	 * @param rules the digester rules inputstream
	 * @param input the input xml file to be parsed
	 * @param useCatalogResolver whether to use the catalog resolver or not
	 * @return the object created
	 * @see Apache Digester library java doc
	 */
	public static Object buildObjectFromXml(File rules, File input, boolean useCatalogResolver) {
		Object retVal = null;
		try {
			retVal = MiscUtil.buildObjectFromXml(rules.toURI().toURL(), new FileInputStream(input), useCatalogResolver);			
		} catch (Exception e) {
			if (log.isErrorEnabled()) { log.error("Error!" , e); }
		}
		return retVal;
	}

	/**
	 * Builds an hierarchy of object(s) from a rule xml file and
	 * a input xml file. The objects are created using digester
	 * library.
	 * @param rules the digester rules file name
	 * @param input the input xml file to be parsed
	 * @return the object created
	 * @see Apache Digester library java doc
	 */
	public static Object buildObjectFromXml(String rules, String input) {
		return MiscUtil.buildObjectFromXml(rules, input, false);
	}

	/**
	 * Builds an hierarchy of object(s) from a rule xml file and
	 * a input xml file. The objects are created using digester
	 * library.
	 * @param rules the digester rules file name
	 * @param input the input xml file to be parsed
	 * @param useCatalogResolver whether to use the catalog resolver or not
	 * @return the object created
	 * @see Apache Digester library java doc
	 */
	public static Object buildObjectFromXml(String rules, String input, boolean useCatalogResolver) {
		File inputFile = new File(input);
		File rulesFile = new File(rules);
		Object retVal = null;
		try {
			retVal = MiscUtil.buildObjectFromXml(rulesFile.toURI().toURL(), new FileInputStream(inputFile), useCatalogResolver);			
		} catch (Exception e) {
			if (log.isErrorEnabled()) { log.error("Error!" , e); }
		}
		return retVal;
	}

	/**
	 * Resolves the key into the object which is represented by the
	 * variable in the key.
	 * Resolving could broadly classify into 2 stages, resolving the
	 * values representing the key into the final key string and
	 * retrieving the values mapped by the final key string. Assumes
	 * the variable is quoted by this form: ${variable}
	 * <pre>
	 * public class TestBean {
	 *     private String name;
	 *     private String message;
	 *     public String getName() { return this.name; }
	 *     public String getMessage() { return this.message; }
	 *     public void setName(String name) { this.name = name; }
	 *     public void setMessage(String message) { this.message = message; }
	 *     public String sayHello() { return "Hello there " + name; }
	 *     public String sayHello2(String extra) { return "Hello There " + name + " :\"" + extra + ".\""; }
	 * }
	 * // ---------------------------
	 * HashMap map = new HashMap();
	 * Object o = null;
	 * TestBean bean = new TestBean();
	 * bean.setName("John");
	 * bean.setMessage("Hello World");
	 * Double doubleArray[] = new Double[4];
	 * doubleArray[0] = new Double(12.3);
	 * doubleArray[1] = new Double(45.6);
	 * doubleArray[2] = new Double(78.9);
	 * doubleArray[3] = new Double(101112.0);
	 * String s[] = {"hello", "world", "how", "are", "you"};
	 *
	 * map.put("myRedirect", "${myInt}");
	 * map.put("myString", "Value1");
	 * map.put("myString2", "Value2");
	 * map.put("myInt", new Integer(999));
	 * map.put("myIndex", new Integer(2));
	 * map.put("myBean", bean);
	 * map.put("myArray", doubleArray);
	 * map.put("a", "${b}");
	 * map.put("b", "${c}");
	 * map.put("c", "${a}"); // recursive setup
	 * map.put("myStringArray", s);
	 * map.put("myRedirect2", "${myStringArray}");
	 *
	 * // retrieving plaing variables ${variable}
	 * o = MiscUtil.resolve(map, "${myString}", "DefaultValue", "${", "}");
	 * System.out.println(o); // "Value1" will be printed
	 * o = MiscUtil.resolve(map, "${myInt}", "DefaultValue", "${", "}");
	 * System.out.println(o); // "999" will be printed
	 *
	 * // retrieving variable attributes ${variable.attribute}
	 * o = MiscUtil.resolve(map, "${myBean.name}", "DefaultValue", "${", "}");
	 * System.out.println(o); // "John" will be printed
	 *
	 * // retrieving variable method ${variable.method()}
	 * // NB: the variable method should not have any input parameter
	 * o = MiscUtil.resolve(map, "${myBean.sayHello()}", "DefaultValue", "${", "}");
	 * System.out.println(o); // "Hello There John" will be printed
	 *
	 * // retrieving indexed variable ${variable[index]}
	 * // NB: the variable method should not have any input parameter
	 * o = MiscUtil.resolve(map, "${myArray[2]}", "DefaultValue", "${", "}");
	 * System.out.println(o); // "78.9" will be printed
	 *
	 * // retrieving redirected variable ${variable}
	 * o = MiscUtil.resolve(map, "${myRedirect}", "DefaultValue", "${", "}");
	 * System.out.println(o); // "999" will be printed as the Integer Object is returned.
	 *
	 * // retrieve partial key variable keyname${variable}
	 * o = MiscUtil.resolve(map, "myString${index}", "DefaultValue", "${", "}");
	 * System.out.println(o); // "myString2" will be printed instead of
	 *
	 * // nested variable ${variable${nestedVariable}}
	 * o = MiscUtil.resolve(map, "${myString${index}}", "DefaultValue", "${", "}");
	 * System.out.println(o); // "DefaultValue" will be printed because it is not resolved
	 *
	 * // more than 1 variable ${variable1}${variable2}
	 * o = MiscUtil.resolve(map, "${myString}${myString2}${myInt}${myBean.sayHello()}", "${", "}");
	 * System.out.println(o); // "Value1Value2999Hello There John" will be printed
	 *
	 * // plain string without variable
	 * o = MiscUtil.resolve(map, "test", "DefaultValue", "${", "}");
	 * System.out.println(o); // "DefaultValue" will be printed
	 *
	 * // plain object
	 * o = MiscUtil.resolve(map, new Integer(1), "DefaultValue", "${", "}");
	 * System.out.println(o); // "DefaultValue" will be printed
	 *
	 * // cyclic recursive
	 * o = MiscUtil.resolve(map, "${a}", "DefaultValue", "${", "}");
	 * System.out.println("DefaultValue==" + o); // "DefaultValue" will be printed
	 *
	 * // multi reference
	 * o = MiscUtil.resolve(map, "${myStringArray[1].length()}", "DefaultValue", "${", "}");
	 * System.out.println(s[1].length() + "==" + o); // "5" will be printed
	 * o = MiscUtil.resolve(map, "${myArray[3].toString().length()}", "DefaultValue", "${", "}");
	 * System.out.println(doubleArray[3].toString().length() + "==" + o); // "8" will be printed
	 * </pre>
	 * </p>
	 *
	 * @param map the map which contain all the values
	 * @param key the key object (in most of the case it is a string)
	 * @param paramStart starting string "${" in this case.
	 * @param paramEnd ending string "}" in this case.
	 * @param defaultValue the default value if the key is not found in the map.
	 * @param isObjectFirst parses object 1st before plain string
	 * @param useJEXL use JEXL to resolve the expression (for mathematical)
	 * @return the resolved object. The defaultValue will be returned if any
	 * of the input is invalid.
	 */
	public static Object resolve(Map map, Object key, Object defaultValue, String paramStart, String paramEnd, boolean isObjectFirst, boolean useJexl) {
		// the hashset is to prevent cyclic reference
		return MiscUtil.resolve(new HashSet(), map, key, defaultValue, paramStart, paramEnd, isObjectFirst, useJexl);
	}

	/** @see #resolve */
	public static Object resolve(Map map, Object key, Object defaultValue, String paramStart, String paramEnd, boolean isObjectFirst) {
		// the hashset is to prevent cyclic reference
		return MiscUtil.resolve(map, key, defaultValue, paramStart, paramEnd, isObjectFirst, true);
	}

	/** @see #resolve */
	public static Object resolve(Map map, Object key, Object defaultValue, String paramStart, String paramEnd) {
		return MiscUtil.resolve(map, key, defaultValue, paramStart, paramEnd, false);
	}

	/** @see #resolve */
	public static Object resolve(Map map, Object key, Object defaultValue) {
		return MiscUtil.resolve(map, key, defaultValue, MiscUtil.START, MiscUtil.END, false);
	}

	/** @see #resolve */
	public static Object resolve(Map map, Object key) {
		return MiscUtil.resolve(map, key, null, MiscUtil.START, MiscUtil.END, false);
	}

	/**
	 * Caters for cyclic recursive entry
	 * @see MiscUtil#resolve
	 */
	private static Object resolve(HashSet set, Map map, Object key, Object defaultValue, String paramStart, String paramEnd, boolean isObjectFirst, boolean useJexl) {

if (log.isDebugEnabled()) { log.debug("===== resolve() ====="); }

		if (set == null || map == null || key == null || paramStart == null || paramEnd == null) {
if (log.isErrorEnabled()) {
	log.error("set   : " + set);
	log.error("map   : " + map);
	log.error("key   : " + key);
	log.error("start : " + paramStart);
	log.error("end   : " + paramEnd);
	log.error("Input is invalid");
}
			return defaultValue;
		}

		// check that key is a string. If not it's just
		// normal map lookup operation
		if (!(key instanceof String)) {
			Object o = map.get(key);
			if (o == null) return defaultValue;
			else return o;
		}

		// get the key in string form.
		String sKey = (String)key;
if (log.isDebugEnabled()) { log.debug("sKey: " + sKey); }

		// key string is zero length or does not contain variable
		// if it does not contain variable mark e.g. ${var}
		// then its just normal lookup operation
		if (sKey.length() <= 0 ||
			sKey.indexOf(paramStart) < 0 ||
			sKey.indexOf(paramEnd) < 0) {
			Object o = map.get(key);
			if (o == null) return defaultValue;
			else return o;
		}

		// extract all the variables in the key
		Collection c = StringUtil.extractParameter(sKey, paramStart, paramEnd);
if (log.isDebugEnabled()) { log.debug("ParamSize: " + c.size()); }

		Object retVal = null;

		if (c.size() == 0) {
			// no valid variable found
if (log.isDebugEnabled()) { log.debug("No param to extract"); }
			return defaultValue;

		} else if (c.size() == 1) {
			// one parameter to extract
			String s = (String)c.iterator().next();
			Object o = null;
			if (useJexl) {
				o = MiscUtil.getParameterJexl(map, s, isObjectFirst);
			} else {
				o = MiscUtil.getParameter(map, s, isObjectFirst);
			}

			if (sKey.length() == (paramStart + s + paramEnd).length()) {
				// if the key contains only the variable
				// e.g. ${var}
				retVal = o;

			} else {
				// if the key contains variable and other string
				// e.g. haha${var}
				String replaceKey = paramStart + s + paramEnd;
				String replaceValue = null;
				if (o != null) {
					replaceValue = o.toString();
				}
				if (replaceValue == null) {
					// add a check if the defaultValue is same as key we 
					// we use a blank string instead this is to prevent
					// cyclic resolution
					if (defaultValue != null && defaultValue.equals(key)) {
						defaultValue = "";
					}
					replaceValue = (defaultValue != null) ? defaultValue.toString() : "";
				}
				StringBuffer sb = new StringBuffer(sKey);
				StringUtil.replace(sb, replaceKey, replaceValue, true);
				retVal = sb.toString();
			}

		} else {
			// more than 1 parameter => end result is a string
			StringBuffer sb = new StringBuffer(sKey);
			Iterator iter = c.iterator();
			String s = null;
			Object o = null;
			while (iter.hasNext()) {
				s = (String)iter.next();
				if (s != null) {
					if (USE_JEXL) {
						o = MiscUtil.getParameterJexl(map, s, isObjectFirst);
					} else {
						o = MiscUtil.getParameter(map, s, isObjectFirst);
					}
					String replaceKey = paramStart + s + paramEnd;
					String replaceValue = null;
					if (o != null) {
						replaceValue = o.toString();
					}
					if (replaceValue == null) {
						// add a check if the defaultValue is same as key we 
						// we use a blank string instead this is to prevent
						// cyclic resolution
						if (defaultValue != null && defaultValue.equals(key)) {
							defaultValue = "";
						}
						replaceValue = (defaultValue != null) ? defaultValue.toString() : "";
					}
					if (replaceKey != null && replaceValue != null) {
						StringUtil.replace(sb, replaceKey, replaceValue, true);
					}
				}
			}
			retVal = sb.toString();
		}

		// check that the retVal does not contain any variable.
		// if it does then it calls itself again to resolve the variable
		if (retVal instanceof String) {
			String s = (String)retVal;
			if (s.length() > 0 && s.indexOf(paramStart) > -1 && s.indexOf(paramEnd) > -1) {
				if (!set.contains(retVal)) {
					set.add(retVal);
					retVal = MiscUtil.resolve(set, map, retVal, defaultValue, paramStart, paramEnd, isObjectFirst, useJexl);
				} else {
					// cyclic recursive
					retVal = defaultValue;
				}
			}
		}
		return retVal;
	}
	
	/**
	 * Get the actual value held in the map using JEXL
	 * @param map the source map
	 * @param param the string parameter
	 * @return the parameter object
	 */
	private static Object getParameterJexl(Map map, String expression, boolean isObjectFirst) {
		Object retVal = null;
		try {
			JexlContext jc = JexlHelper.createContext();
			jc.getVars().putAll(map);
			Expression ex = ExpressionFactory.createExpression(expression);
			retVal = ex.evaluate(jc);
		} catch (Exception e) {
			if (log.isErrorEnabled()) { log.error(e); }
			retVal = expression;
		}
		return retVal;
	}

	/**
	 * Get the actual value held in the map
	 * @param map the source map
	 * @param param the string parameter
	 * @return the parameter object
	 */
	private static Object getParameter(Map map, String param, boolean isObjectFirst) {
		if (param == null) {
if (log.isErrorEnabled()) { log.error("Param is null"); }
			return null;
		}

		Collection c = StringUtil.tokenize(param, ".");
if (log.isDebugEnabled()) { log.debug("ElementSizeInParam: " + c.size()); }

		if (c == null || c.size() == 0) {
			Object o = map.get(param);
if (log.isDebugEnabled()) { log.debug("Can't tokeneise - " + o); }
			return o;
		}

		Object o = null;
		if (!isObjectFirst) {
if (log.isDebugEnabled()) { log.debug("Object evaluated last"); }
			for (int i=c.size()-1; i>=0; i--) {
if (log.isDebugEnabled()) { log.debug("[" + i + "]"); }
				Collection cSrc = combineCollectionElement(c, i);
				o = evaluateExpression(map, cSrc, isObjectFirst);
				if (o != null) {
if (log.isDebugEnabled()) { log.debug("Object Not Null ending loop: " + o); }
					break;
				}
			}
		} else {
if (log.isDebugEnabled()) { log.debug("Object evaluated first"); }
			for (int i=0; i<c.size()-1; i++) {
if (log.isDebugEnabled()) { log.debug("[" + i + "]"); }
				Collection cSrc = combineCollectionElement(c, i);
				o = evaluateExpression(map, cSrc, isObjectFirst);
				if (o != null) {
if (log.isDebugEnabled()) { log.debug("Object Not Null ending loop: " + o); }
					break;
				}
			}
		}
		return o;
	}


	/**
	 * Combines the elements in the collection.
	 * @param c the collection
	 * @param size the size to combine. If the positive the algorithm is
	 * from 1st element to last element. the number is the number of elements
	 * to combine.
	 */
	public static Collection combineCollectionElement(Collection c, int size) {
		if (c.size() == 1) {
			return c;
		}
		ArrayList src = null;
		ArrayList retVal = new ArrayList();
		if (c instanceof ArrayList) {
			src = (ArrayList)c;
		} else {
			src = new ArrayList();
			src.addAll(c);
		}
		StringBuffer sb = new StringBuffer();
		if (size > 0) {
			int start = 0;
			int end = start + Math.abs(size) + 1;
			for (int i=start; i<end; i++) {
				sb.append(src.get(i)).append(".");
			}
			sb.deleteCharAt(sb.length() - 1);
			retVal.add(sb.toString());
			for (int i=end; i<src.size(); i++) {
				retVal.add(src.get(i));
			}
		} else {
			int start = src.size()-1;
			int end = start - Math.abs(size);
			for (int i=end; i<=start; i++) {
				sb.append(src.get(i)).append(".");
			}
			sb.deleteCharAt(sb.length() - 1);
			// add to retVal;
			retVal.add(sb.toString());
			for (int i=end-1; i>=0; i--) {
				retVal.add(src.get(i));
			}
			Collections.reverse(retVal);
		}
if (log.isDebugEnabled()) { log.debug("Combined: " + retVal); }
		return retVal;
	}

	/**
	 * <p>Evaluates the expression.
	 * There are 3 instance of the element combination.
	 * The method will loop throught the collection of elements.
	 * With each loop the element is evaluated. The element can
	 * be either of the the below:
	 * <ol>
	 * <li>It is an attribute: previousObject.currentAttribute</li>
	 * <li>It is an method invocation: previousObject.method()</li>
	 * <li>It is an array index: previousObject.arrayIndex[index]</li>
	 * </ol>
	 * However, the first element must be an object.
	 * </p>
	 * @param map the map to be processed
	 * @param c the collection of elements
	 */
	private static Object evaluateExpression(Map map, Collection c, boolean isObjectFirst) {
		// evaluate the expression
		Iterator iter = c.iterator();
		String s = null;
		Object o = null;

		// check for size
		if (c.size() == 0) {
			return null;
		}

		while (iter.hasNext()) {

			s = (String)iter.next();
if (log.isDebugEnabled()) { log.debug("ElementName: " + s); }

			int idxSqOpen = s.indexOf("[");
			int idxSqClose = s.indexOf("]");
			int idxRndOpen = s.indexOf("(");
			int idxRndClose = s.indexOf(")");

			boolean isArray =
				idxSqOpen >= 0 && idxSqClose >= 0 &&
				idxSqOpen < idxSqClose &&
				idxRndOpen < 0 && idxRndOpen < 0;
			boolean isMethod =
				idxRndOpen >= 0 && idxRndClose >= 0 &&
				idxRndOpen < idxRndClose &&
				idxSqOpen < 0 && idxSqClose < 0;
			boolean isOthers = !isArray && !isMethod;

			if (isArray) {
				Object oTemp = MiscUtil.evaluateArrayElement(map, o, s, isObjectFirst, idxSqOpen, idxSqClose);
				if (oTemp == null) { break; }
				else { o = oTemp; }
			} else if (isMethod) {
				Object oTemp = MiscUtil.evaluateMethodElement(map, o, s, isObjectFirst, idxRndOpen, idxRndClose);
				if (oTemp == null) { break; }
				else { o = oTemp; }
			} else if (isOthers) {
				Object oTemp = MiscUtil.evaluateOthersElement(map, o, s, isObjectFirst);
				if (oTemp == null) { break; }
				else { o = oTemp; }
			} // else
			if (o == null) break;
		} // while
		return o;
	}


	/**
	 * Evaluate an array element
	 */
	private static Object evaluateArrayElement(Map map, Object currentObject, String elementName, boolean isObjectFirst, int idxOpen, int idxClose) {

if (log.isDebugEnabled()) { log.debug("<<isArrayElement>>"); }

		Object o = null;

		// Check if element is an array element[]
		Collection cIndex = StringUtil.extractParameter(elementName, "[", "]");

		if (cIndex == null || cIndex.size() == 0) {
if (log.isDebugEnabled()) { log.debug("No index for array" + elementName); }
			return null;
		}
		// should have only one index and it must be an integer
		String sIndex = (String)cIndex.iterator().next();
		int index = -1;
		try {
			index = Integer.parseInt(sIndex);
		} catch (Exception e) {
			index = -1;
		}
if (log.isDebugEnabled()) { log.debug("Index: " + index); }
		// check for invalid index
		if (index < 0) {
if (log.isDebugEnabled()) { log.debug("Invalid index: " + sIndex); }
			return null;
		}
		// valid index we try to check if the element
		// element is an array.
		String key = elementName.substring(0, idxOpen);

if (log.isDebugEnabled()) { log.debug("Key: " + key); }
		o = map.get(key);
		Object oArray[] = null;
		if (o instanceof Object[]) {
			oArray = (Object[])o;
			if (oArray != null && index >= 0 && index < oArray.length) {
				o = oArray[index];
			}
		}

		return o;
	}

	/**
	 * Evaluate an method element
	 */
	private static Object evaluateMethodElement(Map map, Object currentObject, String elementName, boolean isObjectFirst, int idxOpen, int idxClose) {

if (log.isDebugEnabled()) { log.debug("<<isMethodInvocation>>"); }

		// element is an method invoccation element()
		// invoke via method
		// 1) no argument is defined in the method e.g. method()
		// 2) exactly one argument is defined. e.g. method(args)
		// the argument will be resolved using getParameter.
		// no direct string is supported.
		// 3) multi args separated by commas. e.g. method(arg1, args2)
		// current not implemented

		if (currentObject == null) {
			// 1st element have to retrieve from map
			currentObject = map.get(elementName);
		}

		String methodName = elementName.substring(0, idxOpen);
		String argsString = elementName.substring(idxOpen+1, idxClose);

if (log.isDebugEnabled()) {
	log.debug("args: [" + argsString + "]");
	log.debug("methodName: " + methodName);
	log.debug("object: [" + currentObject + "]");
}

		try {
			Object args = null;
			if (argsString.length() > 0) {
				args = MiscUtil.getParameter(map, argsString, isObjectFirst);
if (log.isDebugEnabled()) {
	log.debug("args: " + args + " " + args.getClass().getName());
}
				currentObject = MethodUtils.invokeMethod(currentObject, methodName, args);
			} else {
				currentObject = MethodUtils.invokeMethod(currentObject, methodName, null);
			}
		} catch (Exception e) {
if (log.isDebugEnabled()) { log.debug(e); }
			// if the first element is an method invocation
			// an error will occur
		}
		return currentObject;
	}

	/**
	 * Evaluate others element
	 */
	private static Object evaluateOthersElement(Map map, Object currentObject, String elementName, boolean isObjectFirst) {

if (log.isDebugEnabled()) { log.debug("<<isOthers>>"); }

		// from map directly or attribute
		// element is an attribute
		// previousElement.currentAttribute
		if (currentObject == null) {
			// 1st element have to retrieve from map
			currentObject = map.get(elementName);
		} else {
			// not the 1st element
			if (currentObject instanceof Map) {
				Map m = (Map)currentObject;
				currentObject = m.get(elementName);
			} else {
				try {
					WrapDynaBean bean = new WrapDynaBean(currentObject);
					currentObject = bean.get(elementName);
				} catch (Exception e) {
					// if it is not an attribute an occur will occur
					// note that the attribute must have a getter method
if (log.isDebugEnabled()) { log.debug(e); }
				}
			} // if
		}
		return currentObject;
	}
	
	public static void populateObject(Object o, Map map) {
		try {
			BeanUtils.populate(o, map);
		} catch (Exception e) {
			if (log.isErrorEnabled()) { log.error("", e); }
		}
	}
	
	public static int getParamCount(Object o, String paramStart, String paramEnd) {
		int size = 0;
		if (o instanceof String) {
			String s = (String)o;
			Collection c = StringUtil.extractParameter(s, "${", "}");
			size = c.size();
		}
		return size;
	}
	public static int getParamCount(Object o) {
		return getParamCount(o, "${", "}");
	}
	
	public static void extractDifference(List l1, List l2, List in1not2, List in2not1, List inBothFrom1, List inBothFrom2, Comparator c) {
		int index1 = 0;
		int index2 = 0;
		Object o1 = null;
		Object o2 = null;
		String curr1 = null;
		String curr2 = null;
		String next1 = null;
		String next2 = null;
		boolean add1 = false;
		boolean add2 = false;
		int compare = 0;
		// 1st the 2 list must be sorted
		if (c == null) {
			Collections.sort(l1);
			Collections.sort(l2);
		} else {
			Collections.sort(l1, c);
			Collections.sort(l2, c);
		}
		
		// then we loop thru the list
		while (index1 < (l1.size() - 1) && index2 < (l2.size() - 1)) {
			o1 = l1.get(index1);
			o2 = l2.get(index2);
			if (o1 == null) {
				index1++;
				continue;
			}
			if (o2 == null) {
				index2++;
				continue;
			}
			curr1 = o1.toString();
			curr2 = o2.toString();
			compare = curr1.compareTo(curr2);
			if (compare == 0) {
				index1++;
				index2++;
				add1 = true;
				add2 = true;
			} else if (compare > 0) {
				// curr1 is bigger than curr2
				if (index2 + 1 < l2.size()) {
					next2 = (String)l2.get(index2 + 1);
					compare = curr2.compareTo(next2);
					if (compare > 0) {
						// check that if the next element of a2 is still
						// less than the current element of a1 then we will 
						// add the curr2 element
						add2 = true;
						add1 = false;
					} else {
						// if the next element of a2 is same as or bigger
						// then current element of a1 we will add curr2
						in2not1.add(curr2);
						add2 = false;
						add1 = false;
					}
				}
				index2++;
			} else if (compare < 0) {
				if (index1 + 1 < l1.size()) {
					// check that if the next element of a1 is still
					// less than the current element then we will add the
					// curr2 element
					next1 = (String)l1.get(index1 + 1);
					compare = next1.compareTo(curr2);
					if (compare < 0) {
						add1 = true;
						add2 = false;
					} else {
						// if the next element of a1 is same as or bigger
						// then current element of a2 we will add curr1
						in1not2.add(curr1);
						add2 = false;
						add1 = false;
					}
				}
				index1++;
			}
			if (add1 && add2) {
				inBothFrom1.add(o1);
				inBothFrom2.add(o2);
			} else {
				if (add1) {
					in1not2.add(o1);
				}
				if (add2) {
					in2not1.add(o2);
				}
			}
		}

		// we reach the end of list 2 but not the end of list 1
		// or list 2 does nto contain any element
		// add all the rest of the l1 to collection
		if (index2 == l2.size() - 1 || l2.size() == 0) {
			curr2 = null;
			curr1 = null;
			o1 = null;
			o2 = null;
			if (l2.size() > 0) {
				o2 = l2.get(l2.size()-1);
				curr2 = o2.toString();
				for (int i=index1; i<l1.size(); i++) {
					o1 = l1.get(i);
					curr1 = o1.toString();
					compare = curr1.compareTo(curr2);
					if (compare == 0) {
						inBothFrom1.add(o1);
					} else {
						in1not2.add(o1);
					}
				}
				// check that last element is not inside the rest of the list
				boolean addLastElement = true;
				if (curr2 != null) {
					for (int i=index1; i<l1.size(); i++) {
						o1 = l1.get(i);
						curr1 = o1.toString();
						compare = curr1.compareTo(curr2);
						if (compare == 0) {
							addLastElement = false;
							break;
						}
					}
				}
				if (addLastElement) {
					in2not1.add(o2);
				} else {
					inBothFrom2.add(o2);
				}
			} else {
				// since l2 size is zero we just add l1's element
				for (int i=index1; i<l1.size(); i++) {
					o1 = l1.get(i);
					in1not2.add(o1);
				}
			}
		}

		// we reach the end of list 1 but not the end of list 2
		// or list 1 does nto contain any element
		// add all the rest of the l2 to collection
		if ((index1 == l1.size() - 1 && index2 != l2.size() - 1) || l1.size() == 0) {
			curr2 = null;
			curr1 = null;
			o1 = null;
			o2 = null;
			if (l1.size() > 0) {
				o1 = l1.get(l1.size()-1);
				curr1 = o1.toString();
				for (int i=index2; i<l2.size(); i++) {
					o2 = l2.get(i);
					curr2 = o2.toString();
					compare = curr1.compareTo(curr2);
					if (compare == 0) {
						inBothFrom2.add(o2);
					} else {
						in2not1.add(o2);
					}
				}
				// check that last element is not inside the rest of the list
				boolean addLastElement = true;
				if (curr1 != null) {
					for (int i=index2; i<l2.size(); i++) {
						o2 = l2.get(i);
						curr2 = o2.toString();
						compare = curr2.compareTo(curr1);
						if (compare == 0) {
							addLastElement = false;
							break;
						}
					}
				}
				if (addLastElement) {
					in1not2.add(o1);
				} else {
					inBothFrom1.add(o1);
				}
			} else {
				for (int i=index2; i<l2.size(); i++) {
					o2 = l2.get(i);
					in2not1.add(o2);
				}
			}
		}
	}

	/**
	 * Flattens a collections of collection. The new list would be a 
	 * collection containing all the collection inside the original
	 * collection
	 * @param collection a parent collection contain child collection
	 * @return the flattened collection 
	 */
	public static Collection flatten(Collection c) {
		Collection retVal = new ArrayList();
		MiscUtil.getListChild(c, retVal);
		return retVal;
	}
	/** 
	 * Get the list of child collections from a parent collection 
	 * It is a reentrant method
	 * @param theParent the parent collection
	 * @param c the output collection
	 */
	private static void getListChild(Collection theParent, Collection c) {
		c.add(theParent);
		if (theParent.size() > 0) {
			Iterator iter = theParent.iterator();
			while (iter.hasNext()) {
				Object iterObj = iter.next();
				if (iterObj != null && iterObj instanceof Collection) {
					Collection theChild = (Collection)iterObj;
					MiscUtil.getListChild(theChild, c);
				}
			}
		}
	}
	
	/**
	 * Sets the proxy for internet connection
	 * @param proxyHost the host of the proxy server
	 * @param the port of the proxy server
	 */
	public static void setProxy(String proxyHost, int proxyPort) {
		MiscUtil.setProxy(proxyHost, "" + proxyPort);
	}
	/**
	 * Sets the proxy for internet connection
	 * @param proxyHost the host of the proxy server
	 * @param the port of the proxy server
	 */
	public static void setProxy(String proxyHost, String proxyPort) {
		System.getProperties().put("proxySet", "true");
		System.getProperties().put("proxyHost", proxyHost);
		System.getProperties().put("proxyPort", proxyPort);
	}

	/**
	 * Generates a random alpha numeric string according to the format.
	 * X: upper case letters
	 * x: lower case letters
	 * n: numeric numbers
	 * @param format the format of the random string
	 */
	public static String getRandomString(String format) {
		int maxSleep = 500 + NumeralUtil.getRandomInt(200) + NumeralUtil.getRandomInt(1000);
		return getRandomString(format, maxSleep);
	}

	
	/**
	 * Generates a random alpha numeric string according to the format.
	 * X: upper case letters
	 * x: lower case letters
	 * n: numeric numbers
	 * @param format the format of the random string
	 * @param maxSleep the randomness seed of the sleep
	 */
	public static String getRandomString(String format, int maxSleep) {
		char c = ' ';
		if (format == null || format.length() == 0) {
			return "";
		}
		StringBuffer sb = new StringBuffer();
		int theChar = 0;
		String s = null;
		for (int i=0; i<format.length(); i++) {
			c = format.charAt(i);
			s = " ";
			if (c == 'X' || c == 'A') {
				theChar = 10 + (NumeralUtil.getRandomInt(26*5000) % 26);
				c = Character.forDigit(theChar, Character.MAX_RADIX);
				s = "" + Character.toUpperCase(c);
				sb.append(s);
			} else if (c == 'x' || c == 'a') {
				theChar = 10 + (NumeralUtil.getRandomInt(26*5000) % 26);
				c = Character.forDigit(theChar, Character.MAX_RADIX);
				s = "" + Character.toLowerCase(c);
				sb.append(s);
			} else if (c == 'n' || c == 'N' || c == '0') {
				s = "" + (NumeralUtil.getRandomInt(5000) % 10);
				sb.append(s);
			} else if (c == 'y' || c == 'Y') {
				char c1 = ' ';
				StringBuffer sb2 = new StringBuffer();
				for (int j=i; j<format.length(); j++) {
					c1 = format.charAt(j);
					if (c1 == 'y' || c1 == 'Y') {
						sb2.append(c1);
					} else {
						break;
					}
				}
				int size = sb2.length();
				Timestamp ts = DateUtil.getCurrentTimestamp();
				if (size == 2 || size == 4) {
					sb.append(DateUtil.formatDate(sb2.toString(), ts));
				} else {
					sb.append(sb2.toString());
				}
				i += size-1;
			} else if (c == 'M') {
				char c1 = ' ';
				StringBuffer sb2 = new StringBuffer();
				for (int j=i; j<format.length(); j++) {
					c1 = format.charAt(j);
					if (c1 == 'M') {
						sb2.append(c1);
					} else {
						break;
					}
				}
				int size = sb2.length();
				Timestamp ts = DateUtil.getCurrentTimestamp();
				if (size == 2 || size == 3) {
					sb.append(DateUtil.formatDate(sb2.toString(), ts));
				} else {
					sb.append(sb2.toString());
				}
				i += size-1;
			} else if (c == 'd') {
				char c1 = ' ';
				StringBuffer sb2 = new StringBuffer();
				for (int j=i; j<format.length(); j++) {
					c1 = format.charAt(j);
					if (c1 == 'd') {
						sb2.append(c1);
					} else {
						break;
					}
				}
				int size = sb2.length();
				Timestamp ts = DateUtil.getCurrentTimestamp();
				if (size == 2) {
					sb.append(DateUtil.formatDate(sb2.toString(), ts));
				} else {
					sb.append(sb2.toString());
				}
				i += size - 1;
			} else if (c == 'H') {
				char c1 = ' ';
				StringBuffer sb2 = new StringBuffer();
				for (int j=i; j<format.length(); j++) {
					c1 = format.charAt(j);
					if (c1 == 'H') {
						sb2.append(c1);
					} else {
						break;
					}
				}
				int size = sb2.length();
				Timestamp ts = DateUtil.getCurrentTimestamp();
				if (size == 2) {
					sb.append(DateUtil.formatDate(sb2.toString(), ts));
				} else {
					sb.append(sb2.toString());
				}
				i += size-1;
			} else if (c == 'm') {
				char c1 = ' ';
				StringBuffer sb2 = new StringBuffer();
				for (int j=i; j<format.length(); j++) {
					c1 = format.charAt(j);
					if (c1 == 'm') {
						sb2.append(c1);
					} else {
						break;
					}
				}
				int size = sb2.length();
				Timestamp ts = DateUtil.getCurrentTimestamp();
				if (size == 2) {
					sb.append(DateUtil.formatDate(sb2.toString(), ts));
				} else {
					sb.append(sb2.toString());
				}
				i += size-1;
			} else if (c == 's') {
				char c1 = ' ';
				StringBuffer sb2 = new StringBuffer();
				for (int j=i; j<format.length(); j++) {
					c1 = format.charAt(j);
					if (c1 == 's') {
						sb2.append(c1);
					} else {
						break;
					}
				}
				int size = sb2.length();
				Timestamp ts = DateUtil.getCurrentTimestamp();
				if (size == 2) {
					sb.append(DateUtil.formatDate(sb2.toString(), ts));
				} else {
					sb.append(sb2.toString());
				}
				i += size-1;
			} else if (c == 'S') {
				char c1 = ' ';
				StringBuffer sb2 = new StringBuffer();
				for (int j=i; j<format.length(); j++) {
					c1 = format.charAt(j);
					if (c1 == 'S') {
						sb2.append(c1);
					} else {
						break;
					}
				}
				int size = sb2.length();
				Timestamp ts = DateUtil.getCurrentTimestamp();
				if (size == 3) {
					sb.append(DateUtil.formatDate(sb2.toString(), ts));
				} else {
					sb.append(sb2.toString());
				}
				i += size-1;
			} else {
				s = "" + c;
				sb.append(s);
			}
			ThreadUtil.randomSleep(maxSleep);
		}
		return sb.toString();
	}
	
	public static String getSysId() {
		StringBuffer sb = new StringBuffer();
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
			DecimalFormat df = new DecimalFormat("000000");
			Random rnd = new Random(System.currentTimeMillis());
			int theInt = rnd.nextInt(999999);
			String s = sdf.format(new Timestamp(System.currentTimeMillis()));
			sb.append(s);
			s = df.format(theInt);
			sb.append(s);
		} catch (Exception e) {
		}
		try {
			Thread.sleep(15);
		} catch(Exception e) {
		}
		return sb.toString();
	}

	
/*
	public static void main(String args[]){
		Collection c = new ArrayList();
		c.add("hello");
		c.add("world");
		System.out.println(MiscUtil.combineCollectionElement(c, 1));
		System.out.println(MiscUtil.combineCollectionElement(c, -1));
		c.add("there");
		System.out.println(MiscUtil.combineCollectionElement(c, 1));
		System.out.println(MiscUtil.combineCollectionElement(c, -1));
		System.out.println(MiscUtil.combineCollectionElement(c, 2));
		System.out.println(MiscUtil.combineCollectionElement(c, -2));

		c.add("haha");
		System.out.println(MiscUtil.combineCollectionElement(c, 1));
		System.out.println(MiscUtil.combineCollectionElement(c, -1));
		System.out.println(MiscUtil.combineCollectionElement(c, 2));
		System.out.println(MiscUtil.combineCollectionElement(c, -2));
		System.out.println(MiscUtil.combineCollectionElement(c, 3));
		System.out.println(MiscUtil.combineCollectionElement(c, -3));
	}
//*/

	public static void main(String args[]){
		/*
		// To run this test you will need
		// a TestBean which is a normal javabean with
		// 2 attributes: name and message.
		// The package setting for the TestBean
		// is to ensure that it will work regardless of
		// any package.
		HashMap map = new HashMap();
		Object o = null;

		TestBean bean = new TestBean();
		bean.setName("John");
		bean.setMessage("Hello World");
		Double doubleArray[] = new Double[4];
		doubleArray[0] = new Double(12.3);
		doubleArray[1] = new Double(45.6);
		doubleArray[2] = new Double(78.9);
		doubleArray[3] = new Double(101112.0);
		String s[] = {"hello", "world", "how", "are", "you"};

		map.put("myRedirect", "${myInt}");
		map.put("myString", "Value1");
		map.put("myString2", "Value2");
		map.put("myInt", new Integer(999));
		map.put("myIndex", new Integer(2));
		map.put("myBean", bean);
		map.put("myArray", doubleArray);
		map.put("a", "${b}");
		map.put("b", "${c}");
		map.put("c", "${a}"); // recursive setup
		map.put("myStringArray", s);
		map.put("myRedirect2", "${myStringArray}");
		map.put("testDir3", "${testDir2}/3");
		map.put("testDir", "C:/Temp");
		map.put("testDir1", "${testDir}/1");
		map.put("testDir2", "${testDir1}/2");
		map.put("myBean.name.toString()", "I am the one");

		int size = 20;
		Object[] test = new Object[size];
		String[] refr = new String[size];
		int testOne = -1;

		test[0] = "${myString}";
		refr[0] = (String)map.get("myString");
		test[1] = "${myInt}";
		refr[1] = "" + map.get("myInt");
		test[2] = "${myBean.name}";
		refr[2] = "" + bean.getName();
		test[3] = "${myBean.sayHello()}";
		refr[3] = "" + bean.sayHello();
		test[4] = "${myArray[2]}";
		refr[4] = "" + doubleArray[2];
		test[5] = "${myRedirect}";
		refr[5] = "999";
		test[6] = "myString${myIndex}";
		refr[6] = "myString2";
		test[7] = "${myString${myIndex}}";
		refr[7] = "DefaultValue";
		test[8] = "${myString}${myString2}${myInt}${myBean.sayHello()}";
		refr[8] = "Value1Value2999Hello There John";
		test[9] = "test";
		refr[9] = "DefaultValue";
		test[10] = new Integer(1);
		refr[10] = "DefaultValue";
		test[11] = "${a}";
		refr[11] = "DefaultValue";
		test[12] = "${myStringArray[1].length()}";
		refr[12] = "" + s[1].length();
		test[13] = "${myArray[3].toString().length()}";
		refr[13] = "" + doubleArray[3].toString().length();
		test[14] = "${testDir3}";
		refr[14] = "C:/Temp/1/2/3";
		//test[15] = "${testDot.attribute.value}";
		test[15] = "${myBean.name.toString()}";
		refr[15] = "" + bean.getName().toString();
		test[16] = "${myBean.sayHello2(myString)}";
		refr[16] = "" + bean.sayHello2("Value1");
		test[17] = "${3+2+1}";
		refr[17] = "" + (3+2+1);
		test[18] = "${3+4*2}";
		refr[18] = "" + (3+4*2);
		test[19] = "${myInt + myIndex}";
		refr[19] = "" + (((Integer)map.get("myInt")).intValue() + ((Integer)map.get("myIndex")).intValue());
		

		Iterator iter = map.entrySet().iterator();
		while (iter.hasNext()) {
			java.util.Map.Entry entry = (java.util.Map.Entry)iter.next();
			System.out.print("[");
			System.out.print(entry.getKey());
			System.out.print("]  ==  [");
			System.out.print(entry.getValue());
			System.out.print("] ");
			if (entry.getValue() != null) {
				System.out.print(" [");
				System.out.print(entry.getValue().getClass().getName());
				System.out.print("]");
			}
			System.out.println("");
		}
		System.out.println("====================");

		for (int i=0; i<test.length; i++) {
			if (testOne < 0 || testOne == (i+1)) {
				o = MiscUtil.resolve(map, test[i], "DefaultValue", "${", "}");
				if (o == null) {
					continue;
				}
				System.out.print("[");
				if (i+1<10) System.out.print("0");
				System.out.print((i+1) + "/" + test.length + "] [");
				System.out.print((o.toString().equals(refr[i]))?"passed":"failed");
				System.out.print("] { ");
				System.out.print(refr[i]);
				System.out.print(" == ");
				System.out.print(o);
				System.out.print(" } ");
				System.out.print(" [");
				System.out.print(test[i]);
				System.out.println("]");
			}
		}

		System.out.println("====================");

		System.out.println("Checking evaluting object 1st or whole parameter 1st");
		o = MiscUtil.resolve(map, "${myBean.name.toString()}", "DefaultValue", "${", "}", true);
		System.out.print(o);
		System.out.print(" <> ");
		o = MiscUtil.resolve(map, "${myBean.name.toString()}", "DefaultValue", "${", "}", false);
		System.out.println(o);
//*/
/*		Map map = new HashMap();
		String s = "This is a test of ${var} null.";
		System.out.println(MiscUtil.resolve(map, s, "*"));
		System.out.println(MiscUtil.resolve(map, s, s));
		s = "This is a test of ${var} null. ${var2}";
		System.out.println(MiscUtil.resolve(map, s, "*"));
		System.out.println(MiscUtil.resolve(map, s, s));
		
		System.out.println("A:" + Character.getNumericValue('A'));
		System.out.println("Z:" + Character.getNumericValue('Z'));
		System.out.println("a:" + Character.getNumericValue('a'));
		System.out.println("z:" + Character.getNumericValue('z'));
		
		System.out.println(Character.forDigit(10, Character.MAX_RADIX));
		System.out.println(Character.forDigit(35, Character.MAX_RADIX));
		
		System.out.println("==========");
		System.out.println(MiscUtil.getRandomString("XXXxxxnnnn"));
		
		for (int i=0; i<10; i++) {
			System.out.println(MiscUtil.getRandomString("yyyyMMddHHmmssnnnnnn"));			
		}//*/
		
		for (int i=0; i<100; i++) {
			System.out.println(MiscUtil.getSysId());
		}
	}
}

