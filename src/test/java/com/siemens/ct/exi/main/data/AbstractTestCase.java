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

package com.siemens.ct.exi.main.data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;

import junit.framework.AssertionFailedError;

import org.apache.xerces.xni.XMLResourceIdentifier;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.parser.XMLEntityResolver;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.custommonkey.xmlunit.XMLTestCase;
import org.custommonkey.xmlunit.XMLUnit;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.siemens.ct.exi.core.EXIFactory;
import com.siemens.ct.exi.core.EncodingOptions;
import com.siemens.ct.exi.core.FidelityOptions;
import com.siemens.ct.exi.core.exceptions.EXIException;
import com.siemens.ct.exi.core.grammars.Grammars;
import com.siemens.ct.exi.core.helpers.DefaultEXIFactory;
import com.siemens.ct.exi.grammars.GrammarFactory;
import com.siemens.ct.exi.main.AbstractTestDecoder;
import com.siemens.ct.exi.main.AbstractTestEncoder;
import com.siemens.ct.exi.main.QuickTestConfiguration;
import com.siemens.ct.exi.main.TestDOMDecoder;
import com.siemens.ct.exi.main.TestDOMEncoder;
import com.siemens.ct.exi.main.TestSAXDecoder;
import com.siemens.ct.exi.main.TestSAXEncoder;
import com.siemens.ct.exi.main.TestStAXDecoder;
import com.siemens.ct.exi.main.TestStAXEncoder;
import com.siemens.ct.exi.main.util.FragmentUtilities;

enum API {
	SAX, DOM, StAX;
}

public abstract class AbstractTestCase extends XMLTestCase {

	public final static String ENCODING = "UTF-8"; // "ISO-8859-1";

	protected List<TestCaseOption> testCaseOptions = new ArrayList<TestCaseOption>();
	protected GrammarFactory grammarFactory = GrammarFactory.newInstance();

	public AbstractTestCase(String s) {
		super(s);
	}

	
	protected XMLEntityResolver getXsdEntityResolver() {
		return new TestXSDResolver();
	}
	
