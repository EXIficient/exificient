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

package com.siemens.ct.exi.util.sort;

import java.util.Comparator;

import javax.xml.XMLConstants;

import org.apache.xerces.xs.XSAttributeDeclaration;
import org.apache.xerces.xs.XSAttributeUse;
import org.apache.xerces.xs.XSElementDeclaration;

import com.siemens.ct.exi.core.Context;

/*
 * Helper Class for sorting element declarations, context et cetera
 */
public class LexicographicSort implements Comparator<Object> {

	public int compare(Object o1, Object o2) {
		String ns1, ns2, localName1, localName2;
		if (o1 instanceof XSElementDeclaration
				&& o2 instanceof XSElementDeclaration) {
			return compare((XSElementDeclaration) o1, (XSElementDeclaration) o2);
		} else if (o1 instanceof XSAttributeDeclaration
				&& o2 instanceof XSAttributeDeclaration) {
			return compare((XSAttributeDeclaration) o1,
					(XSAttributeDeclaration) o2);
		} else if (o1 instanceof XSAttributeUse && o2 instanceof XSAttributeUse) {
			// attribute declaration counts
			return compare(((XSAttributeUse) o1).getAttrDeclaration(),
					((XSAttributeUse) o2).getAttrDeclaration());
		} else if (o1 instanceof Context && o2 instanceof Context) {
			Context c1 = (Context) o1;
			Context c2 = (Context) o2;
			ns1 = c1.getNamespaceURI();
			localName1 = c1.getLocalName();
			ns2 = c2.getNamespaceURI();
			localName2 = c2.getLocalName();
		} else {
			throw new RuntimeException(
					"[EXI] Unsupported types of classes for sorting.");
		}

		return compare(ns1, localName1, ns2, localName2);
	}

	public int compare(XSElementDeclaration e1, XSElementDeclaration e2) {
		return compare(e1.getNamespace(), e1.getName(), e2.getNamespace(), e2
				.getName());
	}

	public int compare(XSAttributeDeclaration a1, XSAttributeDeclaration a2) {
		return compare(a1.getNamespace(), a1.getName(), a2.getNamespace(), a2
				.getName());
	}

	public int compare(Context c1, Context c2) {
		return compare(c1.getNamespaceURI(), c1.getLocalName(), c2
				.getNamespaceURI(), c2.getLocalName());
	}

	// sorted lexicographically by qname local-name, then by qname uri
	public static int compare(String ns1, String ln1, String ns2, String ln2) {
		if (ns1 == null) {
			ns1 = XMLConstants.NULL_NS_URI;
		}
		if (ns2 == null) {
			ns2 = XMLConstants.NULL_NS_URI;
		}
		int cLocalPart = ln1.compareTo(ln2);
		return (cLocalPart == 0 ? ns1.compareTo(ns2) : cLocalPart);
	}

}
