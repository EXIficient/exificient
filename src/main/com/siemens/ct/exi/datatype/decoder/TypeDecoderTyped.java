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

package com.siemens.ct.exi.datatype.decoder;

import java.io.IOException;

import com.siemens.ct.exi.datatype.Datatype;
import com.siemens.ct.exi.io.channel.DecoderChannel;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.1.20081111
 */

public class TypeDecoderTyped extends AbstractTypeDecoder
{
	protected BinaryDatatypeDecoder				binaryDTD;
	protected BooleanDatatypeDecoder			booleanDTD;
	protected BooleanPatternDatatypeDecoder		booleanPatternDTD;
	protected DecimalDatatypeDecoder			decimalDTD;
	protected FloatDatatypeDecoder				floatDTD;
	protected IntegerDatatypeDecoder			integerDTD;
	protected UnsignedIntegerDatatypeDecoder	unsignedIntegerDTD;
	protected NBitIntegerDatatypeDecoder		nBitIntegerDTD;
	protected DatetimeDatatypeDecoder			datetimeDTD;
	protected EnumerationDatatypeDecoder		enumerationDTD;
	protected ListDatatypeDecoder				listDTD;
	protected StringDatatypeDecoder				stringDTD;

	public TypeDecoderTyped ( boolean isSchemaInformed )
	{
		super ( isSchemaInformed );

		binaryDTD = new BinaryDatatypeDecoder ( );
		booleanDTD = new BooleanDatatypeDecoder ( );
		booleanPatternDTD = new BooleanPatternDatatypeDecoder ( );
		decimalDTD = new DecimalDatatypeDecoder ( );
		floatDTD = new FloatDatatypeDecoder ( );
		integerDTD = new IntegerDatatypeDecoder ( );
		unsignedIntegerDTD = new UnsignedIntegerDatatypeDecoder ( );
		nBitIntegerDTD = new NBitIntegerDatatypeDecoder ( );
		datetimeDTD = new DatetimeDatatypeDecoder ( );
		enumerationDTD = new EnumerationDatatypeDecoder ( );
		listDTD = new ListDatatypeDecoder ( );
		stringDTD = new StringDatatypeDecoder ( );
	}

	public String decodeValue ( Datatype datatype, DecoderChannel dc, String namespaceURI, String localName )
			throws IOException
	{
		switch ( datatype.getDefaultBuiltInType ( ) )
		{
			case BUILTIN_BINARY:
				return binaryDTD.decodeValue ( this, datatype, dc, namespaceURI, localName );
			case BUILTIN_BOOLEAN:
				return booleanDTD.decodeValue ( this, datatype, dc, namespaceURI, localName );
			case BUILTIN_BOOLEAN_PATTERN:
				return booleanPatternDTD.decodeValue ( this, datatype, dc, namespaceURI, localName );
			case BUILTIN_DECIMAL:
				return decimalDTD.decodeValue ( this, datatype, dc, namespaceURI, localName );
			case BUILTIN_FLOAT:
				return floatDTD.decodeValue ( this, datatype, dc, namespaceURI, localName );
			case BUILTIN_INTEGER:
				return integerDTD.decodeValue ( this, datatype, dc, namespaceURI, localName );
			case BUILTIN_UNSIGNED_INTEGER:
				return unsignedIntegerDTD.decodeValue ( this, datatype, dc, namespaceURI, localName );
			case BUILTIN_NBIT_INTEGER:
				return nBitIntegerDTD.decodeValue ( this, datatype, dc, namespaceURI, localName );
			case BUILTIN_DATETIME:
				return datetimeDTD.decodeValue ( this, datatype, dc, namespaceURI, localName );
			case BUILTIN_ENUMERATION:
				return enumerationDTD.decodeValue ( this, datatype, dc, namespaceURI, localName );
			case BUILTIN_LIST:
				return listDTD.decodeValue ( this, datatype, dc, namespaceURI, localName );
			case BUILTIN_STRING:
				return stringDTD.decodeValue ( this, datatype, dc, namespaceURI, localName );
				// return decodeValueAsString ( dc, localName );
			default:
				throw new RuntimeException ( "Unknown BuiltIn Type" );
		}
	}
}
