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

package com.siemens.ct.exi.data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Vector;

import org.custommonkey.xmlunit.XMLTestCase;
import org.xml.sax.InputSource;

import com.siemens.ct.exi.AbstractTestEncoder;
import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.GrammarFactory;
import com.siemens.ct.exi.QuickTestConfiguration;
import com.siemens.ct.exi.TestDOMEncoder;
import com.siemens.ct.exi.TestSAXDecoder;
import com.siemens.ct.exi.TestSAXEncoder;
import com.siemens.ct.exi.grammar.Grammar;
import com.siemens.ct.exi.helpers.DefaultEXIFactory;
import com.siemens.ct.exi.util.FragmentUtilities;

enum API {
	SAX, DOM;
}

public abstract class AbstractTestCase extends XMLTestCase {
	protected Vector<TestCaseOption> testCaseOptions = new Vector<TestCaseOption>();
	protected GrammarFactory grammarFactory = GrammarFactory.newInstance();

	public AbstractTestCase(String s) {
		super(s);
	}

	private void _testOption(TestCaseOption tco, API api) throws Exception {
		if (tco.isSchemaInformedOnly() && tco.getSchemaLocation() == null) {
			return;
		}

		// exi factory
		EXIFactory ef = DefaultEXIFactory.newInstance();
		ef.setCodingMode(tco.getCodingMode());
		ef.setFidelityOptions(tco.getFidelityOptions());
		ef.setFragment(tco.isFragments());
		ef.setDatatypeRepresentationMap(tco.getDatatypeRepresentations());
		
		// schema-informed grammar ?
		if (tco.getSchemaLocation() != null) {
			Grammar grammar = grammarFactory.createGrammar(tco
					.getSchemaLocation());
			ef.setGrammar(grammar);
		}

		// EXI output stream
		ByteArrayOutputStream encodedOutput = new ByteArrayOutputStream();

		AbstractTestEncoder testEncoder;
		if (api == API.SAX) {
			testEncoder = new TestSAXEncoder(encodedOutput);
		} else {
			testEncoder = new TestDOMEncoder(encodedOutput);
		}

		// XML input stream
		InputStream xmlInput = new FileInputStream(QuickTestConfiguration
				.getXmlLocation());


		// -> encode
		testEncoder.encodeTo(ef, xmlInput);
		encodedOutput.flush();

		// EXI input stream
		ByteArrayInputStream exiDocument = new ByteArrayInputStream(
				encodedOutput.toByteArray());

		// decoded XML
		ByteArrayOutputStream xmlOutput = new ByteArrayOutputStream();

		// <-- decode
		TestSAXDecoder testDecoder = new TestSAXDecoder();
		testDecoder.decodeTo(ef, exiDocument, xmlOutput);
		xmlOutput.flush();

		// check XML validity
		if (tco.isXmlEqual()) {
			InputStream control = new FileInputStream(QuickTestConfiguration
					.getXmlLocation());
			InputStream test = new ByteArrayInputStream(xmlOutput.toByteArray());
			// InputStream test = new FileInputStream( tmpXML );

			if (ef.isFragment()) {
				// surround with root element for equality check
				control = FragmentUtilities
						.getSurroundingRootInputStream(control);
				test = FragmentUtilities.getSurroundingRootInputStream(test);
			}

			assertXMLEqual(new InputSource(control), new InputSource(test));
		} else {
			// TODO DOCTYPE ?
			// assertXMLValid ( decodedXML );
		}
	}

	protected void _test() throws Exception {
		// schema-less
		_test(null);

		// schema-informed
		_test(QuickTestConfiguration.getXsdLocation());
	}

	private void _test(String schemaLocation) throws Exception {
		// test options
		for (int i = 0; i < testCaseOptions.size(); i++) {
			TestCaseOption tco = testCaseOptions.get(i);
			// update schema
			tco.setSchemaLocation(schemaLocation);
			try {
				//	test both APIs
				
				//	1. SAX
				_testOption(tco, API.SAX);
				//	2. DOM
				_testOption(tco, API.DOM);
				
			} catch (Exception e) {
				throw new Exception(e.getLocalizedMessage() + " ["
						+ tco.toString() + "]", e);
			}
		}
	}

}
