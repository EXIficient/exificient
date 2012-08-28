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

public class RuntimeEvolvingUriContext extends AbstractEvolvingUriContext {

	private static final long serialVersionUID = 6324253980560785631L;

	// empty grammar entries
	public RuntimeEvolvingUriContext(int namespaceUriID, String namespaceUri) {
		super(namespaceUriID, namespaceUri);
	}

	public int getNumberOfQNames() {
		return runtimeQNames.size();
	}

	public QNameContext getQNameContext(int localNameID) {
		assert (localNameID >= 0);
		assert (localNameID < getNumberOfQNames());
		// runtime entries
		return runtimeQNames.get(localNameID);
	}

	public QNameContext getQNameContext(String localName) {
		// runtime entries
		for (QNameContext qnamec : runtimeQNames) {
			if (qnamec.getLocalName().equals(localName)) {
				return qnamec;
			}
		}

		return null;
	}

	public int getNumberOfPrefixes() {
		return runtimePrefixes.size();
	}

	public String getPrefix(int prefixID) {
		assert (prefixID >= 0);
		// runtime entries
		return runtimePrefixes.get(prefixID);
	}

	public int getPrefixID(String prefix) {
		for (int i = 0; i < runtimePrefixes.size(); i++) {
			String pfx = runtimePrefixes.get(i);
			if (pfx.equals(prefix)) {
				return i;
			}
		}

		return -1;
	}

}
