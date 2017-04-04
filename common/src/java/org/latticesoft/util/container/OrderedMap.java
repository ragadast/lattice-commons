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
 */
package org.latticesoft.util.container;

import java.util.Map;
import java.util.Iterator;
import java.util.Set;
import java.util.List;
import java.util.Collection;
import java.util.HashMap;
import java.util.ArrayList;
import java.io.Serializable;

/**
 * <p>OrderedMap is a Map implementation that remembers the
 * order the element is stored into the Map. To retrieve
 * the elements there are several ways. </p>
 * <p>The entrySet does not gives the order of entry. In order
 * to get the set with order, use the keySet instead and retrieve
 * the corresponding value from the get method.</p>
 * <code>
 * Map map = new OrderedMap();
 * Set set = map.keySet();
 * Iterator iter = set.iterator();
 * while (iter.hasNext()) {
 *     Object key = iter.next();
 *     Object value = map.get(key);
 * }
 * </code>
 *
 */
public class OrderedMap implements Map, Serializable {

	public static final long serialVersionUID = 1;
	/** The wrapped object */
	protected Set keySet = new OrderedSet();

	/** Temporary storage. Not meant to be visible at all */
	protected Map map = new HashMap();

	/**
	 * Default Constructor
	 */
	public OrderedMap() {
		map.clear();
		keySet.clear();
	}

	/**
	 * Gets the size of the map
	 * @return the size of the attributes in request
	 */
	public int size() {
		return map.size();
	}

	/**
	 * Checks whether the map is empty.
	 * @return true if empty
	 */
	public boolean isEmpty() {
		return map.isEmpty();
	}

	/**
	 * Determines whether the key is present in the request attributes.
	 * @param key the key to be checked
	 * @return true if present. false otherwise.
	 */
	public boolean containsKey(Object key) {
		return map.containsKey(key);
	}

	/**
	 * Determines whether the value is present in the request attributes.
	 * @param value the value to be checked
	 * @return true if present.
	 */
	public boolean containsValue(Object value) {
		return map.containsValue(value);
	}

	/**
	 * Gets the object in the request attribute with a specified key
	 * @param key the key
	 * @return the object represented in
	 */
	public Object get(Object key) {
		return map.get(key);
	}

	/**
	 * Adds or Set the key-value pair in the request
	 * @return the previous object present if it replaces.
	 */
	public Object put(Object key, Object value) {
		if (key == null) return null;
		if (this.map.containsKey(key)) return value;

		Object retVal = null;
		if (!map.containsKey(key) && !keySet.contains(key)) {
			keySet.add(key);
		}
		if (map.containsKey(key)) {
			retVal = map.get(key);
		}
		map.put(key, value);
		return retVal;
	}

	/**
	 * Removes the object represented by the key
	 * @param key the key representing the object
	 * @return the removed object
	 */
	public Object remove(Object key) {
		if (key == null) return null;
		Object retVal = null;
		if (map.containsKey(key)) {
			retVal = map.get(key);
		}
		keySet.remove(key);
		retVal = map.remove(key);
		return retVal;
	}

	/**
	 * Put all the values in the map into the request.
	 * @param m the input map
	 */
	public void putAll(Map m) {
		Iterator iter = m.keySet().iterator();
		while (iter.hasNext()) {
			Object key = iter.next();
			if (key != null) {
				Object value = m.get(key);
				this.keySet.add(key);
				this.map.put(key, value);
			}
		}
	}

	/**
	 * Clear the attributes
	 */
	public void clear() {
		this.map.clear();
		this.keySet.clear();
	}

	/**
	 * Returns the set of keys in the request.
	 * @return the set of keys
	 */
	public Set keySet() {
		return this.keySet;
	}

	/**
	 * Returns the set of values in the request.
	 * @return the set of values
	 */
	public Collection values() {
		Collection c = new ArrayList();
		Iterator iter = this.keySet.iterator();
		while (iter.hasNext()) {
			c.add(map.get(iter.next()));
		}
		return c;
	}

	/**
	 * Returns the set of values in the request.
	 * @return the set of values
	 */
	public List valuesList() {
		List l = new ArrayList();
		Iterator iter = this.keySet.iterator();
		while (iter.hasNext()) {
			l.add(map.get(iter.next()));
		}
		return l;
	}

	/**
	 * Returns the entry set.
	 * @return the entry set
	 */
	public Set entrySet() {
		return map.entrySet();
	}

	/**
	 * Checks for equality
	 * @param o the test subject
	 * @return true if equal
	 */
	public boolean equals(Object o) {
		return map.equals(o);
	}

	/**
	 * Returns the hashcode
	 * @return the hashcode
	 */
	public int hashCode() {
		return map.hashCode();
	}

	/**
	 * Convert the object into string format.
	 * @return the class expressed in string format
	 */
	public String toString() {
		return map.toString();
	}
	
	public Iterator iterator() {
		return new OrderedMap.OrderedMapIterator(this);
	}
	
	protected class OrderedMapIterator implements Iterator {
		private OrderedMap omap;
		private Iterator iter;
		private Object key = null;
		public OrderedMapIterator(OrderedMap map) {
			this.omap = map;
			iter = omap.keySet().iterator();
		}
		public boolean hasNext() {
			return iter.hasNext();
		}

		public Object next() {
			key = iter.next();
			return omap.get(key);
		}

		public void remove() {
			omap.remove(key);
		}
	}
/*
	public static void main(String args[]){
		Map map = new OrderedMap();
		map.put("Key1", "Value1");
		map.put("Key2", "Value2");
		map.put("Key3", "Value3");
		map.put("Key4", "Value4");
		map.put("Key1", "Value1");

		System.out.println(map);

		Collection c = map.values();
		System.out.println(c);

		Set s = map.keySet();
		System.out.println(s);

		Iterator iter = map.keySet().iterator();
		while (iter.hasNext()) {
			System.out.println(map.get(iter.next()));
		}
	}
//*/
}
