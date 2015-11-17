/*
 * Copyright (c) 2007-2015 Siemens AG
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * 
 */

package com.siemens.ct.exi.datatype.charset;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.siemens.ct.exi.Constants;
import com.siemens.ct.exi.util.MethodsBag;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9.6-SNAPSHOT
 */

public abstract class AbstractRestrictedCharacterSet implements
		RestrictedCharacterSet {
	// #x9, HT (horizontal tab)
	// #xA, LF (line-feed)
	// #xD, CR (carriage-return)
	// #x20, SP (space)

	private static final long serialVersionUID = 1487974340218946481L;

	protected Map<Integer, Integer> codeSet; // codePoint --> internal code
	protected List<Integer> codePointList; // internal code --> codePoint

	protected int size;
	protected int codingLength;

	/*
	 * The characters in the restricted character set are sorted by UCS [ISO/IEC
	 * 10646] code point and represented by integer values in the range (0 ...
	 * N-1) according to their ordinal position in the set. Characters that are
	 * not in this set are represented by the integer N followed by the UCS code
	 * point of the character represented as an Unsigned Integer.
	 */

	protected AbstractRestrictedCharacterSet() {
		codeSet = new HashMap<Integer, Integer>();
		codePointList = new ArrayList<Integer>();
	}

	public int getCodePoint(int code) {
		return codePointList.get(code);
	}

	public int getCode(int codePoint) {
		Integer code = codeSet.get(codePoint);

		return (code == null ? Constants.NOT_FOUND : code);
	}

	public int size() {
		return size;
	}

	public int getCodingLength() {
		return codingLength;
	}

	protected void addValue(int codePoint) {
		codeSet.put(codePoint, codeSet.size());
		codePointList.add(codePoint);

		// adjust size / codingLength
		assert (codeSet.size() == codePointList.size());
		size = codeSet.size();
		codingLength = MethodsBag.getCodingLength(size + 1);
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof AbstractRestrictedCharacterSet) {
			AbstractRestrictedCharacterSet other = (AbstractRestrictedCharacterSet) o;
			if (this.size() == other.size()) {
				for (int code = 0; code < this.size(); code++) {
					if (this.getCodePoint(code) != other.getCodePoint(code)) {
						return false;
					}
				}
				return true;
			}
		}
		return false;
	}
	
	
	@Override
	public int hashCode() {
		return codeSet.hashCode();
	}
	
	@Override
	public String toString() {
		return codePointList.toString();
	}

}
