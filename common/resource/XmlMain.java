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
 * Created on Nov 1, 2006
 *
 */
package org.latticesoft.util.xml;

import java.io.Serializable;
import java.util.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class XmlMain implements Serializable {
	public static final long serialVersionUID = 200610311115230L;
	private static final Log log = LogFactory.getLog(XmlMain.class);

	private Map reference = new HashMap();
	private List childList = new ArrayList();
	private Map childMap = new HashMap();
	
	public XmlMain() {
		XmlHelper.main = this;
	}
	
	Object getObject (Object key) {
		if (key != null) {
			return this.reference.get(key);
		}
		return null;
	}
	
	public boolean add(Object o){
		if (o != null && o instanceof XmlHelper) {
			XmlHelper helper = (XmlHelper)o;
			if (helper.isRef()) {
				this.reference.put(helper.getId(), helper.getObject());
			} else {
				this.childList.add(helper);
				this.childMap.put(helper.getId(), helper);
			}
		}
		return false;
	}
	
	public Object construct(Object key) {
		Object retVal = null;
		XmlHelper helper = (XmlHelper)childMap.get(key);
		helper.construct();
		retVal = helper.getObject();
		return retVal;
	}
	
	/**
	 * Converts the class in a string form
	 * @returns the class in a string form.
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("\n<xmlMain>");
		for (int i=0; i<this.childList.size(); i++) {
			sb.append("\n");
			sb.append(this.childList.get(i));
		}
		sb.append("\n</xmlMain>");
		return sb.toString();
	}
	
}
