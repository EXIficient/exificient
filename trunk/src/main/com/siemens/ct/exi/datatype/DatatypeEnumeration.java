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

import java.io.IOException;

import org.apache.xerces.xs.StringList;

import com.siemens.ct.exi.core.NameContext;
import com.siemens.ct.exi.datatype.strings.StringEncoder;
import com.siemens.ct.exi.io.channel.EncoderChannel;
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

	private int lastOrdinalPosition;
	
	private StringList enumValues;
	private char[][] enumValues3;
	private int codingLength;

	public DatatypeEnumeration(StringList enumValues) {
		super(BuiltInType.ENUMERATION, null);
		
		this.rcs = null; 

		this.enumValues = enumValues;
		enumValues3 = new char[enumValues.getLength()][];
		for (int i=0; i<enumValues.getLength(); i++) {
			enumValues3[i] = enumValues.item(i).toCharArray();
		}
		
		this.codingLength = MethodsBag.getCodingLength(enumValues.getLength());
	}

	public String getEnumerationValueAsString(int index) {
		return enumValues.item(index);
	}
	
	public char[] getEnumerationValueAsCharArray(int index) {
		return enumValues3[index];
	}
	
	public int getEnumerationSize() {
		return enumValues3.length;
	}

	public int getCodingLength() {
		return codingLength;
	}
	
	public boolean isValid(String value) {
		lastOrdinalPosition = -1;
		int index = 0;
		// while (index < lastEnumValues.getLength()) {
		while (index < getEnumerationSize()) {
			if (getEnumerationValueAsString(index).equals(value)) {
				lastOrdinalPosition = index;
				return true;
			}
			index++;
		}

		return false;
	}
	
	@Override
	public boolean isValidRCS(String value) {
		if( isValid(value) ) {
			return super.isValidRCS(value);
		} else  {
			return false;
		}
		
	}

	public void writeValue(EncoderChannel valueChannel, StringEncoder stringEncoder, NameContext context)
			throws IOException {
		valueChannel.encodeNBitUnsignedInteger(lastOrdinalPosition, getCodingLength());
	}
	
	@Override
	public void writeValueRCS(DatatypeRestrictedCharacterSet rcsEncoder, EncoderChannel valueChannel, StringEncoder stringEncoder, NameContext context) throws IOException {
		this.writeValue(valueChannel, stringEncoder, context);
	}
}