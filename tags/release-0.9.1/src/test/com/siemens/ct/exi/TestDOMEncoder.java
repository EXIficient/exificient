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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.siemens.ct.exi.api.dom.DOMWriter;
import com.siemens.ct.exi.api.dom.DocumentFragmentBuilder;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.util.NoEntityResolver;

public class TestDOMEncoder extends AbstractTestEncoder {

	protected DOMWriter enc;
	protected boolean isFragment;

	public TestDOMEncoder(EXIFactory ef) throws EXIException {
		super();
		// dom encoder
		enc = new DOMWriter(ef);
		isFragment = ef.isFragment();
	}


//	@Override
//	public void setupEXIWriter(EXIFactory ef) throws EXIException {
//		// dom encoder
//		enc = new DOMWriter(ef);
//		isFragment = ef.isFragment();
//	}

	
	public static Document getDocument(InputStream is)
			throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();
		dfactory.setNamespaceAware(true);

		DocumentBuilder documentBuilder = dfactory.newDocumentBuilder();

		// *skip* resolving entities like DTDs
		documentBuilder.setEntityResolver(new NoEntityResolver());

		documentBuilder.setErrorHandler(null);
		Document doc = documentBuilder.parse(is);

		return doc;
	}

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
		TestDOMEncoder testEncoder = new TestDOMEncoder(TestDOMEncoder.getQuickTestEXIactory());

//		// get factory
//		EXIFactory exiFactory = testEncoder.getQuickTestEXIactory();
//		testEncoder.setupEXIWriter(exiFactory);

		// EXI input stream
		InputStream xmlInput = new FileInputStream(
				QuickTestConfiguration.XML_FILE_LOCATION);
		

		// EXI output stream
		OutputStream encodedOutput = getOutputStream(QuickTestConfiguration
				.getExiLocation());
		
//		// setup encoding options
//		setupEncodingOptions(exiFactory);

		// generate EXI
//		testEncoder.setupEXIWriter(exiFactory);
		testEncoder.encodeTo(xmlInput, encodedOutput);
		encodedOutput.close();

		System.out.println("[ENC-DOM] "
				+ QuickTestConfiguration.getXmlLocation() + " --> "
				+ QuickTestConfiguration.getExiLocation());
	}

}
