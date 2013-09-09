/*
 * Copyright (C) 2007-2012 Siemens AG
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

import com.siemens.ct.exi.context.QNameContext;
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
 * @version 0.9.2
 */

public class EnumerationDatatype extends AbstractDatatype {

	private static final long serialVersionUID = -5065239322174326749L;

	protected Datatype dtEnumValues;
	
	protected int codingLength;
	protected Value[] enumValues;
	protected int lastValidIndex;

	public EnumerationDatatype(Value[] enumValues, Datatype dtEnumValues,
			QNameContext schemaType) {
		super(BuiltInType.ENUMERATION, schemaType);

		this.dtEnumValues = dtEnumValues;
		this.enumValues = enumValues;
		this.codingLength = MethodsBag.getCodingLength(enumValues.length);
	}
	
	public Datatype getEnumValueDatatype() {
		return dtEnumValues;
	}
	
	public DatatypeID getDatatypeID() {
		return dtEnumValues.getDatatypeID();
	}

	public int getEnumerationSize() {
		return enumValues.length;
	}

	public int getCodingLength() {
		return codingLength;
	}

	public boolean isValid(Value value) {
		int index = 0;
		while (index < enumValues.length) {
			if (enumValues[index].equals(value)) {
				lastValidIndex = index;
				return true;
			}
			index++;
		}

		return false;
	}

	public Value getEnumValue(int i) {
		assert (i >= 0 && i < enumValues.length);
		return enumValues[i];
	}

	public void writeValue(QNameContext qnContext, EncoderChannel valueChannel,
			StringEncoder stringEncoder) throws IOException {
		valueChannel.encodeNBitUnsignedInteger(lastValidIndex, codingLength);
	}

	public Value readValue(QNameContext qnContext, DecoderChannel valueChannel,
			StringDecoder stringDecoder) throws IOException {
		int index = valueChannel.decodeNBitUnsignedInteger(codingLength);
		assert (index >= 0 && index < enumValues.length);
		return enumValues[index];
	}

}