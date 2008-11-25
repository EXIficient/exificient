/*
 * Copyright (C) 2007, 2008 Siemens AG
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

import com.siemens.ct.exi.util.ExpandedName;
import com.siemens.ct.exi.util.MethodsBag;
import com.siemens.ct.exi.util.datatype.XSDInteger;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.2.20081111
 */

public class DatatypeNBitInteger extends AbstractDatatype {
	protected final XSDInteger lowerBound;
	protected final XSDInteger upperBound;
	protected final int numberOfBits4Range;

	public DatatypeNBitInteger(ExpandedName datatypeIdentifier,
			XSDInteger lowerBound, XSDInteger upperBound, int boundedRange) {
		super(BuiltInType.BUILTIN_NBIT_INTEGER, datatypeIdentifier);

		this.lowerBound = lowerBound;
		this.upperBound = upperBound;

		// calculate number of bits to represent range
		numberOfBits4Range = MethodsBag.getCodingLength(boundedRange);
	}

	public XSDInteger getLowerBound() {
		return lowerBound;
	}

	public XSDInteger getUpperBound() {
		return upperBound;
	}

	public int getNumberOfBits() {
		return numberOfBits4Range;
	}

}