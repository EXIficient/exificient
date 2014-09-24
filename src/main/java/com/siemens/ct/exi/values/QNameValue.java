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

public class QNameValue extends AbstractValue {

	private static final long serialVersionUID = -6092774558055449492L;

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
