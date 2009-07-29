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

import com.siemens.ct.exi.EXIFactory;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.3.20090421
 */

public class TypeDecoderTyped extends TypeDecoderTypedSchemaInformed {
	public TypeDecoderTyped(EXIFactory exiFactory) {
		super(exiFactory);

		binaryBase64DTD = new BinaryDatatypeDecoder();
		binaryHexDTD = binaryBase64DTD;
		booleanDTD = new BooleanDatatypeDecoder();
		booleanPatternDTD = new BooleanPatternDatatypeDecoder();
		decimalDTD = new DecimalDatatypeDecoder();
		floatDTD = new FloatDatatypeDecoder();
		doubleDTD = new DoubleDatatypeDecoder();
		integerDTD = new IntegerDatatypeDecoder();
		longDTD = new LongDatatypeDecoder();
		bigIntegerDTD = new BigIntegerDatatypeDecoder();
		unsignedIntegerDTD = new UnsignedIntegerDatatypeDecoder();
		unsignedLongDTD = new UnsignedLongDatatypeDecoder();
		unsignedBigIntegerDTD = new UnsignedBigIntegerDatatypeDecoder();
		nBitIntegerDTD = new NBitIntegerDatatypeDecoder();
		nBitLongDTD = new NBitLongDatatypeDecoder();
		nBitBigIntegerDTD = new NBitBigIntegerDatatypeDecoder();
		datetimeDTD = new DatetimeDatatypeDecoder();
		enumerationDTD = new EnumerationDatatypeDecoder();
		listDTD = new ListDatatypeDecoder(exiFactory);
		stringDTD = new StringDatatypeDecoder();
		restrictedCharSetDTD = new RestrictedCharacterSetDatatypeDecoder();
	}
}
