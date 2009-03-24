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

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

import com.siemens.ct.exi.api.dom.DOMBuilder;

public class TestDOMDecoder extends AbstractTestCoder {
	protected TransformerFactory tf;

	public TestDOMDecoder() {
		super();

		tf = TransformerFactory.newInstance();
	}

	public void decodeTo(EXIFactory ef, InputStream exiDocument,
			OutputStream xmlOutput) throws Exception {
		Transformer transformer = tf.newTransformer();

		if (ef.isFragment()) {
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,
					"yes");
		}
		
		//	decode to DOM
		DOMBuilder domBuilder = new DOMBuilder(ef);
		Document doc = domBuilder.parse(exiDocument);
		
		 //set up a transformer
		 TransformerFactory transfac = TransformerFactory.newInstance();
		 Transformer trans = transfac.newTransformer();
		 trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		 trans.setOutputProperty(OutputKeys.INDENT, "yes");
		
		 //create string from xml tree
		 StringWriter sw = new StringWriter();
		 StreamResult result = new StreamResult(sw);
		 DOMSource source = new DOMSource(doc);
		 trans.transform(source, result);
		 String xmlString = sw.toString();
		 
		 //	
		 // System.out.println(xmlString);
		 xmlOutput.write(xmlString.getBytes());
	}

	public static void main(String[] args) throws Exception {
		// create test-decoder
		TestDOMDecoder testDecoder = new TestDOMDecoder();

		// get factory
		EXIFactory ef = TestDOMDecoder.getQuickTestEXIactory();

		// exi document
		InputStream exiDocument = new FileInputStream(QuickTestConfiguration
				.getExiLocation());

		// decoded xml output
		String decodedXMLLocation = QuickTestConfiguration.getExiLocation()
				+ ".xml";
		OutputStream xmlOutput = new FileOutputStream(decodedXMLLocation);

		// decode EXI to XML
		testDecoder.decodeTo(ef, exiDocument, xmlOutput);

		System.out.println("[DEC_DOM] " + QuickTestConfiguration.getExiLocation()
				+ " --> " + decodedXMLLocation);
	}

}
