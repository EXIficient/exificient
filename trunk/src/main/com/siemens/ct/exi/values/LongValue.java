/*
 * Copyright (C) 2007-2011 Siemens AG
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

import com.siemens.ct.exi.util.MethodsBag;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.6
 */

public class LongValue extends AbstractIntegerValue {

	private static final long serialVersionUID = -2574235168426128663L;
	
	protected final long val;

	public LongValue(long val) {
		this.val = val;
	}
	
	public static LongValue parse(String value) {
		try {
			value = getAdjustedValue(value);
			return new LongValue(Long.parseLong(value));
		} catch (NumberFormatException e) {
			return null;
		}
	}
	
	public long toLong() {
		return val;
	}
	
	public int getCharactersLength() {
		if ( slen == -1 ) {
			if (val == Long.MIN_VALUE) {
				slen = MethodsBag.LONG_MIN_VALUE_CHARARRAY.length;
			} else {
				slen = MethodsBag.getStringSize(val);
			}
		}
		return slen;
	}
	
	public char[] toCharacters(char[] cbuffer, int offset) {
		if (val == Long.MIN_VALUE) {
			return MethodsBag.LONG_MIN_VALUE_CHARARRAY;
		} else {
			assert (cbuffer.length >= getCharactersLength());
			MethodsBag.itos(val, offset + getCharactersLength(), cbuffer);
			return cbuffer;	
		}
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof LongValue) {
			return (val == ((LongValue)o).val);
		} else if (o instanceof String ) {
			LongValue l = LongValue.parse((String) o);
			if (l== null) {
				return false;
			} else {
				return (val == l.val);
			}
		} else {
			return false;	
		}
	}

}
