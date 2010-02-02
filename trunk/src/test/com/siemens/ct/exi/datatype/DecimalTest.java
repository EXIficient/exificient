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
import java.math.BigDecimal;

import com.siemens.ct.exi.io.channel.DecoderChannel;
import com.siemens.ct.exi.io.channel.EncoderChannel;
import com.siemens.ct.exi.util.datatype.XSDDecimal;

public class DecimalTest extends AbstractTestCase {
	private XSDDecimal d = XSDDecimal.newInstance();

	public DecimalTest(String testName) {
		super(testName);
	}

	public void testDecimal0() throws IOException {
		String s = "-1.23";
		boolean valid = d.parse(s);
		assertTrue(valid);

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeDecimal(d.isNegative(), d.getIntegral(), d.getReverseFractional());
		bitEC.flush();
		BigDecimal a= getBitDecoder().decodeDecimalValue().toBigDecimal();
		assertTrue(a.equals(new BigDecimal(s)));
		// Byte
		getByteEncoder().encodeDecimal(d.isNegative(), d.getIntegral(), d.getReverseFractional());
		assertTrue(getByteDecoder().decodeDecimalValue().toBigDecimal().equals(new BigDecimal(s)));
	}

	public void testDecimal1() throws IOException {
		String s = "12678967.543233";
		boolean valid = d.parse(s);
		assertTrue(valid);

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeDecimal(d.isNegative(), d.getIntegral(), d.getReverseFractional());
		bitEC.flush();
		BigDecimal bdBit = getBitDecoder().decodeDecimalValue().toBigDecimal();
		assertTrue(bdBit + "!=" + new BigDecimal(s), bdBit
				.equals(new BigDecimal(s)));
		// Byte
		getByteEncoder().encodeDecimal(d.isNegative(), d.getIntegral(), d.getReverseFractional());
		assertTrue(getByteDecoder().decodeDecimalValue().toBigDecimal().equals(new BigDecimal(s)));
	}

	public void testDecimal2() throws IOException {
		String s = "+100000.0012";
		boolean valid = d.parse(s);
		assertTrue(valid);

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeDecimal(d.isNegative(), d.getIntegral(), d.getReverseFractional());
		bitEC.flush();
		assertTrue(getBitDecoder().decodeDecimalValue().toBigDecimal().equals(new BigDecimal(s)));
		// Byte
		getByteEncoder().encodeDecimal(d.isNegative(), d.getIntegral(), d.getReverseFractional());
		assertTrue(getByteDecoder().decodeDecimalValue().toBigDecimal().equals(new BigDecimal(s)));
	}

	public void testDecimal3() throws IOException {
		String s = "210";
		boolean valid = d.parse(s);
		assertTrue(valid);

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeDecimal(d.isNegative(), d.getIntegral(), d.getReverseFractional());
		bitEC.flush();
		BigDecimal bdBit = getBitDecoder().decodeDecimalValue().toBigDecimal();
		assertTrue(bdBit + "!=" + new BigDecimal(s + ".0"), bdBit
				.equals(new BigDecimal(s + ".0")));
		// Byte
		getByteEncoder().encodeDecimal(d.isNegative(), d.getIntegral(), d.getReverseFractional());
		assertTrue(getByteDecoder().decodeDecimalValue().toBigDecimal().equals(
				new BigDecimal(s + ".0")));
	}

	public void testDecimal4() throws IOException {
		String s = "380";
		boolean valid = d.parse(s);
		assertTrue(valid);

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeDecimal(d.isNegative(), d.getIntegral(), d.getReverseFractional());
		bitEC.flush();
		assertTrue((s + ".0").equals(getBitDecoder().decodeDecimalValue().toString()));
		// Byte
		getByteEncoder().encodeDecimal(d.isNegative(), d.getIntegral(), d.getReverseFractional());
		assertTrue((s + ".0").equals(getByteDecoder().decodeDecimalValue().toString()));
	}

	public void testDecimal5() throws IOException {
		String s = "0.001359";
		boolean valid = d.parse(s);
		assertTrue(valid);

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeDecimal(d.isNegative(), d.getIntegral(), d.getReverseFractional());
		bitEC.flush();
		assertTrue(s.equals(getBitDecoder().decodeDecimalValue().toString()));
		// Byte
		getByteEncoder().encodeDecimal(d.isNegative(), d.getIntegral(), d.getReverseFractional());
		assertTrue(s.equals(getByteDecoder().decodeDecimalValue().toString()));
	}

