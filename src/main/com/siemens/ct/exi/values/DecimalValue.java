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

package com.siemens.ct.exi.values;

import java.math.BigDecimal;

import com.siemens.ct.exi.util.MethodsBag;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.8
 */

public class DecimalValue extends AbstractValue {

	private static final long serialVersionUID = 5268045994978250547L;

	public final boolean negative;
	public final IntegerValue integral;
	public final IntegerValue revFractional;

	protected BigDecimal bd;

	public DecimalValue(boolean negative, IntegerValue integral,
			IntegerValue revFractional) {
		super(ValueType.DECIMAL);
		this.negative = negative;
		this.integral = integral;
		this.revFractional = revFractional;
	}

	public static DecimalValue parse(String decimal) {
		try {
			boolean sNegative;
			IntegerValue sIntegral, sRevFractional;
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
				sIntegral = IntegerValue.parse(decimal);
				// integral.parse(decimal);
				sRevFractional = IntegerValue.ZERO;
				// revFractional.setValue(0);
			} else if (decPoint == 0) {
				// e.g. ".234"
				sIntegral = IntegerValue.ZERO;
				sRevFractional = IntegerValue.parse(new StringBuilder(decimal
						.substring(decPoint + 1, decimal.length())).reverse()
						.toString());
			} else {
				sIntegral = IntegerValue.parse(decimal.substring(0, decPoint));
				sRevFractional = IntegerValue.parse(new StringBuilder(decimal
						.substring(decPoint + 1, decimal.length())).reverse()
						.toString());
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
		switch (revFractional.valueType) {
		case INTEGER_INT:
			MethodsBag.itosReverse(revFractional.ival, offset, cbuffer);
			break;
		case INTEGER_LONG:
			MethodsBag.itosReverse(revFractional.lval, offset, cbuffer);
			break;
		case INTEGER_BIG:
			// TODO look for a more suitable way, big integer
			StringBuilder sb = new StringBuilder(revFractional.bval.toString());
			char[] bi = sb.reverse().toString().toCharArray();
			System.arraycopy(bi, 0, cbuffer, offset, bi.length);
			break;
		default:
			/* ERROR */
			throw new RuntimeException("Unknown Int Type: "
					+ revFractional.valueType);
		}
		// revFractional.toCharactersReverse(cbuffer, offset);

		return cbuffer;
	}
	
	private final boolean _equals(DecimalValue o) {
		return (negative == o.negative && integral.equals(o.integral) && revFractional
				.equals(o.revFractional));
	}

	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		if (o instanceof DecimalValue) {
			return _equals((DecimalValue) o);
		} else {
			DecimalValue dv = DecimalValue.parse(o.toString());
			return dv == null ? false : _equals(dv);
		}
	}
	
	@Override
	public int hashCode() {
		return (negative ? 1 : 0) ^ integral.hashCode() ^ revFractional.hashCode();
	}

}
