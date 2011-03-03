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

package com.siemens.ct.exi.core;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import com.siemens.ct.exi.Constants;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.6
 */

public class RuntimeURIEntry {

	public final String namespaceURI;
	public final int id;
	protected final String[] grammarPrefixes;

	protected final List<QName> localNames;
	protected final List<String> prefixes;

	protected static final String[] EMPTY_PREFIXES = new String[0];

	public RuntimeURIEntry(String namespaceURI, int id) {
		this(namespaceURI, id, EMPTY_PREFIXES);
	}

	public RuntimeURIEntry(String namespaceURI, int id, String[] grammarPrefixes) {
		this.namespaceURI = namespaceURI;
		this.id = id;
		this.grammarPrefixes = grammarPrefixes;
		// localNames
		localNames = new ArrayList<QName>();
		// prefixes
		prefixes = new ArrayList<String>();
		initPrefixes();
	}

	private void initPrefixes() {
		assert (grammarPrefixes != null);
		for (int i = 0; i < grammarPrefixes.length; i++) {
			addPrefix(grammarPrefixes[i]);
		}
	}

	public void clear() {
		localNames.clear();
		prefixes.clear();
		initPrefixes();
	}

	public QName getQName(final int localNameID) {
		return localNames.get(localNameID);
	}

	/*
	 * LocalNames
	 */
	public QName addLocalName(final String localName) {
		QName qname = new QName(namespaceURI, localName);
		localNames.add(qname);
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
		prefixes.add(prefix);
	}

	public List<String> getPrefixes() {
		return this.prefixes;
	}

	public int getPrefixID(String prefix) {
		for (int i = 0; i < prefixes.size(); i++) {
			if (prefixes.get(i).equals(prefix)) {
				return i;
			}
		}
		return Constants.NOT_FOUND;
	}

	public String getPrefix(final int prefixID) {
		assert (prefixID >= 0 && prefixID < prefixes.size());
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
