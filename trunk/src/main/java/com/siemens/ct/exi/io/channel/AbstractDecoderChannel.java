/*
 * Copyright (C) 2007-2015 Siemens AG
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
import java.math.BigInteger;

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

public abstract class AbstractDecoderChannel implements DecoderChannel {

	/* buffer for reading arbitrary large integer values */
	private final int[] maskedOctets = new int[MAX_OCTETS_FOR_LONG];
	/* long == 64 bits, 9 * 7bits = 63 bits */
	private final static int MAX_OCTETS_FOR_LONG = 9;
	
	/* boolean values */
	private final BooleanValue bvTrue = new BooleanValue(true);
	private final BooleanValue bvFalse = new BooleanValue(false);
	
	/* Helper for building strings */
	protected StringBuilder sbHelper;

	
	public AbstractDecoderChannel() {
	}
	
	public BooleanValue decodeBooleanValue() throws IOException {
		// return new BooleanValue(decodeBoolean());
		return decodeBoolean() ? bvTrue : bvFalse;
	}

	/**
	 * Decode a string as a length-prefixed sequence of UCS codepoints, each of
	 * which is encoded as an integer. Look for codepoints of more than 16 bits
	 * that are represented as UTF-16 surrogate pairs in Java.
	 */
	public char[] decodeString() throws IOException {
		return decodeStringOnly(decodeUnsignedInteger());
	}

	/**
	 * Decode the characters of a string whose length (#code-points) has already
	 * been read. Look for codepoints of more than 16 bits that are represented
	 * as UTF-16 surrogate pairs in Java.
	 * 
	 * @param length
	 *            Length of the character sequence to read.
	 * @return The character sequence as a string.
	 */
	public char[] decodeStringOnly(int length) throws IOException {

		final char[] ca = new char[length];

		for (int i = 0; i < length; i++) {
			final int codePoint = decodeUnsignedInteger();
			if (Character.isSupplementaryCodePoint(codePoint)) {
				// supplementary code-point
				// Assumption: it doesn't happen very often
				return decodeStringOnlySupplementaryCodePoints(ca, length, i, codePoint);
			} else {
				ca[i] = (char) codePoint;
			}
		}

		return ca;
	}
	
	private char[] decodeStringOnlySupplementaryCodePoints(char[] ca, int length, int i, int codePoint) throws IOException {
		assert(Character.isSupplementaryCodePoint(codePoint));
		if(sbHelper == null) {
			sbHelper = new StringBuilder(length + 10);
		} else {
			sbHelper.setLength(0);
		}
		
		sbHelper.append(ca, 0, i); // append chars so far
		sbHelper.appendCodePoint(codePoint); // append current code-point
		for (int k = i + 1; k < length; k++) {
			sbHelper.appendCodePoint(decodeUnsignedInteger());
		}
		
		int len = sbHelper.length();
		char dst[] = new char[len];
		sbHelper.getChars(0, len, dst, 0);
		
		return dst;
		
		// return  sb.toString().toCharArray(); // return char array
	}
	

	

	/**
	 * Decode an arbitrary precision non negative integer using a sequence of
	 * octets. The most significant bit of the last octet is set to zero to
	 * indicate sequence termination. Only seven bits per octet are used to
	 * store the integer's value.
	 */
	public final int decodeUnsignedInteger() throws IOException {
		// 0XXXXXXX ... 1XXXXXXX 1XXXXXXX
		int result = decode();

		// < 128: just one byte, optimal case
		// ELSE: multiple bytes...
		
		if(result >= 128) {
			result = (result & 127);
			int mShift = 7;
			int b;

			do {
				// 1. Read the next octet
				b = decode();
				// 2. Multiply the value of the unsigned number represented by
				// the 7 least significant
				// bits of the octet by the current multiplier and add the
				// result to the current value.
				result += (b & 127) << mShift;
				// 3. Multiply the multiplier by 128
				mShift += 7;
				// 4. If the most significant bit of the octet was 1, go back to
				// step 1
			} while (b >= 128);
		}
		
		return result;
	}

	protected long decodeUnsignedLong() throws IOException {
		long lResult = 0L;
		int mShift = 0;
		int b;

		do {
			b = decode();
			lResult += ((long) (b & 127)) << mShift;
			mShift += 7;
		} while ((b >>> 7) == 1);

		return lResult;
	}

	/**
	 * Decode an arbitrary precision integer using a sign bit followed by a
	 * sequence of octets. The most significant bit of the last octet is set to
	 * zero to indicate sequence termination. Only seven bits per octet are used
	 * to store the integer's value.
	 */
	protected int decodeInteger() throws IOException {
		if (decodeBoolean()) {
			// For negative values, the Unsigned Integer holds the
			// magnitude of the value minus 1
			return (-(decodeUnsignedInteger() + 1));
		} else {
			// positive
			return decodeUnsignedInteger();
		}
	}

	protected long decodeLong() throws IOException {
		if (decodeBoolean()) {
			// For negative values, the Unsigned Integer holds the
			// magnitude of the value minus 1
			return (-(decodeUnsignedLong() + 1L));
		} else {
			// positive
			return decodeUnsignedLong();
		}
	}
	
	public IntegerValue decodeIntegerValue() throws IOException {
		return decodeUnsignedIntegerValue(decodeBoolean());
	}

	public IntegerValue decodeUnsignedIntegerValue() throws IOException {
		return decodeUnsignedIntegerValue(false);
	}

	protected final IntegerValue decodeUnsignedIntegerValue(boolean negative)
			throws IOException {
		int b;
		for (int i = 0; i < MAX_OCTETS_FOR_LONG; i++) {
			// Read the next octet
			b = decode();
			// If the most significant bit of the octet was 1,
			// another octet is going to come
			if (b < 128) {
				/* no more octets */
				switch(i) {
				case 0:
					/* one octet only */
					return IntegerValue.valueOf(negative ? -(b+1) : b);
				case 1:
				case 2:
				case 3:
					/* integer value */
					maskedOctets[i] = b;
					/* int == 32 bits, 4 * 7bits = 28 bits */
					int iResult = 0;
					for (int k = i; k >= 0 ; k--) {
						iResult = (iResult << 7) | maskedOctets[k];
					}
					// For negative values, the Unsigned Integer holds the
					// magnitude of the value minus 1
					return IntegerValue.valueOf(negative ? -(iResult+1) : iResult);
				default:
					/* long value */
					maskedOctets[i] = b;
					/* long == 64 bits, 9 * 7bits = 63 bits */
					long lResult = 0L;
					for (int k = i; k >= 0 ; k--) {
						lResult = (lResult << 7) | maskedOctets[k];
					}
					// For negative values, the Unsigned Integer holds the
					// magnitude of the value minus 1
					return IntegerValue.valueOf(negative ? -(lResult+1L) : lResult);
				}
			} else {
				// the 7 least significant bits hold the actual value
				maskedOctets[i] = (b & 127);
			}
		}

		// Grrr, we got a BigInteger value to deal with
		BigInteger bResult = BigInteger.ZERO;
		BigInteger multiplier = BigInteger.ONE;
		// already read bytes
		for (int i = 0; i < MAX_OCTETS_FOR_LONG; i++) {
			bResult = bResult.add(multiplier.multiply(BigInteger
					.valueOf(maskedOctets[i])));
			multiplier = multiplier.shiftLeft(7);
		}
		// read new bytes
		do {
			// 1. Read the next octet
			b = decode();
			// 2. The 7 least significant bits hold the value
			bResult = bResult.add(multiplier.multiply(BigInteger
					.valueOf(b & 127)));
			// 3. Multiply the multiplier by 128
			multiplier = multiplier.shiftLeft(7);
			// If the most significant bit of the octet was 1,
			// another is going to come
		} while (b > 127);

		// For negative values, the Unsigned Integer holds the
		// magnitude of the value minus 1
		if (negative) {
			bResult = bResult.add(BigInteger.ONE).negate();
		}

		return IntegerValue.valueOf(bResult);
	}

	/**
	 * Decodes and returns an n-bit unsigned integer as string.
	 */
	public IntegerValue decodeNBitUnsignedIntegerValue(int n)
			throws IOException {
		return IntegerValue.valueOf(decodeNBitUnsignedInteger(n));
	}

	/**
	 * Decode a decimal represented as a Boolean sign followed by two Unsigned
	 * Integers. A sign value of zero (0) is used to represent positive Decimal
	 * values and a sign value of one (1) is used to represent negative Decimal
	 * values The first Integer represents the integral portion of the Decimal
	 * value. The second positive integer represents the fractional portion of
	 * the decimal with the digits in reverse order to preserve leading zeros.
	 */
	public DecimalValue decodeDecimalValue() throws IOException {
		boolean negative = decodeBoolean();
		
		IntegerValue integral = decodeUnsignedIntegerValue(false);
		IntegerValue revFractional = decodeUnsignedIntegerValue(false);
		
		return new DecimalValue(negative, integral, revFractional);	
	}

	/**
	 * Decode a Float represented as two consecutive Integers. The first Integer
	 * represents the mantissa of the floating point number and the second
	 * Integer represents the 10-based exponent of the floating point number
	 */
	public FloatValue decodeFloatValue() throws IOException {
		return new FloatValue(decodeIntegerValue(), decodeIntegerValue());
	}

	/**
	 * Decode Date-Time as sequence of values representing the individual
	 * components of the Date-Time.
	 */
	public DateTimeValue decodeDateTimeValue(DateTimeType type)
			throws IOException {
		int year = 0, monthDay = 0, time = 0, fractionalSecs = 0;
		boolean presenceFractionalSecs = false;

		switch (type) {
		case gYear: // Year, [Time-Zone]
			year = decodeInteger() + DateTimeValue.YEAR_OFFSET;
			break;
		case gYearMonth: // Year, MonthDay, [TimeZone]
		case date: // Year, MonthDay, [TimeZone]
			year = decodeInteger() + DateTimeValue.YEAR_OFFSET;
			monthDay = decodeNBitUnsignedInteger(DateTimeValue.NUMBER_BITS_MONTHDAY);
			break;
		case dateTime: // Year, MonthDay, Time, [FractionalSecs], [TimeZone]
			// e.g. "0001-01-01T00:00:00.111+00:33";
			year = decodeInteger() + DateTimeValue.YEAR_OFFSET;
			monthDay = decodeNBitUnsignedInteger(DateTimeValue.NUMBER_BITS_MONTHDAY);
			// Note: *no* break;
		case time: // Time, [FractionalSecs], [TimeZone]
			// e.g. "12:34:56.135"
			time = decodeNBitUnsignedInteger(DateTimeValue.NUMBER_BITS_TIME);
			presenceFractionalSecs = decodeBoolean();
			fractionalSecs = presenceFractionalSecs ? decodeUnsignedInteger()
					: 0;
			break;
		case gMonth: // MonthDay, [TimeZone]
			// e.g. "--12"
		case gMonthDay: // MonthDay, [TimeZone]
			// e.g. "--01-28"
		case gDay: // MonthDay, [TimeZone]
			// "---16";
			monthDay = decodeNBitUnsignedInteger(DateTimeValue.NUMBER_BITS_MONTHDAY);
			break;
		default:
			throw new UnsupportedOperationException();
		}

		boolean presenceTimezone = decodeBoolean();
		int timeZone = presenceTimezone ? decodeNBitUnsignedInteger(DateTimeValue.NUMBER_BITS_TIMEZONE)
				- DateTimeValue.TIMEZONE_OFFSET_IN_MINUTES
				: 0;

		return new DateTimeValue(type, year, monthDay, time,
				presenceFractionalSecs, fractionalSecs, presenceTimezone,
				timeZone);
	}

}
