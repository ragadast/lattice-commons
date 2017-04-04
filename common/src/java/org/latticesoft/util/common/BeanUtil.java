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
 * Created on Jun 3, 2005
 *
 */
package org.latticesoft.util.common;

import org.apache.commons.beanutils.*;
import java.util.*;


/**
 * @author clgoh
 */
public final class BeanUtil {

	public class Attribute {
		private String name;
		private String type;
		public String getName() { return this.name; }
		public void setName(String name) { this.name = name; }
		public String getType() { return this.type; }
		public void setType(String type) { this.type = type; }
	}
	
	/** Private constructor. This class mainly used for static invocation only */
	private BeanUtil() {}
	
	public static DynaProperty[] createProperties(String fileName, String rule) {
		Object o = MiscUtil.buildObjectFromXml(fileName, rule);
		ArrayList a = new ArrayList();
		if (o != null && o instanceof Collection) {
			Iterator iter = ((Collection)o).iterator();
			while (iter.hasNext()) {
				Attribute attribute = (BeanUtil.Attribute)iter.next();
				try {
					Class clazz = Class.forName(attribute.getType());
					DynaProperty d = new DynaProperty(attribute.getName(), clazz);
					if (d != null) {
						a.add(d);
					}
				} catch (ClassNotFoundException cnfe) {
					
				}
			}
		}
		DynaProperty[] dd = new DynaProperty[a.size()];
		return (DynaProperty[])a.toArray(dd);
	}
	
	public static DynaClass createDynaClass(String name, Class c, DynaProperty[] p) {
		return new BasicDynaClass(name, c, p);
	}

	public static DynaClass createDynaClass(String name, Class c) {
		return new BasicDynaClass(name, c);
	}
	
	public static Object getAttribute(Object bean, String attribute) {
		Object retVal = null;
		if (bean == null || attribute == null) return null;
		if (bean instanceof Map) {
			Map map = (Map)bean;
			retVal = map.get(attribute);
		} else if (bean instanceof WrapDynaBean){
			WrapDynaBean w = (WrapDynaBean)bean;
			retVal = w.get(attribute);
		} else {
			WrapDynaBean w = new WrapDynaBean(bean);
			retVal = w.get(attribute);
		}
		return retVal;
	}
	
	public static void setAttribute(Object bean, String attribute, Object value) {
		if (bean == null || attribute == null || value == null) return;
		if (bean instanceof Map) {
			Map map = (Map)bean;
			map.put(attribute, value);
		} else if (bean instanceof WrapDynaBean){
			WrapDynaBean w = (WrapDynaBean)bean;
			w.set(attribute, value);
		} else {
			WrapDynaBean w = new WrapDynaBean(bean);
			w.set(attribute, value);
		}
	}

	public static void main(String[] args) {
	}
}