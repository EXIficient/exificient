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

package com.siemens.ct.exi.api.stream;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

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
 * De-Serializes EXI to StAX
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.7
 */

public class StAXStreamReader implements XMLStreamReader {

	protected EXIFactory noOptionsFactory;
	protected EXIStreamDecoder exiStream;
	protected EXIBodyDecoder decoder;

	// protected XMLStreamReader xmlStream;

	protected boolean exiBodyOnly = false;

	protected QName element;
	protected List<AttributeContainer> attributes;
	protected Value characters;
	protected DocType docType;
	protected char[] entityReference;
	protected char[] comment;
	protected ProcessingInstruction processingInstruction;
	protected boolean namespacePrefixes = false;

	/* current event */
	protected EventType eventType; 

	/* pre-read event, e.g., for attribute count */
	protected EventType preReadEventType; 
	
	class AttributeContainer {
		final QName qname;
		final Value value;
		final String prefix;
		public AttributeContainer(QName qname, Value value, String prefix) {
			this.qname = qname;
			this.value = value;
			this.prefix = prefix;
		}
	}
	
	public StAXStreamReader(EXIFactory noOptionsFactory, InputStream is)
			throws EXIException, IOException, XMLStreamException {
		this.noOptionsFactory = noOptionsFactory;
		this.exiStream = new EXIStreamDecoder();
		this.attributes = new ArrayList<AttributeContainer>();
		parseHeader(is);
	}

	protected void initForEachRun() {
		eventType = null;
		preReadEventType = null;
		attributes.clear();

		if (noOptionsFactory.getFidelityOptions().isFidelityEnabled(
				FidelityOptions.FEATURE_PREFIX)) {
			namespacePrefixes = true;
		}
	}

	// public void setXMLStreamReader(XMLStreamReader xmlStream) {
	// this.xmlStream = xmlStream;
	// }

	protected void parseHeader(InputStream is) throws EXIException,
			IOException, XMLStreamException {
		assert (is != null);
		assert (exiStream != null);

		// if (xmlStream == null) {
		// throw new EXIException("No XMLStreamReader set!");
		// }

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
		// ready to process EXI events
		this.next(); // prepare START_DOCUMENT
	}

	public int getEventType() {
		return getEventType(this.eventType);
	}
	
	protected static int getEventType(EventType eventType) {
		assert (eventType != null);
		switch (eventType) {
		case START_DOCUMENT:
			return XMLStreamConstants.START_DOCUMENT;
		case ATTRIBUTE_XSI_TYPE:
		case ATTRIBUTE_XSI_NIL:
		case ATTRIBUTE:
		case ATTRIBUTE_NS:
		case ATTRIBUTE_GENERIC:
		case ATTRIBUTE_INVALID_VALUE:
		case ATTRIBUTE_ANY_INVALID_VALUE:
		case ATTRIBUTE_GENERIC_UNDECLARED:
			return XMLStreamConstants.ATTRIBUTE;
		case START_ELEMENT:
		case START_ELEMENT_NS:
		case START_ELEMENT_GENERIC:
		case START_ELEMENT_GENERIC_UNDECLARED:
			return XMLStreamConstants.START_ELEMENT;
		case END_ELEMENT:
		case END_ELEMENT_UNDECLARED:
			return XMLStreamConstants.END_ELEMENT;
		case CHARACTERS:
		case CHARACTERS_GENERIC:
		case CHARACTERS_GENERIC_UNDECLARED:
			return XMLStreamConstants.CHARACTERS;
		case END_DOCUMENT:
			return XMLStreamConstants.END_DOCUMENT;
		case DOC_TYPE:
			return XMLStreamConstants.DTD;
		case NAMESPACE_DECLARATION:
			return XMLStreamConstants.NAMESPACE;
		case SELF_CONTAINED:
			// TODO SC
			throw new RuntimeException(
					"SC Element not yet supported in StAX API");
		case ENTITY_REFERENCE:
			return XMLStreamConstants.ENTITY_REFERENCE;
		case COMMENT:
			return XMLStreamConstants.COMMENT;
		case PROCESSING_INSTRUCTION:
			return XMLStreamConstants.PROCESSING_INSTRUCTION;
		default:
			throw new RuntimeException("Unexpected EXI Event '" + eventType
					+ "' ");
		}
	}
	
