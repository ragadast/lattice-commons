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

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.io.StringWriter;
import java.io.PrintWriter;
import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
//import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.beanutils.WrapDynaBean;

/**
 * This is an utility class to help in string processing.
 */
public final class StringUtil {

	private static final Log log = LogFactory.getLog(StringUtil.class);
	public static String NEW_LINE_CHAR = System.getProperty("line.separator");
	
	private StringUtil(){}

	/**
	 * Tokenise the source string into collection of string elements.
	 * @param source the string to be parsed.
	 * @param separator the separator string
	 * @return a collection of the string elements
	 */
	public static List tokenize(String source, String separator) {
		return tokenize(source, separator, false);
	}
	
	/**
	 * Tokenize the string into a collection of string elements.
	 * @param source the source string
	 * @param param the size of the of each element defined as an int array
	 * @return the list
	 */
	public static List tokenize(String source, int[] param) {
		ArrayList a = new ArrayList();
		
		if (source == null || param == null || param.length < 1) {
			return a;
		}
		// check length
		if (source.length() < param[param.length - 1]) {
			return a;
		}
		int prev = 0;
		int curr = 0;
		String s = null;
		for (int i=0; i<param.length; i++) {
			curr = prev + param[i];
			if (i == param.length - 1 && curr > source.length()) {
				continue;
			}
			s = source.substring(prev, curr).trim();
			a.add(s);
			prev = curr;
		}
		if (prev < source.length()) {
			s = source.substring(prev, source.length()).trim();
			
		}
		a.add(s);
		return a;
	}

	public static String[] tokenizeIntoStringArray(String source, int[] param) {
		Collection c = tokenize(source, param);
		if (log.isDebugEnabled()) { log.debug("Source:" + source); }
		if (c != null){
			return (String[])c.toArray(new String[c.size()]);
		}
		return null;
	}

	/**
	 * Tokenise the source string into collection of string elements.
	 * @param source the string to be parsed.
	 * @param separator the separator string
	 * @param includeZeroLengthElement true if wish to include zero length elements
	 * @return a collection of the string elements
	 */
	public static List tokenize(String source, String separator, boolean includeZeroLengthElement) {
		List l = new ArrayList();

		if (source == null || separator == null) {
			return l;
		}
		if (source.indexOf(separator) < 0) {
			l.add(source);
			return l;
		}

		int prev = 0;
		int curr = source.indexOf(separator, prev);
		String temp = null;
if (log.isDebugEnabled()) { log.debug("Current Index:" + curr); }

		int sepLen = separator.length();
		while (curr >= 0){
			temp = null;
			if (curr >= prev) {
				temp = source.substring(prev, curr);
			}
			if (temp != null) {
				temp = temp.trim();
				if (temp.length() != 0 || includeZeroLengthElement) { l.add(temp); }
				prev = curr + sepLen;
				curr = source.indexOf(separator, prev);
			}
if (log.isDebugEnabled()) { log.debug("Current Index:" + curr); }
		}
		temp = source.substring(prev, source.length());
		temp = temp.trim();
if (log.isDebugEnabled()) { log.debug("Result:" + temp); }
		if (includeZeroLengthElement || temp.length() != 0) { l.add(temp); }
		return l;
	}

	/**
	 * Tokenise the source string into a string array.
	 * @param source the source string to be parsed
	 * @param separator the separator string
	 * @return a array of string elements
	 * @see StringUtil#tokenize(String source, String separator)
	 */
	public static String[] tokenizeIntoStringArray(String source, String separator) {
		Collection c = tokenize(source, separator);
if (log.isDebugEnabled()) { log.debug("Source:" + source); }
		if (c != null){
			return (String[])c.toArray(new String[c.size()]);
		}
		return null;
	}

	/**
	 * Tokenise the source string into a string array.
	 * @param source the source string to be parsed
	 * @param separator the separator string
	 * @param includeZeroLengthElement true if wish to include zero length elements
	 * @return a array of string elements
	 * @see StringUtil#tokenize(String source, String separator)
	 */
	public static String[] tokenizeIntoStringArray(String source, String separator, boolean includeZeroLengthElement) {
		Collection c = tokenize(source, separator, includeZeroLengthElement);
if (log.isDebugEnabled()) { log.debug("Source:" + source); }
		if (c != null){
			return (String[])c.toArray(new String[c.size()]);
		}
		return null;
	}
	
