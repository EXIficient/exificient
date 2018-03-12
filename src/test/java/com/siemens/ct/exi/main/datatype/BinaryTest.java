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

import com.siemens.ct.exi.core.datatype.BinaryBase64Datatype;
import com.siemens.ct.exi.core.datatype.BinaryHexDatatype;
import com.siemens.ct.exi.core.datatype.Datatype;
import com.siemens.ct.exi.core.exceptions.EXIException;
import com.siemens.ct.exi.core.io.channel.DecoderChannel;
import com.siemens.ct.exi.core.io.channel.EncoderChannel;
import com.siemens.ct.exi.core.types.TypeEncoder;
import com.siemens.ct.exi.core.types.TypedTypeEncoder;
import com.siemens.ct.exi.core.values.BinaryBase64Value;
import com.siemens.ct.exi.core.values.BinaryHexValue;
import com.siemens.ct.exi.core.values.StringValue;
import com.siemens.ct.exi.core.values.Value;

public class BinaryTest extends AbstractTestCase {

	public BinaryTest(String testName) {
		super(testName);
	}

	public void testHexBinaryAsString0FB7() throws IOException, EXIException {
		StringValue src = new StringValue("0FB7");

		Datatype binary = new BinaryHexDatatype(null);
		TypeEncoder typeEncoder = new TypedTypeEncoder();
		assertTrue(typeEncoder.isValid(binary, src));

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		typeEncoder.writeValue(null, bitEC, null);
		bitEC.flush();
		Value val1 = new BinaryHexValue(getBitDecoder().decodeBinary());
		assertTrue(src.equals(val1.toString()));
		// Byte
		typeEncoder.writeValue(null, getByteEncoder(), null);
		Value val2 = new BinaryHexValue(getByteDecoder().decodeBinary());
		assertTrue(src.equals(val2.toString()));
	}

	public void testHexBinaryAsString0FB7_Space() throws IOException, EXIException {
		StringValue src = new StringValue(" 0FB7 ");
		String src_2 = "0FB7";

		Datatype binary = new BinaryHexDatatype(null);
		TypeEncoder typeEncoder = new TypedTypeEncoder();
		assertTrue(typeEncoder.isValid(binary, src));

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		typeEncoder.writeValue(null, bitEC, null);
		bitEC.flush();
		Value val1 = new BinaryHexValue(getBitDecoder().decodeBinary());
		assertTrue(src_2.equals(val1.toString()));
		// Byte
		typeEncoder.writeValue(null, getByteEncoder(), null);
		Value val2 = new BinaryHexValue(getByteDecoder().decodeBinary());
		assertTrue(src_2.equals(val2.toString()));
	}

	public void testHexBinary_1() throws IOException, EXIException {
		StringValue src = new StringValue(
				"0123456789ABCDEFabcdef0123456789ABCDEFabcdef0123456789ABCDEFabcdef0123456789ABCDEFabcdef0123456789ABCDEFabcdef0123456789ABCDEFabcdef0123456789ABCDEFabcdef0123456789ABCDEFabcdef0123456789ABCDEFabcdef0123456789ABCDEFabcdef0123456789ABCDEFabcdef0123456789ABCDEFabcdef0123456789ABCDEFabcdef0123456789ABCDEFabcdef0123456789ABCDEFabcdef0123456789ABCDEFabcdef0123456789ABCDEFabcdef0123456789ABCDEFabcdef0123456789ABCDEFabcdef0123456789ABCDEFabcdef0123456789ABCDEFabcdef");
		String src_2 = "0123456789ABCDEFABCDEF0123456789ABCDEFABCDEF0123456789ABCDEFABCDEF0123456789ABCDEFABCDEF0123456789ABCDEFABCDEF0123456789ABCDEFABCDEF0123456789ABCDEFABCDEF0123456789ABCDEFABCDEF0123456789ABCDEFABCDEF0123456789ABCDEFABCDEF0123456789ABCDEFABCDEF0123456789ABCDEFABCDEF0123456789ABCDEFABCDEF0123456789ABCDEFABCDEF0123456789ABCDEFABCDEF0123456789ABCDEFABCDEF0123456789ABCDEFABCDEF0123456789ABCDEFABCDEF0123456789ABCDEFABCDEF0123456789ABCDEFABCDEF0123456789ABCDEFABCDEF";
		Datatype binary = new BinaryHexDatatype(null);
		TypeEncoder typeEncoder = new TypedTypeEncoder();
		assertTrue(typeEncoder.isValid(binary, src));

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		typeEncoder.writeValue(null, bitEC, null);
		bitEC.flush();
		Value val1 = new BinaryHexValue(getBitDecoder().decodeBinary());
		assertTrue(src_2.equals(val1.toString()));
		// Byte
		typeEncoder.writeValue(null, getByteEncoder(), null);
		Value val2 = new BinaryHexValue(getByteDecoder().decodeBinary());
		assertTrue(src_2.equals(val2.toString()));
	}

