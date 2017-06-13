/*
 * Copyright (c) 2007-2016 Siemens AG
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
		assertTrue(s.equals(getBitDecoder().decodeDateTimeValue(type)
				.toString()));
		// Byte
		getByteEncoder().encodeDateTime(datetime);
		assertTrue(s.equals(getByteDecoder().decodeDateTimeValue(type)
				.toString()));
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
		assertTrue(s.equals(getByteDecoder().decodeDateTimeValue(type)
				.toString()));
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
		assertTrue(s.equals(getBitDecoder().decodeDateTimeValue(type)
				.toString()));
		// Byte
		getByteEncoder().encodeDateTime(datetime);
		assertTrue(s.equals(getByteDecoder().decodeDateTimeValue(type)
				.toString()));
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
		assertTrue(s.equals(getBitDecoder().decodeDateTimeValue(type)
				.toString()));
		// Byte
		getByteEncoder().encodeDateTime(datetime);
		assertTrue(s.equals(getByteDecoder().decodeDateTimeValue(type)
				.toString()));
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
		assertTrue(s.equals(getBitDecoder().decodeDateTimeValue(type)
				.toString()));
		// Byte
		getByteEncoder().encodeDateTime(datetime);
		assertTrue(s.equals(getByteDecoder().decodeDateTimeValue(type)
				.toString()));
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
		assertTrue(s.equals(getBitDecoder().decodeDateTimeValue(type)
				.toString()));
		// Byte
		getByteEncoder().encodeDateTime(datetime);
		assertTrue(s.equals(getByteDecoder().decodeDateTimeValue(type)
				.toString()));
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
		assertTrue(s.equals(getBitDecoder().decodeDateTimeValue(type)
				.toString()));
		// Byte
		getByteEncoder().encodeDateTime(datetime);
		assertTrue(s.equals(getByteDecoder().decodeDateTimeValue(type)
				.toString()));
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
		assertTrue(s.equals(getBitDecoder().decodeDateTimeValue(type)
				.toString()));
		// Byte
		getByteEncoder().encodeDateTime(datetime);
		assertTrue(s.equals(getByteDecoder().decodeDateTimeValue(type)
				.toString()));
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
		assertTrue(s.equals(getByteDecoder().decodeDateTimeValue(type)
				.toString()));
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
		assertTrue(s.equals(getByteDecoder().decodeDateTimeValue(type)
				.toString()));
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
		assertTrue(s.equals(getBitDecoder().decodeDateTimeValue(type)
				.toString()));
		// Byte
		getByteEncoder().encodeDateTime(datetime);
		assertTrue(s.equals(getByteDecoder().decodeDateTimeValue(type)
				.toString()));
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
		assertTrue(s.equals(getBitDecoder().decodeDateTimeValue(type)
				.toString()));
		// Byte
		getByteEncoder().encodeDateTime(datetime);
		assertTrue(s.equals(getByteDecoder().decodeDateTimeValue(type)
				.toString()));
	}
	
	
	// Fractional seconds component MUST be omitted if its value is zero
	public void testDatetimeFractionalSecs1() throws IOException {
		String s = "1996-02-29T19:20:30.00Z";
		String sDec = "1996-02-29T19:20:30Z";
		DateTimeType type = DateTimeType.dateTime;
		DateTimeValue datetime = DateTimeValue.parse(s, type);
		assertTrue(datetime != null);
//		datetime = datetime.normalize();
		assertFalse(datetime.presenceFractionalSecs);
		

		// Bit
		EncoderChannel bitEC = getBitEncoder();
		bitEC.encodeDateTime(datetime);
		bitEC.flush();
		assertTrue(sDec.equals(getBitDecoder().decodeDateTimeValue(type)
				.toString()));
		// Byte
		getByteEncoder().encodeDateTime(datetime);
		assertTrue(sDec.equals(getByteDecoder().decodeDateTimeValue(type)
				.toString()));
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
		assertTrue(s.equals(getByteDecoder().decodeDateTimeValue(type)
				.toString()));
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
		assertTrue(sRes.equals(getByteDecoder().decodeDateTimeValue(type)
				.toString()));
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
		assertTrue(s.equals(getBitDecoder().decodeDateTimeValue(type)
				.toString()));
		// Byte
		getByteEncoder().encodeDateTime(datetime);
		assertTrue(s.equals(getByteDecoder().decodeDateTimeValue(type)
				.toString()));
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
		assertTrue(s.equals(getByteDecoder().decodeDateTimeValue(type)
				.toString()));
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
		assertTrue(s.equals(getBitDecoder().decodeDateTimeValue(type)
				.toString()));
		// Byte
		getByteEncoder().encodeDateTime(datetime);
		assertTrue(s.equals(getByteDecoder().decodeDateTimeValue(type)
				.toString()));
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
		assertTrue(s.equals(getBitDecoder().decodeDateTimeValue(type)
				.toString()));
		// Byte
		getByteEncoder().encodeDateTime(datetime);
		assertTrue(s.equals(getByteDecoder().decodeDateTimeValue(type)
				.toString()));
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
		assertTrue(s.equals(getBitDecoder().decodeDateTimeValue(type)
				.toString()));
		// Byte
		getByteEncoder().encodeDateTime(datetime);
		assertTrue(s.equals(getByteDecoder().decodeDateTimeValue(type)
				.toString()));
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
		assertTrue(sr.equals(getBitDecoder().decodeDateTimeValue(type)
				.toString()));
		// Byte
		getByteEncoder().encodeDateTime(datetime);
		assertTrue(sr.equals(getByteDecoder().decodeDateTimeValue(type)
				.toString()));
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
		assertTrue(s.equals(getBitDecoder().decodeDateTimeValue(type)
				.toString()));
		// Byte
		getByteEncoder().encodeDateTime(datetime);
		assertTrue(s.equals(getByteDecoder().decodeDateTimeValue(type)
				.toString()));
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
		assertTrue(sRes.equals(getBitDecoder().decodeDateTimeValue(type)
				.toString()));
		// Byte
		getByteEncoder().encodeDateTime(datetime);
		assertTrue(sRes.equals(getByteDecoder().decodeDateTimeValue(type)
				.toString()));
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
		assertTrue(s.equals(getBitDecoder().decodeDateTimeValue(type)
				.toString()));
		// Byte
		getByteEncoder().encodeDateTime(datetime);
		assertTrue(s.equals(getByteDecoder().decodeDateTimeValue(type)
				.toString()));
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
		assertTrue(s.equals(getBitDecoder().decodeDateTimeValue(type)
				.toString()));
		// Byte
		getByteEncoder().encodeDateTime(datetime);
		assertTrue(s.equals(getByteDecoder().decodeDateTimeValue(type)
				.toString()));
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
		assertTrue(s.equals(getBitDecoder().decodeDateTimeValue(type)
				.toString()));
		// Byte
		getByteEncoder().encodeDateTime(datetime);
		assertTrue(s.equals(getByteDecoder().decodeDateTimeValue(type)
				.toString()));
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
		assertTrue(s.equals(getByteDecoder().decodeDateTimeValue(type)
				.toString()));
	}
	
	public void testDatetimeEquals1() throws IOException {
		// all the same times
		String s1 = "2015-08-11T23:00:00+09:00";
		String s2 = "2015-08-11T16:00:00+02:00";
		String s3 = "2015-08-11T14:00:00Z"; // UTC
		String s4 = "2015-08-11T07:00:00-07:00";
		DateTimeType type = DateTimeType.dateTime;
		DateTimeValue datetime1 = DateTimeValue.parse(s1, type);
		DateTimeValue datetime2 = DateTimeValue.parse(s2, type);
		DateTimeValue datetime3 = DateTimeValue.parse(s3, type);
		DateTimeValue datetime4 = DateTimeValue.parse(s4, type);
		assertTrue(datetime1 != null);
		assertTrue(datetime2 != null);
		assertTrue(datetime3 != null);
		assertTrue(datetime4 != null);
		
		DateTimeValue datetime1Norm =  datetime1.normalize();
		DateTimeValue datetime2Norm =  datetime2.normalize();
		DateTimeValue datetime3Norm =  datetime3.normalize();
		DateTimeValue datetime4Norm =  datetime4.normalize();
		
		assertTrue(datetime1.equals(datetime2));
		assertTrue(datetime2.equals(datetime3));
		assertTrue(datetime3.equals(datetime4));
		assertTrue(datetime4.equals(datetime1));
		assertTrue(datetime1Norm.equals(datetime2));
		assertTrue(datetime2Norm.equals(datetime3));
		assertTrue(datetime3Norm.equals(datetime4));
		assertTrue(datetime4Norm.equals(datetime1));
		
	}
	
	public void testDatetimeEquals2() throws IOException {
		// all the same times
		String s1 = "2015-08-11T24:00:00-07:30";
		String s2 = "2015-08-12T00:00:00-07:30";
		DateTimeType type = DateTimeType.dateTime;
		DateTimeValue datetime1 = DateTimeValue.parse(s1, type);
		DateTimeValue datetime2 = DateTimeValue.parse(s2, type);
		assertTrue(datetime1 != null);
		assertTrue(datetime2 != null);
		
		DateTimeValue datetime1Norm =  datetime1.normalize();
		DateTimeValue datetime2Norm =  datetime2.normalize();
		
		assertTrue(datetime1.equals(datetime2));
		assertTrue(datetime2.equals(datetime1Norm));
		assertTrue(datetime2Norm.equals(datetime1Norm));
		assertTrue(datetime1.equals(datetime2Norm));
	}

	
	public void testDatetimeEquals3() throws IOException {
		// all the same times
		String s1 = "2015-08-11T16:00:00-08:00";
		String s2 = "2015-08-11T24:00:00+00:00";
		String s3 = "2015-08-11T24:00:00Z"; // UTC
		String s4 = "2015-08-12T00:00:00Z";
		DateTimeType type = DateTimeType.dateTime;
		DateTimeValue datetime1 = DateTimeValue.parse(s1, type);
		DateTimeValue datetime2 = DateTimeValue.parse(s2, type);
		DateTimeValue datetime3 = DateTimeValue.parse(s3, type);
		DateTimeValue datetime4 = DateTimeValue.parse(s4, type);
		assertTrue(datetime1 != null);
		assertTrue(datetime2 != null);
		assertTrue(datetime3 != null);
		assertTrue(datetime4 != null);
		
		DateTimeValue datetime1Norm =  datetime1.normalize();
		DateTimeValue datetime2Norm =  datetime2.normalize();
		DateTimeValue datetime3Norm =  datetime3.normalize();
		DateTimeValue datetime4Norm =  datetime4.normalize();
		
		assertTrue(datetime1.equals(datetime2));
		assertTrue(datetime2.equals(datetime3));
		assertTrue(datetime3.equals(datetime4));
		assertTrue(datetime4.equals(datetime1));
		assertTrue(datetime1Norm.equals(datetime2));
		assertTrue(datetime2Norm.equals(datetime3));
		assertTrue(datetime3Norm.equals(datetime4));
		assertTrue(datetime4Norm.equals(datetime1));
	}
	
	public void testDatetimeEquals4() throws IOException {
		// all the same times
		String s1 = "2000-03-04T23:00:00+03:00";
		String s2 = "2000-03-04T20:00:00Z";
		DateTimeType type = DateTimeType.dateTime;
		DateTimeValue datetime1 = DateTimeValue.parse(s1, type);
		DateTimeValue datetime2 = DateTimeValue.parse(s2, type);
		assertTrue(datetime1 != null);
		assertTrue(datetime2 != null);
		
		DateTimeValue datetime1Norm =  datetime1.normalize();
		DateTimeValue datetime2Norm =  datetime2.normalize();
		
		assertTrue(datetime1.equals(datetime2));
		assertTrue(datetime2.equals(datetime1Norm));
		assertTrue(datetime2Norm.equals(datetime1Norm));
		assertTrue(datetime1.equals(datetime2Norm));
	}
	
	
	public void testDatetimeEquals5() throws IOException {
		// all the same times
		String s1 = "2012-02-28T20:00:00-08:00";
		String s2 = "2012-02-29T04:00:00";
		String s3 = "2012-03-01T01:00:00+21:00";
		DateTimeType type = DateTimeType.dateTime;
		DateTimeValue datetime1 = DateTimeValue.parse(s1, type);
		DateTimeValue datetime2 = DateTimeValue.parse(s2, type);
		DateTimeValue datetime3 = DateTimeValue.parse(s3, type);
		assertTrue(datetime1 != null);
		assertTrue(datetime2 != null);
		assertTrue(datetime3 != null);
		
		DateTimeValue datetime1Norm =  datetime1.normalize();
		DateTimeValue datetime2Norm =  datetime2.normalize();
		DateTimeValue datetime3Norm =  datetime3.normalize();
		
		assertTrue(datetime1.equals(datetime2));
		assertTrue(datetime1.equals(datetime3));
		assertTrue(datetime3.equals(datetime2));
		assertTrue(datetime2.equals(datetime1Norm));
		assertTrue(datetime2Norm.equals(datetime1Norm));
		assertTrue(datetime1.equals(datetime2Norm));
		assertTrue(datetime1.equals(datetime3Norm));
	}
	
	public void testDatetimeFail1() throws IOException {
		String s = "12:34:XXX";
		DateTimeType type = DateTimeType.time;
		DateTimeValue datetime = DateTimeValue.parse(s, type);
		assertFalse(datetime != null);
	}

}