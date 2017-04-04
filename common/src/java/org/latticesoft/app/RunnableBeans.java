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
 * Created on Apr 11, 2006
 *
 */
package org.latticesoft.app;
import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.latticesoft.command.*;
import org.latticesoft.util.common.*;

/**
 * For RunnableBeans this is a version which contains BeanCommand.
 * This is so that you can run several methods on the same bean
 * Why not a param, you might ask? It is because that each method 
 * might take different parameter. So in order to cater for multi-method
 * invocation we need to have a container of BeanCommand that work that
 * work at the same bean. This is the container...
 * 
 * With regards to the output, this will retrieved the return values of
 * the child BeanCommand, stored in a list and finally set inside the
 * map passed in by the execute method. If the method invoked is run instead
 * of the Command method execute, the output can obtain by getReturnValue 
 * accessor method of retunValue attribute. (There is no mutator method.
 * 
 * All the child BeanCommand act on the same bean which is set inside 
 * this parent container.
 * @deprecated
 * @see BeanCommand
 */
public class RunnableBeans implements Runnable, Collection, Command {

	private static final Log log = LogFactory.getLog(RunnableBeans.class);
	private String id;
	private Object bean;
	private int state;
	private Object param;
	private String outputName;
	private ArrayList returnValue = new ArrayList();
	private ArrayList childs = new ArrayList();
	
	public RunnableBeans() {}
	public RunnableBeans(Object bean) {
		this.setBean(bean);
	}
	
	/** @return Returns the bean. */
	public Object getBean() { return (this.bean); }
	/** @param bean The bean to set. */
	public void setBean(Object bean) { this.bean = bean; }

	/** @return Returns the id. */
	public String getId() { return (this.id); }
	/** @param id The id to set. */
	public void setId(String id) { this.id = id; }

	/** @return Returns the state. */
	public int getState() { return (this.state); }
	/** @param state The state to set. */
	public void setState(int state) { this.state = state; }

	/** @return Returns the param. */
	public Object getParam() { return (this.param); }
	/** @param param The param to set. */
	public void setParam(Object param) { this.param = param; }

	/** @return Returns the outputName. */
	public String getOutputName() { return (this.outputName); }
	/** @param outputName The outputName to set. */
	public void setOutputName(String outputName) { this.outputName = outputName; }

	/** @return Returns the returnValue. */
	public ArrayList getReturnValue() { return (this.returnValue); }

	public void run() {
		if (childs.size() >= 1) {
			for (int i=0; i<childs.size(); i++) {
				Object child = childs.get(i);
				// populate the bean attribute
				if (this.bean != null) {
					BeanUtil.setAttribute(child, "bean", this.bean);
				}
				if (this.param != null) {
					BeanUtil.setAttribute(child, "param", this.param);
				}
				if (child instanceof Runnable) {
					Runnable r = (Runnable)child;
					r.run();
				}
				Object retVal = BeanUtil.getAttribute(child, "returnValue");
				if (retVal != null) {
					this.returnValue.add(retVal);
				}
			}
		}
	}
	
	public Object execute(Object o) throws CommandException {
		this.returnValue.clear();
		if (o != null) this.param = o;
		this.run();
		if (this.outputName != null && this.returnValue.size() > 0 && o != null) {
			if (o instanceof Map) {
				Map map = (Map)o;
				map.put(this.outputName, this.returnValue);
			}
		}
		return this.returnValue;
	}
	
	public boolean addChild(Runnable r) {
		if (r == null) return false;
		BeanUtil.setAttribute(r, "bean", this.bean);
		return this.childs.add(r);
	}
	public boolean add(Object o) {
		if (o != null) { 
			if (o instanceof Runnable) {
if (log.isDebugEnabled()) { log.debug("adding " + o); }
				return this.addChild((Runnable)o);
			} else {
				this.setBean(o);
				return true;
			}
		}
		return false;
	}
	public boolean addAll(Collection c) {
		Iterator iter = c.iterator();
		int count = 0;
		while (iter.hasNext()) {
			Object o = iter.next();
			if (this.add(o)) {
				count++;
			}
		}
		return (count > 0);
	}
	public void clear() { this.childs.clear(); }
	public int size() { return this.childs.size(); }
	public Object remove(int index) { return this.childs.remove(index); }
	public boolean contains(Object o) { return this.childs.contains(o); }
	public boolean containsAll(Collection c) { return this.childs.containsAll(c); }
	public boolean isEmpty() { return this.childs.isEmpty(); }
	public Iterator iterator() { return this.childs.iterator(); }
	public boolean remove(Object o) { return this.childs.remove(o); }
	public boolean removeAll(Collection c) { return this.childs.removeAll(c); }
	public boolean retainAll(Collection c) { return this.childs.retainAll(c); }
	public Object[] toArray() { return this.childs.toArray(); }
	public Object[] toArray(Object[] a) { return this.childs.toArray(a); }
	
	public String toString() {
		return StringUtil.formatObjectToXmlString(this);
	}

	public static void main(String[] args) {
		org.latticesoft.util.common.TestBean bean = new org.latticesoft.util.common.TestBean();
		bean.setName("Johann");
		bean.setMessage("How are you?");
		
		RunnableBeans rb = new RunnableBeans();
		BeanCommand rb1 = new BeanCommand();
		BeanCommand rb2 = new BeanCommand();
		
		rb1.setBean(bean);
		rb1.setId("One");
		rb1.setMethod("sayHello");
		rb1.run();
		System.out.println(rb1.getReturnValue());
		
		rb2.setMethod("sayHello2");
		rb2.setBean(bean);
		rb2.setId("Two");
		rb2.getParams().add("I am the best.");
		rb2.run();
		System.out.println(rb2.getReturnValue());

		rb.setBean(bean);
		rb.add(rb1);
		rb.add(rb2);
		rb.setId("Zero");
		rb.run();
		
		System.out.println("=====");
		Iterator iter = rb.iterator();
		while (iter.hasNext()) {
			Object iterObj = iter.next();
			if (iterObj != null && iterObj instanceof BeanCommand) {
				BeanCommand r = (BeanCommand)iterObj;
				System.out.println(r.getReturnValue());
			}
		}
	}

}
