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

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.6
 */

abstract public class AbstractBinaryValue extends AbstractValue {

	private static final long serialVersionUID = -7022141926130631608L;

	protected final byte[] bytes;
	protected String sValue;

	public AbstractBinaryValue(ValueType valueType, byte[] bytes) {
		super(valueType);
		this.bytes = bytes;
	}

	public byte[] toBytes() {
		return bytes;
	}

	// abstract protected void initString();
	//
	// public int getCharactersLength() {
	// if (slen == -1) {
	// initString();
	// }
	// return slen;
	// }

	// public char[] toCharacters(char[] cbuffer, int offset) {
	// return sValue.toCharArray();
	// }

	protected final boolean _equals(byte[] oBytes) {
		if (bytes.length == oBytes.length) {
			for (int i = 0; i < bytes.length; i++) {
				if (bytes[i] != oBytes[i]) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	// @Override
	// public String toString() {
	// if (slen == -1) {
	// initString();
	// }
	// return sValue;
	// }

}
