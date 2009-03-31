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

package com.siemens.ct.exi.core.sax;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.ext.DefaultHandler2;

import com.siemens.ct.exi.EXIEncoder;
import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.FidelityOptions;
import com.siemens.ct.exi.api.sax.EXIWriter;
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

public class SAXEncoder extends DefaultHandler2 implements EXIWriter {
	protected EXIEncoder encoder;

	// buffers the characters of the characters() callback
	protected StringBuilder sbChars;

	// encodes collected char callbacks
	protected AbstractCharactersEncoder charEncoder;

	// attributes
	private AttributeList exiAttributes;
	
	//	prefix mappings
	protected List<String> prefixMappingPfx;
	protected List<String> prefixMappingURI;

	public SAXEncoder(EXIFactory factory) {
		this.encoder = factory.createEXIEncoder();

		// initialize
		sbChars = new StringBuilder();

		// whitespace characters required ?
		if (factory.getFidelityOptions().isFidelityEnabled(
				FidelityOptions.FEATURE_WS)) {
			charEncoder = new CharactersEncoderWhitespaceAware(encoder, sbChars);
		} else {
			charEncoder = new CharactersEncoderWhitespaceLess(encoder, sbChars);
		}
		
		// NS mappings
		prefixMappingPfx = new ArrayList<String>();
		prefixMappingURI = new ArrayList<String>();

		// attribute list
		AttributeFactory attFactory = AttributeFactory.newInstance();
		exiAttributes = attFactory.createAttributeListInstance(factory);
	}

	public void setOutput(OutputStream os, boolean exiBodyOnly)
			throws EXIException {
		encoder.setOutput(os, exiBodyOnly);
	}

	@Override
	public void startPrefixMapping(String prefix, String uri)
			throws SAXException {		
		prefixMappingPfx.add(prefix);
		prefixMappingURI.add(uri);
	}

//	@Override
//	public void endPrefixMapping(String prefix) throws SAXException {
//		globalPrefixMapping.remove(prefix);
//	}

	public void startElement(String uri, String local, String raw,
			Attributes attributes) throws SAXException {
		try {
			checkPendingCharacters();

			// start element
			encoder.encodeStartElement(uri, local, raw);
			
			//	handle NS declarations
			handleNamespaceDeclarations();
			
			// attributes
			if (attributes != null && attributes.getLength() > 0) {
				handleAttributes(attributes);
			}
		} catch (EXIException e) {
			throw new SAXException("startElement: " + raw, e);
		}
	}
	
	protected void handleNamespaceDeclarations() throws EXIException {
		assert(prefixMappingPfx.size()==prefixMappingURI.size());
		
		for(int i=0; i<prefixMappingPfx.size(); i++) {
			encoder.encodeNamespaceDeclaration(prefixMappingURI.get(i), prefixMappingPfx.get(i));
		}
		
		prefixMappingPfx.clear();
		prefixMappingURI.clear();
	}

//	protected void startElementOnly(String uri, String local)
//			throws EXIException {
//		checkPendingCharacters();
//
//		// no prefix mapping
//		encoder.encodeStartElement(uri, local);
//	}

