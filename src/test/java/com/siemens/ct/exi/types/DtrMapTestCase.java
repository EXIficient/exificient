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

package com.siemens.ct.exi.types;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;

import javax.xml.namespace.QName;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;

import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.siemens.ct.exi.Constants;
import com.siemens.ct.exi.EXIBodyDecoder;
import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.FidelityOptions;
import com.siemens.ct.exi.GrammarFactory;
import com.siemens.ct.exi.api.sax.EXIResult;
import com.siemens.ct.exi.api.sax.EXISource;
import com.siemens.ct.exi.api.sax.SAXFactory;
import com.siemens.ct.exi.datatype.AbstractTestCase;
import com.siemens.ct.exi.datatype.Datatype;
import com.siemens.ct.exi.datatype.DatatypeID;
import com.siemens.ct.exi.datatype.ListDatatype;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.grammars.Grammars;
import com.siemens.ct.exi.grammars.event.EventType;
import com.siemens.ct.exi.helpers.DefaultEXIFactory;
import com.siemens.ct.exi.values.StringValue;
import com.siemens.ct.exi.values.Value;

public class DtrMapTestCase extends AbstractTestCase {

	public void testIntegerFail() throws IOException, EXIException {
		String schemaAsString = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ "  <xs:simpleType name='myByte'>"
				+ "    <xs:restriction base='xs:byte'>"
				+ "    </xs:restriction>"
				+ "  </xs:simpleType>"
				+ "</xs:schema>";
		// Grammars g = DatatypeMappingTest.getGrammarFor(schemaAsString);

		Datatype dtInteger = DatatypeMappingTest.getSimpleDatatypeFor(
				schemaAsString, "myByte", "");
		assertTrue(dtInteger.getBuiltInType() == BuiltInType.NBIT_UNSIGNED_INTEGER);
		QName schemaTypeInteger = new QName("", "myByte");
		assertTrue(dtInteger.getSchemaType().getQName()
				.equals(schemaTypeInteger));

		/* DTR Map */
		QName type = new QName(Constants.XML_SCHEMA_NS_URI, "decimal");
		QName representation = new QName(Constants.W3C_EXI_NS_URI, "string");
		QName[] dtrMapTypes = { type };
		QName[] dtrMapRepresentations = { representation };
		// TypeEncoder defaultEncoder = new TypedTypeEncoder();
		TypedTypeEncoder dtrTe = new TypedTypeEncoder(
				dtrMapTypes, dtrMapRepresentations, null);

		// integers
		assertTrue(dtrTe.isValid(dtInteger, new StringValue("+10")));
		// default mapping
		assertTrue(dtrTe.lastDatatype.getBuiltInType() == BuiltInType.NBIT_UNSIGNED_INTEGER);
	}

	public void testByte2String() throws IOException, EXIException {
		String schemaAsString = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ "  <xs:simpleType name='myByte'>"
				+ "    <xs:restriction base='xs:byte'>"
				+ "    </xs:restriction>"
				+ "  </xs:simpleType>"
				+ "</xs:schema>";
		// Grammars g = DatatypeMappingTest.getGrammarFor(schemaAsString);

		Datatype dtInteger = DatatypeMappingTest.getSimpleDatatypeFor(
				schemaAsString, "myByte", "");
		assertTrue(dtInteger.getBuiltInType() == BuiltInType.NBIT_UNSIGNED_INTEGER);
		QName schemaTypeInteger = new QName("", "myByte");
		assertTrue(dtInteger.getSchemaType().getQName()
				.equals(schemaTypeInteger));

		/* DTR Map */
		QName type = new QName(Constants.XML_SCHEMA_NS_URI, "byte");
		QName representation = new QName(Constants.W3C_EXI_NS_URI, "string");
		QName[] dtrMapTypes = { type };
		QName[] dtrMapRepresentations = { representation };
		// TypeEncoder defaultEncoder = new TypedTypeEncoder();
		TypedTypeEncoder dtrTe = new TypedTypeEncoder(
				dtrMapTypes, dtrMapRepresentations, null);

		// integers
		assertTrue(dtrTe.isValid(dtInteger, new StringValue("+10")));
		assertTrue(dtrTe.isValid(dtInteger, new StringValue("XXX any")));
		// default mapping
		assertTrue(dtrTe.lastDatatype.getBuiltInType() == BuiltInType.STRING);
	}

	public void testInt2String() throws IOException, EXIException {
		String schemaAsString = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ "  <xs:simpleType name='myByte'>"
				+ "    <xs:restriction base='xs:byte'>"
				+ "    </xs:restriction>"
				+ "  </xs:simpleType>"
				+ "</xs:schema>";
		// Grammars g = DatatypeMappingTest.getGrammarFor(schemaAsString);

		Datatype dtInteger = DatatypeMappingTest.getSimpleDatatypeFor(
				schemaAsString, "myByte", "");
		assertTrue(dtInteger.getBuiltInType() == BuiltInType.NBIT_UNSIGNED_INTEGER);
		QName schemaTypeInteger = new QName("", "myByte");
		assertTrue(dtInteger.getSchemaType().getQName()
				.equals(schemaTypeInteger));

		/* DTR Map */
		QName type = new QName(Constants.XML_SCHEMA_NS_URI, "int");
		QName representation = new QName(Constants.W3C_EXI_NS_URI, "string");
		QName[] dtrMapTypes = { type };
		QName[] dtrMapRepresentations = { representation };
		// TypeEncoder defaultEncoder = new TypedTypeEncoder();
		TypedTypeEncoder dtrTe = new TypedTypeEncoder(
				dtrMapTypes, dtrMapRepresentations, null);

		// integers
		assertTrue(dtrTe.isValid(dtInteger, new StringValue("+10")));
		assertTrue(dtrTe.isValid(dtInteger, new StringValue("XXX any")));
		// default mapping
		assertTrue(dtrTe.lastDatatype.getBuiltInType() == BuiltInType.STRING);
	}

	// dtr-04
	public void testMulti4() throws IOException, EXIException, SAXException,
			TransformerException {
		String schemaAsString = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ "  <xs:element name='root'>"
				+ "   <xs:complexType>"
				+ "         <!--  Built-in Type: N-Bit Integer  -->"
				+ "         <xs:sequence>"
				+ "            <xs:element name='byte'  type='xs:byte' minOccurs='0' maxOccurs='unbounded' />"
				+ "         </xs:sequence>"
				+ "         <xs:attribute name='float' type='xs:float' use='optional'/>"
				+ "         <xs:attribute name='double' type='xs:double' use='optional'/>"
				+ "    </xs:complexType>"
				+ "  </xs:element>"
				+ "</xs:schema>"
				+ "";

		// nbitInteger-valid-00.xml
		// <root>
		// <byte>33</byte>
		// </root>

		String xmlAsString = "<root><byte>33</byte></root>";

		GrammarFactory grammarFactory = GrammarFactory.newInstance();
		Grammars g = grammarFactory.createGrammars(new ByteArrayInputStream(
				schemaAsString.getBytes()));

		// factory with mapping
		EXIFactory exiFactory = DefaultEXIFactory.newInstance();
		exiFactory.setGrammars(g);
		exiFactory.setFidelityOptions(FidelityOptions.createStrict());
		/* DTR Map */
		QName type1 = new QName(Constants.XML_SCHEMA_NS_URI, "decimal");
		QName type2 = new QName(Constants.XML_SCHEMA_NS_URI, "double");
		QName representation1 = new QName(Constants.W3C_EXI_NS_URI, "string");
		QName representation2 = new QName(Constants.W3C_EXI_NS_URI, "decimal");
		QName[] dtrMapTypes = { type1, type2 };
		QName[] dtrMapRepresentations = { representation1, representation2 };
		exiFactory.setDatatypeRepresentationMap(dtrMapTypes,
				dtrMapRepresentations);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		// encode
		{
			EXIResult exiResult = new EXIResult(exiFactory);
			exiResult.setOutputStream(baos);
			XMLReader xmlReader = XMLReaderFactory.createXMLReader();
			xmlReader.setContentHandler(exiResult.getHandler());
			xmlReader.parse(new InputSource(new ByteArrayInputStream(
					xmlAsString.getBytes())));
		}

		ByteArrayOutputStream baosDecXML = new ByteArrayOutputStream();

		// decode
		{
			InputSource is = new InputSource(new ByteArrayInputStream(
					baos.toByteArray()));
			XMLReader exiReader = new SAXFactory(exiFactory).createEXIReader();

			Result result = new StreamResult(baosDecXML);
			SAXSource exiSource = new SAXSource(is);
			exiSource.setXMLReader(exiReader);
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.transform(exiSource, result);
		}

		// System.out.println(baosDecXML.toString());

	}

	// dtr-08
	public void testMulti8() throws IOException, EXIException, SAXException,
			TransformerException {
		String schemaAsString = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ "  <xs:simpleType name='stringDerived'>"
				+ "   <xs:restriction base='xs:string'>"
				+ "         <xs:enumeration value='Tokyo'/>"
				+ "         <xs:enumeration value='Osaka'/>"
				+ "         <xs:enumeration value='Nagoya'/>"
				+ "   </xs:restriction>"
				+ "  </xs:simpleType>"
				+ "</xs:schema>" + "";

		// enumerationToInteger_03.xml
		// <foo:A xmlns:foo="urn:foo"
		// xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
		// xsi:type="foo:stringDerived">Nagoya</foo:A>

		String xmlAsString = "<foo:A xmlns:foo='urn:foo' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' "
				+ "xsi:type='foo:stringDerived'>NagoyaX</foo:A>";

		GrammarFactory grammarFactory = GrammarFactory.newInstance();
		Grammars g = grammarFactory.createGrammars(new ByteArrayInputStream(
				schemaAsString.getBytes()));

		// factory with mapping
		EXIFactory exiFactory = DefaultEXIFactory.newInstance();
		exiFactory.setGrammars(g);
		exiFactory.setFidelityOptions(FidelityOptions.createStrict());
		/* DTR Map */
		QName type1 = new QName(Constants.XML_SCHEMA_NS_URI, "string");
		QName representation1 = new QName(Constants.W3C_EXI_NS_URI, "integer");
		QName[] dtrMapTypes = { type1 };
		QName[] dtrMapRepresentations = { representation1 };
		exiFactory.setDatatypeRepresentationMap(dtrMapTypes,
				dtrMapRepresentations);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		// encode
		{
			EXIResult exiResult = new EXIResult(exiFactory);
			exiResult.setOutputStream(baos);
			XMLReader xmlReader = XMLReaderFactory.createXMLReader();
			xmlReader.setContentHandler(exiResult.getHandler());
			xmlReader.parse(new InputSource(new ByteArrayInputStream(
					xmlAsString.getBytes())));
		}

		ByteArrayOutputStream baosDecXML = new ByteArrayOutputStream();

		// decode
		{
			InputSource is = new InputSource(new ByteArrayInputStream(
					baos.toByteArray()));
			XMLReader exiReader = new SAXFactory(exiFactory).createEXIReader();

			Result result = new StreamResult(baosDecXML);
			SAXSource exiSource = new SAXSource(is);
			exiSource.setXMLReader(exiReader);
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.transform(exiSource, result);
		}

		// System.out.println(baosDecXML.toString());

	}

	// dtr-02
	// <param name="org.w3c.exi.ttf.datatypeRepresentationMap" value=
	// "{http://www.w3.org/2001/XMLSchema}decimal
	// {http://www.w3.org/2009/exi}string
	// {http://www.w3.org/2001/XMLSchema}double
	// {http://www.w3.org/2009/exi}decimal" />
	// float-valid-08.xml
	public void testMulti2() throws IOException, EXIException, SAXException,
			TransformerException {
		String schemaAsString = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ "  <xs:element name='root'>"
				+ "   <xs:complexType>"
				+ "         <!-- Built-in Type: float -->"
				+ "         <xs:sequence>"
				+ "            <xs:element name='float'  type='xs:float' minOccurs='0' maxOccurs='unbounded' />"
				+ "            <xs:element name='double'  type='xs:double' minOccurs='0' maxOccurs='unbounded' />"
				+ "         </xs:sequence>"
				+ "         <xs:attribute name='float' type='xs:float' use='optional'/>"
				+ "         <xs:attribute name='double' type='xs:double' use='optional'/>"
				+ "    </xs:complexType>"
				+ "  </xs:element>"
				+ "</xs:schema>"
				+ "";

		// float-valid-08.xml
		// <root double="10">
		// <float>-9223372036854775807</float>
		// <float>9223372036854775807</float>
		// <float>9223372036854775808</float>
		// <float>-9223372036854775808</float>
		// <double>4000e-3</double>
		// </root>

		String xmlAsString = "<root double='10'><float>-9223372036854775807</float><float>9223372036854775807</float>"
				+ "  <float>9223372036854775808</float><float>-9223372036854775808</float>"
				+ "<double>4000e-3</double></root>";

		GrammarFactory grammarFactory = GrammarFactory.newInstance();
		Grammars g = grammarFactory.createGrammars(new ByteArrayInputStream(
				schemaAsString.getBytes()));

		// factory with int 2 string mapping
		EXIFactory exiFactory = DefaultEXIFactory.newInstance();
		exiFactory.setGrammars(g);
		/* DTR Map */
		QName type1 = new QName(Constants.XML_SCHEMA_NS_URI, "decimal");
		QName type2 = new QName(Constants.XML_SCHEMA_NS_URI, "decimal");
		QName representation1 = new QName(Constants.W3C_EXI_NS_URI, "double");
		QName representation2 = new QName(Constants.W3C_EXI_NS_URI, "decimal");
		QName[] dtrMapTypes = { type1, type2 };
		QName[] dtrMapRepresentations = { representation1, representation2 };
		exiFactory.setDatatypeRepresentationMap(dtrMapTypes,
				dtrMapRepresentations);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		// encode
		{
			EXIResult exiResult = new EXIResult(exiFactory);
			exiResult.setOutputStream(baos);
			XMLReader xmlReader = XMLReaderFactory.createXMLReader();
			xmlReader.setContentHandler(exiResult.getHandler());
			xmlReader.parse(new InputSource(new ByteArrayInputStream(
					xmlAsString.getBytes())));
		}

		ByteArrayOutputStream baosDecXML = new ByteArrayOutputStream();

		// decode
		{
			InputSource is = new InputSource(new ByteArrayInputStream(
					baos.toByteArray()));
			XMLReader exiReader = new SAXFactory(exiFactory).createEXIReader();

			Result result = new StreamResult(baosDecXML);
			SAXSource exiSource = new SAXSource(is);
			exiSource.setXMLReader(exiReader);
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.transform(exiSource, result);
		}

		// System.out.println(baosDecXML.toString());

	}

