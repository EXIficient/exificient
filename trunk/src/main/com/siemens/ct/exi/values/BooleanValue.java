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

import com.siemens.ct.exi.Constants;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9
 */

public class BooleanValue extends AbstractValue {

	private static final long serialVersionUID = -5198071608091328620L;

	private static final BooleanValue BOOLEAN_VALUE_FALSE = new BooleanValue(
			false);
	private static final BooleanValue BOOLEAN_VALUE_TRUE = new BooleanValue(
			true);

	protected final boolean bool;

	protected final char[] characters;
	protected final String sValue;

	public BooleanValue(boolean bool) {
		super(ValueType.BOOLEAN);
		this.bool = bool;
		if (bool) {
			characters = Constants.DECODED_BOOLEAN_TRUE_ARRAY;
			sValue = Constants.DECODED_BOOLEAN_TRUE;
		} else {
			characters = Constants.DECODED_BOOLEAN_FALSE_ARRAY;
			sValue = Constants.DECODED_BOOLEAN_FALSE;
		}
	}

	public BooleanValue(int boolID) {
		super(ValueType.BOOLEAN);
		switch (boolID) {
		case 0:
			characters = Constants.XSD_BOOLEAN_FALSE_ARRAY;
			sValue = Constants.XSD_BOOLEAN_FALSE;
			bool = false;
			break;
		case 1:
			characters = Constants.XSD_BOOLEAN_0_ARRAY;
			sValue = Constants.XSD_BOOLEAN_0;
			bool = false;
			break;
		case 2:
			characters = Constants.XSD_BOOLEAN_TRUE_ARRAY;
			sValue = Constants.XSD_BOOLEAN_TRUE;
			bool = true;
			break;
		case 3:
			characters = Constants.XSD_BOOLEAN_1_ARRAY;
			sValue = Constants.XSD_BOOLEAN_1;
			bool = true;
			break;
		default:
			throw new RuntimeException(
					"Error while decoding boolean pattern facet");
		}
	}

	public static BooleanValue parse(String value) {
		value = value.trim();
		if (value.equals(Constants.XSD_BOOLEAN_0)
				|| value.equals(Constants.XSD_BOOLEAN_FALSE)) {
			return BOOLEAN_VALUE_FALSE;
		} else if (value.equals(Constants.XSD_BOOLEAN_1)
				|| value.equals(Constants.XSD_BOOLEAN_TRUE)) {
			return BOOLEAN_VALUE_TRUE;
		} else {
			return null;
		}
	}

	public boolean toBoolean() {
		return bool;
	}

	public int getCharactersLength() {
		return characters.length;
	}
	
	public char[] getCharacters() {
		return characters;
	}

	public void getCharacters(char[] cbuffer, int offset) {
		// not optimal, need to copy char data
		System.arraycopy(characters, 0, cbuffer, offset, characters.length);
	}
	
	@Override
	public String toString() {
		return sValue;
	}

	@Override
	public String toString(char[] cbuffer, int offset) {
		return sValue;
	}

	
	private final boolean _equals(BooleanValue o) {
		return (bool == ((BooleanValue) o).bool);
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		if (o instanceof BooleanValue) {
			return _equals((BooleanValue) o);
		} else {
			BooleanValue bv = BooleanValue.parse(o.toString());
			return bv == null ? false : _equals(bv);
		}
	}
	
	@Override
	public int hashCode() {
		return bool ? 1 : 0;
	}

}
