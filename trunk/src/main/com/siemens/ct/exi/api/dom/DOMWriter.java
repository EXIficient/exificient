/*
 * Copyright (C) 2007-2010 Siemens AG
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

import java.io.IOException;
import java.io.OutputStream;

import javax.xml.XMLConstants;

import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.DocumentType;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;

import com.siemens.ct.exi.Constants;
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
 * @version 0.4.20090414
 */

public class DOMWriter {
	protected EXIFactory factory;
	protected EXIEncoder encoder;

	// attributes
	private AttributeList exiAttributes;

	protected boolean preserveWhitespaces;
	protected boolean preserveComments;
	protected boolean preservePIs;
	
	//	collect char events (e.g. textA CM textB --> textA+textB if CM is not preserved)
	protected StringBuilder sbChars;

	public DOMWriter(EXIFactory factory) throws EXIException {
		this.factory = factory;
		this.encoder = factory.createEXIEncoder();

		// attribute list
		AttributeFactory attFactory = AttributeFactory.newInstance();
		exiAttributes = attFactory.createAttributeListInstance(factory);
		
		// characters
		sbChars = new StringBuilder();

		// preserve options
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

	public void encode(Document doc) throws EXIException, IOException {
		encoder.encodeStartDocument();

		// encode all child-nodes to retain root external
		// nodes such as as comments and insignificant whitespaces
		encodeChildNodes(doc.getChildNodes());

		encoder.encodeEndDocument();
		encoder.flush();
	}

	public void encodeFragment(DocumentFragment docFragment)
			throws EXIException, IOException {
		encoder.encodeStartDocument();
		encodeChildNodes(docFragment.getChildNodes());
		encoder.encodeEndDocument();
		encoder.flush();
	}

	public void encode(Node n) throws EXIException, IOException {
		if (n.getNodeType() == Node.DOCUMENT_NODE) {
			encode((Document) n);
		} else if (n.getNodeType() == Node.DOCUMENT_FRAGMENT_NODE) {
			encodeFragment((DocumentFragment) n);
		} else {
			encoder.encodeStartDocument();
			encodeNode(n);
			encoder.encodeEndDocument();
			encoder.flush();
		}
	}

	protected void encodeNode(Node root) throws EXIException, IOException {
		assert (root.getNodeType() == Node.ELEMENT_NODE);
		
		// start element
		String namespaceURI = root.getNamespaceURI() == null ? XMLConstants.NULL_NS_URI
				: root.getNamespaceURI();
		String localName = root.getLocalName();
		if (localName == null) {
			//	namespace-awareness ??
			throw new EXIException("EXI requires namespace-aware DOM (nodes) " + root.getNodeName());
		}

		encoder.encodeStartElement(namespaceURI, localName, root.getPrefix());

		// attributes
		NamedNodeMap attributes = root.getAttributes();
		exiAttributes.parse(root.getAttributes());

		// NS
		for (int i = 0; i < attributes.getLength(); i++) {
			Node at = attributes.item(i);

			// NS
			if (XMLConstants.XMLNS_ATTRIBUTE_NS_URI
					.equals(at.getNamespaceURI())) {
				String pfx = at.getPrefix() == null ? XMLConstants.DEFAULT_NS_PREFIX
						: at.getLocalName();

				encoder.encodeNamespaceDeclaration(at.getNodeValue(), pfx);
			}
		}

		// xsi:type
		if (exiAttributes.hasXsiType()) {
			encoder.encodeXsiType(exiAttributes.getXsiTypeRaw(), exiAttributes.getXsiTypePrefix());
		}

		// xsi:nil
		if (exiAttributes.hasXsiNil()) {
			encoder.encodeXsiNil(exiAttributes.getXsiNil(), exiAttributes.getXsiNilPrefix());
		}

		// AT
		for (int i = 0; i < exiAttributes.getNumberOfAttributes(); i++) {
			encoder.encodeAttribute(exiAttributes.getAttributeURI(i),
					exiAttributes.getAttributeLocalName(i), exiAttributes
							.getAttributePrefix(i), exiAttributes
							.getAttributeValue(i));
		}

		// children
		NodeList children = root.getChildNodes();
		encodeChildNodes(children);

		// end element
		encoder.encodeEndElement();
	}

	protected void encodeChildNodes(NodeList children) throws EXIException, IOException {
		for (int i = 0; i < children.getLength(); i++) {
			Node n = children.item(i);
			switch (n.getNodeType()) {
			case Node.ELEMENT_NODE:
				checkPendingChars();
				encodeNode(n);
				break;
			case Node.ATTRIBUTE_NODE:
				break;
			case Node.TEXT_NODE:
				sbChars.append(n.getNodeValue());
				break;
			case Node.COMMENT_NODE:
				if (preserveComments) {
					checkPendingChars();
					String c = n.getNodeValue();
					encoder.encodeComment(c.toCharArray(), 0, c.length());
				}
				break;
			case Node.DOCUMENT_TYPE_NODE:
				checkPendingChars();
				DocumentType dt = (DocumentType) n;
				String publicID = dt.getPublicId() == null ? "" : dt
						.getPublicId();
				String systemID = dt.getSystemId() == null ? "" : dt
						.getSystemId();
				String text = dt.getInternalSubset() == null ? "" : dt
						.getInternalSubset();
				encoder.encodeDocType(dt.getName(), publicID, systemID, text);
				break;
			case Node.ENTITY_REFERENCE_NODE:
				// checkPendingChars();
				// TODO ER
				break;
			case Node.CDATA_SECTION_NODE:
				checkPendingChars();
				String cdata = n.getNodeValue();
				encoder.encodeCharacters(Constants.CDATA_START + cdata + Constants.CDATA_END);
				break;
			case Node.PROCESSING_INSTRUCTION_NODE:
				if (preservePIs) {
					checkPendingChars();
					ProcessingInstruction pi = (ProcessingInstruction) n;
					encoder.encodeProcessingInstruction(pi.getTarget(), pi
							.getData());
				}
				break;
			default:
				System.err.println("[WARNING] Unhandled DOM NodeType: "
						+ n.getNodeType());
				// throw new EXIException("Unknown NodeType? " +
				// n.getNodeType());
			}
		}
		checkPendingChars();
	}
	
	protected void checkPendingChars() throws EXIException, IOException {
		if (sbChars.length() > 0) {
			encoder.encodeCharacters(sbChars.toString());
			sbChars.setLength(0);
		}
	}
}
