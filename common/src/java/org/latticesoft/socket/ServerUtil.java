/*
 * Copyright Jurong Port Pte Ltd
 * Created on Apr 13, 2011
 */
package org.latticesoft.socket;
import java.io.InputStream;
import org.latticesoft.util.common.*;
import org.latticesoft.util.container.*;
import org.latticesoft.util.resource.*;
import org.latticesoft.util.convert.*;
import org.latticesoft.command.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ServerUtil implements Command{
	
	private static final Log log = LogFactory.getLog(ServerUtil.class);
	/* (non-Javadoc)
	 * @see org.latticesoft.command.Command#execute(java.lang.Object)
	 */
	public Object execute(Object o) throws CommandException {
		if (o instanceof String[]) {
			String[] args = (String[])o;
			if (args.length < 3) {
				return null;
			}
			String instruction = args[0];
			String host = "localhost";
			int port = 8888;
			int maxConnSize = 5;
			int initialConnSize = 1;
			int qSize = 10;
			int mode = 1;
			if ("stop".equalsIgnoreCase(instruction) && args.length >= 3) {
				host = args[1];
				port = NumeralUtil.parseInt(args[2]);
			} else if ("start".equalsIgnoreCase(instruction) && args.length >= 6) {
				port = NumeralUtil.parseInt(args[1]);
				mode = NumeralUtil.parseInt(args[2]);
				maxConnSize = NumeralUtil.parseInt(args[3]);
				initialConnSize = NumeralUtil.parseInt(args[4]);
				qSize = NumeralUtil.parseInt(args[5]);
			}
			if (log.isInfoEnabled()) {
				log.info("Instruction: " + instruction);
				log.info("Host: " + host);
				log.info("Port: " + port);
				log.info("Mode: " + mode);
				log.info("MaxConnSize: " + maxConnSize);
				log.info("InitialConnSize: " + initialConnSize);
				log.info("QueueSize: " + qSize);
			}
			
			if ("start".equalsIgnoreCase(instruction)) {
				this.startServer(port, mode, maxConnSize, initialConnSize, qSize);
			} else if ("stop".equalsIgnoreCase(instruction)) {
				this.stopServer(host, port);
			}
		}
		return null;
	}
	
	public void startServer(int port, int mode, int maxConnSize, int initialConnSize, int qSize) {
		SocketServer server = new SocketServer();
		server.setInitialConnSize(initialConnSize);
		server.setMaxQueueSize(qSize);
		server.setMaxConnSize(maxConnSize);
		server.setPort(port);
		server.setMode(mode);
		server.run();
	}
	
	public void stopServer(String host, int port) {
		SocketUtil.sendEndServerRequest(host, port);
	}

	public static void main(String[] args) {
		if (args == null || args.length <= 0) {
			System.out.println("Usage: ServerUtil stop <host> <port>");
			System.out.println("Usage: ServerUtil start <port> [mode] [maxConnSize] [initialConnSize] [qSize]");
		}
		String propFile = System.getProperty("prop");
		if (propFile != null) {
			PropertyMap.singletonize(propFile);
		} else {
			InputStream is = null;
			PropertyMap.singletonize();
			PropertyMap pm = PropertyMap.getInstance();
			try {
				is = ServerUtil.class.getClassLoader().getResourceAsStream("socketserver.properties");
				if (is != null) {
					pm.read(is);
				}
			} catch(Exception e) {
				if (log.isErrorEnabled()) {
					log.error("Error", e);
				}
			}
		}

		ServerUtil util = new ServerUtil();
		util.execute(args);
	}
}
