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

package com.siemens.ct.exi.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.5
 */

public class RuntimeURIEntry {

	public final String namespaceURI;
	public final int id;

	protected final List<QName> localNames;
	protected final Map<String, Integer> localNameIDs;

	protected final List<String> prefixes;
	protected final Map<String, Integer> prefixIDs;

	public RuntimeURIEntry(String namespaceURI, int id) {
		this.namespaceURI = namespaceURI;
		this.id = id;

		localNames = new ArrayList<QName>();
		localNameIDs = new HashMap<String, Integer>();

		prefixes = new ArrayList<String>();
		prefixIDs = new HashMap<String, Integer>();
	}

	public Integer getLocalNameID(final String localName) {
		return localNameIDs.get(localName);
	}

	public QName getNameContext(final int localNameID) {
		return localNames.get(localNameID);
	}

	/*
	 * LocalNames
	 */
	public QName addLocalName(final String localName) {
		localNameIDs.put(localName, localNames.size());
		QName qname = new QName(namespaceURI, localName);
		localNames.add(qname);
		return qname;
	}

	public QName removeLocalName(final int localNameID) {
		QName qname = localNames.remove(localNameID);
		localNameIDs.remove(qname.getLocalPart());
		return qname;
	}

	public int getLocalNameSize() {
		return localNames.size();
	}

	/*
	 * Prefixes
	 */
	public void addPrefix(final String prefix) {
		assert (!prefixes.contains(prefix));
		int prefixID = prefixes.size();
		prefixIDs.put(prefix, prefixID);
		prefixes.add(prefix);
	}

	public String removePrefix(final int prefixID) {
		String pfx = prefixes.remove(prefixID);
		prefixIDs.remove(pfx);
		return pfx;
	}

	public List<String> getPrefixes() {
		return this.prefixes;
	}

	public Integer getPrefixID(String prefix) {
		return prefixIDs.get(prefix);
	}

	public String getPrefix(final int prefixID) {
		return prefixes.get(prefixID);
	}

	public int getPrefixSize() {
		return prefixes.size();
	}

	@Override
	public String toString() {
		return namespaceURI + "(" + id + ")";
	}

}
