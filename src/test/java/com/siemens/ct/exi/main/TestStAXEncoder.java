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

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import com.siemens.ct.exi.core.EXIFactory;
import com.siemens.ct.exi.core.exceptions.EXIException;
import com.siemens.ct.exi.main.api.stream.StAXEncoder;
import com.siemens.ct.exi.main.util.SkipRootElementXMLEventReader;

public class TestStAXEncoder extends AbstractTestEncoder {

	protected EXIFactory ef;
	protected boolean isFragment;
	protected StAXEncoder exiWriter;
	protected final boolean useXMLStreamReader;

	public TestStAXEncoder(EXIFactory ef) throws EXIException {
		this(ef, false);
	}

	public TestStAXEncoder(EXIFactory ef, boolean useXMLStreamReader)
			throws EXIException {
		super();
		exiWriter = new StAXEncoder(ef);
		this.ef = ef;
		this.isFragment = ef.isFragment();
		this.useXMLStreamReader = useXMLStreamReader;
	}

	// @Override
	// public void setupEXIWriter(EXIFactory ef) throws EXIException {
	// exiWriter = new StAXEncoder(ef);
	// isFragment = ef.isFragment();
	// }

	@Override
	public void encodeTo(InputStream xmlInput, OutputStream exiOutput)
			throws Exception {
		XMLInputFactory xmlFactory = XMLInputFactory.newInstance();

		// do not resolve DTDs
		xmlFactory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES,
				Boolean.FALSE);
		// AND
		// requires the parser to replace internal entity references with their
		// replacement text and report them as characters
		// if(this.ef.getFidelityOptions().isFidelityEnabled(FidelityOptions.FEATURE_DTD))
		// {
		xmlFactory.setProperty(XMLInputFactory.SUPPORT_DTD, Boolean.FALSE);
		xmlFactory.setProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES,
				Boolean.FALSE);
		// } else {
		// xmlFactory.setProperty(XMLInputFactory.SUPPORT_DTD, Boolean.TRUE);
		// xmlFactory.setProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES,
		// Boolean.TRUE);
		// }

		if (isFragment) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int b;
			baos.write("<root>".getBytes());
			while ((b = xmlInput.read()) != -1) {
				baos.write(b);
			}
			baos.write("</root>".getBytes());
			xmlInput = new ByteArrayInputStream(baos.toByteArray());
			// System.err.println("StAX, Fragments not supported yet");
		}

		if (useXMLStreamReader) {
			XMLStreamReader streamReader = xmlFactory
					.createXMLStreamReader(xmlInput);
			// TODO fragment
			exiWriter.setOutputStream(exiOutput);
			exiWriter.encode(streamReader);
		} else {
			XMLEventReader xmlReader = xmlFactory
					.createXMLEventReader(xmlInput);

			if (isFragment) {
				xmlReader = new SkipRootElementXMLEventReader(xmlReader);
			}

			exiWriter.setOutputStream(exiOutput);
			exiWriter.encode(xmlReader);
		}
	}

	public static void main(String[] args) throws Exception {

		// EXI output stream
		OutputStream encodedOutput = getOutputStream(QuickTestConfiguration
				.getExiLocation());

		// XML input stream
		InputStream xmlInput = new BufferedInputStream(new FileInputStream(
				QuickTestConfiguration.getXmlLocation()));

		// create test-encoder & encode to EXI
		TestStAXEncoder testEncoder = new TestStAXEncoder(
				TestStAXEncoder.getQuickTestEXIactory());
		// EXIFactory ef = testEncoder.getQuickTestEXIactory(); // get factory
		// // setup encoding options
		// setupEncodingOptions(ef);

		// testEncoder.setupEXIWriter(ef);
		testEncoder.encodeTo(xmlInput, encodedOutput);

		encodedOutput.flush();

		System.out.println("[ENC-StAX] "
				+ QuickTestConfiguration.getXmlLocation() + " --> "
				+ QuickTestConfiguration.getExiLocation());
	}

}
