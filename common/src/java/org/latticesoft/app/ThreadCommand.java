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
 * Created on Apr 3, 2006
 *
 */
package org.latticesoft.app;
import org.latticesoft.command.*;
import org.latticesoft.util.container.*;
import org.latticesoft.util.common.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.util.concurrent.*;
import java.util.*;
import java.lang.reflect.*;

/**
 * ThreadCommand is a command that can execute all the child Runnable
 * in a thread. More than one thread can be executed 
 */
public class ThreadCommand implements Command, Identity {
	private static final Log log = LogFactory.getLog(ThreadCommand.class);
	private int threadCount = 1;
	private ArrayList jobs = new ArrayList();
	private String name;
	private String id;
	private long timeout;
	private TimeUnit timeoutUnit = TimeUnit.MILLISECONDS;
	private boolean waitFor;
	
	/** @return Returns the threadCount. */
	public int getThreadCount() { return (this.threadCount); }
	/** @param threadCount The threadCount to set. */
	public void setThreadCount(int threadCount) { this.threadCount = threadCount; }

	/** @return Returns the name. */
	public String getName() { return (this.name); }
	/** @param name The name to set. */
	public void setName(String name) { this.name = name; }

	/** @return Returns the id. */
	public String getId() { return (this.id); }
	/** @param id The id to set. */
	public void setId(String id) { this.id = id; }
	
	/** @return Returns the timeout. */
	public long getTimeout() { return (this.timeout); }
	/** @param timeout The timeout to set. */
	public void setTimeout(long timeout) { this.timeout = timeout; }
	
	/** @return Returns the timeoutUnit. */
	public TimeUnit getTimeoutUnit() { return (this.timeoutUnit); }
	/** @param timeoutUnit The timeoutUnit to set. */
	public void setTimeoutUnit(TimeUnit tu) { this.timeoutUnit = tu; }
	
	/** 
	 * Sets the timeint as a string. The method will auto convert to the
	 * actual enum type. It is not case-sensitive.
	 */
	public void setTimeoutUnitString(String s) {
		if (s == null) return;
		try {
			s = s.trim();
			if (s.indexOf("TimeUnit") == 0) {
				s = s.substring("TimeUnit".length());
			} else if (s.indexOf("TimeUnit.") == 0) {
				s = s.substring("TimeUnit.".length());
			}
			Field[] f = TimeUnit.class.getDeclaredFields();
			for (int i=0; i<f.length; i++) {
				String name = f[i].getName();
				if (name != null && name.equalsIgnoreCase(s)) {
					Object o = f[i].get(TimeUnit.class);
					if (o instanceof TimeUnit) {
						this.setTimeoutUnit((TimeUnit)o);
if (log.isDebugEnabled()) { log.debug(this.getTimeoutUnit()); }
						break;
					}
				}
			}
		} catch (Exception e) {
			if (log.isErrorEnabled()) { log.error(e); }
		}
	}

	/** @return Returns the waitFor.*/
	public boolean isWaitFor() { return (this.waitFor); }
	/** @param waitFor The waitFor to set. */
	public void setWaitFor(boolean waitFor) { this.waitFor = waitFor; }

	/** @return Returns the works. */
	public List getJobs() { return (this.jobs); }
	/** @param jobs The jobs to set. */
	public void setJobs(List l) {
		if (l != null && l.size() > 0) {
			this.jobs.addAll(l);
		}
	}
	
	/**
	 * Adds an object to the command. However, only a Runnable or Runnable Command 
	 * is accepted. 
	 * @param o object the object to be added
	 */
	public boolean add(Object o) {
		if (o == null) {
			return false;
		}
		if (o instanceof Command && o instanceof Runnable) {
			return this.jobs.add(o);
		} else if (o instanceof Runnable) {
			return this.jobs.add(o);
		} else { 
			// do nothing
		}
		return false;
	}
	
	public Object execute(Object o) throws CommandException {
		Map map = null;
		if (o != null && o instanceof Map) {
			map = (Map)o;
		} else {
			map = new HashMap();
		}
		try {
			ExecutorService es = Executors.newFixedThreadPool(threadCount);
			if (log.isInfoEnabled()) { log.info("JobSize: " + this.jobs.size()); }
			for (int i=0; i<jobs.size(); i++) {
				Runnable r = (Runnable)jobs.get(i);
				if (r instanceof BeanCommand) {
					BeanCommand cmd = (BeanCommand)r;
					cmd.setExecuteParam(map);
				}
				es.execute(r);
			}
			if (this.waitFor && this.timeout > 0 && this.timeoutUnit != null) {
				es.shutdown();
				long l = System.currentTimeMillis();
if (log.isInfoEnabled()) { log.info("Begin of waiting..."); }
				es.awaitTermination(this.timeout, this.timeoutUnit);
				long l2 = System.currentTimeMillis();
if (log.isInfoEnabled()) { log.info("End of waiting..." + (l2 - l)); }
			}
			//es.shutdown();
		} catch (Exception e) {
			if (log.isErrorEnabled()) { log.error(e); }
		}
if (log.isDebugEnabled()) { log.debug("end!"); }
		return map;
	}

	public String toString() {
		return StringUtil.formatObjectToString(this, true);
	}
	
	public static void main(String[] args) {
		if (args == null || args.length == 0 || args[0] == null) {
			return;
		}
		PropertyMap.singletonize(args[0]);
		PropertyMap pm = PropertyMap.getInstance();
		ThreadCommand app = new ThreadCommand();
		app.execute(pm);
/*
		app.add(new Runnable() {
			public void run() {
				try {
				for (int i=1; i<=10; i++) {
					System.out.println("Thread 1 here :" + i);
					Thread.sleep(1000);
				}
				} catch (Exception e) {}
			}
		});
		app.add(new Runnable() {
			public void run() {
				try {
				for (int i=1; i<=10; i++) {
					System.out.println("Thread 2 here :" + i);
					Thread.sleep(1000);
				}
				} catch (Exception e) {}
			}
		});
		app.add(new Runnable() {
			public void run() {
				try {
				for (int i=1; i<=10; i++) {
					System.out.println("Thread 3 here :" + i);
					Thread.sleep(1000);
				}
				} catch (Exception e) {}
			}
		});
		app.add(new Runnable() {
			public void run() {
				try {
				for (int i=1; i<=10; i++) {
					System.out.println("Thread 4 here :" + i);
					Thread.sleep(1000);
				}
				} catch (Exception e) {}
			}
		});
		app.add(new Runnable() {
			public void run() {
				try {
				for (int i=1; i<=10; i++) {
					System.out.println("Thread 5 here :" + i);
					Thread.sleep(1000);
				}
				} catch (Exception e) {}
			}
		});
		app.setThreadCount(1);
		app.setWaitFor(true);
		app.setTimeout(30);
		app.setTimeoutUnit(TimeUnit.SECONDS);
		app.execute(pm);
		System.out.println("Main done.");//*/
	}
}