	public int next() throws XMLStreamException {
		try {
			int ev;
			if (this.preReadEventType == null) {
				this.eventType = decodeEvent(decoder.next());
			} else {
				this.eventType = preReadEventType;
				preReadEventType = null;
				decodeEvent(eventType);
			}
			// handle associated attributes for start-elements
			ev = getEventType();
			if (ev == XMLStreamConstants.START_ELEMENT) {
				handleAttributes();
			}
			return ev;
		} catch (Exception e) {
			throw new XMLStreamException(e);
		}
	}
	
	
	// without further attribute handling
	protected EventType decodeEvent(EventType nextEventType) throws EXIException, IOException {

		switch (nextEventType) {
		/* DOCUMENT */
		case START_DOCUMENT:
			decoder.decodeStartDocument();
			break;
		case END_DOCUMENT:
			decoder.decodeEndDocument();
			break;
		/* ATTRIBUTES */
		case ATTRIBUTE:
			attributes.add(new AttributeContainer(decoder.decodeAttribute(), decoder.getAttributeValue(), decoder.getAttributePrefix()));
			break;
		case ATTRIBUTE_NS:
			attributes.add(new AttributeContainer(decoder.decodeAttributeNS(), decoder.getAttributeValue(), decoder.getAttributePrefix()));
			break;
		case ATTRIBUTE_XSI_NIL:
			attributes.add(new AttributeContainer(decoder.decodeAttributeXsiNil(), decoder.getAttributeValue(), decoder.getAttributePrefix()));
			break;
		case ATTRIBUTE_XSI_TYPE:
			attributes.add(new AttributeContainer(decoder.decodeAttributeXsiType(), decoder.getAttributeValue(), decoder.getAttributePrefix()));
			break;
		case ATTRIBUTE_INVALID_VALUE:
			attributes.add(new AttributeContainer(decoder.decodeAttributeInvalidValue(), decoder.getAttributeValue(), decoder.getAttributePrefix()));
			break;
		case ATTRIBUTE_ANY_INVALID_VALUE:
			attributes.add(new AttributeContainer(decoder.decodeAttributeAnyInvalidValue(), decoder.getAttributeValue(), decoder.getAttributePrefix()));
			break;
		case ATTRIBUTE_GENERIC:
			attributes.add(new AttributeContainer(decoder.decodeAttributeGeneric(), decoder.getAttributeValue(), decoder.getAttributePrefix()));
			break;
		case ATTRIBUTE_GENERIC_UNDECLARED:
			attributes.add(new AttributeContainer(decoder.decodeAttributeGenericUndeclared(), decoder.getAttributeValue(), decoder.getAttributePrefix()));
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
			element = decoder.decodeStartElement();
			break;
		case START_ELEMENT_NS:
			element = decoder.decodeStartElementNS();
			break;
		case START_ELEMENT_GENERIC:
			element = decoder.decodeStartElementGeneric();
			break;
		case START_ELEMENT_GENERIC_UNDECLARED:
			element = decoder.decodeStartElementGenericUndeclared();
			break;
		/* END ELEMENT */
		case END_ELEMENT:
			List<NamespaceDeclaration> eePrefixes = decoder
					.getDeclaredPrefixDeclarations();
			if (namespacePrefixes) {
				// eeQNameAsString = decoder.getElementQNameAsString();
			}
			element = decoder.decodeEndElement();
			break;
		case END_ELEMENT_UNDECLARED:
			eePrefixes = decoder.getDeclaredPrefixDeclarations();
			if (namespacePrefixes) {
				// eeQNameAsString = decoder.getElementQNameAsString();
			}
			element = decoder.decodeEndElementUndeclared();
			break;
		/* CHARACTERS */
		case CHARACTERS:
			characters = decoder.decodeCharacters();
			break;
		case CHARACTERS_GENERIC:
			characters = decoder.decodeCharactersGeneric();
			break;
		case CHARACTERS_GENERIC_UNDECLARED:
			characters = decoder.decodeCharactersGenericUndeclared();
			break;
		/* MISC */
		case DOC_TYPE:
			docType = decoder.decodeDocType();
			break;
		case ENTITY_REFERENCE:
			entityReference = decoder.decodeEntityReference();
			break;
		case COMMENT:
			comment = decoder.decodeComment();
			break;
		case PROCESSING_INSTRUCTION:
			processingInstruction = decoder.decodeProcessingInstruction();
			break;
		default:
			throw new RuntimeException("Unexpected EXI Event '" + eventType
					+ "' ");
		}
		
		return nextEventType;
	}


	public void close() throws XMLStreamException {
		// TODO Auto-generated method stub

	}

	protected void handleAttributes() throws EXIException, IOException {
		assert(getEventType() == XMLStreamConstants.START_ELEMENT);
		attributes.clear();
		int ev;
		EventType et;
		do {
			et = decoder.next();
			if ( ( ev =getEventType(et)) == XMLStreamConstants.ATTRIBUTE) {
				decodeEvent(et);
			}
		} while(ev == XMLStreamConstants.ATTRIBUTE);
		
		this.preReadEventType = et;
	}
	
	public int getAttributeCount() {
		return this.attributes.size();
	}

	public String getAttributeLocalName(int index) {
		return attributes.get(index).qname.getLocalPart();
	}

	public QName getAttributeName(int index) {
		return attributes.get(index).qname;
	}

	public String getAttributeNamespace(int index) {
		return attributes.get(index).qname.getNamespaceURI();
	}

	public String getAttributePrefix(int index) {
		return attributes.get(index).prefix;
	}

	public String getAttributeType(int index) {
		// Returns the XML type of the attribute at the provided index 
		return null;
	}

	public String getAttributeValue(int index) {
		return attributes.get(index).value.toString();
	}

