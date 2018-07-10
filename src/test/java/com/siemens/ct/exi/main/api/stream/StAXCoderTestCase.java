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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;

import junit.framework.AssertionFailedError;

import com.siemens.ct.exi.core.EXIFactory;
import com.siemens.ct.exi.core.FidelityOptions;
import com.siemens.ct.exi.core.exceptions.EXIException;
import com.siemens.ct.exi.core.helpers.DefaultEXIFactory;
import com.siemens.ct.exi.main.TestStAXDecoder;
import com.siemens.ct.exi.main.TestStAXEncoder;
import com.siemens.ct.exi.main.data.AbstractTestCase;

public class StAXCoderTestCase extends AbstractTestCase {

	public StAXCoderTestCase(String s) {
		super(s);
	}

	public void testNotebook() throws AssertionFailedError, Exception {
		String xmlInput = "./data/W3C/PrimerNotebook/notebook.xml";
		String exiOutput = "./out/W3C/PrimerNotebook/notebook.xml.exi";
		String xmlOutput = "./out/W3C/PrimerNotebook/notebook.xml.exi.xml";

		EXIFactory ef1 = DefaultEXIFactory.newInstance();
		this._test(ef1, xmlInput, exiOutput, xmlOutput, false);

		EXIFactory ef2 = DefaultEXIFactory.newInstance();
		ef2.setFidelityOptions(FidelityOptions.createAll());
		this._test(ef2, xmlInput, exiOutput, xmlOutput, true);
	}
	
	public void testEXIbyExample() throws AssertionFailedError, Exception {
		String xmlInput = "./data/W3C/EXIbyExample/XMLSample.xml";
		String exiOutput = "./out/W3C/EXIbyExample/XMLSample.xml.exi";
		String xmlOutput = "./out/W3C/EXIbyExample/XMLSample.xml.exi.xml";

		EXIFactory ef1 = DefaultEXIFactory.newInstance();
		this._test(ef1, xmlInput, exiOutput, xmlOutput, false);

		EXIFactory ef2 = DefaultEXIFactory.newInstance();
		ef2.setFidelityOptions(FidelityOptions.createAll());
		this._test(ef2, xmlInput, exiOutput, xmlOutput, true);
	}

	public void testXsiType() throws AssertionFailedError, Exception {

		String xmlInput = "./data/schema/xsi-type.xml";
		String exiOutput = "./out/xsi-type.xml.exi";
		String xmlOutput = "./out/xsi-type.xml.exi.xml";

		EXIFactory ef1 = DefaultEXIFactory.newInstance();
		this._test(ef1, xmlInput, exiOutput, xmlOutput, false);

		EXIFactory ef2 = DefaultEXIFactory.newInstance();
		ef2.setFidelityOptions(FidelityOptions.createAll());
		this._test(ef2, xmlInput, exiOutput, xmlOutput, true);
	}

	protected void _test(EXIFactory exiFactory, String xmlInput,
			String exiOutput, String xmlOutput, boolean xmlEqual)
			throws AssertionFailedError, Exception {

		
		for(int i=0; i<2; i++) {
			TestStAXEncoder tse = new TestStAXEncoder(exiFactory);
			if(i == 0) {
				 tse = new TestStAXEncoder(exiFactory);
			} else {
				 tse = new TestStAXEncoder(exiFactory, true);
			}
			
			// encode
			File fOut = new File(exiOutput);
			fOut.getParentFile().mkdirs();
			OutputStream exiOut = new FileOutputStream(fOut);
			InputStream xmlIn = new FileInputStream(xmlInput);
			tse.encodeTo(xmlIn, exiOut);
			exiOut.close();
			xmlIn.close();

			// decode
			InputStream exiIn = new FileInputStream(exiOutput);
			TestStAXDecoder tsd = new TestStAXDecoder(exiFactory);
			OutputStream xmlOut = new FileOutputStream(xmlOutput);
			tsd.decodeTo(exiIn, xmlOut);
			xmlOut.close();

			// check equality
			// @SuppressWarnings("unused")
			// InputStream control = new FileInputStream(xmlInput);
			InputStream testXML = new FileInputStream(xmlOutput);
			this.checkXMLValidity(exiFactory, testXML);
			if (xmlEqual) {
				// this.checkXMLEquality(exiFactory, control, testXML);
			}
		}
		


	}

	// protected void encode(EXIFactory exiFactory, String xmlInput, String
	// exiOutput) throws XMLStreamException, EXIException, IOException {
	// XMLInputFactory xmlFactory = XMLInputFactory.newInstance();
	// InputStream is = new FileInputStream(xmlInput);
	//
	// // XMLStreamReader xmlReader = xmlFactory.createXMLStreamReader(is);
	// XMLEventReader xmlReader = xmlFactory.createXMLEventReader(is);
	//
	//
	// OutputStream os = new FileOutputStream(exiOutput);
	// StAXEncoder exiWriter = new StAXEncoder(exiFactory, os);
	//
	// exiWriter.encode(xmlReader);
	// }

