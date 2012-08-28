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

package com.siemens.ct.exi.context;


public class GrammarUriContext extends AbstractUriContext {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -6565683847377873121L;

	public static String[] EMPTY_PREFIXES = new String[0];

	// grammar local-names
	final QNameContext[] grammarQNames;

	// grammar prefixes
	final String[] grammarPrefixes;
	
	// default prefix
	final String defaultPrefix;

	public GrammarUriContext(int namespaceUriID, String namespaceUri,
			QNameContext[] grammarQNames, String[] grammarPrefixes) {
		super(namespaceUriID, namespaceUri);
		this.grammarQNames = grammarQNames;
		this.grammarPrefixes = grammarPrefixes;
		
		switch (namespaceUriID) {
		case 0:
			// "" [empty string]
			this.defaultPrefix = "";
			break;
		case 1:
			this.defaultPrefix = "xml";
			break;
		case 2:
			this.defaultPrefix = "xsi";
			break;
		default:
			this.defaultPrefix = "ns" + namespaceUriID;
		}
	}
	
	public String getDefaultPrefix() {
		return defaultPrefix;
	}

	public GrammarUriContext(int namespaceUriID, String namespaceUri,
			QNameContext[] grammarQNames) {
		this(namespaceUriID, namespaceUri, grammarQNames, EMPTY_PREFIXES);
	}

	public int getNumberOfQNames() {
		return grammarQNames.length;
	}

	public QNameContext getQNameContext(int localNameID) {
		if (localNameID < grammarQNames.length) {
			return grammarQNames[localNameID];
		}
		return null;
	}

	public QNameContext getQNameContext(String localName) {
		assert (localName != null);
		return binarySearch(grammarQNames, localName);
	}

	protected static QNameContext binarySearch(QNameContext[] grammarQNames,
			String localName) {
		if (grammarQNames == null) {
			System.err.println("ERROR null");
		}
		assert (grammarQNames != null);

		int low = 0;
		int high = grammarQNames.length - 1;

		while (low <= high) {
			int mid = (low + high) >> 1;
			QNameContext midVal = grammarQNames[mid];
			int cmp = midVal.compareTo(localName);

			if (cmp < 0) {
				low = mid + 1;
			} else if (cmp > 0) {
				high = mid - 1;
			} else {
				// return mid; // key found
				return midVal; // key found
			}
		}

		// return -(low + 1); // key not found.
		return null; // key not found.
	}

	public int getNumberOfPrefixes() {
		return grammarPrefixes.length;
	}

	public String getPrefix(int prefixID) {
		assert (prefixID >= 0);
		String pfx = null;
		if (prefixID < grammarPrefixes.length) {
			pfx = grammarPrefixes[prefixID];
		}
		return pfx;
	}

	public int getPrefixID(String prefix) {
		for (int i = 0; i < grammarPrefixes.length; i++) {
			String pfx = grammarPrefixes[i];
			if (pfx.equals(prefix)) {
				return i;
			}
		}
		return -1;
	}

}
