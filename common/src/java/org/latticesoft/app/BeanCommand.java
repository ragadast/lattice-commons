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
 * Created on Apr 4, 2006
 *
 */
package org.latticesoft.app;

import java.util.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.beanutils.*;
import org.latticesoft.command.*;
import org.latticesoft.util.common.*;

/**
 * <p>
 * BeanCommand wraps a bean and invoke the method of
 * a bean while implementing a Runnable and Command interface.
 * The basic function of the command is to link the java bean
 * concept with the command concept.
 * </p><p>
 * For javabean basically u set in the state values into the
 * javabean object and invoke the desired method in order to 
 * do something useful. The bean's instancevariable is usually
 * the dedicated type for the variable and not just string form
 * of the value.
 * </p><p>
 * For command pattern it is similar but the values is extracted 
 * from the object passed into the execute method. So usually
 * command objects will contain how and which variable is extracted
 * from the object passed into (which is usually to be a Map).
 * Thus the Command object's variables is usually a string which
 * is more versatile as it can either contain the actual value or
 * where to extract the value from (the Map).
 * NB: Command interface does not be so. The interface is made quite
 * versatile. 
 * </p>
 * Example Code:
 * <pre>
 * <code>
 * MyBean bean = new MyBean();
 * BeanCommand cmd = new BeanCommand(bean);
 * cmd.setMethod("beanMethodWithoutParam");
 * cmd.setOutputAttributeName("theOutput");
 * Map map = new HashMap(); 
 * cmd.execute(map);
 * System.out.println(map.get("theOutput"));
 * </code>
 * </pre>
 * <p>
 * If more one method is to be invoked in the same bean, you can nest
 * the Command and add to the parent command. The bean does not need to
 * be set into the child Command. The child Command will know where to
 * locate the bean (from the parent). The parent's method will be invoked
 * first followed by the child's method in sequence.
 * </p>
 * Example Code:
 * <pre>
 * <code>
 * MyBean bean = new MyBean();
 * BeanCommand mainCmd = new BeanCommand(bean);
 * BeanCommand child1 = new BeanCommand();
 * BeanCommand child2 = new BeanCommand();
 * mainCmd.add(child1);
 * mainCmd.add(child2);
 * child1.setMethod("beanMethod1WithoutParam");
 * child1.setOutputAttributeName("theOutput1");
 * child2.setMethod("beanMethod2WithoutParam");
 * child2.setOutputAttributeName("theOutput2");
 * Map map = new HashMap(); 
 * cmd.execute(map);
 * System.out.println(map.get("theOutput"));
 * </code>
 * </pre>
 * <p>
 * In the above example, the main BeanCommand does not have any
 * method to invoked. For the child, the method <code>beanMethod1WithoutParam()</code>
 * will be invoked first followed by <code>beanMethod2WithoutParam()</code>.
 * </p>
 * <p>
 * In addition the BeanCommand implements a Runnable interface
 * (i.e. it can be run in a thread). However the Runnable does not
 * have an input as compared to the execute method which takes in
 * an Object and returns an Object. Thus in order for the runnable
 * to respond to the same way the input param needs to be set and
 * the returned value extracted at the end of the run method. 
 * </p>
 * Example Code:
 * <pre>
 * <code>
 * TestBean bean = new TestBean();
 * BeanCommand cmd = new BeanCommand(bean);
 * cmd.setMethod("beanMethod1WithoutParam");
 * cmd.setOutputAttributeName("cmdOutput");
 * cmd.setInputAttributeName("cmdInput");
 * 
 * Map map = new HashMap();
 * map.put("cmdInput.name", "John Williams");
 * map.put("cmdInput.message", "I used to play a Fleta, now playing Smallman.");
 * cmd.setExecuteParam(map);
 * 
 * Thread t = new Thread(cmd);
 * t.start();
 * ThreadUtil.sleep(10000);
 * System.out.println(map.get("cmdOutput"));
 * System.out.println(cmd.getReturnValue());
 * </code>
 * </pre>
 * 
 * <p>
 * The BeanCommand has callback command to execute once its run method 
 * is completed. It serves to update the caller when the thread has finished.
 * However, the invoking directly the execute method will not invoke the 
 * call back method.
 * </p>
 */
