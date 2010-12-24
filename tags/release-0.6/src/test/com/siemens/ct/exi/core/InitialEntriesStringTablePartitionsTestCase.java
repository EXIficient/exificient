package com.siemens.ct.exi.core;

import java.io.ByteArrayInputStream;

import javax.xml.XMLConstants;

import junit.framework.TestCase;

import com.siemens.ct.exi.GrammarFactory;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.grammar.Grammar;
import com.siemens.ct.exi.grammar.GrammarURIEntry;
import com.siemens.ct.exi.grammar.XSDGrammarBuilder;

public class InitialEntriesStringTablePartitionsTestCase extends TestCase {

	protected static boolean containsValue(String[] list, String val) {
		for (String li : list) {
			if (li.equals(val)) {
				return true;
			}
		}
		return false;
	}
	
	protected static Grammar getGrammarFor(String schemaAsString) throws EXIException {
		XSDGrammarBuilder xsdGB = XSDGrammarBuilder.newInstance();
		ByteArrayInputStream bais = new ByteArrayInputStream(schemaAsString
				.getBytes());
		xsdGB.loadGrammar(bais);
		return xsdGB.toGrammar();
	}

	public void testSchemaLess() throws EXIException {
		GrammarFactory gf = GrammarFactory.newInstance();
		Grammar g = gf.createSchemaLessGrammar();
		GrammarURIEntry[] gue = g.getGrammarEntries();

		// Initial Entries in Uri Partition
		assertTrue(gue.length == 3);
		assertTrue(gue[0].uri.equals(XMLConstants.NULL_NS_URI));
		assertTrue(gue[1].uri.equals(XMLConstants.XML_NS_URI));
		assertTrue(gue[2].uri
				.equals(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI));

		// Initial Entries in Prefix Partitions
		assertTrue(gue[0].prefixes.length == 1);
		assertTrue(gue[0].prefixes[0].equals(XMLConstants.DEFAULT_NS_PREFIX));
		assertTrue(gue[1].prefixes.length == 1);
		assertTrue(gue[1].prefixes[0].equals(XMLConstants.XML_NS_PREFIX));
		assertTrue(gue[2].prefixes.length == 1);
		assertTrue(gue[2].prefixes[0].equals("xsi"));

		// Initial Entries in Local-Name Partitions
		assertTrue(gue[0].localNames.length == 0);
		// XML-NS "base", "id", "lang", "space"
		assertTrue(gue[1].localNames.length == 4);
		assertTrue(containsValue(gue[1].localNames, "base"));
		assertTrue(containsValue(gue[1].localNames, "id"));
		assertTrue(containsValue(gue[1].localNames, "lang"));
		assertTrue(containsValue(gue[1].localNames, "space"));
		// XSI-NS "nil", "type"
		assertTrue(gue[2].localNames.length == 2);
		assertTrue(containsValue(gue[2].localNames, "nil"));
		assertTrue(containsValue(gue[2].localNames, "type"));
	}

	public void testXsdTypesOnly() throws EXIException {
		GrammarFactory gf = GrammarFactory.newInstance();
		Grammar g = gf.createXSDTypesOnlyGrammar();
		GrammarURIEntry[] gue = g.getGrammarEntries();

		// Initial Entries in Uri Partition
		assertTrue(gue.length == 4);
		assertTrue(gue[0].uri.equals(XMLConstants.NULL_NS_URI));
		assertTrue(gue[1].uri.equals(XMLConstants.XML_NS_URI));
		assertTrue(gue[2].uri
				.equals(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI));
		assertTrue(gue[3].uri.equals(XMLConstants.W3C_XML_SCHEMA_NS_URI));

		// Initial Entries in Prefix Partitions
		assertTrue(gue[0].prefixes.length == 1);
		assertTrue(gue[0].prefixes[0].equals(XMLConstants.DEFAULT_NS_PREFIX));
		assertTrue(gue[1].prefixes.length == 1);
		assertTrue(gue[1].prefixes[0].equals(XMLConstants.XML_NS_PREFIX));
		assertTrue(gue[2].prefixes.length == 1);
		assertTrue(gue[2].prefixes[0].equals("xsi"));

		// Initial Entries in Local-Name Partitions
		assertTrue(gue[0].localNames.length == 0);
		// XML-NS "base", "id", "lang", "space"
		assertTrue(gue[1].localNames.length == 4);
		assertTrue(containsValue(gue[1].localNames, "base"));
		assertTrue(containsValue(gue[1].localNames, "id"));
		assertTrue(containsValue(gue[1].localNames, "lang"));
		assertTrue(containsValue(gue[1].localNames, "space"));
		// XSI-NS "nil", "type"
		assertTrue(gue[2].localNames.length == 2);
		assertTrue(containsValue(gue[2].localNames, "nil"));
		assertTrue(containsValue(gue[2].localNames, "type"));
		// XSD-NS "ENTITIES", "ENTITY", "ID", "IDREF", "IDREFS", "NCName",
		// "NMTOKEN", "NMTOKENS", "NOTATION", "Name", "QName", "anySimpleType",
		// "anyType", "anyURI", "base64Binary", "boolean", "byte", "date",
		// "dateTime", "decimal", "double", "duration", "float", "gDay",
		// "gMonth", "gMonthDay", "gYear", "gYearMonth", "hexBinary", "int",
		// "integer", "language", "long", "negativeInteger",
		// "nonNegativeInteger", "nonPositiveInteger", "normalizedString",
		// "positiveInteger", "short", "string", "time", "token",
		// "unsignedByte", "unsignedInt", "unsignedLong", "unsignedShort"
		assertTrue(gue[3].localNames.length == 46);
		assertTrue(gue[3].localNames[0].equals("ENTITIES"));
		assertTrue(gue[3].localNames[45].equals("unsignedShort"));
	}

