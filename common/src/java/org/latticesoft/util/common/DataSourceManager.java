package org.latticesoft.util.common;

import sun.jdbc.odbc.ee.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import org.latticesoft.util.container.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.beanutils.BeanUtils;

public class DataSourceManager {

	private final Log log = LogFactory.getLog(getClass());
	private String name;
	private PropertyMap pm = new PropertyMap();
	private final static String PREFIX = "DataSourceManager";

	public void setName(String name) { this.name = name; }
	public String getName() { return (this.name); }
	public Map getMap() { return (this.pm); }

	private String propName(String s) {
		StringBuffer sb = new StringBuffer();
		sb.append(PREFIX);
		sb.append(".");
		sb.append(s);
		return sb.toString();
	}
	private String propName(String s, int index) {
		StringBuffer sb = new StringBuffer();
		sb.append(PREFIX);
		sb.append(".");
		sb.append(s);
		sb.append(".");
		sb.append(index);
		return sb.toString();
	}

	public void init() {
		pm.setName(name);
		pm.read();

		int count = pm.getInt(propName("count"));

		for (int i=1; i<=count; i++) {
			try {
				Map submap = MiscUtil.getSubMap(this.propName("datasource", i), pm);

				if (submap == null) {
					continue;
				}
				String key = (String)submap.get("key");
				DataSource ds = new DataSource();
if (log.isDebugEnabled()) { log.debug(submap); }
				BeanUtils.populate(ds, submap);
				if (key != null) {
					pm.put(key, ds);
				}
if (log.isDebugEnabled()) {
	log.debug("==========");
	log.debug("dbName: " + ds.getDatabaseName());
	log.debug("dsName: " + ds.getDataSourceName());
	log.debug("user  : " + ds.getUser());
	log.debug("passwd: " + ds.getPassword());
	log.debug("timeout: " + ds.getLoginTimeout());
	log.debug("charst: " + ds.getCharSet());
	log.debug("port  : " + ds.getPortNumber());
	log.debug("role  : " + ds.getRoleName());
}
			} catch (Exception e) {
				if (log.isErrorEnabled()) { log.error(e); }
			}
		}
	}

	public Connection getConnection(String key) {
		if (key == null) { return null; }
		if (!pm.containsKey(key)) { return null; }
		Connection conn = null;
		try {
			DataSource ds = (DataSource)pm.get(key);
			conn = ds.getConnection();
		} catch (SQLException e) {
			if (log.isErrorEnabled()) { log.error(e); }
			conn = null;
		}
		return conn;
	}

	public static void main(String[] args) {
		DataSourceManager dm = new DataSourceManager();
		dm.setName("D:/Temp/haha.properties");
		dm.init();
		System.out.println (dm.getConnection("stock"));
		System.out.println (dm.getConnection("acl"));
		try {
			DataSource ds = new DataSource();
			ds.setDataSourceName("acldb");
			ds.setLoginTimeout(100);
			System.out.println (ds.getConnection());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}