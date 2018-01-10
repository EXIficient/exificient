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

package com.siemens.ct.exi.main;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.Comment;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import com.siemens.ct.exi.core.EXIFactory;
import com.siemens.ct.exi.core.exceptions.EXIException;
import com.siemens.ct.exi.main.api.xmlpull.EXISerializer;

public class TestXmlPullEncoder extends AbstractTestEncoder {

	static final boolean DEBUG = false;

	protected EXIFactory ef;
	protected boolean isFragment;
	protected EXISerializer exiSerializer;

	public TestXmlPullEncoder(EXIFactory ef) throws EXIException {
		this(ef, false);
	}

	public TestXmlPullEncoder(EXIFactory ef, boolean useXMLStreamReader)
			throws EXIException {
		super();
		exiSerializer = new EXISerializer(ef);
		this.ef = ef;
		this.isFragment = ef.isFragment();
	}

	@Override
	public void encodeTo(InputStream xmlInput, OutputStream exiOutput)
			throws Exception {

		exiSerializer.setOutput(exiOutput, null);

		XMLInputFactory xmlFactory = XMLInputFactory.newInstance();
		xmlFactory.isPropertySupported(XMLInputFactory.IS_NAMESPACE_AWARE);

		// do not resolve DTDs
		xmlFactory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES,
				Boolean.FALSE);
		// AND
		// requires the parser to replace internal entity references with their
		// replacement text and report them as characters
		xmlFactory.setProperty(XMLInputFactory.SUPPORT_DTD, Boolean.FALSE);
		xmlFactory.setProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES,
				Boolean.FALSE);

		XMLEventReader reader = xmlFactory.createXMLEventReader(xmlInput);

		exiSerializer.startDocument(null, null);

		while (reader.hasNext()) {
			XMLEvent event = reader.nextEvent();
			switch (event.getEventType()) {
			case XMLStreamConstants.NAMESPACE:
				Namespace nsE = (Namespace) event;
				if (DEBUG) {
					System.out.println("Namespace: " + nsE.getPrefix() + "\t"
							+ nsE.getNamespaceURI());
				}
				exiSerializer.setPrefix(nsE.getPrefix(), nsE.getNamespaceURI());
				break;
			case XMLStreamConstants.START_ELEMENT:

				StartElement element = (StartElement) event;
				if (DEBUG) {
					System.out.println("Start Element: " + element.getName());
				}

				@SuppressWarnings("rawtypes")
				Iterator iteratorNSs = element.getNamespaces();
				while (iteratorNSs.hasNext()) {
					Namespace ns = (Namespace) iteratorNSs.next();
					if (DEBUG) {
						System.out.println("Namespace: " + ns.getPrefix()
								+ "\t" + ns.getNamespaceURI());
					}
					exiSerializer.setPrefix(ns.getPrefix(),
							ns.getNamespaceURI());
				}

				exiSerializer.startTag(element.getName().getNamespaceURI(),
						element.getName().getLocalPart());

				@SuppressWarnings("rawtypes")
				Iterator iteratorATs = element.getAttributes();
				while (iteratorATs.hasNext()) {
					Attribute attribute = (Attribute) iteratorATs.next();
					QName name = attribute.getName();
					String value = attribute.getValue();
					if (DEBUG) {
						System.out.println("Attribute name/value: " + name
								+ "/" + value);
					}
					exiSerializer.attribute(name.getNamespaceURI(),
							name.getLocalPart(), value);
				}
				break;
			case XMLStreamConstants.END_ELEMENT:
				EndElement eelement = (EndElement) event;
				if (DEBUG) {
					System.out.println("End element:" + eelement.getName());
				}
				exiSerializer.endTag(eelement.getName().getNamespaceURI(),
						eelement.getName().getLocalPart());
				break;
			case XMLStreamConstants.PROCESSING_INSTRUCTION:
				break;
			case XMLStreamConstants.CHARACTERS:
			case XMLStreamConstants.SPACE: /* ignorable whitespace */
				Characters characters = (Characters) event;
				if (DEBUG) {
					System.out.println("Text: " + characters.getData());
				}
				exiSerializer.text(characters.getData());
				break;
			case XMLStreamConstants.COMMENT:
				Comment cm = (Comment) event;
				if (DEBUG) {
					System.out.println("CM: " + cm.getText());
				}
				exiSerializer.comment(cm.getText());
				break;
			case XMLStreamConstants.START_DOCUMENT:
				break;
			case XMLStreamConstants.END_DOCUMENT:
				break;
			case XMLStreamConstants.ENTITY_REFERENCE:
				break;
			case XMLStreamConstants.ATTRIBUTE:
				Attribute at = (Attribute) event;
				if (DEBUG) {
					System.out.println("AT: " + at.getName().getLocalPart()
							+ ": " + at.getValue());
				}
				exiSerializer.attribute(at.getName().getNamespaceURI(), at
						.getName().getLocalPart(), at.getValue());
				break;
			case XMLStreamConstants.DTD:
				break;
			case XMLStreamConstants.CDATA:
				break;

			case XMLStreamConstants.NOTATION_DECLARATION:
				break;
			case XMLStreamConstants.ENTITY_DECLARATION:
				break;

			}
		}

		exiSerializer.endDocument();
	}

	public static void main(String[] args) throws Exception {

		// EXI output stream
		OutputStream encodedOutput = getOutputStream(QuickTestConfiguration
				.getExiLocation());

		// XML input stream
		InputStream xmlInput = new BufferedInputStream(new FileInputStream(
				QuickTestConfiguration.getXmlLocation()));

		// create test-encoder & encode to EXI
		TestXmlPullEncoder testEncoder = new TestXmlPullEncoder(
				TestXmlPullEncoder.getQuickTestEXIactory());
		// EXIFactory ef = testEncoder.getQuickTestEXIactory(); // get factory
		// // setup encoding options
		// setupEncodingOptions(ef);

		// testEncoder.setupEXIWriter(ef);
		testEncoder.encodeTo(xmlInput, encodedOutput);

		encodedOutput.flush();

		System.out.println("[ENC-StAX] "
				+ QuickTestConfiguration.getXmlLocation() + " --> "
				+ QuickTestConfiguration.getExiLocation());
	}

}
