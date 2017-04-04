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
 * Created on May 24, 2005
 *
 */
package org.latticesoft.util.container;

import java.util.*;
import java.io.*;
import org.latticesoft.util.common.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author clgoh
 */
public class VarBean implements Data, List, Serializable {

	private static final long serialVersionUID = 20050524161635L;
	private static final Log log = LogFactory.getLog(VarBean.class);

	protected ArrayList childList = new ArrayList();
	protected HashMap childMap = new HashMap();
	protected PropertyMap p = new PropertyMap();
	protected VarBean parent = null;
	private Object key;
	
	public VarBean() {
		if (log.isDebugEnabled()) { log.debug("VarBean"); }
	}
	/** Constructs the object with a key defined */
	public VarBean(Object key) {
		if (log.isDebugEnabled()) { log.debug("VarBean"); }
		this.setKey(key);
	}

	/**
	 * The key is necessary when u
	 * @return Returns the key. the name of the attribute
	 */
	public Object getKey() {
		return (this.key);
	}

	/**
	 * @param key The key to set.
	 */
	public void setKey(Object key) {
		this.key = key;
	}
	
	/**
	 * @return Returns the parent.
	 */
	public VarBean getParent() {
		return (this.parent);
	}

	/**
	 * @param parent The parent to set.
	 */
	public void setParent(VarBean parent) {
		this.parent = parent;
	}

	/**
	 * Gets the boolean attribute
	 * @param key the name of the attribute
	 * @return the value of the attribute
	 */
	public boolean getBoolean(Object key) {
		Object o = p.getObject(key);
		if (o != null) {
			return p.getBoolean(key);
		}
		if (parent != null) {
			return parent.getBoolean(key);
		}
		return false;
	}

	/**
	 * Gets a byte attribute
	 * @param key the name of the attribute
	 * @return the value of the attribute
	 */
	public byte getByte(Object key) {
		Object o = p.getObject(key);
		if (o != null) {
			return p.getByte(key);
		}
		if (parent != null) {
			return parent.getByte(key);
		}
		return 0;
	}

	/**
	 * Gets a date attribute
	 * @param key the name of the attribute
	 * @return the value of the attribute
	 */
	public Date getDate(Object key) {
		Object o = p.getObject(key);
		if (o != null) {
			return p.getDate(key);
		}
		if (parent != null) {
			return parent.getDate(key);
		}
		return null;
	}

	/**
	 * Gets a date attribute with a default format specified
	 * @param key the name of the attribute
	 * @param defaultFormat the default date format
	 * @return the value of the attribute
	 */
	public Date getDate(Object key, String defaultFormat) {
		Object o = p.getObject(key);
		if (o != null) {
			return p.getDate(key, defaultFormat);
		}
		if (parent != null) {
			return parent.getDate(key, defaultFormat);
		}
		return null;
	}

	/**
	 * Gets a double attribute
	 * @param key the name of the attribute
	 * @return the value of the attribute
	 */
	public double getDouble(Object key) {
		Object o = p.getObject(key);
		if (o != null) {
			return p.getDouble(key);
		}
		if (parent != null) {
			return parent.getDouble(key);
		}
		return 0.0;
	}

	/**
	 * Gets a float attribute
	 * @param key the name of the attribute
	 * @return the value of the attribute
	 */
	public float getFloat(Object key) {
		Object o = p.getObject(key);
		if (o != null) {
			return p.getFloat(key);
		}
		if (parent != null) {
			return parent.getFloat(key);
		}
		return 0.0F;
	}

	/**
	 * Gets a int attribute
	 * @param key the name of the attribute
	 * @return the value of the attribute
	 */
	public int getInt(Object key) {
		Object o = p.getObject(key);
		if (o != null) {
			return p.getInt(key);
		}
		if (parent != null) {
			return parent.getInt(key);
		}
		return 0;
	}

	/**
	 * Gets a long attribute
	 * @param key the name of the attribute
	 * @return the value of the attribute
	 */
	public long getLong(Object key) {
		Object o = p.getObject(key);
		if (o != null) {
			return p.getLong(key);
		}
		if (parent != null) {
			return parent.getLong(key);
		}
		return 0L;
	}

