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

package com.siemens.ct.exi.api.dom;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

class SaxToDomHandler extends DefaultHandler {

	protected Document document;
	protected Node currentNode;

	public SaxToDomHandler(Document doc) {
		document = doc;
		currentNode = document;
	}

	public void startElement(String uri, String name, String qName,
			Attributes attrs) {
		// create element
		Element element = document.createElementNS(uri, qName);

		// add attribute per attribute
		for (int i = 0; i < attrs.getLength(); ++i) {
			String ns_uri = attrs.getURI(i);
			String qname = attrs.getQName(i);
			String value = attrs.getValue(i);
			Attr attr = document.createAttributeNS(ns_uri, qname);
			attr.setValue(value);
			element.setAttributeNodeNS(attr);
		}

		// add initially created element to tree, and adjust current node
		currentNode.appendChild(element);
		currentNode = element;
	}

	public void endElement(String uri, String name, String qName) {
		// adjust current node for subsequent operations
		currentNode = currentNode.getParentNode();
	}

	public void characters(char[] ch, int start, int length) {
		// add new text node
		Text text = document.createTextNode(new String(ch, start, length));
		currentNode.appendChild(text);
	}

	public void processingInstruction(String target, String data) {
		// add new processing instruction
		ProcessingInstruction pi = document.createProcessingInstruction(target,
				data);
		currentNode.appendChild(pi);
	}

	// TODO error handlers

}
