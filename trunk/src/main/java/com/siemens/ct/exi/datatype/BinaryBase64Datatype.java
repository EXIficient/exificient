/*
 * Copyright (C) 2007-2015 Siemens AG
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
import com.siemens.ct.exi.io.channel.DecoderChannel;
import com.siemens.ct.exi.types.BuiltInType;
import com.siemens.ct.exi.values.BinaryBase64Value;
import com.siemens.ct.exi.values.Value;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9.4
 */

public class BinaryBase64Datatype extends AbstractBinaryDatatype {

	private static final long serialVersionUID = 7266684611493396188L;
	
	public BinaryBase64Datatype(QNameContext schemaType) {
		super(BuiltInType.BINARY_BASE64, schemaType);
	}
	
	public DatatypeID getDatatypeID() {
		return DatatypeID.exi_base64Binary;
	}

	protected boolean isValidString(String value) {
		BinaryBase64Value bv = BinaryBase64Value.parse(value);
		if (bv == null) {
			return false;
		} else {
			bytes = bv.toBytes();
			return true;
		}
	}

	public Value readValue(QNameContext qnContext, DecoderChannel valueChannel,
			StringDecoder stringDecoder) throws IOException {
		return new BinaryBase64Value(valueChannel.decodeBinary());
	}

}