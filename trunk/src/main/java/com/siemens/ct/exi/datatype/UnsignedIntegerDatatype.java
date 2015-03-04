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
import com.siemens.ct.exi.values.IntegerValue;
import com.siemens.ct.exi.values.Value;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9.5-SNAPSHOT
 */

public class UnsignedIntegerDatatype extends AbstractDatatype {

	private static final long serialVersionUID = 8260894749324499802L;

	protected IntegerValue lastUnsignedInteger;

	public UnsignedIntegerDatatype(QNameContext schemaType) {
		super(BuiltInType.UNSIGNED_INTEGER, schemaType);
	}
	
	public DatatypeID getDatatypeID() {
		return DatatypeID.exi_integer;
	}

	protected boolean isValidString(String value) {
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
		} else {
			return isValidString(value.toString());
		}
	}

	public void writeValue(QNameContext qnContext, EncoderChannel valueChannel,
			StringEncoder stringEncoder) throws IOException {
		valueChannel.encodeUnsignedIntegerValue(lastUnsignedInteger);
	}

	public Value readValue(QNameContext qnContext, DecoderChannel valueChannel,
			StringDecoder stringDecoder) throws IOException {
		return valueChannel.decodeUnsignedIntegerValue();
	}
}