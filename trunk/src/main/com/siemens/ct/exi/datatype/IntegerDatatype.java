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

import com.siemens.ct.exi.context.DecoderContext;
import com.siemens.ct.exi.context.EncoderContext;
import com.siemens.ct.exi.context.QNameContext;
import com.siemens.ct.exi.datatype.charset.XSDIntegerCharacterSet;
import com.siemens.ct.exi.io.channel.DecoderChannel;
import com.siemens.ct.exi.io.channel.EncoderChannel;
import com.siemens.ct.exi.types.BuiltInType;
import com.siemens.ct.exi.types.IntegerType;
import com.siemens.ct.exi.values.IntegerValue;
import com.siemens.ct.exi.values.Value;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.8
 */

public class IntegerDatatype extends AbstractDatatype {

	private static final long serialVersionUID = -7131847569262739592L;

	private IntegerValue lastInteger;
	private final IntegerType integerType;

	public IntegerDatatype(IntegerType integerType, QName schemaType) {
		super(BuiltInType.INTEGER, schemaType);
		this.integerType = integerType;
		this.rcs = new XSDIntegerCharacterSet();
	}

	public IntegerType getIntegerType() {
		return integerType;
	}

	protected boolean isValidString(String value) {
		lastInteger = IntegerValue.parse(value);
		return (lastInteger != null);
	}

	public boolean isValid(Value value) {
		if (value instanceof IntegerValue) {
			lastInteger = ((IntegerValue) value);
			return true;
		} else {
			return isValidString(value.toString());
		}
	}

	public void writeValue(EncoderContext encoderContext,
			QNameContext qnContext, EncoderChannel valueChannel)
			throws IOException {
		valueChannel.encodeIntegerValue(lastInteger);
	}

	public Value readValue(DecoderContext decoderContext,
			QNameContext qnContext, DecoderChannel valueChannel)
			throws IOException {
		return valueChannel.decodeIntegerValue();
	}
}