	// register type directly
	public void testIntegerToString1() throws IOException, EXIException {
		String schemaAsString = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ "  <xs:simpleType name='Integer'>"
				+ "    <xs:restriction base='xs:int'>"
				+ "    </xs:restriction>"
				+ "  </xs:simpleType>"
				+ "</xs:schema>";

		// Grammars g = DatatypeMappingTest.getGrammarFor(schemaAsString);
		Datatype dt = DatatypeMappingTest.getSimpleDatatypeFor(schemaAsString,
				"Integer", "");

		QName schemaType = new QName("", "Integer");

		assertTrue(dt.getBuiltInType() == BuiltInType.INTEGER);
		// IntegerDatatype idt = (IntegerDatatype) dt;
		// assertTrue(idt.getIntegerType() == IntegerType.INTEGER_32);
		assertTrue(dt.getSchemaType().getQName().equals(schemaType));

		assertTrue(dt.isValid(new StringValue("+10")));
		assertFalse(dt.isValid(new StringValue("12:32:00")));

		/* DTR Map */
		QName type = schemaType;
		QName representation = new QName(Constants.W3C_EXI_NS_URI, "string");
		QName[] dtrMapTypes = { type };
		QName[] dtrMapRepresentations = { representation };
		// TypeEncoder defaultEncoder = new TypedTypeEncoder();
		TypedTypeEncoder dtrTe = new TypedTypeEncoder(
				dtrMapTypes, dtrMapRepresentations, null);

		assertTrue(dtrTe.isValid(dt, new StringValue("+10")));
		// any string should be valid
		assertTrue(dtrTe.isValid(dt, new StringValue("12:32:00")));
		assertTrue(dtrTe.isValid(dt, new StringValue("Blaa bla")));

		assertTrue(dtrTe.lastDatatype.getBuiltInType() == BuiltInType.STRING);
	}

	// register super-type
	public void testIntegerToString2() throws IOException, EXIException {
		String schemaAsString = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ "  <xs:simpleType name='Integer'>"
				+ "    <xs:restriction base='xs:int'>"
				+ "    </xs:restriction>"
				+ "  </xs:simpleType>"
				+ "</xs:schema>";

		// Grammars g = DatatypeMappingTest.getGrammarFor(schemaAsString);
		Datatype dt = DatatypeMappingTest.getSimpleDatatypeFor(schemaAsString,
				"Integer", "");

		QName schemaType = new QName("", "Integer");

		assertTrue(dt.getBuiltInType() == BuiltInType.INTEGER);
		// IntegerDatatype idt = (IntegerDatatype) dt;
		// assertTrue(idt.getIntegerType() == IntegerType.INTEGER_32);
		assertTrue(dt.getSchemaType().getQName().equals(schemaType));

		assertTrue(dt.isValid(new StringValue("+10")));
		assertFalse(dt.isValid(new StringValue("12:32:00")));

		/* DTR Map */
		QName type = new QName(Constants.XML_SCHEMA_NS_URI, "int");
		QName representation = new QName(Constants.W3C_EXI_NS_URI, "string");
		QName[] dtrMapTypes = { type };
		QName[] dtrMapRepresentations = { representation };
		// TypeEncoder defaultEncoder = new TypedTypeEncoder();
		TypedTypeEncoder dtrTe = new TypedTypeEncoder(
				dtrMapTypes, dtrMapRepresentations, null);

		assertTrue(dtrTe.isValid(dt, new StringValue("+10")));
		// any string should be valid
		assertTrue(dtrTe.isValid(dt, new StringValue("12:32:00")));
		assertTrue(dtrTe.isValid(dt, new StringValue("Blaa bla")));

		assertTrue(dtrTe.lastDatatype.getBuiltInType() == BuiltInType.STRING);
	}

	// register xs:int and encode type, sub-type and anonymous type
	public void testIntegerToString3() throws IOException, EXIException,
			SAXException, TransformerException {
		String schemaAsString = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ "  <xs:element name='root'>"
				+ "   <xs:complexType>"
				+ "      <xs:sequence>"
				+ "         <xs:element name='int' type='xs:int' />"
				+ "         <xs:element name='myInt' type='myInt'></xs:element>"
				+ "         <xs:element name='anonInt'>"
				+ "            <xs:simpleType>"
				+ "                <xs:restriction base='xs:int'>"
				+ "                </xs:restriction>"
				+ "            </xs:simpleType>"
				+ "         </xs:element>"
				+ "      </xs:sequence>"
				+ "    </xs:complexType>"
				+ "  </xs:element>"
				+ "  <xs:simpleType name='myInt'>"
				+ "    <xs:restriction base='xs:int'></xs:restriction>"
				+ "  </xs:simpleType>" + "</xs:schema>" + "";

		// String xmlAsString = "<root>"
		// + "  <int>12</int>"
		// + "  <myInt>13</myInt>"
		// + "  <anonInt>14</anonInt>"
		// + "</root>";

		// invalid integers but treated as String it should be OK and valid
		String xmlAsString = "<root>" + "  <int>XXX 12 XX</int>"
				+ "  <myInt>YYY 13 YYY</myInt>"
				+ "  <anonInt>ZZZZ 14 ZZZZ</anonInt>" + "</root>";

		GrammarFactory grammarFactory = GrammarFactory.newInstance();
		Grammars g = grammarFactory.createGrammars(new ByteArrayInputStream(
				schemaAsString.getBytes()));

		// factory with int 2 string mapping
		EXIFactory exiFactory = DefaultEXIFactory.newInstance();
		exiFactory.setGrammars(g);
		exiFactory.setFidelityOptions(FidelityOptions.createStrict());
		/* DTR Map */
		QName type = new QName(Constants.XML_SCHEMA_NS_URI, "int");
		QName representation = new QName(Constants.W3C_EXI_NS_URI, "string");
		QName[] dtrMapTypes = { type };
		QName[] dtrMapRepresentations = { representation };
		exiFactory.setDatatypeRepresentationMap(dtrMapTypes,
				dtrMapRepresentations);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		// encode
		{
			EXIResult exiResult = new EXIResult(exiFactory);
			exiResult.setOutputStream(baos);
			XMLReader xmlReader = XMLReaderFactory.createXMLReader();
			xmlReader.setContentHandler(exiResult.getHandler());
			xmlReader.parse(new InputSource(new ByteArrayInputStream(
					xmlAsString.getBytes())));
		}

		ByteArrayOutputStream baosDecXML = new ByteArrayOutputStream();

		// decode
		{
			InputSource is = new InputSource(new ByteArrayInputStream(
					baos.toByteArray()));
			XMLReader exiReader = new SAXFactory(exiFactory).createEXIReader();

			Result result = new StreamResult(baosDecXML);
			SAXSource exiSource = new SAXSource(is);
			exiSource.setXMLReader(exiReader);
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.transform(exiSource, result);
		}

		// System.out.println(baosDecXML.toString());

	}

	// dtr-06
	// Note: according to EXI errata ONLY directly referenced enumeration types
	// are handled by DTR maps
	// value="{}stringDerived --> {http://www.w3.org/2009/exi}integer"
	public void testEnumerationToInteger1() throws IOException, EXIException {
		String schemaAsString = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ "  <xs:simpleType name='stringDerived'>"
				+ "    <xs:restriction base='xs:string'>"
				+ "      <xs:enumeration value='Tokyo'/>"
				+ "      <xs:enumeration value='Osaka'/>"
				+ "      <xs:enumeration value='Nagoya'/>"
				+ "    </xs:restriction>"
				+ "  </xs:simpleType>"
				+ "  <xs:simpleType name='stringDerived2'>"
				+ "    <xs:restriction base='stringDerived'/>"
				+ "  </xs:simpleType>" + "</xs:schema>";
		// Grammars g = DatatypeMappingTest.getGrammarFor(schemaAsString);

		Datatype dtEnum = DatatypeMappingTest.getSimpleDatatypeFor(
				schemaAsString, "stringDerived", "");
		assertTrue(dtEnum.getBuiltInType() == BuiltInType.ENUMERATION);
		QName schemaTypeStringDerived = new QName("", "stringDerived");
		assertTrue(dtEnum.getSchemaType().getQName()
				.equals(schemaTypeStringDerived));

		/* DTR Map */
		QName type = new QName("", "stringDerived");
		QName representation = new QName(Constants.W3C_EXI_NS_URI, "integer");
		QName[] dtrMapTypes = { type };
		QName[] dtrMapRepresentations = { representation };
		// TypeEncoder defaultEncoder = new TypedTypeEncoder();
		TypedTypeEncoder dtrTe = new TypedTypeEncoder(
				dtrMapTypes, dtrMapRepresentations, null);

		// can encode integers
		assertTrue(dtrTe.isValid(dtEnum, new StringValue("+10")));
		// indicates that dtr map is in use
		assertTrue(dtrTe.lastDatatype.getBuiltInType() == BuiltInType.INTEGER);
		// IntegerDatatype idt = (IntegerDatatype)
		// dtrTe.getRecentDtrMapDatatype();
		// assertTrue(idt.getIntegerType() == IntegerType.INTEGER_BIG);
	}

	// dtr-07
	// Note: according to EXI errata ONLY referenced enumeration types
	// value="{}stringDerived --> {http://www.w3.org/2009/exi}integer"
	public void testEnumerationToInteger2() throws IOException, EXIException {
		String schemaAsString = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ "  <xs:simpleType name='stringDerived'>"
				+ "    <xs:restriction base='xs:string'>"
				+ "      <xs:enumeration value='Tokyo'/>"
				+ "      <xs:enumeration value='Osaka'/>"
				+ "      <xs:enumeration value='Nagoya'/>"
				+ "    </xs:restriction>"
				+ "  </xs:simpleType>"
				+ "  <xs:simpleType name='stringDerived2'>"
				+ "    <xs:restriction base='stringDerived'/>"
				+ "  </xs:simpleType>" + "</xs:schema>";
		// Grammars g = DatatypeMappingTest.getGrammarFor(schemaAsString);

		Datatype dtEnum = DatatypeMappingTest.getSimpleDatatypeFor(
				schemaAsString, "stringDerived2", "");
		assertTrue(dtEnum.getBuiltInType() == BuiltInType.ENUMERATION);
		QName schemaTypeStringDerived2 = new QName("", "stringDerived2");
		assertTrue(dtEnum.getSchemaType().getQName()
				.equals(schemaTypeStringDerived2));

		/* DTR Map */
		QName type = new QName("", "stringDerived");
		QName representation = new QName(Constants.W3C_EXI_NS_URI, "integer");
		QName[] dtrMapTypes = { type };
		QName[] dtrMapRepresentations = { representation };
		// TypeEncoder defaultEncoder = new TypedTypeEncoder();
		TypedTypeEncoder dtrTe = new TypedTypeEncoder(
				dtrMapTypes, dtrMapRepresentations, null);

		// can encode only int values
		assertTrue(dtrTe.isValid(dtEnum, new StringValue("+123")));
		assertFalse(dtrTe.isValid(dtEnum, new StringValue("Nagoya")));

		// indicates that an dtr map is in use
		assertTrue(dtrTe.lastDatatype.getBuiltInType() == BuiltInType.INTEGER);
	}

