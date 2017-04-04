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

import java.util.*;
import java.sql.Timestamp;
import java.math.BigInteger;
import java.math.BigDecimal;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.beanutils.BeanUtils;


/**
 * ConvertUtil provided the conversion method from the common from one form to another.
 * Most of the time the method are simply wrapper to method provided by other utils 
 */
public final class ConvertUtil {

	public static final int IEEE = 0;
	public static final int MICROSOFT = 1;
	private static final Log log = LogFactory.getLog(ConvertUtil.class);

	private ConvertUtil(){}

	
	/** @see DateUtil#parseUtilDate(int, int, int) */
	public static java.util.Date parseUtilDate(String format, String s) { return DateUtil.parseUtilDate(format, s); }
	/** @see DateUtil#parseTimestamp(String, String) */
	public static Timestamp parseTimestamp(String format, String s) { return DateUtil.parseTimestamp(format, s); }
	/** @see DateUtil#parseSQLDate(String, String) */
	public static java.sql.Date parseSQLDate(String format, String s) { return DateUtil.parseSQLDate(format, s); }
	/** @see DateUtil#getCurrentUtilDate()() */
	public static java.util.Date getCurrentDate() { return DateUtil.getCurrentUtilDate(); }
	/** @see DateUtil#getCurrentSQLDate() */
	public static java.sql.Date getCurrentSQLDate() { return DateUtil.getCurrentSQLDate(); }
	/** Get the current sql timestamp */
	public static Timestamp getCurrentTimestamp() { return DateUtil.getCurrentTimestamp(); }
	/** Formats the date */
	public static String formatDate(String format, java.util.Date date) { return DateUtil.formatDate(format, date); }
	
	
	/** @see NumeralUtil#formatNumber(String, long) */
	public static String formatNumber(String fmt, long num) { return NumeralUtil.formatNumber(fmt, num); }
	/** @see NumeralUtil#formatNumber(String, int) */
	public static String formatNumber(String fmt, int num) { return NumeralUtil.formatNumber(fmt, num); }
	/** @see NumeralUtil#formatNumber(String, short) */
	public static String formatNumber(String fmt, short num) { return NumeralUtil.formatNumber(fmt, num); }
	/** @see NumeralUtil#formatNumber(String, byte) */
	public static String formatNumber(String fmt, byte num) { return NumeralUtil.formatNumber(fmt, num); }
	/** @see NumeralUtil#formatNumber(String, double) */
	public static String formatNumber(String fmt, double num) { return NumeralUtil.formatNumber(fmt, num); }
	/** @see NumeralUtil#formatNumber(String, flat) */
	public static String formatNumber(String fmt, float num) { 	return NumeralUtil.formatNumber(fmt, num); }
	/** @see NumeralUtil#formatNumber(String, BigInteger) */
	public static String formatNumber(String fmt, BigInteger num) { return NumeralUtil.formatNumber(fmt, num); }
	/** @see NumeralUtil#formatNumber(String, BigDecimal) */
	public static String formatNumber(String fmt, BigDecimal num) { return NumeralUtil.formatNumber(fmt, num); }

	
	
	
	
	
	/**
	 * Formats the string to fit specified length with the pad character
	 * @param source the source string
	 * @param length the size of the padded string
	 * @param padChar the character used for padding
	 * @param isLeftPad if the padding is done at left side (true) or right side (false)
	 * @param trimExcess if true the excess padded would be trimmed to fit exactly the length
	 */
	public static String formatString(String source, int length, String pad, boolean isLeftPad, boolean trimExcess) {
		StringBuffer sb = new StringBuffer();
		if (source == null) {
			source = "";
		}
		sb.append(source);
		int diff = length - sb.length();
		if (diff <= 0) {
			return sb.toString();
		}
		
		StringBuffer sb2 = new StringBuffer();
		for (int i=0; i<diff; i+=pad.length()) {
			sb2.append(pad);
		}
		// trim excess padded characters
		// applicable if pad string is more than 1 character long
		diff = sb2.length() + sb.length() - length;
		if (diff > 0) {
			for (int i=0; i<diff; i++) {
				sb2.deleteCharAt(sb2.length() - 1);
			}
		}
		// padd left or right
		if (isLeftPad) {
			sb2.append(sb.toString());
			sb = sb2;
		} else {
			sb.append(sb2.toString());
		}
		return sb.toString();
	}

