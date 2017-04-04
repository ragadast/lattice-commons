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

import java.util.*;
import java.sql.*;
import javax.sql.*;
import org.latticesoft.command.*;
import org.latticesoft.util.common.*;
import org.latticesoft.util.container.*;
import org.latticesoft.util.resource.DatabaseUtil;
import org.apache.commons.beanutils.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class GenericDAO implements Collection {
	
	private static Map map = new HashMap();
	private static final Log log = LogFactory.getLog(GenericDAO.class);
	private boolean closeConnection = true;
	private DataSource dataSource = null;
	private Map services = new OrderedMap();
	
	public synchronized static GenericDAO getInstance(Object key) {
		GenericDAO dao = null;
		if (map.containsKey(key)) {
			Object o = map.get(key);
			if (o instanceof GenericDAO) {
				dao = (GenericDAO)o;
			}
		}
		return dao;
	}
	public synchronized static void setInstance(Object key, GenericDAO dao) {
		if (key != null && dao != null) {
			if (map.containsKey(key)) {
				map.remove(key);
			}
			map.put(key, dao);
		}
	}
	public static void loadInstance(Object key, String rules, String xml) {
		Object o = MiscUtil.buildObjectFromXml(rules, xml);
		if (o != null && o instanceof GenericDAO) {
			GenericDAO dao = (GenericDAO)o;
			setInstance(key, dao);
		}
	}
	
	public GenericDAO() {
	}

	/** @return Returns the closeConnection. */
	public boolean isCloseConnection() { return (this.closeConnection); }
	/** @param closeConnection The closeConnection to set. */
	public void setCloseConnection(boolean b) { this.closeConnection = b; }
	/** @return Returns the dataSource. */
	public DataSource getDataSource() { return (this.dataSource); }
	/** @param dataSource The dataSource to set. */
	public void setDataSource(DataSource dataSource) { this.dataSource = dataSource; }
	/** @return Returns the services. */
	public Map getServices() { return (this.services); }

	public Object executeService(Object key, Object data) {
		Object retVal = null;
		Object o = services.get(key);
		Connection conn = null;
		if (this.dataSource == null) {
if (log.isErrorEnabled()) { log.error("Null datasource!"); }
			return null;
		}
		if (o == null) {
if (log.isErrorEnabled()) { log.error("No service found"); }
		}
		try {
			conn = this.dataSource.getConnection();
			if (o instanceof Service) {
				Service svc = (Service)o;
				svc.setCloseConnection(false);
				svc.setConnection(conn);
				retVal = svc.execute(data);
				svc.setCloseConnection(false);
				svc.setConnection(null);
			} else {
				Map map = new HashMap();
				map.put("closeConnection", "false");
				map.put("connection", conn);
				BeanUtils.populate(o, map);
				if (o instanceof Command) {
					Command cmd = (Command)o;
					retVal = cmd.execute(data);
				}
				map.put("connection", "");
				BeanUtils.populate(o, map);
			}
		} catch (Exception e) {
			if (log.isErrorEnabled()) { log.error(e); }
		} finally {
			if (this.closeConnection) {
				DatabaseUtil.closeConnection(conn);
			}
		}
		return retVal;
	}
	
	public boolean add(Object o) {
		if (o != null && o instanceof Service) {
			Service s = (Service)o;
			if (!this.services.containsKey(s.getName())) {
				this.services.put(s.getName(), s);
				return true;
			}
		}
		return false;
	}

	public boolean addAll(Collection arg0) {
		return false;
	}
	public void clear() {
		this.services.clear();
	}
	public boolean contains(Object o) {
		return this.services.containsValue(o);
	}
	public boolean containsAll(Collection c) {
		return this.services.values().containsAll(c);
	}
	public boolean isEmpty() {
		return this.services.isEmpty();
	}
	public Iterator iterator() {
		return this.services.values().iterator();
	}
	public boolean remove(Object o) {
		return this.services.values().remove(o);
	}
	public boolean removeAll(Collection c) {
		return this.services.values().removeAll(c);
	}
	public boolean retainAll(Collection c) {
		return this.services.values().retainAll(c);
	}
	public int size() {
		return this.services.size();
	}
	public Object[] toArray() {
		return this.services.values().toArray();
	}
	public Object[] toArray(Object[] o) {
		return this.services.values().toArray(o);
	}
	/*
	public static void main(String[] args) {
		GenericDAO.loadInstance("mydao", "resource/daorules.xml", "resource/dao.xml");
		GenericDAO dao = GenericDAO.getInstance("mydao");
		org.latticesoft.lottery.DataLottery d = new org.latticesoft.lottery.DataLottery();
		d.setDrawId(2122);
		d.setToto1(2020);
		d.setToto2(2022);
		Object o = dao.executeService("findToto", d);
		System.out.println(o);
		o = dao.executeService("findTotoBetween", d);
		System.out.println(o);
	}//*/

}