	// dtr-08
	// The codec used for an enumerated type is not affected by DTRM entry
	// attached to its ancestral type.
	// value="{http://www.w3.org/2001/XMLSchema}string {http://www.w3.org/2009/exi}integer"
	public void testEnumerationToInteger3() throws IOException, EXIException {
		String schemaAsString = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ "  <xs:simpleType name='stringDerived'>"
				+ "    <xs:restriction base='xs:string'>"
				+ "      <xs:enumeration value='Tokyo'/>"
				+ "      <xs:enumeration value='Osaka'/>"
				+ "      <xs:enumeration value='Nagoya'/>"
				+ "    </xs:restriction>" + "  </xs:simpleType>"
				// + "  <xs:simpleType name='stringDerived2'>"
				// + "    <xs:restriction base='stringDerived'/>"
				// + "  </xs:simpleType>"
				+ "</xs:schema>";
		// Grammars g = DatatypeMappingTest.getGrammarFor(schemaAsString);

		Datatype dtEnum = DatatypeMappingTest.getSimpleDatatypeFor(
				schemaAsString, "stringDerived", "");
		assertTrue(dtEnum.getBuiltInType() == BuiltInType.ENUMERATION);
		QName schemaTypeStringDerived = new QName("", "stringDerived");
		assertTrue(dtEnum.getSchemaType().getQName()
				.equals(schemaTypeStringDerived));

		/* DTR Map */
		QName type = new QName(Constants.XML_SCHEMA_NS_URI, "string");
		QName representation = new QName(Constants.W3C_EXI_NS_URI, "integer");
		QName[] dtrMapTypes = { type };
		QName[] dtrMapRepresentations = { representation };
		// TypeEncoder defaultEncoder = new TypedTypeEncoder();
		TypedTypeEncoder dtrTe = new TypedTypeEncoder(
				dtrMapTypes, dtrMapRepresentations, null);

		// can encode only enum values
		assertFalse(dtrTe.isValid(dtEnum, new StringValue("+10")));
		assertTrue(dtrTe.isValid(dtEnum, new StringValue("Nagoya")));

		// indicates that NO dtr map is in use
		assertTrue(dtrTe.lastDatatype.getBuiltInType() == BuiltInType.ENUMERATION);
	}
	
	
	public void testEnumerationNotUsed() throws IOException, EXIException {
		String schemaAsString = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ "  <xs:simpleType name='stringDerived'>"
				+ "    <xs:restriction base='xs:string'>"
				+ "      <xs:enumeration value='Tokyo'/>"
				+ "      <xs:enumeration value='Osaka'/>"
				+ "      <xs:enumeration value='Nagoya'/>"
				+ "    </xs:restriction>" + "  </xs:simpleType>"
				// + "  <xs:simpleType name='stringDerived2'>"
				// + "    <xs:restriction base='stringDerived'/>"
				// + "  </xs:simpleType>"
				+ "</xs:schema>";
		// Grammars g = DatatypeMappingTest.getGrammarFor(schemaAsString);

		Datatype dtEnum = DatatypeMappingTest.getSimpleDatatypeFor(
				schemaAsString, "stringDerived", "");
		assertTrue(dtEnum.getBuiltInType() == BuiltInType.ENUMERATION);
		QName schemaTypeStringDerived = new QName("", "stringDerived");
		assertTrue(dtEnum.getSchemaType().getQName()
				.equals(schemaTypeStringDerived));

		/* DTR Map */
		QName type = new QName(Constants.XML_SCHEMA_NS_URI, "stringX");
		QName representation = new QName(Constants.W3C_EXI_NS_URI, "integer");
		QName[] dtrMapTypes = { type };
		QName[] dtrMapRepresentations = { representation };
		// TypeEncoder defaultEncoder = new TypedTypeEncoder();
		TypedTypeEncoder dtrTe = new TypedTypeEncoder(
				dtrMapTypes, dtrMapRepresentations, null);

		// can encode only enum values
		assertFalse(dtrTe.isValid(dtEnum, new StringValue("+10")));
		assertTrue(dtrTe.isValid(dtEnum, new StringValue("Nagoya")));

		// indicates that NO dtr map is in use
		assertTrue(dtrTe.lastDatatype.getBuiltInType() == BuiltInType.ENUMERATION);
	}
	

