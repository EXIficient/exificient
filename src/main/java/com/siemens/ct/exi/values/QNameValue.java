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

public class QNameValue extends AbstractValue {

	protected final String namespaceUri;
	protected final String localName;
	protected final String prefix;

	protected char[] characters;
	protected final String sValue;
	
	public QNameValue(String namespaceUri, String localName, String prefix) {
		super(ValueType.QNAME);
		this.namespaceUri = namespaceUri;
		this.localName = localName;
		this.prefix = prefix;

		if (prefix == null || prefix.length() == 0) {
			sValue = localName;
		} else {
			sValue = prefix + ":" + localName; 
		}
	}

	public String getNamespaceUri() {
		return this.namespaceUri;
	}
	
	public String getLocalName() {
		return this.localName;
	}
	
	public String getPrefix() {
		return prefix;
	}

	public int getCharactersLength() {
		return sValue.length();
	}


	public char[] getCharacters() {
		if(characters == null) {
			int len = sValue.length();
			characters = new char[len];
			sValue.getChars(0, sValue.length(), characters, 0);
		}
		return characters;
	}
	
	public void getCharacters(char[] cbuffer, int offset) {
		for (int i = 0; i < sValue.length(); i++) {
			cbuffer[i + offset] = sValue.charAt(i);
		}
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
		if (o == null) {
			return false;
		}
		if (o instanceof QNameValue) {
			QNameValue other = (QNameValue) o;
			return namespaceUri.equals(other.namespaceUri) && localName.equals(other.localName);
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return namespaceUri.hashCode() ^ localName.hashCode();
	}
	

}
