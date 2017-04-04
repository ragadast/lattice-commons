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
 * Created on Sep 16, 2005
 *
 */
package org.latticesoft.state.impl;

import java.util.*;

import org.latticesoft.state.Event;
import org.latticesoft.util.container.*;
import org.latticesoft.util.common.*;

public class EventImpl implements Event, Data, TokenIdentity {

	private PropertyMap p = new PropertyMap();
	private String name;
	private String id;
	private ArrayList keys = new ArrayList();
	private Map keysMap = new HashMap();
	private Map keysInUsed = new HashMap();
	private String separator = "";
	
	public EventImpl(){}
	public EventImpl(String name) {
		this.setName(name);
	}
	
	/* (non-Javadoc)
	 * @see org.latticesoft.util.common.Identity#getId()
	 */
	public String getId() {
		return this.id;
	}
	/* (non-Javadoc)
	 * @see org.latticesoft.util.common.Identity#setId(java.lang.String)
	 */
	public void setId(String id) {
		this.id = id;
	}
	/* (non-Javadoc)
	 * @see org.latticesoft.util.common.Identity#getName()
	 */
	public String getName() {
		return this.name;
	}
	/* (non-Javadoc)
	 * @see org.latticesoft.util.common.Identity#setName(java.lang.String)
	 */
	public void setName(String name) {
		this.name = name;
	}

	
	
	/* (non-Javadoc)
	 * @see org.latticesoft.util.common.TokenIdentity#addKey(java.lang.String)
	 */
	public boolean addKey(String key) {
		if (key == null) {
			return false;
		}
		if (!keysMap.containsKey(key)) {
			keys.add(key);
			int index = keys.size();
			keysMap.put(key, new Integer(index));
			keysInUsed.put(key, Boolean.TRUE);
		}
		return false;
	}
	/* (non-Javadoc)
	 * @see org.latticesoft.util.common.TokenIdentity#getKey()
	 */
	public String getKey(int index) {
		if (index < 0 || index >= this.keys.size()) {
			return null;
		}
		return (String)this.keys.get(index);
	}
	/* (non-Javadoc)
	 * @see org.latticesoft.util.common.TokenIdentity#clear()
	 */
	public void clear() {
		this.keys.clear();
		this.keysMap.clear();
		this.keysInUsed.clear();
	}
	/* (non-Javadoc)
	 * @see org.latticesoft.util.common.TokenIdentity#getToken(java.util.Map)
	 */
	public String getToken(Map map) {
		StringBuffer sb = new StringBuffer();
		for (int i=0; i<keys.size(); i++) {
			String key = (String)keys.get(i);
			String value = (String)map.get(key);
			if (key != null && value != null) {
				sb.append(value).append(separator);
			}
		}
		if (sb.length() > separator.length()) {
			for (int i=0; i<separator.length(); i++) {
				sb.deleteCharAt(sb.length());
			}
		}
		return null;
	}
	/* (non-Javadoc)
	 * @see org.latticesoft.util.common.TokenIdentity#getTokenFormat()
	 */
	public String getTokenFormat() {
		return this.separator;
	}
	/* (non-Javadoc)
	 * @see org.latticesoft.util.common.TokenIdentity#setTokenFormat(java.lang.String)
	 */
	public void setTokenFormat(String format) {
		if (format != null) {
			this.separator = format;
		}
	}
	/* (non-Javadoc)
	 * @see org.latticesoft.util.common.TokenIdentity#removeKey(int)
	 */
	public boolean removeKey(int index) {
		if (index < 0 && index >= keys.size()) {
			return false;
		}
		String key = (String)keys.get(index);
		if (key != null && keysInUsed.containsKey(key)) {
			keysInUsed.put(key, Boolean.FALSE);
			return true;
		}
		return false;
	}
	/* (non-Javadoc)
	 * @see org.latticesoft.util.common.TokenIdentity#removeKey(java.lang.String)
	 */
	public boolean removeKey(String key) {
		if (key != null && keysInUsed.containsKey(key)) {
			keysInUsed.put(key, Boolean.FALSE);
			return true;
		}
		return false;
	}
	/* (non-Javadoc)
	 * @see org.latticesoft.util.common.TokenIdentity#size()
	 */
	public int size() {
		return keys.size();
	}
	/* (non-Javadoc)
	 * @see org.latticesoft.util.common.TokenIdentity#keySet()
	 */
	public Set keySet() {
		Set set = new HashSet();
		return set;
	}
	/**
	 * Ensures that 2 events have the same key and value.
	 * @see org.latticesoft.state.Event#matches(org.latticesoft.state.Event)
	 */
	public boolean matches(Event that) {
		if (this.size() == 0) {
			if (that instanceof TokenIdentity) {
				TokenIdentity thatToken = (TokenIdentity)that ;
				if (this.getName() != null && thatToken.getName() != null) {
					return this.getName().equals(thatToken.getName());
				} else if (this.getId() != null && thatToken.getId() != null){
					return this.getId().equals(thatToken.getId());
				}
			}
		}
		
		StringBuffer sbThis = new StringBuffer();
		StringBuffer sbThat = new StringBuffer();
		Iterator iter = this.keySet().iterator();
		while (iter.hasNext()) {
			Object key = iter.next();
			Object value = this.get(key);
			sbThis.append(value);
			value = that.get(key);
			sbThat.append(value);
		}
		return sbThis.toString().equals(sbThat.toString());
	}
	/**
	 * Ensures that 2 events have the same keys 
	 */
	public boolean equals(Object o) {
		if (!(o instanceof Event)) {
			return false;
		}
		Event that = (Event)o;
		if (this.size() != that.size()) {
			return false;
		}
		Set s1 = this.keySet();
		Set s2 = that.keySet();
		if (s1 != null && s2 != null && s1.equals(s2)) {
			return true;
		}
		return false;
	}
	
	
	
	
	

	
	
	
	
	
	
	
	
