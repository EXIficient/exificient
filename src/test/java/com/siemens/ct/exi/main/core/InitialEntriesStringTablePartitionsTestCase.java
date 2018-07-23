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
import java.io.IOException;

import junit.framework.TestCase;

import com.siemens.ct.exi.core.Constants;
import com.siemens.ct.exi.core.EXIFactory;
import com.siemens.ct.exi.core.coder.AbstractEXIBodyCoder.RuntimeUriContext;
import com.siemens.ct.exi.core.coder.EXIBodyDecoderInOrder;
import com.siemens.ct.exi.core.coder.EXIBodyEncoderInOrder;
import com.siemens.ct.exi.core.context.GrammarContext;
import com.siemens.ct.exi.core.exceptions.EXIException;
import com.siemens.ct.exi.core.grammars.Grammars;
import com.siemens.ct.exi.core.helpers.DefaultEXIFactory;
import com.siemens.ct.exi.grammars.GrammarFactory;
import com.siemens.ct.exi.grammars.XSDGrammarsBuilder;

public class InitialEntriesStringTablePartitionsTestCase extends TestCase {

	protected static boolean containsValue(String[] list, String val) {
		for (int i = 0; i < list.length; i++) {
			String li = list[i];
			if (li.equals(val)) {
				return true;
			}
		}
		return false;
	}

	protected static Grammars getGrammarFor(String schemaAsString)
			throws EXIException {
		XSDGrammarsBuilder xsdGB = XSDGrammarsBuilder.newInstance();
		ByteArrayInputStream bais = new ByteArrayInputStream(
				schemaAsString.getBytes());
		xsdGB.loadGrammars(bais);
		return xsdGB.toGrammars();
	}

	public void testSchemaLess() throws EXIException {
		GrammarFactory gf = GrammarFactory.newInstance();
		Grammars g = gf.createSchemaLessGrammars();
		GrammarContext gc = g.getGrammarContext();
		// GrammarURIEntry[] gue = g.getGrammarEntries();

		// Initial Entries in Uri Partition
		assertTrue(gc.getNumberOfGrammarUriContexts() == 3);
		assertTrue(gc.getGrammarUriContext(0).getNamespaceUri()
				.equals(Constants.XML_NULL_NS_URI));
		assertTrue(gc.getGrammarUriContext(1).getNamespaceUri()
				.equals(Constants.XML_NS_URI));
		assertTrue(gc.getGrammarUriContext(2).getNamespaceUri()
				.equals(Constants.XML_SCHEMA_INSTANCE_NS_URI));

		// Initial Entries in Prefix Partitions
		assertTrue(gc.getGrammarUriContext(0).getNumberOfPrefixes() == 1);
		assertTrue(gc.getGrammarUriContext(0).getPrefix(0)
				.equals(Constants.XML_DEFAULT_NS_PREFIX));
		assertTrue(gc.getGrammarUriContext(1).getNumberOfPrefixes() == 1);
		assertTrue(gc.getGrammarUriContext(1).getPrefix(0)
				.equals(Constants.XML_NS_PREFIX));
		assertTrue(gc.getGrammarUriContext(2).getNumberOfPrefixes() == 1);
		assertTrue(gc.getGrammarUriContext(2).getPrefix(0).equals("xsi"));

		// Initial Entries in Local-Name Partitions
		assertTrue(gc.getGrammarUriContext(0).getNumberOfQNames() == 0);
		// XML-NS "base", "id", "lang", "space"
		assertTrue(gc.getGrammarUriContext(1).getNumberOfQNames() == 4);
		assertTrue(gc.getGrammarUriContext(1).getQNameContext(0).getLocalName()
				.equals("base"));
		assertTrue(gc.getGrammarUriContext(1).getQNameContext(1).getLocalName()
				.equals("id"));
		assertTrue(gc.getGrammarUriContext(1).getQNameContext(2).getLocalName()
				.equals("lang"));
		assertTrue(gc.getGrammarUriContext(1).getQNameContext(3).getLocalName()
				.equals("space"));
		// XSI-NS "nil", "type"
		assertTrue(gc.getGrammarUriContext(2).getNumberOfQNames() == 2);
		assertTrue(gc.getGrammarUriContext(2).getQNameContext(0).getLocalName()
				.equals("nil"));
		assertTrue(gc.getGrammarUriContext(2).getQNameContext(1).getLocalName()
				.equals("type"));
	}

