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

import com.siemens.ct.exi.io.channel.EncoderChannel;

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
		String s = "äüß";
		
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
		String s = "Michèle";
		
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
		String s = "Miche\u0300le"; //U+0300 is the COMBINING GRAVE ACCENT
		
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
		// 27700 (hex 6C34) 	water (Chinese)
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
		String s = "abcd\u5B66\uD800\uDF30";	// surrogate pair
		
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