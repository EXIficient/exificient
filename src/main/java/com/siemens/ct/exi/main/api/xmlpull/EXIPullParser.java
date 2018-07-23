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

package com.siemens.ct.exi.main.api.xmlpull;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Richard.Kuntschke@siemens.com
 * 

 */

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.siemens.ct.exi.core.EXIBodyDecoder;
import com.siemens.ct.exi.core.EXIFactory;
import com.siemens.ct.exi.core.EXIStreamDecoder;
import com.siemens.ct.exi.core.container.DocType;
import com.siemens.ct.exi.core.container.NamespaceDeclaration;
import com.siemens.ct.exi.core.container.ProcessingInstruction;
import com.siemens.ct.exi.core.context.QNameContext;
import com.siemens.ct.exi.core.exceptions.EXIException;
import com.siemens.ct.exi.core.grammars.event.EventType;
import com.siemens.ct.exi.core.values.Value;

public class EXIPullParser implements XmlPullParser {

	final protected EXIFactory factory;
	final protected EXIStreamDecoder exiStream;

	protected EXIBodyDecoder decoder;

	/* current event */
	protected EventType eventType;

	/* pre-read event, e.g., for attribute count */
	protected EventType preReadEventType;

	protected QNameContext element;
	protected List<AttributeContainer> attributes;
	protected Value characters;
	protected DocType docType;
	protected char[] entityReference;
	protected char[] comment;
	protected ProcessingInstruction processingInstruction;

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

	public EXIPullParser(EXIFactory factory) throws EXIException {
		this.factory = factory;

		exiStream = factory.createEXIStreamDecoder();
		this.attributes = new ArrayList<AttributeContainer>();
		// this.nsContext = new EXINamespaceContext();
	}

	public void setFeature(String name, boolean state)
			throws XmlPullParserException {
		// TODO check if any feature could be of interest
		throw new XmlPullParserException(
				"EXI does not support setting feature " + name + " to " + state);
	}

	public boolean getFeature(String name) {
		return false; // unknown
	}

	public void setProperty(String name, Object value)
			throws XmlPullParserException {
		// TODO check if any property could be of interest
		throw new XmlPullParserException(
				"EXI does not support setting property " + name + " to "
						+ value);
	}

	public Object getProperty(String name) {
		return null; // unknown
	}

	public void setInput(Reader in) throws XmlPullParserException {
		throw new XmlPullParserException(
				"EXI requires byte-based stream. Consider using InputStream.");
	}

	public void setInput(InputStream inputStream, String inputEncoding)
			throws XmlPullParserException {
		try {
			parseHeader(inputStream);
		} catch (EXIException e) {
			throw new XmlPullParserException("[EXI] " + e.getMessage());
		} catch (IOException e) {
			throw new XmlPullParserException("[EXI] " + e.getMessage());
		}
	}

	protected void parseHeader(InputStream is) throws EXIException, IOException {
		assert (is != null);
		assert (exiStream != null);

		// read header
		decoder = exiStream.decodeHeader(is);

		// init
		initForEachRun();

		// ready to process EXI events
		eventType = decoder.next();
		assert (eventType == EventType.START_DOCUMENT);
		decoder.decodeStartDocument();
	}

	protected void initForEachRun() {
		eventType = null;

		preReadEventType = null;
		attributes.clear();
	}

	public String getInputEncoding() {
		return null; // unknown
	}

	public void defineEntityReplacementText(String entityName,
			String replacementText) throws XmlPullParserException {
	}

	public int getNamespaceCount(int depth) throws XmlPullParserException {
		// TODO take into account depth
		List<NamespaceDeclaration> ns = decoder.getDeclaredPrefixDeclarations();
		if (ns == null) {
			return 0;
		} else {
			return ns.size();
		}
	}

	public String getNamespacePrefix(int pos) throws XmlPullParserException {
		List<NamespaceDeclaration> ns = decoder.getDeclaredPrefixDeclarations();
		if (ns == null || pos >= ns.size()) {
			return null;
		} else {
			return ns.get(pos).prefix;
		}
	}

	public String getNamespaceUri(int pos) throws XmlPullParserException {
		List<NamespaceDeclaration> ns = decoder.getDeclaredPrefixDeclarations();
		if (ns == null || pos >= ns.size()) {
			return null;
		} else {
			return ns.get(pos).namespaceURI;
		}
	}

