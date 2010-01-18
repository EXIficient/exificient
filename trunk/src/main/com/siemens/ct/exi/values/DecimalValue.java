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
			bd = new BigDecimal(toCharacters());
		}
		return bd;
	}

	public char[] toCharacters() {
		if (characters == null) {
			char[] caIntegral = integral.toCharacters();
			char[] caFractional = revFractional.toReverseCharacters();

			int aLen = (negative ? 1 : 0) + caIntegral.length + 1
					+ caFractional.length;

			characters = new char[aLen];

			int cnt = 0;

			// negative
			if (negative) {
				characters[cnt++] = '-';
			}
			// integral
			System.arraycopy(caIntegral, 0, characters, cnt, caIntegral.length);
			cnt += caIntegral.length;
			// dot
			characters[cnt++] = '.';
			// fractional
			System.arraycopy(caFractional, 0, characters, cnt,
					caFractional.length);
		}

		return characters;
	}

}
