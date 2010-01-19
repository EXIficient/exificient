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
import com.siemens.ct.exi.util.datatype.XSDBase64;
import com.siemens.ct.exi.values.Value;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.4.20081112
 */

public abstract class AbstractBinaryDatatype extends AbstractDatatype {

	protected XSDBase64 xsdBase64 = XSDBase64.newInstance();

	public AbstractBinaryDatatype(QName datatypeIdentifier,
			BuiltInType binaryType) {
		super(binaryType, datatypeIdentifier);
		assert(binaryType == BuiltInType.BINARY_BASE64 || binaryType == BuiltInType.BINARY_HEX);
	}

	/*
	 * Encoder
	 */
	public boolean isValid(String value) {
		return xsdBase64.parse(value.toCharArray(), 0, value.length());
	}

	public void writeValue(EncoderChannel valueChannel, StringEncoder stringEncoder, QName context)
			throws IOException {
		valueChannel.encodeBinary(xsdBase64.getBytes());
	}
	

	/*
	 * Decoder
	 */

	public Value readValue(DecoderChannel valueChannel,
			StringDecoder stringDecoder, QName context)
			throws IOException {
		return valueChannel.decodeBinary();
	}
}