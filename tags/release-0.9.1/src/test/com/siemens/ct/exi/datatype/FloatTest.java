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

import sun.misc.DoubleConsts;
import sun.misc.FloatConsts;

import com.siemens.ct.exi.Constants;
import com.siemens.ct.exi.io.channel.EncoderChannel;
import com.siemens.ct.exi.values.FloatValue;

public class FloatTest extends AbstractTestCase {

	public FloatTest(String testName) {
		super(testName);
	}

	public void testFloatType18_4() throws IOException {
		float f = 18.4f; // 18.39999962
		FloatValue fv = FloatValue.parse(f);
		assertTrue(fv != null);

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeFloat(fv);
		bitEC.flush();
		float df = getBitDecoder().decodeFloatValue().toFloat();

		float diff = Math.abs(f - df);
		assertTrue(diff < 0.000001);
	}

	public void testFloatType12_25() throws IOException {
		float f = 12.25f;
		FloatValue fv = FloatValue.parse(f);
		assertTrue(fv != null);

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeFloat(fv);
		bitEC.flush();
		float df = getBitDecoder().decodeFloatValue().toFloat();

		float diff = Math.abs(f - df);
		assertTrue(diff < 0.000001);
	}

	public void testFloatType12_75() throws IOException {
		float f = 12.75f;
		FloatValue fv = FloatValue.parse(f);
		assertTrue(fv != null);

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeFloat(fv);
		bitEC.flush();
		float df = getBitDecoder().decodeFloatValue().toFloat();

		float diff = Math.abs(f - df);
		assertTrue(diff < 0.000001);
	}

	public void testFloatType1567_0() throws IOException {
		float f = 1567.0f;
		FloatValue fv = FloatValue.parse(f);
		assertTrue(fv != null);

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeFloat(fv);
		bitEC.flush();
		float df = getBitDecoder().decodeFloatValue().toFloat();

		float diff = Math.abs(f - df);
		assertTrue(diff < 0.000001);
	}

	public void testFloatTypeNeg0_333() throws IOException {
		float f = -0.333f;
		FloatValue fv = FloatValue.parse(f);
		assertTrue(fv != null);

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeFloat(fv);
		bitEC.flush();
		float df = getBitDecoder().decodeFloatValue().toFloat();

		float diff = Math.abs(f - df);
		assertTrue(diff < 0.000001);
	}

	public void testFloatType0() throws IOException {
		float f = 0f;
		FloatValue fv = FloatValue.parse(f);
		assertTrue(fv != null);

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeFloat(fv);
		bitEC.flush();
		float df = getBitDecoder().decodeFloatValue().toFloat();

		float diff = Math.abs(f - df);
		assertTrue(diff < 0.000001);
	}

	public void testFloatType0_33333() throws IOException {
		float f = 1f / 3f;
		FloatValue fv = FloatValue.parse(f);
		assertTrue(fv != null);

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeFloat(fv);
		bitEC.flush();
		float df = getBitDecoder().decodeFloatValue().toFloat();

		float diff = Math.abs(f - df);
		assertTrue(diff < 0.000001);
	}

	public void testFloatType1_123456789() throws IOException {
		double d = 1.123456789d;
		FloatValue fv = FloatValue.parse(d);
		assertTrue(fv != null);

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeFloat(fv);
		bitEC.flush();
		double dd = getBitDecoder().decodeFloatValue().toDouble();

		double diff = Math.abs(d - dd);
		assertTrue(diff < 0.00000000001);
	}

	public void testFloatTypeNeg56783_132154() throws IOException {
		double d = 56783.132154d;
		FloatValue fv = FloatValue.parse(d);
		assertTrue(fv != null);

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeFloat(fv);
		bitEC.flush();
		double dd = getBitDecoder().decodeFloatValue().toDouble();

		double diff = Math.abs(d - dd);
		assertTrue(diff < 0.00000000001);
	}

