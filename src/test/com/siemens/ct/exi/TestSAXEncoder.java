/*
 * Copyright (C) 2007-2009 Siemens AG
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

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.transform.sax.SAXResult;

import org.xml.sax.InputSource;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.siemens.ct.exi.api.sax.EXIResult;
import com.siemens.ct.exi.util.FragmentUtilities;
import com.siemens.ct.exi.util.NoEntityResolver;
import com.siemens.ct.exi.util.SkipRootElementXMLReader;

public class TestSAXEncoder extends AbstractTestEncoder {
	
	protected OutputStream exiOutput;
	
	public TestSAXEncoder(OutputStream exiOutput) {
		super();
		this.exiOutput = exiOutput;
	}

	protected XMLReader getXMLReader() throws Exception {
		// create xml reader
		XMLReader xmlReader;

		if (true) {
			// SAXParserFactory spf = SAXParserFactory.newInstance ( );
			// // spf.setNamespaceAware ( true );
			// xmlReader = spf.newSAXParser ( ).getXMLReader ( );

			xmlReader = XMLReaderFactory
					.createXMLReader("org.apache.xerces.parsers.SAXParser");
			xmlReader
					.setFeature("http://xml.org/sax/features/namespaces", true);
			xmlReader.setFeature(
					"http://xml.org/sax/features/namespace-prefixes", false);
		} else {
			xmlReader = XMLReaderFactory.createXMLReader();
		}

		// *skip* resolving entities like DTDs
		xmlReader.setEntityResolver(new NoEntityResolver());

		// xmlReader.setFeature ( "http://xml.org/sax/features/namespaces",
		// true);
		// xmlReader.setFeature (
		// "http://xml.org/sax/features/namespace-prefixes", false );

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

	public void encodeTo(EXIFactory ef, InputStream xmlInput) throws Exception {
		// XML reader
		XMLReader xmlReader = getXMLReader();

		// set EXI as content & lexical handler
		SAXResult saxResult = new EXIResult(exiOutput, ef);
		xmlReader.setContentHandler(saxResult.getHandler());

		try {
			// set LexicalHandler
			xmlReader.setProperty(
					"http://xml.org/sax/properties/lexical-handler", saxResult
							.getLexicalHandler());
		} catch (SAXNotRecognizedException e) {
		}

		if (ef.isFragment()) {
			xmlInput = updateInputStreamToFragment(xmlInput);
			xmlReader = updateXMLReaderToFragment(xmlReader);
		}

		xmlReader.parse(new InputSource(xmlInput));
	}


	public static void main(String[] args) throws Exception {

		// EXI output stream
		OutputStream encodedOutput = getOutputStream(QuickTestConfiguration
				.getExiLocation());

		// XML input stream
		InputStream xmlInput = new BufferedInputStream(new FileInputStream(
				QuickTestConfiguration.getXmlLocation()));
		
		// create test-encoder & encode to EXI
		TestSAXEncoder testEncoder = new TestSAXEncoder(encodedOutput);
		EXIFactory ef = TestSAXEncoder.getQuickTestEXIactory(); // get factory
		testEncoder.encodeTo(ef, xmlInput);

		encodedOutput.flush();

		System.out.println("[ENC-SAX] " + QuickTestConfiguration.getXmlLocation()
				+ " --> " + QuickTestConfiguration.getExiLocation());
	}
}

