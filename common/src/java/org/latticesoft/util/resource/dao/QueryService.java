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
 * Created on May 31, 2006
 *
 */
package org.latticesoft.util.resource.dao;
import java.sql.*;
import java.util.*;
import org.latticesoft.command.*;
import org.latticesoft.util.resource.*;
import org.latticesoft.util.common.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class QueryService implements Service {
	private static final Log log = LogFactory.getLog(QueryService.class);
	private String name = null;
	private String sql = null;
	private String beanType = null;

	private Connection conn = null;
	private int method = 1;
	private boolean closeConn = true;
	private ArrayList inputParam = new ArrayList();
	private ArrayList outputParam = new ArrayList();
	public static final int EXECUTE_UPDATE = 1;
	public static final int EXECUTE_BATCH = 2;
	public static final int EXECUTE_QUERY = 3;
	public static final int EXECUTE_QUERY_OBJECT = 4;

	/** @return Returns the closeConn. */
	public boolean isCloseConnection() { return (this.closeConn); }
	/** @param closeConn The closeConn to set. */
	public void setCloseConnection(boolean b) { this.closeConn = b; }

	/** @return Returns the conn. */
	public Connection getConnection() { return (this.conn); }
	/** @param conn The conn to set. */
	public void setConnection(Connection conn) { this.conn = conn; }

	/** @return Returns the method. */
	public int getMethod() { return (this.method); }
	/** @param method The method to set. */
	public void setMethod(int method) { this.method = method; }

	/** @return Returns the sql. */
	public String getSql() { return (this.sql); }
	/** @param sql The sql to set. */
	public void setSql(String sql) { this.sql = sql; }

	/** @return Returns the type. */
	public String getBeanType() { return (this.beanType); }
	/** @param type The type to set. */
	public void setBeanType(String type) { this.beanType = type; }

	/** Return the type of the service */
	public int getType() {
		int retVal = Service.QUERY;
		switch (this.method) {
		case EXECUTE_UPDATE: retVal = Service.UPDATE; break;
		case EXECUTE_BATCH: retVal = Service.UPDATE; break;
		case EXECUTE_QUERY_OBJECT: retVal = Service.QUERY; break;
		case EXECUTE_QUERY: retVal = Service.QUERY; break;
		default: retVal = Service.QUERY; break;
		}
		return retVal;
	}
	
	/** @return Returns the method. */
	public String getMethodString() {
		String s = null;
		switch (this.method) {
		case EXECUTE_UPDATE: s = "executeUpdate"; break;
		case EXECUTE_BATCH: s = "executeBatch"; break;
		case EXECUTE_QUERY_OBJECT: s = "executeQuery"; break;
		case EXECUTE_QUERY: s = "executeQueryObject"; break;
		default: s = null; break;
		}
		return s;
	}
	/** @param method The method to set. */
	public void setMethodString(String s) {
		if (s.equalsIgnoreCase("executeUpdate")) {
			this.method = EXECUTE_UPDATE;
		} else if (s.equalsIgnoreCase("executeBatch")) {
			this.method = EXECUTE_BATCH;
		} else if (s.equalsIgnoreCase("executeQuery")) {
			this.method = EXECUTE_QUERY;
		} else if (s.equalsIgnoreCase("executeQueryObject")) {
			this.method = EXECUTE_QUERY_OBJECT;
		}
	}

	/** @return Returns the name. */
	public String getName() { return (this.name); }
	/** @param name The name to set. */
	public void setName(String name) { this.name = name; }

	/** @return Returns the inputParam. */
	public ArrayList getInputParam() { return (this.inputParam); }
	/** @return Returns the outputParam. */
	public ArrayList getOutputParam() { return (this.outputParam); }

	/** Adds a input parameter */
	public boolean addInput(Object o) {
		if (o instanceof Param && this.inputParam != null) {
			return this.inputParam.add(o);
		}
		return false;
	}
	/** Adds a output parameter */
	public boolean addOutput(Object o) {
		if (o instanceof Param && this.outputParam != null) {
			return this.outputParam.add(o);
		}
		return false;
	}
	
	/** @see Command#execute(Object) */
	public Object execute(Object o) throws CommandException {
		switch(this.method) {
		case EXECUTE_UPDATE:
			return new Integer(this.executeUpdate(o));
		case EXECUTE_BATCH:
			Collection c = null;
			if (o instanceof Collection) {
				c = (Collection)o;
			} else {
				c = new ArrayList();
				c.add(o);
			}
			return this.executeBatch(c);
		case EXECUTE_QUERY:
			return this.executeQuery(o);
		case EXECUTE_QUERY_OBJECT:
			return this.executeQueryObject(o);
		default:
		}
		return null;
	}
	
	/**
	 * Prepares the statement for execution
	 * @param pstmt the statement to be prepared
	 * @param data the bean for prepartion
	 */
	private void prepare(PreparedStatement pstmt, Object data) {
		if (data == null || pstmt == null) return;
		for (int i=0; i<this.inputParam.size(); i++) {
			Param p = (Param)inputParam.get(i);
			p.prepare(data, pstmt);
		}
	}
	
	/**
	 * Prepares the statement for batch execution
	 * @param pstmt the statement to be prepared
	 * @param data the bean for prepartion
	 */
	private void prepareBatch(PreparedStatement pstmt, Object data) {
		if (data == null || pstmt == null) return;
		this.prepare(pstmt, data);
		try {
			pstmt.addBatch();
		} catch (SQLException sqle){
			if (log.isErrorEnabled()) { log.error(sqle); }
		}
	}
	
	/** 
	 * Populates the results to the data
	 * @param rs the ResultSet from the query
	 * @param data the bean to be populated
	 */
	private void populate(ResultSet rs, Object data) {
		if (data == null) return;
		for (int i=0; i<this.outputParam.size(); i++) {
			Param p = (Param)outputParam.get(i);
			p.populate(data, rs);
		}
	}
	
	/**
	 * Executes update to the database
	 * @param data the data bean for preparing the statement. Could be null.
	 * @return the result of the update
	 */
	public int executeUpdate(Object data) {
		PreparedStatement pstmt = null;
		int res = 0;
		try {
if (log.isDebugEnabled()) { log.debug(sql); }
			pstmt = conn.prepareStatement(sql);
			this.prepare(pstmt, data);
			res = pstmt.executeUpdate();
		} catch (Exception e) {
			if (log.isErrorEnabled()) { log.error(e); }
		} finally {
			DatabaseUtil.closeStatement(pstmt);
			pstmt = null;
			if (this.closeConn) {
				DatabaseUtil.closeConnection(conn);
				conn = null;
			}
			
		}
		return res;
	}

	/**
	 * Exceutes the batch statements to the database
	 * @param data the data for the batch. Must not be null and must be instances fo
	 * @return the result of the batch execution
	 */
	public int[] executeBatch(Collection c) {
		if (c == null) return null;
		PreparedStatement pstmt = null;
		int[] res = null;
		try {
if (log.isDebugEnabled()) { log.debug(sql); }
			pstmt = conn.prepareStatement(this.sql);
			Iterator iter = c.iterator();
			while (iter.hasNext()) {
				Object iterObj = iter.next();
				if (iterObj != null) {
					this.prepareBatch(pstmt, iterObj);
				}
			}
			res = pstmt.executeBatch();
		} catch (Exception e) {
			if (log.isErrorEnabled()) { log.error(e); }
		} finally {
			DatabaseUtil.closeStatement(pstmt);
			pstmt = null;
			if (this.closeConn) {
				DatabaseUtil.closeConnection(conn);
				conn = null;
			}
			
		}
		return res;
	}
	
	/**
	 * Executes a query for a single object
	 * @param data for preparing the statement
	 * @return the object that resulted from the query
	 */
	public Object executeQueryObject(Object data) {
		Object retVal = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
if (log.isDebugEnabled()) { log.debug(sql); }
			pstmt = conn.prepareStatement(sql);
			this.prepare(pstmt, data);
			rs = pstmt.executeQuery();
			if (rs != null && rs.next()) {
				retVal = ClassUtil.newInstance(this.beanType);
				if (retVal != null) {
					this.populate(rs, retVal);
				}
			}
		} catch (Exception e) {
			if (log.isErrorEnabled()) { log.error(e); }
		} finally {
			DatabaseUtil.closeResultSet(rs);
			DatabaseUtil.closeStatement(pstmt);
			rs = null;
			pstmt = null;
			if (this.closeConn) {
				DatabaseUtil.closeConnection(conn);
				conn = null;
			}
		}
		return retVal;
	}
	
	/**
	 * Executes a query for a collection of objects
	 * @param data for preparing the statement
	 * @return a collection of results 
	 */
	public Collection executeQuery(Object data) {
		ArrayList a = new ArrayList();
		Object retVal = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
if (log.isDebugEnabled()) { log.debug(sql); }
			pstmt = conn.prepareStatement(sql);
			this.prepare(pstmt, data);
			rs = pstmt.executeQuery();
			while (rs != null && rs.next()) {
				retVal = ClassUtil.newInstance(this.beanType);
				if (retVal != null) {
					this.populate(rs, retVal);
					a.add(retVal);
				}
			}
		} catch (Exception e) {
			if (log.isErrorEnabled()) { log.error(e); }
		} finally {
			DatabaseUtil.closeResultSet(rs);
			DatabaseUtil.closeStatement(pstmt);
			rs = null;
			pstmt = null;
			if (this.closeConn) {
				DatabaseUtil.closeConnection(conn);
				conn = null;
			}
		}
		return a;
	}