	private void _testOption(TestCaseOption tco, API api) throws Exception {
		if (tco.isSchemaInformedOnly() && tco.getSchemaLocation() == null) {
			return;
		}

		// exi factory
		EXIFactory ef = DefaultEXIFactory.newInstance();
		ef.setCodingMode(tco.getCodingMode());
		ef.setFidelityOptions(tco.getFidelityOptions());
		if(tco.getSharedStrings() != null) {
			ef.setSharedStrings(tco.getSharedStrings());
		}
		if(tco.isUsingNonEvolvingGrammars()) {
			ef.setUsingNonEvolvingGrammars(tco.isUsingNonEvolvingGrammars());
		}
		ef.setFragment(tco.isFragments());
		ef.setDatatypeRepresentationMap(tco.getDtrMapTypes(),
				tco.getDtrMapRepresentations());
		ef.setSelfContainedElements(tco.getSelfContainedElements());
		if (tco.getBlockSize() >= 0) {
			ef.setBlockSize(tco.getBlockSize());
		}
		if (tco.getValueMaxLength() >= 0) {
			ef.setValueMaxLength(tco.getValueMaxLength());
		}
		if (tco.getValuePartitionCapacity() >= 0) {
			ef.setValuePartitionCapacity(tco.getValuePartitionCapacity());
		}
		ef.setEncodingOptions(tco.getEncodingOptions());

		ef.setLocalValuePartitions(tco.isLocalValuePartitions());
		ef.setMaximumNumberOfBuiltInProductions(tco
				.getMaximumNumberOfBuiltInProductions());
		ef.setMaximumNumberOfBuiltInElementGrammars(tco
				.getMaximumNumberOfEvolvingBuiltInElementGrammars());

		// if (tco.getProfile() != null) {
		// ef.setProfile(tco.getProfile());
		// }

		// schema-informed grammar ?
		if (tco.getSchemaLocation() == null) {
			// schema-less
		} else if (tco.getSchemaLocation().length() == 0) {
			// xsd-types informed
			Grammars grammar = grammarFactory.createXSDTypesOnlyGrammars();
			ef.setGrammars(grammar);
		} else {
			// schema-informed
			// no internet connection, try offline
			XMLEntityResolver entityResolver = getXsdEntityResolver(); 
			Grammars grammar = grammarFactory.createGrammars(
					tco.getSchemaLocation(), entityResolver);
			ef.setGrammars(grammar);
		}

		// EXI output stream
		ByteArrayOutputStream exiEncodedOutput = new ByteArrayOutputStream();

		// XML input stream
		String xmlLocation = QuickTestConfiguration.getXmlLocation();
		InputStream xmlInput = new FileInputStream(xmlLocation);

		AbstractTestEncoder testEncoder = getTestEncoder(api, ef);

		// --> encode
		testEncoder.encodeTo(xmlInput, exiEncodedOutput);
		exiEncodedOutput.flush();

		// EXI input stream
		InputStream exiDocument = new ByteArrayInputStream(
				exiEncodedOutput.toByteArray());

		EncodingOptions encodingOptions = tco.getEncodingOptions();
		// if (tco.isIncludeOptions() && tco.isIncludeSchemaId()) {
		if (encodingOptions
				.isOptionEnabled(EncodingOptions.INCLUDE_OPTIONS)
				&& encodingOptions
						.isOptionEnabled(EncodingOptions.INCLUDE_SCHEMA_ID)) {
			// all EXI options and schemaID from the header have to be used
			ef = DefaultEXIFactory.newInstance();
		} else if (encodingOptions
				.isOptionEnabled(EncodingOptions.INCLUDE_OPTIONS)) {
			// restore schemaId// grammar
			Grammars  grs = ef.getGrammars();
			ef = DefaultEXIFactory.newInstance();
			ef.setGrammars(grs);
		}

		// <-- 1. decode as SAX
		try {
			decode(ef, exiDocument, API.SAX, tco.isXmlEqual());
		} catch (Throwable e) {
			// encode-decode msg
			throw new Exception(
					"{" + api + "->SAX} " + e.getLocalizedMessage() + " ["
							+ tco.toString() + "]", e);
		}

		// <-- 2. decode as DOM
		try {
			exiDocument.reset();
			decode(ef, exiDocument, API.DOM, tco.isXmlEqual());
		} catch (Throwable e) {
			throw new Exception(
					"{" + api + "->DOM} " + e.getLocalizedMessage() + " ["
							+ tco.toString() + "]", e);
		}

		// <-- 3. decode as StAX
		try {
			exiDocument.reset();
			decode(ef, exiDocument, API.StAX, tco.isXmlEqual());
		} catch (Throwable e) {
			throw new Exception(
					"{" + api + "->StAX} " + e.getLocalizedMessage() + " ["
							+ tco.toString() + "]", e);
		}
		
	}

	class TestXSDResolver implements
			org.apache.xerces.xni.parser.XMLEntityResolver {

		public TestXSDResolver() {
		}

		public XMLInputSource resolveEntity(
				XMLResourceIdentifier resourceIdentifier) throws XNIException,
				IOException {
			// String publicId = resourceIdentifier.getPublicId();
			// String baseSystemId = resourceIdentifier.getBaseSystemId();
			// String expandedSystemId =
			// resourceIdentifier.getExpandedSystemId();
			String literalSystemId = resourceIdentifier.getLiteralSystemId(); 
			
			// System.out.println(literalSystemId);
			
			if("XMLSchema.dtd".equals(literalSystemId)  || "datatypes.dtd".equals(literalSystemId)) {
				InputStream isTypes = new FileInputStream("./data/W3C/xsd/" + literalSystemId);

				String publicId = null;
				String systemId = null;
				String baseSystemId = null;
				String encoding = null;
				XMLInputSource xsdSourceTypes = new XMLInputSource(publicId,
				systemId, baseSystemId, isTypes, encoding);
				return xsdSourceTypes;
			} else if("http://www.w3.org/2001/xml.xsd".equals(literalSystemId)) {
				InputStream isTypes = new FileInputStream("./data/W3C/xsd/" + "xml.xsd");

				String publicId = null;
				String systemId = null;
				String baseSystemId = null;
				String encoding = null;
				XMLInputSource xsdSourceTypes = new XMLInputSource(publicId,
				systemId, baseSystemId, isTypes, encoding);
				return xsdSourceTypes;
			} else {
				// Note: if the entity cannot be resolved, this method
				// should return null.
				return null;
			}

		}
	}

