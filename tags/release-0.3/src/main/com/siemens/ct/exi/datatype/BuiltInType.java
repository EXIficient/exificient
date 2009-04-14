/*
 * Copyright (C) 2007-2009 Siemens AG
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

package com.siemens.ct.exi.datatype;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.3.20081110
 */

public enum BuiltInType {
	BUILTIN_BINARY_BASE64, BUILTIN_BINARY_HEX, BUILTIN_BOOLEAN, BUILTIN_BOOLEAN_PATTERN, BUILTIN_DECIMAL, BUILTIN_FLOAT, BUILTIN_NBIT_INTEGER, BUILTIN_UNSIGNED_INTEGER, BUILTIN_INTEGER, BUILTIN_QNAME,
	/* Datetime */
	BUILTIN_DATETIME,
	/* String */
	BUILTIN_STRING,
	/* Enumeration */
	BUILTIN_ENUMERATION,
	/* List */
	BUILTIN_LIST;
}
