/*
 * Copyright (c) 2007-2018 Siemens AG
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
import java.math.BigInteger;

import com.siemens.ct.exi.core.io.channel.DecoderChannel;
import com.siemens.ct.exi.core.io.channel.EncoderChannel;
import com.siemens.ct.exi.core.values.IntegerValue;

public class UnsignedIntegerTest extends AbstractTestCase {

	public UnsignedIntegerTest(String testName) {
		super(testName);
	}

	public void testUnsignedInteger0() throws IOException {
		// Bit
		getBitEncoder().encodeUnsignedInteger(0);
		assertTrue(getBitDecoder().decodeUnsignedInteger() == 0);
		// Byte
		getByteEncoder().encodeUnsignedInteger(0);
		assertTrue(getByteDecoder().decodeUnsignedInteger() == 0);
	}

	public void testUnsignedInteger1() throws IOException {
		// Bit
		getBitEncoder().encodeUnsignedInteger(1);
		assertTrue(getBitDecoder().decodeUnsignedInteger() == 1);
		// Byte
		getByteEncoder().encodeUnsignedInteger(1);
		assertTrue(getByteDecoder().decodeUnsignedInteger() == 1);
	}

	public void testUnsignedInteger2() throws IOException {
		// Bit
		getBitEncoder().encodeUnsignedInteger(2);
		assertTrue(getBitDecoder().decodeUnsignedInteger() == 2);
		// Byte
		getByteEncoder().encodeUnsignedInteger(2);
		assertTrue(getByteDecoder().decodeUnsignedInteger() == 2);
	}

	public void testUnsignedInteger128() throws IOException {
		// Bit
		getBitEncoder().encodeUnsignedInteger(128);
		assertTrue(getBitDecoder().decodeUnsignedInteger() == 128);
		// Byte
		getByteEncoder().encodeUnsignedInteger(128);
		assertTrue(getByteDecoder().decodeUnsignedInteger() == 128);
	}

	public void testUnsignedInteger200() throws IOException {
		// Bit
		getBitEncoder().encodeUnsignedInteger(200);
		assertTrue(getBitDecoder().decodeUnsignedInteger() == 200);
		// Byte
		getByteEncoder().encodeUnsignedInteger(200);
		assertTrue(getByteDecoder().decodeUnsignedInteger() == 200);
	}

	public void testUnsignedInteger2000() throws IOException {
		// Bit
		getBitEncoder().encodeUnsignedInteger(2000);
		assertTrue(getBitDecoder().decodeUnsignedInteger() == 2000);
		// Byte
		getByteEncoder().encodeUnsignedInteger(2000);
		assertTrue(getByteDecoder().decodeUnsignedInteger() == 2000);
	}

	public void testUnsignedInteger20000() throws IOException {
		// Bit
		getBitEncoder().encodeUnsignedInteger(20000);
		assertTrue(getBitDecoder().decodeUnsignedInteger() == 20000);
		// Byte
		getByteEncoder().encodeUnsignedInteger(20000);
		assertTrue(getByteDecoder().decodeUnsignedInteger() == 20000);
	}

	public void testUnsignedInteger200000() throws IOException {
		// Bit
		getBitEncoder().encodeUnsignedInteger(200000);
		assertTrue(getBitDecoder().decodeUnsignedInteger() == 200000);
		// Byte
		getByteEncoder().encodeUnsignedInteger(20000);
		assertTrue(getByteDecoder().decodeUnsignedInteger() == 20000);
	}

	public void testUnsignedInteger2000000() throws IOException {
		// Bit
		getBitEncoder().encodeUnsignedInteger(2000000);
		assertTrue(getBitDecoder().decodeUnsignedInteger() == 2000000);
		// Byte
		getByteEncoder().encodeUnsignedInteger(20000);
		assertTrue(getByteDecoder().decodeUnsignedInteger() == 20000);
	}

	public void testUnsignedIntegerS0() throws IOException {
		String s = "0";
		//
		// int i1 = MethodsBag.numberOfBitsToRepresent( Long.valueOf ( "0" ) );
		// int i2 = MethodsBag.numberOfBitsToRepresent( Long.valueOf ( "2" ) );
		// int i21230354 = MethodsBag.numberOfBitsToRepresent( Long.valueOf (
		// "21230354" ) );
		// int imi = MethodsBag.numberOfBitsToRepresent( Long.valueOf (
		// Integer.MAX_VALUE ) );
		// int ih = MethodsBag.numberOfBitsToRepresent( Long.valueOf (
		// "12678967543233" ) );
		//
		//
		int xmlInteger = Integer.parseInt(s);

		// Bit
		getBitEncoder().encodeUnsignedInteger(xmlInteger);
		assertTrue(s.equals(getBitDecoder().decodeUnsignedIntegerValue()
				.toString()));
		// Byte
		getByteEncoder().encodeUnsignedInteger(xmlInteger);
		assertTrue(s.equals(getByteDecoder().decodeUnsignedIntegerValue()
				.toString()));
	}

	public void testUnsignedIntegerS1() throws IOException {
		String s = "1";

		int xmlInteger = Integer.parseInt(s);

		// Bit
		getBitEncoder().encodeUnsignedInteger(xmlInteger);
		assertTrue(s.equals(getBitDecoder().decodeUnsignedIntegerValue()
				.toString()));
		// Byte
		getByteEncoder().encodeUnsignedInteger(xmlInteger);
		assertTrue(s.equals(getByteDecoder().decodeUnsignedIntegerValue()
				.toString()));
	}

	public void testUnsignedIntegerS329() throws IOException {
		String s = "329";

		int xmlInteger = Integer.parseInt(s);

		// Bit
		getBitEncoder().encodeUnsignedInteger(xmlInteger);
		String sDec = getBitDecoder().decodeUnsignedIntegerValue().toString();
		assertTrue(s.equals(sDec));
		// Byte
		getByteEncoder().encodeUnsignedInteger(xmlInteger);
		assertTrue(s.equals(getByteDecoder().decodeUnsignedIntegerValue()
				.toString()));
	}

	public void testUnsignedIntegerS2147483647() throws IOException {
		String s = "2147483647";

		int xmlInteger = Integer.parseInt(s);

		// Bit
		getBitEncoder().encodeUnsignedInteger(xmlInteger);
		assertTrue(s.equals(getBitDecoder().decodeUnsignedIntegerValue()
				.toString()));
		// Byte
		getByteEncoder().encodeUnsignedInteger(xmlInteger);
		assertTrue(s.equals(getByteDecoder().decodeUnsignedIntegerValue()
				.toString()));
	}

	public void testUnsignedLongS1() throws IOException {
		String s = "2147483649997";

		// long xmlInteger = Long.parseLong(s);
		IntegerValue xmlInteger = IntegerValue.parse(s);

		// Bit
		getBitEncoder().encodeUnsignedIntegerValue(xmlInteger);
		assertTrue(s.equals(getBitDecoder().decodeUnsignedIntegerValue()
				.toString()));
		// Byte
		getByteEncoder().encodeUnsignedIntegerValue(xmlInteger);
		assertTrue(s.equals(getByteDecoder().decodeUnsignedIntegerValue()
				.toString()));
	}

	public void testUnsignedIntegerSBig1() throws IOException {
		String s = "12678967543233";

		// BigInteger xmlInteger = new BigInteger(s);
		IntegerValue xmlInteger = IntegerValue.parse(s);

		// Bit
		getBitEncoder().encodeUnsignedIntegerValue(xmlInteger);
		String s1 = getBitDecoder().decodeUnsignedIntegerValue().toString();
		assertTrue(s + "!=" + s1, s.equals(s1));
		// Byte
		getByteEncoder().encodeUnsignedIntegerValue(xmlInteger);
		assertTrue(s.equals(getByteDecoder().decodeUnsignedIntegerValue()
				.toString()));
	}

	public void testUnsignedIntegerSBig2() throws IOException {
		BigInteger bi = BigInteger.valueOf(Long.MAX_VALUE);
		bi = bi.add(BigInteger.valueOf(1));

		String s = bi.toString();

		// 9223372036854775808
		// BigInteger xmlInteger = new BigInteger(s);
		IntegerValue xmlInteger = IntegerValue.parse(s);

		// Bit
		getBitEncoder().encodeUnsignedIntegerValue(xmlInteger);
		String s1 = getBitDecoder().decodeUnsignedIntegerValue().toString();
		assertTrue(s + "!=" + s1, s.equals(s1));
		// Byte
		getByteEncoder().encodeUnsignedIntegerValue(xmlInteger);
		assertTrue(s.equals(getByteDecoder().decodeUnsignedIntegerValue()
				.toString()));
	}

	public void testUnsignedIntegerSBig3() throws IOException {
		String s = "87139166666670000000000000000001";

		// BigInteger xmlInteger = new BigInteger(s);
		IntegerValue xmlInteger = IntegerValue.parse(s);

		// Bit
		getBitEncoder().encodeUnsignedIntegerValue(xmlInteger);
		String s1 = getBitDecoder().decodeUnsignedIntegerValue().toString();
		assertTrue(s + "!=" + s1, s.equals(s1));
		// Byte
		getByteEncoder().encodeUnsignedIntegerValue(xmlInteger);
		assertTrue(s.equals(getByteDecoder().decodeUnsignedIntegerValue()
				.toString()));
	}

	public void testUnsignedIntegerSBig4() throws IOException {
		String s = "0008713916666667000000000000000000";
		String s2 = "8713916666667000000000000000000";

		// BigInteger xmlInteger = new BigInteger(s);
		IntegerValue xmlInteger = IntegerValue.parse(s);

		// Bit
		getBitEncoder().encodeUnsignedIntegerValue(xmlInteger);
		String s1 = getBitDecoder().decodeUnsignedIntegerValue().toString();
		assertTrue(s2 + "!=" + s1, s2.equals(s1));
		// Byte
		getByteEncoder().encodeUnsignedIntegerValue(xmlInteger);
		assertTrue(s2.equals(getByteDecoder().decodeUnsignedIntegerValue()
				.toString()));
	}

	public void testUnsignedIntegerSFailure() throws IOException {
		String s = "-123";

		// BigInteger xmlInteger = new BigInteger(s);
		IntegerValue xmlInteger = IntegerValue.parse(s);

		try {
			// Bit
			getBitEncoder().encodeUnsignedIntegerValue(xmlInteger);
			fail("Negative values accepted");
		} catch (RuntimeException e) {
			// ok
		}
	}

	public void testUnsignedIntegerSequence() throws IOException {
		// Bit / Byte
		EncoderChannel ecBit = getBitEncoder();
		EncoderChannel ecByte = getByteEncoder();
		for (int i = 0; i < 100000; i++) {
			ecBit.encodeUnsignedInteger(i);
			ecByte.encodeUnsignedInteger(i);
		}

		DecoderChannel dcBit = getBitDecoder();
		DecoderChannel dcByte = getByteDecoder();
		for (int i = 0; i < 100000; i++) {
			assertEquals(dcBit.decodeUnsignedInteger(), i);
			assertEquals(dcByte.decodeUnsignedInteger(), i);
		}
	}

}