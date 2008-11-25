/*
 * Copyright (C) 2007, 2008 Siemens AG
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

package com.siemens.ct.exi.datatype.encoder;

import java.io.IOException;

import org.apache.xerces.xs.StringList;

import com.siemens.ct.exi.datatype.Datatype;
import com.siemens.ct.exi.datatype.DatatypeEnumeration;
import com.siemens.ct.exi.io.channel.EncoderChannel;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.2.20081117
 */

public class EnumerationDatatypeEncoder extends AbstractDatatypeEncoder
		implements DatatypeEncoder {
	private DatatypeEnumeration lastDatatypeEnumeration;
	private StringList lastEnumValues;
	private int lastOrdinalPosition;

	public EnumerationDatatypeEncoder(TypeEncoder typeEncoder) {
		super(typeEncoder);
	}

	public boolean isValid(Datatype datatype, String value) {
		lastDatatypeEnumeration = ((DatatypeEnumeration) datatype);
		lastEnumValues = lastDatatypeEnumeration.getEnumerationValues();

		lastOrdinalPosition = -1;
		int index = 0;
		while (index < lastEnumValues.getLength()) {
			if (lastEnumValues.item(index).equals(value)) {
				lastOrdinalPosition = index;
				return true;
			}
			index++;
		}

		return false;
	}

	public void writeValue(EncoderChannel valueChannel, String uri,
			String localName) throws IOException {
		valueChannel.encodeNBitUnsignedInteger(lastOrdinalPosition,
				lastDatatypeEnumeration.getCodingLength());
	}
}
