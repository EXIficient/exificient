/*
 * Copyright (c) 2007-2015 Siemens AG
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

package com.siemens.ct.exi.io.channel;

import java.io.IOException;
import java.io.OutputStream;

import com.siemens.ct.exi.values.DateTimeValue;
import com.siemens.ct.exi.values.FloatValue;
import com.siemens.ct.exi.values.IntegerValue;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9.5
 */

public interface EncoderChannel {

	public OutputStream getOutputStream();

	public void flush() throws IOException;

	/**
	 * Returns the number of bytes written.
	 * 
	 * @return number of bytes
	 */
	public int getLength();

	/**
	 * Align to next byte-aligned boundary in the stream if it is not already at
	 * such a boundary
	 * 
	 * @throws IOException IO exception
	 */
	public void align() throws IOException;

	public void encode(int b) throws IOException;

	public void encode(byte b[], int off, int len) throws IOException;

	public void encodeNBitUnsignedInteger(int b, int n) throws IOException;

	/**
	 * Encode a single boolean value. A false value is encoded as bit (byte) 0
	 * and true value is encode as bit (byte) 1.
	 * 
	 * @param b boolean
	 * @throws IOException IO exception
	 */
	public void encodeBoolean(boolean b) throws IOException;

	/**
	 * Encode a binary value as a length-prefixed sequence of octets.
	 * 
	 * @param b byte array
	 * @throws IOException IO exception
	 */
	public void encodeBinary(byte[] b) throws IOException;

	/**
	 * Encode a string as a length-prefixed sequence of UCS codepoints, each of
	 * which is encoded as an integer. Look for codepoints of more than 16 bits
	 * that are represented as UTF-16 surrogate pairs in Java.
	 * 
	 * @param s string
	 * @throws IOException IO exception
	 */
	public void encodeString(String s) throws IOException;

	/**
	 * Encode a string as a sequence of UCS codepoints, each of which is encoded
	 * as an integer. Look for codepoints of more than 16 bits that are
	 * represented as UTF-16 surrogate pairs in Java.
	 * 
	 * @param s string
	 * @throws IOException IO exception
	 */
	public void encodeStringOnly(String s) throws IOException;

	/**
	 * Encode an arbitrary precision non negative integer using a sequence of
	 * octets. The most significant bit of the last octet is set to zero to
	 * indicate sequence termination. Only seven bits per octet are used to
	 * store the integer's value.
	 * 
	 * @param n unsigned integer
	 * @throws IOException IO exception
	 */
	public void encodeUnsignedInteger(int n) throws IOException;

//	public void encodeUnsignedLong(long l) throws IOException;

//	public void encodeUnsignedBigInteger(BigInteger bi) throws IOException;

	public void encodeUnsignedIntegerValue(IntegerValue iv) throws IOException;

	/**
	 * Encode an arbitrary precision integer using a sign bit followed by a
	 * sequence of octets. The most significant bit of the last octet is set to
	 * zero to indicate sequence termination. Only seven bits per octet are used
	 * to store the integer's value.
	 * 
	 * @param n integer
	 * @throws IOException IO exception
	 */
	public void encodeInteger(int n) throws IOException;

//	public void encodeLong(long l) throws IOException;

//	public void encodeBigInteger(BigInteger bi) throws IOException;

	public void encodeIntegerValue(IntegerValue iv) throws IOException;

	/**
	 * Encode a decimal represented as a Boolean sign followed by two Unsigned
	 * Integers. A sign value of zero (0) is used to represent positive Decimal
	 * values and a sign value of one (1) is used to represent negative Decimal
	 * values The first Integer represents the integral portion of the Decimal
	 * value. The second positive integer represents the fractional portion of
	 * the decimal with the digits in reverse order to preserve leading zeros.
	 * 
	 * @param negative is negative
	 * @param integral integral value
	 * @param reverseFraction reverse fraction
	 * 
	 * @throws IOException IO exception
	 */
	public void encodeDecimal(boolean negative, IntegerValue integral,
			IntegerValue reverseFraction) throws IOException;

	/**
	 * Encode a Float represented as two consecutive Integers. The first Integer
	 * represents the mantissa of the floating point number and the second
	 * Integer represents the 10-based exponent of the floating point number
	 * 
	 * @param fv float value
	 * @throws IOException IO exception
	 */
	public void encodeFloat(FloatValue fv) throws IOException;

	/**
	 * The Date-Time datatype representation is a sequence of values
	 * representing the individual components of the Date-Time
	 * 
	 * @param cal datetime
	 * @throws IOException IO exception
	 */
	public void encodeDateTime(DateTimeValue cal) throws IOException;

}
