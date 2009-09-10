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

package com.siemens.ct.exi.io.channel;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Calendar;

import com.siemens.ct.exi.Constants;
import com.siemens.ct.exi.util.MethodsBag;
import com.siemens.ct.exi.util.datatype.DatetimeType;
import com.siemens.ct.exi.util.datatype.XSDBoolean;
import com.siemens.ct.exi.util.datatype.XSDDatetime;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.3.20081031
 */

public abstract class AbstractEncoderChannel implements EncoderChannel {

	/**
	 * Encode a binary value as a length-prefixed sequence of octets.
	 */
	public void encodeBinary(byte[] b) throws IOException {
		encodeUnsignedInteger(b.length);
		encode(b, 0, b.length);
	}

	public void encodeBoolean(XSDBoolean b) throws IOException,
			IllegalArgumentException {
		encodeBoolean(b.getBoolean());
	}

	/**
	 * Encode a string as a length-prefixed sequence of UCS codepoints, each of
	 * which is encoded as an integer. Look for codepoints of more than 16 bits
	 * that are represented as UTF-16 surrogate pairs in Java.
	 */
	public void encodeString(final String s) throws IOException {
		encodeUnsignedInteger(s.length());
		this.encodeStringOnly(s);
	}

	/**
	 * 
	 */
	public void encodeStringOnly(final String s) throws IOException {
		for (int i = 0; i < s.length(); i++) {
			final char ch = s.charAt(i);
			
			// Is this a UTF-16 surrogate pair?
			if (Character.isHighSurrogate(ch)) {
				encodeUnsignedInteger(Character.toCodePoint(ch, s.charAt(++i)));
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

	/**
	 * Encode a decimal represented as a Boolean sign followed by two Unsigned
	 * Integers. A sign value of zero (0) is used to represent positive Decimal
	 * values and a sign value of one (1) is used to represent negative Decimal
	 * values The first Integer represents the integral portion of the Decimal
	 * value. The second positive integer represents the fractional portion of
	 * the decimal with the digits in reverse order to preserve leading zeros.
	 */
	public void encodeDecimal(BigDecimal decimal) throws IOException {
		// detect whether decimal value is negative
		if (decimal.signum() < 0) {
			// negative
			decimal = decimal.abs();
			encodeBoolean(true);
		} else {
			// positive
			encodeBoolean(false);
		}

		// integral portion
		BigDecimal integral = decimal.setScale(0, RoundingMode.FLOOR);
		encodeUnsignedBigInteger(integral.toBigInteger());

		// fractional portion (reverse order)
		BigDecimal fractional = integral.signum() < 0 ? decimal.add(integral)
				: decimal.subtract(integral);
		fractional = fractional.movePointRight(decimal.scale());
		StringBuilder sb = new StringBuilder(fractional.toBigInteger()
				.toString());
		sb = sb.reverse();
		int length = sb.length();
		for (int i = 0; i < (decimal.scale() - length); i++) {
			sb.append('0');
		}
		encodeUnsignedBigInteger(new BigInteger(sb.toString()));
	}

	public void encodeDecimal(boolean negative, BigInteger integral,
			BigInteger reverseFraction) throws IOException, RuntimeException {
		// sign, integral, reverse fractional
		encodeBoolean(negative);
		encodeUnsignedBigInteger(integral);
		encodeUnsignedBigInteger(reverseFraction);
	}

	/**
	 * Encode a Double represented as two consecutive Integers. The first
	 * Integer represents the mantissa of the floating point number and the
	 * second Integer represents the 10-based exponent of the floating point
	 * number
	 */
	public void encodeFloat(float f) throws IOException {
		// infinity & not a number
		if (Float.isInfinite(f) || Float.isNaN(f)) {
			// exponent value is -(2^14),
			// . the mantissa value 1 represents INF,
			// . the mantissa value -1 represents -INF
			// . any other mantissa value represents NaN
			if (Float.isNaN(f)) {
				encodeInteger(Constants.FLOAT_MANTISSA_NOT_A_NUMBER); // m
			} else if (f < 0) {
				encodeInteger(Constants.FLOAT_MANTISSA_MINUS_INFINITY); // m
			} else {
				encodeInteger(Constants.FLOAT_MANTISSA_INFINITY); // m
			}
			// exponent (special value)
			encodeInteger(Constants.FLOAT_SPECIAL_VALUES); // e == -(2^14)
		} else {
			/*
			 * floating-point according to the IEEE 754 floating-point
			 * "single format" bit layout.
			 */
			int exponent = 0;
			while (f - (int) f != 0.0f) {
				f *= 10;
				exponent--;
			}
			int mantissa = (int) f;

			encodeFloat(mantissa, exponent);
		}
	}

	public void encodeFloat(int mantissa, int exponent) throws IOException {
		// encode mantissa and exponent
		encodeInteger(mantissa);
		encodeInteger(exponent);
	}

	public void encodeDouble(double d) throws IOException {
		// infinity & not a number
		if (Double.isInfinite(d) || Double.isNaN(d)) {
			// exponent value is -(2^14),
			// . the mantissa value 1 represents INF,
			// . the mantissa value -1 represents -INF
			// . any other mantissa value represents NaN
			if (Double.isNaN(d)) {
				encodeLong(Constants.FLOAT_MANTISSA_NOT_A_NUMBER); // m
			} else if (d < 0) {
				encodeLong(Constants.FLOAT_MANTISSA_MINUS_INFINITY); // m
			} else {
				encodeLong(Constants.FLOAT_MANTISSA_INFINITY); // m
			}
			// exponent (special value)
			encodeLong(Constants.FLOAT_SPECIAL_VALUES); // e == -(2^14)
		} else {
			/*
			 * floating-point according to the IEEE 754 floating-point
			 * "double format" bit layout.
			 */
			long exponent = 0;
			while (d - (long) d != 0.0d) {
				d *= 10;
				exponent--;
			}
			long mantissa = (long) d;

			encodeDouble(mantissa, exponent);
		}
	}

	public void encodeDouble(long mantissa, long exponent) throws IOException {
		// encode mantissa and exponent
		encodeLong(mantissa);
		encodeLong(exponent);
	}

	/**
	 * Encode Date-Time as a sequence of values representing the individual
	 * components of the Date-Time.
	 */
	public void encodeDateTime(Calendar cal, DatetimeType type)
			throws IOException {
		switch (type) {
		case gYear: // gYear Year, [Time-Zone]
		case gYearMonth: // gYearMonth Year, MonthDay, [TimeZone]
		case date: // date Year, MonthDay, [TimeZone]
			encodeInteger(cal.get(Calendar.YEAR) - XSDDatetime.YEAR_OFFSET);
			encodeNBitUnsignedInteger(XSDDatetime.getMonthDay(cal),
					XSDDatetime.NUMBER_BITS_MONTHDAY);
			encodeDateTimeTimezone(XSDDatetime.getTimeZoneInMinutesOffset(cal));
			break;
		case dateTime: // dateTime Year, MonthDay, Time, [FractionalSecs],
			// [TimeZone]
			encodeInteger(cal.get(Calendar.YEAR) - XSDDatetime.YEAR_OFFSET);
			encodeNBitUnsignedInteger(XSDDatetime.getMonthDay(cal),
					XSDDatetime.NUMBER_BITS_MONTHDAY);
			encodeNBitUnsignedInteger(XSDDatetime.getTime(cal),
					XSDDatetime.NUMBER_BITS_TIME);
			encodeDateTimeFractionalSecs(XSDDatetime
					.getFractionalSecondsReverse(cal.get(Calendar.MILLISECOND)));
			encodeDateTimeTimezone(XSDDatetime.getTimeZoneInMinutesOffset(cal));
			break;
		case gMonth: // gMonth MonthDay, [TimeZone]
		case gMonthDay: // gMonthDay MonthDay, [TimeZone]
		case gDay: // gDay MonthDay, [TimeZone]
			encodeNBitUnsignedInteger(XSDDatetime.getMonthDay(cal),
					XSDDatetime.NUMBER_BITS_MONTHDAY);
			encodeDateTimeTimezone(XSDDatetime.getTimeZoneInMinutesOffset(cal));
			break;
		case time: // time Time, [FractionalSecs], [TimeZone]
			this.encodeNBitUnsignedInteger(XSDDatetime.getTime(cal),
					XSDDatetime.NUMBER_BITS_TIME);
			encodeDateTimeFractionalSecs(XSDDatetime
					.getFractionalSecondsReverse(cal.get(Calendar.MILLISECOND)));
			encodeDateTimeTimezone(XSDDatetime.getTimeZoneInMinutesOffset(cal));
			break;
		default:
			throw new UnsupportedOperationException();
		}
	}

	public void encodeDateTime(XSDDatetime datetime) throws IOException {
		switch (datetime.getDatetimeType()) {
		case gYear: // gYear Year, [Time-Zone]
			encodeInteger(datetime.iYear);
			encodeDateTimeTimezone(datetime.iTZMinutes);
			break;
		case gYearMonth: // gYearMonth Year, MonthDay, [TimeZone]
		case date: // date Year, MonthDay, [TimeZone]
			encodeInteger(datetime.iYear);
			encodeNBitUnsignedInteger(datetime.iMonthDay,
					XSDDatetime.NUMBER_BITS_MONTHDAY);
			encodeDateTimeTimezone(datetime.iTZMinutes);
			break;
		case dateTime: // dateTime Year, MonthDay, Time, [FractionalSecs],
			// [TimeZone]
			encodeInteger(datetime.iYear);
			encodeNBitUnsignedInteger(datetime.iMonthDay,
					XSDDatetime.NUMBER_BITS_MONTHDAY);
			encodeNBitUnsignedInteger(datetime.iTime,
					XSDDatetime.NUMBER_BITS_TIME);
			encodeDateTimeFractionalSecs(datetime.iFractionalSecs);
			encodeDateTimeTimezone(datetime.iTZMinutes);
			break;
		case gMonth: // gMonth MonthDay, [TimeZone]
		case gMonthDay: // gMonthDay MonthDay, [TimeZone]
		case gDay: // gDay MonthDay, [TimeZone]
			encodeNBitUnsignedInteger(datetime.iMonthDay,
					XSDDatetime.NUMBER_BITS_MONTHDAY);
			encodeDateTimeTimezone(datetime.iTZMinutes);
			break;
		case time: // time Time, [FractionalSecs], [TimeZone]
			this.encodeNBitUnsignedInteger(datetime.iTime,
					XSDDatetime.NUMBER_BITS_TIME);
			encodeDateTimeFractionalSecs(datetime.iFractionalSecs);
			encodeDateTimeTimezone(datetime.iTZMinutes);
			break;
		default:
			throw new UnsupportedOperationException();
		}
	}

	private void encodeDateTimeTimezone(int tzMinutes) throws IOException {
		if (tzMinutes != 0) {
			encodeBoolean(true);
			encodeNBitUnsignedInteger(tzMinutes,
					XSDDatetime.NUMBER_BITS_TIMEZONE);
		} else {
			encodeBoolean(false);
		}
	}

	private void encodeDateTimeFractionalSecs(int reverseFracSecs)
			throws IOException {
		if (reverseFracSecs != 0) {
			encodeBoolean(true);
			encodeUnsignedInteger(reverseFracSecs);
		} else {
			encodeBoolean(false);
		}
	}
}
