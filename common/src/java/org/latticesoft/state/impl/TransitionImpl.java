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
import org.latticesoft.state.Event;
import org.latticesoft.state.State;
import org.latticesoft.state.Transition;
import org.latticesoft.util.common.Identity;

public class TransitionImpl implements Transition, Identity {

	private State currentState;
	private State nextState;
	private Event event;
	private ArrayList inTransitListeners = new ArrayList();
	
	public String getName() {
		StringBuffer sb = new StringBuffer();
		if (this.currentState != null && this.nextState != null && this.event != null) {
			sb.append(((Identity)this.currentState).getName());
			sb.append("-(");
			sb.append(((Identity)this.event).getName());
			sb.append(")-");
			sb.append(((Identity)this.nextState).getName());
		}
		return sb.toString();
	}

	public void setName(String name) {
		// no implementation
	}

	public String getId() {
		return this.getName();
	}

	public void setId(String id) {
		// no implementation
	}
	
	public Object inTransit(Event event) throws CommandException {
		// invoke the listeners 1st and then the inner state.
		Map map = new HashMap();
		Object retVal = null;
		for (int i=0; i<this.inTransitListeners.size(); i++) {
			Command cmd = (Command)this.inTransitListeners.get(i);
			retVal = cmd.execute(event);
			if (retVal != null) {
				map.put(this.getName() + i, retVal);
			}
		}
		return map;
	}

	public void addInTransitListener(Command cmd) {
		this.inTransitListeners.add(cmd);
	}
	public void removeInTransitListener(int index) {
		this.inTransitListeners.remove(index);
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
		this.currentState.addTransition(this);
	}

	/**
	 * @return Returns the event.
	 */
	public Event getEvent() {
		return (this.event);
	}

	/**
	 * @param event The event to set.
	 */
	public void setEvent(Event event) {
		this.event = event;
	}

	/**
	 * @return Returns the nextState.
	 */
	public State getNextState() {
		return (this.nextState);
	}

	/**
	 * @param nextState The nextState to set.
	 */
	public void setNextState(State nextState) {
		this.nextState = nextState;
	}
	
	/**
	* Converts the class in a string form
	* @returns the class in a string form.
	*/
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("[Transition: ");
		sb.append(this.getName());
		sb.append("]");
		return sb.toString();
	}
}
