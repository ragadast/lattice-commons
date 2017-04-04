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

import java.util.Map;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

/**
 * This class will reset the map content when the internal counter
 * has reached the maximum allow count.
 */
public class CountMap extends ResetMap {

	public static final long serialVersionUID = 1;
	private final static Log log = LogFactory.getLog(CountMap.class);
	private int count = 0;
	private int maxCount = 0;

	/** Default constructor */
	public CountMap() {
		if (log.isDebugEnabled()) { log.debug("CountMap"); }
	}

	/**
	 * Constructs the object
	 * @param maxCount the maximum count before reset
	 */
	public CountMap(int maxCount) {
		this.maxCount = maxCount;
	}

	/** Returns the maximum count */
	public int getMaxCount() { return (this.maxCount); }
	/** Sets the maximum count */
	public void setMaxCount(int maxCount) { this.maxCount = maxCount; }

	/** Returns the existing count before reseting */
	public int getCount() { return this.count; }

	protected void process(Object key) {
		if (++count > this.maxCount) {
			super.map.clear();
			count = 0;
		}
	}

	public static void main(String args[]){
		Map map = new CountMap(5);
		map.put("1", "one");
		map.put("2", "two");
		map.put("3", "three");
		map.put("4", "four");
		map.put("5", "five");
		map.put("6", "six");
		map.put("7", "seven");

		for (int i=0; i<=7; i++) {
			System.out.print("[");
			System.out.print(map.size());
			System.out.print(",");
			System.out.print(((CountMap)map).getCount());
			System.out.print("] ");
			System.out.print(i);
			System.out.print("=");
			System.out.print(map.get("" + i));
			System.out.println("");
		}
	}
}
