package com.siemens.ct.exi.types;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.XMLConstants;
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
import com.siemens.ct.exi.datatype.AbstractTestCase;
import com.siemens.ct.exi.datatype.Datatype;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.grammars.Grammars;
import com.siemens.ct.exi.grammars.event.EventType;
import com.siemens.ct.exi.helpers.DefaultEXIFactory;
import com.siemens.ct.exi.values.StringValue;
import com.siemens.ct.exi.values.Value;

public class DtrMapTestCase extends AbstractTestCase {

	// Note: according to EXI errata ONLY directly referenced enumeration types
	// are not handled by DTR maps
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
		Grammars g = DatatypeMappingTest.getGrammarFor(schemaAsString);
	
		Datatype dtEnum = DatatypeMappingTest.getSimpleDatatypeFor(
				schemaAsString, "stringDerived", "");
		assertTrue(dtEnum.getBuiltInType() == BuiltInType.ENUMERATION);
		QName schemaTypeStringDerived = new QName("", "stringDerived");
		assertTrue(dtEnum.getSchemaType().equals(schemaTypeStringDerived));
	
		/* DTR Map */
		QName type = new QName("", "stringDerived");
		QName representation = new QName(Constants.W3C_EXI_NS_URI, "integer");
		QName[] dtrMapTypes = { type };
		QName[] dtrMapRepresentations = { representation };
		TypeEncoder defaultEncoder = new TypedTypeEncoder();
		DatatypeRepresentationMapTypeEncoder dtrTe = new DatatypeRepresentationMapTypeEncoder(
				defaultEncoder, dtrMapTypes, dtrMapRepresentations, g);
	