	public String getNamespace(String prefix) {
		if (prefix == null) {
			return null;
		}

		List<NamespaceDeclaration> ns = decoder.getDeclaredPrefixDeclarations();
		if (ns != null) {
			for (int i = ns.size() - 1; i >= 0; i--) {
				if (prefix.equals(ns.get(i).prefix)) {
					return ns.get(i).namespaceURI;
				}
			}
		}

		return null;
	}

	public int getDepth() {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getPositionDescription() {
		return null; // not supported
	}

	public int getLineNumber() {
		return 0; // not supported
	}

	public int getColumnNumber() {
		return 0; // not supported
	}

	public boolean isWhitespace() throws XmlPullParserException {
		switch (getEventType()) {
		case XmlPullParser.TEXT:
			return this.characters.toString().trim().length() == 0;
		case XmlPullParser.CDSECT:
			return false;
		case XmlPullParser.COMMENT:
			return false;
		default:
			return false;
		}
	}

	public String getText() {
		try {
			switch (getEventType()) {
			case XmlPullParser.TEXT:
				return characters.toString();
			case XmlPullParser.COMMENT:
				return new String(comment);
			case XmlPullParser.ENTITY_REF:
				return new String(this.entityReference);
			case XmlPullParser.DOCDECL:
				return getDocTypeString();
			default:
				throw new RuntimeException("Unexpected event, id="
						+ getEventType());
			}
		} catch (XmlPullParserException e) {
			throw new RuntimeException("Unexpected text, error="
					+ e.getMessage());
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

	public char[] getTextCharacters(int[] holderForStartAndLength) {
		char[] ch = getText().toCharArray();
		holderForStartAndLength[0] = 0;
		holderForStartAndLength[1] = ch.length;
		return ch;
	}

	public String getNamespace() {
		int et = getEventType(this.eventType);
		if (et == XmlPullParser.START_TAG || et == XmlPullParser.END_TAG) {
			return this.element.getNamespaceUri();
		} else {
			return null;
		}
	}

	public String getName() {
		int et = getEventType(this.eventType);
		if (et == XmlPullParser.START_TAG || et == XmlPullParser.END_TAG) {
			return this.element.getLocalName();
		} else if (et == XmlPullParser.ENTITY_REF) {
			return new String(this.entityReference);
		} else {
			return null;
		}
	}

	public String getPrefix() {
		int et = getEventType(this.eventType);
		if (et == XmlPullParser.START_TAG || et == XmlPullParser.END_TAG) {
			if (this.endElementPrefix != null) {
				return endElementPrefix;
			}

			// Returns the prefix of the current event or null if the event does
			// not
			// have a prefix
			return decoder.getElementPrefix();
		} else {
			return null;
		}
	}

	public boolean isEmptyElementTag() throws XmlPullParserException {
		return false; // by default never degenerated
	}

	public int getAttributeCount() {
		return attributes.size();
	}

	public String getAttributeNamespace(int index) {
		if (index >= 0 && index < attributes.size()) {
			return attributes.get(index).qname.getNamespaceUri();
		} else {
			return null;
		}
	}

	public String getAttributeName(int index) {
		if (index >= 0 && index < attributes.size()) {
			return attributes.get(index).qname.getLocalName();
		} else {
			return null;
		}
	}

	public String getAttributePrefix(int index) {
		if (index >= 0 && index < attributes.size()) {
			return attributes.get(index).prefix;
		} else {
			return null;
		}
	}

	public String getAttributeType(int index) {
		return "CDATA";
	}

	public boolean isAttributeDefault(int index) {
		return false;
	}

	public String getAttributeValue(int index) {
		if (index >= 0 && index < attributes.size()) {
			return attributes.get(index).value.toString();
		} else {
			return null;
		}
	}

	public String getAttributeValue(String namespace, String name) {
		if (name == null) {
			return null;
		}
		if (namespace == null) {
			namespace = "";
		}

		for (int i = 0; i < attributes.size(); i++) {
			if (attributes.get(i).qname.getNamespaceUri().equals(namespace)
					&& attributes.get(i).qname.getLocalName().equals(name)) {
				return attributes.get(i).value.toString();
			}
		}

		return null;
	}

	public int getEventType() throws XmlPullParserException {
		return getEventType(this.eventType);
	}

	protected static int getEventType(EventType eventType) {
		assert (eventType != null);
		switch (eventType) {
		case START_DOCUMENT:
			return XmlPullParser.START_DOCUMENT;
		case ATTRIBUTE_XSI_TYPE:
		case ATTRIBUTE_XSI_NIL:
		case ATTRIBUTE:
		case ATTRIBUTE_NS:
		case ATTRIBUTE_GENERIC:
		case ATTRIBUTE_INVALID_VALUE:
		case ATTRIBUTE_ANY_INVALID_VALUE:
		case ATTRIBUTE_GENERIC_UNDECLARED:
			return -2;
		case START_ELEMENT:
		case START_ELEMENT_NS:
		case START_ELEMENT_GENERIC:
		case START_ELEMENT_GENERIC_UNDECLARED:
			return XmlPullParser.START_TAG;
		case END_ELEMENT:
		case END_ELEMENT_UNDECLARED:
			return XmlPullParser.END_TAG;
		case CHARACTERS:
		case CHARACTERS_GENERIC:
		case CHARACTERS_GENERIC_UNDECLARED:
			return XmlPullParser.TEXT;
		case END_DOCUMENT:
			return XmlPullParser.END_DOCUMENT;
		case DOC_TYPE:
			return XmlPullParser.DOCDECL;
		case NAMESPACE_DECLARATION:
			return -3;
		case SELF_CONTAINED:
			// TODO SC
			return -1;
		case ENTITY_REFERENCE:
			return XmlPullParser.ENTITY_REF;
		case COMMENT:
			return XmlPullParser.COMMENT;
		case PROCESSING_INSTRUCTION:
			return XmlPullParser.PROCESSING_INSTRUCTION;
		default:
			throw new RuntimeException("Unexpected EXI Event '" + eventType
					+ "' ");
		}
	}

	public int next() throws XmlPullParserException, IOException {
		return this.nextToken();
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
			// @SuppressWarnings("unused")
			// List<NamespaceDeclaration> eePrefixes =
			// decoder.getDeclaredPrefixDeclarations();
			// if (namespacePrefixes) {
			// // eeQNameAsString = decoder.getElementQNameAsString();
			// }
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

	protected void handleAttributes() throws EXIException, IOException,
			XmlPullParserException {
		assert (getEventType() == XmlPullParser.START_TAG);
		attributes.clear();
		EventType et;
		do {
			et = decoder.next();
			if (isAttributeEvent(et)) {
				this.decodeEvent(et);
			}
		} while (isAttributeEvent(et));

		this.preReadEventType = et;
	}

	private boolean isAttributeEvent(EventType et) {
		boolean isAttributeEvent = false;
		switch (et) {
		case ATTRIBUTE:
		case ATTRIBUTE_NS:
		case ATTRIBUTE_GENERIC:
		case ATTRIBUTE_GENERIC_UNDECLARED:
		case ATTRIBUTE_INVALID_VALUE:
		case ATTRIBUTE_ANY_INVALID_VALUE:
			isAttributeEvent = true;
			break;
		/* NAMESPACE DECLARATION */
		case NAMESPACE_DECLARATION:
			isAttributeEvent = true;
			break;
		case SELF_CONTAINED:
			isAttributeEvent = true;
			break;
		default:
		}

		return isAttributeEvent;
	}

	public int nextToken() throws XmlPullParserException, IOException {
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
			if (ev == XmlPullParser.START_TAG) {
				handleAttributes();
			}

			return ev;
		} catch (Exception e) {
			throw new IOException(e);
		}
	}

	public void require(int type, String namespace, String name)
			throws XmlPullParserException, IOException {
	}

	public String nextText() throws XmlPullParserException, IOException {
		if (getEventType() != START_TAG) {
			throw new XmlPullParserException(
					"parser must be on START_TAG to read next text", this, null);
		}
		int eventType = next();
		if (eventType == TEXT) {
			String result = getText();
			eventType = next();
			if (eventType != END_TAG) {
				throw new XmlPullParserException(
						"event TEXT it must be immediately followed by END_TAG",
						this, null);
			}
			return result;
		} else if (eventType == END_TAG) {
			return "";
		} else {
			throw new XmlPullParserException(
					"parser must be on START_TAG or TEXT to read text", this,
					null);
		}
	}

	public int nextTag() throws XmlPullParserException, IOException {
		int eventType = next();
		if (eventType == TEXT && isWhitespace()) { // skip whitespace
			eventType = next();
		}
		if (eventType != START_TAG && eventType != END_TAG) {
			throw new XmlPullParserException("expected start or end tag", this,
					null);
		}
		return eventType;
	}

}
