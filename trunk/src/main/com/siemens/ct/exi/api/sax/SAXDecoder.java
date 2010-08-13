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

package com.siemens.ct.exi.api.sax;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.xml.namespace.QName;

import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.AttributesImpl;

import com.siemens.ct.exi.Constants;
import com.siemens.ct.exi.EXIDecoder;
import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.core.PrefixMapping;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.grammar.event.EventType;
import com.siemens.ct.exi.values.Value;

/**
 * Parses EXI stream to SAX events.
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.5
 */

public class SAXDecoder implements XMLReader {

	protected EXIFactory exiFactory;

	protected EXIDecoder decoder;

	protected ContentHandler contentHandler;
	protected DTDHandler dtdHandler;
	protected LexicalHandler lexicalHandler;
	protected DeclHandler declarationHandler;
	protected ErrorHandler errorHandler;

	protected static final String ATTRIBUTE_TYPE = "CDATA";

	final static int DEFAULT_CHAR_BUFFER_SIZE = 4096;
	protected char[] cbuffer = new char[DEFAULT_CHAR_BUFFER_SIZE];

	protected AttributesImpl attributes;

	protected boolean namespaces = true;
	protected boolean namespacePrefixes = false;

	protected String seQNameAsString = Constants.EMPTY_STRING;
	protected String atQNameAsString = Constants.EMPTY_STRING;

	public SAXDecoder(EXIFactory exiFactory) throws EXIException {
		this.exiFactory = exiFactory;
		this.decoder = exiFactory.createEXIDecoder();
		attributes = new AttributesImpl();
	}

	protected void initForEachRun() {
		attributes.clear();
	}

	/*
	 * XML READER INTERFACE
	 */
	public void setContentHandler(ContentHandler handler) {
		this.contentHandler = handler;
	}

	public ContentHandler getContentHandler() {
		return this.contentHandler;
	}

	public void setDTDHandler(DTDHandler handler) {
		this.dtdHandler = handler;
	}

	public DTDHandler getDTDHandler() {
		return this.dtdHandler;
	}

	public EntityResolver getEntityResolver() {
		return null;
	}

	public void setEntityResolver(EntityResolver resolver) {

	}

	public void setErrorHandler(ErrorHandler handler) {
		this.errorHandler = handler;
	}

	public ErrorHandler getErrorHandler() {
		return this.errorHandler;
	}

	/*
	 * All XMLReaders are required to support setting
	 * http://xml.org/sax/features/namespaces to true and
	 * http://xml.org/sax/features/namespace-prefixes to false.
	 */
	public boolean getFeature(String name) throws SAXNotRecognizedException,
			SAXNotSupportedException {
		if ("http://xml.org/sax/features/namespaces".equals(name)) {
			return namespaces;
		} else if ("http://xml.org/sax/features/namespace-prefixes"
				.equals(name)) {
			return namespacePrefixes;
		} else {
			return false;
		}
	}

	public void setFeature(String name, boolean value)
			throws SAXNotRecognizedException, SAXNotSupportedException {
		if ("http://xml.org/sax/features/namespaces".equals(name)) {
			/* EXI needs namespaces to work properly */
			namespaces = value;
		} else if ("http://xml.org/sax/features/namespace-prefixes"
				.equals(name)) {
			namespacePrefixes = value;
		}
	}

	public void setProperty(String name, Object value)
			throws SAXNotRecognizedException, SAXNotSupportedException {
		if ("http://xml.org/sax/properties/lexical-handler".equals(name)) {
			this.lexicalHandler = (LexicalHandler) value;
		} else if ("http://xml.org/sax/properties/declaration-handler"
				.equals(name)) {
			this.declarationHandler = (DeclHandler) value;
		}
	}

