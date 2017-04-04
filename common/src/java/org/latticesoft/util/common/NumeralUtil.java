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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.*;
//import java.sql.Timestamp;
//import java.text.SimpleDateFormat;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class helps to convert strings to numerals
 * (integers or floating point).
 */
public final class NumeralUtil {

	private static final Log log = LogFactory.getLog(NumeralUtil.class);
	private NumeralUtil(){}

	/** Not a valid number */
	public static final int NOT_A_NUMBER = 0;
	/** An integer type of number (without floating point) */
	public static final int WHOLE_NUMBER = 1;
	/** A floating point number */
	public static final int REAL_NUMBER = 2;
	/** A number represented by string */
	public static final int STRING_NUMBER = 3;

	/**
	 * Checks whether the object is a number or not.
	 * @param o the object to be tested
	 * @return the following constants. NOT_A_NUMBER, WHOLE_NUMBER,
	 * REAL_NUMBER, STRING_NUMBER. See the constant meaning.
	 */
	public static int isNumber(Object o) {
		if (o == null) {
			if (log.isDebugEnabled()) { log.debug("Null."); }
			return NumeralUtil.NOT_A_NUMBER;
		}
		if (o instanceof Byte || o instanceof Short ||
			o instanceof Integer || o instanceof Long ||
			o instanceof BigInteger) {
			return NumeralUtil.WHOLE_NUMBER;
		}
		if (o instanceof BigDecimal ||
			o instanceof Double ||
			o instanceof Float) {
			return NumeralUtil.REAL_NUMBER;
		}
		if (o instanceof String) {
			String s = (String)o;
			char c[] = s.toCharArray();
			boolean res = true;
			if (c.length == 0) { res = false; }
			for (int i=0; i<c.length; i++) {
				boolean temp = (Character.isDigit(c[i]) ||
								c[i] == '.' ||
								(i==0 && c[i]=='-'));
				res &= temp;
			}
			if (res) {
				return NumeralUtil.STRING_NUMBER;
			}
		}
		return NumeralUtil.NOT_A_NUMBER;
	}

	/**
	 * Checks if the object is a valid whole number.
	 * In simple words, this checks that the object is an
	 * instance of either Byte, Short, Integer, Long, BigInteger
	 * @param o the object to be tested
	 * @return true if the object is a valid whole number
	 */
	public static boolean isWholeNumber(Object o) {
		return (NumeralUtil.isNumber(o) == NumeralUtil.WHOLE_NUMBER);
	}

	/**
	 * Checks if the object is a valid read number.
	 * Examples of a valid real number,
	 * @param o the object to be tested
	 * @return true if the object is a valid real number
	 */
	public static boolean isRealNumber(Object o) {
		return (NumeralUtil.isNumber(o) == NumeralUtil.REAL_NUMBER);
	}

	/**
	 * Checks if the object is a valid read number.
	 * @param o the object to be tested
	 * @return true if the object is a valid real number
	 */
	public static boolean isStringNumber(Object o) {
		return (NumeralUtil.isNumber(o) == NumeralUtil.STRING_NUMBER);
	}

	/**
	 * Parse the object into byte.
	 * @param o the object to be parsed
	 * @return byte value for the object or 0 if it is an invalid number
	 */
	public static byte parseByte(Object o) {
		return (byte) parseLong(o);
	}
	/**
	 * Parse the object into short.
	 * @param o the object to be parsed
	 * @return short value for the object or 0 if it is an invalid number
	 */
	public static short parseShort(Object o) {
		return (short) parseLong(o);
	}
	/**
	 * Parse the object into integer.
	 * @param o the object to be parsed
	 * @return int value for the object or 0 if it is an invalid number
	 */
	public static int parseInt(Object o) {
		return (int) parseLong(o);
	}

