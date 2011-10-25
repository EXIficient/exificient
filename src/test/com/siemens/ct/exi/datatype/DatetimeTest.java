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

package com.siemens.ct.exi.datatype;

import java.io.IOException;

import com.siemens.ct.exi.io.channel.EncoderChannel;
import com.siemens.ct.exi.types.DateTimeType;
import com.siemens.ct.exi.values.DateTimeValue;

public class DatetimeTest extends AbstractTestCase {

	public DatetimeTest(String testName) {
		super(testName);
	}

	/*
	 * gYear
	 */
	public void testDatetimeGYear0() throws IOException {
		String s = "2007";
		DateTimeType type = DateTimeType.gYear;
		DateTimeValue datetime = DateTimeValue.parse(s, type);
		assertTrue(datetime != null);

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeDateTime(datetime);
		bitEC.flush();
		DateTimeValue dtv1 = getBitDecoder().decodeDateTimeValue(type);
		assertTrue(s.equals(dtv1.toString()));
		// Byte
		getByteEncoder().encodeDateTime(datetime);
		DateTimeValue dtv2 = getByteDecoder().decodeDateTimeValue(type);
		assertTrue(s.equals(dtv2.toString()));
	}

	public void testDatetimeGYear1() throws IOException {
		String s = "-0007";
		DateTimeType type = DateTimeType.gYear;
		DateTimeValue datetime = DateTimeValue.parse(s, type);
		assertTrue(datetime != null);

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeDateTime(datetime);
		bitEC.flush();
		assertTrue(s.equals(getBitDecoder().decodeDateTimeValue(type).toString()));
		// Byte
		getByteEncoder().encodeDateTime(datetime);
		assertTrue(s.equals(getByteDecoder().decodeDateTimeValue(type).toString()));
	}

	public void testDatetimeGYear2() throws IOException {
		String s = "2001Z";
		DateTimeType type = DateTimeType.gYear;
		DateTimeValue datetime = DateTimeValue.parse(s, type);
		assertTrue(datetime != null);

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeDateTime(datetime);
		bitEC.flush();
		DateTimeValue dtv1 = getBitDecoder().decodeDateTimeValue(type);
		assertTrue(s.equals(dtv1.toString()));
		// Byte
		getByteEncoder().encodeDateTime(datetime);
		assertTrue(s.equals(getByteDecoder().decodeDateTimeValue(type).toString()));
	}

	public void testDatetimeGYear3() throws IOException {
		String s = "2001+05:00";
		DateTimeType type = DateTimeType.gYear;
		DateTimeValue datetime = DateTimeValue.parse(s, type);
		assertTrue(datetime != null);

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeDateTime(datetime);
		bitEC.flush();
		assertTrue(s.equals(getBitDecoder().decodeDateTimeValue(type).toString()));
		// Byte
		getByteEncoder().encodeDateTime(datetime);
		assertTrue(s.equals(getByteDecoder().decodeDateTimeValue(type).toString()));
	}
	
	public void testDatetimeGYear4() throws IOException {
		String s = "2009-13:59";
		DateTimeType type = DateTimeType.gYear;
		DateTimeValue datetime = DateTimeValue.parse(s, type);
		assertTrue(datetime != null);

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeDateTime(datetime);
		bitEC.flush();
		assertTrue(s.equals(getBitDecoder().decodeDateTimeValue(type).toString()));
		// Byte
		getByteEncoder().encodeDateTime(datetime);
		assertTrue(s.equals(getByteDecoder().decodeDateTimeValue(type).toString()));
	}

	/*
	 * gYearMonth
	 */
	public void testDatetimeGYearMonth0() throws IOException {
		String s = "1999-10";
		DateTimeType type = DateTimeType.gYearMonth;
		DateTimeValue datetime = DateTimeValue.parse(s, type);
		assertTrue(datetime != null);

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeDateTime(datetime);
		bitEC.flush();
		assertTrue(s.equals(getBitDecoder().decodeDateTimeValue(type).toString()));
		// Byte
		getByteEncoder().encodeDateTime(datetime);
		assertTrue(s.equals(getByteDecoder().decodeDateTimeValue(type).toString()));
	}

	public void testDatetimeGYearMonth1() throws IOException {
		String s = "2007-10";
		DateTimeType type = DateTimeType.gYearMonth;
		DateTimeValue datetime = DateTimeValue.parse(s, type);
		assertTrue(datetime != null);

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeDateTime(datetime);
		bitEC.flush();
		assertTrue(s.equals(getBitDecoder().decodeDateTimeValue(type).toString()));
		// Byte
		getByteEncoder().encodeDateTime(datetime);
		assertTrue(s.equals(getByteDecoder().decodeDateTimeValue(type).toString()));
	}

