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

import java.io.IOException;

import com.siemens.ct.exi.core.NameContext;
import com.siemens.ct.exi.datatype.charset.XSDIntegerCharacterSet;
import com.siemens.ct.exi.datatype.strings.StringDecoder;
import com.siemens.ct.exi.datatype.strings.StringEncoder;
import com.siemens.ct.exi.io.channel.DecoderChannel;
import com.siemens.ct.exi.io.channel.EncoderChannel;
import com.siemens.ct.exi.types.BuiltInType;
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

public class NBitLongDatatype extends AbstractDatatype {
	protected final long lowerBound;
	protected final long upperBound;
	protected final int numberOfBits4Range;

	private int valueToEncode;
	
	public NBitLongDatatype(ExpandedName datatypeIdentifier,
			long lowerBound, long upperBound, int boundedRange) {
		super(BuiltInType.NBIT_LONG, datatypeIdentifier);
		this.rcs = new XSDIntegerCharacterSet();

		this.lowerBound = lowerBound;
		this.upperBound = upperBound;

		// calculate number of bits to represent range
		numberOfBits4Range = MethodsBag.getCodingLength(boundedRange);
	}

	public long getLowerBound() {
		return lowerBound;
	}

	public long getUpperBound() {
		return upperBound;
	}

	public int getNumberOfBits() {
		return numberOfBits4Range;
	}
	
	public boolean isValid(String value) {
		try {
			long lValueToEncode = Long.parseLong(value);

			// check lower & upper bound
			if (lValueToEncode >= lowerBound
					&& lValueToEncode <= upperBound) {
				// retrieve offset & update value
				lValueToEncode -= lowerBound;
				assert(lValueToEncode <= Integer.MAX_VALUE);
				valueToEncode = (int) lValueToEncode;
				return true;
			} else {
				return false;
			}
		} catch (NumberFormatException e) {
			return false;
		}
	}

	public void writeValue(EncoderChannel valueChannel, StringEncoder stringEncoder, NameContext context)
			throws IOException {
		valueChannel.encodeNBitUnsignedInteger(valueToEncode, numberOfBits4Range);
	}

	public char[] readValue(DecoderChannel valueChannel,
			StringDecoder stringDecoder, NameContext context)
			throws IOException {
		// decode value
		int decodedValue = valueChannel.decodeNBitUnsignedInteger(numberOfBits4Range);
		// add offset (lower bound)
		return MethodsBag.itos( decodedValue + lowerBound );
	}

}