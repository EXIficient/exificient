/*
 * Copyright (C) 2007-2010 Siemens AG
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

package com.siemens.ct.exi.util.datatype;

import com.siemens.ct.exi.util.xml.XMLWhitespace;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.3.20080718
 */

public class XSDBase64 {
	// static final byte UNKNOWN = -1;
	// static final char EQUAL_SIGN = '=';

	byte[] bytes;
	int length;

	private XSDBase64() {
	}

	public int getLength() {
		return length;
	}

	public byte[] getBytes() {
		return bytes;
	}

	public static XSDBase64 newInstance() {
		return new XSDBase64();
	}

	// Mapping table from 6-bit nibbles to Base64 characters.
	public static final char[] map1 = new char[64];
	static {
		int i = 0;
		for (char c = 'A'; c <= 'Z'; c++)
			map1[i++] = c;
		for (char c = 'a'; c <= 'z'; c++)
			map1[i++] = c;
		for (char c = '0'; c <= '9'; c++)
			map1[i++] = c;
		map1[i++] = '+';
		map1[i++] = '/';
	}

	// Mapping table from Base64 characters to 6-bit nibbles.
	private static final byte[] map2 = new byte[128];
	static {
		for (int i = 0; i < map2.length; i++)
			map2[i] = -1;
		for (int i = 0; i < 64; i++)
			map2[map1[i]] = (byte) i;
	}

	public boolean parse(String s) {
		return parse(s.toCharArray(), 0, s.length());
	}

	public boolean parse(char[] s, int start, int slength) {
		// walk over the whole array and overwrite possible whitespaces
		int currentPosition = start;
		int iLen = slength;

		for (int i = start; i < slength; i++) {
			if (XMLWhitespace.isWhiteSpace(s[i])) {
				// don't do anything except reducing length
				iLen--;
			} else {
				s[currentPosition++] = s[i];
			}
		}

		if (iLen % 4 != 0) {
			return false;
			// throw new XMLParsingException(
			// "Length of Base64 encoded input string is not a multiple of 4.");
		}

		while (iLen > 0 && s[start + iLen - 1] == '=') {
			iLen--;
		}

		length = (iLen * 3) / 4;

		//	create new byte array
		//	TODO can we re-use old byte array
		bytes = new byte[length];

		int ip = 0;
		int op = 0;

		while (ip < iLen) {
			// 0
			int i0 = s[start + ip++];
			int b0 = map2[i0];
			// 1
			int i1 = s[start + ip++];
			int b1 = map2[i1];
			// 2
			int i2 = ip < iLen ? s[start + ip++] : 'A';
			int b2 = map2[i2];
			// 3
			int i3 = ip < iLen ? s[start + ip++] : 'A';
			int b3 = map2[i3];
			// ok ?
			if (i0 > 127 || b0 < 0 || i1 > 127 || b1 < 0 || i2 > 127 || b2 < 0
					|| i3 > 127 || b3 < 0) {
				return false;
				// throw new XMLParsingException(
				// "Illegal character in Base64 encoded data.");
			}

			int o0 = (b0 << 2) | (b1 >>> 4);
			int o1 = ((b1 & 0xf) << 4) | (b2 >>> 2);
			int o2 = ((b2 & 3) << 6) | b3;
			bytes[op++] = (byte) o0;
			if (op < length) {
				bytes[op++] = (byte) o1;
			}
			if (op < length) {
				bytes[op++] = (byte) o2;
			}
		}

		return true;
	}

	/**
	 * Encodes a byte array into Base64 format. No blanks or line breaks are
	 * inserted.
	 * 
	 * @param in
	 *            an array containing the data bytes to be encoded.
	 * @return A character array with the Base64 encoded data.
	 */
	public static char[] encode(byte[] in) {
		int iLen = in.length;
		// output length without padding
		int oDataLen = (iLen * 4 + 2) / 3; 
		// output length including padding
		int oLen = ((iLen + 2) / 3) * 4;
		
		char[] out = new char[oLen];
		int ip = 0;
		int op = 0;
		while (ip < iLen) {
			int i0 = in[ip++] & 0xff;
			int i1 = ip < iLen ? in[ip++] & 0xff : 0;
			int i2 = ip < iLen ? in[ip++] & 0xff : 0;
			int o0 = i0 >>> 2;
			int o1 = ((i0 & 3) << 4) | (i1 >>> 4);
			int o2 = ((i1 & 0xf) << 2) | (i2 >>> 6);
			int o3 = i2 & 0x3F;
			out[op++] = map1[o0];
			out[op++] = map1[o1];
			out[op] = op < oDataLen ? map1[o2] : '=';
			op++;
			out[op] = op < oDataLen ? map1[o3] : '=';
			op++;
		}
		return out;
	}

}