	/**
	 * Formats the string to fit specified length with the pad character
	 * @param source the source string
	 * @param size the size of the padded string
	 * @param padChar the character used for padding
	 * @param isLeftPad if the padding is done at left side (true) or right side (false)
	 */
	public static String formatString(String source, int size, char padChar, boolean isLeftPad) {
		return formatString(source, size, "" + padChar, isLeftPad, false);
	}
	
	
	
	
	
	

	
	/**
	 * Return a date format as an integer (for those formattable as integer)
	 * @param date the date to be converted
	 * @param dateFormat the format of the date
	 * @return the date part (as an int)
	 */
	public static int convertDateToInt(String dateFormat, java.util.Date date) {
		String s = DateUtil.formatDate(dateFormat, date);
		int retVal = 0;
		try {
			retVal = Integer.parseInt(s);			
		} catch (Exception e) {
		}
		return retVal;
	}
	
	/**
	 * Reads a date in Metastock format.
	 * @return a <code>java.util.Date</code> object formated according to the value
	 * of the given float argument interpreted as in 20030815.0f for the
	 * 15-Aug-2003 (Not that this date has any significance: it's just
	 * today's date!;-)
	 * <p>Also note that this stupid format was not even Y2K compliant, hence
	 * the addition of 1900 to the year to compensate ... and create a Y2.1K problem;-(
	 */
	public static java.util.Date convertFloatToDate(float f) {
		int	date = (int) f;
		int	day = date % 100;
		date /= 100;
		int	month = date % 100;
		date /= 100;
		int	year = 1900 + date;

		StringBuffer sb = new StringBuffer();
		sb.append(year);
		if (month < 10) { sb.append("0"); }
		sb.append(month);
		if (day < 10) { sb.append("0"); }
		sb.append(day);
		java.util.Date result = null;
		try {
			result = DateUtil.FMT_FLOAT_TO_DATE.parse(sb.toString());
		} catch (Exception e) {}
		return result;
	}

	/**
	 * Converts a date into the float number.
	 * For example 4 Mar 2002 the result float will be 1020304.0
	 * The format is as follows: (1yyMMdd)
	 * NB: there is always a 1 in front of the date
	 */
	public static float convertDateToFloat(java.util.Date date) {
		StringBuffer sb = new StringBuffer();
		int retVal = 0;
		try {
			String s = DateUtil.FMT_DATE_TO_FLOAT.format(date);
			if (s != null) {
				sb.append("1");
				sb.append(s);
			}
			retVal = Integer.parseInt(sb.toString());
		} catch (Exception e) {
		}
		return (float)retVal;
	}

	/**
	 * Converts a Microsoft Binary Float integer bits into a IEEE equivalent
	 * Assume all the bits are arrange in a the big endian format
	 */
	public static int convertIntBitsMSBinaryToIEEE(int source) {
		int mantissa = source & 0x007FFFFF;
		int sign = (source & 0x00800000) >> 23;
		int exponent = (source & 0xFF000000) >>> 24;
		exponent = exponent - 0x81 + 0x7F; // get new bias exponent
		sign = sign << 31;
		exponent = exponent << 23;
		int retVal = mantissa | sign | exponent;
		return retVal;
	}

	/**
	 * Converts an IEEE float integer bits into a Microsoft Binary equivalent
	 */
	public static int convertIntBitsIEEEToMSBinary(int source) {
		int mantissa = source & 0x007FFFFF;
		int sign = (source & 0x80000000) >> 31;
		int exponent = (source & 0x7F800000) >>> 23;
		exponent = exponent - 0x7F + 0x81 ; // get new bias exponent
		sign = sign << 22;
		exponent = exponent << 24;
		int retVal = mantissa | sign | exponent;
		return retVal;
	}

	public static String formatBinary(int value) {
		StringBuffer sb = new StringBuffer();
		String s = Integer.toBinaryString(value);
		for(int i=s.length(); i<32; i++) {
			sb.append("0");
		}
		sb.append(s);
		s = sb.toString();
		sb.setLength(0);
		for(int i=0; i<32; i++) {
			sb.append(s.charAt(i));
			if (i>0 && (i+1)%4==0) {
				sb.append(" ");
			}
		}
		return sb.toString();
	}

	public static BigInteger convertRealNumberBits(
			BigInteger source, int size, int direction) {
		return source;
	}

