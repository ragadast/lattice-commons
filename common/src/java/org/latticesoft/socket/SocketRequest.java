/*
 * Copyright Jurong Port Pte Ltd
 * Created on Apr 12, 2011
 */
package org.latticesoft.socket;

import java.io.*;
import java.util.*;
import java.net.*;
import org.latticesoft.util.common.*;
import org.latticesoft.util.container.*;
import org.latticesoft.util.resource.*;
import org.latticesoft.util.convert.*;
import org.latticesoft.command.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SocketRequest implements Serializable {

	private static final Log log = LogFactory.getLog(SocketRequest.class);
	public static final String END_SERVER = "END";
	public static final String EXECUTE_COMMAND = "CMD";
	
	private Map map = new HashMap();
	private String instruction;
	private String commandClassName;
	private transient Socket socket;
	private String id;
	
	
	/** @return Returns the id. */
	public String getId() {
		return (this.id);
	}

	/** @param id The id to set. */
	public void setId(String id) {
		this.id = id;
		this.setAttribute("id", id);
	}

	/** @return Returns the map. */
	public Map getMap() {
		return (this.map);
	}
	
	/** @return Returns the instruction. */
	public String getInstruction() {
		return (this.instruction);
	}

	/** @param command The instruction to set. */
	public void setInstruction(String instruction) {
		this.instruction = instruction;
		this.setAttribute("instruction", instruction);
	}
	
	/** @return Returns the commandClassName. */
	public String getCommandClassName() {
		return (this.commandClassName);
	}

	/** @param commandClassName The commandClassName to set. */
	public void setCommandClassName(String commandClassName) {
		this.commandClassName = commandClassName;
		this.map.put("commandClassName", commandClassName);
	}

	/** @return Returns the socket. */
	public Socket getSocket() {
		return (this.socket);
	}

	/** @param socket The socket to set. */
	public void setSocket(Socket socket) {
		this.socket = socket;
	}
	
	public Object getAttribute(String key) {
		if (key != null && this.map.containsKey(key)) {
			return this.map.get(key);
		}
		return null;
	}
	public void setAttribute(String key, Object o) {
		if (key != null && o != null) {
			this.map.put(key, o);
		}
	}
	
	public Map getAttributes() {
		return this.map;
	}

	public Command getCommandClass() {
		Command cmd = null;
		if (this.commandClassName != null) {
			Object o = ClassUtil.newInstance(this.commandClassName);
			if (o instanceof Command) {
				cmd = (Command)o;
			}
		}
		return cmd;
	}
	
	public boolean isToEndServer() {
		if (this.instruction != null && 
			SocketRequest.END_SERVER.equalsIgnoreCase(this.instruction)) {
			return true;
		}
		return false;
	}
	
	public boolean isExecuteCommand() {
		if (this.instruction != null && 
			SocketRequest.EXECUTE_COMMAND.equalsIgnoreCase(this.instruction)) {
			return true;
		}
		return false;
	}
	
	/**
	 * Converts the class in a string form
	 * @returns the class in a string form.
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("[Request|");
		sb.append("id:").append(this.getId()).append("|");
		sb.append("instr:").append(this.getInstruction()).append("|");
		sb.append("cmd:").append(this.getCommandClassName());
		sb.append("]");
		return sb.toString();
	}
}
