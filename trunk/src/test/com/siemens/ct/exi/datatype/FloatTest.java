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
import java.math.BigDecimal;

import com.siemens.ct.exi.io.channel.EncoderChannel;
import com.siemens.ct.exi.util.datatype.XSDDouble;
import com.siemens.ct.exi.util.datatype.XSDFloat;

public class FloatTest extends AbstractTestCase {
	XSDFloat f = XSDFloat.newInstance();
	XSDDouble d = XSDDouble.newInstance();

	public FloatTest(String testName) {
		super(testName);
	}

	public void testFloatType18_4() throws IOException {
		float f = 18.4f; // 18.39999962
		
		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeFloat(f);
		bitEC.flush();
		float df = getBitDecoder().decodeFloatValue().toFloat();

		float diff = Math.abs(f-df);
		assertTrue(diff < 0.000001);
	}
	
	public void testFloatType12_25() throws IOException {
		float f = 12.25f;

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeFloat(f);
		bitEC.flush();
		float df = getBitDecoder().decodeFloatValue().toFloat();

		float diff = Math.abs(f-df);
		assertTrue(diff < 0.000001);
	}
	
	public void testFloatType12_75() throws IOException {
		 float f = 12.75f;

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeFloat(f);
		bitEC.flush();
		float df = getBitDecoder().decodeFloatValue().toFloat();

		float diff = Math.abs(f-df);
		assertTrue(diff < 0.000001);
	}
	
	public void testFloatType1567_0() throws IOException {
		float f = 1567.0f;

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeFloat(f);
		bitEC.flush();
		float df = getBitDecoder().decodeFloatValue().toFloat();

		float diff = Math.abs(f-df);
		assertTrue(diff < 0.000001);
	}
	
	public void testFloatTypeNeg0_333() throws IOException {
		float f = -0.333f;

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeFloat(f);
		bitEC.flush();
		float df = getBitDecoder().decodeFloatValue().toFloat();

		float diff = Math.abs(f-df);
		assertTrue(diff < 0.000001);
	}
	
	public void testFloatType0() throws IOException {
		float f = 0f;

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeFloat(f);
		bitEC.flush();
		float df = getBitDecoder().decodeFloatValue().toFloat();

		float diff = Math.abs(f-df);
		assertTrue(diff < 0.000001);
	}
	
	public void testFloatType0_33333() throws IOException {
		float f = 1f/3f;

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeFloat(f);
		bitEC.flush();
		float df = getBitDecoder().decodeFloatValue().toFloat();

		float diff = Math.abs(f-df);
		assertTrue(diff < 0.000001);
	}
	
	
	
	public void testFloatNaN() throws IOException {
		String s = "NaN";
		boolean valid = f.parse(s);
		assertTrue(valid);

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeFloat(f.mantissa, f.exponent);
		bitEC.flush();
		char[] sb = getBitDecoder().decodeFloatValue().toCharacters();
		assertTrue(equals(sb, s));
		// Byte
		getByteEncoder().encodeFloat(f.mantissa, f.exponent);
		char[] sB = getByteDecoder().decodeFloatValue().toCharacters();
		assertTrue(equals(sB, s));
	}

	public void testFloatINF() throws IOException {
		String s = "INF";
		boolean valid = f.parse(s);
		assertTrue(valid);

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeFloat(f.mantissa, f.exponent);
		bitEC.flush();
		assertTrue(equals(getBitDecoder().decodeFloatValue().toCharacters(), s));
		// Byte
		getByteEncoder().encodeFloat(f.mantissa, f.exponent);
		assertTrue(equals(getByteDecoder().decodeFloatValue().toCharacters(), s));
	}

	public void testFloatMINF() throws IOException {
		String s = "-INF";
		boolean valid = f.parse(s);
		assertTrue(valid);

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeFloat(f.mantissa, f.exponent);
		bitEC.flush();
		assertTrue(equals(getBitDecoder().decodeFloatValue().toCharacters(), s));
		// Byte
		getByteEncoder().encodeFloat(f.mantissa, f.exponent);
		assertTrue(equals(getByteDecoder().decodeFloatValue().toCharacters(), s));
	}

