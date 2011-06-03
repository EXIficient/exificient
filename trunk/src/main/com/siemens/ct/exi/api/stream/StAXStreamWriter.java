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
import java.io.OutputStream;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import com.siemens.ct.exi.Constants;
import com.siemens.ct.exi.EXIBodyEncoder;
import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.EXIStreamEncoder;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.values.StringValue;

/**
 * Serializes StAX to EXI
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.7
 */

public class StAXStreamWriter implements XMLStreamWriter {
	
	protected EXIBodyEncoder encoder;

	public StAXStreamWriter(EXIFactory factory, OutputStream os) throws EXIException, IOException, XMLStreamException {
		// exi stream
		EXIStreamEncoder exiStream = new EXIStreamEncoder();
		// write header & get body encoder
		this.encoder = exiStream.encodeHeader(factory, os);
	}
	
	public void encode(XMLStreamReader xmlStream) throws XMLStreamException, EXIException {
		
		// StartDocument should be initial state
		assert( xmlStream.getEventType() == XMLStreamConstants.START_DOCUMENT);
		writeStartDocument();
		
		while (xmlStream.hasNext()) { 
		    int event = xmlStream.next();
		    
		    switch(event) {
		    case XMLStreamConstants.START_DOCUMENT:
		    	// should have happen beforehand
		    	throw new EXIException("Unexpected START_DOCUMENT event");
		    case XMLStreamConstants.END_DOCUMENT:
		    	writeEndDocument();
		    	break;
		    case XMLStreamConstants.START_ELEMENT:
		    	QName qn = xmlStream.getName();
		    	System.out.println("> SE " + qn);
		    	writeStartElement(qn.getNamespaceURI(), qn.getLocalPart());
		    	int atts = xmlStream.getAttributeCount();
		    	for(int i=0; i<atts; i++) {
		    		QName atQname = xmlStream.getAttributeName(i);
		    		String atVal = xmlStream.getAttributeValue(i);
		    		System.out.println("  AT " + atQname +  " = " + atVal);
		    		this.writeAttribute(atQname.getNamespaceURI(), atQname.getLocalPart(), atVal);
		    	}
		    	break;
		    case XMLStreamConstants.END_ELEMENT:
		    	writeEndElement();
		    	break;
		    case XMLStreamConstants.NAMESPACE:
		    	String prefix = null;
		    	String namespaceURI = null;
		    	writeNamespace(prefix, namespaceURI);
		    	break;
		    case XMLStreamConstants.CHARACTERS:
		    	String ch = xmlStream.getText();
		    	writeCharacters(ch);
		    	break;
		    case XMLStreamConstants.SPACE:
		    	String ignorableSpace = xmlStream.getText();
		    	break;
		    case XMLStreamConstants.ATTRIBUTE:
		    	int attsX = xmlStream.getAttributeCount();
		    	System.out.println("AT");
		    	// exiWriter.writeCharacters(ch);
		    	break;
		    default:
		    	System.out.println("Event '" + event +"' not supported!");
		    }
		}
		
		this.close();
		
	}
	
	public void close() throws XMLStreamException {
		try {
			encoder.flush();
		} catch (Exception e) {
			throw new XMLStreamException(e);
		}	
	}

	public void flush() throws XMLStreamException {
		// TODO Auto-generated method stub
		
	}

	public NamespaceContext getNamespaceContext() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getPrefix(String arg0) throws XMLStreamException {
		// TODO Auto-generated method stub
		return null;
	}

