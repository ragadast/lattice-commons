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
 * Created on Nov 21, 2007
 *
 */
package org.latticesoft.util.container;

import java.util.*;
import java.io.Serializable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.latticesoft.util.common.*;

/**
 * <p>
 * MapKey is a class for comparing multiple keys regardless of position.
 * An useful application may be in comparing URL parameters. 
 * A MapKey object containing less keys / parameters will match a MapKey
 * that contains a superset of the previous.
 * E.g.
 * <code>
 * MapKey b1 = new MapKey();
 * MapKey b2 = new MapKey();
 * b1.getMap().put("url", "vrl/action/ViewPrintOnline");
 * b1.getMap().put("FUNCTION_ID", "F2008001ET");
 * b2.getMap().put("url", "vrl/action/ViewPrintOnline");
 * b2.getMap().put("FUNCTION_ID", "F2008001ET");
 * b2.getMap().put("dispatch", "displayDetails");
 * System.out.println("Less equals more : (true) " + b1.equals(b2));
 * System.out.println("More equals less : (false)" + b2.equals(b1));
 * </code>
 * </p>
 */
public class MapKey implements Comparable, Serializable {
	private static final Log log = LogFactory.getLog(MapKey.class);

	private Map map = new HashMap();
	private String groupId = null;
	public static final long serialVersionUID = 20070404144830L;
	
	public MapKey() {}
	public MapKey(Map m) {
		super();
		this.setMap(m);
		if (log.isDebugEnabled()) { log.debug("Constructor done."); }
	}
	/** @return Returns the name. */
	public String getGroupId() { return (this.groupId); }
	/** @param name The name to set. */
	public void setGroupId(String name) { this.groupId = name; }
	
	/** @return Returns the param. */
	public Map getMap() { return (this.map); }
	/** @param param The param to set. */
	public void setMap(Map m) {
		if (map != null) {
			this.map.clear();
			this.map.putAll(m);
		}
	}
	/** @return Returns the urlParam as a string */
	public String getMapAsString() { 
		String s = StringUtil.formatMap(this.map, ";", false);
		return s;
	}
	/** @param urlParam The urlParam to set from a string */
	public void setMapFromString(String s) {
		if (s == null) {
			return;
		}
		Map m = StringUtil.parseMap(s, ";");
		if (m != null) {
			this.map.putAll(m);
		}
	}
	
	public void add(String s) {
		this.addParam(s);
	}
	
	public void addParam(String s) {
		if (s == null) {
			return;
		}
		Collection c = StringUtil.tokenize(s, "=");
		String key = null;
		String value = null;
		try {
			Iterator iter = c.iterator();
			if (iter.hasNext()) { 
				key = (String)iter.next();
			}
			if (iter.hasNext()) {
				value = (String)iter.next();
			}
			if (key != null && value != null) {
				this.map.put(key, value);
			}
		} catch (Exception e) {
			log.info("Error in adding param.[" + key + "] [" + value + "]", e);
		}
	}

	public int hashCode() {
		StringBuffer sb = new StringBuffer();
		sb.append(this.getMapAsString());
		return sb.toString().hashCode();
	}
	
	public boolean equals(Object o) {
		if (!(o instanceof MapKey)) {
			return false;
		}
		MapKey that = (MapKey)o;
		// now service method the same we check the rest of the param
		Set thisKeySet = this.getMap().keySet();
		Set thatKeySet = that.getMap().keySet();
		
		if (thisKeySet != null && thatKeySet == null) {
			return false;
		}
		if (thisKeySet == null && thatKeySet != null) {
			return false;
		}
		if (thisKeySet == null && thatKeySet == null) {
			// we return true if both null
			return true;
		}
		// keyset not null
		if (thisKeySet.isEmpty() && thatKeySet.isEmpty()) {
			// we return true if both empty
			return true;
		}
		/*
		// check contains all they must both have the same number of keys
		if (!(thisKeySet.containsAll(thatKeySet))) {
			return false;
		}
		// check contains all the other direction
		if (!(thatKeySet.containsAll(thisKeySet))) {
			return false;
		}//*/
		
		// now loop thru the key set
		// compare individual key
		Iterator iter = thisKeySet.iterator();
		boolean temp = true;
		while (iter.hasNext()) {
			Object key = iter.next();
			Object thisValue = this.getMap().get(key);
			Object thatValue = that.getMap().get(key);
			if (thisValue == null && thatValue != null) {
				temp &= false;
				break;
			}
			if (thisValue != null && thatValue == null) {
				temp &= false;
				break;
			}
			if (thisValue == null && thatValue == null) {
				temp &= true;
			} else if (thisValue.equals(thatValue)) {
				temp &= true;
			} else {
				temp &= false;
				break;
			}
		}
		return temp;
	}

