/*
 * Copyright (C) 2007-2012 Siemens AG
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
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.XMLConstants;
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

import org.xml.sax.SAXException;

import com.siemens.ct.exi.Constants;
import com.siemens.ct.exi.EXIBodyEncoder;
import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.EXIStreamEncoder;
import com.siemens.ct.exi.FidelityOptions;
import com.siemens.ct.exi.attributes.AttributeFactory;
import com.siemens.ct.exi.attributes.AttributeList;
import com.siemens.ct.exi.core.container.NamespaceDeclaration;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.util.SimpleDocTypeParser;
import com.siemens.ct.exi.values.StringValue;

/**
 * Serializes StAX to EXI
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9.2-SNAPSHOT
 */

public class StAXEncoder implements XMLStreamWriter {

	protected EXIBodyEncoder encoder;
	protected EXIStreamEncoder exiStream;

	protected StringBuilder sbChars;

	protected SimpleDocTypeParser dtdParser;

	// preserve options
	protected final boolean preserveDTD;
	protected final boolean preserveComment;
	protected final boolean preservePI;

	// AT or NS Events pending
	protected boolean pendingATs;

	// attributes
	protected AttributeList exiAttributes;

	// namespaces
	protected EncoderNamespaceContext nsContext;

	public StAXEncoder(EXIFactory factory) throws EXIException {
		// initialize char buffer
		sbChars = new StringBuilder();
		// attribute list & NS
		AttributeFactory attFactory = AttributeFactory.newInstance();
		exiAttributes = attFactory.createAttributeListInstance(factory);
		nsContext = new EncoderNamespaceContext();
		// exi stream
		exiStream = new EXIStreamEncoder(factory);
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
		pendingATs = false;
		sbChars.setLength(0);
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
				System.out.println("StAX Event '" + event + "' not supported!");
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
					nsPfx = nsPfx == null ? XMLConstants.DEFAULT_NS_PREFIX
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
				this.writeCharacters(xmlStream.getTextCharacters(),
						xmlStream.getTextStart(), xmlStream.getTextLength());
				break;
			case XMLStreamConstants.DTD:
				// TODO DTD
				break;
			case XMLStreamConstants.ENTITY_REFERENCE:
				// TODO ER
				break;
			default:
				System.out.println("Event '" + event + "' not supported!");
			}
		}

		// this.flush();
	}

	protected void appendChars(String text) {
		sbChars.append(text);
	}

	protected void appendChars(char[] text, int start, int len) {
		sbChars.append(text, start, len);
	}

	protected void checkPendingEvents() throws EXIException, IOException {
		// AT & NS first
		if (pendingATs) {

			// encode NS decls and attributes
			encoder.encodeAttributeList(exiAttributes);
			exiAttributes.clear();

			pendingATs = false;
		}

		// CH
		if (sbChars.length() > 0) {
			// encoder.encodeCharacters(sbChars.toString());
			encoder.encodeCharacters(new StringValue(sbChars.toString()));
			sbChars.setLength(0);
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
			throw new XMLStreamException(e);
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
			this.checkPendingEvents();
			// CDATA
			this.writeCharacters(data);
		} catch (Exception e) {
			throw new XMLStreamException(e);
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
		this.appendChars(text);
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
		this.appendChars(text, start, len);
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
				this.checkPendingEvents();
				// TODO improve EXI API
				char[] chars = data.toCharArray();
				encoder.encodeComment(chars, 0, chars.length);
			} catch (Exception e) {
				throw new XMLStreamException(e);
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
				this.checkPendingEvents();
				SimpleDocTypeParser dtdParser = getDtdParser();
				dtdParser.parse(dtd);

				encoder.encodeDocType(dtdParser.name, dtdParser.publicID,
						dtdParser.systemID, dtdParser.text);
			} catch (Exception e) {
				throw new XMLStreamException(e);
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
			checkPendingEvents();
			encoder.encodeEndDocument();
			encoder.flush();
		} catch (Exception e) {
			throw new XMLStreamException(e);
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
			this.checkPendingEvents();
			encoder.encodeEndElement();
		} catch (Exception e) {
			throw new XMLStreamException(e);
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
				this.checkPendingEvents();
				encoder.encodeEntityReference(name);
			} catch (Exception e) {
				throw new XMLStreamException(e);
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
			throw new XMLStreamException(e);
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
				this.checkPendingEvents();
				encoder.encodeProcessingInstruction(target, data);
			} catch (Exception e) {
				throw new XMLStreamException(e);
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
			throw new XMLStreamException(e);
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
			checkPendingEvents();
			encoder.encodeStartElement(namespaceURI, localName, prefix);
			pendingATs = true;
		} catch (Exception e) {
			throw new XMLStreamException(e);
		}
	}

	public void close() throws XMLStreamException {
		this.close();
	}

	public void flush() throws XMLStreamException {
		try {
			encoder.flush();
		} catch (IOException e) {
			throw new XMLStreamException(e);
		}
	}

	public NamespaceContext getNamespaceContext() {
//		if (this.nsContext == null) {
//			this.nsContext = DefaultNamespaceContext.getInstance();
//		}
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
		this.setPrefix(XMLConstants.DEFAULT_NS_PREFIX, uri);
	}

	public void setNamespaceContext(NamespaceContext context)
			throws XMLStreamException {
		// TODO how to properly allow that
		throw new IllegalArgumentException("NamespaceContext cannot be replaced");
//		this.nsContext = context;
	}

	public void setPrefix(String prefix, String uri) throws XMLStreamException {
		this.writeNamespace(prefix, uri);
	}

	public void writeAttribute(String localName, String value)
			throws XMLStreamException {
		this.writeAttribute(XMLConstants.NULL_NS_URI, localName, value);
	}

	public void writeAttribute(String namespaceURI, String localName,
			String value) throws XMLStreamException {
		this.writeAttribute(getNamespaceContext().getPrefix(namespaceURI), namespaceURI,
				localName, value);
	}

	public void writeDefaultNamespace(String namespaceURI)
			throws XMLStreamException {
		this.setPrefix(XMLConstants.DEFAULT_NS_PREFIX, namespaceURI);
	}

	public void writeEmptyElement(String localName) throws XMLStreamException {
		this.writeEmptyElement(XMLConstants.NULL_NS_URI, localName);
	}

	public void writeEmptyElement(String namespaceURI, String localName)
			throws XMLStreamException {
		this.writeEmptyElement(XMLConstants.DEFAULT_NS_PREFIX, localName,
				namespaceURI);
	}

	public void writeEmptyElement(String prefix, String localName,
			String namespaceURI) throws XMLStreamException {
		this.writeStartElement(prefix, localName, namespaceURI);
		this.writeEndElement();
	}

	public void writeStartDocument(String version) throws XMLStreamException {
		this.writeStartDocument();
	}

	public void writeStartDocument(String encoding, String version)
			throws XMLStreamException {
		this.writeStartDocument();
	}

	public void writeStartElement(String localName) throws XMLStreamException {
		this.writeStartElement(XMLConstants.NULL_NS_URI, localName);
	}

	public void writeStartElement(String namespaceURI, String localName)
			throws XMLStreamException {
		this.writeStartElement(getNamespaceContext().getPrefix(namespaceURI), localName,
				namespaceURI);
	}
	
	static class EncoderNamespaceContext implements NamespaceContext {

		List<List<NamespaceDeclaration>> nsContexts;
		
		public EncoderNamespaceContext() {
			nsContexts = new ArrayList<List<NamespaceDeclaration>>();
		}
		
		public void reset(){
			nsContexts.clear();
			pushContext(); // root context for default and xml
			bindPrefix(XMLConstants.XML_NS_URI, XMLConstants.DEFAULT_NS_PREFIX);
			bindPrefix(XMLConstants.XML_NS_URI, XMLConstants.XML_NS_PREFIX);
		}
		
		public void pushContext() {
			nsContexts.add(null);
		}
		
		public void bindPrefix(String namespaceURI, String prefix) {
			final int level = nsContexts.size()-1;
			List<NamespaceDeclaration> l = nsContexts.get(level);
			if(l == null) {
				l = new ArrayList<NamespaceDeclaration>();
				nsContexts.set(level, l);
			}
			
			l.add(new NamespaceDeclaration(namespaceURI, prefix));
			
		}
		
		// return previously bound prefixes
		public List<NamespaceDeclaration> popContext() {
			assert(nsContexts.size() > 1); // outer root always there
			return nsContexts.remove(nsContexts.size()-1);
		}
		
		public String getNamespaceURI(String prefix) {
			// from bottom to top
			int level = nsContexts.size() - 1;
			while(level >= 0) {
				List<NamespaceDeclaration> l = nsContexts.get(level);
				if(l != null) {
					for(NamespaceDeclaration nsDecl : l) {
						if(nsDecl.prefix.equals(prefix)) {
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
			while(level >= 0) {
				List<NamespaceDeclaration> l = nsContexts.get(level);
				if(l != null) {
					for(NamespaceDeclaration nsDecl : l) {
						if(nsDecl.namespaceURI.equals(namespaceURI)) {
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
			while(level >= 0) {
				List<NamespaceDeclaration> l = nsContexts.get(level);
				if(l != null) {
					for(NamespaceDeclaration nsDecl : l) {
						if(nsDecl.namespaceURI.equals(namespaceURI)) {
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
