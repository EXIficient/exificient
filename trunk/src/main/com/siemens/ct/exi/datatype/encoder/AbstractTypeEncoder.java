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

package com.siemens.ct.exi.datatype.encoder;

import java.io.IOException;

import com.siemens.ct.exi.Constants;
import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.core.CompileConfiguration;
import com.siemens.ct.exi.datatype.AbstractTypeCoder;
import com.siemens.ct.exi.datatype.stringtable.StringTableEncoder;
import com.siemens.ct.exi.datatype.stringtable.StringTableEncoderImpl;
import com.siemens.ct.exi.datatype.stringtable.StringTableEncoderImplNoGlobalValues;
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

public abstract class AbstractTypeEncoder extends AbstractTypeCoder implements
		TypeEncoder {
	
	// EXI string table(s)
	protected StringTableEncoder stringTable;

	public StringTableEncoder getStringTable() {
		return stringTable;
	}
	
	public void setStringTable(StringTableEncoder stringTable) {
		this.stringTable = stringTable;
	}

	// public AbstractTypeEncoder ( boolean isSchemaInformed )
	public AbstractTypeEncoder(EXIFactory exiFactory) {
		if (exiFactory.getCodingMode().usesRechanneling()) {
			stringTable = new StringTableEncoderImplNoGlobalValues(exiFactory
					.getGrammar().isSchemaInformed());
		} else {
			stringTable = new StringTableEncoderImpl(exiFactory.getGrammar()
					.isSchemaInformed());
		}

		// stringTable = exiFactory.getStringTableEncoder (
		// exiFactory.getGrammar ( ).isSchemaInformed( ) );
	}

	public void writeValueAsString(final EncoderChannel valueChannel,
			final String uri, final String localName, String value)
			throws IOException {
		if (CompileConfiguration.NOT_USE_STRING_TABLE) {
			writeStringAsMiss(valueChannel, uri, localName, value);
		} else {
			// default behaviour

			// local-value hit ?
			if (!writeStringAsLocalHit(valueChannel, uri, localName, value)) {
				// global-value hit ?
				if (!writeStringAsGlobalHit(valueChannel, value)) {
					// mhh, it is a string-table miss
					writeStringAsMiss(valueChannel, uri, localName, value);
				}
			}
		}
	}

	public boolean writeStringAsLocalHit(final EncoderChannel valueChannel,
			final String uri, final String localName, final String value)
			throws IOException {
		int localID = stringTable.getLocalValueID(uri, localName, value);

		if (localID == Constants.NOT_FOUND) {
			return false;
		} else {
			// found in local value partition
			// ==> string value is represented as zero (0) encoded as an
			// Unsigned Integer
			// followed by the compact identifier of the string value in the
			// "local" value partition
			valueChannel.encodeUnsignedInteger(0);
			int n = MethodsBag.getCodingLength(stringTable
					.getLocalValueTableSize(uri, localName));
			valueChannel.encodeNBitUnsignedInteger(localID, n);

			return true;
		}
	}

	public boolean writeStringAsGlobalHit(final EncoderChannel valueChannel,
			final String value) throws IOException {
		int globalID = stringTable.getGlobalValueID(value);

		if (globalID == Constants.NOT_FOUND) {
			return false;
		} else {
			// found in global value partition
			// ==> When a string value is not found in the global value
			// partition,
			// but not in the "local" value partition, the String value is
			// represented as one (1) encoded as an Unsigned Integer
			// followed by the compact identifier of the String value in the
			// global value partition.
			valueChannel.encodeUnsignedInteger(1);
			valueChannel.encodeNBitUnsignedInteger(globalID, MethodsBag
					.getCodingLength(stringTable.getGlobalValueTableSize()));

			return true;
		}
	}

	public void writeStringAsMiss(final EncoderChannel valueChannel,
			final String uri, final String localName, final String value)
			throws IOException {
		// not found in global value (and local value) partition
		// ==> string literal is encoded as a String with the length
		// incremented by two.
		valueChannel.encodeUnsignedInteger(value.length() + 2);
		valueChannel.encodeStringOnly(value);
		// After encoding the string value, it is added to both the
		// associated "local" value string table partition and the
		// global value string table partition.
		stringTable.addLocalValue(uri, localName, value);
		stringTable.addGlobalValue(value);
	}

	public void finish() throws IOException {
	}
}