	public void testDecimal6() throws IOException {
		String s = "110.74080";
		boolean valid = d.parse(s);
		assertTrue(valid);

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeDecimal(d.isNegative(), d.getIntegral(), d.getReverseFractional());
		bitEC.flush();
		String sBit = getBitDecoder().decodeDecimalValue().toString();
		assertTrue(new String(sBit) + "!=" + "110.7408", "110.7408".equals(sBit));
		// Byte
		getByteEncoder().encodeDecimal(d.isNegative(), d.getIntegral(), d.getReverseFractional());
		assertTrue("110.7408".equals(getByteDecoder().decodeDecimalValue().toString()));
	}

	public void testDecimal7() throws IOException {
		String s = "55000.0";
		boolean valid = d.parse(s);
		assertTrue(valid);

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeDecimal(d.isNegative(), d.getIntegral(), d.getReverseFractional());
		bitEC.flush();
		DecoderChannel bitDC = getBitDecoder();
		assertTrue(s.equals(bitDC.decodeDecimalValue().toString()));
		// Byte
		EncoderChannel byteEC = getByteEncoder();
		byteEC.encodeDecimal(d.isNegative(), d.getIntegral(), d.getReverseFractional());
		DecoderChannel byteDC = getByteDecoder();
		assertTrue(s.equals(byteDC.decodeDecimalValue().toString()));
	}

	// public void testDecimal8() throws IOException
	// {
	// String s1 = "55000.0";
	// String s2 = "44000.0";
	//    	
	// // Bit
	// EncoderChannel bitEC = getBitEncoder();
	// bitEC.encodeDecimal( s1 );
	// bitEC.encodeDecimal( s2 );
	// bitEC.flush();
	// DecoderChannel bitDC = getBitDecoder();
	// assertTrue( bitDC.decodeDecimalAsString().equals( s1 ) );
	// assertTrue( bitDC.decodeDecimalAsString().equals( s2 ) );
	// // Byte
	//    	
	// EncoderChannel byteEC = getByteEncoder();
	// byteEC.encodeDecimal( s1 );
	// byteEC.encodeDecimal( s2 );
	// ByteDecoderChannel byteDC = (ByteDecoderChannel)getByteDecoder();
	// SkippableDecoderChannel sdc = new SkippableByteDecoderChannel(
	// byteDC.getInputStream ( ) );
	//        
	// assertTrue( sdc.decodeDecimalAsString().equals( s1 ) );
	// assertTrue( sdc.decodeDecimalAsString().equals( s2 ) );
	// }

	public void testDecimal9() throws IOException {
		String s = "3.141592653589";
		boolean valid = d.parse(s);
		assertTrue(valid);

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeDecimal(d.isNegative(), d.getIntegral(), d.getReverseFractional());
		bitEC.flush();
		String sBit = getBitDecoder().decodeDecimalValue().toString();
		assertTrue(new String(sBit) + "!=" + s, s.equals(sBit));
		// Byte
		getByteEncoder().encodeDecimal(d.isNegative(), d.getIntegral(), d.getReverseFractional());
		assertTrue(s.equals(getByteDecoder().decodeDecimalValue().toString()));
	}

	public void testDecimalBig1() throws IOException {
		String s = "36.087139166666670000000000000000001";
		boolean valid = d.parse(s);
		assertTrue(valid);

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeDecimal(d.isNegative(), d.getIntegral(), d.getReverseFractional());
		bitEC.flush();
		String sBit = getBitDecoder().decodeDecimalValue().toString();
		assertTrue(new String(sBit) + "!=" + s, s.equals(sBit));
		// Byte
		getByteEncoder().encodeDecimal(d.isNegative(), d.getIntegral(), d.getReverseFractional());
		assertTrue(s.equals(getByteDecoder().decodeDecimalValue().toString()));
	}
	
	public void testDecimalFail1() throws IOException {
		String s = "9.213.456";
		boolean valid = d.parse(s);
		assertFalse(valid);

	}
	

}