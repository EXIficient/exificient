/*
 * Copyright (C) 2007-2010 Siemens AG
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

import javax.xml.namespace.QName;

import com.siemens.ct.exi.datatype.strings.StringDecoder;
import com.siemens.ct.exi.datatype.strings.StringEncoder;
import com.siemens.ct.exi.io.channel.DecoderChannel;
import com.siemens.ct.exi.io.channel.EncoderChannel;
import com.siemens.ct.exi.types.BuiltInType;
import com.siemens.ct.exi.util.MethodsBag;
import com.siemens.ct.exi.values.Value;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.5
 */

public class EnumerationDatatype extends AbstractDatatype {
	
	protected int codingLength;
	protected Value[] enumValues;
	protected int lastValidIndex;

	public EnumerationDatatype(Value[] enumValues, QName schemaType) {
		super(BuiltInType.ENUMERATION, schemaType);

		this.enumValues = enumValues;
		this.rcs = null;

		this.codingLength = MethodsBag.getCodingLength(enumValues.length);
	}

	public int getEnumerationSize() {
		return enumValues.length;
	}

	public int getCodingLength() {
		return codingLength;
	}

	// When the preserve.lexicalValues option is false, enumerated values are
	// encoded as n-bit Unsigned Integers
	public boolean isValid(String value) {
		int index = 0;
		while (index < enumValues.length) {
			if ( enumValues[index].equals(value) ) {
				lastValidIndex = index; 
				return true;
			}
			index++;
		}
		return false;
	}
	
	public boolean isValid(Value value) {
		int index = 0;
		while (index < enumValues.length) {
			if ( enumValues[index].equals(value) ) {
				lastValidIndex = index; 
				return true;
			}
			index++;
		}
		
		return false;
	}
	
	
	public Value getValue() {
		return enumValues[lastValidIndex];
	}

	public Value getEnumValue(int i) {
		assert(i>=0 && i< enumValues.length);
		return enumValues[i];
	}
	
	@Override
	// When the preserve.lexicalValues option is true, enumerated values are
	// encoded as String
	public boolean isValidRCS(String value) {
		this.lastRCSValue = value;
		return true;
	}

	@Override
	public void writeValueRCS(RestrictedCharacterSetDatatype rcsEncoder,
			EncoderChannel valueChannel, StringEncoder stringEncoder,
			QName context) throws IOException {
		stringEncoder.writeValue(context, valueChannel, lastRCSValue);
	}
	
	
	public void writeValue(EncoderChannel valueChannel,
			StringEncoder stringEncoder, QName context) throws IOException {
		valueChannel.encodeNBitUnsignedInteger(lastValidIndex, codingLength);
	}


	public Value readValue(DecoderChannel valueChannel,
			StringDecoder stringDecoder, QName context) throws IOException {
		int index = valueChannel.decodeNBitUnsignedInteger(codingLength);
		assert(index >= 0 && index <enumValues.length);
		return enumValues[index];
	}


	@Override
	public Value readValueRCS(RestrictedCharacterSetDatatype rcsDecoder,
			DecoderChannel valueChannel, StringDecoder stringDecoder,
			QName context) throws IOException {
		return stringDecoder.readValue(context, valueChannel);
	}

}