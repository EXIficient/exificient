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

package com.siemens.ct.exi.types;

/**
 * EXI can deal with arbitrary large integers have values. This integer-type
 * informs about the type declared in the schema.
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9.2-SNAPSHOT
 */

public enum IntegerType {
	/** Unsigned Integer with at most 8 bits */
	UNSIGNED_INTEGER_8(true),

	/** Unsigned Integer with at most 16 bits */
	UNSIGNED_INTEGER_16(true),

	/** Unsigned Integer with at most 32 bits */
	UNSIGNED_INTEGER_32(true),

	/** Unsigned Integer with at most 64 bits */
	UNSIGNED_INTEGER_64(true),

	/** Unsigned Integer with arbitrary number of bits */
	UNSIGNED_INTEGER_BIG(true),

	/** Integer with at most 8 bits */
	INTEGER_8(false),

	/** Integer with at most 16 bits */
	INTEGER_16(false),

	/** Integer with at most 32 bits */
	INTEGER_32(false),

	/** Integer with at most 64 bits */
	INTEGER_64(false),

	/** Integer with arbitrary number of bits */
	INTEGER_BIG(false);

	private final boolean isUnsigned;

	private IntegerType(boolean isUnsigned) {
		this.isUnsigned = isUnsigned;
	}

	public boolean isUnsigned() {
		return isUnsigned;
	}
}
