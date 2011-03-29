/*
 * Copyright (C) 2007-2011 Siemens AG
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
import com.siemens.ct.exi.EXIBodyDecoder;
import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.EXIStreamDecoder;
import com.siemens.ct.exi.FidelityOptions;
import com.siemens.ct.exi.core.container.DocType;
import com.siemens.ct.exi.core.container.NamespaceDeclaration;
import com.siemens.ct.exi.core.container.ProcessingInstruction;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.grammar.event.EventType;
import com.siemens.ct.exi.values.Value;

/**
 * Parses EXI stream to SAX events.
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.7
 */

public class SAXDecoder implements XMLReader {

	protected EXIFactory noOptionsFactory;
	protected EXIStreamDecoder exiStream;

	protected EXIBodyDecoder decoder;

	protected final static boolean USE_VALUE_CONTENT_HANDLER = false;

	protected ContentHandler contentHandler;
	protected ValueContentHandler valueContentHandler;
	protected DTDHandler dtdHandler;
	protected LexicalHandler lexicalHandler;
	protected DeclHandler declHandler;
	protected ErrorHandler errorHandler;

	protected static final String ATTRIBUTE_TYPE = "CDATA";

	final static int DEFAULT_CHAR_BUFFER_SIZE = 4096;
	protected char[] cbuffer = new char[DEFAULT_CHAR_BUFFER_SIZE];

	protected AttributesImpl attributes;

	protected boolean namespaces = true;
	protected boolean namespacePrefixes = false;
	protected boolean exiBodyOnly = false;

	public SAXDecoder(EXIFactory noOptionsFactory) throws EXIException {
		this.noOptionsFactory = noOptionsFactory;
		this.exiStream = new EXIStreamDecoder();
		this.valueContentHandler = new ValueContentHandler();
		attributes = new AttributesImpl();
		// switch namespace prefixes to TRUE if the stream preserves prefixes
		if (noOptionsFactory.getFidelityOptions().isFidelityEnabled(
				FidelityOptions.FEATURE_PREFIX)) {
			namespacePrefixes = true;
		}
	}

	protected void initForEachRun() {
		attributes.clear();
	}