public class BeanCommand implements Identity, Runnable, Command {

	private static final Log log = LogFactory.getLog(BeanCommand.class);
	private String id;
	private String name;
	private String method;
	private Object bean;
	private String inputName;
	private String outputName;
	private ArrayList params = new ArrayList();
	private BeanCommand parent = null;
	
	private ArrayList childs = new ArrayList();
	private Map childsNameMap = new HashMap();
	private Map childsIdMap = new HashMap();

	private Map returnValue = new HashMap();
	private Object executeParam = new HashMap();
	
	private Command callBackCmd = null;
	
	public BeanCommand() {}
	public BeanCommand(String name, String id) {
		this.setName(name);
		this.setId(id);
	}
	public BeanCommand(Object bean) {
		this.setBean(bean);
	}
	public BeanCommand(Object bean, String method) {
		this.setBean(bean);
		this.setMethod(method);
	}
	public BeanCommand(String name, String id, Object bean) {
		this.setName(name);
		this.setId(id);
		this.setBean(bean);
	}
	public BeanCommand(String name, String id, Object bean, String method) {
		this.setName(name);
		this.setId(id);
		this.setBean(bean);
		this.setMethod(method);
	}
	public BeanCommand(String name, String id, Object bean, String method, String inputName, String outputName) {
		this.setName(name);
		this.setId(id);
		this.setBean(bean);
		this.setMethod(method);
		this.setInputName(inputName);
		this.setOutputName(outputName);
	}
	public BeanCommand(String name, String id, Object bean, String method, String inputName, String outputName, List params) {
		this.setName(name);
		this.setId(id);
		this.setBean(bean);
		this.setMethod(method);
		this.setInputName(inputName);
		this.setOutputName(outputName);
		this.setParams(params);
	}
	/** @return Returns the id. */
	public String getId() { return (this.id); }
	/** @param id The id to set. */
	public void setId(String id) { this.id = id; }
	
	/** @return Returns the name. */
	public String getName() { return (this.name); }
	/** @param name The name to set. */
	public void setName(String name) { this.name = name; }

	/** @return Returns the bean. */
	public Object getBean() {
		if (this.bean == null && this.parent != null) {
			return this.parent.getBean();
		}
		return (this.bean);
	}
	/** @param bean The bean to set. */
	public void setBean(Object bean) { this.bean = bean; }

	/** @return Returns the method. */
	public String getMethod() { return (this.method); }
	/** @param method The method to set. */
	public void setMethod(String method) { this.method = method; }

	/** @return Returns the outputName. */
	public String getOutputName() {
		if (this.outputName == null && this.parent != null) {
			return this.parent.getOutputName();
		}
		return (this.outputName);
	}
	/** @param outputName The outputName to set. */
	public void setOutputName(String name) { this.outputName = name; }

	/** @return Returns the inputName. */
	public String getInputName() {
		if (this.inputName == null && this.parent != null) {
			return this.parent.getInputName();
		}
		return (this.inputName);
	}
	/** @param inputName The inputName to set. */
	public void setInputName(String name) { this.inputName = name; }
	
	/** @return Returns the parent. */
	public BeanCommand getParent() { return (this.parent); }
	/** @param parent The parent to set. */
	public void setParent(BeanCommand parent) { this.parent = parent; }

	/** @return Returns the params. */
	public List getParams() { return (this.params); }
	/** @param params The params to set. */
	public void setParams(List l) {
		if (l == null) return;
		if (l.size() > 0) {
			this.params.addAll(l);
		}
	}