	public void testXsdTypesOnly() throws EXIException {
		GrammarFactory gf = GrammarFactory.newInstance();
		Grammars g = gf.createXSDTypesOnlyGrammars();
		GrammarContext gc = g.getGrammarContext();
		// GrammarURIEntry[] gue = g.getGrammarEntries();

		// Initial Entries in Uri Partition
		assertTrue(gc.getNumberOfGrammarUriContexts() == 4);
		assertTrue(gc.getGrammarUriContext(0).getNamespaceUri()
				.equals(Constants.XML_NULL_NS_URI));
		assertTrue(gc.getGrammarUriContext(1).getNamespaceUri()
				.equals(Constants.XML_NS_URI));
		assertTrue(gc.getGrammarUriContext(2).getNamespaceUri()
				.equals(Constants.XML_SCHEMA_INSTANCE_NS_URI));
		assertTrue(gc.getGrammarUriContext(3).getNamespaceUri()
				.equals(Constants.XML_SCHEMA_NS_URI));

		// Initial Entries in Prefix Partitions
		assertTrue(gc.getGrammarUriContext(0).getNumberOfPrefixes() == 1);
		assertTrue(gc.getGrammarUriContext(0).getPrefix(0)
				.equals(Constants.XML_DEFAULT_NS_PREFIX));
		assertTrue(gc.getGrammarUriContext(1).getNumberOfPrefixes() == 1);
		assertTrue(gc.getGrammarUriContext(1).getPrefix(0)
				.equals(Constants.XML_NS_PREFIX));
		assertTrue(gc.getGrammarUriContext(2).getNumberOfPrefixes() == 1);
		assertTrue(gc.getGrammarUriContext(2).getPrefix(0).equals("xsi"));

		// Initial Entries in Local-Name Partitions
		assertTrue(gc.getGrammarUriContext(0).getNumberOfQNames() == 0);
		// XML-NS "base", "id", "lang", "space"
		assertTrue(gc.getGrammarUriContext(1).getNumberOfQNames() == 4);
		assertTrue(gc.getGrammarUriContext(1).getQNameContext(0).getLocalName()
				.equals("base"));
		assertTrue(gc.getGrammarUriContext(1).getQNameContext(1).getLocalName()
				.equals("id"));
		assertTrue(gc.getGrammarUriContext(1).getQNameContext(2).getLocalName()
				.equals("lang"));
		assertTrue(gc.getGrammarUriContext(1).getQNameContext(3).getLocalName()
				.equals("space"));
		// XSI-NS "nil", "type"
		assertTrue(gc.getGrammarUriContext(2).getNumberOfQNames() == 2);
		assertTrue(gc.getGrammarUriContext(2).getQNameContext(0).getLocalName()
				.equals("nil"));
		assertTrue(gc.getGrammarUriContext(2).getQNameContext(1).getLocalName()
				.equals("type"));
		// XSD-NS "ENTITIES", "ENTITY", "ID", "IDREF", "IDREFS", "NCName",
		// "NMTOKEN", "NMTOKENS", "NOTATION", "Name", "QName", "anySimpleType",
		// "anyType", "anyURI", "base64Binary", "boolean", "byte", "date",
		// "dateTime", "decimal", "double", "duration", "float", "gDay",
		// "gMonth", "gMonthDay", "gYear", "gYearMonth", "hexBinary", "int",
		// "integer", "language", "long", "negativeInteger",
		// "nonNegativeInteger", "nonPositiveInteger", "normalizedString",
		// "positiveInteger", "short", "string", "time", "token",
		// "unsignedByte", "unsignedInt", "unsignedLong", "unsignedShort"
		assertTrue(gc.getGrammarUriContext(3).getNumberOfQNames() == 46);
		assertTrue(gc.getGrammarUriContext(3).getQNameContext(0).getLocalName()
				.equals("ENTITIES"));
		assertTrue(gc.getGrammarUriContext(3).getQNameContext(45)
				.getLocalName().equals("unsignedShort"));
	}