	public void testDatetimeGYearMonth2() throws IOException {
		String s = "1999-10-12:11";
		DateTimeType type = DateTimeType.gYearMonth;
		DateTimeValue datetime = DateTimeValue.parse(s, type);
		assertTrue(datetime != null);

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeDateTime(datetime);
		bitEC.flush();
		assertTrue(s.equals(getBitDecoder().decodeDateTimeValue(type).toString()));
		// Byte
		getByteEncoder().encodeDateTime(datetime);
		assertTrue(s.equals(getByteDecoder().decodeDateTimeValue(type).toString()));
	}

	public void testDatetimeGYearMonth3() throws IOException {
		String s = "1809-10+00:33";
		DateTimeType type = DateTimeType.gYearMonth;
		DateTimeValue datetime = DateTimeValue.parse(s, type);
		assertTrue(datetime != null);

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeDateTime(datetime);
		bitEC.flush();
		assertTrue(s.equals(getBitDecoder().decodeDateTimeValue(type).toString()));
		// Byte
		getByteEncoder().encodeDateTime(datetime);
		assertTrue(s.equals(getByteDecoder().decodeDateTimeValue(type).toString()));
	}
	
	public void testDatetimeGYearMonth4() throws IOException {
		String s = "1809-02Z";
		DateTimeType type = DateTimeType.gYearMonth;
		DateTimeValue datetime = DateTimeValue.parse(s, type);
		assertTrue(datetime != null);

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeDateTime(datetime);
		bitEC.flush();
		DateTimeValue dtv = getBitDecoder().decodeDateTimeValue(type);
		assertTrue(s.equals(dtv.toString()));
		// Byte
		getByteEncoder().encodeDateTime(datetime);
		assertTrue(s.equals(getByteDecoder().decodeDateTimeValue(type).toString()));
	}
	
	public void testDatetimeGYearMonth5() throws IOException {
		String s = "1999-10-12:11";
		DateTimeType type = DateTimeType.gYearMonth;
		DateTimeValue datetime = DateTimeValue.parse(s, type);
		assertTrue(datetime != null);

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeDateTime(datetime);
		bitEC.flush();
		DateTimeValue dtv = getBitDecoder().decodeDateTimeValue(type);
		assertTrue(datetime.presenceTimezone == dtv.presenceTimezone);
		if (datetime.presenceTimezone) {
			assertTrue(datetime.timezone == dtv.timezone);
		}
		assertTrue(s.equals(dtv.toString()));
		// Byte
		getByteEncoder().encodeDateTime(datetime);
		assertTrue(s.equals(getByteDecoder().decodeDateTimeValue(type).toString()));
	}
	
	

	/*
	 * date
	 */
	public void testDatetimeDate0() throws IOException {
		String s = "2002-10-11+13:00";
		DateTimeType type = DateTimeType.date;
		DateTimeValue datetime = DateTimeValue.parse(s, type);
		assertTrue(datetime != null);

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeDateTime(datetime);
		bitEC.flush();
		assertTrue(s.equals(getBitDecoder().decodeDateTimeValue(type).toString()));
		// Byte
		getByteEncoder().encodeDateTime(datetime);
		assertTrue(s.equals(getByteDecoder().decodeDateTimeValue(type).toString()));
	}

	public void testDatetimeDate1() throws IOException {
		String s = "0100-10-11-00:50";
		DateTimeType type = DateTimeType.date;
		DateTimeValue datetime = DateTimeValue.parse(s, type);
		assertTrue(datetime != null);

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeDateTime(datetime);
		bitEC.flush();
		assertTrue(s.equals(getBitDecoder().decodeDateTimeValue(type).toString()));
		// Byte
		getByteEncoder().encodeDateTime(datetime);
		assertTrue(s.equals(getByteDecoder().decodeDateTimeValue(type).toString()));
	}

	/*
	 * dateTime
	 */
	public void testDatetimeDateTime0() throws IOException {
		String s = "0001-01-01T01:03:12";
		DateTimeType type = DateTimeType.dateTime;
		DateTimeValue datetime = DateTimeValue.parse(s, type);
		assertTrue(datetime != null);

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeDateTime(datetime);
		bitEC.flush();
		DateTimeValue dtv1 = getBitDecoder().decodeDateTimeValue(type);
		assertTrue(s.equals(dtv1.toString()));
		// Byte
		getByteEncoder().encodeDateTime(datetime);
		DateTimeValue dtv2 = getByteDecoder().decodeDateTimeValue(type);
		assertTrue(s.equals(dtv2.toString()));
	}