	/* (non-Javadoc)
	 * @see org.latticesoft.util.container.PropertyMap#getBoolean(java.lang.Object)
	 */
	public boolean getBoolean(Object key) {
		return p.getBoolean(key);
	}
	/* (non-Javadoc)
	 * @see org.latticesoft.util.container.PropertyMap#getByte(java.lang.Object)
	 */
	public byte getByte(Object key) {
		return p.getByte(key);
	}
	/* (non-Javadoc)
	 * @see org.latticesoft.util.container.PropertyMap#getCollection(java.lang.Object)
	 */
	public Collection getCollection(Object key) {
		return p.getCollection(key);
	}
	/* (non-Javadoc)
	 * @see org.latticesoft.util.container.PropertyMap#getDate(java.lang.Object, java.lang.String)
	 */
	public Date getDate(Object key, String defaultFormat) {
		return p.getDate(key, defaultFormat);
	}
	/* (non-Javadoc)
	 * @see org.latticesoft.util.container.PropertyMap#getDate(java.lang.Object)
	 */
	public Date getDate(Object key) {
		return p.getDate(key);
	}
	/* (non-Javadoc)
	 * @see org.latticesoft.util.container.PropertyMap#getDouble(java.lang.Object)
	 */
	public double getDouble(Object key) {
		return p.getDouble(key);
	}
	/* (non-Javadoc)
	 * @see org.latticesoft.util.container.PropertyMap#getFloat(java.lang.Object)
	 */
	public float getFloat(Object key) {
		return p.getFloat(key);
	}
	/* (non-Javadoc)
	 * @see org.latticesoft.util.container.PropertyMap#getInt(java.lang.Object)
	 */
	public int getInt(Object key) {
		return p.getInt(key);
	}
	/* (non-Javadoc)
	 * @see org.latticesoft.util.container.PropertyMap#getLong(java.lang.Object)
	 */
	public long getLong(Object key) {
		return p.getLong(key);
	}
	/* (non-Javadoc)
	 * @see org.latticesoft.util.container.PropertyMap#getMap(java.lang.Object)
	 */
	public Map getMap(Object key) {
		return p.getMap(key);
	}
	/* (non-Javadoc)
	 * @see org.latticesoft.util.container.PropertyMap#getObject(java.lang.Object)
	 */
	public Object getObject(Object key) {
		return p.getObject(key);
	}
	/* (non-Javadoc)
	 * @see org.latticesoft.util.container.PropertyMap#getShort(java.lang.Object)
	 */
	public short getShort(Object key) {
		return p.getShort(key);
	}
	/* (non-Javadoc)
	 * @see org.latticesoft.util.container.PropertyMap#getString(java.lang.Object)
	 */
	public String getString(Object key) {
		return p.getString(key);
	}
	/* (non-Javadoc)
	 * @see org.latticesoft.util.container.PropertyMap#setBoolean(java.lang.Object, boolean)
	 */
	public void setBoolean(Object key, boolean value) {
		p.setBoolean(key, value);
	}
	/* (non-Javadoc)
	 * @see org.latticesoft.util.container.PropertyMap#setByte(java.lang.Object, byte)
	 */
	public void setByte(Object key, byte value) {
		p.setByte(key, value);
	}
	/* (non-Javadoc)
	 * @see org.latticesoft.util.container.PropertyMap#setCollection(java.lang.Object, java.util.Collection)
	 */
	public void setCollection(Object key, Collection value) {
		p.setCollection(key, value);
	}
	/* (non-Javadoc)
	 * @see org.latticesoft.util.container.PropertyMap#setDate(java.lang.Object, java.util.Date)
	 */
	public void setDate(Object key, Date date) {
		p.setDate(key, date);
	}
	/* (non-Javadoc)
	 * @see org.latticesoft.util.container.PropertyMap#setDouble(java.lang.Object, double)
	 */
	public void setDouble(Object key, double value) {
		p.setDouble(key, value);
	}
	/* (non-Javadoc)
	 * @see org.latticesoft.util.container.PropertyMap#setFloat(java.lang.Object, float)
	 */
	public void setFloat(Object key, float value) {
		p.setFloat(key, value);
	}
	/* (non-Javadoc)
	 * @see org.latticesoft.util.container.PropertyMap#setInt(java.lang.Object, int)
	 */
	public void setInt(Object key, int value) {
		p.setInt(key, value);
	}
	/* (non-Javadoc)
	 * @see org.latticesoft.util.container.PropertyMap#setLong(java.lang.Object, long)
	 */
	public void setLong(Object key, long value) {
		p.setLong(key, value);
	}
	/* (non-Javadoc)
	 * @see org.latticesoft.util.container.PropertyMap#setMap(java.lang.Object, java.util.Map)
	 */
	public void setMap(Object key, Map value) {
		p.setMap(key, value);
	}
	/* (non-Javadoc)
	 * @see org.latticesoft.util.container.PropertyMap#setObject(java.lang.Object, java.lang.Object)
	 */
	public void setObject(Object key, Object value) {
		p.setObject(key, value);
	}
	/* (non-Javadoc)
	 * @see org.latticesoft.util.container.PropertyMap#setShort(java.lang.Object, short)
	 */
	public void setShort(Object key, short value) {
		p.setShort(key, value);
	}
	/* (non-Javadoc)
	 * @see org.latticesoft.util.container.PropertyMap#setString(java.lang.Object, java.lang.String)
	 */
	public void setString(Object key, String value) {
		p.setString(key, value);
	}
	/* (non-Javadoc)
	 * @see org.latticesoft.util.container.Data#getNumber(java.lang.Object)
	 */
	public Number getNumber(Object key) {
		return p.getNumber(key);
	}
	/* (non-Javadoc)
	 * @see org.latticesoft.util.container.Data#setNumber(java.lang.Object, java.lang.Number)
	 */
	public void setNumber(Object key, Number value) {
		p.setNumber(key, value);
	}
	/* (non-Javadoc)
	 * @see org.latticesoft.util.container.PropertyMap#containsKey(java.lang.Object)
	 */
	public boolean containsKey(Object key) {
		return p.containsKey(key);
	}
	/* (non-Javadoc)
	 * @see org.latticesoft.util.container.PropertyMap#containsValue(java.lang.Object)
	 */
	public boolean containsValue(Object value) {
		return p.containsValue(value);
	}
	/* (non-Javadoc)
	 * @see org.latticesoft.util.container.PropertyMap#entrySet()
	 */
	public Set entrySet() {
		return p.entrySet();
	}
	/* (non-Javadoc)
	 * @see org.latticesoft.util.container.PropertyMap#get(java.lang.Object)
	 */
	public Object get(Object key) {
		return p.get(key);
	}
	/* (non-Javadoc)
	 * @see org.latticesoft.util.container.PropertyMap#isEmpty()
	 */
	public boolean isEmpty() {
		return p.isEmpty();
	}
	/* (non-Javadoc)
	 * @see org.latticesoft.util.container.PropertyMap#put(java.lang.Object, java.lang.Object)
	 */
	public Object put(Object key, Object value) {
		return p.put(key, value);
	}
	/* (non-Javadoc)
	 * @see org.latticesoft.util.container.PropertyMap#putAll(java.util.Map)
	 */
	public void putAll(Map t) {
		p.putAll(t);
	}
	/* (non-Javadoc)
	 * @see org.latticesoft.util.container.PropertyMap#values()
	 */
	public Collection values() {
		return p.values();
	}
	/* (non-Javadoc)
	 * @see org.latticesoft.util.container.PropertyMap#remove(java.lang.Object)
	 */
	public Object remove(Object key) {
		return p.remove(key);
	}
	
	/**
	 * Converts the class in a string form
	 * @returns the class in a string form.
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("[Event:").append(this.name).append("]");
		return sb.toString();
	}
	
}