	public Object getProperty(String arg0) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		return null;
	}

	public void setDefaultNamespace(String arg0) throws XMLStreamException {
		// TODO Auto-generated method stub
		
	}

	public void setNamespaceContext(NamespaceContext arg0)
			throws XMLStreamException {
		// TODO Auto-generated method stub
		
	}

	public void setPrefix(String arg0, String arg1) throws XMLStreamException {
		// TODO Auto-generated method stub
		
	}

	public void writeAttribute(String localName, String value)
			throws XMLStreamException {
		this.writeAttribute(XMLConstants.NULL_NS_URI, localName, value);
	}

	public void writeAttribute(String namespaceURI, String localName, String value)
			throws XMLStreamException {
		this.writeAttribute(null, namespaceURI, localName, value);
	}

	public void writeAttribute(String prefix, String namespaceURI, String localName, String value) throws XMLStreamException {
		try {
			encoder.encodeAttribute(namespaceURI, localName, prefix, new StringValue(value));
		} catch (Exception e) {
			throw new XMLStreamException(e);
		}
	}

	public void writeCData(String data) throws XMLStreamException {
		// TODO CDATA, is this correct
		this.writeCharacters(data);
	}

	public void writeCharacters(String text) throws XMLStreamException {
		try {
			encoder.encodeCharacters(new StringValue(text));
		} catch (Exception e) {
			throw new XMLStreamException(e);
		}
	}

	public void writeCharacters(char[] text, int start, int len)
			throws XMLStreamException {
		try {
			encoder.encodeCharacters(new StringValue(new String(text, start, len)));
		} catch (Exception e) {
			throw new XMLStreamException(e);
		}
	}

	public void writeComment(String data) throws XMLStreamException {
		try {
			// TODO improve EXI API
			char[] chars = data.toCharArray();
			encoder.encodeComment(chars, 0, chars.length);
		} catch (Exception e) {
			throw new XMLStreamException(e);
		}
	}

	public void writeDTD(String dtd) throws XMLStreamException {
		try {
			// TODO check whether this works
			encoder.encodeDocType("", "", "", dtd);
		} catch (Exception e) {
			throw new XMLStreamException(e);
		}
	}

	public void writeDefaultNamespace(String arg0) throws XMLStreamException {
		// TODO Auto-generated method stub
		
	}

	public void writeEmptyElement(String localName) throws XMLStreamException {
		this.writeEmptyElement(XMLConstants.NULL_NS_URI, localName);
	}

	public void writeEmptyElement(String namespaceURI, String localName)
			throws XMLStreamException {
		this.writeEmptyElement(null, localName, namespaceURI);
	}

	public void writeEmptyElement(String prefix, String localName, String namespaceURI)
			throws XMLStreamException {
		try {
			encoder.encodeStartElement(namespaceURI, localName, prefix);
			encoder.encodeEndElement();
		} catch (Exception e) {
			throw new XMLStreamException(e);
		}
	}

	public void writeEndDocument() throws XMLStreamException {
		try {
			encoder.encodeEndDocument();
		} catch (Exception e) {
			throw new XMLStreamException(e);
		}
	}

	public void writeEndElement() throws XMLStreamException {
		try {
			encoder.encodeEndElement();
		} catch (Exception e) {
			throw new XMLStreamException(e);
		}
	}

	public void writeEntityRef(String name) throws XMLStreamException {
		try {
			encoder.encodeEntityReference(name);
		} catch (Exception e) {
			throw new XMLStreamException(e);
		}
	}

	public void writeNamespace(String prefix, String namespaceURI)
			throws XMLStreamException {
		try {
			encoder.encodeNamespaceDeclaration(namespaceURI, prefix);
		} catch (Exception e) {
			throw new XMLStreamException(e);
		}
	}

	public void writeProcessingInstruction(String target)
			throws XMLStreamException {
		this.writeProcessingInstruction(target, Constants.EMPTY_STRING);
	}

	public void writeProcessingInstruction(String target, String data)
			throws XMLStreamException {
		try {
			encoder.encodeProcessingInstruction(target, data);
		} catch (Exception e) {
			throw new XMLStreamException(e);
		}
	}

	public void writeStartDocument() throws XMLStreamException {
		try {
			encoder.encodeStartDocument();
		} catch (Exception e) {
			throw new XMLStreamException(e);
		}
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
		this.writeStartElement(null, localName, namespaceURI);
	}

	public void writeStartElement(String prefix, String localName, String namespaceURI)
			throws XMLStreamException {
		try {
			assert(namespaceURI != null);
			assert(localName != null);
			encoder.encodeStartElement(namespaceURI, localName, prefix);
		} catch (Exception e) {
			throw new XMLStreamException(e);
		}
	}

}
