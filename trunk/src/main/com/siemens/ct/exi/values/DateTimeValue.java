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

package com.siemens.ct.exi.values;

import java.util.Calendar;
import java.util.TimeZone;

import com.siemens.ct.exi.util.MethodsBag;
import com.siemens.ct.exi.util.datatype.DatetimeType;
import com.siemens.ct.exi.util.datatype.XSDDatetime;

public class DateTimeValue extends AbstractValue {

	protected final DatetimeType type;
	protected final int year;
	protected final int monthDay;
	protected final int time;
	protected final int fractionalSecs;
	protected final int timeZone;

	protected Calendar cal;

	public DateTimeValue(DatetimeType type, int year, int monthDay, int time,
			int fractionalSecs, int timeZone) {
		this.type = type;
		this.year = year;
		this.monthDay = monthDay;
		this.time = time;
		this.fractionalSecs = fractionalSecs;
		this.timeZone = timeZone;
	}

	public Calendar toCalendar() {
		if (cal == null) {
			Calendar cal = Calendar.getInstance();
			cal.clear();

			switch (type) {
			case gYear: // gYear Year, [Time-Zone]
				cal.set(Calendar.YEAR, year);
				setTimezone(cal, timeZone);
				break;
			case gYearMonth: // gYearMonth Year, MonthDay, [TimeZone]
			case date: // date Year, MonthDay, [TimeZone]
				cal.set(Calendar.YEAR, year);
				XSDDatetime.setMonthDay(monthDay, cal);
				setTimezone(cal, timeZone);
				break;
			case dateTime: // dateTime Year, MonthDay, Time, [FractionalSecs],
				// [TimeZone]
				cal.set(Calendar.YEAR, year);
				XSDDatetime.setMonthDay(monthDay, cal);
				XSDDatetime.setTime(time, cal);
				cal.set(Calendar.MILLISECOND, fractionalSecs);
				setTimezone(cal, timeZone);
				break;
			case gMonth: // gMonth MonthDay, [TimeZone]
			case gMonthDay: // gMonthDay MonthDay, [TimeZone]
			case gDay: // gDay MonthDay, [TimeZone]
				XSDDatetime.setMonthDay(monthDay, cal);
				setTimezone(cal, timeZone);
				break;
			case time: // time Time, [FractionalSecs], [TimeZone]
				XSDDatetime.setTime(time, cal);
				cal.set(Calendar.MILLISECOND, fractionalSecs);
				setTimezone(cal, timeZone);
				break;
			default:
				throw new UnsupportedOperationException();
			}
		}
		return cal;
	}

