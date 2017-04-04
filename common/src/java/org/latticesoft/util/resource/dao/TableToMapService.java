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
 * Created on Jun 13, 2006
 *
 */
package org.latticesoft.util.resource.dao;

import java.sql.*;
import java.util.*;
import org.latticesoft.util.resource.*;
import org.latticesoft.command.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TableToMapService implements Service {

	private static final Log log = LogFactory.getLog(TableToMapService.class);
	private Connection conn = null;
	private boolean closeConnection = false;
	private String key;
	private String value;
	private String sql;
	private String name = null;

	/** @return Returns the name. */
	public String getName() { return (this.name); }
	/** @param name The name to set. */
	public void setName(String name) { this.name = name; }

	public boolean isCloseConnection() { return this.closeConnection; }
	public void setCloseConnection(boolean b) { this.closeConnection = b; }

	public Connection getConnection() { return this.conn; }
	public void setConnection(Connection conn) { this.conn = conn; }
	
	/** @return Returns the key. */
	public String getKey() { return (this.key); }
	/** @param key The key to set. */
	public void setKey(String s) { this.key = s; }
	/** @return Returns the sql. */
	public String getSql() { return (this.sql); }
	/** @param sql The sql to set. */
	public void setSql(String s) { this.sql = s; }
	/** @return Returns the value. */
	public String getValue() { return (this.value); }
	/** @param value The value to set. */
	public void setValue(String s) { this.value = s; }
	
	public int getType() { return Service.QUERY; }

	public Object execute(Object o) throws CommandException {
		Map map = new HashMap();
		if (this.conn == null || this.key == null || 
			this.value == null || this.sql == null) {
			return map;
		}
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = conn.prepareStatement(this.sql);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				String k = rs.getString(this.key);
				String v = rs.getString(this.value);
				if (k != null && v != null) {
					map.put(k, v);
				}
			}
		} catch (Exception e) {
			if (log.isErrorEnabled()) { log.error(e); }
		} finally {
			DatabaseUtil.closeResultSet(rs);
			DatabaseUtil.closeStatement(pstmt);
			if (this.closeConnection) {
				DatabaseUtil.closeConnection(conn);
			}
			rs = null;
			pstmt = null;
			conn = null;
		}
		return map;
	}

	public static void main(String[] args) {
		Connection conn = null;
		try {
			conn = DatabaseUtil.getDatabaseConnection("download", null, null);
			TableToMapService s = new TableToMapService();
			s.setConnection(conn);
			s.setCloseConnection(false);
			s.setSql("SELECT * FROM header");
			s.setKey("key");
			s.setValue("value");
			Object o = s.execute(null);
			System.out.println(o);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DatabaseUtil.closeConnection(conn);
		}
	}
}
