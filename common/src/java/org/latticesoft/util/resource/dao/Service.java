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
 * Created on Jun 13, 2006
 *
 */
package org.latticesoft.util.resource.dao;
import org.latticesoft.command.Command;
public interface Service extends ConnectionUser, Command {
	public static final int UPDATE = 1;
	public static final int QUERY = 2;

	String getName();
	void setName(String s);
	int getType();
}
