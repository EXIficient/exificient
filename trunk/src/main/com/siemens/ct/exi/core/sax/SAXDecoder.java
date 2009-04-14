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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.XMLConstants;

import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.NamespaceSupport;

import com.siemens.ct.exi.Constants;
import com.siemens.ct.exi.EXIDecoder;
import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.grammar.event.EventType;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.2.20090414
 */

public class SAXDecoder implements XMLReader {

	protected EXIFactory exiFactory;

	protected EXIDecoder decoder;

	protected ContentHandler contentHandler;
	protected DTDHandler dtdHandler;

	protected ErrorHandler errorHandler;

	protected static final String ATTRIBUTE_TYPE = "CDATA";

	protected AttributesImpl attributes = new AttributesImpl();

	// namespace & prefix support
	protected NamespaceSupport namespaces;
	protected Map<String, String> createdPrefixes;
	protected int createdPfxCnt = 1;

	protected String deferredStartElementUri;
	protected String deferredStartElementLocalName;
	protected List<String> eeQualifiedNames;

	public SAXDecoder(EXIFactory exiFactory) {
		this.exiFactory = exiFactory;
		this.decoder = exiFactory.createEXIDecoder();
	}

	protected void init() {
		// deferred elements
		deferredStartElementUri = null;
		deferredStartElementLocalName = null;
		// ee list
		eeQualifiedNames = new ArrayList<String>();

		// namespace & prefix support
		namespaces = decoder.getNamespaces();
		createdPrefixes = new HashMap<String, String>();
		createdPfxCnt = 1;
	}

	protected void checkDeferredStartElement() throws SAXException {
		if (deferredStartElementUri != null) {
			// prefix mapping
			@SuppressWarnings("unchecked")
			Enumeration<String> declaredPrefixes = namespaces
					.getDeclaredPrefixes();
			while (declaredPrefixes.hasMoreElements()) {
				String pfx = declaredPrefixes.nextElement();
				String uri = namespaces.getURI(pfx) == null ? XMLConstants.NULL_NS_URI
						: namespaces.getURI(pfx);
				contentHandler.startPrefixMapping(pfx, uri);
			}

			// start so far deferred start element
			// (+ save qualified-name for EE)
			String qname = getElementQualifiedName(deferredStartElementUri,
					deferredStartElementLocalName);
			eeQualifiedNames.add(qname);
			contentHandler.startElement(deferredStartElementUri,
					deferredStartElementLocalName, qname, attributes);

			// clear information
			deferredStartElementUri = null;
			deferredStartElementLocalName = null;
			attributes.clear();
		}
	}

	protected String getAttributeQualifiedName(String attributeURI,
			String attributeLocalName) throws SAXException {
		String pfx = decoder.getAttributePrefix();

		if (pfx == null) {
			if (attributeURI.equals(namespaces
					.getURI(XMLConstants.DEFAULT_NS_PREFIX))
					|| attributeURI.equals(XMLConstants.NULL_NS_URI)) {
				// default namespace
				pfx = XMLConstants.DEFAULT_NS_PREFIX;
			} else if ((pfx = namespaces.getPrefix(attributeURI)) == null) {
				// create unique prefix
				pfx = this.getUniquePrefix(attributeURI);
			}	
		}

		return (pfx.length() == 0 ? attributeLocalName
				: (pfx + Constants.COLON + attributeLocalName));
	}

	protected String getElementQualifiedName(String elementURI,
			String elementLocalName) throws SAXException {
		
		String pfx = this.decoder.getElementPrefix();

		if (pfx == null) {
			if (elementURI.equals(XMLConstants.NULL_NS_URI)
					|| elementURI.equals(namespaces
							.getURI(XMLConstants.DEFAULT_NS_PREFIX))) {
				// default namespace
				pfx = XMLConstants.DEFAULT_NS_PREFIX;
			} else if ((pfx = namespaces.getPrefix(elementURI)) == null) {
				// create unique prefix
				pfx = getUniquePrefix(elementURI);
			}
		}

		return (pfx.length() == 0 ? elementLocalName
				: (pfx + Constants.COLON + elementLocalName));
	}

