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
 * Created on Aug 7, 2007
 *
 */
package org.latticesoft.state.web;

import java.util.*;
import javax.servlet.http.*;
import org.latticesoft.state.*;
import org.latticesoft.state.impl.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class HttpEvent extends BasicEvent {
	private HttpServletRequest request = null;
	private HttpServletResponse response = null;
	private Map criteria = new HashMap();
	private static final Log log = LogFactory.getLog(HttpEvent.class);

	public HttpEvent(){}
	public HttpEvent(HttpServletRequest request, HttpServletResponse response) {
		this.request = request;
		this.response = response;
	}
	public boolean matches(Event event) {
		if (this.request == null || this.response == null) {
			return false;
		}
		StringBuffer sb1 = new StringBuffer();
		StringBuffer sb2 = new StringBuffer();
		Iterator iter = this.criteria.keySet().iterator();
		while (iter.hasNext()) {
			Object o = iter.next();
			if (o instanceof String) {
				String key = (String)o;
				sb1.append(criteria.get(key));
				sb2.append(request.getParameter(key));
			}
		}
		if (sb1.toString().equals(sb2.toString())) {
			
		}
		return false;
	}
}
