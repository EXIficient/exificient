/*
 * Copyright (c) 2007-2015 Siemens AG
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
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.transform.TransformerConfigurationException;

import com.siemens.ct.exi.exceptions.EXIException;

public class TestSAXDecoderN extends TestSAXDecoder {

	public TestSAXDecoderN(EXIFactory ef)
			throws TransformerConfigurationException, EXIException {
		super(ef);
	}

	protected void test(String exiLocation, String decodedXMLLocation)
			throws Exception {

		// // create test-decoder
		// TestSAXDecoderN testDecoderN = new TestSAXDecoderN(ef);

		// ef.setEXIBodyEncoder("com.siemens.ct.exi.gen.EXIBodyEncoderGen");
		// ef.setEXIBodyDecoder("com.siemens.ct.exi.gen.EXIBodyDecoderGen");

		// exi document
		InputStream exiDocument = new BufferedInputStream(new FileInputStream(
				exiLocation));

		// decoded xml output
		OutputStream xmlOutput = new FileOutputStream(decodedXMLLocation);

		long startTime = System.currentTimeMillis();

		// TransformerFactory tf = TransformerFactory.newInstance();
		// Transformer transformer = tf.newTransformer();
		// transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,
		// "yes");

		// decode EXI to XML
		// testDecoderN.setupEXIReader(ef);
		for (int i = 0; i < TestSAXEncoderN.N_RUNS; i++) {
			decodeTo(exiDocument, xmlOutput); // , transformer);
		}

		System.out.println("[DEC-SAX] "
				+ QuickTestConfiguration.getExiLocation() + " --> "
				+ decodedXMLLocation);

		long duration = System.currentTimeMillis() - startTime;
		System.out.println("Runtime: " + duration + " msecs for "
				+ TestSAXEncoderN.N_RUNS + " runs.");
	}

	public static void main(String[] args) throws Exception {
		String exiLocation = QuickTestConfiguration.getExiLocation() + "_"
				+ TestSAXEncoderN.N_RUNS;
		String decodedXMLLocation = exiLocation + ".xml";

		// create test-decoder
		TestSAXDecoderN testDecoderN = new TestSAXDecoderN(
				TestSAXDecoderN.getQuickTestEXIactory());

		// get factory
		// EXIFactory ef = testDecoderN.getQuickTestEXIactory();

		testDecoderN.test(exiLocation, decodedXMLLocation);
	}

}