	public void testDatetimeDateTime1() throws IOException {
		String s = "2005-01-01T00:00:00Z";
		DateTimeType type = DateTimeType.dateTime;
		DateTimeValue datetime = DateTimeValue.parse(s, type);
		assertTrue(datetime != null);

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeDateTime(datetime);
		bitEC.flush();
		DateTimeValue dtv1 = getBitDecoder().decodeDateTimeValue(type);
		assertTrue(s.equals(dtv1.toString()));
		// Byte
		getByteEncoder().encodeDateTime(datetime);
		assertTrue(s.equals(getByteDecoder().decodeDateTimeValue(type).toString()));
	}

	public void testDatetimeDateTime2() throws IOException {
		String s = "1979-01-01T00:00:00.0120";
		String sRes = "1979-01-01T00:00:00.012";
		DateTimeType type = DateTimeType.dateTime;
		DateTimeValue datetime = DateTimeValue.parse(s, type);
		assertTrue(datetime != null);

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeDateTime(datetime);
		bitEC.flush();
		DateTimeValue dtv1 = getBitDecoder().decodeDateTimeValue(type);
		assertTrue(sRes.equals(dtv1.toString()));
		// Byte
		getByteEncoder().encodeDateTime(datetime);
		assertTrue(sRes.equals(getByteDecoder().decodeDateTimeValue(type).toString()));
	}

	public void testDatetimeDateTime3() throws IOException {
		String s = "0001-01-01T00:00:00.111+00:33";
		DateTimeType type = DateTimeType.dateTime;
		DateTimeValue datetime = DateTimeValue.parse(s, type);
		assertTrue(datetime != null);

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeDateTime(datetime);
		bitEC.flush();
		assertTrue(s.equals(getBitDecoder().decodeDateTimeValue(type).toString()));
		// Byte
		getByteEncoder().encodeDateTime(datetime);
		assertTrue(s.equals(getByteDecoder().decodeDateTimeValue(type).toString()));
	}
	
	public void testDatetimeDateTime4() throws IOException {
		String s = "2009-04-01T12:34:56Z";
		DateTimeType type = DateTimeType.dateTime;
		DateTimeValue datetime = DateTimeValue.parse(s, type);
		assertTrue(datetime != null);

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeDateTime(datetime);
		bitEC.flush();
		DateTimeValue dtv = getBitDecoder().decodeDateTimeValue(type);
		assertTrue(s.equals(dtv.toString()));
		// Byte
		getByteEncoder().encodeDateTime(datetime);
		assertTrue(s.equals(getByteDecoder().decodeDateTimeValue(type).toString()));
	}

	/*
	 * gMonth
	 */
	public void testDatetimeGMonth0() throws IOException {
		String s = "--12";
		DateTimeType type = DateTimeType.gMonth;
		DateTimeValue datetime = DateTimeValue.parse(s, type);
		assertTrue(datetime != null);

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeDateTime(datetime);
		bitEC.flush();
		assertTrue(s.equals(getBitDecoder().decodeDateTimeValue(type).toString()));
		// Byte
		getByteEncoder().encodeDateTime(datetime);
		assertTrue(s.equals(getByteDecoder().decodeDateTimeValue(type).toString()));
	}

	public void testDatetimeGMonth1() throws IOException {
		String s = "--07Z";
		DateTimeType type = DateTimeType.gMonth;
		DateTimeValue datetime = DateTimeValue.parse(s, type);
		assertTrue(datetime != null);

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeDateTime(datetime);
		bitEC.flush();
		assertTrue(s.equals(getBitDecoder().decodeDateTimeValue(type).toString()));
		// Byte
		getByteEncoder().encodeDateTime(datetime);
		assertTrue(s.equals(getByteDecoder().decodeDateTimeValue(type).toString()));
	}
	
	public void testDatetimeGMonth2() throws IOException {
		String s = "--07+12:34";
		DateTimeType type = DateTimeType.gMonth;
		DateTimeValue datetime = DateTimeValue.parse(s, type);
		assertTrue(datetime != null);

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeDateTime(datetime);
		bitEC.flush();
		assertTrue(s.equals(getBitDecoder().decodeDateTimeValue(type).toString()));
		// Byte
		getByteEncoder().encodeDateTime(datetime);
		assertTrue(s.equals(getByteDecoder().decodeDateTimeValue(type).toString()));
	}
	