	/**
	 * Gets a short attribute
	 * @param key the name of the attribute
	 * @return the value of the attribute
	 */
	public short getShort(Object key) {
		Object o = p.getObject(key);
		if (o != null) {
			return p.getShort(key);
		}
		if (parent != null) {
			return parent.getShort(key);
		}
		return 0;
	}

	/**
	 * Gets a string attribute
	 * @param key the name of the attribute
	 * @return the value of the attribute
	 */
	public String getString(Object key) {
		Object o = p.getObject(key);
		if (o != null) {
			return p.getString(key);
		}
		if (parent != null) {
			return parent.getString(key);
		}
		return null;
	}

	/**
	 * Gets a object attribute
	 * @param key the name of the attribute
	 * @return the value of the attribute
	 */
	public Object getObject(Object key) {
		Object o = p.getObject(key);
		if (o != null) {
			return p.getObject(key);
		}
		if (parent != null) {
			return parent.getObject(key);
		}
		return null;
	}

	/**
	 * Gets a Map attribute
	 * @param key the name of the attribute
	 * @return the value of the attribute
	 */
	public Map getMap(Object key) {
		Object o = p.getObject(key);
		if (o != null) {
			return p.getMap(key);
		}
		if (parent != null) {
			return parent.getMap(key);
		}
		return null;
	}
	
	/**
	 * Gets a Collection attribute
	 * @param key the name of the attribute
	 * @return the value of the attribute
	 */
	public Collection getCollection(Object key) {
		Object o = p.getObject(key);
		if (o != null) {
			return p.getCollection(key);
		}
		if (parent != null) {
			return parent.getCollection(key);
		}
		return null;
	}
	/** 
	 * Gets an number
	 */
	public Number getNumber(Object key) {
		return p.getNumber(key);
	}

	
	/**
	 * Sets a boolean attribute
	 * @param key the name of the attribute
	 * @param value the value of the attribute
	 */
	public void setBoolean(Object key, boolean value) {
		p.setBoolean(key, value);
	}

	/**
	 * Sets a byte attribute
	 * @param key the name of the attribute
	 * @param value the value of the attribute
	 */
	public void setByte(Object key, byte value) {
		p.setByte(key, value);
	}

	/**
	 * Sets a date attribute
	 * @param key the name of the attribute
	 * @param value the value of the attribute
	 */
	public void setDate(Object key, Date value) {
		p.setDate(key, value);
	}

	/**
	 * Sets a double attribute
	 * @param key the name of the attribute
	 * @param value the value of the attribute
	 */
	public void setDouble(Object key, double value) {
		p.setDouble(key, value);
	}

	/**
	 * Sets a float attribute
	 * @param key the name of the attribute
	 * @param value the value of the attribute
	 */
	public void setFloat(Object key, float value) {
		p.setFloat(key, value);
	}

	/**
	 * Sets a int attribute
	 * @param key the name of the attribute
	 * @param value the value of the attribute
	 */
	public void setInt(Object key, int value) {
		p.setInt(key, value);
	}

	/**
	 * Sets a long attribute
	 * @param key the name of the attribute
	 * @param value the value of the attribute
	 */
	public void setLong(Object key, long value) {
		p.setLong(key, value);
	}

	/**
	 * Sets a short attribute
	 * @param key the name of the attribute
	 * @param value the value of the attribute
	 */
	public void setShort(Object key, short value) {
		p.setShort(key, value);
	}

	/**
	 * Sets a string attribute
	 * @param key the name of the attribute
	 * @param value the value of the attribute
	 */
	public void setString(Object key, String value) {
		p.setString(key, value);
	}

	/**
	 * Sets a object attribute
	 * @param key the name of the attribute
	 * @param value the value of the attribute
	 */
	public void setObject(Object key, Object value) {
		p.setObject(key, value);
	}
	
	/**
	 * Sets a map attribute
	 * @param key the name of the attribute
	 * @param value the value of the attribute
	 */
	public void setMap(Object key, Map value) {
		p.setObject(key, value);
	}
	
	/**
	 * Sets a collection attribute
	 * @param key the name of the attribute
	 * @param value the value of the attribute
	 */
	public void setCollection(Object key, Collection value) {
		p.setObject(key, value);
	}

	/**
	 * Sets an number
	 */
	public void setNumber(Object key, Number value) {
		p.setNumber(key, value);
	}
	/**
	 * Adds a new child 
	 * @param index the index to add the new object
	 * @param o the object to be added
	 * @see List#add()
	 */
	public void add(int index, Object o) {
		if (o instanceof VarBean) {
			VarBean b = (VarBean)o;
			b.setParent(this);
			this.addChild(index, b);
		}
	}

