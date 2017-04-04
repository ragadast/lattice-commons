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

import org.apache.commons.beanutils.*;
import java.lang.reflect.*;
import java.util.*;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParseException;

/**
 * <p>This utility class helps to load class definitation from
 * class file directly or bytes read from the class file or
 * string representation of the bytes.</p>
 * <p> For testing purpose we need to create a simple javabean:
 * <code>TestBean</code></p>
 * <pre>
 * public class TestBean {
 *     private String name;
 *     public String getName() { return this.name; }
 *     public void setName(String name) { this.name = name; }
 * }
 * </pre>
 * <p>Sample usage: It requires the commons-beanutils library
 * </p>
 * <pre>
 * import org.apache.commons.beanutils.WrapDynaBean;
 * ...
 * Object o = ClassUtil.newInstanceFromFile("TestBean.class");
 * WrapDynaBean bean = new WrapDynaBean(o);
 * bean.set("name", "HelloWorld");
 * System.out.println (o); // HelloWorld is printed
 * </pre>
 *
 * <p>Note that this class is useful for dynamic loading of classes.
 * However the only restriction is that all the methods invoked
 * on the created class must be via reflection. Another side effect
 * is that instanceof keyword will not work for classes loaded
 * by the utility class.</p>
 *
 * <p>For example using the above class the following statement
 * will return false:</p>
 * <pre>
 * Object o = ClassUtil.newInstanceFromFile("TestBean.class");
 * if (o instanceof TestBean) {
 *     System.out.println("I am a TestBean");
 * } else {
 *     System.out.println("I am not a TestBean! :(");
 * }
 * </pre>
 */
public final class ClassUtil extends ClassLoader {

	private ClassUtil() {}

	/** Private instance of classloader - singleton*/
	private static final ClassUtil cu = new ClassUtil();

	/** Invokes the inherited defineClass method */
	private Class getClass(String name, byte[] b) {
		return this.defineClass(name, b, 0, b.length);
	}

	/**
	 * Defines the class from a filename. Note that the file must
	 * be a binary class file.
	 * @param filename the name/location of the class file
	 * @return the class defined. null if it is not valid
	 */
	public static Class defineClassFromFile(String filename) {
		String data = StringUtil.getHexDataFromFile(filename);
		if (data != null) {
			return ClassUtil.defineClassFromString(data);
		}
		return null;
	}

	/**
	 * Defines the class from a filename. Note that the file must
	 * be a binary class file.
	 * @param filename the name/location of the class file
	 * @param classname the name of the class
	 * @return the class defined. null if it is not valid
	 */
	public static Class defineClassFromFile(String classname, String filename) {
		String data = StringUtil.getHexDataFromFile(filename);
		if (data != null) {
			return ClassUtil.defineClassFromString(classname, data);
		}
		return null;
	}

	/**
	 * Defines the class from a string data. The data is the
	 * hexadecimal representation of the binary class file.
	 * Each byte string is represent by 2 string characters
	 * E.g. byte F or 16 is represented by 0F
	 * @param data the string data
	 * @return the class defined. null if it is not valid
	 */
	public static Class defineClassFromString(String data) {
		return ClassUtil.defineClassFromString(null, data);
	}

	/**
	 * Defines the class from a string data. The data is the
	 * hexadecimal representation of the binary class file.
	 * Each byte string is represent by 2 string characters
	 * E.g. byte F or 16 is represented by 0F
	 * @param data the string data
	 * @param classname the name of the class
	 * @return the class defined. null if it is not valid
	 */
	public static Class defineClassFromString(String classname, String data) {
		byte[] b = ConvertUtil.convertStringToByteArray(data);
		return cu.getClass(classname, b);
	}

	/**
	 * Defines the class from a byte data.
	 * @param data binary class file's byte data
	 * @return the class defined. null if it is not valid
	 */
	public static Class defineClassFromBytes(byte[] data) {
		return cu.getClass(null, data);
	}

	/**
	 * Defines the class from a byte data.
	 * @param data binary class file's byte data
	 * @param classname the name of the class
	 * @return the class defined. null if it is not valid
	 */
	public static Class defineClassFromBytes(String classname, byte[] data) {
		return cu.getClass(classname, data);
	}

	/**
	 * Instantiate an object from a class
	 * @param c the Class to instantiate from
	 * @return null if encounter exception
	 */
	public static Object newInstance(Class c) {
		Object o = null;
		if (c == null) return null;
		try {
			o = c.newInstance();
		} catch (Exception e) {
			o = null;
		}
		return o;
	}

