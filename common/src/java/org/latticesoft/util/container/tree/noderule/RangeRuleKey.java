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

public interface RangeRuleKey {

	/**
	 * Check if the object is within this range
	 * @return true if the object is within range false if not 
	 * */
	boolean isWithinRange(Object o);
	
	/**
	 * Evaluate if the object is within this range and return the corresponding value
	 * that the range represents
	 * @return if the o is within range, it will return the value that range represents
	 * else it will return null;  
	 */
	Object evaluate(Object o);
}
