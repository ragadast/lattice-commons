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
package org.latticesoft.util.resource;

import java.sql.*;

import javax.sql.DataSource;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;

import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Properties;
import java.lang.reflect.*;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.latticesoft.util.common.*;
import org.latticesoft.util.resource.dao.Param;

/**
 * The DatabaseUtil is a utility to simplify SQL operations.
 */
public class DatabaseUtil {
	
	private static final Log log = LogFactory.getLog(DatabaseUtil.class);

	/**
	 * Gets a database connection from JNDI Datasource.
	 * @param jndi the name which the datasource is bounded to.
	 * @return the connection if successful. If not null will be returned.
	 * @deprecated 
	 **/
	public static Connection getDatabaseConnection(String jndiName) throws NamingException, SQLException {
		return getConnectionFromJNDI(jndiName);
	}
	/**
	 * Gets a database connection from JNDI Datasource.
	 * @param jndi the name which the datasource is bounded to.
	 * @param env the environment for initializing the initial context
	 * @return the connection if successful. If not null will be returned.
	 * @deprecated 
	 **/
	public static Connection getDatabaseConnection(String jndiName, Map env) throws NamingException, SQLException {
		return DatabaseUtil.getConnectionFromJNDI(jndiName, env);
	}
	/**
	 * Gets a database connection from a ODBC setting
	 * @param odbcName the name which the ODBC is declared.
	 * @param user the user name (can be null)
	 * @param password the password (can be null)
	 * @return the connection if successful. If not null will be returned.
	 * @deprecated 
	 **/
	public static Connection getDatabaseConnection(String odbcName, String user, String password) throws SQLException {
		return DatabaseUtil.getConnectionFromODBC(odbcName, user, password);
	}
	/**
	 * Gets a database connection from a ODBC setting
	 * @param databaseName the name which the odbc is declared.
	 * @param user the user name (can be null)
	 * @param password the password (can be null)
	 * @param timeout the time out
	 * @return the connection if successful. If not null will be returned.
	 * @deprecated 
	 **/
	public static Connection getDatabaseConnection(String odbcName, String user, String password, int timeout) throws SQLException {
		return DatabaseUtil.getConnectionFromODBC(odbcName, user, password, timeout);
	}
	/**
	 * Create a map of connections based on the input map.
	 * The map will contain at least the database name and the key 
	 * to put the connection
	 * E.g.
	 * <pre>
	 * Map map = new HashMap();
	 * int index = 1;
	 * //no password
	 * map.put(DatabaseUtil.DATABASE_NAME + index, "odbc1");
	 * map.put(DatabaseUtil.KEY + index, "odbc1");
	 * // have password
	 * index++;
	 * map.put(DatabaseUtil.DATABASE_NAME + index, "odbc2");
	 * map.put(DatabaseUtil.KEY + index, "odbc2");
	 * map.put(DatabaseUtil.USER + index, "user2");
	 * map.put(DatabaseUtil.PASSWORD + index, "password2");
	 * // create the map of connection
	 * map = DatabaseUtil.getConnection(map);
	 * </pre>
	 * @return a map of connection
	 * @deprecated
	 */
	public static Map getConnection(Map map) throws SQLException {
		return DatabaseUtil.getConnectionFromODBC(map);
	}

	
	

	
	
	
	
	
	/**
	 * Gets a database connection from JNDI Datasource.
	 * @param jndi the name which the datasource is bounded to.
	 * @return the connection if successful. If not null will be returned.
	 **/
	public static Connection getConnectionFromJNDI(String jndiName) throws NamingException, SQLException {
		return DatabaseUtil.getConnectionFromJNDI(jndiName, null);
	}
	