	/**
	 * Creates a new instance of an object.
	 * @param classname the name of the class
	 * @return the instantiated object
	 */
	public static Object newInstance(String classname) {
		Class c = null;
		try {
			c = Class.forName(classname);
		} catch (Exception e) {}
		return ClassUtil.newInstance(c);
	}

	/**
	 * Creates a new instance of an object.
	 * @param classname the name of the class
	 * @param init true if initialization is required
	 * @param cl the class loader to be used
	 * @return the instantiated object
	 */
	public static Object newInstance(String classname, boolean init, ClassLoader cl) {
		Class c = null;
		try {
			c = Class.forName(classname, init, cl);
		} catch (Exception e) {}
		return ClassUtil.newInstance(c);
	}

	/**
	 * Creates a new instance of an object from the file. The file
	 * must be a binary class file
	 * @param filename the name/location of the binary class file
	 * @return the instantiated object
	 */
	public static Object newInstanceFromFile(String filename) {
		Class c = ClassUtil.defineClassFromFile(filename);
		return ClassUtil.newInstance(c);
	}

	/**
	 * Creates a new instance of an object from the file. The file
	 * must be a binary class file
	 * @param filename the name/location of the binary class file
	 * @param classname the name of the class
	 * @return the instantiated object
	 */
	public static Object newInstanceFromFile(String classname, String filename) {
		Class c = ClassUtil.defineClassFromFile(classname, filename);
		return ClassUtil.newInstance(c);
	}

	/**
	 * Creates a new instance of an object from the string data.
	 * The data must be the hexadecimal representation of the bytes
	 * of a class file
	 * @param data the string data
	 * @return the instantiated object
	 */
	public static Object newInstanceFromString(String data) {
		Class c = ClassUtil.defineClassFromString(data);
		return ClassUtil.newInstance(c);
	}

	/**
	 * Creates a new instance of an object from the string data.
	 * The data must be the hexadecimal representation of the bytes
	 * of a class file
	 * @param data the string data
	 * @param classname the name of the class
	 * @return the instantiated object
	 */
	public static Object newInstanceFromString(String classname, String data) {
		Class c = ClassUtil.defineClassFromString(classname, data);
		return ClassUtil.newInstance(c);
	}

	/**
	 * Creates a new instance of an object from the bytes.
	 * @param data the bytes (byte array) of a binary class file
	 * @return the instantiated object
	 */
	public static Object newInstanceFromBytes(byte[] data) {
		Class c = ClassUtil.defineClassFromBytes(data);
		return ClassUtil.newInstance(c);
	}

	/**
	 * Creates a new instance of an object from the bytes.
	 * @param data the bytes of a binary class file
	 * @param classname the name of the class
	 * @return the instantiated object
	 */
	public static Object newInstanceFromBytes(String classname, byte[] data) {
		Class c = ClassUtil.defineClassFromBytes(classname, data);
		return ClassUtil.newInstance(c);
	}
	
	/**
	 * Creates a dynabean class definition from an xml file
	 */
	public static DynaClass parseXml(String filename) {
		DynaClass clazz = null;
		
		return clazz;
	}
	
	public static Object newInstance(String type, String value) {
		return newInstance(type, value, null);
	}
	
