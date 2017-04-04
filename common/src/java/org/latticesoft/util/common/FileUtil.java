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

import java.beans.*;
import java.io.*;
import java.util.*;
import java.util.zip.*;
import java.net.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.latticesoft.util.container.ByteData;
import org.latticesoft.util.container.OrderedMap;

public class FileUtil {

	private FileUtil() {}
	private final static Log log = LogFactory.getLog(FileUtil.class);
	public static final int END_OF_FILE = 0;
	public static final int START_OF_FILE = 0;
	
	public static final int MODE_INCLUDE_SUB_DIR = 10;
	public static final int MODE_CURRENT_DIR_ONLY = 20;
	
	public static class Dir1stComparator implements Comparator {
		public int compare(Object o1, Object o2) {
			if (o1 == null || o2 == null) return 0;
			if (!(o1 instanceof File) || !(o2 instanceof File)) {
				return 0;
			}
			File f1 = (File)o1;
			File f2 = (File)o2;
			if (f1.isDirectory() && !f2.isDirectory()) {
				return -1;
			}
			if (!f1.isDirectory() && f2.isDirectory()) {
				return 1;
			}
			String s1 = f1.getName();
			String s2 = f2.getName();
			return s1.compareTo(s2);
		}
	};
	public static class File1stComparator implements Comparator {
		public int compare(Object o1, Object o2) {
			if (o1 == null || o2 == null) return 0;
			if (!(o1 instanceof File) || !(o2 instanceof File)) {
				return 0;
			}
			File f1 = (File)o1;
			File f2 = (File)o2;
			if (f1.isDirectory() && !f2.isDirectory()) {
				return 1;
			}
			if (!f1.isDirectory() && f2.isDirectory()) {
				return -1;
			}
			String s1 = f1.getName();
			String s2 = f2.getName();
			return s1.compareTo(s2);
		}
	};

	/**
	 * Writes the data into a file.
	 * @param filename the name of the file
	 * @param data the data string to be written
	 * @return none
	 */
	public static void writeToFile(String filename, String data) {
		writeToFile(filename, data, false);
	}

	/**
	 * Writes the data into a file.
	 * @param filename the name of the file
	 * @param c the collection to be written
	 * @param append append to the contents of the file or not
	 * @return none
	 */
	public static void writeToFile(String filename, Collection c, boolean append) {
		if (c == null) return;
		Iterator iter = c.iterator();
		writeToFile(filename, iter, append);
	}
	
	/**
	 * Writes the data into a file.
	 * @param filename the name of the file
	 * @param iter the iterator to a loop
	 * @param append append to the contents of the file or not
	 * @return none
	 */
	public static void writeToFile(String filename, Iterator iter, boolean append) {
		File f = null;
		FileWriter fw = null;
		PrintWriter pw = null;
		try {
			f = new File(filename);
			fw = new FileWriter(f, append);
			pw = new PrintWriter(fw);
			while (iter.hasNext()) {
				Object o = iter.next();
				String line = null;
				if (o instanceof String) {
					line = (String)o;
				} else if (o != null){
					line = o.toString();
				}
				if (line != null) {
					pw.println(line);
				}
			}
		} catch (Exception e) {
			if (log.isErrorEnabled()) { log.error(e); }
		} finally {
			try { pw.flush(); } catch (Exception e) {}
			try { fw.flush(); } catch (Exception e) {}
			try { pw.close(); } catch (Exception e) {}
			try { fw.close(); } catch (Exception e) {}
			pw = null;
			fw = null;
		}
	}
	
	
	/**
	 * Writes the data into a file.
	 * @param filename the name of the file
	 * @param data the data string to be written
	 * @param append append to the contents of the file or not
	 * @return none
	 */
	public static void writeToFile(String filename, String data, boolean append) {
		File f = null;
		FileWriter fw = null;
		PrintWriter pw = null;
		try {
			f = new File(filename);
			fw = new FileWriter(f, append);
			pw = new PrintWriter(fw);
			pw.println(data);
		} catch (Exception e) {
			if (log.isErrorEnabled()) { log.error(e); }
		} finally {
			try { pw.flush(); } catch (Exception e) {}
			try { fw.flush(); } catch (Exception e) {}
			try { pw.close(); } catch (Exception e) {}
			try { fw.close(); } catch (Exception e) {}
			pw = null;
			fw = null;
		}
	}
	
	/** 
	 * Writes the byte data to the file
	 * @param filename the name of the file
	 * @param data the data to be written
	 * @param append append to the file or not 
	 */
	public static void writeToFile(String filename, byte[] data, boolean append) {
		File f = null;
		OutputStream os = null;
		try {
			f = new File(filename);
			os = new FileOutputStream(f, append);
			os.write(data);
		} catch (Exception e) {
			if (log.isErrorEnabled()) { log.error(e); }
		} finally {
			try { os.flush(); } catch (Exception e) {}
			os = null;
		}
	}
	
/*
	public static byte[] readBytesFromFile(String filename) {
		File f = null;
		InputStream is = null;
		ArrayList a = new ArrayList();
		try {
			f = new File(filename);
			is = new FileInputStream(f);
			while ()

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try { is.close(); } catch (Exception e) {}
			is = null;
		}
	}
//*/

	/**
	 * Loads a map with a key value pairs from a properties file.
	 * Note that the file name need not have an extension of
	 * ".properties"
	 * @param filename the name of the file to load
	 * @param map the map to be loaded.
	 * @return the same map file passed in or a new map
	 * if the parameter passed in is null.
	 */
	public static Map loadMap(String filename, Map map) {
		InputStream is = null;
		Properties p = new Properties();
		Map retVal = map;
		if (map == null) {
			retVal = new TreeMap();
		}
		File[] files = FileUtil.listFilesAsArray(filename);
		if (files == null) return map;

		for (int i=0; i<files.length; i++) {
			try {
				is = new FileInputStream(files[i]);
				p.load(is);
				retVal.putAll(p);
				p.clear();
			} catch (Exception e) {
				if (log.isErrorEnabled()) { log.error(e); }
			} finally {
				try { is.close(); } catch (Exception e) {}
				is = null;
			}
		}
		return retVal;
	}
	
