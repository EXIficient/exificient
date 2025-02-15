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

package com.siemens.ct.exi.main.api.stream;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.Comment;
import javax.xml.stream.events.DTD;
import javax.xml.stream.events.EntityReference;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.ProcessingInstruction;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.siemens.ct.exi.core.Constants;
import com.siemens.ct.exi.core.EXIBodyEncoder;
import com.siemens.ct.exi.core.EXIFactory;
import com.siemens.ct.exi.core.EXIStreamEncoder;
import com.siemens.ct.exi.core.FidelityOptions;
import com.siemens.ct.exi.core.attributes.AttributeFactory;
import com.siemens.ct.exi.core.attributes.AttributeList;
import com.siemens.ct.exi.core.container.NamespaceDeclaration;
import com.siemens.ct.exi.core.exceptions.EXIException;
import com.siemens.ct.exi.core.values.StringValue;
import com.siemens.ct.exi.main.util.SimpleDocTypeParser;

/**
 * Serializes StAX to EXI
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Richard.Kuntschke@siemens.com
 * 
 */

public class StAXEncoder implements XMLStreamWriter {

	/** The logger used in this class. */
	private static final Logger LOGGER = LoggerFactory.getLogger(StAXEncoder.class);

	protected EXIBodyEncoder encoder;
	protected EXIStreamEncoder exiStream;

	protected SimpleDocTypeParser dtdParser;

	// preserve options
	protected final boolean preserveDTD;
	protected final boolean preserveComment;
	protected final boolean preservePI;

	// indicate whether an "empty" element was started
	protected boolean emptyElement;

	// AT or NS Events pending
	protected boolean pendingATs;

	// attributes
	protected AttributeList exiAttributes;

	// namespaces
	protected EncoderNamespaceContext nsContext;

	public StAXEncoder(EXIFactory factory) throws EXIException {
		// attribute list & NS
		AttributeFactory attFactory = AttributeFactory.newInstance();
		exiAttributes = attFactory.createAttributeListInstance(factory);
		nsContext = new EncoderNamespaceContext();
		// exi stream
		exiStream = factory.createEXIStreamEncoder();
		// preserve options
		FidelityOptions fo = factory.getFidelityOptions();
		preserveDTD = fo.isFidelityEnabled(FidelityOptions.FEATURE_DTD);
		preserveComment = fo.isFidelityEnabled(FidelityOptions.FEATURE_COMMENT);
		preservePI = fo.isFidelityEnabled(FidelityOptions.FEATURE_PI);
	}

	public void setOutputStream(OutputStream os) throws EXIException,
			IOException {
		// write header & get body encoder
		this.encoder = exiStream.encodeHeader(os);
	}

	protected void init() {
		emptyElement = false;
		pendingATs = false;
		exiAttributes.clear();
		nsContext.reset();
	}

	protected SimpleDocTypeParser getDtdParser() throws SAXException {
		if (dtdParser == null) {
			dtdParser = new SimpleDocTypeParser();
		}
		return dtdParser;
	}

	public void encode(XMLEventReader xmlEvent) throws XMLStreamException,
			EXIException, IOException {

		while (xmlEvent.hasNext()) {
			XMLEvent event = xmlEvent.nextEvent();
			switch (event.getEventType()) {
			case XMLStreamConstants.START_DOCUMENT:
				writeStartDocument();
				break;
			case XMLStreamConstants.END_DOCUMENT:
				writeEndDocument();
				break;
			case XMLStreamConstants.START_ELEMENT:
				StartElement se = event.asStartElement();
				QName qn = se.getName();
				String pfx = qn.getPrefix();
				writeStartElement(pfx, qn.getLocalPart(), qn.getNamespaceURI());

				// parse NS declarations
				@SuppressWarnings("unchecked")
				Iterator<Namespace> namespaces = se.getNamespaces();
				while (namespaces.hasNext()) {
					Namespace ns = namespaces.next();
					this.writeNamespace(ns.getPrefix(), ns.getNamespaceURI());
				}
				// parse attributes
				@SuppressWarnings("unchecked")
				Iterator<Attribute> attributes = se.getAttributes();
				while (attributes.hasNext()) {
					Attribute at = attributes.next();
					QName qnAt = at.getName();
					this.writeAttribute(qnAt.getPrefix(),
							qnAt.getNamespaceURI(), qnAt.getLocalPart(),
							at.getValue());
				}
				break;
			case XMLStreamConstants.END_ELEMENT:
				writeEndElement();
				break;
			case XMLStreamConstants.NAMESPACE:
				Namespace ns = (Namespace) event;
				writeNamespace(ns.getPrefix(), ns.getNamespaceURI());
				break;
			case XMLStreamConstants.CHARACTERS:
				Characters chars = event.asCharacters();
				this.writeCharacters(chars.getData());
				break;
			case XMLStreamConstants.SPACE:
				break;
			case XMLStreamConstants.ATTRIBUTE:
				break;
			case XMLStreamConstants.PROCESSING_INSTRUCTION:
				ProcessingInstruction pi = (ProcessingInstruction) event;
				this.writeProcessingInstruction(pi.getTarget(), pi.getData());
				break;
			case XMLStreamConstants.COMMENT:
				Comment cm = (Comment) event;
				this.writeComment(cm.getText());
				break;
			case XMLStreamConstants.DTD:
				DTD dtd = (DTD) event;
				this.writeDTD(dtd.getDocumentTypeDeclaration());
				break;
			case XMLStreamConstants.ENTITY_DECLARATION:
				break;
			case XMLStreamConstants.ENTITY_REFERENCE:
				EntityReference er = (EntityReference) event;
				this.writeEntityRef(er.getName());
				break;
			default:
				LOGGER.warn("StAX Event '{}' not supported!", event);
			}
		}

		// this.flush();
	}

