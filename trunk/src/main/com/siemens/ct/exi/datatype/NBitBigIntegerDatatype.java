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
import java.math.BigInteger;

import javax.xml.namespace.QName;

import com.siemens.ct.exi.datatype.charset.XSDIntegerCharacterSet;
import com.siemens.ct.exi.datatype.strings.StringDecoder;
import com.siemens.ct.exi.datatype.strings.StringEncoder;
import com.siemens.ct.exi.io.channel.DecoderChannel;
import com.siemens.ct.exi.io.channel.EncoderChannel;
import com.siemens.ct.exi.types.BuiltInType;
import com.siemens.ct.exi.util.HugeInteger;
import com.siemens.ct.exi.util.MethodsBag;
import com.siemens.ct.exi.values.StringValue;
import com.siemens.ct.exi.values.Value;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.3.20081111
 */

public class NBitBigIntegerDatatype extends AbstractDatatype {
	protected final BigInteger lowerBound;
	protected final HugeInteger hiLowerBound;
	protected final BigInteger upperBound;
	protected final HugeInteger hiUpperBound;
	
	protected final int numberOfBits4Range;
	
	private int valueToEncode;

	public NBitBigIntegerDatatype(QName datatypeIdentifier,
			BigInteger lowerBound, BigInteger upperBound, int boundedRange) {
		super(BuiltInType.NBIT_BIG_INTEGER, datatypeIdentifier);
		this.rcs = new XSDIntegerCharacterSet();

		//	lower bound
		this.lowerBound = lowerBound;
		this.hiLowerBound = getHugeInteger(lowerBound);
		// upper bound
		this.upperBound = upperBound;
		this.hiUpperBound = getHugeInteger(upperBound);

		// calculate number of bits to represent range
		numberOfBits4Range = MethodsBag.getCodingLength(boundedRange);
	}
	
	protected static final HugeInteger getHugeInteger(BigInteger bi) {
		if (bi.bitLength() <= 63) {
			//	fits into long
			return new HugeInteger(bi.longValue());
		} else {
			//	need to use BigInteger
			return new HugeInteger(bi);
		}
	}

//	public BigInteger getLowerBound() {
//		return lowerBound;
//	}
//
//	public BigInteger getUpperBound() {
//		return upperBound;
//	}
//
//	public int getNumberOfBits() {
//		return numberOfBits4Range;
//	}
	
	public boolean isValid(String value) {
		try {
			BigInteger bValueToEncode = new BigInteger(value);

			// check lower & upper bound
			if (bValueToEncode.compareTo(lowerBound) >= 0 
					&& bValueToEncode.compareTo(upperBound) <= 0 ) {
				// retrieve offset & update value
				bValueToEncode = bValueToEncode.subtract(lowerBound);
				assert(bValueToEncode.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) <= 0);
				valueToEncode = bValueToEncode.intValue();
				return true;
			} else {
				return false;
			}
		} catch (NumberFormatException e) {
			return false;
		}
	}

	public void writeValue(EncoderChannel valueChannel, StringEncoder stringEncoder, QName context)
			throws IOException {
		valueChannel.encodeNBitUnsignedInteger(valueToEncode, numberOfBits4Range);
	}
	
	public Value readValue(DecoderChannel valueChannel,
			StringDecoder stringDecoder, QName context)
			throws IOException {
		// decode value
		int decodedValue = valueChannel.decodeNBitUnsignedInteger(numberOfBits4Range);
		// add offset (lower bound)
		if (hiLowerBound.isLongValue) {
			return new StringValue( MethodsBag.itos(hiLowerBound.longValue + decodedValue) );
		} else {
			//	not a very efficient way!!
			return new StringValue(lowerBound.add(BigInteger.valueOf(decodedValue)).toString().toCharArray());	
		}
	}

}