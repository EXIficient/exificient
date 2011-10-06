/*
 * Copyright (C) 2007-2011 Siemens AG
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
 * @version 0.8
 */

public enum BuiltInType {
	/* Binary */
	BINARY_BASE64, BINARY_HEX,
	/* Boolean */
	BOOLEAN, BOOLEAN_PATTERN,
	/* Decimal */
	DECIMAL,
	/* Float */
	FLOAT, DOUBLE,
	/* N-Bit Integer */
	NBIT_INTEGER_32, NBIT_INTEGER_64, NBIT_INTEGER_BIG,
	/* Unsigned Integer */
	UNSIGNED_INTEGER_16, UNSIGNED_INTEGER_32, UNSIGNED_INTEGER_64, UNSIGNED_INTEGER_BIG,
	/* (Signed) Integer */
	INTEGER_16, INTEGER_32, INTEGER_64, INTEGER_BIG,
	/* Datetime */
	DATETIME,
	/* String */
	STRING,
	/* Enumeration */
	ENUMERATION,
	/* List */
	LIST,
	/* Restricted Character Set */
	RESTRICTED_CHARACTER_SET,
	/* QName */
	QNAME;
}