	/**
	 * Adds a new object
	 * @param o the object to be added
	 * @return true if successful
	 */
	public boolean add(Object o) {
		if (o instanceof VarBean) {
			VarBean b = (VarBean)o;
			b.setParent(this);
			return this.addChild(b);
		}
		return false;
	}

	/**
	 * Adds a whole lot of VarBeans to the parent
	 * @param index the index to add the whole lot at
	 * @param c the collection of new VarBeans to be added
	 * @return
	 */
	public boolean addAll(int index, Collection c) {
		Iterator iter = c.iterator();
		boolean retVal = false;
		int i = index;
		while (iter.hasNext()) {
			Object o = iter.next();
			if (o instanceof VarBean) {
				VarBean b = (VarBean)o;
				this.addChild(i++, b);
				retVal = true;
			}
		}
		return retVal;
	}

	/**
	 * Adds a whole lot of VarBeans to the parent
	 * @param c the collection to add
	 * @return true if successful
	 */
	public boolean addAll(Collection c) {
		Iterator iter = c.iterator();
		boolean retVal = false;
		while (iter.hasNext()) {
			Object o = iter.next();
			if (o instanceof VarBean) {
				VarBean b = (VarBean)o;
				this.addChild(b);
				retVal = true;
			}
		}
		return retVal;
	}

	/**
	 * Clear the childs list
	 */
	public void clear() {
		childList.clear();
	}

	/**
	 * Checks if the the child's list contain the object
	 * @param o the object to be checked
	 * @return
	 */
	public boolean contains(Object o) {
		return childList.contains(o);
	}

	/**
	 * Checks if the collection is contained in the current list
	 * @param c the collection to be checked
	 * @return
	 */
	public boolean containsAll(Collection c) {
		return childList.containsAll(c);
	}

	/**
	 * @see List#ensureCapacity
	 * @param cap the capacity
	 */
	public void ensureCapacity(int cap) {
		childList.ensureCapacity(cap);
	}

	/**
	 * Gets the Object at a particular index
	 * @param index the index of the object 
	 * @return
	 */
	public Object get(int index) {
		return childList.get(index);
	}

	/**
	 * Gets the last index of a particular object
	 * @param o the object to be checked
	 * @return the last Index 
	 */
	public int indexOf(Object o) {
		return childList.indexOf(o);
	}

	/**
	 * Checks if the list is empty or not
	 * @return true if so
	 */
	public boolean isEmpty() {
		return childList.isEmpty();
	}

	/**
	 * Returns the iterator to the list
	 * @return the iterator
	 */
	public Iterator iterator() {
		return childList.iterator();
	}

	/**
	 * Gets the last index of the Object
	 * @param o the object to be checked
	 * @return
	 */
	public int lastIndexOf(Object o) {
		return childList.lastIndexOf(o);
	}

	/**
	 * Gets the ListIterator
	 * @return the list iterator
	 */
	public ListIterator listIterator() {
		return childList.listIterator();
	}

	/**
	 * Gets the list iterator at a particular index
	 * @param index the index
	 * @return the list iterator
	 */
	public ListIterator listIterator(int index) {
		return childList.listIterator(index);
	}

	/**
	 * Removes the object at a particular index
	 * @param index the index of the object to be remove
	 * @return the removed object
	 */
	public Object remove(int index) {
		Object retVal = childList.remove(index);
		VarBean b = (VarBean)retVal;
		b.setParent(null);
		return retVal; 
	}

	/**
	 * @param o the object to be remove
	 * @return true if successful
	 */
	public boolean remove(Object o) {
		boolean retVal = false;
		if (o instanceof VarBean && childList.contains(o)) {
			VarBean b = (VarBean)o;
			b.setParent(null);
			retVal = true;
		}
		return retVal;
	}

	/**
	 * @param c the whole collection to be remove from the list
	 * @return true if successful
	 */
	public boolean removeAll(Collection c) {
		return childList.removeAll(c);
	}

	/**
	 * @param c the collection to be retain
	 * @return true if successful
	 */
	public boolean retainAll(Collection c) {
		return childList.retainAll(c);
	}