	/*
	 * TODO 6. Encoding EXI Streams Namespace (NS) and attribute (AT) events are
	 * encoded in a specific order following the associated start element (SE)
	 * event. Namespace (NS) events are encoded first followed by the
	 * AT(xsi:type) event if present, followed by the AT(xsi:nil) event if
	 * present, followed by the rest of the attribute (AT) events.
	 * 
	 * When schema-informed grammars are used for processing an element,
	 * attribute events occur in lexical order in the stream sorted first by
	 * qname localName then by qname uri. Otherwise, when built-in element
	 * grammars are used, attribute events can occur in any order. Namespace
	 * (NS) events can occur in any order regardless of the grammars used for
	 * processing the associated element.
	 */
	protected void handleAttributes(Attributes attributes) throws EXIException {
		// exiAttributes.parse(attributes, this.globalPrefixMapping);
		exiAttributes.parse(attributes);
		
		// TODO remove NS event & use start/end PrefixMapping only
		// 1. Namespace Declarations
		// for ( int i = 0; i < exiAttributes.getNumberOfNamespaceDeclarations (
		// ); i++ )
		// {
		// encoder.encodeNamespaceDeclaration (
		// exiAttributes.getNamespaceDeclarationURI ( i ), exiAttributes
		// .getNamespaceDeclarationPrefix ( i ) );
		// }

		// 2. XSI-Type
		if (exiAttributes.hasXsiType()) {
			
			encoder.encodeXsiType(exiAttributes.getXsiTypeRaw());
		}

		// 3. XSI-Nil
		if (exiAttributes.hasXsiNil()) {
			encoder.encodeXsiNil(exiAttributes.getXsiNil());
		}

		// 4. Remaining Attributes
		// TODO AT prefix encoding
		for (int i = 0; i < exiAttributes.getNumberOfAttributes(); i++) {
			encoder.encodeAttribute(exiAttributes.getAttributeURI(i),
					exiAttributes.getAttributeLocalName(i), exiAttributes
							.getAttributeValue(i));
		}
	}

	/* Interface DefaultHandler */
	public void setDocumentLocator(Locator loc) {
	}

	public void startDocument() throws SAXException {
		try {
			encoder.encodeStartDocument();
		} catch (EXIException e) {
			throw new SAXException("startDocument", e);
		}
	}

	public void endDocument() throws SAXException {
		try {
			checkPendingCharacters();

			encoder.encodeEndDocument();
		} catch (EXIException e) {
			throw new SAXException("endDocument", e);
		}
	}

	public void processingInstruction(String target, String data)
			throws SAXException {
		try {
			checkPendingCharacters();

			encoder.encodeProcessingInstruction(target, data);
		} catch (EXIException e) {
			throw new SAXException("processingInstruction", e);
		}
	}

	public void endElement(String uri, String local, String raw)
			throws SAXException {
		try {
			checkPendingCharacters();

			encoder.encodeEndElement();
		} catch (EXIException e) {

			throw new SAXException("endElement=" + raw, e);
		}
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		sbChars.append(ch, start, length);
	}

	protected void checkPendingCharacters() throws EXIException {
		charEncoder.checkPendingChars();
	}

//	public void ignorableWhitespace(char[] ch, int start, int length)
//			throws SAXException {
//		// SAX is very clear that ignorableWhitespace is only called for
//		// "element-content-whitespace"s, which is defined in the context of
//		// DTD." +"
//		// [http://mail-archives.apache.org/mod_mbox/xerces-j-dev/200402.mbox/%3C20040202160336.9569.qmail@nagoya.betaversion.org%3E]
//	}
//
//	public void warning(SAXParseException e) {
//		// TODO Logging of warnings anyway ?
//	}

	/* Interface LexicalHandler */
	public void comment(char[] ch, int start, int length) throws SAXException {
		try {
			checkPendingCharacters();

			encoder.encodeComment(ch, start, length);
		} catch (EXIException e) {
			throw new SAXException("comment", e);
		}
	}

	public void startCDATA() throws SAXException {
		try {
			checkPendingCharacters();
		} catch (EXIException e) {
			throw new SAXException("startCDATA", e);
		}
	}

	public void endCDATA() throws SAXException {
		try {
			checkPendingCharacters();

		} catch (EXIException e) {
			throw new SAXException("endCDATA", e);
		}
	}

	public void startDTD(String name, String publicId, String systemId)
			throws SAXException {
		try {
			checkPendingCharacters();
		} catch (EXIException e) {
			throw new SAXException("startDTD", e);
		}
	}

	public void endDTD() throws SAXException {
		try {
			checkPendingCharacters();
		} catch (Exception e) {
			throw new SAXException("endDTD", e);
		}

	}

	/* Interface ErrorHandler */
	public void error(SAXParseException ex) {
		// TODO Should error logging be done anyway ?
	}

}
