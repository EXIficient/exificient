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

package com.siemens.ct.exi.io.channel;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Calendar;

import com.siemens.ct.exi.util.datatype.DatetimeType;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.1.20081117
 */

public interface DecoderChannel
{
	/**
	 * Decodes a single byte
	 * @return a byte as int
	 * @throws IOException
	 */
	public int decode () throws IOException;

	/**
	 * Decodes and returns an n-bit unsigned integer.
	 */
	public int decodeNBitUnsignedInteger ( int n ) throws IOException;
	
	public String decodeNBitUnsignedIntegerAsString ( int n ) throws IOException;

	/**
	 * Decode a single boolean value. The value false is represented by the bit
	 * (byte) 0, and the value true is represented by the bit (byte) 1.
	 */
	public boolean decodeBoolean () throws IOException;

	public String decodeBooleanAsString () throws IOException;

	/**
	 * Decode a binary value as a length-prefixed sequence of octets.
	 */
	public byte[] decodeBinary () throws IOException;

	public String decodeBinaryAsString () throws IOException;

	/**
	 * Decode a string as a length-prefixed sequence of UCS codepoints, each of
	 * which is encoded as an integer. Look for codepoints of more than 16 bits
	 * that are represented as UTF-16 surrogate pairs in Java.
	 */
	public String decodeString () throws IOException;

	/**
	 * Decode the characters of a string whose length has already been read.
	 * Look for codepoints of more than 16 bits that are represented as UTF-16
	 * surrogate pairs in Java.
	 * 
	 * @param length
	 *            Length of the character sequence to read.
	 * @return The character sequence as a string.
	 */
	public String decodeStringOnly ( int length ) throws IOException;

	/**
	 * Decode an arbitrary precision non negative integer using a sequence of
	 * octets. The most significant bit of the last octet is set to zero to
	 * indicate sequence termination. Only seven bits per octet are used to
	 * store the integer's value.
	 */
	public int decodeUnsignedInteger () throws IOException;

	public long decodeUnsignedIntegerAsLong () throws IOException;

	public String decodeUnsignedIntegerAsString () throws IOException;

	/**
	 * Decode an arbitrary precision integer using a sign bit followed by a
	 * sequence of octets. The most significant bit of the last octet is set to
	 * zero to indicate sequence termination. Only seven bits per octet are used
	 * to store the integer's value.
	 */
	public int decodeInteger () throws IOException;

	public String decodeIntegerAsString () throws IOException;

	/**
	 * Decode a decimal represented as a Boolean sign followed by two Unsigned
	 * Integers. A sign value of zero (0) is used to represent positive Decimal
	 * values and a sign value of one (1) is used to represent negative Decimal
	 * values The first Integer represents the integral portion of the Decimal
	 * value. The second positive integer represents the fractional portion of
	 * the decimal with the digits in reverse order to preserve leading zeros.
	 */
	public BigDecimal decodeDecimal () throws IOException;

	public String decodeDecimalAsString () throws IOException;

	/**
	 * Decode a Float represented as two consecutive Integers. The first Integer
	 * represents the mantissa of the floating point number and the second
	 * Integer represents the 10-based exponent of the floating point number
	 */
	public float decodeFloat () throws IOException;

	public String decodeFloatAsString () throws IOException;

	/**
	 * Decode Date-Time as sequence of values representing the individual
	 * components of the Date-Time.
	 */
	public Calendar decodeDateTime ( DatetimeType type ) throws IOException;

	public String decodeDateTimeAsString ( DatetimeType type ) throws IOException;

}
