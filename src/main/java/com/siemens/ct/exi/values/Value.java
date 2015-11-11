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

package com.siemens.ct.exi.values;

import java.io.Serializable;

/**
 * Value is a container concept encapsulating value items, e.g. String Values
 * but also Integers, Floats etc.
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9.5-SNAPSHOT
 */

public interface Value extends Serializable {

	/**
	 * Returns type of the boxed value e.g., int or boolean.
	 * <p>
	 * Note: can be a string-type even if integer-type is expected (e.g., if
	 * Preserve.LexicalValues is set to TRUE).
	 * </p>
	 * 
	 * @return associated value type
	 */
	public ValueType getValueType();

	
	/**
	 * Returns character array containing the values represented as String
	 * representation for XML.
	 * <p>Please consider using getCharacters(char[] cbuffer, int offset).</p>
	 * 
	 * @return string representation of value
	 */
	public char[] getCharacters();
	
	/**
	 * Fills character array with the value represented as String
	 * 
	 * @param cbuffer character buffer
	 * @param offset character buffer offset
	 */
	public void getCharacters(char[] cbuffer, int offset);
	
	/**
	 * Length of string representation
	 * 
	 * @return string length
	 */
	public int getCharactersLength();

	/**
	 * Returns string representation by making use of the passed character
	 * array.
	 * 
	 * @param cbuffer character buffer
	 * @param offset character buffer offset
	 * @return String representation
	 */
	public String toString(char[] cbuffer, int offset);

	/**
	 * Returns string representation. Please consider using toString(char[]
	 * cbuffer, int offset).
	 * 
	 * @see String
	 * @return String representation
	 */
	@Override
	public String toString();
	

}
