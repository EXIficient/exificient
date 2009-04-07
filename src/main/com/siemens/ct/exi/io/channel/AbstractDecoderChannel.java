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
import java.util.Calendar;
import java.util.TimeZone;

import com.siemens.ct.exi.Constants;
import com.siemens.ct.exi.util.datatype.DatetimeType;
import com.siemens.ct.exi.util.datatype.XSDBase64;
import com.siemens.ct.exi.util.datatype.XSDDatetime;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.2.20081117
 */

public abstract class AbstractDecoderChannel implements DecoderChannel {
	final static int FLOAT_SPECIAL_VALUES = -16384; // -(2^14)

	final StringBuilder sb;

	public AbstractDecoderChannel() {
		sb = new StringBuilder();
	}

	public String decodeBinaryAsString() throws IOException {
		final byte[] b = decodeBinary();

		return new String(XSDBase64.encode(b));
	}

	public String decodeBooleanAsString() throws IOException {
		return (decodeBoolean() ? Constants.DECODED_BOOLEAN_TRUE
				: Constants.DECODED_BOOLEAN_FALSE);
	}

	/**
	 * Decode a string as a length-prefixed sequence of UCS codepoints, each of
	 * which is encoded as an integer. Look for codepoints of more than 16 bits
	 * that are represented as UTF-16 surrogate pairs in Java.
	 */
	public String decodeString() throws IOException {
		final int length = decodeUnsignedInteger();

		// StringBuilder result = new StringBuilder ( length );
		sb.setLength(0);

		for (int i = 0; i < length; i++) {
			int ch = decodeUnsignedInteger();

			if (Character.isSupplementaryCodePoint(ch)) {
				sb.append(Character.toChars(ch));
			} else {
				sb.append((char) ch);
			}
		}
		return sb.toString();
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
	public String decodeStringOnly(int length) throws IOException {
		// StringBuffer result = new StringBuffer ( length );
		sb.setLength(0);

		for (int i = 0; i < length; i++) {
			int ch = decodeUnsignedInteger();

			if (Character.isSupplementaryCodePoint(ch)) {
				sb.append(Character.toChars(ch));
			} else {
				sb.append((char) ch);
			}
		}
		return sb.toString();
	}

	/**
	 * Decode an arbitrary precision integer using a sign bit followed by a
	 * sequence of octets. The most significant bit of the last octet is set to
	 * zero to indicate sequence termination. Only seven bits per octet are used
	 * to store the integer's value.
	 */
	public int decodeInteger() throws IOException {
		// negative integer
		if (decodeBoolean()) {
			// negative
			// For negative values, the Unsigned Integer holds the
			// magnitude of the value minus 1
			return ((decodeUnsignedInteger() + 1) * (-1));
		} else {
			// positive
			return decodeUnsignedInteger();
		}
	}

	public long decodeIntegerAsLong() throws IOException {
		// negative integer
		if (decodeBoolean()) {
			// negative
			return ((decodeUnsignedIntegerAsLong() + 1) * (-1));
		} else {
			// positive
			return decodeUnsignedIntegerAsLong();
		}
	}

	public BigInteger decodeIntegerAsBigInteger() throws IOException {
		// negative integer
		if (decodeBoolean()) {
			return decodeUnsignedIntegerAsBigInteger().add(BigInteger.ONE)
					.negate();
		} else {
			return decodeUnsignedIntegerAsBigInteger();
		}
	}

	public String decodeIntegerAsString() throws IOException {
		if (decodeBoolean()) {
			sb.setLength(0);
			sb.append('-');
			sb.append(decodeUnsignedIntegerAsBigInteger().add(BigInteger.ONE).toString());
			return sb.toString();
		} else {
			return decodeUnsignedIntegerAsString();
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
		int multiplier = 1;
		int b;

		do {
			// 1. Read the next octet
			b = decode();
			// 2. Multiply the value of the unsigned number represented by the 7
			// least significant
			// bits of the octet by the current multiplier and add the result to
			// the current value.
			result += multiplier * (b & 127);
			// 3. Multiply the multiplier by 128
			multiplier = multiplier << 7;
			// 4. If the most significant bit of the octet was 1, go back to
			// step 1
		} while ((b >>> 7) == 1);

		return result;
	}

	public long decodeUnsignedIntegerAsLong() throws IOException {
		long result = 0;

		// 0XXXXXXX ... 1XXXXXXX 1XXXXXXX
		long multiplier = 1;
		int b;

		do {
			// 1. Read the next octet
			b = decode();
			// 2. Multiply the value of the unsigned number represented by the 7
			// least significant
			// bits of the octet by the current multiplier and add the result to
			// the current value.
			result += multiplier * (b & 127);
			// 3. Multiply the multiplier by 128
			multiplier = multiplier << 7;
			// 4. If the most significant bit of the octet was 1, go back to
			// step 1
		} while ((b >>> 7) == 1);

		return result;
	}

	public BigInteger decodeUnsignedIntegerAsBigInteger() throws IOException {
		BigInteger result = BigInteger.ZERO;

		// 0XXXXXXX ... 1XXXXXXX 1XXXXXXX
		BigInteger multiplier = BigInteger.ONE;
		int b;

		do {
			// 1. Read the next octet
			b = decode();
			// 2. Multiply the value of the unsigned number represented by the 7
			// least significant
			// bits of the octet by the current multiplier and add the result to
			// the current value.
			result = result.add(multiplier
					.multiply(BigInteger.valueOf(b & 127)));
			// 3. Multiply the multiplier by 128
			multiplier = multiplier.shiftLeft(7);
			// 4. If the most significant bit of the octet was 1, go back to
			// step 1
		} while ((b >>> 7) == 1);

		return result;
	}

	public String decodeUnsignedIntegerAsString() throws IOException {
		return decodeUnsignedIntegerAsBigInteger().toString();
		// return Integer.toString(decodeUnsignedInteger());
	}

	/**
	 * Decodes and returns an n-bit unsigned integer as string.
	 */
	public String decodeNBitUnsignedIntegerAsString(int n) throws IOException {
		// return decodeNBitUnsignedInteger ( n ) + "";
		sb.setLength(0);
		sb.append(decodeNBitUnsignedInteger(n));
		return sb.toString();
	}

	/**
	 * Decode a decimal represented as a Boolean sign followed by two Unsigned
	 * Integers. A sign value of zero (0) is used to represent positive Decimal
	 * values and a sign value of one (1) is used to represent negative Decimal
	 * values The first Integer represents the integral portion of the Decimal
	 * value. The second positive integer represents the fractional portion of
	 * the decimal with the digits in reverse order to preserve leading zeros.
	 */
	public BigDecimal decodeDecimal() throws IOException {
		boolean negative = decodeBoolean();

		BigInteger integral = decodeUnsignedIntegerAsBigInteger();
		String sFractional = new StringBuilder(
				decodeUnsignedIntegerAsBigInteger().toString()).reverse()
				.toString();

		return new BigDecimal(negative ? "-" + integral + "." + sFractional
				: integral + "." + sFractional);
	}

	public String decodeDecimalAsString() throws IOException {
		boolean negative = decodeBoolean();
		String integral = decodeUnsignedIntegerAsString();
		String sFractional = new StringBuilder(decodeUnsignedIntegerAsString())
				.reverse().toString();

		// return ( negative ? "-" + integral + "." + sFractional : integral +
		// "." + sFractional );
		sb.setLength(0);
		if (negative) {
			// "-"
			sb.append('-');

		}
		// integral + "." + sFractional
		sb.append(integral);
		sb.append('.');
		sb.append(sFractional);

		return sb.toString();
	}

	/**
	 * Decode a Float represented as two consecutive Integers. The first Integer
	 * represents the mantissa of the floating point number and the second
	 * Integer represents the 10-based exponent of the floating point number
	 */
	public float decodeFloat() throws IOException {
		int iMantissa = decodeInteger();
		int iExponent = decodeInteger();

		if (iExponent == FLOAT_SPECIAL_VALUES) {
			if (iMantissa == -1) {
				return Float.NEGATIVE_INFINITY;
			} else if (iMantissa == 1) {
				return Float.POSITIVE_INFINITY;
			} else {
				return Float.NaN;
			}
		} else {
			return Float.parseFloat(iMantissa + "E" + iExponent);
		}
	}

	public String decodeFloatAsString() throws IOException {
		long iMantissa = decodeIntegerAsLong();
		long iExponent = decodeIntegerAsLong();

		if (iExponent == FLOAT_SPECIAL_VALUES) {
			if (iMantissa == -1) {
				return "-INF";
			} else if (iMantissa == 1) {
				return "INF";
			} else {
				return "NaN";
			}
		} else {
			// return iMantissa + "E" + iExponent;
			sb.setLength(0);
			sb.append(iMantissa);
			sb.append('E');
			sb.append(iExponent);
			return sb.toString();
		}
	}

	/**
	 * Decode Date-Time as sequence of values representing the individual
	 * components of the Date-Time.
	 */
	public Calendar decodeDateTime(DatetimeType type) throws IOException {
		Calendar cal = Calendar.getInstance();
		cal.clear();

		switch (type) {
		case gYear: // gYear Year, [Time-Zone]
			cal.set(Calendar.YEAR, decodeInteger() + XSDDatetime.YEAR_OFFSET);
			decodeDateTimeTimezone(cal);
			break;
		case gYearMonth: // gYearMonth Year, MonthDay, [TimeZone]
		case date: // date Year, MonthDay, [TimeZone]
			cal.set(Calendar.YEAR, decodeInteger() + XSDDatetime.YEAR_OFFSET);
			XSDDatetime
					.setMonthDay(
							decodeNBitUnsignedInteger(XSDDatetime.NUMBER_BITS_MONTHDAY),
							cal);
			decodeDateTimeTimezone(cal);
			break;
		case dateTime: // dateTime Year, MonthDay, Time, [FractionalSecs],
			// [TimeZone]
			cal.set(Calendar.YEAR, decodeInteger() + XSDDatetime.YEAR_OFFSET);
			XSDDatetime
					.setMonthDay(
							decodeNBitUnsignedInteger(XSDDatetime.NUMBER_BITS_MONTHDAY),
							cal);
			XSDDatetime.setTime(
					decodeNBitUnsignedInteger(XSDDatetime.NUMBER_BITS_TIME),
					cal);
			decodeDateTimeFractionalSecs(cal);
			decodeDateTimeTimezone(cal);
			break;
		case gMonth: // gMonth MonthDay, [TimeZone]
		case gMonthDay: // gMonthDay MonthDay, [TimeZone]
		case gDay: // gDay MonthDay, [TimeZone]
			XSDDatetime
					.setMonthDay(
							decodeNBitUnsignedInteger(XSDDatetime.NUMBER_BITS_MONTHDAY),
							cal);
			decodeDateTimeTimezone(cal);
			break;
		case time: // time Time, [FractionalSecs], [TimeZone]
			XSDDatetime.setTime(
					decodeNBitUnsignedInteger(XSDDatetime.NUMBER_BITS_TIME),
					cal);
			decodeDateTimeFractionalSecs(cal);
			decodeDateTimeTimezone(cal);
			break;
		default:
			throw new UnsupportedOperationException();
		}

		return cal;
	}

	public String decodeDateTimeAsString(DatetimeType type) throws IOException {
		// StringBuilder sbCal = new StringBuilder ( );
		sb.setLength(0);

		switch (type) {
		case gYear: // gYear Year, [Time-Zone]
			XSDDatetime.appendYear(sb, decodeInteger()
					+ XSDDatetime.YEAR_OFFSET);
			decodeDateTimeTimezone(sb);
			break;
		case gYearMonth: // gYearMonth Year, MonthDay, [TimeZone]
			XSDDatetime.appendYear(sb, decodeInteger()
					+ XSDDatetime.YEAR_OFFSET);
			XSDDatetime
					.appendMonth(
							sb,
							decodeNBitUnsignedInteger(XSDDatetime.NUMBER_BITS_MONTHDAY));
			decodeDateTimeTimezone(sb);
			break;
		case date: // date Year, MonthDay, [TimeZone]
			XSDDatetime.appendYear(sb, decodeInteger()
					+ XSDDatetime.YEAR_OFFSET);
			XSDDatetime
					.appendMonthDay(
							sb,
							decodeNBitUnsignedInteger(XSDDatetime.NUMBER_BITS_MONTHDAY));
			decodeDateTimeTimezone(sb);
			break;
		case dateTime: // dateTime Year, MonthDay, Time, [FractionalSecs],
			// [TimeZone]
			XSDDatetime.appendYear(sb, decodeInteger()
					+ XSDDatetime.YEAR_OFFSET);
			XSDDatetime
					.appendMonthDay(
							sb,
							decodeNBitUnsignedInteger(XSDDatetime.NUMBER_BITS_MONTHDAY));
			sb.append('T');
			XSDDatetime.appendTime(sb,
					decodeNBitUnsignedInteger(XSDDatetime.NUMBER_BITS_TIME));
			decodeDateTimeFractionalSecs(sb);
			decodeDateTimeTimezone(sb);
			break;
		case gMonth: // gMonth MonthDay, [TimeZone]
			sb.append('-');
			XSDDatetime
					.appendMonth(
							sb,
							decodeNBitUnsignedInteger(XSDDatetime.NUMBER_BITS_MONTHDAY));
			decodeDateTimeTimezone(sb);
			break;
		case gMonthDay: // gMonthDay MonthDay, [TimeZone]
			sb.append('-');
			XSDDatetime
					.appendMonthDay(
							sb,
							decodeNBitUnsignedInteger(XSDDatetime.NUMBER_BITS_MONTHDAY));
			decodeDateTimeTimezone(sb);
			break;
		case gDay: // gDay MonthDay, [TimeZone]
			sb.append('-');
			sb.append('-');
			XSDDatetime
					.appendDay(
							sb,
							decodeNBitUnsignedInteger(XSDDatetime.NUMBER_BITS_MONTHDAY));
			decodeDateTimeTimezone(sb);
			break;
		case time: // time Time, [FractionalSecs], [TimeZone]
			XSDDatetime.appendTime(sb,
					decodeNBitUnsignedInteger(XSDDatetime.NUMBER_BITS_TIME));
			decodeDateTimeFractionalSecs(sb);
			decodeDateTimeTimezone(sb);
			break;
		default:
			throw new UnsupportedOperationException();
		}

		return sb.toString();
	}

	private void decodeDateTimeTimezone(Calendar cal) throws IOException {
		int tz = 0;

		if (decodeBoolean()) {
			tz = XSDDatetime
					.getTimeZoneInMillisecs(decodeNBitUnsignedInteger(XSDDatetime.NUMBER_BITS_TIMEZONE)
							- XSDDatetime.TIMEZONE_OFFSET_IN_MINUTES);
		}

		TimeZone tzO = TimeZone.getTimeZone("GMT+00:00");
		tzO.setRawOffset(tz);
		cal.setTimeZone(tzO);
	}

	private void decodeDateTimeTimezone(StringBuilder sbCal) throws IOException {
		if (decodeBoolean()) {
			int tz = decodeNBitUnsignedInteger(XSDDatetime.NUMBER_BITS_TIMEZONE)
					- XSDDatetime.TIMEZONE_OFFSET_IN_MINUTES;
			XSDDatetime.appendTimezone(sbCal, tz);
		}
	}

	private void decodeDateTimeFractionalSecs(Calendar cal) throws IOException {
		cal.set(Calendar.MILLISECOND, decodeBoolean() ? decodeUnsignedInteger()
				: 0);
	}

	private void decodeDateTimeFractionalSecs(StringBuilder sbCal)
			throws IOException {
		XSDDatetime.appendFractionalSeconds(sbCal,
				decodeBoolean() ? decodeUnsignedInteger() : 0);
	}
}
