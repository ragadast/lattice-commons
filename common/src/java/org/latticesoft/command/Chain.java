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

/**
 * <p>The {@link Command} interface defines the execution method for a unit
 * execution of a logic processing. More than 1 {@link Command}s may be
 * chained together to produce a series {@link Chain} of logic processing.
 * There is only one method to implement, <code>execute()</code>. There are
 * several aspects to take note when implementing the interface. </p>
 *
 * <p>Execution</p>
 * <p>Typically, the {@link Command} will encapsulate one unit of execution
 * context. The reuseability should be taken into account when designing
 * the {@link Command}. The scope of the processing should be determine by
 * the designer of the system. If the command is intended for API reuseability,
 * the scope should be as universal as possible.</p>
 *
 * <p>Parameters</p>
 * <p>All the parameters to the command is wrapped in a key-value pair
 * contained within a <code>Map</code> object.</p>
 *
 * <p>Return value (boolean)</p>
 * <p>The return is simple: true or false. When the return value is set to
 * true, this implies that the processing should stop. When the return value
 * is set to false, the processing to carry on to the next {@link Chain}.</p>
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
 */
public interface Chain extends Command {
    /**
     * Returns the Command at a key position.
     */
    public Command getCommand(String key);

    /**
     * Returns the Command at a particular index
     * @param index the index of the Command
     * @return the Command object
     */
    public Command getCommand(int index);
    
    /***/
    public boolean add(Command cmd);
    
    /***/
    public boolean insert(Command cmd);
}