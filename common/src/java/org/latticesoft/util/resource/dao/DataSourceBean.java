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
 * Created on Jun 1, 2006
 *
 */
package org.latticesoft.util.resource.dao;

import java.io.*;
import java.sql.*;

import javax.sql.*;
import org.latticesoft.util.resource.DatabaseUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DataSourceBean implements DataSource {

	private static final Log log = LogFactory.getLog(DataSourceBean.class);
	private DataSource dataSource;
	private String user;
	private String password;
	private String url;
	private String driver;
	private String dsn;
	private String jndi;
	private int timeout;
	
	/** @return Returns the dataSource. */
	public DataSource getDataSource() { return (this.dataSource); }
	/** @param dataSource The dataSource to set. */
	public void setDataSource(DataSource dataSource) { this.dataSource = dataSource; }

	/** @return Returns the driver. */
	public String getDriver() { return (this.driver); }
	/** @param driver The driver to set. */
	public void setDriver(String driver) { this.driver = driver; }

	/** @return Returns the dsn. */
	public String getDsn() { return (this.dsn); }
	/** @param dsn The dsn to set. */
	public void setDsn(String dsn) { this.dsn = dsn; }

	/** @return Returns the jndi. */
	public String getJndi() { return (this.jndi); }
	/** @param jndi The jndi to set. */
	public void setJndi(String jndi) { this.jndi = jndi; }

	/** @return Returns the password. */
	public String getPassword() { return (this.password); }
	/** @param password The password to set. */
	public void setPassword(String password) { this.password = password; }

	/** @return Returns the url. */
	public String getUrl() { return (this.url); }
	/** @param url The url to set. */
	public void setUrl(String url) { this.url = url; }

	/** @return Returns the user. */
	public String getUser() { return (this.user); }
	/** @param user The user to set. */
	public void setUser(String user) { this.user = user; }

	public Connection getConnection() throws SQLException {
		Connection conn = null;
		try {
			if (this.dataSource != null) {
				if (log.isDebugEnabled()) { log.debug("Connection using datasource"); }
				conn = this.dataSource.getConnection();
			}
			if (conn == null && this.jndi != null) {
				if (log.isDebugEnabled()) { log.debug("Connection using JNDI: " + jndi); }
				//conn = DatabaseUtil.getDatabaseConnection(jndi);
				conn = DatabaseUtil.getConnectionFromJNDI(jndi);
			}
			if (conn == null && this.dsn != null) {
				if (log.isDebugEnabled()) {
					log.debug("Connection using DSN: " + dsn + ":" + user + ":" + password);
				}
				//conn = DatabaseUtil.getDatabaseConnection(dsn, user, password, timeout);
				conn = DatabaseUtil.getConnectionFromODBC(dsn, user, password, timeout);
			}
			if (conn == null && this.url != null && this.driver != null) {
				if (log.isDebugEnabled()) {
					log.debug("Connection using driver: " + driver + ":" + url + ":" + user + ":" + password);
				}
				//conn = DatabaseUtil.getConnection(driver, url, user, password);
				conn = DatabaseUtil.getConnectionFromJDBC(driver, url, user, password);
			}
		} catch (Exception e) {
			if (log.isErrorEnabled()) { log.error(e); }
		}
		return conn;
	}

	public Connection getConnection(String username, String password) throws SQLException {
		this.setUser(user);
		this.setPassword(password);
		return this.getConnection();
	}

	public int getLoginTimeout() throws SQLException {
		return this.timeout;
	}

	public PrintWriter getLogWriter() throws SQLException {
		return null;
	}

	public void setLoginTimeout(int seconds) throws SQLException {
		this.timeout = seconds;
	}

	public void setLogWriter(PrintWriter out) throws SQLException {
		// no impl
	}
	/* (non-Javadoc)
	 * @see java.sql.Wrapper#isWrapperFor(java.lang.Class)
	 */
	public boolean isWrapperFor(Class arg0) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}
	/* (non-Javadoc)
	 * @see java.sql.Wrapper#unwrap(java.lang.Class)
	 */
	public Object unwrap(Class arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	
}
