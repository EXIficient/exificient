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

			// Start "Document"
			boolean hasNext = decoder.hasNext();
			assert (hasNext);
			EventType eventType = decoder.next();
			assert (eventType == EventType.START_DOCUMENT);
			decoder.decodeStartDocument();
			contentHandler.startDocument();
			
			//	grammar prefix mapping 
			NamespaceSupport namespaces = decoder.getNamespaces();
			@SuppressWarnings("unchecked")
			Enumeration<String> declaredPrefixes = namespaces
					.getDeclaredPrefixes();
			while (declaredPrefixes.hasMoreElements()) {
				String pfx = declaredPrefixes.nextElement();
				String uri = namespaces.getURI(pfx);
				if (uri == null) {
					uri = XMLConstants.NULL_NS_URI;
				}
				contentHandler.startPrefixMapping(pfx, uri);
			}
			

			// DocContent
			while (decoder.hasNext()) {
				eventType = decoder.next();
				parseElementContent(eventType);
			}

			// End "Document"
			decoder.decodeEndDocument();
			contentHandler.endDocument();

		} catch (EXIException e) {
			throw new SAXException("EXI", e);
		}
	}

	protected void parseStartTagContent() throws IOException, EXIException,
			SAXException {

		decoder.hasNext();
		EventType eventType = decoder.next();

		switch (eventType) {
		/* ATTRIBUTES */
		case ATTRIBUTE:
			decoder.decodeAttribute();
			handleAttribute();
			break;
		case ATTRIBUTE_NS:
			decoder.decodeAttributeNS();
			handleAttribute();
			break;
		case ATTRIBUTE_XSI_NIL:
			decoder.decodeAttributeXsiNil();
			handleAttribute();
			break;
		case ATTRIBUTE_XSI_TYPE:
			decoder.decodeAttributeXsiType();
			handleAttribute();
			break;
		case ATTRIBUTE_INVALID_VALUE:
			decoder.decodeAttributeInvalidValue();
			handleAttribute();
			break;
		case ATTRIBUTE_ANY_INVALID_VALUE:
			decoder.decodeAttributeAnyInvalidValue();
			handleAttribute();
			break;
		case ATTRIBUTE_GENERIC:
			decoder.decodeAttributeGeneric();
			handleAttribute();
			break;
		case ATTRIBUTE_GENERIC_UNDECLARED:
			decoder.decodeAttributeGenericUndeclared();
			handleAttribute();
			break;
		/* NAMESPACE DECLARATION */
		case NAMESPACE_DECLARATION:
			// Note: Prefix declaration etc. is done internally
			decoder.decodeNamespaceDeclaration();
			parseStartTagContent();
			break;
		/* SELF_CONTAINED */
		case SELF_CONTAINED:
			decoder.decodeStartFragmentSelfContained();
			parseStartTagContent();
			 break;
		/* ELEMENT CONTENT EVENTS */
		default:
			// NO Attribute or NS events anymore --> start deferred element

			// get qname as string
			//	NOTE: getting qname needs to be done before starting prefix mapping 
			// given that the qname may require a new qname
			String qname = decoder.getElementQName();
			
			
			//	TODO start prefix mapping differently!
			NamespaceSupport namespaces = decoder.getNamespaces();
			@SuppressWarnings("unchecked")
			Enumeration<String> declaredPrefixes = namespaces
					.getDeclaredPrefixes();
			while (declaredPrefixes.hasMoreElements()) {
				String pfx = declaredPrefixes.nextElement();
				String uri = namespaces.getURI(pfx);
				if (uri == null) {
					uri = XMLConstants.NULL_NS_URI;
				}
				contentHandler.startPrefixMapping(pfx, uri);
			}

			// start so far deferred start element
			contentHandler.startElement(deferredStartElementUri,
					deferredStartElementLocalName, qname, attributes);
			
			// save qualified-name for EE
			eeQualifiedNames.add(qname);
			// clear information
			attributes.clear();

			// process elementContent event
			parseElementContent(eventType);
		}
	}

	protected void parseElementContent(EventType eventType) throws IOException,
			EXIException, SAXException {
		switch (eventType) {
		/* START ELEMENT */
		case START_ELEMENT:
			decoder.decodeStartElement();
			handleStartElement();
			break;
		case START_ELEMENT_NS:
			decoder.decodeStartElementNS();
			handleStartElement();
			break;
		case START_ELEMENT_GENERIC:
			decoder.decodeStartElementGeneric();
			handleStartElement();
			break;
		case START_ELEMENT_GENERIC_UNDECLARED:
			decoder.decodeStartElementGenericUndeclared();
			handleStartElement();
			break;
		/* END ELEMENT */
		case END_ELEMENT:
			decoder.decodeEndElement();
			handleEndElement();
			break;
		case END_ELEMENT_UNDECLARED:
			decoder.decodeEndElementUndeclared();
			handleEndElement();
			break;
		/* CHARACTERS */
		case CHARACTERS:
			decoder.decodeCharacters();
			handleCharacters();
			break;
		case CHARACTERS_GENERIC:
			decoder.decodeCharactersGeneric();
			handleCharacters();
			break;
		case CHARACTERS_GENERIC_UNDECLARED:
			decoder.decodeCharactersGenericUndeclared();
			handleCharacters();
			break;
		/* MISC */
		case DOC_TYPE:
			decoder.decodeDocType();
			handleDocType();
			break;
		case ENTITY_REFERENCE:
			decoder.decodeEntityReference();
			handleEntityReference();
			break;
		case COMMENT:
			decoder.decodeComment();
			handleComment();
			break;
		case PROCESSING_INSTRUCTION:
			decoder.decodeProcessingInstruction();
			contentHandler.processingInstruction(decoder.getPITarget(), decoder
					.getPIData());
			break;
		default:
			throw new RuntimeException("Unexpected EXI Event '" + eventType
					+ "' ");
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
	 * SAX Content Handler
	 */
	protected void handleStartElement() throws SAXException, IOException,
			EXIException {
		// store new deferred start element
		deferredStartElementUri = decoder.getElementURI();
		deferredStartElementLocalName = decoder.getElementLocalName();
		
		// start processing startTag events
		parseStartTagContent();
	}

	protected void handleEndElement() throws SAXException, IOException {
		// start sax end element
		contentHandler.endElement(decoder.getElementURI(), decoder.getElementLocalName(), eeQualifiedNames
				.remove(eeQualifiedNames.size() - 1));
	}

	protected void handleAttribute() throws SAXException, IOException,
			EXIException {

		attributes.addAttribute(decoder.getAttributeURI(), decoder.getAttributeLocalName(),
				decoder.getAttributeQName(), ATTRIBUTE_TYPE, decoder.getAttributeValue());

		// keep processing startTag events
		parseStartTagContent();
	}

	protected void handleCharacters() throws SAXException, IOException {
		char[] chars = decoder.getCharacters();
		
		contentHandler.characters(chars, 0, chars.length);
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
