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

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;

import com.siemens.ct.exi.api.stream.StAXEncoder;
import com.siemens.ct.exi.util.SkipRootElementXMLEventReader;

public class TestStAXEncoder extends AbstractTestEncoder {

	protected OutputStream exiOutput;

	public TestStAXEncoder(OutputStream exiOutput) {
		super();
		this.exiOutput = exiOutput;
	}

	public void encodeTo(EXIFactory ef, InputStream xmlInput) throws Exception {		
		XMLInputFactory xmlFactory = XMLInputFactory.newInstance(); 
		
		// do not resolve DTDs
		xmlFactory.setProperty(XMLInputFactory.SUPPORT_DTD, Boolean.FALSE);
		// requires the parser to replace internal entity references with their replacement text and report them as characters
		xmlFactory.setProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES, Boolean.FALSE);

		if (ef.isFragment()) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int b;
			baos.write("<root>".getBytes());
			while( ( b = xmlInput.read()) != -1) {
				baos.write(b);
			}
			baos.write("</root>".getBytes());
			ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
			xmlInput = bais;
			// System.err.println("StAX, Fragments not supported yet");
		}
		
		// XMLStreamReader xmlReader = xmlFactory.createXMLStreamReader(xmlInput); 
		XMLEventReader xmlReader = xmlFactory.createXMLEventReader(xmlInput); 
		
		if (ef.isFragment()) {
			xmlReader = new SkipRootElementXMLEventReader(xmlReader);
		}
		
		StAXEncoder exiWriter = new StAXEncoder(ef, exiOutput);
		
		exiWriter.encode(xmlReader);
	}

	public static void main(String[] args) throws Exception {

		// EXI output stream
		OutputStream encodedOutput = getOutputStream(QuickTestConfiguration
				.getExiLocation());

		// XML input stream
		InputStream xmlInput = new BufferedInputStream(new FileInputStream(
				QuickTestConfiguration.getXmlLocation()));

		// create test-encoder & encode to EXI
		TestStAXEncoder testEncoder = new TestStAXEncoder(encodedOutput);
		EXIFactory ef = testEncoder.getQuickTestEXIactory(); // get factory		
		// setup encoding options
		setupEncodingOptions(ef);

		testEncoder.encodeTo(ef, xmlInput);

		encodedOutput.flush();

		System.out.println("[ENC-StAX] "
				+ QuickTestConfiguration.getXmlLocation() + " --> "
				+ QuickTestConfiguration.getExiLocation());
	}
}
