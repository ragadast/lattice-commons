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
 * Created on Oct 30, 2006
 *
 */
package org.latticesoft.state.impl;

import java.util.*;
import org.latticesoft.command.Command;
import org.latticesoft.command.CommandException;
import org.latticesoft.state.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class LoopSelector implements Command {

	private static final Log log = LogFactory.getLog(LoopSelector.class);
	public Object execute(Object o) throws CommandException {
		if (o == null || !(o instanceof Map)) {
			return null;
		}
		Map param = (Map)o;
		State state = (State)param.get(Key.STATE);
		Event event = (Event)param.get(Key.EVENT);
		
		Transition retVal = null;
		List transitions = state.getTransitionList();
		for (int i=0; i<transitions.size(); i++) {
			Transition t = (Transition)transitions.get(i);
if (log.isDebugEnabled()) { log.debug(t + " " + t.getEvent() + " " + event); }
			if (t.getEvent() != null && t.getEvent().matches(event)) {
				retVal = t;
				break;
			}
		}
		return retVal;
	}

}