	// normal and simple schema
	public void testSchema1() throws EXIException, IOException {
		String schemaAsString = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ "  <xs:simpleType name='Binary'>"
				+ "    <xs:restriction base='xs:base64Binary'>"
				+ "    </xs:restriction>"
				+ "  </xs:simpleType>"
				+ "</xs:schema>";

		Grammars g = getGrammarFor(schemaAsString);
		EXIFactory exiFactory = DefaultEXIFactory.newInstance();
		exiFactory.setGrammars(g);
		EXIBodyDecoderInOrder decoder = new EXIBodyDecoderInOrder(exiFactory);
		decoder.initForEachRun();

		// GrammarContext gc = g.getGrammarContext();
		// DecoderContext decoderContext = new DecoderContextImpl(gc);
		// GrammarURIEntry[] gue = g.getGrammarEntries();

		// Initial Entries in Uri Partition
		assertTrue(decoder.getNumberOfUris() == 4);
		assertTrue(decoder.getUri(0).getNamespaceUri()
				.equals(Constants.XML_NULL_NS_URI));
		assertTrue(decoder.getUri(1).getNamespaceUri()
				.equals(Constants.XML_NS_URI));
		assertTrue(decoder.getUri(2).getNamespaceUri()
				.equals(Constants.XML_SCHEMA_INSTANCE_NS_URI));
		assertTrue(decoder.getUri(3).getNamespaceUri()
				.equals(Constants.XML_SCHEMA_NS_URI));

		// Initial Entries in Prefix Partitions
		assertTrue(decoder.getUri(0).getNumberOfPrefixes() == 1);
		assertTrue(decoder.getUri(0).getPrefix(0)
				.equals(Constants.XML_DEFAULT_NS_PREFIX));
		assertTrue(decoder.getUri(1).getNumberOfPrefixes() == 1);
		assertTrue(decoder.getUri(1).getPrefix(0)
				.equals(Constants.XML_NS_PREFIX));
		assertTrue(decoder.getUri(2).getNumberOfPrefixes() == 1);
		assertTrue(decoder.getUri(2).getPrefix(0).equals("xsi"));

		// Initial Entries in Local-Name Partitions
		assertTrue(decoder.getUri(0).getNumberOfQNames() == 1);
		assertTrue(decoder.getUri(0).getQNameContext(0).getLocalName()
				.equals("Binary"));
		// XML-NS "base", "id", "lang", "space"
		assertTrue(decoder.getUri(1).getNumberOfQNames() == 4);
		assertTrue(decoder.getUri(1).getQNameContext(0).getLocalName()
				.equals("base"));
		assertTrue(decoder.getUri(1).getQNameContext(1).getLocalName()
				.equals("id"));
		assertTrue(decoder.getUri(1).getQNameContext(2).getLocalName()
				.equals("lang"));
		assertTrue(decoder.getUri(1).getQNameContext(3).getLocalName()
				.equals("space"));
		// XSI-NS "nil", "type"
		assertTrue(decoder.getUri(2).getNumberOfQNames() == 2);
		assertTrue(decoder.getUri(2).getQNameContext(0).getLocalName()
				.equals("nil"));
		assertTrue(decoder.getUri(2).getQNameContext(1).getLocalName()
				.equals("type"));
		// XSD-NS "ENTITIES", "ENTITY", "ID", "IDREF", "IDREFS", "NCName",
		// "NMTOKEN", "NMTOKENS", "NOTATION", "Name", "QName", "anySimpleType",
		// "anyType", "anyURI", "base64Binary", "boolean", "byte", "date",
		// "dateTime", "decimal", "double", "duration", "float", "gDay",
		// "gMonth", "gMonthDay", "gYear", "gYearMonth", "hexBinary", "int",
		// "integer", "language", "long", "negativeInteger",
		// "nonNegativeInteger", "nonPositiveInteger", "normalizedString",
		// "positiveInteger", "short", "string", "time", "token",
		// "unsignedByte", "unsignedInt", "unsignedLong", "unsignedShort"
		assertTrue(decoder.getUri(3).getNumberOfQNames() == 46);
		assertTrue(decoder.getUri(3).getQNameContext(0).getLocalName()
				.equals("ENTITIES"));
		assertTrue(decoder.getUri(3).getQNameContext(45).getLocalName()
				.equals("unsignedShort"));

	}

