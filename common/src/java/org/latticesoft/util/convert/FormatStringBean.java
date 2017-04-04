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
 * Created on Feb 9, 2006
 *
 */
package org.latticesoft.util.convert;

import org.latticesoft.command.Command;
import org.latticesoft.command.CommandException;
import org.latticesoft.util.common.*;

public class FormatStringBean implements Command {

	private boolean leftPad;
	private boolean pad;
	private boolean trim;
	private boolean substring;
	private boolean replace;
	private boolean replaceAll = true;
	private boolean upperCase;
	private boolean lowerCase;
	private String padString;
	private int length;
	private int from;
	private int to;
	private String replaceFrom;
	private String replaceWith;
	
	/** @return Returns the from. */
	public int getFrom() { return (this.from); }
	/** @param from The from to set. */
	public void setFrom(int from) { this.from = from; }

	/** @return Returns the leftPad. */
	public boolean isLeftPad() { return (this.leftPad); }
	/** @param leftPad The leftPad to set. */
	public void setLeftPad(boolean leftPad) { this.leftPad = leftPad; }

	/** @return Returns the length. */
	public int getLength() { return (this.length); }
	/** @param length The length to set. */
	public void setLength(int length) { this.length = length; }

	/** @return Returns the padString. */
	public String getPadString() { return (this.padString); }
	/** @param padString The padString to set. */
	public void setPadString(String padString) { this.padString = padString; }

	/** @return Returns the substring. */
	public boolean isSubstring() { return (this.substring); }
	/** @param substring The substring to set. */
	public void setSubstring(boolean substring) { this.substring = substring; }

	/** @return Returns the to. */
	public int getTo() { return (this.to); }
	/** @param to The to to set. */
	public void setTo(int to) { this.to = to; }

	/** @return Returns the trim. */
	public boolean isTrim() { return (this.trim); }
	/** @param trim The trim to set. */
	public void setTrim(boolean trim) { this.trim = trim; }

	/** @return Returns the pad. */
	public boolean isPad() { return (this.pad); }
	/** @param pad The pad to set. */
	public void setPad(boolean pad) { this.pad = pad; }

	/** @return Returns the replace. */
	public boolean isReplace() { return (this.replace); }
	/** @param replace The replace to set. */
	public void setReplace(boolean replace) { this.replace = replace; }

	/** @return Returns the replaceAll. */
	public boolean isReplaceAll() { return (this.replaceAll); }
	/** @param replaceAll The replaceAll to set. */
	public void setReplaceAll(boolean replaceAll) { this.replaceAll = replaceAll; }

	/** @return Returns the replaceFrom. */
	public String getReplaceFrom() { return (this.replaceFrom); }
	/** @param replaceFrom The replaceFrom to set. */
	public void setReplaceFrom(String replaceFrom) { this.replaceFrom = replaceFrom; }

	/** @return Returns the replaceWith. */
	public String getReplaceWith() { return (this.replaceWith); }
	/** @param replaceWith The replaceWith to set. */
	public void setReplaceWith(String replaceWith) { this.replaceWith = replaceWith; }
	
	/** @return Returns the lowerCase. */
	public boolean isLowerCase() { return (this.lowerCase); }
	/** @param lowerCase The lowerCase to set. */
	public void setLowerCase(boolean lowerCase) { this.lowerCase = lowerCase; }

	/** @return Returns the upperCase. */
	public boolean isUpperCase() { return (this.upperCase); }
	/** @param upperCase The upperCase to set. */
	public void setUpperCase(boolean upperCase) { this.upperCase = upperCase; }

	public Object execute(Object o) throws CommandException {
		if (o == null) return null;
		
		String s = null;
		if (o instanceof String) {
			s = (String)o;
		} else {
			s = o.toString();
		}
		
		if (trim) {
			s = s.trim();
		}
		if (substring && from >= 0 && to < s.length() && to > from) {
			s = s.substring(from, to);
		}
		if (replace) {
			s = StringUtil.replace(s, this.replaceFrom, this.replaceWith, replaceAll);
		}
		if (pad) {
			s = ConvertUtil.formatString(s, this.length, this.padString, this.leftPad, true);
		}
		if (upperCase) {
			s = s.toUpperCase();
		}
		if (lowerCase) {
			s = s.toLowerCase();
		}
		return s;
	}

	public static void main(String[] args) {
		FormatStringBean bean = new FormatStringBean();
		bean.setPad(true);
		bean.setLength(10);
		bean.setPadString("==");
		bean.setLeftPad(true);
		bean.setUpperCase(true);
		System.out.println(bean.execute("Hahaha"));
	}
}
