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
 * Created on Sep 16, 2005
 *
 */
package org.latticesoft.state.impl;

import java.util.*;

import org.latticesoft.command.Command;
import org.latticesoft.command.CommandException;
import org.latticesoft.state.*;
import org.latticesoft.util.container.*;
import org.latticesoft.util.common.Identity;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class StateImpl implements State, Data, Identity {

	private static final Log log = LogFactory.getLog(StateImpl.class);
	private String name;
	private String id;
	private PropertyMap attributes = new PropertyMap();
	private ArrayList onEnterListeners = new ArrayList();
	private ArrayList onExitListeners = new ArrayList();
	private ArrayList inStateListeners = new ArrayList();
	private ArrayList transitions = new ArrayList(); 
	private State currentState = null;
	private Command selector = null;
	
	public StateImpl() {}
	public StateImpl(String name) {
		this.setName(name);
	}
	
	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return (this.name);
	}

	/**
	 * @param name The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return Returns the name.
	 */
	public String getId() {
		return (this.id);
	}

	/**
	 * @param name The name to set.
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * @return Returns the currentState.
	 */
	public State getCurrentState() {
		return (this.currentState);
	}
	/**
	 * @param currentState The currentState to set.
	 */
	public void setCurrentState(State currentState) {
		this.currentState = currentState;
	}

	/** @return Returns the selector. */
	public Command getSelector() {
		return (this.selector);
	}
	/** @param selector The selector to set. */
	public void setSelector(Command selector) {
		this.selector = selector;
	}

	
	
	public Object onEnter(Event event) throws CommandException {
		// invoke the listeners 1st and then the inner state.
		Map map = new HashMap();
		Object retVal = null;
		for (int i=0; i<this.onEnterListeners.size(); i++) {
			Command cmd = (Command)this.onEnterListeners.get(i);
			retVal = cmd.execute(event);
			if (retVal != null) {
				map.put(this.name + i, retVal);
			}
		}
		/*
		if (currentState != null) {
			retVal = currentState.onEnter(event);
			if (retVal != null && retVal instanceof Map) {
				Map m = (Map)retVal;
				map.putAll(m);
			}
		}//*/
		return map;
	}

	public Object onExit(Event event) throws CommandException {
		// invoke the inner state 1st then the listeners.
		Map map = new HashMap();
		Object retVal = null;
		if (currentState != null) {
			retVal = currentState.onExit(event);
			if (retVal != null && retVal instanceof Map) {
				Map m = (Map)retVal;
				map.putAll(m);
			}
		}//*/
		for (int i=0; i<this.onExitListeners.size(); i++) {
			Command cmd = (Command)this.onExitListeners.get(i);
			retVal = cmd.execute(event);
			if (retVal != null) {
				map.put(this.name + i, retVal);
			}
		}
		return map;
	}

	public Object inState(Event event) throws CommandException {
		// invoke the listeners 1st
		Map map = new HashMap();
		Object retVal = null;
		for (int i=0; i<this.inStateListeners.size(); i++) {
			Command cmd = (Command)this.inStateListeners.get(i);
			retVal = cmd.execute(event);
			if (retVal != null) {
				map.put(this.name + i, retVal);
			}
		}
		/*
		if (currentState != null) {
			retVal = currentState.evaluate(event);
			if (retVal != null && retVal instanceof Map) {
				Map m = (Map)retVal;
				map.putAll(m);
			}
		}//*/
		return map;
	}

	public Object evaluate(Event event) throws CommandException {
		Object retVal = null;
		Map map = new HashMap();
		if (currentState == null) {
			return map;
		}
		// get the corresponding transition from  the current state
		Transition t = currentState.getTransition(event);
		// if t is null then invoke the inState
		if (t == null) {
			currentState.inState(event);
			currentState.evaluate(event);
		} else {
			State nextState = t.getNextState();
			retVal = currentState.onExit(event);
			if (retVal != null && retVal instanceof Map) {
				Map m = (Map)retVal;
				map.putAll(m);
			}
			retVal = t.inTransit(event);
			if (retVal != null && retVal instanceof Map) {
				Map m = (Map)retVal;
				map.putAll(m);
			}
			retVal = nextState.onEnter(event);
			if (retVal != null && retVal instanceof Map) {
				Map m = (Map)retVal;
				map.putAll(m);
			}
			retVal = nextState.inState(event);
			if (retVal != null && retVal instanceof Map) {
				Map m = (Map)retVal;
				map.putAll(m);
			}
			// set the next state to be the new current state
			currentState = nextState;

			// call the next state's child states
			nextState.evaluate(event);
		}
		return map;
	}

	
	public void addOnEnterListener(Command cmd) {
		this.onEnterListeners.add(cmd);
	}
	public void addOnExitListener(Command cmd) {
		this.onExitListeners.add(cmd);
	}
	public void addInStateListener(Command cmd) {
		this.inStateListeners.add(cmd);
	}
	public void removeOnEnterListener(int index) {
		this.onEnterListeners.remove(index);
	}
	public void removeOnExitListener(int index) {
		this.onExitListeners.remove(index);
	}
	public void removeInStateListener(int index) {
		this.inStateListeners.remove(index);
	}
	
	public boolean addTransition(Transition t) {
		if (t != null && 
			t.getEvent() != null && 
			((Identity)t.getEvent()).getName() != null) {
			if (!this.equals(t.getCurrentState())) {
				t.setCurrentState(this);
			}
			transitions.add(t);
			return true;
		}
		return false;
	}

	public Transition removeTransition(int index) {
		if (index < 0 || index >= this.transitions.size()) {
			return null;
		}
		return (Transition)this.transitions.remove(index);
	}

	public Transition removeTransition(Transition t) {
		if (t == null) {
			return null;
		}
		int index = -1;
		for (int i=0; i<this.transitions.size(); i++) {
			Transition tt = (Transition)this.transitions.get(i);
			if (tt != null && t.equals(tt)) {
				index = i;
				break;
			}
		}
		if (index >= 0) {
			return (Transition)this.transitions.remove(index);
		}
		return null;
	}

	public Transition removeTransition(Event event) {
		if (event == null) {
			return null;
		}
		int index = -1;
		for (int i=0; i<this.transitions.size(); i++) {
			Transition tt = (Transition)this.transitions.get(i);
			if (tt != null && tt.getEvent().equals(event)) {
				index = i;
				break;
			}
		}
		if (index >= 0) {
			return (Transition)this.transitions.remove(index);
		}
		return null;
	}
	public List getTransitionList() {
		return this.transitions;
	}
	

	public int size() {
		return this.transitions.size();
	}

	public Transition getTransition(Event event) {
		if (event == null || this.selector == null) {
			return null;
		}
		Transition retVal = null;
		Map map = new HashMap();
		map.put(Key.STATE, this);
		map.put(Key.EVENT, event);
		Object o = selector.execute(map);
if (log.isDebugEnabled()) { log.debug(o); }
		if (o instanceof Transition) {
			retVal = (Transition)o;
		} else if (o instanceof Map){
			map = (Map)o;
			Object o1 = map.get(Key.TRANSITION);
			if (o1 instanceof Transition) {
				retVal = (Transition)o1;
			}
		}
		// look for sub state
		if (retVal == null && this.currentState != null) {
			retVal = this.currentState.getTransition(event);
		}
		return retVal;
	}

	/* (non-Javadoc)
	 * @see org.latticesoft.util.container.PropertyMap#getBoolean(java.lang.Object)
	 */
	public boolean getBoolean(Object key) {
		return attributes.getBoolean(key);
	}

	/* (non-Javadoc)
	 * @see org.latticesoft.util.container.PropertyMap#getByte(java.lang.Object)
	 */
	public byte getByte(Object key) {
		return attributes.getByte(key);
	}

	/* (non-Javadoc)
	 * @see org.latticesoft.util.container.PropertyMap#getDate(java.lang.Object, java.lang.String)
	 */
	public Date getDate(Object key, String defaultFormat) {
		return attributes.getDate(key, defaultFormat);
	}

	/* (non-Javadoc)
	 * @see org.latticesoft.util.container.PropertyMap#getDate(java.lang.Object)
	 */
	public Date getDate(Object key) {
		return attributes.getDate(key);
	}

	/* (non-Javadoc)
	 * @see org.latticesoft.util.container.PropertyMap#getDouble(java.lang.Object)
	 */
	public double getDouble(Object key) {
		return attributes.getDouble(key);
	}

	/* (non-Javadoc)
	 * @see org.latticesoft.util.container.PropertyMap#getFloat(java.lang.Object)
	 */
	public float getFloat(Object key) {
		return attributes.getFloat(key);
	}
	
	/* (non-Javadoc)
	 * @see org.latticesoft.util.container.Data#getNumber(java.lang.Object)
	 */
	public Number getNumber(Object key) {
		return attributes.getNumber(key);
	}
	/* (non-Javadoc)
	 * @see org.latticesoft.util.container.Data#setNumber(java.lang.Object, java.lang.Number)
	 */
	public void setNumber(Object key, Number value) {
		attributes.setNumber(key, value);
	}
	/* (non-Javadoc)
	 * @see org.latticesoft.util.container.PropertyMap#getInt(java.lang.Object)
	 */
	public int getInt(Object key) {
		return attributes.getInt(key);
	}

	/* (non-Javadoc)
	 * @see org.latticesoft.util.container.PropertyMap#getLong(java.lang.Object)
	 */
	public long getLong(Object key) {
		return attributes.getLong(key);
	}

	/* (non-Javadoc)
	 * @see org.latticesoft.util.container.PropertyMap#getObject(java.lang.Object)
	 */
	public Object getObject(Object key) {
		return attributes.getObject(key);
	}

	/* (non-Javadoc)
	 * @see org.latticesoft.util.container.PropertyMap#getShort(java.lang.Object)
	 */
	public short getShort(Object key) {
		return attributes.getShort(key);
	}

	/* (non-Javadoc)
	 * @see org.latticesoft.util.container.PropertyMap#getString(java.lang.Object)
	 */
	public String getString(Object key) {
		return attributes.getString(key);
	}
	/* (non-Javadoc)
	 * @see org.latticesoft.util.container.PropertyMap#getCollection(java.lang.Object)
	 */
	public Collection getCollection(Object key) {
		return attributes.getCollection(key);
	}
	/* (non-Javadoc)
	 * @see org.latticesoft.util.container.PropertyMap#getMap(java.lang.Object)
	 */
	public Map getMap(Object key) {
		return attributes.getMap(key);
	}
	/* (non-Javadoc)
	 * @see org.latticesoft.util.container.PropertyMap#setBoolean(java.lang.Object, boolean)
	 */
	public void setBoolean(Object key, boolean value) {
		attributes.setBoolean(key, value);
	}

	/* (non-Javadoc)
	 * @see org.latticesoft.util.container.PropertyMap#setByte(java.lang.Object, byte)
	 */
	public void setByte(Object key, byte value) {
		attributes.setByte(key, value);
	}

	/* (non-Javadoc)
	 * @see org.latticesoft.util.container.PropertyMap#setDate(java.lang.Object, java.util.Date)
	 */
	public void setDate(Object key, Date date) {
		attributes.setDate(key, date);
	}

	/* (non-Javadoc)
	 * @see org.latticesoft.util.container.PropertyMap#setDouble(java.lang.Object, double)
	 */
	public void setDouble(Object key, double value) {
		attributes.setDouble(key, value);
	}

	/* (non-Javadoc)
	 * @see org.latticesoft.util.container.PropertyMap#setFloat(java.lang.Object, float)
	 */
	public void setFloat(Object key, float value) {
		attributes.setFloat(key, value);
	}

	/* (non-Javadoc)
	 * @see org.latticesoft.util.container.PropertyMap#setInt(java.lang.Object, int)
	 */
	public void setInt(Object key, int value) {
		attributes.setInt(key, value);
	}

	/* (non-Javadoc)
	 * @see org.latticesoft.util.container.PropertyMap#setLong(java.lang.Object, long)
	 */
	public void setLong(Object key, long value) {
		attributes.setLong(key, value);
	}

	/* (non-Javadoc)
	 * @see org.latticesoft.util.container.PropertyMap#setObject(java.lang.Object, java.lang.Object)
	 */
	public void setObject(Object key, Object value) {
		attributes.setObject(key, value);
	}

	/* (non-Javadoc)
	 * @see org.latticesoft.util.container.PropertyMap#setShort(java.lang.Object, short)
	 */
	public void setShort(Object key, short value) {
		attributes.setShort(key, value);
	}

	/* (non-Javadoc)
	 * @see org.latticesoft.util.container.PropertyMap#setString(java.lang.Object, java.lang.String)
	 */
	public void setString(Object key, String value) {
		attributes.setString(key, value);
	}
	/* (non-Javadoc)
	 * @see org.latticesoft.util.container.PropertyMap#setCollection(java.lang.Object, java.util.Collection)
	 */
	public void setCollection(Object key, Collection value) {
		attributes.setCollection(key, value);
	}
	/* (non-Javadoc)
	 * @see org.latticesoft.util.container.PropertyMap#setMap(java.lang.Object, java.util.Map)
	 */
	public void setMap(Object key, Map value) {
		attributes.setMap(key, value);
	}
	
	/**
	 * Converts the class in a string form
	 * @returns the class in a string form.
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("[State:").append(this.name).append("]");
		return sb.toString();
	}
}