	/**
	 * Loads a map from a inputstream, the stream is not closed after use
	 * @param is the inputstream
	 * @param map the map to load to
	 * @return the same map after loading
	 */
	public static Map loadMap(InputStream is, Map map) {
		return loadMap(is, map, false);
	}
	/**
	 * Loads a map from a inputstream
	 * @param is the inputstream
	 * @param map the map to load to
	 * @param closeStream close the stream after loading
	 * @return the same map after loading
	 */
	public static Map loadMap(InputStream is, Map map, boolean closeStream) {
		Properties p = new Properties();
		Map retVal = map;
		if (map == null) {
			retVal = new TreeMap();
		}
		try {
			p.load(is);
			retVal.putAll(p);
			p.clear();
		} catch (Exception e) {
			if (log.isErrorEnabled()) { log.error(e); }
		} finally {
			if (closeStream) {
				try { is.close(); } catch (Exception e) {}
			}
			is = null;
		}
		return retVal;
	}

	/**
	 * @see #loadMap
	 */
	public static Map loadMap(String filename) {
		Map map = new TreeMap();
		return loadMap(filename, map);
	}
	
	/**
	 * @see #listFiles(String, int)
	 */
	public static List listFiles(String path) {
		return listFiles(path, 0);
	}

	/**
	 * Lists the files from a stated directories.
	 * Note that this method accepts wildcards,
	 * for example "C:/Temp/*.txt"
	 * @param path the path to be listed.
	 * @param type file or directory
	 * @param int mode 0 - include both, 1 - file only, 2 directory only
	 * @return a collection of files (File)
	 */
	public static List listFiles(String path, int mode) {
		ArrayList retVal = new ArrayList();
		try {
			path = StringUtil.replace(path, "\\", "/");
			if (log.isDebugEnabled()) { log.debug("Path: " + path); }
			int index = path.indexOf("*");
			
			// no wild card so just list all the files in that path
			if (index < 0) {
				File f1 = new File(path);
				if (log.isDebugEnabled()) { log.debug("File: " + f1); }
				if (f1.isFile() && (mode == 0 || mode == 1)) {
					retVal.add(f1);
				}
				if (log.isDebugEnabled()) { log.debug("returning: " + retVal); }
				return retVal;
			}
			
			// with wild card
			// get the wild card type and path
			String[] parts = StringUtil.tokenizeIntoStringArray(path, "/", true);
			StringBuffer sb = new StringBuffer();
			String wildcard = parts[parts.length - 1];// last part of the path
			
			for (int i=0; i<parts.length-1; i++) {
				if (log.isDebugEnabled()) { log.debug(i + ":" + parts[i]); }
				sb.append(parts[i]);
				sb.append("/");
			}
			String actualPath = sb.toString();
			if (log.isDebugEnabled()) { log.debug("Path: " + actualPath); }
			
			File f = new File(actualPath);
			if (f.isDirectory()) {
				if (log.isDebugEnabled()) { log.debug("File is Directory " + f); }
				index = wildcard.lastIndexOf("*.");
				if (index > -1) {
					String startingString = wildcard.substring(0, index); 
					File[] files = f.listFiles(new FileExtensionFilter(wildcard));

					if (files != null) {
						for (int i = 0; i<files.length; i++) {
							if (log.isDebugEnabled()) { log.debug(i + ":" + files[i]); }
							if (index > 0) {
								if (files[i].getName().startsWith(startingString)) {
									if (mode == 1) {
										if (files[i].isFile()) {
											retVal.add(files[i]);
										}
									} else if (mode == 2) {
										if (files[i].isDirectory()) {
											retVal.add(files[i]);
										}
									} else {
										retVal.add(files[i]);
									}
								}
							} else {
								if (mode == 1) {
									if (files[i].isFile()) {
										retVal.add(files[i]);
									}
								} else if (mode == 2) {
									if (files[i].isDirectory()) {
										retVal.add(files[i]);
									}
								} else {
									retVal.add(files[i]);
								}
							}
						}
					}
				}//*/
			}
		} catch (Exception e) {
			if (log.isErrorEnabled()) { log.error(e); }
		}
		if (log.isDebugEnabled()) { log.debug(retVal); }
		return retVal;
	}

	/**
	 * Lists the files as an array
	 * @see #listFiles
	 */
	public static File[] listFilesAsArray(String s){
		List l = FileUtil.listFiles(s);
		return (File[])l.toArray(new File[l.size()]);
	}
	/**
	 * Lists the files as an array
	 * @see #listFiles
	 */
	public static File[] listFilesAsArray(String s, int mode){
		List l = FileUtil.listFiles(s, mode);
		return (File[])l.toArray(new File[l.size()]);
	}
	
	/**
	 * Lists the files as a List
	 * @see #listFiles
	 */
	public static List listFilesAsList(String s) {
		List l = FileUtil.listFiles(s);
		if (l instanceof List) { return (List)l; }
		ArrayList a = new ArrayList();
		a.addAll(l);
		return a;
	}
	/**
	 * Lists the files as a List
	 * @see #listFiles
	 */
	public static List listFilesAsList(String s, int mode) {
		List l = FileUtil.listFiles(s, mode);
		if (l instanceof List) { return (List)l; }
		ArrayList a = new ArrayList();
		a.addAll(l);
		return a;
	}
	
	/** 
	 * Deletes a file
	 * @param filename the name of file to delete  
	 */
	public static void deleteFile(String filename) {
		File f = new File(filename);
		deleteFile(f);
		if (log.isDebugEnabled()) { log.debug(filename + " deleted."); }
	}
	private static void deleteFile(File f) {
		try {
			if (f.exists() && f.isFile()) {
				f.delete();
			} else if (f.exists() && f.isDirectory()){
				File[] childs = f.listFiles();
				for (int i=0; i<childs.length; i++) {
					deleteFile(childs[i]);
				}
			} else {
			}
		} catch (Exception e) {
			String s = StringUtil.getStackTraceMessage(e);
			if (log.isErrorEnabled()) { log.error(s); }
		}
	}
	
	/** 
	 * Renames a file
	 * @param filename the name of file to rename/move to  
	 */
	public static boolean renameFile(String filename, String newName) {
		boolean result = false;
		try {
			File f = new File(filename);
			if (f.exists()) {
				result = f.renameTo(new File(newName));
				if (log.isDebugEnabled()) { log.debug(filename + " renamed : " + result); }
			} else {
				if (log.isDebugEnabled()) { log.debug(filename + " does not exist."); }
			}
		} catch (Exception e) {
			if (log.isErrorEnabled()) { log.error("Error", e); }
		}
		return result;
	}
	
