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

package com.siemens.ct.exi.core.sax;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DefaultHandler2;

import com.siemens.ct.exi.EXIEncoder;
import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.FidelityOptions;
import com.siemens.ct.exi.api.sax.EXIWriter;
import com.siemens.ct.exi.attributes.AttributeFactory;
import com.siemens.ct.exi.attributes.AttributeList;
import com.siemens.ct.exi.exceptions.EXIException;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.2.20090324
 */

public class SAXEncoder extends DefaultHandler2 implements EXIWriter {
	protected EXIEncoder encoder;

	// buffers the characters of the characters() callback
	protected StringBuilder sbChars;

	// encodes collected char callbacks
	protected AbstractCharactersEncoder charEncoder;

	// attributes
	protected AttributeList exiAttributes;

	// prefix mappings
	protected List<String> prefixMappingPfx;
	protected List<String> prefixMappingURI;

	public SAXEncoder(EXIFactory factory) {
		this.encoder = factory.createEXIEncoder();

		// initialize
		sbChars = new StringBuilder();

		// whitespace characters required ?
		if (factory.getFidelityOptions().isFidelityEnabled(
				FidelityOptions.FEATURE_WS)) {
			charEncoder = new CharactersEncoderWhitespaceAware(encoder, sbChars);
		} else {
			charEncoder = new CharactersEncoderWhitespaceLess(encoder, sbChars);
		}

		// NS mappings
		prefixMappingPfx = new ArrayList<String>();
		prefixMappingURI = new ArrayList<String>();

		// attribute list
		AttributeFactory attFactory = AttributeFactory.newInstance();
		exiAttributes = attFactory.createAttributeListInstance(factory);
	}

	public void setOutput(OutputStream os, boolean exiBodyOnly)
			throws EXIException {
		encoder.setOutput(os, exiBodyOnly);
	}

	/*
	 * ======================================================================
	 * Interface ContentHandler
	 * ======================================================================
	 */
	
	@Override
	public void startPrefixMapping(String prefix, String uri)
			throws SAXException {
		prefixMappingPfx.add(prefix);
		prefixMappingURI.add(uri);
	}

	// @Override
	// public void endPrefixMapping(String prefix) throws SAXException {
	// globalPrefixMapping.remove(prefix);
	// }

	public void startElement(String uri, String local, String raw,
			Attributes attributes) throws SAXException {
		try {
			charEncoder.checkPendingChars();

			// start element
			encoder.encodeStartElement(uri, local, raw);

			// handle NS declarations
			handleNamespaceDeclarations();

			// attributes
			if (attributes != null && attributes.getLength() > 0) {
				handleAttributes(attributes);
			}
		} catch (EXIException e) {
			throw new SAXException("startElement: " + raw, e);
		}
	}

	protected void handleNamespaceDeclarations() throws EXIException {
		assert (prefixMappingPfx.size() == prefixMappingURI.size());

		for (int i = 0; i < prefixMappingPfx.size(); i++) {
			encoder.encodeNamespaceDeclaration(prefixMappingURI.get(i),
					prefixMappingPfx.get(i));
		}

		prefixMappingPfx.clear();
		prefixMappingURI.clear();
	}

	protected void handleAttributes(Attributes attributes) throws EXIException {
		//	1. Namespace declaration(s)
		//	(done via startPrefixMapping et cetera)
		
		// parse remaining attributes
		exiAttributes.parse(attributes);

		// 2. XSI-Type
		if (exiAttributes.hasXsiType()) {

			encoder.encodeXsiType(exiAttributes.getXsiTypeRaw());
		}

		// 3. XSI-Nil
		if (exiAttributes.hasXsiNil()) {
			encoder.encodeXsiNil(exiAttributes.getXsiNil());
		}

		// 4. Remaining Attributes
		// TODO AT prefix encoding
		for (int i = 0; i < exiAttributes.getNumberOfAttributes(); i++) {
			encoder.encodeAttribute(exiAttributes.getAttributeURI(i),
					exiAttributes.getAttributeLocalName(i), exiAttributes
							.getAttributeValue(i));
		}
	}

	public void startDocument() throws SAXException {
		try {
			encoder.encodeStartDocument();
		} catch (EXIException e) {
			throw new SAXException("startDocument", e);
		}
	}

	public void endDocument() throws SAXException {
		try {
			charEncoder.checkPendingChars();

			encoder.encodeEndDocument();
		} catch (EXIException e) {
			throw new SAXException("endDocument", e);
		}
	}

	public void processingInstruction(String target, String data)
			throws SAXException {
		try {
			charEncoder.checkPendingChars();

			encoder.encodeProcessingInstruction(target, data);
		} catch (EXIException e) {
			throw new SAXException("processingInstruction", e);
		}
	}

	public void endElement(String uri, String local, String raw)
			throws SAXException {
		try {
			charEncoder.checkPendingChars();

			encoder.encodeEndElement();
		} catch (EXIException e) {

			throw new SAXException("endElement=" + raw, e);
		}
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		sbChars.append(ch, start, length);
		// new String(ch, start, length);
	}

}
