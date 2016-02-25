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

package com.siemens.ct.exi.util.sort;

import java.util.Comparator;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

/**
 * Helper Class for sorting element declarations, context et cetera by qname.
 * 
 * EXI's lexical order: sorted first by qname's local-name then by qname's URI.
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9.6-SNAPSHOT
 */

public class QNameSort implements Comparator<QName> {

	public int compare(QName q1, QName q2) {
			return compare(q1.getNamespaceURI(), q1.getLocalPart(),
					q2.getNamespaceURI(), q2.getLocalPart());
	}
	
	/**
	 * Sort lexicographically by qname local-name, then by qname uri
	 * 
	 * @param ns1 qname1 namespace
	 * @param ln1 qname1 local-name
	 * @param ns2 qname2 namespace
	 * @param ln2 qname2 local-name
	 * @return a negative integer, zero, or a positive integer as the first qname is less than, equal to, or greater than the second.
	 */
	public static int compare(String ns1, String ln1, String ns2, String ln2) {
		if (ns1 == null) {
			ns1 = XMLConstants.NULL_NS_URI;
		}
		if (ns2 == null) {
			ns2 = XMLConstants.NULL_NS_URI;
		}
		if(ln1 == null || ln2 == null) {
			System.err.println("XXXXXXX");
		}
		int cLocalPart = ln1.compareTo(ln2);
		return (cLocalPart == 0 ? ns1.compareTo(ns2) : cLocalPart);
	}

}