	public void testFloat0() throws IOException {
		String s = "-1E4";
		boolean valid = f.parse(s);
		assertTrue(valid);

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeFloat(f.mantissa, f.exponent);
		bitEC.flush();
		char[] sb = getBitDecoder().decodeFloatValue().toCharacters();
		assertTrue(s + " != " + new String(sb), new BigDecimal(s).compareTo(new BigDecimal(
				sb)) == 0);
		// Byte
		getByteEncoder().encodeFloat(f.mantissa, f.exponent);
		char[] sB = getByteDecoder().decodeFloatValue().toCharacters();
		assertTrue(s + " != " + new String(sb), new BigDecimal(s).compareTo(new BigDecimal(
				sB)) == 0);
	}

	public void testFloat1() throws IOException {
		String s = "1267.43233E12";
		boolean valid = f.parse(s);
		assertTrue(valid);

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeFloat(f.mantissa, f.exponent);
		bitEC.flush();
		char[] sb = getBitDecoder().decodeFloatValue().toCharacters();
		assertTrue(s + " != " + new String(sb), new BigDecimal(s).compareTo(new BigDecimal(
				sb)) == 0);
		// Byte
		getByteEncoder().encodeFloat(f.mantissa, f.exponent);
		char[] sB = getByteDecoder().decodeFloatValue().toCharacters();
		assertTrue(s + " != " + new String(sb), new BigDecimal(s).compareTo(new BigDecimal(
				sB)) == 0);
	}

	public void testFloat2() throws IOException {
		String s = "12.78e-2";
		boolean valid = f.parse(s);
		assertTrue(valid);

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeFloat(f.mantissa, f.exponent);
		bitEC.flush();
		char[] sb = getBitDecoder().decodeFloatValue().toCharacters();
		assertTrue(s + " != " + new String(sb), new BigDecimal(s).compareTo(new BigDecimal(
				sb)) == 0);
		// Byte
		getByteEncoder().encodeFloat(f.mantissa, f.exponent);
		char[] sB = getByteDecoder().decodeFloatValue().toCharacters();
		assertTrue(s + " != " + new String(sb), new BigDecimal(s).compareTo(new BigDecimal(
				sB)) == 0);
	}

	public void testFloat3() throws IOException {
		String s = "12";
		boolean valid = f.parse(s);
		assertTrue(valid);

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeFloat(f.mantissa, f.exponent);
		bitEC.flush();
		char[] sb = getBitDecoder().decodeFloatValue().toCharacters();
		assertTrue(s + " != " + new String(sb), new BigDecimal(s).compareTo(new BigDecimal(
				sb)) == 0);
		// Byte
		getByteEncoder().encodeFloat(f.mantissa, f.exponent);
		char[] sB = getByteDecoder().decodeFloatValue().toCharacters();
		assertTrue(s + " != " + new String(sb), new BigDecimal(s).compareTo(new BigDecimal(
				sB)) == 0);
	}

	public void testFloat4() throws IOException {
		String s = "0";
		boolean valid = f.parse(s);
		assertTrue(valid);

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeFloat(f.mantissa, f.exponent);
		bitEC.flush();
		char[] sb = getBitDecoder().decodeFloatValue().toCharacters();
		assertTrue(s + " != " + new String(sb), new BigDecimal(s).compareTo(new BigDecimal(
				sb)) == 0);
		// Byte
		getByteEncoder().encodeFloat(f.mantissa, f.exponent);
		char[] sB = getByteDecoder().decodeFloatValue().toCharacters();
		assertTrue(s + " != " + new String(sb), new BigDecimal(s).compareTo(new BigDecimal(
				sB)) == 0);
	}

	public void testFloat5() throws IOException {
		String s = "-0";
		boolean valid = f.parse(s);
		assertTrue(valid);

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeFloat(f.mantissa, f.exponent);
		bitEC.flush();
		char[] sb = getBitDecoder().decodeFloatValue().toCharacters();
		assertTrue(s + " != " + new String(sb), new BigDecimal(s).compareTo(new BigDecimal(
				sb)) == 0);
		// Byte
		getByteEncoder().encodeFloat(f.mantissa, f.exponent);
		char[] sB = getByteDecoder().decodeFloatValue().toCharacters();
		assertTrue(s + " != " + new String(sb), new BigDecimal(s).compareTo(new BigDecimal(
				sB)) == 0);
	}

