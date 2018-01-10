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

package com.siemens.ct.exi.main.api.sax;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.StringReader;

import org.custommonkey.xmlunit.Validator;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.siemens.ct.exi.grammars.GrammarFactory;
import com.siemens.ct.exi.main.api.sax.EXIResult;
import com.siemens.ct.exi.core.grammars.Grammars;

public class SchemaProperties extends AbstractProperties {
	String schema;

	private Grammars getGrammarFromSchemaAsString(String schemaAsString)
			throws Exception {
		ByteArrayInputStream bais = new ByteArrayInputStream(
				schemaAsString.getBytes());
		GrammarFactory grammarFactory = GrammarFactory.newInstance();
		Grammars grammar = grammarFactory.createGrammars(bais);

		return grammar;
	}

	private boolean isValidXML(String xmlAsString, String schemaAsString)
			throws SAXException {
		InputSource is = new InputSource(new StringReader(xmlAsString));
		Validator v = new Validator(is);
		v.useXMLSchema(true);
		// v.setJAXP12SchemaSource(new File(myXmlSchemaFile));
		// v.setJAXP12SchemaSource( new StringReader( schemaAsString ) ); //
		// reader does NOT work
		v.setJAXP12SchemaSource(new ByteArrayInputStream(schemaAsString
				.getBytes()));

		boolean isValid = v.isValid();

		return isValid;
	}

	private void encodeSchemaInformedToEXI(OutputStream osEXI,
			boolean isXmlSchemaValid) throws Exception {
		if (isXmlSchemaValid) {
			assertTrue("No valid XML or Schema given!", isValidXML(xml, schema));
		}

		// set grammar
		factory.setGrammars(getGrammarFromSchemaAsString(schema));

		// start encoding process
		XMLReader xmlReader = XMLReaderFactory.createXMLReader();

		EXIResult exiResult = new EXIResult(factory);
		exiResult.setOutputStream(osEXI);
		xmlReader.setContentHandler(exiResult.getHandler());

		xmlReader.parse(new InputSource(new StringReader(xml)));
	}

	private void startTest(boolean isXmlSchemaValid, boolean isXMLEqual)
			throws Exception {
		// encode
		ByteArrayOutputStream osEXI = new ByteArrayOutputStream();
		encodeSchemaInformedToEXI(osEXI, isXmlSchemaValid);

		// reverse streams
		ByteArrayInputStream isEXI = new ByteArrayInputStream(
				osEXI.toByteArray());

		// decode
		String sXMLDecoded = decodeEXIToXML(isEXI);

		// equal ?
		if (isXMLEqual) {
			isXMLEqual(sXMLDecoded);
		}

	}

	public void testSimple1() throws Exception {
		schema = SIMPLE_XSD;
		xml = SIMPLE_XML;

		startTest(true, true);
	}

	public void testUnexpectedRoot() throws Exception {
		schema = UNEXPECTED_ROOT_XSD;
		xml = UNEXPECTED_ROOT_XML;

		startTest(false, true);
	}

	public void testXsiType() throws Exception {
		schema = XSI_TYPE_XSD;
		xml = XSI_TYPE_XML;

		// isXMLEqual == false : xsi:type prefixes are not preserved
		startTest(true, false);
	}

}
