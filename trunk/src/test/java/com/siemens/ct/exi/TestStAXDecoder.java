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

package com.siemens.ct.exi;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stax.StAXSource;
import javax.xml.transform.stream.StreamResult;

import com.siemens.ct.exi.api.stream.StAXDecoder;
import com.siemens.ct.exi.data.AbstractTestCase;
import com.siemens.ct.exi.exceptions.EXIException;

@SuppressWarnings("all")
public class TestStAXDecoder extends AbstractTestDecoder {

	public static final boolean JRE6_OR_GREATER = false;

	protected TransformerFactory tf;
	protected Transformer transformer;
	protected boolean isFragment;
	protected StAXDecoder exiReader;

	public TestStAXDecoder(EXIFactory ef)
			throws TransformerConfigurationException, EXIException {
		super();

		tf = TransformerFactory.newInstance();

		transformer = tf.newTransformer();

		isFragment = ef.isFragment();
		if (ef.isFragment()) {
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,
					"yes");
		}
		transformer.setOutputProperty(OutputKeys.METHOD, "xml");
		transformer.setOutputProperty(OutputKeys.ENCODING, AbstractTestCase.ENCODING); // ASCII

		exiReader = new StAXDecoder(ef);
	}

	// @Override
	// public void setupEXIReader(EXIFactory ef) throws Exception {
	// transformer = tf.newTransformer();
	//
	// isFragment = ef.isFragment();
	// if (ef.isFragment()) {
	// transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,
	// "yes");
	// }
	// transformer.setOutputProperty(OutputKeys.METHOD, "xml");
	// transformer.setOutputProperty(OutputKeys.ENCODING, "iso-8859-1"); //
	// ASCII
	//
	// exiReader = new StAXDecoder(ef);
	// }

	@Override
	public void decodeTo(InputStream exiDocument, OutputStream xmlOutput)
			throws Exception {

		if (JRE6_OR_GREATER) {
			// Note: The following code works with Java 1.6+ only
			// Note: Some events seem not be fully supported e.g.,
			// handleComment() { // no-op }
			// see
			// http://www.docjar.com/html/api/com/sun/org/apache/xalan/internal/xsltc/trax/StAXStream2SAX.java.html
			Result result = new StreamResult(xmlOutput);
			exiReader.setInputStream(exiDocument);
			StAXSource exiSource = new StAXSource(exiReader);
			transformer.transform(exiSource, result);
		} else {

			XMLOutputFactory xmlof = XMLOutputFactory.newInstance();

			XMLStreamWriter xmlw;
			ByteArrayOutputStream baosFrags = new ByteArrayOutputStream();
			if (isFragment) {
				xmlw = xmlof.createXMLStreamWriter(baosFrags);
			} else {
				xmlw = xmlof.createXMLStreamWriter(xmlOutput, "ISO 8859-1");
			}

			// XMLStreamReader exiReader = new StAXDecoder(ef, exiDocument);
			exiReader.setInputStream(exiDocument);

			xmlw.writeStartDocument();

			String lnFragment = "xml-fragment";

			if (isFragment) {
				xmlw.writeStartElement("", lnFragment, "");
			}

			while (exiReader.hasNext()) {
				int event = exiReader.next();

				switch (event) {
				case XMLStreamConstants.START_DOCUMENT:
					// should have happened beforehand
					throw new EXIException("Unexpected START_DOCUMENT event");
				case XMLStreamConstants.END_DOCUMENT:

					if (isFragment) {
						xmlw.writeEndElement();
					}

					xmlw.writeEndDocument();
					break;
				case XMLStreamConstants.START_ELEMENT:
					QName qn = exiReader.getName();
					String pfx = exiReader.getPrefix();
					xmlw.writeStartElement(pfx, qn.getLocalPart(),
							qn.getNamespaceURI());

					// http://markmail.org/message/yrdmpcjtdh4utcx7#query:+page:1+mid:yrdmpcjtdh4utcx7+state:results

					// NS declarations
					int nsCnt = exiReader.getNamespaceCount();
					for (int i = 0; i < nsCnt; i++) {
						String nsPfx = exiReader.getNamespacePrefix(i);
						String nsUri = exiReader.getNamespaceURI(i);
						xmlw.writeNamespace(nsPfx, nsUri);
					}
					// attributes
					int atCnt = exiReader.getAttributeCount();
					for (int i = 0; i < atCnt; i++) {
						String atPfx = exiReader.getAttributePrefix(i);
						QName atQn = exiReader.getAttributeName(i);
						String atVal = exiReader.getAttributeValue(i);
						xmlw.writeAttribute(atPfx, atQn.getNamespaceURI(),
								atQn.getLocalPart(), atVal);
					}

					break;
				case XMLStreamConstants.END_ELEMENT:
					xmlw.writeEndElement();
					break;
				case XMLStreamConstants.NAMESPACE:
					break;
				case XMLStreamConstants.CHARACTERS:
					String ch = exiReader.getText();
					xmlw.writeCharacters(ch);
					break;
				case XMLStreamConstants.SPACE:
					@SuppressWarnings("unused")
					String ignorableSpace = exiReader.getText();
					xmlw.writeCharacters(ignorableSpace);
					break;
				case XMLStreamConstants.ATTRIBUTE:
					@SuppressWarnings("unused")
					int attsX = exiReader.getAttributeCount();
					// exiWriter.writeCharacters(ch);
					break;
				case XMLStreamConstants.COMMENT:
					xmlw.writeComment(exiReader.getText());
					break;
				case XMLStreamConstants.PROCESSING_INSTRUCTION:
					xmlw.writeProcessingInstruction(exiReader.getPITarget(),
							exiReader.getPIData());
					break;
				case XMLStreamConstants.DTD:
					xmlw.writeDTD(exiReader.getText());
					break;
				case XMLStreamConstants.ENTITY_REFERENCE:
					xmlw.writeEntityRef(exiReader.getText());
					break;
				default:
					System.out.println("StAX Event '" + event
							+ "' not supported!");
				}
			}

			// Close the writer to flush the output
			xmlw.close();

			if (isFragment) {
				String s = baosFrags.toString();
				int index1 = s.indexOf(lnFragment);
				s = s.substring(index1 + lnFragment.length() + 1);
				int index2 = s.indexOf(lnFragment);
				s = s.substring(0, index2 - 2);
				xmlOutput.write(s.getBytes());
			}

		}

	}

	public static void main(String[] args) throws Exception {

		// create test-decoder
		TestStAXDecoder testDecoder = new TestStAXDecoder(
				TestStAXDecoder.getQuickTestEXIactory());

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

		System.out.println("[DEC-StAX] "
				+ QuickTestConfiguration.getExiLocation() + " --> "
				+ decodedXMLLocation);
	}

}
