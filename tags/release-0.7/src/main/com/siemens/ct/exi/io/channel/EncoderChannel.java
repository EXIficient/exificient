/*
 * Copyright (C) 2007-2011 Siemens AG
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

package com.siemens.ct.exi.io.channel;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;

import com.siemens.ct.exi.values.DateTimeValue;
import com.siemens.ct.exi.values.IntegerValue;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.7
 */

public interface EncoderChannel {

	public OutputStream getOutputStream();

	public void flush() throws IOException;

	/**
	 * Returns the number of bytes written.
	 * 
	 * This feature is optional and channels that no not support this feature
	 * are required to report -1.
	 * */
	public int getLength();

	/**
	 * Align to next byte-aligned boundary in the stream if it is not already at
	 * such a boundary
	 * 
	 * @throws IOException
	 */
	public void align() throws IOException;

	public void encode(int b) throws IOException;

	public void encode(byte b[], int off, int len) throws IOException;

	public void encodeNBitUnsignedInteger(int b, int n) throws IOException;

	/**
	 * Encode a single boolean value. A false value is encoded as bit (byte) 0
	 * and true value is encode as bit (byte) 1.
	 */
	public void encodeBoolean(boolean b) throws IOException;

	/**
	 * Encode a binary value as a length-prefixed sequence of octets.
	 */
	public void encodeBinary(byte[] b) throws IOException;

	/**
	 * Encode a string as a length-prefixed sequence of UCS codepoints, each of
	 * which is encoded as an integer. Look for codepoints of more than 16 bits
	 * that are represented as UTF-16 surrogate pairs in Java.
	 */
	public void encodeString(String s) throws IOException;

	/**
	 * Encode a string as a sequence of UCS codepoints, each of which is encoded
	 * as an integer. Look for codepoints of more than 16 bits that are
	 * represented as UTF-16 surrogate pairs in Java.
	 */
	public void encodeStringOnly(String s) throws IOException;

	/**
	 * Encode an arbitrary precision non negative integer using a sequence of
	 * octets. The most significant bit of the last octet is set to zero to
	 * indicate sequence termination. Only seven bits per octet are used to
	 * store the integer's value.
	 */
	public void encodeUnsignedInteger(int n) throws IOException;

	public void encodeUnsignedLong(long l) throws IOException;

	public void encodeUnsignedBigInteger(BigInteger bi) throws IOException;

	public void encodeUnsignedIntegerValue(IntegerValue iv) throws IOException;

	/**
	 * Encode an arbitrary precision integer using a sign bit followed by a
	 * sequence of octets. The most significant bit of the last octet is set to
	 * zero to indicate sequence termination. Only seven bits per octet are used
	 * to store the integer's value.
	 */
	public void encodeInteger(int n) throws IOException;

	public void encodeLong(long l) throws IOException;

	public void encodeBigInteger(BigInteger bi) throws IOException;

	public void encodeIntegerValue(IntegerValue iv) throws IOException;

	/**
	 * Encode a decimal represented as a Boolean sign followed by two Unsigned
	 * Integers. A sign value of zero (0) is used to represent positive Decimal
	 * values and a sign value of one (1) is used to represent negative Decimal
	 * values The first Integer represents the integral portion of the Decimal
	 * value. The second positive integer represents the fractional portion of
	 * the decimal with the digits in reverse order to preserve leading zeros.
	 */
	public void encodeDecimal(boolean negative, IntegerValue integral,
			IntegerValue reverseFraction) throws IOException;

	/**
	 * Encode a Float represented as two consecutive Integers. The first Integer
	 * represents the mantissa of the floating point number and the second
	 * Integer represents the 10-based exponent of the floating point number
	 */
	public void encodeFloat(long mantissa, long exponent) throws IOException;

	/**
	 * The Date-Time datatype representation is a sequence of values
	 * representing the individual components of the Date-Time
	 * 
	 * @param cal
	 * @throws IOException
	 */
	public void encodeDateTime(DateTimeValue cal) throws IOException;

}