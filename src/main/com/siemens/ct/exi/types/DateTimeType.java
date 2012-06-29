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

package com.siemens.ct.exi.types;

/**
 * The Date-Time datatype representation is a sequence of values representing
 * the individual components of the Date-Time
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9
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
