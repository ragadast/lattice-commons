package org.latticesoft.util.common;

import java.util.*;
import java.io.Serializable;
import org.apache.commons.beanutils.WrapDynaBean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SortHelper implements Comparator, Serializable, List {

	public static final long serialVersionUID = 20061003103940L;
	private List sortOrders = new ArrayList();
	private static final Log log = LogFactory.getLog(SortHelper.class);
	
	public SortHelper(){}
	public SortHelper(Collection c){
		this.addAll(c);
	}
	
	/** @see java.util.List#add(int, java.lang.Object) */
	public void add(int arg0, Object arg1) { this.sortOrders.add(arg0, arg1); }
 	/** @see java.util.List#add(java.lang.Object) */
	public boolean add(Object arg0) { return sortOrders.add(arg0); }
	/** @see java.util.List#addAll(int, java.util.Collection) */
	public boolean addAll(int arg0, Collection arg1) { return sortOrders.addAll(arg0, arg1); }
	/** @see java.util.List#contains(java.lang.Object) */
	public boolean contains(Object o) { return sortOrders.contains(o); }
	/** @see java.util.List#containsAll(java.util.Collection) */
	public boolean containsAll(Collection arg0) { return sortOrders.containsAll(arg0); }
	/** @see java.util.List#get(int) */
	public Object get(int index) { return sortOrders.get(index); }
	/** @see java.util.List#hashCode() */
	public int hashCode() { return sortOrders.hashCode(); }
	/** @see java.util.List#indexOf(java.lang.Object) */
	public int indexOf(Object o) { return sortOrders.indexOf(o); }
	/** @see java.util.List#isEmpty() */
	public boolean isEmpty() { return sortOrders.isEmpty(); }
	/** @see java.util.List#iterator() */
	public Iterator iterator() { return sortOrders.iterator(); }
	/** @see java.util.List#lastIndexOf(java.lang.Object) */
	public int lastIndexOf(Object o) { return sortOrders.lastIndexOf(o); }
	/** @see java.util.List#listIterator() */
	public ListIterator listIterator() { return sortOrders.listIterator(); }
	/** @see java.util.List#listIterator(int) */
	public ListIterator listIterator(int index) { return sortOrders.listIterator(index); }
	/** @see java.util.List#remove(int) */
	public Object remove(int index) { return sortOrders.remove(index); }
	/** @see java.util.List#remove(java.lang.Object) */
	public boolean remove(Object o) { return sortOrders.remove(o); }
	/** @see java.util.List#removeAll(java.util.Collection) */
	public boolean removeAll(Collection arg0) { return sortOrders.removeAll(arg0); }
	/** @see java.util.List#retainAll(java.util.Collection) */
	public boolean retainAll(Collection arg0) { return sortOrders.retainAll(arg0); }
	/** @see java.util.List#set(int, java.lang.Object) */
	public Object set(int arg0, Object arg1) { return sortOrders.set(arg0, arg1); }
	/** @see java.util.List#size() */
	public int size() { return sortOrders.size(); }
	/** @see java.util.List#subList(int, int) */
	public List subList(int fromIndex, int toIndex) { return sortOrders.subList(fromIndex, toIndex); }
	/** @see java.util.List#toArray() */
	public Object[] toArray() { return sortOrders.toArray(); }
	/** @see java.util.List#toArray(java.lang.Object[]) */
	public Object[] toArray(Object[] arg0) { return sortOrders.toArray(arg0); }
	/** @see java.util.List#clear() */
	public void clear() { this.sortOrders.clear(); }
	/** @see java.util.List#addAll(Collection) */
	public boolean addAll(Collection c) {
		if (c == null) { return false; }
		return this.sortOrders.addAll(c);
	}

	public boolean addOrder(String s) {
		if (s != null) {
			return this.add(s);
		}
		return false;
	}
	
	public int compare(Object o1, Object o2) {
		if (this.sortOrders == null || this.sortOrders.size() == 0) {
			return 0;
		}
		if (o1 == null && o2 == null) {
			return 0;
		} else if (o1 != null && o2 == null) {
			return 1;
		} else if (o1 == null && o2 != null) {
			return -1;
		} else if (o1 != null && o2 != null){
			StringBuffer sb1 = new StringBuffer();
			StringBuffer sb2 = new StringBuffer();
			WrapDynaBean bean1 = new WrapDynaBean(o1);
			WrapDynaBean bean2 = new WrapDynaBean(o2);
			for (int i=0; i<this.sortOrders.size(); i++) {
				String attribute = (String)this.sortOrders.get(i);
				try {
					Object value1 = bean1.get(attribute);
					Object value2 = bean2.get(attribute);
					sb1.append(value1);
					sb2.append(value2);
				} catch (Exception e) {
					if (log.isDebugEnabled()) { log.debug(e); }
				}
			}
			return sb1.toString().compareTo(sb2.toString());
		}
		return 0;
	}

	/**
	 * Converts the class in a string form
	 * @returns the class in a string form.
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (int i=0; i<this.sortOrders.size(); i++) {
			sb.append(this.sortOrders.get(i));
			sb.append("|");
		}
		if (sb.length() > 0) {
			sb.deleteCharAt(sb.length()-1);
		}
		return sb.toString();
	}
	
	/**
	 * Sort the list according to given order
	 * @param l the list to be sorted
	 */
	public void sort(List l) {
		if (l != null) {
			Collections.sort(l, this);
		}
	}
	
	/**
	 * Split the list into the separate list according
	 * the sort attributes
	 */
	public Map split(List l) {
		return this.split(l, true);
	}
	/**
	 * Split the list into the separate list according
	 * the sort attributes
	 */
	public Map split(List l, boolean preSortList) {
		
		// Sort the list according to the parameters. 
		// Loop thru each element extract the attribute value
		// Store the attribute in a set attributeName-attributeValue
		// Compare the current set value to the previous value
		// If it is the same store into the existing result List
		// If it is different recreate a new result list and store inside that
		// First element must set a new previous set for comparison
		// Last element must remember to add result List to the map
		Map map = new HashMap();
		if (l == null || l.size() == 0) {
			return map;
		}
		if (this.sortOrders.isEmpty()) {
			return map;
		}
		// Sort the existing list
		if (preSortList) {
			this.sort(l);
		}
		
		// for comparison
		Set prevKey = new HashSet();
		Set currKey = new HashSet();
		ArrayList a = new ArrayList();

		for (int i=0; i<l.size(); i++) {
			Object o = l.get(i);
			WrapDynaBean bean = new WrapDynaBean(o);
			try {
				// current key
				currKey.clear();
				for (int j=0; j<this.sortOrders.size(); j++) {
					String attribute = (String)this.sortOrders.get(j);
					Object value = bean.get(attribute);
					if (value != null) {
						currKey.add(attribute+value);
					}
				}
				// 1st element no reference
				if (prevKey.isEmpty()) {
					prevKey.addAll(currKey);
				}
				
				// compare to prevKey
				if (currKey.equals(prevKey)) {
					a.add(o);
				} else {
					// store in map and create a new temp List
					// update prev key
					map.put(prevKey, a);
					a = new ArrayList();
					a.add(o);
					prevKey = new HashSet();
					prevKey.addAll(currKey);
				}
			} catch (Exception e) {
				if (log.isDebugEnabled()) { log.debug(e); }
			}
		}
		// last element add to map
		if (a != null && !a.isEmpty() && !prevKey.isEmpty()) {
			map.put(prevKey, a);
		}
		return map;
	}
	
	public static void main(String[] args) {
		SortHelper util = new SortHelper();
		
		ArrayList a = new ArrayList();
		TestBean bean = null;
		
		for (int i=0; i<3; i++) {
			for (int j=0; j<3; j++) {
				bean = new TestBean();
				bean.setName("" + i);
				bean.setMessage("" + j);
				a.add(bean);
			}
		}
		Collections.shuffle(a);
		for (int i=0; i<a.size(); i++) {
			bean = (TestBean)a.get(i);
			//System.out.println(bean);
		}
		System.out.println("==================================================");
		util.addOrder("name");
		util.addOrder("message");
		util.sort(a);
		for (int i=0; i<a.size(); i++) {
			bean = (TestBean)a.get(i);
			//System.out.println(bean);
		}
		System.out.println("================================================== Split");
		
		util.clear();
		util.add("name");
		//Collections.shuffle(a);
		for (int i=0; i<a.size(); i++) {
			bean = (TestBean)a.get(i);
			System.out.println(bean);
		}
		System.out.println("================================================== Split");
		Map map = util.split(a, false);
		Set set = map.keySet();
		Iterator iter = set.iterator();
		while (iter.hasNext()) {
			Object o = iter.next();
System.out.println(o);
System.out.println("==================================================");
			List l = (List)map.get(o);
			if (l == null) continue;
			for (int i=0; i<l.size(); i++) {
				System.out.println(l.get(i));
			}
		}
		
		//*/
		
		/*
		SortedSet set = new TreeSet();
		set.add("hello");
		set.add("how are you");
		set.add("123456");
		
		SortedSet set1 = new TreeSet();
		set1.add("hello");
		set1.add("123456");
		set1.add("how are you");
		
		System.out.println(set.equals(set1));
		//*/
	}
}

