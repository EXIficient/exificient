/*
 * Copyright (C) 2007, 2008 Siemens AG
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

import com.siemens.ct.exi.Constants;
import com.siemens.ct.exi.exceptions.XMLParsingException;
import com.siemens.ct.exi.io.channel.EncoderChannel;
import com.siemens.ct.exi.util.datatype.XSDBoolean;

public class BooleanTest extends AbstractTestCase {

	XSDBoolean bool = XSDBoolean.newInstance();

	public BooleanTest(String testName) {
		super(testName);
	}

	public void testBoolean0() throws IOException, XMLParsingException {
		String s = "0";
		bool.parse(s);

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeBoolean(bool);
		bitEC.flush();
		assertTrue(getBitDecoder().decodeBoolean() == false);
		// Byte
		getByteEncoder().encodeBoolean(bool);
		assertTrue(getByteDecoder().decodeBoolean() == false);
	}

	public void testBoolean00() throws IOException, XMLParsingException {
		String s = "0";
		bool.parse(s);

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeBoolean(bool);
		bitEC.flush();
		assertTrue(getBitDecoder().decodeBooleanAsString().equals(
				Constants.DECODED_BOOLEAN_FALSE));
		// Byte
		getByteEncoder().encodeBoolean(bool);
		assertTrue(getByteDecoder().decodeBooleanAsString().equals(
				Constants.DECODED_BOOLEAN_FALSE));
	}

	public void testBooleanFalse() throws IOException, XMLParsingException {
		String s = "false";
		bool.parse(s);

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeBoolean(bool);
		bitEC.flush();
		assertTrue(getBitDecoder().decodeBoolean() == false);
		// Byte
		getByteEncoder().encodeBoolean(bool);
		assertTrue(getByteDecoder().decodeBoolean() == false);
	}

	public void testBoolean1() throws IOException, XMLParsingException {
		String s = "1";
		bool.parse(s);

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeBoolean(bool);
		bitEC.flush();
		assertTrue(getBitDecoder().decodeBoolean() == true);
		// Byte
		getByteEncoder().encodeBoolean(bool);
		assertTrue(getByteDecoder().decodeBoolean() == true);
	}

	public void testBoolean11() throws IOException, XMLParsingException {
		String s = "1";
		bool.parse(s);

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeBoolean(bool);
		bitEC.flush();
		assertTrue(getBitDecoder().decodeBooleanAsString().equals(
				Constants.DECODED_BOOLEAN_TRUE));
		// Byte
		getByteEncoder().encodeBoolean(bool);
		assertTrue(getByteDecoder().decodeBooleanAsString().equals(
				Constants.DECODED_BOOLEAN_TRUE));
	}

	public void testBooleanTrue() throws IOException, XMLParsingException {
		String s = "true";
		bool.parse(s);

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeBoolean(bool);
		bitEC.flush();
		assertTrue(getBitDecoder().decodeBoolean() == true);
		// Byte
		getByteEncoder().encodeBoolean(bool);
		assertTrue(getByteDecoder().decodeBoolean() == true);
	}

	public void testBooleanFailure() throws IOException {
		try {
			String s = "trueX";
			bool.parse(s);

			assertTrue(false);

		} catch (XMLParsingException e) {
			// ok
		}
	}

}