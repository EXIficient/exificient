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

package com.siemens.ct.exi.datatype.charset;

import com.siemens.ct.exi.util.xml.XMLWhitespace;

/**
 * Built-In Restricted Character Set for xsd:integer
 * http://www.w3.org/TR/exi/#builtInRestrictedStrings
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.3.20090421
 */

public class XSDIntegerCharacterSet extends AbstractRestrictedCharacterSet {

	/*
	 * xsd:integer { #x9, #xA, #xD, #x20, +, -, [0-9] }
	 */
	public XSDIntegerCharacterSet() {
		super();
		// #x9, #xA, #xD, #x20
		addValue(XMLWhitespace.WS_TAB);
		addValue(XMLWhitespace.WS_NL);
		addValue(XMLWhitespace.WS_CR);
		addValue(XMLWhitespace.WS_SPACE);
		// +, -
		addValue('+');
		addValue('-');
		// [0-9]
		for (int i = '0'; i <= '9'; i++) {
			addValue((char) i);
		}
	}
}
