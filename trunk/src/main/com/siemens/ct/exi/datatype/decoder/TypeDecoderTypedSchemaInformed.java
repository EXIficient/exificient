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
		
		char[] ccRet;
		
		switch (datatype.getDefaultBuiltInType()) {
		case BINARY_BASE64:
			ccRet = binaryBase64DTD.decodeValue(this, datatype, dc,
					namespaceURI, localName);
			break;
		case BINARY_HEX:
			ccRet = binaryHexDTD.decodeValue(this, datatype, dc, namespaceURI,
					localName);
			break;
		case BOOLEAN:
			ccRet = booleanDTD.decodeValue(this, datatype, dc, namespaceURI,
					localName);
			break;
		case BOOLEAN_PATTERN:
			ccRet = booleanPatternDTD.decodeValue(this, datatype, dc,
					namespaceURI, localName);
			break;
		case DECIMAL:
			ccRet = decimalDTD.decodeValue(this, datatype, dc, namespaceURI,
					localName);
			break;
		case FLOAT:
			ccRet = floatDTD.decodeValue(this, datatype, dc, namespaceURI,
					localName);
			break;
		case DOUBLE:
			ccRet = doubleDTD.decodeValue(this, datatype, dc, namespaceURI,
					localName);
			break;
		case INTEGER:
			ccRet = integerDTD.decodeValue(this, datatype, dc, namespaceURI,
					localName);
			break;
		case LONG:
			ccRet = longDTD.decodeValue(this, datatype, dc, namespaceURI,
					localName);
			break;
		case BIG_INTEGER:
			ccRet = bigIntegerDTD.decodeValue(this, datatype, dc, namespaceURI,
					localName);
			break;
		case UNSIGNED_INTEGER:
			ccRet = unsignedIntegerDTD.decodeValue(this, datatype, dc,
					namespaceURI, localName);
			break;
		case UNSIGNED_LONG:
			ccRet = unsignedLongDTD.decodeValue(this, datatype, dc,
					namespaceURI, localName);
			break;
		case UNSIGNED_BIG_INTEGER:
			ccRet = unsignedBigIntegerDTD.decodeValue(this, datatype, dc,
					namespaceURI, localName);
			break;
		case NBIT_INTEGER:
			ccRet = nBitIntegerDTD.decodeValue(this, datatype, dc, namespaceURI,
					localName);
			break;
		case NBIT_LONG:
			ccRet = nBitLongDTD.decodeValue(this, datatype, dc, namespaceURI,
					localName);
			break;
		case NBIT_BIG_INTEGER:
			ccRet = nBitBigIntegerDTD.decodeValue(this, datatype, dc, namespaceURI,
					localName);
			break;
		case DATETIME:
			ccRet = datetimeDTD.decodeValue(this, datatype, dc, namespaceURI,
					localName);
			break;
		case ENUMERATION:
			ccRet = enumerationDTD.decodeValue(this, datatype, dc, namespaceURI,
					localName);
			break;
		case LIST:
			ccRet = listDTD.decodeValue(this, datatype, dc, namespaceURI,
					localName);
			break;
		case STRING:
			ccRet = stringDTD.decodeValue(this, datatype, dc, namespaceURI,
					localName);
			break;
//			char[] c = stringDTD.decodeValue(this, datatype, dc, namespaceURI,
//					localName);
//			System.out.println("StringDec: " + new String(c));
//			return c;
			// return decodeValueAsString ( dc, localName );
		case RESTRICTED_CHARACTER_SET:
			restrictedCharSetDTD
			.setRestrictedCharacterSet(((DatatypeRestrictedCharacterSet) datatype)
					.getRestrictedCharacterSet());
			ccRet = restrictedCharSetDTD.decodeValue(this, datatype, dc, namespaceURI,
					localName);
			break;
		default:
			throw new RuntimeException("Unknown BuiltIn Type: " + datatype.getDefaultBuiltInType());
		}
		
		// System.out.println("readTypeValidValue " + new String(ccRet));
		return ccRet;
	}
}