	public void testBase64AsString0FB7() throws IOException, EXIException {
		StringValue src = new StringValue("0FB7");

		Datatype binary = new BinaryBase64Datatype(null);
		TypeEncoder typeEncoder = new TypedTypeEncoder();
		assertTrue(typeEncoder.isValid(binary, src));

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		typeEncoder.writeValue(null, bitEC, null);
		bitEC.flush();
		Value val1 = new BinaryBase64Value(getBitDecoder().decodeBinary());
		assertTrue(src.equals(val1.toString()));
		// Byte
		typeEncoder.writeValue(null, getByteEncoder(), null);
		Value val2 = new BinaryBase64Value(getByteDecoder().decodeBinary());
		assertTrue(src.equals(val2.toString()));
	}

	public void testBase64AsString0FB7_Spaces() throws IOException, EXIException {
		StringValue src = new StringValue("  0  F B 7 ");
		String src_2 = "0FB7";

		Datatype binary = new BinaryBase64Datatype(null);
		TypeEncoder typeEncoder = new TypedTypeEncoder();
		assertTrue(typeEncoder.isValid(binary, src));

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		typeEncoder.writeValue(null, bitEC, null);
		bitEC.flush();
		Value val1 = new BinaryBase64Value(getBitDecoder().decodeBinary());
		assertTrue(src_2.equals(val1.toString()));
		// Byte
		typeEncoder.writeValue(null, getByteEncoder(), null);
		Value val2 = new BinaryBase64Value(getByteDecoder().decodeBinary());
		assertTrue(src_2.equals(val2.toString()));
	}

	public void testBase64_1() throws IOException, EXIException {
		StringValue src = new StringValue("ZHM=");

		Datatype binary = new BinaryBase64Datatype(null);
		TypeEncoder typeEncoder = new TypedTypeEncoder();
		assertTrue(typeEncoder.isValid(binary, src));

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		typeEncoder.writeValue(null, bitEC, null);
		bitEC.flush();
		Value val1 = new BinaryBase64Value(getBitDecoder().decodeBinary());
		assertTrue(src.equals(val1.toString()));
		// Byte
		typeEncoder.writeValue(null, getByteEncoder(), null);
		Value val2 = new BinaryBase64Value(getByteDecoder().decodeBinary());
		assertTrue(src.equals(val2.toString()));
	}

	public void testBase64_2() throws IOException, EXIException {
		StringValue src = new StringValue(
				"RGFzIGlzIGphIGVpbiBmZXN0ZXIgQmxlZHNpbm4sIHdlaWwgVW1sYXV0ZSB3aWUg9iB1bmQg/CBtYWNoZW4gU2lubiwgd2llIGF1Y2ggZWluIHNjaGFyZmVzIN8u");

		Datatype binary = new BinaryBase64Datatype(null);
		TypeEncoder typeEncoder = new TypedTypeEncoder();
		assertTrue(typeEncoder.isValid(binary, src));

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		typeEncoder.writeValue(null, bitEC, null);
		bitEC.flush();
		Value val1 = new BinaryBase64Value(getBitDecoder().decodeBinary());
		assertTrue(src.equals(val1.toString()));
		// Byte
		typeEncoder.writeValue(null, getByteEncoder(), null);
		Value val2 = new BinaryBase64Value(getByteDecoder().decodeBinary());
		assertTrue(src.equals(val2.toString()));
	}

