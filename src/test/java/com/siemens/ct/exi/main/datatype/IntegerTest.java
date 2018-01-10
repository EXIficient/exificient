/*
 * Copyright (c) 2007-2016 Siemens AG
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * 
 */

package com.siemens.ct.exi.main.datatype;

import java.io.IOException;

import com.siemens.ct.exi.core.io.channel.DecoderChannel;
import com.siemens.ct.exi.core.io.channel.EncoderChannel;
import com.siemens.ct.exi.core.values.IntegerValue;
import com.siemens.ct.exi.core.values.IntegerValueType;
import com.siemens.ct.exi.core.values.ValueType;

public class IntegerTest extends AbstractTestCase {

	public IntegerTest(String testName) {
		super(testName);
	}

	public void testInteger0() throws IOException {
		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeInteger(0);
		bitEC.flush();
		IntegerValue dec1 = getBitDecoder().decodeIntegerValue();
		assertTrue(dec1.getIntegerValueType() == IntegerValueType.INT);
		assertTrue(dec1.intValue() == 0);
		// Byte
		getByteEncoder().encodeInteger(0);
		IntegerValue dec2 = getByteDecoder().decodeIntegerValue();
		assertTrue(dec2.getIntegerValueType() == IntegerValueType.INT);
		assertTrue(dec2.intValue() == 0);
	}

	public void testInteger1() throws IOException {
		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeInteger(1);
		bitEC.flush();
		IntegerValue dec1 = getBitDecoder().decodeIntegerValue();
		assertTrue(dec1.getIntegerValueType() == IntegerValueType.INT);
		assertTrue(dec1.intValue() == 1);
		// Byte
		getByteEncoder().encodeInteger(1);
		IntegerValue dec2 = getByteDecoder().decodeIntegerValue();
		assertTrue(dec2.getIntegerValueType() == IntegerValueType.INT);
		assertTrue(dec2.intValue() == 1);
	}

	public void testIntegerMaxNegativeInteger() throws IOException {
		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeInteger(Integer.MIN_VALUE);
		bitEC.flush();
		IntegerValue dec1 = getBitDecoder().decodeIntegerValue();
		assertTrue(dec1.getValueType() == ValueType.INTEGER);
		assertTrue(dec1.intValue() == Integer.MIN_VALUE);
		// Byte
		getByteEncoder().encodeInteger(Integer.MIN_VALUE);
		IntegerValue dec2 = getByteDecoder().decodeIntegerValue();
		assertTrue(dec2.getValueType() == ValueType.INTEGER);
		assertTrue(dec2.intValue() == Integer.MIN_VALUE);
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

		// long xmlInteger = Long.parseLong(s);
		IntegerValue iv = IntegerValue.parse(s);

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeIntegerValue(iv);
		bitEC.flush();
		assertTrue(s.equals(getBitDecoder().decodeIntegerValue().toString()));
		// Byte
		getByteEncoder().encodeIntegerValue(iv);
		assertTrue(s.equals(getByteDecoder().decodeIntegerValue().toString()));
	}

	public void testIntegerBig2() throws IOException {
		String s = "2137000000000000000000000000001";

		// BigInteger xmlInteger = new BigInteger(s);
		IntegerValue xmlInteger = IntegerValue.parse(s);

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeIntegerValue(xmlInteger);
		bitEC.flush();
		assertTrue(s.equals(getBitDecoder().decodeIntegerValue().toString()));
		// Byte
		getByteEncoder().encodeIntegerValue(xmlInteger);
		assertTrue(s.equals(getByteDecoder().decodeIntegerValue().toString()));
	}

	public void testIntegerBig1() throws IOException {
		String s = "12678967543233";

		// BigInteger xmlInteger = new BigInteger(s);
		IntegerValue xmlInteger = IntegerValue.parse(s);

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeIntegerValue(xmlInteger);
		bitEC.flush();
		assertTrue(s.equals(getBitDecoder().decodeIntegerValue().toString()));
		// Byte
		getByteEncoder().encodeIntegerValue(xmlInteger);
		assertTrue(s.equals(getByteDecoder().decodeIntegerValue().toString()));
	}

	public void testIntegerBig3() throws IOException {
		String s = "-5153135115135135135135153153135135153";

		// BigInteger xmlInteger = new BigInteger(s);
		IntegerValue xmlInteger = IntegerValue.parse(s);

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeIntegerValue(xmlInteger);
		bitEC.flush();
		assertTrue(s.equals(getBitDecoder().decodeIntegerValue().toString()));
		// Byte
		getByteEncoder().encodeIntegerValue(xmlInteger);
		assertTrue(s.equals(getByteDecoder().decodeIntegerValue().toString()));
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
			IntegerValue dec1 = dcBit.decodeIntegerValue();
			assertTrue(dec1.getIntegerValueType() == IntegerValueType.INT);
			assertEquals(dec1.intValue(), i);
			IntegerValue dec2 = dcByte.decodeIntegerValue();
			assertTrue(dec2.getIntegerValueType() == IntegerValueType.INT);
			assertEquals(dec2.intValue(), i);
		}
	}

}