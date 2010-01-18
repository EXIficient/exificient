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

public class ListValue extends AbstractValue {
	
	protected final Value[] values;
	
	public ListValue(Value[] values) {
		this.values = values;
	}
	
	public Value[] toValues() {
		return values;
	}

	public char[] toCharacters() {
		if (characters == null) {
			//	TODO instead of copying twice the chars find a better way, e.g., toCharacters(array) 
			//	calculate size
			int size = values.length > 0 ? (values.length-1) : 0;	// (n-1) delimiters
			for(int i=0; i<values.length; i++) {
				size += values[i].getCharactersLength();
			}
			//	create array
			characters = new char[size];
			if (values.length > 0 ) {
				//	fill array
				int caIndex = 0;
				for(int i=0; i<(values.length-1); i++) {
					char[] itemValue = values[i].toCharacters();
					System.arraycopy(itemValue, 0, characters, caIndex, itemValue.length);
					caIndex += itemValue.length;
					characters[caIndex++] = Constants.XSD_LIST_DELIM_CHAR;
				}
				char[] lastItemValue = values[values.length-1].toCharacters();
				System.arraycopy(lastItemValue, 0, characters, caIndex, lastItemValue.length);	
			}
		}
		
		return characters;
	}

}
