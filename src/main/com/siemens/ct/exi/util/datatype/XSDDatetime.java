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

package com.siemens.ct.exi.util.datatype;

import java.util.Calendar;

import com.siemens.ct.exi.exceptions.XMLParsingException;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.4.20081014
 */

public class XSDDatetime {

	public static final int NUMBER_BITS_MONTHDAY = 9;
	public static final int NUMBER_BITS_TIME = 17;
	public static final int NUMBER_BITS_TIMEZONE = 11;

	// Date-Time, Year Offset from 2000
	public static final int YEAR_OFFSET = 2000;

	public static final int TIMEZONE_OFFSET_IN_MINUTES = 840;

	public static final int MONTH_MULTIPLICATOR = 32;

	private DatetimeType type;

	private StringBuilder sbCal;

	public int iYear;
	public int iMonthDay;
	public int iTime;
	public int iTZMinutes;
	public int iFractionalSecs;

	private XSDDatetime() {
		sbCal = new StringBuilder();
	}

	public static XSDDatetime newInstance() {
		return new XSDDatetime();
	}

	public DatetimeType getDatetimeType() {
		return type;
	}

	public boolean parse(String cal, DatetimeType type) {
		this.type = type;

		// StringBuffer sbCal = new StringBuffer ( cal.trim ( ) );
		sbCal.setLength(0);
		sbCal.append(cal);

		try {
			switch (type) {
			case gYear: // gYear Year, [Time-Zone]
				iYear = parseYear(sbCal) - YEAR_OFFSET;
				iTZMinutes = parseTimezoneInMinutesOffset(sbCal);
				break;
			case gYearMonth: // gYearMonth Year, MonthDay, [TimeZone]
				iYear = parseYear(sbCal) - YEAR_OFFSET;
				checkCharacter(sbCal, '-'); // hyphen
				iMonthDay = parseMonth(sbCal) * MONTH_MULTIPLICATOR;
				iTZMinutes = parseTimezoneInMinutesOffset(sbCal);
				break;
			case date: // date Year, MonthDay, [TimeZone]
				iYear = parseYear(sbCal) - YEAR_OFFSET; // year
				checkCharacter(sbCal, '-'); // hyphen
				iMonthDay = parseMonthDay(sbCal);
				iTZMinutes = parseTimezoneInMinutesOffset(sbCal);
				break;
			case dateTime: // dateTime Year, MonthDay, Time, [FractionalSecs],
				// [TimeZone]
				iYear = parseYear(sbCal) - YEAR_OFFSET;
				checkCharacter(sbCal, '-'); // hyphen
				iMonthDay = parseMonthDay(sbCal);
				checkCharacter(sbCal, 'T'); // Time
				iTime = parseTime(sbCal);
				iFractionalSecs = parseFractionalSecondsReverse(sbCal);
				iTZMinutes = parseTimezoneInMinutesOffset(sbCal);
				break;
			case gMonth: // gMonth MonthDay, [TimeZone]
				checkCharacter(sbCal, '-'); // hyphen
				checkCharacter(sbCal, '-'); // hyphen
				iMonthDay = parseMonth(sbCal) * MONTH_MULTIPLICATOR;
				iTZMinutes = parseTimezoneInMinutesOffset(sbCal);
				break;
			case gMonthDay: // gMonthDay MonthDay, [TimeZone]
				checkCharacter(sbCal, '-'); // hyphen
				checkCharacter(sbCal, '-'); // hyphen
				iMonthDay = parseMonthDay(sbCal);
				iTZMinutes = parseTimezoneInMinutesOffset(sbCal);
				break;
			case gDay: // gDay MonthDay, [TimeZone]
				checkCharacter(sbCal, '-'); // hyphen
				checkCharacter(sbCal, '-'); // hyphen
				checkCharacter(sbCal, '-'); // hyphen
				iMonthDay = parseDay(sbCal);
				iTZMinutes = parseTimezoneInMinutesOffset(sbCal);
				break;
			case time: // time Time, [FractionalSecs], [TimeZone]
				iTime = parseTime(sbCal);
				iFractionalSecs = parseFractionalSecondsReverse(sbCal);
				iTZMinutes = parseTimezoneInMinutesOffset(sbCal);
				break;
			default:
				throw new UnsupportedOperationException();
			}
			return true;
		} catch (RuntimeException e) {
			return false;
		} catch (XMLParsingException e) {
			return false;
		}
	}

	public static void checkCharacter(StringBuilder sb, char c)
			throws XMLParsingException {
		if (sb.length() > 0 && sb.charAt(0) == c) {
			sb.delete(0, 1);
		} else {
			throw new XMLParsingException("Unexpected character while parsing");
		}
	}

	public static int parseYear(StringBuilder sb) {
		String sYear;
		int len;
		if (sb.charAt(0) == '-') {
			sYear = sb.substring(0, 5);
			len = 5;
		} else {
			sYear = sb.substring(0, 4);
			len = 4;
		}
		int year = Integer.parseInt(sYear);

		// adjust buffer
		sb.delete(0, len);

		return year;
	}

