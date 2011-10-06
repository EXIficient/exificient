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

import javax.xml.namespace.QName;

import com.siemens.ct.exi.datatype.charset.XSDIntegerCharacterSet;
import com.siemens.ct.exi.datatype.strings.StringDecoder;
import com.siemens.ct.exi.datatype.strings.StringEncoder;
import com.siemens.ct.exi.io.channel.DecoderChannel;
import com.siemens.ct.exi.io.channel.EncoderChannel;
import com.siemens.ct.exi.types.BuiltInType;
import com.siemens.ct.exi.values.IntegerValue;
import com.siemens.ct.exi.values.Value;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.8
 */

public class UnsignedIntegerDatatype extends AbstractDatatype {

	private static final long serialVersionUID = 8260894749324499802L;

	protected IntegerValue lastUnsignedInteger;

	public UnsignedIntegerDatatype(BuiltInType builtInType, QName schemaType) {
		super(builtInType, schemaType);
		assert (builtInType == BuiltInType.UNSIGNED_INTEGER_BIG
				|| builtInType == BuiltInType.UNSIGNED_INTEGER_64
				|| builtInType == BuiltInType.UNSIGNED_INTEGER_32 || builtInType == BuiltInType.UNSIGNED_INTEGER_16);
		this.rcs = new XSDIntegerCharacterSet();
	}

	public boolean isValid(String value) {
		lastUnsignedInteger = IntegerValue.parse(value);
		if (lastUnsignedInteger != null) {
			return (lastUnsignedInteger.isPositive());
		} else {
			return false;
		}
	}

	public boolean isValid(Value value) {
		if (value instanceof IntegerValue) {
			lastUnsignedInteger = ((IntegerValue) value);
			return (lastUnsignedInteger.isPositive());
		} else if (isValid(value.toString())) {
			return true;
		} else {
			return false;
		}
	}

	public void writeValue(EncoderChannel valueChannel,
			StringEncoder stringEncoder, QName context) throws IOException {
		valueChannel.encodeUnsignedIntegerValue(lastUnsignedInteger);
	}

	public Value readValue(DecoderChannel valueChannel,
			StringDecoder stringDecoder, QName context) throws IOException {
		return valueChannel.decodeUnsignedIntegerValue();
	}
}