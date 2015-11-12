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

package com.siemens.ct.exi.api.stream;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
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
import com.siemens.ct.exi.context.QNameContext;
import com.siemens.ct.exi.core.container.DocType;
import com.siemens.ct.exi.core.container.NamespaceDeclaration;
import com.siemens.ct.exi.core.container.ProcessingInstruction;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.grammars.event.EventType;
import com.siemens.ct.exi.values.Value;

/**
 * De-Serializes EXI to StAX
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9.5
 */

public class StAXDecoder implements XMLStreamReader
//XMLEventReader
{

	protected EXIFactory noOptionsFactory;
	protected EXIStreamDecoder exiStream;
	protected EXIBodyDecoder decoder;

	// protected XMLStreamReader xmlStream;

	protected boolean exiBodyOnly = false;

	protected QNameContext element;
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

	/* namespace context */
	protected EXINamespaceContext nsContext;
	
	static class AttributeContainer {
		final QNameContext qname;
		final Value value;
		final String prefix;

		public AttributeContainer(QNameContext qname, Value value, String prefix) {
			this.qname = qname;
			this.value = value;
			this.prefix = prefix;
		}
	}

	public StAXDecoder(EXIFactory noOptionsFactory) throws EXIException {
		this.noOptionsFactory = noOptionsFactory;
		this.exiStream = new EXIStreamDecoder(noOptionsFactory);
		this.attributes = new ArrayList<AttributeContainer>();
		this.nsContext = new EXINamespaceContext();
		
	}
	
	public void setInputStream(InputStream is) throws EXIException, IOException, XMLStreamException {
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

	protected void parseHeader(InputStream is) throws EXIException,
			IOException, XMLStreamException {
		assert (is != null);
		assert (exiStream != null);

		// if (xmlStream == null) {
		// throw new EXIException("No XMLStreamReader set!");
		// }

		if (exiBodyOnly) {
			// no EXI header
			decoder = exiStream.getBodyOnlyDecoder(is);
		} else {
			// read header (default)
			decoder = exiStream.decodeHeader(is);
		}

		// init
		initForEachRun();
		// ready to process EXI events
		eventType = decoder.next();
		assert (eventType == EventType.START_DOCUMENT);
		decoder.decodeStartDocument();
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
			return -1;
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
	
	String endElementPrefix;

	// without further attribute handling
	protected EventType decodeEvent(EventType nextEventType)
			throws EXIException, IOException {

		endElementPrefix = null;
		
		switch (nextEventType) {
		/* DOCUMENT */
		case START_DOCUMENT:
			decoder.decodeStartDocument();
			break;
		case END_DOCUMENT:
			decoder.decodeEndDocument();
			break;
		/* ATTRIBUTES */
		case ATTRIBUTE_XSI_NIL:
			attributes.add(new AttributeContainer(decoder
					.decodeAttributeXsiNil(), decoder.getAttributeValue(),
					decoder.getAttributePrefix()));
			break;
		case ATTRIBUTE_XSI_TYPE:
			attributes.add(new AttributeContainer(decoder
					.decodeAttributeXsiType(), decoder.getAttributeValue(),
					decoder.getAttributePrefix()));
			break;
		case ATTRIBUTE:
		case ATTRIBUTE_NS:
		case ATTRIBUTE_GENERIC:
		case ATTRIBUTE_GENERIC_UNDECLARED:
		case ATTRIBUTE_INVALID_VALUE:
		case ATTRIBUTE_ANY_INVALID_VALUE:
			attributes.add(new AttributeContainer(decoder.decodeAttribute(),
					decoder.getAttributeValue(), decoder.getAttributePrefix()));
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
			element = decoder.decodeStartElement();
			break;
		/* END ELEMENT */
		case END_ELEMENT:
		case END_ELEMENT_UNDECLARED:
//			@SuppressWarnings("unused")
//			List<NamespaceDeclaration> eePrefixes = decoder.getDeclaredPrefixDeclarations();
//			if (namespacePrefixes) {
//				// eeQNameAsString = decoder.getElementQNameAsString();
//			}
			endElementPrefix = decoder.getElementPrefix();
			element = decoder.decodeEndElement();
			break;
		/* CHARACTERS */
		case CHARACTERS:
		case CHARACTERS_GENERIC:
		case CHARACTERS_GENERIC_UNDECLARED:
			characters = decoder.decodeCharacters();
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
	}

	protected void handleAttributes() throws EXIException, IOException {
		assert (getEventType() == XMLStreamConstants.START_ELEMENT);
		attributes.clear();
		int ev;
		EventType et;
		do {
			et = decoder.next();
			ev = getEventType(et);
			if (et == EventType.SELF_CONTAINED || ev == XMLStreamConstants.ATTRIBUTE
					|| ev == XMLStreamConstants.NAMESPACE) {
				decodeEvent(et);
			}	
		} while (et == EventType.SELF_CONTAINED || ev == XMLStreamConstants.ATTRIBUTE
				|| ev == XMLStreamConstants.NAMESPACE);

		this.preReadEventType = et;
	}

	public int getAttributeCount() {
		return this.attributes.size();
	}

	public String getAttributeLocalName(int index) {
		return attributes.get(index).qname.getLocalName();
	}

	public QName getAttributeName(int index) {
		return attributes.get(index).qname.getQName();
	}

	public String getAttributeNamespace(int index) {
		return attributes.get(index).qname.getNamespaceUri();
	}

	public String getAttributePrefix(int index) {
		return attributes.get(index).prefix;
	}

	public String getAttributeType(int index) {
		// System.err.println("getAttributeType()");
		// Returns the XML type of the attribute at the provided index
		return "CDATA";
	}

	public String getAttributeValue(int index) {
		return attributes.get(index).value.toString();
	}

	public String getAttributeValue(String namespaceURI, String localName) {
		// Returns the normalized attribute value of the attribute with the namespace and localName
		// If the namespaceURI is null the namespace is not checked for equality
		for(AttributeContainer ac : attributes) {
			if(ac.qname.getLocalName().equals(localName)) {
				if(namespaceURI == null) {
					return ac.value.toString();
				} else if (ac.qname.getNamespaceUri().equals(namespaceURI)) {
					return ac.value.toString();
				}
			}
		}
		return null;
	}

	public String getCharacterEncodingScheme() {
		// TODO Auto-generated method stub
		// System.err.println("getCharacterEncodingScheme()");
		return null;
	}

	
	public String getElementText() throws XMLStreamException {
		//  Reads the content of a text-only element,
		// an exception is thrown if this is not a text-only element.
		switch (getEventType()) {
		case XMLStreamConstants.CHARACTERS:
		case XMLStreamConstants.SPACE:
			return characters.toString();
		default:
			throw new RuntimeException("Unexpected event, id=" + getEventType());
		}
	}

	public String getEncoding() {
		// System.err.println("getEncoding()");
		// TODO Auto-generated method stub
		return null;
	}

	public String getLocalName() {
		// Returns the (local) name of the current event.
		return element.getLocalName();
	}

	public Location getLocation() {
		// TODO Auto-generated method stub
		return EmptyLocation.getInstance();
	}

	/*
	 * Returns a QName for the current START_ELEMENT or END_ELEMENT event 
	 * 
	 * (non-Javadoc)
	 * @see javax.xml.stream.XMLStreamReader#getName()
	 */
	public QName getName() {
		// Returns a QName for the current START_ELEMENT or END_ELEMENT event
		QName qn = new QName( element.getNamespaceUri(), element.getLocalName(), this.getPrefix());
		return qn;
	}

	/*
	 * Returns a read only namespace context for the current position.
	 * 
	 * (non-Javadoc)
	 * 
	 * @see javax.xml.stream.XMLStreamReader#getNamespaceContext()
	 */
	public NamespaceContext getNamespaceContext() {
		nsContext.setNamespaceDeclarations(decoder
				.getDeclaredPrefixDeclarations());
		return nsContext;
	}

	/*
	 * Returns the count of namespaces declared on this START_ELEMENT or
	 * END_ELEMENT, this method is only valid on a START_ELEMENT, END_ELEMENT or
	 * NAMESPACE.
	 * 
	 * (non-Javadoc)
	 * 
	 * @see javax.xml.stream.XMLStreamReader#getNamespaceCount()
	 */
	public int getNamespaceCount() {
		List<NamespaceDeclaration> nsDecls = decoder
				.getDeclaredPrefixDeclarations();
		return nsDecls == null ? 0 : nsDecls.size();
	}

	/*
	 * Returns the prefix for the namespace declared at the index.
	 * 
	 * (non-Javadoc)
	 * 
	 * @see javax.xml.stream.XMLStreamReader#getNamespacePrefix(int)
	 */
	public String getNamespacePrefix(int index) {
		return decoder.getDeclaredPrefixDeclarations().get(index).prefix;
	}

	/*
	 * Returns the uri for the namespace declared at the index.
	 * 
	 * (non-Javadoc)
	 * 
	 * @see javax.xml.stream.XMLStreamReader#getNamespaceURI(int)
	 */
	public String getNamespaceURI(int index) {
		return decoder.getDeclaredPrefixDeclarations().get(index).namespaceURI;
	}

	/*
	 * Return the uri for the given prefix.
	 * 
	 * (non-Javadoc)
	 * 
	 * @see javax.xml.stream.XMLStreamReader#getNamespaceURI(java.lang.String)
	 */
	public String getNamespaceURI(String prefix) {
		List<NamespaceDeclaration> nsDecls = decoder
				.getDeclaredPrefixDeclarations();
		for (int i = 0; i < nsDecls.size(); i++) {
			NamespaceDeclaration nsDecl = nsDecls.get(i);
			if (nsDecl.prefix.equals(prefix)) {
				return nsDecl.namespaceURI;
			}
		}
		return null;
	}

	/*
	 * If the current event is a START_ELEMENT or END_ELEMENT this method
	 * returns the URI of the prefix or the default namespace.
	 * 
	 * (non-Javadoc)
	 * 
	 * @see javax.xml.stream.XMLStreamReader#getNamespaceURI()
	 */
	public String getNamespaceURI() {
		// If the current event is a START_ELEMENT or END_ELEMENT this method
		// returns the URI of the prefix or the default namespace.
		return element.getNamespaceUri();
	}

	public String getPIData() {
		return this.processingInstruction.data;
	}

	public String getPITarget() {
		return this.processingInstruction.target;
	}

	public String getPrefix() {
		if (this.endElementPrefix != null) {
			return endElementPrefix;
		}
		
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
		// System.err.println("getProperty()");
		// // TODO Auto-generated method stub
		return null;
	}

	public String getText() {
		// System.err.println("getText()");
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
			return getDocTypeString();
		default:
			throw new RuntimeException("Unexpected event, id=" + getEventType());
		}
	}
	
	private String getDocTypeString() {
		StringBuilder sb = new StringBuilder();
		sb.append("<!DOCTYPE ");
		sb.append(docType.name);
		
		if (docType.publicID.length > 0) {
			sb.append(" PUBLIC ");
			sb.append('\"');
			sb.append(docType.publicID);
			sb.append('\"');
		}
		if (docType.systemID.length > 0) {
			if (docType.publicID.length == 0) {
				sb.append(" SYSTEM ");	
			} else {
				sb.append(' ');
			}
			sb.append('\"');
			sb.append(docType.systemID);
			sb.append('\"');
		}
		if (docType.text.length > 0) {
			sb.append(' ');
			sb.append('[');
			sb.append(docType.text);
			sb.append(']');			
		}
		sb.append('>');

		return sb.toString();
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
			 return getDocTypeString().toCharArray();
		default:
			throw new RuntimeException("Unexpected event, id=" + getEventType());
		}
	}

	public int getTextCharacters(int sourceStart, char[] target,
			int targetStart, int length) throws XMLStreamException {
		/*
		 * Gets the the text associated with a CHARACTERS, SPACE or CDATA event.
		 * Text starting a "sourceStart" is copied into "target" starting at
		 * "targetStart". Up to "length" characters are copied. The number of
		 * characters actually copied is returned. The "sourceStart" argument
		 * must be greater or equal to 0 and less than or equal to the number of
		 * characters associated with the event. Usually, one requests text
		 * starting at a "sourceStart" of 0. If the number of characters
		 * actually copied is less than the "length", then there is no more
		 * text. Otherwise, subsequent calls need to be made until all text has
		 * been retrieved. For example: int length = 1024; char[] myBuffer = new
		 * char[ length ]; for ( int sourceStart = 0 ; ; sourceStart += length )
		 * { int nCopied = stream.getTextCharacters( sourceStart, myBuffer, 0,
		 * length ); if (nCopied < length) break; } XMLStreamException may be
		 * thrown if there are any XML errors in the underlying source. The
		 * "targetStart" argument must be greater than or equal to 0 and less
		 * than the length of "target", Length must be greater than 0 and
		 * "targetStart + length" must be less than or equal to length of
		 * "target".
		 */
		// arraycopy(Object source, int sourcePosition, Object destination, int destinationPosition, int numberOfElements)
		if (this.getTextLength() > ( target.length - targetStart )) {
			throw new RuntimeException("Buffer too small!");
		}
		
		switch (getEventType()) {
		case XMLStreamConstants.CHARACTERS:
		case XMLStreamConstants.SPACE:
			this.characters.getCharacters(target, targetStart);
			
//			//System.arraycopy(this.characters, sourceStart, target, targetStart, length);
//			char[] ch = this.characters.getCharacters(target, targetStart);
//			if (ch != target) {
//				System.arraycopy(ch, sourceStart, target, targetStart, length);
//			}
			return length;
		case XMLStreamConstants.COMMENT:
			System.arraycopy(this.comment, sourceStart, target, targetStart, length);
			return length;
		case XMLStreamConstants.ENTITY_REFERENCE:
			System.arraycopy(this.entityReference, sourceStart, target, targetStart, length);
			return length;
		case XMLStreamConstants.DTD:
			char[] dt = getDocTypeString().toCharArray();
			System.arraycopy(dt, sourceStart, target, targetStart, length);
			return length;
		default:
			throw new RuntimeException("Unexpected event, id=" + getEventType());
		}
		
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
		switch (getEventType()) {
		case XMLStreamConstants.START_ELEMENT:
		case XMLStreamConstants.ATTRIBUTE:
			return true;
		default:
			return false;
		}
	}

	public boolean hasNext() throws XMLStreamException {
		return (this.eventType != EventType.END_DOCUMENT);
	}

	public boolean hasText() {
		switch (getEventType()) {
		case XMLStreamConstants.CHARACTERS:
		case XMLStreamConstants.CDATA:
		case XMLStreamConstants.COMMENT:
		case XMLStreamConstants.SPACE:
			return true;
		default:
			return false;
		}
	}

	/*
	 * Returns a boolean which indicates if this attribute was created by
	 * default
	 * 
	 * (non-Javadoc)
	 * 
	 * @see javax.xml.stream.XMLStreamReader#isAttributeSpecified(int)
	 */
	public boolean isAttributeSpecified(int arg0) {
		return false;
	}

	public boolean isCharacters() {
		return getEventType() == XMLStreamConstants.CHARACTERS;
	}

	public boolean isEndElement() {
		return  getEventType() == XMLStreamConstants.END_ELEMENT ;
	}

	/*
	 * Checks if standalone was set in the document
	 * 
	 * (non-Javadoc)
	 * 
	 * @see javax.xml.stream.XMLStreamReader#isStandalone()
	 */
	public boolean isStandalone() {
		return true;
	}

	public boolean isStartElement() {
		return getEventType() == XMLStreamConstants.START_ELEMENT;
	}

	/*
	 * Returns true if the cursor points to a character data event that consists
	 * of all whitespace
	 * 
	 * (non-Javadoc)
	 * 
	 * @see javax.xml.stream.XMLStreamReader#isWhiteSpace()
	 */
	public boolean isWhiteSpace() {
		switch (getEventType()) {
		case XMLStreamConstants.CHARACTERS:
			return this.characters.toString().trim().length() == 0;
		case XMLStreamConstants.CDATA:
			return false;
		case XMLStreamConstants.COMMENT:
			return false;
		case XMLStreamConstants.SPACE:
			return true;
		default:
			return false;
		}
	}

	/*
	 * Skips any white space (isWhiteSpace() returns true), COMMENT, or
	 * PROCESSING_INSTRUCTION, until a START_ELEMENT or END_ELEMENT is reached.
	 * 
	 * http://download.oracle.com/javase/6/docs/api/javax/xml/stream/XMLStreamReader
	 * .html#nextTag%28%29
	 * 
	 * (non-Javadoc)
	 * 
	 * @see javax.xml.stream.XMLStreamReader#nextTag()
	 */
	public int nextTag() throws XMLStreamException {
		int eventType = next();
		while ((eventType == XMLStreamConstants.CHARACTERS && isWhiteSpace()) // skip
																				// whitespace
				|| (eventType == XMLStreamConstants.CDATA && isWhiteSpace())
				// skip whitespace
				|| eventType == XMLStreamConstants.SPACE
				|| eventType == XMLStreamConstants.PROCESSING_INSTRUCTION
				|| eventType == XMLStreamConstants.COMMENT) {
			eventType = next();
		}
		if (eventType != XMLStreamConstants.START_ELEMENT
				&& eventType != XMLStreamConstants.END_ELEMENT) {
			throw new XMLStreamException("expected start or end tag",
					getLocation());
		}
		return eventType;
	}

	/*
	 * Test if the current event is of the given type and if the namespace and
	 * name match the current namespace and name of the current event. If the
	 * namespaceURI is null it is not checked for equality, if the localName is
	 * null it is not checked for equality.
	 * 
	 * (non-Javadoc)
	 * 
	 * @see javax.xml.stream.XMLStreamReader#require(int, java.lang.String,
	 * java.lang.String)
	 */
	public void require(int type, String namespaceURI, String localName)
			throws XMLStreamException {

		int eventType = getEventType();

		if (eventType == type) {
			switch (eventType) {
			case XMLStreamConstants.START_ELEMENT:
				if (namespaceURI != null) {
					if (!this.element.getNamespaceUri().equals(namespaceURI)) {
						throw new XMLStreamException();
					}
				}
				if (localName != null) {
					if (!this.element.getLocalName().equals(localName)) {
						throw new XMLStreamException();
					}
				}
				break;
			case XMLStreamConstants.ATTRIBUTE:
				// TODO which attribute?
				throw new XMLStreamException();
			}
		} else {
			throw new XMLStreamException();
		}

	}

	public boolean standaloneSet() {
		return false;
	}
	
	
	class EXINamespaceContext implements NamespaceContext {

		List<NamespaceDeclaration> _nsDecls;

		protected void setNamespaceDeclarations(List<NamespaceDeclaration> nsDecls) {
			_nsDecls = nsDecls;
		}

		public String getNamespaceURI(String prefix) {
			if (_nsDecls != null) {
				for (int i = 0; i < _nsDecls.size(); i++) {
					NamespaceDeclaration nsDecl = _nsDecls.get(i);
					if (nsDecl.prefix.equals(prefix)) {
						return nsDecl.namespaceURI;
					}
				}
			}

			return null;
		}

		public String getPrefix(String namespaceURI) {
			if (_nsDecls != null) {
				for (int i = 0; i < _nsDecls.size(); i++) {
					NamespaceDeclaration nsDecl = _nsDecls.get(i);
					if (nsDecl.namespaceURI.equals(namespaceURI)) {
						return nsDecl.prefix;
					}
				}
			}

			return null;
		}

		@SuppressWarnings("rawtypes")
		public Iterator getPrefixes(String namespaceURI) {
			List<String> prefixes = new ArrayList<String>();
			if (_nsDecls != null) {
				for (int i = 0; i < _nsDecls.size(); i++) {
					NamespaceDeclaration nsDecl = _nsDecls.get(i);
					if (nsDecl.namespaceURI.equals(namespaceURI)) {
						prefixes.add(nsDecl.prefix);
					}
				}
			}

			return prefixes.iterator();
		}

	}

}