	protected void decode(EXIFactory ef, InputStream exiDocument, API api,
			boolean checkXMLEqual) throws Exception {
		// decoded XML
		ByteArrayOutputStream xmlOutput = new ByteArrayOutputStream();

		// decode
		AbstractTestDecoder testDecoder = getTestDecoder(api, ef);
		// AbstractTestDecoder testDecoder = getTestDecoder(API.SAX);
		testDecoder.decodeTo(exiDocument, xmlOutput);
		xmlOutput.flush();

		// check XML validity OR equal
		InputStream testDecXML = new ByteArrayInputStream(
				xmlOutput.toByteArray());

		// Problem
		// com.sun.org.apache.xerces.internal.impl.io.MalformedByteSequenceException:
		// Invalid byte 1 of 1-byte UTF-8 sequence.
		// Solution
		// http://www.mkyong.com/java/sax-error-malformedbytesequenceexception-invalid-byte-1-of-1-byte-utf-8-sequence/
		// Reader reader = new InputStreamReader(testDecXML,"UTF-8");
		// InputSource is = new InputSource(reader);
		// is.setEncoding("UTF-8");

		List<String> domDiffIssues = new ArrayList<String>();
		// entity references
		domDiffIssues.add("./data/general/entityReference1.xml");
		domDiffIssues.add("./data/general/entityReference2.xml");
		// fragments
		domDiffIssues.add("./data/fragment/fragment3a.xml.frag");
		domDiffIssues.add("./data/fragment/fragment3b.xml.frag");
		// ???
		domDiffIssues.add("./data/W3C/xhtml/www.w3.org.htm");
		domDiffIssues.add("./data/W3C/xhtml/en.wikipedia.org-wiki-EXI.htm");

		String xmlLocation = QuickTestConfiguration.getXmlLocation();

		if ((api == API.DOM || api == API.StAX)
				&& domDiffIssues.contains(xmlLocation)) {
			// TODO find a solution for known DOM diff tool issues
			// System.out.println("No DOM diff for: " + xmlLocation);
		} else if (checkXMLEqual) {
			InputStream control = new FileInputStream(xmlLocation);
			checkXMLEquality(ef, control, testDecXML);
		} else {
			checkXMLValidity(ef, testDecXML);
		}
	}

	protected AbstractTestEncoder getTestEncoder(API api, EXIFactory ef)
			throws EXIException {
		if (api == API.SAX) {
			return new TestSAXEncoder(ef);
		} else if (api == API.DOM) {
			return new TestDOMEncoder(ef);
		} else {
			assert (api == API.StAX);
			return new TestStAXEncoder(ef);
		}
	}

	protected AbstractTestDecoder getTestDecoder(API api, EXIFactory ef)
			throws TransformerConfigurationException, EXIException,
			ParserConfigurationException {
		if (api == API.SAX) {
			return new TestSAXDecoder(ef);
		} else if (api == API.DOM) {
			return new TestDOMDecoder(ef);
		} else {
			assert (api == API.StAX);
			return new TestStAXDecoder(ef);
		}
	}