	/**
	 * Replaces the current object with a new object
	 * @param index at where should the new object replace
	 * @param o the new object to replaced.
	 * @return the replaced object or the input object
	 */
	public Object set(int index, Object o) {
		if (o == null) return o;
		if (index < 0 || index > childList.size()) {
			return o;
		}
		if (o instanceof VarBean) {
			VarBean b = (VarBean)o;
			b.setParent(this);
			return childList.set(index, b);
		}
		return o;
	}
	
	/**
	 * @return the size of the childs
	 */
	public int size() {
		return childList.size();
	}

	/**
	 * Creates a sub list from a starting index (inclusive) to an ending index (exclusive)
	 * @param startIndex
	 * @param endIndex
	 * @return the list created
	 */
	public List subList(int startIndex, int endIndex) {
		return childList.subList(startIndex, endIndex);
	}

	/**
	 * @return the childs converted to array
	 */
	public Object[] toArray() {
		return childList.toArray();
	}

	/**
	 * @param array the created array
	 * @return the childs converted to array
	 */
	public Object[] toArray(Object[] array) {
		return childList.toArray(array);
	}

	/**
	 * Trims the list to size
	 */
	public void trimToSize() {
		childList.trimToSize();
	}

	/**
	 * Adds a child. Make sure the child has a valid key to be added
	 * @param child the child to be added
	 */
	public boolean addChild(VarBean child) {
		Object key = child.getKey();
		if (key == null) return false;
		key = child.getObject(key);
		if (key == null) {
			key = child.getKey();
		}
		child.setParent(this);
		this.childList.add(child);
		this.childMap.put(key, child);
		return true;
	}

	/**
	 * Adds a child. Make sure the child has a valid key to be added
	 * @param index the index to add the child at
	 * @param child the child to be added
	 */
	public boolean addChild(int index, VarBean child) {
		Object key = child.getKey();
		if (key == null) return false;
		key = child.getObject(key);
		if (key == null) {
			key = child.getKey();
		}
		child.setParent(this);
		this.childList.add(index, child);
		this.childMap.put(key, child);
		return true;
	}

	/**
	 * Gets a child at a particular index
	 * @param index the index position of the child bean 
	 */
	public VarBean getChildAt(int index) {
		if (index < 0 || index >= childList.size()) {
			return null;
		}
		return (VarBean)this.childList.get(index); 
	}

	/**
	 * Returns the child bean by a key
	 * @param key the key to retrieve the child with
	 */
	public VarBean getChildByKey(Object key) {
		return (VarBean)this.childMap.get(key);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return toString("VarBean", false, false);
	}

	public String toString(String tagName, boolean haveNewLine, boolean haveTab) {
		StringBuffer sb = new StringBuffer();
		if (haveNewLine) { sb.append("\n"); }
		if (haveTab) {
			ThreadUtil.incrementCount();
			for (int i=0; i<ThreadUtil.getCount(); i++) {
				sb.append("\t");
			}
		}
		Iterator iter = null;
		sb.append("<");
		sb.append(tagName);
		if (this.getKey() != null) {
			sb.append(" key=\"");
			sb.append(StringUtil.encodeXmlString((this.key.toString())));
			sb.append("\"");
		}
		Set set = this.p.keySet();
		iter = set.iterator();
		while (iter.hasNext()) {
			Object key = iter.next();
			Object value = null;
			if (key != null) {
				value = this.p.get(key);
				if (value != null) {
					sb.append(" ").append(key).append("=\"");
					sb.append(value).append("\"");
				}
			}
		}
		if (this.childList.size() == 0) {
			sb.append(" />");
			if (haveTab) {
				ThreadUtil.decrementCount();
			}
			return sb.toString();
		}
		sb.append(">");
		iter = this.childList.iterator();
		while (iter.hasNext()) {
			VarBean b = (VarBean)iter.next();
			sb.append(b.toString(tagName, haveNewLine, haveTab));
		}
		if (haveNewLine) { sb.append("\n"); }
		if (haveTab) {
			for (int i=0; i<ThreadUtil.getCount(); i++) {
				sb.append("\t");
			}
		}
		sb.append("</").append(tagName).append(">");
		if (haveTab) {
			ThreadUtil.decrementCount();
		}
		return sb.toString();
	}

