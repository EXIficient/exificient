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

package com.siemens.ct.exi.main.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.namespace.QName;

import junit.framework.TestCase;

import org.xml.sax.SAXException;

import com.siemens.ct.exi.core.CodingMode;
import com.siemens.ct.exi.core.Constants;
import com.siemens.ct.exi.core.EXIBodyDecoder;
import com.siemens.ct.exi.core.EXIBodyEncoder;
import com.siemens.ct.exi.core.EXIFactory;
import com.siemens.ct.exi.core.EXIStreamDecoder;
import com.siemens.ct.exi.core.EncodingOptions;
import com.siemens.ct.exi.core.FidelityOptions;
import com.siemens.ct.exi.core.attributes.AttributeFactory;
import com.siemens.ct.exi.core.attributes.AttributeList;
import com.siemens.ct.exi.core.coder.EXIBodyDecoderInOrder;
import com.siemens.ct.exi.core.coder.EXIBodyDecoderReordered;
import com.siemens.ct.exi.core.exceptions.EXIException;
import com.siemens.ct.exi.core.grammars.Grammars;
import com.siemens.ct.exi.core.grammars.event.EventType;
import com.siemens.ct.exi.core.helpers.DefaultEXIFactory;
import com.siemens.ct.exi.core.io.channel.BitDecoderChannel;
import com.siemens.ct.exi.core.values.BinaryBase64Value;
import com.siemens.ct.exi.core.values.DateTimeValue;
import com.siemens.ct.exi.core.values.DecimalValue;
import com.siemens.ct.exi.core.values.FloatValue;
import com.siemens.ct.exi.core.values.IntegerValue;
import com.siemens.ct.exi.core.values.StringValue;
import com.siemens.ct.exi.core.values.Value;
import com.siemens.ct.exi.grammars.GrammarFactory;
import com.siemens.ct.exi.main.TestSAXEncoder;
import com.siemens.ct.exi.main.api.sax.SAXFactory;

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
			al.addNamespaceDeclaration(Constants.XML_SCHEMA_NS_URI,
					"xsd");
			al.addAttribute(new QName(
					Constants.XML_SCHEMA_INSTANCE_NS_URI, "type"),
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
							Constants.XML_SCHEMA_INSTANCE_NS_URI, "type"));
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

		Grammars g = SchemaInformedTest.getGrammarFromSchemaAsString(schema);
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

		Grammars g = SchemaInformedTest.getGrammarFromSchemaAsString(schema);
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

		Grammars g = SchemaInformedTest.getGrammarFromSchemaAsString(schema);
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

		Grammars g = SchemaInformedTest.getGrammarFromSchemaAsString(schema);
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

		Grammars g = SchemaInformedTest.getGrammarFromSchemaAsString(schema);
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

		Grammars g = SchemaInformedTest.getGrammarFromSchemaAsString(schema);
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

		Grammars g = SchemaInformedTest.getGrammarFromSchemaAsString(schema);
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

		Grammars g = SchemaInformedTest.getGrammarFromSchemaAsString(schema);
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

		Grammars g = SchemaInformedTest.getGrammarFromSchemaAsString(schema);
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

		Grammars g = SchemaInformedTest.getGrammarFromSchemaAsString(schema);
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

		Grammars g = SchemaInformedTest.getGrammarFromSchemaAsString(schema);
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

		Grammars g = SchemaInformedTest.getGrammarFromSchemaAsString(schema);
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

		Grammars g = SchemaInformedTest.getGrammarFromSchemaAsString(schema);
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

		Grammars g = SchemaInformedTest.getGrammarFromSchemaAsString(schema);
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

		Grammars g = SchemaInformedTest.getGrammarFromSchemaAsString(schema);
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

		Grammars g = SchemaInformedTest.getGrammarFromSchemaAsString(schema);
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
			assertTrue(dtv.year == 2003);
			assertTrue(dtv.monthDay == (10 * 32 + 30)); // Month * 32 + Day
			assertTrue(dtv.presenceTimezone == true);
			assertTrue(dtv.timezone == 0);

			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.END_DOCUMENT);
			decoder.decodeEndDocument();
		}
	}

	// not adding any timezone
	public void testDatatypeDateTime3() throws Exception {
		EXIFactory factory = DefaultEXIFactory.newInstance();

		factory.setFidelityOptions(FidelityOptions.createDefault());
		String schema = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ " <xs:element name='el1' type='xs:date'>"
				+ " </xs:element>"
				+ "</xs:schema>";

		String sdt = "2003-10-30";

		Grammars g = SchemaInformedTest.getGrammarFromSchemaAsString(schema);
		factory.setGrammars(g);
		factory.setFidelityOptions(FidelityOptions.createStrict());
		factory.setCodingMode(CodingMode.BIT_PACKED);
		// factory.getEncodingOptions().setOption(EncodingOptions.CANONICAL_EXI);

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
			assertTrue(dtv.year == 2003);
			assertTrue(dtv.monthDay == (10 * 32 + 30)); // Month * 32 + Day
			assertTrue(dtv.presenceTimezone == false);

			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.END_DOCUMENT);
			decoder.decodeEndDocument();
		}
	}
	
	
	// http://www.w3.org/TR/exi-c14n/#dt-dateTime
	// Note: Should be the same after normalizing!!
	// String s1 = "2015-08-11T23:00:00+09:00";
	// String s2 = "2015-08-11T16:00:00+02:00";
	// String s3 = "2015-08-11T14:00:00Z"; // UTC
	public void testDatatypeDateTime4() throws Exception {
		EXIFactory factory = DefaultEXIFactory.newInstance();

		factory.setFidelityOptions(FidelityOptions.createDefault());
		String schema = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ " <xs:element name='el1' type='xs:dateTime'>"
				+ " </xs:element>" + "</xs:schema>";

		String sdt = "2015-08-11T23:00:00+09:00";
		String sdtUTC = "2015-08-11T14:00:00Z";

		Grammars g = SchemaInformedTest.getGrammarFromSchemaAsString(schema);
		factory.setGrammars(g);
		factory.setCodingMode(CodingMode.BIT_PACKED);
		factory.getEncodingOptions().setOption(EncodingOptions.CANONICAL_EXI);
		factory.getEncodingOptions().setOption(EncodingOptions.UTC_TIME);

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
			assertTrue(dtv.toString().equals(sdtUTC));

			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.END_DOCUMENT);
			decoder.decodeEndDocument();
		}
	}
	
	// http://www.w3.org/TR/exi-c14n/#dt-dateTime
	// Note: Should be the same after normalizing!!
	// String s1 = "2015-08-11T23:00:00+09:00";
	// String s2 = "2015-08-11T16:00:00+02:00";
	// String s3 = "2015-08-11T14:00:00Z"; // UTC
	public void testDatatypeDateTime5() throws Exception {
		EXIFactory factory = DefaultEXIFactory.newInstance();

		factory.setFidelityOptions(FidelityOptions.createDefault());
		String schema = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ " <xs:element name='el1' type='xs:dateTime'>"
				+ " </xs:element>" + "</xs:schema>";

		String sdt = "2015-08-11T16:00:00+02:00";
		String sdtUTC = "2015-08-11T14:00:00Z";

		Grammars g = SchemaInformedTest.getGrammarFromSchemaAsString(schema);
		factory.setGrammars(g);
		factory.setCodingMode(CodingMode.BIT_PACKED);
		factory.getEncodingOptions().setOption(EncodingOptions.CANONICAL_EXI);
		factory.getEncodingOptions().setOption(EncodingOptions.UTC_TIME);

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
			assertTrue(dtv.toString().equals(sdtUTC));

			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.END_DOCUMENT);
			decoder.decodeEndDocument();
		}
	}
	
	// The 2-digit numeral representing the hour must not be '24';
	public void testDatatypeDateTime6() throws Exception {
		EXIFactory factory = DefaultEXIFactory.newInstance();

		factory.setFidelityOptions(FidelityOptions.createDefault());
		String schema = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ " <xs:element name='el1' type='xs:dateTime'>"
				+ " </xs:element>" + "</xs:schema>";

		String sdt = "2015-08-11T24:00:00";
		String sdtUTC = "2015-08-12T00:00:00";

		Grammars g = SchemaInformedTest.getGrammarFromSchemaAsString(schema);
		factory.setGrammars(g);
		factory.setCodingMode(CodingMode.BIT_PACKED);
		factory.getEncodingOptions().setOption(EncodingOptions.CANONICAL_EXI);
		factory.getEncodingOptions().setOption(EncodingOptions.UTC_TIME);

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
			assertTrue(dtv.toString().equals(sdtUTC));

			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.END_DOCUMENT);
			decoder.decodeEndDocument();
		}
	}
	
	
	// date overflow
	public void testDatatypeDateTime7() throws Exception {
		EXIFactory factory = DefaultEXIFactory.newInstance();

		factory.setFidelityOptions(FidelityOptions.createDefault());
		String schema = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ " <xs:element name='el1' type='xs:dateTime'>"
				+ " </xs:element>" + "</xs:schema>";

		String sdt = "2012-06-30T23:59:60-06:00";
		String sdtUTC = "2012-07-01T06:00:00Z";

		Grammars g = SchemaInformedTest.getGrammarFromSchemaAsString(schema);
		factory.setGrammars(g);
		factory.setCodingMode(CodingMode.BIT_PACKED);
		factory.getEncodingOptions().setOption(EncodingOptions.CANONICAL_EXI);
		factory.getEncodingOptions().setOption(EncodingOptions.UTC_TIME);

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
			assertTrue(dtv.toString().equals(sdtUTC));

			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.END_DOCUMENT);
			decoder.decodeEndDocument();
		}
	}

	public void testStreamHeaderEXIOptions0() throws Exception {
		EXIFactory factory = DefaultEXIFactory.newInstance();
		factory.getEncodingOptions().setOption(EncodingOptions.CANONICAL_EXI);
		// Note: without EXI options
		// factory.getEncodingOptions().setOption(EncodingOptions.INCLUDE_OPTIONS);

		String xml = "<foo>" + "text content" + "</foo>";

		// encode to EXI
		TestSAXEncoder enc = new TestSAXEncoder(factory);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		enc.encodeTo(new ByteArrayInputStream(xml.getBytes()), baos);

		// decode and check
		InputStream isCan = new ByteArrayInputStream(baos.toByteArray());
		BitDecoderChannel bdc = new BitDecoderChannel(isCan);
		assertTrue("Distinguishing Bits", bdc.decodeNBitUnsignedInteger(2) == 2); // Distinguishing
																					// Bits
		// Note: by default the Canonical EXI Option "omitOptionsDocument" is false
		assertTrue("Presence Bit for EXI Options",
				bdc.decodeNBitUnsignedInteger(1) == 1); // Presence Bit for EXI
														// Options
	}

	public void testStreamHeaderEXIOptions1() throws Exception {
		EXIFactory factory = DefaultEXIFactory.newInstance();
		factory.getEncodingOptions().setOption(EncodingOptions.CANONICAL_EXI);
		// Note: without EXI options
		boolean b= factory.getEncodingOptions().unsetOption(EncodingOptions.INCLUDE_OPTIONS);
		assertTrue("INCLUDE_OPTIONS should have been there", b);

		String xml = "<foo>" + "text content" + "</foo>";

		// encode to EXI
		TestSAXEncoder enc = new TestSAXEncoder(factory);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		enc.encodeTo(new ByteArrayInputStream(xml.getBytes()), baos);

		// decode and check
		InputStream isCan = new ByteArrayInputStream(baos.toByteArray());
		BitDecoderChannel bdc = new BitDecoderChannel(isCan);
		assertTrue("Distinguishing Bits", bdc.decodeNBitUnsignedInteger(2) == 2); // Distinguishing
																					// Bits
		assertTrue("Presence Bit for EXI Options",
				bdc.decodeNBitUnsignedInteger(1) == 0); // Presence Bit for EXI
														// Options
	}

	// header MUST include EXI Options
	public void testStreamHeader0() throws Exception {
		EXIFactory factory = DefaultEXIFactory.newInstance();
		factory.getEncodingOptions().setOption(EncodingOptions.CANONICAL_EXI);
		// Note: with EXI options
		factory.getEncodingOptions().setOption(EncodingOptions.INCLUDE_OPTIONS);

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
		EXIStreamDecoder sdec = noOptionsFactory.createEXIStreamDecoder();
		EXIBodyDecoder bdec = sdec.decodeHeader(isCan);
		assertTrue(bdec instanceof EXIBodyDecoderInOrder);
		EXIBodyDecoderInOrder bdec2 = (EXIBodyDecoderInOrder) bdec;
		assertTrue(bdec2.exiFactory != noOptionsFactory);

		new SAXFactory(bdec2.exiFactory).createEXIReader();
	}

	// When the alignment option compression is set, pre-compress MUST be used
	// instead of compression.
	public void testStreamHeader1() throws Exception {
		EXIFactory factory = DefaultEXIFactory.newInstance();
		factory.getEncodingOptions().setOption(EncodingOptions.CANONICAL_EXI);
		// Note: with EXI options
		factory.getEncodingOptions().setOption(EncodingOptions.INCLUDE_OPTIONS);
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
		EXIStreamDecoder sdec = noOptionsFactory.createEXIStreamDecoder();
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
		// Note: with EXI options
		factory.getEncodingOptions().setOption(EncodingOptions.INCLUDE_OPTIONS);

		/* DTR Map */
		QName type1 = new QName(Constants.XML_SCHEMA_NS_URI, "decimal");
		QName representation1 = new QName(Constants.W3C_EXI_NS_URI, "string");
		QName type2 = new QName(Constants.XML_SCHEMA_NS_URI, "boolean");
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
		EXIStreamDecoder sdec = noOptionsFactory.createEXIStreamDecoder();
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

	// datatypeRepresentationMap: When the value of the Preserve.lexicalValues
	// fidelity option is true the element datatypeRepresentationMap MUST be
	// omitted.
	public void testStreamHeader3() throws Exception {
		EXIFactory factory = DefaultEXIFactory.newInstance();
		factory.getEncodingOptions().setOption(EncodingOptions.CANONICAL_EXI);
		// Note: with EXI options
		factory.getEncodingOptions().setOption(EncodingOptions.INCLUDE_OPTIONS);
		factory.getFidelityOptions().setFidelity(
				FidelityOptions.FEATURE_LEXICAL_VALUE, true);

		/* DTR Map */
		QName type1 = new QName(Constants.XML_SCHEMA_NS_URI, "decimal");
		QName representation1 = new QName(Constants.W3C_EXI_NS_URI, "string");
		QName type2 = new QName(Constants.XML_SCHEMA_NS_URI, "boolean");
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
		EXIStreamDecoder sdec = noOptionsFactory.createEXIStreamDecoder();
		EXIBodyDecoder bdec = sdec.decodeHeader(isCan);
		assertTrue(bdec instanceof EXIBodyDecoderInOrder);
		EXIBodyDecoderInOrder bdec2 = (EXIBodyDecoderInOrder) bdec;
		assertTrue(bdec2.exiFactory != noOptionsFactory);

		assertTrue(bdec2.exiFactory.getDatatypeRepresentationMapTypes() == null
				|| bdec2.exiFactory.getDatatypeRepresentationMapTypes().length == 0);
		assertTrue(bdec2.exiFactory
				.getDatatypeRepresentationMapRepresentations() == null
				|| bdec2.exiFactory
						.getDatatypeRepresentationMapRepresentations().length == 0);

	}

	public void testEmptyCharactersSchemaInformedString1() throws Exception {
		EXIFactory factory = DefaultEXIFactory.newInstance();

		factory.setFidelityOptions(FidelityOptions.createDefault());
		String schema = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ " <xs:element name='el1' type='xs:string'>"
				+ " </xs:element>" + "</xs:schema>";

		Grammars g = SchemaInformedTest.getGrammarFromSchemaAsString(schema);
		factory.setGrammars(g);

		String xml = "<el1></el1>";

		// encode to EXI
		TestSAXEncoder enc = new TestSAXEncoder(factory);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		enc.encodeTo(new ByteArrayInputStream(xml.getBytes()), baos);

		// decoder
		{
			EXIStreamDecoder sdec = factory.createEXIStreamDecoder();
			EXIBodyDecoder decoder = sdec
					.decodeHeader(new ByteArrayInputStream(baos.toByteArray()));

			assertTrue(decoder.next() == EventType.START_DOCUMENT);
			decoder.decodeStartDocument();

			assertTrue(decoder.next() == EventType.START_ELEMENT);
			assertTrue(decoder.decodeStartElement().getQName().getLocalPart()
					.equals("el1"));

			if (newEmptyChStrategy) {
				assertTrue(decoder.next() == EventType.END_ELEMENT_UNDECLARED);
			} else {
				assertTrue(decoder.next() == EventType.CHARACTERS);
				Value v = decoder.decodeCharacters();
				assertTrue(v instanceof StringValue);
				StringValue dtv = (StringValue) v;
				assertTrue(dtv.toString().equals(""));

				assertTrue(decoder.next() == EventType.END_ELEMENT);
			}
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.END_DOCUMENT);
			decoder.decodeEndDocument();
		}
	}

	final boolean newEmptyChStrategy = true;

	public void testEmptyCharactersSchemaInformedBinary1() throws Exception {

		EXIFactory factory = DefaultEXIFactory.newInstance();

		factory.setFidelityOptions(FidelityOptions.createDefault());
		String schema = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ " <xs:element name='el1' type='xs:base64Binary'>"
				+ " </xs:element>" + "</xs:schema>";

		Grammars g = SchemaInformedTest.getGrammarFromSchemaAsString(schema);
		factory.setGrammars(g);

		String xml = "<el1></el1>";

		// encode to EXI
		TestSAXEncoder enc = new TestSAXEncoder(factory);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		enc.encodeTo(new ByteArrayInputStream(xml.getBytes()), baos);

		// decoder
		{
			EXIStreamDecoder sdec = factory.createEXIStreamDecoder();
			EXIBodyDecoder decoder = sdec
					.decodeHeader(new ByteArrayInputStream(baos.toByteArray()));

			assertTrue(decoder.next() == EventType.START_DOCUMENT);
			decoder.decodeStartDocument();

			assertTrue(decoder.next() == EventType.START_ELEMENT);
			assertTrue(decoder.decodeStartElement().getQName().getLocalPart()
					.equals("el1"));

			if (newEmptyChStrategy) {
				assertTrue(decoder.next() == EventType.END_ELEMENT_UNDECLARED);
			} else {
				assertTrue(decoder.next() == EventType.CHARACTERS);
				Value v = decoder.decodeCharacters();
				assertTrue(v instanceof BinaryBase64Value);
				BinaryBase64Value dtv = (BinaryBase64Value) v;
				assertTrue(dtv.toString().equals(""));
				assertTrue(decoder.next() == EventType.END_ELEMENT);
			}
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.END_DOCUMENT);
			decoder.decodeEndDocument();
		}
	}

	public void testEmptyCharactersSchemaInformedEnumeration1()
			throws Exception {

		EXIFactory factory = DefaultEXIFactory.newInstance();

		factory.setFidelityOptions(FidelityOptions.createDefault());
		String schema = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ " <xs:element name='el1'>"
				+ "    <xs:simpleType>"
				+ "       <xs:restriction base='xs:string'>"
				+ "         <xs:enumeration value='A'/>"
				+ "         <xs:enumeration value=''/>"
				+ "       </xs:restriction>"
				+ "   </xs:simpleType>"
				+ " </xs:element>" + "</xs:schema>";

		Grammars g = SchemaInformedTest.getGrammarFromSchemaAsString(schema);
		factory.setGrammars(g);

		String xml = "<el1></el1>";

		// encode to EXI
		TestSAXEncoder enc = new TestSAXEncoder(factory);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		enc.encodeTo(new ByteArrayInputStream(xml.getBytes()), baos);

		// decoder
		{
			EXIStreamDecoder sdec = factory.createEXIStreamDecoder();
			EXIBodyDecoder decoder = sdec
					.decodeHeader(new ByteArrayInputStream(baos.toByteArray()));

			assertTrue(decoder.next() == EventType.START_DOCUMENT);
			decoder.decodeStartDocument();

			assertTrue(decoder.next() == EventType.START_ELEMENT);
			assertTrue(decoder.decodeStartElement().getQName().getLocalPart()
					.equals("el1"));

			if (newEmptyChStrategy) {
				assertTrue(decoder.next() == EventType.END_ELEMENT_UNDECLARED);
			} else {
				assertTrue(decoder.next() == EventType.CHARACTERS);
				Value v = decoder.decodeCharacters();
				assertTrue(v instanceof StringValue);
				StringValue dtv = (StringValue) v;
				assertTrue(dtv.toString().equals(""));

				assertTrue(decoder.next() == EventType.END_ELEMENT);
			}
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.END_DOCUMENT);
			decoder.decodeEndDocument();
		}
	}

	public void testEmptyCharactersSchemaInformedEnumerationFail1()
			throws Exception {

		EXIFactory factory = DefaultEXIFactory.newInstance();

		factory.setFidelityOptions(FidelityOptions.createDefault());
		String schema = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ " <xs:element name='el1'>"
				+ "    <xs:simpleType>"
				+ "       <xs:restriction base='xs:string'>"
				+ "         <xs:enumeration value='A'/>"
				+ "         <xs:enumeration value='B'/>"
				+ "       </xs:restriction>"
				+ "   </xs:simpleType>"
				+ " </xs:element>" + "</xs:schema>";

		Grammars g = SchemaInformedTest.getGrammarFromSchemaAsString(schema);
		factory.setGrammars(g);

		String xml = "<el1></el1>";

		// encode to EXI
		TestSAXEncoder enc = new TestSAXEncoder(factory);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		enc.encodeTo(new ByteArrayInputStream(xml.getBytes()), baos);

		// decoder
		{
			EXIStreamDecoder sdec = factory.createEXIStreamDecoder();
			EXIBodyDecoder decoder = sdec
					.decodeHeader(new ByteArrayInputStream(baos.toByteArray()));

			assertTrue(decoder.next() == EventType.START_DOCUMENT);
			decoder.decodeStartDocument();

			assertTrue(decoder.next() == EventType.START_ELEMENT);
			assertTrue(decoder.decodeStartElement().getQName().getLocalPart()
					.equals("el1"));

			// Note: no CH --> deviation

			assertTrue(decoder.next() == EventType.END_ELEMENT_UNDECLARED);
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.END_DOCUMENT);
			decoder.decodeEndDocument();
		}
	}

	public void testEmptyCharactersSchemaInformedContent1() throws Exception {

		EXIFactory factory = DefaultEXIFactory.newInstance();

		factory.setFidelityOptions(FidelityOptions.createDefault());
		String schema = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ " <xs:element name='root'>"
				+ "    <xs:complexType>"
				+ "        <xs:sequence minOccurs=\"0\" maxOccurs=\"unbounded\">"
				+ "            <xs:element name=\"entry\" type=\"xs:string\" />"
				+ "        </xs:sequence>"
				+ "    </xs:complexType>"
				+ " </xs:element>" + "</xs:schema>";

		Grammars g = SchemaInformedTest.getGrammarFromSchemaAsString(schema);
		factory.setGrammars(g);

		String xml = "<root>   <!-- no entry element at all -->     </root>";

		// encode to EXI
		TestSAXEncoder enc = new TestSAXEncoder(factory);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		enc.encodeTo(new ByteArrayInputStream(xml.getBytes()), baos);

		// decoder
		{
			EXIStreamDecoder sdec = factory.createEXIStreamDecoder();
			EXIBodyDecoder decoder = sdec
					.decodeHeader(new ByteArrayInputStream(baos.toByteArray()));

			assertTrue(decoder.next() == EventType.START_DOCUMENT);
			decoder.decodeStartDocument();

			assertTrue(decoder.next() == EventType.START_ELEMENT);
			assertTrue(decoder.decodeStartElement().getQName().getLocalPart()
					.equals("root"));

			// Note: simple data --> preserve empty CH
			assertTrue(decoder.next() == EventType.CHARACTERS_GENERIC_UNDECLARED);
			String s = decoder.decodeCharacters().toString();
			assertTrue("Not only WS characters", s.trim().length() == 0);

			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.END_DOCUMENT);
			decoder.decodeEndDocument();
		}
	}

	public void testEmptyCharactersSchemaInformedComplexMixedContent1()
			throws Exception {

		EXIFactory factory = DefaultEXIFactory.newInstance();

		factory.setFidelityOptions(FidelityOptions.createDefault());
		String schema = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ " <xs:element name='el1'>"
				+ "    <xs:complexType mixed='true' />"
				+ " </xs:element>"
				+ "</xs:schema>";

		Grammars g = SchemaInformedTest.getGrammarFromSchemaAsString(schema);
		factory.setGrammars(g);

		String xml = "<el1></el1>";

		// encode to EXI
		TestSAXEncoder enc = new TestSAXEncoder(factory);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		enc.encodeTo(new ByteArrayInputStream(xml.getBytes()), baos);

		// decoder
		{
			EXIStreamDecoder sdec = factory.createEXIStreamDecoder();
			EXIBodyDecoder decoder = sdec
					.decodeHeader(new ByteArrayInputStream(baos.toByteArray()));

			assertTrue(decoder.next() == EventType.START_DOCUMENT);
			decoder.decodeStartDocument();

			assertTrue(decoder.next() == EventType.START_ELEMENT);
			assertTrue(decoder.decodeStartElement().getQName().getLocalPart()
					.equals("el1"));

			// Note: no empty CH for mixed content

			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.END_DOCUMENT);
			decoder.decodeEndDocument();
		}
	}

	public void testEmptyCharactersSchemaInformedComplexMixedContent2()
			throws Exception {

		EXIFactory factory = DefaultEXIFactory.newInstance();

		factory.setFidelityOptions(FidelityOptions.createDefault());
		String schema = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ " <xs:element name='el1'>"
				+ "    <xs:complexType mixed='true'>"
				+ "      <xs:sequence>"
				+ "        <xs:element name='t' type='xs:string' minOccurs='0'/>"
				+ "        <xs:element name='v' type='xs:string' minOccurs='0'/>"
				+ "      </xs:sequence>"
				+ "    </xs:complexType>"
				+ " </xs:element>" + "</xs:schema>";

		Grammars g = SchemaInformedTest.getGrammarFromSchemaAsString(schema);
		factory.setGrammars(g);

		String xml = "<el1><t>bla</t> X <v>foo</v></el1>";

		// encode to EXI
		TestSAXEncoder enc = new TestSAXEncoder(factory);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		enc.encodeTo(new ByteArrayInputStream(xml.getBytes()), baos);

		// decoder
		{
			EXIStreamDecoder sdec = factory.createEXIStreamDecoder();
			EXIBodyDecoder decoder = sdec
					.decodeHeader(new ByteArrayInputStream(baos.toByteArray()));

			assertTrue(decoder.next() == EventType.START_DOCUMENT);
			decoder.decodeStartDocument();

			assertTrue(decoder.next() == EventType.START_ELEMENT);
			assertTrue(decoder.decodeStartElement().getQName().getLocalPart()
					.equals("el1"));

			// Note: no empty CH for mixed content
			{
				assertTrue(decoder.next() == EventType.START_ELEMENT);
				assertTrue(decoder.decodeStartElement().getQName()
						.getLocalPart().equals("t"));

				assertTrue(decoder.next() == EventType.CHARACTERS);
				Value v = decoder.decodeCharacters();
				assertTrue(v instanceof StringValue);
				StringValue dtv = (StringValue) v;
				assertTrue(dtv.toString().equals("bla"));

				assertTrue(decoder.next() == EventType.END_ELEMENT);
				decoder.decodeEndElement();
			}

			assertTrue(decoder.next() == EventType.CHARACTERS_GENERIC);
			Value vX = decoder.decodeCharacters();
			assertTrue(vX instanceof StringValue);
			StringValue dtvX = (StringValue) vX;
			assertTrue(dtvX.toString().equals(" X "));

			{
				assertTrue(decoder.next() == EventType.START_ELEMENT);
				assertTrue(decoder.decodeStartElement().getQName()
						.getLocalPart().equals("v"));

				assertTrue(decoder.next() == EventType.CHARACTERS);
				Value v = decoder.decodeCharacters();
				assertTrue(v instanceof StringValue);
				StringValue dtv = (StringValue) v;
				assertTrue(dtv.toString().equals("foo"));

				assertTrue(decoder.next() == EventType.END_ELEMENT);
				decoder.decodeEndElement();
			}

			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.END_DOCUMENT);
			decoder.decodeEndDocument();
		}
	}

	public void testEmptyCharactersSchemaInformedComplexMixedContent3()
			throws Exception {

		EXIFactory factory = DefaultEXIFactory.newInstance();

		factory.setFidelityOptions(FidelityOptions.createDefault());
		String schema = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ " <xs:element name='el1'>"
				+ "    <xs:complexType mixed='true'>"
				+ "      <xs:sequence>"
				+ "        <xs:element name='t' type='xs:string' minOccurs='0'/>"
				+ "        <xs:element name='v' type='xs:string' minOccurs='0'/>"
				+ "      </xs:sequence>"
				+ "    </xs:complexType>"
				+ " </xs:element>" + "</xs:schema>";

		Grammars g = SchemaInformedTest.getGrammarFromSchemaAsString(schema);
		factory.setGrammars(g);

		String xml = "<el1></el1>";

		// encode to EXI
		TestSAXEncoder enc = new TestSAXEncoder(factory);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		enc.encodeTo(new ByteArrayInputStream(xml.getBytes()), baos);

		// decoder
		{
			EXIStreamDecoder sdec = factory.createEXIStreamDecoder();
			EXIBodyDecoder decoder = sdec
					.decodeHeader(new ByteArrayInputStream(baos.toByteArray()));

			assertTrue(decoder.next() == EventType.START_DOCUMENT);
			decoder.decodeStartDocument();

			assertTrue(decoder.next() == EventType.START_ELEMENT);
			assertTrue(decoder.decodeStartElement().getQName().getLocalPart()
					.equals("el1"));

			// Note: no empty CH for mixed content

			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.END_DOCUMENT);
			decoder.decodeEndDocument();
		}
	}

	// Taki, public mailing list
	public void testEmptyCharactersSchemaInformedComplexMixedContent4()
			throws Exception {

		EXIFactory factory = DefaultEXIFactory.newInstance();

		factory.setFidelityOptions(FidelityOptions.createDefault());
		String schema = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ " <xs:element name='A'>"
				+ "    <xs:complexType mixed='true'>"
				+ "      <xs:sequence>"
				+ "        <xs:element name='B'>"
				+ "           <xs:complexType/>"
				+ "        </xs:element>"
				+ "      </xs:sequence>"
				+ "    </xs:complexType>"
				+ " </xs:element>" + "</xs:schema>";

		Grammars g = SchemaInformedTest.getGrammarFromSchemaAsString(schema);
		factory.setGrammars(g);

		String xml = "<A></A>";

		// encode to EXI
		TestSAXEncoder enc = new TestSAXEncoder(factory);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		enc.encodeTo(new ByteArrayInputStream(xml.getBytes()), baos);

		// decoder
		{
			EXIStreamDecoder sdec = factory.createEXIStreamDecoder();
			EXIBodyDecoder decoder = sdec
					.decodeHeader(new ByteArrayInputStream(baos.toByteArray()));

			assertTrue(decoder.next() == EventType.START_DOCUMENT);
			decoder.decodeStartDocument();

			assertTrue(decoder.next() == EventType.START_ELEMENT);
			assertTrue(decoder.decodeStartElement().getQName().getLocalPart()
					.equals("A"));

			// Note: no empty CH for mixed content

			assertTrue(decoder.next() == EventType.END_ELEMENT_UNDECLARED);
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.END_DOCUMENT);
			decoder.decodeEndDocument();
		}
	}

	public void testEmptyCharactersSchemaInformedXmlSpaceStrictFail()
			throws Exception {

		EXIFactory factory = DefaultEXIFactory.newInstance();

		factory.setFidelityOptions(FidelityOptions.createStrict());
		String schema = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ " <xs:import namespace='http://www.w3.org/XML/1998/namespace' schemaLocation='http://www.w3.org/2001/xml.xsd'/>"
				+ " <xs:element name='el1'>"
				+ "    <xs:complexType>"
				+ "      <xs:sequence>"
				+ "        <xs:element name='t' type='xs:string' minOccurs='0'/>"
				+ "      </xs:sequence>"
				+ "      <xs:attribute ref='xml:space'/>"
				+ "    </xs:complexType>" + " </xs:element>" + "</xs:schema>";

		Grammars g = SchemaInformedTest.getGrammarFromSchemaAsString(schema);
		factory.setGrammars(g);

		String xml = "<el1 xml:space='preserve'> </el1>";

		// encode to EXI
		try {
			TestSAXEncoder enc = new TestSAXEncoder(factory);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			enc.encodeTo(new ByteArrayInputStream(xml.getBytes()), baos);

			fail("Should fail because it cannot represent empty characters");
		} catch (Exception e) {
			// fine to fail
		}
	}

	public void testEmptyCharactersSchemaLess0() throws Exception {
		EXIFactory factory = DefaultEXIFactory.newInstance();
		factory.setFidelityOptions(FidelityOptions.createDefault());

		String xml = "<el1></el1>";

		// encode to EXI
		TestSAXEncoder enc = new TestSAXEncoder(factory);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		enc.encodeTo(new ByteArrayInputStream(xml.getBytes()), baos);

		// decoder
		{
			EXIStreamDecoder sdec = factory.createEXIStreamDecoder();
			EXIBodyDecoder decoder = sdec
					.decodeHeader(new ByteArrayInputStream(baos.toByteArray()));

			assertTrue(decoder.next() == EventType.START_DOCUMENT);
			decoder.decodeStartDocument();

			assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC);
			assertTrue(decoder.decodeStartElement().getQName().getLocalPart()
					.equals("el1"));

			// Note: no empty CH for schema-less streams

			assertTrue(decoder.next() == EventType.END_ELEMENT_UNDECLARED);
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.END_DOCUMENT);
			decoder.decodeEndDocument();
		}
	}

	public void testEmptyCharactersSchemaLess1() throws Exception {
		EXIFactory factory = DefaultEXIFactory.newInstance();
		factory.setFidelityOptions(FidelityOptions.createDefault());

		String xml = "<el1><foo>XX</foo><foo>YY</foo><foo></foo></el1>";

		// encode to EXI
		TestSAXEncoder enc = new TestSAXEncoder(factory);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		enc.encodeTo(new ByteArrayInputStream(xml.getBytes()), baos);

		// decoder
		{
			EXIStreamDecoder sdec = factory.createEXIStreamDecoder();
			EXIBodyDecoder decoder = sdec
					.decodeHeader(new ByteArrayInputStream(baos.toByteArray()));

			assertTrue(decoder.next() == EventType.START_DOCUMENT);
			decoder.decodeStartDocument();

			assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC);
			assertTrue(decoder.decodeStartElement().getQName().getLocalPart()
					.equals("el1"));

			{
				assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC_UNDECLARED);
				assertTrue(decoder.decodeStartElement().getQName()
						.getLocalPart().equals("foo"));

				assertTrue(decoder.next() == EventType.CHARACTERS_GENERIC_UNDECLARED);
				Value v = decoder.decodeCharacters();
				assertTrue(v instanceof StringValue);
				StringValue dtv = (StringValue) v;
				assertTrue(dtv.toString().equals("XX"));

				assertTrue(decoder.next() == EventType.END_ELEMENT);
				decoder.decodeEndElement();
			}

			{
				assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC_UNDECLARED);
				assertTrue(decoder.decodeStartElement().getQName()
						.getLocalPart().equals("foo"));

				assertTrue(decoder.next() == EventType.CHARACTERS); // learned
				Value v = decoder.decodeCharacters();
				assertTrue(v instanceof StringValue);
				StringValue dtv = (StringValue) v;
				assertTrue(dtv.toString().equals("YY"));

				assertTrue(decoder.next() == EventType.END_ELEMENT);
				decoder.decodeEndElement();
			}

			{
				assertTrue(decoder.next() == EventType.START_ELEMENT); // learned
				assertTrue(decoder.decodeStartElement().getQName()
						.getLocalPart().equals("foo"));

				// Note: no empty CH for schema-less streams (even if CH exists
				// due to learning)

				assertTrue(decoder.next() == EventType.END_ELEMENT_UNDECLARED);
				decoder.decodeEndElement();
			}

			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.END_DOCUMENT);
			decoder.decodeEndDocument();
		}
	}

	public void testEmptyCharactersSchemaLess2() throws Exception {
		EXIFactory factory = DefaultEXIFactory.newInstance();
		factory.setFidelityOptions(FidelityOptions.createAll());

		String xml = "<None>  <!-- abc -->   </None>";

		// encode to EXI
		TestSAXEncoder enc = new TestSAXEncoder(factory);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		enc.encodeTo(new ByteArrayInputStream(xml.getBytes()), baos);

		// decoder
		{
			EXIStreamDecoder sdec = factory.createEXIStreamDecoder();
			EXIBodyDecoder decoder = sdec
					.decodeHeader(new ByteArrayInputStream(baos.toByteArray()));

			assertTrue(decoder.next() == EventType.START_DOCUMENT);
			decoder.decodeStartDocument();

			assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC);
			assertTrue(decoder.decodeStartElement().getQName().getLocalPart()
					.equals("None"));

			{
				assertTrue(decoder.next() == EventType.CHARACTERS_GENERIC_UNDECLARED);
				Value v1 = decoder.decodeCharacters();
				assertTrue(v1 instanceof StringValue);
				StringValue dtv1 = (StringValue) v1;
				assertTrue(dtv1.toString().equals("  "));

				assertTrue(decoder.next() == EventType.COMMENT);
				char[] cm = decoder.decodeComment();
				assertTrue("'" + new String(cm) + "'",
						new String(cm).equals(" abc "));

				assertTrue(decoder.next() == EventType.CHARACTERS_GENERIC_UNDECLARED);
				Value v2 = decoder.decodeCharacters();
				assertTrue(v2 instanceof StringValue);
				StringValue dtv2 = (StringValue) v2;
				assertTrue(dtv2.toString().equals("   "));
			}

			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.END_DOCUMENT);
			decoder.decodeEndDocument();
		}
	}

	public void testEmptyCharactersSchemaLess3() throws Exception {
		EXIFactory factory = DefaultEXIFactory.newInstance();
		factory.setFidelityOptions(FidelityOptions.createAll());

		String xml = "<None>  <?PITarget PIContent?>   </None>";

		// encode to EXI
		TestSAXEncoder enc = new TestSAXEncoder(factory);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		enc.encodeTo(new ByteArrayInputStream(xml.getBytes()), baos);

		// decoder
		{
			EXIStreamDecoder sdec = factory.createEXIStreamDecoder();
			EXIBodyDecoder decoder = sdec
					.decodeHeader(new ByteArrayInputStream(baos.toByteArray()));

			assertTrue(decoder.next() == EventType.START_DOCUMENT);
			decoder.decodeStartDocument();

			assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC);
			assertTrue(decoder.decodeStartElement().getQName().getLocalPart()
					.equals("None"));

			{
				assertTrue(decoder.next() == EventType.CHARACTERS_GENERIC_UNDECLARED);
				Value v1 = decoder.decodeCharacters();
				assertTrue(v1 instanceof StringValue);
				StringValue dtv1 = (StringValue) v1;
				assertTrue(dtv1.toString().equals("  "));

				assertTrue(decoder.next() == EventType.PROCESSING_INSTRUCTION);
				@SuppressWarnings("unused")
				com.siemens.ct.exi.core.container.ProcessingInstruction pi = decoder
						.decodeProcessingInstruction();

				assertTrue(decoder.next() == EventType.CHARACTERS_GENERIC_UNDECLARED);
				Value v2 = decoder.decodeCharacters();
				assertTrue(v2 instanceof StringValue);
				StringValue dtv2 = (StringValue) v2;
				assertTrue(dtv2.toString().equals("   "));
			}

			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.END_DOCUMENT);
			decoder.decodeEndDocument();
		}
	}

	public void testEmptyCharactersSchemaLess4() throws Exception {
		EXIFactory factory = DefaultEXIFactory.newInstance();
		factory.setFidelityOptions(FidelityOptions.createDefault());
		factory.getFidelityOptions().setFidelity(
				FidelityOptions.FEATURE_COMMENT, true);

		String xml = "<None>  <!-- abc -->   </None>";

		// encode to EXI
		TestSAXEncoder enc = new TestSAXEncoder(factory);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		enc.encodeTo(new ByteArrayInputStream(xml.getBytes()), baos);

		// decoder
		{
			EXIStreamDecoder sdec = factory.createEXIStreamDecoder();
			EXIBodyDecoder decoder = sdec
					.decodeHeader(new ByteArrayInputStream(baos.toByteArray()));

			assertTrue(decoder.next() == EventType.START_DOCUMENT);
			decoder.decodeStartDocument();

			assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC);
			assertTrue(decoder.decodeStartElement().getQName().getLocalPart()
					.equals("None"));

			{
				assertTrue(decoder.next() == EventType.CHARACTERS_GENERIC_UNDECLARED);
				Value v1 = decoder.decodeCharacters();
				assertTrue(v1 instanceof StringValue);
				StringValue dtv1 = (StringValue) v1;
				assertTrue(dtv1.toString().equals("  "));

				assertTrue(decoder.next() == EventType.COMMENT);
				char[] cm = decoder.decodeComment();
				assertTrue("'" + new String(cm) + "'",
						new String(cm).equals(" abc "));

				assertTrue(decoder.next() == EventType.CHARACTERS_GENERIC_UNDECLARED);
				Value v2 = decoder.decodeCharacters();
				assertTrue(v2 instanceof StringValue);
				StringValue dtv2 = (StringValue) v2;
				assertTrue(dtv2.toString().equals("   "));
			}

			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.END_DOCUMENT);
			decoder.decodeEndDocument();
		}
	}

	public void testEmptyCharactersSchemaLess5() throws Exception {
		EXIFactory factory = DefaultEXIFactory.newInstance();
		factory.setFidelityOptions(FidelityOptions.createDefault());
		factory.getFidelityOptions().setFidelity(FidelityOptions.FEATURE_PI,
				true);
		factory.getFidelityOptions().setFidelity(
				FidelityOptions.FEATURE_PREFIX, true);
		factory.getEncodingOptions().setOption(EncodingOptions.CANONICAL_EXI);

		String xml = "<p><a>foo</a> </p>";

		// encode to EXI
		TestSAXEncoder enc = new TestSAXEncoder(factory);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		enc.encodeTo(new ByteArrayInputStream(xml.getBytes()), baos);

		// decoder
		{
			EXIStreamDecoder sdec = factory.createEXIStreamDecoder();
			EXIBodyDecoder decoder = sdec
					.decodeHeader(new ByteArrayInputStream(baos.toByteArray()));

			assertTrue(decoder.next() == EventType.START_DOCUMENT);
			decoder.decodeStartDocument();

			assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC);
			assertTrue(decoder.decodeStartElement().getQName().getLocalPart()
					.equals("p"));

			{
				assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC_UNDECLARED);
				assertTrue(decoder.decodeStartElement().getQName()
						.getLocalPart().equals("a"));

				{
					assertTrue(decoder.next() == EventType.CHARACTERS_GENERIC_UNDECLARED);
					Value v1 = decoder.decodeCharacters();
					assertTrue(v1 instanceof StringValue);
					StringValue dtv1 = (StringValue) v1;
					assertTrue(dtv1.toString().equals("foo"));
				}

				assertTrue(decoder.next() == EventType.END_ELEMENT);
				decoder.decodeEndElement();

				// assertTrue(decoder.next() ==
				// EventType.CHARACTERS_GENERIC_UNDECLARED);
				// Value v2 = decoder.decodeCharacters();
				// assertTrue(v2 instanceof StringValue);
				// StringValue dtv2 = (StringValue) v2;
				// assertTrue(dtv2.toString().equals(" "));
			}

			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.END_DOCUMENT);
			decoder.decodeEndDocument();
		}
	}

	public void testEmptyCharactersSchemaLess6() throws Exception {
		EXIFactory factory = DefaultEXIFactory.newInstance();
		factory.setFidelityOptions(FidelityOptions.createDefault());
		factory.getFidelityOptions().setFidelity(FidelityOptions.FEATURE_PI,
				true);
		factory.getFidelityOptions().setFidelity(
				FidelityOptions.FEATURE_PREFIX, true);
		factory.getEncodingOptions().setOption(EncodingOptions.CANONICAL_EXI);

		String xml = "<text:p xmlns:text=\"urn:oasis:names:tc:opendocument:xmlns:text:1.0\"  xmlns:xlink=\"http://www.w3.org/1999/xlink\" text:style-name=\"List_20_Contents\"><text:a xlink:type=\"simple\" xlink:href=\"http://www.w3.org/TR/2002/REC-xml-exc-c14n-20020718/\">http://www.w3.org/TR/2002/REC-xml-exc-c14n-20020718/</text:a> </text:p>";

		// encode to EXI
		TestSAXEncoder enc = new TestSAXEncoder(factory);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		enc.encodeTo(new ByteArrayInputStream(xml.getBytes()), baos);

		// decoder
		{
			EXIStreamDecoder sdec = factory.createEXIStreamDecoder();
			EXIBodyDecoder decoder = sdec
					.decodeHeader(new ByteArrayInputStream(baos.toByteArray()));

			assertTrue(decoder.next() == EventType.START_DOCUMENT);
			decoder.decodeStartDocument();

			assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC);
			assertTrue(decoder.decodeStartElement().getQName().getLocalPart()
					.equals("p"));

			assertTrue(decoder.next() == EventType.NAMESPACE_DECLARATION);
			decoder.decodeNamespaceDeclaration();

			assertTrue(decoder.next() == EventType.NAMESPACE_DECLARATION);
			decoder.decodeNamespaceDeclaration();

			// text:style-name=\"List_20_Contents\">
			assertTrue(decoder.next() == EventType.ATTRIBUTE_GENERIC_UNDECLARED);
			assertTrue(decoder.decodeAttribute().getQName().getLocalPart()
					.equals("style-name"));

			{
				// <text:a xlink:type=\"simple\"
				// xlink:href=\"http://www.w3.org/TR/2002/REC-xml-exc-c14n-20020718/\"

				assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC_UNDECLARED);
				assertTrue(decoder.decodeStartElement().getQName()
						.getLocalPart().equals("a"));

				assertTrue(decoder.next() == EventType.ATTRIBUTE_GENERIC_UNDECLARED);
				assertTrue(decoder.decodeAttribute().getQName().getLocalPart()
						.equals("href"));

				assertTrue(decoder.next() == EventType.ATTRIBUTE_GENERIC_UNDECLARED);
				assertTrue(decoder.decodeAttribute().getQName().getLocalPart()
						.equals("type"));

				{
					assertTrue(decoder.next() == EventType.CHARACTERS_GENERIC_UNDECLARED);
					Value v1 = decoder.decodeCharacters();
					assertTrue(v1 instanceof StringValue);
					StringValue dtv1 = (StringValue) v1;
					assertTrue(dtv1
							.toString()
							.equals("http://www.w3.org/TR/2002/REC-xml-exc-c14n-20020718/"));
				}

				assertTrue(decoder.next() == EventType.END_ELEMENT);
				decoder.decodeEndElement();

				// assertTrue(decoder.next() ==
				// EventType.CHARACTERS_GENERIC_UNDECLARED);
				// Value v2 = decoder.decodeCharacters();
				// assertTrue(v2 instanceof StringValue);
				// StringValue dtv2 = (StringValue) v2;
				// assertTrue(dtv2.toString().equals(" "));
			}

			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.END_DOCUMENT);
			decoder.decodeEndDocument();
		}
	}

	// learned
	public void testEmptyCharactersSchemaLess7() throws Exception {
		EXIFactory factory = DefaultEXIFactory.newInstance();
		factory.setFidelityOptions(FidelityOptions.createDefault());
		factory.getFidelityOptions().setFidelity(FidelityOptions.FEATURE_PI,
				true);
		factory.getFidelityOptions().setFidelity(
				FidelityOptions.FEATURE_PREFIX, true);
		factory.getEncodingOptions().setOption(EncodingOptions.CANONICAL_EXI);

		String xml = "<outer><p><a/>bla</p><p>bla2</p><p>bla3</p><p><a>foo</a> </p></outer>";

		// encode to EXI
		TestSAXEncoder enc = new TestSAXEncoder(factory);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		enc.encodeTo(new ByteArrayInputStream(xml.getBytes()), baos);

		// decoder
		{
			EXIStreamDecoder sdec = factory.createEXIStreamDecoder();
			EXIBodyDecoder decoder = sdec
					.decodeHeader(new ByteArrayInputStream(baos.toByteArray()));

			assertTrue(decoder.next() == EventType.START_DOCUMENT);
			decoder.decodeStartDocument();

			assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC);
			assertTrue(decoder.decodeStartElement().getQName().getLocalPart()
					.equals("outer"));

			assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC_UNDECLARED);
			assertTrue(decoder.decodeStartElement().getQName().getLocalPart()
					.equals("p"));
			{
				assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC_UNDECLARED);
				assertTrue(decoder.decodeStartElement().getQName()
						.getLocalPart().equals("a"));
				assertTrue(decoder.next() == EventType.END_ELEMENT_UNDECLARED);
				decoder.decodeEndElement();

				assertTrue(decoder.next() == EventType.CHARACTERS_GENERIC_UNDECLARED);
				Value v1 = decoder.decodeCharacters();
				assertTrue(v1 instanceof StringValue);
				StringValue dtv1 = (StringValue) v1;
				assertTrue(dtv1.toString().equals("bla"));
			}
			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC_UNDECLARED);
			assertTrue(decoder.decodeStartElement().getQName().getLocalPart()
					.equals("p"));
			{
				assertTrue(decoder.next() == EventType.CHARACTERS_GENERIC_UNDECLARED);
				Value v1 = decoder.decodeCharacters();
				assertTrue(v1 instanceof StringValue);
				StringValue dtv1 = (StringValue) v1;
				assertTrue(dtv1.toString().equals("bla2"));
			}
			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.START_ELEMENT);
			assertTrue(decoder.decodeStartElement().getQName().getLocalPart()
					.equals("p"));
			{
				assertTrue(decoder.next() == EventType.CHARACTERS);
				Value v1 = decoder.decodeCharacters();
				assertTrue(v1 instanceof StringValue);
				StringValue dtv1 = (StringValue) v1;
				assertTrue(dtv1.toString().equals("bla3"));
			}
			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.START_ELEMENT);
			assertTrue(decoder.decodeStartElement().getQName().getLocalPart()
					.equals("p"));

			{
				assertTrue(decoder.next() == EventType.START_ELEMENT);
				assertTrue(decoder.decodeStartElement().getQName()
						.getLocalPart().equals("a"));

				{
					assertTrue(decoder.next() == EventType.CHARACTERS_GENERIC_UNDECLARED);
					Value v1 = decoder.decodeCharacters();
					assertTrue(v1 instanceof StringValue);
					StringValue dtv1 = (StringValue) v1;
					assertTrue(dtv1.toString().equals("foo"));
				}

				assertTrue(decoder.next() == EventType.END_ELEMENT);
				decoder.decodeEndElement();
			}

			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement(); // p

			assertTrue(decoder.next() == EventType.END_ELEMENT); // outer
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.END_DOCUMENT);
			decoder.decodeEndDocument();
		}
	}
}