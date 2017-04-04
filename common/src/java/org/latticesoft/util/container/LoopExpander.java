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
 * Created on Feb 3, 2006
 *
 */
package org.latticesoft.util.container;


import org.latticesoft.command.*;
import org.latticesoft.util.common.*;
import java.util.*;
import java.math.*;

/**
 * This class helps to expand a nested loop into a single loop with
 * an option to limit the total number. The resultant is a collection
 * of strings with the index replace by the LoopExpander. Index can be
 * in 3 formats: numerals, uppercase and lowercase alphabets.
 * The first loop param added is the innermost loop. Outer loop are
 * added later.
 * E.g.<code>
 * LoopExpander lp = new LoopExpander(); // main object
 * lp.setFormat("${indexOuter}-${indexInner}");
 * lp.setStop(-1); // no limit
 * lp.addParam("${indexInner}", "00" 1, 3);
 * lp.addParam("${indexOuter}", "0" 1, 2);
 * Iterator iter = lp.flatten().iterator();
 * while (iter.hasNext()) { System.out.println(iter.next()); }
 * </code> 
 * The resultant value is a collection of strings
 * <code>
 * 1-01
 * 1-02
 * 1-03
 * 2-01
 * 2-02
 * 2-03
 * </code>
 * To change to a descending sequence, simply change the start and stop
 * value accordingly. Note that for alpabets, the index starts from 0 not 1.
 * 
 */
public class LoopExpander implements List, Command, Identity {

	// TODO: Cater for variable enumerations. Current only number and alphabet
	
	private ArrayList childs = new ArrayList();
	public static final String ALPHABET_LOWER_CASE = "alphaLowerCase";
	public static final String ALPHABET_UPPER_CASE = "alphaUpperCase";
	public static final String NUMERIC = "numeric";
	public static final String CUSTOM = "custom";
	
	public static final String[] LOOKUP_LOWER = {
		"a", "b", "c", "d", "e",
		"f", "g", "h", "i", "j",
		"k", "l", "m", "n", "o",
		"p", "q", "r", "s", "t",
		"u", "v", "w", "x", "y", "z"
	};
	public static final String[] LOOKUP_UPPER = {
		"A", "B", "C", "D", "E",
		"F", "G", "H", "I", "J",
		"K", "L", "M", "N", "O",
		"P", "Q", "R", "S", "T",
		"U", "V", "W", "X", "Y", "Z"
	};
	
	private String[] customLookup = null;
	private String name;
	private String id;
	private int start = -1;
	private int stop = -1;
	private String format;
	private int currentIndex;
	
	public LoopExpander(){}
	
	public LoopExpander(String name, String format, int start, int stop){
		this.setName(name);
		this.setFormat(format);
		this.setStart(start);
		this.setStop(stop);
	}

	/** @return Returns the id. */
	public String getId() { return (this.id); }
	/** @param id The id to set. */
	public void setId(String id) { this.id = id; }
	/** @return Returns the name. */
	public String getName() { return (this.name); }
	/** @param name The name to set. */
	public void setName(String name) { this.name = name; }

	/** @return Returns the format. */
	public String getFormat() { return (this.format); }
	/** @param format The format to set. */
	public void setFormat(String format) { this.format = format; }
	
	/** @return Returns the start. */
	public int getStart() { return (this.start); }
	/** @param start The start to set. */
	public void setStart(int start) {
		this.start = start;
		this.currentIndex = start;
	}
	
	/** @return Returns the stop. */
	public int getStop() { return (this.stop); }
	/** @param stop The stop to set. */
	public void setStop(int stop) { this.stop = stop; }
	
	/** @return Returns the currentIndex. */
	public int getCurrentIndex() { return (this.currentIndex); }
	/** @param currentIndex The currentIndex to set. */
	public void setCurrentIndex(int currentIndex) { this.currentIndex = currentIndex; }

	/** @return Returns the customLookup. */
	public String[] getCustomLookup() { return (this.customLookup); }
	/** @param customLookup The customLookup to set. */
	public void setCustomLookup(String[] customLookup) { this.customLookup = customLookup; }

	public boolean next() {
		if (start > stop && stop > 0) {
			return this.decrement();
		} else {
			return this.increment();
		}
	}
	
	/**
	 * Increment the current index.
	 * @return true if reset is done 
	 */
	public boolean increment() {
		this.currentIndex++;
		boolean retVal = false;
		if (this.currentIndex > this.stop && this.stop != -1) {
			this.currentIndex = start; // reset
			retVal = true;
		}
		return retVal;
	}
	/**
	 * Decrement the current index.
	 * @return true if reset is done 
	 */
	public boolean decrement() {
		this.currentIndex--;
		boolean retVal = false;
		if (this.currentIndex < this.stop && this.stop != -1) {
			this.currentIndex = start; // reset
			retVal = true;
		}
		return retVal;
	}
	
