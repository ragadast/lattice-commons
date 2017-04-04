/*
 * Copyright Jurong Port Pte Ltd
 * Created on Apr 12, 2011
 */
package org.latticesoft.socket;

import java.io.*;
import java.net.*;
import java.util.concurrent.*; 

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.latticesoft.util.common.*;
import org.latticesoft.util.container.*;
import org.latticesoft.util.resource.*;
import org.latticesoft.util.convert.*;
import org.latticesoft.command.*;

public class SocketServer implements Runnable, RejectedExecutionHandler {
	private static final Log log = LogFactory.getLog(SocketServer.class);
	public static final int MODE_SYNCHRONOUS = 1;
	public static final int MODE_ASYNCHRONOUS = 2;
	private int port = 8888;
	private int initialConnSize = 1;
	private int maxConnSize = 2;
	private int maxQueueSize = 5;
	private int mode = MODE_SYNCHRONOUS;
	private ThreadPoolExecutor threadPool;
	private BlockingQueue queue; 
	private PropertyMap pm = PropertyMap.getInstance();
	
	public SocketServer() {
	}

	/** @return Returns the port. */
	public int getPort() {
		return (this.port);
	}

	/**
	 * @param port The port to set.
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/** @return Returns the maxConn. */
	public int getMaxConnSize() {
		return (this.maxConnSize);
	}

	/**
	 * @param maxConn
	 *            The maxConn to set.
	 */
	public void setMaxConnSize(int maxConnSize) {
		this.maxConnSize = maxConnSize;
	}

	/** @return Returns the initialConnSize. */
	public int getInitialConnSize() {
		return (this.initialConnSize);
	}

	/** @param initialConnSize The initialConnSize to set. */
	public void setInitialConnSize(int initialConnSize) {
		this.initialConnSize = initialConnSize;
	}
	/** @return Returns the maxQueueSize. */
	public int getMaxQueueSize() {
		return (this.maxQueueSize);
	}

	/** @param maxQueueSize The maxQueueSize to set. */
	public void setMaxQueueSize(int maxQueueSize) {
		this.maxQueueSize = maxQueueSize;
	}
	
	/** @return Returns the mode. */
	public int getMode() {
		return (this.mode);
	}

	/** @param mode The mode to set. */
	public void setMode(int mode) {
		this.mode = mode;
	}

	public void run() {
		if (this.init()) {
			this.listen();
		}
	}

	public boolean init() {
		boolean b = false;

		if (this.port <= 0 || this.maxConnSize <= 0) {
			this.maxConnSize = pm.getInt("maxConnSize");
			this.initialConnSize = pm.getInt("initialConnSize");
			this.maxQueueSize = pm.getInt("maxQueueSize");
			this.port = pm.getInt("port");
			this.mode = pm.getInt("mode");
			
			if (log.isInfoEnabled()) {
				log.info("Mode: " + this.mode);
				log.info("Port: " + this.port);
				log.info("MaxConnSize: " + this.maxConnSize);
				log.info("InitialConnSize: " + this.initialConnSize);
				log.info("MaxQueueSize: " + this.maxQueueSize);
			}
			if (this.port > 0 && this.maxConnSize > 0) {
				b = true;
			}
		} else {
			b = true;
		}
		queue = new ArrayBlockingQueue(maxQueueSize);	
		threadPool = new ThreadPoolExecutor(this.initialConnSize, this.maxConnSize, 10, TimeUnit.SECONDS, queue, this);

		return b;
	}

	public void listen() {
		ServerSocket listener = null;
		Socket socket = null;
		SocketHelper helper = null;
		SocketRequest request = null;
		ObjectInputStream ois = null;
		boolean continueLoop = false;
		try {
			listener = new ServerSocket(port);
			if (log.isDebugEnabled()) {
				log.debug("listener: " + listener);
			}
			continueLoop = true;
		} catch (Exception e) {
			if (log.isErrorEnabled()) {
				log.error("Listening error", e);
			}
		}
		if (log.isDebugEnabled()) {
			log.debug("Starting to listen: " + continueLoop);
		}
		while (continueLoop && this.maxConnSize > 0) {
			try {
				socket = listener.accept();
				ois = new ObjectInputStream(socket.getInputStream());
				Object o = ois.readObject();
				if (o != null && o instanceof SocketRequest) {
					request = (SocketRequest)o;
					if (this.MODE_SYNCHRONOUS == this.mode) {
						request.setSocket(socket);
					}
					request.setId(MiscUtil.getSysId());
					if (log.isInfoEnabled()) {
						log.info(request.toString());
					}
					if (request.isToEndServer()) {
						continueLoop = false;
						threadPool.shutdown();
						ThreadUtil.sleep(5000);
					} else if (request.isExecuteCommand()) {
						helper = new SocketHelper(request, this);
						threadPool.execute(helper);
					}
				}
			} catch (Exception e) {
				if (log.isErrorEnabled()) {
					log.error("Exception in accepting new request", e);
				}
			} finally {
				try { ois.close(); } catch (Exception e) {}
				ois = null;
				if (this.mode == this.MODE_ASYNCHRONOUS) {
					try { socket.close(); } catch (Exception e) {}
				}
				socket = null;
			}
		}
		if (log.isInfoEnabled()) {
			log.info("Server ended");
		}
	}

	/* (non-Javadoc)
	 * @see java.util.concurrent.RejectedExecutionHandler#rejectedExecution(java.lang.Runnable, java.util.concurrent.ThreadPoolExecutor)
	 */
	public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
		if (log.isErrorEnabled()) {
			log.error("Request rejected " + r);
		}
	}

	private static void initProperties() {
		String propFile = System.getProperty("prop");
		if (log.isInfoEnabled()) {
			log.info("Properties: " + propFile);
		}
		if (propFile != null) {
			PropertyMap.singletonize(propFile);
		} else {
			InputStream is = null;
			PropertyMap.singletonize();
			PropertyMap pm = PropertyMap.getInstance();
			try {
				is = SocketServer.class.getClassLoader().getResourceAsStream("socketserver.properties");
				if (is != null) {
					pm.read(is);
				}
			} catch(Exception e) {
				if (log.isErrorEnabled()) {
					log.error("Error", e);
				}
			}
		}
	}
	
	public static void main(String[] args) {
		SocketServer.initProperties();
		SocketServer server = new SocketServer();
		server.run();
	}
}
