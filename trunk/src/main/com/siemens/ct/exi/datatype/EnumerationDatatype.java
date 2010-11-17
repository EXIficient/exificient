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

import com.siemens.ct.exi.datatype.charset.XSDBase64CharacterSet;
import com.siemens.ct.exi.datatype.charset.XSDBooleanCharacterSet;
import com.siemens.ct.exi.datatype.charset.XSDDateTimeCharacterSet;
import com.siemens.ct.exi.datatype.charset.XSDDecimalCharacterSet;
import com.siemens.ct.exi.datatype.charset.XSDDoubleCharacterSet;
import com.siemens.ct.exi.datatype.charset.XSDHexBinaryCharacterSet;
import com.siemens.ct.exi.datatype.charset.XSDIntegerCharacterSet;
import com.siemens.ct.exi.datatype.charset.XSDStringCharacterSet;
import com.siemens.ct.exi.datatype.strings.StringDecoder;
import com.siemens.ct.exi.datatype.strings.StringEncoder;
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
 * @version 0.5
 */

public class EnumerationDatatype extends AbstractDatatype {
	
	private static final long serialVersionUID = -5065239322174326749L;
	
	protected int codingLength;
	protected Value[] enumValues;
	protected int lastValidIndex;

	public EnumerationDatatype(Value[] enumValues, BuiltInType bitEnumValues, QName schemaType) {
		super(BuiltInType.ENUMERATION, schemaType);

		this.enumValues = enumValues;
		this.codingLength = MethodsBag.getCodingLength(enumValues.length);
		
		// restricted character set
		switch(bitEnumValues) {
		/* Binary */
		case BINARY_BASE64:
			this.rcs = new XSDBase64CharacterSet();
			break;
		case BINARY_HEX:
			this.rcs = new XSDHexBinaryCharacterSet();
			break;
		/* Boolean */
		case BOOLEAN:
		case BOOLEAN_PATTERN:
			this.rcs = new XSDBooleanCharacterSet();
			break;
		/* Decimal */
		case DECIMAL:
			this.rcs = new XSDDecimalCharacterSet();
			break;
		/* Float */
		case FLOAT:
		case DOUBLE:
			this.rcs = new XSDDoubleCharacterSet();
		/* N-Bit Integer */ /* Unsigned Integer */ /* (Signed) Integer */
		case NBIT_INTEGER_32:
		case NBIT_INTEGER_64:
		case NBIT_INTEGER_BIG:
		case UNSIGNED_INTEGER_16:
		case UNSIGNED_INTEGER_32:
		case UNSIGNED_INTEGER_64:
		case UNSIGNED_INTEGER_BIG:
		case INTEGER_16:
		case INTEGER_32:
		case INTEGER_64:
		case INTEGER_BIG:
			this.rcs = new XSDIntegerCharacterSet();
			break;
		/* Datetime */
		case DATETIME:
			this.rcs = new XSDDateTimeCharacterSet();
			break;
		/* String */
		// STRING,
		/* Enumeration */
		// ENUMERATION,
		/* List */
		// LIST,
		/* Restricted Character Set */
		// RESTRICTED_CHARACTER_SET,
		/* QName */
		// QNAME;
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

	// When the preserve.lexicalValues option is false, enumerated values are
	// encoded as n-bit Unsigned Integers
	public boolean isValid(String value) {
		int index = 0;
		while (index < enumValues.length) {
			if ( enumValues[index].equals(value) ) {
				lastValidIndex = index; 
				return true;
			}
			index++;
		}
		return false;
	}
	
	public boolean isValid(Value value) {
		int index = 0;
		while (index < enumValues.length) {
			if ( enumValues[index].equals(value) ) {
				lastValidIndex = index; 
				return true;
			}
			index++;
		}
		
		return false;
	}
	
	
	public Value getValue() {
		return enumValues[lastValidIndex];
	}

	public Value getEnumValue(int i) {
		assert(i>=0 && i< enumValues.length);
		return enumValues[i];
	}
	
//	@Override
//	// When the preserve.lexicalValues option is true, enumerated values are
//	// encoded as String
//	public boolean isValidRCS(String value) {
//		// super.isValidRCS(value);
//		this.lastRCSValue = value;
//		return true;
//	}
//
//	@Override
//	public void writeValueRCS(RestrictedCharacterSetDatatype rcsEncoder,
//			EncoderChannel valueChannel, StringEncoder stringEncoder,
//			QName context) throws IOException {
//		// super.writeValueRCS(rcsEncoder, valueChannel, stringEncoder, context)
//		stringEncoder.writeValue(context, valueChannel, lastRCSValue);
//	}
	
	
	public void writeValue(EncoderChannel valueChannel,
			StringEncoder stringEncoder, QName context) throws IOException {
		valueChannel.encodeNBitUnsignedInteger(lastValidIndex, codingLength);
	}


	public Value readValue(DecoderChannel valueChannel,
			StringDecoder stringDecoder, QName context) throws IOException {
		int index = valueChannel.decodeNBitUnsignedInteger(codingLength);
		assert(index >= 0 && index <enumValues.length);
		return enumValues[index];
	}


//	@Override
//	public Value readValueRCS(RestrictedCharacterSetDatatype rcsDecoder,
//			DecoderChannel valueChannel, StringDecoder stringDecoder,
//			QName context) throws IOException {
//		return stringDecoder.readValue(context, valueChannel);
//	}

}