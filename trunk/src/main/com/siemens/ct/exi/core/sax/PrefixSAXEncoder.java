/*
 * Copyright (C) 2007, 2008 Siemens AG
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

package com.siemens.ct.exi.core.sax;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.util.xml.QNameUtilities;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.1.20080908
 */

public class PrefixSAXEncoder extends NoPrefixSAXEncoder {
	protected Map<String, String> elementPrefixMapping;

	public PrefixSAXEncoder(EXIFactory factory) {
		super(factory);

		elementPrefixMapping = new HashMap<String, String>();
	}

	@Override
	public void startPrefixMapping(String prefix, String uri)
			throws SAXException {
		super.startPrefixMapping(prefix, uri);

		// preserve prefix only
		elementPrefixMapping.put(prefix, uri);
	}

	@Override
	public void startElement(String uri, String local, String raw,
			Attributes attributes) throws SAXException {
		try {
			checkPendingCharacters();

			// prefix mapping
			String pfx = QNameUtilities.getPrefixPart(raw);
			encoder.encodeStartElement(uri, local, pfx);

			// prefixes from (startPrefixMapping)
			handleNS();

			// attributes
			if (attributes != null && attributes.getLength() > 0) {
				handleAttributes(attributes);
			}
		} catch (Exception e) {
			throw new SAXException("startElement: " + raw, e);
		}
	}

	/*
	 * TODO 6. Encoding EXI Streams Namespace (NS) and attribute (AT) events are
	 * encoded in a specific order following the associated start element (SE)
	 * event. Namespace (NS) events are encoded first followed by the
	 * AT(xsi:type) event if present, followed by the AT(xsi:nil) event if
	 * present, followed by the rest of the attribute (AT) events.
	 * 
	 * When schema-informed grammars are used for processing an element,
	 * attribute events occur in lexical order in the stream sorted first by
	 * qname localName then by qname uri. Otherwise, when built-in element
	 * grammars are used, attribute events can occur in any order. Namespace
	 * (NS) events can occur in any order regardless of the grammars used for
	 * processing the associated element.
	 */
	protected void handleNS() throws Exception {
		for (Iterator<String> i = elementPrefixMapping.keySet().iterator(); i
				.hasNext();) {
			String pfx = i.next();
			encoder.encodeNamespaceDeclaration(elementPrefixMapping.get(pfx),
					pfx);
		}

		// reset
		elementPrefixMapping.clear();
	}

}
