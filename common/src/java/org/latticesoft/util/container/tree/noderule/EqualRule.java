/*
 * Copyright Jurong Port Pte Ltd
 * Created on Nov 19, 2007
 */
package org.latticesoft.util.container.tree.noderule;

import java.util.*;
import org.latticesoft.util.common.*;
import org.latticesoft.util.container.tree.MultiAttributeNodeRule;
import org.latticesoft.util.container.tree.Node;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class EqualRule implements MultiAttributeNodeRule {
	private static final Log log = LogFactory.getLog(EqualRule.class);
	private Node parentNode = null;
	private Map reference = new HashMap();
	private List attributeList = new ArrayList();
	
	public void addAttributeName(String name) {
		if (name != null) {
			this.attributeList.add(name);
		}
	}

	public List getAttributeList() {
		return (this.attributeList);
	}

	public void setAttributeList(List l) {
		if (l != null) {
			this.attributeList.addAll(l);
		}
	}
	
	public int getSize() {
		return this.attributeList.size();
	}

	public Node findNode(Object data) {
		String key = this.formatKey(data);
		if (parentNode == null) {
			return null;
		}
		String nodeName = (String)this.reference.get(key);
		Node theNode = parentNode.getChildByName(nodeName);
		if (log.isInfoEnabled()) { log.info(key + " --> " + nodeName); }
		return theNode;
	}
	
	public String formatKey(Object o) {
		StringBuffer sb = new StringBuffer();
		for (int i=0; i<this.attributeList.size(); i++) {
			String attributeName = (String)this.attributeList.get(i);
			Object attributeValue = BeanUtil.getAttribute(o, attributeName);
			if (attributeValue != null) {
				sb.append(attributeValue);
			}
		}
		return sb.toString();
	}

	/* (non-Javadoc)
	 * @see org.latticesoft.util.container.tree.NodeRule#isDynamic()
	 */
	public boolean isDynamic() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.latticesoft.util.container.tree.NodeRule#isStatic()
	 */
	public boolean isStatic() {
		// TODO Auto-generated method stub
		return false;
	}
	
	
}
