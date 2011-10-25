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
	
	/** Float & Double */
	FLOAT,
	
	/** N-Bit UnsignedInteger */
	NBIT_UNSIGNED_INTEGER,
	
	/** Unsigned Integer */
	UNSIGNED_INTEGER,
	
	/** (Signed) Integer */
	INTEGER,
	
	/** Datetime */
	DATETIME,
	
	/** String & RestrictedCharSet */
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
