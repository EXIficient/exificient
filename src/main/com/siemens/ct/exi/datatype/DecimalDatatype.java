/*
 * Copyright (C) 2007-2012 Siemens AG
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
import com.siemens.ct.exi.datatype.charset.XSDDecimalCharacterSet;
import com.siemens.ct.exi.io.channel.DecoderChannel;
import com.siemens.ct.exi.io.channel.EncoderChannel;
import com.siemens.ct.exi.types.BuiltInType;
import com.siemens.ct.exi.values.DecimalValue;
import com.siemens.ct.exi.values.Value;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9
 */

public class DecimalDatatype extends AbstractDatatype {

	private static final long serialVersionUID = -1045398309238670727L;

	protected DecimalValue lastValidDecimal;

	public DecimalDatatype(QName schemaType) {
		super(BuiltInType.DECIMAL, schemaType);
		this.rcs = new XSDDecimalCharacterSet();
	}

	protected boolean isValidString(String value) {
		lastValidDecimal = DecimalValue.parse(value);
		return (lastValidDecimal != null);
	}

	public boolean isValid(Value value) {
		if (value instanceof DecimalValue) {
			lastValidDecimal = ((DecimalValue) value);
			return true;
		} else {
			return isValidString(value.toString());
		}
	}

	public void writeValue(EncoderContext encoderContext,
			QNameContext qnContext, EncoderChannel valueChannel)
			throws IOException {
		valueChannel.encodeDecimal(lastValidDecimal.isNegative(),
				lastValidDecimal.getIntegral(), lastValidDecimal.getRevFractional());
	}

	public Value readValue(DecoderContext decoderContext,
			QNameContext qnContext, DecoderChannel valueChannel)
			throws IOException {
		return valueChannel.decodeDecimalValue();
	}
}