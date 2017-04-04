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
package org.latticesoft.util.container.tree.noderule;

import java.util.*;
import org.latticesoft.util.common.*;
import org.latticesoft.util.container.tree.Node;
import org.latticesoft.util.container.tree.SingleAttributeNodeRule;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The range rule is a rule which will a data object to a node based on 
 * if the certain attribute of the data object is within certain range.
 * The range key will be searched to see if the object's attribute fall
 * into the range specified by the key. If it does it will format the key
 * by appending the value represented by the search key.
 * 
 * After which the key checked against the reference to see which node 
 * to refers to. If no match is found the default node will be used
 */
public class RangeRule implements SingleAttributeNodeRule {

	private static final Log log = LogFactory.getLog(EqualRule.class);
	private Node parentNode = null;
	private List keyList = new ArrayList();
	private Map reference = new HashMap();
	private String attributeName = null;
	
	/** @return Returns the attributeName. */
	public String getAttributeName() {
		return (this.attributeName);
	}
	/** @param attributeName The attributeName to set. */
	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}

	public Node findNode(Object data) {
		Node theNode = null;
		String nodeName = null;
		StringBuffer sb = new StringBuffer();
		for (int i=0; i<keyList.size(); i++) {
			RangeRuleKey key = (RangeRuleKey)this.keyList.get(i);
			Object dataAttribute = BeanUtil.getAttribute(data, this.attributeName);
			Object value = key.evaluate(dataAttribute);
			if (value != null) {
				sb.append(value);
				sb.append("|");
			}
		}
		if (sb.length() > 0) {
			sb.deleteCharAt(sb.length() - 1);
		}
		if (sb.length() == 0) {
			sb.append("default");
		}
		nodeName = (String)this.reference.get(sb.toString());
		if (log.isInfoEnabled()) { log.info(sb.toString() + " --> " + nodeName); }
		theNode = this.parentNode.getChild(nodeName);
		return theNode;
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
