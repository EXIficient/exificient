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

package com.siemens.ct.exi.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.xml.namespace.QName;

import junit.framework.TestCase;

import com.siemens.ct.exi.CodingMode;
import com.siemens.ct.exi.Constants;
import com.siemens.ct.exi.EXIBodyDecoder;
import com.siemens.ct.exi.EXIBodyEncoder;
import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.EncodingOptions;
import com.siemens.ct.exi.FidelityOptions;
import com.siemens.ct.exi.GrammarFactory;
import com.siemens.ct.exi.grammars.Grammars;
import com.siemens.ct.exi.grammars.event.EventType;
import com.siemens.ct.exi.helpers.DefaultEXIFactory;
import com.siemens.ct.exi.values.BooleanValue;
import com.siemens.ct.exi.values.QNameValue;
import com.siemens.ct.exi.values.StringValue;
import com.siemens.ct.exi.values.Value;

public class SchemaInformedTest extends TestCase {
	
	public static Grammars getGrammarFromSchemaAsString(String schemaAsString)
			throws Exception {
		ByteArrayInputStream bais = new ByteArrayInputStream(
				schemaAsString.getBytes());
		GrammarFactory grammarFactory = GrammarFactory.newInstance();
		Grammars grammar = grammarFactory.createGrammars(bais);

		return grammar;
	}

	public SchemaInformedTest(String testName) {
		super(testName);
	}