	public char[] toCharacters() {
		if (characters == null) {
			int index = 0;
			switch (type) {
			case gYear: // Year, [Time-Zone]
				characters = new char[(year < 0 ? 5 : 4)
						+ (timeZone == 0 ? 0 : 6)];
				index += appendYear(characters, index, year);
				appendTimezone(characters, index, timeZone);
				break;
			case gYearMonth: // Year, MonthDay, [TimeZone]
				characters = new char[(year < 0 ? 5 : 4) + 3
						+ (timeZone == 0 ? 0 : 6)];
				index += appendYear(characters, index, year);
				index += appendMonth(characters, index, monthDay);
				appendTimezone(characters, index, timeZone);
				break;
			case date: // Year, MonthDay, [TimeZone]
				characters = new char[(year < 0 ? 5 : 4) + 6
						+ (timeZone == 0 ? 0 : 6)];
				index += appendYear(characters, index, year);
				index += appendMonthDay(characters, index, monthDay);
				appendTimezone(characters, index, timeZone);
				break;
			case dateTime: // Year, MonthDay, Time, [FractionalSecs], [TimeZone]
				// e.g. "0001-01-01T00:00:00.111+00:33";
				int sizeFractionalSecs = fractionalSecs == 0 ? 0 : MethodsBag
						.getStringSize(fractionalSecs) + 1;
				characters = new char[(year < 0 ? 5 : 4) + 6 + 9
						+ (sizeFractionalSecs) + (timeZone == 0 ? 0 : 6)];
				index += appendYear(characters, index, year);
				index += appendMonthDay(characters, index, monthDay);
				characters[index++] = 'T';
				index += appendTime(characters, index, time);
				index += appendFractionalSeconds(characters, index,
						fractionalSecs, sizeFractionalSecs - 1);
				appendTimezone(characters, index, timeZone);
				break;
			case gMonth: // MonthDay, [TimeZone]
				// e.g. "--12"
				characters = new char[1 + 3 + (timeZone == 0 ? 0 : 6)];
				characters[index++] = '-';
				index += appendMonth(characters, index, monthDay);
				appendTimezone(characters, index, timeZone);
				break;
			case gMonthDay: // MonthDay, [TimeZone]
				// e.g. "--01-28"
				characters = new char[1 + 6 + (timeZone == 0 ? 0 : 6)];
				characters[index++] = '-';
				index += appendMonthDay(characters, index, monthDay);
				appendTimezone(characters, index, timeZone);
				break;
			case gDay: // MonthDay, [TimeZone]
				// "---16";
				characters = new char[3 + 2 + (timeZone == 0 ? 0 : 6)];
				characters[index++] = '-';
				characters[index++] = '-';
				characters[index++] = '-';
				index += appendDay(characters, index, monthDay);
				appendTimezone(characters, index, timeZone);
				break;
			case time: // Time, [FractionalSecs], [TimeZone]
				// e.g. "12:34:56.135"
				sizeFractionalSecs = fractionalSecs == 0 ? 0 : MethodsBag
						.getStringSize(fractionalSecs) + 1;
				characters = new char[8 + (sizeFractionalSecs)
						+ (timeZone == 0 ? 0 : 6)];
				index += appendTime(characters, index, time);
				index += appendFractionalSeconds(characters, index,
						fractionalSecs, sizeFractionalSecs - 1);
				appendTimezone(characters, index, timeZone);
				break;
			default:
				throw new UnsupportedOperationException();
			}
		}

		return characters;
	}

	private static void setTimezone(Calendar cal, int tz) {
		TimeZone tzO = TimeZone.getTimeZone("GMT+00:00");
		tzO.setRawOffset(tz);
		cal.setTimeZone(tzO);
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
			// :
			ca[index++] = ':';
			// minutes
			int minutes = tz - (hours * 60);
			appendTwoDigits(ca, index, minutes);
		}
	}

	private static int appendFractionalSeconds(char[] ca, int index,
			int fracSecs, int sLen) {
		if (fracSecs > 0) {
			// ".123"
			ca[index++] = '.';
			// reverse fracSecs
			int chars = MethodsBag.itosReverse(fracSecs, index, ca);
			return chars + 1;
		} else {
			return 0;
		}
	}

	private static int appendTwoDigits(char[] ca, int index, int i) {
		if (i > 9) {
			MethodsBag.itos(i, index + 2, ca);
			// index++;
		} else {
			ca[index++] = '0';
			MethodsBag.itos(i, index + 1, ca);
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
			MethodsBag.itos(year, index + 4, ca);
		} else if (year > 99) {
			ca[index++] = '0';
			MethodsBag.itos(year, index + 3, ca);
		} else if (year > 9) {
			ca[index++] = '0';
			ca[index++] = '0';
			MethodsBag.itos(year, index + 2, ca);
		} else {
			ca[index++] = '0';
			ca[index++] = '0';
			ca[index++] = '0';
			MethodsBag.itos(year, index + 1, ca);
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
		int month = monthDay / XSDDatetime.MONTH_MULTIPLICATOR;
		int day = monthDay - (month * XSDDatetime.MONTH_MULTIPLICATOR);

		// -MM-DD
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

		// hh ':' mm ':' ss
		index += appendTwoDigits(ca, index, hour);
		ca[index++] = ':';
		index += appendTwoDigits(ca, index, minutes);
		ca[index++] = ':';
		index += appendTwoDigits(ca, index, seconds);

		return 8;
	}

}
