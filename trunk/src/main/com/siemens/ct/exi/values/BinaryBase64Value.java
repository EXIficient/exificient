/*
 * Copyright (C) 2007-2011 Siemens AG
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

package com.siemens.ct.exi.values;

import org.apache.xerces.impl.dv.util.Base64;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.6
 */
// re-uses code from org.apache.xerces.impl.dv.util.Base64Bin;
public class BinaryBase64Value extends AbstractBinaryValue {

	private static final long serialVersionUID = -2690177084175673837L;

	private int fewerThan24bits;
	private int numberTriplets;
	private int numberQuartet;

	static private final int LOOKUPLENGTH = 64;
	static private final int EIGHTBIT = 8;
	static private final int SIXTEENBIT = 16;
	static private final int TWENTYFOURBITGROUP = 24;
	static private final int SIGN = -128;
	static private final char PAD = '=';
	static final private char[] lookUpBase64Alphabet = new char[LOOKUPLENGTH];

	static {
		for (int i = 0; i <= 25; i++)
			lookUpBase64Alphabet[i] = (char) ('A' + i);

		for (int i = 26, j = 0; i <= 51; i++, j++)
			lookUpBase64Alphabet[i] = (char) ('a' + j);

		for (int i = 52, j = 0; i <= 61; i++, j++)
			lookUpBase64Alphabet[i] = (char) ('0' + j);
		lookUpBase64Alphabet[62] = (char) '+';
		lookUpBase64Alphabet[63] = (char) '/';
	}

	public BinaryBase64Value(byte[] bytes) {
		super(ValueType.BINARY_BASE64, bytes);
	}

	public static BinaryBase64Value parse(String val) {
		byte[] bytes = Base64.decode(val);
		if (bytes == null) {
			return null;
		} else {
			return new BinaryBase64Value(bytes);
		}
	}

	public int getCharactersLength() {
		if (slen == -1) {
			int lengthDataBits = bytes.length * EIGHTBIT;
			if (lengthDataBits == 0) {
				slen = 0;
			} else {
				fewerThan24bits = lengthDataBits % TWENTYFOURBITGROUP;
				numberTriplets = lengthDataBits / TWENTYFOURBITGROUP;
				numberQuartet = fewerThan24bits != 0 ? numberTriplets + 1
						: numberTriplets;

				slen = numberQuartet * 4;
			}
		}
		return slen;
	}

	public char[] toCharacters(char[] cbuffer, int offset) {
		getCharactersLength();

		byte k = 0, l = 0, b1 = 0, b2 = 0, b3 = 0;

		int encodedIndex = 0;
		int dataIndex = 0;

		for (int i = 0; i < numberTriplets; i++) {
			b1 = bytes[dataIndex++];
			b2 = bytes[dataIndex++];
			b3 = bytes[dataIndex++];

			l = (byte) (b2 & 0x0f);
			k = (byte) (b1 & 0x03);

			byte val1 = ((b1 & SIGN) == 0) ? (byte) (b1 >> 2)
					: (byte) ((b1) >> 2 ^ 0xc0);

			byte val2 = ((b2 & SIGN) == 0) ? (byte) (b2 >> 4)
					: (byte) ((b2) >> 4 ^ 0xf0);
			byte val3 = ((b3 & SIGN) == 0) ? (byte) (b3 >> 6)
					: (byte) ((b3) >> 6 ^ 0xfc);

			cbuffer[offset + encodedIndex++] = lookUpBase64Alphabet[val1];
			cbuffer[offset + encodedIndex++] = lookUpBase64Alphabet[val2
					| (k << 4)];
			cbuffer[offset + encodedIndex++] = lookUpBase64Alphabet[(l << 2)
					| val3];
			cbuffer[offset + encodedIndex++] = lookUpBase64Alphabet[b3 & 0x3f];
		}

		// form integral number of 6-bit groups
		if (fewerThan24bits == EIGHTBIT) {
			b1 = bytes[dataIndex];
			k = (byte) (b1 & 0x03);

			byte val1 = ((b1 & SIGN) == 0) ? (byte) (b1 >> 2)
					: (byte) ((b1) >> 2 ^ 0xc0);
			cbuffer[offset + encodedIndex++] = lookUpBase64Alphabet[val1];
			cbuffer[offset + encodedIndex++] = lookUpBase64Alphabet[k << 4];
			cbuffer[offset + encodedIndex++] = PAD;
			cbuffer[offset + encodedIndex++] = PAD;
		} else if (fewerThan24bits == SIXTEENBIT) {
			b1 = bytes[dataIndex];
			b2 = bytes[dataIndex + 1];
			l = (byte) (b2 & 0x0f);
			k = (byte) (b1 & 0x03);

			byte val1 = ((b1 & SIGN) == 0) ? (byte) (b1 >> 2)
					: (byte) ((b1) >> 2 ^ 0xc0);
			byte val2 = ((b2 & SIGN) == 0) ? (byte) (b2 >> 4)
					: (byte) ((b2) >> 4 ^ 0xf0);

			cbuffer[offset + encodedIndex++] = lookUpBase64Alphabet[val1];
			cbuffer[offset + encodedIndex++] = lookUpBase64Alphabet[val2
					| (k << 4)];
			cbuffer[offset + encodedIndex++] = lookUpBase64Alphabet[l << 2];
			cbuffer[offset + encodedIndex++] = PAD;
		}

		return cbuffer;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof BinaryBase64Value) {
			return _equals(((BinaryBase64Value) o).bytes);
		} else if (o instanceof String || o instanceof StringValue) {
			BinaryBase64Value b = BinaryBase64Value.parse(o.toString());
			if (b == null) {
				return false;
			} else {
				return _equals(b.toBytes());
			}
		} else {
			return false;
		}
	}

}
