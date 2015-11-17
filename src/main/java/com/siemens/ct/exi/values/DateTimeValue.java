/*
 * Copyright (c) 2007-2015 Siemens AG
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * 
 */

package com.siemens.ct.exi.values;

import java.io.IOException;
import java.util.Calendar;
import java.util.TimeZone;

import com.siemens.ct.exi.exceptions.XMLParsingException;
import com.siemens.ct.exi.types.DateTimeType;
import com.siemens.ct.exi.util.MethodsBag;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9.6-SNAPSHOT
 */

public class DateTimeValue extends AbstractValue {

	private static final long serialVersionUID = 298943383646546462L;

	public static final int NUMBER_BITS_MONTHDAY = 9;
	public static final int NUMBER_BITS_TIME = 17;
	public static final int NUMBER_BITS_TIMEZONE = 11;

	// Date-Time, Year Offset from 2000
	public static final int YEAR_OFFSET = 2000;

	public static final int TIMEZONE_OFFSET_IN_MINUTES = 896; // ( = 14 * 64)

	// EXI seconds in representation (different to default!)
	static final int SECONDS_IN_MINUTE = 64;
	static final int SECONDS_IN_HOUR = 64 * 64;

	public static final int MONTH_MULTIPLICATOR = 32;

	public final DateTimeType type;
	public final int year;
	public final int monthDay;
	public final int time;
	public final boolean presenceFractionalSecs;
	public final int fractionalSecs;
	public final boolean presenceTimezone;
	public final int timezone;
	public final boolean normalized;

	protected Calendar cal;

	protected DateTimeValue normalizedDateTimeValue;

	int sizeFractionalSecs = -1;

	public DateTimeValue(DateTimeType type, int year, int monthDay, int time,
			boolean presenceFractionalSecs, int fractionalSecs,
			boolean presenceTimezone, int timezone) {
		this(type, year, monthDay, time, presenceFractionalSecs,
				fractionalSecs, presenceTimezone, timezone, false);
	}

	private DateTimeValue(DateTimeType type, int year, int monthDay, int time,
			boolean presenceFractionalSecs, int fractionalSecs,
			boolean presenceTimezone, int timezone, boolean normalized) {
		super(ValueType.DATETIME);
		this.type = type;
		this.year = year;
		this.monthDay = monthDay;
		this.time = time;
		this.presenceFractionalSecs = presenceFractionalSecs;
		this.fractionalSecs = fractionalSecs;
		this.presenceTimezone = presenceTimezone;
		this.timezone = timezone;
		this.normalized = normalized;
	}