	public int compareTo(Object o) {
		if (!(o instanceof MapKey)) {
			return 0;
		}
		MapKey that = (MapKey)o;
		StringBuffer sb1 = new StringBuffer();
		StringBuffer sb2 = new StringBuffer();
		
		// sort the key in order
		ArrayList a = new ArrayList();
		Iterator iter = null;
		a.clear();
		a.addAll(this.getMap().keySet());
		Collections.sort(a);
		iter = a.iterator();
		sb1.append("{");
		while (iter.hasNext()) {
			Object key = iter.next();
			Object value = this.getMap().get(key);
			sb1.append(key).append("=").append(value).append(",");
		}
		if (sb1.charAt(sb1.length()-1) == ',') {
			sb1.deleteCharAt(sb1.length()-1);
		}
		sb1.append("}");
		
		a.clear();
		a.addAll(that.getMap().keySet());
		Collections.sort(a);
		iter = a.iterator();
		sb2.append("{");
		while (iter.hasNext()) {
			Object key = iter.next();
			Object value = that.getMap().get(key);
			sb2.append(key).append("=").append(value).append(",");
		}
		if (sb2.charAt(sb2.length()-1) == ',') {
			sb2.deleteCharAt(sb2.length()-1);
		}
		sb2.append("}");
		
		//if (log.isInfoEnabled()) { log.info(sb1); }
		//if (log.isInfoEnabled()) { log.info(sb2); }
		return sb1.toString().compareTo(sb2.toString());
	}
	
	/**
	 * Converts the class in a string form
	 * @returns the class in a string form.
	 */
	public String toString() {
		return this.getMapAsString();
	}
	
	public static void main(String[] args) {
		MapKey b1 = new MapKey();
		MapKey b2 = new MapKey();
		System.out.println("default null: " + b1.equals(b2));
		/*
		// param
		System.out.println("============================================");
		b1.getMap().put("key1", "value1");
		b1.getMap().put("key2", "value2");
		b2.getMap().putAll(b1.getMap());
		System.out.println("b1: " + b1);
		System.out.println("b2: " + b2);
		System.out.println(b1.compareTo(b2));
		System.out.println("Same param: " + b1.equals(b2));
		
		System.out.println("============================================");		
		b2.getMap().remove("key1");
		b2.getMap().put("key1", "value2");
		System.out.println("b1: " + b1);
		System.out.println("b2: " + b2);
		System.out.println(b1.compareTo(b2));
		System.out.println("Diff param same key diff value: " + b1.equals(b2));
		
		System.out.println("============================================");
		b2.getMap().clear();
		b2.getMap().put("key3", "value3");
		System.out.println("b1: " + b1);
		System.out.println("b2: " + b2);
		System.out.println(b1.compareTo(b2));
		System.out.println("Diff param diff key diff value: " + b1.equals(b2));//*/
		
		//http://VRLDWAS01/vrl/action/ViewPrintOnline
		//FUNCTION_ID=F2008001ET;dispatch=displayDetails
		b1.getMap().put("url", "vrl/action/ViewPrintOnline");
		b1.getMap().put("FUNCTION_ID", "F2008001ET");
		
		b2.getMap().put("url", "vrl/action/ViewPrintOnline");
		b2.getMap().put("FUNCTION_ID", "F2008001ET");
		b2.getMap().put("dispatch", "displayDetails");
		
		System.out.println("Less equals more : " + b1.equals(b2) + " : " + b1 + " | " + b2);
		System.out.println("More equals less : " + b2.equals(b1) + " : " + b2 + " | " + b1);
	}
}


