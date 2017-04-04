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
 * Created on Mar 20, 2006
 *
 */
package org.latticesoft.util.convert;

import java.util.*;

import org.latticesoft.util.common.*;
import org.apache.commons.beanutils.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>
 * BeanParser is a utility converter class to help to parse a
 * raw datas into string attributes for populating java beans. Typical
 * usage includes parsing HTML pages into beans extracting important
 * information from the page. It is essentially a line parser.
 * </p>
 * <code>
 * BeanParser b = new BeanParser();
 * FormatStringBean bean = new FormatStringBean();
 * b.setBean(bean);
 * b.setStartFlag("haha");
 * b.setEndFlag("hoho");
 * b.getTemplates().add("hahah 1111 ${length} , oooo oooo , ${padString} hoho");
 * b.getTemplates().add("hahah 2222 ${from} hoho");
 * b.getTemplates().add("${replaceFrom},${replaceWith}");
 * b.getIgnore().add("hehe,huhu");
 * b.convert("hahah 1111 20 , oooo oooo , ninaohnyah hoho");
 * System.out.println(bean.getLength() + " : " + bean.getFrom() + " : " + bean.getPadString());
 * b.convert("reiyorsjyjetujtlr;u");
 * System.out.println(bean.getLength() + " : " + bean.getFrom() + " : " + bean.getPadString());
 * b.convert("hahah 2222 3 hoho");
 * System.out.println(bean.getLength() + " : " + bean.getFrom() + " : " + bean.getPadString());
 * b.convert("hoho,haha");
 * System.out.println(bean.getReplaceFrom() + " : " + bean.getReplaceWith());
 * b.convert("hehe,huhu");
 * System.out.println(bean.getReplaceFrom() + " : " + bean.getReplaceWith());
 * </code>
 * In the above examples, the start flag defined the staring point for
 * the bean populater to take attention of the data. Similarly, the end flag
 * signifies the end point. The templates defines the templates which would
 * extract the required attributes from the line. Once the attribute is found it is
 * populated into the bean straight. The template must match the bean's attribute.
 * 
 * The output from the above example.
 * <code>
 * 20 : 0 : ninaohnyah
 * 20 : 0 : ninaohnyah
 * 20 : 3 : ninaohnyah
 * hoho : haha
 * hoho : haha
 * </code>
 */
public class BeanParser implements Converter {

	private static final Log log = LogFactory.getLog(BeanParser.class);
	private String startFlag;
	private String endFlag;
	private ArrayList templates = new ArrayList();
	private ArrayList ignore = new ArrayList();
	private boolean active = false;
	private Object bean = null;

	public String getEndFlag() { return (this.endFlag); }
	public void setEndFlag(String endFlag) { this.endFlag = endFlag; }
	public String getStartFlag() { return (this.startFlag); }
	public void setStartFlag(String startFlag) { this.startFlag = startFlag; }
	public ArrayList getTemplates() { return (this.templates); }
	public ArrayList getIgnore() { return (this.ignore); }
	public Object getBean() { return (this.bean); }
	public void setBean(Object bean) { this.bean = bean; }
	
	public boolean addTemplate(String s) {
		if (s != null) {
			return this.templates.add(s);
		}
		return false;
	}
	public boolean addIgnore(String s) {
		if (s != null) {
			return this.ignore.add(s);
		}
		return false;
	}
	
	public Object convert(Object o) {
		if (o == null) return null;
		if (bean == null) {
			return null;
		}
		String line = null;
		if (o instanceof String){
			line = (String)o;
		} else {
			line = o.toString();
		}
		line = line.trim();
if (log.isDebugEnabled()) { log.debug(line); }

		// check for start flag to signify active mode
		if (startFlag == null) {
			active = true;
		} else if (line.indexOf(this.startFlag) > -1) {
			active = true;
		}
		
		boolean populated = false;
		if (active) {
			boolean ignoreFlag = false;
			// check for ignore flag
			for (int i=0; i<this.ignore.size(); i++) {
				String ig = (String)ignore.get(i);
				if (line.startsWith(ig)) {
					ignoreFlag = true;
					break;
				}
			}
			if (ignoreFlag) return null;

			// check for templates and populate the bean accordingly
			for (int i=0; i<templates.size(); i++) {
				String template = (String)templates.get(i);
				Map map = StringUtil.extractParameterValue(line, template);
if (log.isDebugEnabled()) { log.debug(map); }
				if (map.size() == 0) {
					continue;
				} else {
					try { BeanUtils.populate(bean, map); } catch (Exception e) {}
					populated = true;
					break;
				}
			}
			// check for inactive mode
			if (endFlag != null && line.indexOf(this.endFlag) > -1) {
				active = false;
			}
		}
		if (populated) return bean;
		else return null;
	}
	
	/**
	* Converts the class in a string form
	* @returns the class in a string form.
	*/
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("[lp|");
		sb.append(this.startFlag).append("|");
		sb.append(this.endFlag).append("|");
		sb.append(this.templates.size()).append("]");
		return sb.toString();
	}
	
	public static void main(String[] args) {
		BeanParser b = new BeanParser();
		FormatStringBean bean = new FormatStringBean();
		b.setBean(bean);
		b.setStartFlag("haha");
		b.setEndFlag("hoho");
		b.getTemplates().add("hahah 1111 ${length} , oooo oooo , ${padString} hoho");
		b.getTemplates().add("hahah 2222 ${from} hoho");
		b.getTemplates().add("${replaceFrom},${replaceWith}");
		b.getIgnore().add("hehe,huhu");
		
		b.convert("hahah 1111 20 , oooo oooo , ninaohnyah hoho");
System.out.println(bean.getLength() + " : " + bean.getFrom() + " : " + bean.getPadString());
		b.convert("reiyorsjyjetujtlr;u");
System.out.println(bean.getLength() + " : " + bean.getFrom() + " : " + bean.getPadString());
		b.convert("hahah 2222 3 hoho");
System.out.println(bean.getLength() + " : " + bean.getFrom() + " : " + bean.getPadString());
		b.convert("hoho,haha");
System.out.println(bean.getReplaceFrom() + " : " + bean.getReplaceWith());
		b.convert("hehe,huhu");
System.out.println(bean.getReplaceFrom() + " : " + bean.getReplaceWith());
	}
}

