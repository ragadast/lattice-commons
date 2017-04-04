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
 * Created on 3 Jan 2008
 *
 */
package org.latticesoft.util.spring;
import java.io.*;
import org.springframework.core.*;
import org.springframework.beans.factory.*;
import org.springframework.beans.factory.xml.*;
import org.springframework.core.io.*;
import org.springframework.context.*;
import org.springframework.context.support.*;

/**
 * This utility class provided utility methods for managing spring applications
 */
public final class SpringUtil {
	public static BeanFactory createBeanFactory(String filename) {
		if (filename == null) {
			return null;
		}
		BeanFactory fac = null;
		Resource res = new FileSystemResource(filename);
		fac = new XmlBeanFactory(res);
		return fac;
	}
	
	public static ApplicationContext createContext(String filename) {
		if (filename == null) {
			return null;
		}
		ApplicationContext ctx = null;
		ctx = new FileSystemXmlApplicationContext(filename);
		if (ctx == null) {
			ctx = new ClassPathXmlApplicationContext(filename);
		}
		return ctx;
	}
	
	public static void main(String[] args) {
		ApplicationContext ctx = SpringUtil.createContext("resource/springtest.xml");
		System.out.println(ctx);
		Object o = ctx.getBean("helloBean1");
		System.out.println(o);
		o = ctx.getBean("helloBean2");
		System.out.println(o);
	}
}