/*	
	public static void main(String[] args) {
		try {
			QueryService s = new QueryService();
			System.out.println(s);
			DataLottery d = new DataLottery();
			d.setDrawId(2120);
			Connection conn = DatabaseUtil.getDatabaseConnection("lottery", null, null);
			s.setConnection(conn);
			s.setMethod(QueryService.EXECUTE_QUERY);
			s.setSql("SELECT * FROM toto WHERE id=? ");
			s.setType("org.latticesoft.lottery.DataLottery");
			s.getInputParam().add(Param.parse("1|id|INTEGER|drawId"));
			s.getOutputParam().add(Param.parse("0|id|INTEGER|drawId"));
			s.getOutputParam().add(Param.parse("0|ddate|TIMESTAMP|drawDate"));
			s.getOutputParam().add(Param.parse("0|d1|INTEGER|toto1"));
			s.getOutputParam().add(Param.parse("0|d2|INTEGER|toto2"));
			s.getOutputParam().add(Param.parse("0|d3|INTEGER|toto3"));
			s.getOutputParam().add(Param.parse("0|d4|INTEGER|toto4"));
			s.getOutputParam().add(Param.parse("0|d5|INTEGER|toto5"));
			s.getOutputParam().add(Param.parse("0|d6|INTEGER|toto6"));
			s.getOutputParam().add(Param.parse("0|d7|INTEGER|toto7"));
			Object o = s.executeQueryObject(d);
			System.out.println(o);
			
			System.out.println("=======================");
			s.setSql("SELECT * FROM toto WHERE id>=? ");
			conn = DatabaseUtil.getDatabaseConnection("lottery", null, null);
			s.setConnection(conn);
			Collection c = s.executeQuery(d);
			if (c != null) {
				Iterator iter = c.iterator();
				while (iter.hasNext()) {
					Object iterObj = iter.next();
					System.out.println(iterObj);
					System.out.println("==========");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}//*/
}

