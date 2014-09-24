/*
 * Copyright (C) 2007-2014 Siemens AG
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
 * @version 0.9.3
 */

public abstract class AbstractValue implements Value {

	private static final long serialVersionUID = 7617762856524097078L;

	protected int slen = -1;

	protected final ValueType valueType;

	public AbstractValue(ValueType valueType) {
		this.valueType = valueType;
	}

	public final ValueType getValueType() {
		return valueType;
	}
	
	public char[] getCharacters() {
		char[] dst = new char[getCharactersLength()];
		getCharacters(dst, 0);
		return dst;
	}

	@Override
	public String toString() {
		return new String(getCharacters());
	}

	public String toString(char[] cbuffer, int offset) {
		getCharacters(cbuffer, offset);
		return new String(cbuffer, offset,
				getCharactersLength());
	}

}