	public void testFloatType18_4_a() throws IOException {
		double d = 18.4d;
		FloatValue fv = FloatValue.parse(d);
		assertTrue(fv != null);

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeFloat(fv);
		bitEC.flush();
		double dd = getBitDecoder().decodeFloatValue().toDouble();

		double diff = Math.abs(d - dd);
		assertTrue(diff < 0.00000000001);
	}

	public void testFloatNaN() throws IOException {
		String s = "NaN";
		FloatValue f = FloatValue.parse(s);
		assertTrue(f != null);

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeFloat(f);
		bitEC.flush();
		String sb = getBitDecoder().decodeFloatValue().toString();
		assertTrue(s.equals(sb));
		// Byte
		getByteEncoder().encodeFloat(f);
		String sB = getByteDecoder().decodeFloatValue().toString();
		assertTrue(s.equals(sB));
	}

	public void testFloatINF() throws IOException {
		String s = "INF";
		FloatValue f = FloatValue.parse(s);
		assertTrue(f != null);

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeFloat(f);
		bitEC.flush();
		assertTrue(s.equals(getBitDecoder().decodeFloatValue().toString()));
		// Byte
		getByteEncoder().encodeFloat(f);
		assertTrue(s.equals(getByteDecoder().decodeFloatValue().toString()));
	}

	public void testFloatMINF() throws IOException {
		String s = "-INF";
		FloatValue f = FloatValue.parse(s);
		assertTrue(f != null);

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeFloat(f);
		bitEC.flush();
		assertTrue(s.equals(getBitDecoder().decodeFloatValue().toString()));
		// Byte
		getByteEncoder().encodeFloat(f);
		assertTrue(s.equals(getByteDecoder().decodeFloatValue().toString()));
	}

	public void testFloat0() throws IOException {
		String s = "-1E4";
		FloatValue f = FloatValue.parse(s);
		assertTrue(f != null);

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeFloat(f);
		bitEC.flush();
		String f1 = getBitDecoder().decodeFloatValue().toString();
		assertTrue(s + " != " + f1, new BigDecimal(s).compareTo(new BigDecimal(
				f1)) == 0);
		// Byte
		getByteEncoder().encodeFloat(f);
		String f2 = getByteDecoder().decodeFloatValue().toString();
		assertTrue(s + " != " + f2, new BigDecimal(s).compareTo(new BigDecimal(
				f2)) == 0);
	}

	public void testFloat1() throws IOException {
		String s = "1267.43233E12";
		FloatValue f = FloatValue.parse(s);
		assertTrue(f != null);

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeFloat(f);
		bitEC.flush();
		String f1 = getBitDecoder().decodeFloatValue().toString();
		assertTrue(s + " != " + f1, new BigDecimal(s).compareTo(new BigDecimal(
				f1)) == 0);
		// Byte
		getByteEncoder().encodeFloat(f);
		String f2 = getByteDecoder().decodeFloatValue().toString();
		assertTrue(s + " != " + f2, new BigDecimal(s).compareTo(new BigDecimal(
				f2)) == 0);
	}

	public void testFloat2() throws IOException {
		String s = "12.78e-2";
		FloatValue f = FloatValue.parse(s);
		assertTrue(f != null);

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeFloat(f);
		bitEC.flush();
		String f1 = getBitDecoder().decodeFloatValue().toString();
		assertTrue(s + " != " + f1, new BigDecimal(s).compareTo(new BigDecimal(
				f1)) == 0);
		// Byte
		getByteEncoder().encodeFloat(f);
		String f2 = getByteDecoder().decodeFloatValue().toString();
		assertTrue(s + " != " + f2, new BigDecimal(s).compareTo(new BigDecimal(
				f2)) == 0);
	}