	/**
	 * Tokenize the string into a map
	 * 2 separaters are required, one the separator between each element
	 * and the separator between the attribute
	 */
	public static Map tokenizeIntoMap(String source, String element, String attribute) {
		List l = tokenize(source, element);
		List l2 = null;
		Map map = new HashMap();
		for (int i=0; i<l.size(); i++) {
			String s = (String)l.get(i);
			l2 = StringUtil.tokenize(s, attribute);
			if (l2.size() >= 2) {
				Object key = l2.get(0);
				Object value = l2.get(1);
				if (key != null && value != null) {
					map.put(key, value);
				}
			}
		}
		return map;
	}

	/**
	 * Substring the source string
	 * @param source the source string to be processed
	 * @param from the starting string
	 * @param to the ending string
	 * @return the sub string
	 */
	public static String substring(String source, String from, String to) {
		return substring(source, from, to, true);
	}

	/**
	 * Return the character String before a certain string.
	 * For example:
	 * <pre>
	 * String s = "Hello This is a test.";
	 * String result = StringUtil.charBefore(s, "his");
	 * System.out.println(result);
	 * </pre>
	 * The letter T will be printed.
	 * @param source the source string to be processed
	 * @param key the string key
	 * @return one character in String form before the key.
	 */
	public static String charBefore(String source, String key) {
		int index = source.indexOf(key);
		char c = '\0';
		String retVal = null;
		if (index > -1) {
			c = source.charAt(index - 1);
		}
		if (c != '\0') {
			retVal = "" + c;
		} else {
			retVal = null;
		}
		return retVal;
	}

	/**
	 * Checks if the test string is after the keyword in the source string.
	 * <pre>
	 * String s = "Hello this is a test.";
	 * boolean result = StringUtil.isAfter(s, "ell", "is");
	 * System.out.println(result); // true will be printed as "ello" is in front of "is"
	 * </pre>
	 * @param source the source string
	 * @param keyword the key string
	 * @param test the string in question
	 * @return true is the test string is after the keyword.
	 * false if one of them does not exist in the source string or
	 * the keyword is after the test string instead.
	 */
	public static boolean isAfter(String source, String keyword, String test) {
		if (source == null || keyword == null || test == null) return false;
		String s = source.toUpperCase();
		int index1 = s.indexOf(keyword.toUpperCase());
		int index2 = s.indexOf(test.toUpperCase());
		if (index1 < 0 || index2 < 0) { return false; }
		if (index1 < index2) { return true; }
		else return false;
	}

	/**
	 * Returns the count of occurence of test string within the source string
	 * @param source the source string
	 * @param test the string in question
	 * @return the count of occurence. 0 if no occurence.
	 */
	public static int countOf(String source, String test) {
		int count = 0;
		int index = -1;
		do {
			index = source.indexOf(test, index+1);
			if (index >= 0) { count++; }
		} while (index >= 0);
		return count;
	}

	/**
	 * Extracts a partial string from the source string.
	 * @param source the source string
	 * @param from the starting string
	 * @param to the ending string
	 * @param exclude true is the from and to string are to be excluded in the result
	 * @return the extracted string
	 */
	public static String substring(String source, String from, String to, boolean exclude) {
		String retVal = null;
		int index1 = -1, index2 = -1;
		index1 = source.indexOf(from);
if (log.isDebugEnabled()) { log.debug("Index1:" + index1); }
		if (index1 < 0) return null;

		index2 = source.indexOf(to, index1);
if (log.isDebugEnabled()) { log.debug("Index2:" + index2); }
		if (index2 < index1) return null;

		if (exclude) {
			retVal = source.substring(index1 + from.length(), index2);
		} else {
			int toindex = index2 + to.length();
			if (toindex > source.length()) toindex = source.length();
			retVal = source.substring(index1, index2 + to.length());
		}
		return retVal;
	}

	/**
	 * Replaces the key string with the new value inside the source.
	 * This method will only replace all the instance of the string inside
	 * the source string. To replace only the first instance please use the
	 * next method.
	 * @param source the source string
	 * @param key the string to be replaced
	 * @param value the new string to replace the key string
	 * @return the final result after replacement
	 */
	public static String replace(String source, String key, String value) {
		return replace(new StringBuffer(source), key, value, true);
	}

