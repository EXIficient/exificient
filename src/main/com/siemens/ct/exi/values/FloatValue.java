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

import com.siemens.ct.exi.Constants;
import com.siemens.ct.exi.util.MethodsBag;

public class FloatValue extends AbstractValue {

	protected final int iMantissa;
	protected final int iExponent;

	protected int slenMantissa = -1;
	
	protected Float f;

	public FloatValue(int iMantissa, int iExponent) {
		this.iMantissa = iMantissa;
		this.iExponent = iExponent;
	}
	
	public Float toFloat() {
		if (f == null)  {
			if (iExponent == Constants.FLOAT_SPECIAL_VALUES) {
				if (iMantissa == -1) {
					f = Float.NEGATIVE_INFINITY;
				} else if (iMantissa == 1) {
					f = Float.POSITIVE_INFINITY;
				} else {
					f = Float.NaN;
				}
			} else {
				f = iMantissa * (float)(Math.pow(10, iExponent));
			}
		}
		return f;
	}
	
	public int getCharactersLength() {
		if (slen == -1) {
			if (iExponent == Constants.FLOAT_SPECIAL_VALUES) {
				if (iMantissa == -1) {
					slen = Constants.FLOAT_MINUS_INFINITY_CHARARRAY.length;
				} else if (iMantissa == 1) {
					slen = Constants.FLOAT_INFINITY_CHARARRAY.length;
				} else {
					slen = Constants.FLOAT_NOT_A_NUMBER_CHARARRAY.length;
				}
			} else {
				// iMantissa + "E" + iExponent;
				slenMantissa = MethodsBag.getStringSize(iMantissa);
				slen = slenMantissa + 1 + MethodsBag.getStringSize(iExponent);	
			}
		}
		return slen;
	}
	
	public char[] toCharacters(char[] cbuffer, int offset) {
		if (iExponent == Constants.FLOAT_SPECIAL_VALUES) {
			if (iMantissa == -1) {
				return Constants.FLOAT_MINUS_INFINITY_CHARARRAY;
			} else if (iMantissa == 1) {
				return Constants.FLOAT_INFINITY_CHARARRAY;
			} else {
				return Constants.FLOAT_NOT_A_NUMBER_CHARARRAY;
			}
		} else {
			MethodsBag.itos(iExponent, getCharactersLength(), cbuffer);
			cbuffer[slenMantissa] = 'E';
			MethodsBag.itos(iMantissa, slenMantissa, cbuffer);
			
			return cbuffer;			
		}
	}
	
	@Override
	public String toString() {
		if (iExponent == Constants.FLOAT_SPECIAL_VALUES) {
			if (iMantissa == -1) {
				return Constants.FLOAT_MINUS_INFINITY;
			} else if (iMantissa == 1) {
				return Constants.FLOAT_INFINITY;
			} else {
				return Constants.FLOAT_NOT_A_NUMBER;
			}
		} else {
			char[] cbuffer = new char[getCharactersLength()];
			return new String(toCharacters(cbuffer, 0));	
		}
	}
	
	@Override
	public String toString(char[] cbuffer, int offset) {
		if (iExponent == Constants.FLOAT_SPECIAL_VALUES) {
			if (iMantissa == -1) {
				return Constants.FLOAT_MINUS_INFINITY;
			} else if (iMantissa == 1) {
				return Constants.FLOAT_INFINITY;
			} else {
				return Constants.FLOAT_NOT_A_NUMBER;
			}
		} else {
			return super.toString(cbuffer, offset);
		}
	}

}
