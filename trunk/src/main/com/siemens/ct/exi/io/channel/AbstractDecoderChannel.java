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
import java.math.BigInteger;
import java.util.Calendar;
import java.util.TimeZone;

import com.siemens.ct.exi.Constants;
import com.siemens.ct.exi.util.HugeInteger;
import com.siemens.ct.exi.util.MethodsBag;
import com.siemens.ct.exi.util.datatype.DatetimeType;
import com.siemens.ct.exi.util.datatype.XSDBase64;
import com.siemens.ct.exi.util.datatype.XSDDatetime;
import com.siemens.ct.exi.values.DecimalValue;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.3.20081117
 */

public abstract class AbstractDecoderChannel implements DecoderChannel {

	public AbstractDecoderChannel() {
	}

	public char[] decodeBinaryAsCharacters() throws IOException {
		final byte[] b = decodeBinary();
		return XSDBase64.encode(b);
	}


	public char[] decodeBooleanAsCharacters() throws IOException {
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

	
	public char[] decodeIntegerAsCharacters() throws IOException {
		return MethodsBag.itos(decodeInteger());
	}
	
	public char[] decodeLongAsCharacters() throws IOException {
		return MethodsBag.itos(decodeLong());
	}
	
	public char[] decodeBigIntegerAsCharacters() throws IOException {
		if (decodeBoolean()) {
			// For negative values, the Unsigned Integer holds the
			// magnitude of the value minus 1
			HugeInteger bi = decodeUnsignedHugeInteger();
			if (bi.isLongValue) {
				 return MethodsBag.itos(-(bi.longValue + 1L));
			} else {
				// TODO look for a more memory sensitive way !?
				return bi.bigIntegerValue.add(BigInteger.ONE).negate().toString().toCharArray();
			}
		} else {
			// positive
			HugeInteger bi = decodeUnsignedHugeInteger();
			if (bi.isLongValue) {
				return MethodsBag.itos(bi.longValue);
			} else {
				// TODO look for a more memory sensitive way !?
				return bi.bigIntegerValue.toString().toCharArray();
			}
		}
	}
	
	protected HugeInteger decodeUnsignedHugeInteger() throws IOException {
		long lResult = 0L;
		int mShift = 0;
		int b;
		
		//	long == 64 bits
		//	9 x 7 bits --> 63
		int cntBytes = 0;
		boolean isLongValue = true;

		do {
			if (cntBytes >= 9) {
				isLongValue = false;
				break;
			}
			b = decode();
			cntBytes++;
			lResult += ((long)(b & 127)) << mShift;
			mShift += 7;
		} while ((b >>> 7) == 1);
		
		if (isLongValue) {
			return new HugeInteger(lResult);
		} else {
			//	keep on decoding
			BigInteger bResult = BigInteger.valueOf(lResult);
			BigInteger multiplier = BigInteger.ONE;
			multiplier = multiplier.shiftLeft(7 * cntBytes);
			
			do {
				b = decode();
				bResult = bResult.add(multiplier.multiply(BigInteger
						.valueOf(b & 127)));
				multiplier = multiplier.shiftLeft(7);
			} while ((b >>> 7) == 1);
			
			return new HugeInteger(bResult);
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

	public char[] decodeUnsignedIntegerAsCharacters() throws IOException {
		return MethodsBag.itos(decodeUnsignedInteger());
	}
	
	public char[] decodeUnsignedLongAsCharacters() throws IOException {
		return MethodsBag.itos(decodeUnsignedLong());
	}
	
	public char[] decodeUnsignedBigIntegerAsCharacters() throws IOException {
		return decodeUnsignedHugeInteger().toCharacters();
	}

	/**
	 * Decodes and returns an n-bit unsigned integer as string.
	 */
	public char[] decodeNBitUnsignedIntegerAsCharacters(int n) throws IOException {
		return MethodsBag.itos(n);
	}

	/**
	 * Decode a decimal represented as a Boolean sign followed by two Unsigned
	 * Integers. A sign value of zero (0) is used to represent positive Decimal
	 * values and a sign value of one (1) is used to represent negative Decimal
	 * values The first Integer represents the integral portion of the Decimal
	 * value. The second positive integer represents the fractional portion of
	 * the decimal with the digits in reverse order to preserve leading zeros.
	 */
	public DecimalValue decodeDecimal() throws IOException {
		boolean negative = decodeBoolean();

		HugeInteger integral = decodeUnsignedHugeInteger();
		HugeInteger revFractional = decodeUnsignedHugeInteger();
		
		return new DecimalValue(negative, integral, revFractional);
	}
	
//	public BigDecimal decodeDecimal() throws IOException {
//		// return new BigDecimal(decodeDecimalAsString());
//		return new BigDecimal(decodeDecimalAsCharacters());
//	}
//
//	public char[] decodeDecimalAsCharacters() throws IOException {
//		boolean negative = decodeBoolean();
//
//		char[] integral = decodeUnsignedBigIntegerAsCharacters();
//		HugeInteger hiFractional = decodeUnsignedHugeInteger();
//		char[] fractional = hiFractional.isLongValue ? MethodsBag.itosReverse(hiFractional.longValue) :
//			MethodsBag.itosReverse(hiFractional.bigIntegerValue);
//		
//		int aLen = (negative? 1 : 0) + integral.length + 1 + fractional.length;
//		
//		char[] caDecimal = new char[aLen];
//		
//		int cnt = 0;
//		
//		//	negative
//		if (negative) {
//			caDecimal[cnt++] = '-';
//		}
//		//	integral
//		System.arraycopy(integral, 0, caDecimal, cnt, integral.length);
//		cnt += integral.length;
//		//	dot
//		caDecimal[cnt++] = '.';
//		//	fractional
//		System.arraycopy(fractional, 0, caDecimal, cnt, fractional.length);
//		
//		return caDecimal;
//	}

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
	
	public char[] decodeFloatAsCharacters() throws IOException {
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
			// return iMantissa + "E" + iExponent;
			int sizeMantissa = MethodsBag.getStringSize(iMantissa);
			int stringSize = sizeMantissa + 1 + MethodsBag.getStringSize(iExponent);
			
			char[] cFloat = new char[stringSize];
			
			MethodsBag.itos(iExponent, stringSize, cFloat);
			cFloat[sizeMantissa] = 'E';
			MethodsBag.itos(iMantissa, sizeMantissa, cFloat);

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
	

	public char[] decodeDoubleAsCharacters() throws IOException {
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
			// return iMantissa + "E" + iExponent;
			int sizeMantissa = MethodsBag.getStringSize(lMantissa);
			int stringSize = sizeMantissa + 1 + MethodsBag.getStringSize(lExponent);
			
			char[] cDouble = new char[stringSize];
			
			MethodsBag.itos(lExponent, stringSize, cDouble);
			cDouble[sizeMantissa] = 'E';
			MethodsBag.itos(lMantissa, sizeMantissa, cDouble);
			
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
	
	private static void appendTimezone(char[] ca, int index, int tz) {
		if (tz == 0) {
			// per default no 'Z'
		} else {
			// +/-
			if (tz < 0) {
				ca[index++] = '-';
				tz *= -1;
			} else {
				ca[index++] = '+';
			}
			// hours
			int hours = tz / 60;
			index += appendTwoDigits(ca, index, hours);
			//	:
			ca[index++] = ':';
			//	minutes
			int minutes = tz - (hours * 60);
			appendTwoDigits(ca, index, minutes);
		}
	}
	
	private static int appendFractionalSeconds(char[] ca, int index, int fracSecs, int sLen) {
		if (fracSecs > 0) {
			//	".123"
			ca[index++] = '.';
			// reverse fracSecs
			int chars = MethodsBag.itosReverse(fracSecs, index, ca);
			return chars+1;
			
//			char[] fracSecA = MethodsBag.itos(fracSecs);
//			for(int i= fracSecA.length-1; i>=0; i--) {
//				ca[index++] = fracSecA[i];
//			}
//			return (1+fracSecA.length);
		} else {
			return 0;
		}
	}
	
	private static int appendTwoDigits(char[] ca, int index, int i) {
		if (i > 9) {
			MethodsBag.itos(i, index+2, ca);
			// index++;
		} else {
			ca[index++] = '0';
			MethodsBag.itos(i, index+1, ca);
		}
		return 2;
	}
	
	private static int appendYear(char[] ca, int index, int year) {
		int sLen = 4;
		
		if (year < 0) {
			ca[index] = '-';
			index++;
			year = -year;
			sLen++;
		}
		
		if (year > 999) {
			MethodsBag.itos(year, index+4, ca);
		} else if (year > 99) {
			ca[index++] = '0';
			MethodsBag.itos(year, index+3, ca);
		} else if ( year > 9) {
			ca[index++] = '0';
			ca[index++] = '0';
			MethodsBag.itos(year, index+2, ca);
		} else {
			ca[index++] = '0';
			ca[index++] = '0';
			ca[index++] = '0';
			MethodsBag.itos(year, index+1, ca);
		}
		
		return sLen;
	}
	
	private static int appendMonth(char[] ca, int index, int monthDay) {
		int month = monthDay / XSDDatetime.MONTH_MULTIPLICATOR;
		assert ((monthDay - month * XSDDatetime.MONTH_MULTIPLICATOR) == 0);

		// -MM
		ca[index++] = '-';
		return appendTwoDigits(ca, index, month) + 1;
	}
	
	private static int appendMonthDay(char[] ca, int index, int monthDay) {
		// monthDay: Month * 32 + Day
		
		// month & day
		int month = monthDay /  XSDDatetime.MONTH_MULTIPLICATOR;
		int day = monthDay - (month * XSDDatetime.MONTH_MULTIPLICATOR);
		
		//-MM-DD
		ca[index++] = '-';
		index += appendTwoDigits(ca, index, month);
		ca[index++] = '-';
		appendTwoDigits(ca, index, day);
		
		return 6;
	}
	
	private static int appendDay(char[] ca, int index, int day) {
		assert (day < 31); // day range 0-30
		appendTwoDigits(ca, index, day);
		return 2;
	}
	
	private static int appendTime(char[] ca, int index, int time) {
		// time = ( ( hour * 60) + minutes ) * 60 + seconds ;
		final int secHour = 60 * 60;
		final int secMinute = 60;

		int hour = time / secHour;
		time -= hour * secHour;
		int minutes = time / secMinute;
		int seconds = time - minutes * secMinute;

		//  hh ':' mm ':' ss
		index += appendTwoDigits(ca, index, hour);
		ca[index++] = ':';
		index += appendTwoDigits(ca, index, minutes);
		ca[index++] = ':';
		index += appendTwoDigits(ca, index, seconds);
		
		return 8;
	}
	

	public char[] decodeDateTimeAsCharacters(DatetimeType type) throws IOException {
		
		int year, timeZone, time, fractionalSecs;
		int index = 0;
		char [] ca;

		switch (type) {
		case gYear: // Year, [Time-Zone]
			year = decodeInteger() + XSDDatetime.YEAR_OFFSET;
			timeZone = decodeDateTimeTimezone();
			
			ca = new char[(year < 0 ? 5 : 4) + (timeZone == 0 ? 0 : 6)];
			
			index += appendYear(ca, index, year);
			appendTimezone(ca, index, timeZone);
			break;
		case gYearMonth: // Year, MonthDay, [TimeZone]
			year = decodeInteger() + XSDDatetime.YEAR_OFFSET;
			int monthDay = decodeNBitUnsignedInteger(XSDDatetime.NUMBER_BITS_MONTHDAY);
			timeZone = decodeDateTimeTimezone();
			
			ca = new char[(year < 0 ? 5 : 4) + 3 + (timeZone == 0 ? 0 : 6)];
			
			index += appendYear(ca, index, year);
			index += appendMonth(ca, index, monthDay);
			appendTimezone(ca, index, timeZone);
			break;
		case date: // Year, MonthDay, [TimeZone]
			year = decodeInteger() + XSDDatetime.YEAR_OFFSET;
			monthDay = decodeNBitUnsignedInteger(XSDDatetime.NUMBER_BITS_MONTHDAY);
			timeZone = decodeDateTimeTimezone();

			ca = new char[(year < 0 ? 5 : 4) + 6 + (timeZone == 0 ? 0 : 6)];
			
			index += appendYear(ca, index, year);
			index += appendMonthDay(ca, index, monthDay);
			appendTimezone(ca, index, timeZone);
			break;
		case dateTime: // Year, MonthDay, Time, [FractionalSecs], [TimeZone]
			// e.g. "0001-01-01T00:00:00.111+00:33";
			year = decodeInteger() + XSDDatetime.YEAR_OFFSET;
			monthDay = decodeNBitUnsignedInteger(XSDDatetime.NUMBER_BITS_MONTHDAY);
			time = decodeNBitUnsignedInteger(XSDDatetime.NUMBER_BITS_TIME);
			fractionalSecs = decodeDateTimeFractionalSecs();
			timeZone = decodeDateTimeTimezone();
			
			int sizeFractionalSecs = fractionalSecs == 0 ? 0 : MethodsBag.getStringSize(fractionalSecs) + 1;
			ca = new char[(year < 0 ? 5 : 4) + 6 + 9 + (sizeFractionalSecs) +(timeZone == 0 ? 0 : 6)];
			
			index += appendYear(ca, index, year);
			index += appendMonthDay(ca, index, monthDay);
			ca[index++] = 'T';
			index += appendTime(ca, index, time);
			index += appendFractionalSeconds(ca, index, fractionalSecs, sizeFractionalSecs-1);
			appendTimezone(ca, index, timeZone);
			break;
		case gMonth: // MonthDay, [TimeZone]
			// e.g.  "--12"
			monthDay = decodeNBitUnsignedInteger(XSDDatetime.NUMBER_BITS_MONTHDAY);
			timeZone = decodeDateTimeTimezone();
			
			ca = new char[1 + 3 + (timeZone == 0 ? 0 : 6)];
			
			ca[index++] = '-';
			index += appendMonth(ca, index, monthDay);
			appendTimezone(ca, index, timeZone);
			break;
		case gMonthDay: // MonthDay, [TimeZone]
			// e.g.  "--01-28"
			monthDay = decodeNBitUnsignedInteger(XSDDatetime.NUMBER_BITS_MONTHDAY);
			timeZone = decodeDateTimeTimezone();
			
			ca = new char[1 + 6 + (timeZone == 0 ? 0 : 6)];
			
			ca[index++] = '-';
			index += appendMonthDay(ca, index, monthDay);
			appendTimezone(ca, index, timeZone);
			break;
		case gDay: // MonthDay, [TimeZone]
			// "---16";
			monthDay = decodeNBitUnsignedInteger(XSDDatetime.NUMBER_BITS_MONTHDAY);
			timeZone = decodeDateTimeTimezone();
			
			ca = new char[3 + 2 + (timeZone == 0 ? 0 : 6)];
			
			ca[index++] = '-';
			ca[index++] = '-';
			ca[index++] = '-';
			index += appendDay(ca, index, monthDay);
			appendTimezone(ca, index, timeZone);
			break;
		case time: // Time, [FractionalSecs], [TimeZone]
			// e.g. "12:34:56.135"
			time = decodeNBitUnsignedInteger(XSDDatetime.NUMBER_BITS_TIME);
			fractionalSecs = decodeDateTimeFractionalSecs();
			timeZone = decodeDateTimeTimezone();
			
			sizeFractionalSecs = fractionalSecs == 0 ? 0 : MethodsBag.getStringSize(fractionalSecs) + 1;
			ca = new char[ 8 + (sizeFractionalSecs) +(timeZone == 0 ? 0 : 6)];
			
			index += appendTime(ca, index, time);
			index += appendFractionalSeconds(ca, index, fractionalSecs, sizeFractionalSecs-1);
			appendTimezone(ca, index, timeZone);
			break;
		default:
			throw new UnsupportedOperationException();
		}
		
		return ca;
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
	
	private int decodeDateTimeTimezone() throws IOException {
		return decodeBoolean() ? decodeNBitUnsignedInteger(XSDDatetime.NUMBER_BITS_TIMEZONE)
					- XSDDatetime.TIMEZONE_OFFSET_IN_MINUTES : 0;
	}

	private void decodeDateTimeFractionalSecs(Calendar cal) throws IOException {
		cal.set(Calendar.MILLISECOND, decodeBoolean() ? decodeUnsignedInteger()
				: 0);
	}
	
	private int decodeDateTimeFractionalSecs() throws IOException {
		return decodeBoolean() ? decodeUnsignedInteger() : 0;
	}

}
