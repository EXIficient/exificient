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

import com.siemens.ct.exi.EXIBodyEncoder;
import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.EXIStreamEncoder;
import com.siemens.ct.exi.FidelityOptions;
import com.siemens.ct.exi.attributes.AttributeFactory;
import com.siemens.ct.exi.attributes.AttributeList;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.values.StringValue;

/**
 * Serializes an Document/DocumentFragment to an EXI stream.
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9.5
 */

public class DOMWriter {
	protected EXIFactory factory;
	protected EXIStreamEncoder exiStream;
	protected EXIBodyEncoder exiBody;

	// attributes
	private AttributeList exiAttributes;

	protected boolean preserveWhitespaces;
	protected boolean preserveComments;
	protected boolean preservePIs;

	public DOMWriter(EXIFactory factory) throws EXIException {
		this.factory = factory;

		this.exiStream = new EXIStreamEncoder(factory);

		// attribute list
		AttributeFactory attFactory = AttributeFactory.newInstance();
		exiAttributes = attFactory.createAttributeListInstance(factory);

		// preserve options
		preserveComments = factory.getFidelityOptions().isFidelityEnabled(
				FidelityOptions.FEATURE_COMMENT);
		preservePIs = factory.getFidelityOptions().isFidelityEnabled(
				FidelityOptions.FEATURE_PI);
	}

	public void setOutput(OutputStream os) throws EXIException, IOException {
		exiBody = exiStream.encodeHeader(os);
	}

	public void encode(Document doc) throws EXIException, IOException {
		exiBody.encodeStartDocument();

		// encode all child-nodes to retain root external
		// nodes such as as comments and insignificant whitespaces
		encodeChildNodes(doc.getChildNodes());

		exiBody.encodeEndDocument();
		exiBody.flush();
	}

	public void encodeFragment(DocumentFragment docFragment)
			throws EXIException, IOException {
		exiBody.encodeStartDocument();
		encodeChildNodes(docFragment.getChildNodes());
		exiBody.encodeEndDocument();
		exiBody.flush();
	}

	public void encode(Node n) throws EXIException, IOException {
		if (n.getNodeType() == Node.DOCUMENT_NODE) {
			encode((Document) n);
		} else if (n.getNodeType() == Node.DOCUMENT_FRAGMENT_NODE) {
			encodeFragment((DocumentFragment) n);
		} else {
			exiBody.encodeStartDocument();
			encodeNode(n);
			exiBody.encodeEndDocument();
			exiBody.flush();
		}
	}

	protected void encodeNode(Node root) throws EXIException, IOException {
		assert (root.getNodeType() == Node.ELEMENT_NODE);

		// start element
		String namespaceURI = root.getNamespaceURI() == null ? XMLConstants.NULL_NS_URI
				: root.getNamespaceURI();
		String localName = root.getLocalName();
		if (localName == null) {
			// namespace-awareness ??
			throw new EXIException("EXI requires namespace-aware DOM (nodes) "
					+ root.getNodeName());
		}

		String prefix = root.getPrefix();
		if (prefix == null) {
			prefix = XMLConstants.DEFAULT_NS_PREFIX;
		}
		exiBody.encodeStartElement(namespaceURI, localName, prefix);

		// attributes
		NamedNodeMap attributes = root.getAttributes();

		for (int i = 0; i < attributes.getLength(); i++) {
			Node at = attributes.item(i);

			// NS
			if (XMLConstants.XMLNS_ATTRIBUTE_NS_URI
					.equals(at.getNamespaceURI())) {
				String pfx = at.getPrefix() == null ? XMLConstants.DEFAULT_NS_PREFIX
						: at.getLocalName();
				exiAttributes.addNamespaceDeclaration(at.getNodeValue(), pfx);
			} else {
				exiAttributes.addAttribute(at.getNamespaceURI(), at.getLocalName(), at.getPrefix(), at.getNodeValue());
			}
			
		}
		
		exiBody.encodeAttributeList(exiAttributes);
		exiAttributes.clear();

		// children
		NodeList children = root.getChildNodes();
		encodeChildNodes(children);

		// end element
		exiBody.encodeEndElement();
	}

	protected void encodeChildNodes(NodeList children) throws EXIException,
			IOException {
		for (int i = 0; i < children.getLength(); i++) {
			Node n = children.item(i);
			switch (n.getNodeType()) {
			case Node.ELEMENT_NODE:
				encodeNode(n);
				break;
			case Node.ATTRIBUTE_NODE:
				break;
			case Node.TEXT_NODE:
				exiBody.encodeCharacters(new StringValue(n.getNodeValue()));
				break;
			case Node.COMMENT_NODE:
				if (preserveComments) {
					String c = n.getNodeValue();
					exiBody.encodeComment(c.toCharArray(), 0, c.length());
				}
				break;
			case Node.DOCUMENT_TYPE_NODE:
				DocumentType dt = (DocumentType) n;
				String publicID = dt.getPublicId() == null ? "" : dt
						.getPublicId();
				String systemID = dt.getSystemId() == null ? "" : dt
						.getSystemId();
				String text = dt.getInternalSubset() == null ? "" : dt
						.getInternalSubset();
				exiBody.encodeDocType(dt.getName(), publicID, systemID, text);
				break;
			case Node.ENTITY_REFERENCE_NODE:
				// checkPendingChars();
				// TODO ER
				break;
			case Node.CDATA_SECTION_NODE:
				// String cdata = n.getNodeValue();
				// exiBody.encodeCharacters(new
				// StringValue(Constants.CDATA_START
				// + cdata + Constants.CDATA_END));
				exiBody.encodeCharacters(new StringValue(n.getNodeValue()));
				break;
			case Node.PROCESSING_INSTRUCTION_NODE:
				if (preservePIs) {
					ProcessingInstruction pi = (ProcessingInstruction) n;
					exiBody.encodeProcessingInstruction(pi.getTarget(),
							pi.getData());
				}
				break;
			default:
				System.err.println("[WARNING] Unhandled DOM NodeType: "
						+ n.getNodeType());
				// throw new EXIException("Unknown NodeType? " +
				// n.getNodeType());
			}
		}
	}
}
