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
 */
package org.latticesoft.util.container.tree.noderule;

import java.util.*;

public class DiscreteRangeRuleKey implements RangeRuleKey {

	private List keys = new ArrayList();
	private List values = new ArrayList();
	
	public void addGroup(Set key, String value) {
		if (key != null && value != null) {
			keys.add(key);
			values.add(value);
		}
	}

	public boolean isWithinRange(Object o) {
		if (evaluate(o) != null) {
			return true;
		}
		return false;
	}
	public Object evaluate(Object o) {
		// loop thru the list of sets s
		Object retVal = null;
		for (int i=0; i<keys.size(); i++) {
			Set key = (Set)keys.get(i);
			if (key.contains(o)) {
				retVal = (String)values.get(i);
				break;
			}
		}
		return retVal;
	}
}