	public void testFloat3() throws IOException {
		String s = "12";
		FloatValue f = FloatValue.parse(s);
		assertTrue(f != null);

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeFloat(f);
		bitEC.flush();
		String f1 = getBitDecoder().decodeFloatValue().toString();
		assertTrue(s + " != " + f1, new BigDecimal(s).compareTo(new BigDecimal(
				f1)) == 0);
		// Byte
		getByteEncoder().encodeFloat(f);
		String f2 = getByteDecoder().decodeFloatValue().toString();
		assertTrue(s + " != " + f2, new BigDecimal(s).compareTo(new BigDecimal(
				f2)) == 0);
	}

	public void testFloat4() throws IOException {
		String s = "0";
		FloatValue f = FloatValue.parse(s);
		assertTrue(f != null);

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeFloat(f);
		bitEC.flush();
		String f1 = getBitDecoder().decodeFloatValue().toString();
		assertTrue(s + " != " + f1, new BigDecimal(s).compareTo(new BigDecimal(
				f1)) == 0);
		// Byte
		getByteEncoder().encodeFloat(f);
		String f2 = getByteDecoder().decodeFloatValue().toString();
		assertTrue(s + " != " + f2, new BigDecimal(s).compareTo(new BigDecimal(
				f2)) == 0);
	}

	public void testFloat5() throws IOException {
		String s = "-0";
		FloatValue f = FloatValue.parse(s);
		assertTrue(f != null);

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeFloat(f);
		bitEC.flush();
		String f1 = getBitDecoder().decodeFloatValue().toString();
		assertTrue(s + " != " + f1, new BigDecimal(s).compareTo(new BigDecimal(
				f1)) == 0);
		// Byte
		getByteEncoder().encodeFloat(f);
		String f2 = getByteDecoder().decodeFloatValue().toString();
		assertTrue(s + " != " + f2, new BigDecimal(s).compareTo(new BigDecimal(
				f2)) == 0);
	}

	public void testFloat6() throws IOException {
		String s = "-1";
		FloatValue f = FloatValue.parse(s);
		assertTrue(f != null);

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeFloat(f);
		bitEC.flush();
		String f1 = getBitDecoder().decodeFloatValue().toString();
		assertTrue(s + " != " + f1, new BigDecimal(s).compareTo(new BigDecimal(
				f1)) == 0);
		// Byte
		getByteEncoder().encodeFloat(f);
		String f2 = getByteDecoder().decodeFloatValue().toString();
		assertTrue(s + " != " + f2, new BigDecimal(s).compareTo(new BigDecimal(
				f2)) == 0);
	}

	public void testFloat7() throws IOException {
		String s = "119.999999999929";
		FloatValue f = FloatValue.parse(s);
		assertTrue(f != null);

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeFloat(f);
		bitEC.flush();
		String d1 = getBitDecoder().decodeFloatValue().toString();
		assertTrue(s + " != " + d1, new BigDecimal(s).compareTo(new BigDecimal(
				d1)) == 0);
		// Byte
		getByteEncoder().encodeFloat(f);
		String d2 = getByteDecoder().decodeFloatValue().toString();
		assertTrue(s + " != " + d2, new BigDecimal(s).compareTo(new BigDecimal(
				d2)) == 0);
	}

	public void testFloat8() throws IOException {
		String s = "000123400.0031200";
		FloatValue f = FloatValue.parse(s);
		assertTrue(f != null);

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeFloat(f);
		bitEC.flush();
		String d1 = getBitDecoder().decodeFloatValue().toString();
		assertTrue(s + " != " + d1, new BigDecimal(s).compareTo(new BigDecimal(
				d1)) == 0);
		// Byte
		getByteEncoder().encodeFloat(f);
		String d2 = getByteDecoder().decodeFloatValue().toString();
		assertTrue(s + " != " + d2, new BigDecimal(s).compareTo(new BigDecimal(
				d2)) == 0);
	}

