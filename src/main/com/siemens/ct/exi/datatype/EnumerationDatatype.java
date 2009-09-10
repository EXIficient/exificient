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

import com.siemens.ct.exi.core.Context;
import com.siemens.ct.exi.datatype.strings.StringDecoder;
import com.siemens.ct.exi.datatype.strings.StringEncoder;
import com.siemens.ct.exi.io.channel.DecoderChannel;
import com.siemens.ct.exi.io.channel.EncoderChannel;
import com.siemens.ct.exi.types.BuiltInType;
import com.siemens.ct.exi.util.MethodsBag;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.3.20081117
 */

public class EnumerationDatatype extends AbstractDatatype {

	private int lastOrdinalPosition;

	private StringList enumValuesSL;
	private char[][] enumValuesCH;
	private int codingLength;

	public EnumerationDatatype(StringList enumValues) {
		super(BuiltInType.ENUMERATION, null);

		this.rcs = null;

		this.enumValuesSL = enumValues;
		enumValuesCH = new char[enumValues.getLength()][];
		for (int i = 0; i < enumValues.getLength(); i++) {
			enumValuesCH[i] = enumValues.item(i).toCharArray();
		}

		this.codingLength = MethodsBag.getCodingLength(enumValues.getLength());
	}

	public String getEnumerationValueAsString(int index) {
		return enumValuesSL.item(index);
	}

	public char[] getEnumerationValueAsCharArray(int index) {
		return enumValuesCH[index];
	}

	public int getEnumerationSize() {
		return enumValuesCH.length;
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
		if (isValid(value)) {
			return super.isValidRCS(value);
		} else {
			return false;
		}

	}

	public void writeValue(EncoderChannel valueChannel,
			StringEncoder stringEncoder, Context context)
			throws IOException {
		valueChannel.encodeNBitUnsignedInteger(lastOrdinalPosition,
				codingLength);
	}

	@Override
	public void writeValueRCS(RestrictedCharacterSetDatatype rcsEncoder,
			EncoderChannel valueChannel, StringEncoder stringEncoder,
			Context context) throws IOException {
		writeValue(valueChannel, stringEncoder, context);
	}

	public char[] readValue(DecoderChannel valueChannel,
			StringDecoder stringDecoder, Context context)
			throws IOException {
		int index = valueChannel.decodeNBitUnsignedInteger(codingLength);

		return getEnumerationValueAsCharArray(index);
	}

	@Override
	public char[] readValueRCS(RestrictedCharacterSetDatatype rcsDecoder,
			DecoderChannel valueChannel, StringDecoder stringDecoder,
			Context context) throws IOException {
		return readValue(valueChannel, stringDecoder, context);	
	}

}