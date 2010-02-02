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

public class DecimalValue extends AbstractValue {

	protected final boolean negative;
	protected final HugeIntegerValue integral;
	protected final HugeIntegerValue revFractional;

	protected BigDecimal bd;
	
	public DecimalValue(boolean negative, HugeIntegerValue integral,
			HugeIntegerValue revFractional) {
		this.negative = negative;
		this.integral = integral;
		this.revFractional = revFractional;
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
			slen = (negative ? 1 : 0) + integral.getCharactersLength() + 1 + revFractional.getCharactersLength();
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

}
