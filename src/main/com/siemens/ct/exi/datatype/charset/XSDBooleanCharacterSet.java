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

package com.siemens.ct.exi.datatype.charset;

import com.siemens.ct.exi.util.xml.XMLWhitespace;

/**
 * Built-In Restricted Character Set for xsd:boolean
 * http://www.w3.org/TR/exi/#builtInRestrictedStrings
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9
 */

public class XSDBooleanCharacterSet extends AbstractRestrictedCharacterSet {

	private static final long serialVersionUID = -2931452374414551649L;

	/*
	 * xsd:boolean { #x9, #xA, #xD, #x20, 0, 1, a, e, f, l, r, s, t, u }
	 */
	public XSDBooleanCharacterSet() {
		super();
		// #x9, #xA, #xD, #x20
		addValue(XMLWhitespace.WS_TAB);
		addValue(XMLWhitespace.WS_NL);
		addValue(XMLWhitespace.WS_CR);
		addValue(XMLWhitespace.WS_SPACE);
		// 0, 1
		addValue('0');
		addValue('1');
		// a, e, f, l, r, s, t, u
		addValue('a');
		addValue('e');
		addValue('f');
		addValue('l');
		addValue('r');
		addValue('s');
		addValue('t');
		addValue('u');
	}
}
