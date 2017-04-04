/*
 * Copyright Jurong Port Pte Ltd
 * Created on Sep 9, 2014
 */
package org.latticesoft.util.common;

import java.io.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class RuntimeMessagePrinter extends Thread {
	private static final Log log = LogFactory.getLog(RuntimeMessagePrinter.class);

	private InputStream is;
	private String type;
	private OutputStream os;

	public RuntimeMessagePrinter(InputStream is, String type) {
		this(is, type, null);
	}

	public RuntimeMessagePrinter(InputStream is, String type, OutputStream redirect) {
		this.is = is;
		this.type = type;
		this.os = redirect;
	}

	public void run() {
		try {
			PrintWriter pw = null;
			if (os != null) {
				pw = new PrintWriter(os);
			}

			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			while ((line = br.readLine()) != null) {
				if (pw != null) {
					pw.println(line);
				}
				//System.out.println(type + ">" + line);
				if (log.isInfoEnabled()) {
					log.info("[" + type + "] : " + line);
				}
			}
			if (pw != null) {
				pw.flush();
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			this.is = null;
			this.os = null;
			if (log.isInfoEnabled()) {
				log.info("[" + type + "] : Done");
			}
		}
	}
}
