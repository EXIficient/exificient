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
import java.io.IOException;
import java.io.InputStream;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import junit.framework.TestCase;

import org.xml.sax.SAXException;

import com.siemens.ct.exi.CodingMode;
import com.siemens.ct.exi.Constants;
import com.siemens.ct.exi.EXIBodyDecoder;
import com.siemens.ct.exi.EXIBodyEncoder;
import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.EXIStreamDecoder;
import com.siemens.ct.exi.EncodingOptions;
import com.siemens.ct.exi.FidelityOptions;
import com.siemens.ct.exi.GrammarFactory;
import com.siemens.ct.exi.TestSAXEncoder;
import com.siemens.ct.exi.attributes.AttributeFactory;
import com.siemens.ct.exi.attributes.AttributeList;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.grammars.GrammarTest;
import com.siemens.ct.exi.grammars.Grammars;
import com.siemens.ct.exi.grammars.event.EventType;
import com.siemens.ct.exi.helpers.DefaultEXIFactory;
import com.siemens.ct.exi.io.channel.BitDecoderChannel;
import com.siemens.ct.exi.values.DateTimeValue;
import com.siemens.ct.exi.values.DecimalValue;
import com.siemens.ct.exi.values.FloatValue;
import com.siemens.ct.exi.values.IntegerValue;
import com.siemens.ct.exi.values.StringValue;
import com.siemens.ct.exi.values.Value;

public class CanonicalEXITestCase extends TestCase {

	public CanonicalEXITestCase(String testName) {
		super(testName);
	}