	protected void checkXMLValidity(EXIFactory ef, InputStream testXML)
			throws Exception {
		if (ef.isFragment()) {
			// surround with root element for equality check
			testXML = FragmentUtilities.getSurroundingRootInputStream(testXML);
		}

		// try to read stream and create DOM
		try {
			// @SuppressWarnings("unused")
			Document docTest = TestDOMEncoder.getDocument(testXML);
			assertTrue(docTest != null);
		} catch (Exception e) {
			String msg = e.getMessage();
			if (msg.contains("The entity \"ent\" was referenced, but not declared")) {
				// known issue? --> entityReference2 for StAX
				return;
			}
			throw new Exception("Not able to create DOM. " + ef.getCodingMode()
					+ ", schema=" + ef.getGrammars().isSchemaInformed() + " "
					+ ef.getFidelityOptions().toString(), e);
		}
		// assertXMLValid(new InputSource(test));
	}

	protected void checkXMLEquality(EXIFactory ef, InputStream control,
			InputStream testXML) throws IOException, AssertionFailedError,
			ParserConfigurationException, SAXException {
		if (ef.isFragment()) {
			// surround with root element for equality check
			control = FragmentUtilities.getSurroundingRootInputStream(control);
			testXML = FragmentUtilities.getSurroundingRootInputStream(testXML);
		}

		XMLUnit.setIgnoreWhitespace(true);
		XMLUnit.setIgnoreAttributeOrder(true);
		XMLUnit.setIgnoreDiffBetweenTextAndCDATA(true);

		Document docControl = TestDOMEncoder.getDocument(control); // ,
																	// "ISO-8859-1"
		Document docTest = null;
		try {
			docTest = TestDOMEncoder.getDocument(testXML, ENCODING);
		} catch (SAXParseException e1) {
			String msg = e1.getMessage();
			if (msg.contains("The entity \"ent\" was referenced, but not declared")) {
				// known issue? --> entityReference2 for StAX
				return;
			}
			throw e1;
		}

		try {
			assertXMLEqual(ef.getCodingMode() + ", schema="
					+ ef.getGrammars().isSchemaInformed() + " "
					+ ef.getFidelityOptions().toString(), docControl, docTest);
		} catch (AssertionFailedError e) {
			// XMLUnit seems to have problems with XHTML and DTD throwing wrong
			// assertion failure
			String msg = e.getMessage();
			// System.out.println(msg);
			if (msg.contains("Expected doctype name 'html'")) {
				// do nothing, false failure
			} else if (msg
					.contains("Expected number of child nodes '3' but was '2' - comparing <greeting...> at /greeting[1] to <greeting...> at /greeting[1]")) {
				// ER issue, see testEntityReference1
			} else {
				throw new AssertionFailedError(msg);
			}
		}

		// assertXMLEqual(new InputSource(control), new InputSource(test));
	}

	protected void _test(FidelityOptions noValidOptions) throws Exception {
		// schema-less
		_test(null, noValidOptions);

		// schema-informed
		_test(QuickTestConfiguration.getXsdLocation(), noValidOptions);

		// // schema-informed (XSD-types only)
		// _test("", noValidOptions);
	}

	protected void _test() throws Exception {
		_test(null);
	}

	private void _test(String schemaLocation, FidelityOptions noValidOptions)
			throws Exception {
		// test options
		for (int i = 0; i < testCaseOptions.size(); i++) {
			TestCaseOption tco = testCaseOptions.get(i);

			if (tco.getFidelityOptions().equals(noValidOptions)) {
				continue;
			}

			// update schema
			tco.setSchemaLocation(schemaLocation);
			// test all encode APIs

			// 1. encode SAX
			_testOption(tco, API.SAX);

			// 2. encode DOM
			_testOption(tco, API.DOM);

			// 3. encode StAX
			_testOption(tco, API.StAX);
		}
	}

}