	public void testBase64_3() throws IOException, EXIException {
		StringValue src = new StringValue(
				"SMOkdHRlbiBIw7x0ZSBlaW4gw58gaW0gTmFtZW4sIHfDpHJlbiBzaWUgbcO2Z2xpY2hlcndlaXNlIGtlaW5lIEjDvHRlIG1laHIsDQpzb25kZXJuIEjDvMOfZS4NCg==");

		Datatype binary = new BinaryBase64Datatype(null);
		TypeEncoder typeEncoder = new TypedTypeEncoder();
		assertTrue(typeEncoder.isValid(binary, src));

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		typeEncoder.writeValue(null, bitEC, null);
		bitEC.flush();
		Value val1 = new BinaryBase64Value(getBitDecoder().decodeBinary());
		assertTrue(src.equals(val1.toString()));
		// Byte
		typeEncoder.writeValue(null, getByteEncoder(), null);
		Value val2 = new BinaryBase64Value(getByteDecoder().decodeBinary());
		assertTrue(src.equals(val2.toString()));
	}

	public void testBase64_4() throws IOException, EXIException {
		String s1 = "R0lGODdhWAK+ov////v7++fn58DAwI6Ojl5eXjExMQMDAyxYAr5AA/8Iutz+MMpJq7046827/2Ao";
		String sE = "\n \n ";
		String s2 = "jmRpnmiqPsKxvvBqCIxgxHg=";
		StringValue src = new StringValue(s1 + sE + s2);
		String src_2 = s1 + s2;

		Datatype binary = new BinaryBase64Datatype(null);
		TypeEncoder typeEncoder = new TypedTypeEncoder();
		assertTrue(typeEncoder.isValid(binary, src));

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		typeEncoder.writeValue(null, bitEC, null);
		bitEC.flush();
		Value val1 = new BinaryBase64Value(getBitDecoder().decodeBinary());
		assertTrue(src_2.equals(val1.toString()));
		// Byte
		typeEncoder.writeValue(null, getByteEncoder(), null);
		Value val2 = new BinaryBase64Value(getByteDecoder().decodeBinary());
		assertTrue(src_2.equals(val2.toString()));
	}

	public void testBinary_1() throws IOException {
		String s = "blabla";

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeBinary(s.getBytes());
		bitEC.flush();
		String sDec = new String(getBitDecoder().decodeBinary());
		assertTrue(s.equals(sDec));

		// Byte
		EncoderChannel byteEC = getByteEncoder();
		byteEC.encodeBinary(s.getBytes());
		sDec = new String(getByteDecoder().decodeBinary());
		assertTrue(s.equals(sDec));
	}

	public void testBinary_2() throws IOException {
		String s = "blabla";

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeNBitUnsignedInteger(2, 3);
		bitEC.encodeBinary(s.getBytes());
		bitEC.encodeNBitUnsignedInteger(5, 7);
		bitEC.flush();
		DecoderChannel bitDC = getBitDecoder();
		assertTrue(bitDC.decodeNBitUnsignedIntegerValue(3).intValue() == 2);
		String sDec = new String(bitDC.decodeBinary());
		assertTrue(s.equals(sDec));
		assertTrue(bitDC.decodeNBitUnsignedIntegerValue(7).intValue() == 5);

		// Byte
		EncoderChannel byteEC = getByteEncoder();
		byteEC.encodeNBitUnsignedInteger(2, 3);
		byteEC.encodeBinary(s.getBytes());
		byteEC.encodeNBitUnsignedInteger(5, 7);
		DecoderChannel byteDC = getByteDecoder();
		assertTrue(byteDC.decodeNBitUnsignedIntegerValue(3).intValue() == 2);
		sDec = new String(byteDC.decodeBinary());
		assertTrue(s.equals(sDec));
		assertTrue(byteDC.decodeNBitUnsignedIntegerValue(7).intValue() == 5);
	}

