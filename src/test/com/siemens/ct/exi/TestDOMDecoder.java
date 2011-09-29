/*
 * Copyright (C) 2007-2011 Siemens AG
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
import java.io.StringWriter;
import java.io.Writer;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Node;

import com.siemens.ct.exi.api.dom.DOMBuilder;

public class TestDOMDecoder extends AbstractTestDecoder {
	protected TransformerFactory tf;
	protected boolean isFragment;
	protected DOMBuilder domBuilder;

	public TestDOMDecoder(EXIFactory ef) throws ParserConfigurationException {
		super();

		tf = TransformerFactory.newInstance();
		
		domBuilder = new DOMBuilder(ef);
		isFragment = ef.isFragment();
	}


//	@Override
//	public void setupEXIReader(EXIFactory ef) throws Exception {
//		domBuilder = new DOMBuilder(ef);
//		isFragment = ef.isFragment();
//	}

	
	public static void nodeToWriter(Node n, Writer writer)
			throws TransformerException {
		// set up a transformer
		TransformerFactory transfac = TransformerFactory.newInstance();
		Transformer trans = transfac.newTransformer();
		// output options
		trans.setOutputProperty(OutputKeys.METHOD, "xml");
		// due to fragments
		trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		// remaining keys
		trans.setOutputProperty(OutputKeys.ENCODING, "iso-8859-1"); // "ASCII" "UTF-8"
		trans.setOutputProperty(OutputKeys.INDENT, "yes");

		// // TEST DOCTYPE
		// if ( n.getNodeType() == Node.DOCUMENT_NODE ) {
		// Document doc = (Document)n;
		// DocumentType dt = doc.getDoctype();
		//			
		// String publicID = dt.getPublicId();
		// if ( publicID != null && publicID.length() > 0 ) {
		// trans.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, publicID);
		// }
		// String systemID = dt.getSystemId();
		// if (systemID != null && systemID.length() > 0) {
		// trans.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, systemID);
		// }
		// }

		// create string from xml tree
		StreamResult result = new StreamResult(writer);
		DOMSource source = new DOMSource(n);
		trans.transform(source, result);
	}
	


	@Override
	public void decodeTo(InputStream exiDocument,
			OutputStream xmlOutput) throws Exception {
		// decode to DOM
		Node doc;
		if (isFragment) {
			doc = domBuilder.parseFragment(exiDocument);
		} else {
			doc = domBuilder.parse(exiDocument);
		}
		// create string from xml tree
		StringWriter sw = new StringWriter();
		nodeToWriter(doc, sw);
		String xmlString = sw.toString();

		// System.out.println(xmlString);
		xmlOutput.write(xmlString.getBytes());
	}

	public static void main(String[] args) throws Exception {
		// create test-decoder
		TestDOMDecoder testDecoder = new TestDOMDecoder(TestDOMDecoder.getQuickTestEXIactory());

		// get factory
//		EXIFactory ef = testDecoder.getQuickTestEXIactory();

		// exi document
		InputStream exiDocument = new FileInputStream(QuickTestConfiguration
				.getExiLocation());

		// decoded xml output
		String decodedXMLLocation = QuickTestConfiguration.getExiLocation()
				+ ".xml";
		OutputStream xmlOutput = new FileOutputStream(decodedXMLLocation);

		// decode EXI to XML
//		testDecoder.setupEXIReader(ef);
		testDecoder.decodeTo(exiDocument, xmlOutput);

		System.out.println("[DEC_DOM] "
				+ QuickTestConfiguration.getExiLocation() + " --> "
				+ decodedXMLLocation);
	}



}
