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
 */
package org.latticesoft.util.container.tree.noderule;

import java.util.*;
import org.latticesoft.util.common.DateUtil;

public class DateTimeRangeRuleKey implements RangeRuleKey {

	private Object end;
	private Object start;
	private String value; 

	/** @return Returns the end. */
	public Object getEnd() { return (this.end); }
	/** @param end The end to set. */
	public void setEnd(Object end) { this.end = end; }

	/** @return Returns the start. */
	public Object getStart() { return (this.start); }
	/** @param start The start to set. */
	public void setStart(Object start) { this.start = start; }

	/** @return Returns the value. */
	public String getValue() { return (this.value); }
	/** @param value The value to set. */
	public void setValue(String value) { this.value = value; }

	public boolean isWithinRange(Object o) {
		Date startDate = null;
		Date endDate = null;
		Date test = null;
		return DateUtil.isWithinDateTimePeriod(startDate, endDate, test);
	}
	public Object evaluate(Object o) {
		if (this.isWithinRange(o)) {
			return this.value;
		} else {
			return null;
		}
	}
}
