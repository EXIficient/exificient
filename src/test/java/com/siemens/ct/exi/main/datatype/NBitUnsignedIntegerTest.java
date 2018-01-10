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

import com.siemens.ct.exi.core.datatype.Datatype;
import com.siemens.ct.exi.core.exceptions.EXIException;
import com.siemens.ct.exi.core.io.channel.DecoderChannel;
import com.siemens.ct.exi.core.io.channel.EncoderChannel;
import com.siemens.ct.exi.main.types.DatatypeMappingTest;
import com.siemens.ct.exi.core.values.StringValue;
import com.siemens.ct.exi.core.values.Value;

public class NBitUnsignedIntegerTest extends AbstractTestCase {

	static final int log2CeilValues[] = new int[64];

	// Cache values of log2Ceil(n) for n in 0..63
	static {
		log2CeilValues[0] = 1; // by definition

		for (int j = 1; j < 64; j++) {
			for (int i = 31; i >= 0; i--) {
				if ((j >>> i) > 0) {
					log2CeilValues[j] = i + 1;
					break;
				}
			}
		}
	}

	/**
	 * Returns the least number of bits that is needed to represent the int
	 * <param>n</param>. Returns 1 if <param>n</param> is 0.
	 * 
	 * @param n
	 *            Integer value. If <param>n</param> is negative it is
	 *            interpreted as a unsigned int. Thus, for every n < 0 we have
	 *            log2Ceil(n) = 32.
	 * 
	 */
	public static int numberOfBitsToRepresent(int n) {
		if (0 <= n && n < log2CeilValues.length) {
			return log2CeilValues[n];
		}

		for (int i = 31; i >= 0; i--) {
			if ((n >>> i) > 0) {
				return i + 1;
			}
		}

		return 1;
	}

	public static int numberOfBitsToRepresent(long l) {
		if (l <= Integer.MAX_VALUE) {
			return numberOfBitsToRepresent((int) l);
		} else {

			for (int i = 63; i >= 0; i--) {
				if ((l >>> i) > 0) {
					return i + 1;
				}
			}
		}

		return 1;
	}

	public NBitUnsignedIntegerTest(String testName) {
		super(testName);
	}

	public void testNBitUnsignedInteger0_1() throws IOException {
		int value = 0;
		int nbits = 1;

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeNBitUnsignedInteger(value, nbits);
		bitEC.flush();
		assertTrue(getBitDecoder().decodeNBitUnsignedInteger(nbits) == value);
		// Byte
		getByteEncoder().encodeNBitUnsignedInteger(value, nbits);
		assertTrue(getByteDecoder().decodeNBitUnsignedInteger(nbits) == value);
	}

	public void testNBitUnsignedInteger1_1() throws IOException {
		int value = 1;
		int nbits = 1;

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeNBitUnsignedInteger(value, nbits);
		bitEC.flush();
		assertTrue(getBitDecoder().decodeNBitUnsignedInteger(nbits) == value);
		// Byte
		getByteEncoder().encodeNBitUnsignedInteger(value, nbits);
		assertTrue(getByteDecoder().decodeNBitUnsignedInteger(nbits) == value);
	}

	public void testNBitUnsignedInteger8_4() throws IOException {
		int value = 8;
		int nbits = 4;

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeNBitUnsignedInteger(value, nbits);
		bitEC.flush();
		assertTrue(getBitDecoder().decodeNBitUnsignedInteger(nbits) == value);
		// Byte
		getByteEncoder().encodeNBitUnsignedInteger(value, nbits);
		assertTrue(getByteDecoder().decodeNBitUnsignedInteger(nbits) == value);
	}

	public void testNBitUnsignedInteger33_9() throws IOException {
		int value = 33;
		int nbits = 9;

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeNBitUnsignedInteger(value, nbits);
		bitEC.flush();
		assertTrue(getBitDecoder().decodeNBitUnsignedInteger(nbits) == value);
		// Byte
		getByteEncoder().encodeNBitUnsignedInteger(value, nbits);
		assertTrue(getByteDecoder().decodeNBitUnsignedInteger(nbits) == value);
	}

	public void testNBitUnsignedInteger78935_20() throws IOException {
		int value = 78935;
		int nbits = 20;

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeNBitUnsignedInteger(value, nbits);
		bitEC.flush();
		assertTrue(getBitDecoder().decodeNBitUnsignedInteger(nbits) == value);
		// Byte
		getByteEncoder().encodeNBitUnsignedInteger(value, nbits);
		assertTrue(getByteDecoder().decodeNBitUnsignedInteger(nbits) == value);
	}

	public void testNBitUnsignedInteger8448_20() throws IOException {
		int value = 8448;
		int nbits = 20;

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeNBitUnsignedInteger(value, nbits);
		bitEC.flush();
		assertTrue(getBitDecoder().decodeNBitUnsignedInteger(nbits) == value);
		// Byte
		getByteEncoder().encodeNBitUnsignedInteger(value, nbits);
		assertTrue(getByteDecoder().decodeNBitUnsignedInteger(nbits) == value);
	}

