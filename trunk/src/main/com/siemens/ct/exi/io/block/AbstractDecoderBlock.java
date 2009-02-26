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

package com.siemens.ct.exi.io.block;

import java.io.IOException;
import java.io.InputStream;

import com.siemens.ct.exi.datatype.Datatype;
import com.siemens.ct.exi.datatype.decoder.TypeDecoder;
import com.siemens.ct.exi.datatype.stringtable.StringTableDecoder;
import com.siemens.ct.exi.io.channel.DecoderChannel;
import com.siemens.ct.exi.util.MethodsBag;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.2.20081105
 */

public abstract class AbstractDecoderBlock implements DecoderBlock {
	protected InputStream inputStream;

	protected TypeDecoder typeDecoder;

	public AbstractDecoderBlock(InputStream is, TypeDecoder typeDecoder)
			throws IOException {
		inputStream = is;
		this.typeDecoder = typeDecoder;

		init();
	}

	protected abstract void init() throws IOException;

	protected abstract DecoderChannel getStructureChannel();

	protected abstract DecoderChannel getValueChannel(String namespaceURI,
			String localName) throws IOException;

	public int readEventCode(int codeLength) throws IOException {
		return getStructureChannel().decodeNBitUnsignedInteger(codeLength);
	}

	public String readString() throws IOException {
		return getStructureChannel().decodeString();
	}

	public String readUri() throws IOException {
		DecoderChannel structure = getStructureChannel();
		StringTableDecoder stringTable = typeDecoder.getStringTable();

		String uri;

		int mUri = stringTable.getURITableSize(); // number of entries
		int nUri = MethodsBag.getCodingLength(mUri + 1); // n-bit
		int uriID = structure.decodeNBitUnsignedInteger(nUri);
		if (uriID == 0) {
			// string value was not found
			// ==> zero (0) as an n-nit unsigned integer
			// followed by uri encoded as string
			uri = structure.decodeString();
			// after encoding string value is added to table
			stringTable.addURI(uri);
		} else {
			// string value found
			// ==> value(i+1) is encoded as n-bit unsigned integer
			uri = stringTable.getURIValue(uriID - 1);
		}

		return uri;
	}

	public String readLocalName(String uri) throws IOException {
		DecoderChannel structure = getStructureChannel();
		StringTableDecoder stringTable = typeDecoder.getStringTable();

		String localName;
		int length = structure.decodeUnsignedInteger();

		if (length > 0) {
			// string value was not found in local partition
			// ==> string literal is encoded as a String
			// with the length of the string incremented by one
			length--;
			localName = structure.decodeStringOnly(length);
			// After encoding the string value, it is added to the string table
			// partition and assigned the next available compact identifier.
			stringTable.addLocalName(uri, localName);
		} else {
			// string value found in local partition
			// ==> string value is represented as zero (0) encoded as an
			// Unsigned Integer
			// followed by an the compact identifier of the string value as an
			// n-bit unsigned integer
			// n is log2 m and m is the number of entries in the string table
			// partition
			int n = MethodsBag.getCodingLength(stringTable
					.getLocalNameTableSize(uri));
			int localNameID = structure.decodeNBitUnsignedInteger(n);
			localName = stringTable.getLocalNameValue(uri, localNameID);
		}

		return localName;
	}

	public String readPrefix(String uri) throws IOException {
		DecoderChannel structure = getStructureChannel();
		StringTableDecoder stringTable = typeDecoder.getStringTable();

		String prefix;

		int mPfx = stringTable.getPrefixTableSize(uri); // number of entries
		int nPfx = MethodsBag.getCodingLength(mPfx + 1); // n-bit
		int pfxID = structure.decodeNBitUnsignedInteger(nPfx);
		if (pfxID == 0) {
			// string value was not found
			// ==> zero (0) as an n-nit unsigned integer
			// followed by pfx encoded as string
			prefix = structure.decodeString();
			// after decoding pfx value is added to table
			stringTable.addPrefix(uri, prefix);
		} else {
			// string value found
			// ==> value(i+1) is encoded as n-bit unsigned integer
			prefix = stringTable.getPrefixValue(uri, pfxID - 1);
		}

		return prefix;
	}

	public boolean readBoolean() throws IOException {
		return getStructureChannel().decodeBoolean();
	}

	public String readTypedValidValue(Datatype datatype,
			final String namespaceURI, final String localName)
			throws IOException {
		return typeDecoder.readTypeValidValue(datatype, getValueChannel(
				namespaceURI, localName), namespaceURI, localName);
	}

	public String readValueAsString(String namespaceURI, String localName)
			throws IOException {
		return typeDecoder.readValueAsString(getValueChannel(namespaceURI,
				localName), namespaceURI, localName);
	}

	public void skipToNextByteBoundary() throws IOException {
	}
	
	public TypeDecoder getTypeDecoder() {
		return this.typeDecoder;
	}
}
