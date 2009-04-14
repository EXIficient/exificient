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

import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.datatype.AbstractTypeCoder;
import com.siemens.ct.exi.datatype.stringtable.StringTableDecoder;
import com.siemens.ct.exi.datatype.stringtable.StringTableDecoderImpl;
import com.siemens.ct.exi.io.channel.DecoderChannel;
import com.siemens.ct.exi.util.MethodsBag;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.3.20081117
 */

public abstract class AbstractTypeDecoder extends AbstractTypeCoder implements
		TypeDecoder {
	// EXI string table(s)
	protected StringTableDecoder stringTable;

	public AbstractTypeDecoder(EXIFactory exiFactory) {
		stringTable = new StringTableDecoderImpl(exiFactory.getGrammar()
				.isSchemaInformed());
	}

	public StringTableDecoder getStringTable() {
		return stringTable;
	}

	public void setStringTable(StringTableDecoder stringTable) {
		this.stringTable = stringTable;
	}

	public String readValueAsString(DecoderChannel dc,
			final String namespaceURI, final String localName)
			throws IOException {
		String value;

		int i = dc.decodeUnsignedInteger();

		if (i == 0) {
			// local value partition
			value = readStringAsLocalHit(dc, namespaceURI, localName);
		} else if (i == 1) {
			// found in global value partition
			value = readStringAsGlobalHit(dc);
		} else {
			// not found in global value (and local value) partition
			// ==> string literal is encoded as a String with the length
			// incremented by two.
			value = this.readStringAsMiss(dc, namespaceURI, localName, i - 2);
		}

		assert (value != null);

		return value;
	}

	public String readStringAsLocalHit(DecoderChannel dc,
			final String namespaceURI, final String localName)
			throws IOException {
		int n = MethodsBag.getCodingLength(stringTable.getLocalValueTableSize(
				namespaceURI, localName));
		int localID = dc.decodeNBitUnsignedInteger(n);

		return stringTable.getLocalValue(namespaceURI, localName, localID);
	}

	public String readStringAsGlobalHit(DecoderChannel dc) throws IOException {
		int n = MethodsBag.getCodingLength(stringTable
				.getGlobalValueTableSize());
		int globalID = dc.decodeNBitUnsignedInteger(n);

		return stringTable.getGlobalValue(globalID);
	}

	public String readStringAsMiss(DecoderChannel dc,
			final String namespaceURI, final String localName, final int slen)
			throws IOException {
		String value = dc.decodeStringOnly(slen);
		// After encoding the string value, it is added to both the
		// associated "local" value string table partition and the global value
		// string table partition.
		stringTable.addLocalValue(namespaceURI, localName, value);
		stringTable.addGlobalValue(value);

		return value;
	}

}