	public void testBinary_3() throws IOException {
		String s = "X";

		// Bit, not byte aligned, 1 byte only
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeNBitUnsignedInteger(2, 3);
		bitEC.encodeBinary(s.getBytes());
		bitEC.encodeNBitUnsignedInteger(5, 7);
		bitEC.flush();
		DecoderChannel bitDC = getBitDecoder();
		assertTrue(bitDC.decodeNBitUnsignedIntegerValue(3).intValue() == 2);
		String sDec = new String(bitDC.decodeBinary());
		assertTrue(s.equals(sDec));
		assertTrue(bitDC.decodeNBitUnsignedIntegerValue(7).intValue() == 5);

		// Byte
		EncoderChannel byteEC = getByteEncoder();
		byteEC.encodeNBitUnsignedInteger(2, 3);
		byteEC.encodeBinary(s.getBytes());
		byteEC.encodeNBitUnsignedInteger(5, 7);
		DecoderChannel byteDC = getByteDecoder();
		assertTrue(byteDC.decodeNBitUnsignedIntegerValue(3).intValue() == 2);
		sDec = new String(byteDC.decodeBinary());
		assertTrue(s.equals(sDec));
		assertTrue(byteDC.decodeNBitUnsignedIntegerValue(7).intValue() == 5);
	}

	public void testHexBinaryFailure1() throws IOException, EXIException {
		StringValue src = new StringValue("ZHM=");

		Datatype binary = new BinaryHexDatatype(null);
		TypeEncoder typeEncoder = new TypedTypeEncoder();
		assertFalse(typeEncoder.isValid(binary, src));
	}

	public void testHexBinaryFailure2() throws IOException, EXIException {
		StringValue src = new StringValue(
				"RGFzIGlzIGphIGVpbiBmZXN0ZXIgQmxlZHNpbm4sIHdlaWwgVW1sYXV0ZSB3aWUg9iB1bmQg/CBtYWNoZW4gU2lubiwgd2llIGF1Y2ggZWluIHNjaGFyZmVzIN8u");

		Datatype binary = new BinaryHexDatatype(null);
		TypeEncoder typeEncoder = new TypedTypeEncoder();
		assertFalse(typeEncoder.isValid(binary, src));
	}

	public void testHexBinaryFailure3() throws IOException, EXIException {
		StringValue src = new StringValue("R0lGODlhAgSzzs7O3t7e");

		Datatype binary = new BinaryHexDatatype(null);
		TypeEncoder typeEncoder = new TypedTypeEncoder();
		assertFalse(typeEncoder.isValid(binary, src));
	}

	public void testHexBinaryFailure4() throws IOException, EXIException {
		StringValue src = new StringValue(
				"SMOkdHRlbiBIw7x0ZSBlaW4gw58gaW0gTmFtZW4sIHfDpHJlbiBzaWUgbcO2Z2xpY2hlcndlaXNlIGtlaW5lIEjDvHRlIG1laHIsDQpzb25kZXJuIEjDvMOfZS4NCg==");

		Datatype binary = new BinaryHexDatatype(null);
		TypeEncoder typeEncoder = new TypedTypeEncoder();
		assertFalse(typeEncoder.isValid(binary, src));
	}

	public void testBase64Failure1() throws IOException, EXIException {
		StringValue src = new StringValue("0FB7 x");

		Datatype binary = new BinaryBase64Datatype(null);
		TypeEncoder typeEncoder = new TypedTypeEncoder();
		assertFalse(typeEncoder.isValid(binary, src));
	}

	public void testBase64Failure2() throws IOException, EXIException {
		StringValue src = new StringValue(
				"g123456789ABCDEFabcdef0123456789ABCDEFabcdef0123456789ABCDEFabcdef0123456789ABCDEFabcdef0123456789ABCDEFabcdef0123456789ABCDEFabcdef0123456789ABCDEFabcdef0123456789ABCDEFabcdef0123456789ABCDEFabcdef0123456789ABCDEFabcdef0123456789ABCDEFabcdef0123456789ABCDEFabcdef0123456789ABCDEFabcdef0123456789ABCDEFabcdef0123456789ABCDEFabcdef0123456789ABCDEFabcdef0123456789ABCDEFabcdef0123456789ABCDEFabcdef0123456789ABCDEFabcdef0123456789ABCDEFabcdef0123456789ABCDEFabcdef");

		Datatype binary = new BinaryBase64Datatype(null);
		TypeEncoder typeEncoder = new TypedTypeEncoder();
		assertFalse(typeEncoder.isValid(binary, src));
	}

}