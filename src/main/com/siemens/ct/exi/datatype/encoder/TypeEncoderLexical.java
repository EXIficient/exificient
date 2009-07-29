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

public class TypeEncoderLexical extends AbstractTypeEncoderSchemaInformed {
	public TypeEncoderLexical(EXIFactory exiFactory) {
		super(exiFactory);

		binaryBase64DTE = new RestrictedCharacterSetDatatypeEncoder(this,
				new XSDBase64CharacterSet());
		binaryHexDTE = new RestrictedCharacterSetDatatypeEncoder(this,
				new XSDHexBinaryCharacterSet());
		booleanDTE = new RestrictedCharacterSetDatatypeEncoder(this,
				new XSDBooleanCharacterSet());
		booleanPatternDTE = booleanDTE;
		decimalDTE = new RestrictedCharacterSetDatatypeEncoder(this,
				new XSDDecimalCharacterSet());
		floatDTE = new RestrictedCharacterSetDatatypeEncoder(this,
				new XSDDoubleCharacterSet());
		doubleDTE = new RestrictedCharacterSetDatatypeEncoder(this,
				new XSDDoubleCharacterSet());
		integerDTE = new RestrictedCharacterSetDatatypeEncoder(this,
				new XSDIntegerCharacterSet());
		longDTE = integerDTE;
		bigIntegerDTE = integerDTE;
		unsignedIntegerDTE = integerDTE;
		unsignedLongDTE = integerDTE;
		unsignedBigIntegerDTE = integerDTE;
		nBitIntegerDTE = integerDTE;
		nBitLongDTE = integerDTE;
		nBitBigIntegerDTE = integerDTE;
		datetimeDTE = new RestrictedCharacterSetDatatypeEncoder(this,
				new XSDDateTimeCharacterSet());
		enumerationDTE = new EnumerationDatatypeEncoder(this);
		listDTE = new ListDatatypeEncoder(this, exiFactory);
		stringDTE = new StringDatatypeEncoder(this);
		restrictedCharSetDTE = new RestrictedCharacterSetDatatypeEncoder(this);
	}
}