	/**
	 * Parse the object into long.
	 * @param o the object to be parsed
	 * @return long value for the object or 0 if it is an invalid number
	 */
	public static long parseLong(Object o) {
		if (o == null) {
			if (log.isDebugEnabled()) { log.debug("Null"); }
			return 0;
		}
		long retVal = 0;
		if (o instanceof Byte) {
			retVal = ((Byte)o).longValue();
		}
		if (o instanceof Short) {
			retVal = ((Short)o).longValue();
		}
		if (o instanceof Integer) {
			retVal = ((Integer)o).longValue();
		}
		if (o instanceof Long) {
			retVal = ((Long)o).longValue();
		}
		if (o instanceof BigInteger) {
			retVal = ((BigInteger)o).longValue();
		}
		if (o instanceof String) {
			if (isStringNumber(o)) {
				try {
					retVal = Long.parseLong((String)o);
				} catch (Exception e) {}
			}
		}
		return retVal;
	}

	/**
	 * Parse the object into float.
	 * @param o the object to be parsed
	 * @return float value for the object or 0 if it is an invalid number
	 */
	public static float parseFloat(Object o) {
		if (o == null) {
			if (log.isDebugEnabled()) { log.debug("Null."); }
			return 0.0F;
		}
		if (o instanceof Double) {
			return ((Double)o).floatValue();
		}
		if (o instanceof Float) {
			return ((Float)o).floatValue();
		}
		if (o instanceof BigDecimal) {
			return ((BigDecimal)o).floatValue();
		}
		if (!isStringNumber(o)) {
			return 0.0F;
		}
		float retVal = 0.0F;
		try {
			retVal = Float.parseFloat((String)o);
		} catch (Exception ex) {
			if (log.isDebugEnabled()) { log.debug("Error parsing float"); }
		}
		return retVal;
	}


	/**
	 * Parse the object into double.
	 * @param o the object to be parsed
	 * @return double value for the object or 0 if it is an invalid number
	 */
	public static double parseDouble(Object o) {
		if (o == null) {
			if (log.isDebugEnabled()) { log.debug("Null."); }
			return 0.0;
		}
		if (o instanceof Double) {
			return ((Double)o).doubleValue();
		}
		if (o instanceof Float) {
			return ((Float)o).doubleValue();
		}
		if (o instanceof BigDecimal) {
			return ((BigDecimal)o).doubleValue();
		}
		if (!isStringNumber(o)) {
			return 0.0;
		}
		double retVal = 0.0;
		try {
			retVal = Double.parseDouble((String)o);
		} catch (Exception ex) {
			if (log.isDebugEnabled()) { log.debug("Error parsing double"); }
		}
		return retVal;
	}

	/**
	 * Parse the object into BigDecimal.
	 * @param o the object to be parsed
	 * @return BigDecimal value for the object or null if it is an invalid number
	 */
	public static BigDecimal parseBigDecimal(Object o) {
		if (o == null) {
			if (log.isDebugEnabled()) { log.debug("Null."); }
			return null;
		}
		BigDecimal retVal = null;
		if (o instanceof Double) {
			retVal = new BigDecimal(((Double)o).doubleValue());
		}
		if (o instanceof Float) {
			retVal = new BigDecimal(((Float)o).doubleValue());
		}
		if (o instanceof BigDecimal) {
			retVal = (BigDecimal)o;
		}
		if (o instanceof String) {
			retVal = new BigDecimal((String)o);
		}
		return retVal;
	}

	/**
	 * Compares 2 object to check for equality between the 2 classes.
	 * @param o1 the object to be tested
	 * @param o2 the object to be tested
	 * @return true if the 2 objects are equal
	 */
	public static boolean equals(Object o1, Object o2) {
		boolean retVal = false;
		if (o1 == null && o2 == null) {
			return true;
		}
		if (o1.getClass().equals(o2.getClass())) {
			if (log.isDebugEnabled()) { log.debug("Same class: direct compare."); }
			retVal = o1.equals(o2);
		} else {
			if (log.isDebugEnabled()) {
				log.debug("Diff class: convert to string.");
				log.debug(o1.toString() + ":" + o2.toString());
			}
			retVal = o1.toString().equals(o2.toString());
		}
		return retVal;
	}