	public void testFloat9() throws IOException {
		String s = "562949953421312";
		FloatValue f = FloatValue.parse(s);
		assertTrue(f != null);

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeFloat(f);
		bitEC.flush();
		String d1 = getBitDecoder().decodeFloatValue().toString();
		assertTrue(s + " != " + d1, new BigDecimal(s).compareTo(new BigDecimal(
				d1)) == 0);
		// Byte
		getByteEncoder().encodeFloat(f);
		String d2 = getByteDecoder().decodeFloatValue().toString();
		assertTrue(s + " != " + d2, new BigDecimal(s).compareTo(new BigDecimal(
				d2)) == 0);
	}
	
//	public void testFloat10() throws IOException {
//		// String s = "1.0";
//		// String s = "-1E4";
//		String s = "-1.34E4";
//		FloatValue f = FloatValue.parse(s);
//		assertTrue(f != null);
//		
//		FloatingDecimal fd = FloatingDecimal.readJavaFormatString(s);
//		double d = Double.parseDouble(s);
//		int exp = getExponent(d);
//		long mantissaC = (long)d;
//
//
//		int exponent = (int)Math.log10(d) + 1;
//		double mantissa = d * Math.pow(10.0, -exponent);
//		
//		double expX = Math.exp(d);
//		
//		BigDecimal bd = BigDecimal.valueOf(d);
//		
//		
////	    //long bits = Double.doubleToLongBits(5894.349580349);
////		long bits = Double.doubleToLongBits(1.0);
////	     
////		
////	    boolean negative = (bits & 0x8000000000000000L) != 0;
////	    long exponentX = bits & 0x7ff0000000000000L >> 52;
////	    long mantissaX = bits & 0x000fffffffffffffL;
//		
//		
//		
//		System.err.println("dsd");
////		 long bits = Double.doubleToLongBits(15.00);  
////         
////		 boolean negative = (bits & 0x8000000000000000L) != 0;   
////		 long exponent = bits & 0x7ff0000000000000L;  
////		 long mantissa = bits & 0x000fffffffffffffL;  
////		 long mantissa2 = (bits & 0x000fffffffffffffL) | 0x0010000000000000L; 
////		 System.out.println("negative: " + negative);  
////		 System.out.println("exponent: " + Double.longBitsToDouble(exponent));  
////		 System.out.println("mantissa: " + Double.longBitsToDouble(mantissa));  
////		 System.out.println("mantissa2: " + Double.longBitsToDouble(mantissa2));  
////		
////		
////		System.err.println("dsd");
//
//		// Bit
//	}
	
	
	/**
     * Returns unbiased exponent of a <code>double</code>.
     */
    public static int getExponent(double d) {
        /*
         * Bitwise convert d to long, mask out exponent bits, shift
         * to the right and then subtract out double's bias adjust to
         * get true exponent value.
         */
        return (int) (((Double.doubleToRawLongBits(d) & DoubleConsts.EXP_BIT_MASK) >> (DoubleConsts.SIGNIFICAND_WIDTH - 1)) - DoubleConsts.EXP_BIAS);
    }

    /**
     * Returns unbiased exponent of a <code>float</code>.
     */
    public static int getExponent(float f) {
        /*
         * Bitwise convert f to integer, mask out exponent bits, shift
         * to the right and then subtract out float's bias adjust to
         * get true exponent value
         */
        return ((Float.floatToRawIntBits(f) & FloatConsts.EXP_BIT_MASK) >> (FloatConsts.SIGNIFICAND_WIDTH - 1))
                - FloatConsts.EXP_BIAS;
    }
    
    
	
	
	
	
	

	// the range of the mantissa is -(2^63) to 2^63-1 
	// the range of the exponent is -(2^14-1) to 2^14-1
	public void testFloatBoundary1() throws IOException {
		// mantissa MINIMUM
		long m = Constants.FLOAT_MANTISSA_MIN_RANGE;
		long e = 123;
		
		FloatValue fv = new FloatValue(m, e);
		String s = fv.toString();
		assert("-9223372036854775808E123".equals(s));
//		String s = "-9223372036854775808E123";
		
		FloatValue f = FloatValue.parse(s);
		assertTrue(f != null);

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeFloat(f);
		bitEC.flush();
		String d1 = getBitDecoder().decodeFloatValue().toString();
		assertTrue(s + " != " + d1, new BigDecimal(s).compareTo(new BigDecimal(
				d1)) == 0);
		// Byte
		getByteEncoder().encodeFloat(f);
		String d2 = getByteDecoder().decodeFloatValue().toString();
		assertTrue(s + " != " + d2, new BigDecimal(s).compareTo(new BigDecimal(
				d2)) == 0);
	}
	
