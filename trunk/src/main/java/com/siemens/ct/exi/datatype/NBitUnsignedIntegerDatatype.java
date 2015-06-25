/*
 * Copyright (C) 2007-2015 Siemens AG
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

import com.siemens.ct.exi.context.QNameContext;
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
 * @version 0.9.5-SNAPSHOT
 */

public class NBitUnsignedIntegerDatatype extends AbstractDatatype {

	private static final long serialVersionUID = -7109188105049008275L;

	protected IntegerValue validValue;

	protected final IntegerValue lowerBound;
	protected final IntegerValue upperBound;
	protected final int numberOfBits4Range;

	public NBitUnsignedIntegerDatatype(IntegerValue lowerBound,
			IntegerValue upperBound, QNameContext schemaType) {
		super(BuiltInType.NBIT_UNSIGNED_INTEGER, schemaType);

		// assert (upperBound >= lowerBound);
		assert (upperBound.compareTo(lowerBound) >= 0);
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;

		// calculate number of bits to represent range
		IntegerValue diff = upperBound.subtract(lowerBound);
		numberOfBits4Range = MethodsBag.getCodingLength(diff.intValue() + 1);
	}
	
	public DatatypeID getDatatypeID() {
		return DatatypeID.exi_integer;
	}

	public IntegerValue getLowerBound() {
		return lowerBound;
	}

	public IntegerValue getUpperBound() {
		return upperBound;
	}

	public int getNumberOfBits() {
		return numberOfBits4Range;
	}

	protected boolean isValidString(String value) {
		validValue = IntegerValue.parse(value);

		if (validValue == null) {
			return false;
		} else {
			return checkBounds();
		}
	}

	public boolean isValid(Value value) {
		if (value instanceof IntegerValue) {
			validValue = ((IntegerValue) value);
			return checkBounds();
		} else {
			return isValidString(value.toString());
		}
	}

	// check lower & upper bound
	protected boolean checkBounds() {
		return (validValue.compareTo(lowerBound) >= 0 && validValue
				.compareTo(upperBound) <= 0);
	}

	public void writeValue(QNameContext qnContext, EncoderChannel valueChannel,
			StringEncoder stringEncoder) throws IOException {
		IntegerValue iv = validValue.subtract(lowerBound);
		valueChannel.encodeNBitUnsignedInteger(iv.intValue(),
				numberOfBits4Range);
	}

	public Value readValue(QNameContext qnContext, DecoderChannel valueChannel,
			StringDecoder stringDecoder) throws IOException {
		IntegerValue iv = valueChannel
				.decodeNBitUnsignedIntegerValue(numberOfBits4Range);
		return iv.add(lowerBound);
	}
	
	@Override
	public boolean equals(Object o) {
		if(super.equals(o) && o instanceof NBitUnsignedIntegerDatatype ) {
			NBitUnsignedIntegerDatatype nb = (NBitUnsignedIntegerDatatype) o;
			return (this.lowerBound.equals(nb.lowerBound) && this.upperBound.equals(nb.upperBound));
		}
		return false;
	}

}