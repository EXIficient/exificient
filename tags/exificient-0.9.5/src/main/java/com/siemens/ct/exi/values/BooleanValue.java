/*
 * Copyright (c) 2007-2015 Siemens AG
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * 
 */

package com.siemens.ct.exi.values;

import com.siemens.ct.exi.Constants;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9.5
 */

public class BooleanValue extends AbstractValue {

	private static final long serialVersionUID = -5198071608091328620L;

	public static final BooleanValue BOOLEAN_VALUE_FALSE = new BooleanValue(
			false);
	public static final BooleanValue BOOLEAN_VALUE_TRUE = new BooleanValue(
			true);
	
	public static final BooleanValue BOOLEAN_VALUE_0 = new BooleanValue(0);
	public static final BooleanValue BOOLEAN_VALUE_1 = new BooleanValue(1);
	public static final BooleanValue BOOLEAN_VALUE_2 = new BooleanValue(2);
	public static final BooleanValue BOOLEAN_VALUE_3 = new BooleanValue(3);

	protected final boolean bool;

	protected final char[] characters;
	protected final String sValue;

	private BooleanValue(boolean bool) {
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
	
	public static BooleanValue getBooleanValue(boolean bool) {
		return bool ? BooleanValue.BOOLEAN_VALUE_TRUE : BooleanValue.BOOLEAN_VALUE_FALSE;
	}

	private BooleanValue(int boolID) {
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
					"Error while creating boolean pattern facet with boolID==" + boolID);
		}
	}
	
	public static BooleanValue getBooleanValue(int boolID) {
		BooleanValue bv;
		switch (boolID) {
		case 0:
			bv = BooleanValue.BOOLEAN_VALUE_0;
			break;
		case 1:
			bv = BooleanValue.BOOLEAN_VALUE_1;
			break;
		case 2:
			bv = BooleanValue.BOOLEAN_VALUE_2;
			break;
		case 3:
			bv = BooleanValue.BOOLEAN_VALUE_3;
			break;
		default:
			throw new RuntimeException(
					"Error while creating boolean pattern facet with boolID==" + boolID);
		}
		
		return bv;
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
