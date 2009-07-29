/*
 * Copyright (C) 2007-2009 Siemens AG
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

package com.siemens.ct.exi.util;

import java.util.Comparator;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.3.20080718
 */

public class ExpandedNameComparator implements Comparator<ExpandedName> {

	public int compare(ExpandedName o1, ExpandedName o2) {
		int cLocalPart = o1.getLocalName().compareTo(o2.getLocalName());
		return (cLocalPart == 0 ? o1.getNamespaceURI().compareTo(
				o2.getNamespaceURI()) : cLocalPart);
	}
	
	public static int compare(String ns1, String ln1, String ns2, String ln2) {
		int cLocalPart = ln1.compareTo(ln2);
		return (cLocalPart == 0 ? ns1.compareTo(ns2) : cLocalPart);
	}
}
