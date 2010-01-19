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

package com.siemens.ct.exi.io.channel;

import java.io.IOException;

import com.siemens.ct.exi.util.datatype.DatetimeType;
import com.siemens.ct.exi.values.BinaryValue;
import com.siemens.ct.exi.values.BooleanValue;
import com.siemens.ct.exi.values.DateTimeValue;
import com.siemens.ct.exi.values.DecimalValue;
import com.siemens.ct.exi.values.DoubleValue;
import com.siemens.ct.exi.values.FloatValue;
import com.siemens.ct.exi.values.HugeIntegerValue;
import com.siemens.ct.exi.values.IntegerValue;
import com.siemens.ct.exi.values.LongValue;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.4.20081117
 */

public interface DecoderChannel {
	/**
	 * Decodes a single byte
	 * 
	 * @return a byte as int
	 * @throws IOException
	 */
	public int decode() throws IOException;

	/**
	 * Align to next byte-aligned boundary in the stream if it is not already at such a boundary
	 * @throws IOException
	 */
	public void align() throws IOException;
	
	/**
	 * Decodes and returns an n-bit unsigned integer.
	 */
	public int decodeNBitUnsignedInteger(int n) throws IOException;

	public IntegerValue decodeNBitUnsignedIntegerValue(int n) throws IOException;

	/**
	 * Decode a single boolean value. The value false is represented by the bit
	 * (byte) 0, and the value true is represented by the bit (byte) 1.
	 */
	public boolean decodeBoolean() throws IOException;

	public BooleanValue decodeBooleanValue() throws IOException;

	/**
	 * Decode a binary value as a length-prefixed sequence of octets.
	 */
	public BinaryValue decodeBinary() throws IOException;

	/**
	 * Decode a string as a length-prefixed sequence of UCS codepoints, each of
	 * which is encoded as an integer. Look for codepoints of more than 16 bits
	 * that are represented as UTF-16 surrogate pairs in Java.
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
	 */
	public char[] decodeStringOnly(int length) throws IOException;

	/**
	 * Decode an arbitrary precision non negative integer using a sequence of
	 * octets. The most significant bit of the last octet is set to zero to
	 * indicate sequence termination. Only seven bits per octet are used to
	 * store the integer's value.
	 */
	public int decodeUnsignedInteger() throws IOException;
	
	public IntegerValue decodeUnsignedIntegerValue() throws IOException;
	
	public LongValue decodeUnsignedLongValue() throws IOException;
	
	public HugeIntegerValue decodeUnsignedHugeIntegerValue() throws IOException;

	/**
	 * Decode an arbitrary precision integer using a sign bit followed by a
	 * sequence of octets. The most significant bit of the last octet is set to
	 * zero to indicate sequence termination. Only seven bits per octet are used
	 * to store the integer's value.
	 */
	public IntegerValue decodeIntegerValue() throws IOException;
	
	public LongValue decodeLongValue() throws IOException;
	
	public HugeIntegerValue decodeHugeIntegerValue() throws IOException;
	
	/**
	 * Decode a decimal represented as a Boolean sign followed by two Unsigned
	 * Integers. A sign value of zero (0) is used to represent positive Decimal
	 * values and a sign value of one (1) is used to represent negative Decimal
	 * values The first Integer represents the integral portion of the Decimal
	 * value. The second positive integer represents the fractional portion of
	 * the decimal with the digits in reverse order to preserve leading zeros.
	 */
	public DecimalValue decodeDecimalValue() throws IOException;

	/**
	 * Decode a Float represented as two consecutive Integers. The first Integer
	 * represents the mantissa of the floating point number and the second
	 * Integer represents the 10-based exponent of the floating point number
	 */
	public FloatValue decodeFloatValue() throws IOException;
		
	public DoubleValue decodeDoubleValue() throws IOException;

	/**
	 * Decode Date-Time as sequence of values representing the individual
	 * components of the Date-Time.
	 */
	public DateTimeValue decodeDateTimeValue(DatetimeType type) throws IOException;

}
