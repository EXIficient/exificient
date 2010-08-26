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

package com.siemens.ct.exi.datatype;

import java.io.IOException;

import javax.xml.namespace.QName;

import com.siemens.ct.exi.datatype.charset.XSDIntegerCharacterSet;
import com.siemens.ct.exi.datatype.strings.StringDecoder;
import com.siemens.ct.exi.datatype.strings.StringEncoder;
import com.siemens.ct.exi.io.channel.DecoderChannel;
import com.siemens.ct.exi.io.channel.EncoderChannel;
import com.siemens.ct.exi.types.BuiltInType;
import com.siemens.ct.exi.util.MethodsBag;
import com.siemens.ct.exi.values.IntegerValue;
import com.siemens.ct.exi.values.Value;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.5
 */

public class NBitIntegerDatatype extends AbstractDatatype {

	private static final long serialVersionUID = -7109188105049008275L;

	protected IntegerValue validValue;

	protected final int lowerBound;
	protected final int upperBound;
	protected final int numberOfBits4Range;

	public NBitIntegerDatatype(int lowerBound, int upperBound, QName schemaType) {
		super(BuiltInType.NBIT_INTEGER_32, schemaType);
		this.rcs = new XSDIntegerCharacterSet();

		this.lowerBound = lowerBound;
		this.upperBound = upperBound;

		// calculate number of bits to represent range
		assert (upperBound >= lowerBound);
		numberOfBits4Range = MethodsBag.getCodingLength(upperBound - lowerBound
				+ 1);
	}

	public int getLowerBound() {
		return lowerBound;
	}

	public int getUpperBound() {
		return upperBound;
	}

	public int getNumberOfBits() {
		return numberOfBits4Range;
	}

	public boolean isValid(String value) {
		validValue = IntegerValue.parse(value);

		if (validValue == null) {
			return false;
		} else {
			return checkBounds();
		}
	}

	// check lower & upper bound
	protected boolean checkBounds() {
		int iValidValue = validValue.toInteger();
		if (iValidValue >= lowerBound && iValidValue <= upperBound) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isValid(Value value) {
		if (value instanceof IntegerValue) {
			validValue = ((IntegerValue) value);
			return checkBounds();
		} else {
			return false;
		}
	}

	public Value getValue() {
		return validValue;
	}

	public void writeValue(EncoderChannel valueChannel,
			StringEncoder stringEncoder, QName context) throws IOException {
		valueChannel.encodeNBitUnsignedInteger(validValue.toInteger()
				- lowerBound, numberOfBits4Range);
	}

	public Value readValue(DecoderChannel valueChannel,
			StringDecoder stringDecoder, QName context) throws IOException {
		IntegerValue iv = valueChannel
				.decodeNBitUnsignedIntegerValue(numberOfBits4Range);
		if (lowerBound != 0) {
			iv = new IntegerValue(iv.toInteger() + lowerBound);
		}
		return iv;
	}

}