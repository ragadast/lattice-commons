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

import java.util.Map;

/**
 * An Event is the initiation for change in states.
 * However not all events result in state changes. Only
 * those successfully matches that event of a particular 
 * Transition will have a transition into another or same
 * state depending on where the transition leads to.
 */
public interface Event extends Map {
	/**
	 * Matches another event
	 */
	boolean matches(Event event);
}