	// primer example
	public void testSchema2() throws EXIException, IOException {
		String schemaAsString = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema' elementFormDefault='qualified'>"
				+ "	    <xs:element name='notebook'>"
				+ "	        <xs:complexType>"
				+ "	            <xs:sequence maxOccurs='unbounded'>"
				+ "	                <xs:element name='note' type='Note'/>"
				+ "	            </xs:sequence>"
				+ "	            <xs:attribute ref='date'/>"
				+ "	        </xs:complexType>"
				+ "	    </xs:element>"
				+ "	    <xs:complexType name='Note'>"
				+ "	        <xs:sequence>"
				+ "	            <xs:element name='subject' type='xs:string'/>"
				+ "	            <xs:element name='body' type='xs:string'/>"
				+ "	        </xs:sequence>"
				+ "	        <xs:attribute ref='date' use='required'/>"
				+ "	        <xs:attribute name='category' type='xs:string'/>"
				+ "	    </xs:complexType>"
				+ "	    <xs:attribute name='date' type='xs:date'/>"
				+ "	</xs:schema>";

		Grammars g = getGrammarFor(schemaAsString);
		EXIFactory exiFactory = DefaultEXIFactory.newInstance();
		exiFactory.setGrammars(g);
		EXIBodyEncoderInOrder encoder = new EXIBodyEncoderInOrder(exiFactory);
		encoder.initForEachRun();
		// GrammarContext gc = g.getGrammarContext();
		// EncoderContext encoderContext = new EncoderContextImpl(gc);
		// GrammarURIEntry[] gue = g.getGrammarEntries();

		// Initial Entries in Uri Partition
		assertTrue(encoder.getNumberOfUris() == 4);
		assertTrue(encoder.getUri(Constants.XML_NULL_NS_URI)
				.getNamespaceUriID() == 0);
		assertTrue(encoder.getUri(Constants.XML_NS_URI).getNamespaceUriID() == 1);
		assertTrue(encoder.getUri(Constants.XML_SCHEMA_INSTANCE_NS_URI)
				.getNamespaceUriID() == 2);
		assertTrue(encoder.getUri(Constants.XML_SCHEMA_NS_URI)
				.getNamespaceUriID() == 3);

		// Initial Entries in Local-Name Partitions
		RuntimeUriContext uc0 = encoder.getUri(Constants.XML_NULL_NS_URI);
		assertTrue(uc0.getNumberOfQNames() == 7);
		assertTrue(uc0.getQNameContext("Note").getLocalNameID() == 0);
		assertTrue(uc0.getQNameContext("body").getLocalNameID() == 1);
		assertTrue(uc0.getQNameContext("category").getLocalNameID() == 2);
		assertTrue(uc0.getQNameContext("date").getLocalNameID() == 3);
		assertTrue(uc0.getQNameContext("note").getLocalNameID() == 4);
		assertTrue(uc0.getQNameContext("notebook").getLocalNameID() == 5);
		assertTrue(uc0.getQNameContext("subject").getLocalNameID() == 6);
		// XML-NS "base", "id", "lang", "space"
		assertTrue(encoder.getUri(Constants.XML_NS_URI).getNumberOfQNames() == 4);
		// XSI-NS "nil", "type"
		assertTrue(encoder.getUri(Constants.XML_SCHEMA_INSTANCE_NS_URI)
				.getNumberOfQNames() == 2);
		// XSD-NS "ENTITIES", ..., "unsignedShort"
		assertTrue(encoder.getUri(Constants.XML_SCHEMA_NS_URI)
				.getNumberOfQNames() == 46);
	}