	public Object getProperty(String name) throws SAXNotRecognizedException,
			SAXNotSupportedException {
		if ("http://xml.org/sax/properties/lexical-handler".equals(name)) {
			return this.lexicalHandler;
		} else if ("http://xml.org/sax/properties/declaration-handler"
				.equals(name)) {
			return this.declarationHandler;
		} else {
			return null;
		}
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

			// System.out.println("namespaces:" + namespaces);
			// System.out.println("namespacePrefixes: " + namespacePrefixes);

			// init
			initForEachRun();

			// process EXI events
			parseEXIEvents();

		} catch (EXIException e) {
			throw new SAXException("EXI", e);
		}
	}

	protected void parseEXIEvents() throws IOException, EXIException,
			SAXException {

		EventType eventType;

		String eeQNameAsString = Constants.EMPTY_STRING;
		List<PrefixMapping> eePrefixes = null;

		boolean deferredStartElement = false;

		while (decoder.hasNext()) {
			eventType = decoder.next();

			if (deferredStartElement) {
				switch (eventType) {
				/* ELEMENT CONTENT EVENTS */
				case START_ELEMENT:
				case START_ELEMENT_NS:
				case START_ELEMENT_GENERIC:
				case START_ELEMENT_GENERIC_UNDECLARED:
				case END_ELEMENT:
				case END_ELEMENT_UNDECLARED:
				case CHARACTERS:
				case CHARACTERS_GENERIC:
				case CHARACTERS_GENERIC_UNDECLARED:
				case DOC_TYPE:
				case ENTITY_REFERENCE:
				case COMMENT:
				case PROCESSING_INSTRUCTION:
					// No Attribute or NS event --> start deferred element
					handleDeferredStartElement();
					deferredStartElement = false;
				}
			}

			switch (eventType) {
			/* DOCUMENT */
			case START_DOCUMENT:
				decoder.decodeStartDocument();
				contentHandler.startDocument();
				break;
			case END_DOCUMENT:
				decoder.decodeEndDocument();
				contentHandler.endDocument();
				break;
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
				break;
			/* SELF_CONTAINED */
			case SELF_CONTAINED:
				decoder.decodeStartSelfContainedFragment();
				break;
			/* ELEMENT CONTENT EVENTS */
			/* START ELEMENT */
			case START_ELEMENT:
				decoder.decodeStartElement();
				// defer start element and keep on processing
				deferredStartElement = true;
				break;
			case START_ELEMENT_NS:
				decoder.decodeStartElementNS();
				// defer start element and keep on processing
				deferredStartElement = true;
				break;
			case START_ELEMENT_GENERIC:
				decoder.decodeStartElementGeneric();
				// defer start element and keep on processing
				deferredStartElement = true;
				break;
			case START_ELEMENT_GENERIC_UNDECLARED:
				decoder.decodeStartElementGenericUndeclared();
				// defer start element and keep on processing
				deferredStartElement = true;
				break;
			/* END ELEMENT */
			case END_ELEMENT:
				decoder.decodeEndElement();
				if (namespacePrefixes) {
					eeQNameAsString = decoder.getEndElementQNameAsString();
					// eePrefixes = decoder.getUndeclaredPrefixDeclarations();
				}
				eePrefixes = decoder.getUndeclaredPrefixDeclarations();
				handleEndElement(eeQNameAsString, eePrefixes);
				break;
			case END_ELEMENT_UNDECLARED:
				decoder.decodeEndElementUndeclared();
				if (namespacePrefixes) {
					eeQNameAsString = decoder.getEndElementQNameAsString();
					// eePrefixes = decoder.getUndeclaredPrefixDeclarations();
				}
				eePrefixes = decoder.getUndeclaredPrefixDeclarations();
				handleEndElement(eeQNameAsString, eePrefixes);
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
				contentHandler.processingInstruction(decoder.getPITarget(),
						decoder.getPIData());
				break;
			default:
				throw new RuntimeException("Unexpected EXI Event '" + eventType
						+ "' ");
			}
		}
	}

	protected final void startPrefixMappings(List<PrefixMapping> prefixes)
			throws SAXException {
		if (prefixes != null) {
			for (PrefixMapping pm : prefixes) {
				contentHandler.startPrefixMapping(pm.pfx, pm.uri);
			}
		}
	}

	protected final void endPrefixMappings(List<PrefixMapping> eePrefixes)
			throws SAXException {
		if (eePrefixes != null) {
			for (PrefixMapping pm : eePrefixes) {
				contentHandler.endPrefixMapping(pm.pfx);
			}
		}
	}

	/*
	 * SAX Content Handler
	 */
	protected void handleDeferredStartElement() throws SAXException,
			IOException, EXIException {

		// NOTE: getting qname needs to be done before starting prefix
		// mapping given that the qname may require a new qname prefix.
		if (namespacePrefixes) {
			seQNameAsString = decoder.getStartElementQNameAsString();
		}

		if (namespaces) {
			startPrefixMappings(decoder.getPrefixDeclarations());
		}

		/*
		 * the qualified name is required when the namespace-prefixes property
		 * is true, and is optional when the namespace-prefixes property is
		 * false (the default).
		 */

		// start so far deferred start element
		QName seQName = decoder.getElementQName();
		contentHandler.startElement(seQName.getNamespaceURI(), seQName
				.getLocalPart(), seQNameAsString, attributes);
		
		// clear AT information
		attributes.clear();
	}

	protected void handleEndElement(String eeQNameAsString,
			List<PrefixMapping> eePrefixes) throws SAXException, IOException {
		QName eeQName = decoder.getElementQName();

		// start sax end element
		contentHandler.endElement(eeQName.getNamespaceURI(), eeQName
				.getLocalPart(), eeQNameAsString);

		// endPrefixMapping
		endPrefixMappings(eePrefixes);
	}

	protected void handleAttribute() throws SAXException, IOException,
			EXIException {
		Value val = decoder.getAttributeValue();

		int slen = val.getCharactersLength();
		if (slen > cbuffer.length) {
			// need to create a new (expanded) buffer
			cbuffer = new char[slen];
		}

		// empty string if no qualified name is necessary
		if (namespacePrefixes) {
			atQNameAsString = decoder.getAttributeQNameAsString();
		}
		QName atQName = decoder.getAttributeQName();
		String sVal = val.toString(cbuffer, 0);
		attributes.addAttribute(atQName.getNamespaceURI(), atQName
				.getLocalPart(), atQNameAsString, ATTRIBUTE_TYPE, sVal);
	}

	protected void handleCharacters() throws SAXException, IOException {
		Value val = decoder.getCharactersValue();

		int slen = val.getCharactersLength();
		if (slen > cbuffer.length) {
			// need to create a new (expanded) buffer
			cbuffer = new char[slen];
		}

		// returns char array that contains value
		// Note: can be a different array than the one passed
		char[] sres = val.toCharacters(cbuffer, 0);

		contentHandler.characters(sres, 0, slen);
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