	private static final int BUFFER_SIZE = 512;
	private static void copyFileBytes(File fSrc, File fTgt) {
		if (fSrc == null || fTgt == null) { return; }
		if (fSrc.isDirectory() || fTgt.isDirectory()) { return; }
		InputStream is = null;
		OutputStream os = null;
		byte[] b = new byte[BUFFER_SIZE];
		long len = fSrc.length();
		int cnt = (int)len / (BUFFER_SIZE); 
		int readCnt = 0;
		boolean failedLastPart = false;
		try {
			is = new FileInputStream(fSrc);
			os = new FileOutputStream(fTgt);
			for (readCnt=0; readCnt<cnt; readCnt++) {
				is.read(b);
				os.write(b);
			}
/*
			int readSize = readCnt*BUFFER_SIZE;
			int finalChuckSize = (int)len-readSize;
			try {
				is.read(b, readSize, finalChuckSize);
				os.write(b, 0, finalChuckSize);
			} catch (Exception e) {
				failedLastPart = true;
			}//*/
			failedLastPart = true;
			if (failedLastPart) {
				int data = is.read();
				while (data > -1) {
					os.write(data);
					data = is.read();
				}
			}
		} catch (Exception e) {
			if (log.isErrorEnabled()) { log.error("Copy fail", e); }
		} finally {
			try { is.close(); } catch (Exception e) {}
			try { os.close(); } catch (Exception e) {}
			try { fTgt.setLastModified(fSrc.lastModified()); } catch (Exception e) {}
		}
	}
	/** 
	 * Copy a file
	 * @param filename the name of file to be copied
	 * @param dest the destination filename
	 */
	public static void copyFile(String filename, String dest) {
		File fSrc = new File(filename);
		File fTgt = new File(dest);

		if (filename.indexOf("*") > -1) {
			List l = FileUtil.listFilesAsList(filename);
			File destDir = null;
			if (fTgt.isDirectory()) {
				destDir = fTgt;
			} else {
				String s = FileUtil.getDirectory(dest);
				destDir = new File(s);
			}
			for (int i=0; i<l.size(); i++) {
				File srcFile = (File)l.get(i);
				File destFile = new File(destDir.getPath() + "/" + srcFile.getName());
				copyFile(srcFile, destFile);
			}
			return;
		} else if (fSrc.isFile() && fTgt.isFile()) {
			copyFile(fSrc, fTgt);
		} else if (fSrc.isDirectory() && fTgt.isDirectory()) {
			copyFile(fSrc, fTgt);
		} else if (fSrc.isFile() && fTgt.isDirectory()) {
			File destFile = new File(fTgt.getPath() + "/" + fSrc.getName());
			copyFile(fSrc, destFile);
		} 
	}