	/**
	 * Gets a database connection from JNDI Datasource.
	 * @param jndi the name which the datasource is bounded to.
	 * @param env the environment for initializing the initial context
	 * @return the connection if successful. If not null will be returned. 
	 **/
	public static Connection getConnectionFromJNDI(String jndiName, Map env) throws NamingException, SQLException {
		Properties p = null;
		InitialContext ctx = null;
		if (env != null) {
			if (env instanceof Properties) {
				p = (Properties)env;
			} else {
				p = new Properties();
				p.putAll(env);
			}
			ctx = new InitialContext(p);
		} else {
			ctx = new InitialContext();
		}
		Object o = ctx.lookup(jndiName);
		javax.sql.DataSource ds = (javax.sql.DataSource)PortableRemoteObject.narrow(o, DataSource.class);
		Connection conn = ds.getConnection();
		return conn;
	}
	
	/**
	 * Gets a database connection from a ODBC setting
	 * @param odbcName the name which the ODBC is declared.
	 * @param user the user name (can be null)
	 * @param password the password (can be null)
	 * @return the connection if successful. If not null will be returned. 
	 **/
	public static Connection getConnectionFromODBC(String odbcName, String user, String password) throws SQLException {
		return DatabaseUtil.getConnectionFromODBC(odbcName, user, password, -1);
	}
	
	/**
	 * Gets a database connection from a ODBC setting
	 * @param odbcName the name which the ODBC is declared.
	 * @param user the user name (can be null)
	 * @param password the password (can be null)
	 * @param timeout the time out
	 * @return the connection if successful. If not null will be returned. 
	 **/
	public static Connection getConnectionFromODBC(String databaseName, String user, String password, int timeout) throws SQLException {
		sun.jdbc.odbc.ee.DataSource ds = new sun.jdbc.odbc.ee.DataSource();
		ds.setDatabaseName(databaseName);
		if (timeout >= 0) {
			ds.setLoginTimeout(timeout);
		}
		if (user != null) {
			ds.setUser(user);
		}
		if (password != null) {
			ds.setPassword(password);
		}
		Connection conn = ds.getConnection();
		return conn;
	}

	
	
	
	public static final String DATABASE_NAME = "org_latticesoft_util_resource_DatabaseUtil_databaseName";
	public static final String USER = "org_latticesoft_util_resource_DatabaseUtil_user";
	public static final String PASSWORD = "org_latticesoft_util_resource_DatabaseUtil_password";
	public static final String KEY = "org_latticesoft_util_resource_DatabaseUtil_key";
	/**
	 * Create a map of connections based on the input map.
	 * The map will contain at least the database name and the key 
	 * to put the connection
	 * E.g.
	 * <pre>
	 * Map map = new HashMap();
	 * int index = 1;
	 * //no password
	 * map.put(DatabaseUtil.DATABASE_NAME + index, "odbc1");
	 * map.put(DatabaseUtil.KEY + index, "odbc1");
	 * // have password
	 * index++;
	 * map.put(DatabaseUtil.DATABASE_NAME + index, "odbc2");
	 * map.put(DatabaseUtil.KEY + index, "odbc2");
	 * map.put(DatabaseUtil.USER + index, "user2");
	 * map.put(DatabaseUtil.PASSWORD + index, "password2");
	 * // create the map of connection
	 * map = DatabaseUtil.getConnection(map);
	 * </pre>
	 * @return a map of connection
	 */
	public static Map getConnectionFromODBC(Map map) throws SQLException {
		int index = 1;
		String db = (String)map.get(DatabaseUtil.DATABASE_NAME + index);
		String user = (String)map.get(DatabaseUtil.USER + index);
		String pwd = (String)map.get(DatabaseUtil.PASSWORD + index);
		String key = (String)map.get(DatabaseUtil.KEY + index);
		Connection conn = null;
		while (db != null && key != null) {
			try {
				conn = DatabaseUtil.getConnectionFromODBC(db, user, pwd);
				if (conn != null && key != null) {
					map.put(key, conn);
				}
			} catch (SQLException e) {
			}
			index++;
			db = (String)map.get(DatabaseUtil.DATABASE_NAME + index);
			user = (String)map.get(DatabaseUtil.USER + index);
			pwd = (String)map.get(DatabaseUtil.PASSWORD + index);
			key = (String)map.get(DatabaseUtil.KEY + index);
		}
		return map;
	}
	
