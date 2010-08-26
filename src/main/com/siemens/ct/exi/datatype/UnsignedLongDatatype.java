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
import com.siemens.ct.exi.values.LongValue;
import com.siemens.ct.exi.values.Value;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.5
 */

public class UnsignedLongDatatype extends AbstractDatatype {

	private static final long serialVersionUID = -1301092165221268158L;
	
	private LongValue lastUnsignedLong;

	public UnsignedLongDatatype(BuiltInType builtInType, QName schemaType) {
		super(builtInType, schemaType);
		assert (builtInType == BuiltInType.UNSIGNED_INTEGER_32);
		this.rcs = new XSDIntegerCharacterSet();
	}

	public boolean isValid(String value) {
		lastUnsignedLong = LongValue.parse(value);
		if (lastUnsignedLong != null) {
			return (lastUnsignedLong.toLong() >= 0);
		} else {
			return false;
		}
	}

	public boolean isValid(Value value) {
		if (value instanceof LongValue) {
			lastUnsignedLong = ((LongValue) value);
			return (lastUnsignedLong.toLong() >= 0);
		} else {
			return false;
		}
	}

	public Value getValue() {
		return lastUnsignedLong;
	}

	public void writeValue(EncoderChannel valueChannel,
			StringEncoder stringEncoder, QName context) throws IOException {
		valueChannel.encodeUnsignedLong(lastUnsignedLong.toLong());
	}

	public Value readValue(DecoderChannel valueChannel,
			StringDecoder stringDecoder, QName context) throws IOException {
		return valueChannel.decodeUnsignedLongValue();
	}
}