	/**
	 * Compares 2 object to check for equality between the 2 classes,
	 * ignoring case difference.
	 * @param o1 the object to be tested
	 * @param o2 the object to be tested
	 * @return true if the 2 objects are equal
	 */
	public static boolean equalsIgnoreCase(Object o1, Object o2) {
		boolean retVal = false;
		if (o1 == null && o2 == null) {
			return true;
		}
		if (o1.getClass().equals(o2.getClass())) {
			if (log.isDebugEnabled()) { log.debug("Same class: direct compare."); }
			retVal = o1.equals(o2);
		} else {
			if (log.isDebugEnabled()) {
				log.debug("Diff class: convert to string.");
				log.debug(o1.toString() + ":" + o2.toString());
			}
			retVal = o1.toString().equalsIgnoreCase(o2.toString());
		}
		return retVal;
	}

	/**
	 * Compares 2 object to check whether object 1 is greater than object 2.
	 * @param o1 the object to be tested
	 * @param o2 the object to be tested
	 * @return true if the 2 objects are equal
	 */
	public static boolean isGreaterThan(Object o1, Object o2) {
		if (o1 == null || o2 == null) {
			return false;
		}
		// compare using string
		char c1[] = o1.toString().toCharArray();
		char c2[] = o2.toString().toCharArray();
		return (compareCharArray(c1, c2) > 0);
	}

	/**
	 * Compares 2 object to check whether object 1 is greater than object 2,
	 * ignoring case difference
	 * @param o1 the object to be tested
	 * @param o2 the object to be tested
	 * @return true if the 2 objects are equal
	 */
	public static boolean isGreaterThanIgnoreCase(Object o1, Object o2) {
		if (o1 == null || o2 == null) {
			return false;
		}
		// compare using string
		char c1[] = o1.toString().toUpperCase().toCharArray();
		char c2[] = o2.toString().toUpperCase().toCharArray();
		return (compareCharArray(c1, c2) > 0);
	}

	/**
	 * Compares 2 object to check whether object 1 is less than object 2.
	 * @param o1 the object to be tested
	 * @param o2 the object to be tested
	 * @return true if the 2 objects are equal
	 */
	public static boolean lessThan(Object o1, Object o2) {
		if (o1 == null || o2 == null) {
			return false;
		}
		// compare using string
		char c1[] = o1.toString().toCharArray();
		char c2[] = o2.toString().toCharArray();
		return (compareCharArray(c1, c2) < 0);
	}

	/**
	 * Compares 2 object to check whether object 1 is less than object 2,
	 * ignoring case difference
	 * @param o1 the object to be tested
	 * @param o2 the object to be tested
	 * @return true if the 2 objects are equal
	 */
	public static boolean isLessThanIgnoreCase(Object o1, Object o2) {
		if (o1 == null || o2 == null) {
			return false;
		}
		// compare using string
		char c1[] = o1.toString().toUpperCase().toCharArray();
		char c2[] = o2.toString().toUpperCase().toCharArray();
		return (compareCharArray(c1, c2) < 0);
	}

	/**
	 * Compares 2 char array for equality, greater than or less than.
	 * @param c1 char array 1
	 * @param c2 char array 2
	 * @return 1 if char array 1 is greater than char array 2;
	 * 0 if they are the same; and -1 if char array 1 is less
	 * than char array 2
	 */
	public static int compareCharArray(char c1[], char c2[]) {
		int count = (c1.length < c2.length) ? c1.length : c2.length;
		for (int i=0; i<count; i++) {
			int i1 = Character.getNumericValue(c1[i]);
			int i2 = Character.getNumericValue(c2[i]);
			if (i1 > i2) {
				return 1;
			} else if (i1 < i2) {
				return -1;
			} // else is equal
		}
		// at this stage, either equal
		// now check length
		if (c1.length > c2.length) {
			return 1;
		} else if (c1.length < c2.length) {
			return -1;
		}
		return 0;
	}