	/**
	 * Returns the value
	 * @param s the input string
	 */
	protected String getValue(String s) {
		StringBuffer sb = new StringBuffer();
		sb.append(s);
		this.getValue(sb);
		return sb.toString();
	}
	/**
	 * Returns the value
	 * @param s the input string
	 */
	protected void getValue(StringBuffer sb) {
		if (this.format != null && this.name != null) {
			String fmt = null; 
			if (format.equals(LoopExpander.ALPHABET_UPPER_CASE)) {
				int index = this.currentIndex % LOOKUP_UPPER.length;
				fmt = LOOKUP_UPPER[index];
			} else if (format.equals(LoopExpander.ALPHABET_LOWER_CASE)) {
				int index = this.currentIndex % LOOKUP_LOWER.length;
				fmt = LOOKUP_LOWER[index];
			} else if (format.equals(LoopExpander.NUMERIC)){
				fmt = NumeralUtil.formatNumber(format, currentIndex);
			} else if (format.equals(LoopExpander.CUSTOM)){
				if (this.customLookup != null) {
					int index = this.currentIndex % this.customLookup.length;
					fmt = customLookup[index];
				} else {
					fmt = "";
				}
			} else {
				fmt = NumeralUtil.formatNumber(format, currentIndex);
			}
			StringUtil.replace(sb, this.name, fmt, true);
		}
	}
	
	public List flatten(String format, int limit) {
		this.setStop(limit);
		this.setFormat(format);
		return this.flatten();
	}
	public List flatten(int limit) {
		this.setStop(limit);
		return this.flatten();
	}
	public List flatten(List l) {
		if (l != null) {
			return this.flatten(l.size());
		}
		return this.flatten();
	}
	public List flatten(String format) {
		this.setFormat(format);
		return this.flatten();
	}
	public List flatten() {
		return LoopExpander.flattenLoop(format, this, stop);
	}

	/** @see #flattenLoop(String, Collection, int) */
	public static List flattenLoop(String input, List loopParams) {
		return flattenLoop(input, loopParams, -1);
	}

	/**
	 * Expand the param string into the single loop ArrayList
	 * @param input the string containing the format
	 * @param loopParams the collection of loop params. Outermost loops param are added first
	 * @param limit limit the generation once this limit is reach no more would be generated
	 * @return the flattened loop params
	 */
	public static List flattenLoop(String input, List loopParams, int limit) {
		ArrayList retVal = new ArrayList();
		ArrayList a = null;
		boolean exit = false;
		if (loopParams.size() == 0) {
			return retVal;
		}
		if (loopParams instanceof ArrayList) {
			a = (ArrayList)loopParams;
		} else {
			a = new ArrayList();
			a.addAll(loopParams);
		}
		StringBuffer sb = new StringBuffer();
		do {
			sb.setLength(0);
			sb.append(input);
			boolean incrementNextParam = true;
			for (int i=0; i<a.size(); i++) {
				if (limit > 0 && retVal.size() >= limit) {
					exit = true;
					sb.setLength(0);
					continue;
				}
				LoopExpander param = (LoopExpander)a.get(i);
				param.getValue(sb);
				if (incrementNextParam) {
					incrementNextParam = param.next();
				}
				if (i == (a.size()-1) && incrementNextParam) {
					// final loop param reached
					exit = true;
				}
				// once the maximum number is reached we stop generation
			}
			if (sb.length() > 0) {
				retVal.add(sb.toString());
			}
		} while (!exit);
		return retVal;
	}
	
	public static LoopExpander parse(String s) {
		if (s == null) { return null; }
		String[] ss = StringUtil.tokenizeIntoStringArray(s, "|");
		if (ss == null || ss.length < 4) {
			ss = StringUtil.tokenizeIntoStringArray(s, ";");
		}
		if (ss == null || ss.length < 4) {
			return null;
		}
		LoopExpander param = new LoopExpander();
		param.setName(ss[0]);
		param.setFormat(ss[1]);
		param.setStart(NumeralUtil.parseInt(ss[2]));
		param.setStop(NumeralUtil.parseInt(ss[3]));
		return param;
	}
	
	/**Add a loop param */
	public boolean addObject(String name, String format, int start, int stop) {
		if (name == null || format == null) {
			return false;
		}
		LoopExpander p = new LoopExpander(name, format, start, stop);
		p.setCustomLookup(this.getCustomLookup());
		return this.add(p);
	}

	/**Add a loop param */
	public boolean addObject(String[] s) {
		if (s == null || s.length < 4) {
			return false;
		}
		String name = s[0];
		String format = s[1];
		int start = NumeralUtil.parseInt(s[2]);
		int stop = NumeralUtil.parseInt(s[3]);
		return this.addObject(name, format, start, stop);
	}
	
	/**Add a loop param */
	public boolean addObject(LoopExpander p) {
		if (p != null) {
			p.setCustomLookup(this.getCustomLookup());
			return childs.add(p);
		}
		return false;
	}

	/**Add a loop param */
	public boolean addObject(String s) {
		if (s != null) {
			LoopExpander p = LoopExpander.parse(s);
			p.setCustomLookup(this.getCustomLookup());
			return childs.add(p);
		}
		return false;
	}
	
