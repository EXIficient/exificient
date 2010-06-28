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
import com.siemens.ct.exi.values.LongValue;
import com.siemens.ct.exi.values.Value;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.4.20081111
 */

public class NBitLongDatatype extends AbstractDatatype {
	protected final long lowerBound;
	protected final long upperBound;
	protected final int numberOfBits4Range;

	protected LongValue validValue;

	public NBitLongDatatype(long lowerBound, long upperBound, QName schemaType) {
		super(BuiltInType.NBIT_LONG, schemaType);
		this.rcs = new XSDIntegerCharacterSet();

		this.lowerBound = lowerBound;
		this.upperBound = upperBound;

		// calculate number of bits to represent range
		numberOfBits4Range = MethodsBag.getCodingLength((int) (upperBound
				- lowerBound + 1));
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
		validValue = LongValue.parse(value);

		if (validValue == null) {
			return false;
		} else {
			return checkBounds();
		}
	}

	// check lower & upper bound
	protected boolean checkBounds() {
		long lValue = validValue.toLong();
		if (lValue >= lowerBound && lValue <= upperBound) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isValid(Value value) {
		if (value instanceof LongValue) {
			validValue = ((LongValue) value);
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
		valueChannel.encodeNBitUnsignedInteger(
				(int) (validValue.toLong() - lowerBound), numberOfBits4Range);
	}

	public Value readValue(DecoderChannel valueChannel,
			StringDecoder stringDecoder, QName context) throws IOException {
		int decodedValue = valueChannel
				.decodeNBitUnsignedInteger(numberOfBits4Range);
		LongValue lv;
		if (lowerBound == 0) {
			lv = new LongValue(decodedValue);
		} else {
			lv = new LongValue(decodedValue + lowerBound);
		}
		return lv;
	}

}