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
import org.latticesoft.command.*;
import org.latticesoft.util.common.*;
import org.apache.commons.beanutils.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class RecursiveService extends QueryService {
	private static final Log log = LogFactory.getLog(RecursiveService.class);
	private ArrayList childs = new ArrayList();

	/** Adds a child service */
	public boolean addService(Service s) {
		if (s != null) {
			return childs.add(s);
		}
		return false;
	}
	/** Adds a child (only service is allowed) */
	public boolean add(Object o) {
		if (o != null && o instanceof Service) {
			return childs.add(o);
		}
		return false;
	}

	public Object execute(Object o) throws CommandException {
		Object retVal = super.execute(o);
		if (this.getType() == Service.QUERY) {
			// when in query mode we passed the queried object to the 
			// child services
			if (retVal == null) return null;
if (log.isDebugEnabled()) { log.debug("RetVal " + retVal); }
			if (retVal instanceof Collection) {
				Collection c = (Collection)retVal;
				Iterator iter = c.iterator();
				while (iter.hasNext()) {
					Object iterObj = iter.next();
					if (iterObj != null) {
						this.processBeanByChildService(iterObj);
					}
				}
			} else {
				this.processBeanByChildService(retVal);
			}
		} else if (this.getType() == Service.UPDATE){
			// when in update mode we passed the original object to the
			// child services
			this.processBeanByChildService(o);
		}
		return retVal;
	}
	
	/** Process the bean by the child services */
	private void processBeanByChildService(Object bean) {
if (log.isDebugEnabled()) { log.debug("Bean " + bean); }
		for (int i=0; i<this.childs.size(); i++) {
			Service child = (Service)this.childs.get(i);
			child.setConnection(this.getConnection());
			child.setCloseConnection(false);
			if (child.getType() == Service.QUERY) {
				Object res = child.execute(bean);
				// for query childs we need to update back to the bean 
				Map map = new HashMap();
				map.put(child.getName(), res);
				try {
					BeanUtils.populate(bean, map);
				} catch (Exception e) {
					if (log.isErrorEnabled()) { log.error(e); }
				}
			} else if (child.getType() == Service.UPDATE) {
				// for update we get the attribute from teh bean and pass to the 
				// child service
				Object data = BeanUtil.getAttribute(bean, child.getName());
				child.execute(data);
			}
			child.setConnection(null);
		}
	}
}

