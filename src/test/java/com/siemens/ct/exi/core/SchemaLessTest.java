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

package com.siemens.ct.exi.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
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
import com.siemens.ct.exi.grammars.event.EventType;
import com.siemens.ct.exi.helpers.DefaultEXIFactory;
import com.siemens.ct.exi.values.StringValue;
import com.siemens.ct.exi.values.Value;

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
			decoder.setInputStream(new ByteArrayInputStream(baos.toByteArray()));

			assertTrue(decoder.next() == EventType.START_DOCUMENT);
			decoder.decodeStartDocument();

			assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC);
			// assertTrue(decoder.decodeStartElementGeneric().equals(s1));
			assertTrue(decoder.decodeStartElement().getQName().equals(s1));

			assertTrue(decoder.next() == EventType.END_ELEMENT_UNDECLARED);
			// decoder.decodeEndElementUndeclared();
			decoder.decodeEndElement();

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
		Value ch1 = new StringValue("a");
		Value ch2 = new StringValue("b");
		Value ch3 = new StringValue("c");

		// encoder
		{
			EXIBodyEncoder encoder = factory.createEXIBodyEncoder();
			encoder.setOutputStream(baos);

			String pfx = null; // unset according fidelity-options

			encoder.encodeStartDocument();
			encoder.encodeStartElement(root.getNamespaceURI(),
					root.getLocalPart(), pfx);

			encoder.encodeStartElement(el1.getNamespaceURI(),
					el1.getLocalPart(), pfx);
			encoder.encodeCharacters(ch1);
			encoder.encodeEndElement();

			encoder.encodeStartElement(el2.getNamespaceURI(),
					el2.getLocalPart(), pfx);
			encoder.encodeCharacters(ch2);
			encoder.encodeEndElement();

			encoder.encodeStartElement(el3.getNamespaceURI(),
					el3.getLocalPart(), pfx);
			encoder.encodeCharacters(ch3);
			encoder.encodeEndElement();

			encoder.encodeStartElement(el2.getNamespaceURI(),
					el2.getLocalPart(), pfx);
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
			decoder.setInputStream(new ByteArrayInputStream(baos.toByteArray()));
			decoder.decodeStartDocument();

			assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC);
			// assertTrue(decoder.decodeStartElementGeneric().equals(root));
			assertTrue(decoder.decodeStartElement().getQName().equals(root));

			assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC_UNDECLARED);
			// assertTrue(decoder.decodeStartElementGenericUndeclared().equals(el1));
			assertTrue(decoder.decodeStartElement().getQName().equals(el1));

			assertTrue(decoder.next() == EventType.CHARACTERS_GENERIC_UNDECLARED);
			// assertTrue(decoder.decodeCharactersGenericUndeclared().equals(ch1));
			assertTrue(decoder.decodeCharacters().equals(ch1));

			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC_UNDECLARED);
			// assertTrue(decoder.decodeStartElementGenericUndeclared().equals(el2));
			assertTrue(decoder.decodeStartElement().getQName().equals(el2));

			assertTrue(decoder.next() == EventType.CHARACTERS_GENERIC_UNDECLARED);
			// assertTrue(decoder.decodeCharactersGenericUndeclared().equals(ch2));
			assertTrue(decoder.decodeCharacters().equals(ch2));

			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC_UNDECLARED);
			// assertTrue(decoder.decodeStartElementGenericUndeclared().equals(el3));
			assertTrue(decoder.decodeStartElement().getQName().equals(el3));

			assertTrue(decoder.next() == EventType.CHARACTERS_GENERIC_UNDECLARED);
			// assertTrue(decoder.decodeCharactersGenericUndeclared().equals(ch3));
			assertTrue(decoder.decodeCharacters().equals(ch3));

			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.START_ELEMENT);
			assertTrue(decoder.decodeStartElement().getQName().equals(el2));

			assertTrue(decoder.next() == EventType.CHARACTERS);
			assertTrue(decoder.decodeCharacters().equals(ch1));

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
		Value ch1 = new StringValue("a");
		Value ch2 = new StringValue("b");

		QName at1 = new QName("", "at1");
		Value atCh1 = new StringValue("at-ch");

		// encoder
		{
			EXIBodyEncoder encoder = factory.createEXIBodyEncoder();
			encoder.setOutputStream(baos);

			String pfx = null; // unset according fidelity-options

			encoder.encodeStartDocument();
			encoder.encodeStartElement(root.getNamespaceURI(),
					root.getLocalPart(), pfx);
			encoder.encodeAttribute(at1.getNamespaceURI(), at1.getLocalPart(),
					pfx, atCh1);

			encoder.encodeStartElement(el1.getNamespaceURI(),
					el1.getLocalPart(), pfx);
			encoder.encodeCharacters(ch1);
			encoder.encodeEndElement();

			encoder.encodeStartElement(el2.getNamespaceURI(),
					el2.getLocalPart(), pfx);
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
			decoder.setInputStream(new ByteArrayInputStream(baos.toByteArray()));
			decoder.decodeStartDocument();

			assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC);
			// assertTrue(decoder.decodeStartElementGeneric().equals(root));
			assertTrue(decoder.decodeStartElement().getQName().equals(root));

			assertTrue(decoder.next() == EventType.ATTRIBUTE_GENERIC_UNDECLARED);
			// assertTrue(decoder.decodeAttributeGenericUndeclared().equals(at1));
			assertTrue(decoder.decodeAttribute().getQName().equals(at1));
			assertTrue(decoder.getAttributeValue().equals(atCh1));

			assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC_UNDECLARED);
			// assertTrue(decoder.decodeStartElementGenericUndeclared().equals(el1));
			assertTrue(decoder.decodeStartElement().getQName().equals(el1));

			assertTrue(decoder.next() == EventType.CHARACTERS_GENERIC_UNDECLARED);
			// assertTrue(decoder.decodeCharactersGenericUndeclared().equals(ch1));
			assertTrue(decoder.decodeCharacters().equals(ch1));

			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC_UNDECLARED);
			// assertTrue(decoder.decodeStartElementGenericUndeclared().equals(el2));
			assertTrue(decoder.decodeStartElement().getQName().equals(el2));

			assertTrue(decoder.next() == EventType.ATTRIBUTE_GENERIC_UNDECLARED);
			// assertTrue(decoder.decodeAttributeGenericUndeclared().equals(at1));
			assertTrue(decoder.decodeAttribute().getQName().equals(at1));
			assertTrue(decoder.getAttributeValue().equals(atCh1));

			assertTrue(decoder.next() == EventType.CHARACTERS_GENERIC_UNDECLARED);
			// assertTrue(decoder.decodeCharactersGenericUndeclared().equals(ch2));
			assertTrue(decoder.decodeCharacters().equals(ch2));

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

		Value ch1 = new StringValue("a");
		Value ch2 = new StringValue("b");
		Value ch3 = new StringValue("c");

		QName at1 = new QName("", "at1");
		Value atCh1 = new StringValue("at-ch1");
		Value atCh2 = new StringValue("at-ch2");

		// encoder
		{
			EXIBodyEncoder encoder = factory.createEXIBodyEncoder();
			encoder.setOutputStream(baos);

			String pfx = null; // unset according fidelity-options

			encoder.encodeStartDocument();
			encoder.encodeStartElement(root.getNamespaceURI(),
					root.getLocalPart(), pfx);

			encoder.encodeStartElement(el1.getNamespaceURI(),
					el1.getLocalPart(), pfx);
			encoder.encodeAttribute(at1.getNamespaceURI(), at1.getLocalPart(),
					pfx, atCh1);
			encoder.encodeStartElement(elx1.getNamespaceURI(),
					elx1.getLocalPart(), pfx);

			encoder.encodeStartElement(elxx1.getNamespaceURI(),
					elxx1.getLocalPart(), pfx);
			encoder.encodeCharacters(ch1);
			encoder.encodeEndElement();
			encoder.encodeStartElement(elxx2.getNamespaceURI(),
					elxx2.getLocalPart(), pfx);
			encoder.encodeCharacters(ch2);
			encoder.encodeEndElement();

			encoder.encodeEndElement();
			encoder.encodeStartElement(elx2.getNamespaceURI(),
					elx2.getLocalPart(), pfx);
			encoder.encodeCharacters(ch2);
			encoder.encodeEndElement();
			encoder.encodeStartElement(elx3.getNamespaceURI(),
					elx3.getLocalPart(), pfx);
			encoder.encodeCharacters(ch3);
			encoder.encodeEndElement();
			encoder.encodeEndElement();

			encoder.encodeStartElement(el1.getNamespaceURI(),
					el1.getLocalPart(), pfx);
			encoder.encodeAttribute(at1.getNamespaceURI(), at1.getLocalPart(),
					pfx, atCh2);
			encoder.encodeStartElement(elx1.getNamespaceURI(),
					elx1.getLocalPart(), pfx);

			encoder.encodeStartElement(elxx1.getNamespaceURI(),
					elxx1.getLocalPart(), pfx);
			encoder.encodeCharacters(ch1);
			encoder.encodeEndElement();
			encoder.encodeStartElement(elxx2.getNamespaceURI(),
					elxx2.getLocalPart(), pfx);
			encoder.encodeCharacters(ch2);
			encoder.encodeEndElement();

			encoder.encodeEndElement();
			encoder.encodeStartElement(elx2.getNamespaceURI(),
					elx2.getLocalPart(), pfx);
			encoder.encodeCharacters(ch2);
			encoder.encodeEndElement();
			encoder.encodeStartElement(elx3.getNamespaceURI(),
					elx3.getLocalPart(), pfx);
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
			decoder.setInputStream(new ByteArrayInputStream(baos.toByteArray()));

			assertTrue(decoder.next() == EventType.START_DOCUMENT);
			decoder.decodeStartDocument();

			assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC);
			// assertTrue(decoder.decodeStartElementGeneric().equals(root));
			assertTrue(decoder.decodeStartElement().getQName().equals(root));

			/*
			 * first el1
			 */
			assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC_UNDECLARED);
			// assertTrue(decoder.decodeStartElementGenericUndeclared().equals(el1));
			assertTrue(decoder.decodeStartElement().getQName().equals(el1));

			assertTrue(decoder.next() == EventType.ATTRIBUTE_GENERIC_UNDECLARED);
			// assertTrue(decoder.decodeAttributeGenericUndeclared().equals(at1));
			assertTrue(decoder.decodeAttribute().getQName().equals(at1));
			assertTrue(decoder.getAttributeValue().equals(atCh1));

			assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC_UNDECLARED);
			// assertTrue(decoder.decodeStartElementGenericUndeclared().equals(elx1));
			assertTrue(decoder.decodeStartElement().getQName().equals(elx1));

			assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC_UNDECLARED);
			// assertTrue(decoder.decodeStartElementGenericUndeclared().equals(elxx1));
			assertTrue(decoder.decodeStartElement().getQName().equals(elxx1));

			assertTrue(decoder.next() == EventType.CHARACTERS_GENERIC_UNDECLARED);
			// assertTrue(decoder.decodeCharactersGenericUndeclared().equals(ch1));
			assertTrue(decoder.decodeCharacters().equals(ch1));

			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC_UNDECLARED);
			// assertTrue(decoder.decodeStartElementGenericUndeclared().equals(elxx2));
			assertTrue(decoder.decodeStartElement().getQName().equals(elxx2));

			assertTrue(decoder.next() == EventType.CHARACTERS_GENERIC_UNDECLARED);
			// assertTrue(decoder.decodeCharactersGenericUndeclared().equals(ch2));
			assertTrue(decoder.decodeCharacters().equals(ch2));

			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC_UNDECLARED);
			// assertTrue(decoder.decodeStartElementGenericUndeclared().equals(elx2));
			assertTrue(decoder.decodeStartElement().getQName().equals(elx2));

			assertTrue(decoder.next() == EventType.CHARACTERS_GENERIC_UNDECLARED);
			// assertTrue(decoder.decodeCharactersGenericUndeclared().equals(ch2));
			assertTrue(decoder.decodeCharacters().equals(ch2));

			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC_UNDECLARED);
			// assertTrue(decoder.decodeStartElementGenericUndeclared().equals(elx3));
			assertTrue(decoder.decodeStartElement().getQName().equals(elx3));

			assertTrue(decoder.next() == EventType.CHARACTERS_GENERIC_UNDECLARED);
			// assertTrue(decoder.decodeCharactersGenericUndeclared().equals(ch3));
			assertTrue(decoder.decodeCharacters().equals(ch3));

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
			// assertTrue(decoder.decodeStartElementGenericUndeclared().equals(el1));
			assertTrue(decoder.decodeStartElement().getQName().equals(el1));

			assertTrue(decoder.next() == EventType.ATTRIBUTE);
			assertTrue(decoder.decodeAttribute().getQName().equals(at1));
			assertTrue(decoder.getAttributeValue().equals(atCh2));

			assertTrue(decoder.next() == EventType.START_ELEMENT);
			assertTrue(decoder.decodeStartElement().getQName().equals(elx1));

			assertTrue(decoder.next() == EventType.START_ELEMENT);
			assertTrue(decoder.decodeStartElement().getQName().equals(elxx1));

			assertTrue(decoder.next() == EventType.CHARACTERS);
			assertTrue(decoder.decodeCharacters().equals(ch1));

			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.START_ELEMENT);
			assertTrue(decoder.decodeStartElement().getQName().equals(elxx2));

			assertTrue(decoder.next() == EventType.CHARACTERS);
			assertTrue(decoder.decodeCharacters().equals(ch2));

			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.START_ELEMENT);
			assertTrue(decoder.decodeStartElement().getQName().equals(elx2));

			assertTrue(decoder.next() == EventType.CHARACTERS);
			assertTrue(decoder.decodeCharacters().equals(ch2));

			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.START_ELEMENT);
			assertTrue(decoder.decodeStartElement().getQName().equals(elx3));

			assertTrue(decoder.next() == EventType.CHARACTERS);
			assertTrue(decoder.decodeCharacters().equals(ch3));

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
	 * <?xml version="1.0" encoding="utf-8"?> <a
	 * xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"> <b xsi:type="c"/>
	 * <b xsi:type="d"/> </a>
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

		QName atXsiType = new QName(
				"http://www.w3.org/2001/XMLSchema-instance", "type");

		// encoder
		{
			EXIBodyEncoder encoder = factory.createEXIBodyEncoder();
			encoder.setOutputStream(baos);

			String pfx = null; // unset according fidelity-options

			encoder.encodeStartDocument();
			encoder.encodeStartElement(a.getNamespaceURI(), a.getLocalPart(),
					pfx);

			encoder.encodeStartElement(b.getNamespaceURI(), b.getLocalPart(),
					pfx);
			encoder.encodeAttributeXsiType(new StringValue("c"), "");
			// encoder.encodeAttribute(atXsiType.getNamespaceURI(),
			// atXsiType.getLocalPart(),pfx, "c");
			encoder.encodeEndElement();

			encoder.encodeStartElement(b.getNamespaceURI(), b.getLocalPart(),
					pfx);
			encoder.encodeAttributeXsiType(new StringValue("d"), "");
			// encoder.encodeAttribute(atXsiType.getNamespaceURI(),
			// atXsiType.getLocalPart(),pfx, "d");
			encoder.encodeEndElement();

			encoder.encodeEndElement(); // a

			encoder.encodeEndDocument();
			encoder.flush();
		}

		baos.flush();

		// decoder
		{
			EXIBodyDecoder decoder = factory.createEXIBodyDecoder();
			decoder.setInputStream(new ByteArrayInputStream(baos.toByteArray()));

			assertTrue(decoder.next() == EventType.START_DOCUMENT);
			decoder.decodeStartDocument();

			assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC);
			// assertTrue(decoder.decodeStartElementGeneric().equals(a));
			assertTrue(decoder.decodeStartElement().getQName().equals(a));

			/*
			 * first b
			 */
			assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC_UNDECLARED);
			// assertTrue(decoder.decodeStartElementGenericUndeclared().equals(b));
			assertTrue(decoder.decodeStartElement().getQName().equals(b));

			assertTrue(decoder.next() == EventType.ATTRIBUTE_GENERIC_UNDECLARED);
			// assertTrue(decoder.decodeAttributeGenericUndeclared().equals(atXsiType));
			assertTrue(decoder.decodeAttribute().getQName().equals(atXsiType));

			assertTrue(decoder.next() == EventType.END_ELEMENT_UNDECLARED);
			// decoder.decodeEndElementUndeclared();
			decoder.decodeEndElement();

			/*
			 * second b
			 */
			assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC_UNDECLARED);
			// assertTrue(decoder.decodeStartElementGenericUndeclared().equals(b));
			assertTrue(decoder.decodeStartElement().getQName().equals(b));

			assertTrue(decoder.next() == EventType.ATTRIBUTE);
			QName at = decoder.decodeAttribute().getQName();
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

	/*
	 * Bug-ID: ID: 3310940
	 * 
	 * Kenji: EE for <b/> is not correct.
	 * 
	 * <a> <b>t</b> <b/> </a>
	 */
	public void testSchemaLess5() throws IOException, SAXException,
			EXIException {
		EXIFactory factory = DefaultEXIFactory.newInstance();

		factory.setFidelityOptions(FidelityOptions.createDefault());
		factory.setCodingMode(CodingMode.BYTE_PACKED);
		// factory.setGrammar ( GrammarFactory.getSchemaLessGrammar ( ) );

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		QName a = new QName("", "root");
		QName b = new QName("", "b");

		// encoder
		{
			EXIBodyEncoder encoder = factory.createEXIBodyEncoder();
			encoder.setOutputStream(baos);

			encoder.encodeStartDocument();
			encoder.encodeStartElement(a);

			encoder.encodeStartElement(b);
			encoder.encodeCharacters(new StringValue("t"));
			encoder.encodeEndElement();

			encoder.encodeStartElement(b);
			encoder.encodeEndElement();

			encoder.encodeEndElement(); // a

			encoder.encodeEndDocument();
			encoder.flush();
		}

		baos.flush();
		byte[] bytes = baos.toByteArray();

		// for(int i=0; i<bytes.length; i++) {
		// byte bb = bytes[i];
		// // System.out.print(Integer.toHexString(bb) + " ");
		// System.out.print(bb + " ");
		// }
		// System.out.println();

		// decoder
		{
			EXIBodyDecoder decoder = factory.createEXIBodyDecoder();
			decoder.setInputStream(new ByteArrayInputStream(bytes));

			assertTrue(decoder.next() == EventType.START_DOCUMENT);
			decoder.decodeStartDocument();

			assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC);
			// assertTrue(decoder.decodeStartElementGeneric().equals(a));
			assertTrue(decoder.decodeStartElement().getQName().equals(a));

			/*
			 * first b
			 */
			assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC_UNDECLARED);
			// assertTrue(decoder.decodeStartElementGenericUndeclared().equals(b));
			assertTrue(decoder.decodeStartElement().getQName().equals(b));

			assertTrue(decoder.next() == EventType.CHARACTERS_GENERIC_UNDECLARED);
			// assertTrue(decoder.decodeCharactersGenericUndeclared().toString().equals("t"));
			assertTrue(decoder.decodeCharacters().toString().equals("t"));

			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();

			/*
			 * second b
			 */
			assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC_UNDECLARED);
			// assertTrue(decoder.decodeStartElementGenericUndeclared().equals(b));
			assertTrue(decoder.decodeStartElement().getQName().equals(b));

			// empty char event
			EventType et = decoder.next();
			if (et == EventType.CHARACTERS) {
				assertTrue(decoder.decodeCharacters().toString().equals(""));
				assertTrue(decoder.next() == EventType.END_ELEMENT);
				decoder.decodeEndElement();
			} else {
				assertTrue(et == EventType.END_ELEMENT_UNDECLARED);
				// decoder.decodeEndElementUndeclared();
				decoder.decodeEndElement();
			}

			// end root a
			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.END_DOCUMENT);
			decoder.decodeEndDocument();
		}
	}

	/*
	 * Duplicate entries <foo foo="foo at value"> </foo>
	 */
	public void testSchemaLess6DuplicateEntries1() throws IOException,
			SAXException, EXIException {

		EXIFactory factory = DefaultEXIFactory.newInstance();

		factory.setFidelityOptions(FidelityOptions.createDefault());
		factory.setCodingMode(CodingMode.BYTE_PACKED);
		// factory.setProfile(EXIFactory.UCD_PROFILE);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		QName bla = new QName("", "foo");
		String sValue = "foo at value";

		// encoder
		{
			EXIBodyEncoder encoder = factory.createEXIBodyEncoder();
			encoder.setOutputStream(baos);

			encoder.encodeStartDocument();
			encoder.encodeStartElement(bla);
			encoder.encodeAttribute(bla, new StringValue(sValue));
			encoder.encodeEndElement();

			encoder.encodeEndDocument();
			encoder.flush();
		}

		baos.flush();
		byte[] bytes = baos.toByteArray();

		File f = File.createTempFile("exi-profile", "_1");
		FileOutputStream fos = new FileOutputStream(f);
		fos.write(128);
		fos.write(bytes);
		fos.close();
		// System.out.println(f);

		// for(int i=0; i<bytes.length; i++) {
		// byte bb = bytes[i];
		// // System.out.print(Integer.toHexString(bb) + " ");
		// System.out.print(bb + " ");
		// }
		// System.out.println();

		// decoder
		{
			EXIBodyDecoder decoder = factory.createEXIBodyDecoder();
			decoder.setInputStream(new ByteArrayInputStream(bytes));

			assertTrue(decoder.next() == EventType.START_DOCUMENT);
			decoder.decodeStartDocument();

			assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC);
			assertTrue(decoder.decodeStartElement().getQName().equals(bla));

			assertTrue(decoder.next() == EventType.ATTRIBUTE_GENERIC_UNDECLARED);
			assertTrue(decoder.decodeAttribute().getQName().equals(bla));
			decoder.getAttributeValue().toString().equals(sValue);

			// end root bla
			assertTrue(decoder.next() == EventType.END_ELEMENT_UNDECLARED);
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.END_DOCUMENT);
			decoder.decodeEndDocument();
		}
	}

	/*
	 * Duplicate entries <bla:foo xmlns:bla="uri:bla"
	 * bla:foo="bla:foo at value"> </bla:foo>
	 */
	public void testSchemaLess6DuplicateEntries2() throws IOException,
			SAXException, EXIException {

		EXIFactory factory = DefaultEXIFactory.newInstance();

		factory.setFidelityOptions(FidelityOptions.createDefault());
		factory.setCodingMode(CodingMode.BYTE_PACKED);
		// factory.setProfile(EXIFactory.UCD_PROFILE);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		QName bla = new QName("uri:bla", "foo");
		String sValue = "bla:foo at value";

		// encoder
		{
			EXIBodyEncoder encoder = factory.createEXIBodyEncoder();
			encoder.setOutputStream(baos);

			encoder.encodeStartDocument();
			encoder.encodeStartElement(bla);
			encoder.encodeAttribute(bla, new StringValue(sValue));
			encoder.encodeEndElement();

			encoder.encodeEndDocument();
			encoder.flush();
		}

		baos.flush();
		byte[] bytes = baos.toByteArray();

		File f = File.createTempFile("exi-profile", "_2");
		FileOutputStream fos = new FileOutputStream(f);
		fos.write(128);
		fos.write(bytes);
		fos.close();
		// System.out.println(f);

		// for(int i=0; i<bytes.length; i++) {
		// byte bb = bytes[i];
		// // System.out.print(Integer.toHexString(bb) + " ");
		// System.out.print(bb + " ");
		// }
		// System.out.println();

		// decoder
		{
			EXIBodyDecoder decoder = factory.createEXIBodyDecoder();
			decoder.setInputStream(new ByteArrayInputStream(bytes));

			assertTrue(decoder.next() == EventType.START_DOCUMENT);
			decoder.decodeStartDocument();

			assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC);
			assertTrue(decoder.decodeStartElement().getQName().equals(bla));

			assertTrue(decoder.next() == EventType.ATTRIBUTE_GENERIC_UNDECLARED);
			assertTrue(decoder.decodeAttribute().getQName().equals(bla));
			decoder.getAttributeValue().toString().equals(sValue);

			// end root bla
			assertTrue(decoder.next() == EventType.END_ELEMENT_UNDECLARED);
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.END_DOCUMENT);
			decoder.decodeEndDocument();
		}
	}

}