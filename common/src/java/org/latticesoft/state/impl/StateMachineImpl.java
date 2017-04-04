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
 * Created on Aug 8, 2007
 *
 */
package org.latticesoft.state.impl;

import org.latticesoft.state.Event;
import org.latticesoft.state.State;
import org.latticesoft.state.StateMachine;

public class StateMachineImpl implements StateMachine {

	public StateImpl mainState = null;
	public void init() {
		
	}
	
	public Object evaluate(Event e) {
		if (mainState != null) {
			return mainState.evaluate(e);
		}
		return null;
	}

	public State getCurrentState() {
		return mainState.getCurrentState();
	}

}
