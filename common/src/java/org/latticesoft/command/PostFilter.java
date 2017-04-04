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
 * Created on Sep 15, 2005
 *
 */
package org.latticesoft.command;

/**
 * Filter is a wrapper to the {@link Command}. It provides a post
 * execution method. The post execution is executed after the
 * execute method is invoked.
 * @author clgoh
 */

public interface PostFilter {
	/**
	 * Processes the object after the actual execution.
	 * 
	 * @param o the input parameter. For inputs with more than
	 * one parameter required, a map is passed instead and the Object
	 * is casted into a map.
	 *
	 * @return true is the {@link Chain} is terminated. false if the {@link Chain}
	 * of execution is is to be continues. See above paragraph for more details.
	 *
	 * @throws Exception when an exception occurs see above text for more details
	 * on exception handling.
	 */
	Object postExecute(Object o) throws CommandException;
}
