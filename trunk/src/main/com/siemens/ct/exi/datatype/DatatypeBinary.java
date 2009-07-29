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

import com.siemens.ct.exi.core.NameContext;
import com.siemens.ct.exi.io.channel.EncoderChannel;
import com.siemens.ct.exi.util.ExpandedName;
import com.siemens.ct.exi.util.datatype.XSDBase64;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.3.20081112
 */

public class DatatypeBinary extends AbstractDatatype {

	private XSDBase64 xsdBase64 = XSDBase64.newInstance();

	public DatatypeBinary(ExpandedName datatypeIdentifier,
			BuiltInType binaryType) {
		super(binaryType, datatypeIdentifier);

		if (!(binaryType == BuiltInType.BINARY_BASE64 || binaryType == BuiltInType.BINARY_HEX)) {
			throw new RuntimeException("Illegal type '" + binaryType
					+ "' for DatatypeBinary");
		}
	}

	/*
	 * Encoder
	 */
	public boolean isValid(Datatype datatype, String value) {
		return xsdBase64.parse(value.toCharArray(), 0, value.length());
	}

	public void writeValue(NameContext context, EncoderChannel valueChannel)
			throws IOException {
		valueChannel.encodeBinary(xsdBase64.getBytes());
	}

	/*
	 * Decoder
	 */
}