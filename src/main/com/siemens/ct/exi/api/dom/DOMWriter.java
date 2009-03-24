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

import java.io.OutputStream;

import javax.xml.XMLConstants;

import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;

import com.siemens.ct.exi.EXIEncoder;
import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.FidelityOptions;
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

public class DOMWriter {
	protected EXIFactory factory;
	protected EXIEncoder encoder;

	// attributes
	private AttributeList exiAttributes;

	protected boolean preservePrefixes;
	protected boolean preserveWhitespaces;
	protected boolean preserveComments;
	protected boolean preservePIs;

	public DOMWriter(EXIFactory factory) {
		this.factory = factory;
		this.encoder = factory.createEXIEncoder();

		// attribute list
		AttributeFactory attFactory = AttributeFactory.newInstance();
		exiAttributes = attFactory.createAttributeListInstance(factory);

		// preserve options
		preservePrefixes = factory.getFidelityOptions().isFidelityEnabled(
				FidelityOptions.FEATURE_PREFIX);
		preserveWhitespaces = factory.getFidelityOptions().isFidelityEnabled(
				FidelityOptions.FEATURE_WS);
		preserveComments = factory.getFidelityOptions().isFidelityEnabled(
				FidelityOptions.FEATURE_COMMENT);
		preservePIs = factory.getFidelityOptions().isFidelityEnabled(
				FidelityOptions.FEATURE_PI);
	}

	public void setOutput(OutputStream os) throws EXIException {
		encoder.setOutput(os, factory.isEXIBodyOnly());
	}

	public void encode(Document doc) throws EXIException {
		encoder.encodeStartDocument();

		Element root = doc.getDocumentElement();

		// previous nodes
		// etc. such as comments and insignificant whitespaces
		// Node prev = root;
		// while (root.getPreviousSibling() != null) {
		// prev = root.getPreviousSibling();
		//
		// switch (prev.getNodeType()) {
		// case Node.TEXT_NODE:
		// String value = prev.getNodeValue();
		// if (preserveWhitespaces || (value = value.trim()).length() > 0) {
		// encoder.encodeCharacters(value);
		// }
		// break;
		// case Node.COMMENT_NODE:
		// if (preserveComments) {
		// String c = n.getNodeValue();
		// encoder.encodeComment(c.toCharArray(), 0, c.length());
		// }
		// break;
		// }
		// }
		encodeNode(root);

		// next nodes
		// etc. such as comments and insignificant whitespaces

		encoder.encodeEndDocument();
	}
	
	public void encodeFragment(DocumentFragment docFragment) throws EXIException {
		encoder.encodeStartDocument();
		
		NodeList nl = docFragment.getChildNodes();
		for(int i=0;i<nl.getLength(); i++) {
			encodeNode(nl.item(i));
		}
		encoder.encodeEndDocument();
	}

	public void encode(Node n) throws EXIException {
		if (n.getNodeType() == Node.DOCUMENT_NODE ) {
			encode((Document)n);
		} else if (n.getNodeType() == Node.DOCUMENT_FRAGMENT_NODE ) {
			encodeFragment((DocumentFragment)n);
		} else {
			encoder.encodeStartDocument();
			encodeNode(n);
			encoder.encodeEndDocument();
		}
	}
	
	protected void encodeNode(Node root) throws EXIException {
		assert (root.getNodeType() == Node.ELEMENT_NODE);

		String namespaceURI = root.getNamespaceURI() == null ? XMLConstants.NULL_NS_URI
				: root.getNamespaceURI();
		encoder.encodeStartElement(namespaceURI, root.getLocalName());

		if (preservePrefixes) {
			String pfx = root.getPrefix() == null ? XMLConstants.DEFAULT_NS_PREFIX
					: root.getPrefix();
			encoder.encodeStartElementPrefixMapping(namespaceURI, pfx);
		}

		// attributes
		exiAttributes.parse(root.getAttributes());

		// root.getOwnerDocument().getNamespaceURI();

		// NS
		for (int i = 0; i < exiAttributes.getNumberOfNamespaceDeclarations(); i++) {
			encoder.encodeNamespaceDeclaration(exiAttributes
					.getNamespaceDeclarationURI(i), exiAttributes
					.getNamespaceDeclarationPrefix(i));
		}

		// xsi:type
		if (exiAttributes.hasXsiType()) {
			encoder.encodeXsiType(exiAttributes.getXsiTypeURI(), exiAttributes
					.getXsiTypeLocalName(), exiAttributes.getXsiTypeRaw());
		}

		// xsi:nil
		if (exiAttributes.hasXsiNil()) {
			encoder.encodeXsiNil(exiAttributes.getXsiNil());
		}

		// AT
		for (int i = 0; i < exiAttributes.getNumberOfAttributes(); i++) {
			encoder.encodeAttribute(exiAttributes.getAttributeURI(i),
					exiAttributes.getAttributeLocalName(i), exiAttributes
							.getAttributeValue(i));
		}

		NodeList children = root.getChildNodes();

		for (int i = 0; i < children.getLength(); i++) {
			Node n = children.item(i);
			switch (n.getNodeType()) {
			case Node.ELEMENT_NODE:
				encodeNode(n);
				break;
			case Node.ATTRIBUTE_NODE:
				break;
			case Node.TEXT_NODE:
				String value = n.getNodeValue();
				if (preserveWhitespaces || (value = value.trim()).length() > 0) {
					encoder.encodeCharacters(value);
				}
				break;
			case Node.COMMENT_NODE:
				if (preserveComments) {
					String c = n.getNodeValue();
					encoder.encodeComment(c.toCharArray(), 0, c.length());
				}
				break;
			case Node.ENTITY_REFERENCE_NODE:
				// TODO ER
				break;
			case Node.PROCESSING_INSTRUCTION_NODE:
				if (preservePIs) {
					ProcessingInstruction pi = (ProcessingInstruction) n;
					encoder.encodeProcessingInstruction(pi.getTarget(), pi
							.getData());
				}
				break;
			default:
				throw new EXIException("Unknown NodeType? " + n.getNodeType());
			}
		}

		encoder.encodeEndElement();
	}
}
