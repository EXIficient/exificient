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

abstract public class AbstractBinaryValue extends AbstractValue {

	protected final byte[] bytes;
	protected String sValue;

	public AbstractBinaryValue(byte[] bytes) {
		this.bytes = bytes;
	}
	
	public byte[] toBytes() {
		return bytes;
	}
	
	abstract protected void init();
	
	public int getCharactersLength() {
		if (slen == -1) {
			init();
		}
		return slen;
	}
	
	public char[] toCharacters(char[] cbuffer, int offset) {
		return sValue.toCharArray();
	}
	
	@Override
	public String toString() {
		if (slen == -1) {
			init();
		}
		return sValue;
	}

}