	/**
	 * Instantiate a new object from a string value
	 * 
	 */
	public static Object newInstance(String type, String value, String format) {
		Object o = null;
		Class clazz = null;
		if (type == null || value == null) {
			return null;
		}
		if (type.equals("java.lang.String")) {
			return value;
		}
		try {
			clazz = Class.forName(type);
			if ("java.lang.Character".equals(type)) {
				o = new Character(value.charAt(0));
			} else if ("java.util.Date".equals(type) || 
					"java.sql.Time".equals(type) ||
					"java.sql.Timestamp".equals(type)) {
				if (format == null) {
					format = "yyyy-MM-dd HH:mm:ss.SSS";
				}
				DateFormat fmt = new SimpleDateFormat(format);
				try { o = fmt.parse(value); } catch (ParseException pe) {}
				if (o != null && "java.sql.Time".equals(type)) {
					Date d = (Date)o;
					o = new Time(d.getTime());
				}
				if (o != null && "java.sql.Timestamp".equals(type)) {
					Date d = (Date)o;
					o = new Timestamp(d.getTime());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (o != null) return o;
		try {
			Method[] m = clazz.getMethods();
			for (int i=0; i<m.length; i++) {
				if ("fromString".equalsIgnoreCase(m[i].getName())) {
					Object[] args = {value};
					o = m[i].invoke(clazz, args);
					break;
				} else if ("parse".equalsIgnoreCase(m[i].getName())) {
					Object[] args = {value};
					o = m[i].invoke(clazz, args);
					break;
				} else if ("newInstance".equalsIgnoreCase(m[i].getName())) {
					Object[] args = {value};
					o = m[i].invoke(clazz, args);
					break;
				} else if ("valueOf".equalsIgnoreCase(m[i].getName())) {
					Object[] args = {value};
					o = m[i].invoke(clazz, args);
					break;
				}
			}
		} catch (Exception e) {
		}

		if (o != null) { return o; }
		try {
			Constructor[] c = clazz.getConstructors();
			for (int i=0; i<c.length; i++) {
				Class[] param = c[i].getParameterTypes();
				if (param != null && param.length == 1 && 
					param[0].getName().equals("java.lang.String")) {
					Object[] args = {value};
					o = c[i].newInstance(args);
				}
			}
		} catch (Exception e){
		}

		if (o == null) {
			o = value;
		}
		return o;
	}
	
	/**
	 * Returns the names of the settable attributes or gettable attributes
	 * @param c the class to be analysse
	 * @param isSettable set or get attributes
	 * @return a string array of attributes
	 */
	public static Collection getAttributeNames(Class c, boolean isSettable) {
		ArrayList a = new ArrayList();
		if (c == null) return a;
		try {
			Method[] m = c.getMethods();
			StringBuffer sb = new StringBuffer();
			for (int i=0; i<m.length; i++) {
				String name = m[i].getName();
				if (isSettable) {
					if (name.startsWith("set")) {
						String attribute = name.substring(4, name.length());
						char ch = Character.toLowerCase(name.charAt(3));
						sb.setLength(0);
						sb.append(ch);
						sb.append(attribute);
						a.add(sb.toString());
					}
				} else {
					if (name.startsWith("get")) {
						String attribute = name.substring(4, name.length());
						char ch = Character.toLowerCase(name.charAt(3));
						sb.setLength(0);
						sb.append(ch);
						sb.append(attribute);
						a.add(sb.toString());
					}
					if (name.startsWith("is")) {
						String attribute = name.substring(3, name.length());
						char ch = Character.toLowerCase(name.charAt(2));
						sb.setLength(0);
						sb.append(ch);
						sb.append(attribute);
						a.add(sb.toString());
					}
				}
			}
		} catch (Exception e) {
		}
		return a;
	}
	
	/**
	 * Returns the names of the settable attributes or gettable attributes
	 * @param c the class to be analysse
	 * @param isSettable set or get attributes
	 * @return a string array of attributes
	 */
	public static String[] getAttributeNamesAsArray(Class c, boolean isSettable) {
		ArrayList a = (ArrayList)getAttributeNames(c, isSettable);
		String[] s = new String[a.size()];
		s = (String[])a.toArray(s);
		return s;
	}
	
	
	/** Returns the integer value from the attribute name */
	public static int getAttributeIntFromName(Class clazz, String name) {
		if (name == null) return 0;
		int retVal = 0;
		try {
			retVal = clazz.getField(name.toUpperCase()).getInt(clazz);
		} catch (Exception e) {
		
		}
		return retVal;
	}

	public static void main(String args[]){
		/*
		Object o = ClassUtil.newInstanceFromFile("C:/MyProjects/deploy/JRPA.ear/lib/lattice.jar/TestBean.class");
		org.apache.commons.beanutils.WrapDynaBean bean = new org.apache.commons.beanutils.WrapDynaBean(o);
		bean.set("name", "HelloWorld");
		System.out.println (o);
		//*/
		
		/*
		Object o = ClassUtil.newInstance("java.sql.Timestamp", "2005-11-30 20:35:24.000");
		System.out.println(o.getClass() + " " + o);
		
		o = ClassUtil.newInstance("java.lang.Integer", "9899");
		System.out.println(o.getClass() + " " + o);
		
		o = ClassUtil.newInstance("java.util.Date", "Sat Aug 15 13:30:00 SGT 2006");
		//o = ClassUtil.newInstance("java.util.Date", "Aug/12/05 2:30 P.M.");
		System.out.println(o.getClass() + " " + o);
		
		System.out.println(new Date());
	
		o = ClassUtil.newInstance("java.math.BigDecimal", "0.12345678901234567890");
		System.out.println(o.getClass() + " " + o);
		//*/
	}
}