	// other NS example
	public void testSchema3() throws EXIException, IOException {
		String schemaAsString = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema' elementFormDefault='qualified'"
				+ "		    targetNamespace='http://www.foo.com' xmlns:ns='http://www.foo.com'>"
				+ "		    <xs:element name='personnel'>"
				+ "		        <xs:complexType>"
				+ "		            <xs:sequence maxOccurs='unbounded'>"
				+ "		                <xs:element name='person' type='ns:Person'/>"
				+ "		            </xs:sequence>"
				+ "		        </xs:complexType>"
				+ "		    </xs:element>"
				+ "		    <xs:complexType name='Person'>"
				+ "		        <xs:sequence>"
				+ "		            <xs:element name='name' type='xs:string'/>"
				+ "		            <xs:element name='email' type='xs:string'/>"
				+ "		        </xs:sequence>"
				+ "		    </xs:complexType>"
				+ "		</xs:schema>";

		Grammars g = getGrammarFor(schemaAsString);
		EXIFactory exiFactory = DefaultEXIFactory.newInstance();
		exiFactory.setGrammars(g);
		EXIBodyDecoderInOrder decoder = new EXIBodyDecoderInOrder(exiFactory);
		decoder.initForEachRun();

		// GrammarURIEntry[] gue = g.getGrammarEntries();
		// GrammarContext gc = g.getGrammarContext();
		// DecoderContext decoderContext = new DecoderContextImpl(gc);

		// Initial Entries in Uri Partition
		assertTrue(decoder.getNumberOfUris() == 5);
		assertTrue(decoder.getUri(0).getNamespaceUri()
				.equals(Constants.XML_NULL_NS_URI));
		assertTrue(decoder.getUri(1).getNamespaceUri()
				.equals(Constants.XML_NS_URI));
		assertTrue(decoder.getUri(2).getNamespaceUri()
				.equals(Constants.XML_SCHEMA_INSTANCE_NS_URI));
		assertTrue(decoder.getUri(3).getNamespaceUri()
				.equals(Constants.XML_SCHEMA_NS_URI));
		assertTrue(decoder.getUri(4).getNamespaceUri()
				.equals("http://www.foo.com"));

		// Initial Entries in Local-Name Partitions
		assertTrue(decoder.getUri(0).getNumberOfQNames() == 0);
		// XML-NS "base", "id", "lang", "space"
		assertTrue(decoder.getUri(1).getNumberOfQNames() == 4);
		// XSI-NS "nil", "type"
		assertTrue(decoder.getUri(2).getNumberOfQNames() == 2);
		// XSD-NS "ENTITIES", ..., "unsignedShort"
		assertTrue(decoder.getUri(3).getNumberOfQNames() == 46);
		// http://www.foo.com, Person, email, name, person, personnel
		assertTrue(decoder.getUri(4).getNumberOfQNames() == 5);
		RuntimeUriContext uc4 = decoder.getUri(4);
		assertTrue(uc4.getQNameContext(0).getLocalName().equals("Person"));
		assertTrue(uc4.getQNameContext(1).getLocalName().equals("email"));
		assertTrue(uc4.getQNameContext(2).getLocalName().equals("name"));
		assertTrue(uc4.getQNameContext(3).getLocalName().equals("person"));
		assertTrue(uc4.getQNameContext(4).getLocalName().equals("personnel"));
	}

	// TODO XML schema, adding xsd local-names
	public void testSchema4() throws EXIException, IOException {
		GrammarFactory gf = GrammarFactory.newInstance();
		Grammars g = gf.createGrammars("./data/W3C/xsd/XMLSchema.xsd");
		EXIFactory exiFactory = DefaultEXIFactory.newInstance();
		exiFactory.setGrammars(g);
		EXIBodyEncoderInOrder encoder = new EXIBodyEncoderInOrder(exiFactory);
		encoder.initForEachRun();
		// GrammarContext gc = g.getGrammarContext();
		// EncoderContext encoderContext = new EncoderContextImpl(gc);
		// GrammarURIEntry[] gue = g.getGrammarEntries();

		// Initial Entries in Uri Partition
		assertTrue(encoder.getNumberOfUris() == 4);
		assertTrue(encoder.getUri(Constants.XML_NULL_NS_URI)
				.getNamespaceUriID() == 0);
		assertTrue(encoder.getUri(Constants.XML_NS_URI).getNamespaceUriID() == 1);
		assertTrue(encoder.getUri(Constants.XML_SCHEMA_INSTANCE_NS_URI)
				.getNamespaceUriID() == 2);
		assertTrue(encoder.getUri(Constants.XML_SCHEMA_NS_URI)
				.getNamespaceUriID() == 3);

		// Initial Entries in Local-Name Partitions
		// some more than usual, #34
		RuntimeUriContext uc0 = encoder.getUri(Constants.XML_NULL_NS_URI);
		assertTrue(uc0.getNumberOfQNames() > 30);
		assertTrue(uc0.getQNameContext("elementFormDefault") != null);
		// XML-NS "base", "id", "lang", "space"
		// XML-NS "base", "id", "lang", "space"
		assertTrue(encoder.getUri(Constants.XML_NS_URI).getNumberOfQNames() == 4);
		// XSI-NS "nil", "type"
		assertTrue(encoder.getUri(Constants.XML_SCHEMA_INSTANCE_NS_URI)
				.getNumberOfQNames() == 2);
		RuntimeUriContext uc3 = encoder.getUri(Constants.XML_SCHEMA_NS_URI);
		// XSD-NS "ENTITIES", ..., "unsignedShort"
		// Note: there should be some more new local-names!!! # 126
		assertTrue(uc3.getNumberOfQNames() > 120);
		assertTrue(uc3.getQNameContext("attributeGroupRef") != null);
	}

}
