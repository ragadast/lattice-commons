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
package org.latticesoft.util.container.tree.impl;

import java.util.*;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import org.latticesoft.util.container.tree.Hierarchy;
import org.latticesoft.util.container.tree.Node;

/**
 * <p>
 * The NodeImpl is an implementation of the Node 
 * </p>
 */
public class NodeImpl implements Node {
	
	public static final long serialVersionUID = 1;
	protected static final Log log = LogFactory.getLog(NodeImpl.class);
	protected Node parent = null;
	protected ArrayList childList = new ArrayList();
	protected Map childMap = new HashMap();

	protected String name;
	protected String id;

	protected Map properties = new HashMap();
		
	protected List dataList = new ArrayList();

	/** Constructs the element */
	public NodeImpl() {}

	/**
	 * Constructs the container
	 * @param name the name of the element.
	 */
	public NodeImpl(String name) {
		this.setName(name);
	}
	/**
	 * Constructs the container
	 * @param name the name of the element.
	 */
	public NodeImpl(String name, String id) {
		this.setName(name);
		this.setId(id);
	}

	// Identity interface
	public String getName() { return (this.name); }
	public void setName(String name) { this.name = name; }
	
	public String getId() { return this.id; }
	public void setId(String id) { this.id = id; }
	
	
	// DataList interface
	public List getDataList() { return (this.dataList); }
	public void addData(Object o) {
		if (o != null) {
			this.getChildList().add(o);
		}
	}
	public Object getData(int index) {
		return this.childList.get(index);
	}

	// Attributable interface
	public Object getAttribute(String name) {
		Object o = this.properties.get(name);
		if (o == null) {
			if (this.parent != null) {
				o = this.parent.getAttribute(name);
			}
		}
		return o;
	}
	public void setAttribute(String name, Object value) {
		if (name == null || value == null) return;
		this.properties.put(name, value);
	}
	public Map getAllAttribute() {
		return this.properties;
	}

	public Node getParent() { return (this.parent); }
	public void setParent(Node parent) { this.parent = parent; }

	public boolean addChild(Hierarchy child) {
		List list = this.getHierarchicalList();

		// excludes already added child
		Iterator iter = list.iterator();
		while (iter.hasNext()) {
			if (iter.next() == child) return false;
		}
		child.setParent(this);
		if (child instanceof Node) {
			Node childNode = (Node)child;
			if (childNode.getName() != null) {
				if (!this.childMap.containsKey(childNode.getName())) {
					childMap.put(childNode.getName(), childNode);
				}
			}
		}
		return childList.add(child);
	}

	public void init() {
		Iterator iter = this.getChildList().iterator();
		while (iter.hasNext()) {
			NodeImpl child = (NodeImpl)iter.next();
			child.setParent(this);
			child.init();
		}
	}

	public Node getChildByIndex(int index) {
		if (index >= childList.size() || index < 0) {
			return null;
		}
		return (NodeImpl)childList.get(index);
	}

	public Node getChildByName(String s) {
		if (s == null) return null;
		if (!this.childMap.containsKey(s)) return null;
		return (Node)this.childMap.get(s);
	}

	public Node getChild(int index) {
		return this.getChildByIndex(index);
	}

	public Node getChild(String name) {
		return this.getChildByName(name);
	}
	
	public List getChildList() {
		return this.childList;
	}

	public List getHierarchicalList() {
		ArrayList a = new ArrayList();
		Set set = new java.util.HashSet();
		Node cntr = this;
		int count = 0;
		while (cntr != null && count < 1000) {
			if (!set.contains(cntr)) {
				set.add(cntr);
				a.add(cntr);
			} else {
				break;
			}
			cntr = cntr.getParent();
			count++;
		}
		java.util.Collections.reverse(a);
		return a;
	}
	public int[] getHierarchicalListIndex() {
		List l = this.getHierarchicalList();
		int[] retVal = new int[l.size()];
		for (int i=0; i<l.size(); i++) {
			Node n = (Node)l.get(i);
			retVal[i] = n.getHorizontalIndex();
		}
		return retVal;
	}
	public String getHierarchicalListName() {
		List l = this.getHierarchicalList();
		StringBuffer sb = new StringBuffer();
		for (int i=0; i<l.size(); i++) {
			Node n = (Node)l.get(i);
			sb.append(n.getName());
			sb.append("-");
		}
		if (sb.length() > 0) {
			sb.deleteCharAt(sb.length() - 1);
		}
		return sb.toString();
	}

