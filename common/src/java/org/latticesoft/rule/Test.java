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
 * Created on Dec 18, 2006
 *
 */
package org.latticesoft.rule;
import java.util.*;
import org.apache.commons.jexl.*;
import org.apache.commons.jexl.util.*;
import org.apache.commons.jexl.context.*;
import org.latticesoft.util.common.*;

public class Test {
	public static void main(String[] args) {
		try {
			JexlContext jc = JexlHelper.createContext();
			Map map = jc.getVars();
			String s = null;
			Expression e = null;
			
			s = "foo.sayHello()";
			e = ExpressionFactory.createExpression(s);
			TestBean b = new TestBean();
			b.setName("BestOfTheBest");
			map.put("foo", b);
			Object o = e.evaluate(jc);
			System.out.println("result: " + o);
			
			s = "one + two == 3";
			e = ExpressionFactory.createExpression(s);
			
			map.put("one", new Integer(1));
			map.put("two", new Integer(2));
			o = e.evaluate(jc);
			System.out.println(o);
			
			s = "i[1] == 1";
			int[] i = {0, 1, 2, 3};
			e = ExpressionFactory.createExpression(s);
			map = jc.getVars();
			map.put("i", i);
			o = e.evaluate(jc);
			System.out.println(o);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