	/**
	 * Compares 2 float value based on the given number of decimal
	 * place to compare.
	 * @param f1 float variable 1
	 * @param f2 float variable 2
	 * @param decimalPlace the number of dp to compare
	 * @return 0 if they are the same, 1 if f1
	 */
	public static int compareFloat(float f1, float f2, int decimalPlace) {
		String[][] s = new String[2][];
		s[0] = StringUtil.tokenizeIntoStringArray(Float.toString(f1), ".");
		s[1] = StringUtil.tokenizeIntoStringArray(Float.toString(f2), ".");
		if (s[0] == null || s[1] == null) return 0;
		int[] x = new int[2];
		x[0] = NumeralUtil.parseInt(s[0][0]);
		x[1] = NumeralUtil.parseInt(s[1][0]);
		if (x[0] > x[1]) return 1;
		if (x[0] < x[1]) return -1;

		// they are equal in whole number
		// now compare decimal point up the the stated decimal places
		StringBuffer sb = new StringBuffer();
		for (int i=0; i<2; i++) {
			sb.setLength(0);
			int nextPoint = 0;
			if (s[0][1].length() < decimalPlace) {
				sb.append(s[i][1]);
				for (int j=s[i][1].length(); j<decimalPlace; j++) {
					sb.append("0");
				}
				nextPoint = 0;
			} else {
				sb.append(s[i][1].substring(0, decimalPlace));
				if (s[i][1].length() >= decimalPlace+1) {
					nextPoint = NumeralUtil.parseInt(s[i][1].substring(decimalPlace, decimalPlace+1));
				} else {
					nextPoint = 0;
				}
			}
			x[i] = NumeralUtil.parseInt(sb.toString());
			if (nextPoint >= 5) x[i]++;
		}

		if (x[0] > x[1]) return 1;
		else if (x[0] < x[1]) return -1;
		else return 0;
	}

	/**
	 * Converts the number into the specified format
	 * @param number the number to be formatted
	 * @param format the format of the integer
	 * @return the number in the specified format
	 */
	public static String formatNumber(String format, long number) {
		if (format == null) return "" + number;
		DecimalFormat df = new DecimalFormat(format);
		String retVal = null;
		try {
			retVal = df.format(number);
		} catch (Exception e) {
			retVal = "" + number;
		}
		return retVal;
	}
	/**
	 * @see NumeralUtil#formatNumber(long, String)
	 */
	public static String formatNumber(String format, int number) {
		return formatNumber(format, (long)number);
	}
	/**
	 * @see NumeralUtil#formatNumber(long, String)
	 */
	public static String formatNumber(String format, short number) {
		return formatNumber(format, (long)number);
	}
	/**
	 * @see NumeralUtil#formatNumber(long, String)
	 */
	public static String formatNumber(String format, byte number) {
		return formatNumber(format, (long)number);
	}
	/**
	 * Converts the number into the specified format
	 * @param number the number to be formatted
	 * @param format the format of the integer
	 * @return the number in the specified format
	 */
	public static String formatNumber(String format, double number) {
		if (format == null) return "" + number;
		DecimalFormat df = new DecimalFormat(format);
		String retVal = null;
		try {
			retVal = df.format(number);
		} catch (Exception e) {
			retVal = "" + number;
		}
		return retVal;
	}
	/**
	 * @see ConvertUtil#formatNumber(double, String)
	 */
	public static String formatNumber(String format, float number) {
		return formatNumber(format, (double)number);
	}
	/**
	 * Converts the number into the specified format
	 * @param number the number to be formatted
	 * @param format the format of the integer
	 * @return the number in the specified format
	 */
	public static String formatNumber(String format, BigInteger number) {
		if (format == null) return "" + number;
		DecimalFormat df = new DecimalFormat(format);
		String retVal = null;
		try {
			retVal = df.format(number);
		} catch (Exception e) {
			retVal = "" + number;
		}
		return retVal;
	}
	/**
	 * @see ConvertUtil#formatNumber(double, String)
	 */
	public static String formatNumber(String format, BigDecimal number) {
		if (format == null) return "" + number;
		DecimalFormat df = new DecimalFormat(format);
		String retVal = null;
		try {
			retVal = df.format(number);
		} catch (Exception e) {
			retVal = "" + number;
		}
		return retVal;
	}

