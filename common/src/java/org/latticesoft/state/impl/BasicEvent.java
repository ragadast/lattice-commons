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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class BasicEvent implements Event, Data, Identity {

	private static final Log log = LogFactory.getLog(BasicEvent.class);
	private PropertyMap p = new PropertyMap();
	private String name;
	private String id;
	
	public BasicEvent(){}
	public BasicEvent(String name) {
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
	/**
	 * Ensures that 2 events have the same key and value.
	 * @see org.latticesoft.state.Event#matches(org.latticesoft.state.Event)
	 */
	public boolean matches(Event event) {
		boolean retVal = false;
		if (event instanceof Identity) {
			Identity that = (Identity)event;
			if (that.getName() != null && this.getName() != null && 
				that.getName().equals(this.getName())) {
				retVal = true;
			}
			if (that.getId() != null && this.getId() != null && 
				that.getId().equals(this.getId())) {
				retVal = true;
			}
		}
if (log.isDebugEnabled()) { log.debug("matching... " + this + " " + event + " " + retVal); }
		return retVal;
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
	 * @see org.latticesoft.util.container.Data#getNumber(java.lang.Object)
	 */
	public Number getNumber(Object key) {
		return p.getNumber(key);
	}
	/* (non-Javadoc)
	 * @see org.latticesoft.util.container.PropertyMap#getMap(java.lang.Object)
	 */
	public Map getMap(Object key) {
		return p.getMap(key);
	}
	/* (non-Javadoc)
	 * @see org.latticesoft.util.container.PropertyMap#getCollection(java.lang.Object)
	 */
	public Collection getCollection(Object key) {
		return p.getCollection(key);
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
	 * @see org.latticesoft.util.container.Data#setNumber(java.lang.Object, java.lang.Number)
	 */
	public void setNumber(Object key, Number n) {
		p.setNumber(key, n);
	}
	/* (non-Javadoc)
	 * @see org.latticesoft.util.container.PropertyMap#setMap(java.lang.Object, java.util.Map)
	 */
	public void setMap(Object key, Map value) {
		p.setMap(key, value);
	}
	/* (non-Javadoc)
	 * @see org.latticesoft.util.container.PropertyMap#setCollection(java.lang.Object, java.util.Collection)
	 */
	public void setCollection(Object key, Collection value) {
		p.setCollection(key, value);
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
	/* (non-Javadoc)
	 * @see org.latticesoft.util.container.PropertyMap#keySet()
	 */
	public Set keySet() {
		return p.keySet();
	}
	/* (non-Javadoc)
	 * @see org.latticesoft.util.container.PropertyMap#size()
	 */
	public int size() {
		return p.size();
	}
	/* (non-Javadoc)
	 * @see org.latticesoft.util.container.PropertyMap#clear()
	 */
	public void clear() {
		p.clear();
	}
	
	/**
	* Converts the class in a string form
	* @returns the class in a string form.
	*/
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("[Event : ");
		sb.append(this.getName());
		sb.append("]");
		return sb.toString();
	}
	
}
