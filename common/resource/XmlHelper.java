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
 * Created on Oct 31, 2006
 *
 */
package org.latticesoft.util.xml;

import java.io.Serializable;
import java.util.*;
import org.apache.commons.beanutils.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class XmlHelper implements Serializable {
	public static final long serialVersionUID = 200610311115215L;
	private static final Log log = LogFactory.getLog(XmlHelper.class);
	
	private String id;
	private String refId;
	private Object object;
	private String method;
	private boolean isRef = false;
	static XmlMain main;
	
	
	private List childList = new ArrayList();
	private Map childMap = new HashMap();
	private XmlHelper parent;
	
	/** @return Returns the object. */
	public Object getObject() {
		Object retVal = null;
		if (retVal == null && refId != null) {
			// get from main
			if (main != null) {
				retVal = main.getObject(this.refId);
			}
		} 
		if (retVal == null && id != null) {
			retVal = this.object;
		}
		return retVal;
	}
	
	/** @param object The object to set. */
	public void setObject(Object object) { this.object = object; }
	/** @return Returns the id. */
	public String getId() { return (this.id); }
	/** @param id The id to set. */
	public void setId(String id) { this.id = id; }
	/** @return Returns the refId. */
	public String getRefId() { return (this.refId); }
	/** @param refId The refId to set. */
	public void setRefId(String refId) { this.refId = refId; }
	/** @return Returns the method. */
	public String getMethod() { return (this.method); }
	/** @param method The method to set. */
	public void setMethod(String method) { this.method = method; }
	/** @return Returns the parent. */
	public XmlHelper getParent() { return (this.parent); }
	/** @param parent The parent to set. */
	public void setParent(XmlHelper parent) { this.parent = parent; }
	/** @return Returns the isReference. */
	public boolean isRef() { return (this.isRef); }
	/** @param isReference The isReference to set. */
	public void setRef(boolean isRef) { this.isRef = isRef; }


	public boolean add(Object o) {
		if (o instanceof XmlHelper) {
			XmlHelper child = (XmlHelper)o;
			if (child.getId() != null) {
				this.childMap.put(id, child);
			}
			child.setParent(this);
			this.childList.add(o);
		} else {
			this.object = o;
		}
		return true;
	}
	
	public List getChildList() {
		return this.childList;
	}
	public Map getChildMap() {
		return this.childMap;
	}
	
	public void clear() {
		this.childMap.clear();
		for (int i=0; i<this.childList.size(); i++) {
			XmlHelper child = (XmlHelper)childList.get(i);
			child.clear();
		}
		this.childList.clear();
	}
	
	void construct() {
		for (int i=0; i<childList.size(); i++) {
			XmlHelper child = (XmlHelper)childList.get(i);
			child.construct();
		}
		if (parent != null) {
			Object arg = this.getObject();
			String mtd = this.getMethod();
			Object obj = parent.getObject();
			try {
				MethodUtils.invokeMethod(obj, mtd, arg);
			} catch (Exception e) {
				if (log.isErrorEnabled()) { log.error(e); }
			}
		}
	}
	/**
	 * Converts the class in a string form
	 * @returns the class in a string form.
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("<xmlHelper ");
		sb.append("id=\"").append(this.getId()).append("\" ");
		sb.append("refId=\"").append(this.getRefId()).append("\" ");
		sb.append("mtd=\"").append(this.getMethod()).append("\" ");
		if (this.childList.size() == 0) {
			sb.append("/>");
		} else {
			sb.append(">");
			for (int i=0; i<this.childList.size(); i++) {
				XmlHelper child = (XmlHelper)this.childList.get(i);
				sb.append("\n");
				sb.append(child.toString());
			}
			sb.append("\n");
			sb.append("</xmlHelper>");
		}
		
		return sb.toString();
	}
}