	public boolean add(Object o) {
		if (o instanceof LoopExpander) {
			LoopExpander p = (LoopExpander)o;
			p.setCustomLookup(this.getCustomLookup());
			return this.addObject(p);
		}
		if (o instanceof String) {
			String s = (String)o;
			return this.addObject(s);
		}
		if (o instanceof String[]) {
			String[] s = (String[])o;
			return this.addObject(s);
		}
		return false;
	}

	public boolean addAll(Collection c) {
		Iterator iter = c.iterator();
		int cnt = 0;
		while (iter.hasNext()) {
			Object iterObj = iter.next();
			if (iterObj != null && iterObj instanceof LoopExpander) {
				this.childs.add(iterObj);
				cnt++;
			}
		}
		return (cnt > 0);
	}
	public void clear() { this.childs.clear(); }
	public boolean contains(Object o) { return childs.contains(o); }
	public boolean containsAll(Collection c) { return childs.containsAll(c); }
	public boolean isEmpty() { return childs.isEmpty(); }
	public Iterator iterator() { return this.childs.iterator(); }
	public boolean remove(Object o) { return this.childs.remove(o); }
	public boolean removeAll(Collection c) { return this.childs.removeAll(c); }
	public boolean retainAll(Collection c) { return childs.retainAll(c); }
	public int size() { return childs.size(); }
	public Object[] toArray() { return childs.toArray(); }
	public Object[] toArray(Object[] o) { return childs.toArray(o); }
	public void add(int index, Object o) { childs.add(index, o); }
	public boolean addAll(int index, Collection c) { return childs.addAll(index, c); }
	public Object get(int index) { return childs.get(index); }
	public int indexOf(Object o) { return childs.indexOf(o); }
	public int lastIndexOf(Object o) { return childs.lastIndexOf(o); }
	public ListIterator listIterator() { return childs.listIterator(); }
	public ListIterator listIterator(int index) { return childs.listIterator(index); }
	public Object remove(int index) { return childs.remove(index); }
	public Object set(int index, Object o) { return childs.set(index, o); }
	public List subList(int fromIndex, int toIndex) { return childs.subList(fromIndex, toIndex); }

	/**
	 * Flattens the collection.
	 * When the object passed in is a collection/list, it will set the
	 * limit to the size of the Collection.
	 * When the object passed in is a String or numeral, the will
	 * set the limit to this size
	 * 
	 * @return collection generated during flatten
	 */
	public Object execute(Object o) throws CommandException {
		if (o != null && o instanceof Collection) {
			Collection c = (Collection)o;
			return this.flatten(c.size());
		} else if (o != null && 
				(o instanceof Integer || o instanceof String || o instanceof Byte ||
				o instanceof Short || o instanceof Long || o instanceof BigDecimal)) {
			int i = NumeralUtil.parseInt(o);
			return this.flatten(i);
		} else {
			return this.flatten();
		}
	}
	
	/**
	 * Converts the class in a string form
	 * @returns the class in a string form.
	 */
	public String toString() {
		return StringUtil.formatObjectToString(this);
	}

	public static void main(String[] args) {
		
		String[] custom = {"AAAA", "BBBB", "CCCC", "DDDD", "EEEE", "FFFF"};
		
		LoopExpander lp = new LoopExpander();
		lp.setStop(-1);
		lp.setCustomLookup(custom);
		lp.setFormat("#{l}-#{k}-#{j}-#{i}");
		lp.addObject("#{i}", "000", 1, 4);
		//lp.addParam(#{j}", "000", 1, 4);
		//lp.addParam("#{k}", LoopExpander.ALPHABET_UPPER_CASE, 0, 2);
		//lp.addParam("#{l}", LoopExpander.ALPHABET_LOWER_CASE, 0, 2);
		ArrayList a = (ArrayList)lp.flatten();
		for (int i=0; i<a.size(); i++) {
			System.out.println(a.get(i));
		}
		System.out.println("Size: " + a.size());
		
		System.out.println("======================");
		a = null;
		lp.clear();
		lp.setFormat("#{l}-#{k}-#{j}-#{i}");
		lp.setStop(-1);
		lp.addObject("#{i}", "000", 1, 5);
		lp.addObject("#{j}", "000", 3, 1);
		lp.addObject("#{k}", LoopExpander.ALPHABET_UPPER_CASE, 5, 0);
		lp.addObject("#{l}", LoopExpander.CUSTOM, 0, 3);
		a = (ArrayList)lp.flatten();
		for (int i=1; i<=a.size(); i++) {
			System.out.print(a.get(i-1));
			System.out.print("   ");
			if (i > 0 && i % 5 == 0) {
				System.out.println("");
			}
		}
		//*/
		System.out.println("");
		System.out.println("Size: " + a.size());
		System.out.println("======================");
		int limit = 27;
		System.out.println("Limit to " + limit);
		lp.setStop(limit);
		a = (ArrayList)lp.flatten();
		for (int i=1; i<=a.size(); i++) {
			System.out.print(a.get(i-1));
			System.out.print("   ");
			if (i > 0 && i % 5 == 0) {
				System.out.println("");
			}
		}
		System.out.println("");
		System.out.println("Size: " + a.size());
	}

}