	/**
	 * Replaces the key string with the new value inside the source.
	 * This method allows the user the set to replace all instance of
	 * occurence or just the first instance of occurence inside the
	 * source string.
	 * @param source the source string
	 * @param key the string to be replaced
	 * @param value the new string to replace the key string
	 * @param all replace all if true. If false only the first instance.
	 * @return the final result after replacement
	 */
	public static String replace(String source, String key, String value, boolean all) {
		return replace(new StringBuffer(source), key, value, all);
	}

	/**
	 * Replaces the key string with the new value inside the source.
	 * Similar to above methods except for the string buffer input
	 * instead of string input.
	 * @param source the source string buffer
	 * @param key the string to be replaced
	 * @param value the new string to replace the key string
	 * @return the final result after replacement
	 */
	public static String replace(StringBuffer source, String key, String value, boolean all) {
		int count = 0;
		int index = source.toString().indexOf(key, count);
		while (index >= 0) {
			source.replace(index, index+key.length(), value);
			count = index + value.length();
			if (count > 0 && !all) break;
			index = source.toString().indexOf(key, count);

if (log.isDebugEnabled()) {
	log.debug("Index:" + index);
	log.debug("String:" + source.toString());
}
		}
		return source.toString();
	}

	/**
	 * Replaces the all the parameters in a string to the respective values
	 * @deprecated
	 * @see MiscUtil#resolve(Map, Object, Object, String, String);
	 */
	public static String replaceParameter(Map map, String source, String start, String end) {
		Object o = MiscUtil.resolve(map, source, source, start, end);
		if (o != null) {
			return o.toString();
		}
		return null;
	}

	public static String replace(String source, Map param) {
		return replace(source, param, true);
	}

	public static String replace(String source, Map param, boolean all) {
		StringBuffer sb = new StringBuffer();
		sb.append(source);
		return replace(sb, param, all);
	}
	public static String replace(StringBuffer source, Map param, boolean all) {
		if (param == null || source == null) {
			return null;
		}
		Iterator iter = param.keySet().iterator();
		Object oKey = null;
		Object oValue = null;
		String sKey = null;
		String sValue = null;
		while (iter.hasNext()) {
			oKey = iter.next();
			oValue = param.get(oKey);
			sKey = null;
			sValue = null;
			if (oKey != null) {
				sKey = oKey.toString();
			}
			if (oValue != null) {
				sValue = oValue.toString();
			}
			if (sKey != null && sValue != null) {
				replace(source, sKey, sValue, all);
			}
		}
		return source.toString();
	}
	

