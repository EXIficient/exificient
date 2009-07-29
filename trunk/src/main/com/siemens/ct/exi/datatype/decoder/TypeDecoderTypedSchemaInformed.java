/*
 * Copyright (C) 2007-2009 Siemens AG
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

package com.siemens.ct.exi.datatype.decoder;

import java.io.IOException;

import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.datatype.Datatype;
import com.siemens.ct.exi.datatype.DatatypeRestrictedCharacterSet;
import com.siemens.ct.exi.io.channel.DecoderChannel;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.3.20090421
 */

public class TypeDecoderTypedSchemaInformed extends AbstractTypeDecoder {
	protected DatatypeDecoder binaryBase64DTD;
	protected DatatypeDecoder binaryHexDTD;
	protected DatatypeDecoder booleanDTD;
	protected DatatypeDecoder booleanPatternDTD;
	protected DatatypeDecoder decimalDTD;
	protected DatatypeDecoder floatDTD;
	protected DatatypeDecoder doubleDTD;
	protected DatatypeDecoder integerDTD;
	protected DatatypeDecoder longDTD;
	protected DatatypeDecoder bigIntegerDTD;
	protected DatatypeDecoder unsignedIntegerDTD;
	protected DatatypeDecoder unsignedLongDTD;
	protected DatatypeDecoder unsignedBigIntegerDTD;
	protected DatatypeDecoder nBitIntegerDTD;
	protected DatatypeDecoder nBitLongDTD;
	protected DatatypeDecoder nBitBigIntegerDTD;
	protected DatatypeDecoder datetimeDTD;
	protected DatatypeDecoder enumerationDTD;
	protected DatatypeDecoder listDTD;
	protected DatatypeDecoder stringDTD;
	protected RestrictedCharacterSetDatatypeDecoder restrictedCharSetDTD;

	public TypeDecoderTypedSchemaInformed(EXIFactory exiFactory) {
		super(exiFactory);
	}

	public char[] readTypeValidValue(Datatype datatype, DecoderChannel dc,
			String namespaceURI, String localName) throws IOException {
		switch (datatype.getDefaultBuiltInType()) {
		case BINARY_BASE64:
			return binaryBase64DTD.decodeValue(this, datatype, dc,
					namespaceURI, localName);
		case BINARY_HEX:
			return binaryHexDTD.decodeValue(this, datatype, dc, namespaceURI,
					localName);
		case BOOLEAN:
			return booleanDTD.decodeValue(this, datatype, dc, namespaceURI,
					localName);
		case BOOLEAN_PATTERN:
			return booleanPatternDTD.decodeValue(this, datatype, dc,
					namespaceURI, localName);
		case DECIMAL:
			return decimalDTD.decodeValue(this, datatype, dc, namespaceURI,
					localName);
		case FLOAT:
			return floatDTD.decodeValue(this, datatype, dc, namespaceURI,
					localName);
		case DOUBLE:
			return doubleDTD.decodeValue(this, datatype, dc, namespaceURI,
					localName);
		case INTEGER:
			return integerDTD.decodeValue(this, datatype, dc, namespaceURI,
					localName);
		case LONG:
			return longDTD.decodeValue(this, datatype, dc, namespaceURI,
					localName);
		case BIG_INTEGER:
			return bigIntegerDTD.decodeValue(this, datatype, dc, namespaceURI,
					localName);
		case UNSIGNED_INTEGER:
			return unsignedIntegerDTD.decodeValue(this, datatype, dc,
					namespaceURI, localName);
		case UNSIGNED_LONG:
			return unsignedLongDTD.decodeValue(this, datatype, dc,
					namespaceURI, localName);
		case UNSIGNED_BIG_INTEGER:
			return unsignedBigIntegerDTD.decodeValue(this, datatype, dc,
					namespaceURI, localName);
		case NBIT_INTEGER:
			return nBitIntegerDTD.decodeValue(this, datatype, dc, namespaceURI,
					localName);
		case NBIT_LONG:
			return nBitLongDTD.decodeValue(this, datatype, dc, namespaceURI,
					localName);
		case NBIT_BIG_INTEGER:
			return nBitBigIntegerDTD.decodeValue(this, datatype, dc, namespaceURI,
					localName);
		case DATETIME:
			return datetimeDTD.decodeValue(this, datatype, dc, namespaceURI,
					localName);
		case ENUMERATION:
			return enumerationDTD.decodeValue(this, datatype, dc, namespaceURI,
					localName);
		case LIST:
			return listDTD.decodeValue(this, datatype, dc, namespaceURI,
					localName);
		case STRING:
			return stringDTD.decodeValue(this, datatype, dc, namespaceURI,
					localName);
//			char[] c = stringDTD.decodeValue(this, datatype, dc, namespaceURI,
//					localName);
//			System.out.println("StringDec: " + new String(c));
//			return c;
			// return decodeValueAsString ( dc, localName );
		case RESTRICTED_CHARACTER_SET:
			restrictedCharSetDTD
			.setRestrictedCharacterSet(((DatatypeRestrictedCharacterSet) datatype)
					.getRestrictedCharacterSet());
			return restrictedCharSetDTD.decodeValue(this, datatype, dc, namespaceURI,
					localName);
		default:
			throw new RuntimeException("Unknown BuiltIn Type: " + datatype.getDefaultBuiltInType());
		}
	}
}
