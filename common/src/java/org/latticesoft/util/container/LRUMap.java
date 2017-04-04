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
import java.util.HashMap;
import java.util.Iterator;
import java.util.ArrayList;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

/**
 * This is an implementation of the Least Recently Used algorithm.
 */
public class LRUMap extends ResetMap {

	public static final long serialVersionUID = 1;
	private static final Log log = LogFactory.getLog(LRUMap.class);

	protected long interval;
	protected int count = 0;
	protected int maxCount = -1;
	protected Map recentHit = new HashMap();

	/** Default constructor */
	public LRUMap() {
		if (log.isDebugEnabled()) { log.debug("LRUMap"); }
	}

	/**
	 * Constructs the object
	 * @param interval the interval difference allowed before reseting the cache
	 */
	public LRUMap(long interval) {
		this.setInterval(interval);
	}

	/**
	 * Constructs the object
	 * @param maxCount the maximum count before reset
	 */
	public LRUMap(int maxCount) {
		this.setMaxCount(maxCount);
	}

	/**
	 * Constructs the object
	 * @param processOff to turn off the processing
	 */
	public LRUMap(boolean processOff) {
		this.setProcessOff(processOff);
	}

	/**
	 * Constructs the object
	 * @param interval the interval difference allowed before reseting the cache
	 * @param maxCount the maximum count before reset
	 * @param processOff to turn off the processing
	 */
	public LRUMap(long interval, int maxCount, boolean processOff) {
		this.setInterval(interval);
		this.setMaxCount(maxCount);
		this.setProcessOff(processOff);
	}

	/** Returns the interval */
	public long getInterval() { return (this.interval); }
	/** Sets the interval */
	public void setInterval(long interval) { this.interval = interval; }

	/** Gets the internal counter */
	public int getCount() { return (this.count); }

	/** Gets the maximum counter before processing is executed */
	public int getMaxCount() { return (this.maxCount); }
	/** Sets the maximum counter before processing is executed */
	public void setMaxCount(int maxCount) { this.maxCount = maxCount; }

	public void clear() {
		super.clear();
		recentHit.clear();
	}

	/**
	 * Implements the least recently used caching algorithm.
	 */
	protected void process(Object key) {

		if (count++ < maxCount) {
			return;
		} else {
			count = 0;
		}

		long prev = 0;
		long curr = System.currentTimeMillis();
		recentHit.put(key, new Long(curr));

		ArrayList a = new ArrayList();
		a.addAll(recentHit.keySet());

		Iterator iter = a.iterator();
		while (iter.hasNext()) {
			Object o = iter.next();
			Long l = (Long)recentHit.get(o);
			if (l == null) {
				prev = curr;
			} else {
				prev = l.longValue();
			}
			//long diff = curr - prev;

			if ((curr - prev) >= this.interval) {
				super.map.remove(o);
				recentHit.remove(o);
			}
		}
	}
/*
	public static void main(String args[]){

		Map map = new LRUMap(1000, 3, false);

		java.util.Random rand = new java.util.Random(System.currentTimeMillis());

		for (int j=0; j<10; j++) {
			if (j%3 == 0) {
				map.clear();
				map.put("1", "one");
				map.put("2", "two");
				map.put("3", "three");
				map.put("4", "four");
				map.put("5", "five");
				map.put("6", "six");
				map.put("7", "seven");
				map.put("8", "eight");
				map.put("9", "nine");
				map.put("10", "ten");
			}

			for (int i=1; i<=10; i++) {
				Object o = map.get("" + i);
				int size = map.size();
				System.out.print("[");
				System.out.print(size);
				System.out.print("] ");
				System.out.print(i);
				System.out.print("=");
				System.out.print(o);
				System.out.print("      ");
				System.out.print(map);
				System.out.println("");

			}
			try {Thread.sleep(rand.nextInt(1000) + 1000);} catch (Exception e) {}
			System.out.println("==========");
		}
	}//*/
}