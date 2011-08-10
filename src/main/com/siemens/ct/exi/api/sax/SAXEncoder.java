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

package com.siemens.ct.exi.api.sax;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DefaultHandler2;

import com.siemens.ct.exi.EXIBodyEncoder;
import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.EXIStreamEncoder;
import com.siemens.ct.exi.attributes.AttributeFactory;
import com.siemens.ct.exi.attributes.AttributeList;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.values.StringValue;

/**
 * Serializes SAX events to EXI stream.
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.7
 */

public class SAXEncoder extends DefaultHandler2 {
	protected EXIBodyEncoder encoder;

	// buffers the characters of the characters() callback
	protected StringBuilder sbChars;

	// attributes
	protected AttributeList exiAttributes;

	// prefix mappings
	protected List<PrefixMapping> prefixMappings;

	public SAXEncoder(EXIFactory factory, OutputStream os) throws EXIException {
		try {
			// initialize char buffer
			sbChars = new StringBuilder();

			// prefix to NS mappings
			prefixMappings = new ArrayList<PrefixMapping>();

			// attribute list
			AttributeFactory attFactory = AttributeFactory.newInstance();
			exiAttributes = attFactory.createAttributeListInstance(factory);

			// exi stream
			EXIStreamEncoder exiStream = new EXIStreamEncoder();
			
			// buffer stream if not already
			// TODO is there a *nice* way to detect whether a stream is buffered
			if (!(os instanceof BufferedOutputStream)) {
				os = new BufferedOutputStream(os);
			}
			
			// write header & get body encoder
			this.encoder = exiStream.encodeHeader(factory, os);
			
		} catch (IOException e) {
			throw new EXIException(e);
		}
	}

	/*
	 * ======================================================================
	 * Interface ContentHandler
	 * ======================================================================
	 */

	@Override
	public void startPrefixMapping(String prefix, String uri)
			throws SAXException {
		prefixMappings.add(new PrefixMapping(prefix, uri));
	}

	// @Override
	// public void endPrefixMapping(String prefix) throws SAXException {
	// globalPrefixMapping.remove(prefix);
	// }

	public void startElement(String uri, String local, String raw,
			Attributes attributes) throws SAXException {
		try {
			// no prefix
			this.startElementPfx(uri, local, null, attributes);
		} catch (Exception e) {
			throw new SAXException("startElement: " + raw, e);
		}
	}

	protected void startElementPfx(String uri, String local, String prefix,
			Attributes attributes) throws EXIException, IOException {
		checkPendingChars();

		// start element
		encoder.encodeStartElement(uri, local, prefix);

		// handle NS declarations
		handleNamespaceDeclarations();

		// attributes
		if (attributes != null && attributes.getLength() > 0) {
			handleAttributes(attributes);
		}
	}

	protected void handleNamespaceDeclarations() throws EXIException,
			IOException {
		int size = prefixMappings.size();
		if (size > 0) {
			for (int i = 0; i < size; i++) {
				PrefixMapping pm = prefixMappings.get(i);
				encoder.encodeNamespaceDeclaration(pm.uri, pm.prefix);
			}

			prefixMappings.clear();
		}
	}

	protected void handleAttributes(Attributes attributes) throws EXIException,
			IOException {
		// 1. Namespace declaration(s)
		// (done via startPrefixMapping et cetera)

		// parse remaining attributes
		exiAttributes.parse(attributes);

		// 2. XSI-Type
		if (exiAttributes.hasXsiType()) {

			encoder.encodeAttributeXsiType(
					new StringValue(exiAttributes.getXsiTypeRaw()),
					exiAttributes.getXsiTypePrefix());
		}

		// 3. XSI-Nil
		if (exiAttributes.hasXsiNil()) {
			encoder.encodeAttributeXsiNil(
					new StringValue(exiAttributes.getXsiNil()),
					exiAttributes.getXsiNilPrefix());
		}

		// 4. Remaining Attributes
		for (int i = 0; i < exiAttributes.getNumberOfAttributes(); i++) {
			// encoder.encodeAttribute(exiAttributes.getAttributeURI(i),
			// exiAttributes.getAttributeLocalName(i), exiAttributes
			// .getAttributePrefix(i), exiAttributes
			// .getAttributeValue(i));
			encoder.encodeAttribute(exiAttributes.getAttributeURI(i),
					exiAttributes.getAttributeLocalName(i), exiAttributes
							.getAttributePrefix(i), new StringValue(
							exiAttributes.getAttributeValue(i)));
		}
	}

	public void startDocument() throws SAXException {
		try {
			encoder.encodeStartDocument();
		} catch (Exception e) {
			throw new SAXException("startDocument", e);
		}
	}

	public void endDocument() throws SAXException {
		try {
			checkPendingChars();
			encoder.encodeEndDocument();
			encoder.flush();
		} catch (Exception e) {
			throw new SAXException("endDocument", e);
		}
	}

	public void endElement(String uri, String local, String raw)
			throws SAXException {
		try {
			checkPendingChars();
			encoder.encodeEndElement();
		} catch (Exception e) {
			throw new SAXException("endElement=" + raw, e);
		}
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		sbChars.append(ch, start, length);
		// new String(ch, start, length);
	}

	protected void checkPendingChars() throws EXIException, IOException {
		if (sbChars.length() > 0) {
			// encoder.encodeCharacters(sbChars.toString());
			encoder.encodeCharacters(new StringValue(sbChars.toString()));
			sbChars.setLength(0);
		}
	}

	static final class PrefixMapping {
		final String prefix;
		final String uri;

		public PrefixMapping(String prefix, String uri) {
			this.prefix = prefix;
			this.uri = uri;
		}
	}

}