	public void testNBitUnsignedIntegerFailureBit() throws IOException {
		int value = -3;
		int nbits = 5;

		try {
			getBitEncoder().encodeNBitUnsignedInteger(value, nbits);
			fail("Negative values accepted");
		} catch (RuntimeException e) {
			// ok
		}
	}

	public void testNBitUnsignedIntegerFailureByte() throws IOException {
		int value = -3;
		int nbits = 5;

		try {
			getByteEncoder().encodeNBitUnsignedInteger(value, nbits);
			fail("Negative values accepted");
		} catch (RuntimeException e) {
			// ok
		}
	}

	public void testNBitUnsignedIntegerFacet1() throws IOException,
			EXIException {
		String schemaAsString = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ "  <xs:simpleType name='NBit'>"
				+ "    <xs:restriction base='xs:integer'>"
				+ "      <xs:minInclusive value='2' />"
				+ "      <xs:maxExclusive value='10'/>"
				+ "    </xs:restriction>"
				+ "  </xs:simpleType>"
				+ "</xs:schema>";

		Datatype datatype = DatatypeMappingTest.getSimpleDatatypeFor(
				schemaAsString, "NBit", "");

		// try to validate
		assertFalse(datatype.isValid(new StringValue("12")));
	}

	public void testNBitUnsignedIntegerFacet2() throws IOException,
			EXIException {
		String schemaAsString = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ "  <xs:simpleType name='NBit'>"
				+ "    <xs:restriction base='xs:long'>"
				+ "      <xs:minInclusive value='-200' />"
				+ "      <xs:maxExclusive value='-10'/>"
				+ "    </xs:restriction>"
				+ "  </xs:simpleType>"
				+ "</xs:schema>";

		StringValue sValue = new StringValue("-12");

		Datatype datatype = DatatypeMappingTest.getSimpleDatatypeFor(
				schemaAsString, "NBit", "");

		// write (bit & byte )
		assertTrue(datatype.isValid(sValue));
		// bit
		EncoderChannel bitEC = getBitEncoder();
		datatype.writeValue(null, bitEC, null);
		bitEC.flush();
		// byte
		datatype.writeValue(null, getByteEncoder(), null);

		// read
		Value sDecoded;
		// bit
		sDecoded = datatype.readValue(null, getBitDecoder(), null);
		assertTrue(sValue + " != " + sDecoded, sDecoded.equals(sValue));
		// byte
		sDecoded = datatype.readValue(null, getByteDecoder(), null);
		assertTrue(sValue + " != " + sDecoded, sDecoded.equals(sValue));
	}

	public void testNBitUnsignedIntegerFacet3() throws IOException,
			EXIException {
		String schemaAsString = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ "  <xs:simpleType name='NBit'>"
				+ "    <xs:restriction base='xs:int'>"
				+ "      <xs:minInclusive value='-200' />"
				+ "      <xs:maxExclusive value='-10'/>"
				+ "    </xs:restriction>"
				+ "  </xs:simpleType>"
				+ "</xs:schema>";

		StringValue sValue = new StringValue("-12");

		Datatype datatype = DatatypeMappingTest.getSimpleDatatypeFor(
				schemaAsString, "NBit", "");

		// write (bit & byte )
		assertTrue(datatype.isValid(sValue));
		// bit
		EncoderChannel bitEC = getBitEncoder();
		datatype.writeValue(null, bitEC, null);
		bitEC.flush();
		// byte
		datatype.writeValue(null, getByteEncoder(), null);

		// read
		Value sDecoded;
		// bit
		sDecoded = datatype.readValue(null, getBitDecoder(), null);
		assertTrue(sValue + " != " + sDecoded, sDecoded.equals(sValue));
		// byte
		sDecoded = datatype.readValue(null, getByteDecoder(), null);
		assertTrue(sValue + " != " + sDecoded, sDecoded.equals(sValue));
	}

	public void testNBitUnsignedIntegerSequence() throws IOException {
		// Bit / Byte
		EncoderChannel ecBit = getBitEncoder();
		EncoderChannel ecByte = getByteEncoder();
		for (int i = 0; i < 1000000; i++) {
			int value = i;
			int nbits = numberOfBitsToRepresent(value);
			ecBit.encodeNBitUnsignedInteger(value, nbits);
			ecByte.encodeNBitUnsignedInteger(value, nbits);
		}
		ecBit.flush();

		DecoderChannel dcBit = getBitDecoder();
		DecoderChannel dcByte = getByteDecoder();
		for (int i = 0; i < 1000000; i++) {
			int value = i;
			int nbits = numberOfBitsToRepresent(value);
			assertEquals(dcBit.decodeNBitUnsignedInteger(nbits), value);
			assertEquals(dcByte.decodeNBitUnsignedInteger(nbits), value);
		}
	}

}