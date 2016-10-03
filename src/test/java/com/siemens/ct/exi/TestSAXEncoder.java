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

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.xml.sax.DTDHandler;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.siemens.ct.exi.api.sax.EXIResult;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.util.FragmentUtilities;
import com.siemens.ct.exi.util.NoEntityResolver;
import com.siemens.ct.exi.util.SkipRootElementXMLReader;

public class TestSAXEncoder extends AbstractTestEncoder {

	protected EXIResult exiResult;
	protected boolean isFragment;

	public TestSAXEncoder(EXIFactory ef) throws EXIException {
		super();
		exiResult = new EXIResult(ef);
		isFragment = ef.isFragment();
	}

	// public void setupEXIWriter(EXIFactory ef) throws EXIException {
	// exiResult = new EXIResult(ef);
	// isFragment = ef.isFragment();
	// }

	protected XMLReader getXMLReader() throws Exception {
		// create xml reader
		XMLReader xmlReader;

		xmlReader = XMLReaderFactory
				.createXMLReader("org.apache.xerces.parsers.SAXParser");
		// xmlReader = XMLReaderFactory.createXMLReader();

		// set XMLReader features
		xmlReader.setFeature("http://xml.org/sax/features/namespaces", true);
		// do not report namespace declarations as attributes
		xmlReader.setFeature("http://xml.org/sax/features/namespace-prefixes",
				false);
		// avoid validation
		xmlReader.setFeature("http://xml.org/sax/features/validation", false);
		// DTD
		xmlReader.setFeature("http://xml.org/sax/features/resolve-dtd-uris",
				false);
		// *skip* resolving entities like DTDs
		xmlReader.setEntityResolver(new NoEntityResolver());

		return xmlReader;
	}

	protected XMLReader updateXMLReaderToFragment(XMLReader xmlReader)
			throws IOException {
		// skip root element when passing infoset to EXI encoder
		return new SkipRootElementXMLReader(xmlReader);
	}

	protected InputStream updateInputStreamToFragment(InputStream xmlInput)
			throws IOException {
		// surround fragment section with *root* element
		// (necessary for xml reader to avoid messages like "root element must
		// be well-formed")
		return FragmentUtilities.getSurroundingRootInputStream(xmlInput);
	}

	@Override
	public void encodeTo(InputStream xmlInput, OutputStream exiOutput)
			throws Exception {
		// XML reader
		XMLReader xmlReader = getXMLReader();

		exiResult.setOutputStream(exiOutput);

		// set EXI as content & lexical handler
		// EXIResult saxResult = new EXIResult(exiOutput, ef);
		xmlReader.setContentHandler(exiResult.getHandler());

		// set LexicalHandler
		xmlReader.setProperty("http://xml.org/sax/properties/lexical-handler",
				exiResult.getLexicalHandler());
		// set DeclHandler
		xmlReader.setProperty(
				"http://xml.org/sax/properties/declaration-handler",
				exiResult.getLexicalHandler());
		// set DTD handler
		xmlReader.setDTDHandler((DTDHandler) exiResult.getHandler());

		if (isFragment) {
			xmlInput = updateInputStreamToFragment(xmlInput);
			xmlReader = updateXMLReaderToFragment(xmlReader);
		}

		xmlReader.parse(new InputSource(xmlInput));
	}

	public static void main(String[] args) throws Exception {
		// int cp = 0x2000B;
		// StringBuffer sb = new StringBuffer();
		// sb.appendCodePoint(cp);
		// String s = sb.toString();
		// System.out.println(s + ": " + s.length());
		// char[] c = s.toCharArray();

		// EXI output stream
		OutputStream encodedOutput = getOutputStream(QuickTestConfiguration
				.getExiLocation());

		// XML input stream
		InputStream xmlInput = new BufferedInputStream(new FileInputStream(
				QuickTestConfiguration.getXmlLocation()));

		// create test-encoder & encode to EXI
		TestSAXEncoder testEncoder = new TestSAXEncoder(
				TestSAXEncoder.getQuickTestEXIactory());
		// EXIFactory ef = testEncoder.getQuickTestEXIactory(); // get factory

		// // setup encoding options
		// setupEncodingOptions(ef);

		// testEncoder.setupEXIWriter(ef);
		testEncoder.encodeTo(xmlInput, encodedOutput);

		encodedOutput.flush();

		System.out.println("[ENC-SAX] "
				+ QuickTestConfiguration.getXmlLocation() + " --> "
				+ QuickTestConfiguration.getExiLocation());
	}
}