	public static DateTimeValue parse(String cal, DateTimeType type) {

		cal = cal.trim();

		int sYear = 0;
		int sMonthDay = 0;
		int sTime = 0;
		boolean sPresenceFractionalSecs = false;
		int sFractionalSecs = 0;
		boolean sPresenceTimezone;
		int sTimezone;

		StringBuilder sbCal = new StringBuilder(cal);

		try {
			switch (type) {
			case gYear: // gYear Year, [Time-Zone]
				sYear = parseYear(sbCal);
				break;
			case gYearMonth: // gYearMonth Year, MonthDay, [TimeZone]
				sYear = parseYear(sbCal);
				checkCharacter(sbCal, '-'); // hyphen
				sMonthDay = parseMonth(sbCal) * MONTH_MULTIPLICATOR;
				break;
			case date: // date Year, MonthDay, [TimeZone]
				sYear = parseYear(sbCal); // year
				checkCharacter(sbCal, '-'); // hyphen
				sMonthDay = parseMonthDay(sbCal);
				break;
			case dateTime: // dateTime Year, MonthDay, Time, [FractionalSecs],
				// [TimeZone]
				sYear = parseYear(sbCal);
				checkCharacter(sbCal, '-'); // hyphen
				sMonthDay = parseMonthDay(sbCal);
				checkCharacter(sbCal, 'T'); // Time
				// Note: *no* break;
			case time: // time Time, [FractionalSecs], [TimeZone]
				sTime = parseTime(sbCal);
				if (sbCal.length() > 0 && sbCal.charAt(0) == '.') {
					sbCal.deleteCharAt(0); // can't remove it immediately,
											// because
					// fracSec is option (could be timezone)
					int digits = countDigits(sbCal);
					sFractionalSecs = Integer.parseInt(new StringBuilder(sbCal
							.substring(0, digits)).reverse().toString());
					sPresenceFractionalSecs = true;
					// adjust buffer
					sbCal.delete(0, digits);
				}
				break;
			case gMonth: // gMonth MonthDay, [TimeZone]
				checkCharacter(sbCal, '-'); // hyphen
				checkCharacter(sbCal, '-'); // hyphen
				sMonthDay = parseMonth(sbCal) * MONTH_MULTIPLICATOR;
				if (sbCal.length() > 1 && sbCal.charAt(0) == sbCal.charAt(1)
						&& sbCal.charAt(0) == '-') {
					checkCharacter(sbCal, '-'); // hyphen
					checkCharacter(sbCal, '-'); // hyphen
				}
				break;
			case gMonthDay: // gMonthDay MonthDay, [TimeZone]
				checkCharacter(sbCal, '-'); // hyphen
				checkCharacter(sbCal, '-'); // hyphen
				sMonthDay = parseMonthDay(sbCal);
				break;
			case gDay: // gDay MonthDay, [TimeZone]
				checkCharacter(sbCal, '-'); // hyphen
				checkCharacter(sbCal, '-'); // hyphen
				checkCharacter(sbCal, '-'); // hyphen
				sMonthDay = parseDay(sbCal);
				break;
			default:
				throw new UnsupportedOperationException();
			}
			// [TimeZone]
			// lexical representation of a timezone: (('+' | '-') hh ':' mm) |
			// 'Z',
			// where
			// * hh is a two-digit numeral (with leading zeros as required) that
			// represents the hours,
			// * mm is a two-digit numeral that represents the minutes,
			// * '+' indicates a nonnegative duration,
			// * '-' indicates a nonpositive duration.
			//
			// TimeZone TZHours * 64 + TZMinutes (896 = 14 * 64)

			// plus, minus, Z or nothing ?
			if (sbCal.length() == 0) {
				sPresenceTimezone = false;
				sTimezone = 0;
			} else if (sbCal.length() == 1 && sbCal.charAt(0) == 'Z') {
				sbCal.delete(0, 1);
				sPresenceTimezone = true;
				// sTimezone = TIMEZONE_OFFSET_IN_MINUTES;
				sTimezone = 0;
			} else {
				sPresenceTimezone = true;
				int multiplicator;
				if (sbCal.charAt(0) == '+') {
					multiplicator = 1;
				} else if (sbCal.charAt(0) == '-') {
					multiplicator = -1;
				} else {
					throw new XMLParsingException(
							"Unexpected character while parsing");
				}

				// hours
				int hours = Integer.parseInt(sbCal.substring(1, 3));
				// colon
				assert (sbCal.charAt(3) == ':');
				// minutes
				int minutes = Integer.parseInt(sbCal.substring(4, 6));

				// sTimezone = (multiplicator) * (hours * 64 + minutes) +
				// TIMEZONE_OFFSET_IN_MINUTES;
				sTimezone = (multiplicator) * (hours * 64 + minutes);
			}

			return new DateTimeValue(type, sYear, sMonthDay, sTime,
					sPresenceFractionalSecs, sFractionalSecs,
					sPresenceTimezone, sTimezone);
		} catch (RuntimeException e) {
			return null;
		} catch (XMLParsingException e) {
			return null;
		}
	}

