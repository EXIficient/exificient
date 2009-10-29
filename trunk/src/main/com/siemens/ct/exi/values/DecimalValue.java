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

import java.math.BigDecimal;

import com.siemens.ct.exi.util.HugeInteger;
public class DecimalValue implements Value {
	
	protected final boolean negative;
	protected final HugeInteger integral;
	protected final HugeInteger revFractional;
	
	protected char[] characters;
	protected String sValue;
	
	public DecimalValue(boolean negative, HugeInteger integral, HugeInteger revFractional) {
		this.negative = negative;
		this.integral = integral;
		this.revFractional = revFractional;
	}
	
	public BigDecimal getBigDecimal() {
		return new BigDecimal(toCharacters());
	}

	public char[] toCharacters() {
		if (characters == null) {
			char[] caIntegral = integral.toCharacters();
			char[] caFractional = revFractional.toReverseCharacters();
			
			int aLen = (negative? 1 : 0) + caIntegral.length + 1 + caFractional.length;
			
			characters = new char[aLen];
			
			int cnt = 0;
			
			//	negative
			if (negative) {
				characters[cnt++] = '-';
			}
			//	integral
			System.arraycopy(caIntegral, 0, characters, cnt, caIntegral.length);
			cnt += caIntegral.length;
			//	dot
			characters[cnt++] = '.';
			//	fractional
			System.arraycopy(caFractional, 0, characters, cnt, caFractional.length);	
		}
		
		return characters;
	}
	
	public int getCharactersLength() {
		return toCharacters().length;
	}

	@Override
	public String toString() {
		if (sValue == null) {
			sValue = new String(toCharacters());
		}
		
		return sValue;
	}

}