	public void encode(XMLStreamReader xmlStream) throws XMLStreamException,
			EXIException, IOException {

		// StartDocument should be initial state
		assert (xmlStream.getEventType() == XMLStreamConstants.START_DOCUMENT);

		writeStartDocument();

		while (xmlStream.hasNext()) {
			int event = xmlStream.next();
			switch (event) {
			case XMLStreamConstants.START_DOCUMENT:
				// should have happened beforehand
				throw new EXIException("Unexpected START_DOCUMENT event");
			case XMLStreamConstants.END_DOCUMENT:
				this.writeEndDocument();
				break;
			case XMLStreamConstants.START_ELEMENT:
				QName qn = xmlStream.getName();
				String pfx = qn.getPrefix();
				writeStartElement(pfx, qn.getLocalPart(), qn.getNamespaceURI());
				// parse NS declarations
				int nsCnt = xmlStream.getNamespaceCount();
				for (int i = 0; i < nsCnt; i++) {
					String nsPfx = xmlStream.getNamespacePrefix(i);
					nsPfx = nsPfx == null ? Constants.XML_DEFAULT_NS_PREFIX
							: nsPfx;
					String nsUri = xmlStream.getNamespaceURI(i);
					this.writeNamespace(nsPfx, nsUri);
				}
				// parse attributes
				int atCnt = xmlStream.getAttributeCount();
				for (int i = 0; i < atCnt; i++) {
					QName atQname = xmlStream.getAttributeName(i);
					this.writeAttribute(atQname.getPrefix(),
							atQname.getNamespaceURI(), atQname.getLocalPart(),
							xmlStream.getAttributeValue(i));
				}

				break;
			case XMLStreamConstants.END_ELEMENT:
				writeEndElement();
				break;
			case XMLStreamConstants.NAMESPACE:
				break;
			case XMLStreamConstants.CHARACTERS:
				this.writeCharacters(xmlStream.getTextCharacters(),
						xmlStream.getTextStart(), xmlStream.getTextLength());
				break;
			case XMLStreamConstants.SPACE:
				// @SuppressWarnings("unused")
				String ignorableSpace = xmlStream.getText();
				writeCharacters(ignorableSpace);
				break;
			case XMLStreamConstants.ATTRIBUTE:
				// @SuppressWarnings("unused")
				// int attsX = xmlStream.getAttributeCount();
				break;
			case XMLStreamConstants.PROCESSING_INSTRUCTION:
				this.writeProcessingInstruction(xmlStream.getPITarget(),
						xmlStream.getPIData());
				break;
			case XMLStreamConstants.COMMENT:
				this.writeComment(xmlStream.getText());
				break;
			case XMLStreamConstants.DTD:
				// TODO DTD
				break;
			case XMLStreamConstants.ENTITY_REFERENCE:
				// TODO ER
				break;
			default:
				LOGGER.warn("Event '{}' not supported!", event);
			}
		}

		// this.flush();
	}