	public static int parseMonth(StringBuilder sb) {
		int month = Integer.parseInt(sb.substring(0, 2));

		// adjust buffer
		sb.delete(0, 2);

		return month;
	}

	public static int parseDay(StringBuilder sb) {
		String sDay = sb.substring(0, 2);
		int day = Integer.parseInt(sDay);

		// adjust buffer
		sb.delete(0, 2);

		return day;
	}

	public static int parseMonthDay(StringBuilder sb)
			throws XMLParsingException {
		int month = parseMonth(sb); // month
		checkCharacter(sb, '-'); // hyphen
		int day = parseDay(sb); // day

		return month * MONTH_MULTIPLICATOR + day;
	}

	// Time ((Hour * 60) + Minutes) * 60 + seconds
	public static int parseTime(StringBuilder sb) throws XMLParsingException {
		// hour
		int hour = Integer.parseInt(sb.substring(0, 2));
		sb.delete(0, 2);

		checkCharacter(sb, ':'); // colon

		// minute
		int minutes = Integer.parseInt(sb.substring(0, 2));
		sb.delete(0, 2);

		checkCharacter(sb, ':'); // colon

		// second
		int seconds = Integer.parseInt(sb.substring(0, 2));
		sb.delete(0, 2);

		return ((hour * 60) + minutes) * 60 + seconds;
	}

	// lexical representation of a timezone: (('+' | '-') hh ':' mm) | 'Z',
	// where
	// * hh is a two-digit numeral (with leading zeros as required) that
	// represents the hours,
	// * mm is a two-digit numeral that represents the minutes,
	// * '+' indicates a nonnegative duration,
	// * '-' indicates a nonpositive duration.
	//
	// TimeZone TZHours * 60 + TZSeconds (offset by 840)
	public static int parseTimezoneInMinutesOffset(StringBuilder sb)
			throws XMLParsingException {

		// plus, minus, Z or nothing ?
		int multiplicator;
		if (sb.length() == 0) {
			return 0;
		} else if (sb.charAt(0) == 'Z') {
			sb.delete(0, 1);
			return 0;
		} else if (sb.charAt(0) == '+') {
			multiplicator = 1;
		} else if (sb.charAt(0) == '-') {
			multiplicator = -1;
		} else {
			throw new XMLParsingException("Unexpected character while parsing");
		}

		// hours
		int hours = Integer.parseInt(sb.substring(1, 3));

		// colon
		assert (sb.charAt(3) == ':');

		// minutes
		int minutes = Integer.parseInt(sb.substring(4, 6));

		return (multiplicator) * (hours * 60 + minutes)
				+ TIMEZONE_OFFSET_IN_MINUTES;
	}

	public static int parseFractionalSecondsReverse(StringBuilder sb)
			throws StringIndexOutOfBoundsException {
		if (sb.length() > 0 && sb.charAt(0) == '.') {
			sb.deleteCharAt(0); // can't remove it immediately, because
			// fracSec is option (could be timezone)
			int digits = countDigits(sb);
			int revFracSecs = Integer.parseInt(new StringBuilder(sb.substring(
					0, digits)).reverse().toString());

			// adjust buffer
			sb.delete(0, digits);

			return revFracSecs;
		}

		return 0;
	}

	private static int countDigits(StringBuilder sb) {
		int length = sb.length();
		int index = 0;

		while (index < length && isDigit(sb.charAt(index))) {
			index++;
		}

		return index;
	}

	private static boolean isDigit(char c) {
		boolean isDigit = false;

		char[] digits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };

		int i = 0;
		while (!isDigit && i < digits.length) {
			if (c == digits[i]) {
				isDigit = true;
			}
			i++;
		}