	protected String getUniquePrefix(String uri) throws SAXException {
		String pfx;
		if (createdPrefixes.containsKey(uri)) {
			// *re-use* previous created prefix
			pfx = createdPrefixes.get(uri);
			// add to namespace context, if not already
			if (namespaces.getPrefix(uri) == null) {
				namespaces.declarePrefix(pfx, uri);
			}
		} else {
			// create *new* prefix
			do {
				pfx = "ns" + createdPfxCnt++;
			} while (namespaces.getURI(pfx) != null);

			namespaces.declarePrefix(pfx, uri);
			createdPrefixes.put(uri, pfx);
		}
		return pfx;
	}

	/*
	 * XML READER INTERFACE
	 */

	public ContentHandler getContentHandler() {
		return this.contentHandler;
	}

	public DTDHandler getDTDHandler() {
		return this.dtdHandler;
	}

	public EntityResolver getEntityResolver() {
		return null;
	}

	public ErrorHandler getErrorHandler() {
		return this.errorHandler;
	}

	public boolean getFeature(String name) throws SAXNotRecognizedException,
			SAXNotSupportedException {
		return false;
	}

	public Object getProperty(String name) throws SAXNotRecognizedException,
			SAXNotSupportedException {
		return null;
	}

	public void parse(String systemId) throws IOException, SAXException {
		FileInputStream fis = new FileInputStream(systemId);
		InputSource is = new InputSource(fis);
		this.parse(is);
	}

