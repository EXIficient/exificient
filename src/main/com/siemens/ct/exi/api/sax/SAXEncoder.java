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

package com.siemens.ct.exi.api.sax;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.xml.XMLConstants;

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
 * @version 0.9
 */

public class SAXEncoder extends DefaultHandler2 {
	protected EXIFactory factory;
	
	protected EXIStreamEncoder exiStream;
	protected EXIBodyEncoder encoder;

	// buffers the characters of the characters() callback
	protected StringBuilder sbChars;

	// attributes
	protected AttributeList exiAttributes;

	public SAXEncoder(EXIFactory factory) throws EXIException {
		this.factory = factory;
		
		// exi stream
		exiStream = new EXIStreamEncoder(factory);
		
		// initialize char buffer
		sbChars = new StringBuilder();
		
		// attribute list
		AttributeFactory attFactory = AttributeFactory.newInstance();
		exiAttributes = attFactory.createAttributeListInstance(factory);
	}
	
	public void setOutputStream(OutputStream os) throws EXIException, IOException {
		// buffer stream if not already
		// TODO is there a *nice* way to detect whether a stream is buffered
		if (!(os instanceof BufferedOutputStream)) {
			os = new BufferedOutputStream(os);
		}
		
		// write header & get body encoder
		this.encoder = exiStream.encodeHeader(os);
	}

	/*
	 * ======================================================================
	 * Interface ContentHandler
	 * ======================================================================
	 */

	@Override
	public void startPrefixMapping(String prefix, String uri)
			throws SAXException {
		this.exiAttributes.addNamespaceDeclaration(uri, prefix);
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
		
		// add remaining attributes (if any)
		if (attributes != null) {
			for (int i = 0; i < attributes.getLength(); i++) {
				exiAttributes.addAttribute(attributes.getURI(i), attributes.getLocalName(i), getPrefixOf(attributes, i), attributes.getValue(i));
			}	
		}
		
		// encode NS and attributes
		encoder.encodeAttributeList(exiAttributes);
		exiAttributes.clear();
	}


	private String getPrefixOf(Attributes atts, int index) {
		String qname = atts.getQName(index);
		String localName = atts.getLocalName(index);

		int lengthDifference = qname.length() - localName.length();
		return (lengthDifference == 0 ? XMLConstants.DEFAULT_NS_PREFIX : qname
				.substring(0, lengthDifference - 1));
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
	}

	protected void checkPendingChars() throws EXIException, IOException {
		if (sbChars.length() > 0) {
			encoder.encodeCharacters(new StringValue(sbChars.toString()));
			sbChars.setLength(0);
		}
	}

}
