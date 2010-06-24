package com.siemens.ct.exi.datatype;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.siemens.ct.exi.Constants;
import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.FidelityOptions;
import com.siemens.ct.exi.GrammarFactory;
import com.siemens.ct.exi.api.sax.EXIResult;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.grammar.Grammar;
import com.siemens.ct.exi.helpers.DefaultEXIFactory;
import com.siemens.ct.exi.types.BuiltInType;
import com.siemens.ct.exi.types.DatatypeRepresentationMapTypeEncoder;

public class DtrMapTestCase extends AbstractTestCase {

	// register type directly
	public void testIntegerToString1() throws IOException, EXIException {
		String schemaAsString = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ "  <xs:simpleType name='Integer'>"
				+ "    <xs:restriction base='xs:int'>"
				+ "    </xs:restriction>"
				+ "  </xs:simpleType>"
				+ "</xs:schema>";

		Grammar g = DatatypeMappingTest.getGrammarFor(schemaAsString);
		Datatype dt = DatatypeMappingTest.getSimpleDatatypeFor(schemaAsString,
				"Integer", "");

		QName schemaType = new QName("", "Integer");

		assertTrue(dt.getBuiltInType() == BuiltInType.INTEGER);
		assertTrue(dt.getSchemaType().equals(schemaType));

		assertTrue(dt.isValid("+10"));
		assertFalse(dt.isValid("12:32:00"));

		/* DTR Map */
		QName type = schemaType;
		QName representation = new QName(Constants.W3C_EXI_NS_URI, "string");
		QName[] dtrMapTypes = { type };
		QName[] dtrMapRepresentations = { representation };
		DatatypeRepresentationMapTypeEncoder dtrTe = new DatatypeRepresentationMapTypeEncoder(
				null, dtrMapTypes, dtrMapRepresentations, g);

		assertTrue(dtrTe.isValid(dt, "+10"));
		// any string should be valid
		assertTrue(dtrTe.isValid(dt, "12:32:00"));
		assertTrue(dtrTe.isValid(dt, "Blaa bla"));
	}

	// register super-type
	public void testIntegerToString2() throws IOException, EXIException {
		String schemaAsString = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ "  <xs:simpleType name='Integer'>"
				+ "    <xs:restriction base='xs:int'>"
				+ "    </xs:restriction>"
				+ "  </xs:simpleType>"
				+ "</xs:schema>";

		Grammar g = DatatypeMappingTest.getGrammarFor(schemaAsString);
		Datatype dt = DatatypeMappingTest.getSimpleDatatypeFor(schemaAsString,
				"Integer", "");

		QName schemaType = new QName("", "Integer");

		assertTrue(dt.getBuiltInType() == BuiltInType.INTEGER);
		assertTrue(dt.getSchemaType().equals(schemaType));

		assertTrue(dt.isValid("+10"));
		assertFalse(dt.isValid("12:32:00"));

		/* DTR Map */
		QName type = new QName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "int");
		QName representation = new QName(Constants.W3C_EXI_NS_URI, "string");
		QName[] dtrMapTypes = { type };
		QName[] dtrMapRepresentations = { representation };
		DatatypeRepresentationMapTypeEncoder dtrTe = new DatatypeRepresentationMapTypeEncoder(
				null, dtrMapTypes, dtrMapRepresentations, g);

		assertTrue(dtrTe.isValid(dt, "+10"));
		// any string should be valid
		assertTrue(dtrTe.isValid(dt, "12:32:00"));
		assertTrue(dtrTe.isValid(dt, "Blaa bla"));
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
		Grammar g = grammarFactory.createGrammar(new ByteArrayInputStream(
				schemaAsString.getBytes()));

		// factory with int 2 string mapping
		EXIFactory exiFactory = DefaultEXIFactory.newInstance();
		exiFactory.setGrammar(g);
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
			SAXResult saxResult = new EXIResult(baos, exiFactory);
			XMLReader xmlReader = XMLReaderFactory.createXMLReader();
			xmlReader.setContentHandler(saxResult.getHandler());
			xmlReader.parse(new InputSource(new ByteArrayInputStream(
					xmlAsString.getBytes())));
		}

		ByteArrayOutputStream baosDecXML = new ByteArrayOutputStream();

		// decode
		{
			InputSource is = new InputSource(new ByteArrayInputStream(baos
					.toByteArray()));
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

}