		return isDigit;
	}

	/*
	 * 
	 * 
	 * 
	 * 
	 */

	/**
	 * Returns monthDay representation defined in the EXI format (Month * 32 +
	 * Day)
	 */
	public static int getMonthDay(Calendar cal) {
		int month = cal.get(Calendar.MONTH) + 1;
		int day = cal.get(Calendar.DAY_OF_MONTH);
		int monthDay = month * MONTH_MULTIPLICATOR + day;

		return monthDay;
	}

	/**
	 * Sets month and day of the given calendar making use of of the monthDay
	 * representation defined in EXI format
	 */
	public static void setMonthDay(int monthDay, Calendar cal) {
		// monthDay = month * 32 + day;
		int month = monthDay / MONTH_MULTIPLICATOR;
		cal.set(Calendar.MONTH, month - 1);
		int day = monthDay - month * MONTH_MULTIPLICATOR;
		cal.set(Calendar.DAY_OF_MONTH, day);
	}

	/**
	 * Returns time representation defined in the EXI format ((Hour * 64) +
	 * Minutes) * 64 + seconds
	 */
	public static int getTime(Calendar cal) {
		int time = cal.get(Calendar.HOUR_OF_DAY);
		time *= 64;
		time += cal.get(Calendar.MINUTE);
		time *= 64;
		time += cal.get(Calendar.SECOND);
		return time;
	}

	/**
	 * Sets hour, minute and second of the given calendar making use of of the
	 * time representation defined in EXI format
	 */
	public static void setTime(int time, Calendar cal) {
		// ((Hour * 64) + Minutes) * 64 + seconds
		int hour = time / (64 * 64);
		time -= hour * (64 * 64);
		int minute = time / 64;
		time -= minute * 64; // second
		cal.set(Calendar.HOUR_OF_DAY, hour);
		cal.set(Calendar.MINUTE, minute);
		cal.set(Calendar.SECOND, time);
	}

	/**
	 * Returns time-zone in minutes offset by 840 ( = 14 * 60 )
	 */
	public static int getTimeZoneInMinutesOffset(Calendar cal) {
		return cal.getTimeZone().getRawOffset() / (1000 * 60)
				+ TIMEZONE_OFFSET_IN_MINUTES;
	}

	/**
	 * Returns time-zone offset in millisecs according to the given minutes
	 */
	public static int getTimeZoneInMillisecs(int minutes) {
		return minutes * (1000 * 60); // minutes to millisec
	}

	public static int getFractionalSecondsReverse(int millisec) {
		int revFracSecs = 0;
		if (millisec == 0) {
			// ok -> 0
		} else if (millisec < 10) {
			revFracSecs = Integer.parseInt(new StringBuilder("00" + millisec)
					.reverse().toString());
		} else if (millisec < 100) {
			revFracSecs = Integer.parseInt(new StringBuilder("0" + millisec)
					.reverse().toString());
		} else {
			revFracSecs = Integer.parseInt(new StringBuilder(millisec)
					.reverse().toString());
		}

		return revFracSecs;
	}

//	public static void appendYear(StringBuilder sb, int year) {
//		if (year < 0) {
//			// leading minus
//			sb.append('-');
//			year = (-1) * year;
//		}
//		if (year > 999) {
//			sb.append(year);
//		} else if (year > 99) {
//			sb.append('0');
//			sb.append(year);
//		} else if (year > 9) {
//			sb.append("00");
//			sb.append(year);
//		} else {
//			sb.append("000");
//			sb.append(year);
//		}
//	}

//	public static void appendMonth(StringBuilder sb, int monthDay) {
//		int month = monthDay / MONTH_MULTIPLICATOR;
//		assert ((monthDay - month * MONTH_MULTIPLICATOR) == 0);
//
//		appendHyphen2Digits(sb, month);
//	}
//
//	public static void appendDay(StringBuilder sb, int day) {
//		assert (day < 31); // day range 0-30
//
//		appendHyphen2Digits(sb, day);
//	}

//	private static void appendHyphen2Digits(StringBuilder sb, int n) {
//		if (n > 9) {
//			sb.append("-" + n);
//		} else {
//			sb.append("-0" + n);
//		}
//	}

//	public static void appendMonthDay(StringBuilder sb, int monthDay) {
//		// monthDay: Month * 32 + Day
//
//		// month
//		int month = monthDay / MONTH_MULTIPLICATOR;
//		appendHyphen2Digits(sb, month);
//		// day
//		appendHyphen2Digits(sb, monthDay - (month * MONTH_MULTIPLICATOR));
//	}

//	public static void appendTime(StringBuilder sb, int time) {
//		// time = ( ( hour * 60) + minutes ) * 60 + seconds ;
//		final int secHour = 60 * 60;
//		final int secMinute = 60;
//
//		int hour = time / secHour;
//		time -= hour * secHour;
//		int minutes = time / secMinute;
//		int seconds = time - minutes * secMinute;
//
//		sb.append(hour < 10 ? "0" + hour : hour);
//		sb.append(':');
//		sb.append(minutes < 10 ? "0" + minutes : minutes);
//		sb.append(':');
//		sb.append(seconds < 10 ? "0" + seconds : seconds);
//	}

//	public static void appendFractionalSeconds(StringBuilder sb, int fracSecs) {
//		if (fracSecs > 0) {
//			// append after reversing fracSecs
//			sb.append('.');
//			sb.append(new StringBuilder(fracSecs + "").reverse());
//		}
//	}

//	public static void appendTimezone(StringBuilder sb, int tz) {
//
//		if (tz == 0) {
//			sb.append('Z');
//		} else {
//			// +/-
//			if (tz < 0) {
//				sb.append('-');
//				tz *= -1;
//			} else {
//				sb.append('+');
//			}
//			// hours
//			int hours = tz / 60;
//			if (hours > 9) {
//				sb.append(hours);
//			} else {
//				sb.append('0');
//				sb.append(hours);
//			}
//			sb.append(':');
//			int minutes = tz - (hours * 60);
//			if (minutes > 9) {
//				sb.append(minutes);
//			} else {
//				sb.append('0');
//				sb.append(minutes);
//			}
//		}
//
//	}

}
