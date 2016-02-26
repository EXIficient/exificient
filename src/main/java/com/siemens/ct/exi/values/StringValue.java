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

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9.6-SNAPSHOT
 */

public class StringValue extends AbstractValue {

	protected char[] characters;
	protected String sValue;

	public StringValue(char[] ca) {
		super(ValueType.STRING);
		this.characters = ca;
	}

	public StringValue(String s) {
		super(ValueType.STRING);
		// this(s.toCharArray());
		sValue = s;
	}

	private void checkCharacters() {
		if (characters == null) {
			characters = sValue.toCharArray();
		}
	}

	public int getCharactersLength() {
		checkCharacters();
		return characters.length;
	}
	
	public char[] getCharacters() {
		checkCharacters();
		return characters;
	}

	public void getCharacters(char[] cbuffer, int offset) {
		checkCharacters();
		
		// not optimal, need to copy char data
		System.arraycopy(characters, 0, cbuffer, offset, characters.length);
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
		if (o == null) {
			return false;
		}
		return this == o ? true : toString().equals(o.toString());
	}
	
	@Override
	public int hashCode() {
		return toString().hashCode();
	}
	

}
