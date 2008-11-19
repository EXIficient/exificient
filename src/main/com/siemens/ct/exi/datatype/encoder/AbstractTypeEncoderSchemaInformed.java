/*
 * Copyright (C) 2007, 2008 Siemens AG
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
import com.siemens.ct.exi.datatype.Datatype;
import com.siemens.ct.exi.io.channel.EncoderChannel;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.1.20081112
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
	protected DatatypeEncoder integerDTE;
	protected DatatypeEncoder unsignedIntegerDTE;
	protected DatatypeEncoder nBitIntegerDTE;
	protected DatatypeEncoder datetimeDTE;
	protected DatatypeEncoder enumerationDTE;
	protected DatatypeEncoder listDTE;
	protected DatatypeEncoder stringDTE;

	public AbstractTypeEncoderSchemaInformed(EXIFactory exiFactory) {
		super(exiFactory);
	}

	public boolean isTypeValid(Datatype datatype, String value) {
		switch (datatype.getDefaultBuiltInType()) {
		case BUILTIN_BINARY_BASE64:
			lastDatatypeEncoder = binaryBase64DTE;
			break;
		case BUILTIN_BINARY_HEX:
			lastDatatypeEncoder = binaryHexDTE;
			break;
		case BUILTIN_BOOLEAN:
			lastDatatypeEncoder = booleanDTE;
			break;
		case BUILTIN_BOOLEAN_PATTERN:
			lastDatatypeEncoder = booleanPatternDTE;
			break;
		case BUILTIN_DECIMAL:
			lastDatatypeEncoder = decimalDTE;
			break;
		case BUILTIN_FLOAT:
			lastDatatypeEncoder = floatDTE;
			break;
		case BUILTIN_INTEGER:
			lastDatatypeEncoder = integerDTE;
			break;
		case BUILTIN_UNSIGNED_INTEGER:
			lastDatatypeEncoder = unsignedIntegerDTE;
			break;
		case BUILTIN_NBIT_INTEGER:
			lastDatatypeEncoder = nBitIntegerDTE;
			break;
		case BUILTIN_DATETIME:
			lastDatatypeEncoder = datetimeDTE;
			break;
		case BUILTIN_ENUMERATION:
			lastDatatypeEncoder = enumerationDTE;
			break;
		case BUILTIN_LIST:
			lastDatatypeEncoder = listDTE;
			break;
		case BUILTIN_STRING:
			lastDatatypeEncoder = stringDTE;
			break;
		default:
			throw new RuntimeException("Unknown BuiltIn Type");
		}

		return lastDatatypeEncoder.isValid(datatype, value);
	}

	// first isValueTypeValid has to be called
	public void writeTypeValidValue(EncoderChannel valueChannel, String uri,
			String localName) throws IOException {
		lastDatatypeEncoder.writeValue(valueChannel, uri, localName);
	}

}
