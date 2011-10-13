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

package com.siemens.ct.exi.datatype;

import java.io.IOException;
import java.util.Arrays;

import com.siemens.ct.exi.EnhancedQName;
import com.siemens.ct.exi.core.RuntimeURIEntry;
import com.siemens.ct.exi.grammar.GrammarURIEntry;
import com.siemens.ct.exi.io.channel.EncoderChannel;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.8
 */

public class QNameDatatypeUCDProfile extends QNameDatatype {

	private static final long serialVersionUID = -5388595112370214500L;

	public QNameDatatypeUCDProfile() {
		super();
	}

	// String lastAddedURI;

	@Override
	protected int addURI(final String namespaceURI) {
		// no "real" addition, we just have one fall-in URI
		int uriID = grammarURIEntries.length;
		if (runtimeURIEntries.size() == grammarURIEntries.length) {
			runtimeURIEntries.add(new RuntimeURIEntry(namespaceURI, uriID, preservePrefix));
		} else {
			// replace old entry
			runtimeURIEntries.set(uriID, new RuntimeURIEntry(namespaceURI,
					uriID, preservePrefix));
		}
		// lastAddedURI = namespaceURI;
		return uriID;
	}

	@Override
	public EnhancedQName getEnhancedQName(String localName, int uriID) {
		// grammar entries
		if (uriID < grammarURIEntries.length) {
			GrammarURIEntry gue = grammarURIEntries[uriID];
			// binary search given that localName grammar entries are sorted
			int localNameID = Arrays.binarySearch(gue.localNames, localName);
			if (localNameID >= 0) {
				// return localNameID;
				return grammarURIEntries[uriID].eQNames[localNameID];
			}
		}

		// no appropriate local-name ID found
		// return Constants.NOT_FOUND;
		return null;
	}

	@Override
	protected EnhancedQName addLocalName(final String localName, int uriID) {
		// no "real" addition
//		if (uriID < grammarURIEntries.length) {
//			// grammar entries
//			return new QName(grammarURIEntries[uriID].namespaceURI, localName);
//		} else {
//			return new QName(
//					runtimeURIEntries.get(grammarURIEntries.length).namespaceURI,
//					localName);
//		}
//		return -1;
		return null;
		// return runtimeURIEntries.get(uriID).addLocalName(localName);
	}

	@Override
	protected void addPrefix(final String prefix, int uriID) {
		// no additions
	}

	@Override
	public void encodeQNamePrefix(String prefix, String uri,
			EncoderChannel channel) throws IOException {
		encodeQNamePrefix(prefix, requireUriID(uri), channel);
	}

	@Override
	public void encodeQNamePrefix(String prefix, int uriID,
			EncoderChannel channel) throws IOException {
		super.encodeQNamePrefix(prefix, uriID, channel);
		
//		if (uriID < grammarURIEntries.length) {
//			super.encodeQNamePrefix(prefix, uriID, channel);
//		} else {
//			super.encodeQNamePrefix(prefix, 0, channel);
//		}
	}

}