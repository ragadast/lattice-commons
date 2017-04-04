/*
 * Copyright Jurong Port Pte Ltd
 * Created on Apr 12, 2011
 */
package org.latticesoft.socket;

import java.util.*;
import java.net.*;
import java.io.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.latticesoft.util.common.*;
import org.latticesoft.util.container.*;
import org.latticesoft.util.resource.*;
import org.latticesoft.util.convert.*;
import org.latticesoft.command.*;


public class SocketUtil {
	private static final Log log = LogFactory.getLog(SocketUtil.class);
	
	public static void sendRequest(String host, int port, String commandName, Map param) {
		SocketRequest request = new SocketRequest();
		request.setCommandClassName(commandName);
		request.setInstruction(SocketRequest.EXECUTE_COMMAND);
		Map map = request.getMap();
		map.putAll(param);
		ObjectOutputStream oos = null;
		Socket socket = null;
		try {
			socket = new Socket(host, port);
			oos = new ObjectOutputStream(socket.getOutputStream());
			oos.writeObject(request);
		} catch (Exception e) {
			if (log.isErrorEnabled()) {
				log.error("Exception", e);
			}
		} finally {
			try { oos.close(); } catch (Exception e) {}
			try { socket.close(); } catch (Exception e) {}
			oos = null;
			socket = null;
		}
	}
	
	public static void sendEndServerRequest(String host, int port) {
		SocketRequest request = new SocketRequest();
		request.setInstruction(SocketRequest.END_SERVER);
		Map map = request.getMap();
		ObjectOutputStream oos = null;
		Socket socket = null;
		try {
			socket = new Socket(host, port);
			oos = new ObjectOutputStream(socket.getOutputStream());
			oos.writeObject(request);
		} catch (Exception e) {
			if (log.isErrorEnabled()) {
				log.error("Exception", e);
			}
		} finally {
			try { oos.close(); } catch (Exception e) {}
			try { socket.close(); } catch (Exception e) {}
			oos = null;
			socket = null;
		}
	}
	
	public static void main(String[] args) {
		Map map = new HashMap();
		SocketUtil.log.info("Send command");
		int port = 8888; 
		String host = "localhost";
		
		if (args != null && args.length >= 2) {
			host = args[0];
			port = NumeralUtil.parseInt(args[1]);
		}
		for (int i=0; i<10; i++) {
			map.remove("message");
			map.put("message", "Hello World " + i);
			SocketUtil.sendRequest(host, port, "org.latticesoft.socket.TestCommand", map);
		}
		
		//SocketUtil.sendEndServerRequest(host, port);
	}
}
