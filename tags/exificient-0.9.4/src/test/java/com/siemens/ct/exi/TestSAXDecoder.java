/*
 * Copyright (C) 2007-2015 Siemens AG
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

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import com.siemens.ct.exi.data.AbstractTestCase;
import com.siemens.ct.exi.exceptions.EXIException;

@SuppressWarnings("all")
public class TestSAXDecoder extends AbstractTestDecoder {
	protected TransformerFactory tf;
	protected XMLReader exiReader;
	protected Transformer transformer;

	// protected EXIFactory ef;

	public TestSAXDecoder(EXIFactory ef) throws EXIException,
			TransformerConfigurationException {
		super();

		// this.ef = ef;
		tf = TransformerFactory.newInstance();

		exiReader = ef.createEXIReader();

		transformer = tf.newTransformer();

		if (ef.isFragment()) {
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,
					"yes");
		}
		transformer.setOutputProperty(OutputKeys.METHOD, "xml");
		transformer.setOutputProperty(OutputKeys.ENCODING, AbstractTestCase.ENCODING); // ASCII
	}

	// @Override
	// public void setupEXIReader(EXIFactory ef) throws EXIException,
	// TransformerConfigurationException {
	// exiReader = ef.createEXIReader();
	//
	// transformer = tf.newTransformer();
	//
	// if (ef.isFragment()) {
	// transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,
	// "yes");
	// }
	// transformer.setOutputProperty(OutputKeys.METHOD, "xml");
	// transformer.setOutputProperty(OutputKeys.ENCODING, "iso-8859-1"); //
	// ASCII
	//
	// }

	@Override
	public void decodeTo(InputStream exiDocument, OutputStream xmlOutput)
			throws Exception {

		Result result = new StreamResult(xmlOutput);
		InputSource is = new InputSource(exiDocument);
		SAXSource exiSource = new SAXSource(is);
		exiSource.setXMLReader(exiReader);
		transformer.transform(exiSource, result);
		
		// this.decodeTo(ef, exiDocument, xmlOutput, transformer);
	}

	// protected void decodeTo(EXIFactory ef, InputStream exiDocument,
	// OutputStream xmlOutput, Transformer transformer) throws
	// TransformerException, ParserConfigurationException, SAXException,
	// IOException, EXIException {
	//
	// // exiDocument = new BufferedInputStream(exiDocument);
	//
	//
	//
	//
	// // ContentHandler ch = null;
	// // exiReader.setContentHandler(ch);
	// // exiReader.parse(is);
	// //
	// // SAXParserFactory spf = SAXParserFactory.newInstance();
	// // SAXParser sp = spf.newSAXParser();
	// // DefaultHandler dh = new DefaultEXIHandler(ef);
	// // sp.parse(exiDocument, dh);
	//
	// Result result = new StreamResult(xmlOutput);
	// InputSource is = new InputSource(exiDocument);
	// SAXSource exiSource = new SAXSource(is);
	// exiSource.setXMLReader(exiReader);
	// transformer.transform(exiSource, result);
	// }

	public static void main(String[] args) throws Exception {
		// create test-decoder
		TestSAXDecoder testDecoder = new TestSAXDecoder(
				TestSAXDecoder.getQuickTestEXIactory());

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

		System.out.println("[DEC-SAX] "
				+ QuickTestConfiguration.getExiLocation() + " --> "
				+ decodedXMLLocation);
	}

}
