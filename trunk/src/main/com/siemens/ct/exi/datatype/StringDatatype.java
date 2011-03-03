/*
 * Copyright (C) 2007-2011 Siemens AG
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

import com.siemens.ct.exi.datatype.charset.XSDStringCharacterSet;
import com.siemens.ct.exi.datatype.strings.StringDecoder;
import com.siemens.ct.exi.datatype.strings.StringEncoder;
import com.siemens.ct.exi.io.channel.DecoderChannel;
import com.siemens.ct.exi.io.channel.EncoderChannel;
import com.siemens.ct.exi.types.BuiltInType;
import com.siemens.ct.exi.values.StringValue;
import com.siemens.ct.exi.values.Value;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.6
 */

public class StringDatatype extends AbstractDatatype {

	private static final long serialVersionUID = 4636133910606239257L;

	protected String lastValue;

	public StringDatatype(QName schemaType) {
		super(BuiltInType.STRING, schemaType);
		this.rcs = new XSDStringCharacterSet();
	}

	public boolean isValid(String value) {
		lastValue = value;
		return true;
	}

	public boolean isValid(Value value) {
		if (value instanceof StringValue) {
			lastValue = ((StringValue) value).toString();
			return true;
		} else {
			return false;
		}
	}

	// public Value getValue() {
	// return new StringValue(lastValue);
	// }

	public void writeValue(EncoderChannel valueChannel,
			StringEncoder stringEncoder, QName context) throws IOException {
		stringEncoder.writeValue(context, valueChannel, lastValue);
	}

	@Override
	public void writeValueRCS(RestrictedCharacterSetDatatype rcsEncoder,
			EncoderChannel valueChannel, StringEncoder stringEncoder,
			QName context) throws IOException {
		stringEncoder.writeValue(context, valueChannel, this.lastRCSValue);
	}

	public Value readValue(DecoderChannel valueChannel,
			StringDecoder stringDecoder, QName context) throws IOException {
		return stringDecoder.readValue(context, valueChannel);
	}

	@Override
	public Value readValueRCS(RestrictedCharacterSetDatatype rcsDecoder,
			DecoderChannel valueChannel, StringDecoder stringDecoder,
			QName context) throws IOException {
		return stringDecoder.readValue(context, valueChannel);
	}
}