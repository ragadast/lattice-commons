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

import java.util.Map;
import java.util.Set;

/**
 * An interface to indicate to identify (uniquely or not)
 * an object. The attributes to identify an object includes
 * name, id, and a composite token key/value pair.
 * 
 * The composite key value will generate a token basen on the
 * composite key entered.
 */
public interface TokenIdentity extends Identity {
	/** 
	 * Adds the key. The order of addition will determine order of the
	 * token generated.
	 */
	boolean addKey(String key);
	/** Removes a key */
	boolean removeKey(int index);
	/** Removes a key */
	boolean removeKey(String key);
	/** Returns the size of the key store */
	int size();
	/** Returns the key. */
	String getKey(int index);
	/** Returns the key set*/
	Set keySet();
	
	/** Sets the token separator. */
	void setTokenFormat(String format);
	/** Returns the token separator */
	String getTokenFormat();
	
	/** Clears the keys */
	void clear();
	/** Generates the composite key based on the key stored */
	String getToken(Map map);
}
