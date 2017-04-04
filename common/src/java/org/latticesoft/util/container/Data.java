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
 * Created on May 1, 2005
 *
 */
package org.latticesoft.util.container;

import java.util.Date;
import java.util.Map;
import java.util.Collection;

/**
 * The Data interface defines the methods which is require
 * for extracting a simple type from the Map or other related
 * interfaces 
 * @author clgoh
 */
public interface Data {

	public String getString(Object key);
	public void setString(Object key, String value);

	public int getInt(Object key);
	public void setInt(Object key, int value);

	public long getLong(Object key);
	public void setLong(Object key, long value);

	public short getShort(Object key);
	public void setShort(Object key, short value);

	public byte getByte(Object key);
	public void setByte(Object key, byte value);

	public boolean getBoolean(Object key);
	public void setBoolean(Object key, boolean value);

	public float getFloat(Object key);
	public void setFloat(Object key, float value);

	public double getDouble(Object key);
	public void setDouble(Object key, double value);
	
	public Object getObject(Object key);
	public void setObject(Object key, Object value);
	
	public Date getDate(Object key);
	public Date getDate(Object key, String defaultFormat);
	public void setDate(Object key, Date value);
	
	public Map getMap(Object key);
	public void setMap(Object key, Map map);

	public Collection getCollection(Object key);
	public void setCollection(Object key, Collection c);
	
	public Number getNumber(Object key);
	public void setNumber(Object key, Number n);
}