	/** @return Returns the returnValue. */
	public Object getReturnValue() { return (this.returnValue); }
	/** @return Returns the executeMap. */
	public Object getExecuteParam() { return (this.executeParam); }
	/** @param executeMap The executeMap to set. */
	public void setExecuteParam(Object o) { this.executeParam = o; }

	/** @return Returns the callBackCmd. */
	public Command getCallBackCmd() {
		return (this.callBackCmd);
	}
	/** @param callBackCmd The callBackCmd to set. */
	public void setCallBackCmd(Command callBackCmd) {
		this.callBackCmd = callBackCmd;
	}

	/** Adds a child */
	public boolean addChild(String name, String id) {
		BeanCommand cmd = new BeanCommand(name, id);
		return this.addChild(cmd);
	}
	/** Adds a child */
	public boolean addChild(Object bean, String method) {
		BeanCommand cmd = new BeanCommand(bean, method);
		return this.addChild(cmd);
	}
	/** Adds a child */
	public boolean addChild(String name, String id, Object bean, String method) {
		BeanCommand cmd = new BeanCommand(name, id, bean, method);
		return this.addChild(cmd);
	}
	/** Adds a child */
	public boolean addChild(String name, String id, Object bean, String method, String inputName, String outputName) {
		BeanCommand cmd = new BeanCommand(name, id, bean, method, inputName, outputName);
		return this.addChild(cmd);
	}
	/** Adds a child */
	public boolean addChild(String name, String id, Object bean, String method, String inputName, String outputName, List params) {
		BeanCommand cmd = new BeanCommand(name, id, bean, method, inputName, outputName, params);
		return this.addChild(cmd);
	}
	/** Adds a child */
	public boolean addChild(BeanCommand cmd) {
		if (cmd != null) {
			cmd.setParent(this);
			if (cmd.getId() != null) {
				this.childsIdMap.put(cmd.getId(), cmd);
			}
			if (cmd.getName() != null) {
				this.childsNameMap.put(cmd.getName(), cmd);
			}
			return this.childs.add(cmd);
		}
		return false;
	}
	public BeanCommand getChild (int index) {
		if (index >= 0 && index < this.childs.size()) {
			return (BeanCommand)this.childs.get(index);
		}
		return null;
	}
	public BeanCommand getChildByName (String name) {
		if (name != null && this.childsNameMap.containsKey(name)) {
			return (BeanCommand)this.childsNameMap.get(name);
		}
		return null;
	}
	public BeanCommand getChildById (String id) {
		if (name != null && this.childsIdMap.containsKey(name)) {
			return (BeanCommand)this.childsIdMap.get(id);
		}
		return null;
	}
	public int size() {
		return this.childs.size();
	}
	
	
	
	/** Adds an object */
	public boolean add(Object o){
		if (o == null) return false;
		if (o instanceof BeanCommand){
			this.addChild((BeanCommand)o);
			return true;
		} else if (o instanceof Parameter) {
			Parameter p = (Parameter)o;
			try {
				Object param = ClassUtil.newInstance(p.getType());
				BeanUtil.setAttribute(param, p.getName(), p.getValue());
				this.params.add(param);
			} catch (Exception e) {
			}
		} else {
			if (this.bean == null) {
				this.setBean(o);
				return true;
			}
		}
		return false;
	}
	
	/** Reset the state of the command */
	public void reset() {
		this.returnValue.clear();
	}

	public void run() {
		this.execute(this.executeParam);
		if (this.callBackCmd != null) {
			this.callBackCmd.execute(this.executeParam);
		}
	}
	
