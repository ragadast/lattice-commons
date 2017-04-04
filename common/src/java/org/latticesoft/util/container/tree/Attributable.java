/*
 * Copyright Jurong Port Pte Ltd
 * Created on Nov 16, 2007
 */
package org.latticesoft.util.container.tree;
import java.util.Map;

public interface Attributable {
	public Object getAttribute(String key);
	public void setAttribute(String key, Object value);
	public Map getAllAttribute();
}
