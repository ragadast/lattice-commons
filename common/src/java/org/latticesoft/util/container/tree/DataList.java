/*
 * Copyright Jurong Port Pte Ltd
 * Created on Nov 16, 2007
 */
package org.latticesoft.util.container.tree;

import java.util.List;

public interface DataList {
	/** Adds the data */
	public void addData(Object o);
	
	/** Return the aggregated data list */
	public List getDataList();
	
	/** Return the object at the index */
	public Object getData(int index);
}
