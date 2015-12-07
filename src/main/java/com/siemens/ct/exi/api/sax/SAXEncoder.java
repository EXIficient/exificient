/*
 * Copyright (c) 2007-2015 Siemens AG
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * 
 */

package com.siemens.ct.exi.api.sax;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
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
 * @version 0.9.6-SNAPSHOT
 */

public class SAXEncoder extends DefaultHandler2 {
	protected EXIFactory factory;
	
	protected EXIStreamEncoder exiStream;
	protected EXIBodyEncoder encoder;

	// attributes
	protected AttributeList exiAttributes;

	public SAXEncoder(EXIFactory factory) throws EXIException {
		this.factory = factory;
		
		// exi stream
		exiStream = factory.createEXIStreamEncoder();
		
		// attribute list
		AttributeFactory attFactory = AttributeFactory.newInstance();
		exiAttributes = attFactory.createAttributeListInstance(factory);
	}
	
	public void setOutputStream(OutputStream os) throws EXIException, IOException {
		// buffer stream if not already
		// TODO is there a *nice* way to detect whether a stream is buffered
		if (!(os instanceof BufferedOutputStream || os instanceof ByteArrayOutputStream || os instanceof DataOutputStream)) {
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
			encoder.encodeEndDocument();
			encoder.flush();
		} catch (Exception e) {
			throw new SAXException("endDocument", e);
		}
	}

	public void endElement(String uri, String local, String raw)
			throws SAXException {
		try {
			encoder.encodeEndElement();
		} catch (Exception e) {
			throw new SAXException("endElement=" + raw, e);
		}
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		try {
			encoder.encodeCharacters(new StringValue(new String(ch, start, length)));
		} catch (Exception e) {
			throw new SAXException("characters=" + new String(ch, start, length), e);
		}
	}

}