	/**
	 * @deprecated
	 */
	public static String replaceSQLParameter(String source, String start, String end, Map map) {
		StringBuffer sb = new StringBuffer(source);

		Collection c = StringUtil.extractParameter(source, start, end);
if (log.isDebugEnabled()) log.debug("Extract parameter..." + c);

		Iterator iter = c.iterator();
		Object key = null;
		Object value = null;
		while (iter.hasNext()) {
			key = iter.next();
			if (key != null) {
				value = map.get(key);
				String replaceKey = start + key + end;
				String replaceValue = null;
if (log.isDebugEnabled()) log.debug("{" + key + ", " + value + "}");
				if (value != null) {
					if (value instanceof String) {
						replaceValue = "'" + value.toString() + "'";
					} else if (value instanceof Byte || value instanceof Short ||
						value instanceof Integer || value instanceof Long ||
						value instanceof Float || value instanceof Double) {
						replaceValue = value.toString();
					} else if (value instanceof Timestamp) {
						Timestamp ts = (Timestamp)value;
						//java.util.Date date = new java.util.Date(ts.getTime());
						SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
						String s = sdf.format(ts);
						replaceValue = "TO_DATE('" + s + "', 'yyyymmddhh24miss')" ;
					}
				} else {
					replaceValue = "IS NULL";
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

	/**
	 * Extracts all the parameters from a string.
	 * @param source source string to parse
	 * @param start starting characters for indicating parameter
	 * @param end ending characters for indicating end of parameter
	 * @return a collection of parameters
	 */
	public static List extractParameter(String source, String start, String end) {
		StringBuffer sb = new StringBuffer(source);
		List l = new ArrayList();
		int index1 = -1;
		int index2 = -1;
		String param = null;
if (log.isDebugEnabled()) {
	StringBuffer disp = new StringBuffer();
	disp.append("source:");
	for (int i=0; i<source.length(); i++) {
		disp.append(i%10);
	}
	log.debug(disp.toString());
	log.debug("source:" + source);
}
		do {
			param = null;
			index1 = sb.toString().indexOf(start, index2);
			index2 = sb.toString().indexOf(end, index1+start.length());

			if (index1 < index2 && index1 > -1 && index2 > -1) {
				int from = index1 + start.length();
				int to = -1;
				if (index2 > source.length()) {
					to = source.length();
				} else {
					to = index2;// - end.length();
				}
				param = sb.substring(from, to);
if (log.isDebugEnabled()) { log.debug("[" + from + ", " + to + "] " + param); }
			}
			if (param != null) {
				index2 += end.length();
				l.add(param);
			}
		} while (param != null);
if (log.isDebugEnabled()) { log.debug("-----"); }
		return l;
	}

	/**
	 * Gets the stack trace in string form.
	 * @param t the throwable to be parsed
	 * @return the string form of the stack trace
	 */
	public static String getStackTraceMessage(Throwable t) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		t.printStackTrace(pw);
		pw.flush();
		sw.flush();
		return sw.toString();
	}

	/**
	 * Parse the stack trace to get the various information:
	 * package name, class name, method name, and line number
	 * @param t throwable to be parsed.
	 * @return a collection of string stating the results in a
	 * string array form. The elements of the string array is as
	 * listed above
	 */
	public static Collection parseStackTrace(Throwable t) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		t.printStackTrace(pw);
		pw.flush();
		sw.flush();
		StringBuffer sb = sw.getBuffer();
		try { pw.close(); } catch (Exception ex) {}
		try { sw.close(); } catch (Exception ex) {}
		sw = null;
		pw = null;

		Collection c = StringUtil.tokenize(sb.toString(), "\n");
		ArrayList a = new ArrayList();
		Iterator iter = c.iterator();
		iter.next(); // drop 1st element;
		String className = null, packageName = null;
		String methodName = null, lineNumber = null;
		int index = 0;
		String result[] = null;
		while (iter.hasNext()) {
			result = new String[4];
			String s = (String)iter.next();

			index = s.indexOf("(");
			lineNumber = s.substring(index, s.length());
			packageName = s.substring(3, index);

			index = packageName.lastIndexOf(".");
			methodName = packageName.substring(index+1, packageName.length());
			packageName = packageName.substring(0, index);

			index = packageName.lastIndexOf(".");
			className = packageName.substring(index+1, packageName.length());
			packageName = packageName.substring(0, index);

			lineNumber = StringUtil.substring(lineNumber, ":", ")");

if (log.isDebugEnabled()) {
	log.debug("packageName:" + packageName);
	log.debug("className:" + className);
	log.debug("methodName:" + methodName);
	log.debug("lineNumber:" + lineNumber);
}

			result[0] = packageName;
			result[1] = className;
			result[2] = methodName;
			result[3] = lineNumber;
			a.add(result);
		}
		return a;
	}


	/** Full mode <FullTag></FullTag>*/
	public static final int MODE_FULL_LONG = 1;

	/** Short mode <FullTag/>*/
	public static final int MODE_FULL_STANDARD = 2;

	/** Only the start tag <StartTag> */
	public static final int MODE_START_TAG = 3;

	/** Only the end tag </EndTag>*/
	public static final int MODE_END_TAG = 4;

	/**
	 * Print the properties of an object into a xml string.
	 * This is useful in the object's toString method.
	 * @param o the object to be converted
	 * @return the string-fied xml form of the object
	 */
	public static String formatObjectToXmlString(Object o) {
		return StringUtil.formatObjectToXmlString(o, StringUtil.MODE_FULL_STANDARD, false);
	}

	/**
	 * Print the properties of an object into a xml string.
	 * This is useful in the object's toString method.
	 * @param o the object to be converted
	 * @param mode one of the above mode
	 * @param displayAll display all attributes including those which are null.
	 * @return the string-fied xml form of the object
	 */
	public static String formatObjectToXmlString(Object o, int mode, boolean displayAll) {
		if (o == null) return "<NullClass/>";

		StringBuffer sb = new StringBuffer();
		String s = o.getClass().getName();
		String p = o.getClass().getPackage().getName();
		String className = s.substring(p.length()+1, s.length());

		if (mode == StringUtil.MODE_END_TAG) {
			sb.append("</");
			sb.append(className);
			sb.append(">");
			return sb.toString();
		}

		sb.append("<");
		sb.append(className);

		// list of attributes
		Field f[] = o.getClass().getDeclaredFields();
		WrapDynaBean dyn = null;
		try {
			dyn = new WrapDynaBean(o);
		} catch (Exception e) {}

		for (int i=0; i<f.length; i++) {
			String name = f[i].getName();
			int modifier = f[i].getModifiers();
			if (Modifier.isFinal(modifier)
				|| Modifier.isAbstract(modifier)
				|| Modifier.isInterface(modifier)
				|| Modifier.isStatic(modifier)) {
				continue;
			}

			Object value = null;
			try {
				value = dyn.get(name);
			} catch (Exception e) {
				//if (log.isErrorEnabled()) { log.error(e); }
			}
			if (name != null) {
				if ((value != null && !displayAll) ||
					(displayAll)){
					sb.append(" ");
					sb.append(name);
					sb.append("=\"");
					sb.append(value);
					sb.append("\"");
				}
			}
		}
		switch (mode) {
			default:
			case StringUtil.MODE_FULL_STANDARD:
				sb.append("/>");
				break;
			case StringUtil.MODE_FULL_LONG:
				sb.append("></");
				sb.append(className);
				sb.append(">");
				break;
			case StringUtil.MODE_START_TAG:
				sb.append(">");
				break;
		}
		return sb.toString();
	}

	/**
	 * Get the string representation of the byte from data from the file.
	 * The data is displayed as Hexadecimal representation of the bytes
	 * @param filename the name of the file
	 */
	public static String getHexDataFromFile(String filename) {
		File f = null;
		InputStream is = null;
		StringBuffer sb = new StringBuffer();
		try {
			f = new File(filename);
			is = new FileInputStream(f);
			int i = is.read();
			while (i >= 0) {
				String s = Integer.toHexString(i);
				if (s.length() == 1) {
					s = "0" + s;
				}
				sb.append(s.toUpperCase());
				i = is.read();
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			try {is.close();} catch (Exception e) {}
		}
		return sb.toString();
	}

	/**
	 * Encode the string for xml
	 */
	public static String encodeXmlString(String source) {
		if (source.indexOf("&") >= 0) {
			source = StringUtil.replace(source, "&", "&#38;");
		}
		if (source.indexOf("<") >= 0) {
			source = StringUtil.replace(source, "<", "&#60;");
		}
		if (source.indexOf(">") >= 0) {
			source = StringUtil.replace(source, ">", "&#62;");
		}
		if (source.indexOf("\'") >= 0) {
			source = StringUtil.replace(source, "\'", "&#39;");
		}
		if (source.indexOf("\"") >= 0) {
			source = StringUtil.replace(source, "\"", "&#34;");
		}
		return source;
	}

	/**
	 * Adds an xml style key-value attribute to a stringbuffer
	 * @param sb the stringbuffer to append to
	 * @param key the key of the attribute pair
	 * @param value the value of the attribute pair
	 */
	public static void addAttribute(StringBuffer sb, Object key, Object value) {
		addAttribute(sb, key, value, false);
	}

	/**
	 * Adds an xml style key-value attribute to a stringbuffer
	 * @param sb the stringbuffer to append to
	 * @param key the key of the attribute pair
	 * @param value the value of the attribute pair
	 * @param includeNull
	 */
	public static void addAttribute(StringBuffer sb, Object key, Object value, boolean includeNull) {
		if (sb == null) return;
		if (key == null) return;
		if (value == null && !includeNull) { return; }
		sb.append(key).append("=\"").append(value).append("\" ");
	}
	
	/**
	 * Extract the parameter and its corresponding value give an input string
	 * E.g.
	 * <code>
	 * String input = "How are you? John. My name is Peter";
	 * String template = "How are you? ${name}. My name is ${self}";
	 * Map map = StringUtil.extractParameterValue(input, template);
	 * System.out.println(map.get("name")); // John
	 * System.out.println(map.get("self")); // Peter
	 * </code>
	 * @param template the template string
	 * @param template the input string
	 */
	public static Map extractParameterValue(String input, String template, String start, String end) {
		Map map = new HashMap();
		if (template == null || input == null || start == null || end == null) {
			return map;
		}
		Collection c = StringUtil.extractParameter(template, start, end);
		if (c == null || c.size() == 0) {
			return map;
		}
		ArrayList param = null;
		if (c instanceof ArrayList) {
			param = (ArrayList)c;
		} else {
			param = new ArrayList();
			param.addAll(c);
		}
		String[] envelop = new String[c.size() + 1];
		int prevIndex = 0;
		int currIndex = -1;
		int i = 0;
		for (i=0; i<param.size(); i++) {
			String s = (String)param.get(i);
			currIndex = template.indexOf(s, prevIndex);
			currIndex -= start.length();
			if (currIndex > template.length()) {
				continue;
			}
			envelop[i] = template.substring(prevIndex, currIndex);
			prevIndex = currIndex + start.length() + s.length() + end.length();
		}
		if (prevIndex > 0) {
			envelop[i] = template.substring(prevIndex, template.length());
		}
		prevIndex = 0;
		for (i=1; i<envelop.length; i++) {
			if (envelop[i] == null || envelop[i-1] == null) {
				break;
			}
			prevIndex = input.indexOf(envelop[i-1], prevIndex);
			if (prevIndex > input.length() || prevIndex < 0) {
				break;
			}
			prevIndex += envelop[i-1].length();
			if (envelop[i].equals("")) {
				if (i == envelop.length - 1) {
					currIndex = input.length();
				} else {
					currIndex = input.indexOf(envelop[i+1], prevIndex);
				}
			} else {
				currIndex = input.indexOf(envelop[i], prevIndex);
			}
			if (currIndex < 0 || currIndex > input.length()) {
				break;
			}
			String key = (String)param.get(i-1);
			String value = input.substring(prevIndex, currIndex);
			if (key != null && value != null) {
				map.put(key, value);
			}
		}
		
		return map;
	}
	
	public static Map extractParameterValue(String input, String template) {
		return extractParameterValue(input, template, "${", "}");
	}
	
	public static Object parseObjectFromString(String s) {
		if (s == null) return null;
		s = s.trim();
		if (s.length() == 0) return null;
		Object o = null;
		if (!s.startsWith("[") || !s.endsWith("]")) {
			return null;
		}
		s = s.substring(1,s.length()-1);
		String[] elements = StringUtil.tokenizeIntoStringArray(s, "|");
		if (elements == null || elements.length == 0) { return null; }
		try {
			Map map = new HashMap();
			for (int i=1; i<elements.length; i++) {
				String[] components = StringUtil.tokenizeIntoStringArray(elements[i], "=");
				if (components != null && components.length == 2) {
					Object key = components[0];
					Object value = components[1];
					if (components[1].startsWith("[") && 
						components[1].endsWith("]")) {
						value = StringUtil.parseObjectFromString(components[1]);
					}
					if (key != null && value != null) map.put(key, value);
				}
			}
			o = ClassUtil.newInstance(elements[0]);
			MiscUtil.populateObject(o, map);
		} catch (Exception e) {
			if (log.isErrorEnabled()) { log.error(e); }
		}
		return o;
	}
	
	public static String formatObjectToString(Object o) {
		return formatObjectToString(o, false);
	}
	
	public static String formatObjectToString(Object o, boolean includeChild) {
		if (o == null) return "";
		if (o == null) return "";

		StringBuffer sb = new StringBuffer();
		String className = o.getClass().getName();

		sb.append("[");
		sb.append(className);
		sb.append("|");

		// list of attributes
		Field f[] = o.getClass().getDeclaredFields();
		WrapDynaBean dyn = null;
		try {
			dyn = new WrapDynaBean(o);
		} catch (Exception e) {}

		for (int i=0; i<f.length; i++) {
			String name = f[i].getName();
			int modifier = f[i].getModifiers();
			if (Modifier.isFinal(modifier)
				|| Modifier.isAbstract(modifier)
				|| Modifier.isInterface(modifier)
				|| Modifier.isStatic(modifier)) {
				continue;
			}
			Object value = null;
			try {
				value = dyn.get(name);
			} catch (Exception e) {
				//if (log.isErrorEnabled()) { log.error(e); }
			}
			if (name != null && value != null) {
				sb.append(name);
				sb.append("=");
				if (value instanceof Map) {
					Map map = (Map)value;
					if (includeChild) {
						sb.append(value);
					} else {
						sb.append(map.size());
					}
					sb.append("|");
				} else if (value instanceof Collection){
					Collection c = (Collection)value;
					if (includeChild) {
						sb.append(value);
					} else {
						sb.append(c.size());
					}
					sb.append("|");
				} else {
					sb.append(value);
					sb.append("|");
				}
			}
		}
		sb.deleteCharAt(sb.length() - 1);
		sb.append("]");
		return sb.toString();
	}
	
	/** Formats a map */
	public static String formatMap(Map map) {
		if (map != null) {
			return map.toString();
		}
		return null;
	}
	/**
	 * Formats a map into string
	 * @param map the map to be formatted
	 * @param separator the separator to separate the elements
	 * @param enclosing to include the enclosing braces or not
	 * @return the formatted string
	 */
	public static String formatMap(Map map, String separator, boolean enclosing) {
		StringBuffer sb = new StringBuffer();
		if (map == null || separator == null) {
			return null;
		}
		Iterator iter = map.keySet().iterator();
		if (enclosing) {
			sb.append("{");
		}
		while (iter.hasNext()) {
			Object key = iter.next();
			Object value = map.get(key);
			if (key != null) {
				sb.append(key).append("=");
				if (value != null) {
					sb.append(value);
				}
				sb.append(separator);
			}
		}
		int len = separator.length();
		if (sb.length() > len) {
			int startIndex = sb.length() - len;
			int endIndex = sb.length();
			String s = sb.substring(startIndex, endIndex);
			if (s.equals(separator)) {
				for (int i=0; i<len; i++) {
					sb.deleteCharAt(sb.length()-1);
				}
			}
		}
		if (enclosing) {
			sb.append("}");
		}
		return sb.toString();
	}
	/**
	 * Parse the string into a map
	 * @param s the source string
	 * @param separator the elements' separator
	 * @return the parsed map
	 */
	public static Map parseMap(String s, String separator) {
		if (s == null || separator == null) {
			return null;
		}
		Map map = new HashMap();
		if (s.length() == 0) {
			return map;
		}
		if (s.charAt(0) == '{') {
			s = s.substring(1);
		}
		if (s.charAt(s.length()-1) == '}') {
			s = s.substring(0, s.length()-1);
		}
		List param = StringUtil.tokenize(s, separator);
		for (int i=0; i<param.size(); i++) {
			String ss = (String)param.get(i);
			List l = StringUtil.tokenize(ss, "=");
			if (l != null && l.size() >= 1) {
				Object key = l.get(0);
				Object value = null;
				if (l.size() == 2) {
					value = l.get(1);
				}
				if (key != null) {
					if (value != null && !value.equals("")) {
						map.put(key, value);
					} else {
						map.put(key, null);
					}
				}
			}
		}
		return map;
	}
	
	/**
	 * Formats a list into string
	 * @param l the list to be formatted
	 * @param separator the separator between the elements
	 * @param enclosing to include the enclosing sq brackets or not
	 * @return the formatted string
	 */
	public static String formatList(List l, String separator, boolean enclosing) {
		if (l == null || separator == null) {
			return null;
		}
		StringBuffer sb = new StringBuffer();
		if (enclosing) {
			sb.append("[");
		}
		for (int i=0; i<l.size(); i++) {
			Object o = l.get(i);
			if (o != null) {
				sb.append(o).append(separator);
			}
		}
		int len = separator.length();
		if (sb.length() > len) {
			int startIndex = sb.length() - len;
			int endIndex = sb.length();
			String s = sb.substring(startIndex, endIndex);
			if (s.equals(separator)) {
				for (int i=0; i<len; i++) {
					sb.deleteCharAt(sb.length()-1);
				}
			}
		}
		if (enclosing) {
			sb.append("]");
		}
		return sb.toString();
	}
	/**
	 * Parse the string into a list. Essentially its the same as tokenize
	 * Except that there is preprocessing of the sq brackets
	 * @param s the source string
	 * @param separator the separator string
	 * @return the parsed list
	 */
	public static List parseList(String s, String separator) {
		if (s == null || separator == null) {
			return null;
		}
		if (s.charAt(0) == '[') {
			s = s.substring(1);
		}
		if (s.charAt(s.length()-1) == ']') {
			s = s.substring(0, s.length()-1);
		}
		return tokenize(s, separator);
	}

	/**
	 * Splits a uri string into the URL and the query parameters
	 * @param uri the URI string
	 * @return an array of string
	 */
	public static String[] splitURIQuery(String uri) {
		if (uri == null) {
			return null;
		}
		int index = uri.indexOf("?");
		String[] s = new String[2];
		if (index > -1) {
			s[0] = uri.substring(0, index);
			s[1] = uri.substring(index+1, uri.length());
		} else {
			s[0] = uri;
			s[1] = "";
		}
		return s;
	}
	
	/**
	 * Tokenize the URI query string into its raw elements. 
	 * The elements are not decoded.
	 * @param query the query string
	 * @return the map containing the attributes
	 */
	public static Map tokenizeURIQuery(String query) {
		Map map = new HashMap();
		if (query == null) {
			return map;
		}
		int index = query.indexOf("?");
		String url = null;
		String param = null;
		if (index > -1) {
			url = query.substring(0, index);
			param = query.substring(index+1, query.length());
		} else {
			url = null;
			param = query;
		}
		List l = StringUtil.tokenize(param, "&");
		for (int i=0; i<l.size(); i++) {
			String s = (String)l.get(i);
			List l2 = StringUtil.tokenize(s, "=");
			if (l2.size() >= 2) {
				Object key = l2.get(0);
				Object value = l2.get(1);
				if (key != null && value != null) {
					map.put(key, value);
				}
			}
		}
		if (log.isInfoEnabled()) { log.info(url); }
		return map;
	}
	
	public static Map mapFromString(String s) {
		Map map = new HashMap();
		if (s != null && s.startsWith("{") && s.endsWith("}")) {
			s = s.substring(1, s.length()-1);
if (log.isDebugEnabled()) { log.debug(s); }
			List l = StringUtil.tokenize(s, ",");
			for (int i=0; i<l.size(); i++) {
				s = (String)l.get(i);
				s = s.trim();
				String[] items = StringUtil.tokenizeIntoStringArray(s, "=");
				if (items != null && items.length == 2) {
					map.put(items[0], items[1]);
				}
			}
		}
		return map;
	}
	
	public static void main(String[] args) {
		/*
		String input = "How are you? John. My name is Peter,Ho";
		String template = "How are you? ${name}. My name is ${self},${surname}";
		Map map = StringUtil.extractParameterValue(input, template);
		System.out.println(map.get("name")); // John
		System.out.println(map.get("self")); // Peter
		System.out.println(map.get("surname")); // Ho
		
		input = "21-Mar-06,41.50,42.01,41.08,41.11,135298896,41.11";
		template = "${timeString},${open},${high},${low},${close},${volume},${adjustedClose}";
		map = StringUtil.extractParameterValue(input, template);
		System.out.println(map);//*/
		/*
		TestBean bean = new TestBean();
		bean.setName("Johnny");
		bean.setMessage("How are you");
		String s = StringUtil.formatObjectToString(bean);
		System.out.println(s);
		
		Object o = StringUtil.parseObjectFromString(s);
		System.out.println(o);//*/
		/*
		try {
			java.net.URI uri = new java.net.URI("http://www.google.com?search=I%20am%20the%20Best&display=20&show=true");
			Map map = StringUtil.tokenizeURIQuery(uri.getRawQuery());
			System.out.println(map);
			map = StringUtil.tokenizeURIQuery(uri.getQuery());
			System.out.println(map);
		} catch (Exception e) {
			
		}//*/
		/*
		Map map = new HashMap();
		map.put("one", "ichi");
		map.put("two", "ni");
		map.put("three", "san");
		map.put("four", null);
		String s = StringUtil.formatMap(map, "|", true);
		System.out.println(s);
		map = StringUtil.parseMap(s, "|");
		System.out.println(map);
		
		List l = new ArrayList();
		l.add("ichi");
		l.add("ni");
		l.add("san");
		l.add("yon");
		s = StringUtil.formatList(l, "| ", true);
		System.out.println(s);
		
		System.out.println(StringUtil.parseList(s, "| "));//*/
		
	}
}

