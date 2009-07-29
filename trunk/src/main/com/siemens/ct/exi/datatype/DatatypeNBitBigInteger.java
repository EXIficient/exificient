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

import java.math.BigInteger;

import com.siemens.ct.exi.util.ExpandedName;
import com.siemens.ct.exi.util.MethodsBag;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.3.20081111
 */

public class DatatypeNBitBigInteger extends AbstractDatatype {
	protected final BigInteger lowerBound;
	protected final BigInteger upperBound;
	protected final int numberOfBits4Range;

	public DatatypeNBitBigInteger(ExpandedName datatypeIdentifier,
			BigInteger lowerBound, BigInteger upperBound, int boundedRange) {
		super(BuiltInType.NBIT_BIG_INTEGER, datatypeIdentifier);

		this.lowerBound = lowerBound;
		this.upperBound = upperBound;

		// calculate number of bits to represent range
		numberOfBits4Range = MethodsBag.getCodingLength(boundedRange);
	}

	public BigInteger getLowerBound() {
		return lowerBound;
	}

	public BigInteger getUpperBound() {
		return upperBound;
	}

	public int getNumberOfBits() {
		return numberOfBits4Range;
	}

}