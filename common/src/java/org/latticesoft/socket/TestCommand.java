/*
 * Copyright Jurong Port Pte Ltd
 * Created on Apr 12, 2011
 */
package org.latticesoft.socket;

import java.util.*;
import org.latticesoft.util.common.*;
import org.latticesoft.util.container.*;
import org.latticesoft.util.resource.*;
import org.latticesoft.util.convert.*;
import org.latticesoft.command.*;

import org.latticesoft.command.Command;
import org.latticesoft.command.CommandException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TestCommand implements Command {

	private static final Log log = LogFactory.getLog(TestCommand.class);
	
	public Object execute(Object o) throws CommandException {
		Map map = null;
		if (o instanceof Map) {
			map = (Map)o;
		}
		int sleepTime = NumeralUtil.getRandomInt(5) * 1000;
		ThreadUtil.sleep(sleepTime);
		String s = (String)map.get("message");
		String id = (String)map.get("id");
		if (log.isInfoEnabled()) {
			log.info(id + " : " + s);
		}
		sleepTime = 3 + NumeralUtil.getRandomInt(8) * 1000;
		ThreadUtil.sleep(sleepTime);
		
		if (log.isInfoEnabled()) {
			log.info(id + " command ended");
		}
		
		return null;
	}

}
