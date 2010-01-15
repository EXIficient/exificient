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
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;

import org.custommonkey.xmlunit.XMLTestCase;
import org.custommonkey.xmlunit.XMLUnit;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.siemens.ct.exi.AbstractTestDecoder;
import com.siemens.ct.exi.AbstractTestEncoder;
import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.GrammarFactory;
import com.siemens.ct.exi.QuickTestConfiguration;
import com.siemens.ct.exi.TestDOMDecoder;
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
		ef.setSelfContainedElements(tco.getSelfContainedElements());

		// schema-informed grammar ?
		if (tco.getSchemaLocation() != null) {
			Grammar grammar = grammarFactory.createGrammar(tco
					.getSchemaLocation());
			ef.setGrammar(grammar);
		}

		// EXI output stream
		ByteArrayOutputStream exiEncodedOutput = new ByteArrayOutputStream();

		// XML input stream
		InputStream xmlInput = new FileInputStream(QuickTestConfiguration
				.getXmlLocation());

		AbstractTestEncoder testEncoder = getTestEncoder(api, exiEncodedOutput);

		// --> encode
		testEncoder.encodeTo(ef, xmlInput);
		exiEncodedOutput.flush();

		// EXI input stream
		ByteArrayInputStream exiDocument = new ByteArrayInputStream(
				exiEncodedOutput.toByteArray());

		// <-- decode as SAX
		decode(ef, exiDocument, API.SAX, tco.isXmlEqual());

		// <-- decode as DOM
		exiDocument.reset();
		decode(ef, exiDocument, API.DOM, tco.isXmlEqual());
	}

	protected void decode(EXIFactory ef, InputStream exiDocument, API api,
			boolean checkValidity) throws Exception {
		try {
			// decoded XML
			ByteArrayOutputStream xmlOutput = new ByteArrayOutputStream();

			// decode
			AbstractTestDecoder testDecoder = getTestDecoder(API.SAX);
			testDecoder.decodeTo(ef, exiDocument, xmlOutput);
			xmlOutput.flush();

			// check XML validity
			if (checkValidity) {
				InputStream control = new FileInputStream(
						QuickTestConfiguration.getXmlLocation());
				InputStream test = new ByteArrayInputStream(xmlOutput
						.toByteArray());

				checkXMLValidity(ef, control, test);

			}
		} catch (Exception e) {
			throw new Exception("Decode " + api, e);
		}
	}

	protected AbstractTestEncoder getTestEncoder(API api,
			OutputStream encodedOutput) {
		if (api == API.SAX) {
			return new TestSAXEncoder(encodedOutput);
		} else {
			return new TestDOMEncoder(encodedOutput);
		}
	}

	protected AbstractTestDecoder getTestDecoder(API api) {
		if (api == API.SAX) {
			return new TestSAXDecoder();
		} else {
			return new TestDOMDecoder();
		}
	}

	protected void checkXMLValidity(EXIFactory ef, InputStream control,
			InputStream test) throws IOException, ParserConfigurationException,
			SAXException {
		if (ef.isFragment()) {
			// surround with root element for equality check
			control = FragmentUtilities.getSurroundingRootInputStream(control);
			test = FragmentUtilities.getSurroundingRootInputStream(test);
		}

		XMLUnit.setIgnoreWhitespace(true);
		XMLUnit.setIgnoreAttributeOrder(true);
		XMLUnit.setIgnoreDiffBetweenTextAndCDATA(true);

		Document docControl = TestDOMEncoder.getDocument(control);
		Document docTest = TestDOMEncoder.getDocument(test);
		assertXMLEqual(ef.getCodingMode() + ", schema=" + ef.getGrammar().isSchemaInformed()+ " " + ef.getFidelityOptions().toString(), docControl, docTest);
		// assertXMLEqual(new InputSource(control), new InputSource(test));
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
				// test both APIs

				// 1. SAX
				_testOption(tco, API.SAX);
				
				// 2. DOM
				_testOption(tco, API.DOM);

			} catch (Exception e) {
				throw new Exception(e.getLocalizedMessage() + " ["
						+ tco.toString() + "]", e);
			}
		}
	}

}