	public void testFloat6() throws IOException {
		String s = "-1";
		boolean valid = f.parse(s);
		assertTrue(valid);

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeFloat(f.mantissa, f.exponent);
		bitEC.flush();
		char[] sb = getBitDecoder().decodeFloatValue().toCharacters();
		assertTrue(s + " != " + new String(sb), new BigDecimal(s).compareTo(new BigDecimal(
				sb)) == 0);
		// Byte
		getByteEncoder().encodeFloat(f.mantissa, f.exponent);
		char[] sB = getByteDecoder().decodeFloatValue().toCharacters();
		assertTrue(s + " != " + new String(sb), new BigDecimal(s).compareTo(new BigDecimal(
				sB)) == 0);
	}

	public void testDouble1() throws IOException {
		String s = "119.999999999929";
		boolean valid = d.parse(s);
		assertTrue(valid);

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeDouble(d.mantissa, d.exponent);
		bitEC.flush();
		char[] sb = getBitDecoder().decodeDoubleValue().toCharacters();
		assertTrue(s + " != " + new String(sb), new BigDecimal(s).compareTo(new BigDecimal(
				sb)) == 0);
		// Byte
		getByteEncoder().encodeDouble(d.mantissa, d.exponent);
		char[] sB = getByteDecoder().decodeDoubleValue().toCharacters();
		assertTrue(s + " != " + new String(sb), new BigDecimal(s).compareTo(new BigDecimal(
				sB)) == 0);
	}

	public void testDouble2() throws IOException {
		String s = "000123400.0031200";
		boolean valid = d.parse(s);
		assertTrue(valid);

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeDouble(d.mantissa, d.exponent);
		bitEC.flush();
		char[] sb = getBitDecoder().decodeDoubleValue().toCharacters();
		assertTrue(s + " != " + new String(sb), new BigDecimal(s).compareTo(new BigDecimal(
				sb)) == 0);
		// Byte
		getByteEncoder().encodeDouble(d.mantissa, d.exponent);
		char[] sB = getByteDecoder().decodeDoubleValue().toCharacters();
		assertTrue(s + " != " + new String(sb), new BigDecimal(s).compareTo(new BigDecimal(
				sB)) == 0);
	}
	
	public void testDoubleType18_4() throws IOException {
		double d = 18.4d;
		
		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeDouble(d);
		bitEC.flush();
		double dd = getBitDecoder().decodeDoubleValue().toDouble();

		double diff = Math.abs(d-dd);
		assertTrue(diff < 0.00000000001);
	}
	
	public void testDoubleType1_123456789() throws IOException {
		double d = 1.123456789d;
		
		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeDouble(d);
		bitEC.flush();
		double dd = getBitDecoder().decodeDoubleValue().toDouble();

		double diff = Math.abs(d-dd);
		assertTrue(diff < 0.00000000001);
	}
	
	public void testDoubleTypeNeg56783_132154() throws IOException {
		double d = 56783.132154d;
		
		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeDouble(d);
		bitEC.flush();
		double dd = getBitDecoder().decodeDoubleValue().toDouble();

		double diff = Math.abs(d-dd);
		assertTrue(diff < 0.00000000001);
	}

	public void testFloatInvalid0() {
		String s = "x11.1";

			boolean valid = f.parse(s);
			assertFalse("Invalid float '" + s + "' parsed successfully", valid);
	}

	public void testFloatInvalid1() {
		String s = "";

			boolean valid = f.parse(s);
			assertFalse("Invalid float value '" + s + "' parsed successfully",
					valid);
		
	}

	public void testFloatInvalid2() {
		String s = "1.1223x";

			boolean valid = f.parse(s);
			assertFalse("Invalid float value '" + s + "' parsed successfully",
					valid);
	}

}