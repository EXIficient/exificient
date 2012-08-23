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

package com.siemens.ct.exi.values;

/**
 * Value types: e.g., String Values, Integers, Floats etc.
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9
 */

public enum ValueType {
	/* Binary */
	BINARY_BASE64, BINARY_HEX,
	/* Boolean */
	BOOLEAN,
	/* Decimal */
	DECIMAL,
	/* Float */
	FLOAT,
	/* Integer int, long, BigInteger */
	INTEGER_INT, INTEGER_LONG, INTEGER_BIG,
	/* Datetime */
	DATETIME,
	/* String */
	STRING,
	/* List */
	LIST,
	/* QName */
	QNAME;
}