	/**
	 * Gets a connection from a <code>DriverManager</code>.
	 * @param driver the name of the database driver
	 * @param url the database URL
	 * @param user the user name (can be null)
	 * @param password password (can be null)
	 * @return the connection if successful. null if not.
	 */
	public static Connection getConnection(String driver, String url, String user, String password) throws SQLException {
		return DatabaseUtil.getConnectionFromJDBC(driver, url, user, password);
	}
		
	/**
	 * Gets a connection from a <code>DriverManager</code>.
	 * @param driver the name of the database driver
	 * @param url the database URL
	 * @param user the user name (can be null)
	 * @param password password (can be null)
	 * @return the connection if successful. null if not.
	 */
	public static Connection getConnectionFromJDBC(String driver, String url, String user, String password) throws SQLException {
		if (driver == null || url == null) {
			return null;
		}
		Connection conn = null;
		try {
			Class.forName(driver);
			if (user == null || password == null) {
				conn = DriverManager.getConnection(url);
			} else {
				conn = DriverManager.getConnection(url, user, password);
			}
		} catch (ClassNotFoundException cnfe) {
			// do nothing
			cnfe.printStackTrace();
		} catch(SQLException sqle) {
			throw sqle;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return conn;
	}
	
	/**
	 * Close all the resource properly
	 * @param o either a ResultSet or Statement or Connection 
	 */
	public static void closeResource(Object o) {
		if (o == null) return;
		if (o instanceof Connection) {
			DatabaseUtil.closeConnection((Connection)o);
		}
		if (o instanceof ResultSet) {
			DatabaseUtil.closeResultSet((ResultSet)o);
		}
		if (o instanceof Statement) {
			DatabaseUtil.closeStatement((Statement)o);
		}
	}
	
	/**
	 * Closes the connection.
	 * @param conn the connection to be closed
	 */
	public static void closeConnection(Connection conn) {
		try {
			if (conn != null) {
				conn.close();
			}
		} catch (Exception e) {
			if (log.isErrorEnabled()) { log.error("Error in closing connection", e); }
			conn = null;
		}
	}
	
	/**
	 * Closes the statement
	 * @param stmt the statement to be closed
	 */
	public static void closeStatement(Statement stmt) {
		try {
			if (stmt != null && stmt instanceof PreparedStatement) {
				PreparedStatement pstmt = (PreparedStatement)stmt;
				pstmt.close();
			} else if (stmt != null) {
				stmt.close();
			}
		} catch (Exception e) {
			if (log.isErrorEnabled()) { log.error("Error in closing statement", e); }
			stmt = null;
		}
	}
	
	/**
	 * Closes the ResultSet
	 * @param stmt the result set to be closed
	 */
	public static void closeResultSet(ResultSet rs) {
		try {
			if (rs != null) {
				rs.close();
			}
		} catch (Exception e) {
			if (log.isErrorEnabled()) { log.error("Error in closing resultset", e); }
			rs = null;
		}
	}
	
	/**
	 * Retrieves the results into a bean
	 * @param rs the ResultSet
	 * @param columns the column name
	 * @param beanClass the bean to be instantiated
	 * @param beanProps the attributes of the column
	 * @return a collection of beans of result
	 */
	public static Collection retrieveResult(ResultSet rs, String[] columns, Class beanClass, String[] beanProps) throws SQLException {
		Collection c = new ArrayList();
		if (rs == null || beanClass == null || columns == null || beanProps == null) {
			return c;
		}
		if (columns.length != beanProps.length) {
			return c;
		}
		Map map = new HashMap();
		while (rs.next()) {
			try {
				Object bean = beanClass.newInstance();
				map.clear();
				for (int i=0; i<columns.length; i++) {
					Object column = rs.getObject(columns[i]);
					map.put(beanProps[i], column);
				}
				BeanUtils.populate(bean, map);
			} catch (InvocationTargetException ite) {
				if (log.isErrorEnabled()) {
					log.error(ite);
				}
			} catch (IllegalAccessException iae) {
				if (log.isErrorEnabled()) {
					log.error(iae);
				}
			} catch (InstantiationException ie) {
				if (log.isErrorEnabled()) {
					log.error(ie);
				}
			} catch (SQLException sqle) {
				if (log.isErrorEnabled()) {
					log.error(sqle);
				}
			}
		}
		return c;
	}
	
	/**
	 * Invoke the Param object to populate the object from the ResultSet
	 */
	public static void populate(Object vo, ResultSet rs, Param p[]) {
		if (vo == null || rs == null || p == null) {
			return;
		}
		for (int i=0; i<p.length; i++) {
			if (p[i] != null) {
				p[i].populate(vo, rs);
			}
		}
	}
	
	/**
	 * Executes an update statement
	 * @param sql the statement to execute
	 * @param conn the connection to execute
	 * @param closeConn true to close the connection after use 
	 */
	public static void executeUpdate(String sql, Connection conn, boolean closeConn) {
		if (sql == null || conn == null) return;
		Statement stmt = null;
		try {
			stmt = conn.createStatement();
			stmt.executeUpdate(sql);
		} catch (Exception e) {
			if (log.isErrorEnabled()) { log.error(e); }
		} finally {
			try { stmt.close(); } catch (Exception e) {}
			if (closeConn) {
				try { conn.close(); } catch (Exception e) {}
			}
			stmt = null;
			conn = null;
		}
	}
	/**
	 * Defaults to close connection after use
	 * @see #executeUpdate(String, Connection, boolean)
	 */
	public static void executeUpdate(String sql, Connection conn) {
		executeUpdate(sql, conn, true);
	}
	
	
	/**
	 * Executes the query in batch statement
	 * @param c the collection of SQL statements to execute
	 * @param conn the connection
	 * @param closeConn true to close the connection after use
	 * @return int[] a integer array of the executed result
	 */
	public int[] executeBatch(Collection c, Connection conn, boolean closeConn) {
		if (c == null || conn == null) return null;
		if (c.size() == 0) return null;
		Statement stmt = null;
		String sql = null;
		Iterator iter = c.iterator();
		int[] retVal = null;
		try {
			stmt = conn.createStatement();
			while (iter.hasNext()) {
				Object o = iter.next();
				if (o == null) continue;
				sql = o.toString();
				stmt.addBatch(sql);
			}
			retVal = stmt.executeBatch();
		} catch (Exception e) {
			if (log.isErrorEnabled()) { log.error(e); }
		} finally {
			try { stmt.close(); } catch (Exception e) {}
			if (closeConn) {
				try { conn.close(); } catch (Exception e) {}
			}
			stmt = null;
			conn = null;
		}
		return retVal;
	}
	/**
	 * Defaults to closed connection after use
	 * @see #executeBatch(Collection, Connection, boolean)
	 */
	public int[] executeBatch(Collection c, Connection conn) {
		return executeBatch(c, conn, true);
	}
	
	/** 
	 * Analyse the SQL string into the components
	 * @return a String[] of the components 
	 */
	private static String[] analyseSelectSQL(String sql) {
		String[] s = null;
		if (sql == null) return s;
		sql = sql.trim();
		String ss = sql.toLowerCase();
		if (!ss.startsWith("select")) return s;
		int index = ss.indexOf("from");
		if (index < 0) return s;
		String cols = sql.substring("select".length(), index).trim();

		if (cols.equals("*")) {
			s = null;
		} else {
			s = StringUtil.tokenizeIntoStringArray(cols, ",");
			// regroup
			int open = 0;
			int close = 0;
			ArrayList result = new ArrayList();
			StringBuffer sb = new StringBuffer();
			for (int i=0; i<s.length; i++) {
				sb.append(s[i]).append(",");
				open += StringUtil.countOf(s[i], "(");
				close += StringUtil.countOf(s[i], ")");
				if (open == close) {
					sb.deleteCharAt(sb.length() - 1);
					result.add(sb.toString());
					sb.setLength(0);
					open = 0;
					close = 0;
				}
			}
			s = new String[result.size()];
			s = (String[])result.toArray(s);
			
			for (int i=0; i<s.length; i++) {
				if (s[i] != null) {
					index = s[i].toLowerCase().indexOf(" as ");
					if (index > -1) {
						s[i] = s[i].substring(index+4, s[i].length()).trim();
					}
					index = s[i].toLowerCase().indexOf(" ");
					if (index > -1) {
						s[i] = s[i].substring(index+1, s[i].length()).trim();
					}
				}
			}
		}
		return s;
	}
	
	/** @see #executeQuery(String, Class, Connection, boolean) */
	public static Collection executeQuery(String sql, String beanClass, Connection conn, boolean closeConn) {
		Class clazz = null;
		try {
			clazz = Class.forName(beanClass);
		} catch (Exception e) {
		}
		return executeQuery(sql, clazz, conn, closeConn);
	}
	
	/**
	 * Executes the query and populate the results into a collection of the beanClass instance.
	 * The sql statement must include the bean attribute name map to the database column
	 * E.g.
	 * SELECT dbcol1 AS beanAttribute1, dbcol2 AS beanAttribute2 FROM someTable
	 * In this case the dbcol1 is the name of the table column which maps to the
	 * beanAttribute1 name of the java bean
	 * 
	 * @param sql the query to execute
	 * @param beanClass the class of the java bean
	 * @param conn the connection
	 * @param closeConn true to close the connection false will leave it open
	 * @return a collection of the populated bean object 
	 */
	public static Collection executeQuery(String sql, Class beanClass, Connection conn, boolean closeConn) {
		ArrayList a = new ArrayList();
		if (sql == null || conn == null || beanClass == null) return a;
		Statement stmt = null;
		ResultSet rs = null;
		Map map = new HashMap();
		try {
			boolean isAll = false;
			String[] dbFields = DatabaseUtil.analyseSelectSQL(sql);
			String[] classFields = ClassUtil.getAttributeNamesAsArray(beanClass, true);
			if (dbFields == null) {
				isAll = true;
			}
			if (dbFields == null && classFields == null) {
				return a;
			}
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			Object o = null;
			String field = null;
			while (rs.next()) {
				if (isAll) {
					for (int i=0; i<classFields.length; i++) {
						field = classFields[i];
						o = rs.getObject(field);
						map.put(field, o);
					}
				} else {
					for (int i=0; i<dbFields.length; i++) {
						field = dbFields[i];
						o = rs.getObject(field);
						map.put(field, o);
					}
				}
				Object bean = beanClass.newInstance();
				BeanUtils.populate(bean, map);
				a.add(bean);
			}
		} catch (Exception e) {
			if (log.isErrorEnabled()) { log.error(e); }
		} finally {
			try { stmt.close(); } catch (Exception e) {}
			if (closeConn) {
				try { conn.close(); } catch (Exception e) {}
			}
			stmt = null;
			conn = null;
		}
		return a;
	}
	/**
	 * Default close connection 
	 * @see #executeQuery(String, Class, Connection, boolean) 
	 */
	public static Collection executeQuery(String sql, Class beanClass, Connection conn) {
		return executeQuery(sql, beanClass, conn, true);
	}
	/**
	 * Default close connection 
	 * @see #executeQuery(String, Class, Connection, boolean) 
	 */
	public static Collection executeQuery(String sql, String beanClass, Connection conn) {
		return executeQuery(sql, beanClass, conn, true);
	}
}