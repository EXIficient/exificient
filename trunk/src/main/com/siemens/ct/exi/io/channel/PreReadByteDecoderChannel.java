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

package com.siemens.ct.exi.io.channel;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

import com.siemens.ct.exi.datatype.Datatype;
import com.siemens.ct.exi.datatype.decoder.TypeDecoder;
import com.siemens.ct.exi.util.ExpandedName;
import com.siemens.ct.exi.util.datatype.DatetimeType;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.1.20081111
 */

public class PreReadByteDecoderChannel implements DecoderChannel {
	private String[] decodedValues;
	private int currentValueIndex = 0;

	public PreReadByteDecoderChannel(TypeDecoder decoder,
			ByteDecoderChannel bdc, ExpandedName qname,
			List<Datatype> datatypes, int occurrences) throws IOException {
		assert (datatypes.size() == occurrences);

		decodedValues = new String[occurrences];

		for (int i = 0; i < occurrences; i++) {
			decodedValues[i] = (decoder.readTypeValidValue(datatypes.get(i),
					bdc, qname.getNamespaceURI(), qname.getLocalName()));
		}
	}

	protected String getNextValue() {
		return decodedValues[currentValueIndex++];
	}

	public int decode() throws IOException {
		// TODO
		throw new RuntimeException();
	}

	public byte[] decodeBinary() throws IOException {
		// TODO
		throw new RuntimeException();
	}

	public String decodeBinaryAsString() throws IOException {
		return getNextValue();
	}

	public boolean decodeBoolean() throws IOException {
		// TODO
		throw new RuntimeException();
	}

	public String decodeBooleanAsString() throws IOException {
		return getNextValue();
	}

	public Calendar decodeDateTime(DatetimeType type) throws IOException {
		// TODO
		throw new RuntimeException();
	}

	public String decodeDateTimeAsString(DatetimeType type) throws IOException {
		return getNextValue();
	}

	public BigDecimal decodeDecimal() throws IOException {
		// TODO
		throw new RuntimeException();
	}

	public String decodeDecimalAsString() throws IOException {
		return getNextValue();
	}

	public int decodeEventCode(int characteristics) throws IOException {
		// TODO
		throw new RuntimeException();
	}

	public float decodeFloat() throws IOException {
		// TODO
		throw new RuntimeException();
	}

	public String decodeFloatAsString() throws IOException {
		return getNextValue();
	}

	public int decodeInteger() throws IOException {
		// TODO
		throw new RuntimeException();
	}

	public String decodeIntegerAsString() throws IOException {
		return getNextValue();
	}

	public int decodeNBitUnsignedInteger(int n) throws IOException {
		// TODO
		throw new RuntimeException();
	}

	/**
	 * Decodes and returns an n-bit unsigned integer as string.
	 */
	public String decodeNBitUnsignedIntegerAsString(int n) throws IOException {
		return getNextValue();
	}

	public String decodeString() throws IOException {
		return getNextValue();
	}

	public String decodeStringOnly(int length) throws IOException {
		// TODO
		throw new RuntimeException();
	}

	public int decodeUnsignedInteger() throws IOException {
		// TODO
		throw new RuntimeException();
	}

	public long decodeUnsignedIntegerAsLong() throws IOException {
		// TODO
		throw new RuntimeException();
	}

	public String decodeUnsignedIntegerAsString() throws IOException {
		return getNextValue();
	}

}
