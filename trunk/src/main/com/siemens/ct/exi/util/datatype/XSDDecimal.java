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

import java.math.BigInteger;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.4.20081111
 */

public class XSDDecimal {
	private boolean negative;

	private BigInteger integral;
	private BigInteger revFractional;

	private XSDDecimal() {
	}

	public static XSDDecimal newInstance() {
		return new XSDDecimal();
	}

	public boolean isNegative() {
		return negative;
	}

	public BigInteger getIntegral() {
		return integral;
	}

	public BigInteger getReverseFractional() {
		return revFractional;
	}

	public boolean parse(String decimal) {
		try {
			decimal = decimal.trim();
			// --- handle sign
			negative = false; // default

			if (decimal.charAt(0) == '-') {
				negative = true;
				decimal = decimal.substring(1);
			} else if (decimal.charAt(0) == '+') {
				// sign = false;
				decimal = decimal.substring(1);
			}

			// --- handle decimal point
			final int decPoint = decimal.indexOf('.');

			if (decPoint == -1) {
				// no decimal point at all
				integral = new BigInteger(decimal);
				// integral.parse(decimal);
				revFractional = BigInteger.ZERO;
				// revFractional.setValue(0);
			} else {
				integral = new BigInteger(decimal.substring(0, decPoint));
				revFractional = new BigInteger(new StringBuilder(decimal
						.substring(decPoint + 1, decimal.length())).reverse()
						.toString());
			}
			return true;
		} catch (Exception e) {
			// throw new XMLParsingException(e.getMessage());
			return false;
		}

	}
}
