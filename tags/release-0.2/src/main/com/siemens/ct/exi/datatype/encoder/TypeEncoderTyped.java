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
import com.siemens.ct.exi.datatype.stringtable.StringTableEncoder;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.1.20081112
 */

public class TypeEncoderTyped extends AbstractTypeEncoderSchemaInformed {
	public TypeEncoderTyped(EXIFactory exiFactory) {
		super(exiFactory);

		binaryBase64DTE = new BinaryDatatypeEncoder(this);
		binaryHexDTE = binaryBase64DTE;
		booleanDTE = new BooleanDatatypeEncoder(this);
		booleanPatternDTE = new BooleanPatternDatatypeEncoder(this);
		decimalDTE = new DecimalDatatypeEncoder(this);
		floatDTE = new FloatDatatypeEncoder(this);
		integerDTE = new IntegerDatatypeEncoder(this);
		unsignedIntegerDTE = new UnsignedIntegerDatatypeEncoder(this);
		nBitIntegerDTE = new NBitIntegerDatatypeEncoder(this);
		datetimeDTE = new DatetimeDatatypeEncoder(this);
		enumerationDTE = new EnumerationDatatypeEncoder(this);
		listDTE = new ListDatatypeEncoder(this, exiFactory);
		stringDTE = new StringDatatypeEncoder(this);
	}

	public TypeEncoderTyped(EXIFactory exiFactory,
			StringTableEncoder stringTable) {
		// typed encoder needs to be schemaInformed
		this(exiFactory);

		this.stringTable = stringTable;
	}
}
