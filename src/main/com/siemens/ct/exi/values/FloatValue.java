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

package com.siemens.ct.exi.values;

import com.siemens.ct.exi.Constants;
import com.siemens.ct.exi.util.MethodsBag;

public class FloatValue extends AbstractValue {

	protected final int iMantissa;
	protected final int iExponent;

	protected Float f;

	public FloatValue(int iMantissa, int iExponent) {
		this.iMantissa = iMantissa;
		this.iExponent = iExponent;
	}
	
	public float toFloat() {
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

	public char[] toCharacters() {
		if (characters == null) {
			if (iExponent == Constants.FLOAT_SPECIAL_VALUES) {
				if (iMantissa == -1) {
					characters = Constants.FLOAT_MINUS_INFINITY_CHARARRAY;
				} else if (iMantissa == 1) {
					characters = Constants.FLOAT_INFINITY_CHARARRAY;
				} else {
					characters = Constants.FLOAT_NOT_A_NUMBER_CHARARRAY;
				}
			} else {
				// return iMantissa + "E" + iExponent;
				int sizeMantissa = MethodsBag.getStringSize(iMantissa);
				int stringSize = sizeMantissa + 1 + MethodsBag.getStringSize(iExponent);
				
				characters = new char[stringSize];
				
				MethodsBag.itos(iExponent, stringSize, characters);
				characters[sizeMantissa] = 'E';
				MethodsBag.itos(iMantissa, sizeMantissa, characters);
			}
		}
		return characters;
	}

}
