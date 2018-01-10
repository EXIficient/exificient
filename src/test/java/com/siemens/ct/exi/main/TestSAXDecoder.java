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

import com.siemens.ct.exi.main.api.sax.SAXFactory;
import com.siemens.ct.exi.main.data.AbstractTestCase;
import com.siemens.ct.exi.core.EXIFactory;
import com.siemens.ct.exi.core.exceptions.EXIException;

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

		exiReader = new SAXFactory(ef).createEXIReader();

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
