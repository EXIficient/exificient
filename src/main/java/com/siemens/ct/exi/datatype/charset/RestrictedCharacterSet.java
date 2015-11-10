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

import java.io.Serializable;

/**
 * If a string value is associated with a schema datatype and one or more of the
 * datatypes in its datatype hierarchy has one or more pattern facets, there may
 * be a restricted character set defined for the string value.
 * http://www.w3.org/TR/exi/#restrictedCharSet
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9.5-SNAPSHOT
 */

public interface RestrictedCharacterSet extends Serializable {
	/**
	 * Retrieves the code-point for given code.
	 * 
	 * @param code
	 * @return codePoint
	 * @throws IndexOutOfBoundsException
	 */

	public int getCodePoint(int code);

	/**
	 * Returns code for given code-point or <code>NOT_FOUND</code> == -1 for
	 * invalid char.
	 * 
	 * @param codePoint
	 *            character of interest
	 * @return code
	 */
	public int getCode(int codePoint);

	/**
	 * Returns the number of entries for the set.
	 * 
	 * @return number of entries
	 */
	public int size();

	/**
	 * Returns the number of bits to encode codes for the set.
	 * <p>
	 * codingLength = ceil( log2(N + 1) ) and N is the number of characters in
	 * the restricted character set.
	 * </p>
	 * 
	 * @see RestrictedCharacterSet#size
	 * @return number of entries
	 */
	public int getCodingLength();

}
