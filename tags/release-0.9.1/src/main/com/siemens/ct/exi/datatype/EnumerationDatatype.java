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
import com.siemens.ct.exi.datatype.charset.XSDBase64CharacterSet;
import com.siemens.ct.exi.datatype.charset.XSDBooleanCharacterSet;
import com.siemens.ct.exi.datatype.charset.XSDDateTimeCharacterSet;
import com.siemens.ct.exi.datatype.charset.XSDDecimalCharacterSet;
import com.siemens.ct.exi.datatype.charset.XSDDoubleCharacterSet;
import com.siemens.ct.exi.datatype.charset.XSDHexBinaryCharacterSet;
import com.siemens.ct.exi.datatype.charset.XSDIntegerCharacterSet;
import com.siemens.ct.exi.datatype.charset.XSDStringCharacterSet;
import com.siemens.ct.exi.io.channel.DecoderChannel;
import com.siemens.ct.exi.io.channel.EncoderChannel;
import com.siemens.ct.exi.types.BuiltInType;
import com.siemens.ct.exi.util.MethodsBag;
import com.siemens.ct.exi.values.Value;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9.1
 */

public class EnumerationDatatype extends AbstractDatatype {

	private static final long serialVersionUID = -5065239322174326749L;

	protected int codingLength;
	protected Value[] enumValues;
	protected BuiltInType bitEnumValues;
	protected int lastValidIndex;

	public EnumerationDatatype(Value[] enumValues, BuiltInType bitEnumValues,
			QName schemaType) {
		super(BuiltInType.ENUMERATION, schemaType);

		this.enumValues = enumValues;
		this.bitEnumValues = bitEnumValues;
		this.codingLength = MethodsBag.getCodingLength(enumValues.length);

		// restricted character set
		switch (bitEnumValues) {
		/* Binary */
		case BINARY_BASE64:
			this.rcs = new XSDBase64CharacterSet();
			break;
		case BINARY_HEX:
			this.rcs = new XSDHexBinaryCharacterSet();
			break;
		/* Boolean */
		case BOOLEAN:
			// case BOOLEAN_PATTERN:
			this.rcs = new XSDBooleanCharacterSet();
			break;
		/* Decimal */
		case DECIMAL:
			this.rcs = new XSDDecimalCharacterSet();
			break;
		/* Float */
		case FLOAT:
			this.rcs = new XSDDoubleCharacterSet();
			break;
		/* N-Bit Integer *//* Unsigned Integer *//* (Signed) Integer */
		case INTEGER:
			this.rcs = new XSDIntegerCharacterSet();
			break;
		/* Datetime */
		case DATETIME:
			this.rcs = new XSDDateTimeCharacterSet();
			break;
		/* Others */
		default:
			this.rcs = new XSDStringCharacterSet(); // String
		}

	}

	public int getEnumerationSize() {
		return enumValues.length;
	}

	public int getCodingLength() {
		return codingLength;
	}

	public boolean isValid(Value value) {
		int index = 0;
		while (index < enumValues.length) {
			if (enumValues[index].equals(value)) {
				lastValidIndex = index;
				return true;
			}
			index++;
		}

		return false;
	}

	public Value getEnumValue(int i) {
		assert (i >= 0 && i < enumValues.length);
		return enumValues[i];
	}

	public BuiltInType getEnumValueBuiltInType() {
		return bitEnumValues;
	}

	public void writeValue(EncoderContext encoderContext,
			QNameContext qnContext, EncoderChannel valueChannel)
			throws IOException {
		valueChannel.encodeNBitUnsignedInteger(lastValidIndex, codingLength);
	}

	public Value readValue(DecoderContext decoderContext,
			QNameContext qnContext, DecoderChannel valueChannel)
			throws IOException {
		int index = valueChannel.decodeNBitUnsignedInteger(codingLength);
		assert (index >= 0 && index < enumValues.length);
		return enumValues[index];
	}

}