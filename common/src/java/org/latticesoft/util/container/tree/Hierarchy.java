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
 */package org.latticesoft.util.container.tree;

import java.util.List;

/**
 * An Hierarchy defines a node interface in a tree.
 * 
 */
public interface Hierarchy {
	
	/**
	 * Gets the parent node
	 * @return the parent of this node
	 */
	public Node getParent();
	/**
	 * Sets the parent node.
	 * @param parent the parent container of this node
	 */
	public void setParent(Node parent);
	
	/**
	 * Adds a child container to the current node. Cyclic addition is not
	 * allowed. The container will check for cyclic addition for actual
	 * objects within in the hierarchy. Clones of the hierarchical
	 * objects cannot be checked.
	 * @param child the child node to be added
	 * @return true if the addition is successful
	 */
	public boolean addChild(Hierarchy child);
	
	/** Returns the list of all the children */
	public List getChildList();

	/**
	 * Get the child by a specified index. The index must be in range
	 * @param index the specified index
	 * @return the child specified
	 */
	public Node getChildByIndex(int index);

	/**
	 * Get the child by name
	 * @param name the specified name
	 * @return the child specified
	 */
	public Node getChildByName(String s);

	/**
	 * Get the child by a specified index. The index must be in range
	 * @param index the specified index
	 * @return the child specified
	 */
	public Node getChild(int index);
	/**
	 * Get the child by name
	 * @param name the specified name
	 * @return the child specified
	 */
	public Node getChild(String name);



	/**
	 * Returns a hierarchical list of the parents containing the
	 * current node. Not that all sibling branches will not be
	 * include in the list.
	 * The list contains the actual object and thus the list
	 * not for updating the hierarchy within the node.
	 * @return list of parent nodes
	 */
	public List getHierarchicalList();
	
	/** Returns the hierarchical index information */
	public int[] getHierarchicalListIndex();

	/** Returns the hierarchical name information */
	public String getHierarchicalListName();

	/**
	 * Return the index of the current node with reference from the same parent
	 */
	public int getHorizontalIndex();

	/**
	 * Returns the index of the current node with reference from the vertical hierarchy
	 */
	public int getVerticalIndex();
	
}
