/*
 * Copyright (C) 2007-2015 Siemens AG
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
 * @version 0.9.5-SNAPSHOT
 */

public class DecimalValue extends AbstractValue {

	private static final long serialVersionUID = 5268045994978250547L;

	protected final boolean negative;
	protected final IntegerValue integral;
	protected final IntegerValue revFractional;
	
	protected BigDecimal bd;
	
	/* Helper for building strings */
	protected StringBuilder sbHelper;

	public DecimalValue(boolean negative, IntegerValue integral,
			IntegerValue revFractional) {
		super(ValueType.DECIMAL);
		this.negative = negative;
		this.integral = integral;
		this.revFractional = revFractional;
	}
	
	public boolean isNegative() {
		return negative;
	}

	public IntegerValue getIntegral() {
		return integral;
	}
	
	public IntegerValue getRevFractional() {
		return revFractional;
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
			getCharacters(characters, 0);
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

	public void getCharacters(char[] cbuffer, int offset) {
		// negative
		if (negative) {
			cbuffer[offset++] = '-';
		}
		// integral
		integral.getCharacters(cbuffer, offset);
		offset += integral.getCharactersLength();
		// dot
		cbuffer[offset++] = '.';
		// fractional
		switch (revFractional.getIntegerValueType()) {
		case INT:
			MethodsBag.itosReverse(revFractional.ival, offset, cbuffer);
			break;
		case LONG:
			MethodsBag.itosReverse(revFractional.lval, offset, cbuffer);
			break;
		case BIG:
			// TODO look for a more suitable way, big integer
			if(sbHelper == null) {
				sbHelper = new StringBuilder(revFractional.bval.toString());
			} else {
				sbHelper.setLength(0);
				sbHelper.append(revFractional.bval.toString());
			}
			sbHelper = sbHelper.reverse();
			
			int len = sbHelper.length();
			sbHelper.getChars(0, len, cbuffer, offset);
			
			break;
		default:
			/* ERROR */
			throw new RuntimeException("Unknown Int Type: "
					+ revFractional.valueType);
		}

		// return cbuffer;
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
