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

package com.siemens.ct.exi.util.xml;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.3.20080718
 */

public class QNameUtilities {
	/**
	 * Returns the local part of the given raw qname.
	 * 
	 * @param qname
	 *            raw qname input
	 * 
	 * @return Local part of the name if prefixed, or the given name if not
	 */
	public static String getLocalPart(String qname) {
		int index = qname.indexOf(':');

		return (index < 0) ? qname : qname.substring(index + 1);
	}

	/**
	 * Returns the prefix part of the given raw qname.
	 * 
	 * @param qname
	 *            raw qname input
	 * 
	 * @return Prefix of name or empty string if none there
	 */
	public static String getPrefixPart(String qname) {
		int index = qname.indexOf(':');

		return (index >= 0) ? qname.substring(0, index) : "";
	}

}
