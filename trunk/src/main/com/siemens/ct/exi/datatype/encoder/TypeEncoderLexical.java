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

import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.helpers.BuiltInRestrictedCharacterSets;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.1.20081112
 */

public class TypeEncoderLexical extends AbstractTypeEncoderSchemaInformed {
	public TypeEncoderLexical(EXIFactory exiFactory) {
		super(exiFactory);

		binaryBase64DTE = new RestrictedCharacterSetDatatypeEncoder(this,
				BuiltInRestrictedCharacterSets.newXSDBase64BinaryInstance());
		binaryHexDTE = new RestrictedCharacterSetDatatypeEncoder(this,
				BuiltInRestrictedCharacterSets.newXSDHexBinaryInstance());
		booleanDTE = new RestrictedCharacterSetDatatypeEncoder(this,
				BuiltInRestrictedCharacterSets.newXSDBooleanInstance());
		booleanPatternDTE = booleanDTE;
		decimalDTE = new RestrictedCharacterSetDatatypeEncoder(this,
				BuiltInRestrictedCharacterSets.newXSDDecimalInstance());
		floatDTE = new RestrictedCharacterSetDatatypeEncoder(this,
				BuiltInRestrictedCharacterSets.newXSDDoubleInstance());
		integerDTE = new RestrictedCharacterSetDatatypeEncoder(this,
				BuiltInRestrictedCharacterSets.newXSDIntegerInstance());
		unsignedIntegerDTE = integerDTE;
		nBitIntegerDTE = integerDTE;
		datetimeDTE = new RestrictedCharacterSetDatatypeEncoder(this,
				BuiltInRestrictedCharacterSets.newXSDDateTimeInstance());
		enumerationDTE = new EnumerationDatatypeEncoder(this);
		listDTE = new ListDatatypeEncoder(this, exiFactory);
		stringDTE = new StringDatatypeEncoder(this);
	}
}