	/**
	 * Generates next random Int
	 * @param int max int
	 * @return the generated int
	 */
	public static int getRandomInt(int max) {
		Random rand = new Random(System.nanoTime()); // for jdk 6 only
		//Random rand = new Random(System.currentTimeMillis());
		if (max > 0) {
			return rand.nextInt(max);
		}
		return rand.nextInt();
	}
	/**
	 * Generates next random Int
lo	 * @return the generated int
	 */
	public static int getRandomInt() {
		return getRandomInt(-1);
	}
	/**
	 * Generates next random long
	 * @return the generated long
	 */
	public static long getRandomLong() {
		Random rand = new Random(System.currentTimeMillis());
		return rand.nextLong();
	}
	/**
	 * Generates next random long
	 * @return the generated long
	 */
	public static long getRandomLong(long start, long end) {
		long l = 0;
		if (end > start) {
			long diff = end - start;
			int rand = getRandomInt((int)diff);
			l = start + rand;
		} else {
			l = getRandomLong();
		}
		return l;
	}

	/**
	 * Generates next random boolean
	 * @return the generated boolean
	 */
	public static boolean getRandomBoolean() {
		Random rand = new Random(System.currentTimeMillis());
		return rand.nextBoolean();
	}
	/**
	 * Generates next random float
	 * @return the generated float
	 */
	public static float getRandomFloat() {
		Random rand = new Random(System.currentTimeMillis());
		return rand.nextFloat();
	}
	/**
	 * Generates next random double
	 * @return the generated double
	 */
	public static double getRandomDouble() {
		return getRandomDouble(0, 0);
	}

	/**
	 * Generates next random double
	 * @param start
	 * @return the generated double
	 */
	public static double getRandomDouble(double start, double end) {
		Random rand = new Random(System.currentTimeMillis());
		double d = rand.nextDouble();
		if (end > start) {
			d = d * (end - start) + start;
		}
		return d; 
	}
	
	/**
	 * Check whether the object o is between the two other object
	 */
	public static boolean isBetween(Object start, Object end, Object o) {
		boolean retVal = false;
		if (NumeralUtil.isGreaterThan(start, o) && NumeralUtil.isGreaterThan(o, end)) {
			retVal = true;
		}
		return retVal;
	}
	
	/**
	 * Convert a byte into Hex string
	 * @param b the byte to be converted
	 */
	public static String toHexString(byte b) {
		String s = Integer.toHexString(b);
		s = s.toUpperCase();
		StringBuffer sb = new StringBuffer();
		int len = s.length();
		if (len == 1) {
			sb.append("0").append(s);
		} else if (len == 2) {
			sb.append(s);
		} else if (len > 2 && s.startsWith("F")) {
			sb.append(s.substring(len - 2, len));
		} else {
			sb.append("NA");
		}
		return sb.toString();
	}
	
	/**
	 * Convert a byte array into hex string 
	 */
	public static String toHexString(byte[] b) {
		if (b == null || b.length == 0) {
			return "";
		}
		StringBuffer sb = new StringBuffer();
		for (int i=0; i<b.length; i++) {
			sb.append(toHexString(b[i]));
		}
		return sb.toString();
	}
	
	public static byte convertHexStringToByte(String s) {
		byte b = (byte)0;
		if (s == null || s.length() == 0) {
			return b;
		}
		if (s.length() != 2) {
			return b;
		}
		if (s.startsWith("F")) {
			
		}
		int i = Integer.parseInt(s, 16);
		b = (byte)i;
		return b;
	}
	
