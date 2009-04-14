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

package com.siemens.ct.exi.datatype;

import org.apache.xerces.xs.StringList;

import com.siemens.ct.exi.util.MethodsBag;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.3.20081117
 */

public class DatatypeEnumeration extends AbstractDatatype {
	private StringList enumValues;
	private int codingLength;

	public DatatypeEnumeration(StringList enumValues) {
		super(BuiltInType.BUILTIN_ENUMERATION, null);

		this.enumValues = enumValues;
		this.codingLength = MethodsBag.getCodingLength(enumValues.getLength());
	}

	public StringList getEnumerationValues() {
		return enumValues;
	}

	public int getCodingLength() {
		return codingLength;
	}
}