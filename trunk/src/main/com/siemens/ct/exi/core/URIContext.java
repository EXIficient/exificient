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

package com.siemens.ct.exi.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class URIContext {
	
	public final String namespaceURI;
	public final int id;

	protected final List<NameContext> localNames;
	protected final Map<String, Integer> localNameIDs;
	
	protected final List<String> prefixes;
	protected final Map<String, Integer> prefixIDs;
	
	public URIContext(String namespaceURI, int id) {
		this.namespaceURI = namespaceURI;
		this.id = id;
		
		localNames = new ArrayList<NameContext>();
		localNameIDs = new HashMap<String, Integer>();
		
		prefixes = new ArrayList<String>();
		prefixIDs = new HashMap<String, Integer>();
	}
	
	
	public Integer getLocalNameID(final String localName) {
		return localNameIDs.get(localName);
	}
	
	public NameContext getNameContext(final int localNameID) {
		return localNames.get(localNameID);
	}
	
	/*
	 * LocalNames
	 */
	public void addLocalName(final String localName) {
		assert (!localNames.contains(localName));
		int localNameID = localNames.size();
		localNameIDs.put(localName, localNameID);
		localNames.add(new NameContext(localNameID, localName, namespaceURI));
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
	
	public Integer getPrefixID(String prefix) {
		return prefixIDs.get(prefix);
	}
	
	public String getPrefix(int prefixID) {
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