	// protected void decode(EXIFactory exiFactory, String exiInput, String
	// xmlOutput) throws EXIException, IOException, TransformerException,
	// XMLStreamException {
	// InputStream is = new FileInputStream(exiInput);
	// OutputStream os = new FileOutputStream(xmlOutput);
	// XMLStreamReader exiReader = new StAXStreamReader(exiFactory, is);
	// // XMLStreamReader xmlStream = null;
	// // exiReader.setXMLStreamReader(xmlStream);
	//
	// // System.err.println("TEST");
	// // FileInputStream fileInputStream = new
	// FileInputStream("./data/schema/xsi-type.xml");
	// // exiReader =
	// XMLInputFactory.newInstance().createXMLStreamReader(fileInputStream);
	//
	//
	//
	// System.out.println("----");
	//
	// XMLOutputFactory xof = XMLOutputFactory.newInstance();
	// XMLStreamWriter xmlWriter = xof.createXMLStreamWriter(os);
	//
	// xmlWriter.writeStartDocument();
	//
	// while(exiReader.hasNext()) {
	// int event = exiReader.next();
	// switch(event) {
	// case XMLStreamConstants.START_DOCUMENT:
	// // should have happen beforehand
	// throw new EXIException("Unexpected START_DOCUMENT event");
	// case XMLStreamConstants.END_DOCUMENT:
	// xmlWriter.writeEndDocument();
	// break;
	// case XMLStreamConstants.START_ELEMENT:
	// QName qn = exiReader.getName();
	// String pfx = exiReader.getPrefix();
	// System.out.println("> SE " + qn);
	// xmlWriter.writeStartElement(pfx, qn.getLocalPart(),
	// qn.getNamespaceURI());
	//
	// //
	// // xmlWriter.setPrefix(pfx, qn.getNamespaceURI());
	// xmlWriter.writeNamespace(pfx, qn.getNamespaceURI());
	//
	// int atts = exiReader.getAttributeCount();
	// for(int i=0; i<atts; i++) {
	// QName atQname = exiReader.getAttributeName(i);
	// String atPfx = exiReader.getAttributePrefix(i);
	// String atVal = exiReader.getAttributeValue(i);
	// System.out.println("  AT " + atQname + " = " + atVal);
	// xmlWriter.writeAttribute(atPfx, atQname.getNamespaceURI(),
	// atQname.getLocalPart(), atVal);
	// }
	// break;
	// case XMLStreamConstants.END_ELEMENT:
	// System.out.println("< EE ");
	// xmlWriter.writeEndElement();
	// break;
	// case XMLStreamConstants.NAMESPACE:
	// String prefix = null;
	// String namespaceURI = null;
	// xmlWriter.writeNamespace(prefix, namespaceURI);
	// break;
	// case XMLStreamConstants.CHARACTERS:
	// String ch = exiReader.getText();
	// System.out.println("> ch " + ch);
	// xmlWriter.writeCharacters(ch);
	// break;
	// case XMLStreamConstants.SPACE:
	// String ignorableSpace = exiReader.getText();
	// break;
	// case XMLStreamConstants.ATTRIBUTE:
	// int attsX = exiReader.getAttributeCount();
	// // exiWriter.writeCharacters(ch);
	// break;
	// default:
	// System.out.println("Event '" + event +"' not supported!");
	// }
	// }
	//
	//
	//
	//
	//
	//
	//
	// xmlWriter.writeEndDocument();
	//
	// xmlWriter.flush();
	// xmlWriter.close();
	// }
	//
	