	public static byte[] convertHexStringToByteArray(String s) {
		byte[] retVal = null;
		if (s == null || s.length() == 0) {
			return null;
		}
		if (s.length() % 2 != 0) {
			return null;
		}
		s = s.toUpperCase();
		int index = 0;
		//int len = s.length();
		
		List l = new ArrayList();
		StringBuffer sb = new StringBuffer();
		while (index < s.length()) {
			sb.setLength(0);
			sb.append(s.charAt(index++));
			sb.append(s.charAt(index++));
			byte b = convertHexStringToByte(sb.toString());
			l.add(new Byte(b));
		}
		retVal = new byte[l.size()];
		for (int i=0; i<l.size(); i++) {
			Byte bb = (Byte)l.get(i);
			retVal[i] = bb.byteValue();
		}
		return retVal;
	}
	
	public static void resetByteArray(byte[] b) {
		if (b == null) {
			return;
		}
		for (int i=0; i<b.length; i++) {
			b[i] = 0;
		}
	}
	
	public static void main (String args[]){
		/*
		Object o[][] = {{null, null},
						{new Integer(34), new Integer(33)},
						{new Double(32.2), new Double(32.20004)},
						{new Double(32.200000000000001), new Double(32.2)},
						{new Double(32.2000000000000001), new Double(32.2)},
						{"32.200000000000001", "32.2"},
						{"ABCdef", "ABCdef"}};

		for (int i=0; i<o.length; i++) {
			System.out.println (o[i][0] + ":" + o[i][1]);
			System.out.println ("== " + NumeralUtil.equals(o[i][0], o[i][1]));
			System.out.println ("== " + NumeralUtil.equalsIgnoreCase(o[i][0], o[i][1]));
			System.out.println (">  " + NumeralUtil.isGreaterThan(o[i][0], o[i][1]));
			System.out.println (">  " + NumeralUtil.isGreaterThanIgnoreCase(o[i][0], o[i][1]));
			System.out.println ("<  " + NumeralUtil.lessThan(o[i][0], o[i][1]));
			System.out.println ("<  " + NumeralUtil.isLessThanIgnoreCase(o[i][0], o[i][1]));
			System.out.println ("-----------");
		}
		
		BigDecimal bd1 = new BigDecimal("9.000009"); 
		BigDecimal bd2 = new BigDecimal("9.000001");
		BigDecimal bd3 = new BigDecimal("9.000003");
		BigDecimal bd4 = new BigDecimal("9.000010");
		
		System.out.println(bd1 + " " + bd2 + " " + bd3 + " " + NumeralUtil.isBetween(bd1, bd2, bd3));
		System.out.println(bd1 + " " + bd2 + " " + bd4 + " " + NumeralUtil.isBetween(bd1, bd2, bd4));
		
		
		
		java.sql.Timestamp ts1 = java.sql.Timestamp.valueOf("2007-01-01 00:00:00.000");
		java.sql.Timestamp ts2 = java.sql.Timestamp.valueOf("2007-01-02 00:00:00.000");
		java.sql.Timestamp ts3 = java.sql.Timestamp.valueOf("2007-01-01 01:00:00.000");
		
		System.out.println(ts1 + " " + ts2 + " " + ts3 + " " + NumeralUtil.isBetween(ts1, ts2, ts3));
		System.out.println(NumeralUtil.getRandomDouble(1.2, 1.3));
		System.out.println(NumeralUtil.getRandomLong(3999999993L, 3999999999L));
		//*/
		/*
		byte[] b = new byte[20];
		for (int i=0; i<b.length; i++) {
			int ii = NumeralUtil.getRandomInt(99999);
			ii %= 255;
			ThreadUtil.sleep(15);
			System.out.print(Integer.toHexString(ii));
			System.out.print(" ");
			b[i] = (byte)(ii);
		}
		System.out.println("");
		System.out.println(NumeralUtil.toHexString(b));
		//*/
	}//*/
}
