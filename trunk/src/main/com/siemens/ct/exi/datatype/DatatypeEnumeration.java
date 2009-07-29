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
//	private CharArray[] enumValues2;
	private char[][] enumValues3;
	private int codingLength;

	public DatatypeEnumeration(StringList enumValues) {
		super(BuiltInType.ENUMERATION, null);

		 this.enumValues = enumValues;
//		enumValues2 = new CharArray[enumValues.getLength()];
		enumValues3 = new char[enumValues.getLength()][];
		for (int i=0; i<enumValues.getLength(); i++) {
			// enumValues2[i]  = new CharArray(enumValues.item(i).toCharArray());
			enumValues3[i] = enumValues.item(i).toCharArray();
		}
		
		this.codingLength = MethodsBag.getCodingLength(enumValues.getLength());
	}

	public String getEnumerationValueAsString(int index) {
		return enumValues.item(index);
	}
	
	public char[] getEnumerationValueAsCharArray(int index) {
		return enumValues3[index];
		// return enumValues2[index].toCharArray();
	}
	
	public int getEnumerationSize() {
		return enumValues3.length;
//		return enumValues2.length;
	}
	
//	public StringList getEnumerationValues() {
//		return enumValues;
//	}

	public int getCodingLength() {
		return codingLength;
	}
}