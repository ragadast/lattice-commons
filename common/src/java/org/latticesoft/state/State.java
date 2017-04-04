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
package org.latticesoft.state;

import org.latticesoft.command.*;
import java.util.List;

public interface State {
	
	/**
	 * Evaluates whether a state is to be changed or not 
	 */
	public Object evaluate(Event event) throws CommandException;

	/**
	 * The event is invoked upon entry into the state  
	 */
	public Object onEnter(Event event) throws CommandException;
	
	/**
	 * The event is invoked when the state is exitted. 
	 */
	public Object onExit(Event event) throws CommandException;
	
	/**
	 * The event is invoked while still in the state. 
	 */
	public Object inState(Event event) throws CommandException;
	
	/**
	 * Adds a transition.
	 * @param t the transition to be added
	 */
	public boolean addTransition(Transition t);
	
	/**
	 * Removes a transition by index
	 * @param index the index 
	 * @return the transition object removed. null if not found
	 */
	public Transition removeTransition(int index);
	
	/**
	 * Removes a transition by object
	 * @param t the transition object to be removed 
	 * @return the transition object removed. null if not found
	 */
	public Transition removeTransition(Transition t);

	/**
	 * Removes a transition by event
	 * @param event the event corresponding to the transition object 
	 * @return the transition object removed. null if not found
	 */
	public Transition removeTransition(Event event);

	/**
	 * Returns the size of the transitions inside the state 
	 */
	public int size();
	
	/**
	 * Gets a transition based on the event generated 
	 */
	public Transition getTransition(Event event);
	
	/**
	 * Returns the list of transitions linked to the state
	 */
	public List getTransitionList();
}
