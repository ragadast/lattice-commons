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
 * Created on Apr 24, 2007
 *
 */
package org.latticesoft.util.common;

import java.io.Serializable;
import java.util.*;

public class TimePeriod implements Serializable {
	
	public static final long serialVersionUID = 20070424160143L;
	private int year;
	private int month;
	private int day;
	private int hour;
	private int minute;
	private int second;
	private int milli;
	private int nano;
	/** @return Returns the day. */
	public int getDay() {
		return (this.day);
	}
	/** @param day The day to set. */
	public void setDay(int day) {
		this.day = day;
	}
	/** @return Returns the hour. */
	public int getHour() {
		return (this.hour);
	}
	/** @param hour The hour to set. */
	public void setHour(int hour) {
		this.hour = hour;
	}
	/** @return Returns the milli. */
	public int getMilli() {
		return (this.milli);
	}
	/** @param milli The milli to set. */
	public void setMilli(int milli) {
		this.milli = milli;
	}
	/** @return Returns the minute. */
	public int getMinute() {
		return (this.minute);
	}
	/** @param minute The minute to set. */
	public void setMinute(int minute) {
		this.minute = minute;
	}
	/** @return Returns the month. */
	public int getMonth() {
		return (this.month);
	}
	/** @param month The month to set. */
	public void setMonth(int month) {
		this.month = month;
	}
	/** @return Returns the nano. */
	public int getNano() {
		return (this.nano);
	}
	/** @param nano The nano to set. */
	public void setNano(int nano) {
		this.nano = nano;
	}
	/** @return Returns the second. */
	public int getSecond() {
		return (this.second);
	}
	/** @param second The second to set. */
	public void setSecond(int second) {
		this.second = second;
	}
	/** @return Returns the year. */
	public int getYear() {
		return (this.year);
	}
	/** @param year The year to set. */
	public void setYear(int year) {
		this.year = year;
	}
	/**
	 * Converts the class in a string form
	 * @returns the class in a string form.
	 */
	public String toString() {
		return StringUtil.formatObjectToString(this);
	}
	
	public static TimePeriod parse(String s) {
		TimePeriod tp = new TimePeriod();
		s = s.toUpperCase();
		Map map = StringUtil.tokenizeIntoMap(s, ",", ":");
		tp.setYear(NumeralUtil.parseInt(map.get("YEAR")));
		tp.setMonth(NumeralUtil.parseInt(map.get("MONTH")));
		tp.setDay(NumeralUtil.parseInt(map.get("DAY")));
		tp.setHour(NumeralUtil.parseInt(map.get("HOUR")));
		tp.setMinute(NumeralUtil.parseInt(map.get("MINUTE")));
		tp.setSecond(NumeralUtil.parseInt(map.get("SECOND")));
		tp.setMilli(NumeralUtil.parseInt(map.get("MILLI")));
		tp.setNano(NumeralUtil.parseInt(map.get("NANO")));
		return tp;
	}
	
	public static void main(String[] args) {
		TimePeriod tp = TimePeriod.parse("YEAR:3,MONTH:3");
		System.out.println(tp);
	}
}
