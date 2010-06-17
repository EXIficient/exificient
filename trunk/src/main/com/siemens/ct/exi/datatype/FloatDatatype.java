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

import com.siemens.ct.exi.datatype.charset.XSDDoubleCharacterSet;
import com.siemens.ct.exi.datatype.strings.StringDecoder;
import com.siemens.ct.exi.datatype.strings.StringEncoder;
import com.siemens.ct.exi.io.channel.DecoderChannel;
import com.siemens.ct.exi.io.channel.EncoderChannel;
import com.siemens.ct.exi.types.BuiltInType;
import com.siemens.ct.exi.values.FloatValue;
import com.siemens.ct.exi.values.Value;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.4.20080718
 */

public class FloatDatatype extends AbstractDatatype {

	protected FloatValue lastValidFloat;

	public FloatDatatype(BuiltInType builtInType, QName schemaType) {
		super(builtInType, schemaType);
		assert (builtInType == BuiltInType.FLOAT || builtInType == BuiltInType.DOUBLE);
		this.rcs = new XSDDoubleCharacterSet();
	}

	public boolean isValid(String value) {
		lastValidFloat = FloatValue.parse(value);
		return (lastValidFloat != null);
	}

	public boolean isValid(Value value) {
		if (value instanceof FloatValue) {
			lastValidFloat = ((FloatValue) value);
			return true;
		} else {
			return false;
		}
	}

	public Value getValue() {
		// return new DoubleValue(mantissa, exponent);
		return lastValidFloat;
	}

	public void writeValue(EncoderChannel valueChannel,
			StringEncoder stringEncoder, QName context) throws IOException {
		valueChannel.encodeFloat(lastValidFloat.mantissa,
				lastValidFloat.exponent);
	}

	public Value readValue(DecoderChannel valueChannel,
			StringDecoder stringDecoder, QName context) throws IOException {
		return valueChannel.decodeFloatValue();
	}
}