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
import java.math.BigInteger;

import com.siemens.ct.exi.util.MethodsBag;
import com.siemens.ct.exi.values.DateTimeValue;
import com.siemens.ct.exi.values.HugeIntegerValue;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.6
 */

public abstract class AbstractEncoderChannel implements EncoderChannel {

	/**
	 * Encode a binary value as a length-prefixed sequence of octets.
	 */
	public void encodeBinary(byte[] b) throws IOException {
		encodeUnsignedInteger(b.length);
		encode(b, 0, b.length);
	}

	/**
	 * Encode a string as a length-prefixed sequence of UCS codepoints, each of
	 * which is encoded as an integer. Look for codepoints of more than 16 bits
	 * that are represented as UTF-16 surrogate pairs in Java.
	 */
	public void encodeString(final String s) throws IOException {
		final int lenChars = s.length();
		final int lenCharacters = s.codePointCount(0, lenChars);
		encodeUnsignedInteger(lenCharacters);
		encodeStringOnly(s);
	}

	/**
	 * 
	 */
	public void encodeStringOnly(final String s) throws IOException {
		final int lenChars = s.length();
		for (int i = 0; i<lenChars; i++) {
			final char ch = s.charAt(i);

			// Is this a UTF-16 surrogate pair?
			if (Character.isHighSurrogate(ch)) {
				// use code-point and increment loop count (2 char's)
				encodeUnsignedInteger(s.codePointAt(i++));
			} else {
				encodeUnsignedInteger(ch);
			}
		}
	}

	/**
	 * Encode an arbitrary precision integer using a sign bit followed by a
	 * sequence of octets. The most significant bit of the last octet is set to
	 * zero to indicate sequence termination. Only seven bits per octet are used
	 * to store the integer's value.
	 */
	public void encodeInteger(int n) throws IOException {
		// signalize sign
		if (n < 0) {
			encodeBoolean(true);
			// For negative values, the Unsigned Integer holds the
			// magnitude of the value minus 1
			encodeUnsignedInteger((-n) - 1);
		} else {
			encodeBoolean(false);
			encodeUnsignedInteger(n);
		}
	}

	public void encodeLong(long l) throws IOException {
		// signalize sign
		if (l < 0) {
			encodeBoolean(true);
			encodeUnsignedLong((-l) - 1);
		} else {
			encodeBoolean(false);
			encodeUnsignedLong(l);
		}
	}

	public void encodeBigInteger(BigInteger bi) throws IOException {
		if (bi.signum() < 0) {
			encodeBoolean(true); // negative
			encodeUnsignedBigInteger(bi.negate().subtract(BigInteger.ONE));
		} else {
			encodeBoolean(false); // positive
			encodeUnsignedBigInteger(bi);
		}
	}
	
	public void encodeHugeInteger(HugeIntegerValue hi) throws IOException {
		if (hi.isLongValue) {
			encodeLong(hi.longValue);
		} else {
			encodeBigInteger(hi.bigIntegerValue);
		}
	}

	/**
	 * Encode an arbitrary precision non negative integer using a sequence of
	 * octets. The most significant bit of the last octet is set to zero to
	 * indicate sequence termination. Only seven bits per octet are used to
	 * store the integer's value.
	 */
	public void encodeUnsignedInteger(int n) throws IOException {
		if (n < 0) {
			throw new UnsupportedOperationException();
		}

		if (n < 128) {
			// write byte as is
			encode(n);
		} else {
			final int n7BitBlocks = MethodsBag.numberOf7BitBlocksToRepresent(n);

			switch (n7BitBlocks) {
			case 5:
				encode(128 | n);
				n = n >>> 7;
			case 4:
				encode(128 | n);
				n = n >>> 7;
			case 3:
				encode(128 | n);
				n = n >>> 7;
			case 2:
				encode(128 | n);
				n = n >>> 7;
			case 1:
				// 0 .. 7 (last byte)
				encode(0 | n);
			}
		}
	}

	public void encodeUnsignedLong(long l) throws IOException {
		if (l < 0) {
			throw new UnsupportedOperationException();
		}

		int lastEncode = (int) l;
		l >>>= 7;

		while (l != 0) {
			encode(lastEncode | 128);
			lastEncode = (int) l;
			l >>>= 7;
		}

		encode(lastEncode);
	}

