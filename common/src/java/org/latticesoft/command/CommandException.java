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
package org.latticesoft.command;
import java.util.Map;
import java.util.HashMap;

/**
 * <p>The {@link CommandException} class is the exception thrown by a Command
 * during its execution. The fact that it extends Runtime exception means that
 * CommandException need not have a try and catch block explicitly declared. 
 * However it is always good to have a try and catch block handling the exception.
 * </p>
 *
 * <p>Exception Handling</p>
 * <p>All exceptions should be caught and handle within a try and catch
 * block. The exception should never be thrown to the caller. If the
 * processing logic is to be halted when an exception occurs, the
 * {@link Command} should end elegantly by catching  the exception. The
 * handling will then do the necessary logic like logging of exceptions or
 * putting the exception to a collection for other exception processing.
 * Finally, the command should decide whether the exception should halt the
 * entire {@link Chain} (in which the return value is true) or to continue
 * with the chain processing.</p>
 * 
 * @see {@link Command} also.
 */
public class CommandException extends RuntimeException {
    public static final long serialVersionUID = 20050307103603L;
    protected Map map = new HashMap();
    
    public CommandException() {}
    public CommandException(Throwable t) {
    	this.initCause(t);
    }
    public CommandException(String s) {
    	this.initCause(new Exception(s));
    	this.map.put("message", s);
    }
    
	/**
	 * @return Returns the map.
	 */
	public Map getMap() {
		return (this.map);
	}
	/**
	 * @param map The map to set.
	 */
	public void setMap(Map map) {
		this.map = map;
	}
}