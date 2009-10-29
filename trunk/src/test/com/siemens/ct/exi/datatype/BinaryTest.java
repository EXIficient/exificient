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

package com.siemens.ct.exi.datatype;

import java.io.IOException;

import com.siemens.ct.exi.io.channel.DecoderChannel;
import com.siemens.ct.exi.io.channel.EncoderChannel;
import com.siemens.ct.exi.util.datatype.XSDBase64;

public class BinaryTest extends AbstractTestCase {

	public BinaryTest(String testName) {
		super(testName);
	}

	public void testHexBinaryAsString0FB7() throws IOException {
		String src = "0FB7";

		XSDBase64 b64 = XSDBase64.newInstance();
		boolean valid = b64.parse(src);
		assertTrue(valid);

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeBinary(b64.getBytes());
		bitEC.flush();
		char[] d1 = getBitDecoder().decodeBinaryAsCharacters();
		assertTrue(equals(d1, src));
		// Byte
		getByteEncoder().encodeBinary(b64.getBytes());
		assertTrue(equals(getByteDecoder().decodeBinaryAsCharacters(), src));
	}
	
	public void testHexBinaryAsString0FB7NotByteAligned() throws IOException {
		String src = "0FB7";

		XSDBase64 b64 = XSDBase64.newInstance();
		boolean valid = b64.parse(src);
		assertTrue(valid);

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeNBitUnsignedInteger(1, 2);
		bitEC.encodeBinary(b64.getBytes());
		bitEC.flush();
		DecoderChannel bd = getBitDecoder();
		bd.decodeNBitUnsignedInteger(2);
		char[] d1 = bd.decodeBinaryAsCharacters();
		assertTrue(equals(d1, src));
	}

	public void testBase64BinaryAsString0() throws IOException {
		String src = "ZHM=";

		XSDBase64 b64 = XSDBase64.newInstance();
		boolean valid = b64.parse(src);
		assertTrue(valid);

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeBinary(b64.getBytes());
		bitEC.flush();
		assertTrue(equals(getBitDecoder().decodeBinaryAsCharacters(), src));
		// Byte
		getByteEncoder().encodeBinary(b64.getBytes());
		assertTrue(equals(getByteDecoder().decodeBinaryAsCharacters(), src));
	}
	
	public void testBase64BinaryAsString0NotByteAligned() throws IOException {
		String src = "ZHM=";

		XSDBase64 b64 = XSDBase64.newInstance();
		boolean valid = b64.parse(src);
		assertTrue(valid);

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeNBitUnsignedInteger(34, 6);
		bitEC.encodeBinary(b64.getBytes());
		bitEC.flush();
		DecoderChannel bitDC = getBitDecoder();
		assertTrue(bitDC.decodeNBitUnsignedInteger(6) == 34);
		assertTrue(equals(bitDC.decodeBinaryAsCharacters(), src));
	}

	public void testBase64BinaryAsString1() throws IOException {

		String src = "RGFzIGlzIGphIGVpbiBmZXN0ZXIgQmxlZHNpbm4sIHdlaWwgVW1sYXV0ZSB3aWUg9iB1bmQg/CBtYWNoZW4gU2lubiwgd2llIGF1Y2ggZWluIHNjaGFyZmVzIN8u";
		// String src = "R0lGODlhAgSzzs7O3t7e";

		XSDBase64 b64 = XSDBase64.newInstance();
		boolean valid = b64.parse(src);
		assertTrue(valid);

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeBinary(b64.getBytes());
		bitEC.flush();
		char[] d1 = getBitDecoder().decodeBinaryAsCharacters();
		assertTrue(equals(d1, src));
		// Byte
		getByteEncoder().encodeBinary(b64.getBytes());
		assertTrue(equals(getByteDecoder().decodeBinaryAsCharacters(), src));
	}
	
	public void testBase64BinaryAsString1NotByteAligned() throws IOException {

		String src = "RGFzIGlzIGphIGVpbiBmZXN0ZXIgQmxlZHNpbm4sIHdlaWwgVW1sYXV0ZSB3aWUg9iB1bmQg/CBtYWNoZW4gU2lubiwgd2llIGF1Y2ggZWluIHNjaGFyZmVzIN8u";

		XSDBase64 b64 = XSDBase64.newInstance();
		boolean valid = b64.parse(src);
		assertTrue(valid);

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeNBitUnsignedInteger(0, 3);
//		bitEC.encodeNBitUnsignedInteger(4, 7);
		bitEC.encodeBinary(b64.getBytes());
		bitEC.flush();
		DecoderChannel bitDC = getBitDecoder();
		bitDC.decodeNBitUnsignedInteger(3);
//		bitDC.decodeNBitUnsignedInteger(7);
		char[] d1 = bitDC.decodeBinaryAsCharacters();
		assertTrue(equals(d1, src));
	}

	public void testBase64BinaryAsString2() throws IOException {
		String src = "SMOkdHRlbiBIw7x0ZSBlaW4gw58gaW0gTmFtZW4sIHfDpHJlbiBzaWUgbcO2Z2xpY2hlcndlaXNlIGtlaW5lIEjDvHRlIG1laHIsDQpzb25kZXJuIEjDvMOfZS4NCg==";

		XSDBase64 b64 = XSDBase64.newInstance();
		boolean valid = b64.parse(src);
		assertTrue(valid);

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeBinary(b64.getBytes());
		bitEC.flush();
		assertTrue(equals(getBitDecoder().decodeBinaryAsCharacters(), src));
		// Byte
		getByteEncoder().encodeBinary(b64.getBytes());
		assertTrue(equals(getByteDecoder().decodeBinaryAsCharacters(), src));
	}

	public void testBase64BinarySpaces1() throws IOException {
		String s1 = "ZH";
		String s2 = "M=";
		String src = s1 + "  " + s2 + "\n";

		XSDBase64 b64 = XSDBase64.newInstance();
		boolean valid = b64.parse(src);
		assertTrue(valid);

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeBinary(b64.getBytes());
		bitEC.flush();
		char[] d1 = getBitDecoder().decodeBinaryAsCharacters();
		assertTrue(equals(d1, s1 + s2));
		// Byte
		getByteEncoder().encodeBinary(b64.getBytes());
		assertTrue(equals(getByteDecoder().decodeBinaryAsCharacters(), s1 + s2));
	}

	public void testBase64BinarySpaces2() throws IOException {
		String s1 = "R0lGODdhWAK+ov////v7++fn58DAwI6Ojl5eXjExMQMDAyxYAr5AA/8Iutz+MMpJq7046827/2Ao";
		String sE = "\n \n ";
		String s2 = "jmRpnmiqPsKxvvBqCIxgxHg=";
		String src = s1 + sE + s2;

		XSDBase64 b64 = XSDBase64.newInstance();
		boolean valid = b64.parse(src);
		assertTrue(valid);

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeBinary(b64.getBytes());
		bitEC.flush();
		char[] d1 = getBitDecoder().decodeBinaryAsCharacters();
		assertTrue(equals(d1, s1 + s2));
		// Byte
		getByteEncoder().encodeBinary(b64.getBytes());
		assertTrue(equals(getByteDecoder().decodeBinaryAsCharacters(), s1 + s2));
	}

	public void testBinaryFailure() throws IOException {
		String src = "*invalid-bit*";

		XSDBase64 b64 = XSDBase64.newInstance();
		boolean valid = b64.parse(src);

		assertFalse(valid);
	}

}