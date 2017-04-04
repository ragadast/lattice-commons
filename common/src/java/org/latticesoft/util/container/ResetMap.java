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

import org.latticesoft.util.container.OrderedMap;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

/**
 * <p>The map is a self resetable Map. This map is used
 * in maintaining the balance between caching speed and
 * memory usage.
 * <p>The only method which requires implementation is
 * the process method. The method is the algorithm of
 * reseting the cache method.
 */
public abstract class ResetMap extends OrderedMap {

	private static final Log log = LogFactory.getLog(ResetMap.class);
	protected boolean processOff = false;

	/** Gets the process off flag */
	public boolean getProcessOff() { return (this.processOff); }
	/** Sets the process off flag */
	public void setProcessOff(boolean processOff) { this.processOff = processOff; }

	/**
	 * Returns the value in the cache. It resets after the get count
	 * exceeds the max count
	 * @return the value represented by the key
	 */
	public Object get(Object key) {
		if (log.isDebugEnabled()) { log.debug(""); }
		Object value = null;
		if (key != null) {
			value = super.get(key);
		}
		if (!this.processOff) {
			this.process(key);
		}
		return value;
	}

	/**
	 * Processes the caching mechanism.
	 */
	protected abstract void process(Object key);
}