	// the range of the mantissa is -(2^63) to 2^63-1 
	// the range of the exponent is -(2^14-1) to 2^14-1
	public void testFloatBoundary2() throws IOException {
		// mantissa MAXIMUM
		long m = Constants.FLOAT_MANTISSA_MAX_RANGE;
		long e = 123;
		FloatValue fv = new FloatValue(m, e);
		
		String s = fv.toString();
		assert("9223372036854775807E123".equals(s));
		
		FloatValue f = FloatValue.parse(s);
		assertTrue(f != null);

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeFloat(f);
		bitEC.flush();
		String d1 = getBitDecoder().decodeFloatValue().toString();
		assertTrue(s + " != " + d1, new BigDecimal(s).compareTo(new BigDecimal(
				d1)) == 0);
		// Byte
		getByteEncoder().encodeFloat(f);
		String d2 = getByteDecoder().decodeFloatValue().toString();
		assertTrue(s + " != " + d2, new BigDecimal(s).compareTo(new BigDecimal(
				d2)) == 0);
	}

	// the range of the mantissa is -(2^63) to 2^63-1 
	// the range of the exponent is -(2^14-1) to 2^14-1
	public void testFloatBoundary3() throws IOException {
		// exponent MINIMUM
		long m = 456;
		long e = Constants.FLOAT_EXPONENT_MIN_RANGE;
		FloatValue fv = new FloatValue(m, e);
		
		String s = fv.toString();
		assert("456E-16383".equals(s));
		
		FloatValue f = FloatValue.parse(s);
		assertTrue(f != null);

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeFloat(f);
		bitEC.flush();
		String d1 = getBitDecoder().decodeFloatValue().toString();
		assertTrue(s + " != " + d1, new BigDecimal(s).compareTo(new BigDecimal(
				d1)) == 0);
		// Byte
		getByteEncoder().encodeFloat(f);
		String d2 = getByteDecoder().decodeFloatValue().toString();
		assertTrue(s + " != " + d2, new BigDecimal(s).compareTo(new BigDecimal(
				d2)) == 0);
	}
	
	// the range of the mantissa is -(2^63) to 2^63-1 
	// the range of the exponent is -(2^14-1) to 2^14-1
	public void testFloatBoundary4() throws IOException {
		// exponent MAXIMUM
		long m = 456;
		long e = Constants.FLOAT_EXPONENT_MAX_RANGE;
		FloatValue fv = new FloatValue(m, e);
		
		String s = fv.toString();
		assert("456E16383".equals(s));
		
		FloatValue f = FloatValue.parse(s);
		assertTrue(f != null);

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeFloat(f);
		bitEC.flush();
		String d1 = getBitDecoder().decodeFloatValue().toString();
		assertTrue(s + " != " + d1, new BigDecimal(s).compareTo(new BigDecimal(
				d1)) == 0);
		// Byte
		getByteEncoder().encodeFloat(f);
		String d2 = getByteDecoder().decodeFloatValue().toString();
		assertTrue(s + " != " + d2, new BigDecimal(s).compareTo(new BigDecimal(
				d2)) == 0);
	}
	
	public void testFloatInvalid0() {
		String s = "x11.1";

		FloatValue f = FloatValue.parse(s);
		assertFalse("Invalid float '" + s + "' parsed successfully", f != null);
	}

	public void testFloatInvalid1() {
		String s = "";

		FloatValue f = FloatValue.parse(s);
		assertFalse("Invalid float value '" + s + "' parsed successfully",
				f != null);

	}