	// https://github.com/EXIficient/exificient/issues/18
	public void testIssue18() throws AssertionFailedError, Exception {
		EXIFactory ef = DefaultEXIFactory.newInstance();
		// ef.getFidelityOptions().setFidelity(FidelityOptions.FEATURE_PREFIX, true);
		String sxml = "<?xml version=\"1.0\" ?><ns2:create-resource-request-message xmlns:ns5=\"http://www.bubblegumproject.com/2018/polis\" xmlns=\"\" xmlns:ns3=\"http://www.bubblegumproject.com/2018/uia\" xmlns:ns2=\"http://www.bubblegumproject.com/2018/fabric\"><ns2:type>urn:fabric:type:eUol2jOc4XR67WEZ8jA2vg:0.0.0?=name=com.bubblegumproject.fabric:Type/Message/CreateResourceRequestMessage</ns2:type><ns2:coordinates>urn:fabric:co:wUv2GxsVDcFdqVu81XWMqw:0.0.0</ns2:coordinates><ns2:source>urn:fabric:co:JkaddGXCtF_66OBmbo94sg:0.0.0</ns2:source><ns2:timestamp>2018-07-10T09:59:09.594470900Z</ns2:timestamp><ns2:correlation-id>wUv2GxsVDcFdqVu81XWMqw</ns2:correlation-id><ns2:destination>urn:fabric:co:qEooMf4_59zR1RKnP7VvqQ:0.0.0</ns2:destination><ns2:data xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"ns5:blog-post-data\"><ns2:type>urn:fabric:type:rE2_W6kHEdHDm_aogLb5nQ:0.0.0?=name=com.bubblegumproject.polis:/Type/Blog/BlogPostData</ns2:type><ns2:coordinates>urn:fabric:co:H0DiR9nVOTaqySRJlxPGhg:0.0.0</ns2:coordinates><ns2:title><ns3:default><ns3:value>Hello World!</ns3:value><ns3:locale>en-US</ns3:locale></ns3:default><ns3:alternatives><ns3:alt><ns3:value>Hello World!</ns3:value><ns3:locale>en-US</ns3:locale></ns3:alt></ns3:alternatives></ns2:title><ns2:created-time>2018-07-10T09:59:09.590473600Z</ns2:created-time><ns2:modified-time>2018-07-10T09:59:09.590473600Z</ns2:modified-time><ns2:segment xsi:type=\"ns2:segment\"><ns2:name>/hello-world</ns2:name></ns2:segment><ns5:content xsi:type=\"ns2:text-content\"><ns2:type>urn:fabric:type:2EewWCsXigVak99a0Z6srQ:0.0.0?=name=com.bubblegumproject.fabric:/Type/Content/Text/Plain</ns2:type><ns2:coordinates>urn:fabric:co:-kyXnYxJjuUbiqdH1IRFpg:0.0.0</ns2:coordinates><ns2:text>Hello World!</ns2:text></ns5:content><ns5:published-time>2018-07-10T09:59:09.589474300Z</ns5:published-time></ns2:data></ns2:create-resource-request-message>";
		InputStream isXML = new ByteArrayInputStream(sxml.getBytes());
		
		// encode
		TestStAXEncoder tse = new TestStAXEncoder(ef);
		ByteArrayOutputStream osEXI =  new ByteArrayOutputStream();
		tse.encodeTo(isXML, osEXI);
		
		// decode
		StAXDecoder exiReader = new StAXDecoder(ef);
		exiReader.setInputStream(new ByteArrayInputStream(osEXI.toByteArray()));
		
		List<Integer> nsCnts = new ArrayList<Integer>();
		
		while (exiReader.hasNext()) {
			int event = exiReader.next();

			switch (event) {
			case XMLStreamConstants.START_DOCUMENT:
				// should have happened beforehand
				break;
			case XMLStreamConstants.END_DOCUMENT:
				break;
			case XMLStreamConstants.START_ELEMENT:
				QName qn = exiReader.getName();
				String pfx = exiReader.getPrefix();
				System.out.println(">> " + pfx + " : " + qn);
				
				// NS declarations
				int nsCnt = exiReader.getNamespaceCount();
				nsCnts.add(nsCnt);
				for (int i = 0; i < nsCnt; i++) {
					String nsPfx = exiReader.getNamespacePrefix(i);
					String nsUri = exiReader.getNamespaceURI(i);
					System.out.println("\tNS: " +  nsPfx + " : " + nsUri);
				}
				// attributes
				int atCnt = exiReader.getAttributeCount();
				for (int i = 0; i < atCnt; i++) {
					String atPfx = exiReader.getAttributePrefix(i);
					QName atQn = exiReader.getAttributeName(i);
					String atVal = exiReader.getAttributeValue(i);
					System.out.println("\tAT: " +  atPfx + " : " + atQn + " = " + atVal);
				}

				break;
			case XMLStreamConstants.END_ELEMENT:
				// NS declarations
				int nsCntEndElement = exiReader.getNamespaceCount();
				int nsCntStartElement = nsCnts.remove(nsCnts.size()-1);
				System.out.println("<< " + exiReader.getPrefix() + " : " +  exiReader.getName());
				assertTrue(exiReader.getName()+ ", " + nsCntEndElement + " vs. " + nsCntStartElement, nsCntEndElement == nsCntStartElement);
				break;
			case XMLStreamConstants.NAMESPACE:
				break;
			case XMLStreamConstants.CHARACTERS:
				String ch = exiReader.getText();
				System.out.println("\tCH: " +  ch);
				break;
			case XMLStreamConstants.SPACE:
				break;
			case XMLStreamConstants.ATTRIBUTE:
				@SuppressWarnings("unused")
				int attsX = exiReader.getAttributeCount();
				break;
			case XMLStreamConstants.COMMENT:
				break;
			case XMLStreamConstants.PROCESSING_INSTRUCTION:
				break;
			case XMLStreamConstants.DTD:
				break;
			case XMLStreamConstants.ENTITY_REFERENCE:
				break;
			default:
				System.out.println("StAX Event '" + event
						+ "' not supported!");
			}
		}
		
	}

	public static void main(String[] args) throws Exception {

		StAXCoderTestCase st = new StAXCoderTestCase("StAX");
		// st.testNotebook();
		st.testXsiType();

	}

}
