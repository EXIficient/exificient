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

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;

import org.xml.sax.InputSource;

public class TestDecoder extends AbstractTestCoder {
	protected TransformerFactory tf;

	public TestDecoder() {
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

		SAXSource exiSource = new SAXSource(new InputSource(exiDocument));
		exiSource.setXMLReader(ef.createEXIReader());

		transformer.transform(exiSource, new StreamResult(xmlOutput));
	}

	public static void main(String[] args) throws Exception {
		// create test-decoder
		TestDecoder testDecoder = new TestDecoder();

		// get factory
		EXIFactory ef = testDecoder.getQuickTestEXIactory();

		// exi document
		InputStream exiDocument = new FileInputStream(QuickTestConfiguration
				.getExiLocation());

		// decoded xml output
		String decodedXMLLocation = QuickTestConfiguration.getExiLocation()
				+ ".xml";
		OutputStream xmlOutput = new FileOutputStream(decodedXMLLocation);

		// decode EXI to XML
		testDecoder.decodeTo(ef, exiDocument, xmlOutput);

		System.out.println("[DEC] " + QuickTestConfiguration.getExiLocation()
				+ " --> " + decodedXMLLocation);
	}

}
