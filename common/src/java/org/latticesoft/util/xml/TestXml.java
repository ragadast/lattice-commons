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
 * Created on Nov 1, 2006
 *
 */
package org.latticesoft.util.xml;

import org.latticesoft.util.common.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TestXml {
	private static final Log log = LogFactory.getLog(TestXml.class);
	
	public void testParsing(String rule, String xml) {
		Object o = MiscUtil.buildObjectFromXml(rule, xml);
		if (o instanceof XmlMain) {
			XmlMain main = (XmlMain)o;
if (log.isInfoEnabled()) { log.info(main); }
			Object root = main.construct("rootObject");
if (log.isInfoEnabled()) { log.info(root); }
		}
	}
	public static void main(String[] args) {
		TestXml test = new TestXml();
		test.testParsing("resource/testRule.xml", "resource/test.xml");
	}
}
