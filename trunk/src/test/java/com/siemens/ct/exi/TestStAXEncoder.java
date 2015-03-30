/*
 * Copyright (C) 2007-2015 Siemens AG
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
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import com.siemens.ct.exi.api.stream.StAXEncoder;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.util.SkipRootElementXMLEventReader;

public class TestStAXEncoder extends AbstractTestEncoder {

	protected EXIFactory ef;
	protected boolean isFragment;
	protected StAXEncoder exiWriter;
	protected final boolean useXMLStreamReader;

	public TestStAXEncoder(EXIFactory ef) throws EXIException {
		this(ef, false);
	}
	
	public TestStAXEncoder(EXIFactory ef, boolean useXMLStreamReader) throws EXIException {
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
		xmlFactory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, Boolean.FALSE);
		// AND
		// requires the parser to replace internal entity references with their
		// replacement text and report them as characters
//		if(this.ef.getFidelityOptions().isFidelityEnabled(FidelityOptions.FEATURE_DTD)) {
			xmlFactory.setProperty(XMLInputFactory.SUPPORT_DTD, Boolean.FALSE);
			xmlFactory.setProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES,
					Boolean.FALSE);			
//		} else {
//			xmlFactory.setProperty(XMLInputFactory.SUPPORT_DTD, Boolean.TRUE);
//			xmlFactory.setProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES,
//					Boolean.TRUE);
//		}


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
			XMLStreamReader streamReader = xmlFactory.createXMLStreamReader(xmlInput);
			// TODO fragment
			exiWriter.setOutputStream(exiOutput);
			exiWriter.encode(streamReader);
		} else {
			XMLEventReader xmlReader = xmlFactory.createXMLEventReader(xmlInput);

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