	/** Returns a collection of all the menus in a flatten structure */
	public Collection flatten() {
		return MiscUtil.flatten(this);
	}
	
	/**
	 * Returns a hierarchical list of the parents containing the
	 * current menu. Not that all sibling branches will not be
	 * reflected in the list.
	 * The list contains the actual object and thus the list
	 * not for updating the hierachy within the menu.
	 * @return list of parent menus
	 */
	public final List getHierarchyList() {
		ArrayList a = new ArrayList();
		Set set = new java.util.HashSet();
		VarBean b = this;
		int count = 0;
		while (b != null && count < 1000) {
			if (!set.contains(b) || set.isEmpty()) {
				set.add(b);
				a.add(b);
			} else {
				break;
			}
			b = b.getParent();
			count++;
		}
		java.util.Collections.reverse(a);
		return a;
	}
	
	/** Returns the index of this varbean */
	public int getIndex() {
		int index = 0;
		if (this.parent != null) {
			Iterator iter = parent.iterator();
			while (iter.hasNext()) {
				if (iter.next() == this) break;
				index++;
			}
		}
		return index;
	}

	/** Returns the level of the menu within the hierarchy. */
	public int getHierarchyLevel() { return this.getHierarchyList().size(); }


	/**
	 * Returns the full index including that of the parent(s) separated
	 * by the separator String.
	 * @param separator the separator for the name
	 */
	public String getHierarchyIndex(String separator) {
		if (separator == null) {
			separator = "-";
		}
		List l = this.getHierarchyList();
		StringBuffer sb = new StringBuffer();
		Iterator iter = l.iterator();
		while (iter.hasNext()) {
			VarBean b = (VarBean)iter.next();
			sb.append(b.getIndex());
			sb.append(separator);
		}
		String retVal = "";
		if (sb.length() > separator.length()) {
			retVal = sb.substring(0, sb.length()-separator.length());
		} else {
			retVal = sb.toString();
		}
		return retVal;
	}

	/**
	 * Returns the full name including that of the parent(s) separated
	 * by the separator String.
	 * @param separator the separator for the name
	 * @param attributeName the name for the attribute to append in the list
	 */
	public String getHierarchyName(String separator, String attributeName) {
		if (separator == null) {
			separator = "-";
		}
		List l = this.getHierarchyList();
		StringBuffer sb = new StringBuffer();
		Iterator iter = l.iterator();
		while (iter.hasNext()) {
			VarBean b = (VarBean)iter.next();
			if (b.getString(attributeName) != null) {
				sb.append(b.getString(attributeName));
				sb.append(separator);
			}
		}
		String retVal = "";
		if (sb.length() > separator.length()) {
			retVal = sb.substring(0, sb.length()-separator.length());
		} else {
			retVal = sb.toString();
		}
		return retVal;
	}

	public static void main(String[] args) {
		VarBean parent = new VarBean("${name}");
		VarBean child1 = new VarBean("${name}");
		VarBean child2 = new VarBean("${name}");
		VarBean child3 = new VarBean("${name}");
		VarBean grandchild = new VarBean("${name}");
		grandchild.setString("name", "grandchild1");

		child1.setString("name", "child1");
		child1.addChild(grandchild);

		parent.setString("name", "parent");
		parent.setString("myValue", "I am the best");
		parent.setInt("myInt", 1036);
		parent.addChild(child1);
		
		child2.setString("name", "child2");
		child2.setString("myresult", "I got an A+");
		parent.addChild(child2);

		child3.setString("name", "child3");
		child3.setString("character", "naughty");
		parent.addChild(child3);
		
		System.out.println(child3.getParent());
		System.out.println(child3.getString("myValue"));
		
		VarBean test = parent.getChildByKey("child3");
		System.out.println("test: " + test.getString("name"));
		
		System.out.println(parent.childMap);
		System.out.println(parent.toString("person", true, true));
		
		Collection c = parent.flatten();
		Iterator iter = c.iterator();
		while (iter.hasNext()) {
			Object iterObj = iter.next();
			if (iterObj != null) {
				VarBean b = (VarBean)iterObj;
				System.out.print(b.getString("name"));
				System.out.print("    ");
				System.out.print(b.getHierarchyIndex("-"));
				System.out.print("    ");
				System.out.print(b.getHierarchyName("-", "name"));
			}
			System.out.println("");
		}
		
	}//*/
}

