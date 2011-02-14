/*
 * Copyright (C) 2007-2010 Siemens AG
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

package com.siemens.ct.exi.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import junit.framework.TestCase;

import com.siemens.ct.exi.CodingMode;
import com.siemens.ct.exi.EXIBodyDecoder;
import com.siemens.ct.exi.EXIBodyEncoder;
import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.EncodingOptions;
import com.siemens.ct.exi.FidelityOptions;
import com.siemens.ct.exi.grammar.Grammar;
import com.siemens.ct.exi.grammar.GrammarTest;
import com.siemens.ct.exi.grammar.event.EventType;
import com.siemens.ct.exi.helpers.DefaultEXIFactory;
import com.siemens.ct.exi.values.BooleanValue;
import com.siemens.ct.exi.values.QNameValue;
import com.siemens.ct.exi.values.Value;

public class SchemaInformedTest extends TestCase {

	public SchemaInformedTest(String testName) {
		super(testName);
	}

	// skip xsi:nil
	public void testIncludeInsignificantXsiNilA() throws Exception {
		String schema = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
			+ " <xs:element name='root' type='xs:string' nillable='true' >"
			+ " </xs:element>" + "</xs:schema>";

		Grammar g = GrammarTest.getGrammarFromSchemaAsString(schema);
		
		
		EXIFactory factory = DefaultEXIFactory.newInstance();

		factory.setFidelityOptions(FidelityOptions.createStrict());
		factory.setCodingMode(CodingMode.BIT_PACKED);
		factory.setGrammar ( g );

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		QName qnRoot = new QName("", "root");

		// encoder
		{
			EXIBodyEncoder encoder = factory.createEXIBodyEncoder();
			encoder.setOutputStream(baos);
			String pfx = null; // unset according fidelity-options
			encoder.encodeStartDocument();
			encoder.encodeStartElement(qnRoot.getNamespaceURI(), qnRoot.getLocalPart(),
					pfx);
			encoder.encodeXsiNil("false", pfx);
			encoder.encodeEndElement();
			encoder.encodeEndDocument();
			encoder.flush();
		}

		// decoder
		{
			EXIBodyDecoder decoder = factory.createEXIBodyDecoder();
			decoder.setInputStream(
					new ByteArrayInputStream(baos.toByteArray()));

			assertTrue(decoder.next() == EventType.START_DOCUMENT);
			decoder.decodeStartDocument();

			assertTrue(decoder.next() == EventType.START_ELEMENT);
			assertTrue(decoder.decodeStartElement().equals(qnRoot));

			assertTrue(decoder.next() == EventType.CHARACTERS);
			assertTrue(decoder.decodeCharacters().equals(""));
			
			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElementUndeclared();

			assertTrue(decoder.next() == EventType.END_DOCUMENT);
			decoder.decodeEndDocument();
		}
	}
	
	// retain xsi:nil
	public void testIncludeInsignificantXsiNilB() throws Exception {
		String schema = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
			+ " <xs:element name='root' type='xs:string' nillable='true' >"
			+ " </xs:element>" + "</xs:schema>";
	
		Grammar g = GrammarTest.getGrammarFromSchemaAsString(schema);
		
		
		EXIFactory factory = DefaultEXIFactory.newInstance();
	
		factory.setFidelityOptions(FidelityOptions.createStrict());
		factory.setCodingMode(CodingMode.BIT_PACKED);
		factory.setGrammar ( g );
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
			encoder.encodeStartElement(qnRoot.getNamespaceURI(), qnRoot.getLocalPart(),
					pfx);
			encoder.encodeXsiNil("false", pfx);
			encoder.encodeEndElement();
			encoder.encodeEndDocument();
			encoder.flush();
		}
	
		// decoder
		{
			EXIBodyDecoder decoder = factory.createEXIBodyDecoder();
			decoder.setInputStream(
					new ByteArrayInputStream(baos.toByteArray()));
	
			assertTrue(decoder.next() == EventType.START_DOCUMENT);
			decoder.decodeStartDocument();
	
			assertTrue(decoder.next() == EventType.START_ELEMENT);
			assertTrue(decoder.decodeStartElement().equals(qnRoot));
	
			assertTrue(decoder.next() == EventType.ATTRIBUTE_XSI_NIL);
			assertTrue(decoder.decodeAttributeXsiNil().getLocalPart().equals("nil"));
			
			Value xsiNil = decoder.getAttributeValue();
			assertTrue(xsiNil instanceof BooleanValue);
			BooleanValue bv = (BooleanValue) xsiNil;
			assertFalse(bv.toBoolean());
			
			assertTrue(decoder.next() == EventType.CHARACTERS);
			assertTrue(decoder.decodeCharacters().equals(""));
			
			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElementUndeclared();
	
			assertTrue(decoder.next() == EventType.END_DOCUMENT);
			decoder.decodeEndDocument();
		}
	}

	// skip xsi:type
	public void testIncludeInsignificantXsiTypeA() throws Exception {
		String schema = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
			+ " <xs:element name='root' type='xs:string'  >"
			+ " </xs:element>" + "</xs:schema>";
	
		Grammar g = GrammarTest.getGrammarFromSchemaAsString(schema);
		
		
		EXIFactory factory = DefaultEXIFactory.newInstance();
	
		factory.setFidelityOptions(FidelityOptions.createStrict());
		factory.setCodingMode(CodingMode.BIT_PACKED);
		factory.setGrammar ( g );
	
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		QName qnRoot = new QName("", "root");
	
		// encoder
		{
			EXIBodyEncoder encoder = factory.createEXIBodyEncoder();
			encoder.setOutputStream(baos);
			String pfx = null; // unset according fidelity-options
			encoder.encodeStartDocument();
			encoder.encodeStartElement(qnRoot.getNamespaceURI(), qnRoot.getLocalPart(),
					pfx);
			encoder.encodeNamespaceDeclaration(XMLConstants.W3C_XML_SCHEMA_NS_URI, "xs");
			encoder.encodeXsiType("xs:string", pfx);
			
			encoder.encodeEndElement();
			encoder.encodeEndDocument();
			encoder.flush();
		}
	
		// decoder
		{
			EXIBodyDecoder decoder = factory.createEXIBodyDecoder();
			decoder.setInputStream(
					new ByteArrayInputStream(baos.toByteArray()));
	
			assertTrue(decoder.next() == EventType.START_DOCUMENT);
			decoder.decodeStartDocument();
	
			assertTrue(decoder.next() == EventType.START_ELEMENT);
			assertTrue(decoder.decodeStartElement().equals(qnRoot));
			
			assertTrue(decoder.next() == EventType.CHARACTERS);
			assertTrue(decoder.decodeCharacters().equals(""));
			
			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElementUndeclared();
	
			assertTrue(decoder.next() == EventType.END_DOCUMENT);
			decoder.decodeEndDocument();
		}
	}

	// retain insignificant xsi:type
	public void testIncludeInsignificantXsiTypeB() throws Exception {
		String schema = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
			+ " <xs:element name='root' type='xs:string'  >"
			+ " </xs:element>" + "</xs:schema>";
	
		Grammar g = GrammarTest.getGrammarFromSchemaAsString(schema);
		
		
		EXIFactory factory = DefaultEXIFactory.newInstance();
	
		factory.setFidelityOptions(FidelityOptions.createStrict());
		factory.setCodingMode(CodingMode.BIT_PACKED);
		factory.setGrammar ( g );
		EncodingOptions eo = factory.getEncodingOptions();
		eo.setOption(EncodingOptions.INCLUDE_INSIGNIFICANT_XSI_TYPE);
	
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		QName qnRoot = new QName("", "root");
	
		// encoder
		{
			EXIBodyEncoder encoder = factory.createEXIBodyEncoder();
			encoder.setOutputStream(baos);
			String pfx = null; // unset according fidelity-options
			encoder.encodeStartDocument();
			encoder.encodeStartElement(qnRoot.getNamespaceURI(), qnRoot.getLocalPart(),
					pfx);
			encoder.encodeNamespaceDeclaration(XMLConstants.W3C_XML_SCHEMA_NS_URI, "xs");
			encoder.encodeXsiType("xs:string", pfx);
			
			encoder.encodeEndElement();
			encoder.encodeEndDocument();
			encoder.flush();
		}
	
		// decoder
		{
			EXIBodyDecoder decoder = factory.createEXIBodyDecoder();
			decoder.setInputStream(
					new ByteArrayInputStream(baos.toByteArray()));
	
			assertTrue(decoder.next() == EventType.START_DOCUMENT);
			decoder.decodeStartDocument();
	
			assertTrue(decoder.next() == EventType.START_ELEMENT);
			assertTrue(decoder.decodeStartElement().equals(qnRoot));
			
			assertTrue(decoder.next() == EventType.ATTRIBUTE_XSI_TYPE);
			assertTrue(decoder.decodeAttributeXsiType().getLocalPart().equals("type"));
			
			Value xsiType = decoder.getAttributeValue();
			assertTrue(xsiType instanceof QNameValue);
			QNameValue qv = (QNameValue) xsiType;
			assertTrue(qv.toQName().getLocalPart().equals("string"));
			
			assertTrue(decoder.next() == EventType.CHARACTERS);
			assertTrue(decoder.decodeCharacters().equals(""));
			
			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElementUndeclared();
	
			assertTrue(decoder.next() == EventType.END_DOCUMENT);
			decoder.decodeEndDocument();
		}
	}

}