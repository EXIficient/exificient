package com.siemens.ct.exi.core;

import java.io.ByteArrayInputStream;

import javax.xml.XMLConstants;

import junit.framework.TestCase;

import com.siemens.ct.exi.GrammarFactory;
import com.siemens.ct.exi.context.DecoderContext;
import com.siemens.ct.exi.context.DecoderContextImpl;
import com.siemens.ct.exi.context.EncoderContext;
import com.siemens.ct.exi.context.EncoderContextImpl;
import com.siemens.ct.exi.context.EvolvingUriContext;
import com.siemens.ct.exi.context.GrammarContext;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.grammar.Grammar;
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
		GrammarContext gc = g.getGrammarContext();
		// GrammarURIEntry[] gue = g.getGrammarEntries();

		// Initial Entries in Uri Partition
		assertTrue(gc.getNumberOfGrammarUriContexts() == 3);
		assertTrue(gc.getGrammarUriContext(0).getNamespaceUri().equals(XMLConstants.NULL_NS_URI));
		assertTrue(gc.getGrammarUriContext(1).getNamespaceUri().equals(XMLConstants.XML_NS_URI));
		assertTrue(gc.getGrammarUriContext(2).getNamespaceUri().equals(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI));

		// Initial Entries in Prefix Partitions
		assertTrue(gc.getGrammarUriContext(0).getNumberOfPrefixes() == 1);
		assertTrue(gc.getGrammarUriContext(0).getPrefix(0).equals(XMLConstants.DEFAULT_NS_PREFIX));
		assertTrue(gc.getGrammarUriContext(1).getNumberOfPrefixes()== 1);
		assertTrue(gc.getGrammarUriContext(1).getPrefix(0).equals(XMLConstants.XML_NS_PREFIX));
		assertTrue(gc.getGrammarUriContext(2).getNumberOfPrefixes() == 1);
		assertTrue(gc.getGrammarUriContext(2).getPrefix(0).equals("xsi"));

		// Initial Entries in Local-Name Partitions
		assertTrue(gc.getGrammarUriContext(0).getNumberOfQNames() == 0);
		// XML-NS "base", "id", "lang", "space"
		assertTrue(gc.getGrammarUriContext(1).getNumberOfQNames() == 4);
		assertTrue(gc.getGrammarUriContext(1).getQNameContext(0).getLocalName().equals("base"));
		assertTrue(gc.getGrammarUriContext(1).getQNameContext(1).getLocalName().equals("id"));
		assertTrue(gc.getGrammarUriContext(1).getQNameContext(2).getLocalName().equals("lang"));
		assertTrue(gc.getGrammarUriContext(1).getQNameContext(3).getLocalName().equals("space"));
		// XSI-NS "nil", "type"
		assertTrue(gc.getGrammarUriContext(2).getNumberOfQNames() == 2);
		assertTrue(gc.getGrammarUriContext(2).getQNameContext(0).getLocalName().equals("nil"));
		assertTrue(gc.getGrammarUriContext(2).getQNameContext(1).getLocalName().equals("type"));
	}

	public void testXsdTypesOnly() throws EXIException {
		GrammarFactory gf = GrammarFactory.newInstance();
		Grammar g = gf.createXSDTypesOnlyGrammar();
		GrammarContext gc = g.getGrammarContext();
//		GrammarURIEntry[] gue = g.getGrammarEntries();
		

		// Initial Entries in Uri Partition
		assertTrue(gc.getNumberOfGrammarUriContexts()  == 4);
		assertTrue(gc.getGrammarUriContext(0).getNamespaceUri().equals(XMLConstants.NULL_NS_URI));
		assertTrue(gc.getGrammarUriContext(1).getNamespaceUri().equals(XMLConstants.XML_NS_URI));
		assertTrue(gc.getGrammarUriContext(2).getNamespaceUri().equals(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI));
		assertTrue(gc.getGrammarUriContext(3).getNamespaceUri().equals(XMLConstants.W3C_XML_SCHEMA_NS_URI));

		// Initial Entries in Prefix Partitions
		assertTrue(gc.getGrammarUriContext(0).getNumberOfPrefixes() == 1);
		assertTrue(gc.getGrammarUriContext(0).getPrefix(0).equals(XMLConstants.DEFAULT_NS_PREFIX));
		assertTrue(gc.getGrammarUriContext(1).getNumberOfPrefixes()== 1);
		assertTrue(gc.getGrammarUriContext(1).getPrefix(0).equals(XMLConstants.XML_NS_PREFIX));
		assertTrue(gc.getGrammarUriContext(2).getNumberOfPrefixes() == 1);
		assertTrue(gc.getGrammarUriContext(2).getPrefix(0).equals("xsi"));

		// Initial Entries in Local-Name Partitions
		assertTrue(gc.getGrammarUriContext(0).getNumberOfQNames() == 0);
		// XML-NS "base", "id", "lang", "space"
		assertTrue(gc.getGrammarUriContext(1).getNumberOfQNames() == 4);
		assertTrue(gc.getGrammarUriContext(1).getQNameContext(0).getLocalName().equals("base"));
		assertTrue(gc.getGrammarUriContext(1).getQNameContext(1).getLocalName().equals("id"));
		assertTrue(gc.getGrammarUriContext(1).getQNameContext(2).getLocalName().equals("lang"));
		assertTrue(gc.getGrammarUriContext(1).getQNameContext(3).getLocalName().equals("space"));
		// XSI-NS "nil", "type"
		assertTrue(gc.getGrammarUriContext(2).getNumberOfQNames() == 2);
		assertTrue(gc.getGrammarUriContext(2).getQNameContext(0).getLocalName().equals("nil"));
		assertTrue(gc.getGrammarUriContext(2).getQNameContext(1).getLocalName().equals("type"));
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
		assertTrue(gc.getGrammarUriContext(3).getQNameContext(0).getLocalName().equals("ENTITIES"));
		assertTrue(gc.getGrammarUriContext(3).getQNameContext(45).getLocalName().equals("unsignedShort"));
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
		GrammarContext gc = g.getGrammarContext();
		
		DecoderContext decoderContext = new DecoderContextImpl(gc, null);
		// GrammarURIEntry[] gue = g.getGrammarEntries();
		
		
		// Initial Entries in Uri Partition
		assertTrue(decoderContext.getNumberOfUris() == 4);
		assertTrue(decoderContext.getUriContext(0).getNamespaceUri().equals(XMLConstants.NULL_NS_URI));
		assertTrue(decoderContext.getUriContext(1).getNamespaceUri().equals(XMLConstants.XML_NS_URI));
		assertTrue(decoderContext.getUriContext(2).getNamespaceUri().equals(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI));
		assertTrue(decoderContext.getUriContext(3).getNamespaceUri().equals(XMLConstants.W3C_XML_SCHEMA_NS_URI));

		// Initial Entries in Prefix Partitions
		assertTrue(decoderContext.getUriContext(0).getNumberOfPrefixes() == 1);
		assertTrue(decoderContext.getUriContext(0).getPrefix(0).equals(XMLConstants.DEFAULT_NS_PREFIX));
		assertTrue(decoderContext.getUriContext(1).getNumberOfPrefixes() == 1);
		assertTrue(decoderContext.getUriContext(1).getPrefix(0).equals(XMLConstants.XML_NS_PREFIX));
		assertTrue(decoderContext.getUriContext(2).getNumberOfPrefixes() == 1);
		assertTrue(decoderContext.getUriContext(2).getPrefix(0).equals("xsi"));

		// Initial Entries in Local-Name Partitions
		assertTrue(decoderContext.getUriContext(0).getNumberOfQNames() == 1);
		assertTrue(decoderContext.getUriContext(0).getQNameContext(0).getLocalName().equals("Binary"));
		// XML-NS "base", "id", "lang", "space"
		assertTrue(decoderContext.getUriContext(1).getNumberOfQNames() == 4);
		assertTrue(decoderContext.getUriContext(1).getQNameContext(0).getLocalName().equals("base"));
		assertTrue(decoderContext.getUriContext(1).getQNameContext(1).getLocalName().equals("id"));
		assertTrue(decoderContext.getUriContext(1).getQNameContext(2).getLocalName().equals("lang"));
		assertTrue(decoderContext.getUriContext(1).getQNameContext(3).getLocalName().equals("space"));
		// XSI-NS "nil", "type"
		assertTrue(decoderContext.getUriContext(2).getNumberOfQNames()== 2);
		assertTrue(decoderContext.getUriContext(2).getQNameContext(0).getLocalName().equals("nil"));
		assertTrue(decoderContext.getUriContext(2).getQNameContext(1).getLocalName().equals("type"));
		// XSD-NS "ENTITIES", "ENTITY", "ID", "IDREF", "IDREFS", "NCName",
		// "NMTOKEN", "NMTOKENS", "NOTATION", "Name", "QName", "anySimpleType",
		// "anyType", "anyURI", "base64Binary", "boolean", "byte", "date",
		// "dateTime", "decimal", "double", "duration", "float", "gDay",
		// "gMonth", "gMonthDay", "gYear", "gYearMonth", "hexBinary", "int",
		// "integer", "language", "long", "negativeInteger",
		// "nonNegativeInteger", "nonPositiveInteger", "normalizedString",
		// "positiveInteger", "short", "string", "time", "token",
		// "unsignedByte", "unsignedInt", "unsignedLong", "unsignedShort"
		assertTrue(decoderContext.getUriContext(3).getNumberOfQNames() == 46);
		assertTrue(decoderContext.getUriContext(3).getQNameContext(0).getLocalName().equals("ENTITIES"));
		assertTrue(decoderContext.getUriContext(3).getQNameContext(45).getLocalName().equals("unsignedShort"));
		
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
		GrammarContext gc = g.getGrammarContext();
		EncoderContext encoderContext = new EncoderContextImpl(gc, null);
		//GrammarURIEntry[] gue = g.getGrammarEntries();
		
		// Initial Entries in Uri Partition
		assertTrue(encoderContext.getNumberOfUris() == 4);
		assertTrue(encoderContext.getUriContext(XMLConstants.NULL_NS_URI).getNamespaceUriID() == 0 );
		assertTrue(encoderContext.getUriContext(XMLConstants.XML_NS_URI).getNamespaceUriID() == 1);
		assertTrue(encoderContext.getUriContext(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI).getNamespaceUriID() == 2);
		assertTrue(encoderContext.getUriContext(XMLConstants.W3C_XML_SCHEMA_NS_URI).getNamespaceUriID() == 3);
		
		// Initial Entries in Local-Name Partitions
		EvolvingUriContext uc0 = encoderContext.getUriContext(XMLConstants.NULL_NS_URI);
		assertTrue(uc0.getNumberOfQNames() == 7);
		assertTrue(uc0.getQNameContext("Note").getLocalNameID() == 0);
		assertTrue(uc0.getQNameContext("body").getLocalNameID() == 1);
		assertTrue(uc0.getQNameContext("category").getLocalNameID() == 2);
		assertTrue(uc0.getQNameContext("date").getLocalNameID() == 3);
		assertTrue(uc0.getQNameContext("note").getLocalNameID() == 4);
		assertTrue(uc0.getQNameContext("notebook").getLocalNameID() == 5);
		assertTrue(uc0.getQNameContext("subject").getLocalNameID() == 6);
		// XML-NS "base", "id", "lang", "space"
		assertTrue(encoderContext.getUriContext(XMLConstants.XML_NS_URI).getNumberOfQNames() == 4);
		// XSI-NS "nil", "type"
		assertTrue(encoderContext.getUriContext(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI).getNumberOfQNames()  == 2);
		// XSD-NS "ENTITIES", ...,  "unsignedShort"
		assertTrue(encoderContext.getUriContext(XMLConstants.W3C_XML_SCHEMA_NS_URI).getNumberOfQNames()== 46);
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
//		GrammarURIEntry[] gue = g.getGrammarEntries();
		GrammarContext gc = g.getGrammarContext();
		DecoderContext decoderContext = new DecoderContextImpl(gc, null);
		
		// Initial Entries in Uri Partition
		assertTrue(decoderContext.getNumberOfUris() == 5);
		assertTrue(decoderContext.getUriContext(0).getNamespaceUri().equals(XMLConstants.NULL_NS_URI));
		assertTrue(decoderContext.getUriContext(1).getNamespaceUri().equals(XMLConstants.XML_NS_URI));
		assertTrue(decoderContext.getUriContext(2).getNamespaceUri().equals(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI));
		assertTrue(decoderContext.getUriContext(3).getNamespaceUri().equals(XMLConstants.W3C_XML_SCHEMA_NS_URI));
		assertTrue(decoderContext.getUriContext(4).getNamespaceUri().equals("http://www.foo.com"));
		
		// Initial Entries in Local-Name Partitions
		assertTrue(decoderContext.getUriContext(0).getNumberOfQNames()  == 0);
		// XML-NS "base", "id", "lang", "space"
		assertTrue(decoderContext.getUriContext(1).getNumberOfQNames() == 4);
		// XSI-NS "nil", "type"
		assertTrue(decoderContext.getUriContext(2).getNumberOfQNames() == 2);
		// XSD-NS "ENTITIES", ...,  "unsignedShort"
		assertTrue(decoderContext.getUriContext(3).getNumberOfQNames() == 46);
		// http://www.foo.com, Person, email, name, person, personnel
		assertTrue(decoderContext.getUriContext(4).getNumberOfQNames() == 5);
		EvolvingUriContext uc4 =decoderContext.getUriContext(4);
		assertTrue(uc4.getQNameContext(0).getLocalName().equals("Person"));
		assertTrue(uc4.getQNameContext(1).getLocalName().equals("email"));
		assertTrue(uc4.getQNameContext(2).getLocalName().equals("name"));
		assertTrue(uc4.getQNameContext(3).getLocalName().equals("person"));
		assertTrue(uc4.getQNameContext(4).getLocalName().equals("personnel"));
	}
	
	// TODO XML schema, adding xsd local-names
	public void testSchema4() throws EXIException {
		GrammarFactory gf = GrammarFactory.newInstance();
		Grammar g = gf.createGrammar("./data/W3C/xsd/XMLSchema.xsd");
		GrammarContext gc = g.getGrammarContext();
		EncoderContext encoderContext = new EncoderContextImpl(gc, null);
//		GrammarURIEntry[] gue = g.getGrammarEntries();
		
		// Initial Entries in Uri Partition
		assertTrue(encoderContext.getNumberOfUris() == 4);
		assertTrue(encoderContext.getUriContext(XMLConstants.NULL_NS_URI).getNamespaceUriID() == 0 );
		assertTrue(encoderContext.getUriContext(XMLConstants.XML_NS_URI).getNamespaceUriID() == 1);
		assertTrue(encoderContext.getUriContext(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI).getNamespaceUriID() == 2);
		assertTrue(encoderContext.getUriContext(XMLConstants.W3C_XML_SCHEMA_NS_URI).getNamespaceUriID() == 3);
		
		// Initial Entries in Local-Name Partitions
		// some more than usual, #34
		EvolvingUriContext uc0 = encoderContext.getUriContext(XMLConstants.NULL_NS_URI);
		assertTrue(uc0.getNumberOfQNames() > 30);
		assertTrue(uc0.getQNameContext("elementFormDefault") != null);
		// XML-NS "base", "id", "lang", "space"
		// XML-NS "base", "id", "lang", "space"
		assertTrue(encoderContext.getUriContext(XMLConstants.XML_NS_URI).getNumberOfQNames() == 4);
		// XSI-NS "nil", "type"
		assertTrue(encoderContext.getUriContext(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI).getNumberOfQNames()  == 2);
		EvolvingUriContext uc3 = encoderContext.getUriContext(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		// XSD-NS "ENTITIES", ...,  "unsignedShort"
		// Note: there should be some more new local-names!!! # 126
		assertTrue(uc3.getNumberOfQNames() > 120);
		assertTrue(uc3.getQNameContext("attributeGroupRef")!=null);
	}

}
