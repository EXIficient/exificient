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

package com.siemens.ct.exi.values;

import java.math.BigDecimal;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.5
 */

public class DecimalValue extends AbstractValue {

	private static final long serialVersionUID = 5268045994978250547L;
	
	public final boolean negative;
	public final HugeIntegerValue integral;
	public final HugeIntegerValue revFractional;

	protected BigDecimal bd;

	public DecimalValue(boolean negative, HugeIntegerValue integral,
			HugeIntegerValue revFractional) {
		this.negative = negative;
		this.integral = integral;
		this.revFractional = revFractional;
	}

	public static DecimalValue parse(String decimal) {
		try {
			boolean sNegative;
			HugeIntegerValue sIntegral, sRevFractional;
			decimal = decimal.trim();
			// --- handle sign
			sNegative = false; // default

			if (decimal.charAt(0) == '-') {
				sNegative = true;
				decimal = decimal.substring(1);
			} else if (decimal.charAt(0) == '+') {
				// sign = false;
				decimal = decimal.substring(1);
			}

			// --- handle decimal point
			final int decPoint = decimal.indexOf('.');

			if (decPoint == -1) {
				// no decimal point at all
				sIntegral = HugeIntegerValue.parse(decimal);
				// integral.parse(decimal);
				sRevFractional = HugeIntegerValue.ZERO;
				// revFractional.setValue(0);
			} else if (decPoint == 0) {
				// e.g. ".234"
				sIntegral = HugeIntegerValue.ZERO;
				sRevFractional = HugeIntegerValue.parse(new StringBuilder(
						decimal.substring(decPoint + 1, decimal.length()))
						.reverse().toString());
			} else {
				sIntegral = HugeIntegerValue.parse(decimal.substring(0,
						decPoint));
				sRevFractional = HugeIntegerValue.parse(new StringBuilder(
						decimal.substring(decPoint + 1, decimal.length()))
						.reverse().toString());
			}
			if (sIntegral == null || sRevFractional == null) {
				return null;
			} else {
				return new DecimalValue(sNegative, sIntegral, sRevFractional);
			}
		} catch (Exception e) {
			return null;
		}

	}

	public BigDecimal toBigDecimal() {
		if (bd == null) {
			char[] characters = new char[getCharactersLength()];
			toCharacters(characters, 0);
			bd = new BigDecimal(characters);
		}
		return bd;
	}

	public int getCharactersLength() {
		if (slen == -1) {
			// +12.34
			slen = (negative ? 1 : 0) + integral.getCharactersLength() + 1
					+ revFractional.getCharactersLength();
		}
		return slen;
	}

	public char[] toCharacters(char[] cbuffer, int offset) {
		// negative
		if (negative) {
			cbuffer[offset++] = '-';
		}
		// integral
		integral.toCharacters(cbuffer, offset);
		offset += integral.getCharactersLength();
		// dot
		cbuffer[offset++] = '.';
		// fractional
		revFractional.toCharactersReverse(cbuffer, offset);

		return cbuffer;
	}

	protected final boolean _equals(DecimalValue o) {
		return (negative == o.negative && integral._equals(o.integral) && revFractional
				._equals(o.revFractional));
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof DecimalValue) {
			return _equals((DecimalValue) o);
		} else if (o instanceof String) {
			DecimalValue d = DecimalValue.parse((String) o);
			if (d == null) {
				return false;
			} else {
				return _equals(d);	
			}
		} else {
			return false;
		}
	}

}