	public void encodeUnsignedBigInteger(BigInteger bi) throws IOException {
		if (bi.signum() < 0) {
			throw new UnsupportedOperationException();
		}

		// does not fit into long (64 bits)
		// approach: write byte per byte
		int m = bi.bitLength() % 7;
		int nbytes = bi.bitLength() / 7 + (m > 0 ? 1 : 0);

		while (--nbytes > 0) {
			// 1XXXXXXX ... 1XXXXXXX
			encode(128 | bi.intValue());
			bi = bi.shiftRight(7);
		}

		// 0XXXXXXX
		encode(0 | bi.intValue());
	}
	
	public void encodeUnsignedHugeInteger(HugeIntegerValue hi) throws IOException {
		if (hi.isLongValue) {
			encodeUnsignedLong(hi.longValue);
		} else {
			encodeUnsignedBigInteger(hi.bigIntegerValue);
		}
	}

	/**
	 * Encode a decimal represented as a Boolean sign followed by two Unsigned
	 * Integers. A sign value of zero (0) is used to represent positive Decimal
	 * values and a sign value of one (1) is used to represent negative Decimal
	 * values The first Integer represents the integral portion of the Decimal
	 * value. The second positive integer represents the fractional portion of
	 * the decimal with the digits in reverse order to preserve leading zeros.
	 */

	public void encodeDecimal(boolean negative, HugeIntegerValue integral,
			HugeIntegerValue reverseFraction) throws IOException, RuntimeException {
		// sign, integral, reverse fractional
		encodeBoolean(negative);
		encodeUnsignedHugeInteger(integral);
		encodeUnsignedHugeInteger(reverseFraction);
	}

	/**
	 * Encode a Float represented as two consecutive Integers. The first
	 * Integer represents the mantissa of the floating point number and the
	 * second Integer represents the 10-based exponent of the floating point
	 * number
	 */
	public void encodeFloat(long mantissa, long exponent) throws IOException {
		// encode mantissa and exponent
		encodeLong(mantissa);
		encodeLong(exponent);
	}



	public void encodeDateTime(DateTimeValue datetime) throws IOException {
		switch (datetime.type) {
		case gYear: // Year, [Time-Zone]
			encodeInteger(datetime.year - DateTimeValue.YEAR_OFFSET);
			break;
		case gYearMonth: // Year, MonthDay, [TimeZone]
		case date: // Year, MonthDay, [TimeZone]
			encodeInteger(datetime.year - DateTimeValue.YEAR_OFFSET);
			encodeNBitUnsignedInteger(datetime.monthDay,
					DateTimeValue.NUMBER_BITS_MONTHDAY);
			break;
		case dateTime: // Year, MonthDay, Time, [FractionalSecs],
			// [TimeZone]
			encodeInteger(datetime.year - DateTimeValue.YEAR_OFFSET);
			encodeNBitUnsignedInteger(datetime.monthDay,
					DateTimeValue.NUMBER_BITS_MONTHDAY);
			// Note: *no* break;
		case time: // Time, [FractionalSecs], [TimeZone]
			this.encodeNBitUnsignedInteger(datetime.time,
					DateTimeValue.NUMBER_BITS_TIME);
			if (datetime.presenceFractionalSecs) {
				encodeBoolean(true);
				encodeUnsignedInteger(datetime.fractionalSecs);
			} else {
				encodeBoolean(false);
			}
			break;
		case gMonth: // MonthDay, [TimeZone]
		case gMonthDay: // MonthDay, [TimeZone]
		case gDay: // MonthDay, [TimeZone]
			encodeNBitUnsignedInteger(datetime.monthDay,
					DateTimeValue.NUMBER_BITS_MONTHDAY);
			break;
		default:
			throw new UnsupportedOperationException();
		}
		// [TimeZone]
		if (datetime.presenceTimezone) {
			encodeBoolean(true);
			encodeNBitUnsignedInteger(datetime.timezone,
					DateTimeValue.NUMBER_BITS_TIMEZONE);
		} else {
			encodeBoolean(false);
		}
	}

}
