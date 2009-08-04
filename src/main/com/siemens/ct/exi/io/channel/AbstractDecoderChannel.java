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
import com.siemens.ct.exi.util.MethodsBag;
import com.siemens.ct.exi.util.datatype.DatetimeType;
import com.siemens.ct.exi.util.datatype.XSDBase64;
import com.siemens.ct.exi.util.datatype.XSDDatetime;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.3.20081117
 */

public abstract class AbstractDecoderChannel implements DecoderChannel {
	final StringBuilder sb;

	public AbstractDecoderChannel() {
		sb = new StringBuilder();
	}

	public char[] decodeBinaryAsString() throws IOException {
		final byte[] b = decodeBinary();
		return XSDBase64.encode(b);
		// return new CharArray(XSDBase64.encode(b));
		// return new String(XSDBase64.encode(b));
	}


	public char[] decodeBooleanAsString() throws IOException {
		// return (decodeBoolean() ? Constants.DECODED_BOOLEAN_TRUE
		// : Constants.DECODED_BOOLEAN_FALSE);
		return (decodeBoolean() ? Constants.DECODED_BOOLEAN_TRUE : Constants.DECODED_BOOLEAN_FALSE);
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
	public int decodeInteger() throws IOException {
		if (decodeBoolean()) {
			// For negative values, the Unsigned Integer holds the
			// magnitude of the value minus 1
			return (-(decodeUnsignedInteger() + 1));
		} else {
			// positive
			return decodeUnsignedInteger();
		}
	}
	
	public long decodeLong() throws IOException {
		if (decodeBoolean()) {
			// For negative values, the Unsigned Integer holds the
			// magnitude of the value minus 1
			return (-(decodeUnsignedLong() + 1L));
		} else {
			// positive
			return decodeUnsignedLong();
		}
	}
	
	public BigInteger decodeBigInteger() throws IOException {
		if (decodeBoolean()) {
			// For negative values, the Unsigned Integer holds the
			// magnitude of the value minus 1
			return decodeUnsignedBigInteger().add(BigInteger.ONE).negate();
		} else {
			// positive
			return decodeUnsignedBigInteger();
		}
	}
	
//	protected long decodeUnsignedIntegerAsLong() throws IOException {
//		long result = 0;
//
//		// 0XXXXXXX ... 1XXXXXXX 1XXXXXXX
//		long multiplier = 1;
//		int b;
//
//		do {
//			// 1. Read the next octet
//			b = decode();
//			// 2. Multiply the value of the unsigned number represented by the 7
//			// least significant
//			// bits of the octet by the current multiplier and add the result to
//			// the current value.
//			result += multiplier * (b & 127);
//			// 3. Multiply the multiplier by 128
//			multiplier = multiplier << 7;
//			// 4. If the most significant bit of the octet was 1, go back to
//			// step 1
//		} while ((b >>> 7) == 1);
//
//		return result;
//	}

//	protected long decodeIntegerAsLong() throws IOException {
//		// negative integer
//		if (decodeBoolean()) {
//			// negative
//			return (-(decodeUnsignedIntegerAsLong() + 1));
//		} else {
//			// positive
//			return decodeUnsignedIntegerAsLong();
//		}
//	}

	// public BigInteger decodeIntegerAsBigInteger() throws IOException {
	// // negative integer
	// if (decodeBoolean()) {
	// return decodeUnsignedIntegerAsBigInteger().add(BigInteger.ONE)
	// .negate();
	// } else {
	// return decodeUnsignedIntegerAsBigInteger();
	// }
	// }

	
	public char[] decodeIntegerAsString() throws IOException {
		return MethodsBag.itos(decodeInteger());
	}
	
	public char[] decodeLongAsString() throws IOException {
		return MethodsBag.itos(decodeLong());
	}
	
	public char[] decodeBigIntegerAsString() throws IOException {
		//	TODO look for a more memory sensitive way !?
		return decodeBigInteger().toString().toCharArray();
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
	
	public long decodeUnsignedLong() throws IOException {
		long lResult = 0L;
		int mShift = 0;
		int b;
		
		do {
			b = decode();
			lResult += ((long)(b & 127)) << mShift;
			mShift += 7;
		} while ((b >>> 7) == 1);
		
		return lResult;
	}
	
	public BigInteger decodeUnsignedBigInteger() throws IOException {
		int b;
		BigInteger bResult = BigInteger.ZERO;
		BigInteger multiplier = BigInteger.ONE;
		
		do {
			b = decode();
			bResult = bResult.add(multiplier.multiply(BigInteger
					.valueOf(b & 127)));
			multiplier = multiplier.shiftLeft(7);
		} while ((b >>> 7) == 1);
		
		return bResult;
	}

//	protected void decodeToXSDInteger(XSDInteger xsd) throws IOException {
//		boolean negative = decodeBoolean();
//		decodeToUnsignedXSDInteger(xsd);
//		if (negative) {
//			xsd.addOneAndNegate();
//		}
//	}
//
//
//	protected void decodeToUnsignedXSDInteger(XSDInteger xsd) throws IOException {
//		// 0XXXXXXX ... 1XXXXXXX 1XXXXXXX
//		// 
//		// 1. Read the next octet
//		// 2. Multiply the value of the unsigned number represented by
//		// the 7 least significant bits of the octet by the current multiplier
//		// and add the result to the current value.
//		// 3. Multiply the multiplier by 128
//		// 4. If the most significant bit of the octet was 1, go back to
//		// step 1
//
//		int b;
//
//		// integer ?
////		int iMultiplier = 1;
//		int mShift = 0;
//		int iResult = 0;
//		for (int i = 0; i < 4; i++) {
//			b = decode();
//			// iResult += (b & 127) * iMultiplier;
//			iResult += (b & 127) << mShift;
//			if ((b >>> 7) != 1) {
//				xsd.setValue(iResult);
//				return;
//			}
////			 iMultiplier *= 128;
////			iMultiplier = iMultiplier << 7;
//			mShift += 7;
//		}
//
//		// long ?
//		long lResult = iResult;
////		long lMultiplier = iMultiplier;
//		for (int i = 0; i < 4; i++) {
//			b = decode();
//			// lResult += (b & 127) * lMultiplier;
//			lResult += ((long)(b & 127)) << mShift;
//			if ((b >>> 7) != 1) {
//				xsd.setValue(lResult);
//				return;
//			}
////			 lMultiplier *= 128;
////			lMultiplier = lMultiplier << 7;
//			mShift += 7;
//		}
//
//		// big integer
//		BigInteger bResult = BigInteger.valueOf(lResult);
//		// BigInteger multiplier = BigInteger.valueOf(lMultiplier);
//		BigInteger multiplier = BigInteger.valueOf(72057594037927936L); //	72057594037927936
//		do {
//			b = decode();
//			bResult = bResult.add(multiplier.multiply(BigInteger
//					.valueOf(b & 127)));
//			//	shift left does not work !?
//			// bResult = BigInteger.valueOf(b & 127).shiftLeft(mShift);
//			multiplier = multiplier.shiftLeft(7);
//			// mShift += 7;
//		} while ((b >>> 7) == 1);
//
//		xsd.setValue(bResult);
//	}

	public char[] decodeUnsignedIntegerAsString() throws IOException {
		return MethodsBag.itos(decodeUnsignedInteger());
	}
	
	public char[] decodeUnsignedLongAsString() throws IOException {
		return MethodsBag.itos(decodeUnsignedLong());
	}
	
	public char[] decodeUnsignedBigIntegerAsString() throws IOException {
		return decodeUnsignedBigInteger().toString().toCharArray();
	}

	/**
	 * Decodes and returns an n-bit unsigned integer as string.
	 */
	public char[] decodeNBitUnsignedIntegerAsString(int n) throws IOException {
//		decodeNBitUnsignedInteger(n);
		return MethodsBag.itos(n);
		// return new CharArray(Integer.toString(decodeNBitUnsignedInteger(n)).toCharArray());
		// return Integer.toString(decodeNBitUnsignedInteger(n));
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
		// return new BigDecimal(decodeDecimalAsString());
		return new BigDecimal(decodeDecimalAsString());
	}

	public char[] decodeDecimalAsString() throws IOException {
		boolean negative = decodeBoolean();

		char[] integral = decodeUnsignedBigIntegerAsString();
		char[] fractional = decodeUnsignedBigIntegerAsString();
		
		int aLen = (negative? 1 : 0) + integral.length + 1 + fractional.length;
		char[] caDecimal = new char[aLen];
		
		int cnt = 0;
		//	negative
		if (negative) {
			caDecimal[cnt++] = '-';
		}
		//	integral
		for(int i=0; i<integral.length; i++) {
			caDecimal[cnt++] = integral[i];
		}
		//	dot
		caDecimal[cnt++] = '.';
		//	fractional (reverse)
		for(int i=fractional.length-1; i>=0; i--) {
			caDecimal[cnt++] = fractional[i];
		}

		return caDecimal;
		
		
		
		
//		boolean negative = decodeBoolean();
//
//		String integral = decodeUnsignedIntegerAsString();
//		sb.setLength(0);
//		sb.append(decodeUnsignedIntegerAsString());
//		String sFractional = sb.reverse().toString();
//
//		return (negative ? "-" + integral + "." + sFractional : integral + "."
//				+ sFractional);
	}

	/**
	 * Decode a Float represented as two consecutive Integers. The first Integer
	 * represents the mantissa of the floating point number and the second
	 * Integer represents the 10-based exponent of the floating point number
	 */
	public float decodeFloat() throws IOException {
		int iMantissa = decodeInteger();
		int iExponent = decodeInteger();

		if (iExponent == Constants.FLOAT_SPECIAL_VALUES) {
			if (iMantissa == -1) {
				return Float.NEGATIVE_INFINITY;
			} else if (iMantissa == 1) {
				return Float.POSITIVE_INFINITY;
			} else {
				return Float.NaN;
			}
		} else {
			return iMantissa * (float)(Math.pow(10, iExponent));
		}
	}
	
	public char[] decodeFloatAsString() throws IOException {
		int iMantissa = decodeInteger();
		int iExponent = decodeInteger();

		if (iExponent == Constants.FLOAT_SPECIAL_VALUES) {
			if (iMantissa == -1) {
				return Constants.FLOAT_MINUS_INFINITY_CHARARRAY;
			} else if (iMantissa == 1) {
				return Constants.FLOAT_INFINITY_CHARARRAY;
			} else {
				return Constants.FLOAT_NOT_A_NUMBER_CHARARRAY;
			}
		} else {
			char[] cMantissa = MethodsBag.itos(iMantissa);
			char[] cExponent = MethodsBag.itos(iExponent);
			// return iMantissa + "E" + iExponent;
			char[] cFloat = new char[cMantissa.length + 1 + cExponent.length];
			System.arraycopy(cMantissa, 0, cFloat, 0, cMantissa.length);
			cFloat[cMantissa.length] = 'E';
			System.arraycopy(cExponent, 0, cFloat, cMantissa.length+1, cExponent.length);
			return cFloat;
		}
	}
	
	public double decodeDouble() throws IOException {
		long lMantissa = decodeLong();
		long lExponent = decodeLong();

		if (lExponent == Constants.FLOAT_SPECIAL_VALUES) {
			if (lMantissa == -1L) {
				return Float.NEGATIVE_INFINITY;
			} else if (lMantissa == 1) {
				return Float.POSITIVE_INFINITY;
			} else {
				return Float.NaN;
			}
		} else {
			return lMantissa * (double)(Math.pow(10, lExponent));
		}		
	}
	

	public char[] decodeDoubleAsString() throws IOException {
		long lMantissa = decodeLong();
		long lExponent = decodeLong();

		if (lExponent == Constants.FLOAT_SPECIAL_VALUES) {
			if (lMantissa == -1) {
				return Constants.FLOAT_MINUS_INFINITY_CHARARRAY;
			} else if (lMantissa == 1) {
				return Constants.FLOAT_INFINITY_CHARARRAY;
			} else {
				return Constants.FLOAT_NOT_A_NUMBER_CHARARRAY;
			}
		} else {
			char[] cMantissa = MethodsBag.itos(lMantissa);
			char[] cExponent = MethodsBag.itos(lExponent);
			// return iMantissa + "E" + iExponent;
			char[] cDouble = new char[cMantissa.length + 1 + cExponent.length];
			System.arraycopy(cMantissa, 0, cDouble, 0, cMantissa.length);
			cDouble[cMantissa.length] = 'E';
			System.arraycopy(cExponent, 0, cDouble, cMantissa.length+1, cExponent.length);
			return cDouble;
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

	public char[] decodeDateTimeAsString(DatetimeType type) throws IOException {
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

		
		//	TODO char array like behaviour
		
//		return sb.toString();
//		return new CharArrayString(sb.toString());
		return sb.toString().toCharArray();
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
