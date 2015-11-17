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
 * EXI can deal with arbitrary large integers have values. This integer-type
 * informs about the type declared in the schema.
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9.6-SNAPSHOT
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
