/*
 * Copyright (C) 2007-2012 Siemens AG
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

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.xml.namespace.QName;

import com.siemens.ct.exi.Constants;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9.1
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

	/**
	 * Returns qualified name as String
	 * 
	 * <p>
	 * QName ::= PrefixedName | UnprefixedName <br />
	 * PrefixedName ::= Prefix ':' LocalPart <br />
	 * UnprefixedName ::= LocalPart
	 * </p>
	 * 
	 * @return <code>String</code> for qname
	 */
	public static final String getQualifiedName(String localName, String pfx) {
		// System.out.println("getQualifiedName " + localName + " & "  + pfx);
		return pfx.length() == 0 ? localName
				: (pfx + Constants.COLON + localName);
	}

	/**
	 * Returns the className for a given qname e.g.,
	 * {http://www.w3.org/2001/XMLSchema}decimal -->
	 * org.w3.2001.XMLSchema.decimal
	 * 
	 * @param qname
	 * @return className or null if converting is not possible
	 */
	public static String getClassName(QName qname) {
		try {
			StringBuilder className = new StringBuilder();

			// e.g., {http://www.w3.org/2001/XMLSchema}decimal
			StringTokenizer st1 = new StringTokenizer(qname.getNamespaceURI(),
					"/");
			// --> "http:" -> "www.w3.org" --> "2001" -> "XMLSchema"
			st1.nextToken(); // protocol, e.g. "http:"
			String domain = st1.nextToken(); // "www.w3.org"

			StringTokenizer st2 = new StringTokenizer(domain, ".");
			List<String> lDomain = new ArrayList<String>();
			while (st2.hasMoreTokens()) {
				lDomain.add(st2.nextToken());
			}
			assert (lDomain.size() >= 2);
			className.append(lDomain.get(lDomain.size() - 1)); // "org"
			className.append('.');
			className.append(lDomain.get(lDomain.size() - 2)); // "w3"

			while (st1.hasMoreTokens()) {
				className.append('.');
				className.append(st1.nextToken());
			}

			className.append('.');
			className.append(qname.getLocalPart());

			return className.toString();
		} catch (Exception e) {
			return null;
		}
	}

}
