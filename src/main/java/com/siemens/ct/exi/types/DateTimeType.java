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

package com.siemens.ct.exi.types;

/**
 * The Date-Time datatype representation is a sequence of values representing
 * the individual components of the Date-Time
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9.6-SNAPSHOT
 */

public enum DateTimeType {
	/** gYear represents a gregorian calendar year */
	gYear,

	/**
	 * gYearMonth represents a specific gregorian month in a specific gregorian
	 * year
	 */
	gYearMonth,

	/**
	 * A date is an object with year, month, and day properties just like those
	 * of dateTime objects, plus an optional timezone-valued timezone property
	 */
	date,
	/**
	 * dateTime values may be viewed as objects with integer-valued year, month,
	 * day, hour and minute properties, a decimal-valued second property, and a
	 * boolean timezoned property.
	 */
	dateTime,

	/** gMonth is a gregorian month that recurs every year */
	gMonth,

	/**
	 * gMonthDay is a gregorian date that recurs, specifically a day of the year
	 * such as the third of May
	 */
	gMonthDay,

	/**
	 * gDay is a gregorian day that recurs, specifically a day of the month such
	 * as the 5th of the month
	 */
	gDay,

	/** time represents an instant of time that recurs every day */
	time;
}
