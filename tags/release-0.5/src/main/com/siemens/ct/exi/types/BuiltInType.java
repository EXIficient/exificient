/*
 * Copyright (C) 2007-2010 Siemens AG
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
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.4.20090421
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
	NBIT_INTEGER, NBIT_LONG, NBIT_BIG_INTEGER,
	/* Unsigned Integer */
	UNSIGNED_INTEGER, UNSIGNED_LONG, UNSIGNED_BIG_INTEGER,
	/* (Signed) Integer */
	INTEGER, LONG, BIG_INTEGER,
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