	protected void checkPendingATEvents() throws EXIException, IOException {
		// NS first & ATs
		if (pendingATs) {
			// encode NS decls and attributes
			encoder.encodeAttributeList(exiAttributes);
			exiAttributes.clear();
			pendingATs = false;

			if (emptyElement) {
				encoder.encodeEndElement();
				emptyElement = false;
			}
		}
	}

	/*
	 * Writes an attribute to the output stream
	 * 
	 * (non-Javadoc)
	 * 
	 * @see javax.xml.stream.XMLStreamWriter#writeAttribute(java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String)
	 */
	public void writeAttribute(String prefix, String namespaceURI,
			String localName, String value) throws XMLStreamException {
		try {
			this.exiAttributes.addAttribute(namespaceURI, localName, prefix,
					value);
		} catch (Exception e) {
			throw new XMLStreamException(e.getLocalizedMessage(), e);
		}
	}

	/*
	 * Writes a CData section
	 * 
	 * (non-Javadoc)
	 * 
	 * @see javax.xml.stream.XMLStreamWriter#writeCData(java.lang.String)
	 */
	public void writeCData(String data) throws XMLStreamException {
		try {
			this.checkPendingATEvents();
			// CDATA
			this.writeCharacters(data);
		} catch (Exception e) {
			throw new XMLStreamException(e.getLocalizedMessage(), e);
		}
	}

	/*
	 * Write text to the output
	 * 
	 * (non-Javadoc)
	 * 
	 * @see javax.xml.stream.XMLStreamWriter#writeCharacters(java.lang.String)
	 */
	public void writeCharacters(String text) throws XMLStreamException {
		try {
			this.checkPendingATEvents();
			encoder.encodeCharacters(new StringValue(text));
		} catch (Exception e) {
			throw new XMLStreamException(e.getLocalizedMessage(), e);
		}
	}

	/*
	 * Write text to the output
	 * 
	 * (non-Javadoc)
	 * 
	 * @see javax.xml.stream.XMLStreamWriter#writeCharacters(char[], int, int)
	 */
	public void writeCharacters(char[] text, int start, int len)
			throws XMLStreamException {
		this.writeCharacters(new String(text, start, len));
	}

	/*
	 * Writes an xml comment with the data enclosed
	 * 
	 * (non-Javadoc)
	 * 
	 * @see javax.xml.stream.XMLStreamWriter#writeComment(java.lang.String)
	 */
	public void writeComment(String data) throws XMLStreamException {
		if (preserveComment) {
			try {
				this.checkPendingATEvents();
				// TODO improve EXI API
				char[] chars = data.toCharArray();
				encoder.encodeComment(chars, 0, chars.length);
			} catch (Exception e) {
				throw new XMLStreamException(e.getLocalizedMessage(), e);
			}
		}
	}

	/*
	 * Write a DTD section.
	 * 
	 * (non-Javadoc)
	 * 
	 * @see javax.xml.stream.XMLStreamWriter#writeDTD(java.lang.String)
	 */
	public void writeDTD(String dtd) throws XMLStreamException {
		if (preserveDTD) {
			try {
				this.checkPendingATEvents();
				SimpleDocTypeParser dtdParser = getDtdParser();
				dtdParser.parse(dtd);

				encoder.encodeDocType(dtdParser.name, dtdParser.publicID,
						dtdParser.systemID, dtdParser.text);
			} catch (Exception e) {
				throw new XMLStreamException(e.getLocalizedMessage(), e);
			}
		}
	}

	/*
	 * Closes any start tags and writes corresponding end tags.
	 * 
	 * (non-Javadoc)
	 * 
	 * @see javax.xml.stream.XMLStreamWriter#writeEndDocument()
	 */
	public void writeEndDocument() throws XMLStreamException {
		try {
			checkPendingATEvents();
			encoder.encodeEndDocument();
			encoder.flush();
		} catch (Exception e) {
			throw new XMLStreamException(e.getLocalizedMessage(), e);
		}
	}

	/*
	 * Writes an end tag to the output relying on the internal state of the
	 * writer to determine the prefix and local name of the event.
	 * 
	 * (non-Javadoc)
	 * 
	 * @see javax.xml.stream.XMLStreamWriter#writeEndElement()
	 */
	public void writeEndElement() throws XMLStreamException {
		try {
			this.checkPendingATEvents();
			encoder.encodeEndElement();
		} catch (Exception e) {
			throw new XMLStreamException(e.getLocalizedMessage(), e);
		}
	}