	public void testDatetimeGMonth3() throws IOException {
		String s = "--07--+09:00";
		String sr = "--07+09:00";
		DateTimeType type = DateTimeType.gMonth;
		DateTimeValue datetime = DateTimeValue.parse(s, type);
		assertTrue(datetime != null);
	
		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeDateTime(datetime);
		bitEC.flush();
		assertTrue(sr.equals(getBitDecoder().decodeDateTimeValue(type).toString()));
		// Byte
		getByteEncoder().encodeDateTime(datetime);
		assertTrue(sr.equals(getByteDecoder().decodeDateTimeValue(type).toString()));
	}

	/*
	 * gMonthDay
	 */
	public void testDatetimeGMonthDay0() throws IOException {
		String s = "--01-28";
		DateTimeType type = DateTimeType.gMonthDay;
		DateTimeValue datetime = DateTimeValue.parse(s, type);
		assertTrue(datetime != null);

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeDateTime(datetime);
		bitEC.flush();
		assertTrue(s.equals(getBitDecoder().decodeDateTimeValue(type).toString()));
		// Byte
		getByteEncoder().encodeDateTime(datetime);
		assertTrue(s.equals(getByteDecoder().decodeDateTimeValue(type).toString()));
	}

	public void testDatetimeGMonthDay1() throws IOException {
		String s = "--10-17+00:00";
		String sRes = "--10-17Z";
		DateTimeType type = DateTimeType.gMonthDay;
		DateTimeValue datetime = DateTimeValue.parse(s, type);
		assertTrue(datetime != null);

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeDateTime(datetime);
		bitEC.flush();
		assertTrue(sRes.equals(getBitDecoder().decodeDateTimeValue(type).toString()));
		// Byte
		getByteEncoder().encodeDateTime(datetime);
		assertTrue(sRes.equals(getByteDecoder().decodeDateTimeValue(type).toString()));
	}

	/*
	 * gDay
	 */
	public void testDatetimeGDay0() throws IOException {
		String s = "---16";
		DateTimeType type = DateTimeType.gDay;
		DateTimeValue datetime = DateTimeValue.parse(s, type);
		assertTrue(datetime != null);

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeDateTime(datetime);
		bitEC.flush();
		assertTrue(s.equals(getBitDecoder().decodeDateTimeValue(type).toString()));
		// Byte
		getByteEncoder().encodeDateTime(datetime);
		assertTrue(s.equals(getByteDecoder().decodeDateTimeValue(type).toString()));
	}

	/*
	 * time
	 */
	public void testDatetimeTime0() throws IOException {
		String s = "12:34:56";
		DateTimeType type = DateTimeType.time;
		DateTimeValue datetime = DateTimeValue.parse(s, type);
		assertTrue(datetime != null);

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeDateTime(datetime);
		bitEC.flush();
		assertTrue(s.equals(getBitDecoder().decodeDateTimeValue(type).toString()));
		// Byte
		getByteEncoder().encodeDateTime(datetime);
		assertTrue(s.equals(getByteDecoder().decodeDateTimeValue(type).toString()));
	}

	public void testDatetimeTime1() throws IOException {
		String s = "12:34:56.135";
		DateTimeType type = DateTimeType.time;
		DateTimeValue datetime = DateTimeValue.parse(s, type);
		assertTrue(datetime != null);

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeDateTime(datetime);
		bitEC.flush();
		assertTrue(s.equals(getBitDecoder().decodeDateTimeValue(type).toString()));
		// Byte
		getByteEncoder().encodeDateTime(datetime);
		assertTrue(s.equals(getByteDecoder().decodeDateTimeValue(type).toString()));
	}

	public void testDatetimeTime2() throws IOException {
		String s = "12:34:56.135-12:56";
		DateTimeType type = DateTimeType.time;
		DateTimeValue datetime = DateTimeValue.parse(s, type);
		assertTrue(datetime != null);

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeDateTime(datetime);
		bitEC.flush();
		DateTimeValue dtv = getBitDecoder().decodeDateTimeValue(type);
		assertTrue(s.equals(dtv.toString()));
		// Byte
		getByteEncoder().encodeDateTime(datetime);
		assertTrue(s.equals(getByteDecoder().decodeDateTimeValue(type).toString()));
	}

	public void testDatetimeFail1() throws IOException {
		String s = "12:34:XXX";
		DateTimeType type = DateTimeType.time;
		DateTimeValue datetime = DateTimeValue.parse(s, type);
		assertFalse(datetime != null);
	}

}