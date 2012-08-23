/*
 * Copyright (C) 2007-2012 Siemens AG
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
 * @version 0.9
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