	// skip xsi:nil
	public void testIncludeInsignificantXsiNilA() throws Exception {
		String schema = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ " <xs:element name='root' type='xs:string' nillable='true' >"
				+ " </xs:element>" + "</xs:schema>";

		Grammars g = SchemaInformedTest.getGrammarFromSchemaAsString(schema);

		EXIFactory factory = DefaultEXIFactory.newInstance();

		factory.setFidelityOptions(FidelityOptions.createStrict());
		factory.setCodingMode(CodingMode.BIT_PACKED);
		factory.setGrammars(g);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		QName qnRoot = new QName("", "root");

		// encoder
		{
			EXIBodyEncoder encoder = factory.createEXIBodyEncoder();
			encoder.setOutputStream(baos);
			String pfx = null; // unset according fidelity-options
			encoder.encodeStartDocument();
			encoder.encodeStartElement(qnRoot.getNamespaceURI(),
					qnRoot.getLocalPart(), pfx);
			encoder.encodeAttributeXsiNil(new StringValue("false"), pfx);
			encoder.encodeEndElement();
			encoder.encodeEndDocument();
			encoder.flush();
		}

		// decoder
		{
			EXIBodyDecoder decoder = factory.createEXIBodyDecoder();
			decoder.setInputStream(new ByteArrayInputStream(baos.toByteArray()));

			assertTrue(decoder.next() == EventType.START_DOCUMENT);
			decoder.decodeStartDocument();

			assertTrue(decoder.next() == EventType.START_ELEMENT);
			assertTrue(decoder.decodeStartElement().getQName().equals(qnRoot));

			assertTrue(decoder.next() == EventType.CHARACTERS);
			assertTrue(decoder.decodeCharacters().equals(""));

			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.END_DOCUMENT);
			decoder.decodeEndDocument();
		}
	}

	// retain xsi:nil
	public void testIncludeInsignificantXsiNilB() throws Exception {
		String schema = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ " <xs:element name='root' type='xs:string' nillable='true' >"
				+ " </xs:element>" + "</xs:schema>";

		Grammars g = SchemaInformedTest.getGrammarFromSchemaAsString(schema);

		EXIFactory factory = DefaultEXIFactory.newInstance();

		factory.setFidelityOptions(FidelityOptions.createStrict());
		factory.setCodingMode(CodingMode.BIT_PACKED);
		factory.setGrammars(g);
		EncodingOptions eo = factory.getEncodingOptions();
		eo.setOption(EncodingOptions.INCLUDE_INSIGNIFICANT_XSI_NIL);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		QName qnRoot = new QName("", "root");

		// encoder
		{
			EXIBodyEncoder encoder = factory.createEXIBodyEncoder();
			encoder.setOutputStream(baos);
			String pfx = null; // unset according fidelity-options
			encoder.encodeStartDocument();
			encoder.encodeStartElement(qnRoot.getNamespaceURI(),
					qnRoot.getLocalPart(), pfx);
			encoder.encodeAttributeXsiNil(BooleanValue.BOOLEAN_VALUE_FALSE, pfx);
			encoder.encodeEndElement();
			encoder.encodeEndDocument();
			encoder.flush();
		}

		// decoder
		{
			EXIBodyDecoder decoder = factory.createEXIBodyDecoder();
			decoder.setInputStream(new ByteArrayInputStream(baos.toByteArray()));

			assertTrue(decoder.next() == EventType.START_DOCUMENT);
			decoder.decodeStartDocument();

			assertTrue(decoder.next() == EventType.START_ELEMENT);
			assertTrue(decoder.decodeStartElement().getQName().equals(qnRoot));

			assertTrue(decoder.next() == EventType.ATTRIBUTE_XSI_NIL);
			assertTrue(decoder.decodeAttributeXsiNil().getLocalName()
					.equals("nil"));

			Value xsiNil = decoder.getAttributeValue();
			assertTrue(xsiNil instanceof BooleanValue);
			BooleanValue bv = (BooleanValue) xsiNil;
			assertFalse(bv.toBoolean());

			assertTrue(decoder.next() == EventType.CHARACTERS);
			assertTrue(decoder.decodeCharacters().equals(""));

			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.END_DOCUMENT);
			decoder.decodeEndDocument();
		}
	}

	// skip xsi:type
	public void testIncludeInsignificantXsiTypeA() throws Exception {
		String schema = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ " <xs:element name='root' type='xs:string'  >"
				+ " </xs:element>" + "</xs:schema>";

		Grammars g = SchemaInformedTest.getGrammarFromSchemaAsString(schema);

		EXIFactory factory = DefaultEXIFactory.newInstance();

		factory.setFidelityOptions(FidelityOptions.createStrict());
		factory.setCodingMode(CodingMode.BIT_PACKED);
		factory.setGrammars(g);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		QName qnRoot = new QName("", "root");

		// encoder
		{
			EXIBodyEncoder encoder = factory.createEXIBodyEncoder();
			encoder.setOutputStream(baos);
			String pfx = null; // unset according fidelity-options
			encoder.encodeStartDocument();
			encoder.encodeStartElement(qnRoot.getNamespaceURI(),
					qnRoot.getLocalPart(), pfx);
			encoder.encodeNamespaceDeclaration(
					Constants.XML_SCHEMA_NS_URI, "xs");
			encoder.encodeAttributeXsiType(new StringValue("xs:string"), pfx);

			encoder.encodeEndElement();
			encoder.encodeEndDocument();
			encoder.flush();
		}

		// decoder
		{
			EXIBodyDecoder decoder = factory.createEXIBodyDecoder();
			decoder.setInputStream(new ByteArrayInputStream(baos.toByteArray()));

			assertTrue(decoder.next() == EventType.START_DOCUMENT);
			decoder.decodeStartDocument();

			assertTrue(decoder.next() == EventType.START_ELEMENT);
			assertTrue(decoder.decodeStartElement().getQName().equals(qnRoot));

			assertTrue(decoder.next() == EventType.ATTRIBUTE_XSI_TYPE);
			assertTrue(decoder.decodeAttributeXsiType().getLocalName()
					.equals("type"));
			QNameValue qnv = (QNameValue) decoder.getAttributeValue();
			assertTrue(qnv.getLocalName().equals("string"));
			assertTrue(qnv.getNamespaceUri().equals(
					"http://www.w3.org/2001/XMLSchema"));

			assertTrue(decoder.next() == EventType.CHARACTERS);
			assertTrue(decoder.decodeCharacters().equals(""));

			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.END_DOCUMENT);
			decoder.decodeEndDocument();
		}
	}

	// retain insignificant xsi:type
	public void testIncludeInsignificantXsiTypeB() throws Exception {
		String schema = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ " <xs:element name='root' type='xs:string'  >"
				+ " </xs:element>" + "</xs:schema>";

		Grammars g = SchemaInformedTest.getGrammarFromSchemaAsString(schema);

		EXIFactory factory = DefaultEXIFactory.newInstance();

		factory.setFidelityOptions(FidelityOptions.createStrict());
		factory.setCodingMode(CodingMode.BIT_PACKED);
		factory.setGrammars(g);
		// EncodingOptions eo = factory.getEncodingOptions();
		// eo.setOption(EncodingOptions.INCLUDE_INSIGNIFICANT_XSI_TYPE);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		QName qnRoot = new QName("", "root");

		// encoder
		{
			EXIBodyEncoder encoder = factory.createEXIBodyEncoder();
			encoder.setOutputStream(baos);
			String pfx = null; // unset according fidelity-options
			encoder.encodeStartDocument();
			encoder.encodeStartElement(qnRoot.getNamespaceURI(),
					qnRoot.getLocalPart(), pfx);
			encoder.encodeNamespaceDeclaration(
					Constants.XML_SCHEMA_NS_URI, "xs");
			encoder.encodeAttributeXsiType(new StringValue("xs:string"), pfx);

			encoder.encodeEndElement();
			encoder.encodeEndDocument();
			encoder.flush();
		}

		// decoder
		{
			EXIBodyDecoder decoder = factory.createEXIBodyDecoder();
			decoder.setInputStream(new ByteArrayInputStream(baos.toByteArray()));

			assertTrue(decoder.next() == EventType.START_DOCUMENT);
			decoder.decodeStartDocument();

			assertTrue(decoder.next() == EventType.START_ELEMENT);
			assertTrue(decoder.decodeStartElement().getQName().equals(qnRoot));

			assertTrue(decoder.next() == EventType.ATTRIBUTE_XSI_TYPE);
			assertTrue(decoder.decodeAttributeXsiType().getLocalName()
					.equals("type"));

			Value xsiType = decoder.getAttributeValue();
			assertTrue(xsiType instanceof QNameValue);
			QNameValue qv = (QNameValue) xsiType;
			// assertTrue(qv.toQName().getLocalPart().equals("string"));
			assertTrue(qv.getLocalName().equals("string"));

			assertTrue(decoder.next() == EventType.CHARACTERS);
			assertTrue(decoder.decodeCharacters().equals(""));

			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.END_DOCUMENT);
			decoder.decodeEndDocument();
		}
	}

}