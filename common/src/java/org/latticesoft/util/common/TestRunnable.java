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
 *
 * Created on Apr 11, 2007
 *
 */
package org.latticesoft.util.common;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TestRunnable implements Runnable {
	private static final Log log = LogFactory.getLog(TestRunnable.class);
	private int sleepCount = 10;
	private int id = 0;

	/** @return Returns the id. */
	public int getId() { return (this.id); }
	/** @param id The id to set. */
	public void setId(int id) { this.id = id; }
	/** @return Returns the sleepCount. */
	public int getSleepCount() { return (this.sleepCount);}
	/** @param sleepCount The sleepCount to set. */
	public void setSleepCount(int sleepCount) { this.sleepCount = sleepCount; }

	public TestRunnable() {
		
	}
	public TestRunnable(int id, int sleepCount) {
		this.id = id;
		this.sleepCount = sleepCount;
	}

	public void run() {
		for (int i=0; i<this.sleepCount; i++) {
			if (log.isInfoEnabled()) {
				log.info(this.id + " : " + i + "/" + this.sleepCount);
			}
			try { Thread.sleep(1000); } catch (Exception e) {}
		}
		if (log.isInfoEnabled()) { log.info(this.id + " : Done."); }
	}

	public static void main(String[] args) {
		TestRunnable r = new TestRunnable(1, 10);
		Thread t = new Thread(r);
		t.start();
		System.out.println("Main thread done");
	}
}