	public void testFloatInvalid2() {
		String s = "1.1223x";

		FloatValue f = FloatValue.parse(s);
		assertFalse("Invalid float value '" + s + "' parsed successfully",
				f != null);
	}

	public void testFloatInvalid3() {
		String s = "E";

		FloatValue f = FloatValue.parse(s);
		assertFalse("Invalid float value '" + s + "' parsed successfully",
				f != null);
	}

	public void testFloatInvalid4() throws IOException {
		// too large mantissa
		// The range of the mantissa is - (2^63) to 2^63-1
		// and the range of the exponent is - (2^14-1) to 2^14-1
		// 9223372036854775807L would be OK!
		String s = "9223372036854775808E3";
		FloatValue f = FloatValue.parse(s);
		assertFalse("Float Mantissa " + s + " too large", f != null);
	}
	
	public void testFloatInvalid4b() throws IOException {
		// too large mantissa
		// The range of the mantissa is - (2^63) to 2^63-1
		// and the range of the exponent is - (2^14-1) to 2^14-1
		// 9223372036854775807L would be OK!
		String s = "9223372036854775809E3";
		FloatValue f = FloatValue.parse(s);
		assertFalse("Float Mantissa " + s + " too large", f != null);
	}

	public void testFloatInvalid5() throws IOException {
		// too large exponent
		// The range of the mantissa is - (2^63) to 2^63-1
		// and the range of the exponent is - (2^14-1) to 2^14-1
		String s = "123E16384";
		FloatValue f = FloatValue.parse(s);
		assertFalse("Float exponent" + s + " too large", f != null);
	}

	public void testFloatInvalid6() throws IOException {
		// too large mantissa
		// The range of the mantissa is - (2^63) to 2^63-1
		// and the range of the exponent is - (2^14-1) to 2^14-1
		/* -(2^63) == -9223372036854775808L */
		String s = "-9223372036854775809E3";
		FloatValue f = FloatValue.parse(s);
		assertFalse("Double Mantissa " + s + " too large", f != null);
	}
	
	public void testFloatInvalid6b() throws IOException {
		// too large mantissa
		// The range of the mantissa is - (2^63) to 2^63-1
		// and the range of the exponent is - (2^14-1) to 2^14-1
		/* -(2^63) == -9223372036854775808L */
		String s = "-9223372036854775810E3";
		FloatValue f = FloatValue.parse(s);
		assertFalse("Double Mantissa " + s + " too large", f != null);
	}

	public void testFloatInvalid7() throws IOException {
		// too large exponent
		// The range of the mantissa is - (2^63) to 2^63-1
		// and the range of the exponent is - (2^14-1) to 2^14-1
		String s = "123E-16384";
		FloatValue f = FloatValue.parse(s);
		assertFalse("Double exponent" + s + " too large", f != null);
	}
	
	public void testFloatInvalid8() throws IOException {
		String s = "123E-+16384";
		FloatValue f = FloatValue.parse(s);
		assertFalse("Float invalid" + s + "", f != null);
	}
	
	public void testFloatEquals1() throws IOException {
		// e.g. 234E2 vs. 2340E1
		String s1 = "234E2";
		String s2 = "2340E1";
		
		FloatValue f1 = FloatValue.parse(s1);
		FloatValue f2 = FloatValue.parse(s2);
		
		assertTrue(f1.equals(f2));
	}
	
	public void testFloatEquals2() throws IOException {
		// e.g. 30E0 vs. 3E1
		String s1 = "30E0";
		String s2 = "3E1";
		
		FloatValue f1 = FloatValue.parse(s1);
		FloatValue f2 = FloatValue.parse(s2);
		
		assertTrue(f1.equals(f2));
	}
	
	public void testFloatNotEquals1() throws IOException {
		String s1 = "234E3";
		String s2 = "2340E1";
		
		FloatValue f1 = FloatValue.parse(s1);
		FloatValue f2 = FloatValue.parse(s2);
		
		assertFalse(f1.equals(f2));
	}
	

}