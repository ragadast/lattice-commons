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
package org.latticesoft.util.common;

import java.util.Map;
import java.util.HashMap;

public class ThreadUtil extends ThreadLocal {

	private static final String TIMER_START = "ThreadUtil_timerStart";
	private static final String TIMER_STOP = "ThreadUtil_timerStop";
	/**
	 * Private constructor
	 */
	private ThreadUtil() {}

	/**
	 * @see ThreadLocal#initialValue
	 */
	protected Object initialValue() {
		Map map = new HashMap();
		map.put("Count", new Integer(-1));
		return map;
	}

	public Map getMap() {
		return (Map)super.get();
	}

	private static ThreadUtil map = new ThreadUtil();

	public static Object get(Object key) {
		return map.getMap().get(key);
	}

	public static void clear() {
		map.getMap().clear();
	}

	public static void put(Object key, Object o) {
		map.getMap().put(key, o);
	}

	public static int getCount() {
		Integer i = (Integer)map.getMap().get("Count");
		return i.intValue();
	}

	public static int incrementCount() {
		Integer i = (Integer)map.getMap().get("Count");
		int cnt = i.intValue();
		cnt++;
		map.getMap().put("Count", new Integer(cnt));
		return cnt;
	}

	public static int decrementCount() {
		Integer i = (Integer)map.getMap().get("Count");
		int cnt = i.intValue();
		cnt--;
		map.getMap().put("Count", new Integer(cnt));
		return cnt;
	}
	
	public static void startTimer() {
		Long start = new Long(System.currentTimeMillis());
		map.getMap().put(TIMER_START, start);
	}

	public static void stopTimer() {
		Long stop = new Long(System.currentTimeMillis());
		map.getMap().put(TIMER_STOP, stop);
	}

	public static long getTime() {
		return getTime(true);
	}
	public static long getTime(boolean reset) {
		Long start = (Long)map.getMap().get(TIMER_START);
		Long stop = (Long)map.getMap().get(TIMER_STOP);
		long diff = 0;
		if (start != null && stop != null) {
			diff = stop.longValue() - start.longValue();
		}
		if (reset) {
			map.getMap().remove(TIMER_START);
			map.getMap().remove(TIMER_STOP);
		}
		return diff;
	}
	
	public static boolean sleep(long delay) {
		try {
			Thread.sleep(delay); 
		} catch(Exception e){
			return false;
		}
		return true;
	}
	
	public static boolean randomSleep(long max) {
		try {
			long l = NumeralUtil.getRandomLong() % max;
			Thread.sleep(l); 
		} catch(Exception e){
			return false;
		}
		return true;
	}
	
	public static void main (String args[]){
		Thread t = new Thread(new Runnable() {
			public void run(){
				ThreadUtil.startTimer();
				java.util.Random rand = new java.util.Random(System.currentTimeMillis());
				for (int i=0; i<10; i++) {
					int sleep = rand.nextInt(500);
					System.out.println ("Thread1:" + sleep + ":" + ThreadUtil.incrementCount());
					try { Thread.sleep(sleep); } catch (Exception ex) {}
				}
				ThreadUtil.stopTimer();
				System.out.println("Total Time (1): " + ThreadUtil.getTime());
			}
		});

		Thread t2 = new Thread(new Runnable() {
			public void run(){
				java.util.Random rand = new java.util.Random(System.currentTimeMillis());
				ThreadUtil.startTimer();
				for (int i=0; i<10; i++) {
					int sleep = rand.nextInt(700);
					System.out.println ("Thread2:" + sleep + ":" + ThreadUtil.incrementCount());
					try { Thread.sleep(sleep); } catch (Exception ex) {}
				}
				ThreadUtil.stopTimer();
				System.out.println("Total Time (2): " + ThreadUtil.getTime());
			}
		});
		t.start();
		t2.start();

		
		
		
	}
}