	/**
	 * Convert byte array into int array
	 */
	public static int[] convertByteArrayToIntArray(byte[] data) {
		int[] dataInt = new int[data.length];
		for (int i=0; i<data.length; i++) {
			if (data[i] > 0) {
				dataInt[i] = data[i];
			} else {
				dataInt[i] = 256 + data[i];
			}
		}
		return dataInt;
	}

	/**
	 * Convert int array into byte array
	 */
	public static byte[] convertIntArrayToByteArray(int[] data) {
		byte[] dataByte = new byte[data.length];
		for (int i=0; i<data.length; i++) {
			if (data[i] < 128) {
				dataByte[i] = (byte)data[i];
			} else if (data[i] >= 128 && data[i] < 256){
				dataByte[i] = (byte)(data[i] - 256);
			} else {
				dataByte[i] = 0;
			}
		}
		return dataByte;
	}

	/**
	 * Convert the integer array into hexadecimal string form
	 * @param data an integer array
	 * @return the hexadecimal string form of the array
	 */
	public static String convertIntArrayToString(int[] data) {
		StringBuffer sb = new StringBuffer();
		for (int i=0; i<data.length; i++) {
			String s = Integer.toHexString(data[i]);
			if (s.length() == 1) {
				s = "0" + s;
			}
			if (s.length() > 2) {
				s = "00";
			}
			sb.append(s.toUpperCase());
		}
		return sb.toString();
	}

	/**
	 * Convert the byte array into hexadecimal string form
	 * @param data an byte array
	 * @return the hexadecimal string form of the array
	 */
	public static String convertByteArrayToString(byte[] data) {
		int[] dataInt = ConvertUtil.convertByteArrayToIntArray(data);
		return ConvertUtil.convertIntArrayToString(dataInt);
	}

	/**
	 * Convert the string of hexadecimal representation into
	 * the equivalent byte array
	 * @return the byte array from the string data
	 */
	public static byte[] convertStringToByteArray(String data) {
		if (data.length() % 2 != 0) {
			return null;
		}
		byte[] b = new byte[data.length()/2];
		try {
			for (int i=0; i*2<data.length(); i++) {
				String s = data.substring(i*2, (i*2)+2);
				int val = Integer.parseInt(s, 16);
				if (val > 127) val = val - 256;
				b[i] = (byte)val;
			}
		} catch (Exception e) {
			if (log.isErrorEnabled()) { log.error(e); }
		}
		return b;
	}
	
	/**
	 * Convert a collection of objects into a string
	 */
	public static String convertCollectionToString(Collection c) {
		if (c == null) return null;
		return convertIteratorToString(c.iterator());
	}

	/**
	 * Convert a list of objects into a string
	 */
	public static String convertListToString(List list) {
		if (list == null) return null;
		return convertIteratorToString(list.iterator());
	}

	/**
	 * Convert a set of objects into a string
	 */
	public static String convertSetToString(Set set) {
		if (set == null) return null;
		return convertIteratorToString(set.iterator());
	}
	
	/** Converts a collection into a list */
	public static List convertCollectionToList(Collection c) {
		List l = null;
		if (c instanceof List) {
			l = (List)c;
		} else {
			l = new ArrayList();
			l.addAll(c);
		}
		return l;
	}

	/**
	 * Convert a iterator of objects into a string
	 */
	public static String convertIteratorToString(Iterator iter) {
		if (iter == null) return null;
		StringBuffer sb = new StringBuffer();
		while (iter.hasNext()) {
			sb.append(iter.next());
			sb.append(StringUtil.NEW_LINE_CHAR);
		}
		if (sb.length() > 0) {
			sb.deleteCharAt(sb.length() - 1);
		}
		return sb.toString();
	}
	
