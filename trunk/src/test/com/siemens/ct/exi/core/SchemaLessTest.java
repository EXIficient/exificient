/*
 * Copyright (C) 2007-2011 Siemens AG
 *
 * This program and its interfaces are free software;
 * you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.siemens.ct.exi.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.xml.namespace.QName;

import junit.framework.TestCase;

import org.xml.sax.SAXException;

import com.siemens.ct.exi.CodingMode;
import com.siemens.ct.exi.EXIBodyDecoder;
import com.siemens.ct.exi.EXIBodyEncoder;
import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.FidelityOptions;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.grammar.event.EventType;
import com.siemens.ct.exi.helpers.DefaultEXIFactory;

public class SchemaLessTest extends TestCase {

	public SchemaLessTest(String testName) {
		super(testName);
	}

	public void testSchemaLess0() throws IOException, SAXException,
			EXIException {
		EXIFactory factory = DefaultEXIFactory.newInstance();

		factory.setFidelityOptions(FidelityOptions.createDefault());
		factory.setCodingMode(CodingMode.BIT_PACKED);
		// factory.setGrammar ( GrammarFactory.getSchemaLessGrammar ( ) );

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		QName s1 = new QName("", "el1");

		// encoder
		{
			EXIBodyEncoder encoder = factory.createEXIBodyEncoder();
			encoder.setOutputStream(baos);
			String pfx = null; // unset according fidelity-options
			encoder.encodeStartDocument();
			encoder.encodeStartElement(s1.getNamespaceURI(), s1.getLocalPart(),
					pfx);
			encoder.encodeEndElement();
			encoder.encodeEndDocument();
			encoder.flush();
		}

		// decoder
		{
			EXIBodyDecoder decoder = factory.createEXIBodyDecoder();
			decoder.setInputStream(
					new ByteArrayInputStream(baos.toByteArray()));

			assertTrue(decoder.next() == EventType.START_DOCUMENT);
			decoder.decodeStartDocument();

			assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC);
			assertTrue(decoder.decodeStartElementGeneric().equals(s1));

			assertTrue(decoder.next() == EventType.END_ELEMENT_UNDECLARED);
			decoder.decodeEndElementUndeclared();

			assertTrue(decoder.next() == EventType.END_DOCUMENT);
			decoder.decodeEndDocument();
		}
	}

	public void testSchemaLess1() throws IOException, SAXException,
			EXIException {
		EXIFactory factory = DefaultEXIFactory.newInstance();

		factory.setFidelityOptions(FidelityOptions.createDefault());
		factory.setCodingMode(CodingMode.BIT_PACKED);
		// factory.setGrammar ( GrammarFactory.getSchemaLessGrammar ( ) );

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		QName root = new QName("", "root");
		QName el1 = new QName("", "el1");
		QName el2 = new QName("", "el2");
		QName el3 = new QName("", "el3");
		String ch1 = "a";
		String ch2 = "b";
		String ch3 = "c";

		// encoder
		{
			EXIBodyEncoder encoder = factory.createEXIBodyEncoder();
			encoder.setOutputStream(baos);

			String pfx = null; // unset according fidelity-options

			encoder.encodeStartDocument();
			encoder.encodeStartElement(root.getNamespaceURI(), root
					.getLocalPart(), pfx);

			encoder.encodeStartElement(el1.getNamespaceURI(), el1
					.getLocalPart(), pfx);
			encoder.encodeCharacters(ch1);
			encoder.encodeEndElement();

			encoder.encodeStartElement(el2.getNamespaceURI(), el2
					.getLocalPart(), pfx);
			encoder.encodeCharacters(ch2);
			encoder.encodeEndElement();

			encoder.encodeStartElement(el3.getNamespaceURI(), el3
					.getLocalPart(), pfx);
			encoder.encodeCharacters(ch3);
			encoder.encodeEndElement();

			encoder.encodeStartElement(el2.getNamespaceURI(), el2
					.getLocalPart(), pfx);
			encoder.encodeCharacters(ch1);
			encoder.encodeEndElement();

			encoder.encodeEndElement();
			encoder.encodeEndDocument();
			encoder.flush();
		}

		baos.flush();

		// decoder
		{
			EXIBodyDecoder decoder = factory.createEXIBodyDecoder();
			decoder.setInputStream(
					new ByteArrayInputStream(baos.toByteArray()));
			decoder.decodeStartDocument();

			assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC);
			assertTrue(decoder.decodeStartElementGeneric().equals(root));

			assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC_UNDECLARED);
			assertTrue(decoder.decodeStartElementGenericUndeclared().equals(el1));

			assertTrue(decoder.next() == EventType.CHARACTERS_GENERIC_UNDECLARED);
			assertTrue(decoder.decodeCharactersGenericUndeclared().toString().equals(ch1));

			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC_UNDECLARED);
			assertTrue(decoder.decodeStartElementGenericUndeclared().equals(el2));

			assertTrue(decoder.next() == EventType.CHARACTERS_GENERIC_UNDECLARED);
			assertTrue(decoder.decodeCharactersGenericUndeclared().toString().equals(ch2));

			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC_UNDECLARED);
			assertTrue(decoder.decodeStartElementGenericUndeclared().equals(el3));

			assertTrue(decoder.next() == EventType.CHARACTERS_GENERIC_UNDECLARED);
			assertTrue(decoder.decodeCharactersGenericUndeclared().toString().equals(ch3));

			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.START_ELEMENT);
			assertTrue(decoder.decodeStartElement().equals(el2));

			assertTrue(decoder.next() == EventType.CHARACTERS);
			assertTrue(decoder.decodeCharacters().toString().equals(ch1));

			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.END_DOCUMENT);
			decoder.decodeEndDocument();
		}
	}

	public void testSchemaLess2() throws IOException, SAXException,
			EXIException {
		EXIFactory factory = DefaultEXIFactory.newInstance();

		factory.setFidelityOptions(FidelityOptions.createDefault());
		factory.setCodingMode(CodingMode.BIT_PACKED);
		// factory.setGrammar ( GrammarFactory.getSchemaLessGrammar ( ) );

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		QName root = new QName("", "root");
		QName el1 = new QName("", "el1");
		QName el2 = new QName("", "el2");
		String ch1 = "a";
		String ch2 = "b";

		QName at1 = new QName("", "at1");
		String atCh1 = "at-ch";

		// encoder
		{
			EXIBodyEncoder encoder = factory.createEXIBodyEncoder();
			encoder.setOutputStream(baos);

			String pfx = null; // unset according fidelity-options

			encoder.encodeStartDocument();
			encoder.encodeStartElement(root.getNamespaceURI(), root
					.getLocalPart(), pfx);
			encoder.encodeAttribute(at1.getNamespaceURI(), at1.getLocalPart(),
					pfx, atCh1);

			encoder.encodeStartElement(el1.getNamespaceURI(), el1
					.getLocalPart(), pfx);
			encoder.encodeCharacters(ch1);
			encoder.encodeEndElement();

			encoder.encodeStartElement(el2.getNamespaceURI(), el2
					.getLocalPart(), pfx);
			encoder.encodeAttribute(at1.getNamespaceURI(), at1.getLocalPart(),
					pfx, atCh1);
			encoder.encodeCharacters(ch2);
			encoder.encodeEndElement();

			encoder.encodeEndElement();
			encoder.encodeEndDocument();
			encoder.flush();
		}

		baos.flush();

		// decoder
		{
			EXIBodyDecoder decoder = factory.createEXIBodyDecoder();
			decoder.setInputStream(
					new ByteArrayInputStream(baos.toByteArray()));
			decoder.decodeStartDocument();

			assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC);
			assertTrue(decoder.decodeStartElementGeneric().equals(root));

			assertTrue(decoder.next() == EventType.ATTRIBUTE_GENERIC_UNDECLARED);
			assertTrue(decoder.decodeAttributeGenericUndeclared().equals(at1));
			assertTrue(decoder.getAttributeValue().toString().equals(atCh1));

			assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC_UNDECLARED);
			assertTrue(decoder.decodeStartElementGenericUndeclared().equals(el1));

			assertTrue(decoder.next() == EventType.CHARACTERS_GENERIC_UNDECLARED);
			assertTrue(decoder.decodeCharactersGenericUndeclared().toString().equals(ch1));

			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC_UNDECLARED);
			assertTrue(decoder.decodeStartElementGenericUndeclared().equals(el2));
			
			assertTrue(decoder.next() == EventType.ATTRIBUTE_GENERIC_UNDECLARED);
			assertTrue(decoder.decodeAttributeGenericUndeclared().equals(at1));
			assertTrue(decoder.getAttributeValue().toString().equals(atCh1));

			assertTrue(decoder.next() == EventType.CHARACTERS_GENERIC_UNDECLARED);
			assertTrue(decoder.decodeCharactersGenericUndeclared().toString().equals(ch2));

			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.END_DOCUMENT);
			decoder.decodeEndDocument();
		}
	}

	public void testSchemaLess3() throws IOException, SAXException,
			EXIException {
		EXIFactory factory = DefaultEXIFactory.newInstance();

		factory.setFidelityOptions(FidelityOptions.createDefault());
		factory.setCodingMode(CodingMode.BIT_PACKED);
		// factory.setGrammar ( GrammarFactory.getSchemaLessGrammar ( ) );

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		QName root = new QName("", "root");
		QName el1 = new QName("", "el1");
		QName elx1 = new QName("", "elx1");
		QName elx2 = new QName("", "elx2");
		QName elx3 = new QName("", "elx3");

		QName elxx1 = new QName("", "elxx1");
		QName elxx2 = new QName("", "elxx2");

		String ch1 = "a";
		String ch2 = "b";
		String ch3 = "c";

		QName at1 = new QName("", "at1");
		String atCh1 = "at-ch1";
		String atCh2 = "at-ch2";

		// encoder
		{
			EXIBodyEncoder encoder = factory.createEXIBodyEncoder();
			encoder.setOutputStream(baos);

			String pfx = null; // unset according fidelity-options

			encoder.encodeStartDocument();
			encoder.encodeStartElement(root.getNamespaceURI(), root
					.getLocalPart(), pfx);

			encoder.encodeStartElement(el1.getNamespaceURI(), el1
					.getLocalPart(), pfx);
			encoder.encodeAttribute(at1.getNamespaceURI(), at1.getLocalPart(),
					pfx, atCh1);
			encoder.encodeStartElement(elx1.getNamespaceURI(), elx1
					.getLocalPart(), pfx);

			encoder.encodeStartElement(elxx1.getNamespaceURI(), elxx1
					.getLocalPart(), pfx);
			encoder.encodeCharacters(ch1);
			encoder.encodeEndElement();
			encoder.encodeStartElement(elxx2.getNamespaceURI(), elxx2
					.getLocalPart(), pfx);
			encoder.encodeCharacters(ch2);
			encoder.encodeEndElement();

			encoder.encodeEndElement();
			encoder.encodeStartElement(elx2.getNamespaceURI(), elx2
					.getLocalPart(), pfx);
			encoder.encodeCharacters(ch2);
			encoder.encodeEndElement();
			encoder.encodeStartElement(elx3.getNamespaceURI(), elx3
					.getLocalPart(), pfx);
			encoder.encodeCharacters(ch3);
			encoder.encodeEndElement();
			encoder.encodeEndElement();

			encoder.encodeStartElement(el1.getNamespaceURI(), el1
					.getLocalPart(), pfx);
			encoder.encodeAttribute(at1.getNamespaceURI(), at1.getLocalPart(),
					pfx, atCh2);
			encoder.encodeStartElement(elx1.getNamespaceURI(), elx1
					.getLocalPart(), pfx);

			encoder.encodeStartElement(elxx1.getNamespaceURI(), elxx1
					.getLocalPart(), pfx);
			encoder.encodeCharacters(ch1);
			encoder.encodeEndElement();
			encoder.encodeStartElement(elxx2.getNamespaceURI(), elxx2
					.getLocalPart(), pfx);
			encoder.encodeCharacters(ch2);
			encoder.encodeEndElement();

			encoder.encodeEndElement();
			encoder.encodeStartElement(elx2.getNamespaceURI(), elx2
					.getLocalPart(), pfx);
			encoder.encodeCharacters(ch2);
			encoder.encodeEndElement();
			encoder.encodeStartElement(elx3.getNamespaceURI(), elx3
					.getLocalPart(), pfx);
			encoder.encodeCharacters(ch3);
			encoder.encodeEndElement();
			encoder.encodeEndElement();

			encoder.encodeEndElement();
			encoder.encodeEndDocument();
			encoder.flush();
		}

		baos.flush();

		// decoder
		{
			EXIBodyDecoder decoder = factory.createEXIBodyDecoder();
			decoder.setInputStream(
					new ByteArrayInputStream(baos.toByteArray()));

			assertTrue(decoder.next() == EventType.START_DOCUMENT);
			decoder.decodeStartDocument();

			assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC);
			assertTrue(decoder.decodeStartElementGeneric().equals(root));

			/*
			 * first el1
			 */
			assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC_UNDECLARED);
			assertTrue(decoder.decodeStartElementGenericUndeclared().equals(el1));

			assertTrue(decoder.next() == EventType.ATTRIBUTE_GENERIC_UNDECLARED);
			assertTrue(decoder.decodeAttributeGenericUndeclared().equals(at1));
			assertTrue(decoder.getAttributeValue().toString().equals(atCh1));

			assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC_UNDECLARED);
			assertTrue(decoder.decodeStartElementGenericUndeclared().equals(elx1));

			assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC_UNDECLARED);
			assertTrue(decoder.decodeStartElementGenericUndeclared().equals(elxx1));

			assertTrue(decoder.next() == EventType.CHARACTERS_GENERIC_UNDECLARED);
			assertTrue(decoder.decodeCharactersGenericUndeclared().toString().equals(ch1));

			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC_UNDECLARED);
			assertTrue(decoder.decodeStartElementGenericUndeclared().equals(elxx2));

			assertTrue(decoder.next() == EventType.CHARACTERS_GENERIC_UNDECLARED);
			assertTrue(decoder.decodeCharactersGenericUndeclared().toString().equals(ch2));

			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC_UNDECLARED);
			assertTrue(decoder.decodeStartElementGenericUndeclared().equals(elx2));

			assertTrue(decoder.next() == EventType.CHARACTERS_GENERIC_UNDECLARED);
			assertTrue(decoder.decodeCharactersGenericUndeclared().toString().equals(ch2));

			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC_UNDECLARED);
			assertTrue(decoder.decodeStartElementGenericUndeclared().equals(elx3));

			assertTrue(decoder.next() == EventType.CHARACTERS_GENERIC_UNDECLARED);
			assertTrue(decoder.decodeCharactersGenericUndeclared().toString().equals(ch3));

			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();

			/*
			 * second el1
			 */
			// still generic start element, because first el1 was a StartTag
			// rule, this is a Content rule
			assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC_UNDECLARED);
			assertTrue(decoder.decodeStartElementGenericUndeclared().equals(el1));

			assertTrue(decoder.next() == EventType.ATTRIBUTE);
			assertTrue(decoder.decodeAttribute().equals(at1));
			assertTrue(decoder.getAttributeValue().toString().equals(atCh2));

			assertTrue(decoder.next() == EventType.START_ELEMENT);
			assertTrue(decoder.decodeStartElement().equals(elx1));

			assertTrue(decoder.next() == EventType.START_ELEMENT);
			assertTrue(decoder.decodeStartElement().equals(elxx1));

			assertTrue(decoder.next() == EventType.CHARACTERS);
			assertTrue(decoder.decodeCharacters().toString().equals(ch1));

			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.START_ELEMENT);
			assertTrue(decoder.decodeStartElement().equals(elxx2));

			assertTrue(decoder.next() == EventType.CHARACTERS);
			assertTrue(decoder.decodeCharacters().toString().equals(ch2));

			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.START_ELEMENT);
			assertTrue(decoder.decodeStartElement().equals(elx2));

			assertTrue(decoder.next() == EventType.CHARACTERS);
			assertTrue(decoder.decodeCharacters().toString().equals(ch2));

			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.START_ELEMENT);
			assertTrue(decoder.decodeStartElement().equals(elx3));

			assertTrue(decoder.next() == EventType.CHARACTERS);
			assertTrue(decoder.decodeCharacters().toString().equals(ch3));

			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.END_DOCUMENT);
			decoder.decodeEndDocument();
		}
	}

	/*
		 * Bug-ID: ID: 3030325
		 * 
		 * xsi:type learning in schema-less mode
		 * 
		 * <?xml version="1.0" encoding="utf-8"?>
		 * <a xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
		 * <b xsi:type="c"/>
		 * <b xsi:type="d"/>
		 * </a>
		 */
		public void testSchemaLess4() throws IOException, SAXException,
				EXIException {
			EXIFactory factory = DefaultEXIFactory.newInstance();
		
			factory.setFidelityOptions(FidelityOptions.createDefault());
			factory.setCodingMode(CodingMode.BIT_PACKED);
			// factory.setGrammar ( GrammarFactory.getSchemaLessGrammar ( ) );
		
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			QName a = new QName("", "root");
			QName b = new QName("", "b");
		
			QName atXsiType = new QName("http://www.w3.org/2001/XMLSchema-instance", "type");
			
			// encoder
			{
				EXIBodyEncoder encoder = factory.createEXIBodyEncoder();
				encoder.setOutputStream(baos);
		
				String pfx = null; // unset according fidelity-options
		
				encoder.encodeStartDocument();
				encoder.encodeStartElement(a.getNamespaceURI(), a
						.getLocalPart(), pfx);
		
				encoder.encodeStartElement(b.getNamespaceURI(), b
						.getLocalPart(), pfx);
				encoder.encodeXsiType("c", "");
	//			encoder.encodeAttribute(atXsiType.getNamespaceURI(), atXsiType.getLocalPart(),pfx, "c");
				encoder.encodeEndElement();
				
				encoder.encodeStartElement(b.getNamespaceURI(), b
						.getLocalPart(), pfx);
				encoder.encodeXsiType("d", "");
	//			encoder.encodeAttribute(atXsiType.getNamespaceURI(), atXsiType.getLocalPart(),pfx, "d");
				encoder.encodeEndElement();
		
				encoder.encodeEndElement(); // a
	
				encoder.encodeEndDocument();
				encoder.flush();
			}
		
			baos.flush();
		
			// decoder
			{
				EXIBodyDecoder decoder = factory.createEXIBodyDecoder();
				decoder.setInputStream(
						new ByteArrayInputStream(baos.toByteArray()));
		
				assertTrue(decoder.next() == EventType.START_DOCUMENT);
				decoder.decodeStartDocument();
		
				assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC);
				assertTrue(decoder.decodeStartElementGeneric().equals(a));
		
				/*
				 * first b
				 */
				assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC_UNDECLARED);
				assertTrue(decoder.decodeStartElementGenericUndeclared().equals(b));
				
				assertTrue(decoder.next() == EventType.ATTRIBUTE_GENERIC_UNDECLARED);
				assertTrue(decoder.decodeAttributeGenericUndeclared().equals(atXsiType));
				
				assertTrue(decoder.next() == EventType.END_ELEMENT_UNDECLARED);
				decoder.decodeEndElementUndeclared();
		
				/*
				 * second b
				 */
				assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC_UNDECLARED);
				assertTrue(decoder.decodeStartElementGenericUndeclared().equals(b));
		
				assertTrue(decoder.next() == EventType.ATTRIBUTE);
				QName at = decoder.decodeAttribute();
				assertTrue(at.equals(atXsiType));
				
				assertTrue(decoder.next() == EventType.END_ELEMENT);
				decoder.decodeEndElement();
				
				// end root a
				assertTrue(decoder.next() == EventType.END_ELEMENT);
				decoder.decodeEndElement();
		
				assertTrue(decoder.next() == EventType.END_DOCUMENT);
				decoder.decodeEndDocument();
			}
		}

}