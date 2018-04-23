/*
 * Copyright (c) 2007-2018 Siemens AG
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

package com.siemens.ct.exi.main.api.dom;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Attr;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.xml.sax.Attributes;
import org.xml.sax.DTDHandler;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.DefaultHandler;

import com.siemens.ct.exi.core.Constants;

/**
 * Wraps SAX events and build DOM based on it.
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 1.0.0
 */

public class SaxToDomHandler extends DefaultHandler implements LexicalHandler,
		DTDHandler, DeclHandler {

	protected Document document;
	protected DocumentFragment docFragment;

	protected DOMImplementation domImplementation;
	protected boolean fragment;

	protected Node currentNode;

	protected DocumentType dt;

	protected List<PrefixMapping> prefixes;

	public SaxToDomHandler(DOMImplementation domImplementation, boolean fragment) {
		this.domImplementation = domImplementation;
		this.fragment = fragment;

		prefixes = new ArrayList<PrefixMapping>();
	}

	protected Document checkDocument() {
		if (document == null) {
			// create empty document
			document = domImplementation.createDocument(null, null, dt);

			if (fragment) {
				docFragment = document.createDocumentFragment();
				currentNode = docFragment;
			} else {
				currentNode = document;
			}
		}
		return document;
	}

	public Document getDocument() {
		return this.document;
	}

	public DocumentFragment getDocumentFragment() {
		return this.docFragment;
	}

	public void startElement(String uri, String name, String qName,
			Attributes attrs) throws SAXException {
		// create element
		Element element = checkDocument().createElementNS(uri, qName);

		// add NS declarations
		for (int i = 0; i < prefixes.size(); i++) {
			PrefixMapping pm = prefixes.get(i);
			String qname = pm.prefix.length() == 0 ? Constants.XML_NS_ATTRIBUTE
					: Constants.XML_NS_ATTRIBUTE + ":" + pm.prefix;
			Attr attr = checkDocument().createAttributeNS(
					Constants.XML_NS_ATTRIBUTE_NS_URI, qname);
			attr.setValue(pm.uri);
			element.setAttributeNodeNS(attr);
		}
		prefixes.clear(); // re-set

		// add attribute per attribute
		for (int i = 0; i < attrs.getLength(); ++i) {
			String value = attrs.getValue(i);
			String ns_uri = attrs.getURI(i);
			// String localName = attrs.getLocalName(i);
			String qname = attrs.getQName(i);
			Attr attr = checkDocument().createAttributeNS(ns_uri, qname);
			// Attr attr = document.createAttribute(name);
			attr.setValue(value);
			element.setAttributeNodeNS(attr);
		}

		// add initially created element to tree, and adjust current node
		currentNode.appendChild(element);
		currentNode = element;
	}

	static class PrefixMapping {
		public final String prefix;
		public final String uri;

		public PrefixMapping(String prefix, String uri) {
			this.prefix = prefix;
			this.uri = uri;
		}
	}

	public void startPrefixMapping(String prefix, String uri)
			throws SAXException {
		// System.out.println("PfxMapping " + prefix + " --> " + uri);
		prefixes.add(new PrefixMapping(prefix, uri));
	}

	public void endElement(String uri, String name, String qName) {
		// adjust current node for subsequent operations
		currentNode = currentNode.getParentNode();
	}

	public void characters(char[] ch, int start, int length) {
		if (length > 0) {
			// add new text node
			String ss = new String(ch, start, length);
			Text text = checkDocument().createTextNode(ss);
			currentNode.appendChild(text);
		}
	}

	public void processingInstruction(String target, String data) {
		// add new processing instruction
		ProcessingInstruction pi = checkDocument().createProcessingInstruction(
				target, data);
		currentNode.appendChild(pi);
	}

	public void comment(char[] ch, int start, int length) throws SAXException {
		// add comment node
		Comment cm = checkDocument().createComment(
				new String(ch, start, length));
		currentNode.appendChild(cm);
	}

	public void startDTD(String name, String publicId, String systemId)
			throws SAXException {
		dt = domImplementation.createDocumentType(name, publicId, systemId);

		checkDocument();

		// currentNode.appendChild(dt);
		// document.appendChild(dt);
	}

	public void endDTD() throws SAXException {
		dt = null;
	}

	public void endCDATA() throws SAXException {
	}

	public void startCDATA() throws SAXException {
	}

	public void endEntity(String name) throws SAXException {
	}

	public void startEntity(String name) throws SAXException {
	}

	/*
	 * DTD Handler
	 */
	public void notationDecl(String name, String publicId, String systemId)
			throws SAXException {
		// TODO notationDecl

	}

	public void unparsedEntityDecl(String name, String publicId,
			String systemId, String notationName) throws SAXException {
		// TODO unparsedEntityDecl
	}

	/*
	 * Decl-Handler
	 */

	public void attributeDecl(String eName, String aName, String type,
			String mode, String value) throws SAXException {
		// TODO attributeDecl
	}

	public void elementDecl(String name, String model) throws SAXException {
		// TODO elementDecl
	}

	public void externalEntityDecl(String name, String publicId, String systemId)
			throws SAXException {
		// TODO externalEntityDecl
	}

	public void internalEntityDecl(String name, String value)
			throws SAXException {
		if (dt != null) {
			// NamedNodeMap nnm = dt.getEntities();
			EntityReference er = checkDocument().createEntityReference(name);
			er.setNodeValue(value);
		}
		// TODO internalEntityDecl
	}

	// TODO error handlers

}