	/** 
	 * Copy a file
	 * @param fSrc source file
	 * @param fTgt target file / destination
	 */
	public static void copyFile(File fSrc, File fTgt) {
		InputStream is = null;
		OutputStream os = null;
		try {
			if (fSrc.isFile()) {
				if (fSrc.exists()) {
					copyFileBytes(fSrc, fTgt);
					if (log.isDebugEnabled()) { log.debug(fSrc + " copied."); }
				} else {
					if (log.isDebugEnabled()) { log.debug(fSrc + " does not exist."); }
				}
			} else if (fSrc.isDirectory()){
				if (!fTgt.exists()) {
					fTgt.mkdir();
				}
				if (fTgt.exists()) {
					File[] f = fSrc.listFiles();
					if (f != null) {
						StringBuffer sb = new StringBuffer();
						for (int i=0; i<f.length; i++) {
							if (f[i] != null && f[i].exists()) {
								if (f[i].isFile()) {
									sb.setLength(0);
									sb.append(FileUtil.getUnixPath(fTgt.getPath()));
									sb.append("/");
									sb.append(f[i].getName());
									File f1 = new File(sb.toString());
									copyFileBytes(f[i], f1);
								} else if (f[i].isDirectory()) {
									sb.setLength(0);
									sb.append(FileUtil.getUnixPath(fTgt.getPath()));
									sb.append("/");
									sb.append(fSrc.getName());
									copyFile(f[i].getPath(), sb.toString());
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			if (log.isErrorEnabled()) { log.error("Error", e); }
		} finally {
			try { is.close(); } catch (Exception e) {}
			try { os.close(); } catch (Exception e) {}
		}
	}
	
	/**
	 * Using OS api to copy file
	 */
	public static void copyFileUsingOS(String src, String tgt) {
		String s = System.getProperty("os.name");
		String sep = System.getProperty("file.separator");
		s = s.toLowerCase();
		StringBuffer sb = new StringBuffer();
		if (s.indexOf("win") > -1) {
			File fSrc = new File(src);
			File fTgt = new File(tgt);
			if (fSrc.isDirectory()) {
				if (!fTgt.exists()) {
					fTgt.mkdir();
				}
				sb.setLength(0);
				sb.append("xcopy /q /s ");
				sb.append(FileUtil.getWinPath(src));
				sb.append(sep);
				sb.append("*.* ");
				sb.append(FileUtil.getWinPath(tgt));
			} else if (fSrc.isFile()) {
				sb.setLength(0);
				sb.append("xcopy /q ");
				sb.append(FileUtil.getWinPath(src));
				sb.append(" ");
				sb.append(FileUtil.getWinPath(fTgt.getParent()));
				sb.append(sep);
			}
		} else {
			File fSrc = new File(src);
			File fTgt = new File(tgt);
			if (fSrc.isDirectory()) {
				if (!fTgt.exists()) {
					fTgt.mkdir();
				}
				sb.setLength(0);
				sb.append("cp -r ");
				sb.append(FileUtil.getWinPath(src));
				sb.append(sep);
				sb.append("*.* ");
				sb.append(FileUtil.getWinPath(tgt));
			} else if (fSrc.isFile()) {
				sb.setLength(0);
				sb.append("cp ");
				sb.append(FileUtil.getWinPath(src));
				sb.append(" ");
				sb.append(FileUtil.getWinPath(tgt));
			}
		}
		try {
			if (log.isDebugEnabled()) { log.debug(sb.toString()); }
			Process p = Runtime.getRuntime().exec(sb.toString());
			InputStream err = p.getErrorStream();
			InputStream in = p.getInputStream();
			RuntimeMessagePrinter rmpIn = new RuntimeMessagePrinter(in, "IN ");
			RuntimeMessagePrinter rmpErr = new RuntimeMessagePrinter(err, "ERR");
			rmpIn.start();
			rmpErr.start();
			p.waitFor();
		} catch (Exception e) {
			if (log.isErrorEnabled()) { log.error(StringUtil.getStackTraceMessage(e)); }
		}
	}
	
	
	/**
	 * @see FileUtil#readLinesFromStream(InputStream, boolean)
	 * Default is set close to true 
	 */
	public static Collection readLinesFromStream(InputStream is) {
		return FileUtil.readLinesFromStream(is, true);
	}
	/**
	 * Read lines from an inputstream.
	 * @param is the InputStream
	 * @param close if set to true, after reading the input stream would be closed.
	 * @return an collection of the lines read
	 */
	public static List readLinesFromStream(InputStream is, boolean close) {
		return FileUtil.readLinesFromStream(is, true, 1, -1);
	}
	/**
	 * Read lines from an inputstream.
	 * @param is the InputStream
	 * @param start the line of the file to read from. Line starts from 0. 
	 * If count is 1, 1 line would be skipped.
	 * @param end the line of the file to stop reading
	 * @return an collection of the lines read
	 */
	public static List readLinesFromStream(InputStream is, int start, int end) {
		return FileUtil.readLinesFromStream(is, true, start, end);
	}
	/**
	 * Read lines from an inputstream.
	 * @param is the InputStream
	 * @param close if set to true, after reading the input stream would be closed.
	 * @param start the line of the file to read from. Line starts from 0. 
	 * If count is 1, 1 line would be skipped.
	 * @param end the line of the file to stop reading. The end line is included.
	 * @return an collection of the lines read
	 */
	public static List readLinesFromStream(InputStream is, boolean close, int start, int end) {
		if (is == null) { return null; }
		ArrayList a = new ArrayList();
		Reader r = null;
		LineNumberReader lr = null;
		try {
			r = new InputStreamReader(is);
			lr = new LineNumberReader(r);
			String line = null;
			do {
				line = lr.readLine();
				int currLine = lr.getLineNumber();
				if (start > 0 && end > 0 && currLine >= start && currLine <= end) {
					if (line != null) {
						a.add(line);
					}
				}
				if (start == START_OF_FILE && end == END_OF_FILE) {
					if (line != null) {
						a.add(line);
					}
				}
				if (end > 0 && currLine > end) {
					break;
				}
			} while (line != null);
		} catch (Exception e) {
			if (log.isErrorEnabled()) { log.error(e); }
		} finally {
			if (close) {
				try { lr.close(); } catch (Exception e) {}
				try { r.close(); } catch (Exception e) {}
			}
			lr = null;
			r = null;
		}
		return a;
	}
	
	/**
	 * Read lines from URL 
	 */
	public static List readLinesFromURL(URL url) {
		return readLinesFromURL(url, true, START_OF_FILE, END_OF_FILE);
	}
	/**
	 * Read lines from URL 
	 */
	public static List readLinesFromURL(URL url, boolean close, int start, int end) {
		if (url == null) { return null; }
		List c = null;
		try {
			InputStream is = url.openStream();
			c = FileUtil.readLinesFromStream(is, close, start, end);
			is = null;
		} catch (Exception e) {
			if (log.isErrorEnabled()) { log.error(e); }
		} finally {
		}
		return c;
	}
	/**
	 * Read lines from URL 
	 */
	public static List readLinesFromURL(String url) {
		return readLinesFromURL(url, true, START_OF_FILE, END_OF_FILE);
	}
	/**
	 * Read lines from URL 
	 */
	public static List readLinesFromURL(String url, boolean close, int start, int end) {
		if (url == null) { return null; }
		List l = null;
		try {
			URL u = new URL(url);
			InputStream is = u.openStream();
			l = FileUtil.readLinesFromStream(is, close, start, end);
			is = null;
			u = null;
		} catch (Exception e) {
			if (log.isErrorEnabled()) { log.error(e); }
		} finally {
		}
		return l;
	}
	/**
	 * Reads the lines from a text file into a collection of
	 * Strings.
	 * @param filename the text file to be read
	 * @return a collection of strings
	 */
	public static List readLinesFromFile(String filename) {
		return readLinesFromFile(filename, true, START_OF_FILE, END_OF_FILE);
	}

	/**
	 * Reads the lines from a text file into a collection of
	 * Strings.
	 * @param filename the text file to be read
	 * @param start the line of the file to read from. Line starts from 0. 
	 * If count is 1, 1 line would be skipped.
	 * @param end the line of the file to stop reading
	 * @return a collection of strings
	 */
	public static List readLinesFromFile(String filename, boolean close, int start, int end) {
		if (filename == null) { return null; }
		List l = null;
		try {
			File f = new File(filename);
			InputStream is = new FileInputStream(f);
			l = readLinesFromStream(is, close, start, end);
		} catch (Exception e) {
			if (log.isErrorEnabled()) { log.error(e); }
		} finally {
		}
		return l;
	}
	
	/** Read a file which is divided into sections. 
	 * Section header are enclosed by square brackets.
	 * [SECTION HEADER]
	 * The section header will be the key to retrieved the list.
	 * The KeySet will return the order which the file is created.
	 * The [ALL] key will retrieve the original file input
	 */
	public static Map readSectionsFromFile(String filename, boolean close, int start, int end) {
		Map om = new OrderedMap();
		List l = FileUtil.readLinesFromFile(filename, close, start, end);
		om.put("[ALL]", l);
		List ll = null;
		for (int i=0; i<l.size(); i++) {
			String line = (String)l.get(i);
			if (line.startsWith("[") && line.endsWith("]")) {
				String header = line.substring(1, line.length()-1);
				ll = new ArrayList();
				om.put(header, ll);
			} else if (ll != null) {
				ll.add(line);
			}
		}
		return om;
	}
	/** @see #readSectionsFromFile */
	public static Map readSectionsFromFile(String filename, boolean close) {
		return readSectionsFromFile(filename, close, START_OF_FILE, END_OF_FILE);
	}
	/** @see #readSectionsFromFile */
	public static Map readSectionsFromFile(String filename) {
		return readSectionsFromFile(filename, true, START_OF_FILE, END_OF_FILE);
	}
	
	/**
	 * Checks whethers the file exists or not
	 * @return true if it exists
	 */
	public static boolean exist(String filename) {
		boolean retVal = false;
		try {
			File f = new File(filename);
			retVal = f.isDirectory() || f.isFile();
		} catch (Exception e) {
			if (log.isErrorEnabled()) { log.error(e); }
		}
		return retVal;
	}

	/**
	 * Checks whethers the file is a directory or not
	 * @return true if it is a directory
	 */
	public static boolean isDirectory(String filename) {
		boolean retVal = false;
		try {
			File f = new File(filename);
			retVal = f.isDirectory();
		} catch (Exception e) {
			if (log.isErrorEnabled()) { log.error(e); }
		}
		return retVal;
	}
	/**
	 * @see #isDirectory(String)
	 */
	public static boolean isFolder(String filename) {
		return isDirectory(filename);
	}

	/**
	 * Checks whethers the file is a file or not
	 * @return true if it is a file
	 */
	public static boolean isFile(String filename) {
		boolean retVal = false;
		try {
			File f = new File(filename);
			retVal = f.isFile();
		} catch (Exception e) {
			if (log.isErrorEnabled()) { log.error(e); }
		}
		return retVal;
	}

	/**
	 * Checks whethers the file is hidden or not
	 * @return true if it is hidden
	 */
	public static boolean isHidden(String filename) {
		boolean retVal = false;
		try {
			File f = new File(filename);
			retVal = f.isHidden();
		} catch (Exception e) {
			if (log.isErrorEnabled()) { log.error(e); }
		}
		return retVal;
	}
	
	/**
	 * Creates the directory
	 * @param filename the folder's name
	 * @return true if created
	 */
	public static boolean createDirectory(String dirName) {
		boolean retVal = false;
		if (!FileUtil.exist(dirName)) {
			try {
				retVal = new File(dirName).mkdir();
			} catch (Exception e) {
			}
		}
		return retVal;
	}
	/** @see #createDirectory(String) */
	public static boolean createFolder(String dirName) {
		return FileUtil.createDirectory(dirName);
	}
	
	/**
	 * Appends a string to a filename before the file extension
	 * E.g. originalFilename.txt ---(append 123)---> originalFilename123.txt
	 * @param filename the original filename
	 * @param append the text to append to
	 * @return the final result
	 */
	public static String appendFilename(String filename, String append) {
		if (filename == null || append == null) {
			return filename;
		}
		int index = filename.lastIndexOf(".");
		StringBuffer sb = new StringBuffer();
		sb.append(filename.substring(0, index));
		sb.append(append);
		sb.append(filename.substring(index, filename.length()));
		return sb.toString();
	}
	/** Change the extension of the file */
	public static String changeFileExtension(String filename, String extension) {
		if (filename == null || extension == null) {
			return filename;
		}
		int index = filename.lastIndexOf(".");
		StringBuffer sb = new StringBuffer();
		sb.append(filename.substring(0, index));
		sb.append(".");
		sb.append(extension);
		return sb.toString();
	}
	
	/** Gets an input stream */
	public static InputStream getInputStream(String filename) {
		File f = new File(filename);
		return getInputStream(f);
	}
	/** Gets an input stream */
	public static InputStream getInputStream(File f) {
		InputStream is = null;
		try {
			is = new FileInputStream(f);
		} catch (Exception e) {
			try { is.close(); } catch (Exception ex) {}
			is = null;
		}
		return is;
	}
	/** Gets a line number reader */
	public static LineNumberReader getLineNumberReader(String filename) {
		InputStream is = getInputStream(filename);
		return getLineNumberReader(is);
	}

	/** Gets a line number reader */
	public static LineNumberReader getLineNumberReader(InputStream is) {
		if (is == null) return null;
		LineNumberReader lr = null;
		try {
			lr = new LineNumberReader(new InputStreamReader(is));
		} catch (Exception e) {
			if (log.isErrorEnabled()) { log.error(e); }
		}
		return lr;
	}
	
	/** Gets an output stream */
	public static OutputStream getOutputStream(String filename) {
		OutputStream os = null;
		try {
			File f = new File(filename);
			os = new FileOutputStream(f);
		} catch (Exception e) {
			try { os.close(); } catch (Exception ex) {}
			os = null;
		}
		return os;
	}
	/** Closes an input stream */
	public static void closeStream(InputStream is) {
		try { is.close(); } catch (Exception e) {}
	}
	/** Closes an output stream */
	public static void closeStream(OutputStream os) {
		try { os.close(); } catch (Exception e) {}
	}
	
	/**
	 * @see #downloadFromURL(URL, String)
	 */
	public static void downloadFromURL(String url, String outputFilename) {
		try {
			URL u = new URL(url);
			downloadFromURL(u, outputFilename); 
		} catch (Exception e) {
			if (log.isErrorEnabled()) { log.error(e); }
		}
	}
	/**
	 * Download lines from an URL into a file
	 * @param url the url to download from
	 * @param the output filename
	 */
	public static void downloadFromURL(URL url, String outputFilename) {
		if (url == null || outputFilename == null) {
			return;
		}
		InputStream is = null;
		OutputStream os = null;
		Reader r = null;
		BufferedReader br = null;
		PrintWriter pw = null;
		String line = null;
		try {
			is = url.openStream();
			os = FileUtil.getOutputStream(outputFilename);
			r = new InputStreamReader(is);
			br = new BufferedReader(r);
			pw = new PrintWriter(os);
			
			do {
				line = br.readLine();
				if (line != null) {
					pw.println(line);
				}
			} while (line != null);
		} catch (Exception e) {
			if (log.isErrorEnabled()) { log.error(e); }
		} finally {
			try { br.close(); } catch (Exception e) {}
			try { r.close(); } catch (Exception e) {}
			try { is.close(); } catch (Exception e) {}
			try { pw.close(); } catch (Exception e) {}
			try { os.close(); } catch (Exception e) {}
		}
	}

	/**
	 * @see #uploadToURL(URL, String)
	 */
	public static void uploadToURL(String url, String inputFilename) {
		try {
			URL u = new URL(url);
			FileUtil.uploadToURL(u, inputFilename);
		} catch (Exception e) {
			if (log.isErrorEnabled()) { log.error(e); }
		}
	}

	/**
	 * Uploads a file content to an url
	 * @param url the url to upload to
	 * @param input filename the input file to read from
	 */
	public static void uploadToURL(URL url, String inputFilename) {
		if (url == null || inputFilename == null) {
			return;
		}
		InputStream is = null;
		OutputStream os = null;
		Reader r = null;
		BufferedReader br = null;
		PrintWriter pw = null;
		String line = null;
		URLConnection conn = null;
		try {
			conn = url.openConnection();
			conn.connect();
			os = conn.getOutputStream();
			is = FileUtil.getInputStream(inputFilename);
			r = new InputStreamReader(is);
			br = new BufferedReader(r);
			pw = new PrintWriter(os);
			do {
				line = br.readLine();
				if (line != null) {
					pw.println(line);
				}
			} while (line != null);
		} catch (Exception e) {
			if (log.isErrorEnabled()) { log.error(e); }
		} finally {
			try { br.close(); } catch (Exception e) {}
			try { r.close(); } catch (Exception e) {}
			try { is.close(); } catch (Exception e) {}
			try { pw.close(); } catch (Exception e) {}
			try { os.close(); } catch (Exception e) {}
		}
	}

	/** Close the iterator */
	public static void closeFileIterator(Iterator iter) {
		if (iter != null && iter instanceof FileIterator) {
			((FileIterator)iter).close();
		}
	}
	
	/** 
	 * Extracts the struct of the file inside a directory.
	 * For example file1 is C:/Temp/9/theDir/inner1/inner2/inner3/file1.txt
	 * We want the struct under C:/Temp/9/theDir directory
	 * So we have the result as /inner1/inner2/inner3/file1.txt
	 * with respect to the parent C:/Temp/9/theDir
	 * @param child the file (may be a directory also)
	 * @param parent the parent directory (must be a directory and must be parent)
	 * @param useUnixSeparator use / as file separator
	 * @return the structure in string
	 */
	public static String getRelativePath(File child, File parent, boolean useUnixSeparator) {
		if (child == null || parent == null) {
			return null;
		}
		if (child.getPath().indexOf(parent.getPath()) > 0) { 
			// must be at the starting or == 0
			return child.getPath();
		}
		String retVal = child.getPath().substring(parent.getPath().length());
		if (useUnixSeparator && retVal.indexOf("\\") > -1) {
			retVal = StringUtil.replace(retVal, "\\", "/");
		}
		return retVal;
	}
	/** @see #getRelativePath(File, File, boolean) */
	public static String getRelativePath(File child, File parent) {
		return getRelativePath(child, parent, true);
	}
	/** @see #getRelativePath(File, File, boolean) */
	public static String getRelativePath(String child, String parent) {
		File fChild = new File(child);
		File fParent = new File(parent);
		return getRelativePath(fChild, fParent, true);
	}
	
	/** Return the directory of the file */
	public static String getDirectory(String s) {
		if (s == null) {
			return s;
		}
		File f = new File(s);
		return getDirectory(f);
	}
	/** Return the directory of the file */
	public static String getDirectory(File f) {
		String path = f.getAbsolutePath();
		String filename = f.getName();
		int index = -1;
		StringBuffer sb = new StringBuffer();
		try {
			index = path.lastIndexOf(filename);
			if (index > 0) {
				sb.append(path.substring(0, index));
			}
		} catch (Exception e) {
		}
		return sb.toString();
	}
	
	public static long getFileSize(String filename) {
		File file = new File(filename);
		if (!file.exists() || !file.isFile()) {
			return -1;
		}
		return file.length();
	}
	
	/**
	 * Sort file list
	 * @param l list of files
	 * @param boolean directory 1st 
	 */
	public static void sortFileList(List l, boolean dir1st) {
		if (dir1st) {
			Collections.sort(l, new FileUtil.Dir1stComparator());
		} else {
			Collections.sort(l, new FileUtil.File1stComparator());
		}
	}
	
	public static String getUnixPath(String s) {
		return StringUtil.replace(s, "\\", "/");
	}
	public static String getWinPath(String s) {
		return StringUtil.replace(s, "/", "\\");
	}
	
	/** @see #writeToZip(File, Collection) */
	public static void writeToZip(String zip, Collection c, String baseDir) {
		if (c == null || c.size() == 0) return;
		File f = new File(zip);
		if (baseDir == null) {
			writeToZip(f, c, null);
		} else {
			File bd = new File(baseDir);
			writeToZip(f, c, bd);
		}
	}
	
	/**
	 * Write a collection of files to a zip file
	 * @param zip the zip file
	 * @param c the collection of files
	 */
	public static void writeToZip(File zip, Collection c, File baseDir) {
		ZipOutputStream zos = null;
		if (c == null || c.size() == 0) return;
		try {
			Iterator iter = c.iterator();
			zos = new ZipOutputStream(new FileOutputStream(zip, true));
			while (iter.hasNext()) {
				Object iterObj = iter.next();
				if (iterObj != null && iterObj instanceof File) {
					File file = (File)iterObj;
					if (file.isFile()) {
						FileUtil.writeToZip(zos, file, baseDir);
					} else if (file.isDirectory()) {
						Map map = null; 
						if (baseDir == null) {
							map = FileUtil.listAllFiles(file.getPath(), null);
						} else {
							map = FileUtil.listAllFiles(file.getPath(), baseDir.getPath());
						}
						Iterator iter2 = map.keySet().iterator();
						while (iter.hasNext()) {
							Object iterObj2 = iter2.next();
							if (iterObj != null) {
								String key = (String)iterObj2;
								Object o = map.get(key);
								if (o instanceof File) {
									File f = (File)o;
									if (f.isFile()) {
										FileUtil.writeToZip(zos, f, null);
									}
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			if (log.isErrorEnabled()) { log.error(e); }
		} finally {
			try { zos.close(); } catch (Exception e) {}
			zos = null;
		}
	}
	
	/** @see #addFileToZip(File, File, File) */
	public static void writeToZip(String zip, String file, String baseDir) {
		if (zip == null || file == null) return;
		File z = new File(zip);
		File f = new File(file);
		File d = null;
		if (baseDir != null) {
			d = new File(baseDir);
		}
		writeToZip(z, f, d);
	}
	/**
	 * Add file to a zip file
	 * @param zip the zip file
	 * @param file the file to be added
	 * @param baseDir the base directory to start the zip file structure. 
	 * 	If null the root directory will be used instead
	 */
	public static void writeToZip(File zip, File file, File baseDir) {
		ZipOutputStream zos = null;
		try {
			zos = new ZipOutputStream(new FileOutputStream(zip, true));
			if (file.isFile()) {
				FileUtil.writeToZip(zos, file, baseDir);
			} else if (file.isDirectory()) {
				Map map = FileUtil.listAllFiles(file.getPath(), baseDir.getPath());
				Iterator iter = map.keySet().iterator();
				while (iter.hasNext()) {
					Object iterObj = iter.next();
					if (iterObj != null) {
						String key = (String)iterObj;
						Object o = map.get(key);
						if (o instanceof File) {
							File f = (File)o;
							if (f.isFile()) {
								FileUtil.writeToZip(zos, f, baseDir);
							}
						}
					}
				}
				
			}
		} catch (Exception e) {
			if (log.isErrorEnabled()) { log.error(e); }
		} finally {
			try { zos.close(); } catch (Exception e) {}
			zos = null;
		}
	}
	
	/**
	 * List all files in a map with respect to a reference directory
	 * @param dir the directory
	 * @param referenceDir the reference directory.if null is taken from the root
	 * @return map the result in a map the key is the relative path 
	 */
	public static Map listAllFiles(String dir, String referenceDir) {
		Map map = new HashMap();
		String s = dir;
		if (dir.indexOf("*") < 0) {
			StringBuffer sb = new StringBuffer();
			sb.append(s);
			sb.append(File.separatorChar);
			sb.append("*.*");
			s = sb.toString();
		}
		Collection c = FileUtil.listFiles(s);
		Iterator iter = c.iterator();
		File parent = new File(referenceDir);
		String relativePath = null;
		while (iter.hasNext()) {
			Object iterObj = iter.next();
			if (iterObj != null && iterObj instanceof File) {
				File f = (File)iterObj;
				if (parent == null) {
					relativePath = f.getAbsolutePath();
				} else {
					relativePath = FileUtil.getRelativePath(f, parent);
				}
				if (relativePath == null) {
if (log.isInfoEnabled()) { log.info("Invalid relative path! " + f + parent); }
					continue;
				}
				if (f.isFile()) {
					map.put(relativePath, f);
				} else if (f.isDirectory()) {
					Map m = FileUtil.listAllFiles(f.getPath(), referenceDir);
					if (m != null && m.size() > 0) {
						map.putAll(m);
					}
				}
			}
		}
		return map;
	}
	
	private static void writeToZip(ZipOutputStream zos, File file, File baseDir)  {
		BufferedInputStream bis = null;
		try {
			if (file == null || zos == null || file.isDirectory()) {
				return;
			}
			bis = new BufferedInputStream(new FileInputStream(file));
			StringBuffer sb = new StringBuffer();
			String path = null;
			if (baseDir == null) {
				sb.append(file.getPath());
			} else {
				sb.append(FileUtil.getRelativePath(file, baseDir, false));
				sb.deleteCharAt(0);
			}
			path = sb.toString();
		
			ZipEntry target = new ZipEntry(path);
if (log.isInfoEnabled()) { log.info("Zipping : " + path); }
			zos.putNextEntry(target);
			byte buf[] = new byte[1024];
			int count;
			while ((count = bis.read(buf, 0, 1024)) != -1) {
				zos.write(buf, 0, count);
			}
		} catch (Exception e) {
			if (log.isErrorEnabled()) { log.error(e); }
		} finally {
			try { bis.close(); } catch (Exception e) {}
			try { zos.closeEntry(); } catch (Exception e) {}
			bis = null;
		}
	}

	public static List getZipFileList(String file) {
		File f = new File(file);
		return getZipFileList(f);
	}
	
	public static List getZipFileList(File f) {
		List l = new ArrayList();
		try {
			BufferedOutputStream dest = null;
			FileInputStream fis = new FileInputStream(f);
			ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
			ZipEntry entry = null;
			StringBuffer sb = new StringBuffer();
			while((entry = zis.getNextEntry()) != null) {
				String name = entry.getName();
				l.add(name);
			}
			zis.close();
		} catch(Exception e) {
			if (log.isErrorEnabled()) { log.error(e); }
		}
		return l;
	}
	
	
	/**
	 * Unzip the file to the target destination
	 */
	public static final int SIZE = 512;
	public static void unzipFile(String src, String target) {
		try {
			BufferedOutputStream dest = null;
			FileInputStream fis = new FileInputStream(src);
			ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
			ZipEntry entry = null;
			StringBuffer sb = new StringBuffer();
			byte data[] = new byte[SIZE];
			while((entry = zis.getNextEntry()) != null) {
				int count;
				sb.setLength(0);
				sb.append(target).append(File.separator).append(entry.getName());
				if (log.isInfoEnabled()) { log.info("Unzipping to " + sb.toString()); }
				FileOutputStream fos = new FileOutputStream(sb.toString());
				dest = new BufferedOutputStream(fos, SIZE);
				while ((count = zis.read(data, 0, SIZE))!= -1) {
					dest.write(data, 0, count);
				}
				dest.flush();
				dest.close();
			}
			zis.close();
		} catch(Exception e) {
			if (log.isErrorEnabled()) { log.error(e); }
		}
	}
	
	public static byte[] readBytesFromInputStream (InputStream is) {
		byte[] buffer = new byte[BUFFER_SIZE];
		int sizeRead = -1;
		byte[] retVal = null;
		ArrayList a = new ArrayList();
		try {
			sizeRead = is.read(buffer);
			while (sizeRead > -1) {
				ByteData bd = new ByteData(buffer, sizeRead);
				a.add(bd);
				sizeRead = is.read(buffer);
			}
			retVal = ByteData.addByteData(a);
		} catch (Exception e) {
			if (log.isErrorEnabled()) {
				log.error("Error while reading from InputStream", e);
			}
		}
		return retVal;
	}
	
	/**
	 * Write an object into the xmlfile
	 * @param filename the name of the file to output to
	 * @param o the object to be serialized
	 */
	public static void writeToXmlFile(String filename, Object o) {
		if (o == null || filename == null) {
			return;
		}
		XMLEncoder encoder = null;
		try {
			encoder = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(filename)));
			encoder.writeObject(o);
		} catch (Exception e) {
			if (log.isErrorEnabled()) { log.error(e); }
		} finally {
			try { encoder.close(); } catch (Exception e) {}
		}
	}

	/**
	 * Reads an object from a xml file
	 * @param filename the filename to read from
	 * @return the object
	 */
	public static Object readFromXmlFile(String filename) {
		if (filename == null) {
			return null;
		}
		XMLDecoder decoder = null;
		Object o = null;
		try {
			decoder = new XMLDecoder(new BufferedInputStream(new FileInputStream(filename)));
			o = decoder.readObject();
		} catch (Exception e) {
		} finally {
			try { decoder.close(); } catch (Exception e) {}
		}
		return o;
	}
	
	public static void main(String args[]){
		ThreadUtil.startTimer();
		try {
			/*
			File f = new File("C:/Temp/haha.properties");
			System.out.println(f.getCanonicalPath());
			System.out.println(f.getAbsolutePath());
			System.out.println(f.getParentFile());
			System.out.println(f.getName());
			FileUtil.deleteFile("C:/Temp/test1.txt");
			//*/
			
			/*
			Collection c = null; 
			FileUtil.deleteFile("C:/Temp/xxx.txt");
			FileUtil.deleteFile("C:/Temp/zzz.txt");
			FileUtil.deleteFile("C:/Temp/x1.txt");
			StringBuffer sb = new StringBuffer();
			for (int i=0; i<1024*1024; i++) {
				sb.append(i%10);
				if (i % 100 == 0) {
					sb.append("\n");
				}
			}
			FileUtil.writeToFile("C:/Temp/xxx.txt", sb.toString(), false);

			c = FileUtil.readLinesFromFile("C:/temp/xxx.txt", true, 1, 10);
			System.out.println(c);
			
			System.out.println(FileUtil.exist("C:/Temp/haha.haha"));
			System.out.println(FileUtil.exist("C:/Temp/zzz.txt"));
			System.out.println(FileUtil.exist("C:/Temp"));
			
			System.out.println(FileUtil.isDirectory("C:/Temp"));
			System.out.println(FileUtil.isDirectory("C:/Temp/zzz.txt"));
			
			
			FileUtil.copyFile("C:/Temp/xxx.txt", "C:/Temp/zzz.txt");
			FileUtil.renameFile("C:/Temp/zzz.txt", "C:/Temp/x1.txt");
			//*/
			/*
			Collection c = null;
			c = FileUtil.listFiles("c:/temp/*.pdf");
			System.out.println(c);
			//*/
			
			/*
			FileUtil.downloadFromURL("file:/C:/temp/haha.txt", "C:/temp/hoho.txt");
			FileUtil.uploadToURL("file:/C:/temp/hehe.txt", "C:/temp/url.txt");
			//*/
			/*
			File f = new File("C:/Temp/9/hahaha.txt");
			System.out.println("Path: " + f.getPath());
			System.out.println("Name: " + f.getName());
			System.out.println("AbsolutePath: " + f.getAbsolutePath());
			System.out.println("AbsoluteFile: " + f.getAbsoluteFile());
			System.out.println("CanonicalPath: " + f.getCanonicalPath());
			System.out.println("Parent: " + f.getParent());
			//*/

			/*
			File f = new File("C:/Temp/9/1/file1.txt"); 
			File p = new File("C:/temp/9");
			String s = FileUtil.getRelativePath(f, p);
			System.out.println(s);//*/
			/*
			List l = FileUtil.listFilesAsList("C:/Temp/9/*.*", 2);
			System.out.println(l);
			FileUtil.sortFileList(l, true);
			System.out.println(l);
			
			l = FileUtil.listFilesAsList("C:/Temp/9/*.*", 1);
			System.out.println(l);
			FileUtil.sortFileList(l, true);
			System.out.println(l);
			
			l = FileUtil.listFilesAsList("C:/Temp/9/*.*", 0);
			System.out.println(l);
			FileUtil.sortFileList(l, true);
			System.out.println(l);//*/
			
			/*
			Map m = FileUtil.listAllFiles("C:/Temp/9", "C:/Temp/9");
			System.out.println(m);
			//*/
			/*
			FileUtil.deleteFile("C:/Temp/zzzhaha.zip");
			//FileUtil.deleteFile("C:/temp/zzz.zip");
			//FileUtil.writeToZip("C:/Temp/zzzhaha.zip", "C:/Temp/9", "C:/Temp/9");
			FileUtil.writeToZip("C:/Temp/zzz.zip", "C:/Temp/7/url.txt", "C:/Temp");
			//*/
			
			//FileUtil.unzipFile("C:/VRL/test.zip", "C:/VRL/appl");
			/*
			Map map = new HashMap();
			FileInputStream is = new FileInputStream("C:/Temp/1.properties");
			FileUtil.loadMap(is, map);
			is.close();
			System.out.println(map);//*/
			/*
			FileInputStream is = new FileInputStream("C:/Temp/1.txt");
			byte[] b = FileUtil.readBytesFromInputStream(is);
			is.close();
			FileOutputStream os = new FileOutputStream("C:/Temp/2.txt");
			os.write(b);
			os.close();//*/
			/*
			List l = FileUtil.listFiles("C:/MyProjects/latticesoft/vrlcrl/src/temp/*.log");
			for (int i=0; i<l.size(); i++) {
				System.out.println(l.get(i));
			}//*/
			
			//Map map = FileUtil.listAllFiles("C:/jrpapp1/wrk/jrpasit/jrpawar/WEB-INF/classes/*.*", "C:/jrpapp1/wrk/jrpasit/jrpawar/WEB-INF/classes");
			//System.out.println(map.size());
			
			File f = new File("C:/Temp/5/1.html");
			File fTgt = new File("C:/Temp/5");
			System.out.println(fTgt.isDirectory());
			File fDest = new File(fTgt.getPath() + "/" + f.getName());
			System.out.println(fDest);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ThreadUtil.stopTimer();
		}
		System.out.println(ThreadUtil.getTime() + " ms");
	}
}

