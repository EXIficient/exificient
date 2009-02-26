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
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.siemens.ct.exi.EXIEncoder;
import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.attributes.AttributeFactory;
import com.siemens.ct.exi.attributes.AttributeList;
import com.siemens.ct.exi.exceptions.EXIException;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.2.20080718
 */

public class DOMWriter {
	protected EXIFactory factory;
	protected EXIEncoder encoder;

	// attributes
	private AttributeList exiAttributes;

	public DOMWriter(EXIFactory factory) {
		this.factory = factory;
		this.encoder = factory.createEXIEncoder();

		// attribute list
		boolean isSchemaInformed = factory.getGrammar().isSchemaInformed();
		AttributeFactory attFactory = AttributeFactory.newInstance();
		exiAttributes = attFactory
				.createAttributeListInstance(isSchemaInformed);
	}

	public void setOutput(OutputStream os) throws EXIException {
		encoder.setOutput(os, factory.isEXIBodyOnly());
	}

	public void encode(Document doc) throws EXIException {
		encoder.encodeStartDocument();

		Element root = doc.getDocumentElement();
		encode(root);

		encoder.encodeEndDocument();
	}

	protected void encode(Node root) throws EXIException {
		assert (root.getNodeType() == Node.ELEMENT_NODE);

		String namespaceURI = root.getNamespaceURI() == null ? XMLConstants.NULL_NS_URI  : root.getNamespaceURI();
		encoder.encodeStartElement(namespaceURI, root.getNodeName());

		// attributes
		exiAttributes.parse(root.getAttributes());

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
				encode(n);
				break;
			case Node.ATTRIBUTE_NODE:
				break;
			case Node.TEXT_NODE:
				String value = n.getNodeValue();
				if ((value = value.trim()).length() > 0) {
					encoder.encodeCharacters(value);
				}
				break;
			case Node.COMMENT_NODE:
				// TODO CM
				break;
			case Node.ENTITY_REFERENCE_NODE:
				// TODO ER
				break;
			case Node.PROCESSING_INSTRUCTION_NODE:
				// TODO PI
				break;
			default:
				throw new EXIException("Unknown NodeType? " + n.getNodeType());
			}
		}

		encoder.encodeEndElement();
	}
}
