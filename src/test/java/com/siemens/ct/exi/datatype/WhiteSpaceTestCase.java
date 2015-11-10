/*
 * Copyright (c) 2007-2015 Siemens AG
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

package com.siemens.ct.exi.datatype;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import junit.framework.TestCase;

import com.siemens.ct.exi.EXIBodyDecoder;
import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.EXIStreamDecoder;
import com.siemens.ct.exi.TestSAXEncoder;
import com.siemens.ct.exi.grammars.GrammarTest;
import com.siemens.ct.exi.grammars.Grammars;
import com.siemens.ct.exi.grammars.event.EventType;
import com.siemens.ct.exi.helpers.DefaultEXIFactory;

public class WhiteSpaceTestCase extends TestCase {

	public WhiteSpaceTestCase(String testName) {
		super(testName);
	}
	
	public void testXmlSpacePreserve0() throws Exception {
		EXIFactory factory = DefaultEXIFactory.newInstance();
		
		String c1 = "\n\t";
		String c = " This is     great. ";
		String c2 = "\n\n";
		String xml = "<root xml:space='preserve'>" + c1 + "<test>" + c + "</test>" + c2 + "</root>";
		
		// encode to EXI
		TestSAXEncoder enc = new TestSAXEncoder(factory);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		enc.encodeTo(new ByteArrayInputStream(xml.getBytes()), baos);

		// decode
		{
			EXIStreamDecoder streamDecoder = new EXIStreamDecoder(factory);
			EXIBodyDecoder decoder = streamDecoder.decodeHeader(new ByteArrayInputStream(baos.toByteArray()));

			assertTrue(decoder.next() == EventType.START_DOCUMENT);
			decoder.decodeStartDocument();

			assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC);
			assertTrue(decoder.decodeStartElement().getLocalName().equals("root"));
			
			assertTrue(decoder.next() == EventType.ATTRIBUTE_GENERIC_UNDECLARED);
			assertTrue(decoder.decodeAttribute().getLocalName().equals("space"));
			assertTrue(decoder.getAttributeValue().toString().equals("preserve"));
			
			assertTrue(decoder.next() == EventType.CHARACTERS_GENERIC_UNDECLARED);
			String svCH1 = decoder.decodeCharacters().toString(); 
			assertTrue("'" + svCH1 + "' != '" + c1 + "'", svCH1.equals(c1));
			
			assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC_UNDECLARED);
			assertTrue(decoder.decodeStartElement().getLocalName().equals("test"));

			assertTrue(decoder.next() == EventType.CHARACTERS_GENERIC_UNDECLARED);
			String sv = decoder.decodeCharacters().toString(); 
			assertTrue("'" + sv + "' != '" + c + "'", sv.equals(c));

			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();
			
			assertTrue(decoder.next() == EventType.CHARACTERS_GENERIC_UNDECLARED);
			String svCH2 = decoder.decodeCharacters().toString(); 
			assertTrue("'" + svCH2 + "' != '" + c2 + "'", svCH2.equals(c2));
			
			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.END_DOCUMENT);
			decoder.decodeEndDocument();
		}
	}
	
	public void testXmlSpacePreserve1() throws Exception {
		EXIFactory factory = DefaultEXIFactory.newInstance();
		
		String c1 = "\n\t";
		String c = " This is     great. ";
		String c2 = "\n\n";
		String xml = "<root>" + c1 + "<test xml:space='preserve'>" + c + "</test>" + c2 + "</root>";
		
		// encode to EXI
		TestSAXEncoder enc = new TestSAXEncoder(factory);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		enc.encodeTo(new ByteArrayInputStream(xml.getBytes()), baos);

		// decode
		{
			EXIStreamDecoder streamDecoder = new EXIStreamDecoder(factory);
			EXIBodyDecoder decoder = streamDecoder.decodeHeader(new ByteArrayInputStream(baos.toByteArray()));

			assertTrue(decoder.next() == EventType.START_DOCUMENT);
			decoder.decodeStartDocument();

			assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC);
			assertTrue(decoder.decodeStartElement().getLocalName().equals("root"));
			
//			assertTrue(decoder.next() == EventType.CHARACTERS_GENERIC_UNDECLARED);
//			String svCH1 = decoder.decodeCharacters().toString(); 
//			assertTrue("'" + svCH1 + "' != '" + c1 + "'", svCH1.equals(c1));
			
			assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC_UNDECLARED);
			assertTrue(decoder.decodeStartElement().getLocalName().equals("test"));
			
			assertTrue(decoder.next() == EventType.ATTRIBUTE_GENERIC_UNDECLARED);
			assertTrue(decoder.decodeAttribute().getLocalName().equals("space"));
			assertTrue(decoder.getAttributeValue().toString().equals("preserve"));

			assertTrue(decoder.next() == EventType.CHARACTERS_GENERIC_UNDECLARED);
			String sv = decoder.decodeCharacters().toString(); 
			assertTrue("'" + sv + "' != '" + c + "'", sv.equals(c));

			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();
			
//			assertTrue(decoder.next() == EventType.CHARACTERS_GENERIC_UNDECLARED);
//			String svCH2 = decoder.decodeCharacters().toString(); 
//			assertTrue("'" + svCH2 + "' != '" + c2 + "'", svCH2.equals(c2));
			
			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.END_DOCUMENT);
			decoder.decodeEndDocument();
		}
	}
	
	public void testXmlSpacePreserve2() throws Exception {
		EXIFactory factory = DefaultEXIFactory.newInstance();
		
		String c1 = "\n\t";
		String c = " This is     great. ";
		String c2 = "\n\n";
		String xml = "<root xml:space='preserve'>" + c1 + "<test xml:space='default'>" + c + "</test>" + c2 + "</root>";
		
		// encode to EXI
		TestSAXEncoder enc = new TestSAXEncoder(factory);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		enc.encodeTo(new ByteArrayInputStream(xml.getBytes()), baos);

		// decode
		{
			EXIStreamDecoder streamDecoder = new EXIStreamDecoder(factory);
			EXIBodyDecoder decoder = streamDecoder.decodeHeader(new ByteArrayInputStream(baos.toByteArray()));

			assertTrue(decoder.next() == EventType.START_DOCUMENT);
			decoder.decodeStartDocument();

			assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC);
			assertTrue(decoder.decodeStartElement().getLocalName().equals("root"));
			
			assertTrue(decoder.next() == EventType.ATTRIBUTE_GENERIC_UNDECLARED);
			assertTrue(decoder.decodeAttribute().getLocalName().equals("space"));
			assertTrue(decoder.getAttributeValue().toString().equals("preserve"));
			
			assertTrue(decoder.next() == EventType.CHARACTERS_GENERIC_UNDECLARED);
			String svCH1 = decoder.decodeCharacters().toString(); 
			assertTrue("'" + svCH1 + "' != '" + c1 + "'", svCH1.equals(c1));
			
			assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC_UNDECLARED);
			assertTrue(decoder.decodeStartElement().getLocalName().equals("test"));
			
			assertTrue(decoder.next() == EventType.ATTRIBUTE_GENERIC_UNDECLARED);
			assertTrue(decoder.decodeAttribute().getLocalName().equals("space"));
			assertTrue(decoder.getAttributeValue().toString().equals("default"));

			assertTrue(decoder.next() == EventType.CHARACTERS_GENERIC_UNDECLARED);
			String sv = decoder.decodeCharacters().toString(); 
			// simple data preserved
			assertTrue("'" + sv + "' != '" + c + "'", sv.equals(c));

			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();
			
			assertTrue(decoder.next() == EventType.CHARACTERS_GENERIC_UNDECLARED);
			String svCH2 = decoder.decodeCharacters().toString(); 
			assertTrue("'" + svCH2 + "' != '" + c2 + "'", svCH2.equals(c2));
			
			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.END_DOCUMENT);
			decoder.decodeEndDocument();
		}
	}
	
	
	public void testXmlSpacePreserve3() throws Exception {
		EXIFactory factory = DefaultEXIFactory.newInstance();
		
		String c1 = "\n\t";
		// String c = " This is     great. ";
		String c2 = "\n\n";
		String cx = "  "; // should be removed
		String xml = "<root xml:space='preserve'>" + c1 + "<test xml:space='default'>" + cx + "<foo />" + cx + "</test>" + c2 + "</root>";
		
		// encode to EXI
		TestSAXEncoder enc = new TestSAXEncoder(factory);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		enc.encodeTo(new ByteArrayInputStream(xml.getBytes()), baos);

		// decode
		{
			EXIStreamDecoder streamDecoder = new EXIStreamDecoder(factory);
			EXIBodyDecoder decoder = streamDecoder.decodeHeader(new ByteArrayInputStream(baos.toByteArray()));

			assertTrue(decoder.next() == EventType.START_DOCUMENT);
			decoder.decodeStartDocument();

			assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC);
			assertTrue(decoder.decodeStartElement().getLocalName().equals("root"));
			
			assertTrue(decoder.next() == EventType.ATTRIBUTE_GENERIC_UNDECLARED);
			assertTrue(decoder.decodeAttribute().getLocalName().equals("space"));
			assertTrue(decoder.getAttributeValue().toString().equals("preserve"));
			
			assertTrue(decoder.next() == EventType.CHARACTERS_GENERIC_UNDECLARED);
			String svCH1 = decoder.decodeCharacters().toString(); 
			assertTrue("'" + svCH1 + "' != '" + c1 + "'", svCH1.equals(c1));
			
			assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC_UNDECLARED);
			assertTrue(decoder.decodeStartElement().getLocalName().equals("test"));
			
			assertTrue(decoder.next() == EventType.ATTRIBUTE_GENERIC_UNDECLARED);
			assertTrue(decoder.decodeAttribute().getLocalName().equals("space"));
			assertTrue(decoder.getAttributeValue().toString().equals("default"));

			// Note cx should be removed (complex data)
			
			assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC_UNDECLARED);
			assertTrue(decoder.decodeStartElement().getLocalName().equals("foo"));
			
			assertTrue(decoder.next() == EventType.END_ELEMENT_UNDECLARED);
			decoder.decodeEndElement();
			
			// Note cx should be removed again (complex data)

			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();
			
			assertTrue(decoder.next() == EventType.CHARACTERS_GENERIC_UNDECLARED);
			String svCH2 = decoder.decodeCharacters().toString(); 
			assertTrue("'" + svCH2 + "' != '" + c2 + "'", svCH2.equals(c2));
			
			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.END_DOCUMENT);
			decoder.decodeEndDocument();
		}
	}
	
	public void testXmlSpaceDefault() throws Exception {
		EXIFactory factory = DefaultEXIFactory.newInstance();
		
		String c1 = "\n\t";
		String c = " This is     great. ";
		String c2 = "\n\n";
		String xml = "<root xml:space='default'>" + c1 + "<test>" + c + "</test>" + c2 + "</root>";
		
		// encode to EXI
		TestSAXEncoder enc = new TestSAXEncoder(factory);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		enc.encodeTo(new ByteArrayInputStream(xml.getBytes()), baos);

		// decode
		{
			EXIStreamDecoder streamDecoder = new EXIStreamDecoder(factory);
			EXIBodyDecoder decoder = streamDecoder.decodeHeader(new ByteArrayInputStream(baos.toByteArray()));

			assertTrue(decoder.next() == EventType.START_DOCUMENT);
			decoder.decodeStartDocument();

			assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC);
			assertTrue(decoder.decodeStartElement().getLocalName().equals("root"));
			
			assertTrue(decoder.next() == EventType.ATTRIBUTE_GENERIC_UNDECLARED);
			assertTrue(decoder.decodeAttribute().getLocalName().equals("space"));
			assertTrue(decoder.getAttributeValue().toString().equals("default"));
			
//			assertTrue(decoder.next() == EventType.CHARACTERS_GENERIC_UNDECLARED);
//			String svCH1 = decoder.decodeCharacters().toString(); 
//			assertTrue("'" + svCH1 + "' != '" + c1 + "'", svCH1.equals(c1));
			
			assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC_UNDECLARED);
			assertTrue(decoder.decodeStartElement().getLocalName().equals("test"));

			assertTrue(decoder.next() == EventType.CHARACTERS_GENERIC_UNDECLARED);
			String sv = decoder.decodeCharacters().toString(); 
			assertTrue("'" + sv + "' != '" + c + "'", sv.equals(c));

			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();
			
//			assertTrue(decoder.next() == EventType.CHARACTERS_GENERIC_UNDECLARED);
//			String svCH2 = decoder.decodeCharacters().toString(); 
//			assertTrue("'" + svCH2 + "' != '" + c2 + "'", svCH2.equals(c2));
			
			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.END_DOCUMENT);
			decoder.decodeEndDocument();
		}
	}
	

	// http://www.w3.org/TR/2000/WD-xml-2e-20000814#AVNormalize
	public void testSchemaLessAttributes0() throws Exception {
		EXIFactory factory = DefaultEXIFactory.newInstance();
		
		
		String xml = "<foo a='\n\nxyz' ></foo>";
		
		// encode to EXI
		TestSAXEncoder enc = new TestSAXEncoder(factory);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		enc.encodeTo(new ByteArrayInputStream(xml.getBytes()), baos);

		// decode
		{
			EXIStreamDecoder streamDecoder = new EXIStreamDecoder(factory);
			EXIBodyDecoder decoder = streamDecoder.decodeHeader(new ByteArrayInputStream(baos.toByteArray()));

			assertTrue(decoder.next() == EventType.START_DOCUMENT);
			decoder.decodeStartDocument();

			assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC);
			assertTrue(decoder.decodeStartElement().getLocalName().equals("foo"));

			assertTrue(decoder.next() == EventType.ATTRIBUTE_GENERIC_UNDECLARED);
			assertTrue(decoder.decodeAttribute().getLocalName().equals("a"));
			// #x20 #x20 x y z
			assertTrue(decoder.getAttributeValue().toString().equals("  xyz"));

			assertTrue(decoder.next() == EventType.END_ELEMENT_UNDECLARED);
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.END_DOCUMENT);
			decoder.decodeEndDocument();
		}
	}

	
	public void testSchemaLessAttributes1() throws Exception {
		EXIFactory factory = DefaultEXIFactory.newInstance();
		
		
		String xml = "<!DOCTYPE  foo ["+ "<!ENTITY d \"&#xD;\">" + "<!ENTITY a \"&#xA;\">" + "<!ENTITY da \"&#xD;&#xA;\">" + "]>"
				+ "<foo a='&d;&d;A&a;&a;B&da;' ></foo>";
		
		// encode to EXI
		TestSAXEncoder enc = new TestSAXEncoder(factory);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		enc.encodeTo(new ByteArrayInputStream(xml.getBytes()), baos);

		// decode
		{
			EXIStreamDecoder streamDecoder = new EXIStreamDecoder(factory);
			EXIBodyDecoder decoder = streamDecoder.decodeHeader(new ByteArrayInputStream(baos.toByteArray()));

			assertTrue(decoder.next() == EventType.START_DOCUMENT);
			decoder.decodeStartDocument();

			assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC);
			assertTrue(decoder.decodeStartElement().getLocalName().equals("foo"));

			assertTrue(decoder.next() == EventType.ATTRIBUTE_GENERIC_UNDECLARED);
			assertTrue(decoder.decodeAttribute().getLocalName().equals("a"));
			// #xD #xD A #xA #xA B #xD #xD
			assertTrue(decoder.getAttributeValue().toString().equals("  A  B  "));

			assertTrue(decoder.next() == EventType.END_ELEMENT_UNDECLARED);
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.END_DOCUMENT);
			decoder.decodeEndDocument();
		}
	}
	
	public void testSchemaLessAttributes2() throws Exception {
		EXIFactory factory = DefaultEXIFactory.newInstance();
		
		String xml = "<!DOCTYPE  foo ["+ "<!ENTITY d \"&#xD;\">" + "<!ENTITY a \"&#xA;\">" + "<!ENTITY da \"&#xD;&#xA;\">" + "]>"
				+ "<foo a='&#xd;&#xd;A&#xa;&#xa;B&#xd;&#xa;' ></foo>";
		
		// encode to EXI
		TestSAXEncoder enc = new TestSAXEncoder(factory);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		enc.encodeTo(new ByteArrayInputStream(xml.getBytes()), baos);

		// decode
		{
			EXIStreamDecoder streamDecoder = new EXIStreamDecoder(factory);
			EXIBodyDecoder decoder = streamDecoder.decodeHeader(new ByteArrayInputStream(baos.toByteArray()));

			assertTrue(decoder.next() == EventType.START_DOCUMENT);
			decoder.decodeStartDocument();

			assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC);
			assertTrue(decoder.decodeStartElement().getLocalName().equals("foo"));

			assertTrue(decoder.next() == EventType.ATTRIBUTE_GENERIC_UNDECLARED);
			assertTrue(decoder.decodeAttribute().getLocalName().equals("a"));
			// #xD #xD A #xA #xA B #xD #xD
			// ERROR in last character ??  is it #xA
			String c = "\r\rA\n\nB\r\n";
			String v = decoder.getAttributeValue().toString();
			assertTrue("'" + v + "' != '" + c + "'", v.equals(c));
			// Note #xD == carriage return
			// Note #xA == new line

			assertTrue(decoder.next() == EventType.END_ELEMENT_UNDECLARED);
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.END_DOCUMENT);
			decoder.decodeEndDocument();
		}
	}
	
	// Whitespace handling in TTFMS
	// https://lists.w3.org/Archives/Public/public-exi/2015Oct/0008.html
	// If it is schema-less: - Simple data (data between s+e) are all preserved.
	public void testSchemaLessElementContent0() throws Exception {
		EXIFactory factory = DefaultEXIFactory.newInstance();
		
		String c = "  text content ";
		String xml = "<foo>" + c + "</foo>";
		
		// encode to EXI
		TestSAXEncoder enc = new TestSAXEncoder(factory);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		enc.encodeTo(new ByteArrayInputStream(xml.getBytes()), baos);

		// decode
		{
			EXIStreamDecoder streamDecoder = new EXIStreamDecoder(factory);
			EXIBodyDecoder decoder = streamDecoder.decodeHeader(new ByteArrayInputStream(baos.toByteArray()));

			assertTrue(decoder.next() == EventType.START_DOCUMENT);
			decoder.decodeStartDocument();

			assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC);
			assertTrue(decoder.decodeStartElement().getLocalName().equals("foo"));

			assertTrue(decoder.next() == EventType.CHARACTERS_GENERIC_UNDECLARED);
			String sv = decoder.decodeCharacters().toString(); 
			assertTrue("'" + sv + "' != '" + c + "'", sv.equals(c));

			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.END_DOCUMENT);
			decoder.decodeEndDocument();
		}
	}
	

	
	// Whitespace handling in TTFMS
	// https://lists.w3.org/Archives/Public/public-exi/2015Oct/0008.html
	// If it is schema-less:
	// - Simple data (data between s+e) are all preserved.
	// - For complex data (data between s+s, e+s, e+e), whitespaces nodes (i.e.
	//   strings that consist solely of whitespaces) are removed.
	public void testSchemaLessElementContent1() throws Exception {
		EXIFactory factory = DefaultEXIFactory.newInstance();
		
		String c = "  text  X   content ";
		String xml = "<foo>  <inner>" + c + "</inner>  </foo>";
		
		// encode to EXI
		TestSAXEncoder enc = new TestSAXEncoder(factory);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		enc.encodeTo(new ByteArrayInputStream(xml.getBytes()), baos);

		// decode
		{
			EXIStreamDecoder streamDecoder = new EXIStreamDecoder(factory);
			EXIBodyDecoder decoder = streamDecoder.decodeHeader(new ByteArrayInputStream(baos.toByteArray()));

			assertTrue(decoder.next() == EventType.START_DOCUMENT);
			decoder.decodeStartDocument();

			assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC);
			assertTrue(decoder.decodeStartElement().getLocalName().equals("foo"));
			
			assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC_UNDECLARED);
			assertTrue(decoder.decodeStartElement().getLocalName().equals("inner"));

			assertTrue(decoder.next() == EventType.CHARACTERS_GENERIC_UNDECLARED);
			String sv = decoder.decodeCharacters().toString(); 
			assertTrue("'" + sv + "' != '" + c + "'", sv.equals(c));

			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();
			
			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.END_DOCUMENT);
			decoder.decodeEndDocument();
		}
	}
	
	
	// - For complex data (data between s+s, e+s, e+e), whitespaces nodes (i.e.
	//   strings that consist solely of whitespaces) are removed.
	public void testSchemaLessComplexContent1() throws Exception {
		EXIFactory factory = DefaultEXIFactory.newInstance();
		
		String c = "  text  X   content ";
		String cd1 = " C1 ";
		String cd2 = " C2 ";
		String xml = "<foo>" + cd1 + "<inner>" + c + "</inner>" + cd2 + "</foo>";
		
		// encode to EXI
		TestSAXEncoder enc = new TestSAXEncoder(factory);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		enc.encodeTo(new ByteArrayInputStream(xml.getBytes()), baos);

		// decode
		{
			EXIStreamDecoder streamDecoder = new EXIStreamDecoder(factory);
			EXIBodyDecoder decoder = streamDecoder.decodeHeader(new ByteArrayInputStream(baos.toByteArray()));

			assertTrue(decoder.next() == EventType.START_DOCUMENT);
			decoder.decodeStartDocument();

			assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC);
			assertTrue(decoder.decodeStartElement().getLocalName().equals("foo"));
			
			// complex data not trimmed given that it foes consist solely of whitespaces
			assertTrue(decoder.next() == EventType.CHARACTERS_GENERIC_UNDECLARED);
			String svCD1 = decoder.decodeCharacters().toString(); 
			assertTrue("'" + svCD1 + "' != '" + cd1 + "'", svCD1.equals(cd1));
			
			assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC_UNDECLARED);
			assertTrue(decoder.decodeStartElement().getLocalName().equals("inner"));

			assertTrue(decoder.next() == EventType.CHARACTERS_GENERIC_UNDECLARED);
			String sv = decoder.decodeCharacters().toString(); 
			assertTrue("'" + sv + "' != '" + c + "'", sv.equals(c));

			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();
			
			// complex data not trimmed given that it foes consist solely of whitespaces
			assertTrue(decoder.next() == EventType.CHARACTERS_GENERIC_UNDECLARED);
			String svCD2 = decoder.decodeCharacters().toString(); 
			assertTrue("'" + svCD2 + "' != '" + cd2 + "'", svCD2.equals(cd2));
			
			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.END_DOCUMENT);
			decoder.decodeEndDocument();
		}
	}
	
	
	// with attribute
	public void testSchemaLessElementContent2() throws Exception {
		EXIFactory factory = DefaultEXIFactory.newInstance();
		
		String c = "  text  X   content ";
		String xml = "<foo>  <inner at='foo'>" + c + "</inner>  </foo>";
		
		// encode to EXI
		TestSAXEncoder enc = new TestSAXEncoder(factory);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		enc.encodeTo(new ByteArrayInputStream(xml.getBytes()), baos);

		// decode
		{
			EXIStreamDecoder streamDecoder = new EXIStreamDecoder(factory);
			EXIBodyDecoder decoder = streamDecoder.decodeHeader(new ByteArrayInputStream(baos.toByteArray()));

			assertTrue(decoder.next() == EventType.START_DOCUMENT);
			decoder.decodeStartDocument();

			assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC);
			assertTrue(decoder.decodeStartElement().getLocalName().equals("foo"));
			
			assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC_UNDECLARED);
			assertTrue(decoder.decodeStartElement().getLocalName().equals("inner"));
			
			assertTrue(decoder.next() == EventType.ATTRIBUTE_GENERIC_UNDECLARED);
			assertTrue(decoder.decodeAttribute().getLocalName().equals("at"));
			assertTrue(decoder.getAttributeValue().toString().equals("foo"));

			assertTrue(decoder.next() == EventType.CHARACTERS_GENERIC_UNDECLARED);
			String sv = decoder.decodeCharacters().toString(); 
			assertTrue("'" + sv + "' != '" + c + "'", sv.equals(c));

			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();
			
			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.END_DOCUMENT);
			decoder.decodeEndDocument();
		}
	}
	
	public void testSchemaInformedElementContentPreserve0() throws Exception {
		EXIFactory factory = DefaultEXIFactory.newInstance();
		
		String schema = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ " <xs:element name='foo' type='xs:string'>"
				+ " </xs:element>"
				+ "</xs:schema>";
	
		Grammars g = GrammarTest.getGrammarFromSchemaAsString(schema);
		factory.setGrammars(g);
		
		String c = "  text content ";
		String xml = "<foo>" + c + "</foo>";
		
		// encode to EXI
		TestSAXEncoder enc = new TestSAXEncoder(factory);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		enc.encodeTo(new ByteArrayInputStream(xml.getBytes()), baos);

		// decode
		{
			EXIStreamDecoder streamDecoder = new EXIStreamDecoder(factory);
			EXIBodyDecoder decoder = streamDecoder.decodeHeader(new ByteArrayInputStream(baos.toByteArray()));

			assertTrue(decoder.next() == EventType.START_DOCUMENT);
			decoder.decodeStartDocument();

			assertTrue(decoder.next() == EventType.START_ELEMENT);
			assertTrue(decoder.decodeStartElement().getLocalName().equals("foo"));

			assertTrue(decoder.next() == EventType.CHARACTERS);
			String sv = decoder.decodeCharacters().toString();
			//  string: default value of whiteSpace is preserve
			assertTrue("'" + sv + "' != '" + c + "'", sv.equals(c));

			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.END_DOCUMENT);
			decoder.decodeEndDocument();
		}
	}
	
	public void testSchemaInformedElementContentReplace0() throws Exception {
		EXIFactory factory = DefaultEXIFactory.newInstance();
		
		String schema = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ " <xs:element name='foo' type='stringReplace'>"
				+ " </xs:element>"
				+ "<xs:simpleType name='stringReplace'>"
				+ "<xs:restriction base='xs:string'>"
				+ "<xs:whiteSpace value='replace'/>"
				+ "</xs:restriction>"
				+ "</xs:simpleType>"
				+ "</xs:schema>";
	
		Grammars g = GrammarTest.getGrammarFromSchemaAsString(schema);
		factory.setGrammars(g);
		
		String c = " \t\r text content \n";
		String xml = "<foo>" + c + "</foo>";
		
		// encode to EXI
		TestSAXEncoder enc = new TestSAXEncoder(factory);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		enc.encodeTo(new ByteArrayInputStream(xml.getBytes()), baos);

		// decode
		{
			EXIStreamDecoder streamDecoder = new EXIStreamDecoder(factory);
			EXIBodyDecoder decoder = streamDecoder.decodeHeader(new ByteArrayInputStream(baos.toByteArray()));

			assertTrue(decoder.next() == EventType.START_DOCUMENT);
			decoder.decodeStartDocument();

			assertTrue(decoder.next() == EventType.START_ELEMENT);
			assertTrue(decoder.decodeStartElement().getLocalName().equals("foo"));

			assertTrue(decoder.next() == EventType.CHARACTERS);
			String sv = decoder.decodeCharacters().toString();
			// replace
		    // All occurrences of #x9 (tab), #xA (line feed) and #xD (carriage return) are replaced with #x20 (space)
			c = replace(c);	
			assertTrue("'" + sv + "' != '" + c + "'", sv.equals(c));

			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.END_DOCUMENT);
			decoder.decodeEndDocument();
		}
	}

	private String replace(String s) {
		// replace
	    // All occurrences of #x9 (tab), #xA (line feed) and #xD (carriage return) are replaced with #x20 (space)
		s = s.replace('\t', ' ');
		s = s.replace('\n', ' ');
		s = s.replace('\r', ' ');
		return s;
	}
	
	private String collapse(String s) {
		//  collapse
	    // After the processing implied by replace, contiguous sequences of #x20's are collapsed to a single #x20, and leading and trailing #x20's are removed. 
		s = replace(s);
		s = s.replaceAll("\\s+", " ");
		s = s.trim();
		return s;
	}
	
	public void testSchemaInformedElementContentCollapse0() throws Exception {
		EXIFactory factory = DefaultEXIFactory.newInstance();
		
		String schema = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ " <xs:element name='foo' type='stringCollapse'>"
				+ " </xs:element>"
				+ "<xs:simpleType name='stringCollapse'>"
				+ "<xs:restriction base='xs:string'>"
				+ "<xs:whiteSpace value='collapse'/>"
				+ "</xs:restriction>"
				+ "</xs:simpleType>"
				+ "</xs:schema>";
	
		Grammars g = GrammarTest.getGrammarFromSchemaAsString(schema);
		factory.setGrammars(g);
		
		String c = " \t\n text \t content xyz     \n \n\r";
		String xml = "<foo>" + c + "</foo>";
		
		// encode to EXI
		TestSAXEncoder enc = new TestSAXEncoder(factory);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		enc.encodeTo(new ByteArrayInputStream(xml.getBytes()), baos);

		// decode
		{
			EXIStreamDecoder streamDecoder = new EXIStreamDecoder(factory);
			EXIBodyDecoder decoder = streamDecoder.decodeHeader(new ByteArrayInputStream(baos.toByteArray()));

			assertTrue(decoder.next() == EventType.START_DOCUMENT);
			decoder.decodeStartDocument();

			assertTrue(decoder.next() == EventType.START_ELEMENT);
			assertTrue(decoder.decodeStartElement().getLocalName().equals("foo"));

			assertTrue(decoder.next() == EventType.CHARACTERS);
			String sv = decoder.decodeCharacters().toString();
			//  collapse
		    // After the processing implied by replace, contiguous sequences of #x20's are collapsed to a single #x20, and leading and trailing #x20's are removed. 
			c = collapse(c);			
			assertTrue("'" + sv + "' != '" + c + "'", sv.equals(c));

			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.END_DOCUMENT);
			decoder.decodeEndDocument();
		}
	}
	
	
	

}