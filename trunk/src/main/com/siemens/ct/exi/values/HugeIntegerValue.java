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
	
	public int getCharactersLength() {
		if (slen == -1 )  {
			slen = isLongValue ? MethodsBag.getStringSize(longValue) : bigIntegerValue.toString().length();
		}
		return slen;
	}
	
	public char[] toCharacters(char[] cbuffer, int offset) {
		if (isLongValue) {
			MethodsBag.itos(longValue, getCharactersLength()+offset, cbuffer);
		} else {
			//	TODO look for a more suitable way, big integer
			char[] bi = bigIntegerValue.toString().toCharArray();
			System.arraycopy(bi, 0, cbuffer, offset, bi.length);
		}
		
		return cbuffer;
	}
	
	public void toCharactersReverse(char[] cbuffer, int offset) {
		if (isLongValue) {
			MethodsBag.itosReverse(longValue, offset, cbuffer);
		} else {
			//	TODO look for a more suitable way, big integer
			StringBuilder sb = new StringBuilder(bigIntegerValue.toString());
			char[] bi = sb.reverse().toString().toCharArray();
			System.arraycopy(bi, 0, cbuffer, offset, bi.length);
		}
	}
	
	@Override
	public String toString() {
		if (isLongValue) {
			return super.toString();
		} else {
			return bigIntegerValue.toString();
		}
	}
	
	@Override
	public String toString(char[] cbuffer, int offset) {
		if (isLongValue) {
			return super.toString(cbuffer, offset);
		} else {
			return bigIntegerValue.toString();
		}
	}

}
