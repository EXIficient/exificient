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
import java.math.BigInteger;

import com.siemens.ct.exi.values.BooleanValue;
import com.siemens.ct.exi.values.DateTimeType;
import com.siemens.ct.exi.values.DateTimeValue;
import com.siemens.ct.exi.values.DecimalValue;
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

public abstract class AbstractDecoderChannel implements DecoderChannel {

	public AbstractDecoderChannel() {
	}

	public BooleanValue decodeBooleanValue() throws IOException {
		return new BooleanValue(decodeBoolean() );
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
	 * Decode the characters of a string whose length has already been read.
	 * Look for codepoints of more than 16 bits that are represented as UTF-16
	 * surrogate pairs in Java.
	 * 
	 * @param length
	 *            Length of the character sequence to read.
	 * @return The character sequence as a string.
	 */
	public char[] decodeStringOnly(int length) throws IOException {
		char[] ca = new char[length];
		
		for (int i = 0; i < length; i++) {
			int codePoint = decodeUnsignedInteger();

			if (Character.isSupplementaryCodePoint(codePoint)) {
				Character.toChars(codePoint, ca, i++);
			} else {
				ca[i] = (char) codePoint;
			}
		}
		
		return ca;
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
	
	public IntegerValue decodeIntegerValue() throws IOException {
		return new IntegerValue(decodeInteger());
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
	
	public LongValue decodeLongValue() throws IOException {
		return new LongValue (decodeLong());
	}
	


	public HugeIntegerValue decodeHugeIntegerValue() throws IOException {
		HugeIntegerValue bi;
		if (decodeBoolean()) {
			// For negative values, the Unsigned Integer holds the
			// magnitude of the value minus 1
			bi = decodeUnsignedHugeIntegerValue();
			if (bi.isLongValue) {
				bi = new HugeIntegerValue(-(bi.longValue + 1L));
			} else {
				// TODO look for a more memory sensitive way !?
				bi = new HugeIntegerValue( bi.bigIntegerValue.add(BigInteger.ONE).negate());
			}
		} else {
			// positive
			bi = decodeUnsignedHugeIntegerValue();
		}
		
		return bi;
	}

	public HugeIntegerValue decodeUnsignedHugeIntegerValue() throws IOException {
		long lResult = 0L;
		int mShift = 0;
		int b;

		// long == 64 bits
		// 9 x 7 bits --> 63
		int cntBytes = 0;
		boolean isLongValue = true;

		do {
			if (cntBytes >= 9) {
				isLongValue = false;
				break;
			}
			b = decode();
			cntBytes++;
			lResult += ((long) (b & 127)) << mShift;
			mShift += 7;
		} while ((b >>> 7) == 1);

		if (isLongValue) {
			return new HugeIntegerValue(lResult);
		} else {
			// keep on decoding
			BigInteger bResult = BigInteger.valueOf(lResult);
			BigInteger multiplier = BigInteger.ONE;
			multiplier = multiplier.shiftLeft(7 * cntBytes);

			do {
				b = decode();
				bResult = bResult.add(multiplier.multiply(BigInteger
						.valueOf(b & 127)));
				multiplier = multiplier.shiftLeft(7);
			} while ((b >>> 7) == 1);

			return new HugeIntegerValue(bResult);
		}
	}

	/**
	 * Decode an arbitrary precision non negative integer using a sequence of
	 * octets. The most significant bit of the last octet is set to zero to
	 * indicate sequence termination. Only seven bits per octet are used to
	 * store the integer's value.
	 */
	public int decodeUnsignedInteger() throws IOException {
		int result = 0;

		// 0XXXXXXX ... 1XXXXXXX 1XXXXXXX
		// int multiplier = 1;
		int mShift = 0;
		int b;

		do {
			// 1. Read the next octet
			b = decode();
			// 2. Multiply the value of the unsigned number represented by the 7
			// least significant
			// bits of the octet by the current multiplier and add the result to
			// the current value.
			// result += (b & 127) * multiplier;
			result += (b & 127) << mShift;
			// 3. Multiply the multiplier by 128
			// multiplier = multiplier << 7;
			mShift += 7;
			// 4. If the most significant bit of the octet was 1, go back to
			// step 1
		} while ((b >>> 7) == 1);

		return result;
	}
	
	public IntegerValue decodeUnsignedIntegerValue() throws IOException {
		return new IntegerValue(decodeUnsignedInteger());
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
	
	public LongValue decodeUnsignedLongValue() throws IOException {
		return new LongValue(decodeUnsignedLong());
	}

	/**
	 * Decodes and returns an n-bit unsigned integer as string.
	 */
	public IntegerValue decodeNBitUnsignedIntegerValue(int n)
			throws IOException {
		return new IntegerValue(decodeNBitUnsignedInteger(n));
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

		HugeIntegerValue integral = decodeUnsignedHugeIntegerValue();
		HugeIntegerValue revFractional = decodeUnsignedHugeIntegerValue();

		return new DecimalValue(negative, integral, revFractional);
	}

	/**
	 * Decode a Float represented as two consecutive Integers. The first Integer
	 * represents the mantissa of the floating point number and the second
	 * Integer represents the 10-based exponent of the floating point number
	 */
	public FloatValue decodeFloatValue() throws IOException {
		long mantissa = decodeLong();
		long exponent = decodeLong();
		return new FloatValue(mantissa, exponent);
	}

	/**
	 * Decode Date-Time as sequence of values representing the individual
	 * components of the Date-Time.
	 */
	public DateTimeValue decodeDateTimeValue(DateTimeType type) throws IOException {
		int year = 0, monthDay = 0, time = 0, fractionalSecs = 0;
		boolean presenceFractionalSecs = false;

		switch (type) {
		case gYear: // Year, [Time-Zone]
			year = decodeInteger() + DateTimeValue.YEAR_OFFSET;
			break;
		case gYearMonth: // Year, MonthDay, [TimeZone]
			year = decodeInteger() + DateTimeValue.YEAR_OFFSET;
			monthDay = decodeNBitUnsignedInteger(DateTimeValue.NUMBER_BITS_MONTHDAY);
			break;
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
			fractionalSecs = presenceFractionalSecs ? decodeUnsignedInteger() : 0;
			break;
		case gMonth: // MonthDay, [TimeZone]
			// e.g. "--12"
			monthDay = decodeNBitUnsignedInteger(DateTimeValue.NUMBER_BITS_MONTHDAY);
			break;
		case gMonthDay: // MonthDay, [TimeZone]
			// e.g. "--01-28"
			monthDay = decodeNBitUnsignedInteger(DateTimeValue.NUMBER_BITS_MONTHDAY);
			break;
		case gDay: // MonthDay, [TimeZone]
			// "---16";
			monthDay = decodeNBitUnsignedInteger(DateTimeValue.NUMBER_BITS_MONTHDAY);
			break;
		default:
			throw new UnsupportedOperationException();
		}

		boolean presenceTimezone = decodeBoolean();
		int timeZone =  presenceTimezone ? decodeNBitUnsignedInteger(DateTimeValue.NUMBER_BITS_TIMEZONE)
				- DateTimeValue.TIMEZONE_OFFSET_IN_MINUTES
				: 0;
		
		return new DateTimeValue(type, year, monthDay, time, presenceFractionalSecs, fractionalSecs,
				presenceTimezone, timeZone);
	}

}
