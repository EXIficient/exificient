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

package com.siemens.ct.exi.main.grammars;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;

import junit.framework.TestCase;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.siemens.ct.exi.core.EXIFactory;
import com.siemens.ct.exi.core.grammars.Grammars;
import com.siemens.ct.exi.core.helpers.DefaultEXIFactory;
import com.siemens.ct.exi.main.api.sax.EXIResult;
import com.siemens.ct.exi.main.api.sax.SAXFactory;
import com.siemens.ct.exi.main.core.SchemaInformedTest;

public class TypeEmptyTest extends TestCase {
	
	public void testComplexType04Code() throws Exception {
		// acceptance.xsd 
		String schema = "<xsd:schema targetNamespace=\"urn:foo\"\r\n           xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\r\n           xmlns:foo=\"urn:foo\" xmlns:hoo=\"urn:hoo\">\r\n\r\n<!--<xsd:import namespace=\"urn:goo\" schemaLocation=\"acceptance_imported_goo.xsd\"/>\r\n<xsd:import namespace=\"urn:hoo\" schemaLocation=\"acceptance_imported_hoo.xsd\"/>\r\n<xsd:import namespace=\"urn:ioo\" schemaLocation=\"acceptance_imported_ioo.xsd\"/>\r\n<xsd:import schemaLocation=\"acceptance_imported_default.xsd\"/>-->\r\n\r\n<xsd:simpleType name=\"finalString\" final=\"#all\">\r\n  <xsd:restriction base=\"xsd:string\" />\r\n</xsd:simpleType>\r\n\r\n<xsd:complexType name=\"extendedDate\">\r\n  <xsd:simpleContent>\r\n    <xsd:extension base=\"xsd:date\">\r\n      <xsd:attribute ref=\"foo:aA\" />\r\n    </xsd:extension>\r\n  </xsd:simpleContent>\r\n</xsd:complexType>\r\n\r\n<xsd:simpleType name=\"listOfBytes\">\r\n  <xsd:list>\r\n    <xsd:simpleType>\r\n      <xsd:restriction base=\"xsd:byte\"/>\r\n    </xsd:simpleType>\r\n  </xsd:list>\r\n</xsd:simpleType>\r\n\r\n<xsd:element name=\"ANY\" type=\"xsd:anyType\"/>\r\n\r\n<xsd:element name=\"nillable_ANY\" type=\"xsd:anyType\" nillable=\"true\" />\r\n\r\n<xsd:complexType name=\"anyType\">\r\n  <xsd:complexContent mixed=\"true\">\r\n    <xsd:restriction base=\"xsd:anyType\">\r\n      <xsd:sequence>\r\n        <xsd:any namespace=\"##any\" processContents=\"lax\" minOccurs=\"0\" maxOccurs=\"unbounded\" />\r\n      </xsd:sequence>\r\n      <xsd:anyAttribute namespace=\"##any\" processContents=\"lax\" />\r\n    </xsd:restriction>\r\n  </xsd:complexContent>\r\n</xsd:complexType>\r\n\r\n<xsd:element name=\"A\">\r\n  <xsd:complexType>\r\n    <xsd:sequence>\r\n      <xsd:sequence>\r\n        <xsd:element ref=\"foo:AB\"/>\r\n        <xsd:element ref=\"foo:AC\" minOccurs=\"0\" maxOccurs=\"2\"/>\r\n      </xsd:sequence>\r\n      <xsd:sequence minOccurs=\"1\"/>\r\n      <xsd:element ref=\"foo:AD\"/>\r\n      <xsd:element ref=\"foo:AE\" minOccurs=\"0\"/>\r\n    </xsd:sequence>\r\n  </xsd:complexType>\r\n</xsd:element>\r\n\r\n<xsd:complexType name=\"B\">\r\n  <xsd:sequence>\r\n    <xsd:element ref=\"foo:AB\"/>\r\n    <xsd:element ref=\"foo:AC\" minOccurs=\"0\" maxOccurs=\"2\"/>\r\n    <xsd:element ref=\"foo:AD\" minOccurs=\"0\"/>\r\n  </xsd:sequence>\r\n</xsd:complexType>\r\n\r\n<xsd:complexType name=\"restricted_B\">\r\n  <xsd:complexContent>\r\n    <xsd:restriction base=\"foo:B\">\r\n      <xsd:sequence>\r\n        <xsd:element ref=\"foo:AB\"/>\r\n        <xsd:element ref=\"foo:AC\" minOccurs=\"0\"/>\r\n        <xsd:element ref=\"foo:AD\" minOccurs=\"0\"/>\r\n      </xsd:sequence>\r\n    </xsd:restriction>\r\n  </xsd:complexContent>\r\n</xsd:complexType>\r\n\r\n<xsd:complexType name=\"extended_B\">\r\n  <xsd:complexContent>\r\n    <xsd:extension base=\"foo:B\">\r\n      <xsd:attribute ref=\"foo:aA\" use=\"required\"/>\r\n    </xsd:extension>\r\n  </xsd:complexContent>\r\n</xsd:complexType>\r\n\r\n<xsd:element name=\"B\" type=\"foo:B\" nillable=\"false\"/>\r\n\r\n<xsd:element name=\"nillable_B\" type=\"foo:B\" nillable=\"true\" />\r\n\r\n<xsd:complexType name=\"C\">\r\n  <xsd:all>\r\n    <xsd:element ref=\"foo:AB\" minOccurs=\"0\" />\r\n    <xsd:element ref=\"foo:AC\" />\r\n  </xsd:all>\r\n</xsd:complexType>\r\n\r\n<xsd:element name=\"C\" type=\"foo:C\"/>\r\n\r\n<xsd:element name=\"AB\" type=\"xsd:anySimpleType\" nillable=\"false\" />\r\n<xsd:element name=\"nillable_AB\" type=\"xsd:anySimpleType\" nillable=\"true\" />\r\n<xsd:element name=\"AC\" type=\"xsd:anySimpleType\"/>\r\n<xsd:element name=\"AD\" type=\"xsd:anySimpleType\"/>\r\n<xsd:element name=\"AE\" type=\"xsd:anySimpleType\"/>\r\n<xsd:element name=\"AF\" type=\"xsd:date\"/>\r\n\r\n<xsd:element name=\"D\">\r\n  <xsd:complexType>\r\n    <xsd:sequence>\r\n      <xsd:sequence>\r\n        <xsd:element name=\"A\" minOccurs=\"0\" maxOccurs=\"2\" />\r\n        <xsd:sequence maxOccurs=\"2\">\r\n          <xsd:element name=\"B\" />\r\n          <xsd:element name=\"C\" minOccurs=\"0\" />\r\n          <xsd:element name=\"D\" minOccurs=\"0\" />\r\n        </xsd:sequence>\r\n      </xsd:sequence>\r\n      <xsd:element name=\"E\" minOccurs=\"0\" />\r\n      <xsd:element name=\"F\" minOccurs=\"0\" />\r\n    </xsd:sequence>\r\n  </xsd:complexType>\r\n</xsd:element>\r\n\r\n<xsd:element name=\"E\">\r\n  <xsd:complexType>\r\n    <xsd:sequence>\r\n      <xsd:choice>\r\n        <xsd:sequence maxOccurs=\"2\">\r\n          <xsd:element name=\"A\" minOccurs=\"0\" maxOccurs=\"2\" />\r\n          <xsd:element name=\"B\" />\r\n          <xsd:element name=\"C\" minOccurs=\"0\" />\r\n        </xsd:sequence>\r\n        <xsd:sequence minOccurs=\"0\">\r\n          <xsd:element name=\"D\" />\r\n          <xsd:element name=\"E\" />\r\n          <xsd:element name=\"F\" />\r\n        </xsd:sequence>\r\n      </xsd:choice>\r\n      <xsd:element name=\"G\" minOccurs=\"0\" />\r\n      <xsd:choice minOccurs=\"1\"/>\r\n      <xsd:element name=\"H\" minOccurs=\"0\" />\r\n    </xsd:sequence>\r\n  </xsd:complexType>\r\n</xsd:element>\r\n\r\n<xsd:attribute name=\"aA\" />\r\n<xsd:attribute name=\"aB\" />\r\n<xsd:attribute name=\"aC\" />\r\n<xsd:attribute name=\"aD\" />\r\n<xsd:attribute name=\"aE\" />\r\n<xsd:attribute name=\"aF\" />\r\n<xsd:attribute name=\"aG\" />\r\n<xsd:attribute name=\"aH\" />\r\n<xsd:attribute name=\"aI\" />\r\n<xsd:attribute name=\"aJ\" />\r\n<xsd:attribute name=\"aK\" type=\"xsd:date\" />\r\n<xsd:attribute name=\"aL\" type=\"xsd:integer\" />\r\n<xsd:attribute name=\"aM\" type=\"xsd:base64Binary\" />\r\n<xsd:attribute name=\"aN\" type=\"foo:listOfBytes\" />\r\n\r\n<xsd:attribute name=\"aBoolean\" type=\"xsd:boolean\" />\r\n\r\n<xsd:complexType name=\"F\">\r\n  <xsd:sequence>\r\n    <xsd:element ref=\"foo:AB\"/>\r\n  </xsd:sequence>\r\n  <xsd:attribute ref=\"foo:aB\" />\r\n  <xsd:attribute ref=\"foo:aC\" />\r\n  <xsd:attribute ref=\"foo:aA\" use=\"required\"/>\r\n</xsd:complexType>\r\n\r\n<xsd:complexType name=\"extended_F\">\r\n  <xsd:complexContent>\r\n    <xsd:extension base=\"foo:F\">\r\n      <xsd:anyAttribute namespace=\"##any\" />\r\n    </xsd:extension>\r\n  </xsd:complexContent>\r\n</xsd:complexType>\r\n\r\n<xsd:element name=\"F\" type=\"foo:F\" nillable=\"true\"/>\r\n\r\n<xsd:element name=\"G\" nillable=\"true\">\r\n  <xsd:complexType>\r\n    <xsd:sequence>\r\n      <xsd:element ref=\"foo:AB\" minOccurs=\"0\"/>\r\n    </xsd:sequence>\r\n    <xsd:attribute ref=\"foo:aB\" />\r\n    <xsd:attribute ref=\"foo:aC\" />\r\n    <xsd:attribute ref=\"foo:aA\" use=\"required\"/>\r\n  </xsd:complexType>\r\n</xsd:element>\r\n\r\n<xsd:element name=\"G2\">\r\n  <xsd:complexType><!-- Don't make it a named type -->\r\n      <xsd:anyAttribute namespace=\"##any\" />\r\n  </xsd:complexType>\r\n</xsd:element>\r\n\r\n<xsd:element name=\"G3\"><!-- Don't make it nillable -->\r\n  <xsd:complexType>\r\n      <xsd:sequence>\r\n        <xsd:element name=\"A\" />\r\n      </xsd:sequence>\r\n      <xsd:anyAttribute namespace=\"##any\" />\r\n  </xsd:complexType>\r\n</xsd:element>\r\n\r\n<xsd:element name=\"G4\">\r\n  <xsd:complexType>\r\n    <xsd:attribute name=\"aK\" type=\"xsd:positiveInteger\" use=\"required\"/>\r\n  </xsd:complexType>\r\n</xsd:element>\r\n\r\n<xsd:element name=\"H\">\r\n  <xsd:complexType>\r\n    <xsd:sequence>\r\n      <xsd:element name=\"A\" minOccurs=\"0\"/>\r\n      <xsd:any namespace=\"urn:eoo urn:goo\" />\r\n      <xsd:element name=\"B\" />\r\n      <xsd:any namespace=\"##other\" />\r\n    </xsd:sequence>\r\n  </xsd:complexType>\r\n</xsd:element>\r\n\r\n<xsd:element name=\"H2\">\r\n  <xsd:complexType>\r\n    <xsd:sequence>\r\n      <xsd:any namespace=\"##other\" minOccurs=\"0\" />\r\n      <xsd:any namespace=\"##targetNamespace ##local\" />\r\n    </xsd:sequence>\r\n  </xsd:complexType>\r\n</xsd:element>\r\n\r\n<xsd:element name=\"H3\">\r\n  <xsd:complexType>\r\n    <xsd:sequence>\r\n      <xsd:any namespace=\"urn:none_01\" minOccurs=\"0\" /> <!-- so that \"urn:none_01\" gets in uri partition. -->\r\n      <xsd:element ref=\"foo:AB\" />\r\n      <xsd:any namespace=\"##any\" minOccurs=\"0\" />\r\n    </xsd:sequence>\r\n  </xsd:complexType>\r\n</xsd:element>\r\n\r\n<!--<xsd:element name=\"H4\">\r\n  <xsd:complexType>\r\n    <xsd:sequence>\r\n      <xsd:sequence>\r\n        <xsd:any namespace=\"##local ##targetNamespace\" minOccurs=\"0\" maxOccurs=\"2\"/>\r\n        <xsd:element ref=\"hoo:AC\" minOccurs=\"0\"/>\r\n        <xsd:sequence>\r\n          <xsd:any namespace=\"urn:goo\" minOccurs=\"0\" />\r\n          <xsd:element ref=\"hoo:AB\" minOccurs=\"0\"/>\r\n        </xsd:sequence>\r\n      </xsd:sequence>\r\n      <xsd:any namespace=\"urn:ioo\" minOccurs=\"0\" />\r\n    </xsd:sequence>\r\n  </xsd:complexType>\r\n</xsd:element>-->\r\n\r\n<xsd:element name=\"I\" nillable=\"true\">\r\n  <xsd:complexType>\r\n    <xsd:choice>\r\n      <xsd:element name=\"A\">\r\n        <xsd:complexType>\r\n          <xsd:simpleContent>\r\n            <xsd:extension base=\"xsd:anySimpleType\">\r\n              <xsd:anyAttribute namespace=\"urn:hoo urn:none_02 urn:goo urn:foo urn:hoo urn:hoo ##local\" />\r\n            </xsd:extension>\r\n          </xsd:simpleContent>\r\n        </xsd:complexType>\r\n      </xsd:element>\r\n      <xsd:element name=\"B\">\r\n        <xsd:complexType>\r\n          <xsd:simpleContent>\r\n            <xsd:extension base=\"xsd:anySimpleType\">\r\n              <xsd:anyAttribute namespace=\"##other\" />\r\n            </xsd:extension>\r\n          </xsd:simpleContent>\r\n        </xsd:complexType>\r\n      </xsd:element>\r\n    </xsd:choice>\r\n    <xsd:attribute ref=\"foo:aF\" />\r\n    <xsd:attribute ref=\"foo:aI\" use=\"required\" />\r\n    <xsd:attribute ref=\"foo:aC\" />\r\n    <xsd:anyAttribute namespace=\"##any\" />\r\n  </xsd:complexType>\r\n</xsd:element>\r\n\r\n<xsd:complexType name=\"I2\">\r\n  <xsd:sequence>\r\n    <xsd:element name=\"A\" minOccurs=\"0\" />\r\n    <xsd:element name=\"B\" minOccurs=\"0\" />\r\n  </xsd:sequence>\r\n  <xsd:attribute ref=\"foo:aF\" />\r\n  <xsd:attribute ref=\"foo:aI\" use=\"required\" />\r\n  <xsd:attribute ref=\"foo:aC\" />\r\n  <xsd:anyAttribute namespace=\"urn:hoo urn:none_03 urn:goo\" />\r\n</xsd:complexType>\r\n\r\n<xsd:complexType name=\"extended_I2\">\r\n  <xsd:complexContent>\r\n    <xsd:extension base=\"foo:I2\">\r\n      <xsd:anyAttribute namespace=\"##targetNamespace ##local\" />\r\n    </xsd:extension>\r\n  </xsd:complexContent>\r\n</xsd:complexType>\r\n\r\n<xsd:element name=\"I2\" type=\"foo:I2\" nillable=\"true\" />\r\n\r\n<xsd:element name=\"J\">\r\n  <xsd:complexType>\r\n    <xsd:sequence maxOccurs=\"2\">\r\n      <xsd:element ref=\"foo:AB\"/>\r\n      <xsd:element ref=\"foo:AC\" minOccurs=\"0\" maxOccurs=\"2\"/>\r\n    </xsd:sequence>\r\n  </xsd:complexType>\r\n</xsd:element>\r\n\r\n<xsd:element name=\"K\" type=\"foo:finalString\"/>\r\n\r\n<xsd:element name=\"L\">\r\n  <xsd:complexType>\r\n    <xsd:sequence minOccurs=\"2\" maxOccurs=\"2\">\r\n      <xsd:element ref=\"foo:AB\"/>\r\n    </xsd:sequence>\r\n  </xsd:complexType>\r\n</xsd:element>\r\n\r\n<xsd:element name=\"M\">\r\n  <xsd:complexType>\r\n    <xsd:attribute ref=\"foo:aA\" />\r\n    <xsd:attribute ref=\"foo:aB\" use=\"required\" />\r\n    <xsd:attribute ref=\"foo:aC\" />\r\n    <xsd:attribute ref=\"foo:aD\" />\r\n  </xsd:complexType>\r\n</xsd:element>\r\n\r\n<xsd:attribute name=\"aJ4M2\" type=\"xsd:hexBinary\" />\r\n<xsd:attribute name=\"aK4M2\" type=\"xsd:hexBinary\" />\r\n<xsd:attribute name=\"aL4M2\" type=\"xsd:hexBinary\" />\r\n<xsd:attribute name=\"aM4M2\" type=\"xsd:hexBinary\" />\r\n<xsd:attribute name=\"aN4M2\" type=\"xsd:hexBinary\" />\r\n<xsd:attribute name=\"aO4M2\" type=\"xsd:hexBinary\" />\r\n\r\n<xsd:element name=\"M2\">\r\n  <xsd:complexType>\r\n    <xsd:sequence maxOccurs=\"unbounded\">\r\n      <xsd:element name=\"A\">\r\n        <xsd:complexType>\r\n          <xsd:attribute ref=\"foo:aK\" /><!-- xsd:date -->\r\n          <xsd:attribute ref=\"foo:aL\" /><!-- xsd:integer -->\r\n          <xsd:attribute ref=\"foo:aM\" /><!-- xsd:base64Binary -->\r\n          <xsd:attribute ref=\"foo:aN\" /><!-- xsd:listOfBytes -->\r\n        </xsd:complexType>\r\n      </xsd:element>\r\n    </xsd:sequence>\r\n  </xsd:complexType>\r\n</xsd:element>\r\n\r\n<xsd:element name=\"N\">\r\n  <xsd:complexType>\r\n    <xsd:sequence maxOccurs=\"unbounded\">\r\n      <xsd:element name=\"A\" type=\"foo:extendedDate\" />\r\n    </xsd:sequence>\r\n  </xsd:complexType>\r\n</xsd:element>\r\n\r\n<xsd:element name=\"P\">\r\n  <xsd:complexType>\r\n    <xsd:choice maxOccurs=\"unbounded\">\r\n      <xsd:element ref=\"foo:P1\" />\r\n      <xsd:element ref=\"foo:P2\" />\r\n      <xsd:element ref=\"foo:P3\" />\r\n    </xsd:choice>\r\n  </xsd:complexType>\r\n</xsd:element>\r\n\r\n<xsd:complexType name=\"P1\">\r\n  <xsd:sequence>\r\n    <xsd:element ref=\"foo:AB\" />\r\n  </xsd:sequence>\r\n</xsd:complexType>\r\n\r\n<xsd:element name=\"P1\" type=\"foo:P1\" />\r\n\r\n<xsd:complexType name=\"extended_P1\">\r\n  <xsd:complexContent>\r\n    <xsd:extension base=\"foo:P1\">\r\n      <xsd:sequence>\r\n        <xsd:element ref=\"foo:AB\" />\r\n      </xsd:sequence>\r\n    </xsd:extension>\r\n  </xsd:complexContent>\r\n</xsd:complexType>\r\n\r\n<xsd:complexType name=\"P2\">\r\n  <xsd:sequence>\r\n    <xsd:element ref=\"foo:AC\" />\r\n  </xsd:sequence>\r\n  <xsd:attribute ref=\"foo:aA\" />\r\n</xsd:complexType>\r\n\r\n<xsd:element name=\"P2\" type=\"foo:P2\" />\r\n\r\n<xsd:complexType name=\"extended_P2\">\r\n  <xsd:complexContent>\r\n    <xsd:extension base=\"foo:P2\">\r\n      <xsd:sequence>\r\n        <xsd:element ref=\"foo:AC\" />\r\n      </xsd:sequence>\r\n    </xsd:extension>\r\n  </xsd:complexContent>\r\n</xsd:complexType>\r\n\r\n<xsd:complexType name=\"P3\">\r\n  <xsd:sequence>\r\n    <xsd:element ref=\"foo:AD\" />\r\n  </xsd:sequence>\r\n  <xsd:anyAttribute namespace=\"urn:eoo urn:foo\" />\r\n</xsd:complexType>\r\n\r\n<xsd:element name=\"P3\" type=\"foo:P3\" />\r\n\r\n<xsd:complexType name=\"extended_P3\">\r\n  <xsd:complexContent>\r\n    <xsd:extension base=\"foo:P3\">\r\n      <xsd:sequence>\r\n        <xsd:element ref=\"foo:AD\" />\r\n      </xsd:sequence>\r\n    </xsd:extension>\r\n  </xsd:complexContent>\r\n</xsd:complexType>\r\n\r\n<xsd:element name=\"Q\">\r\n  <xsd:complexType mixed=\"true\">\r\n    <xsd:sequence>\r\n      <xsd:element name=\"Qc\" form=\"qualified\" minOccurs=\"0\">\r\n        <xsd:complexType/>\r\n      </xsd:element>\r\n      <xsd:any namespace=\"##other\" minOccurs=\"0\" />\r\n      <xsd:element ref=\"foo:Qb\" minOccurs=\"0\" /><!-- element Qb constitutes a substitution group. see below. -->\r\n      <xsd:any namespace=\"##local\" minOccurs=\"0\"/>\r\n      <xsd:element name=\"Qa\" form=\"qualified\" minOccurs=\"0\">\r\n        <xsd:complexType/>\r\n      </xsd:element>\r\n    </xsd:sequence>\r\n    <xsd:attribute ref=\"foo:aL\" /><!-- xsd:integer -->\r\n    <xsd:attribute ref=\"foo:aK\" /><!-- xsd:date -->\r\n    <xsd:anyAttribute namespace=\"urn:hoo urn:goo ##local urn:foo\" />\r\n  </xsd:complexType>\r\n</xsd:element>\r\n\r\n<xsd:element name=\"Qb\" type=\"foo:tQb\" />\r\n<xsd:element name=\"Qz\" type=\"foo:tQz\" substitutionGroup=\"foo:Qb\" />\r\n\r\n<xsd:complexType name=\"tQb\"/>\r\n<xsd:complexType name=\"tQz\">\r\n  <xsd:complexContent>\r\n    <xsd:extension base=\"foo:tQb\" />\r\n  </xsd:complexContent>\r\n</xsd:complexType>\r\n\r\n</xsd:schema>\r\n";
		
		Grammars g = SchemaInformedTest.getGrammarFromSchemaAsString(schema);
		
		String xml = "<foo:F xmlns:foo='urn:foo' xsi:nil='true'"
				+ " \n "
				+ " foo:aA='' foo:aB='' foo:aC=''"
				+ " \n "
				+ " xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'>"
				+ " \n "
				+ "  <!-- No content here. -->"
				+ " \n "
				+ "</foo:F>";
		
		
		EXIFactory ef = DefaultEXIFactory.newInstance();
		ef.setGrammars(g);
		// ef.getFidelityOptions().setFidelity(FidelityOptions.FEATURE_COMMENT, true);
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		// encode
		{
			EXIResult exiResult = new EXIResult(ef);
			exiResult.setOutputStream(baos);
			XMLReader xmlReader = XMLReaderFactory.createXMLReader();
			xmlReader.setContentHandler( exiResult.getHandler() );
			xmlReader.parse(new InputSource(new ByteArrayInputStream(xml.getBytes())));			
		}

		// decode
		ByteArrayOutputStream baosDecXML = new ByteArrayOutputStream();
		{
			InputSource is = new InputSource(new ByteArrayInputStream(
					baos.toByteArray()));
			XMLReader exiReader = new SAXFactory(ef).createEXIReader();

			Result result = new StreamResult(baosDecXML);
			SAXSource exiSource = new SAXSource(is);
			exiSource.setXMLReader(exiReader);
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.transform(exiSource, result);
		}
		
		
//		System.out.println(new String(baosDecXML.toByteArray()));
		
	}

}
