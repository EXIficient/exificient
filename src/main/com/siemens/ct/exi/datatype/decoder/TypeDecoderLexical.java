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
import com.siemens.ct.exi.datatype.charset.XSDBase64CharacterSet;
import com.siemens.ct.exi.datatype.charset.XSDBooleanCharacterSet;
import com.siemens.ct.exi.datatype.charset.XSDDateTimeCharacterSet;
import com.siemens.ct.exi.datatype.charset.XSDDecimalCharacterSet;
import com.siemens.ct.exi.datatype.charset.XSDDoubleCharacterSet;
import com.siemens.ct.exi.datatype.charset.XSDHexBinaryCharacterSet;
import com.siemens.ct.exi.datatype.charset.XSDIntegerCharacterSet;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.3.20090421
 */

public class TypeDecoderLexical extends TypeDecoderTypedSchemaInformed {
	public TypeDecoderLexical(EXIFactory exiFactory) {
		super(exiFactory);

		binaryBase64DTD = new RestrictedCharacterSetDatatypeDecoder(
				new XSDBase64CharacterSet());
		binaryHexDTD = new RestrictedCharacterSetDatatypeDecoder(
				new XSDHexBinaryCharacterSet());
		booleanDTD = new RestrictedCharacterSetDatatypeDecoder(
				new XSDBooleanCharacterSet());
		booleanPatternDTD = booleanDTD;
		decimalDTD = new RestrictedCharacterSetDatatypeDecoder(
				new XSDDecimalCharacterSet());
		floatDTD = new RestrictedCharacterSetDatatypeDecoder(
				new XSDDoubleCharacterSet());
		doubleDTD = new RestrictedCharacterSetDatatypeDecoder(
				new XSDDoubleCharacterSet());
		integerDTD = new RestrictedCharacterSetDatatypeDecoder(
				new XSDIntegerCharacterSet());
		longDTD = integerDTD;
		bigIntegerDTD = integerDTD;
		unsignedIntegerDTD = integerDTD;
		unsignedLongDTD = integerDTD;
		unsignedBigIntegerDTD = integerDTD;
		nBitIntegerDTD = integerDTD;
		nBitLongDTD = integerDTD;
		nBitBigIntegerDTD = integerDTD;
		datetimeDTD = new RestrictedCharacterSetDatatypeDecoder(
				new XSDDateTimeCharacterSet());
		enumerationDTD = new EnumerationDatatypeDecoder();
		listDTD = new ListDatatypeDecoder(exiFactory);
		stringDTD = new StringDatatypeDecoder();
		restrictedCharSetDTD = new RestrictedCharacterSetDatatypeDecoder();
	}
}
