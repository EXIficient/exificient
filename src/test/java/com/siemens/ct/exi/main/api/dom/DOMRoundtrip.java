/*
 * Copyright (c) 2007-2016 Siemens AG
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

package com.siemens.ct.exi.main.api.dom;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.custommonkey.xmlunit.XMLTestCase;
import org.custommonkey.xmlunit.XMLUnit;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.siemens.ct.exi.core.EXIFactory;
import com.siemens.ct.exi.core.FidelityOptions;
import com.siemens.ct.exi.grammars.GrammarFactory;
import com.siemens.ct.exi.main.api.dom.DOMBuilder;
import com.siemens.ct.exi.main.api.dom.DOMWriter;
import com.siemens.ct.exi.main.api.dom.DocumentFragmentBuilder;
import com.siemens.ct.exi.core.exceptions.EXIException;
import com.siemens.ct.exi.core.grammars.Grammars;
import com.siemens.ct.exi.core.helpers.DefaultEXIFactory;

public class DOMRoundtrip extends XMLTestCase {

	public void testSchemaNillable1() throws Exception {
		EXIFactory exiFactory = DefaultEXIFactory.newInstance();
		exiFactory.setFidelityOptions(FidelityOptions.createAll());
		roundtrip("./data/schema/nillable1.xml", exiFactory);
	}

	public void testW3CNotebook() throws Exception {
		EXIFactory exiFactory = DefaultEXIFactory.newInstance();
		exiFactory.setFidelityOptions(FidelityOptions.createAll());
		roundtrip("./data/W3C/PrimerNotebook/notebook.xml", exiFactory);
	}

	public void testW3CEXIbyExample() throws Exception {
		EXIFactory exiFactory = DefaultEXIFactory.newInstance();
		exiFactory.setFidelityOptions(FidelityOptions.createAll());
		roundtrip("./data/W3C/EXIbyExample/XMLSample.xml", exiFactory);
	}

	public void testW3CXMLSample() throws Exception {
		EXIFactory exiFactory = DefaultEXIFactory.newInstance();
		// exiFactory.setFidelityOptions(FidelityOptions.createAll());
		roundtrip("./data/W3C/XMLSample/XMLSample.xml", exiFactory);
	}

	public void testGeneralPerson() throws Exception {
		EXIFactory exiFactory = DefaultEXIFactory.newInstance();
		// processing instruction
		exiFactory.setFidelityOptions(FidelityOptions.createAll());
		roundtrip("./data/general/person.xml", exiFactory);
	}

	// /////////////////////

	public void testSchemaXsiType() throws Exception {
		EXIFactory exiFactory = DefaultEXIFactory.newInstance();
		// type-cast pfx for xml-comparison
		exiFactory.setFidelityOptions(FidelityOptions.createAll());
		// type-cast --> schema-informed
		Grammars g = GrammarFactory.newInstance().createGrammars(
				"./data/schema/xsi-type.xsd");
		exiFactory.setGrammars(g);

		roundtrip("./data/schema/xsi-type.xml", exiFactory);
	}

	public void testSchemaXsiType4() throws Exception {
		EXIFactory exiFactory = DefaultEXIFactory.newInstance();
		// type-cast pfx for xml-comparison
		exiFactory.setFidelityOptions(FidelityOptions.createAll());
		// type-cast --> schema-informed
		Grammars g = GrammarFactory.newInstance().createGrammars(
				"./data/schema/xsi-type4.xsd");
		exiFactory.setGrammars(g);

		roundtrip("./data/schema/xsi-type4.xml", exiFactory);
	}

	public void testSchemaVehicle() throws Exception {
		EXIFactory exiFactory = DefaultEXIFactory.newInstance();
		// type-cast pfx for xml-comparison
		exiFactory.setFidelityOptions(FidelityOptions.createAll());
		// type-cast --> schema-informed
		Grammars g = GrammarFactory.newInstance().createGrammars(
				"./data/schema/vehicle.xsd");
		exiFactory.setGrammars(g);

		roundtrip("./data/schema/vehicle.xml", exiFactory);
	}

	public void testFragment1() throws Exception {
		EXIFactory exiFactory = DefaultEXIFactory.newInstance();
		exiFactory.setFragment(true);
		// exiFactory.setCodingMode(CodingMode.BYTE_PACKED);

		roundtrip("./data/fragment/fragment1.xml.frag", exiFactory);
	}

	public void testFragment2() throws Exception {
		EXIFactory exiFactory = DefaultEXIFactory.newInstance();
		exiFactory.setFragment(true);

		exiFactory.setFidelityOptions(FidelityOptions.createStrict());
		// type-cast --> schema-informed
		Grammars g = GrammarFactory.newInstance().createGrammars(
				"./data/fragment/fragment.xsd");
		exiFactory.setGrammars(g);

		roundtrip("./data/fragment/fragment2.xml.frag", exiFactory);
	}

	public void roundtrip(String sXML, EXIFactory exiFactory)
			throws ParserConfigurationException, SAXException, IOException,
			EXIException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder = factory.newDocumentBuilder();

		Node doc;
		if (exiFactory.isFragment()) {
			DocumentFragmentBuilder fdb = new DocumentFragmentBuilder(builder);
			doc = fdb.parse(new FileInputStream(sXML));
		} else {
			doc = builder.parse(new File(sXML));
		}

		// encode DOM to EXI
		DOMWriter domEncoder = new DOMWriter(exiFactory);
		ByteArrayOutputStream osEXI = new ByteArrayOutputStream();
		// File f = File.createTempFile("prefix", "suffix");
		// OutputStream osEXI = new FileOutputStream(f);
		domEncoder.setOutput(osEXI);
		domEncoder.encode(doc);
		osEXI.flush();

		// decode EXI to DOM
		InputStream is = new ByteArrayInputStream(osEXI.toByteArray());
		// InputStream is = new FileInputStream(f);
		DOMBuilder db = new DOMBuilder(exiFactory);

		if (exiFactory.isFragment()) {
			// @SuppressWarnings("unused")
			DocumentFragment exiDocumentFragment = db.parseFragment(is);
			assertTrue(exiDocumentFragment != null);
		} else {
			Document exiDocument = db.parse(is);
			// equal ?
			isXMLEqual((Document) doc, exiDocument);
		}
	}

	protected void isXMLEqual(Document control, Document test)
			throws SAXException, IOException {
		XMLUnit.setIgnoreWhitespace(true);
		XMLUnit.setIgnoreAttributeOrder(true);
		XMLUnit.setIgnoreComments(true);
		XMLUnit.setIgnoreDiffBetweenTextAndCDATA(true);
		// XMLUnit.setNormalize(true);

		// Diff diff = compareXML (control, test);

		assertXMLEqual(control, test);
	}
}
