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

package com.siemens.ct.exi.util;

import java.math.BigInteger;

public final class HugeInteger {
	public final boolean isLongValue;
	public final BigInteger bigIntegerValue;
	public final long longValue;
	
	public HugeInteger(long l) {
		this.longValue = l;
		this.bigIntegerValue = null;
		this.isLongValue = true;
	}
	public HugeInteger(BigInteger bi) {
		this.bigIntegerValue = bi;
		this.longValue = -1;
		this.isLongValue = false;
	}
	
	public int getStringSize() {
		return (isLongValue ? MethodsBag.getStringSize(longValue) : bigIntegerValue.toString().length());
	}
	
	public char[] toCharacters() {
		return (isLongValue ? MethodsBag.itos(longValue) : MethodsBag.itos(bigIntegerValue));
	}
	
	public char[] toReverseCharacters() {
		return (isLongValue ? MethodsBag.itosReverse(longValue) :
			MethodsBag.itosReverse(bigIntegerValue));
	}

}