	public void parse(InputSource inputSource) throws IOException, SAXException {
		assert (inputSource != null);
		assert (decoder != null);

		try {
			// setup (bit) input stream
			InputStream inputStream = inputSource.getByteStream();
			decoder.setInputStream(inputStream, exiFactory.isEXIBodyOnly());

			if (contentHandler == null) {
				throw new SAXException("No content handler set!");
			}

			// init
			init();

			// first event ( SD ? )
			decoder.inspectEvent();

			while (decoder.hasNextEvent()) {
				EventType eventType = decoder.getNextEventType();
				switch (eventType) {
				/* START DOCUMENT */
				case START_DOCUMENT:
					decoder.decodeStartDocument();
					contentHandler.startDocument();
					break;
				/* START ELEMENT */
				case START_ELEMENT:
				case START_ELEMENT_GENERIC:
				case START_ELEMENT_GENERIC_UNDECLARED:
					checkDeferredStartElement();
					if (eventType == EventType.START_ELEMENT) {
						decoder.decodeStartElement();
					} else if (eventType == EventType.START_ELEMENT_GENERIC) {
						decoder.decodeStartElementGeneric();
					} else {
						// START_ELEMENT_GENERIC_UNDECLARED
						decoder.decodeStartElementGenericUndeclared();
					}
					// set new deferred start element
					deferredStartElementUri = decoder.getElementURI();
					deferredStartElementLocalName = decoder
							.getElementLocalName();
					break;
				/* NAMESPACE_DECLARATION */
				case NAMESPACE_DECLARATION:
					decoder.decodeNamespaceDeclaration();
					namespaces.declarePrefix(decoder.getNSPrefix(), decoder
							.getNSUri());
					break;
				/* ATTRIBUTES */
				case ATTRIBUTE:
				case ATTRIBUTE_INVALID_VALUE:
				case ATTRIBUTE_GENERIC:
				case ATTRIBUTE_GENERIC_UNDECLARED:
					if (eventType == EventType.ATTRIBUTE) {
						decoder.decodeAttribute();
					} else if (eventType == EventType.ATTRIBUTE_INVALID_VALUE) {
						decoder.decodeAttributeInvalidValue();
					} else if (eventType == EventType.ATTRIBUTE_GENERIC) {
						decoder.decodeAttributeGeneric();
					} else {
						// ATTRIBUTE_GENERIC_UNDECLARED
						decoder.decodeAttributeGenericUndeclared();
					}
					/* attribute handling */
					String attributeURI = decoder.getAttributeURI();
					String attributeLocalName = decoder.getAttributeLocalName();
					String attributeValue = decoder.getAttributeValue();

					attributes.addAttribute(attributeURI, attributeLocalName,
							getAttributeQualifiedName(attributeURI,
									attributeLocalName), ATTRIBUTE_TYPE,
							attributeValue);
					break;
				case ATTRIBUTE_XSI_TYPE:
					decoder.decodeXsiType();
					attributes
							.addAttribute(
									XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI,
									Constants.XSI_TYPE,
									getAttributeQualifiedName(
											XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI,
											Constants.XSI_TYPE),
									ATTRIBUTE_TYPE, getAttributeQualifiedName(
											decoder.getXsiTypeUri(), decoder
													.getXsiTypeName()));
					break;
				case ATTRIBUTE_XSI_NIL:
				case ATTRIBUTE_XSI_NIL_DEVIATION:
					String attributeXsiValue;
					if (eventType == EventType.ATTRIBUTE_XSI_NIL) {
						decoder.decodeXsiNil();
						attributeXsiValue = decoder.getXsiNil() ? Constants.DECODED_BOOLEAN_TRUE
								: Constants.DECODED_BOOLEAN_FALSE;
					} else {
						// ATTRIBUTE_XSI_NIL_DEVIATION
						decoder.decodeXsiNilDeviation();
						attributeXsiValue = decoder.getXsiNilDeviation();
					}
					attributes
							.addAttribute(
									XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI,
									Constants.XSI_NIL,
									getAttributeQualifiedName(
											XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI,
											Constants.XSI_NIL), ATTRIBUTE_TYPE,
									attributeXsiValue);
					break;
				/* CHARACTERS */
				case CHARACTERS:
				case CHARACTERS_GENERIC:
				case CHARACTERS_GENERIC_UNDECLARED:
					checkDeferredStartElement();
					if (eventType == EventType.CHARACTERS) {
						decoder.decodeCharacters();
					} else if (eventType == EventType.CHARACTERS_GENERIC) {
						decoder.decodeCharactersGeneric();
					} else {
						// CHARACTERS_GENERIC_UNDECLARED
						decoder.decodeCharactersGenericUndeclared();
					}
					contentHandler
							.characters(decoder.getCharacters().toCharArray(),
									0, decoder.getCharacters().length());
					break;
				/* END-ELEMENTS */
				case END_ELEMENT:
				case END_ELEMENT_UNDECLARED:
					checkDeferredStartElement();
					// fetch scope before popping rule etc.
					String eeUri = decoder.getScopeURI();
					String eeLocalName = decoder.getScopeLocalName();
					if (eventType == EventType.END_ELEMENT) {
						decoder.decodeEndElement();
					} else {
						// END_ELEMENT_UNDECLARED
						decoder.decodeEndElementUndeclared();
					}
					// start sax end element
					contentHandler.endElement(eeUri, eeLocalName,
							eeQualifiedNames
									.remove(eeQualifiedNames.size() - 1));
					break;
				/* MISC */
				case DOC_TYPE:
					decoder.decodeDocType();
					handleDocType();
					break;
				case ENTITY_REFERENCE:
					checkDeferredStartElement();
					decoder.decodeEntityReference();
					handleEntityReference();
					break;
				case COMMENT:
					checkDeferredStartElement();
					decoder.decodeComment();
					handleComment();
					break;
				case PROCESSING_INSTRUCTION:
					checkDeferredStartElement();
					decoder.decodeProcessingInstruction();
					contentHandler.processingInstruction(decoder.getPITarget(),
							decoder.getPIData());
					break;
				case SELF_CONTAINED:
					decoder.decodeStartFragmentSelfContained();
					break;
				case END_DOCUMENT: // SelfContained END_DOCUMENT
					decoder.decodeEndFragmentSelfContained();
					break;
				default:
					// ERROR
					throw new IllegalArgumentException(
							"Unknown event while decoding! "
									+ decoder.getNextEventType());
				}

				// inspect stream whether there is still content
				decoder.inspectEvent();
			}

			// case END_DOCUMENT
			decoder.decodeEndDocument();
			contentHandler.endDocument();
		} catch (EXIException e) {
			throw new SAXException("EXI", e);
		}
	}

	public void setContentHandler(ContentHandler handler) {
		this.contentHandler = handler;
	}

	public void setDTDHandler(DTDHandler handler) {
		this.dtdHandler = handler;
	}

	public void setEntityResolver(EntityResolver resolver) {

	}

	public void setErrorHandler(ErrorHandler handler) {
		this.errorHandler = handler;
	}

	public void setFeature(String name, boolean value)
			throws SAXNotRecognizedException, SAXNotSupportedException {
	}

	public void setProperty(String name, Object value)
			throws SAXNotRecognizedException, SAXNotSupportedException {
	}

	/*
	 * Hooks for Decl & Lexical Handler
	 */
	protected void handleDocType() throws SAXException, IOException {
	}

	protected void handleEntityReference() throws SAXException {
	}

	protected void handleComment() throws SAXException {
	}
}
