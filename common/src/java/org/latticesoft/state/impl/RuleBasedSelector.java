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

public class RuleBasedSelector implements Command {

	private static final Log log = LogFactory.getLog(RuleBasedSelector.class);
	private List rules = new ArrayList();
	
	public void addRule(Object o) {
		
	}
	
	public Object execute(Object o) throws CommandException {
		Transition retVal = null;
		if (o == null) { return retVal; }
		if (!(o instanceof Map)) { return retVal; }
		
		Map map = (Map)o;
		State state = (State)map.get(Key.STATE);
		Event event = (Event)map.get(Key.EVENT);
		if (state == null || event == null) { return retVal; }
		
		// loop thru the rules to locate a match for the event
		for (int i=0; i<rules.size(); i++) {
			Object rule = rules.get(i);
			if (log.isInfoEnabled()) { log.info(rule); }
		}
		
		
		return retVal;
	}
}
