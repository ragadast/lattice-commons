/*
 * Copyright Jurong Port Pte Ltd
 * Created on Nov 16, 2007
 */
package org.latticesoft.util.container.tree.util;

import java.util.*;
import org.latticesoft.util.common.*;
import org.latticesoft.util.container.tree.*;
import org.latticesoft.util.container.tree.noderule.*;
import org.latticesoft.util.container.tree.impl.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class TreeUtil {

	private static final Log log = LogFactory.getLog(TreeUtil.class);
	/**
	 * Builds a tree based on the data and the grouping rule.
	 * The rule is added in grouping sequence
	 * @param groupBy the grouping key
	 * @param data the list of data containing all the objects
	 * @return the Node tree built and populated
	 */
	public Node buildAndPopulateTree(List groupByList, List dataList) {
		// 1st of all scan thru the list of data for the unique group by keys
		if (groupByList == null || dataList == null || 
			groupByList.size() == 0 || dataList.size() == 0) {
			return null;
		}
		
		/*
		TODO: Things to consider when building a tree
		
		1) Tree is built by fixed criteria 
		E.g. Range criteria time period range
		The range like time period usually is predetermined. 
		Range rules are used to classify values into groups.
		 
		2) Tree is built by dynamic values in the data (Equal Rule)
		The tree is group by the number of unique values in the data, then
		the node will be dynamic.
		The problem with dynamic data is when a new node has to be added to this
		node and all the same-level siblings.
		If this node has some children, the newly added node will have to create 
		the same set of children and grand children...
		
		Alternatively, if we adopt create-on-the-fly way, it means, we will 
		only add nodes when necessary, rather than create the entire tree. 
		when some of the node will no entries.
		This method has to be consciously aware of the rule at each level.
		The vertical index will be helpful in such case to reference the rules and nodes
		at a particular level
		
		Design is a column of grouping keys with a node rule at each level.
		The data is evaluated at each level.
		When the node at that corresponding level does not have the appropriate children,
		the child will be created.
		Then the data is passed to the next level to evaluate.
		At any level the current node is kept as reference.
		//*/
		Node rootNode = new NodeImpl();
		NodeRule rule = null;
		Object o = null;
		Object data = null;
		Node currentNode = null;
		Node childNode = null;
		for (int dataIndex=0; dataIndex<dataList.size(); dataIndex++) {
			currentNode = rootNode;
			data = dataList.get(dataIndex);
			// loop thru rule to get to the data
			for (int ruleIndex=0; ruleIndex<groupByList.size(); ruleIndex++) {
				o = groupByList.get(ruleIndex);
				if (o instanceof NodeRule) {
					rule = (NodeRule)o;
					o = rule.findNode(data);
					// this o will lead us to the next node
					childNode = currentNode.getChild(o.toString());
					if (childNode == null) {
						// create new child for the current node
						childNode = new NodeImpl();
						childNode.setName(o.toString());
						childNode.setId(o.toString());
						if (log.isDebugEnabled()) {
							log.debug("Creating a new node: " + childNode.getHierarchicalListName());
							log.debug("Adding to parent: " + currentNode.getHierarchicalListName());
						}
						currentNode.addChild(childNode);
					}
					// now set the current node to child node
					if (childNode != null) {
						currentNode = childNode;
					}
				} else {
					continue;
				}
				// when we reach the end of the index
				// we add the data to the leaf node itself 
				// (last node in the vertical hierarchy)
				if (ruleIndex == groupByList.size() - 1 && currentNode != null) {
					if (log.isDebugEnabled()) {
						log.debug("Adding data to node: " + currentNode.getHierarchicalListName());
					}
					currentNode.addData(data);
				}
			}
		}
		return rootNode;
	}
	
	/** 
	 * Returns a list of all the nodes in a flatten structure.
	 * The order is such that a branch is fully added first 
	 * before proceeding with another branch. (Depth before Width)
	 */
	public static List getFlattenList(Node n) {
		List l = new ArrayList();
		TreeUtil.getFlattenList(n, l);
		return l;
	}
	
	/** 
	 * Returns a List of all the nodes in a flatten structure
	 * @param n the node to extract the child nodes 
	 * @param l the list to add the child
	 */
	public static void getFlattenList(Node n, List l) {
		l.addAll(n.getChildList());
		int size = n.getChildList().size();
		if (size > 0) {
			for (int i=0; i<size; i++) {
				Node child = n.getChild(i);
				TreeUtil.getFlattenList(child, l);
			}
		}
	}

	/**
	 * Get a list of all the outer edge leaf nodes.
	 * If the node has child it will not be included.
	 * @param n the node
	 */
	public static List getLeafNodes(Node n) {
		List l = new ArrayList();
		// loop thru the node to extract only the leaf i.e. outer edge nodes
		TreeUtil.getLeafNodes(n, l);
		return l;
		
	}
	/**
	 * Get a list of all the outer edge leaf nodes.
	 * If the node has child it will not be included.
	 * @param n the node
	 * @param l the list to add the nodes
	 */
	public static void getLeafNodes(Node n, List l) {
		int size = n.getChildList().size();
		if (size == 0) {
			l.add(n);
		} else {
			for (int i=0; i<size; i++) {
				Node child = n.getChild(i);
				TreeUtil.getLeafNodes(child, l);
			}
		}
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
		/*
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
		}//*/
		/*
		System.out.println(((NodeImpl)n0).toString(true));
		System.out.println("==========");
		System.out.println(TreeUtil.getFlattenList(n0));
		System.out.println("==========");
		System.out.println(TreeUtil.getLeafNodes(n0));
		//*/
		
		
		// testing building a node network
		EqualRule rule = new EqualRule();
		
	}
}
