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

package com.siemens.ct.exi.types;

/**
 * EXI has a list built-in EXI datatype representations used for representing
 * content items in EXI stream (see
 * http://www.w3.org/TR/exi/#encodingDatatypes).
 * 
 * <p>
 * Additionally some built-in types are augmented with information necessary for
 * machine processing (e.g., number of bits).
 * <p>
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9.5-SNAPSHOT
 */

public enum BuiltInType {
	/** Binary Base64 */
	BINARY_BASE64,

	/** Binary Hex */
	BINARY_HEX,

	/** Boolean */
	BOOLEAN,

	/** Boolean Facet */
	BOOLEAN_FACET,

	/** Decimal */
	DECIMAL,

	/** Float &amp; Double */
	FLOAT,

	/** N-Bit UnsignedInteger */
	NBIT_UNSIGNED_INTEGER,

	/** Unsigned Integer */
	UNSIGNED_INTEGER,

	/** (Signed) Integer */
	INTEGER,

	/** Datetime */
	DATETIME,

	/** String &amp; RestrictedCharSet */
	STRING,

	/** Restricted character set string */
	RCS_STRING,

	/** Enumeration */
	ENUMERATION,

	/** List */
	LIST,

	/** QName */
	QNAME;
}
