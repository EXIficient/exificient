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

import javax.xml.namespace.QName;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.6
 */

public class QNameValue extends AbstractValue {

	private static final long serialVersionUID = -6092774558055449492L;
	
	protected final QName qname;
	protected final String prefix;

	protected char[] characters;
	protected String sValue;
	
	public QNameValue(QName qname, String prefix) {
		this.qname = qname;
		this.prefix = prefix;
		
		if (prefix == null || prefix.length() == 0 ) {
			sValue = qname.getLocalPart();
		} else {
			sValue = prefix + ":" + qname.getLocalPart();
		}
	}
	
	public QName toQName() {
		return qname;
	}
	
	public String getPrefix()  {
		return prefix;
	}
	
	public int getCharactersLength() {
		return sValue.length();
	}
	
	public char[] toCharacters(char[] cbuffer, int offset) {
		for(int i=0; i<sValue.length(); i++) {
			cbuffer[i+offset] = sValue.charAt(i);
		}
		return cbuffer;
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
		if (o instanceof QNameValue) {
			return qname.equals((QNameValue)o);
		} else {
			return false;	
		}
	}

}