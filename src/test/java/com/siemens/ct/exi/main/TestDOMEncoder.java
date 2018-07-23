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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.siemens.ct.exi.core.EXIFactory;
import com.siemens.ct.exi.core.exceptions.EXIException;
import com.siemens.ct.exi.main.api.dom.DOMWriter;
import com.siemens.ct.exi.main.api.dom.DocumentFragmentBuilder;
import com.siemens.ct.exi.main.util.NoEntityResolver;

public class TestDOMEncoder extends AbstractTestEncoder {

	protected DOMWriter enc;
	protected boolean isFragment;

	public TestDOMEncoder(EXIFactory ef) throws EXIException {
		super();
		// dom encoder
		enc = new DOMWriter(ef);
		isFragment = ef.isFragment();
	}

	// @Override
	// public void setupEXIWriter(EXIFactory ef) throws EXIException {
	// // dom encoder
	// enc = new DOMWriter(ef);
	// isFragment = ef.isFragment();
	// }

	public static Document getDocument(InputStream istr)
			throws ParserConfigurationException, SAXException, IOException {
		return getDocument(istr, "UTF-8");
	}

	public static Document getDocument(InputStream istr, String encoding)
			throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();
		dfactory.setNamespaceAware(true);

		DocumentBuilder documentBuilder = dfactory.newDocumentBuilder();

		// *skip* resolving entities like DTDs
		documentBuilder.setEntityResolver(new NoEntityResolver());

		documentBuilder.setErrorHandler(null);

		Reader reader = new InputStreamReader(istr, encoding);
		InputSource is = new InputSource(reader);
		is.setEncoding(encoding);

		Document doc = documentBuilder.parse(is);

		return doc;
	}

	// public static Document getDocument(InputSource is)
	// throws ParserConfigurationException, SAXException, IOException {
	// DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();
	// dfactory.setNamespaceAware(true);
	//
	// DocumentBuilder documentBuilder = dfactory.newDocumentBuilder();
	//
	// // *skip* resolving entities like DTDs
	// documentBuilder.setEntityResolver(new NoEntityResolver());
	//
	// documentBuilder.setErrorHandler(null);
	// Document doc = documentBuilder.parse(is);
	//
	// return doc;
	// }

	public static DocumentFragment getDocumentFragment(InputStream is)
			throws ParserConfigurationException, SAXException, IOException {

		DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();
		dfactory.setNamespaceAware(true);

		DocumentBuilder documentBuilder = dfactory.newDocumentBuilder();
		DocumentFragmentBuilder dfb = new DocumentFragmentBuilder(
				documentBuilder);

		DocumentFragment docFragment = dfb.parse(is);

		return docFragment;
	}

	public void encodeTo(InputStream xmlInput, OutputStream exiOutput)
			throws Exception {
		// document
		Node doc;
		if (isFragment) {
			doc = getDocumentFragment(xmlInput);
		} else {
			doc = getDocument(xmlInput);

		}

		// // write to console
		// StringWriter sw = new StringWriter();
		// TestDOMDecoder.nodeToWriter(doc, sw);
		// System.out.println(sw.toString());

		enc.setOutput(exiOutput);

		enc.encode(doc);

	}

	public static void main(String[] args) throws Exception {

		// create DOM encoder
		TestDOMEncoder testEncoder = new TestDOMEncoder(
				TestDOMEncoder.getQuickTestEXIactory());

		// // get factory
		// EXIFactory exiFactory = testEncoder.getQuickTestEXIactory();
		// testEncoder.setupEXIWriter(exiFactory);

		// EXI input stream
		InputStream xmlInput = new FileInputStream(
				QuickTestConfiguration.XML_FILE_LOCATION);

		// EXI output stream
		OutputStream encodedOutput = getOutputStream(QuickTestConfiguration
				.getExiLocation());

		// // setup encoding options
		// setupEncodingOptions(exiFactory);

		// generate EXI
		// testEncoder.setupEXIWriter(exiFactory);
		testEncoder.encodeTo(xmlInput, encodedOutput);
		encodedOutput.close();

		System.out.println("[ENC-DOM] "
				+ QuickTestConfiguration.getXmlLocation() + " --> "
				+ QuickTestConfiguration.getExiLocation());
	}

}
