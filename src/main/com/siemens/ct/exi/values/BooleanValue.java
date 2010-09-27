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

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.5
 */

public class BooleanValue extends AbstractValue {

	private static final long serialVersionUID = -5198071608091328620L;

	protected final boolean bool;

	protected char[] characters;
	protected String sValue;

	public BooleanValue(boolean bool) {
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

	public static Boolean parse(String value) {
		value = value.trim();
		if (value.equals(Constants.XSD_BOOLEAN_0)
				|| value.equals(Constants.XSD_BOOLEAN_FALSE)) {
			return Boolean.FALSE;
		} else if (value.equals(Constants.XSD_BOOLEAN_1)
				|| value.equals(Constants.XSD_BOOLEAN_TRUE)) {
			return Boolean.TRUE;
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

	public char[] toCharacters(char[] cbuffer, int offset) {
		// return internal char buffer to indicate that this should be used
		return characters;
	}

	@Override
	public String toString() {
		return sValue;
	}

	@Override
	public String toString(char[] cbuffer, int offset) {
		return sValue;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof BooleanValue) {
			return (bool == ((BooleanValue) o).bool);
		} else if (o instanceof String) {
			Boolean b = BooleanValue.parse((String) o);
			if (b == null) {
				return false;
			} else {
				return (bool == b);
			}
		} else {
			return false;
		}
	}

}
