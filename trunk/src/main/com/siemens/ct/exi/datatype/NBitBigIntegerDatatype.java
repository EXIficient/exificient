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
import com.siemens.ct.exi.util.MethodsBag;
import com.siemens.ct.exi.values.HugeIntegerValue;
import com.siemens.ct.exi.values.Value;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.6
 */

public class NBitBigIntegerDatatype extends AbstractDatatype {

	private static final long serialVersionUID = -2502808105531063938L;
	
	protected final HugeIntegerValue hiLowerBound;
	protected final HugeIntegerValue hiUpperBound;

	protected final int numberOfBits4Range;

	protected HugeIntegerValue lastValidValue;

	public NBitBigIntegerDatatype(BigInteger lowerBound, BigInteger upperBound, QName schemaType) {
		super(BuiltInType.NBIT_INTEGER_BIG, schemaType);
		this.rcs = new XSDIntegerCharacterSet();

		// lower bound
		this.hiLowerBound = HugeIntegerValue.parse(lowerBound);
		// upper bound
		this.hiUpperBound = HugeIntegerValue.parse(upperBound);

		// calculate number of bits to represent range
		numberOfBits4Range = MethodsBag.getCodingLength(upperBound.subtract(
				lowerBound).add(BigInteger.ONE).intValue());
	}

	public int getNumberOfBits() {
		return numberOfBits4Range;
	}

	public HugeIntegerValue getLowerBound() {
		return hiLowerBound;
	}

	public boolean isValid(String value) {
		try {
			lastValidValue = HugeIntegerValue.parse(value);

			if (lastValidValue == null) {
				return false;
			} else {
				return checkBounds();
			}
		} catch (NumberFormatException e) {
			return false;
		}
	}

	// check lower & upper bound
	protected boolean checkBounds() {
		if (lastValidValue.compareTo(hiLowerBound) >= 0
				&& lastValidValue.compareTo(hiUpperBound) <= 0) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isValid(Value value) {
		if (value instanceof HugeIntegerValue) {
			lastValidValue = ((HugeIntegerValue) value);
			return checkBounds();
		} else if (isValid(value.toString())) {
			return true;
		} else {
			return false;
		}
	}

//	public Value getValue() {
//		return lastValidValue;
//	}

	public void writeValue(EncoderChannel valueChannel,
			StringEncoder stringEncoder, QName context) throws IOException {
		valueChannel.encodeNBitUnsignedInteger(lastValidValue.subtract(
				hiLowerBound).toInteger(), numberOfBits4Range);
	}

	public Value readValue(DecoderChannel valueChannel,
			StringDecoder stringDecoder, QName context) throws IOException {
		int decodedValue = valueChannel
				.decodeNBitUnsignedInteger(numberOfBits4Range);

		HugeIntegerValue hv;
		if (hiLowerBound.isLongValue) {
			hv = new HugeIntegerValue(decodedValue + hiLowerBound.longValue);
		} else {
			// TODO look for an efficient way!!
			hv = hiLowerBound.add(new HugeIntegerValue(decodedValue));
		}
		return hv;
	}

}