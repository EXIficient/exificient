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

import java.util.List;

import com.siemens.ct.exi.Constants;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.5
 */

public class ListValue extends AbstractValue {
	
	protected final List<Value> values;
	
	public ListValue(List<Value> values) {
		this.values = values;
	}
	
	public List<Value> toValues() {
		return values;
	}

	public int getCharactersLength() {
		if (slen == -1) {
			slen = values.size() > 0 ? (values.size()-1) : 0;	// (n-1) delimiters
			int vlen = values.size();
			for(int i=0; i<vlen; i++) {
				slen += values.get(i).getCharactersLength();
			}
		}
		return slen;
	}
	
	public char[] toCharacters(char[] cbuffer, int offset) {		
		if (values.size() > 0 ) {
			//	fill buffer (except last item)
			char[] cres;
			Value iVal;
			int vlenMinus1 = values.size()-1;
			for(int i=0; i<vlenMinus1; i++) {
				iVal = values.get(i);
				cres = iVal.toCharacters(cbuffer, offset);
				if (cres != cbuffer) {
					// characters were NOT written directly to buffer
					//	"cres" contains characters --> copy
					copyCharacters(cres, cbuffer, offset);
				}
				offset += iVal.getCharactersLength();
				cbuffer[offset++] = Constants.XSD_LIST_DELIM_CHAR;
			}
			
			// last item (no delimiter)
			iVal = values.get(vlenMinus1);
			cres = iVal.toCharacters(cbuffer, offset);
			if (cres != cbuffer) {
				// characters were NOT written directly to buffer
				copyCharacters(cres, cbuffer, offset);
			}
		}
		
		return cbuffer;
	}
	
	private void copyCharacters(char[] src, char[] dest, int destOffset) {
		// characters were NOT written directly to buffer
		//	"cres" contains characters --> copy
		System.arraycopy(src, 0, dest, destOffset, src.length);
	}
	
	protected final boolean _equals(ListValue o) {
		if(values.size() == o.values.size()) {
			for(int i=0; i<values.size(); i++) {
				if (! values.get(i).equals(o.values.get(i))) {
					return false;
				}
			}
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof ListValue) {
			return _equals((ListValue)o);
		} else if (o instanceof String ) {
			// TODO list datatype
			return false;
		} else {
			return false;	
		}
	}

}