	/**
	 * Parse an input string from a specified format and populate the
	 * result into the object.
	 * The specified format is in the following structure
	 * ${name}<somechars>${name}...
	 * E.g. ${volume,java.lang.Integer},${date,java.util.Date,yyyyMMdd}...
	 * @param source the source input string
	 * @param format the format of the input string
	 * @param o the object to be populated
	 * @param start the start tag
	 * @param end the end tag
	 * @return the List of params parsed
	 */
	public static Collection parse(String source, String format, Object o, String start, String end) {
		Map map = new HashMap();
		ArrayList a = new ArrayList();
		try {
			StringBuffer sb = new StringBuffer(format);
			int index1 = -1;
			int index2 = -1;
			int index3 = -1;
			String param = null;
			String text = null;
			do {
				param = null;
				text = null;
				index1 = sb.toString().indexOf(start, index2);
				if (index1 >= 0) {
					index2 = sb.toString().indexOf(end, index1+start.length());
				}
				if (index1 < index2 && index1 > -1 && index2 > -1) {
					int from = index1 + start.length();
					int to = -1;
					if (index2 > sb.length()) {
						to = sb.length();
					} else {
						to = index2;// - end.length();
					}
					param = sb.substring(from, to);
				}
				if (index1 > index3+1) {
					text = sb.substring(index3+1, index1);
				}
				if (text != null) {
					Param p = Param.fromString(text);
					a.add(p);
				}
				if (index2 > 0) {
					index3 = index2;
				}
				if (param != null) {
					index2 += end.length();
					Param p = Param.fromString(param);
					a.add(p);
				}
			} while (param != null);
			if (sb.length() > index3) {
				text = sb.substring(index3, sb.length());
				Param p = Param.fromString(text);
				a.add(p);
			}
			// now parse the source string
			index1 = 0;
			index2 = 0;
			index3 = -1;
			for (int i=0; i<a.size(); i++) {
				Param p = (Param)a.get(i);
				if (p.isParam()) {
					if (i+1 < a.size()) {
						Param nextP = (Param)a.get(i+1);
						index3 = source.indexOf(nextP.getValue(), index2);
					} else {
						index3 = source.length();
					}
					if (index3 > index2) {
						String s = source.substring(index2, index3);
						p.setValue(s);
						Object value = ClassUtil.newInstance(p.getType(), p.getValue(), p.getFormat());
						map.put(p.getName(), value);
					}
				} else {
					index1 = source.indexOf(p.getValue(), index2);
					index2 = index1 + p.getValue().length();
				}
			}
		} catch (Exception e) {
			if (log.isErrorEnabled()) { log.error(e); }
		}
		try {
			BeanUtils.populate(o, map);
		} catch (Exception e) {
			if (log.isErrorEnabled()) { log.error(e); }
		}
		return a;
	}
	
	public static class Param {
		private String name;
		private String type;
		private String format;
		private String value;
		private boolean param;
		/** @return Returns the format. */
		public String getFormat() { return (this.format); }
		/** @param format The format to set. */
		public void setFormat(String format) { this.format = format; }
		/** @return Returns the name. */
		public String getName() { return (this.name); }
		/** @param name The name to set. */
		public void setName(String name) { this.name = name; }
		/** @return Returns the type. */
		public String getType() { return (this.type); }
		/** @param type The type to set. */
		public void setType(String type) { this.type = type; }
		/** @return Returns the value. */
		public String getValue() { return (this.value); }
		/** @param value The value to set. */
		public void setValue(String value) { this.value = value; }
		/** @return Returns the isParam. */
		public boolean isParam() { return (this.param); }
		/** @param isParam The isParam to set. */
		public void setParam(boolean param) { this.param = param; }
		public static Param fromString(String s) {
			if (s == null) { return null; }
			String[] sa = StringUtil.tokenizeIntoStringArray(s, ",");
			Param p = new Param();
			if (sa == null) {
				p.setParam(false);
				p.setValue(s);
			}
			if (sa.length == 2) {
				p.setName(sa[0]);
				p.setType(sa[1]);
				p.setParam(true);
			} else if (sa.length == 3) {
				p.setName(sa[0]);
				p.setType(sa[1]);
				p.setFormat(sa[2]);
				p.setParam(true);
			} else {
				p.setParam(false);
				p.setValue(s);
			}
			return p;
		}
		/** Converts the class in a string form 
		 * @returns the class in a string form. 
		 */
		public String toString() {
			StringBuffer sb = new StringBuffer();
			sb.append("[name:").append(this.name);
			sb.append("|type:").append(this.type);
			sb.append("|format:").append(this.format);
			sb.append("|value:").append(this.value);
			sb.append("|isParam:").append(this.param);
			sb.append("]  ");
			return sb.toString();
		}
	};
	
	/** @see ConvertUtil#parse(String, String, Object, String, String)*/
	public static Collection parse(String source, String format, Object o) {
		return parse (source, format, o, MiscUtil.START, MiscUtil.END);
	}
}