	public int getHorizontalIndex() {
		int index = 0;
		if (this.parent != null) {
			index = parent.getChildList().indexOf(this);
		}
		return index;
	}

	public int getVerticalIndex() {
		int index = 0;
		Node parent = this;
		do {
			parent = parent.getParent();
			if (parent != null) {
				index++;
			}
		} while (parent != null);
		return index;
	}
	
	/**
	 * Converts the class in a string form
	 * @returns the class in a string form.
	 */
	public String toString() {
		return this.toString(true);
	}

	public String toString(boolean showChild) {
		StringBuffer sb = new StringBuffer();
		if (showChild) {
			sb.append("\n");
			int index = this.getVerticalIndex();
			for (int j=0; j < index; j++) {
				sb.append("\t");
			}
		}
		sb.append("[Node");
		sb.append("|name=").append(this.getName());
		sb.append("|id=").append(this.getId());
		sb.append("||");
		Iterator iter = this.properties.keySet().iterator();
		while (iter.hasNext()) {
			Object key = iter.next();
			Object value = this.properties.get(key);
			sb.append("|").append(key).append("=").append(value);
		}
		sb.append("]");

		if (showChild) {
			for (int i=0; i<this.childList.size(); i++) {
				NodeImpl child = (NodeImpl)this.getChild(i);
				sb.append(child.toString(showChild));
			}
		}
		return sb.toString();
	}

	public static void main(String args[]){
		List l = new ArrayList();
		Node n0 = new NodeImpl("N0", "ID_0");
		Node n1 = new NodeImpl("N1", "ID_1");
		Node n2 = new NodeImpl("N2", "ID_2");
		Node n2a = new NodeImpl("N2a", "ID_2a");
		Node n2b = new NodeImpl("N2b", "ID_2b");
		Node n3 = new NodeImpl("N3", "ID_3");
		Node n3aa = new NodeImpl("N3aa", "ID_3aa");
		Node n4 = new NodeImpl("N4", "ID_4");
		Node n4a = new NodeImpl("N4a", "ID_4a");
		Node n4b = new NodeImpl("N4b", "ID_4b");
		Node n4c = new NodeImpl("N4c", "ID_4c");
		Node n5 = new NodeImpl("N5", "ID_5");
		l.add(n0);
		l.add(n1);
		l.add(n2);
		l.add(n2a);
		l.add(n2b);
		l.add(n3);
		l.add(n3aa);
		l.add(n4);
		l.add(n4a);
		l.add(n4b);
		l.add(n4c);
		l.add(n5);
		
		n0.addChild(n1);
		n1.addChild(n2);
		n2.addChild(n3);
		n3.addChild(n4);
		n4.addChild(n5);
		n1.addChild(n2a);
		n1.addChild(n2b);
		n2a.addChild(n3aa);
		n3.addChild(n4a);
		n3.addChild(n4b);
		n3.addChild(n4c);
		
		for (int i=0; i<l.size(); i++) {
			NodeImpl n = (NodeImpl)l.get(i);
			System.out.println("==========");
			System.out.println(n.toString(true));
			System.out.println("==========");
			
			System.out.println(n.getHierarchicalList());
			System.out.println(n.getHierarchicalListName());
			for (int j=0; j<n.getHierarchicalListIndex().length; j++) {
				System.out.print(n.getHierarchicalListIndex()[j]);
				System.out.print("-");
			}
			System.out.println("");
			System.out.println(n.getVerticalIndex());
			System.out.println(n.getHorizontalIndex());
		}
		System.out.println("==========");
	}
}
