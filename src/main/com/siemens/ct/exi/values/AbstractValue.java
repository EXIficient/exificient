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

public abstract class AbstractValue implements Value {
	
	private static final long serialVersionUID = 7617762856524097078L;
	
	protected int slen = -1;
	
	@Override
	public String toString() {
		char[] cbuffer = new char[getCharactersLength()];
		return new String(toCharacters(cbuffer, 0));
	}
	
	public String toString(char[] cbuffer, int offset) {
		return new String(toCharacters(cbuffer, offset), offset, getCharactersLength());
	}

}
