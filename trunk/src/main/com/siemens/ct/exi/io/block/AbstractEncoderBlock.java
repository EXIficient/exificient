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
import java.io.OutputStream;

import com.siemens.ct.exi.Constants;
import com.siemens.ct.exi.datatype.Datatype;
import com.siemens.ct.exi.datatype.encoder.TypeEncoder;
import com.siemens.ct.exi.datatype.stringtable.StringTableEncoder;
import com.siemens.ct.exi.io.channel.EncoderChannel;
import com.siemens.ct.exi.util.MethodsBag;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.2.20080718
 */

public abstract class AbstractEncoderBlock implements EncoderBlock {
	private int blockValues = 0;
	protected OutputStream outputStream;

	// TypeEncoder
	TypeEncoder typeEncoder;

	public AbstractEncoderBlock(OutputStream outputStream,
			TypeEncoder typeEncoder) {
		this.outputStream = outputStream;
		this.typeEncoder = typeEncoder;

		init();
	}

	protected abstract void init();

	protected int getNumberOfBlockValues() {
		return blockValues;
	}

	protected abstract EncoderChannel getStructureChannel();

	protected abstract EncoderChannel getValueChannel(String uri,
			String localName);

	/*
	 * Structure Channel
	 */
	public void writeEventCode(int eventCode, int codeLength)
			throws IOException {
		getStructureChannel().encodeNBitUnsignedInteger(eventCode, codeLength);
	}

	public void writeString(String text) throws IOException {
		getStructureChannel().encodeString(text);
	}

	public void writeUri(String uri) throws IOException {
		EncoderChannel structure = getStructureChannel();
		StringTableEncoder stringTable = typeEncoder.getStringTable();

		int uriID = stringTable.getURIID(uri);
		int nUri = MethodsBag
				.getCodingLength(stringTable.getURITableSize() + 1); // numberEntries+1
																		// -->
																		// n-bit
		if (uriID == Constants.NOT_FOUND) {
			// string value was not found
			// ==> zero (0) as an n-nit unsigned integer
			// followed by uri encoded as string
			structure.encodeNBitUnsignedInteger(0, nUri);
			structure.encodeString(uri);
			// after encoding string value is added to table
			stringTable.addURI(uri);
		} else {
			// string value found
			// ==> value(i+1) is encoded as n-bit unsigned integer
			structure.encodeNBitUnsignedInteger(uriID + 1, nUri);
		}
	}

	public void writeLocalName(String localName, String uri) throws IOException {
		EncoderChannel structure = getStructureChannel();
		StringTableEncoder stringTable = typeEncoder.getStringTable();

		int localNameID = stringTable.getLocalNameID(uri, localName);

		if (localNameID == Constants.NOT_FOUND) {
			// string value was not found in local partition
			// ==> string literal is encoded as a String
			// with the length of the string incremented by one
			structure.encodeUnsignedInteger(localName.length() + 1);
			structure.encodeStringOnly(localName);
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
			structure.encodeUnsignedInteger(0);
			int n = MethodsBag.getCodingLength(stringTable
					.getLocalNameTableSize(uri));
			structure.encodeNBitUnsignedInteger(localNameID, n);
		}
	}

	public void writePrefix(String prefix, String uri) throws IOException {
		EncoderChannel structure = getStructureChannel();
		StringTableEncoder stringTable = typeEncoder.getStringTable();

		int pfxID = stringTable.getPrefixID(uri, prefix);
		int mPfx = stringTable.getPrefixTableSize(uri); // number of entries
		int nPfx = MethodsBag.getCodingLength(mPfx + 1); // n-bit
		if (pfxID == Constants.NOT_FOUND) {
			// string value was not found
			// ==> zero (0) as an n-bit unsigned integer
			// followed by pfx encoded as string
			structure.encodeNBitUnsignedInteger(0, nPfx);
			structure.encodeString(prefix);
			// after encoding string value is added to table
			stringTable.addPrefix(uri, prefix);
		} else {
			// string value found
			// ==> value(i+1) is encoded as n-bit unsigned integer
			structure.encodeNBitUnsignedInteger(pfxID + 1, nPfx);
		}
	}

	public void writeBoolean(boolean b) throws IOException {
		getStructureChannel().encodeBoolean(b);
	}

	/*
	 * Value Channel
	 */
	public boolean isTypeValid(Datatype datatype, String value) {
		return typeEncoder.isTypeValid(datatype, value);
	}

	public void writeTypeValidValue(final String uri, final String localName)
			throws IOException {
		blockValues++;
		EncoderChannel valueChannel = getValueChannel(uri, localName);
		valueChannel.incrementValues();
		typeEncoder.writeTypeValidValue(valueChannel, uri, localName);
		// typeEncoder.writeTypeValidValue ( getValueChannel ( uri, localName ),
		// uri, localName );
	}

	public void writeValueAsString(final String uri, final String localName,
			final String value) throws IOException {
		blockValues++;
		EncoderChannel valueChannel = getValueChannel(uri, localName);
		valueChannel.incrementValues();
		typeEncoder.writeValueAsString(valueChannel, uri, localName, value);
		// typeEncoder.writeValueAsString ( getValueChannel ( uri, localName ),
		// uri, localName, value );
	}

	public void close() throws IOException {
		outputStream.close();
	}
}
