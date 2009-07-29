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
import java.util.List;

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
 * @version 0.3.20090414
 */

public class SAXDecoder implements XMLReader {

	protected EXIFactory exiFactory;

	protected EXIDecoder decoder;

	protected ContentHandler contentHandler;
	protected DTDHandler dtdHandler;

	protected ErrorHandler errorHandler;

	protected static final String ATTRIBUTE_TYPE = "CDATA";

	protected AttributesImpl attributes = new AttributesImpl();

	protected String deferredStartElementUri;
	protected String deferredStartElementLocalName;
	protected List<String> eeQualifiedNames;

	public SAXDecoder(EXIFactory exiFactory) {
		this.exiFactory = exiFactory;
		this.decoder = exiFactory.createEXIDecoder();
		eeQualifiedNames = new ArrayList<String>();
	}

	protected void initForEachRun() {
		// deferred elements
		deferredStartElementUri = null;
		deferredStartElementLocalName = null;
		// ee list
		eeQualifiedNames.clear();
	}

	protected void startDeferredElement() throws SAXException {
		// context prefixes
		NamespaceSupport namespaces = decoder.getNamespaces();
		@SuppressWarnings("unchecked")
		Enumeration<String> declaredPrefixes = namespaces.getDeclaredPrefixes();
		while (declaredPrefixes.hasMoreElements()) {
			String pfx = declaredPrefixes.nextElement();
			String uri = namespaces.getURI(pfx);
			if (uri == null) {
				uri = XMLConstants.NULL_NS_URI;
			}
			contentHandler.startPrefixMapping(pfx, uri);
		}

		// start so far deferred start element
//		String qname = decoder.getQualifiedName(deferredStartElementUri,
//				deferredStartElementLocalName, decoder.getElementPrefix());
		String qname = decoder.getElementQName();		
		contentHandler.startElement(deferredStartElementUri,
				deferredStartElementLocalName, qname, attributes);
		// save qualified-name for EE)
		eeQualifiedNames.add(qname);
		// clear information
		deferredStartElementUri = null;
		deferredStartElementLocalName = null;
		attributes.clear();
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
			initForEachRun();

//			// first event ( SD ? )
//			decoder.inspectStream();

			while (decoder.hasNext()) {
				EventType eventType = decoder.next();
				
				switch (eventType) {
				/* START DOCUMENT */
				case START_DOCUMENT:
					decoder.decodeStartDocument();
					contentHandler.startDocument();
					break;
				/* START ELEMENT */
				case START_ELEMENT:
				case START_ELEMENT_NS:
				case START_ELEMENT_GENERIC:
				case START_ELEMENT_GENERIC_UNDECLARED:
					if (deferredStartElementUri != null) {
						startDeferredElement();
					}
					decoder.decodeStartElement();
					
					// set new deferred start element
					deferredStartElementUri = decoder.getElementURI();
					deferredStartElementLocalName = decoder
							.getElementLocalName();
					break;
				/* ATTRIBUTES */
				case ATTRIBUTE:
				case ATTRIBUTE_NS:
				case ATTRIBUTE_XSI_TYPE:
				case ATTRIBUTE_XSI_NIL:
				case ATTRIBUTE_INVALID_VALUE:
				case ATTRIBUTE_ANY_INVALID_VALUE:
				case ATTRIBUTE_GENERIC:
				case ATTRIBUTE_GENERIC_UNDECLARED:
					decoder.decodeAttribute();

					String attributeURI = decoder.getAttributeURI();
					String attributeLocalName = decoder.getAttributeLocalName();
					String attributeQname = decoder.getAttributeQName();
					String attributeValue = new String(decoder.getAttributeValue());

					attributes.addAttribute(attributeURI, attributeLocalName,
							attributeQname, ATTRIBUTE_TYPE, attributeValue);
					break;
				/* CHARACTERS */
				case CHARACTERS:
				case CHARACTERS_GENERIC:
				case CHARACTERS_GENERIC_UNDECLARED:
					if (deferredStartElementUri != null) {
						startDeferredElement();
					}
					decoder.decodeCharacters();
					char[] chars = decoder.getCharacters();
					contentHandler.characters(chars, 0, chars.length);
					break;
				/* END-ELEMENTS */
				case END_ELEMENT:
				case END_ELEMENT_UNDECLARED:
					if (deferredStartElementUri != null) {
						startDeferredElement();
					}
					decoder.decodeEndElement();
					
					// fetch scope before popping rule etc.
					String eeUri = decoder.getElementURI();
					String eeLocalName = decoder.getElementLocalName();
					
					// start sax end element
					contentHandler.endElement(eeUri, eeLocalName,
							eeQualifiedNames
									.remove(eeQualifiedNames.size() - 1));
					break;
				/* NAMESPACE_DECLARATION */
				case NAMESPACE_DECLARATION:
					decoder.decodeNamespaceDeclaration();
					// Note: Prefix declaration etc. is done internally
					break;
				/* MISC */
				case DOC_TYPE:
					decoder.decodeDocType();
					handleDocType();
					break;
				case ENTITY_REFERENCE:
					if (deferredStartElementUri != null) {
						startDeferredElement();
					}
					decoder.decodeEntityReference();
					handleEntityReference();
					break;
				case COMMENT:
					if (deferredStartElementUri != null) {
						startDeferredElement();
					}
					decoder.decodeComment();
					handleComment();
					break;
				case PROCESSING_INSTRUCTION:
					if (deferredStartElementUri != null) {
						startDeferredElement();
					}
					decoder.decodeProcessingInstruction();
					contentHandler.processingInstruction(decoder.getPITarget(),
							decoder.getPIData());
					break;
				case SELF_CONTAINED:
					decoder.decodeStartFragmentSelfContained();
					break;
				case END_DOCUMENT:
					// TODO SelfContained
					// decoder.decodeEndFragmentSelfContained();
					break;
				default:
					// ERROR
					throw new IllegalArgumentException(
							"Unknown event while decoding! "
									+ decoder.next());
				}

//				// inspect stream whether there is still content
//				decoder.inspectStream();
			}

			// END_DOCUMENT
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