		// can encode integers
		assertTrue(dtrTe.isValid(dtEnum, new StringValue("+10")));
		// indicates that dtr map is in use
		assertTrue(dtrTe.getRecentDtrMapDatatype().getBuiltInType() == BuiltInType.INTEGER);
		// IntegerDatatype idt = (IntegerDatatype) dtrTe.getRecentDtrMapDatatype();
		// assertTrue(idt.getIntegerType() == IntegerType.INTEGER_BIG);
	}

	// The codec used for an enumerated type is not affected by DTRM entry attached to its ancestral type.
	// value="{http://www.w3.org/2001/XMLSchema}string {http://www.w3.org/2009/exi}integer"
	public void testEnumerationToInteger3() throws IOException, EXIException {
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
		Grammars g = DatatypeMappingTest.getGrammarFor(schemaAsString);
	
		Datatype dtEnum = DatatypeMappingTest.getSimpleDatatypeFor(
				schemaAsString, "stringDerived2", "");
		assertTrue(dtEnum.getBuiltInType() == BuiltInType.ENUMERATION);
		QName schemaTypeStringDerived2 = new QName("", "stringDerived2");
		assertTrue(dtEnum.getSchemaType().equals(schemaTypeStringDerived2));
	
		/* DTR Map */
		QName type = new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "string");
		QName representation = new QName(Constants.W3C_EXI_NS_URI, "integer");
		QName[] dtrMapTypes = { type };
		QName[] dtrMapRepresentations = { representation };
		TypeEncoder defaultEncoder = new TypedTypeEncoder();
		DatatypeRepresentationMapTypeEncoder dtrTe = new DatatypeRepresentationMapTypeEncoder(
				defaultEncoder, dtrMapTypes, dtrMapRepresentations, g);
	
		// can encode only enum values
		assertFalse(dtrTe.isValid(dtEnum, new StringValue("+10")));
		assertTrue(dtrTe.isValid(dtEnum, new StringValue("Nagoya")));
	
		// indicates that NO dtr map is in use
		assertTrue(dtrTe.getRecentDtrMapDatatype() == null);
	}

	// Note: according to EXI errata ONLY  referenced enumeration types
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
		Grammars g = DatatypeMappingTest.getGrammarFor(schemaAsString);

		Datatype dtEnum = DatatypeMappingTest.getSimpleDatatypeFor(
				schemaAsString, "stringDerived2", "");
		assertTrue(dtEnum.getBuiltInType() == BuiltInType.ENUMERATION);
		QName schemaTypeStringDerived2 = new QName("", "stringDerived2");
		assertTrue(dtEnum.getSchemaType().equals(schemaTypeStringDerived2));

		/* DTR Map */
		QName type = new QName("", "stringDerived");
		QName representation = new QName(Constants.W3C_EXI_NS_URI, "integer");
		QName[] dtrMapTypes = { type };
		QName[] dtrMapRepresentations = { representation };
		TypeEncoder defaultEncoder = new TypedTypeEncoder();
		DatatypeRepresentationMapTypeEncoder dtrTe = new DatatypeRepresentationMapTypeEncoder(
				defaultEncoder, dtrMapTypes, dtrMapRepresentations, g);

		// can encode only int values
		assertTrue(dtrTe.isValid(dtEnum, new StringValue("+10")));
		assertFalse(dtrTe.isValid(dtEnum, new StringValue("Nagoya")));

		// indicates that an dtr map is in use
		assertTrue(dtrTe.getRecentDtrMapDatatype() != null);
	}
	
	
	// register type directly
	public void testIntegerToString1() throws IOException, EXIException {
		String schemaAsString = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ "  <xs:simpleType name='Integer'>"
				+ "    <xs:restriction base='xs:int'>"
				+ "    </xs:restriction>"
				+ "  </xs:simpleType>"
				+ "</xs:schema>";

		Grammars g = DatatypeMappingTest.getGrammarFor(schemaAsString);
		Datatype dt = DatatypeMappingTest.getSimpleDatatypeFor(schemaAsString,
				"Integer", "");

		QName schemaType = new QName("", "Integer");

		assertTrue(dt.getBuiltInType() == BuiltInType.INTEGER);
		// IntegerDatatype idt = (IntegerDatatype) dt;
		// assertTrue(idt.getIntegerType() == IntegerType.INTEGER_32);
		assertTrue(dt.getSchemaType().equals(schemaType));

		assertTrue(dt.isValid(new StringValue("+10")));
		assertFalse(dt.isValid(new StringValue("12:32:00")));

		/* DTR Map */
		QName type = schemaType;
		QName representation = new QName(Constants.W3C_EXI_NS_URI, "string");
		QName[] dtrMapTypes = { type };
		QName[] dtrMapRepresentations = { representation };
		TypeEncoder defaultEncoder = new TypedTypeEncoder();
		DatatypeRepresentationMapTypeEncoder dtrTe = new DatatypeRepresentationMapTypeEncoder(
				defaultEncoder, dtrMapTypes, dtrMapRepresentations, g);

		assertTrue(dtrTe.isValid(dt, new StringValue("+10")));
		// any string should be valid
		assertTrue(dtrTe.isValid(dt, new StringValue("12:32:00")));
		assertTrue(dtrTe.isValid(dt, new StringValue("Blaa bla")));

		assertTrue(dtrTe.getRecentDtrMapDatatype().getBuiltInType() == BuiltInType.STRING);
	}

	// register super-type
	public void testIntegerToString2() throws IOException, EXIException {
		String schemaAsString = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ "  <xs:simpleType name='Integer'>"
				+ "    <xs:restriction base='xs:int'>"
				+ "    </xs:restriction>"
				+ "  </xs:simpleType>"
				+ "</xs:schema>";

		Grammars g = DatatypeMappingTest.getGrammarFor(schemaAsString);
		Datatype dt = DatatypeMappingTest.getSimpleDatatypeFor(schemaAsString,
				"Integer", "");

		QName schemaType = new QName("", "Integer");

		assertTrue(dt.getBuiltInType() == BuiltInType.INTEGER);
		// IntegerDatatype idt = (IntegerDatatype) dt;
		// assertTrue(idt.getIntegerType() == IntegerType.INTEGER_32);
		assertTrue(dt.getSchemaType().equals(schemaType));

		assertTrue(dt.isValid(new StringValue("+10")));
		assertFalse(dt.isValid(new StringValue("12:32:00")));

		/* DTR Map */
		QName type = new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "int");
		QName representation = new QName(Constants.W3C_EXI_NS_URI, "string");
		QName[] dtrMapTypes = { type };
		QName[] dtrMapRepresentations = { representation };
		TypeEncoder defaultEncoder = new TypedTypeEncoder();
		DatatypeRepresentationMapTypeEncoder dtrTe = new DatatypeRepresentationMapTypeEncoder(
				defaultEncoder, dtrMapTypes, dtrMapRepresentations, g);

		assertTrue(dtrTe.isValid(dt, new StringValue("+10")));
		// any string should be valid
		assertTrue(dtrTe.isValid(dt, new StringValue("12:32:00")));
		assertTrue(dtrTe.isValid(dt, new StringValue("Blaa bla")));

		assertTrue(dtrTe.getRecentDtrMapDatatype().getBuiltInType() == BuiltInType.STRING);
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
		QName type = new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "int");
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
			XMLReader exiReader = exiFactory.createEXIReader();

			Result result = new StreamResult(baosDecXML);
			SAXSource exiSource = new SAXSource(is);
			exiSource.setXMLReader(exiReader);
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.transform(exiSource, result);
		}

		// System.out.println(baosDecXML.toString());

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
		QName type1 = new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "short");
		QName type2 = new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "integer");
		QName representation1 = new QName(Constants.W3C_EXI_NS_URI, "integer");
		QName representation2 = new QName(Constants.W3C_EXI_NS_URI, "string");
		QName[] dtrMapTypes = { type1, type2 };
		QName[] dtrMapRepresentations = { representation1, representation2 };
		exiFactory.setDatatypeRepresentationMap(dtrMapTypes,
				dtrMapRepresentations);

		TypeEncoder defaultEncoder = new TypedTypeEncoder();
		DatatypeRepresentationMapTypeEncoder dtrTe = new DatatypeRepresentationMapTypeEncoder(
				defaultEncoder, dtrMapTypes, dtrMapRepresentations, g);
		Datatype dtShort = DatatypeMappingTest.getSimpleDatatypeFor(
				schemaAsString, "short", XMLConstants.W3C_XML_SCHEMA_NS_URI);
		dtrTe.isValid(dtShort, new StringValue("XXX 12 XX"));
		assertTrue(dtrTe.getRecentDtrMapDatatype() == null);

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
		QName type1 = new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "short");
		QName type2 = new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "integer");
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
			QName se = decoder.decodeStartElement();
			assertTrue(se.equals(new QName("", "root")));

			et = decoder.next();
			assertTrue(et == EventType.START_ELEMENT);
			se = decoder.decodeStartElement();
			assertTrue(se.equals(new QName("", "shortVal")));

			et = decoder.next();
			assertTrue(et == EventType.CHARACTERS);
			Value ch = decoder.decodeCharacters();
			assertTrue(ch.toString().equals("XXX 14 XX"));

			et = decoder.next();
			assertTrue(et == EventType.END_ELEMENT);
			QName ee = decoder.decodeEndElement();
			assertTrue(ee.equals(new QName("", "shortVal")));

			et = decoder.next();
			assertTrue(et == EventType.END_ELEMENT);
			ee = decoder.decodeEndElement();
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

		Grammars g = DatatypeMappingTest.getGrammarFor(schemaAsString);
		Datatype dtDecimal = DatatypeMappingTest.getSimpleDatatypeFor(
				schemaAsString, "decimal", "");
		assertTrue(dtDecimal.getBuiltInType() == BuiltInType.DECIMAL);
		QName schemaTypeDecimal = new QName("", "decimal");
		assertTrue(dtDecimal.getSchemaType().equals(schemaTypeDecimal));

		Datatype dtInteger = DatatypeMappingTest.getSimpleDatatypeFor(
				schemaAsString, "integer", "");
		assertTrue(dtInteger.getBuiltInType() == BuiltInType.INTEGER);
		// IntegerDatatype idt = (IntegerDatatype) dtInteger;
		// assertTrue(idt.getIntegerType() == IntegerType.INTEGER_32);
		QName schemaTypeInteger = new QName("", "integer");
		assertTrue(dtInteger.getSchemaType().equals(schemaTypeInteger));

		/* DTR Map */
		QName type = new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "decimal");
		QName representation = new QName(Constants.W3C_EXI_NS_URI, "string");
		QName[] dtrMapTypes = { type };
		QName[] dtrMapRepresentations = { representation };
		TypeEncoder defaultEncoder = new TypedTypeEncoder();
		DatatypeRepresentationMapTypeEncoder dtrTe = new DatatypeRepresentationMapTypeEncoder(
				defaultEncoder, dtrMapTypes, dtrMapRepresentations, g);

		// decimals
		assertTrue(dtrTe.isValid(dtDecimal, new StringValue("+10")));
		assertTrue(dtrTe.getRecentDtrMapDatatype().getBuiltInType() == BuiltInType.STRING);

		// integers
		assertTrue(dtrTe.isValid(dtInteger, new StringValue("+10")));
		// null indicates that no dtr map is in use
		assertTrue(dtrTe.getRecentDtrMapDatatype() == null);
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
		Grammars g = DatatypeMappingTest.getGrammarFor(schemaAsString);

		Datatype dtInteger = DatatypeMappingTest.getSimpleDatatypeFor(
				schemaAsString, "integer", "");
		assertTrue(dtInteger.getBuiltInType() == BuiltInType.INTEGER);
		// IntegerDatatype idt = (IntegerDatatype) dtInteger;
		// assertTrue(idt.getIntegerType() == IntegerType.INTEGER_32);
		QName schemaTypeInteger = new QName("", "integer");
		assertTrue(dtInteger.getSchemaType().equals(schemaTypeInteger));

		/* DTR Map */
		QName type = new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "integer");
		QName representation = new QName(Constants.W3C_EXI_NS_URI, "integer");
		QName[] dtrMapTypes = { type };
		QName[] dtrMapRepresentations = { representation };
		TypeEncoder defaultEncoder = new TypedTypeEncoder();
		DatatypeRepresentationMapTypeEncoder dtrTe = new DatatypeRepresentationMapTypeEncoder(
				defaultEncoder, dtrMapTypes, dtrMapRepresentations, g);

		// integers
		assertTrue(dtrTe.isValid(dtInteger, new StringValue("+10")));
		// null indicates that no dtr map is in use
		assertTrue(dtrTe.getRecentDtrMapDatatype() == null);
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
		Grammars g = DatatypeMappingTest.getGrammarFor(schemaAsString);

		Datatype dtInteger = DatatypeMappingTest.getSimpleDatatypeFor(
				schemaAsString, "integer", "");
		assertTrue(dtInteger.getBuiltInType() == BuiltInType.INTEGER);
		// IntegerDatatype idt = (IntegerDatatype) dtInteger;
		// assertTrue(idt.getIntegerType() == IntegerType.INTEGER_32);
		QName schemaTypeInteger = new QName("", "integer");
		assertTrue(dtInteger.getSchemaType().equals(schemaTypeInteger));

		/* DTR Map */
		QName type = new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "int");
		QName representation = new QName(Constants.W3C_EXI_NS_URI, "integer");
		QName[] dtrMapTypes = { type };
		QName[] dtrMapRepresentations = { representation };
		TypeEncoder defaultEncoder = new TypedTypeEncoder();
		DatatypeRepresentationMapTypeEncoder dtrTe = new DatatypeRepresentationMapTypeEncoder(
				defaultEncoder, dtrMapTypes, dtrMapRepresentations, g);

		// integers
		assertTrue(dtrTe.isValid(dtInteger, new StringValue("+10")));
		// null indicates that no dtr map is in use
		assertTrue(dtrTe.getRecentDtrMapDatatype() == null);
	}

}
