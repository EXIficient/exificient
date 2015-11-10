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

package com.siemens.ct.exi.util;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.XMLReaderFactory;


public class SimpleDocTypeParser implements LexicalHandler, ContentHandler, DeclHandler {

	XMLReader xmlReader;
	
	public String name;
	public String publicID;
	public String systemID;
	public String text;
	
	public SimpleDocTypeParser() throws SAXException {

		xmlReader = XMLReaderFactory.createXMLReader();

		xmlReader.setContentHandler(this);
		
		// LexicalHandler
		xmlReader.setProperty(
		 "http://xml.org/sax/properties/lexical-handler", this);
		
		// DeclHandler
		xmlReader.setProperty(
				"http://xml.org/sax/properties/declaration-handler", this);
		
		// DTD
		xmlReader.setFeature("http://xml.org/sax/features/resolve-dtd-uris",
				false);
		// *skip* resolving entities like DTDs
		xmlReader.setEntityResolver(new NoEntityResolver());

//		xmlReader.setProperty(
//				"http://xml.org/sax/properties/declaration-handler", dh);
		
	}

	public void parse(String docTypeDecl) throws IOException, SAXException {
		
		StringBuilder dt = new StringBuilder();
		dt.append(docTypeDecl);
		dt.append("<foo />");
		Reader r = new StringReader(dt.toString());
		xmlReader.parse(new InputSource(r));
	}

	public void startDTD(String name, String publicId, String systemId)
			throws SAXException {
		this.name = name;
		this.publicID = publicId == null ? "" : publicId;
		this.systemID = systemId == null ? "" : systemId;
		this.text = "";
		
	}

	public void endDTD() throws SAXException {
	}

	public void startEntity(String name) throws SAXException {
	}

	public void endEntity(String name) throws SAXException {
	}

	public void startCDATA() throws SAXException {
	}

	public void endCDATA() throws SAXException {
	}
	

	public void comment(char[] ch, int start, int length) throws SAXException {
	}

	
	/*
	 * CONTENT handler
	 */	
	public void setDocumentLocator(Locator locator) {
	}

	public void startDocument() throws SAXException {
	}

	public void endDocument() throws SAXException {
	}

	public void startPrefixMapping(String prefix, String uri)
			throws SAXException {
	}

	public void endPrefixMapping(String prefix) throws SAXException {
	}

	public void startElement(String uri, String localName, String qName,
			Attributes atts) throws SAXException {
	}

	public void endElement(String uri, String localName, String qName)
			throws SAXException {
	}

	public void characters(char[] ch, int start, int length)
			throws SAXException {
		this.text += new String(ch, start, length);
	}

	public void ignorableWhitespace(char[] ch, int start, int length)
			throws SAXException {
	}

	public void processingInstruction(String target, String data)
			throws SAXException {
	}

	public void skippedEntity(String name) throws SAXException {
	}

	
	/*
	 * DeclHandler
	 */
	public void elementDecl(String name, String model) throws SAXException {
		// e.g. <!ELEMENT Hello (#PCDATA)>
		// --> name == Hello && model == (#PCDATA) <--
		text += "<!ELEMENT " + name + " " + model + "> ";
	}

	public void attributeDecl(String eName, String aName, String type,
			String mode, String value) throws SAXException {
		// e.g. <!ATTLIST TVSCHEDULE NAME CDATA #REQUIRED>
		text += "<!ATTLIST " + eName + " " + aName + " " + type
				+ " " + mode + "> ";
	}

	public void internalEntityDecl(String name, String value)
			throws SAXException {
		// e.g. <!ENTITY eacute "&#xE9;">
		text += "<!ENTITY " + name + " \"" + value + "\"> ";
	}

	public void externalEntityDecl(String name, String publicId, String systemId)
			throws SAXException {
		// e.g., <!ENTITY ent SYSTEM "entityReference2-er.xml">
		if (publicId == null) {
			text += "<!ENTITY " + name + " SYSTEM \"" + systemId + "\"> ";
		} else {
			text += "<!ENTITY " + name + " PUBLIC \"" + systemId + "\"> ";
		}
	}

}
