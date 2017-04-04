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
import java.sql.Connection;

/**
 * This interface describes a java.sqlConnection user
 */
public interface ConnectionUser {
	/** 
	 * @return a boolean flag to indicate whether to close 
	 * the connection after use or not 
	 */
	boolean isCloseConnection();
	/**
	 * Sets the the boolean flag on whether to close the connection after use
	 */
	void setCloseConnection(boolean b);
	/**
	 * Returns the connection
	 */
	Connection getConnection();
	/**
	 * Sets the connection
	 */
	void setConnection(Connection conn);
}
