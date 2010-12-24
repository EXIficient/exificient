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

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.6
 */

public class StringValue extends AbstractValue {
	
	private static final long serialVersionUID = -2583604220181066337L;
	
	protected char[] characters;
	protected String sValue;
	
	public StringValue(char[] ca) {
		this.characters = ca;
	}
	
	public StringValue(String s) {
		this(s.toCharArray());
		sValue = s;
	}
	
	public int getCharactersLength() {
		return characters.length;
	}
	
	public char[] toCharacters(char[] cbuffer, int offset) {
		// return internal char buffer to indicate that this should be used
		return this.characters;
	}
	
	@Override
	public String toString() {
		if (sValue == null) {
			sValue = new String(characters);
		}
		return sValue;
	}
	
	@Override
	public String toString(char[] cbuffer, int offset) {
		return toString();
	}
	
	@Override
	public boolean equals(Object o) {
		return toString().equals(o.toString());
	}
	
	
}
