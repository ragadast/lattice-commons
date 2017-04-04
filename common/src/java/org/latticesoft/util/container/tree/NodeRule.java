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
package org.latticesoft.util.container.tree;

/**
 * A Node Rule allows a data's attribute value to map to certain node.
 * DataObject.attribute --(NodeRule)--> Node
 * 
 */
public interface NodeRule {

	/** Locate the node which is supposed to add the data */
	Node findNode(Object data);
	

	/** 
	 * Check if the working mode is dynamic or static. Dynamic rules will
	 * add new mapping when a data is not found and return the newly created
	 * node. Whereas static rules will just return a null value. By default
	 * a ranged rule will be static in nature because it is impossible to create
	 * a dynamic range unless there is some boundary setting algorithm setting
	 * built in the rule. On the other hand, discrete mapping rules can be dynamic 
	 * or static in nature. 
	 **/
	boolean isDynamic();
	
	/**
	 * Check is the working mode is static. This should be complementary to
	 * the above method. This method is added as a matter of convenience.
	 */
	boolean isStatic();
}
