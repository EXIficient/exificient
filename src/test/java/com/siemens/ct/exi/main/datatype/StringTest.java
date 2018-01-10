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

public class StringTest extends AbstractTestCase {

	public StringTest(String testName) {
		super(testName);
	}

	public void testString1() throws IOException {
		String s = "abc";

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeString(s);
		bitEC.flush();
		char[] sd1 = getBitDecoder().decodeString();
		assertTrue(s.equals(new String(sd1)));

		// Byte
		getByteEncoder().encodeString(s);
		char[] sd2 = getByteDecoder().decodeString();
		assertTrue(s.equals(new String(sd2)));
	}

	public void testString2() throws IOException {
		String s = "";

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeString(s);
		bitEC.flush();
		char[] sd1 = getBitDecoder().decodeString();
		assertTrue(s.equals(new String(sd1)));

		// Byte
		getByteEncoder().encodeString(s);
		char[] sd2 = getByteDecoder().decodeString();
		assertTrue(s.equals(new String(sd2)));
	}

	public void testStringUmlaute() throws IOException {
		String s = "���";

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeString(s);
		bitEC.flush();
		char[] sd1 = getBitDecoder().decodeString();
		assertTrue(s.equals(new String(sd1)));

		// Byte
		getByteEncoder().encodeString(s);
		char[] sd2 = getByteDecoder().decodeString();
		assertTrue(s.equals(new String(sd2)));
	}

	public void testStringSpecial1() throws IOException {
		String s = "Mich�le";

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeString(s);
		bitEC.flush();
		char[] sd1 = getBitDecoder().decodeString();
		assertTrue(s.equals(new String(sd1)));

		// Byte
		getByteEncoder().encodeString(s);
		char[] sd2 = getByteDecoder().decodeString();
		assertTrue(s.equals(new String(sd2)));
	}

	public void testStringSpecial2() throws IOException {
		String s = "Miche\u0300le"; // U+0300 is the COMBINING GRAVE ACCENT

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeString(s);
		bitEC.flush();
		char[] sd1 = getBitDecoder().decodeString();
		assertTrue(s.equals(new String(sd1)));

		// Byte
		getByteEncoder().encodeString(s);
		char[] sd2 = getByteDecoder().decodeString();
		assertTrue(s.equals(new String(sd2)));
	}

	public void testStringSpecial3() throws IOException {
		// 27700 (hex 6C34) water (Chinese)
		String s = "\u6C34";

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeString(s);
		bitEC.flush();
		char[] sd1 = getBitDecoder().decodeString();
		assertTrue(s.equals(new String(sd1)));

		// Byte
		getByteEncoder().encodeString(s);
		char[] sd2 = getByteDecoder().decodeString();
		assertTrue(s.equals(new String(sd2)));
	}

	public void testStringSpecial4() throws IOException {
		String s = "abcd\u5B66\uD800\uDF30"; // surrogate pair

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeString(s);
		bitEC.flush();
		char[] sd1 = getBitDecoder().decodeString();
		assertTrue(s.equals(new String(sd1)));

		// Byte
		getByteEncoder().encodeString(s);
		char[] sd2 = getByteDecoder().decodeString();
		assertTrue(s.equals(new String(sd2)));
	}

	public void testStringSpecial5() throws IOException {
		String s = "\uD834\uDD1E \uD834\uDD1E"; // surrogate pair

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeString(s);
		bitEC.flush();
		char[] sd1 = getBitDecoder().decodeString();
		assertTrue(s.equals(new String(sd1)));

		// Byte
		getByteEncoder().encodeString(s);
		char[] sd2 = getByteDecoder().decodeString();
		assertTrue(s.equals(new String(sd2)));
	}

	public void testStringHighSurrogatePairs1() throws IOException {
		String s = "\uD834\uDD1E"; // for U+1D11E == 119070
		assertTrue(s.length() == 2); // char length
		assertTrue(s.codePointCount(0, s.length()) == 1); // characters length

		/*
		 * Test Encode
		 */
		{
			EncoderChannel bitEC = getBitEncoder();
			bitEC.encodeString(s);
			bitEC.flush();
			DecoderChannel bitDC = getBitDecoder();
			int charactersLen = bitDC.decodeUnsignedInteger();
			assertTrue(charactersLen == 1);
			int cp = bitDC.decodeUnsignedInteger();
			assertTrue(cp == 119070);
		}

		/*
		 * TestDecode
		 */
		{
			EncoderChannel bitEC = getBitEncoder();
			bitEC.encodeUnsignedInteger(1);
			bitEC.encodeUnsignedInteger(119070);
			bitEC.flush();
			DecoderChannel bitDC = getBitDecoder();
			char[] ca = bitDC.decodeString();
			assertTrue(s.equals(new String(ca)));
		}

	}

	public void testStringHighSurrogatePairs2() throws IOException {
		String s = "\uD834\uDD1E \uD834\uDD1B"; // for U+1D11E + ws + U+1D11B

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeString(s);
		bitEC.flush();
		char[] sd1 = getBitDecoder().decodeString();
		assertTrue(s.equals(new String(sd1)));

		// Byte
		getByteEncoder().encodeString(s);
		char[] sd2 = getByteDecoder().decodeString();
		assertTrue(s.equals(new String(sd2)));
	}

	public void testStringHighSurrogatePairs3() throws IOException {
		String s = "ahoi .. &�� \uD834\uDD1B";

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeString(s);
		bitEC.flush();
		char[] sd1 = getBitDecoder().decodeString();
		assertTrue(s.equals(new String(sd1)));

		// Byte
		getByteEncoder().encodeString(s);
		char[] sd2 = getByteDecoder().decodeString();
		assertTrue(s.equals(new String(sd2)));
	}

	public void testNonBMP1() throws IOException {
		// "&#x10FFF;" NON BMP ( == 69631 )
		// X &#x10FFF; Y
		String s = new StringBuilder().append("X ").appendCodePoint(0x10FFF)
				.append(" Y").toString();

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeString(s);
		bitEC.flush();
		char[] sd1 = getBitDecoder().decodeString();
		assertTrue(s.equals(new String(sd1)));

		// Byte
		getByteEncoder().encodeString(s);
		char[] sd2 = getByteDecoder().decodeString();
		assertTrue(s.equals(new String(sd2)));
	}

}