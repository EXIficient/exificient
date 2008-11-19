/*
 * Copyright (C) 2007, 2008 Siemens AG
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

package com.siemens.ct.exi.api.sax;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.StringReader;

import javax.xml.transform.sax.SAXResult;

import org.custommonkey.xmlunit.Validator;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.siemens.ct.exi.GrammarFactory;
import com.siemens.ct.exi.grammar.Grammar;

public class SchemaProperties extends AbstractProperties {
	String schema;

	private Grammar getGrammarFromSchemaAsString(String schemaAsString)
			throws Exception {
		ByteArrayInputStream bais = new ByteArrayInputStream(schemaAsString
				.getBytes());
		GrammarFactory grammarFactory = GrammarFactory.newInstance();
		Grammar grammar = grammarFactory.createGrammar(bais);

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
		factory.setGrammar(getGrammarFromSchemaAsString(schema));

		// start encoding process
		XMLReader xmlReader = XMLReaderFactory.createXMLReader();

		SAXResult saxResult = new EXIResult(osEXI, factory);
		xmlReader.setContentHandler(saxResult.getHandler());

		xmlReader.parse(new InputSource(new StringReader(xml)));
	}

	private void startTest(boolean isXmlSchemaValid, boolean isXMLEqual)
			throws Exception {
		// encode
		ByteArrayOutputStream osEXI = new ByteArrayOutputStream();
		encodeSchemaInformedToEXI(osEXI, isXmlSchemaValid);

		// reverse streams
		ByteArrayInputStream isEXI = new ByteArrayInputStream(osEXI
				.toByteArray());

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
