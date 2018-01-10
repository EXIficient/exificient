/*
 * Copyright (c) 2007-2018 Siemens AG
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

package com.siemens.ct.exi.main.api.sax;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;

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
import org.xml.sax.helpers.XMLReaderFactory;

import com.siemens.ct.exi.core.Constants;
import com.siemens.ct.exi.core.EXIBodyDecoder;
import com.siemens.ct.exi.core.EXIFactory;
import com.siemens.ct.exi.core.EXIStreamDecoder;
import com.siemens.ct.exi.core.FidelityOptions;
import com.siemens.ct.exi.core.context.QNameContext;
import com.siemens.ct.exi.core.container.DocType;
import com.siemens.ct.exi.core.container.NamespaceDeclaration;
import com.siemens.ct.exi.core.container.ProcessingInstruction;
import com.siemens.ct.exi.core.exceptions.EXIException;
import com.siemens.ct.exi.core.grammars.event.EventType;
import com.siemens.ct.exi.main.helpers.DefaultSchemaIdResolver;
import com.siemens.ct.exi.main.util.NoEntityResolver;
import com.siemens.ct.exi.core.values.ListValue;
import com.siemens.ct.exi.core.values.Value;
import com.siemens.ct.exi.core.values.ValueType;

/**
 * Parses EXI stream to SAX events.
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9.7-SNAPSHOT
 */

public class SAXDecoder implements XMLReader {

	protected EXIFactory noOptionsFactory;
	protected EXIStreamDecoder exiStream;

	protected EXIBodyDecoder decoder;

	protected ContentHandler contentHandler;
	protected DTDHandler dtdHandler;
	protected LexicalHandler lexicalHandler;
	protected DeclHandler declHandler;
	protected ErrorHandler errorHandler;

	protected static final String ATTRIBUTE_TYPE = "CDATA";

	final static int DEFAULT_CHAR_BUFFER_SIZE = 4096;
	protected char[] cbuffer = new char[DEFAULT_CHAR_BUFFER_SIZE];

	protected boolean namespaces = true;
	protected boolean namespacePrefixes = false;
	protected boolean exiBodyOnly = false;

	/* Helper for building strings */
	protected StringBuilder sbHelper;

