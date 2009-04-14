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

package com.siemens.ct.exi.datatype.decoder;

import java.io.IOException;

import com.siemens.ct.exi.datatype.Datatype;
import com.siemens.ct.exi.datatype.RestrictedCharacterSet;
import com.siemens.ct.exi.datatype.stringtable.StringTableDecoder;
import com.siemens.ct.exi.exceptions.UnknownElementException;
import com.siemens.ct.exi.io.channel.DecoderChannel;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.3.20080718
 */

public class RestrictedCharacterSetDatatypeDecoder extends
		AbstractDatatypeDecoder {
	protected RestrictedCharacterSet rcs;

	public RestrictedCharacterSetDatatypeDecoder(RestrictedCharacterSet rcs) {
		super();

		this.rcs = rcs;
	}

	public String decodeValue(TypeDecoder decoder, Datatype datatype,
			DecoderChannel dc, String namespaceURI, String localName)
			throws IOException {
		String value;

		int i = dc.decodeUnsignedInteger();

		if (i == 0) {
			// local value partition
			value = decoder.readStringAsLocalHit(dc, namespaceURI, localName);
		} else if (i == 1) {
			// found in global value partition
			value = decoder.readStringAsGlobalHit(dc);
		} else {
			// not found in global value (and local value) partition
			// ==> restricted character string literal is encoded as a String
			// with the length
			// incremented by two.
			int slen = i - 2;

			// number of bits
			int numberOfBits = rcs.getCodingLength();
			int size = rcs.size();

			StringBuilder sb = new StringBuilder();
			int code;

			try {
				for (int k = 0; k < slen; k++) {
					if ((code = dc.decodeNBitUnsignedInteger(numberOfBits)) == size) {
						// UCS code point of the character
						// TODO UTF-16 surrogate pair?
						sb.append((char) dc.decodeUnsignedInteger());
					} else {
						sb.append(rcs.getCharacter(code));
					}
				}
			} catch (UnknownElementException e) {
				throw new IOException(e.getMessage());
				// TODO Java 1.5 does not support Throwable as parameter
				// throw new IOException(e);
			}

			value = sb.toString();

			// After encoding the string value, it is added to both the
			// associated "local" value string table partition and the global
			// value
			// string table partition.
			StringTableDecoder stringTable = decoder.getStringTable();
			stringTable.addLocalValue(namespaceURI, localName, value);
			stringTable.addGlobalValue(value);
		}

		return value;
	}
}