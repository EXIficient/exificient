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

package com.siemens.ct.exi.util.datatype;

import com.siemens.ct.exi.exceptions.XMLParsingException;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Martin.Winter@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.3.20081117
 */

public class XSDFloat {
	public static final long FLOAT_SPECIAL_VALUES = -16384; // -(2^14)
	public static final long MANTISSA_INFINITY = 1;
	public static final long MANTISSA_MINUS_INFINITY = -1;
	public static final long MANTISSA_NOT_A_NUMBER = 0;

	public long iMantissa;
	public long iExponent;

	private XSDFloat() {
	}

	public static XSDFloat newInstance() {
		return new XSDFloat();
	}

	public void parse(String s) throws XMLParsingException {
		if (s.length() == 0) {
			throw new XMLParsingException("Empty string while parsing float");
		} else if (s.equals("INF")) {
			iMantissa = MANTISSA_INFINITY;
			iExponent = FLOAT_SPECIAL_VALUES;
		} else if (s.equals("-INF")) {
			iMantissa = MANTISSA_MINUS_INFINITY;
			iExponent = FLOAT_SPECIAL_VALUES;
		} else if (s.equals("NaN")) {
			iMantissa = MANTISSA_NOT_A_NUMBER;
			iExponent = FLOAT_SPECIAL_VALUES;
		} else {
			char[] chars = s.toCharArray();

			int decimalDigits = 0;
			int len = chars.length;
			int pos = 0;
			iMantissa = 0;
			iExponent = 0;
			char c;
			boolean negative = false;
			boolean negativeExponent = false;

			// status: detecting sign
			if ((c = chars[pos]) == '+') {
				pos++;
			} else if (c == '-') {
				negative = true;
				pos++;
			}

			// status: parsing mantissa before decimal point
			while (pos < len && (c = chars[pos++]) != '.' && c != 'e'
					&& c != 'E') {
				if (c == '0') {
					iMantissa = 10 * iMantissa;
				} else if (c > '0' && c <= '9') {
					iMantissa = 10 * iMantissa + (c - '0');
				} else {
					throw new XMLParsingException(
							"Illegal character while parsing float: " + c
									+ " at pos " + (pos - 1));
				}
			}

			// status: parsing mantissa after decimal point
			if (c == '.') {
				while (pos < len && (c = chars[pos++]) != 'e' && c != 'E') {
					if (c == '0') {
						iMantissa = 10 * iMantissa;
						decimalDigits++;
					} else if (c > '0' && c <= '9') {
						iMantissa = 10 * iMantissa + (c - '0');
						decimalDigits++;
					} else {
						throw new XMLParsingException(
								"Illegal character while parsing float: " + c
										+ " at pos " + (pos - 1));
					}
				}
			}

			// status: parsing exponent after e or E
			if (c == 'e' || c == 'E') {
				// status: checking sign of exponent
				if ((c = chars[pos]) == '-') {
					negativeExponent = true;
					pos++;
				} else if (c == '+') {
					pos++;
				}

				while (pos < len) {
					// c = s.charAt ( pos++ );
					c = chars[pos++];

					if (c >= '0' && c <= '9') {
						iExponent = 10 * iExponent + (c - '0');
					} else {
						throw new XMLParsingException(
								"Illegal character while parsing float: " + c
										+ " at pos " + (pos - 1));
					}
				}

				if (negativeExponent) {
					iExponent = -iExponent;
				}
			}

			// check whether whole string is parsed successfully
			if (pos != len) {
				throw new XMLParsingException(
						"Illegal character while parsing float: " + c
								+ " at pos " + (pos - 1));
			}

			// adjust exponent and mantissa
			iExponent -= decimalDigits;

			if (negative) {
				iMantissa = -iMantissa;
			}

			// always encode zero as 0E0
			if (iMantissa == 0) {
				iExponent = 0;
			}
		}
	}
}