	/*
	 * XML READER INTERFACE
	 */
	public void setContentHandler(ContentHandler handler) {
		this.contentHandler = handler;
		if (USE_VALUE_CONTENT_HANDLER) {
			this.valueContentHandler.setContentHandler(handler);
		}
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
		} else if (Constants.W3C_EXI_FEATURE_BODY_ONLY.equals(name)) {
			exiBodyOnly = value;
		} else {
			throw new SAXNotRecognizedException(name);
		}
	}

	public void setProperty(String name, Object value)
			throws SAXNotRecognizedException, SAXNotSupportedException {
		if ("http://xml.org/sax/properties/lexical-handler".equals(name)) {
			this.lexicalHandler = (LexicalHandler) value;
		} else if ("http://xml.org/sax/properties/declaration-handler"
				.equals(name)) {
			this.declHandler = (DeclHandler) value;
		} else {
			throw new SAXNotRecognizedException(name);
		}
	}

	public Object getProperty(String name) throws SAXNotRecognizedException,
			SAXNotSupportedException {
		if ("http://xml.org/sax/properties/lexical-handler".equals(name)) {
			return this.lexicalHandler;
		} else if ("http://xml.org/sax/properties/declaration-handler"
				.equals(name)) {
			return this.declHandler;
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
		assert (exiStream != null);

		if (contentHandler == null) {
			throw new SAXException("No content handler set!");
		}

		try {
			// setup (bit) input stream
			InputStream is = inputSource.getByteStream();

			if (exiBodyOnly) {
				// no EXI header
				decoder = noOptionsFactory.createEXIBodyDecoder();
				decoder.setInputStream(is);
			} else {
				// read header (default)
				decoder = exiStream.decodeHeader(noOptionsFactory, is);
			}

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
		List<NamespaceDeclaration> eePrefixes = null;

		QName deferredStartElement = null;

		while ((eventType = decoder.next()) != null) {

			if (deferredStartElement != null) {
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
					handleDeferredStartElement(deferredStartElement);
					deferredStartElement = null;
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
				handleAttribute(decoder.decodeAttribute());
				break;
			case ATTRIBUTE_NS:
				handleAttribute(decoder.decodeAttributeNS());
				break;
			case ATTRIBUTE_XSI_NIL:
				handleAttribute(decoder.decodeAttributeXsiNil());
				break;
			case ATTRIBUTE_XSI_TYPE:
				handleAttribute(decoder.decodeAttributeXsiType());
				break;
			case ATTRIBUTE_INVALID_VALUE:
				handleAttribute(decoder.decodeAttributeInvalidValue());
				break;
			case ATTRIBUTE_ANY_INVALID_VALUE:
				handleAttribute(decoder.decodeAttributeAnyInvalidValue());
				break;
			case ATTRIBUTE_GENERIC:
				handleAttribute(decoder.decodeAttributeGeneric());
				break;
			case ATTRIBUTE_GENERIC_UNDECLARED:
				handleAttribute(decoder.decodeAttributeGenericUndeclared());
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
				// defer start element and keep on processing
				deferredStartElement = decoder.decodeStartElement();
				break;
			case START_ELEMENT_NS:
				// defer start element and keep on processing
				deferredStartElement = decoder.decodeStartElementNS();
				break;
			case START_ELEMENT_GENERIC:
				// defer start element and keep on processing
				deferredStartElement = decoder.decodeStartElementGeneric();
				break;
			case START_ELEMENT_GENERIC_UNDECLARED:
				// defer start element and keep on processing
				deferredStartElement = decoder
						.decodeStartElementGenericUndeclared();
				break;
			/* END ELEMENT */
			case END_ELEMENT:
				eePrefixes = decoder.getDeclaredPrefixDeclarations();
				if (namespacePrefixes) {
					eeQNameAsString = decoder.getElementQNameAsString();
				}
				QName eeQName = decoder.decodeEndElement();
				handleEndElement(eeQName, eeQNameAsString, eePrefixes);
				break;
			case END_ELEMENT_UNDECLARED:
				eePrefixes = decoder.getDeclaredPrefixDeclarations();
				if (namespacePrefixes) {
					eeQNameAsString = decoder.getElementQNameAsString();
				}
				eeQName = decoder.decodeEndElementUndeclared();
				handleEndElement(eeQName, eeQNameAsString, eePrefixes);
				break;
			/* CHARACTERS */
			case CHARACTERS:
				handleCharacters(decoder.decodeCharacters());
				break;
			case CHARACTERS_GENERIC:
				handleCharacters(decoder.decodeCharactersGeneric());
				break;
			case CHARACTERS_GENERIC_UNDECLARED:
				handleCharacters(decoder.decodeCharactersGenericUndeclared());
				break;
			/* MISC */
			case DOC_TYPE:
				handleDocType(decoder.decodeDocType());
				break;
			case ENTITY_REFERENCE:
				handleEntityReference(decoder.decodeEntityReference());
				break;
			case COMMENT:
				handleComment(decoder.decodeComment());
				break;
			case PROCESSING_INSTRUCTION:
				ProcessingInstruction pi = decoder
						.decodeProcessingInstruction();
				contentHandler.processingInstruction(pi.target, pi.data);
				break;
			default:
				throw new RuntimeException("Unexpected EXI Event '" + eventType
						+ "' ");
			}
		}
	}

	protected final void startPrefixMappings(List<NamespaceDeclaration> prefixes)
			throws SAXException {
		if (prefixes != null) {
			for (NamespaceDeclaration ns : prefixes) {
				contentHandler.startPrefixMapping(ns.prefix, ns.namespaceURI);
			}
		}
	}

	protected final void endPrefixMappings(List<NamespaceDeclaration> eePrefixes)
			throws SAXException {
		if (eePrefixes != null) {
			for (NamespaceDeclaration ns : eePrefixes) {
				contentHandler.endPrefixMapping(ns.prefix);
			}
		}
	}

	/*
	 * SAX Content Handler
	 */
	protected void handleDeferredStartElement(QName deferredStartElement)
			throws SAXException, IOException, EXIException {

		if (namespaces) {
			startPrefixMappings(decoder.getDeclaredPrefixDeclarations());
		}

		/*
		 * the qualified name is required when the namespace-prefixes property
		 * is true, and is optional when the namespace-prefixes property is
		 * false (the default).
		 */
		String seQNameAsString = Constants.EMPTY_STRING;
		if (namespacePrefixes) {
			seQNameAsString = decoder.getElementQNameAsString();
		}

		// start so far deferred start element
		contentHandler.startElement(deferredStartElement.getNamespaceURI(),
				deferredStartElement.getLocalPart(), seQNameAsString,
				attributes);

		// clear AT information
		attributes.clear();
	}

	protected void handleEndElement(QName eeQName, String eeQNameAsString,
			List<NamespaceDeclaration> eePrefixes) throws SAXException,
			IOException {

		// start sax end element
		contentHandler.endElement(eeQName.getNamespaceURI(),
				eeQName.getLocalPart(), eeQNameAsString);

		// endPrefixMapping
		endPrefixMappings(eePrefixes);
	}

	protected final void ensureBufferCapacity(int reqSize) {
		if (reqSize > cbuffer.length) {
			int newSize = cbuffer.length;

			do {
				newSize = newSize << 2;
			} while (newSize < reqSize);

			cbuffer = new char[newSize];
		}
	}

	protected void handleAttribute(QName atQName) throws SAXException,
			IOException, EXIException {
		Value val = decoder.getAttributeValue();
		String sVal;

		if (USE_VALUE_CONTENT_HANDLER) {
			sVal = valueContentHandler.reportAttributeString(val);
		} else {
			int slen = val.getCharactersLength();
			ensureBufferCapacity(slen);
			sVal = val.toString(cbuffer, 0);
		}

		// empty string if no qualified name is necessary
		String atQNameAsString = Constants.EMPTY_STRING;
		if (namespacePrefixes) {
			atQNameAsString = decoder.getAttributeQNameAsString();
		}

		attributes.addAttribute(atQName.getNamespaceURI(),
				atQName.getLocalPart(), atQNameAsString, ATTRIBUTE_TYPE, sVal);
	}

	protected void handleCharacters(Value val) throws SAXException, IOException {
		if (USE_VALUE_CONTENT_HANDLER) {
			valueContentHandler.reportCharacters(val);
		} else {
			int slen = val.getCharactersLength();
			ensureBufferCapacity(slen);

			// returns char array that contains value
			// Note: can be a different array than the one passed
			char[] sres = val.toCharacters(cbuffer, 0);

			contentHandler.characters(sres, 0, slen);
		}
	}

	/*
	 * Hooks for Decl & Lexical Handler
	 */
	protected void handleDocType(DocType docType) throws SAXException,
			IOException {
	}

	protected void handleEntityReference(char[] erName) throws SAXException {
	}

	protected void handleComment(char[] comment) throws SAXException {
	}
}
