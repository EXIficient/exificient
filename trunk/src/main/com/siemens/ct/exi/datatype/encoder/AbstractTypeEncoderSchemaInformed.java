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

package com.siemens.ct.exi.datatype.encoder;

import java.io.IOException;

import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.core.NameContext;
import com.siemens.ct.exi.datatype.Datatype;
import com.siemens.ct.exi.datatype.DatatypeRestrictedCharacterSet;
import com.siemens.ct.exi.io.channel.EncoderChannel;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.3.20090421
 */

public abstract class AbstractTypeEncoderSchemaInformed extends
		AbstractTypeEncoder {
	protected DatatypeEncoder lastDatatypeEncoder;

	protected DatatypeEncoder binaryBase64DTE;
	protected DatatypeEncoder binaryHexDTE;
	protected DatatypeEncoder booleanDTE;
	protected DatatypeEncoder booleanPatternDTE;
	protected DatatypeEncoder decimalDTE;
	protected DatatypeEncoder floatDTE;
	protected DatatypeEncoder doubleDTE;
	protected DatatypeEncoder integerDTE;
	protected DatatypeEncoder longDTE;
	protected DatatypeEncoder bigIntegerDTE;
	protected DatatypeEncoder unsignedIntegerDTE;
	protected DatatypeEncoder unsignedLongDTE;
	protected DatatypeEncoder unsignedBigIntegerDTE;
	protected DatatypeEncoder nBitIntegerDTE;
	protected DatatypeEncoder nBitLongDTE;
	protected DatatypeEncoder nBitBigIntegerDTE;
	protected DatatypeEncoder datetimeDTE;
	protected DatatypeEncoder enumerationDTE;
	protected DatatypeEncoder listDTE;
	protected DatatypeEncoder stringDTE;
	protected RestrictedCharacterSetDatatypeEncoder restrictedCharSetDTE;

	public AbstractTypeEncoderSchemaInformed(EXIFactory exiFactory) {
		super(exiFactory);
	}

	public boolean isTypeValid(Datatype datatype, String value) {
		switch (datatype.getDefaultBuiltInType()) {
		case BINARY_BASE64:
			lastDatatypeEncoder = binaryBase64DTE;
			break;
		case BINARY_HEX:
			lastDatatypeEncoder = binaryHexDTE;
			break;
		case BOOLEAN:
			lastDatatypeEncoder = booleanDTE;
			break;
		case BOOLEAN_PATTERN:
			lastDatatypeEncoder = booleanPatternDTE;
			break;
		case DECIMAL:
			lastDatatypeEncoder = decimalDTE;
			break;
		case FLOAT:
			lastDatatypeEncoder = floatDTE;
			break;
		case DOUBLE:
			lastDatatypeEncoder = doubleDTE;
			break;
		case INTEGER:
			lastDatatypeEncoder = integerDTE;
			break;
		case LONG:
			lastDatatypeEncoder = longDTE;
			break;
		case BIG_INTEGER:
			lastDatatypeEncoder = bigIntegerDTE;
			break;
		case UNSIGNED_INTEGER:
			lastDatatypeEncoder = unsignedIntegerDTE;
			break;
		case UNSIGNED_LONG:
			lastDatatypeEncoder = unsignedLongDTE;
			break;
		case UNSIGNED_BIG_INTEGER:
			lastDatatypeEncoder = unsignedBigIntegerDTE;
			break;
		case NBIT_INTEGER:
			lastDatatypeEncoder = nBitIntegerDTE;
			break;
		case NBIT_LONG:
			lastDatatypeEncoder = nBitLongDTE;
			break;
		case NBIT_BIG_INTEGER:
			lastDatatypeEncoder = nBitBigIntegerDTE;
			break;
		case DATETIME:
			lastDatatypeEncoder = datetimeDTE;
			break;
		case ENUMERATION:
			lastDatatypeEncoder = enumerationDTE;
			break;
		case LIST:
			lastDatatypeEncoder = listDTE;
			break;
		case STRING:
			lastDatatypeEncoder = stringDTE;
			break;
		case RESTRICTED_CHARACTER_SET:
			restrictedCharSetDTE
					.setRestrictedCharacterSet(((DatatypeRestrictedCharacterSet) datatype)
							.getRestrictedCharacterSet());
			lastDatatypeEncoder = restrictedCharSetDTE;
			break;
		default:
			throw new RuntimeException("Unknown BuiltIn Type: " + datatype.getDefaultBuiltInType());
		}

		return lastDatatypeEncoder.isValid(datatype, value);
	}

	// first isValueTypeValid has to be called
//	public void writeTypeValidValue(EncoderChannel valueChannel, String uri,
//			String localName) throws IOException {
//		lastDatatypeEncoder.writeValue(valueChannel, uri, localName);
//	}
	
	public void writeTypeValidValue(NameContext context, EncoderChannel valueChannel) throws IOException {
		lastDatatypeEncoder.writeValue(context, valueChannel);
	}

}
