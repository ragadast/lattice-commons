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

import java.util.Iterator;
import java.util.Set;
import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import java.util.HashSet;
import java.io.Serializable;

/**
 * <p>The OrderedSet is a Set implementation that remembers
 * the order of the elements of added into the set. The
 * elements can be retrieved by the iterator method. The
 * usage is as per usage of any other Set. Note that it is
 * not thread safe.
 */
public class OrderedSet implements Set, Serializable {

	public static final long serialVersionUID = 1;
	protected Set set = new HashSet();
	protected List list = new ArrayList();

	/**
	 * Returns the size of the set.
	 */
	public int size() {
		return set.size();
	}

	/**
	 * Checks whether the set is empty or not
	 * @return true if empty
	 */
	public boolean isEmpty() {
		return set.isEmpty();
	}

	/**
	 * Checks whether the set contains the object or not
	 * @param o the test subject
	 * @return true if o is in the set
	 */
	public boolean contains(Object o) {
		return set.contains(o);
	}

	/**
	 * Returns the iterator to the set
	 */
	public Iterator iterator() {
		return list.iterator();
	}

	/**
	 * Converts the set into an array.
	 * @return the object array in the set in order
	 */
	public Object[] toArray() {
		return list.toArray();
	}

	/**
	 * Converts the set into an array.
	 * @return the object array in the set in order
	 * @param o the initialized array to hold the content
	 * @return the object array
	 */
	public Object[] toArray(Object[] o) {
		return list.toArray(o);
	}

	/**
	 * Adds an object to the set. The ordered of addition will
	 * be remembered.
	 * @param o the object to be added
	 * @return true if the addition succeeds.
	 */
	public boolean add(Object o) {
		// once set has the object we can return already
		if (set.contains(o)) return false;

		boolean setFlag = set.add(o);
		boolean listFlag = false;
		if (setFlag) {
			listFlag = list.add(o);
		}
		if (listFlag & !setFlag) {
			list.remove(o);
		} else if (!listFlag & setFlag) {
			set.remove(o);
		}
		return listFlag & setFlag;
	}

	/**
	 * Removes the object.
	 * @param o the object to be removed.
	 * @return true if succeed
	 */
	public boolean remove(Object o) {
		if (!list.contains(o) || !set.contains(o)) {
			return false;
		}

		int index = list.lastIndexOf(o);

		boolean setFlag = set.remove(o);
		boolean listFlag = false;
		if (setFlag) {
			listFlag = list.remove(o);
		}
		if (!listFlag && setFlag) {
			set.add(o);
		} else if (listFlag && !setFlag) {
			list.add(index, o);
		}
		return listFlag && setFlag;
	}

	/**
	 * Checks whether the collection is present in the set.
	 * @param c the collection test subject
	 * @return true if present
	 */
	public boolean containsAll(Collection c) {
		return set.containsAll(c);
	}

	/**
	 * Adds a collection to the set
	 * @param c the collection to be added
	 * @return true if succeeds
	 */
	public boolean addAll(Collection c) {
		// backup the set 1st
		Set backupSet = new HashSet();
		backupSet.addAll(this.set);
		List backupList = new ArrayList();
		list.addAll(this.list);

		boolean setFlag = set.addAll(c);
		boolean listFlag = false;
		if (setFlag) {
			listFlag = list.addAll(c);
		}
		if (!listFlag || !setFlag) {
			this.list = backupList;
			this.set = backupSet;
		}
		return listFlag && setFlag;
	}

	/**
	 * Retains the collection
	 * @param c the subject
	 * @return true if succeeds
	 */
	public boolean retainAll(Collection c) {
		// backup the set 1st
		Set backupSet = new HashSet();
		backupSet.addAll(this.set);
		List backupList = new ArrayList();
		list.addAll(this.list);

		boolean setFlag = set.retainAll(c);
		boolean listFlag = false;
		if (setFlag) {
			listFlag = list.retainAll(c);
		}
		if (!listFlag || !setFlag) {
			this.list = backupList;
			this.set = backupSet;
		}
		return listFlag && setFlag;
	}

	/**
	 * Removes all the object in the collection
	 * @param c the collection in question
	 * @return true if succeed
	 */
	public boolean removeAll(Collection c) {
		// backup the set 1st
		Set backupSet = new HashSet();
		backupSet.addAll(this.set);
		List backupList = new ArrayList();
		list.addAll(this.list);

		boolean setFlag = set.removeAll(c);
		boolean listFlag = false;
		if (setFlag) {
			listFlag = list.removeAll(c);
		}
		if (!listFlag || !setFlag) {
			this.list = backupList;
			this.set = backupSet;
		}
		return listFlag && setFlag;
	}

	/**
	 * Clears the collection
	 */
	public void clear() {
		// backup the set 1st
		Set backupSet = new HashSet();
		backupSet.addAll(this.set);
		List backupList = new ArrayList();
		list.addAll(this.list);

		set.clear();
		boolean setFlag = (set.size() == 0);
		boolean listFlag = false;
		if (setFlag) {
			list.clear();
			listFlag = (list.size() == 0);
		}
		if (!listFlag || !setFlag) {
			this.list = backupList;
			this.set = backupSet;
		}
	}

	/**
	 * Checks for equality
	 * @param o the test subject
	 * @return true if equal
	 */
	public boolean equals(Object o) {
		return set.equals(o);
	}

	/**
	 * Returns the hashcode of the set
	 */
	public int hashCode() {
		return set.hashCode();
	}

	/**
	 * Convert the object into string format.
	 * @return the class expressed in string format
	 */
	public String toString() {
		return list.toString();
	}
/*
	public static void main(String args[]){
		OrderedSet set = new OrderedSet();
		set.add("Test1");
		set.add("Test2");
		set.add("Test3");
		set.add("Test1");
		System.out.println(set);

		Iterator iter = set.iterator();
		while (iter.hasNext()) {
			System.out.println(iter.next());
		}
	}//*/
}