	// normal and simple schema
	public void testSchema1() throws EXIException {
		String schemaAsString = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
			+ "  <xs:simpleType name='Binary'>"
			+ "    <xs:restriction base='xs:base64Binary'>"
			+ "    </xs:restriction>"
			+ "  </xs:simpleType>"
			+ "</xs:schema>";
		
		Grammar g = getGrammarFor(schemaAsString);
		GrammarURIEntry[] gue = g.getGrammarEntries();
		
		// Initial Entries in Uri Partition
		assertTrue(gue.length == 4);
		assertTrue(gue[0].uri.equals(XMLConstants.NULL_NS_URI));
		assertTrue(gue[1].uri.equals(XMLConstants.XML_NS_URI));
		assertTrue(gue[2].uri
				.equals(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI));
		assertTrue(gue[3].uri.equals(XMLConstants.W3C_XML_SCHEMA_NS_URI));

		// Initial Entries in Prefix Partitions
		assertTrue(gue[0].prefixes.length == 1);
		assertTrue(gue[0].prefixes[0].equals(XMLConstants.DEFAULT_NS_PREFIX));
		assertTrue(gue[1].prefixes.length == 1);
		assertTrue(gue[1].prefixes[0].equals(XMLConstants.XML_NS_PREFIX));
		assertTrue(gue[2].prefixes.length == 1);
		assertTrue(gue[2].prefixes[0].equals("xsi"));

		// Initial Entries in Local-Name Partitions
		assertTrue(gue[0].localNames.length == 1);
		assertTrue(containsValue(gue[0].localNames, "Binary"));
		// XML-NS "base", "id", "lang", "space"
		assertTrue(gue[1].localNames.length == 4);
		assertTrue(containsValue(gue[1].localNames, "base"));
		assertTrue(containsValue(gue[1].localNames, "id"));
		assertTrue(containsValue(gue[1].localNames, "lang"));
		assertTrue(containsValue(gue[1].localNames, "space"));
		// XSI-NS "nil", "type"
		assertTrue(gue[2].localNames.length == 2);
		assertTrue(containsValue(gue[2].localNames, "nil"));
		assertTrue(containsValue(gue[2].localNames, "type"));
		// XSD-NS "ENTITIES", "ENTITY", "ID", "IDREF", "IDREFS", "NCName",
		// "NMTOKEN", "NMTOKENS", "NOTATION", "Name", "QName", "anySimpleType",
		// "anyType", "anyURI", "base64Binary", "boolean", "byte", "date",
		// "dateTime", "decimal", "double", "duration", "float", "gDay",
		// "gMonth", "gMonthDay", "gYear", "gYearMonth", "hexBinary", "int",
		// "integer", "language", "long", "negativeInteger",
		// "nonNegativeInteger", "nonPositiveInteger", "normalizedString",
		// "positiveInteger", "short", "string", "time", "token",
		// "unsignedByte", "unsignedInt", "unsignedLong", "unsignedShort"
		assertTrue(gue[3].localNames.length == 46);
		assertTrue(gue[3].localNames[0].equals("ENTITIES"));
		assertTrue(gue[3].localNames[45].equals("unsignedShort"));
		
	}