	/** Adds all the value from the child command to the parent command */
	private void addReturnValueFromChild(BeanCommand cmd) {
		Map childMap = null;
		StringBuffer sb = new StringBuffer();
		String prefix = cmd.getId();
		String opName = cmd.getOutputName();
		if (prefix == null) {
			prefix = cmd.getName();
		}
		Object o = cmd.getReturnValue();
		if (o instanceof Map) {
			childMap = (Map)o;
			Iterator iter = childMap.keySet().iterator();
			while (iter.hasNext()) {
				Object key = iter.next();
				Object value = null;
				if (key != null) {
					value = childMap.get(key);
				}
				if (key != null && value != null && prefix != null) {
					sb.setLength(0);
					sb.append(prefix).append(".").append(key);
					this.returnValue.put(sb.toString(), value);
				}
			}
		} else if (opName != null) {
			sb.append(prefix).append(cmd.getOutputName());
			this.returnValue.put(sb.toString(), o);
		}
		
	}

	/** Populate the bean before invoking the method */
	private void populateBean(Map map) {
		try {
			Map m = null;
			if (inputName != null) {
				m = MiscUtil.getSubMap(this.getInputName()+".", map);
			}
			if (m == null) {
				m = map;
			}
			BeanUtils.populate(this.bean, m);
		} catch (Exception e) {
			if (log.isErrorEnabled()) { log.error(e); }
		}
	}
	
	/** Invoke the method on the bean */
	public void invokeMethod() {
		if (this.method == null) return;
		if (this.getBean() == null) return;
		try {
			Object o = null;
			if (params.size() == 1) {
				Object args = null;
				args = params.get(0);
				o = MethodUtils.invokeMethod(this.getBean(), method, args);
			} else if (params.size() > 1) {
				Object[] args = null;
				args = params.toArray();
				o = MethodUtils.invokeMethod(this.getBean(), method, args);
			} else {
				o = MethodUtils.invokeMethod(this.getBean(), method, null);
			}
			if (o != null && this.outputName != null) {
				this.returnValue.put(this.outputName, o);
			}
		} catch (Exception e) {
			if (log.isErrorEnabled()) { log.error("Invoking Method error", e); }
		}
	}
	
	public String toString() {
		return StringUtil.formatObjectToString(this, true);
	}
	
	public Object execute(Object o) throws CommandException {
		if (this.getBean() == null && this.getMethod() == null && this.childs.size() == 0) {
			if (log.isInfoEnabled()) { log.info("Null Bean stop execution"); }
			return null;
		}

		Map map = null;
		if (o != null && this.outputName != null) {
			if (o instanceof Map) {
				map = (Map)o;
			}
		}
		if (map == null) {
			map = new HashMap();
		}
		this.populateBean(map);
		this.invokeMethod();
		for (int i=0; i<this.childs.size(); i++) {
			BeanCommand child = (BeanCommand)this.childs.get(i);
			child.execute(o);
			this.addReturnValueFromChild(child);
		}
		if (this.returnValue.size() > 0) {
			map.putAll(this.returnValue);			
		}
		return this.returnValue;
	}

	public static void main(String[] args) {
		ArrayList param = new ArrayList();
		param.add("Parameters for method");
		Map map = new HashMap();
		TestBean b = new TestBean();
		BeanCommand c1 = new BeanCommand("c1", "c1", b, null, "bean", "c1");
		BeanCommand c2 = new BeanCommand("c2", "c2", null, "sayHello", null, "_c2");
		BeanCommand c3 = new BeanCommand("c3", "c3", null, "sayHello2", null, "_c3", param);
		c1.addChild(c2);
		c2.addChild(c3);

		map.put("bean.name", "John Williams");
		map.put("bean.message", "I am the best of the best.");
		
		c1.reset();
		c1.execute(map);
		System.out.println(map);
		System.out.println(c1.getReturnValue());
		System.out.println("==============");//*/
		//*/
		c1.setExecuteParam(map);
		c1.reset();
		b.setMessage("I am the best of the best.");
		b.setName("John Williams");
		c1.run();
		System.out.println(c1.getExecuteParam());
		System.out.println(c1.getReturnValue());
		
		c1.setCallBackCmd(new Command(){
			public Object execute(Object o) {
				System.out.println("I am done");
				return null;
			}
		});
		try {
			Thread t = new Thread(c1);
			t.start();
		} catch (Exception e) {
		}
	}
}
