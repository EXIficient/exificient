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

package com.siemens.ct.exi.values;

import java.math.BigDecimal;

import com.siemens.ct.exi.util.MethodsBag;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9.6-SNAPSHOT
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
		// normalize "-0.0" to "0.0"
		if (negative && IntegerValue.ZERO.equals(integral) && IntegerValue.ZERO.equals(revFractional) ) {
			negative = false;
		}
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
