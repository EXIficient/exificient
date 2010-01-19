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

public class DoubleValue extends AbstractValue {

	protected final long lMantissa;
	protected final long lExponent;

	protected Double d;

	public DoubleValue(long lMantissa, long lExponent) {
		this.lMantissa = lMantissa;
		this.lExponent = lExponent;
	}
	
	public double toDouble() {
		if (d == null)  {
			if (lExponent == Constants.FLOAT_SPECIAL_VALUES) {
				if (lMantissa == -1L) {
					d = Double.NEGATIVE_INFINITY;
				} else if (lMantissa == 1) {
					d = Double.POSITIVE_INFINITY;
				} else {
					d =  Double.NaN;
				}
			} else {
				d = lMantissa * (double)(Math.pow(10, lExponent));
			}	
		}
		return d;
	}

	public char[] toCharacters() {
		if (characters == null) {
			if (lExponent == Constants.FLOAT_SPECIAL_VALUES) {
				if (lMantissa == -1) {
					characters = Constants.FLOAT_MINUS_INFINITY_CHARARRAY;
				} else if (lMantissa == 1) {
					characters = Constants.FLOAT_INFINITY_CHARARRAY;
				} else {
					characters = Constants.FLOAT_NOT_A_NUMBER_CHARARRAY;
				}
			} else {
				// return iMantissa + "E" + iExponent;
				int sizeMantissa = MethodsBag.getStringSize(lMantissa);
				int stringSize = sizeMantissa + 1 + MethodsBag.getStringSize(lExponent);
				
				characters = new char[stringSize];
				
				MethodsBag.itos(lExponent, stringSize, characters);
				characters[sizeMantissa] = 'E';
				MethodsBag.itos(lMantissa, sizeMantissa, characters);
			}
		}

		return characters;
	}

}
