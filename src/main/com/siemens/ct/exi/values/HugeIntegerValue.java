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

import java.math.BigInteger;

import com.siemens.ct.exi.util.MethodsBag;

public final class HugeIntegerValue extends AbstractValue {
	
	public final boolean isLongValue;
	public final long longValue;
	public final BigInteger bigIntegerValue;
	
	public HugeIntegerValue(long l) {
		this.longValue = l;
		this.bigIntegerValue = null;
		this.isLongValue = true;
	}
	public HugeIntegerValue(BigInteger bi) {
		this.bigIntegerValue = bi;
		this.longValue = -1;
		this.isLongValue = false;
	}
	
	public char[] toCharacters() {
		if (characters == null) {
			characters = isLongValue ? MethodsBag.itos(longValue) : MethodsBag.itos(bigIntegerValue);
		}
		return characters;
	}
	
	public char[] toReverseCharacters() {
		return (isLongValue ? MethodsBag.itosReverse(longValue) :
			MethodsBag.itosReverse(bigIntegerValue));
	}

}