	protected static int parseYear(StringBuilder sb) {
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

	protected static void checkCharacter(StringBuilder sb, char c)
			throws XMLParsingException {
		if (sb.length() > 0 && sb.charAt(0) == c) {
			sb.delete(0, 1);
		} else {
			throw new XMLParsingException("Unexpected character while parsing");
		}
	}

	protected static int parseMonth(StringBuilder sb) {
		int month = Integer.parseInt(sb.substring(0, 2));

		// adjust buffer
		sb.delete(0, 2);

		return month;
	}

	protected static int parseDay(StringBuilder sb) {
		String sDay = sb.substring(0, 2);
		int day = Integer.parseInt(sDay);

		// adjust buffer
		sb.delete(0, 2);

		return day;
	}

	protected static int parseMonthDay(StringBuilder sb)
			throws XMLParsingException {
		int month = parseMonth(sb); // month
		checkCharacter(sb, '-'); // hyphen
		int day = parseDay(sb); // day

		return month * MONTH_MULTIPLICATOR + day;
	}

	// Time ((Hour * 64) + Minutes) * 64 + seconds
	protected static int parseTime(StringBuilder sb) throws XMLParsingException {
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

		return ((hour * 64) + minutes) * 64 + seconds;
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

	/**
	 * Encode Date-Time as a sequence of values representing the individual
	 * components of the Date-Time.
	 * 
	 * @param cal calendar
	 * @param type date-time type
	 * @return date-time value
	 * 
	 * @throws IOException IO exception
	 */
	public static DateTimeValue parse(Calendar cal, DateTimeType type)
			throws IOException {

		int sYear = 0;
		int sMonthDay = 0;
		int sTime = 0;
		boolean sPresenceFractionalSecs = false;
		int sFractionalSecs = 0;
		boolean sPresenceTimezone = false;
		int sTimezone;

		switch (type) {
		case gYear: // gYear Year, [Time-Zone]
		case gYearMonth: // gYearMonth Year, MonthDay, [TimeZone]
		case date: // date Year, MonthDay, [TimeZone]
			sYear = cal.get(Calendar.YEAR);
			sMonthDay = getMonthDay(cal);
			break;
		case dateTime: // dateTime Year, MonthDay, Time, [FractionalSecs],
			// [TimeZone]
			sYear = cal.get(Calendar.YEAR);
			sMonthDay = getMonthDay(cal);
			// Note: *no* break;
		case time: // time Time, [FractionalSecs], [TimeZone]
			sTime = getTime(cal);
			sFractionalSecs = cal.get(Calendar.MILLISECOND);
			if (sFractionalSecs != 0) {
				sPresenceFractionalSecs = true;
			}
			break;
		case gMonth: // gMonth MonthDay, [TimeZone]
		case gMonthDay: // gMonthDay MonthDay, [TimeZone]
		case gDay: // gDay MonthDay, [TimeZone]
			sMonthDay = getMonthDay(cal);
			break;
		default:
			throw new UnsupportedOperationException();
		}
		// [TimeZone]
		sTimezone = getTimeZoneInMinutesOffset(cal);
		if (sTimezone != 0) {
			sPresenceTimezone = true;
		}

		return new DateTimeValue(type, sYear, sMonthDay, sTime,
				sPresenceFractionalSecs, sFractionalSecs, sPresenceTimezone,
				sTimezone);
	}

	/**
	 * Returns monthDay representation defined in the EXI format (Month * 32 +
	 * Day)
	 * 
	 * @param cal calendar 
	 * @return monthDay
	 */
	public static int getMonthDay(Calendar cal) {
		int month = cal.get(Calendar.MONTH) + 1;
		int day = cal.get(Calendar.DAY_OF_MONTH);
		int monthDay = month * MONTH_MULTIPLICATOR + day;

		return monthDay;
	}

	/**
	 * Returns time representation defined in the EXI format ((Hour * 64) +
	 * Minutes) * 64 + seconds
	 * 
	 * @param cal calendar 
	 * @return time representation
	 */
	public static int getTime(Calendar cal) {
		int time = cal.get(Calendar.HOUR_OF_DAY);
		time *= 64;
		time += cal.get(Calendar.MINUTE);
		time *= 64;
		time += cal.get(Calendar.SECOND);
		return time;
	}

	public Calendar toCalendar() {
		if (cal == null) {
			Calendar cal = Calendar.getInstance();
			cal.clear();

			switch (type) {
			case gYear: // gYear Year, [Time-Zone]
				cal.set(Calendar.YEAR, year);
				break;
			case gYearMonth: // gYearMonth Year, MonthDay, [TimeZone]
			case date: // date Year, MonthDay, [TimeZone]
				cal.set(Calendar.YEAR, year);
				setMonthDay(monthDay, cal);
				break;
			case dateTime: // dateTime Year, MonthDay, Time, [FractionalSecs],
				// [TimeZone]
				cal.set(Calendar.YEAR, year);
				setMonthDay(monthDay, cal);
				setTime(time, cal);
				cal.set(Calendar.MILLISECOND, fractionalSecs);
				break;
			case gMonth: // gMonth MonthDay, [TimeZone]
			case gMonthDay: // gMonthDay MonthDay, [TimeZone]
			case gDay: // gDay MonthDay, [TimeZone]
				setMonthDay(monthDay, cal);
				break;
			case time: // time Time, [FractionalSecs], [TimeZone]
				setTime(time, cal);
				cal.set(Calendar.MILLISECOND, fractionalSecs);
				break;
			default:
				throw new UnsupportedOperationException();
			}
			setTimezone(cal, timezone);
		}
		return cal;
	}

	/**
	 * Sets month and day of the given calendar making use of of the monthDay
	 * representation defined in EXI format
	 * 
	 * @param monthDay monthDay
	 * @param cal calendar
	 */
	protected static void setMonthDay(int monthDay, Calendar cal) {
		// monthDay = month * 32 + day;
		int month = monthDay / MONTH_MULTIPLICATOR;
		cal.set(Calendar.MONTH, month - 1);
		int day = monthDay - month * MONTH_MULTIPLICATOR;
		cal.set(Calendar.DAY_OF_MONTH, day);
	}

	/**
	 * Sets hour, minute and second of the given calendar making use of of the
	 * time representation defined in EXI format
	 * 
	 * @param time time
	 * @param cal calendar
	 */
	protected static void setTime(int time, Calendar cal) {
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
	 * Returns time-zone in minutes offset
	 * 
	 * @param cal calendar
	 * @return time-zone in minutes offset
	 */
	protected static int getTimeZoneInMinutesOffset(Calendar cal) {
		return cal.getTimeZone().getRawOffset() / (1000 * 60)
				+ TIMEZONE_OFFSET_IN_MINUTES;
	}

	/**
	 * Returns time-zone offset in millisecs according to the given minutes
	 * 
	 * @param minutes minutes
	 * @return time-zone offset in millisecs
	 */
	protected static int getTimeZoneInMillisecs(int minutes) {
		return minutes * (1000 * 60); // minutes to millisec
	}

	// protected static int getFractionalSecondsReverse(int millisec) {
	// int revFracSecs = 0;
	// if (millisec == 0) {
	// // ok -> 0
	// // } else if (millisec < 10) {
	// // revFracSecs = Integer.parseInt(new StringBuilder("00" + millisec)
	// // .reverse().toString());
	// // } else if (millisec < 100) {
	// // revFracSecs = Integer.parseInt(new StringBuilder("0" + millisec)
	// // .reverse().toString());
	// } else {
	// revFracSecs = Integer.parseInt(new StringBuilder(millisec + "")
	// .reverse().toString());
	// }
	//
	// return revFracSecs;
	// }

	public int getCharactersLength() {
		if (slen == -1) {
			switch (type) {
			case gYear: // Year, [Time-Zone]
				slen = (year < 0 ? 5 : 4);
				break;
			case gYearMonth: // Year, MonthDay, [TimeZone]
				slen = (year < 0 ? 5 : 4) + 3;
				break;
			case date: // Year, MonthDay, [TimeZone]
				slen = (year < 0 ? 5 : 4) + 6;
				break;
			case dateTime: // Year, MonthDay, Time, [FractionalSecs], [TimeZone]
				// e.g. "0001-01-01T00:00:00.111+00:33";
				sizeFractionalSecs = fractionalSecs == 0 ? 0 : MethodsBag
						.getStringSize(fractionalSecs) + 1;
				slen = (year < 0 ? 5 : 4) + 6 + 9 + (sizeFractionalSecs);
				break;
			case gMonth: // MonthDay, [TimeZone]
				// e.g. "--12"
				slen = 1 + 3;
				break;
			case gMonthDay: // MonthDay, [TimeZone]
				// e.g. "--01-28"
				slen = 1 + 6;
				break;
			case gDay: // MonthDay, [TimeZone]
				// "---16";
				slen = 3 + 2;
				break;
			case time: // Time, [FractionalSecs], [TimeZone]
				// e.g. "12:34:56.135"
				sizeFractionalSecs = fractionalSecs == 0 ? 0 : MethodsBag
						.getStringSize(fractionalSecs) + 1;
				slen = 8 + (sizeFractionalSecs);
				break;
			default:
				throw new UnsupportedOperationException();
			}

			// [TimeZone]
			if (presenceTimezone) {
				slen += timezone == 0 ? 1 : 6;
			}
		}

		return slen;
	}

	public void getCharacters(char[] cbuffer, int offset) {
		switch (type) {
		case gYear: // Year, [Time-Zone]
			offset = appendYear(cbuffer, offset, year);
			break;
		case gYearMonth: // Year, MonthDay, [TimeZone]
			offset = appendYear(cbuffer, offset, year);
			offset = appendMonth(cbuffer, offset, monthDay);
			break;
		case date: // Year, MonthDay, [TimeZone]
			offset = appendYear(cbuffer, offset, year);
			offset = appendMonthDay(cbuffer, offset, monthDay);
			break;
		case dateTime: // Year, MonthDay, Time, [FractionalSecs], [TimeZone]
			// e.g. "0001-01-01T00:00:00.111+00:33";
			offset = appendYear(cbuffer, offset, year);
			offset = appendMonthDay(cbuffer, offset, monthDay);
			cbuffer[offset++] = 'T';
			offset = appendTime(cbuffer, offset, time);
			assert (sizeFractionalSecs != -1);
			offset = appendFractionalSeconds(cbuffer, offset, fractionalSecs,
					sizeFractionalSecs - 1);
			break;
		case gMonth: // MonthDay, [TimeZone]
			// e.g. "--12"
			cbuffer[offset++] = '-';
			offset = appendMonth(cbuffer, offset, monthDay);
			break;
		case gMonthDay: // MonthDay, [TimeZone]
			// e.g. "--01-28"
			cbuffer[offset++] = '-';
			offset = appendMonthDay(cbuffer, offset, monthDay);
			break;
		case gDay: // MonthDay, [TimeZone]
			// "---16";
			cbuffer[offset++] = '-';
			cbuffer[offset++] = '-';
			cbuffer[offset++] = '-';
			offset = appendDay(cbuffer, offset, monthDay);
			break;
		case time: // Time, [FractionalSecs], [TimeZone]
			// e.g. "12:34:56.135"
			offset = appendTime(cbuffer, offset, time);
			assert (sizeFractionalSecs != -1);
			offset = appendFractionalSeconds(cbuffer, offset, fractionalSecs,
					sizeFractionalSecs - 1);
			break;
		default:
			throw new UnsupportedOperationException();
		}
		// [TimeZone]
		if (presenceTimezone) {
			appendTimezone(cbuffer, offset, timezone);
		}

		// return cbuffer;
	}

	private static void setTimezone(Calendar cal, int tz) {
		TimeZone tzO = TimeZone.getTimeZone("GMT+00:00");
		tzO.setRawOffset(tz);
		cal.setTimeZone(tzO);
	}

	private static int appendTimezone(char[] ca, int index, int tz) {
		if (tz == 0) {
			// per default 'Z'
			ca[index++] = 'Z';
		} else {
			// +/-
			if (tz < 0) {
				ca[index++] = '-';
				tz *= -1;
			} else {
				ca[index++] = '+';
			}
			// hours
			int hours = tz / 64;
			index = appendTwoDigits(ca, index, hours);
			// :
			ca[index++] = ':';
			// minutes
			int minutes = tz - (hours * 64);
			index = appendTwoDigits(ca, index, minutes);
		}
		return index;
	}

	private static int appendFractionalSeconds(char[] ca, int index,
			int fracSecs, int sLen) {
		if (fracSecs > 0) {
			// ".123"
			ca[index++] = '.';
			// reverse fracSecs
			index += MethodsBag.itosReverse(fracSecs, index, ca);
		}

		return index;
	}

	private static int appendTwoDigits(char[] ca, int index, int i) {
		if (i > 9) {
			index += 2;

		} else {
			ca[index++] = '0';
			index++;
		}
		MethodsBag.itos(i, index, ca);

		return index;
	}

	private static int appendYear(char[] ca, int index, int year) {
		// int sLen = 4;

		if (year < 0) {
			ca[index] = '-';
			index++;
			year = -year;
			// sLen++;
		}

		if (year > 999) {
			index += 4;
		} else if (year > 99) {
			ca[index++] = '0';
			index += 3;
		} else if (year > 9) {
			ca[index++] = '0';
			ca[index++] = '0';
			index += 2;

		} else {
			ca[index++] = '0';
			ca[index++] = '0';
			ca[index++] = '0';
			index++;
		}

		MethodsBag.itos(year, index, ca);

		return index;
	}

	private static int appendMonth(char[] ca, int index, int monthDay) {
		int month = monthDay / MONTH_MULTIPLICATOR;
		assert ((monthDay - month * MONTH_MULTIPLICATOR) == 0);

		// -MM
		ca[index++] = '-';
		// return appendTwoDigits(ca, index, month) + 1;
		return appendTwoDigits(ca, index, month);
	}

	private static int appendMonthDay(char[] ca, int index, int monthDay) {
		// monthDay: Month * 32 + Day

		// month & day
		int month = monthDay / MONTH_MULTIPLICATOR;
		int day = monthDay - (month * MONTH_MULTIPLICATOR);

		// -MM-DD
		ca[index++] = '-';
		index = appendTwoDigits(ca, index, month);
		ca[index++] = '-';
		return appendTwoDigits(ca, index, day);

		// return 6;
	}

	private static int appendDay(char[] ca, int index, int day) {
		assert (day < 31); // day range 0-30
		return appendTwoDigits(ca, index, day);
		// return 2;
	}

	private static int appendTime(char[] ca, int index, int time) {
		// time = ( ( hour * 64) + minutes ) * 64 + seconds ;
		// final int secHour = 64 * 64;
		// final int secMinute = 64;

		int hour = time / SECONDS_IN_HOUR;
		time -= hour * SECONDS_IN_HOUR;
		int minutes = time / SECONDS_IN_MINUTE;
		int seconds = time - minutes * SECONDS_IN_MINUTE;

		// hh ':' mm ':' ss
		index = appendTwoDigits(ca, index, hour);
		ca[index++] = ':';
		index = appendTwoDigits(ca, index, minutes);
		ca[index++] = ':';
		index = appendTwoDigits(ca, index, seconds);

		return index;
	}

	protected final boolean _equals(DateTimeValue o) {
		boolean ret = true;
		if (type == o.type && year == o.year && monthDay == o.monthDay
				&& time == o.time) {
			if (presenceFractionalSecs == o.presenceFractionalSecs) {
				if (fractionalSecs != o.fractionalSecs) {
					ret = false;
				}
			}
			if (ret && presenceTimezone == o.presenceTimezone) {
				if (timezone != o.timezone) {
					ret = false;
				}
			}
		} else {
			ret = false;
		}

		if (ret) {
			// easy match
			return ret;
		} else {
			// normalize both (if not already)
			if (this.normalized && o.normalized) {
				// not equal
			} else {
				// give it another try in normalized form
				DateTimeValue tn = this.normalize();
				DateTimeValue on = o.normalize();
				ret = tn._equals(on);
			}
		}

		return ret;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		if (o instanceof DateTimeValue) {
			return _equals((DateTimeValue) o);
		} else {
			DateTimeValue dt = DateTimeValue.parse(o.toString(), type);
			return dt == null ? false : _equals(dt);
		}
	}

	@Override
	public int hashCode() {
		return type.hashCode() ^ year ^ monthDay ^ time
				^ (presenceFractionalSecs ? 1 : 0) ^ fractionalSecs
				^ (presenceTimezone ? 1 : 0) ^ timezone;
	}

	public DateTimeValue normalize() {
		if (this.normalized) {
			return this;
		}
		if (normalizedDateTimeValue == null) {
			normalizedDateTimeValue = doNormalize();
		}

		return normalizedDateTimeValue;
	}

	// /////////////////////////////////////////////////////////////////////////////////////////
	// Algorithm http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/#d0e11648
	protected DateTimeValue doNormalize() {
		// year & month & day
		int year = this.year;
		int month = monthDay / MONTH_MULTIPLICATOR;
		int day = monthDay - (month * MONTH_MULTIPLICATOR);
		// time
		int hour = time / SECONDS_IN_HOUR;
		int time = this.time;
		time -= hour * SECONDS_IN_HOUR;
		int minutes = time / SECONDS_IN_MINUTE;
		int seconds = time - minutes * SECONDS_IN_MINUTE;
		// timezone, per default 'Z'
		int tzMinutes = 0;
		int tzHours = 0;
		if (this.presenceTimezone && this.timezone != 0) {
			final int tz = this.timezone; // +/-
			// hours
			tzHours = tz / 64;
			// minutes
			tzMinutes = tz - (tzHours * 64);
		}

		
		// start Algorithm
		final int negate = -1;

		// Minutes temp := S[minute] + D[minute] + carry E[minute] :=
		// modulo(temp, 60) carry := fQuotient(temp, 60)
		int temp = minutes + negate * tzMinutes;
		minutes = modulo(temp, 60);
		int carry = fQuotient(temp, 60);

		// Hours temp := S[hour] + D[hour] + carry E[hour] := modulo(temp, 24)
		// carry := fQuotient(temp, 24)
		temp = hour + negate * tzHours + carry;
		hour = modulo(temp, 24);
		carry = fQuotient(temp, 24);

		// Days
		int tempDays;
		// if S[day] > maximumDayInMonthFor(E[year], E[month])
		if (day > maximumDayInMonthFor(year, month)) {
			// tempDays := maximumDayInMonthFor(E[year], E[month])
			tempDays = maximumDayInMonthFor(year, month);
			// else if S[day] < 1
		} else if (day < 1) {
			// tempDays := 1
			tempDays = 1;
			// else
		} else {
			// tempDays := S[day]
			tempDays = day;
		}
		// E[day] := tempDays + D[day] + carry
		day = tempDays + carry;

		while (true) {
			if (day < 1) {
				day = day + maximumDayInMonthFor(year, month - 1);
				carry = -1;
			} else if (day > maximumDayInMonthFor(year, month)) {
				day = day - maximumDayInMonthFor(year, month);
				carry = 1;
			} else {
				break;
			}
			temp = month + carry;
			month = modulo(temp, 1, 13);
			year = year + fQuotient(temp, 1, 13);
		}

		// create new DateTimeValue
		int monthDay = month * 32 + day; // Month * 32 + Day
		time = ((hour * 64) + minutes) * 64 + seconds;// ((Hour * 64) + Minutes)
														// * 64 + seconds
		boolean presenceTimezone = true;
		int timezone = 0;

		return new DateTimeValue(this.type, year, monthDay, time,
				presenceFractionalSecs, fractionalSecs, presenceTimezone,
				timezone, true);
	}


	//
	// help function described in W3C PR Schema [E Adding durations to
	// dateTimes]
	//
	protected int fQuotient(int a, int b) {
		// fQuotient(a, b) = the greatest integer less than or equal to a/b
		return (int) Math.floor((float) a / b);
	}

	//
	// help function described in W3C PR Schema [E Adding durations to
	// dateTimes]
	//
	protected int fQuotient(int temp, int low, int high) {
		// fQuotient(a - low, high - low)
		return fQuotient(temp - low, high - low);
	}

	protected int modulo(int a, int b) {
		// a - fQuotient(a,b)*b
		return (a - fQuotient(a, b) * b);
	}

	//
	// help function described in W3C PR Schema [E Adding durations to
	// dateTimes]
	//
	protected int modulo(int a, int low, int high) {
		// modulo(a - low, high - low) + low 
		return modulo(a - low, high - low) + low;
	}

	//
	// help function described in W3C PR Schema [E Adding durations to
	// dateTimes]
	//
	protected int maximumDayInMonthFor(int year, int month) {
		// 31 M = January, March, May, July, August, October, or December
		// 30 M = April, June, September, or November
		// 29 M = February AND (modulo(Y, 400) = 0 OR (modulo(Y, 100) != 0) AND
		// modulo(Y, 4) = 0)
		// 28 Otherwise

		if (month == 1 || month == 3 || month == 7 || month == 8 || month == 10
				|| month == 12) {
			return 31;
		} else if (month == 4 || month == 6 || month == 9 || month == 11) {
			return 30;
		} else if (month == 2
				&& (modulo(year, 400) == 0 || (modulo(year, 100) != 0)
						&& modulo(year, 4) == 0)) {
			return 29;
		} else {
			return 28;
		}
	}
}
