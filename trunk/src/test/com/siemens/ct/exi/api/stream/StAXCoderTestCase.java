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

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.TransformerException;

import org.custommonkey.xmlunit.XMLTestCase;

import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.helpers.DefaultEXIFactory;

public class StAXCoderTestCase extends XMLTestCase {

	public void _testNotebook() throws XMLStreamException, EXIException, IOException, TransformerException {
		String xmlInput = "./data/W3C/PrimerNotebook/notebook.xml";
		String exi = "./out/W3C/PrimerNotebook/notebook.xml.exi";
		String xmlOutput = "./out/W3C/PrimerNotebook/notebook.xml.exi.xml";
		EXIFactory exiFactory = DefaultEXIFactory.newInstance();
		
		// exiFactory.setCodingMode(CodingMode.BYTE_PACKED);
		
		encode(exiFactory, xmlInput, exi);
		
		decode(exiFactory, exi, xmlOutput);
	}
	
	
	protected void encode(EXIFactory exiFactory, String xmlInput, String exiOutput) throws XMLStreamException, EXIException, IOException {
		XMLInputFactory xmlFactory = XMLInputFactory.newInstance(); 
		InputStream is = new FileInputStream(xmlInput);
		XMLStreamReader xmlReader = xmlFactory.createXMLStreamReader(is); 
		
		
		
		OutputStream os = new FileOutputStream(exiOutput);
		StAXStreamWriter exiWriter = new StAXStreamWriter(exiFactory, os);
		
		exiWriter.encode(xmlReader);
	}
	
	protected void decode(EXIFactory exiFactory, String exiInput, String xmlOutput) throws EXIException, IOException, TransformerException, XMLStreamException {
		InputStream is = new FileInputStream(exiInput);
		OutputStream os = new FileOutputStream(xmlOutput);
		StAXStreamReader exiReader = new StAXStreamReader(exiFactory, is);
//		XMLStreamReader xmlStream = null;
//		exiReader.setXMLStreamReader(xmlStream);
		
		System.out.println("----");
		
		XMLOutputFactory xof =  XMLOutputFactory.newInstance();
		XMLStreamWriter xmlWriter =  xof.createXMLStreamWriter(os);
		
		xmlWriter.writeStartDocument();
		
		while(exiReader.hasNext()) {
	         int event = exiReader.next();
			 switch(event) {
			    case XMLStreamConstants.START_DOCUMENT:
			    	// should have happen beforehand
			    	throw new EXIException("Unexpected START_DOCUMENT event");
			    case XMLStreamConstants.END_DOCUMENT:
			    	xmlWriter.writeEndDocument();
			    	break;
			    case XMLStreamConstants.START_ELEMENT:
			    	QName qn = exiReader.getName();
			    	String pfx = exiReader.getPrefix();
			    	System.out.println("> SE " + qn);
			    	xmlWriter.writeStartElement(pfx, qn.getLocalPart(), qn.getNamespaceURI());
			    	int atts = exiReader.getAttributeCount();
			    	for(int i=0; i<atts; i++) {
			    		QName atQname = exiReader.getAttributeName(i);
			    		String atVal = exiReader.getAttributeValue(i);
			    		System.out.println("  AT " + atQname +  " = " + atVal);
			    		xmlWriter.writeAttribute(atQname.getNamespaceURI(), atQname.getLocalPart(), atVal);
			    	}
			    	break;
			    case XMLStreamConstants.END_ELEMENT:
			    	System.out.println("< EE ");
			    	xmlWriter.writeEndElement();
			    	break;
			    case XMLStreamConstants.NAMESPACE:
			    	String prefix = null;
			    	String namespaceURI = null;
			    	xmlWriter.writeNamespace(prefix, namespaceURI);
			    	break;
			    case XMLStreamConstants.CHARACTERS:
			    	String ch = exiReader.getText();
			    	System.out.println("> ch " + ch);
			    	xmlWriter.writeCharacters(ch);
			    	break;
			    case XMLStreamConstants.SPACE:
			    	String ignorableSpace = exiReader.getText();
			    	break;
			    case XMLStreamConstants.ATTRIBUTE:
			    	int attsX = exiReader.getAttributeCount();
			    	// exiWriter.writeCharacters(ch);
			    	break;
			    default:
			    	System.out.println("Event '" + event +"' not supported!");
			    }
	     }
		
		
		
		
		
		
		
		xmlWriter.writeEndDocument();

	   xmlWriter.flush();
	   xmlWriter.close();
		
//		TransformerFactory tf = TransformerFactory.newInstance();
//		Transformer t = tf.newTransformer();
//		StAXSource staxSource = new StAXSource(exiReader);
//		Result xmlResult = new StreamResult(xmlOutput);
//		
//		t.transform(staxSource, xmlResult);
		
//		InputStream is = new FileInputStream(exiInput);
//		exiReader.parse(is);
	}
	
	
	
	
	public static void main(String[] args) throws Exception {

		StAXCoderTestCase st = new StAXCoderTestCase();
		st._testNotebook();

	}

}