	// 1. register subtype of integer (short) as integer
	// 2. register integer as String
	// --> short should still be encoded as EXI Integer and "XXX 12 XX" should
	// NOT be encodable
	public void testClosestAncestor1() throws IOException, EXIException,
			SAXException, TransformerException {
		String schemaAsString = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ "  <xs:element name='root'>"
				+ "   <xs:complexType>"
				+ "      <xs:sequence>"
				+ "         <xs:element name='shortVal' type='xs:short' />"
				+ "      </xs:sequence>"
				+ "    </xs:complexType>"
				+ "  </xs:element>" + "</xs:schema>" + "";

		// invalid short and should throw error given that it is not treated as
		// String
		String xmlAsString = "<root>" + "  <shortVal>XXX 12 XX</shortVal>"
				+ "</root>";

		GrammarFactory grammarFactory = GrammarFactory.newInstance();
		Grammars g = grammarFactory.createGrammars(new ByteArrayInputStream(
				schemaAsString.getBytes()));

		// factory with mapping
		EXIFactory exiFactory = DefaultEXIFactory.newInstance();
		exiFactory.setGrammars(g);
		exiFactory.setFidelityOptions(FidelityOptions.createStrict());
		/* DTR Map */
		QName type1 = new QName(Constants.XML_SCHEMA_NS_URI, "short");
		QName type2 = new QName(Constants.XML_SCHEMA_NS_URI, "integer");
		QName representation1 = new QName(Constants.W3C_EXI_NS_URI, "integer");
		QName representation2 = new QName(Constants.W3C_EXI_NS_URI, "string");
		QName[] dtrMapTypes = { type1, type2 };
		QName[] dtrMapRepresentations = { representation1, representation2 };
		exiFactory.setDatatypeRepresentationMap(dtrMapTypes,
				dtrMapRepresentations);

		// TypeEncoder defaultEncoder = new TypedTypeEncoder();
		TypedTypeEncoder dtrTe = new TypedTypeEncoder(
				dtrMapTypes, dtrMapRepresentations, null);
		Datatype dtShort = DatatypeMappingTest.getSimpleDatatypeFor(
				schemaAsString, "short", Constants.XML_SCHEMA_NS_URI);
		assertFalse("Should not be encodable",
				dtrTe.isValid(dtShort, new StringValue("XXX 12 XX")));
		// assertTrue(dtrTe.getRecentDtrMapDatatype() == null);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		try {
			// encode
			EXIResult exiResult = new EXIResult(exiFactory);
			exiResult.setOutputStream(baos);
			XMLReader xmlReader = XMLReaderFactory.createXMLReader();
			xmlReader.setContentHandler(exiResult.getHandler());
			xmlReader.parse(new InputSource(new ByteArrayInputStream(
					xmlAsString.getBytes())));

			fail("Invalid short value");
		} catch (IOException e) {
			// correct, error thrown
			assertTrue(true);
		} catch (SAXException e) {
			// correct, error thrown
			assertTrue(true);
		}

	}

	// 1. register subtype of integer (short) as string
	// 2. register integer as EXI Integer
	// --> short should still be encoded as String and "XXX 14 XX" should be
	// encodable
	public void testClosestAncestor2() throws IOException, EXIException,
			SAXException, TransformerException {
		String schemaAsString = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ "  <xs:element name='root'>"
				+ "   <xs:complexType>"
				+ "      <xs:sequence>"
				+ "         <xs:element name='shortVal' type='xs:short' />"
				+ "      </xs:sequence>"
				+ "    </xs:complexType>"
				+ "  </xs:element>" + "</xs:schema>" + "";

		// invalid short value treated as String
		String xmlAsString = "<root>" + "  <shortVal>XXX 14 XX</shortVal>"
				+ "</root>";

		GrammarFactory grammarFactory = GrammarFactory.newInstance();
		Grammars g = grammarFactory.createGrammars(new ByteArrayInputStream(
				schemaAsString.getBytes()));

		// factory with mapping
		EXIFactory exiFactory = DefaultEXIFactory.newInstance();
		exiFactory.setGrammars(g);
		exiFactory.setFidelityOptions(FidelityOptions.createStrict());
		/* DTR Map */
		QName type1 = new QName(Constants.XML_SCHEMA_NS_URI, "short");
		QName type2 = new QName(Constants.XML_SCHEMA_NS_URI, "integer");
		QName representation1 = new QName(Constants.W3C_EXI_NS_URI, "string");
		QName representation2 = new QName(Constants.W3C_EXI_NS_URI, "integer");
		QName[] dtrMapTypes = { type1, type2 };
		QName[] dtrMapRepresentations = { representation1, representation2 };
		exiFactory.setDatatypeRepresentationMap(dtrMapTypes,
				dtrMapRepresentations);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		// encode
		{
			EXIResult exiResult = new EXIResult(exiFactory);
			exiResult.setOutputStream(baos);
			XMLReader xmlReader = XMLReaderFactory.createXMLReader();
			xmlReader.setContentHandler(exiResult.getHandler());
			xmlReader.parse(new InputSource(new ByteArrayInputStream(
					xmlAsString.getBytes())));
		}

		// decode
		{
			InputStream is = new ByteArrayInputStream(baos.toByteArray());

			EXIBodyDecoder decoder = exiFactory.createEXIBodyDecoder();
			is.read(); // header
			decoder.setInputStream(is);

			EventType et = decoder.next();
			assertTrue(et == EventType.START_DOCUMENT);
			decoder.decodeStartDocument();

			et = decoder.next();
			assertTrue(et == EventType.START_ELEMENT);
			QName se = decoder.decodeStartElement().getQName();
			assertTrue(se.equals(new QName("", "root")));

			et = decoder.next();
			assertTrue(et == EventType.START_ELEMENT);
			se = decoder.decodeStartElement().getQName();
			assertTrue(se.equals(new QName("", "shortVal")));

			et = decoder.next();
			assertTrue(et == EventType.CHARACTERS);
			Value ch = decoder.decodeCharacters();
			assertTrue(ch.toString().equals("XXX 14 XX"));

			et = decoder.next();
			assertTrue(et == EventType.END_ELEMENT);
			QName ee = decoder.decodeEndElement().getQName();
			assertTrue(ee.equals(new QName("", "shortVal")));

			et = decoder.next();
			assertTrue(et == EventType.END_ELEMENT);
			ee = decoder.decodeEndElement().getQName();
			assertTrue(ee.equals(new QName("", "root")));

			et = decoder.next();
			assertTrue(et == EventType.END_DOCUMENT);
			decoder.decodeEndDocument();
		}

	}

	// register decimal, integer types should not be affected type directly
	public void testDecimalToString1() throws IOException, EXIException {
		String schemaAsString = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ "  <xs:simpleType name='decimal'>"
				+ "    <xs:restriction base='xs:decimal'>"
				+ "    </xs:restriction>"
				+ "  </xs:simpleType>"
				+ "  <xs:simpleType name='integer'>"
				+ "    <xs:restriction base='xs:int'>"
				+ "    </xs:restriction>"
				+ "  </xs:simpleType>"
				+ "</xs:schema>";

		// Grammars g = DatatypeMappingTest.getGrammarFor(schemaAsString);
		Datatype dtDecimal = DatatypeMappingTest.getSimpleDatatypeFor(
				schemaAsString, "decimal", "");
		assertTrue(dtDecimal.getBuiltInType() == BuiltInType.DECIMAL);
		QName schemaTypeDecimal = new QName("", "decimal");
		assertTrue(dtDecimal.getSchemaType().getQName()
				.equals(schemaTypeDecimal));

		Datatype dtInteger = DatatypeMappingTest.getSimpleDatatypeFor(
				schemaAsString, "integer", "");
		assertTrue(dtInteger.getBuiltInType() == BuiltInType.INTEGER);
		// IntegerDatatype idt = (IntegerDatatype) dtInteger;
		// assertTrue(idt.getIntegerType() == IntegerType.INTEGER_32);
		QName schemaTypeInteger = new QName("", "integer");
		assertTrue(dtInteger.getSchemaType().getQName()
				.equals(schemaTypeInteger));

		/* DTR Map */
		QName type = new QName(Constants.XML_SCHEMA_NS_URI, "decimal");
		QName representation = new QName(Constants.W3C_EXI_NS_URI, "string");
		QName[] dtrMapTypes = { type };
		QName[] dtrMapRepresentations = { representation };
		// TypeEncoder defaultEncoder = new TypedTypeEncoder();
		TypedTypeEncoder dtrTe = new TypedTypeEncoder(
				dtrMapTypes, dtrMapRepresentations, null);

		// decimals
		assertTrue(dtrTe.isValid(dtDecimal, new StringValue("+10")));
		assertTrue(dtrTe.lastDatatype.getBuiltInType() == BuiltInType.STRING);

		// integers
		assertTrue(dtrTe.isValid(dtInteger, new StringValue("+10")));
		// indicates no dtr map is in use (default)
		assertTrue(dtrTe.lastDatatype.getBuiltInType() == BuiltInType.INTEGER);
	}

	// register xsd:integer to exi:integer, integer types should not be affected
	// type directly
	public void testIntegerToInteger1() throws IOException, EXIException {
		String schemaAsString = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ "  <xs:simpleType name='integer'>"
				+ "    <xs:restriction base='xs:int'>"
				+ "    </xs:restriction>"
				+ "  </xs:simpleType>"
				+ "</xs:schema>";
		// Grammars g = DatatypeMappingTest.getGrammarFor(schemaAsString);

		Datatype dtInteger = DatatypeMappingTest.getSimpleDatatypeFor(
				schemaAsString, "integer", "");
		assertTrue(dtInteger.getBuiltInType() == BuiltInType.INTEGER);
		// IntegerDatatype idt = (IntegerDatatype) dtInteger;
		// assertTrue(idt.getIntegerType() == IntegerType.INTEGER_32);
		QName schemaTypeInteger = new QName("", "integer");
		assertTrue(dtInteger.getSchemaType().getQName()
				.equals(schemaTypeInteger));

		/* DTR Map */
		QName type = new QName(Constants.XML_SCHEMA_NS_URI, "integer");
		QName representation = new QName(Constants.W3C_EXI_NS_URI, "integer");
		QName[] dtrMapTypes = { type };
		QName[] dtrMapRepresentations = { representation };
		// TypeEncoder defaultEncoder = new TypedTypeEncoder();
		TypedTypeEncoder dtrTe = new TypedTypeEncoder(
				dtrMapTypes, dtrMapRepresentations, null);

		// integers
		assertTrue(dtrTe.isValid(dtInteger, new StringValue("+10")));
		// default datatype
		assertTrue(dtrTe.lastDatatype.getBuiltInType() == BuiltInType.INTEGER);
	}

	// register xsd:int to exi:integer, integer types should not be affected
	// type directly
	public void testIntegerToInteger2() throws IOException, EXIException {
		String schemaAsString = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ "  <xs:simpleType name='integer'>"
				+ "    <xs:restriction base='xs:int'>"
				+ "    </xs:restriction>"
				+ "  </xs:simpleType>"
				+ "</xs:schema>";
		// Grammars g = DatatypeMappingTest.getGrammarFor(schemaAsString);

		Datatype dtInteger = DatatypeMappingTest.getSimpleDatatypeFor(
				schemaAsString, "integer", "");
		assertTrue(dtInteger.getBuiltInType() == BuiltInType.INTEGER);
		// IntegerDatatype idt = (IntegerDatatype) dtInteger;
		// assertTrue(idt.getIntegerType() == IntegerType.INTEGER_32);
		QName schemaTypeInteger = new QName("", "integer");
		assertTrue(dtInteger.getSchemaType().getQName()
				.equals(schemaTypeInteger));

		/* DTR Map */
		QName type = new QName(Constants.XML_SCHEMA_NS_URI, "int");
		QName representation = new QName(Constants.W3C_EXI_NS_URI, "integer");
		QName[] dtrMapTypes = { type };
		QName[] dtrMapRepresentations = { representation };
		// TypeEncoder defaultEncoder = new TypedTypeEncoder();
		TypedTypeEncoder dtrTe = new TypedTypeEncoder(
				dtrMapTypes, dtrMapRepresentations, null);

		// integers
		assertTrue(dtrTe.isValid(dtInteger, new StringValue("+10")));
		// default mapping
		assertTrue(dtrTe.lastDatatype.getBuiltInType() == BuiltInType.INTEGER);
	}
	
	
	public void testUnsignedIntegerNotUsed1() throws IOException, EXIException {
		String schemaAsString = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ "  <xs:simpleType name='uinteger'>"
				+ "    <xs:restriction base='xs:unsignedInt'>"
				+ "    </xs:restriction>"
				+ "  </xs:simpleType>"
				+ "</xs:schema>";
		// Grammars g = DatatypeMappingTest.getGrammarFor(schemaAsString);

		Datatype dtInteger = DatatypeMappingTest.getSimpleDatatypeFor(
				schemaAsString, "uinteger", "");
		assertTrue(dtInteger.getBuiltInType() == BuiltInType.UNSIGNED_INTEGER);
		// IntegerDatatype idt = (IntegerDatatype) dtInteger;
		// assertTrue(idt.getIntegerType() == IntegerType.INTEGER_32);
		QName schemaTypeInteger = new QName("", "uinteger");
		assertTrue(dtInteger.getSchemaType().getQName()
				.equals(schemaTypeInteger));

		/* DTR Map */
		QName type = new QName(Constants.XML_SCHEMA_NS_URI, "positiveInteger");
		QName representation = new QName(Constants.W3C_EXI_NS_URI, "integer");
		QName[] dtrMapTypes = { type };
		QName[] dtrMapRepresentations = { representation };
		// TypeEncoder defaultEncoder = new TypedTypeEncoder();
		TypedTypeEncoder dtrTe = new TypedTypeEncoder(
				dtrMapTypes, dtrMapRepresentations, null);

		// integers
		assertFalse(dtrTe.isValid(dtInteger, new StringValue("-10")));
		// default datatype
		assertTrue(dtrTe.lastDatatype.getBuiltInType() == BuiltInType.UNSIGNED_INTEGER);
	}
	
	public void testUnsignedIntegerNotUsed2() throws IOException, EXIException {
		String schemaAsString = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ "  <xs:simpleType name='ubyte'>"
				+ "    <xs:restriction base='xs:unsignedByte'>"
				+ "    </xs:restriction>"
				+ "  </xs:simpleType>"
				+ "</xs:schema>";
		// Grammars g = DatatypeMappingTest.getGrammarFor(schemaAsString);

		Datatype dtInteger = DatatypeMappingTest.getSimpleDatatypeFor(
				schemaAsString, "ubyte", "");
		assertTrue(dtInteger.getBuiltInType() == BuiltInType.NBIT_UNSIGNED_INTEGER);
		// IntegerDatatype idt = (IntegerDatatype) dtInteger;
		// assertTrue(idt.getIntegerType() == IntegerType.INTEGER_32);
		QName schemaTypeInteger = new QName("", "ubyte");
		assertTrue(dtInteger.getSchemaType().getQName()
				.equals(schemaTypeInteger));

		/* DTR Map */
		QName type = new QName(Constants.XML_SCHEMA_NS_URI, "positiveInteger");
		QName representation = new QName(Constants.W3C_EXI_NS_URI, "integer");
		QName[] dtrMapTypes = { type };
		QName[] dtrMapRepresentations = { representation };
		// TypeEncoder defaultEncoder = new TypedTypeEncoder();
		TypedTypeEncoder dtrTe = new TypedTypeEncoder(
				dtrMapTypes, dtrMapRepresentations, null);

		// integers
		assertFalse(dtrTe.isValid(dtInteger, new StringValue("-10")));
		// default datatype
		assertTrue(dtrTe.lastDatatype.getBuiltInType() == BuiltInType.NBIT_UNSIGNED_INTEGER);
	}
	
	public void testUnsignedIntegerUsed1() throws IOException, EXIException {
		String schemaAsString = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ "  <xs:simpleType name='uinteger'>"
				+ "    <xs:restriction base='xs:unsignedInt'>"
				+ "    </xs:restriction>"
				+ "  </xs:simpleType>"
				+ "</xs:schema>";
		// Grammars g = DatatypeMappingTest.getGrammarFor(schemaAsString);

		Datatype dtInteger = DatatypeMappingTest.getSimpleDatatypeFor(
				schemaAsString, "uinteger", "");
		assertTrue(dtInteger.getBuiltInType() == BuiltInType.UNSIGNED_INTEGER);
		// IntegerDatatype idt = (IntegerDatatype) dtInteger;
		// assertTrue(idt.getIntegerType() == IntegerType.INTEGER_32);
		QName schemaTypeInteger = new QName("", "uinteger");
		assertTrue(dtInteger.getSchemaType().getQName()
				.equals(schemaTypeInteger));

		/* DTR Map */
		QName type = new QName(Constants.XML_SCHEMA_NS_URI, "integer");
		QName representation = new QName(Constants.W3C_EXI_NS_URI, "integer");
		QName[] dtrMapTypes = { type };
		QName[] dtrMapRepresentations = { representation };
		// TypeEncoder defaultEncoder = new TypedTypeEncoder();
		TypedTypeEncoder dtrTe = new TypedTypeEncoder(
				dtrMapTypes, dtrMapRepresentations, null);

		// integers
		assertTrue(dtrTe.isValid(dtInteger, new StringValue("10")));
		assertFalse(dtrTe.isValid(dtInteger, new StringValue("-10"))); // still unsigned integer
		// default datatype
		assertTrue(dtrTe.lastDatatype.getBuiltInType() == BuiltInType.UNSIGNED_INTEGER);
	}
	
	
	
	public void testTokenToInteger() throws IOException, EXIException {
		String schemaAsString = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ "  <xs:simpleType name='myToken'>"
				+ "    <xs:restriction base='xs:token'>"
				+ "    </xs:restriction>"
				+ "  </xs:simpleType>"
				+ "</xs:schema>";
		// Grammars g = DatatypeMappingTest.getGrammarFor(schemaAsString);

		Datatype dtToken = DatatypeMappingTest.getSimpleDatatypeFor(
				schemaAsString, "myToken", "");
		assertTrue(dtToken.getBuiltInType() == BuiltInType.STRING);
		
		QName schemaTypeInteger = new QName("", "myToken");
		assertTrue(dtToken.getSchemaType().getQName()
				.equals(schemaTypeInteger));
		DatatypeID dtID = dtToken.getDatatypeID();
		assertTrue(dtID == DatatypeID.exi_string);

		/* DTR Map */
		QName type = new QName(Constants.XML_SCHEMA_NS_URI, "token");
		QName representation = new QName(Constants.W3C_EXI_NS_URI, "integer");
		QName[] dtrMapTypes = { type };
		QName[] dtrMapRepresentations = { representation };
		// TypeEncoder defaultEncoder = new TypedTypeEncoder();
		TypedTypeEncoder dtrTe = new TypedTypeEncoder(
				dtrMapTypes, dtrMapRepresentations, null);
		LexicalTypeEncoder dtrLe = new LexicalTypeEncoder(
				dtrMapTypes, dtrMapRepresentations, null);

		// integers
		assertTrue(dtrTe.isValid(dtToken, new StringValue("+1234567890")));
		assertFalse(dtrTe.isValid(dtToken, new StringValue("ABC")));
		assertTrue(dtrLe.isValid(dtToken, new StringValue("+1234567890")));
		// mapping
		assertTrue(dtrTe.lastDatatype.getBuiltInType() == BuiltInType.INTEGER);
		assertTrue(dtrLe.lastDatatype.getBuiltInType() == BuiltInType.INTEGER);
	}
	

	// unions
	// dtr-11
	public void testUnion1() throws IOException, EXIException {
		String schemaAsString = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ "  <xs:simpleType name='unionType'>"
				+ "    <xs:union memberTypes='xs:int'/>"
				+ "  </xs:simpleType>"
				+ "</xs:schema>";
		// Grammars g = DatatypeMappingTest.getGrammarFor(schemaAsString);

		Datatype dtString = DatatypeMappingTest.getSimpleDatatypeFor(
				schemaAsString, "unionType", "");
		assertTrue(dtString.getBuiltInType() == BuiltInType.STRING);

		QName schemaTypeInteger = new QName("", "unionType");
		assertTrue(dtString.getSchemaType().getQName()
				.equals(schemaTypeInteger));

		/* DTR Map */
		QName type = new QName(Constants.XML_SCHEMA_NS_URI,
				"anySimpleType");
		QName representation = new QName(Constants.W3C_EXI_NS_URI, "decimal");
		QName[] dtrMapTypes = { type };
		QName[] dtrMapRepresentations = { representation };
		// TypeEncoder defaultEncoder = new TypedTypeEncoder();
		TypedTypeEncoder dtrTe = new TypedTypeEncoder(
				dtrMapTypes, dtrMapRepresentations, null);

		//
		assertTrue(dtrTe.isValid(dtString, new StringValue("12345")));

		// indicates that no dtr map is in use
		assertTrue(dtrTe.lastDatatype.getBuiltInType() == BuiltInType.STRING);
	}

	// dtr-09
	public void testUnion2() throws IOException, EXIException {
		String schemaAsString = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ "  <xs:simpleType name='unionType'>"
				+ "    <xs:union memberTypes='xs:int'/>"
				+ "  </xs:simpleType>"
				+ "</xs:schema>";
		// Grammars g = DatatypeMappingTest.getGrammarFor(schemaAsString);

		Datatype dtString = DatatypeMappingTest.getSimpleDatatypeFor(
				schemaAsString, "unionType", "");
		assertTrue(dtString.getBuiltInType() == BuiltInType.STRING);

		QName schemaTypeInteger = new QName("", "unionType");
		assertTrue(dtString.getSchemaType().getQName()
				.equals(schemaTypeInteger));

		/* DTR Map */
		QName type = new QName("", "unionType");
		QName representation = new QName(Constants.W3C_EXI_NS_URI, "decimal");
		QName[] dtrMapTypes = { type };
		QName[] dtrMapRepresentations = { representation };
		// TypeEncoder defaultEncoder = new TypedTypeEncoder();
		TypedTypeEncoder dtrTe = new TypedTypeEncoder(
				dtrMapTypes, dtrMapRepresentations, null);

		//
		assertTrue(dtrTe.isValid(dtString, new StringValue("12345")));

		// dtr map is in use
		assertTrue(dtrTe.lastDatatype.getBuiltInType() == BuiltInType.DECIMAL);
	}

	// dtr-10
	// <!-- "123.45" should be encoded using exi:decimal as designated by an -->
	// <!-- DTRM entry pegged at the base datatype that is an union datatype. -->
	public void testUnion3() throws IOException, EXIException {
		String schemaAsString = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema' targetNamespace='urn:foo' xmlns:foo='urn:foo'>"
				+ "  <xs:simpleType name='unionTypeDerived'>"
				+ "    <xs:restriction base='foo:unionType'>"
				+ "      <xs:enumeration value='12345'/>"
				+ "    </xs:restriction>"
				+ "  </xs:simpleType>"

				+ "  <xs:simpleType name='unionType'>"
				+ "    <xs:union memberTypes='xs:int'/>"
				+ "  </xs:simpleType>"
				+ "</xs:schema>";
		// Grammars g = DatatypeMappingTest.getGrammarFor(schemaAsString);

		Datatype dtString = DatatypeMappingTest.getSimpleDatatypeFor(
				schemaAsString, "unionTypeDerived", "urn:foo");
		assertTrue(dtString.getBuiltInType() == BuiltInType.STRING);

		QName schemaTypeInteger = new QName("urn:foo", "unionTypeDerived");
		assertTrue(dtString.getSchemaType().getQName()
				.equals(schemaTypeInteger));

		/* DTR Map */
		QName type = new QName("urn:foo", "unionType");
		QName representation = new QName(Constants.W3C_EXI_NS_URI, "decimal");
		QName[] dtrMapTypes = { type };
		QName[] dtrMapRepresentations = { representation };
		// TypeEncoder defaultEncoder = new TypedTypeEncoder();
		TypedTypeEncoder dtrTe = new TypedTypeEncoder(
				dtrMapTypes, dtrMapRepresentations, null);

		//
		assertTrue(dtrTe.isValid(dtString, new StringValue("12345")));

		// dtr map in use
		assertTrue(dtrTe.lastDatatype.getBuiltInType() == BuiltInType.DECIMAL);
	}

	// lists
	// dtr-15
	// A DTRM entry is pegged at xsd:anySimpleType, which should not affect list
	// datatype encodings.
	public void testList1() throws IOException, EXIException {
		String schemaAsString = "<xsd:schema xmlns:xsd='http://www.w3.org/2001/XMLSchema' targetNamespace='urn:foo' xmlns:foo='urn:foo' >"
				+ "  <xsd:simpleType name='listType'>"
				+ "    <xsd:list itemType='xsd:int'/>"
				+ "  </xsd:simpleType>"
				+ "</xsd:schema>";
		// Grammars g = DatatypeMappingTest.getGrammarFor(schemaAsString);

		Datatype dtString = DatatypeMappingTest.getSimpleDatatypeFor(
				schemaAsString, "listType", "urn:foo");
		assertTrue(dtString.getBuiltInType() == BuiltInType.LIST);

		QName schemaTypeInteger = new QName("urn:foo", "listType");
		assertTrue(dtString.getSchemaType().getQName()
				.equals(schemaTypeInteger));

		/* DTR Map */
		QName type = new QName(Constants.XML_SCHEMA_NS_URI,
				"anySimpleType");
		QName representation = new QName(Constants.W3C_EXI_NS_URI, "string");
		// QName representation = new QName(Constants.W3C_EXI_NS_URI,
		// "decimal");
		QName[] dtrMapTypes = { type };
		QName[] dtrMapRepresentations = { representation };
		// TypeEncoder defaultEncoder = new TypedTypeEncoder();
		TypedTypeEncoder dtrTe = new TypedTypeEncoder(
				dtrMapTypes, dtrMapRepresentations, null);

		//
		assertTrue(dtrTe.isValid(dtString, new StringValue("12345")));

		// indicates that no dtr map is in use
		assertTrue(dtrTe.lastDatatype.getBuiltInType() == BuiltInType.LIST);
		ListDatatype ld = (ListDatatype) dtrTe.lastDatatype;
		assertTrue(ld.getListDatatype().getBuiltInType() == BuiltInType.INTEGER);
	}

	// dtr-15 MODIFIED!
	public void testList2() throws IOException, EXIException {
		String schemaAsString = "<xsd:schema xmlns:xsd='http://www.w3.org/2001/XMLSchema' targetNamespace='urn:foo' xmlns:foo='urn:foo' >"
				+ "  <xsd:simpleType name='listType'>"
				+ "    <xsd:list itemType='xsd:int'/>"
				+ "  </xsd:simpleType>"
				+ "</xsd:schema>";
		// Grammars g = DatatypeMappingTest.getGrammarFor(schemaAsString);

		Datatype dtString = DatatypeMappingTest.getSimpleDatatypeFor(
				schemaAsString, "listType", "urn:foo");
		assertTrue(dtString.getBuiltInType() == BuiltInType.LIST);

		QName schemaTypeInteger = new QName("urn:foo", "listType");
		assertTrue(dtString.getSchemaType().getQName()
				.equals(schemaTypeInteger));

		/* DTR Map */
		QName type = new QName("urn:foo", "listType");
		QName representation = new QName(Constants.W3C_EXI_NS_URI, "string");
		QName[] dtrMapTypes = { type };
		QName[] dtrMapRepresentations = { representation };
		// TypeEncoder defaultEncoder = new TypedTypeEncoder();
		TypedTypeEncoder dtrTe = new TypedTypeEncoder(
				dtrMapTypes, dtrMapRepresentations, null);

		//
		assertTrue(dtrTe.isValid(dtString, new StringValue("12345")));

		// null indicates that no dtr map is in use
		 assertTrue(dtrTe.lastDatatype.getBuiltInType() == BuiltInType.STRING);
	}

	// dtr-14
	// A DTRM entry is pegged at a base datatype that is a list datatype.
	public void testList3() throws IOException, EXIException {
		String schemaAsString = "<xsd:schema xmlns:xsd='http://www.w3.org/2001/XMLSchema' targetNamespace='urn:foo' xmlns:foo='urn:foo' >"
				+ "  <xsd:simpleType name='listTypeDerived'>"
				+ "    <xsd:restriction base='foo:listType'>"
				+ "      <xsd:length value='4'/>"
				+ "    </xsd:restriction>"
				+ "  </xsd:simpleType>"
				+ "  <xsd:simpleType name='listType'>"
				+ "    <xsd:list itemType='xsd:int'/>"
				+ "  </xsd:simpleType>"
				+ "</xsd:schema>";
		// Grammars g = DatatypeMappingTest.getGrammarFor(schemaAsString);

		Datatype dtString = DatatypeMappingTest.getSimpleDatatypeFor(
				schemaAsString, "listTypeDerived", "urn:foo");
		assertTrue(dtString.getBuiltInType() == BuiltInType.LIST);

		QName schemaTypeInteger = new QName("urn:foo", "listTypeDerived");
		assertTrue(dtString.getSchemaType().getQName()
				.equals(schemaTypeInteger));

		/* DTR Map */
		QName type = new QName("urn:foo", "listType");
		QName representation = new QName(Constants.W3C_EXI_NS_URI, "string");
		QName[] dtrMapTypes = { type };
		QName[] dtrMapRepresentations = { representation };
		// TypeEncoder defaultEncoder = new TypedTypeEncoder();
		TypedTypeEncoder dtrTe = new TypedTypeEncoder(
				dtrMapTypes, dtrMapRepresentations, null);

		//
		assertTrue(dtrTe.isValid(dtString, new StringValue("12345")));

		// null indicates that no dtr map is in use
		 assertTrue(dtrTe.lastDatatype.getBuiltInType() == BuiltInType.STRING);
	}

	// dtr-16
	// A DTRM entry is pegged at the item type of a list datatype.
	// should be encoded as a list of strings instead of a list of ints.
	public void testList4() throws IOException, EXIException {
		String schemaAsString = "<xsd:schema xmlns:xsd='http://www.w3.org/2001/XMLSchema' targetNamespace='urn:foo' xmlns:foo='urn:foo' >"
				+ "  <xsd:simpleType name='listTypeDerived'>"
				+ "    <xsd:restriction base='foo:listType'>"
				+ "      <xsd:length value='4'/>"
				+ "    </xsd:restriction>"
				+ "  </xsd:simpleType>"
				+ "  <xsd:simpleType name='listType'>"
				+ "    <xsd:list itemType='xsd:int'/>"
				+ "  </xsd:simpleType>"
				+ "</xsd:schema>";
		// Grammars g = DatatypeMappingTest.getGrammarFor(schemaAsString);

		Datatype dt = DatatypeMappingTest.getSimpleDatatypeFor(schemaAsString,
				"listType", "urn:foo");
		assertTrue(dt.getBuiltInType() == BuiltInType.LIST);
		ListDatatype ldt = (ListDatatype) dt;
		assertTrue(ldt.getListDatatype().getBuiltInType() == BuiltInType.INTEGER);

		QName schemaTypeInteger = new QName("urn:foo", "listType");
		assertTrue(dt.getSchemaType().getQName().equals(schemaTypeInteger));

		/* DTR Map */
		QName type = new QName(Constants.XML_SCHEMA_NS_URI, "int");
		QName representation = new QName(Constants.W3C_EXI_NS_URI, "string");
		QName[] dtrMapTypes = { type };
		QName[] dtrMapRepresentations = { representation };
		// TypeEncoder defaultEncoder = new TypedTypeEncoder();
		TypedTypeEncoder dtrTe = new TypedTypeEncoder(
				dtrMapTypes, dtrMapRepresentations, null);

		//
		assertTrue(dtrTe.isValid(dt, new StringValue("any string")));
		assertTrue(dtrTe.isValid(dt, new StringValue("12345")));

		// indicates that no dtr map is in use
		assertTrue(dtrTe.lastDatatype.getBuiltInType() == BuiltInType.LIST);
		ListDatatype ld = (ListDatatype) dtrTe.lastDatatype;
		assertTrue(ld.getListDatatype().getBuiltInType() == BuiltInType.STRING);
	}
	
	public void testNoDTRChangeDatatypes() throws IOException, EXIException, SAXException, TransformerException {
		String xsd = "./data/general/datatypes.xsd";
		String xml = "./data/general/datatypes.xml";
		testNoDTR(new FileInputStream(xsd), new FileInputStream(xml));
	}
	
	public void testNoDTRChangeDatatypes2() throws IOException, EXIException, SAXException, TransformerException {
		String xsd = "./data/general/datatypes2.xsd";
		String xml = "./data/general/datatypes2.xml";
		testNoDTR(new FileInputStream(xsd), new FileInputStream(xml));
	}
	
	private static void testNoDTR(InputStream xsd, InputStream xml) throws EXIException, SAXException, IOException, TransformerException {
		/* senseless DTR Map */
		QName type = new QName("", "FooUnknownXYZ");
		QName representation = new QName(Constants.W3C_EXI_NS_URI, "boolean");
		QName[] dtrMapTypes = { type };
		QName[] dtrMapRepresentations = { representation };
		
		// factory
		EXIFactory ef = DefaultEXIFactory.newInstance();
		ef.setFidelityOptions(FidelityOptions.createStrict());
		ef.setGrammars(GrammarFactory.newInstance().createGrammars(xsd));
		ef.setDatatypeRepresentationMap(dtrMapTypes, dtrMapRepresentations);
		
		// unset DTR
		ef.setDatatypeRepresentationMap(null, null);
		// encode with unknown DTR
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		EXIResult exiResult = new EXIResult(ef);
		exiResult.setOutputStream(baos);
		XMLReader xmlReader = XMLReaderFactory.createXMLReader();
		xmlReader.setContentHandler( exiResult.getHandler() );
		xmlReader.parse(new InputSource(xml)); // parse XML input
		
		System.out.println("Size EXI " + baos.size());

		// decode without any DTR --> interoperability
		OutputStream baosXML = new ByteArrayOutputStream();
		Result result = new StreamResult(baosXML);
		InputSource is = new InputSource(new ByteArrayInputStream(baos.toByteArray()));
		SAXSource exiSource = new EXISource(ef);
		exiSource.setInputSource(is);
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		transformer.transform(exiSource, result);
	}
	
	public void testNoDTRChangeCSS() throws IOException, EXIException, SAXException, TransformerException {
		String xsdAsString = "<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" elementFormDefault=\"qualified\">\r\n" + 
				"  <xs:element name=\"stylesheet\">\r\n" + 
				"    <xs:complexType>\r\n" + 
				"      <xs:sequence>\r\n" + 
				"\r\n" + 
				"        <xs:element name=\"cssCharsetRule\" minOccurs=\"0\" type=\"xs:string\"/>\r\n" + 
				"        \r\n" + 
				"        <xs:element name=\"cssImportRule\" minOccurs=\"0\" maxOccurs=\"unbounded\">\r\n" + 
				"          <xs:complexType>\r\n" + 
				"            <xs:sequence>\r\n" + 
				"              <xs:element name=\"href\" type=\"xs:string\" />\r\n" + 
				"              <xs:element name=\"mediaList\" minOccurs=\"0\">\r\n" + 
				"                <xs:simpleType>\r\n" + 
				"                  <xs:list itemType=\"xs:string\"/>\r\n" + 
				"                </xs:simpleType>\r\n" + 
				"              </xs:element>\r\n" + 
				"            </xs:sequence>\r\n" + 
				"          </xs:complexType>\r\n" + 
				"        </xs:element>\r\n" + 
				"        \r\n" + 
				"        <xs:sequence minOccurs=\"0\" maxOccurs=\"unbounded\">\r\n" + 
				"          \r\n" + 
				"          <xs:element ref=\"cssStyleRule\" minOccurs=\"0\"/>\r\n" + 
				"          \r\n" + 
				"          <xs:element name=\"cssFontFaceRule\" minOccurs=\"0\">\r\n" + 
				"            <xs:complexType>\r\n" + 
				"              <xs:sequence>\r\n" + 
				"                <xs:element ref=\"style\" />\r\n" + 
				"              </xs:sequence>\r\n" + 
				"            </xs:complexType>\r\n" + 
				"          </xs:element>\r\n" + 
				"\r\n" + 
				"          <xs:element name=\"cssMediaRule\" minOccurs=\"0\">\r\n" + 
				"            <xs:complexType>\r\n" + 
				"              <xs:sequence>\r\n" + 
				"                <xs:element name=\"mediaList\">\r\n" + 
				"                  <xs:simpleType>\r\n" + 
				"                    <xs:list itemType=\"xs:string\"/>\r\n" + 
				"                  </xs:simpleType>\r\n" + 
				"                </xs:element>\r\n" + 
				"                \r\n" + 
				"                <xs:element ref=\"cssStyleRule\" minOccurs=\"0\" maxOccurs=\"unbounded\"/>\r\n" + 
				"              </xs:sequence>\r\n" + 
				"            </xs:complexType>\r\n" + 
				"          </xs:element>\r\n" + 
				"          \r\n" + 
				"          <xs:element name=\"cssPageRule\" minOccurs=\"0\">\r\n" + 
				"            <xs:complexType>\r\n" + 
				"              <xs:sequence>\r\n" + 
				"                <xs:element ref=\"selectorText\" />\r\n" + 
				"                <xs:element ref=\"style\" />\r\n" + 
				"              </xs:sequence>\r\n" + 
				"            </xs:complexType>\r\n" + 
				"          </xs:element>\r\n" + 
				"          \r\n" + 
				"          <xs:element name=\"cssUnknownRule\" minOccurs=\"0\" type=\"xs:string\"/>\r\n" + 
				"          \r\n" + 
				"        </xs:sequence>\r\n" + 
				"\r\n" + 
				"      </xs:sequence>\r\n" + 
				"    </xs:complexType>\r\n" + 
				"  </xs:element>\r\n" + 
				"\r\n" + 
				"  <xs:element name=\"cssStyleRule\">\r\n" + 
				"    <xs:complexType>\r\n" + 
				"      <xs:sequence>\r\n" + 
				"        <xs:element ref=\"selectorText\" />\r\n" + 
				"        <xs:element ref=\"style\" />\r\n" + 
				"      </xs:sequence>\r\n" + 
				"    </xs:complexType>\r\n" + 
				"  </xs:element>\r\n" + 
				"  \r\n" + 
				"  <!-- selectorText: one string vs. separate selectors -->\r\n" + 
				"  <!--  <xs:element name=\"selectorText\" type=\"xs:string\"/>  -->\r\n" + 
				"  <xs:element name=\"selectorText\">\r\n" + 
				"    <xs:simpleType>\r\n" + 
				"      <xs:list itemType=\"xs:string\"/>\r\n" + 
				"    </xs:simpleType>\r\n" + 
				"  </xs:element>\r\n" + 
				"  \r\n" + 
				"  <xs:element name=\"style\">\r\n" + 
				"    <xs:complexType>\r\n" + 
				"      <xs:sequence minOccurs=\"0\" maxOccurs=\"unbounded\">\r\n" + 
				"        <xs:element name=\"property\" type=\"propertyType\"/>\r\n" + 
				"        <xs:element ref=\"cssValue\"/>\r\n" + 
				"        <xs:element name=\"priority\" type=\"xs:string\" minOccurs=\"0\"/>\r\n" + 
				"      </xs:sequence>\r\n" + 
				"    </xs:complexType>\r\n" + 
				"  </xs:element>\r\n" + 
				"  <xs:simpleType name=\"propertyType\">\r\n" + 
				"    <xs:restriction base=\"xs:string\"/>\r\n" + 
				"    <!--<xs:restriction base=\"cssProperty\"/>-->\r\n" + 
				"  </xs:simpleType>\r\n" + 
				"\r\n" + 
				"  <xs:element name=\"cssValue\">\r\n" + 
				"    <xs:complexType>\r\n" + 
				"      <xs:choice>\r\n" + 
				"        <xs:element ref=\"cssPrimitiveValue\"/>\r\n" + 
				"        <xs:element ref=\"cssValueList\"/>\r\n" + 
				"        <xs:element ref=\"cssInherit\"/>\r\n" + 
				"      </xs:choice>\r\n" + 
				"    </xs:complexType>\r\n" + 
				"  </xs:element>\r\n" + 
				"\r\n" + 
				"  <xs:element name=\"cssValueList\">\r\n" + 
				"    <xs:complexType>\r\n" + 
				"      <xs:choice minOccurs=\"0\" maxOccurs=\"unbounded\">\r\n" + 
				"        <xs:element ref=\"cssPrimitiveValue\"/>\r\n" + 
				"        <xs:element ref=\"cssValueList\"/>\r\n" + 
				"      </xs:choice>\r\n" + 
				"    </xs:complexType>\r\n" + 
				"  </xs:element>\r\n" + 
				"\r\n" + 
				"  <xs:element name=\"cssInherit\">\r\n" + 
				"    <xs:complexType/>\r\n" + 
				"  </xs:element>\r\n" + 
				"\r\n" + 
				"  <xs:element name=\"cssPrimitiveValue\" type=\"cssPrimitiveValueType\"/>\r\n" + 
				"\r\n" + 
				"  <xs:complexType name=\"cssPrimitiveValueType\">\r\n" + 
				"    <xs:choice>\r\n" + 
				"      <xs:element name=\"cssUnknown\" type=\"xs:string\"/>\r\n" + 
				"      <xs:element name=\"cssNumber\" type=\"xs:double\"/>\r\n" + 
				"      <xs:element name=\"cssPercentage\" type=\"xs:double\"/>\r\n" + 
				"      <xs:element name=\"cssEms\" type=\"xs:double\"/>\r\n" + 
				"      <xs:element name=\"cssExs\" type=\"xs:double\"/>\r\n" + 
				"      <xs:element name=\"cssPx\" type=\"xs:double\"/>\r\n" + 
				"      <xs:element name=\"cssCm\" type=\"xs:double\"/>\r\n" + 
				"      <xs:element name=\"cssMm\" type=\"xs:double\"/>\r\n" + 
				"      <xs:element name=\"cssIn\" type=\"xs:double\"/>\r\n" + 
				"      <xs:element name=\"cssPt\" type=\"xs:double\"/>\r\n" + 
				"      <xs:element name=\"cssPc\" type=\"xs:double\"/>\r\n" + 
				"      <xs:element name=\"cssDeg\" type=\"xs:double\"/>\r\n" + 
				"      <xs:element name=\"cssRad\" type=\"xs:double\"/>\r\n" + 
				"      <xs:element name=\"cssGrad\" type=\"xs:double\"/>\r\n" + 
				"      <xs:element name=\"cssMs\" type=\"xs:double\"/>\r\n" + 
				"      <xs:element name=\"cssS\" type=\"xs:double\"/>\r\n" + 
				"      <xs:element name=\"cssHz\" type=\"xs:double\"/>\r\n" + 
				"      <xs:element name=\"cssKhz\" type=\"xs:double\"/>\r\n" + 
				"      <xs:element name=\"cssDimension\" type=\"xs:double\"/>\r\n" + 
				"      <xs:element name=\"cssString\" type=\"xs:string\"/>\r\n" + 
				"      <xs:element name=\"cssUri\" type=\"xs:string\"/>\r\n" + 
				"      <xs:element name=\"cssIdent\" type=\"xs:string\"/>\r\n" + 
				"      <xs:element name=\"cssAttr\" type=\"xs:string\"/>\r\n" + 
				"      <xs:element name=\"cssCounter\">\r\n" + 
				"        <xs:complexType>\r\n" + 
				"          <xs:sequence>\r\n" + 
				"            <!-- TODO what is required and what not -->\r\n" + 
				"            <xs:element name=\"identifier\" type=\"xs:string\"/>\r\n" + 
				"            <xs:element name=\"listStyle\" type=\"xs:string\" minOccurs=\"0\"/>\r\n" + 
				"            <xs:element name=\"separator\" type=\"xs:string\" minOccurs=\"0\"/>\r\n" + 
				"          </xs:sequence>\r\n" + 
				"        </xs:complexType>\r\n" + 
				"      </xs:element>\r\n" + 
				"      <xs:element name=\"cssRect\">\r\n" + 
				"        <xs:complexType>\r\n" + 
				"          <xs:sequence>\r\n" + 
				"            <!-- TODO what is required and what not -->\r\n" + 
				"            <xs:element name=\"top\" type=\"cssPrimitiveValueType\"/>\r\n" + 
				"            <xs:element name=\"right\" type=\"cssPrimitiveValueType\"/>\r\n" + 
				"            <xs:element name=\"bottom\" type=\"cssPrimitiveValueType\"/>\r\n" + 
				"            <xs:element name=\"left\" type=\"cssPrimitiveValueType\"/>\r\n" + 
				"          </xs:sequence>\r\n" + 
				"        </xs:complexType>\r\n" + 
				"      </xs:element>\r\n" + 
				"      <xs:element name=\"cssRgbColor\">\r\n" + 
				"        <xs:complexType>\r\n" + 
				"          <xs:sequence>\r\n" + 
				"            <xs:element name=\"r\" type=\"xs:unsignedByte\"/>\r\n" + 
				"            <xs:element name=\"g\" type=\"xs:unsignedByte\"/>\r\n" + 
				"            <xs:element name=\"b\" type=\"xs:unsignedByte\"/>\r\n" + 
				"          </xs:sequence>\r\n" + 
				"        </xs:complexType>\r\n" + 
				"      </xs:element>\r\n" + 
				"    </xs:choice>\r\n" + 
				"  </xs:complexType>\r\n" + 
				"  \r\n" + 
				"  <!-- CSS property list -->\r\n" + 
				"  <!-- taken from http://www.w3schools.com/cssref/ -->\r\n" + 
				"  <xs:simpleType name=\"cssProperty\">\r\n" + 
				"    <xs:restriction base=\"xs:string\">\r\n" + 
				"      <!-- Color Properties -->\r\n" + 
				"      <xs:enumeration value=\"color\"/>\r\n" + 
				"      <xs:enumeration value=\"opacity\"/>\r\n" + 
				"      \r\n" + 
				"      <!-- Background and Border Properties -->\r\n" + 
				"      <xs:enumeration value=\"background\"/>\r\n" + 
				"      <xs:enumeration value=\"background-attachment\"/>\r\n" + 
				"      <xs:enumeration value=\"background-blend-mode\"/>\r\n" + 
				"      <xs:enumeration value=\"background-color\"/>\r\n" + 
				"      <xs:enumeration value=\"background-image\"/>\r\n" + 
				"      <xs:enumeration value=\"background-position\"/>\r\n" + 
				"      <xs:enumeration value=\"background-repeat\"/>\r\n" + 
				"      <xs:enumeration value=\"background-clip\"/>\r\n" + 
				"      <xs:enumeration value=\"background-origin\"/>\r\n" + 
				"      <xs:enumeration value=\"background-size\"/>\r\n" + 
				"      <xs:enumeration value=\"border\"/>\r\n" + 
				"      <xs:enumeration value=\"border-bottom\"/>\r\n" + 
				"      <xs:enumeration value=\"border-bottom-color\"/>\r\n" + 
				"      <xs:enumeration value=\"border-bottom-left-radius\"/>\r\n" + 
				"      <xs:enumeration value=\"border-bottom-right-radius\"/>\r\n" + 
				"      <xs:enumeration value=\"border-bottom-style\"/>\r\n" + 
				"      <xs:enumeration value=\"border-bottom-width\"/>\r\n" + 
				"      <xs:enumeration value=\"border-color\"/>\r\n" + 
				"      <xs:enumeration value=\"border-image\"/>\r\n" + 
				"      <xs:enumeration value=\"border-image-outset\"/>\r\n" + 
				"      <xs:enumeration value=\"border-image-repeat\"/>\r\n" + 
				"      <xs:enumeration value=\"border-image-slice\"/>\r\n" + 
				"      <xs:enumeration value=\"border-image-source\"/>\r\n" + 
				"      <xs:enumeration value=\"border-image-width\"/>\r\n" + 
				"      <xs:enumeration value=\"border-left\"/>\r\n" + 
				"      <xs:enumeration value=\"border-left-color\"/>\r\n" + 
				"      <xs:enumeration value=\"border-left-style\"/>\r\n" + 
				"      <xs:enumeration value=\"border-left-width\"/>\r\n" + 
				"      <xs:enumeration value=\"border-radius\"/>\r\n" + 
				"      <xs:enumeration value=\"border-right\"/>\r\n" + 
				"      <xs:enumeration value=\"border-right-color\"/>\r\n" + 
				"      <xs:enumeration value=\"border-right-style\"/>\r\n" + 
				"      <xs:enumeration value=\"border-right-width\"/>\r\n" + 
				"      <xs:enumeration value=\"border-style\"/>\r\n" + 
				"      <xs:enumeration value=\"border-top\"/>\r\n" + 
				"      <xs:enumeration value=\"border-top-color\"/>\r\n" + 
				"      <xs:enumeration value=\"border-top-left-radius\"/>\r\n" + 
				"      <xs:enumeration value=\"border-top-right-radius\"/>\r\n" + 
				"      <xs:enumeration value=\"border-top-style\"/>\r\n" + 
				"      <xs:enumeration value=\"border-top-width\"/>\r\n" + 
				"      <xs:enumeration value=\"border-width\"/>\r\n" + 
				"      <xs:enumeration value=\"box-decoration-break\"/>\r\n" + 
				"      <xs:enumeration value=\"box-shadow\"/>\r\n" + 
				"      \r\n" + 
				"      <!-- Basic Box Properties -->\r\n" + 
				"      <xs:enumeration value=\"bottom\"/>\r\n" + 
				"      <xs:enumeration value=\"clear\"/>\r\n" + 
				"      <xs:enumeration value=\"clip\"/>\r\n" + 
				"      <xs:enumeration value=\"display\"/>\r\n" + 
				"      <xs:enumeration value=\"float\"/>\r\n" + 
				"      <xs:enumeration value=\"height\"/>\r\n" + 
				"      <xs:enumeration value=\"left\"/>\r\n" + 
				"      <xs:enumeration value=\"margin\"/>\r\n" + 
				"      <xs:enumeration value=\"margin-bottom\"/>\r\n" + 
				"      <xs:enumeration value=\"margin-left\"/>\r\n" + 
				"      <xs:enumeration value=\"margin-right\"/>\r\n" + 
				"      <xs:enumeration value=\"margin-top\"/>\r\n" + 
				"      <xs:enumeration value=\"max-height\"/>\r\n" + 
				"      <xs:enumeration value=\"max-width\"/>\r\n" + 
				"      <xs:enumeration value=\"min-height\"/>\r\n" + 
				"      <xs:enumeration value=\"min-width\"/>\r\n" + 
				"      <xs:enumeration value=\"overflow\"/>\r\n" + 
				"      <xs:enumeration value=\"overflow-x\"/>\r\n" + 
				"      <xs:enumeration value=\"overflow-y\"/>\r\n" + 
				"      <xs:enumeration value=\"padding\"/>\r\n" + 
				"      <xs:enumeration value=\"padding-bottom\"/>\r\n" + 
				"      <xs:enumeration value=\"padding-left\"/>\r\n" + 
				"      <xs:enumeration value=\"padding-right\"/>\r\n" + 
				"      <xs:enumeration value=\"padding-top\"/>\r\n" + 
				"      <xs:enumeration value=\"position\"/>\r\n" + 
				"      <xs:enumeration value=\"right\"/>\r\n" + 
				"      <xs:enumeration value=\"top\"/>\r\n" + 
				"      <xs:enumeration value=\"visibility\"/>\r\n" + 
				"      <xs:enumeration value=\"width\"/>\r\n" + 
				"      <xs:enumeration value=\"vertical-align\"/>\r\n" + 
				"      <xs:enumeration value=\"z-index\"/>\r\n" + 
				"      \r\n" + 
				"      <!-- Flexible Box Layout -->\r\n" + 
				"      <xs:enumeration value=\"align-content\"/>\r\n" + 
				"      <xs:enumeration value=\"align-items\"/>\r\n" + 
				"      <xs:enumeration value=\"align-self\"/>\r\n" + 
				"      <xs:enumeration value=\"flex\"/>\r\n" + 
				"      <xs:enumeration value=\"flex-basis\"/>\r\n" + 
				"      <xs:enumeration value=\"flex-direction\"/>\r\n" + 
				"      <xs:enumeration value=\"flex-flow\"/>\r\n" + 
				"      <xs:enumeration value=\"flex-grow\"/>\r\n" + 
				"      <xs:enumeration value=\"flex-shrink\"/>\r\n" + 
				"      <xs:enumeration value=\"flex-wrap\"/>\r\n" + 
				"      <xs:enumeration value=\"justify-content\"/>\r\n" + 
				"      <xs:enumeration value=\"order\"/>\r\n" + 
				"      \r\n" + 
				"      <!-- Text Properties -->\r\n" + 
				"      <xs:enumeration value=\"hanging-punctuation\"/>\r\n" + 
				"      <xs:enumeration value=\"hyphens\"/>\r\n" + 
				"      <xs:enumeration value=\"letter-spacing\"/>\r\n" + 
				"      <xs:enumeration value=\"line-break\"/>\r\n" + 
				"      <xs:enumeration value=\"line-height\"/>\r\n" + 
				"      <xs:enumeration value=\"overflow-wrap\"/>\r\n" + 
				"      <xs:enumeration value=\"tab-size\"/>\r\n" + 
				"      <xs:enumeration value=\"text-align\"/>\r\n" + 
				"      <xs:enumeration value=\"text-align-last\"/>\r\n" + 
				"      <xs:enumeration value=\"text-combine-upright\"/>\r\n" + 
				"      <xs:enumeration value=\"text-indent\"/>\r\n" + 
				"      <xs:enumeration value=\"text-justify\"/>\r\n" + 
				"      <xs:enumeration value=\"text-transform\"/>\r\n" + 
				"      <xs:enumeration value=\"white-space\"/>\r\n" + 
				"      <xs:enumeration value=\"word-break\"/>\r\n" + 
				"      <xs:enumeration value=\"word-spacing\"/>\r\n" + 
				"      <xs:enumeration value=\"word-wrap\"/>\r\n" + 
				"      \r\n" + 
				"      <!-- Text Decoration Properties -->\r\n" + 
				"      <xs:enumeration value=\"text-decoration\"/>\r\n" + 
				"      <xs:enumeration value=\"text-decoration-color\"/>\r\n" + 
				"      <xs:enumeration value=\"text-decoration-line\"/>\r\n" + 
				"      <xs:enumeration value=\"text-decoration-style\"/>\r\n" + 
				"      <xs:enumeration value=\"text-shadow\"/>\r\n" + 
				"      <xs:enumeration value=\"text-underline-position\"/>\r\n" + 
				"      \r\n" + 
				"      <!-- Font Properties -->\r\n" + 
				"      <xs:enumeration value=\"@font-face\"/>\r\n" + 
				"      <xs:enumeration value=\"@font-feature-values\"/>\r\n" + 
				"      <xs:enumeration value=\"font\"/>\r\n" + 
				"      <xs:enumeration value=\"font-family\"/>\r\n" + 
				"      <xs:enumeration value=\"font-feature-settings\"/>\r\n" + 
				"      <xs:enumeration value=\"font-kerning\"/>\r\n" + 
				"      <xs:enumeration value=\"font-language-override\"/>\r\n" + 
				"      <xs:enumeration value=\"font-size\"/>\r\n" + 
				"      <xs:enumeration value=\"font-size-adjust\"/>\r\n" + 
				"      <xs:enumeration value=\"font-stretch\"/>\r\n" + 
				"      <xs:enumeration value=\"font-style\"/>\r\n" + 
				"      <xs:enumeration value=\"font-synthesis\"/>\r\n" + 
				"      <xs:enumeration value=\"font-variant\"/>\r\n" + 
				"      <xs:enumeration value=\"font-variant-alternates\"/>\r\n" + 
				"      <xs:enumeration value=\"font-variant-caps\"/>\r\n" + 
				"      <xs:enumeration value=\"font-variant-east-asian\"/>\r\n" + 
				"      <xs:enumeration value=\"font-variant-ligatures\"/>\r\n" + 
				"      <xs:enumeration value=\"font-variant-numeric\"/>\r\n" + 
				"      <xs:enumeration value=\"font-variant-position\"/>\r\n" + 
				"      <xs:enumeration value=\"font-weight\"/>\r\n" + 
				"      \r\n" + 
				"      <!-- Writing Modes Properties -->\r\n" + 
				"      <xs:enumeration value=\"direction\"/>\r\n" + 
				"      <xs:enumeration value=\"text-orientation\"/>\r\n" + 
				"      <xs:enumeration value=\"text-combine-upright\"/>\r\n" + 
				"      <xs:enumeration value=\"unicode-bidi\"/>\r\n" + 
				"      <xs:enumeration value=\"writing-mode\"/>\r\n" + 
				"      \r\n" + 
				"      <!-- Table Properties -->\r\n" + 
				"      <xs:enumeration value=\"border-collapse\"/>\r\n" + 
				"      <xs:enumeration value=\"border-spacing\"/>\r\n" + 
				"      <xs:enumeration value=\"caption-side\"/>\r\n" + 
				"      <xs:enumeration value=\"empty-cells\"/>\r\n" + 
				"      <xs:enumeration value=\"table-layout\"/>\r\n" + 
				"      <xs:enumeration value=\"\"/>\r\n" + 
				"      \r\n" + 
				"      <!-- Lists and Counters Properties -->\r\n" + 
				"      <xs:enumeration value=\"counter-increment\"/>\r\n" + 
				"      <xs:enumeration value=\"counter-reset\"/>\r\n" + 
				"      <xs:enumeration value=\"list-style\"/>\r\n" + 
				"      <xs:enumeration value=\"list-style-image\"/>\r\n" + 
				"      <xs:enumeration value=\"list-style-position\"/>\r\n" + 
				"      <xs:enumeration value=\"list-style-type\"/>\r\n" + 
				"      <xs:enumeration value=\"\"/>\r\n" + 
				"      \r\n" + 
				"      <!-- Animation Properties -->\r\n" + 
				"      <xs:enumeration value=\"@keyframes\"/>\r\n" + 
				"      <xs:enumeration value=\"animation\"/>\r\n" + 
				"      <xs:enumeration value=\"animation-delay\"/>\r\n" + 
				"      <xs:enumeration value=\"animation-direction\"/>\r\n" + 
				"      <xs:enumeration value=\"animation-duration\"/>\r\n" + 
				"      <xs:enumeration value=\"animation-fill-mode\"/>\r\n" + 
				"      <xs:enumeration value=\"animation-iteration-count\"/>\r\n" + 
				"      <xs:enumeration value=\"animation-name\"/>\r\n" + 
				"      <xs:enumeration value=\"animation-play-state\"/>\r\n" + 
				"      <xs:enumeration value=\"animation-timing-function\"/>\r\n" + 
				"      \r\n" + 
				"      <!-- Transform Properties -->\r\n" + 
				"      <xs:enumeration value=\"backface-visibility\"/>\r\n" + 
				"      <xs:enumeration value=\"perspective\"/>\r\n" + 
				"      <xs:enumeration value=\"perspective-origin\"/>\r\n" + 
				"      <xs:enumeration value=\"transform\"/>\r\n" + 
				"      <xs:enumeration value=\"transform-origin\"/>\r\n" + 
				"      <xs:enumeration value=\"transform-style\"/>\r\n" + 
				"      \r\n" + 
				"      <!-- Transitions Properties -->\r\n" + 
				"      <xs:enumeration value=\"transition\"/>\r\n" + 
				"      <xs:enumeration value=\"transition-property\"/>\r\n" + 
				"      <xs:enumeration value=\"transition-duration\"/>\r\n" + 
				"      <xs:enumeration value=\"transition-timing-function\"/>\r\n" + 
				"      <xs:enumeration value=\"transition-delay\"/>\r\n" + 
				"      \r\n" + 
				"      <!-- Basic User Interface Properties -->\r\n" + 
				"      <xs:enumeration value=\"box-sizing\"/>\r\n" + 
				"      <xs:enumeration value=\"content\"/>\r\n" + 
				"      <xs:enumeration value=\"cursor\"/>\r\n" + 
				"      <xs:enumeration value=\"ime-mode\"/>\r\n" + 
				"      <xs:enumeration value=\"nav-down\"/>\r\n" + 
				"      <xs:enumeration value=\"nav-index\"/>\r\n" + 
				"      <xs:enumeration value=\"nav-left\"/>\r\n" + 
				"      <xs:enumeration value=\"nav-right\"/>\r\n" + 
				"      <xs:enumeration value=\"nav-up\"/>\r\n" + 
				"      <xs:enumeration value=\"outline\"/>\r\n" + 
				"      <xs:enumeration value=\"outline-color\"/>\r\n" + 
				"      <xs:enumeration value=\"outline-offset\"/>\r\n" + 
				"      <xs:enumeration value=\"outline-style\"/>\r\n" + 
				"      <xs:enumeration value=\"outline-width\"/>\r\n" + 
				"      <xs:enumeration value=\"resize\"/>\r\n" + 
				"      <xs:enumeration value=\"text-overflow\"/>\r\n" + 
				"      <xs:enumeration value=\"\"/>\r\n" + 
				"      \r\n" + 
				"      <!-- Multi-column Layout Properties -->\r\n" + 
				"      <xs:enumeration value=\"break-after\"/>\r\n" + 
				"      <xs:enumeration value=\"break-before\"/>\r\n" + 
				"      <xs:enumeration value=\"break-inside\"/>\r\n" + 
				"      <xs:enumeration value=\"column-count\"/>\r\n" + 
				"      <xs:enumeration value=\"column-fill\"/>\r\n" + 
				"      <xs:enumeration value=\"column-gap\"/>\r\n" + 
				"      <xs:enumeration value=\"column-rule\"/>\r\n" + 
				"      <xs:enumeration value=\"column-rule-color\"/>\r\n" + 
				"      <xs:enumeration value=\"column-rule-style\"/>\r\n" + 
				"      <xs:enumeration value=\"column-rule-width\"/>\r\n" + 
				"      <xs:enumeration value=\"column-span\"/>\r\n" + 
				"      <xs:enumeration value=\"column-width\"/>\r\n" + 
				"      <xs:enumeration value=\"columns\"/>\r\n" + 
				"      <xs:enumeration value=\"widows\"/>\r\n" + 
				"      \r\n" + 
				"      <!-- Paged Media -->\r\n" + 
				"      <xs:enumeration value=\"orphans\"/>\r\n" + 
				"      <xs:enumeration value=\"page-break-after\"/>\r\n" + 
				"      <xs:enumeration value=\"page-break-before\"/>\r\n" + 
				"      <xs:enumeration value=\"page-break-inside\"/>\r\n" + 
				"      \r\n" + 
				"      <!-- Generated Content for Paged Media -->\r\n" + 
				"      <xs:enumeration value=\"marks\"/>\r\n" + 
				"      <xs:enumeration value=\"quotes\"/>\r\n" + 
				"      <xs:enumeration value=\"\"/>\r\n" + 
				"      \r\n" + 
				"      <!-- Filter Effects Properties -->\r\n" + 
				"      <xs:enumeration value=\"filter\"/>\r\n" + 
				"      \r\n" + 
				"      <!-- Image Values and Replaced Content -->\r\n" + 
				"      <xs:enumeration value=\"image-orientation\"/>\r\n" + 
				"      <xs:enumeration value=\"image-rendering\"/>\r\n" + 
				"      <xs:enumeration value=\"image-resolution\"/>\r\n" + 
				"      <xs:enumeration value=\"object-fit\"/>\r\n" + 
				"      <xs:enumeration value=\"object-position\"/>\r\n" + 
				"      \r\n" + 
				"      <!-- Masking Properties -->\r\n" + 
				"      <xs:enumeration value=\"mask\"/>\r\n" + 
				"      <xs:enumeration value=\"mask-type\"/>\r\n" + 
				"      \r\n" + 
				"      <!-- Speech Properties -->\r\n" + 
				"      <xs:enumeration value=\"mark\"/>\r\n" + 
				"      <xs:enumeration value=\"mark-after\"/>\r\n" + 
				"      <xs:enumeration value=\"mark-before\"/>\r\n" + 
				"      <xs:enumeration value=\"phonemes\"/>\r\n" + 
				"      <xs:enumeration value=\"rest\"/>\r\n" + 
				"      <xs:enumeration value=\"rest-after\"/>\r\n" + 
				"      <xs:enumeration value=\"rest-before\"/>\r\n" + 
				"      <xs:enumeration value=\"voice-balance\"/>\r\n" + 
				"      <xs:enumeration value=\"voice-duration\"/>\r\n" + 
				"      <xs:enumeration value=\"voice-pitch\"/>\r\n" + 
				"      <xs:enumeration value=\"voice-pitch-range\"/>\r\n" + 
				"      <xs:enumeration value=\"voice-rate\"/>\r\n" + 
				"      <xs:enumeration value=\"voice-stress\"/>\r\n" + 
				"      <xs:enumeration value=\"voice-volume\"/>\r\n" + 
				"      \r\n" + 
				"      <!-- Marquee Properties -->\r\n" + 
				"      <xs:enumeration value=\"marquee-direction\"/>\r\n" + 
				"      <xs:enumeration value=\"marquee-play-count\"/>\r\n" + 
				"      <xs:enumeration value=\"marquee-speed\"/>\r\n" + 
				"      <xs:enumeration value=\"marquee-style\"/>\r\n" + 
				"      \r\n" + 
				"      <!-- TODO missing stuff (Exploration) !? -->\r\n" + 
				"      <!-- @page -->\r\n" + 
				"      <xs:enumeration value=\"size\"/>\r\n" + 
				"      <!-- @font-face -->\r\n" + 
				"      <xs:enumeration value=\"src\"/>\r\n" + 
				"\r\n" + 
				"      <!-- Other -->\r\n" + 
				"      <!--\r\n" + 
				"      <xs:enumeration value=\"pointer-events\"/>\r\n" + 
				"      <xs:enumeration value=\"text-rendering\"/>\r\n" + 
				"      <xs:enumeration value=\"unicode-range\"/>\r\n" + 
				"      \r\n" + 
				"      <xs:enumeration value=\"-moz-border-top-colors\"/>\r\n" + 
				"      <xs:enumeration value=\"-moz-border-bottom-colors\"/>\r\n" + 
				"      <xs:enumeration value=\"-moz-border-left-colors\"/>\r\n" + 
				"      <xs:enumeration value=\"-moz-border-right-colors\"/>\r\n" + 
				"      <xs:enumeration value=\"-moz-transition-property\"/>\r\n" + 
				"      <xs:enumeration value=\"-moz-user-select\"/>\r\n" + 
				"      <xs:enumeration value=\"-moz-border-right-colors\"/>\r\n" + 
				"      <xs:enumeration value=\"-moz-transition-duration\"/>\r\n" + 
				"      <xs:enumeration value=\"-moz-border-radius\"/>\r\n" + 
				"      <xs:enumeration value=\"-moz-box-shadow\"/>\r\n" + 
				" \r\n" + 
				"      <xs:enumeration value=\"-o-transition-property\"/>\r\n" + 
				"      <xs:enumeration value=\"-o-transition-duration\"/>     \r\n" + 
				"      \r\n" + 
				"      <xs:enumeration value=\"-webkit-transition-duration\"/>\r\n" + 
				"      <xs:enumeration value=\"-webkit-transition-property\"/>\r\n" + 
				"      <xs:enumeration value=\"-webkit-border-radius\"/>\r\n" + 
				"      <xs:enumeration value=\"-webkit-box-shadow\"/>\r\n" + 
				"        -->\r\n" + 
				"      \r\n" + 
				"    </xs:restriction>\r\n" + 
				"  </xs:simpleType>\r\n" + 
				"\r\n" + 
				"\r\n" + 
				"</xs:schema>";
		String xmlAsString = "<stylesheet>\r\n" + 
				"    <cssStyleRule>\r\n" + 
				"        <selectorText>body</selectorText>\r\n" + 
				"        <style>\r\n" + 
				"            <property>background-color</property>\r\n" + 
				"            <cssValue>\r\n" + 
				"                <cssPrimitiveValue>\r\n" + 
				"                    <cssRgbColor>\r\n" + 
				"                        <r>208</r>\r\n" + 
				"                        <g>228</g>\r\n" + 
				"                        <b>254</b>\r\n" + 
				"                    </cssRgbColor>\r\n" + 
				"                </cssPrimitiveValue>\r\n" + 
				"            </cssValue>\r\n" + 
				"        </style>\r\n" + 
				"    </cssStyleRule>\r\n" + 
				"    <cssStyleRule>\r\n" + 
				"        <selectorText>h1</selectorText>\r\n" + 
				"        <style>\r\n" + 
				"            <property>color</property>\r\n" + 
				"            <cssValue>\r\n" + 
				"                <cssPrimitiveValue>\r\n" + 
				"                    <cssIdent>orange</cssIdent>\r\n" + 
				"                </cssPrimitiveValue>\r\n" + 
				"            </cssValue>\r\n" + 
				"            <property>text-align</property>\r\n" + 
				"            <cssValue>\r\n" + 
				"                <cssPrimitiveValue>\r\n" + 
				"                    <cssIdent>center</cssIdent>\r\n" + 
				"                </cssPrimitiveValue>\r\n" + 
				"            </cssValue>\r\n" + 
				"        </style>\r\n" + 
				"    </cssStyleRule>\r\n" + 
				"    <cssStyleRule>\r\n" + 
				"        <selectorText>p</selectorText>\r\n" + 
				"        <style>\r\n" + 
				"            <property>font-family</property>\r\n" + 
				"            <cssValue>\r\n" + 
				"                <cssPrimitiveValue>\r\n" + 
				"                    <cssString>Times New Roman</cssString>\r\n" + 
				"                </cssPrimitiveValue>\r\n" + 
				"            </cssValue>\r\n" + 
				"            <property>font-size</property>\r\n" + 
				"            <cssValue>\r\n" + 
				"                <cssPrimitiveValue>\r\n" + 
				"                    <cssPx>20.0</cssPx>\r\n" + 
				"                </cssPrimitiveValue>\r\n" + 
				"            </cssValue>\r\n" + 
				"        </style>\r\n" + 
				"    </cssStyleRule>\r\n" + 
				"</stylesheet>";
		testNoDTR(new ByteArrayInputStream(xsdAsString.getBytes()), new ByteArrayInputStream(xmlAsString.getBytes()));
	}
	
	String xsdAsStringEString = "<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\">\r\n" + 
			"    <xs:element name=\"root\" type=\"es\"/>\r\n" + 
			"    <xs:simpleType name=\"es\">\r\n" + 
			"        <xs:annotation exi:prepopulateValues=\"true\" xmlns:exi=\"http://www.w3.org/2009/exi\">\r\n" + 
			"            <xs:appinfo>\r\n" + 
			"                <xs:restriction base=\"xs:string\">\r\n" + 
			"                    <xs:enumeration value=\"Volvo\"/>\r\n" + 
			"                    <xs:enumeration value=\"BMW\"/>\r\n" + 
			"                    <xs:enumeration value=\"Volkswagen\"/>\r\n" + 
			"                </xs:restriction>\r\n" + 
			"            </xs:appinfo>\r\n" + 
			"        </xs:annotation>\r\n" + 
			"        <xs:restriction base=\"xs:string\"/>\r\n" + 
			"    </xs:simpleType>\r\n" + 
			"</xs:schema>";
	
	public void testEString1() throws EXIException, IOException, SAXException, TransformerException {
		// valid enum match
		String xmlAsString ="<root>Volkswagen</root>";
		byte[] bs = testEString(xmlAsString);
		assertTrue("Should be encoded with estring, was " + bs.length + " Bytes", bs.length < 4);
	}
	
	public void testEString2() throws EXIException, IOException, SAXException, TransformerException {
		// no valid enum match
		String xmlAsString ="<root>Toyota</root>";
		byte[] bs = testEString(xmlAsString);
		assertTrue("Should be encoded as string, was " + bs.length + " Bytes", bs.length > 7);
	}
	
	private byte[] testEString(String xmlAsString) throws EXIException, IOException, SAXException, TransformerException {
		/* DTR Map */
		QName type = new QName("", "es");
		QName representation = new QName(Constants.W3C_EXI_NS_URI, "estring");
		QName[] dtrMapTypes = { type };
		QName[] dtrMapRepresentations = { representation };
		
		// factory
		EXIFactory ef = DefaultEXIFactory.newInstance();
		ef.setFidelityOptions(FidelityOptions.createStrict());
		ef.setGrammars(GrammarFactory.newInstance().createGrammars(new ByteArrayInputStream(xsdAsStringEString.getBytes())));
		ef.setDatatypeRepresentationMap(dtrMapTypes, dtrMapRepresentations);
		
		// encode 
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		EXIResult exiResult = new EXIResult(ef);
		exiResult.setOutputStream(baos);
		XMLReader xmlReader = XMLReaderFactory.createXMLReader();
		xmlReader.setContentHandler( exiResult.getHandler() );
		xmlReader.parse(new InputSource(new ByteArrayInputStream(xmlAsString.getBytes()))); // parse XML input
		
		
		
		// decode
		ByteArrayOutputStream baosXML = new ByteArrayOutputStream();
		Result result = new StreamResult(baosXML);
		InputSource is = new InputSource(new ByteArrayInputStream(baos.toByteArray()));
		SAXSource exiSource = new EXISource(ef);
		exiSource.setInputSource(is);
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		transformer.transform(exiSource, result);
		
		// System.out.println(new String(baosXML.toByteArray()));
		
		XMLUnit.setIgnoreWhitespace(true);
		XMLUnit.setIgnoreAttributeOrder(true);
		XMLUnit.setIgnoreDiffBetweenTextAndCDATA(true);
		
		XMLAssert.assertXMLEqual(new StringReader(xmlAsString), new StringReader(new String(baosXML.toByteArray())));
		
		return baos.toByteArray();
	}

}
