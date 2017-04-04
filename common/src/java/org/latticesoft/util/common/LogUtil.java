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
package org.latticesoft.util.common;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

/**
 * LogUtil is a logging utility. It helps to determine if the
 * log statement is above or below the stated level of logging.
 */
public final class LogUtil {

	private LogUtil() {}
	/**
	 * Fatal level: the level with the highest priority. The
	 * whole system will cease to function at this level.
	 */
	public static final int FATAL = 1;
	/**
	 * Error level: the level with the second highest priority.
	 * The system will be disrupted. However with proper error
	 * and exception handling, the system should continue to
	 * function except for the affected module.
	 */
	public static final int ERROR = 2;

	/**
	 * Info level: the default level.
	 * At this level the information is output for information
	 * purpose. Note that only essential information should be
	 * output.
	 */
	public static final int INFO = 4;

	/**
	 */
	public static final int DEBUG = 8;
	public static final int WARNING = 16;
	public static final int TRACE = 32;
	public static final String LOG_LEVEL = "";

	/**
	 * Returns the integer equivalent of the <code>String</code> name.
	 * @param level the name of the level.
	 * @return the int representing the level. If none matches, the level
	 * return is INFO.
	 */
	public static int getLevel(String level) {
		if (level == null) return LogUtil.INFO;
		if (level.equalsIgnoreCase("FATAL")) {
			return LogUtil.FATAL;
		} else if (level.equalsIgnoreCase("ERROR")) {
			return LogUtil.ERROR;
		} else if (level.equalsIgnoreCase("INFO")) {
			return LogUtil.INFO;
		} else if (level.equalsIgnoreCase("DEBUG")) {
			return LogUtil.DEBUG;
		} else if (level.equalsIgnoreCase("WARNING")) {
			return LogUtil.WARNING;
		} else if (level.equalsIgnoreCase("TRACE")) {
			return LogUtil.TRACE;
		} else {
			return LogUtil.INFO;
		}
	}

	public static boolean isEnabled (String test, String level) {
		int intTest = LogUtil.getLevel(test);
		int intLevel = LogUtil.getLevel(level);
		return LogUtil.isEnabled(intTest, intLevel, true);
	}

	public static boolean isEnabled (int test, String level) {
		int intLevel = LogUtil.getLevel(level);
		return LogUtil.isEnabled(test, intLevel, true);
	}

	public static boolean isEnabled (String test, int level) {
		int intTest = LogUtil.getLevel(test);
		return LogUtil.isEnabled(intTest, level, true);
	}

	public static boolean isEnabled(int test, int level) {
		return LogUtil.isEnabled(test, level, true);
	}

	public static boolean isEnabled (String test, String level, boolean log) {
		if (!log) return false;
		int intTest = LogUtil.getLevel(test);
		int intLevel = LogUtil.getLevel(level);
		return LogUtil.isEnabled(intTest, intLevel, log);
	}

	public static boolean isEnabled (int test, String level, boolean log) {
		if (!log) return false;
		int intLevel = LogUtil.getLevel(level);
		return LogUtil.isEnabled(test, intLevel, true);
	}

	public static boolean isEnabled (String test, int level, boolean log) {
		if (!log) return false;
		int intTest = LogUtil.getLevel(test);
		return LogUtil.isEnabled(intTest, level, log);
	}

	public static boolean isEnabled (int test, int level, boolean log) {
		return log && (test <= level);
	}

	public static void log(int level, Object o, Throwable t, Log log) {
		if (level == LogUtil.DEBUG) log.debug(o, t);
		else if (level == LogUtil.ERROR) log.error(o, t);
		else if (level == LogUtil.FATAL) log.fatal(o, t);
		else if (level == LogUtil.INFO) log.info(o, t);
		else if (level == LogUtil.TRACE) log.trace(o, t);
		else if (level == LogUtil.WARNING) log.warn(o, t);
	}

	public static void main(String args[]){
		Log log = LogFactory.getLog(LogUtil.class);

		System.out.println (LogUtil.isEnabled("WARNING", "WARNING", true));
		System.out.println (LogUtil.isEnabled("DEBUG", "WARNING", true));
		System.out.println (LogUtil.isEnabled("INFO", "WARNING", true));
		System.out.println (LogUtil.isEnabled("ERROR", "WARNING", true));
		System.out.println (LogUtil.isEnabled("FATAL", "WARNING", true));
		System.out.println ("-----");

		System.out.println (LogUtil.isEnabled("WARNING", "DEBUG", true));
		System.out.println (LogUtil.isEnabled("DEBUG", "DEBUG", true));
		System.out.println (LogUtil.isEnabled("INFO", "DEBUG", true));
		System.out.println (LogUtil.isEnabled("ERROR", "DEBUG", true));
		System.out.println (LogUtil.isEnabled("FATAL", "DEBUG", true));
		System.out.println ("-----");

		System.out.println (LogUtil.isEnabled("WARNING", "INFO", true));
		System.out.println (LogUtil.isEnabled("DEBUG", "INFO", true));
		System.out.println (LogUtil.isEnabled("INFO", "INFO", true));
		System.out.println (LogUtil.isEnabled("ERROR", "INFO", true));
		System.out.println (LogUtil.isEnabled("FATAL", "INFO", true));
		System.out.println ("-----");

		System.out.println (LogUtil.isEnabled("WARNING", "ERROR", true));
		System.out.println (LogUtil.isEnabled("DEBUG", "ERROR", true));
		System.out.println (LogUtil.isEnabled("INFO", "ERROR", true));
		System.out.println (LogUtil.isEnabled("ERROR", "ERROR", true));
		System.out.println (LogUtil.isEnabled("FATAL", "ERROR", true));
		System.out.println ("-----");

		System.out.println (LogUtil.isEnabled("WARNING", "FATAL", true));
		System.out.println (LogUtil.isEnabled("DEBUG", "FATAL", true));
		System.out.println (LogUtil.isEnabled("INFO", "FATAL", true));
		System.out.println (LogUtil.isEnabled("ERROR", "FATAL", true));
		System.out.println (LogUtil.isEnabled("FATAL", "FATAL", true));
		System.out.println ("-----");

		System.out.println (LogUtil.isEnabled("FATAL", "WARNING", false));

		if (LogUtil.isEnabled("FATAL", "FATAL", log.isFatalEnabled())) {
			LogUtil.log(LogUtil.getLevel("FATAL"), "test", null, log);
		}

		if (LogUtil.isEnabled("INFO", LogUtil.INFO, log.isInfoEnabled())) {
			LogUtil.log(LogUtil.getLevel("INFO"), "test", null, log);
		}


	}//*/
}