/*
 * Copyright (c) 2007-2016 Siemens AG
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

package com.siemens.ct.exi;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

import org.xmlpull.v1.XmlPullParser;

import com.siemens.ct.exi.api.xmlpull.EXIPullParser;
import com.siemens.ct.exi.exceptions.EXIException;

public class TestXmlPullDecoder extends AbstractTestDecoder {

	static final boolean DEBUG = false;

	protected boolean isFragment;
	protected EXIPullParser xpp;

	public TestXmlPullDecoder(EXIFactory ef) throws EXIException {
		super();

		xpp = new EXIPullParser(ef);
	}

	@Override
	public void decodeTo(InputStream exiDocument, OutputStream xmlOutput)
			throws Exception {

		// ByteArrayOutputStream baos = new ByteArrayOutputStream();
		// XMLOutputFactory output = XMLOutputFactory.newInstance();
		// XMLStreamWriter writer = output.createXMLStreamWriter( baos );
		// writer.writeStartDocument();
		// if(false) {
		// writer.setPrefix("c","http://c");
		// writer.writeStartElement("http://c","a");
		//
		// // writer.writeAttribute("b","blah");
		// // writer.writeNamespace("c","http://c");
		// // writer.setPrefix("d","http://c");
		// // writer.writeEmptyElement("http://c","d");
		// // writer.writeAttribute("http://c","chris","fry");
		// // writer.writeNamespace("d","http://c");
		// // writer.writeCharacters("Jean Arp");
		//
		// writer.writeEndElement();
		// } else {
		// writer.setPrefix("ns3","http://www.foo.com");
		// writer.writeStartElement("http://www.foo.com","personnel");
		// writer.writeNamespace("ns3","http://www.foo.com");
		// writer.writeEndElement();
		// }
		//
		// writer.flush();

		XMLStreamWriter out = XMLOutputFactory.newInstance()
				.createXMLStreamWriter(
						new OutputStreamWriter(xmlOutput, "utf-8"));

		xpp.setInput(exiDocument, null);

		out.writeStartDocument();

		while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
			int ev = xpp.next();
			switch (ev) {
			case XmlPullParser.START_TAG:
				// set prefix
				int nsCnt = xpp.getNamespaceCount(-1);
				for (int i = 0; i < nsCnt; i++) {
					out.setPrefix(xpp.getNamespacePrefix(i),
							xpp.getNamespaceUri(i));
				}

				// element
				if (DEBUG) {
					System.out.println("SE > " + xpp.getName());
				}
				out.writeStartElement(xpp.getNamespace(), xpp.getName());

				// write namespace
				for (int i = 0; i < nsCnt; i++) {
					out.writeNamespace(xpp.getNamespacePrefix(i),
							xpp.getNamespaceUri(i));
					out.setPrefix(xpp.getNamespacePrefix(i),
							xpp.getNamespaceUri(i));
					if (DEBUG) {
						System.out.println("\tNS " + xpp.getNamespacePrefix(i)
								+ " -> " + xpp.getNamespaceUri(i));
					}
				}

				// Attributes
				int atCnt = xpp.getAttributeCount();
				if (atCnt > 0) {
					if (DEBUG) {
						System.out.println("\t" + atCnt + "# Attributes ");
					}
					for (int i = 0; i < atCnt; i++) {
						if (DEBUG) {
							System.out.println("\t" + i + "\t"
									+ xpp.getAttributeName(i) + " : "
									+ xpp.getAttributeValue(i));
						}
						String ns = xpp.getAttributeNamespace(i);
						// javax.xml.stream.XMLStreamException: Unbound
						// namespace URI ''
						if (ns.length() > 0) {
							out.writeAttribute(ns, xpp.getAttributeName(i),
									xpp.getAttributeValue(i));
						} else {
							out.writeAttribute(xpp.getAttributeName(i),
									xpp.getAttributeValue(i));
						}
					}
				}
				// xpp.getNamespaceCount(depth)
				break;
			case XmlPullParser.END_TAG:
				if (DEBUG) {
					System.out.println("EE < " + xpp.getName());
				}
				out.writeEndElement();
				break;
			case XmlPullParser.TEXT:
				if (DEBUG) {
					System.out.println("CH  " + xpp.getText());
				}
				out.writeCharacters(xpp.getText());
				break;
			case XmlPullParser.COMMENT:
				if (DEBUG) {
					System.out.println("CM  " + xpp.getText());
				}
				out.writeComment(xpp.getText());
				break;
			}
		}

		out.writeEndDocument();

	}

	public static void main(String[] args) throws Exception {

		// create test-decoder
		TestXmlPullDecoder testDecoder = new TestXmlPullDecoder(
				TestXmlPullDecoder.getQuickTestEXIactory());

		// // get factory
		// EXIFactory ef;
		// if(QuickTestConfiguration.INCLUDE_OPTIONS &&
		// QuickTestConfiguration.INCLUDE_SCHEMA_ID) {
		// // decoder should be able to decode file without settings:
		// // EXI Options document carries necessary information
		// ef = DefaultEXIFactory.newInstance();
		// } else {
		// ef = testDecoder.getQuickTestEXIactory();
		// }

		// exi document
		InputStream exiDocument = new FileInputStream(
				QuickTestConfiguration.getExiLocation());
		assert (exiDocument.available() > 0);

		// decoded xml output
		String decodedXMLLocation = QuickTestConfiguration.getExiLocation()
				+ ".xml";
		OutputStream xmlOutput = new FileOutputStream(decodedXMLLocation);

		// decode EXI to XML
		// testDecoder.setupEXIReader(ef);
		testDecoder.decodeTo(exiDocument, xmlOutput);

		System.out.println("[DEC-XmlPull] "
				+ QuickTestConfiguration.getExiLocation() + " --> "
				+ decodedXMLLocation);
	}

}
