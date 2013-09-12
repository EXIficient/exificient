/*
 * Copyright (C) 2007-2012 Siemens AG
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
import java.math.BigDecimal;

import com.siemens.ct.exi.io.channel.DecoderChannel;
import com.siemens.ct.exi.io.channel.EncoderChannel;
import com.siemens.ct.exi.values.DecimalValue;

public class DecimalTest extends AbstractTestCase {

	public DecimalTest(String testName) {
		super(testName);
	}

	public void testDecimal0() throws IOException {
		String s = "-1.23";
		DecimalValue d = DecimalValue.parse(s);
		assertTrue(d != null);

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeDecimal(d.isNegative(), d.getIntegral(),
				d.getRevFractional());
		bitEC.flush();
		BigDecimal a = getBitDecoder().decodeDecimalValue().toBigDecimal();
		assertTrue(a.equals(new BigDecimal(s)));
		// Byte
		getByteEncoder().encodeDecimal(d.isNegative(), d.getIntegral(),
				d.getRevFractional());
		assertTrue(getByteDecoder().decodeDecimalValue().toBigDecimal()
				.equals(new BigDecimal(s)));
	}

	public void testDecimal1() throws IOException {
		String s = "12678967.543233";
		DecimalValue d = DecimalValue.parse(s);
		assertTrue(d != null);

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeDecimal(d.isNegative(), d.getIntegral(),
				d.getRevFractional());
		bitEC.flush();
		BigDecimal bdBit = getBitDecoder().decodeDecimalValue().toBigDecimal();
		assertTrue(bdBit + "!=" + new BigDecimal(s),
				bdBit.equals(new BigDecimal(s)));
		// Byte
		getByteEncoder().encodeDecimal(d.isNegative(), d.getIntegral(),
				d.getRevFractional());
		assertTrue(getByteDecoder().decodeDecimalValue().toBigDecimal()
				.equals(new BigDecimal(s)));
	}

	public void testDecimal2() throws IOException {
		String s = "+100000.0012";
		DecimalValue d = DecimalValue.parse(s);
		assertTrue(d != null);

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeDecimal(d.isNegative(), d.getIntegral(),
				d.getRevFractional());
		bitEC.flush();
		assertTrue(getBitDecoder().decodeDecimalValue().toBigDecimal()
				.equals(new BigDecimal(s)));
		// Byte
		getByteEncoder().encodeDecimal(d.isNegative(), d.getIntegral(),
				d.getRevFractional());
		assertTrue(getByteDecoder().decodeDecimalValue().toBigDecimal()
				.equals(new BigDecimal(s)));
	}

	public void testDecimal3() throws IOException {
		String s = "210";
		DecimalValue d = DecimalValue.parse(s);
		assertTrue(d != null);

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeDecimal(d.isNegative(), d.getIntegral(),
				d.getRevFractional());
		bitEC.flush();
		BigDecimal bdBit = getBitDecoder().decodeDecimalValue().toBigDecimal();
		assertTrue(bdBit + "!=" + new BigDecimal(s + ".0"),
				bdBit.equals(new BigDecimal(s + ".0")));
		// Byte
		getByteEncoder().encodeDecimal(d.isNegative(), d.getIntegral(),
				d.getRevFractional());
		assertTrue(getByteDecoder().decodeDecimalValue().toBigDecimal()
				.equals(new BigDecimal(s + ".0")));
	}

	public void testDecimal4() throws IOException {
		String s = "380";
		DecimalValue d = DecimalValue.parse(s);
		assertTrue(d != null);

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeDecimal(d.isNegative(), d.getIntegral(),
				d.getRevFractional());
		bitEC.flush();
		assertTrue((s + ".0").equals(getBitDecoder().decodeDecimalValue()
				.toString()));
		// Byte
		getByteEncoder().encodeDecimal(d.isNegative(), d.getIntegral(),
				d.getRevFractional());
		assertTrue((s + ".0").equals(getByteDecoder().decodeDecimalValue()
				.toString()));
	}

	public void testDecimal5() throws IOException {
		String s = "0.001359";
		DecimalValue d = DecimalValue.parse(s);
		assertTrue(d != null);

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeDecimal(d.isNegative(), d.getIntegral(),
				d.getRevFractional());
		bitEC.flush();
		assertTrue(s.equals(getBitDecoder().decodeDecimalValue().toString()));
		// Byte
		getByteEncoder().encodeDecimal(d.isNegative(), d.getIntegral(),
				d.getRevFractional());
		assertTrue(s.equals(getByteDecoder().decodeDecimalValue().toString()));
	}

	public void testDecimal6() throws IOException {
		String s = "110.74080";
		DecimalValue d = DecimalValue.parse(s);
		assertTrue(d != null);

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeDecimal(d.isNegative(), d.getIntegral(),
				d.getRevFractional());
		bitEC.flush();
		String sBit = getBitDecoder().decodeDecimalValue().toString();
		assertTrue(sBit + "!=" + "110.7408", "110.7408".equals(sBit));
		// Byte
		getByteEncoder().encodeDecimal(d.isNegative(), d.getIntegral(),
				d.getRevFractional());
		assertTrue("110.7408".equals(getByteDecoder().decodeDecimalValue()
				.toString()));
	}

	public void testDecimal7() throws IOException {
		String s = "55000.0";
		DecimalValue d = DecimalValue.parse(s);
		assertTrue(d != null);

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeDecimal(d.isNegative(), d.getIntegral(),
				d.getRevFractional());
		bitEC.flush();
		DecoderChannel bitDC = getBitDecoder();
		assertTrue(s.equals(bitDC.decodeDecimalValue().toString()));
		// Byte
		EncoderChannel byteEC = getByteEncoder();
		byteEC.encodeDecimal(d.isNegative(), d.getIntegral(),
				d.getRevFractional());
		DecoderChannel byteDC = getByteDecoder();
		assertTrue(s.equals(byteDC.decodeDecimalValue().toString()));
	}

	public void testDecimal8() throws IOException {
		String s = "3.141592653589";
		DecimalValue d = DecimalValue.parse(s);
		assertTrue(d != null);

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeDecimal(d.isNegative(), d.getIntegral(),
				d.getRevFractional());
		bitEC.flush();
		String sBit = getBitDecoder().decodeDecimalValue().toString();
		assertTrue(sBit + "!=" + s, s.equals(sBit));
		// Byte
		getByteEncoder().encodeDecimal(d.isNegative(), d.getIntegral(),
				d.getRevFractional());
		assertTrue(s.equals(getByteDecoder().decodeDecimalValue().toString()));
	}

	public void testDecimal9() throws Exception {
		String s = "-.1";
		String s2 = "-0.1";

		DecimalValue d = DecimalValue.parse(s);
		assertTrue(d != null);

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeDecimal(d.isNegative(), d.getIntegral(),
				d.getRevFractional());
		bitEC.flush();
		String sBit = getBitDecoder().decodeDecimalValue().toString();
		assertTrue(sBit + "!=" + s2, s2.equals(sBit));
		// Byte
		getByteEncoder().encodeDecimal(d.isNegative(), d.getIntegral(),
				d.getRevFractional());
		assertTrue(s2.equals(getByteDecoder().decodeDecimalValue().toString()));
	}

	public void testDecimal10() throws Exception {
		String s = "-.234";
		String s2 = "-0.234";

		DecimalValue d = DecimalValue.parse(s);
		assertTrue(d != null);

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeDecimal(d.isNegative(), d.getIntegral(),
				d.getRevFractional());
		bitEC.flush();
		String sBit = getBitDecoder().decodeDecimalValue().toString();
		assertTrue(sBit + "!=" + s2, s2.equals(sBit));
		// Byte
		getByteEncoder().encodeDecimal(d.isNegative(), d.getIntegral(),
				d.getRevFractional());
		assertTrue(s2.equals(getByteDecoder().decodeDecimalValue().toString()));
	}

	public void testDecimalBig1() throws IOException {
		String s = "36.087139166666670000000000000000001";
		DecimalValue d = DecimalValue.parse(s);
		assertTrue(d != null);

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeDecimal(d.isNegative(), d.getIntegral(),
				d.getRevFractional());
		bitEC.flush();
		String sBit = getBitDecoder().decodeDecimalValue().toString();
		assertTrue(sBit + "!=" + s, s.equals(sBit));
		// Byte
		getByteEncoder().encodeDecimal(d.isNegative(), d.getIntegral(),
				d.getRevFractional());
		assertTrue(s.equals(getByteDecoder().decodeDecimalValue().toString()));
	}

	// deviation 8
	public void testDecimalBig2() throws IOException {
		String s = "123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890.1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678912345678901234567890123456789012345678901234567890123456789012345678901234567890123456789";
		DecimalValue d = DecimalValue.parse(s);
		assertTrue(d != null);

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeDecimal(d.isNegative(), d.getIntegral(),
				d.getRevFractional());
		bitEC.flush();
		DecimalValue dv1 = getBitDecoder().decodeDecimalValue();
		String sBit = dv1.toString();
		assertTrue(sBit + "!=" + s, s.equals(sBit));
		// Byte
		getByteEncoder().encodeDecimal(d.isNegative(), d.getIntegral(),
				d.getRevFractional());
		assertTrue(s.equals(getByteDecoder().decodeDecimalValue().toString()));
	}

	public void testDecimalFail1() throws IOException {
		String s = "9.213.456";
		DecimalValue d = DecimalValue.parse(s);
		assertFalse(d != null);

	}

}