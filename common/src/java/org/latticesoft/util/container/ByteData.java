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
 * Created on Dec 7, 2006
 *
 */
package org.latticesoft.util.container;

import java.util.*;
import java.io.Serializable;

public class ByteData implements Serializable {

	public static final long serialVersionUID = 20061207100000L;
	private byte[] data;
	
	public ByteData(){}
	public ByteData(byte[] src, int size){
		this.add(src, size);
	}
	
	private int size;
	/** @return Returns the data. */
	public byte[] getData() { return (this.data); }
	/** @param data The data to set. */
	public void setData(byte[] data) { this.data = data; }
	/** @return Returns the size. */
	public int getSize() { return (this.size); }
	/** @param size The size to set. */
	public void setSize(int size) { this.size = size; }
	
	public boolean add(byte[] src, int size) {
		if (src == null) {
			return false;
		}
		this.size = size;
		this.data = new byte[size];
		System.arraycopy(src, 0, this.data, 0, size);
		return true;
	}
	
	public static byte[] addByteData(ByteData first, ByteData second) {
		byte[] retVal = null;
		if (first == null && second == null) {
			retVal = null;
		} else if (first != null && second == null) {
			retVal = new byte[first.getSize()];
			System.arraycopy(first.getData(), 0, retVal, 0, first.getSize());
		} else if (first == null && second != null) {
			retVal = new byte[second.getSize()];
			System.arraycopy(second.getData(), 0, retVal, 0, second.getSize());
		} else if (first != null && second != null) {
			retVal = new byte[first.getSize() + second.getSize()];
			System.arraycopy(first.getData(), 0, retVal, 0, first.getSize());
			System.arraycopy(second.getData(), 0, retVal, first.getSize(), second.getSize());
		}
		return retVal;
	}

	public static byte[] addByteData(List l) {
		if (l == null || l.size() == 0) {
			return null;
		}
		byte[] retVal = null;
		int totalSize = 0;
		int index = 0;
		for (int i=0; i<l.size(); i++) {
			Object o = l.get(i);
			if (o instanceof ByteData) {
				ByteData bd = (ByteData)o;
				totalSize += bd.getSize();
			}
		}
		if (totalSize == 0) {
			return null;
		}
		retVal = new byte[totalSize];
		for (int i=0; i<l.size(); i++) {
			Object o = l.get(i);
			if (o instanceof ByteData) {
				ByteData bd = (ByteData)o;
				System.arraycopy(bd.getData(), 0, retVal, index, bd.getSize());
				index += bd.getSize();
			}
		}
		return retVal;
	}
	
	public static void main(String[] args) {
		byte[] b1 = new byte[2];
		byte[] b2 = new byte[2];
		byte[] b3 = new byte[3];
		for (int i=0; i<b1.length; i++) {
			b1[i] = 1;
		}
		for (int i=0; i<b2.length; i++) {
			b2[i] = 2;
		}
		for (int i=0; i<b3.length; i++) {
			b3[i] = 3;
		}
		ByteData bd1 = new ByteData(b1, b1.length);
		ByteData bd2 = new ByteData(b2, b2.length);
		ByteData bd3 = new ByteData(b3, b3.length);
		
		byte[] res = ByteData.addByteData(bd1, bd2);
		for (int i=0; i<res.length; i++) {
			System.out.print(res[i]);
			System.out.print(", ");
		}
		System.out.println("");
		
		ArrayList a = new ArrayList();
		a.add(bd1);
		a.add(bd3);
		a.add(bd2);
		res = ByteData.addByteData(a);
		for (int i=0; i<res.length; i++) {
			System.out.print(res[i]);
			System.out.print(", ");
		}
		System.out.println("");
	}
}

