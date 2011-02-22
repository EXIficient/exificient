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

import org.apache.xerces.impl.dv.util.HexBin;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.6
 */

// re-uses code from org.apache.xerces.impl.dv.util.HexBin;
public class BinaryHexValue extends AbstractBinaryValue {

	private static final long serialVersionUID = 4914135099644891193L;

	static private final int LOOKUPLENGTH = 16;
	static final private char[] lookUpHexAlphabet = new char[LOOKUPLENGTH];

	private int lengthData;

	static {
		for (int i = 0; i < 10; i++) {
			lookUpHexAlphabet[i] = (char) ('0' + i);
		}
		for (int i = 10; i <= 15; i++) {
			lookUpHexAlphabet[i] = (char) ('A' + i - 10);
		}
	}

	public BinaryHexValue(byte[] bytes) {
		super(ValueType.BINARY_HEX, bytes);
	}

	public static BinaryHexValue parse(String val) {
		byte[] bytes = HexBin.decode(val);
		if (bytes == null) {
			return null;
		} else {
			return new BinaryHexValue(bytes);
		}
	}

	public int getCharactersLength() {
		if (slen == -1) {
			lengthData = bytes.length;
			slen = lengthData * 2;
		}
		return slen;
	}
	
	public char[] toCharacters(char[] cbuffer, int offset) {
		getCharactersLength();
		
		int temp;
		for (int i = 0; i < lengthData; i++) {
			temp = bytes[i];
			if (temp < 0)
				temp += 256;
			cbuffer[offset + i * 2] = lookUpHexAlphabet[temp >> 4];
			cbuffer[offset + i * 2 + 1] = lookUpHexAlphabet[temp & 0xf];
		}

		return cbuffer;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof BinaryHexValue) {
			return _equals(((BinaryHexValue) o).bytes);
		} else if (o instanceof String || o instanceof StringValue) {
			BinaryHexValue b = BinaryHexValue.parse(o.toString());
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
