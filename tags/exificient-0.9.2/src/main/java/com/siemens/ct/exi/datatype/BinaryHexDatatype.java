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
import com.siemens.ct.exi.io.channel.DecoderChannel;
import com.siemens.ct.exi.types.BuiltInType;
import com.siemens.ct.exi.values.BinaryHexValue;
import com.siemens.ct.exi.values.Value;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9.2
 */

public class BinaryHexDatatype extends AbstractBinaryDatatype {

	private static final long serialVersionUID = -1129597802269768531L;

	public BinaryHexDatatype(QNameContext schemaType) {
		super(BuiltInType.BINARY_HEX, schemaType);
	}
	
	public DatatypeID getDatatypeID() {
		return DatatypeID.exi_hexBinary;
	}

	protected boolean isValidString(String value) {
		value = value.trim();
		BinaryHexValue bv = BinaryHexValue.parse(value);
		if (bv == null) {
			return false;
		} else {
			bytes = bv.toBytes();
			return true;
		}
	}

	public Value readValue(QNameContext qnContext, DecoderChannel valueChannel,
			StringDecoder stringDecoder) throws IOException {
		return new BinaryHexValue(valueChannel.decodeBinary());
	}

}