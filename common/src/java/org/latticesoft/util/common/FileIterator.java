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
 * Created on Mar 23, 2006
 *
 */
package org.latticesoft.util.common;

import java.util.Iterator;
import java.io.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class FileIterator implements Iterator {

	private static final Log log = LogFactory.getLog(FileIterator.class);
	private File file = null;
	private InputStream is = null;
	private InputStreamReader isr = null;
	private LineNumberReader lnr = null;
	private String filename;
	private boolean active = false;
	private String buffer = null;
	
	private FileIterator(){}

	public FileIterator(String filename) {
		try {
			File file = new File(filename);
			is = new FileInputStream(file);
			isr = new InputStreamReader(is);
			lnr = new LineNumberReader(isr);
			active = true;
		} catch (Exception e) {
			if (log.isErrorEnabled()) { log.error(e); }
		}
	}

	/** @return Returns the filename. */
	public String getFilename() { return (this.filename); }
	/** @param filename The filename to set. */
	public void setFilename(String filename) { this.filename = filename; }

	/**
	 * No implementation
	 */
	public void remove() {
		if (file != null) file.canRead(); // dummy
	}

	public boolean hasNext() {
		if (!active) return false;
		try { buffer = lnr.readLine(); } catch (Exception e) {}
		if (buffer != null) return true;
		return false;
	}

	public Object next() {
		if (!active) return null;
		return buffer;
	}

	public void close() {
		try { this.lnr.close(); } catch (Exception e) {}
		try { this.isr.close(); } catch (Exception e) {}
		try { this.is.close(); } catch (Exception e) {}
		lnr = null;
		isr = null;
		is = null;
		file = null;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#finalize()
	 */
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		super.finalize();
		this.close();
	}
	
	

}
