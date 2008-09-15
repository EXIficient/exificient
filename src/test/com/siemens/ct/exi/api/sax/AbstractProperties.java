package com.siemens.ct.exi.api.sax;

import java.io.IOException;
import java.io.StringReader;

import org.custommonkey.xmlunit.XMLTestCase;
import org.custommonkey.xmlunit.XMLUnit;
import org.xml.sax.SAXException;

import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.helpers.DefaultEXIFactory;

public abstract class AbstractProperties extends XMLTestCase
{
	String xml;
	
	EXIFactory factory;
	
	@Override
	protected void setUp()
	{
		factory = DefaultEXIFactory.newInstance ( );	
	}
	
	
	
	protected void isXMLEqual( String sXMLDecoded ) throws SAXException, IOException
	{
		StringReader control = new StringReader( xml );
		StringReader test = new StringReader( sXMLDecoded );

		XMLUnit.setIgnoreWhitespace( true ); 
		
		//Diff diff = compareXML ( control, test );
		// XMLUnit.setNormalize ( true );
		
		XMLUnit.setIgnoreAttributeOrder ( true );
		
		assertXMLEqual ( control, test );
	}
	
	
	static final String SIMPLE_XSD = "<schema xmlns='http://www.w3.org/2001/XMLSchema'>" +
				" <element name='root'>" +
				"  <complexType>" +
				"   <sequence maxOccurs='unbounded'>" +
				"    <element name='strings' type='string' />" +
				"   </sequence>" +
				"  </complexType>" +
				" </element>" +
				"</schema>";

	static final String SIMPLE_XML = "<root>" +
				" <strings>a</strings>" +
				" <strings>b</strings>" +
				" <strings>c</strings>" +
				" <strings>a</strings>" +
				"</root>";
	
	static final String UNEXPECTED_ROOT_XSD = "<schema xmlns='http://www.w3.org/2001/XMLSchema'>" +
				" <element name='root'>" +
				"  <complexType>" +
				"   <sequence maxOccurs='unbounded'>" +
				"    <element name='strings' type='string' />" +
				"   </sequence>" +
				"  </complexType>" +
				" </element>" +
				"</schema>";

	static final String UNEXPECTED_ROOT_XML = "<unknown>" + 
				"?!?!" + 
				"</unknown>";
	
	
	static final String XSI_TYPE_XSD = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>" +
				" <xs:element name='values'>" +
				"  <xs:complexType>" +
				"   <xs:sequence maxOccurs='unbounded'>" +
				"    <xs:element ref='value' />" +
				"   </xs:sequence>" +
				"  </xs:complexType>" +
				" </xs:element>" +
				"" +
				" <xs:element name='value' type='tValue' />" +
				"" +
				" <xs:complexType name='tValue' />" +
				"" +
				" <xs:complexType name='tDouble'>" +
				"  <xs:complexContent>" +
				"   <xs:extension base='tValue'>" +
				"    <xs:sequence>" +
				"     <xs:element name='val' type='xs:double' />" +
				"    </xs:sequence>" +
				"   </xs:extension>" +
				"  </xs:complexContent>" +
				" </xs:complexType>" +
				"" +
				" <xs:complexType name='tInteger'>" +
				"  <xs:complexContent>" +
				"   <xs:extension base='tValue'>" +
				"    <xs:sequence>" +
				"     <xs:element name='val' type='xs:int' />" +
				"    </xs:sequence>" +
				"   </xs:extension>" +
				"  </xs:complexContent>" +
				" </xs:complexType>" +
				"" +
				"</xs:schema>";
	
	static final String XSI_TYPE_XML = "<values xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' >" +
				" <value />" +
				" <value xsi:type='tDouble' >" +
				"   <val>12.00</val>" +
				" </value>" +
				" <value xsi:type='tInteger' >" + 
				"   <val>12</val>" +
				" </value>" +
				" <value xsi:type='tDouble' >" +
				"   <val>1.23</val>" +
				" </value>" +
				" <value />" +
				"</values>";
}
