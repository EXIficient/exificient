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
import com.siemens.ct.exi.EnhancedQName;
import com.siemens.ct.exi.util.xml.QNameUtilities;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.8
 */

public class RuntimeURIEntry {

	public final String namespaceURI;
	public final int id;
	protected final EnhancedQName[] grammarQNames;
	protected final String[] grammarPrefixes;
	protected final boolean preservePrefix;
	protected final String[] grammarQNamesAsString;

	protected final List<EnhancedQName> runtimeQNames;
	protected final List<String> runtimePrefixes;
	protected final List<String> runtimeQNamesAsString;

	protected static final String[] EMPTY_STRING_LIST = new String[0];
	protected static final EnhancedQName[] EMPTY_ENHANCEDQNAME_LIST = new EnhancedQName[0];

	public RuntimeURIEntry(String namespaceURI, int id, boolean preservePrefix) {
		this(namespaceURI, id, EMPTY_ENHANCEDQNAME_LIST, EMPTY_STRING_LIST, preservePrefix);
	}

	public RuntimeURIEntry(String namespaceURI, int id, EnhancedQName[] grammarQNames, String[] grammarPrefixes, boolean preservePrefix) {
		this.namespaceURI = namespaceURI;
		this.id = id;
		this.grammarQNames = grammarQNames;
		this.grammarPrefixes = grammarPrefixes;
		this.preservePrefix = preservePrefix;
		// localNames
		runtimeQNames = new ArrayList<EnhancedQName>();
		// prefixes
		runtimePrefixes = new ArrayList<String>();
		for (int i = 0; i < grammarPrefixes.length; i++) {
			addPrefix(grammarPrefixes[i]);
		}
		// (default) qnames as string
		runtimeQNamesAsString = new ArrayList<String>();
		grammarQNamesAsString = new String[grammarQNames.length];
		if (!preservePrefix) {
			// System.out.println("default prefixes for " + this.namespaceURI);
			for(int i=0; i<grammarQNames.length; i++) {
				grammarQNamesAsString[i] = QNameUtilities.getQualifiedName(grammarQNames[i].getQName().getLocalPart(), getDefaultPrefix());	
			}	
		}
	}
	

	public final String getDefaultPrefix() {
		String pfx;
		if (grammarPrefixes.length > 0) {
			pfx = grammarPrefixes[0];
		} else {
			pfx = "ns" + id;
		}
		
		return pfx;
	}

	public void clear() {
		runtimeQNames.clear();
		runtimeQNamesAsString.clear();
		while(runtimePrefixes.size() != grammarPrefixes.length) {
			runtimePrefixes.remove(runtimePrefixes.size()-1);
		}
		
		if (preservePrefix) {
			// unset all previously collected sqnames
			for(int i=0; i<grammarQNamesAsString.length; i++) {
				grammarQNamesAsString[i] = null;
			}
		}
		
	}
	
	public String getQNameAsString(final EnhancedQName eqname, String pfx) {
		int localNameID = eqname.getLocalNameID();
		if (preservePrefix) {
			String sqname;
			// it is beneficial ONLY if one prefix is declared
			if (localNameID < this.grammarQNamesAsString.length) {
				if (this.runtimePrefixes.size() == 1) {
					sqname = grammarQNamesAsString[localNameID];
					if (sqname == null) {
						sqname = QNameUtilities.getQualifiedName(eqname.getQName().getLocalPart(), pfx);
						grammarQNamesAsString[localNameID] = sqname;
					}
				} else {
					sqname = QNameUtilities.getQualifiedName(eqname.getQName().getLocalPart(), pfx);
				}
			} else {
				if (this.runtimePrefixes.size() == 1) {
					int index = localNameID-grammarQNamesAsString.length;
					sqname = this.runtimeQNamesAsString.get(index);
					if (sqname == null) {
						sqname = QNameUtilities.getQualifiedName(eqname.getQName().getLocalPart(), pfx);
						runtimeQNamesAsString.set(index, sqname);
					}
				} else {
					sqname = QNameUtilities.getQualifiedName(eqname.getQName().getLocalPart(), pfx);
				}
			}
			return sqname;
		} else {
			if (localNameID < this.grammarQNamesAsString.length) {
				return grammarQNamesAsString[localNameID];
			} else {
				return this.runtimeQNamesAsString.get(localNameID-grammarQNamesAsString.length);
			}
		}
	}
	
	public EnhancedQName getEnhancedQName(final int localNameID) {
		if (localNameID < this.grammarQNames.length) {
			 return this.grammarQNames[localNameID];
		} else {
			return runtimeQNames.get(localNameID-grammarQNames.length);
		}
		// return localNames.get(localNameID);
	}

	/*
	 * LocalNames
	 */
	public EnhancedQName addLocalName(final String localName) {
		QName qname = new QName(namespaceURI, localName);
		int localNameID = runtimeQNames.size() + this.grammarQNames.length;
		EnhancedQName eqname = new EnhancedQName(qname, id, localNameID);
		runtimeQNames.add(eqname);
		String sqname = null;
		// no prefix (null) is added if prefixes are preserved
		if (!preservePrefix) {
			sqname = QNameUtilities.getQualifiedName(localName, getDefaultPrefix());
		}
		runtimeQNamesAsString.add(sqname);
		return eqname;
	}

	public int getLocalNameSize() {
		return runtimeQNames.size();
	}

	/*
	 * Prefixes
	 */
	public void addPrefix(final String prefix) {
		assert (!runtimePrefixes.contains(prefix));
		runtimePrefixes.add(prefix);
	}

	public List<String> getPrefixes() {
		return this.runtimePrefixes;
	}

	public int getPrefixID(String prefix) {
		for (int i = 0; i < runtimePrefixes.size(); i++) {
			if (runtimePrefixes.get(i).equals(prefix)) {
				return i;
			}
		}
		return Constants.NOT_FOUND;
	}

	public String getPrefix(final int prefixID) {
		assert (prefixID >= 0 && prefixID < runtimePrefixes.size());
		return runtimePrefixes.get(prefixID);
	}

	public int getPrefixSize() {
		return runtimePrefixes.size();
	}

	@Override
	public String toString() {
		return namespaceURI + "(" + id + ")";
	}

}