	// primer example
	public void testSchema2() throws EXIException {
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
		
		Grammar g = getGrammarFor(schemaAsString);
		GrammarURIEntry[] gue = g.getGrammarEntries();
		
		// Initial Entries in Uri Partition
		assertTrue(gue.length == 4);
		assertTrue(gue[0].uri.equals(XMLConstants.NULL_NS_URI));
		assertTrue(gue[1].uri.equals(XMLConstants.XML_NS_URI));
		assertTrue(gue[2].uri
				.equals(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI));
		assertTrue(gue[3].uri.equals(XMLConstants.W3C_XML_SCHEMA_NS_URI));
		
		// Initial Entries in Local-Name Partitions
		assertTrue(gue[0].localNames.length == 7);
		assertTrue(gue[0].localNames[0].equals("Note"));
		assertTrue(gue[0].localNames[1].equals("body"));
		assertTrue(gue[0].localNames[2].equals("category"));
		assertTrue(gue[0].localNames[3].equals("date"));
		assertTrue(gue[0].localNames[4].equals("note"));
		assertTrue(gue[0].localNames[5].equals("notebook"));
		assertTrue(gue[0].localNames[6].equals("subject"));
		// XML-NS "base", "id", "lang", "space"
		assertTrue(gue[1].localNames.length == 4);
		// XSI-NS "nil", "type"
		assertTrue(gue[2].localNames.length == 2);
		// XSD-NS "ENTITIES", ...,  "unsignedShort"
		assertTrue(gue[3].localNames.length == 46);
	}
	
	// other NS example
	public void testSchema3() throws EXIException {
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
		
		Grammar g = getGrammarFor(schemaAsString);
		GrammarURIEntry[] gue = g.getGrammarEntries();
		
		// Initial Entries in Uri Partition
		assertTrue(gue.length == 5);
		assertTrue(gue[0].uri.equals(XMLConstants.NULL_NS_URI));
		assertTrue(gue[1].uri.equals(XMLConstants.XML_NS_URI));
		assertTrue(gue[2].uri
				.equals(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI));
		assertTrue(gue[3].uri.equals(XMLConstants.W3C_XML_SCHEMA_NS_URI));
		assertTrue(gue[4].uri.equals("http://www.foo.com"));
		
		// Initial Entries in Local-Name Partitions
		assertTrue(gue[0].localNames.length == 0);
		// XML-NS "base", "id", "lang", "space"
		assertTrue(gue[1].localNames.length == 4);
		// XSI-NS "nil", "type"
		assertTrue(gue[2].localNames.length == 2);
		// XSD-NS "ENTITIES", ...,  "unsignedShort"
		assertTrue(gue[3].localNames.length == 46);
		// http://www.foo.com, Person, email, name, person, personnel
		assertTrue(gue[4].localNames.length == 5);
		assertTrue(gue[4].localNames[0].equals("Person"));
		assertTrue(gue[4].localNames[1].equals("email"));
		assertTrue(gue[4].localNames[2].equals("name"));
		assertTrue(gue[4].localNames[3].equals("person"));
		assertTrue(gue[4].localNames[4].equals("personnel"));
	}
	
	// TODO XML schema, adding xsd local-names
	public void testSchema4() throws EXIException {
		GrammarFactory gf = GrammarFactory.newInstance();
		Grammar g = gf.createGrammar("./data/W3C/xsd/XMLSchema.xsd");

		GrammarURIEntry[] gue = g.getGrammarEntries();
		
		// Initial Entries in Uri Partition
		assertTrue(gue.length == 4);
		assertTrue(gue[0].uri.equals(XMLConstants.NULL_NS_URI));
		assertTrue(gue[1].uri.equals(XMLConstants.XML_NS_URI));
		assertTrue(gue[2].uri
				.equals(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI));
		assertTrue(gue[3].uri.equals(XMLConstants.W3C_XML_SCHEMA_NS_URI));
		
		// Initial Entries in Local-Name Partitions
		// some more than usual, #34
		assertTrue(gue[0].localNames.length > 30);
		assertTrue(containsValue(gue[0].localNames, "elementFormDefault"));
		// XML-NS "base", "id", "lang", "space"
		assertTrue(gue[1].localNames.length == 4);
		// XSI-NS "nil", "type"
		assertTrue(gue[2].localNames.length == 2);
		// XSD-NS "ENTITIES", ...,  "unsignedShort"
		// Note: there should be some more new local-names!!! # 126
		assertTrue(gue[3].localNames.length > 120);
		assertTrue(containsValue(gue[3].localNames, "attributeGroupRef"));
	}

}
