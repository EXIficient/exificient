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

import com.siemens.ct.exi.types.DateTimeType;
import com.siemens.ct.exi.values.BooleanValue;
import com.siemens.ct.exi.values.DateTimeValue;
import com.siemens.ct.exi.values.DecimalValue;
import com.siemens.ct.exi.values.FloatValue;
import com.siemens.ct.exi.values.IntegerValue;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9.5-SNAPSHOT
 */

public interface DecoderChannel {
	/**
	 * Decodes a single byte
	 * 
	 * @return a byte as int
	 * @throws IOException IO exception
	 */
	public int decode() throws IOException;

	/**
	 * Align to next byte-aligned boundary in the stream if it is not already at
	 * such a boundary
	 * 
	 * @throws IOException IO exception
	 */
	public void align() throws IOException;

	/**
	 * Skips over and discards <code>n</code> bytes of data from this channel.
	 * 
	 * @param n number of bytes to skip
	 * @throws IOException IO exception
	 */
	public void skip(long n) throws IOException;

	/**
	 * Decodes and returns an n-bit unsigned integer.
	 * 
	 * @param n number of bits
	 * @return nbit value
	 * @throws IOException IO exception
	 */
	public int decodeNBitUnsignedInteger(int n) throws IOException;

	public IntegerValue decodeNBitUnsignedIntegerValue(int n)
			throws IOException;

	/**
	 * Decode a single boolean value. The value false is represented by the bit
	 * (byte) 0, and the value true is represented by the bit (byte) 1.
	 * 
	 * @return boolean value
	 * @throws IOException IO exception
	 */
	public boolean decodeBoolean() throws IOException;

	public BooleanValue decodeBooleanValue() throws IOException;

	/**
	 * Decode a binary value as a length-prefixed sequence of octets.
	 * 
	 * @return binary data
	 * @throws IOException IO exception
	 */
	public byte[] decodeBinary() throws IOException;

	/**
	 * Decode a string as a length-prefixed sequence of UCS codepoints, each of
	 * which is encoded as an integer. Look for codepoints of more than 16 bits
	 * that are represented as UTF-16 surrogate pairs in Java.
	 * 
	 * @return characters
	 * @throws IOException IO exception
	 */
	public char[] decodeString() throws IOException;

	/**
	 * Decode the characters of a string whose length has already been read.
	 * Look for codepoints of more than 16 bits that are represented as UTF-16
	 * surrogate pairs in Java.
	 * 
	 * @param length
	 *            Length of the character sequence to read.
	 * @return The character sequence
	 * @throws IOException IO exception
	 */
	public char[] decodeStringOnly(int length) throws IOException;

	/**
	 * Decode an arbitrary precision non negative integer using a sequence of
	 * octets. The most significant bit of the last octet is set to zero to
	 * indicate sequence termination. Only seven bits per octet are used to
	 * store the integer's value.
	 * 
	 * @return unsigned integer
	 * @throws IOException IO exception
	 */
	public int decodeUnsignedInteger() throws IOException;

	public IntegerValue decodeUnsignedIntegerValue() throws IOException;

	/**
	 * Decode an arbitrary precision integer using a sign bit followed by a
	 * sequence of octets. The most significant bit of the last octet is set to
	 * zero to indicate sequence termination. Only seven bits per octet are used
	 * to store the integer's value.
	 *
	 * @return integer value
	 * @throws IOException IO exception
	 */
	public IntegerValue decodeIntegerValue() throws IOException;

	/**
	 * Decode a decimal represented as a Boolean sign followed by two Unsigned
	 * Integers. A sign value of zero (0) is used to represent positive Decimal
	 * values and a sign value of one (1) is used to represent negative Decimal
	 * values The first Integer represents the integral portion of the Decimal
	 * value. The second positive integer represents the fractional portion of
	 * the decimal with the digits in reverse order to preserve leading zeros.
	 * 
	 * @return decimal value
	 * @throws IOException IO exception
	 */
	public DecimalValue decodeDecimalValue() throws IOException;

	/**
	 * Decode a Float represented as two consecutive Integers. The first Integer
	 * represents the mantissa of the floating point number and the second
	 * Integer represents the 10-based exponent of the floating point number
	 * 
	 * @return float value
	 * @throws IOException IO exception
	 */
	public FloatValue decodeFloatValue() throws IOException;

	/**
	 * Decode Date-Time as sequence of values representing the individual
	 * components of the Date-Time.
	 * 
	 * @param type date-Time type
	 * @return date-time value
	 * @throws IOException IO exception
	 */
	public DateTimeValue decodeDateTimeValue(DateTimeType type)
			throws IOException;

}
