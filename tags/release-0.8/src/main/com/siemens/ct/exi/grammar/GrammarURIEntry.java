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

package com.siemens.ct.exi.grammar;

import java.io.Serializable;

import javax.xml.namespace.QName;

import com.siemens.ct.exi.EnhancedQName;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.8
 */

public class GrammarURIEntry implements Serializable {

	private static final long serialVersionUID = 352521635962295594L;

	public final int namespaceUriID;
	public final String namespaceURI;
	public final String[] localNames;
	public final String[] prefixes;
	public final EnhancedQName[] eQNames;

	public GrammarURIEntry(int namespaceUriID, String uri, String[] localNames, String[] prefixes) {
		assert (uri != null);
		assert (localNames != null);
		assert (prefixes != null);
		this.namespaceUriID = namespaceUriID;
		this.namespaceURI = uri;
		this.localNames = localNames;
		this.eQNames = new EnhancedQName[localNames.length];
		for (int i = 0; i < localNames.length; i++) {
			this.eQNames[i] = new EnhancedQName( new QName(uri, localNames[i]), namespaceUriID, i);
		}
		this.prefixes = prefixes;
	}

	
	@Override
	public String toString() {
		return "{" + namespaceURI + "}(localNames#" + localNames.length
				+ ", prefixes#" + prefixes.length + ")";
	}
	

}