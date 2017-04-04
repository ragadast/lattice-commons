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
 * Created on Sep 19, 2005
 *
 */
package org.latticesoft.util.common;

/**
 * An interface to indicate to identify (uniquely or not)
 * an object. The attributes to identify an object includes
 * name and id.
 */
public interface Identity {
	/** Sets the name of the object implementation */
	void setName(String name);
	/** Returns the name of the object implementation */
	String getName();
	
	/** Sets the ID of the object implementation */
	void setId(String id);
	/** Returns the ID of the object implementation */
	String getId();
}
