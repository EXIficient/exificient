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

package com.siemens.ct.exi.datatype;

import java.io.IOException;
import java.math.BigInteger;

import com.siemens.ct.exi.io.channel.DecoderChannel;
import com.siemens.ct.exi.io.channel.EncoderChannel;

public class IntegerTest extends AbstractTestCase {

	public IntegerTest(String testName) {
		super(testName);
	}

	public void testInteger0() throws IOException {
		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeInteger(0);
		bitEC.flush();
		assertTrue(getBitDecoder().decodeIntegerValue().toInteger() == 0);
		// Byte
		getByteEncoder().encodeInteger(0);
		assertTrue(getByteDecoder().decodeIntegerValue().toInteger() == 0);
	}

	public void testInteger1() throws IOException {
		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeInteger(1);
		bitEC.flush();
		assertTrue(getBitDecoder().decodeIntegerValue().toInteger() == 1);
		// Byte
		getByteEncoder().encodeInteger(1);
		assertTrue(getByteDecoder().decodeIntegerValue().toInteger() == 1);
	}

	public void testIntegerMaxNegativeInteger() throws IOException {
		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeInteger(Integer.MIN_VALUE);
		bitEC.flush();
		assertTrue(getBitDecoder().decodeIntegerValue().toInteger() == Integer.MIN_VALUE);
		// Byte
		getByteEncoder().encodeInteger(Integer.MIN_VALUE);
		assertTrue(getByteDecoder().decodeIntegerValue().toInteger() == Integer.MIN_VALUE);
	}

	public void testInteger0S() throws IOException {
		String s = "0";

		int xmlInteger = Integer.parseInt(s);

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeInteger(xmlInteger);
		bitEC.flush();
		assertTrue(s.equals(getBitDecoder().decodeIntegerValue().toString()));
		// Byte
		getByteEncoder().encodeInteger(xmlInteger);
		assertTrue(s.equals(getByteDecoder().decodeIntegerValue().toString()));
	}

	public void testInteger1S() throws IOException {
		String s = "1";

		int xmlInteger = Integer.parseInt(s);

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeInteger(xmlInteger);
		bitEC.flush();
		assertTrue(s.equals(getBitDecoder().decodeIntegerValue().toString()));
		// Byte
		getByteEncoder().encodeInteger(xmlInteger);
		assertTrue(s.equals(getByteDecoder().decodeIntegerValue().toString()));
	}

	public void testIntegerM128S() throws IOException {
		String s = "-128";

		int xmlInteger = Integer.parseInt(s);

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeInteger(xmlInteger);
		bitEC.flush();
		assertTrue(s.equals(getBitDecoder().decodeIntegerValue().toString()));
		// Byte
		getByteEncoder().encodeInteger(xmlInteger);
		assertTrue(s.equals(getByteDecoder().decodeIntegerValue().toString()));
	}

	// public void testIntegerSpace35S() throws IOException
	// {
	// String s = "35   ";
	// String sDec = "35";
	//    	
	// XMLInteger xmlInteger = XMLInteger.newInstance();
	// xmlInteger.parse ( s );
	//    	
	// // Bit
	// EncoderChannel bitEC = getBitEncoder();
	// bitEC.encodeInteger( xmlInteger );
	// bitEC.flush();
	// assertTrue(getBitDecoder().decodeIntegerAsString().equals( sDec ) );
	// // Byte
	// getByteEncoder().encodeInteger( xmlInteger );
	// assertTrue(getByteDecoder().decodeIntegerAsString().equals( sDec ) );
	// }

	public void testIntegerLong1() throws IOException {
		String s = "12131321321";

		long xmlInteger = Long.parseLong(s);

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeLong(xmlInteger);
		bitEC.flush();
		assertTrue(s.equals(getBitDecoder().decodeLongValue().toString()));
		// Byte
		getByteEncoder().encodeLong(xmlInteger);
		assertTrue(s.equals(getByteDecoder().decodeLongValue().toString()));
	}


	public void testIntegerBig2() throws IOException {
		String s = "2137000000000000000000000000001";

		BigInteger xmlInteger = new BigInteger(s);

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeBigInteger(xmlInteger);
		bitEC.flush();
		assertTrue(s.equals(getBitDecoder().decodeHugeIntegerValue().toString()));
		// Byte
		getByteEncoder().encodeBigInteger(xmlInteger);
		assertTrue(s.equals(getByteDecoder().decodeHugeIntegerValue().toString()));
	}
	
	public void testIntegerBig1() throws IOException {
		String s = "12678967543233";

		BigInteger xmlInteger = new BigInteger(s);

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeBigInteger(xmlInteger);
		bitEC.flush();
		assertTrue(s.equals(getBitDecoder().decodeHugeIntegerValue().toString()));
		// Byte
		getByteEncoder().encodeBigInteger(xmlInteger);
		assertTrue(s.equals(getByteDecoder().decodeHugeIntegerValue().toString()));
	}


	public void testIntegerBig3() throws IOException {
		String s = "-5153135115135135135135153153135135153";

		BigInteger xmlInteger = new BigInteger(s);

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeBigInteger(xmlInteger);
		bitEC.flush();
		assertTrue(s.equals(getBitDecoder().decodeHugeIntegerValue().toString()));
		// Byte
		getByteEncoder().encodeBigInteger(xmlInteger);
		assertTrue(s.equals(getByteDecoder().decodeHugeIntegerValue().toString()));
	}

	public void testIntegerSequence() throws IOException {
		// Bit / Byte
		EncoderChannel ecBit = getBitEncoder();
		EncoderChannel ecByte = getByteEncoder();
		for (int i = 0; i < 100000; i++) {
			ecBit.encodeInteger(i);
			ecByte.encodeInteger(i);
		}

		DecoderChannel dcBit = getBitDecoder();
		DecoderChannel dcByte = getByteDecoder();
		for (int i = 0; i < 100000; i++) {
			assertEquals(dcBit.decodeIntegerValue().toInteger(), i);
			assertEquals(dcByte.decodeIntegerValue().toInteger(), i);
		}
	}

}