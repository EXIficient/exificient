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
 * @version 0.9.6-SNAPSHOT
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
	 * QName ::= PrefixedName | UnprefixedName <br>
	 * PrefixedName ::= Prefix ':' LocalPart <br>
	 * UnprefixedName ::= LocalPart
	 * </p>
	 * 
	 * @param localName local-name
	 * @param pfx prefix
	 * @return <code>String</code> for qname
	 */
	public static final String getQualifiedName(String localName, String pfx) {
		// System.out.println("getQualifiedName " + localName + " & "  + pfx);
		return pfx.length() == 0 ? localName
				: (pfx + Constants.COLON + localName);
	}

	/**
	 * Returns the className for a given qname e.g.,
	 * {http://www.w3.org/2001/XMLSchema}decimal &rarr;
	 * org.w3.2001.XMLSchema.decimal
	 * 
	 * @param qname qualified name
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