	/*
	 * Writes an entity reference
	 * 
	 * (non-Javadoc)
	 * 
	 * @see javax.xml.stream.XMLStreamWriter#writeEntityRef(java.lang.String)
	 */
	public void writeEntityRef(String name) throws XMLStreamException {
		if (preserveDTD) {
			try {
				this.checkPendingATEvents();
				encoder.encodeEntityReference(name);
			} catch (Exception e) {
				throw new XMLStreamException(e.getLocalizedMessage(), e);
			}
		}
	}

	/*
	 * Writes a namespace to the output stream If the prefix argument to this
	 * method is the empty string, "xmlns", or null this method will delegate to
	 * writeDefaultNamespace
	 * 
	 * (non-Javadoc)
	 * 
	 * @see javax.xml.stream.XMLStreamWriter#writeNamespace(java.lang.String,
	 * java.lang.String)
	 */
	public void writeNamespace(String prefix, String namespaceURI)
			throws XMLStreamException {
		try {
			this.exiAttributes.addNamespaceDeclaration(namespaceURI, prefix);
			// encoder.encodeNamespaceDeclaration(namespaceURI, prefix);
		} catch (Exception e) {
			throw new XMLStreamException(e.getLocalizedMessage(), e);
		}
	}

	/*
	 * Writes a processing instruction
	 * 
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.xml.stream.XMLStreamWriter#writeProcessingInstruction(java.lang
	 * .String)
	 */
	public void writeProcessingInstruction(String target)
			throws XMLStreamException {
		this.writeProcessingInstruction(target, Constants.EMPTY_STRING);
	}

	/*
	 * Writes a processing instruction
	 * 
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.xml.stream.XMLStreamWriter#writeProcessingInstruction(java.lang
	 * .String, java.lang.String)
	 */
	public void writeProcessingInstruction(String target, String data)
			throws XMLStreamException {
		if (preservePI) {
			try {
				this.checkPendingATEvents();
				encoder.encodeProcessingInstruction(target, data);
			} catch (Exception e) {
				throw new XMLStreamException(e.getLocalizedMessage(), e);
			}
		}
	}

	/*
	 * Write the XML Declaration.
	 * 
	 * (non-Javadoc)
	 * 
	 * @see javax.xml.stream.XMLStreamWriter#writeStartDocument()
	 */
	public void writeStartDocument() throws XMLStreamException {
		this.init();
		try {
			encoder.encodeStartDocument();
		} catch (Exception e) {
			throw new XMLStreamException(e.getLocalizedMessage(), e);
		}
	}