	public SAXDecoder(EXIFactory noOptionsFactory) throws EXIException {
		this.noOptionsFactory = noOptionsFactory;
		if(noOptionsFactory.getSchemaIdResolver() == null) {
			// set default schemaId resolver
			noOptionsFactory.setSchemaIdResolver(new DefaultSchemaIdResolver());
		}
		this.exiStream = noOptionsFactory.createEXIStreamDecoder();
		/*
		 * Note: it looks like widely used APIs (Xerces, Saxon, ..) provide the
		 * textual qname even when
		 * http://xml.org/sax/features/namespace-prefixes is set to false
		 * http://
		 * sourceforge.net/projects/exificient/forums/forum/856596/topic/5839494
		 */
		// switch namespace prefixes to TRUE if the stream preserves prefixes
		if (noOptionsFactory.getFidelityOptions().isFidelityEnabled(
				FidelityOptions.FEATURE_PREFIX)) {
			namespacePrefixes = true;
		}
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
			// setup input stream
			InputStream is = inputSource.getByteStream();

			// systemId ?
			if (is == null && inputSource.getSystemId() != null) {
				is = new FileInputStream(inputSource.getSystemId());
			}
			if (is == null) {
				throw new EXIException("No valid input source " + is);
			}

			if (exiBodyOnly) {
				// no EXI header
				decoder = exiStream.getBodyOnlyDecoder(is);
				// decoder = noOptionsFactory.createEXIBodyDecoder();
				// decoder.setInputStream(is);
			} else {
				// read header (default)
				decoder = exiStream.decodeHeader(is);
			}

			// process EXI events
			parseEXIEvents();

		} catch (EXIException e) {
			throw new SAXException("EXI " + e.getLocalizedMessage(), e);
		}
	}

	protected void parseEXIEvents() throws IOException, EXIException,
			SAXException {

		EventType eventType;

		QNameContext deferredStartElement = null;
		boolean isStartElementDeferred = false;
		final AttributesImpl attributes = new AttributesImpl();

		while ((eventType = decoder.next()) != null) {

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
			case ATTRIBUTE_XSI_NIL:
				handleAttribute(decoder.decodeAttributeXsiNil(), attributes);
				break;
			case ATTRIBUTE_XSI_TYPE:
				handleAttribute(decoder.decodeAttributeXsiType(), attributes);
				break;
			case ATTRIBUTE:
			case ATTRIBUTE_NS:
			case ATTRIBUTE_GENERIC:
			case ATTRIBUTE_GENERIC_UNDECLARED:
			case ATTRIBUTE_INVALID_VALUE:
			case ATTRIBUTE_ANY_INVALID_VALUE:
				handleAttribute(decoder.decodeAttribute(), attributes);
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
			case START_ELEMENT_NS:
			case START_ELEMENT_GENERIC:
			case START_ELEMENT_GENERIC_UNDECLARED:
				// handle deferred element if any first
				if(isStartElementDeferred) {
					handleDeferredStartElement(deferredStartElement, attributes);
				}
				// defer start element and keep on processing
				deferredStartElement = decoder.decodeStartElement();
				isStartElementDeferred = true;
				// System.out.println("> SE: " + deferredStartElement);
				break;
			/* END ELEMENT */
			case END_ELEMENT:
			case END_ELEMENT_UNDECLARED:
				// handle deferred element if any first
				if(isStartElementDeferred) {
					handleDeferredStartElement(deferredStartElement, attributes);
					isStartElementDeferred = false;
				}

				/*
				 * Note: it looks like widely used APIs (Xerces, Saxon, ..)
				 * provide the textual qname even when
				 * http://xml.org/sax/features/namespace-prefixes is set to
				 * false http://
				 * sourceforge.net/projects/exificient/forums/forum
				 * /856596/topic/5839494
				 */
				List<NamespaceDeclaration> eePrefixes = null;
				if (namespaces) {
					eePrefixes = decoder.getDeclaredPrefixDeclarations();
				}
				String eeQNameAsString = decoder.getElementQNameAsString();

				QNameContext eeQName = decoder.decodeEndElement();
				// start sax end element
				contentHandler.endElement(eeQName.getNamespaceUri(),
						eeQName.getLocalName(), eeQNameAsString);

				// endPrefixMapping
				if (namespaces && eePrefixes != null) {
					for (int i = 0; i < eePrefixes.size(); i++) {
						NamespaceDeclaration ns = eePrefixes.get(i);
						contentHandler.endPrefixMapping(ns.prefix);
					}
				}
				break;
			/* CHARACTERS */
			case CHARACTERS:
			case CHARACTERS_GENERIC:
			case CHARACTERS_GENERIC_UNDECLARED:
				// handle deferred element if any first
				if(isStartElementDeferred) {
					handleDeferredStartElement(deferredStartElement, attributes);
					isStartElementDeferred = false;
				}

				Value val = decoder.decodeCharacters();
				char[] chars;

				switch (val.getValueType()) {
				case BOOLEAN:
				case STRING:
					chars = val.getCharacters();
					contentHandler.characters(chars, 0, chars.length);
					break;
				case LIST:
					ListValue lv = (ListValue) val;
					Value[] values = lv.toValues();

					if (values.length > 0) {
						ValueType vt = values[0].getValueType();
						int len;

						for (int i = 0; i < values.length; i++) {
							Value val2 = values[i];
							switch (vt) {
							case BOOLEAN:
							case STRING:
								chars = val2.getCharacters();
								contentHandler.characters(chars, 0,
										chars.length);
								contentHandler
										.characters(
												Constants.XSD_LIST_DELIM_CHAR_ARRAY,
												0,
												Constants.XSD_LIST_DELIM_CHAR_ARRAY.length);
								break;
							default:
								int offset = 0;
								len = val2.getCharactersLength();

								if (cbuffer.length < (offset + len + 1)) {
									contentHandler.characters(cbuffer, 0,
											offset);
									offset = 0;
								}
								val2.getCharacters(cbuffer, offset);
								offset += len;
								cbuffer[offset++] = ' ';
								// pending chars
								contentHandler.characters(cbuffer, 0, offset);
								break;
							}

						}
					}
					break;
				default:
					int slen = val.getCharactersLength();
					ensureBufferCapacity(slen);

					// fills char array with value
					val.getCharacters(cbuffer, 0);
					contentHandler.characters(cbuffer, 0, slen);
					break;
				}
				break;
			/* MISC */
			case DOC_TYPE:
				// handle deferred element if any first
				if(isStartElementDeferred) {
					handleDeferredStartElement(deferredStartElement, attributes);
					isStartElementDeferred = false;
				}

				handleDocType(decoder.decodeDocType());
				break;
			case ENTITY_REFERENCE:
				// handle deferred element if any first
				if(isStartElementDeferred) {
					handleDeferredStartElement(deferredStartElement, attributes);
					isStartElementDeferred = false;
				}

				handleEntityReference(decoder.decodeEntityReference());
				break;
			case COMMENT:
				// handle deferred element if any first
				if(isStartElementDeferred) {
					handleDeferredStartElement(deferredStartElement, attributes);
					isStartElementDeferred = false;
				}

				handleComment(decoder.decodeComment());
				break;
			case PROCESSING_INSTRUCTION:
				// handle deferred element if any first
				if(isStartElementDeferred) {
					handleDeferredStartElement(deferredStartElement, attributes);
					isStartElementDeferred = false;
				}

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

	/*
	 * SAX Content Handler
	 */
	protected void handleDeferredStartElement(QNameContext deferredStartElement, final AttributesImpl attributes) throws SAXException,
			IOException, EXIException {

		if (namespaces) {
			List<NamespaceDeclaration> prefixes = decoder.getDeclaredPrefixDeclarations();
			if (prefixes != null) {
				for (int i = 0; i < prefixes.size(); i++) {
					NamespaceDeclaration ns = prefixes.get(i);
					contentHandler.startPrefixMapping(ns.prefix, ns.namespaceURI);
				}
			}
		}

		/*
		 * the qualified name is required when the namespace-prefixes
		 * property is true, and is optional when the namespace-prefixes
		 * property is false (the default).
		 */
		// String seQNameAsString = Constants.EMPTY_STRING;
		// if (namespacePrefixes) {
		// seQNameAsString = decoder.getElementQNameAsString();
		// }
		/*
		 * Note: it looks like widely used APIs (Xerces, Saxon, ..) provide
		 * the textual qname even when
		 * http://xml.org/sax/features/namespace-prefixes is set to false
		 * http://
		 * sourceforge.net/projects/exificient/forums/forum/856596/topic
		 * /5839494
		 */
		String seQNameAsString = decoder.getElementQNameAsString();

		// start so far deferred start element
		contentHandler.startElement(deferredStartElement.getNamespaceUri(),
				deferredStartElement.getLocalName(), seQNameAsString,
				attributes);

		// clear AT information
		attributes.clear();
	}

	private final void ensureBufferCapacity(int reqSize) {
		if (reqSize > cbuffer.length) {
			int newSize = cbuffer.length;

			do {
				newSize = newSize << 2;
			} while (newSize < reqSize);

			cbuffer = new char[newSize];
		}
	}

	protected void handleAttribute(final QNameContext atQName, final AttributesImpl attributes) throws SAXException,
			IOException, EXIException {
		Value val = decoder.getAttributeValue();
		String sVal;

		// System.out.println("> AT: " + atQName + ": " + val);

		switch (val.getValueType()) {
		case BOOLEAN:
		case STRING:
			sVal = val.toString();
			break;
		case LIST:
			ListValue lv = (ListValue) val;

			if (lv.getNumberOfValues() > 0) {
				if (sbHelper == null) {
					sbHelper = new StringBuilder();
				} else {
					sbHelper.setLength(0);
				}

				Value[] values = lv.toValues();
				ValueType vt = values[0].getValueType();
				for (int i = 0; i < values.length; i++) {
					Value val2 = values[i];
					switch (vt) {
					case BOOLEAN:
					case STRING:
						sbHelper.append(val2.getCharacters());
						sbHelper.append(' ');
						break;
					default:
						int slen = val2.getCharactersLength();
						ensureBufferCapacity(slen);
						val2.getCharacters(cbuffer, 0);
						sbHelper.append(cbuffer, 0, slen);
						sbHelper.append(' ');
						break;
					}
				}
				sVal = sbHelper.toString();
			} else {
				sVal = Constants.EMPTY_STRING;
			}
			break;
		default:
			int slen = val.getCharactersLength();
			ensureBufferCapacity(slen);
			sVal = val.toString(cbuffer, 0);
			break;
		}

		// // empty string if no qualified name is necessary
		// String atQNameAsString = Constants.EMPTY_STRING;
		// if (namespacePrefixes) {
		// atQNameAsString = decoder.getAttributeQNameAsString();
		// }
		/*
		 * Note: it looks like widely used APIs (Xerces, Saxon, ..) provide the
		 * textual qname even when
		 * http://xml.org/sax/features/namespace-prefixes is set to false
		 * http://
		 * sourceforge.net/projects/exificient/forums/forum/856596/topic/5839494
		 */
		String atQNameAsString = decoder.getAttributeQNameAsString();

		attributes.addAttribute(atQName.getNamespaceUri(),
				atQName.getLocalName(), atQNameAsString, ATTRIBUTE_TYPE, sVal);
	}

	protected void handleDocType(DocType docType) throws SAXException,
			IOException {
		if (lexicalHandler != null) {

			String name = new String(docType.name);
			String publicId = docType.publicID.length == 0 ? null : new String(
					docType.publicID);
			String systemId = docType.systemID.length == 0 ? null : new String(
					docType.systemID);

			// force XML declaration output first
			contentHandler.characters(new char[0], 0, 0);

			lexicalHandler.startDTD(name, publicId, systemId);

			if (docType.text.length > 0) {

				// create & parse DTD text and register decl-handler
				// TODO find a better way to handle DTD
				DeclHandler dh = null;
				if (declHandler != null) {
					dh = this.declHandler;
				} else if (lexicalHandler instanceof DeclHandler) {
					// JAXP ?
					dh = (DeclHandler) lexicalHandler;
				}

				if (dh != null) {
					DocTypeTextLexicalHandler dttlh = new DocTypeTextLexicalHandler(
							dh);
					dttlh.parse(docType.text);
				}
			}

			lexicalHandler.endDTD();
		}
	}

	protected void handleEntityReference(char[] erName) throws SAXException {

		String entityReferenceName = new String(erName);
		contentHandler.skippedEntity(entityReferenceName);
		// contentHandler.characters(amp, 0, amp.length);
		// contentHandler.characters(erName, 0, erName.length);
		// contentHandler.characters(semicolon, 0, semicolon.length);

		// // JAXP ?
		// char[] entity = ("&" + entityReferenceName + ";").toCharArray();
		// contentHandler.processingInstruction(Result.PI_DISABLE_OUTPUT_ESCAPING,
		// "");
		// contentHandler.characters(entity, 0, entity.length);
		// contentHandler.processingInstruction(Result.PI_ENABLE_OUTPUT_ESCAPING,
		// "");
	}

	protected void handleComment(char[] comment) throws SAXException {
		if (lexicalHandler != null) {
			// char[] comment = decoder.getComment();
			lexicalHandler.comment(comment, 0, comment.length);
		}
	}

	static class DocTypeTextLexicalHandler {

		// e.g. (a) Internal DTD
		// <!DOCTYPE foo [
		// <!ELEMENT foo (#PCDATA)>
		// ]>

		// e.g., (b) External DTD
		// <!DOCTYPE root_element SYSTEM "DTD_location">
		// <!DOCTYPE root_element PUBLIC "DTD_name" "DTD_location">

		// Combining
		// <!DOCTYPE document SYSTEM "subjects.dtd" [
		// <!ATTLIST assessment assessment_type (exam | assignment | prac)>
		// <!ELEMENT results (#PCDATA)>
		// ]>

		// DeclHandler dh;
		XMLReader xmlReader;

		public DocTypeTextLexicalHandler(DeclHandler dh) throws SAXException {
			// this.dh = dh;

			xmlReader = XMLReaderFactory.createXMLReader();

			// xmlReader.setProperty(
			// "http://xml.org/sax/properties/lexical-handler",
			// lh);

			// DTD
			xmlReader.setFeature(
					"http://xml.org/sax/features/resolve-dtd-uris", false);
			// *skip* resolving entities like DTDs
			xmlReader.setEntityResolver(new NoEntityResolver());

			xmlReader.setProperty(
					"http://xml.org/sax/properties/declaration-handler", dh);
		}

		public void parse(char[] docTypeText) throws IOException, SAXException {
			StringBuilder dt = new StringBuilder();

			dt.append("<!DOCTYPE foo_name [ ");
			dt.append(docTypeText);
			dt.append("]>");
			dt.append("<foo />");

			Reader r = new StringReader(dt.toString());
			xmlReader.parse(new InputSource(r));
		}

		// /*
		// * DeclHandler
		// */
		//
		// public void attributeDecl(String eName, String aName, String type,
		// String mode, String value) throws SAXException {
		// dh.attributeDecl(eName, aName, type, mode, value);
		// }
		//
		// public void elementDecl(String name, String model) throws
		// SAXException {
		// dh.elementDecl(name, model);
		// }
		//
		// public void externalEntityDecl(String name, String publicId,
		// String systemId) throws SAXException {
		// dh.externalEntityDecl(name, publicId, systemId);
		// }
		//
		// public void internalEntityDecl(String name, String value)
		// throws SAXException {
		// dh.internalEntityDecl(name, value);
		// }
	}
}
