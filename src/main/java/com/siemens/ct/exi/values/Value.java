/*
 * Copyright (C) 2007-2015 Siemens AG
 *
 * This program and its interfaces are free software;
 * you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
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
	 * <p>Please consider using toCharacters(char[] cbuffer, int offset).</p>
	 * 
	 * @return string representation of value
	 */
	public char[] getCharacters();
	
	/**
	 * Fills character array with the value represented as String
	 * 
	 * @param cbuffer
	 * @param offset
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
	 * @param cbuffer
	 * @param offset
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
