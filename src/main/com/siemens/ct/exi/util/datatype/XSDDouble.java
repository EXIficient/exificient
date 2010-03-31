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

import com.siemens.ct.exi.Constants;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Martin.Winter@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.4.20081117
 */

public class XSDDouble {

	public long mantissa;
	public long exponent;

	protected XSDDouble() {
	}

	public static XSDDouble newInstance() {
		return new XSDDouble();
	}

	public boolean parse(String s) {
		try {
			s = s.trim();
			if (s.length() == 0) {
				return false;
			} else if (s.equals(Constants.FLOAT_INFINITY)) {
				mantissa = Constants.FLOAT_MANTISSA_INFINITY;
				exponent = Constants.FLOAT_SPECIAL_VALUES;
			} else if (s.equals(Constants.FLOAT_MINUS_INFINITY)) {
				mantissa = Constants.FLOAT_MANTISSA_MINUS_INFINITY;
				exponent = Constants.FLOAT_SPECIAL_VALUES;
			} else if (s.equals(Constants.FLOAT_NOT_A_NUMBER)) {
				mantissa = Constants.FLOAT_MANTISSA_NOT_A_NUMBER;
				exponent = Constants.FLOAT_SPECIAL_VALUES;
			} else {
				char[] chars = s.toCharArray();

				int decimalDigits = 0;
				int len = chars.length;
				int pos = 0;
				mantissa = 0;
				exponent = 0;
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
						mantissa = 10 * mantissa;
					} else if (c > '0' && c <= '9') {
						mantissa = 10 * mantissa + (c - '0');
					} else {
						return false;
					}
				}

				// status: parsing mantissa after decimal point
				if (c == '.') {
					while (pos < len && (c = chars[pos++]) != 'e' && c != 'E') {
						if (c == '0') {
							mantissa = 10 * mantissa;
							decimalDigits++;
						} else if (c > '0' && c <= '9') {
							mantissa = 10 * mantissa + (c - '0');
							decimalDigits++;
						} else {
							return false;
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
							exponent = 10 * exponent + (c - '0');
						} else {
							return false;
						}
					}

					if (negativeExponent) {
						exponent = -exponent;
					}
				}

				// check whether whole string has been parsed successfully
				if (pos != len) {
					return false;
				}

				// adjust exponent and mantissa
				exponent -= decimalDigits;

				// overflow
				if ( mantissa < 0 ) {
					return false;
				}
						
				if (negative) {
					mantissa = -mantissa;
				}
				
				// too large ranges
				if ( mantissa < Constants.FLOAT_MANTISSA_MIN_RANGE
						|| mantissa > Constants.FLOAT_MANTISSA_MAX_RANGE
						|| exponent < Constants.FLOAT_EXPONENT_MIN_RANGE
						|| exponent > Constants.FLOAT_EXPONENT_MAX_RANGE) {
					return false;
				}

				// always encode zero as 0E0
				if (mantissa == 0) {
					exponent = 0;
				}
			}

			return true;
		} catch (Exception e) {
			// e.g. out-of-bound exception
			return false;
		}
	}
}