	/*
	 * Writes a start tag to the output.
	 * 
	 * (non-Javadoc)
	 * 
	 * @see javax.xml.stream.XMLStreamWriter#writeStartElement(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	public void writeStartElement(String prefix, String localName,
			String namespaceURI) throws XMLStreamException {
		try {
			assert (namespaceURI != null);
			assert (localName != null);
			// System.out.println("> SE " + localName);
			checkPendingATEvents();
			encoder.encodeStartElement(namespaceURI, localName, prefix);
			pendingATs = true;
		} catch (Exception e) {
			throw new XMLStreamException(e.getLocalizedMessage(), e);
		}
	}

	public void close() throws XMLStreamException {
		this.flush();
	}

	public void flush() throws XMLStreamException {
		// try {
		// Note: encodeDocument flushes already
		// encoder.flush();
		// } catch (IOException e) {
		// throw new XMLStreamException(e.getLocalizedMessage(), e);
		// }
	}

	public NamespaceContext getNamespaceContext() {
		// if (this.nsContext == null) {
		// this.nsContext = DefaultNamespaceContext.getInstance();
		// }
		return this.nsContext;
	}

	public String getPrefix(String uri) throws XMLStreamException {
		return getNamespaceContext().getPrefix(uri);
	}

	public Object getProperty(String name) throws IllegalArgumentException {
		// TODO
		return null;
	}

	public void setDefaultNamespace(String uri) throws XMLStreamException {
		this.setPrefix(Constants.XML_DEFAULT_NS_PREFIX, uri);
	}

	public void setNamespaceContext(NamespaceContext context)
			throws XMLStreamException {
		// TODO how to properly allow that
		throw new IllegalArgumentException(
				"NamespaceContext cannot be replaced");
		// this.nsContext = context;
	}

	public void setPrefix(String prefix, String uri) throws XMLStreamException {
		this.writeNamespace(prefix, uri);
	}

	public void writeAttribute(String localName, String value)
			throws XMLStreamException {
		this.writeAttribute(Constants.XML_NULL_NS_URI, localName, value);
	}

	public void writeAttribute(String namespaceURI, String localName,
			String value) throws XMLStreamException {
		this.writeAttribute(getNamespaceContext().getPrefix(namespaceURI),
				namespaceURI, localName, value);
	}

	public void writeDefaultNamespace(String namespaceURI)
			throws XMLStreamException {
		this.setPrefix(Constants.XML_DEFAULT_NS_PREFIX, namespaceURI);
	}

	public void writeEmptyElement(String localName) throws XMLStreamException {
		this.writeEmptyElement(Constants.XML_NULL_NS_URI, localName);
	}

	public void writeEmptyElement(String namespaceURI, String localName)
			throws XMLStreamException {
		this.writeEmptyElement(Constants.XML_DEFAULT_NS_PREFIX, localName,
				namespaceURI);
	}

	public void writeEmptyElement(String prefix, String localName,
			String namespaceURI) throws XMLStreamException {
		this.writeStartElement(prefix, localName, namespaceURI);
		// Note: we cannot close the element immediately since attributes might follow
		// this.writeEndElement();
		emptyElement = true;
	}

	public void writeStartDocument(String version) throws XMLStreamException {
		this.writeStartDocument();
	}

	public void writeStartDocument(String encoding, String version)
			throws XMLStreamException {
		this.writeStartDocument();
	}

	public void writeStartElement(String localName) throws XMLStreamException {
		this.writeStartElement(Constants.XML_NULL_NS_URI, localName);
	}

	public void writeStartElement(String namespaceURI, String localName)
			throws XMLStreamException {
		this.writeStartElement(getNamespaceContext().getPrefix(namespaceURI),
				localName, namespaceURI);
	}

	static class EncoderNamespaceContext implements NamespaceContext {

		List<List<NamespaceDeclaration>> nsContexts;

		public EncoderNamespaceContext() {
			nsContexts = new ArrayList<List<NamespaceDeclaration>>();
		}

		public void reset() {
			nsContexts.clear();
			pushContext(); // root context for default and xml
			bindPrefix(Constants.XML_NS_URI, Constants.XML_DEFAULT_NS_PREFIX);
			bindPrefix(Constants.XML_NS_URI, Constants.XML_NS_PREFIX);
		}

		public void pushContext() {
			nsContexts.add(null);
		}

		public void bindPrefix(String namespaceURI, String prefix) {
			final int level = nsContexts.size() - 1;
			List<NamespaceDeclaration> l = nsContexts.get(level);
			if (l == null) {
				l = new ArrayList<NamespaceDeclaration>();
				nsContexts.set(level, l);
			}

			l.add(new NamespaceDeclaration(namespaceURI, prefix));

		}

		// return previously bound prefixes
		public List<NamespaceDeclaration> popContext() {
			assert (nsContexts.size() > 1); // outer root always there
			return nsContexts.remove(nsContexts.size() - 1);
		}

		public String getNamespaceURI(String prefix) {
			// from bottom to top
			int level = nsContexts.size() - 1;
			while (level >= 0) {
				List<NamespaceDeclaration> l = nsContexts.get(level);
				if (l != null) {
					for (NamespaceDeclaration nsDecl : l) {
						if (nsDecl.prefix.equals(prefix)) {
							return nsDecl.namespaceURI;
						}
					}
				}
				level--;
			}

			return null;
		}

		public String getPrefix(String namespaceURI) {
			// from bottom to top
			int level = nsContexts.size() - 1;
			while (level >= 0) {
				List<NamespaceDeclaration> l = nsContexts.get(level);
				if (l != null) {
					for (NamespaceDeclaration nsDecl : l) {
						if (nsDecl.namespaceURI.equals(namespaceURI)) {
							return nsDecl.prefix;
						}
					}
				}
				level--;
			}

			return null;
		}

		@SuppressWarnings("rawtypes")
		public Iterator getPrefixes(String namespaceURI) {
			List<String> prefixes = new ArrayList<String>();

			int level = nsContexts.size() - 1;
			while (level >= 0) {
				List<NamespaceDeclaration> l = nsContexts.get(level);
				if (l != null) {
					for (NamespaceDeclaration nsDecl : l) {
						if (nsDecl.namespaceURI.equals(namespaceURI)) {
							prefixes.add(nsDecl.prefix);
						}
					}
				}
				level--;
			}

			return prefixes.iterator();
		}

	}

}
