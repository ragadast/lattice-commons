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
 * Created on Mar 21, 2006
 *
 */
package org.latticesoft.util.resource.dao;

import java.io.Serializable;
import java.sql.*;
import java.math.*;
import java.util.*;
import java.lang.reflect.*;
import org.latticesoft.util.common.*;
import org.apache.commons.beanutils.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Param implements Serializable {
	public static final long serialVersionUID = 2006103909;
	private static final Log log = LogFactory.getLog(Param.class);
	
	private String className;
	private String method;
	private int sqlIndex;
	private String sqlName;
	private int sqlType;
	private String attribute;
	private String format;
	
	public Param(){}
	public Param(String columnName, int columnIndex, String attribute, int sqlType){
		this.setSqlName(columnName);
		this.setSqlIndex(columnIndex);
		this.setAttribute(attribute);
		this.setSqlType(sqlType);
	}
	/** @return Returns the attribute. */
	public String getAttribute() { return (this.attribute); }
	/** @param attribute The attribute to set. */
	public void setAttribute(String attribute) { this.attribute = attribute; }
	/** @return Returns the column. */
	public String getSqlName() { return (this.sqlName); }
	/** @param column The column to set. */
	public void setSqlName(String columnName) { this.sqlName = columnName; }
	/** @return Returns the sqlType. */
	public int getSqlType() { return (this.sqlType); }
	/** @param sqlType The sqlType to set. */
	public void setSqlType(int type) { this.sqlType = type; }
	/** @return Returns the sqlType. */
	public String getSqlTypeString() {
		StringBuffer sb = new StringBuffer();
		try {
			Field[] f = Types.class.getFields();
			for (int i=0; i<f.length; i++) {
				if (f[i].getInt(Types.class) == this.sqlType) {
					sb.append(f[i].getName());
					break;
				}
			}
		} catch (Exception e) {
			if (log.isErrorEnabled()) { log.error(e); }
		}
		return sb.toString();
	}
	/** @param sqlType The sqlType to set. */
	public void setSqlTypeString(String type) {
		if (type == null) return;
		try {
			int i = Types.class.getField(type.toUpperCase()).getInt(Types.class);
			this.setSqlType(i);
		} catch (Exception e) {
			if (log.isErrorEnabled()) { log.error(e); }
		}
	}
	/** @return Returns the sqlIndex. */
	public int getSqlIndex() { return (this.sqlIndex); }
	/** @param sqlIndex The sqlIndex to set. */
	public void setSqlIndex(int columnIndex) { this.sqlIndex = columnIndex; }
	/** @return Returns the format. */
	public String getFormat() { return (this.format); }
	/** @param format The format to set. */
	public void setFormat(String format) { this.format = format; }
	/** @return Returns the class name. */
	/** @return Returns the className. */
	public String getClassName() { return (this.className); }
	/** @param className The className to set. */
	public void setClassName(String className) { this.className = className; }
	/** @return Returns the method. */
	public String getMethod() { return (this.method); }
	/** @param method The method to set. */
	public void setMethod(String method) { this.method = method; }

	/**
	* Converts the class in a string form
	* @returns the class in a string form.
	*/
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(this.getSqlIndex()).append("|");
		sb.append(this.getSqlName()).append("|");
		sb.append(this.getSqlType()).append("|");
		sb.append(this.getAttribute());
		return sb.toString();
	}
	/** Parse object from string */
	public static Param parse(String s) {
		return fromString(s);
	}
	/** Parse object from string */
	public static Param fromString(String s) {
		if (s == null) return null;
		s = s.trim();
		if (s.length() == 0) return null;
		Param p = new Param();
		String[] ss = StringUtil.tokenizeIntoStringArray(s, "|");
		if (ss == null) return null;
		if (ss.length < 4) return null;
		for (int i=0; i<ss.length; i++) {
			if (ss[i] == null) return null;
		}
		int index = NumeralUtil.parseInt(ss[0]);
		if (index > 0) {
			p.setSqlIndex(index);
		}
		p.setSqlName(ss[1]);
		try {
			int i = Types.class.getField(ss[2].toUpperCase()).getInt(Types.class);
			p.setSqlType(i);
		} catch (Exception e) {
			if (log.isErrorEnabled()) { log.error(e); }
		}
		p.setAttribute(ss[3]);
		return p;
	}

	/**
	 * Populate the object with the attributes from the ResultSet
	 * @param o the object to be populated
	 * @param rs the ResultSet from the db query
	 */
	public void populate(Object o, ResultSet rs) {
		try {
			this.populateEx(o, rs);
		} catch (Exception e) {
			if (log.isErrorEnabled()) { log.error(e); }
		}
	}
	/**
	 * @see #populate(Object, ResultSet)
	 * @exception throws SQLException
	 */
	public void populateEx(Object o, ResultSet rs) throws SQLException {
		Object value = this.readValue(rs);
		if (o instanceof WrapDynaBean) {
			WrapDynaBean bean = (WrapDynaBean)o;
			bean.set(this.getAttribute(), value);
		} else if (o instanceof Map) {
			Map map = (Map)o;
			map.put(this.getAttribute(), value);
		} else {
			WrapDynaBean bean = new WrapDynaBean(o);
			bean.set(this.getAttribute(), value);
		}
	}
	/**
	 * Prepares the statement for query
	 * @param o the bean for the query
	 * @param pstmt the statement for query
	 */
	public void prepare(Object o, PreparedStatement pstmt) {
		try {
			this.prepareEx(o, pstmt);
		} catch (Exception e) {
			if (log.isErrorEnabled()) { log.error(e); }
		}
	}
	/**
	 * @see #prepare(Object, PreparedStatement)
	 * @exception throws SQLException
	 */
	public void prepareEx(Object o, PreparedStatement pstmt) throws SQLException {
		Object param = null;
		if (o instanceof WrapDynaBean) {
			WrapDynaBean bean = (WrapDynaBean)o;
			param = bean.get(this.getAttribute());
		} else if (o instanceof Map) {
			Map map = (Map)o;
			param = map.get(this.getAttribute());
		} else {
			WrapDynaBean bean = new WrapDynaBean(o);
			param = bean.get(this.getAttribute());
		}
		this.setValueToStatement(param, pstmt);
	}
	
	private Object readValue(ResultSet rs) throws SQLException {
		Object retVal = null;
		switch (this.sqlType) {
		case Types.VARCHAR:
		case Types.CHAR:
			String s = null;
			if (this.getSqlIndex() == 0) {
				s = rs.getString(this.getSqlName());
			} else {
				s = rs.getString(this.getSqlIndex()); 
			}
			retVal = s;
			break;
		case Types.BOOLEAN:
			boolean b = false;
			if (this.getSqlIndex() == 0) {
				b = rs.getBoolean(this.getSqlName());
			} else {
				b = rs.getBoolean(this.getSqlIndex()); 
			}
			retVal = new Boolean(b);
			break;
		case Types.INTEGER:
			int i = 0;
			if (this.getSqlIndex() == 0) {
				i = rs.getInt(this.getSqlName());
			} else {
				i = rs.getInt(this.getSqlIndex()); 
			}
			retVal = new Integer(i);
			break;
		case Types.SMALLINT:
			short ss = 0;
			if (this.getSqlIndex() == 0) {
				ss = rs.getShort(this.getSqlName());
			} else {
				ss = rs.getShort(this.getSqlIndex()); 
			}
			retVal = new Short(ss);
			break;
		case Types.TINYINT:
			byte bb = 0;
			if (this.getSqlIndex() == 0) {
				bb = rs.getByte(this.getSqlName());
			} else {
				bb = rs.getByte(this.getSqlIndex()); 
			}
			retVal = new Byte(bb);
			break;
		case Types.BIGINT:
			long l = 0;
			if (this.getSqlIndex() == 0) {
				l = rs.getLong(this.getSqlName());
			} else {
				l = rs.getLong(this.getSqlIndex()); 
			}
			retVal = new Long(l);
			break;
		case Types.DOUBLE:
			double dd = 0;
			if (this.getSqlIndex() == 0) {
				dd = rs.getDouble(this.getSqlName());
			} else {
				dd = rs.getDouble(this.getSqlIndex()); 
			}
			retVal = new Double(dd);
			break;
		case Types.FLOAT:
			float f = 0;
			if (this.getSqlIndex() == 0) {
				f = rs.getFloat(this.getSqlName());
			} else {
				f = rs.getFloat(this.getSqlIndex()); 
			}
			retVal = new Float(f);
			break;
		case Types.NUMERIC:
			BigDecimal bd = null;
			if (this.getSqlIndex() == 0) {
				bd = rs.getBigDecimal(this.getSqlName());
			} else {
				bd = rs.getBigDecimal(this.getSqlIndex()); 
			}
			retVal = bd;
			break;
		case Types.TIMESTAMP:
			Timestamp ts = null;
			if (this.getSqlIndex() == 0) {
				ts = rs.getTimestamp(this.getSqlName());
			} else {
				ts = rs.getTimestamp(this.getSqlIndex()); 
			}
			retVal = ts;
			break;
		default:
			if (this.getSqlIndex() == 0) {
				retVal = rs.getObject(this.getSqlName());
			} else {
				retVal = rs.getObject(this.getSqlIndex()); 
			}
			break;
		}
if (log.isDebugEnabled()) { log.debug(this.getAttribute() + "=" + retVal); }
		return retVal;
	}
	private void setValueToStatement(Object o, PreparedStatement pstmt) throws SQLException {
if (log.isDebugEnabled()) { log.debug(this.sqlIndex + "=" + o); }
		switch (this.sqlType) {
		case Types.VARCHAR:
		case Types.CHAR:
			String s = (String)o;
			pstmt.setString(this.sqlIndex, s);
			break;
		case Types.BOOLEAN:
			if (o != null && o instanceof Boolean) {
				boolean b = ((Boolean)o).booleanValue();
				pstmt.setBoolean(this.sqlIndex, b);
			}
			break;
		case Types.INTEGER:
			if (o != null && o instanceof Integer) {
				int i = ((Integer)o).intValue();
				pstmt.setInt(this.sqlIndex, i);
			}
			break;
		case Types.SMALLINT:
			if (o != null && o instanceof Short) {
				short ss = ((Short)o).shortValue();
				pstmt.setShort(this.sqlIndex, ss);
			}
			break;
		case Types.TINYINT:
			if (o != null && o instanceof Byte) {
				byte bb = ((Byte)o).byteValue();
				pstmt.setByte(this.sqlIndex, bb);
			}
			break;
		case Types.BIGINT:
			if (o != null && o instanceof Long) {
				long l = ((Long)o).longValue();
				pstmt.setLong(this.sqlIndex, l);
			}
			break;
		case Types.DOUBLE:
			if (o != null && o instanceof Double) {
				double dd = ((Double)o).doubleValue();
				pstmt.setDouble(this.sqlIndex, dd);
			}
			break;
		case Types.FLOAT:
			if (o != null && o instanceof Float) {
				float f = ((Float)o).floatValue();
				pstmt.setFloat(this.sqlIndex, f);
			}
			break;
		case Types.NUMERIC:
			if (o != null && o instanceof BigDecimal) {
				BigDecimal bd = (BigDecimal)o;
				pstmt.setBigDecimal(this.sqlIndex, bd);
			}
			break;
		case Types.TIMESTAMP:
			if (o != null && o instanceof Timestamp) {
				Timestamp ts = (Timestamp)o;
				pstmt.setTimestamp(this.sqlIndex, ts);
			}
			break;
		case Types.NULL:
if (log.isDebugEnabled()) { log.debug(this.sqlIndex + " IS NULL"); }
			pstmt.setNull(this.sqlIndex, Types.NULL);
			break;
		default:
			if (o != null) {
				pstmt.setObject(this.sqlIndex, o);
			}
		}
	}
	/*
	public static void main(String[] args) {

		org.latticesoft.util.convert.FormatStringBean bean = new org.latticesoft.util.convert.FormatStringBean();
		bean.setLength(2122);
		Param ip = Param.fromString("1|id|INTEGER|length");
		java.util.ArrayList a = new java.util.ArrayList();
		Param op = Param.fromString("0|id|INTEGER|length");
		a.add(op);
		op = Param.fromString("0|d1|INTEGER|length");
		a.add(op);
		op = Param.fromString("0|d2|INTEGER|length");
		a.add(op);
		op = Param.fromString("0|d3|INTEGER|length");
		a.add(op);
		op = Param.fromString("0|d4|INTEGER|length");
		a.add(op);
		op = Param.fromString("0|d5|INTEGER|length");
		a.add(op);
		op = Param.fromString("0|d6|INTEGER|length");
		a.add(op);
		op = Param.fromString("0|d7|INTEGER|length");
		a.add(op);

		op = Param.fromString("0|d7|VARCHAR|length");
		System.out.println(op.getSqlTypeString());
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = DatabaseUtil.getDatabaseConnection("lottery", null, null);
			pstmt = conn.prepareStatement("SELECT * FROM toto WHERE id=? ");
			ip.prepareEx(bean, pstmt);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				for (int i=0; i<a.size(); i++) {
					op = (Param)a.get(i);
					op.populateEx(bean, rs);
					System.out.print(op.getSqlName());
					System.out.print("=");
					System.out.print(bean.getLength());
					System.out.print(", ");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DatabaseUtil.closeResultSet(rs);
			DatabaseUtil.closeStatement(pstmt);
			DatabaseUtil.closeConnection(conn);
		}
	}//*/
}