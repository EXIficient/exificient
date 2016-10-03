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
import java.io.IOException;
import java.io.InputStream;

import javax.xml.namespace.QName;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;

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
				dtrMapTypes, dtrMapRepresentations);

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
				dtrMapTypes, dtrMapRepresentations);

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
				dtrMapTypes, dtrMapRepresentations);

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
				dtrMapTypes, dtrMapRepresentations);

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
				dtrMapTypes, dtrMapRepresentations);

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
				dtrMapTypes, dtrMapRepresentations);

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
				dtrMapTypes, dtrMapRepresentations);

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
				dtrMapTypes, dtrMapRepresentations);

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
				dtrMapTypes, dtrMapRepresentations);
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
				dtrMapTypes, dtrMapRepresentations);

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
				dtrMapTypes, dtrMapRepresentations);

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
				dtrMapTypes, dtrMapRepresentations);

		// integers
		assertTrue(dtrTe.isValid(dtInteger, new StringValue("+10")));
		// default mapping
		assertTrue(dtrTe.lastDatatype.getBuiltInType() == BuiltInType.INTEGER);
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
				dtrMapTypes, dtrMapRepresentations);
		LexicalTypeEncoder dtrLe = new LexicalTypeEncoder(
				dtrMapTypes, dtrMapRepresentations);

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
				dtrMapTypes, dtrMapRepresentations);

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
				dtrMapTypes, dtrMapRepresentations);

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
				dtrMapTypes, dtrMapRepresentations);

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
				dtrMapTypes, dtrMapRepresentations);

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
				dtrMapTypes, dtrMapRepresentations);

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
				dtrMapTypes, dtrMapRepresentations);

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
				dtrMapTypes, dtrMapRepresentations);

		//
		assertTrue(dtrTe.isValid(dt, new StringValue("any string")));
		assertTrue(dtrTe.isValid(dt, new StringValue("12345")));

		// indicates that no dtr map is in use
		assertTrue(dtrTe.lastDatatype.getBuiltInType() == BuiltInType.LIST);
		ListDatatype ld = (ListDatatype) dtrTe.lastDatatype;
		assertTrue(ld.getListDatatype().getBuiltInType() == BuiltInType.STRING);
	}

}
