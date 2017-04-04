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
 * Created on May 25, 2005
 *
 */
package org.latticesoft.command;

/**
 * Filter is a wrapper to the {@link Command}. It provides a pre and post
 * execution method. The preprocessing is execute before the
 * execute method and the post execution is execute after the
 * execute method is invoked.
 * @author clgoh
 */
public interface Filter extends Command, PreFilter, PostFilter {
}