	public String getAttributeValue(String arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getCharacterEncodingScheme() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getElementText() throws XMLStreamException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getEncoding() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getLocalName() {
		// Returns the (local) name of the current event.
		return element.getLocalPart();
	}

	public Location getLocation() {
		// TODO Auto-generated method stub
		return EmptyLocation.getInstance();
	}

	public QName getName() {
		// Returns a QName for the current START_ELEMENT or END_ELEMENT event
		return element;
	}

	public NamespaceContext getNamespaceContext() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getNamespaceCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getNamespacePrefix(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getNamespaceURI() {
		// If the current event is a START_ELEMENT or END_ELEMENT this method
		// returns the URI of the prefix or the default namespace.
		return element.getNamespaceURI();
	}

	public String getNamespaceURI(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getNamespaceURI(String arg0) {
		// If the current event is a START_ELEMENT or END_ELEMENT this method
		// returns the URI of the prefix or the default namespace.
		// TODO Auto-generated method stub
		return null;
	}

	public String getPIData() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getPITarget() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getPrefix() {
		// Returns the prefix of the current event or null if the event does not
		// have a prefix
		if (getEventType() == XMLStreamConstants.START_ELEMENT
				|| getEventType() == XMLStreamConstants.END_ELEMENT) {
			return decoder.getElementPrefix();
		} else {
			assert (getEventType() == XMLStreamConstants.ATTRIBUTE);
			return decoder.getAttributePrefix();
		}
	}

	public Object getProperty(String arg0) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getText() {
		// Returns the current value of the parse event as a string, this
		// returns the string value of a CHARACTERS event, returns the value of
		// a COMMENT, the replacement value for an ENTITY_REFERENCE, the string
		// value of a CDATA section, the string value for a SPACE event, or the
		// String value of the internal subset of the DTD.
		switch (getEventType()) {
		case XMLStreamConstants.CHARACTERS:
		case XMLStreamConstants.SPACE:
			return characters.toString();
		case XMLStreamConstants.COMMENT:
			return new String(comment);
		case XMLStreamConstants.ENTITY_REFERENCE:
			return new String(this.entityReference);
		case XMLStreamConstants.DTD:
			return new String("TODO DOCTYPE");
		default:
			throw new RuntimeException("Unexpected event, id=" + getEventType());
		}
	}

	public char[] getTextCharacters() {
		// Returns an array which contains the characters from this event.
		switch (getEventType()) {
		case XMLStreamConstants.CHARACTERS:
		case XMLStreamConstants.SPACE:
			return characters.toString().toCharArray();
		case XMLStreamConstants.COMMENT:
			return comment;
		case XMLStreamConstants.ENTITY_REFERENCE:
			return this.entityReference;
		case XMLStreamConstants.DTD:
			// TODO DOCTYPE
			return null;
		default:
			throw new RuntimeException("Unexpected event, id=" + getEventType());
		}
	}

	public int getTextCharacters(int sourceStart, char[] target,
			int targetStart, int length) throws XMLStreamException {
		// Gets the the text associated with a CHARACTERS, SPACE or CDATA event.

		// TODO What does this really mean?
		return -1;

		// switch(getEventType()) {
		// case XMLStreamConstants.CHARACTERS:
		// case XMLStreamConstants.SPACE:
		// return characters.toString().toCharArray();
		// case XMLStreamConstants.DTD:
		// // TODO DOCTYPE
		// return -1;
		// default:
		// throw new RuntimeException("Unexpected event, id=" + getEventType());
		// }
	}

	public int getTextLength() {
		// Returns the length of the sequence of characters for this Text event
		// within the text character array.
		switch (getEventType()) {
		case XMLStreamConstants.CHARACTERS:
		case XMLStreamConstants.SPACE:
			return characters.getCharactersLength();
		case XMLStreamConstants.COMMENT:
			return comment.length;
		case XMLStreamConstants.ENTITY_REFERENCE:
			return this.entityReference.length;
		case XMLStreamConstants.DTD:
			// TODO DOCTYPE
			return -1;
		default:
			throw new RuntimeException("Unexpected event, id=" + getEventType());
		}
	}

	public int getTextStart() {
		// Returns the offset into the text character array where the first
		// character (of this text event) is stored
		return 0;
	}

	public String getVersion() {
		// Get the xml version declared on the xml declaration Returns null if
		// none was declared
		return null;
	}

	public boolean hasName() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean hasNext() throws XMLStreamException {
		// TODO Auto-generated method stub
		return (this.eventType != EventType.END_DOCUMENT);
	}

	public boolean hasText() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isAttributeSpecified(int arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isCharacters() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isEndElement() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isStandalone() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isStartElement() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isWhiteSpace() {
		// TODO Auto-generated method stub
		return false;
	}

	public int nextTag() throws XMLStreamException {
		// TODO Auto-generated method stub
		return 0;
	}

	public void require(int arg0, String arg1, String arg2)
			throws XMLStreamException {
		// TODO Auto-generated method stub

	}

	public boolean standaloneSet() {
		// TODO Auto-generated method stub
		return false;
	}

	
	
}
