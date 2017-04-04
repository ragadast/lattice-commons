/*
 * Copyright Jurong Port Pte Ltd
 * Created on Apr 12, 2011
 */
package org.latticesoft.socket;

import java.io.*;
import java.net.*;
import java.util.*;

import org.latticesoft.util.common.*;
import org.latticesoft.util.container.*;
import org.latticesoft.util.resource.*;
import org.latticesoft.util.convert.*;
import org.latticesoft.command.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SocketHelper implements Runnable {

	private SocketServer server;
	private SocketRequest request;
	private static final Log log = LogFactory.getLog(SocketHelper.class);
	
	public SocketHelper() {
		
	}
	
	public SocketHelper(SocketRequest request, SocketServer server) {
		this.request = request;
		this.server = server;
	}
	
	/** @return Returns the request. */
	public SocketRequest getRequest() {
		return (this.request);
	}

	/** @param request The request to set. */
	public void setRequest(SocketRequest request) {
		this.request = request;
	}
	
	/** @return Returns the server. */
	public SocketServer getServer() {
		return (this.server);
	}

	/** @param server The server to set. */
	public void setServer(SocketServer server) {
		this.server = server;
	}

	public void run() {
		Socket socket = request.getSocket();
		if (this.request == null) { return; }
		try {
			Command cmd = this.request.getCommandClass();
			if (cmd != null) {
				cmd.execute(this.request.getMap());
			}
			
		} catch (Exception e) {
			if (log.isErrorEnabled()) {
				log.error("Exception", e);
			}
		} finally {
			try { socket.close(); } catch (Exception e) {}
			request.setSocket(null);
		}
	}
	
	/**
	 * Converts the class in a string form
	 * @returns the class in a string form.
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(request.toString());
		return sb.toString();
	}

}
