/*
 * Copyright (C) 2007-2014 Siemens AG
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

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DefaultHandler2;
import org.xml.sax.ext.LexicalHandler;

/**
 * Serializes SAX events to EXI stream.
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9.3-SNAPSHOT
 */

public class SAXHandler extends DefaultHandler2 {
	ContentHandler contentHandler;

	public SAXHandler(ContentHandler ch) {
		this.contentHandler = ch;
	}

	@Override
	public void startDocument() throws SAXException {
		contentHandler.startDocument();
	}

	@Override
	public void endDocument() throws SAXException {
		contentHandler.endDocument();
	}

	@Override
	public void startPrefixMapping(String prefix, String uri)
			throws SAXException {
		contentHandler.startPrefixMapping(prefix, uri);
	}

	@Override
	public void endPrefixMapping(String prefix) throws SAXException {
		contentHandler.endPrefixMapping(prefix);
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		contentHandler.startElement(uri, localName, qName, attributes);
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		contentHandler.endElement(uri, localName, qName);
	}

	@Override
	public void characters(char ch[], int start, int length)
			throws SAXException {
		contentHandler.characters(ch, start, length);
	}

	@Override
	public void processingInstruction(String target, String data)
			throws SAXException {
		contentHandler.processingInstruction(target, data);
	}

	@Override
	public void comment(char ch[], int start, int length) throws SAXException {
		if (contentHandler instanceof LexicalHandler) {
			((LexicalHandler) contentHandler).comment(ch, start, length);
		}
	}

}