	public void testSchemaLessAttributes0() throws IOException, SAXException,
			EXIException {
		EXIFactory factory = DefaultEXIFactory.newInstance();

		factory.setFidelityOptions(FidelityOptions.createDefault());
		factory.setCodingMode(CodingMode.BIT_PACKED);
		factory.getEncodingOptions().setOption(EncodingOptions.CANONICAL_EXI);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		QName s1 = new QName("", "el1");

		QName aX = new QName("uri:foo", "atX");
		QName aY = new QName("uri:foo", "atY");
		QName aZ = new QName("uri:foo", "atZ");

		// encoder
		{
			EXIBodyEncoder encoder = factory.createEXIBodyEncoder();
			encoder.setOutputStream(baos);
			encoder.encodeStartDocument();
			encoder.encodeStartElement(s1.getNamespaceURI(), s1.getLocalPart(),
					null);
			AttributeList al = AttributeFactory.newInstance()
					.createAttributeListInstance(factory);
			al.addAttribute(aZ, "valZ");
			al.addAttribute(aY, "valY");
			al.addAttribute(aX, "valX");
			encoder.encodeAttributeList(al);
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

			assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC);
			assertTrue(decoder.decodeStartElement().getQName().equals(s1));

			assertTrue(decoder.next() == EventType.ATTRIBUTE_GENERIC_UNDECLARED);
			assertTrue(decoder.decodeAttribute().getQName().equals(aX));

			assertTrue(decoder.next() == EventType.ATTRIBUTE_GENERIC_UNDECLARED);
			assertTrue(decoder.decodeAttribute().getQName().equals(aY));

			assertTrue(decoder.next() == EventType.ATTRIBUTE_GENERIC_UNDECLARED);
			assertTrue(decoder.decodeAttribute().getQName().equals(aZ));

			assertTrue(decoder.next() == EventType.END_ELEMENT_UNDECLARED);
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.END_DOCUMENT);
			decoder.decodeEndDocument();
		}
	}

	public void testSchemaLessNs0() throws IOException, SAXException,
			EXIException {
		EXIFactory factory = DefaultEXIFactory.newInstance();

		factory.setFidelityOptions(FidelityOptions.createDefault());
		factory.getFidelityOptions().setFidelity(
				FidelityOptions.FEATURE_PREFIX, true);
		factory.setCodingMode(CodingMode.BIT_PACKED);
		factory.getEncodingOptions().setOption(EncodingOptions.CANONICAL_EXI);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		QName s1 = new QName("", "el1", "");

		String u1 = "uri:foo1";
		String u2 = "uri:foo2";
		String u3 = "uri:foo3";
		String u3b = "uri:foo";

		String pX = "X";
		String pY = "Y";
		String pZ = "Z";
		String pZZ = "ZZ";

		// encoder
		{
			EXIBodyEncoder encoder = factory.createEXIBodyEncoder();
			encoder.setOutputStream(baos);
			encoder.encodeStartDocument();
			encoder.encodeStartElement(s1);
			AttributeList al = AttributeFactory.newInstance()
					.createAttributeListInstance(factory);
			al.addNamespaceDeclaration(u3, pZ);
			al.addNamespaceDeclaration(u3b, pZZ);
			al.addNamespaceDeclaration(u2, pY);
			al.addNamespaceDeclaration(u1, pX);
			encoder.encodeAttributeList(al);
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

			assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC);
			assertTrue(decoder.decodeStartElement().getQName().equals(s1));

			assertTrue(decoder.next() == EventType.NAMESPACE_DECLARATION);
			assertTrue(decoder.decodeNamespaceDeclaration().prefix.equals(pX));

			assertTrue(decoder.next() == EventType.NAMESPACE_DECLARATION);
			assertTrue(decoder.decodeNamespaceDeclaration().prefix.equals(pY));

			assertTrue(decoder.next() == EventType.NAMESPACE_DECLARATION);
			assertTrue(decoder.decodeNamespaceDeclaration().prefix.equals(pZ));

			assertTrue(decoder.next() == EventType.NAMESPACE_DECLARATION);
			assertTrue(decoder.decodeNamespaceDeclaration().prefix.equals(pZZ));

			assertTrue(decoder.next() == EventType.END_ELEMENT_UNDECLARED);
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.END_DOCUMENT);
			decoder.decodeEndDocument();
		}
	}

	// A sign value of one (1) MUST be changed to zero (0) if both the integral
	// portion and the fractional portion of the Decimal value are 0 (zero).
	public void testDatatypeDecimal0() throws IOException, SAXException,
			EXIException {
		EXIFactory factory = DefaultEXIFactory.newInstance();

		factory.setFidelityOptions(FidelityOptions.createDefault());
		factory.getFidelityOptions().setFidelity(
				FidelityOptions.FEATURE_PREFIX, true);
		factory.setGrammars(GrammarFactory.newInstance()
				.createXSDTypesOnlyGrammars());
		factory.setCodingMode(CodingMode.BIT_PACKED);
		factory.getEncodingOptions().setOption(EncodingOptions.CANONICAL_EXI);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		QName s1 = new QName("", "el1", "");

		// encoder
		{
			EXIBodyEncoder encoder = factory.createEXIBodyEncoder();
			encoder.setOutputStream(baos);
			encoder.encodeStartDocument();
			encoder.encodeStartElement(s1);
			AttributeList al = AttributeFactory.newInstance()
					.createAttributeListInstance(factory);
			al.addNamespaceDeclaration(XMLConstants.W3C_XML_SCHEMA_NS_URI,
					"xsd");
			al.addAttribute(new QName(
					XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "type"),
					"xsd:decimal");
			encoder.encodeAttributeList(al);
			encoder.encodeCharacters(new StringValue("-0.0"));
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

			assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC);
			assertTrue(decoder.decodeStartElement().getQName().equals(s1));

			assertTrue(decoder.next() == EventType.NAMESPACE_DECLARATION);
			assertTrue(decoder.decodeNamespaceDeclaration().prefix
					.equals("xsd"));

			assertTrue(decoder.next() == EventType.ATTRIBUTE_GENERIC_UNDECLARED);
			decoder.decodeAttribute()
					.getQName()
					.equals(new QName(
							XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "type"));
			// decoder.decodeAttributeXsiType();

			assertTrue(decoder.next() == EventType.CHARACTERS);
			Value v = decoder.decodeCharacters();
			assertTrue(v instanceof DecimalValue);
			DecimalValue dv = (DecimalValue) v;
			assertTrue(!dv.isNegative());
			assertTrue(dv.getIntegral().equals(IntegerValue.ZERO));
			assertTrue(dv.getRevFractional().equals(IntegerValue.ZERO));

			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.END_DOCUMENT);
			decoder.decodeEndDocument();
		}
	}

	// A sign value of one (1) MUST be changed to zero (0) if both the integral
	// portion and the fractional portion of the Decimal value are 0 (zero).
	public void testDatatypeDecimal1() throws Exception {
		EXIFactory factory = DefaultEXIFactory.newInstance();

		factory.setFidelityOptions(FidelityOptions.createDefault());
		String schema = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ " <xs:element name='el1' type='xs:decimal'>"
				+ " </xs:element>" + "</xs:schema>";

		Grammars g = GrammarTest.getGrammarFromSchemaAsString(schema);
		factory.setGrammars(g);
		factory.setCodingMode(CodingMode.BIT_PACKED);
		factory.getEncodingOptions().setOption(EncodingOptions.CANONICAL_EXI);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		QName s1 = new QName("", "el1", "");

		// encoder
		{
			EXIBodyEncoder encoder = factory.createEXIBodyEncoder();
			encoder.setOutputStream(baos);
			encoder.encodeStartDocument();
			encoder.encodeStartElement(s1);
			encoder.encodeCharacters(new StringValue("-0.0"));
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
			assertTrue(decoder.decodeStartElement().getQName().equals(s1));

			assertTrue(decoder.next() == EventType.CHARACTERS);
			Value v = decoder.decodeCharacters();
			assertTrue(v instanceof DecimalValue);
			DecimalValue dv = (DecimalValue) v;
			assertTrue(!dv.isNegative());
			assertTrue(dv.getIntegral().equals(IntegerValue.ZERO));
			assertTrue(dv.getRevFractional().equals(IntegerValue.ZERO));

			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.END_DOCUMENT);
			decoder.decodeEndDocument();
		}
	}

	// EXI processors MUST support Unsigned Integer values less than 2147483648.
	public void testDatatypeUnsignedInteger0() throws Exception {
		EXIFactory factory = DefaultEXIFactory.newInstance();

		factory.setFidelityOptions(FidelityOptions.createDefault());
		String schema = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ " <xs:element name='el1' type='xs:nonNegativeInteger'>"
				+ " </xs:element>" + "</xs:schema>";

		Grammars g = GrammarTest.getGrammarFromSchemaAsString(schema);
		factory.setGrammars(g);
		factory.setCodingMode(CodingMode.BIT_PACKED);
		factory.getEncodingOptions().setOption(EncodingOptions.CANONICAL_EXI);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		QName s1 = new QName("", "el1");

		// encoder
		{
			EXIBodyEncoder encoder = factory.createEXIBodyEncoder();
			encoder.setOutputStream(baos);
			encoder.encodeStartDocument();
			encoder.encodeStartElement(s1.getNamespaceURI(), s1.getLocalPart(),
					null);
			encoder.encodeCharacters(new StringValue("" + (2147483648L + 1)));
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
			assertTrue(decoder.decodeStartElement().getQName().equals(s1));

			assertTrue(decoder.next() == EventType.CHARACTERS);
			Value v = decoder.decodeCharacters();
			assertTrue(v instanceof IntegerValue);
			IntegerValue iv = (IntegerValue) v;
			assertTrue(iv.isPositive());
			assertTrue(iv.longValue() == (2147483648L + 1));

			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.END_DOCUMENT);
			decoder.decodeEndDocument();
		}
	}

	// When there is more than one item that represents the same value in the
	// enumeration, the value MUST be represented by using the first ordinal
	// position that represents the value.
	public void testDatatypeEnumeration0() throws Exception {
		EXIFactory factory = DefaultEXIFactory.newInstance();

		factory.setFidelityOptions(FidelityOptions.createDefault());
		String schema = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ " <xs:element name='el1' type='size'>"
				+ " </xs:element>"
				// twice enum value "small"
				+ "<xs:simpleType name='size'><xs:restriction base='xs:string'><xs:enumeration value='small' /><xs:enumeration value='medium' /><xs:enumeration value='large' /><xs:enumeration value='small' /></xs:restriction></xs:simpleType>"
				+ "</xs:schema>";

		Grammars g = GrammarTest.getGrammarFromSchemaAsString(schema);
		factory.setGrammars(g);
		factory.setCodingMode(CodingMode.BIT_PACKED);
		factory.getEncodingOptions().setOption(EncodingOptions.CANONICAL_EXI);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		QName s1 = new QName("", "el1");

		// encoder
		{
			EXIBodyEncoder encoder = factory.createEXIBodyEncoder();
			encoder.setOutputStream(baos);
			encoder.encodeStartDocument();
			encoder.encodeStartElement(s1.getNamespaceURI(), s1.getLocalPart(),
					null);
			encoder.encodeCharacters(new StringValue("small"));
			encoder.encodeEndElement();
			encoder.encodeEndDocument();
			encoder.flush();
		}

		// decoder
		{
			EXIBodyDecoder decoder = factory.createEXIBodyDecoder();
			BitDecoderChannel bdc = new BitDecoderChannel(
					new ByteArrayInputStream(baos.toByteArray()));
			decoder.setInputChannel(bdc);
			// decoder.setInputStream(new
			// ByteArrayInputStream(baos.toByteArray()));

			assertTrue(decoder.next() == EventType.START_DOCUMENT);
			decoder.decodeStartDocument();

			assertTrue(decoder.next() == EventType.START_ELEMENT);
			assertTrue(decoder.decodeStartElement().getQName().equals(s1));

			assertTrue(decoder.next() == EventType.CHARACTERS);
			// // Note: 4 enumerated values.. hence decode 2 bits --> result
			// should be zero for first ordinal position
			// int ev = bdc.decodeNBitUnsignedInteger(2);
			// assertTrue(ev == 0);

			Value v = decoder.decodeCharacters();
			assertTrue(v instanceof StringValue);
			assertTrue(v.toString().equals("small"));

			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.END_DOCUMENT);
			decoder.decodeEndDocument();

			// check that everywhere "zeros" are used and no other bit..
			byte[] bytes = baos.toByteArray();
			assertTrue(bytes.length == 1);
			assertTrue(bytes[0] == 0); // 0000 0000
		}
	}

	// http://www.w3.org/TR/exi-c14n/#dt-float
	// 123.012300 --> 1230123 -4
	public void testDatatypeFloat0() throws Exception {
		EXIFactory factory = DefaultEXIFactory.newInstance();

		factory.setFidelityOptions(FidelityOptions.createDefault());
		String schema = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ " <xs:element name='el1' type='xs:float'>"
				+ " </xs:element>"
				+ "</xs:schema>";

		Grammars g = GrammarTest.getGrammarFromSchemaAsString(schema);
		factory.setGrammars(g);
		factory.setCodingMode(CodingMode.BIT_PACKED);
		factory.getEncodingOptions().setOption(EncodingOptions.CANONICAL_EXI);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		QName s1 = new QName("", "el1");

		// encoder
		{
			EXIBodyEncoder encoder = factory.createEXIBodyEncoder();
			encoder.setOutputStream(baos);
			encoder.encodeStartDocument();
			encoder.encodeStartElement(s1.getNamespaceURI(), s1.getLocalPart(),
					null);
			encoder.encodeCharacters(new StringValue("123.012300"));
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
			assertTrue(decoder.decodeStartElement().getQName().equals(s1));

			assertTrue(decoder.next() == EventType.CHARACTERS);
			Value v = decoder.decodeCharacters();
			assertTrue(v instanceof FloatValue);
			FloatValue fv = (FloatValue) v;
			assertTrue(fv.getMantissa().equals(IntegerValue.parse("1230123")));
			assertTrue(fv.getExponent().equals(IntegerValue.parse("-4")));

			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.END_DOCUMENT);
			decoder.decodeEndDocument();
		}
	}

	// http://www.w3.org/TR/exi-c14n/#dt-float
	// 0.0 --> 0 0
	public void testDatatypeFloat1() throws Exception {
		EXIFactory factory = DefaultEXIFactory.newInstance();

		factory.setFidelityOptions(FidelityOptions.createDefault());
		String schema = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ " <xs:element name='el1' type='xs:float'>"
				+ " </xs:element>"
				+ "</xs:schema>";

		Grammars g = GrammarTest.getGrammarFromSchemaAsString(schema);
		factory.setGrammars(g);
		factory.setCodingMode(CodingMode.BIT_PACKED);
		factory.getEncodingOptions().setOption(EncodingOptions.CANONICAL_EXI);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		QName s1 = new QName("", "el1");

		// encoder
		{
			EXIBodyEncoder encoder = factory.createEXIBodyEncoder();
			encoder.setOutputStream(baos);
			encoder.encodeStartDocument();
			encoder.encodeStartElement(s1.getNamespaceURI(), s1.getLocalPart(),
					null);
			encoder.encodeCharacters(new StringValue("0.0"));
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
			assertTrue(decoder.decodeStartElement().getQName().equals(s1));

			assertTrue(decoder.next() == EventType.CHARACTERS);
			Value v = decoder.decodeCharacters();
			assertTrue(v instanceof FloatValue);
			FloatValue fv = (FloatValue) v;
			assertTrue(fv.getMantissa().equals(IntegerValue.parse("0")));
			assertTrue(fv.getExponent().equals(IntegerValue.parse("0")));

			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.END_DOCUMENT);
			decoder.decodeEndDocument();
		}
	}

	// http://www.w3.org/TR/exi-c14n/#dt-float
	// -0.0 --> 0 0
	public void testDatatypeFloat2() throws Exception {
		EXIFactory factory = DefaultEXIFactory.newInstance();

		factory.setFidelityOptions(FidelityOptions.createDefault());
		String schema = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ " <xs:element name='el1' type='xs:float'>"
				+ " </xs:element>"
				+ "</xs:schema>";

		Grammars g = GrammarTest.getGrammarFromSchemaAsString(schema);
		factory.setGrammars(g);
		factory.setCodingMode(CodingMode.BIT_PACKED);
		factory.getEncodingOptions().setOption(EncodingOptions.CANONICAL_EXI);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		QName s1 = new QName("", "el1");

		// encoder
		{
			EXIBodyEncoder encoder = factory.createEXIBodyEncoder();
			encoder.setOutputStream(baos);
			encoder.encodeStartDocument();
			encoder.encodeStartElement(s1.getNamespaceURI(), s1.getLocalPart(),
					null);
			encoder.encodeCharacters(new StringValue("-0.0"));
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
			assertTrue(decoder.decodeStartElement().getQName().equals(s1));

			assertTrue(decoder.next() == EventType.CHARACTERS);
			Value v = decoder.decodeCharacters();
			assertTrue(v instanceof FloatValue);
			FloatValue fv = (FloatValue) v;
			assertTrue(fv.getMantissa().equals(IntegerValue.parse("0")));
			assertTrue(fv.getExponent().equals(IntegerValue.parse("0")));

			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.END_DOCUMENT);
			decoder.decodeEndDocument();
		}
	}

	// http://www.w3.org/TR/exi-c14n/#dt-float
	// 1.0 --> 1 0
	public void testDatatypeFloat3() throws Exception {
		EXIFactory factory = DefaultEXIFactory.newInstance();

		factory.setFidelityOptions(FidelityOptions.createDefault());
		String schema = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ " <xs:element name='el1' type='xs:float'>"
				+ " </xs:element>"
				+ "</xs:schema>";

		Grammars g = GrammarTest.getGrammarFromSchemaAsString(schema);
		factory.setGrammars(g);
		factory.setCodingMode(CodingMode.BIT_PACKED);
		factory.getEncodingOptions().setOption(EncodingOptions.CANONICAL_EXI);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		QName s1 = new QName("", "el1");

		// encoder
		{
			EXIBodyEncoder encoder = factory.createEXIBodyEncoder();
			encoder.setOutputStream(baos);
			encoder.encodeStartDocument();
			encoder.encodeStartElement(s1.getNamespaceURI(), s1.getLocalPart(),
					null);
			encoder.encodeCharacters(new StringValue("1.0"));
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
			assertTrue(decoder.decodeStartElement().getQName().equals(s1));

			assertTrue(decoder.next() == EventType.CHARACTERS);
			Value v = decoder.decodeCharacters();
			assertTrue(v instanceof FloatValue);
			FloatValue fv = (FloatValue) v;
			assertTrue(fv.getMantissa().equals(IntegerValue.parse("1")));
			assertTrue(fv.getExponent().equals(IntegerValue.parse("0")));

			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.END_DOCUMENT);
			decoder.decodeEndDocument();
		}
	}

	// http://www.w3.org/TR/exi-c14n/#dt-float
	// -1230.01 --> -123001 -2
	public void testDatatypeFloat4() throws Exception {
		EXIFactory factory = DefaultEXIFactory.newInstance();

		factory.setFidelityOptions(FidelityOptions.createDefault());
		String schema = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ " <xs:element name='el1' type='xs:float'>"
				+ " </xs:element>"
				+ "</xs:schema>";

		Grammars g = GrammarTest.getGrammarFromSchemaAsString(schema);
		factory.setGrammars(g);
		factory.setCodingMode(CodingMode.BIT_PACKED);
		factory.getEncodingOptions().setOption(EncodingOptions.CANONICAL_EXI);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		QName s1 = new QName("", "el1");

		// encoder
		{
			EXIBodyEncoder encoder = factory.createEXIBodyEncoder();
			encoder.setOutputStream(baos);
			encoder.encodeStartDocument();
			encoder.encodeStartElement(s1.getNamespaceURI(), s1.getLocalPart(),
					null);
			encoder.encodeCharacters(new StringValue("-1230.01"));
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
			assertTrue(decoder.decodeStartElement().getQName().equals(s1));

			assertTrue(decoder.next() == EventType.CHARACTERS);
			Value v = decoder.decodeCharacters();
			assertTrue(v instanceof FloatValue);
			FloatValue fv = (FloatValue) v;
			assertTrue(fv.getMantissa().equals(IntegerValue.parse("-123001")));
			assertTrue(fv.getExponent().equals(IntegerValue.parse("-2")));

			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.END_DOCUMENT);
			decoder.decodeEndDocument();
		}
	}

	// http://www.w3.org/TR/exi-c14n/#dt-float
	// 0.1230 --> 123 -3
	public void testDatatypeFloat5() throws Exception {
		EXIFactory factory = DefaultEXIFactory.newInstance();

		factory.setFidelityOptions(FidelityOptions.createDefault());
		String schema = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ " <xs:element name='el1' type='xs:float'>"
				+ " </xs:element>"
				+ "</xs:schema>";

		Grammars g = GrammarTest.getGrammarFromSchemaAsString(schema);
		factory.setGrammars(g);
		factory.setCodingMode(CodingMode.BIT_PACKED);
		factory.getEncodingOptions().setOption(EncodingOptions.CANONICAL_EXI);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		QName s1 = new QName("", "el1");

		// encoder
		{
			EXIBodyEncoder encoder = factory.createEXIBodyEncoder();
			encoder.setOutputStream(baos);
			encoder.encodeStartDocument();
			encoder.encodeStartElement(s1.getNamespaceURI(), s1.getLocalPart(),
					null);
			encoder.encodeCharacters(new StringValue("0.1230"));
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
			assertTrue(decoder.decodeStartElement().getQName().equals(s1));

			assertTrue(decoder.next() == EventType.CHARACTERS);
			Value v = decoder.decodeCharacters();
			assertTrue(v instanceof FloatValue);
			FloatValue fv = (FloatValue) v;
			assertTrue(fv.getMantissa().equals(IntegerValue.parse("123")));
			assertTrue(fv.getExponent().equals(IntegerValue.parse("-3")));

			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.END_DOCUMENT);
			decoder.decodeEndDocument();
		}
	}

	// http://www.w3.org/TR/exi-c14n/#dt-float
	// 12300 --> 123 2
	public void testDatatypeFloat6() throws Exception {
		EXIFactory factory = DefaultEXIFactory.newInstance();

		factory.setFidelityOptions(FidelityOptions.createDefault());
		String schema = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ " <xs:element name='el1' type='xs:float'>"
				+ " </xs:element>"
				+ "</xs:schema>";

		Grammars g = GrammarTest.getGrammarFromSchemaAsString(schema);
		factory.setGrammars(g);
		factory.setCodingMode(CodingMode.BIT_PACKED);
		factory.getEncodingOptions().setOption(EncodingOptions.CANONICAL_EXI);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		QName s1 = new QName("", "el1");

		// encoder
		{
			EXIBodyEncoder encoder = factory.createEXIBodyEncoder();
			encoder.setOutputStream(baos);
			encoder.encodeStartDocument();
			encoder.encodeStartElement(s1.getNamespaceURI(), s1.getLocalPart(),
					null);
			encoder.encodeCharacters(new StringValue("12300"));
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
			assertTrue(decoder.decodeStartElement().getQName().equals(s1));

			assertTrue(decoder.next() == EventType.CHARACTERS);
			Value v = decoder.decodeCharacters();
			assertTrue(v instanceof FloatValue);
			FloatValue fv = (FloatValue) v;
			assertTrue(fv.getMantissa().equals(IntegerValue.parse("123")));
			assertTrue(fv.getExponent().equals(IntegerValue.parse("2")));

			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.END_DOCUMENT);
			decoder.decodeEndDocument();
		}
	}

	// http://www.w3.org/TR/exi-c14n/#dt-float
	// 12.0 --> 12 0
	public void testDatatypeFloat7() throws Exception {
		EXIFactory factory = DefaultEXIFactory.newInstance();

		factory.setFidelityOptions(FidelityOptions.createDefault());
		String schema = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ " <xs:element name='el1' type='xs:float'>"
				+ " </xs:element>"
				+ "</xs:schema>";

		Grammars g = GrammarTest.getGrammarFromSchemaAsString(schema);
		factory.setGrammars(g);
		factory.setCodingMode(CodingMode.BIT_PACKED);
		factory.getEncodingOptions().setOption(EncodingOptions.CANONICAL_EXI);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		QName s1 = new QName("", "el1");

		// encoder
		{
			EXIBodyEncoder encoder = factory.createEXIBodyEncoder();
			encoder.setOutputStream(baos);
			encoder.encodeStartDocument();
			encoder.encodeStartElement(s1.getNamespaceURI(), s1.getLocalPart(),
					null);
			encoder.encodeCharacters(new StringValue("12.0"));
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
			assertTrue(decoder.decodeStartElement().getQName().equals(s1));

			assertTrue(decoder.next() == EventType.CHARACTERS);
			Value v = decoder.decodeCharacters();
			assertTrue(v instanceof FloatValue);
			FloatValue fv = (FloatValue) v;
			assertTrue(fv.getMantissa().equals(IntegerValue.parse("12")));
			assertTrue(fv.getExponent().equals(IntegerValue.parse("0")));

			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.END_DOCUMENT);
			decoder.decodeEndDocument();
		}
	}

	// http://www.w3.org/TR/exi-c14n/#dt-float
	// 120E-1 --> 12 0
	public void testDatatypeFloat8() throws Exception {
		EXIFactory factory = DefaultEXIFactory.newInstance();

		factory.setFidelityOptions(FidelityOptions.createDefault());
		String schema = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ " <xs:element name='el1' type='xs:float'>"
				+ " </xs:element>"
				+ "</xs:schema>";

		Grammars g = GrammarTest.getGrammarFromSchemaAsString(schema);
		factory.setGrammars(g);
		factory.setCodingMode(CodingMode.BIT_PACKED);
		factory.getEncodingOptions().setOption(EncodingOptions.CANONICAL_EXI);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		QName s1 = new QName("", "el1");

		// encoder
		{
			EXIBodyEncoder encoder = factory.createEXIBodyEncoder();
			encoder.setOutputStream(baos);
			encoder.encodeStartDocument();
			encoder.encodeStartElement(s1.getNamespaceURI(), s1.getLocalPart(),
					null);
			encoder.encodeCharacters(new StringValue("120E-1"));
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
			assertTrue(decoder.decodeStartElement().getQName().equals(s1));

			assertTrue(decoder.next() == EventType.CHARACTERS);
			Value v = decoder.decodeCharacters();
			assertTrue(v instanceof FloatValue);
			FloatValue fv = (FloatValue) v;
			assertTrue(fv.getMantissa().equals(IntegerValue.parse("12")));
			assertTrue(fv.getExponent().equals(IntegerValue.parse("0")));

			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.END_DOCUMENT);
			decoder.decodeEndDocument();
		}
	}

	// http://www.w3.org/TR/exi-c14n/#dt-float
	// 1.2E1 --> 12 0
	public void testDatatypeFloat9() throws Exception {
		EXIFactory factory = DefaultEXIFactory.newInstance();

		factory.setFidelityOptions(FidelityOptions.createDefault());
		String schema = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ " <xs:element name='el1' type='xs:float'>"
				+ " </xs:element>"
				+ "</xs:schema>";

		Grammars g = GrammarTest.getGrammarFromSchemaAsString(schema);
		factory.setGrammars(g);
		factory.setCodingMode(CodingMode.BIT_PACKED);
		factory.getEncodingOptions().setOption(EncodingOptions.CANONICAL_EXI);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		QName s1 = new QName("", "el1");

		// encoder
		{
			EXIBodyEncoder encoder = factory.createEXIBodyEncoder();
			encoder.setOutputStream(baos);
			encoder.encodeStartDocument();
			encoder.encodeStartElement(s1.getNamespaceURI(), s1.getLocalPart(),
					null);
			encoder.encodeCharacters(new StringValue("1.2E1"));
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
			assertTrue(decoder.decodeStartElement().getQName().equals(s1));

			assertTrue(decoder.next() == EventType.CHARACTERS);
			Value v = decoder.decodeCharacters();
			assertTrue(v instanceof FloatValue);
			FloatValue fv = (FloatValue) v;
			assertTrue(fv.getMantissa().equals(IntegerValue.parse("12")));
			assertTrue(fv.getExponent().equals(IntegerValue.parse("0")));

			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.END_DOCUMENT);
			decoder.decodeEndDocument();
		}
	}

	// http://www.w3.org/TR/exi-c14n/#dt-dateTime
	// Note: used to be the same... now not anymore!!
	// String s1 = "2015-08-11T23:00:00+09:00";
	// String s2 = "2015-08-11T16:00:00+02:00";
	// String s3 = "2015-08-11T14:00:00Z"; // UTC
	public void testDatatypeDateTime0() throws Exception {
		EXIFactory factory = DefaultEXIFactory.newInstance();

		factory.setFidelityOptions(FidelityOptions.createDefault());
		String schema = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ " <xs:element name='el1' type='xs:dateTime'>"
				+ " </xs:element>" + "</xs:schema>";

		String sdt = "2015-08-11T23:00:00+09:00";

		Grammars g = GrammarTest.getGrammarFromSchemaAsString(schema);
		factory.setGrammars(g);
		factory.setCodingMode(CodingMode.BIT_PACKED);
		factory.getEncodingOptions().setOption(EncodingOptions.CANONICAL_EXI);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		QName s1 = new QName("", "el1");

		// encoder
		{
			EXIBodyEncoder encoder = factory.createEXIBodyEncoder();
			encoder.setOutputStream(baos);
			encoder.encodeStartDocument();
			encoder.encodeStartElement(s1.getNamespaceURI(), s1.getLocalPart(),
					null);
			encoder.encodeCharacters(new StringValue(sdt));
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
			assertTrue(decoder.decodeStartElement().getQName().equals(s1));

			assertTrue(decoder.next() == EventType.CHARACTERS);
			Value v = decoder.decodeCharacters();
			assertTrue(v instanceof DateTimeValue);
			DateTimeValue dtv = (DateTimeValue) v;
			// assertTrue(dtv.toString().equals("2015-08-11T14:00:00Z"));
			assertTrue(dtv.toString().equals(sdt));

			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.END_DOCUMENT);
			decoder.decodeEndDocument();
		}
	}

	public void testDatatypeDateTime1() throws Exception {
		EXIFactory factory = DefaultEXIFactory.newInstance();

		factory.setFidelityOptions(FidelityOptions.createDefault());
		String schema = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ " <xs:element name='el1' type='xs:date'>"
				+ " </xs:element>"
				+ "</xs:schema>";

		String sdt = "2003-10-30";

		Grammars g = GrammarTest.getGrammarFromSchemaAsString(schema);
		factory.setGrammars(g);
		factory.setCodingMode(CodingMode.BIT_PACKED);
		factory.getEncodingOptions().setOption(EncodingOptions.CANONICAL_EXI);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		QName s1 = new QName("", "el1");

		// encoder
		{
			EXIBodyEncoder encoder = factory.createEXIBodyEncoder();
			encoder.setOutputStream(baos);
			encoder.encodeStartDocument();
			encoder.encodeStartElement(s1.getNamespaceURI(), s1.getLocalPart(),
					null);
			encoder.encodeCharacters(new StringValue(sdt));
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
			assertTrue(decoder.decodeStartElement().getQName().equals(s1));

			assertTrue(decoder.next() == EventType.CHARACTERS);
			Value v = decoder.decodeCharacters();
			assertTrue(v instanceof DateTimeValue);
			DateTimeValue dtv = (DateTimeValue) v;
			assertTrue(dtv.toString().equals(sdt));

			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.END_DOCUMENT);
			decoder.decodeEndDocument();
		}
	}

	public void testDatatypeDateTime2() throws Exception {
		EXIFactory factory = DefaultEXIFactory.newInstance();

		factory.setFidelityOptions(FidelityOptions.createDefault());
		String schema = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ " <xs:element name='el1' type='xs:date'>"
				+ " </xs:element>"
				+ "</xs:schema>";

		String sdt = "2003-10-30Z";

		Grammars g = GrammarTest.getGrammarFromSchemaAsString(schema);
		factory.setGrammars(g);
		factory.setCodingMode(CodingMode.BIT_PACKED);
		factory.getEncodingOptions().setOption(EncodingOptions.CANONICAL_EXI);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		QName s1 = new QName("", "el1");

		// encoder
		{
			EXIBodyEncoder encoder = factory.createEXIBodyEncoder();
			encoder.setOutputStream(baos);
			encoder.encodeStartDocument();
			encoder.encodeStartElement(s1.getNamespaceURI(), s1.getLocalPart(),
					null);
			encoder.encodeCharacters(new StringValue(sdt));
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
			assertTrue(decoder.decodeStartElement().getQName().equals(s1));

			assertTrue(decoder.next() == EventType.CHARACTERS);
			Value v = decoder.decodeCharacters();
			assertTrue(v instanceof DateTimeValue);
			DateTimeValue dtv = (DateTimeValue) v;
			assertTrue(dtv.toString().equals(sdt));

			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.END_DOCUMENT);
			decoder.decodeEndDocument();
		}
	}
	
	public void testStreamHeaderEXIOptions0() throws Exception {
		EXIFactory factory = DefaultEXIFactory.newInstance();
		factory.getEncodingOptions().setOption(EncodingOptions.CANONICAL_EXI_WITHOUT_EXI_OPTIONS);

		String xml = "<foo>" + "text content" + "</foo>";

		// encode to EXI
		TestSAXEncoder enc = new TestSAXEncoder(factory);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		enc.encodeTo(new ByteArrayInputStream(xml.getBytes()), baos);

		// decode and check
		InputStream isCan = new ByteArrayInputStream(baos.toByteArray());
		BitDecoderChannel bdc = new BitDecoderChannel(isCan);
		assertTrue("Distinguishing Bits", bdc.decodeNBitUnsignedInteger(2) == 2); // Distinguishing Bits
		assertTrue("Presence Bit for EXI Options", bdc.decodeNBitUnsignedInteger(1) == 0); // Presence Bit for EXI Options
	}
	
	public void testStreamHeaderEXIOptions1() throws Exception {
		EXIFactory factory = DefaultEXIFactory.newInstance();
		factory.getEncodingOptions().setOption(EncodingOptions.CANONICAL_EXI);

		String xml = "<foo>" + "text content" + "</foo>";

		// encode to EXI
		TestSAXEncoder enc = new TestSAXEncoder(factory);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		enc.encodeTo(new ByteArrayInputStream(xml.getBytes()), baos);

		// decode and check
		InputStream isCan = new ByteArrayInputStream(baos.toByteArray());
		BitDecoderChannel bdc = new BitDecoderChannel(isCan);
		assertTrue("Distinguishing Bits", bdc.decodeNBitUnsignedInteger(2) == 2); // Distinguishing Bits
		assertTrue("Presence Bit for EXI Options", bdc.decodeNBitUnsignedInteger(1) == 1); // Presence Bit for EXI Options
	}
	

	// header MUST include EXI Options
	public void testStreamHeader0() throws Exception {
		EXIFactory factory = DefaultEXIFactory.newInstance();
		factory.getEncodingOptions().setOption(EncodingOptions.CANONICAL_EXI);

		String xml = "<foo>" + "text content" + "</foo>";

		// encode to EXI
		TestSAXEncoder enc = new TestSAXEncoder(factory);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		enc.encodeTo(new ByteArrayInputStream(xml.getBytes()), baos);

		// decode and check
		InputStream isCan = new ByteArrayInputStream(baos.toByteArray());
		EXIFactory noOptionsFactory = DefaultEXIFactory.newInstance();
		noOptionsFactory.setCodingMode(CodingMode.COMPRESSION); // wrong
																// setting,
																// stream should
																// contain the
																// right options
		EXIStreamDecoder sdec = new EXIStreamDecoder(noOptionsFactory);
		EXIBodyDecoder bdec = sdec.decodeHeader(isCan);
		assertTrue(bdec instanceof EXIBodyDecoderInOrder);
		EXIBodyDecoderInOrder bdec2 = (EXIBodyDecoderInOrder) bdec;
		assertTrue(bdec2.exiFactory != noOptionsFactory);

		bdec2.exiFactory.createEXIReader();
	}

	// When the alignment option compression is set, pre-compress MUST be used
	// instead of compression.
	public void testStreamHeader1() throws Exception {
		EXIFactory factory = DefaultEXIFactory.newInstance();
		factory.getEncodingOptions().setOption(EncodingOptions.CANONICAL_EXI);
		factory.setCodingMode(CodingMode.COMPRESSION);

		String xml = "<foo>" + "text content" + "</foo>";

		// encode to EXI
		TestSAXEncoder enc = new TestSAXEncoder(factory);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		enc.encodeTo(new ByteArrayInputStream(xml.getBytes()), baos);

		// decode and check
		InputStream isCan = new ByteArrayInputStream(baos.toByteArray());
		EXIFactory noOptionsFactory = DefaultEXIFactory.newInstance();
		noOptionsFactory.setCodingMode(CodingMode.BYTE_PACKED); // wrong
																// setting,
																// stream should
																// contain the
																// right options
		EXIStreamDecoder sdec = new EXIStreamDecoder(noOptionsFactory);
		EXIBodyDecoder bdec = sdec.decodeHeader(isCan);
		assertTrue(bdec instanceof EXIBodyDecoderReordered);
		EXIBodyDecoderReordered bdec2 = (EXIBodyDecoderReordered) bdec;
		assertTrue(bdec2.exiFactory != noOptionsFactory);
		assertTrue(bdec2.exiFactory.getCodingMode() == CodingMode.PRE_COMPRESSION);
	}

	// datatypeRepresentationMap: the tuples are to be sorted lexicographically
	// according to the schema datatype first by {name} then by {namespace}
	public void testStreamHeader2() throws Exception {
		EXIFactory factory = DefaultEXIFactory.newInstance();
		factory.getEncodingOptions().setOption(EncodingOptions.CANONICAL_EXI);

		/* DTR Map */
		QName type1 = new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "decimal");
		QName representation1 = new QName(Constants.W3C_EXI_NS_URI, "string");
		QName type2 = new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "boolean");
		QName representation2 = new QName(Constants.W3C_EXI_NS_URI, "integer");
		QName[] dtrMapTypes = { type1, type2 };
		QName[] dtrMapRepresentations = { representation1, representation2 };
		factory.setDatatypeRepresentationMap(dtrMapTypes, dtrMapRepresentations);

		String xml = "<foo>" + "text content" + "</foo>";

		// encode to EXI
		TestSAXEncoder enc = new TestSAXEncoder(factory);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		enc.encodeTo(new ByteArrayInputStream(xml.getBytes()), baos);

		// decode and check
		InputStream isCan = new ByteArrayInputStream(baos.toByteArray());
		EXIFactory noOptionsFactory = DefaultEXIFactory.newInstance();
		noOptionsFactory.setCodingMode(CodingMode.COMPRESSION); // wrong
																// setting,
																// stream should
																// contain the
																// right options
		EXIStreamDecoder sdec = new EXIStreamDecoder(noOptionsFactory);
		EXIBodyDecoder bdec = sdec.decodeHeader(isCan);
		assertTrue(bdec instanceof EXIBodyDecoderInOrder);
		EXIBodyDecoderInOrder bdec2 = (EXIBodyDecoderInOrder) bdec;
		assertTrue(bdec2.exiFactory != noOptionsFactory);

		assertTrue(bdec2.exiFactory.getDatatypeRepresentationMapTypes().length == 2);
		assertTrue(bdec2.exiFactory.getDatatypeRepresentationMapTypes()[0]
				.getLocalPart().equals("boolean"));
		assertTrue(bdec2.exiFactory
				.getDatatypeRepresentationMapRepresentations()[0]
				.getLocalPart().equals("integer"));
		assertTrue(bdec2.exiFactory.getDatatypeRepresentationMapTypes()[1]
				.getLocalPart().equals("decimal"));
		assertTrue(bdec2.exiFactory
				.getDatatypeRepresentationMapRepresentations()[1]
				.getLocalPart().equals("string"));

	}

}