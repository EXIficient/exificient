/*
 * Copyright (C) 2007-2014 Siemens AG
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
import java.io.InputStream;

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

public class SelfContainedTestCase extends TestCase {

	public SelfContainedTestCase(String testName) {
		super(testName);
	}

	/*
	 * <root> text <sc>text</sc> <sc>text</sc> </root>
	 */
	public void testSelfContained0() throws IOException, SAXException,
			EXIException {
		EXIFactory factory = DefaultEXIFactory.newInstance();

		factory.setFidelityOptions(FidelityOptions.createDefault());
		factory.setCodingMode(CodingMode.BIT_PACKED);
		FidelityOptions fo = factory.getFidelityOptions();
		fo.setFidelity(FidelityOptions.FEATURE_SC, true);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		QName root = new QName("", "root");
		QName sc = new QName("", "sc");
		Value s = new StringValue("text");

		QName[] scElements = new QName[1];
		scElements[0] = sc;
		factory.setSelfContainedElements(scElements);

		int offsetSC1, offsetSC2;

		// encoder
		{
			EXIBodyEncoder encoder = factory.createEXIBodyEncoder();
			encoder.setOutputStream(baos);
			String pfx = null;
			encoder.encodeStartDocument();
			encoder.encodeStartElement(root.getNamespaceURI(),
					root.getLocalPart(), pfx);
			encoder.encodeCharacters(s);
			{
				encoder.encodeStartElement(sc.getNamespaceURI(),
						sc.getLocalPart(), pfx);

				offsetSC1 = baos.toByteArray().length;
				// System.out.println("SC_1: " + offsetSC1);

				encoder.encodeCharacters(s);
				encoder.encodeEndElement();
			}
			{
				encoder.encodeStartElement(sc.getNamespaceURI(),
						sc.getLocalPart(), pfx);

				offsetSC2 = baos.toByteArray().length;
				// System.out.println("SC_2: " + offsetSC2);

				encoder.encodeCharacters(s);
				encoder.encodeEndElement();
			}
			encoder.encodeEndElement(); // root
			encoder.encodeEndDocument();
			encoder.flush();
		}

		// decoder ALL
		{
			EXIBodyDecoder decoder = factory.createEXIBodyDecoder();
			decoder.setInputStream(new ByteArrayInputStream(baos.toByteArray()));

			assertTrue(decoder.next() == EventType.START_DOCUMENT);
			decoder.decodeStartDocument();

			assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC);
			// assertTrue(decoder.decodeStartElementGeneric().equals(root));
			assertTrue(decoder.decodeStartElement().getQName().equals(root));

			assertTrue(decoder.next() == EventType.CHARACTERS_GENERIC_UNDECLARED);
			// assertTrue(s.equals(decoder.decodeCharactersGenericUndeclared().toString()));
			assertTrue(s.equals(decoder.decodeCharacters().toString()));

			assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC_UNDECLARED);
			// <sc> #1
			// decoder.decodeStartElementGenericUndeclared();
			decoder.decodeStartElement();
			{
				assertTrue(decoder.next() == EventType.SELF_CONTAINED);
				decoder.decodeStartSelfContainedFragment();

				assertTrue(decoder.next() == EventType.CHARACTERS_GENERIC_UNDECLARED);
				// assertTrue(s.equals(decoder.decodeCharactersGenericUndeclared().toString()));
				assertTrue(s.equals(decoder.decodeCharacters().toString()));

				assertTrue(decoder.next() == EventType.END_ELEMENT);
				decoder.decodeEndElement();
			}

			assertTrue(decoder.next() == EventType.START_ELEMENT);
			// <sc> #2
			decoder.decodeStartElement();
			{
				assertTrue(decoder.next() == EventType.SELF_CONTAINED);
				decoder.decodeStartSelfContainedFragment();

				assertTrue(decoder.next() == EventType.CHARACTERS_GENERIC_UNDECLARED);
				// assertTrue(s.equals(decoder.decodeCharactersGenericUndeclared().toString()));
				assertTrue(s.equals(decoder.decodeCharacters().toString()));

				assertTrue(decoder.next() == EventType.END_ELEMENT);
				decoder.decodeEndElement();
			}

			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.END_DOCUMENT);
			decoder.decodeEndDocument();
		}

		EXIFactory scEXIFactory = factory.clone();
		scEXIFactory.setFragment(true);
		// scEXIFactory.setEXIBodyOnly(true);

		int MINUS_BYTE_OFFSET = 3; // TODO why 3

		// decoder SC #1
		{
			EXIBodyDecoder decoder = scEXIFactory.createEXIBodyDecoder();
			InputStream is = new ByteArrayInputStream(baos.toByteArray());
			// is.skip(offsetSC1-MINUS_BYTE_OFFSET);
			int toSkip = offsetSC1 - MINUS_BYTE_OFFSET;
			while (toSkip != 0) {
				toSkip -= is.skip(toSkip);
			}
			decoder.setInputStream(is);

			assertTrue(decoder.next() == EventType.START_DOCUMENT);
			decoder.decodeStartDocument();

			assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC);
			// decoder.decodeStartElementGeneric();
			decoder.decodeStartElement();

			assertTrue(decoder.next() == EventType.CHARACTERS_GENERIC_UNDECLARED);
			// assertTrue(s.equals(decoder.decodeCharactersGenericUndeclared().toString()));
			assertTrue(s.equals(decoder.decodeCharacters().toString()));

			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.END_DOCUMENT);
			decoder.decodeEndDocument();
		}

		// decoder SC #2
		{
			EXIBodyDecoder decoder = scEXIFactory.createEXIBodyDecoder();
			InputStream is = new ByteArrayInputStream(baos.toByteArray());
			// is.skip(offsetSC2-MINUS_BYTE_OFFSET);
			int toSkip = offsetSC2 - MINUS_BYTE_OFFSET;
			while (toSkip != 0) {
				toSkip -= is.skip(toSkip);
			}
			decoder.setInputStream(is);

			assertTrue(decoder.next() == EventType.START_DOCUMENT);
			decoder.decodeStartDocument();

			assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC);
			// decoder.decodeStartElementGeneric();
			decoder.decodeStartElement();

			assertTrue(decoder.next() == EventType.CHARACTERS_GENERIC_UNDECLARED);
			// assertTrue(s.equals(decoder.decodeCharactersGenericUndeclared().toString()));
			assertTrue(s.equals(decoder.decodeCharacters().toString()));

			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.END_DOCUMENT);
			decoder.decodeEndDocument();
		}

	}

	/*
	 * <foo> <foo>text</foo> </foo>
	 */
	public void testSelfContained1() throws IOException, SAXException,
			EXIException {
		// String xmlAsString = "<foo><foo>text</foo></foo>";
		// XMLReader xmlReader = XMLReaderFactory.createXMLReader();

		EXIFactory factory = DefaultEXIFactory.newInstance();

		factory.setFidelityOptions(FidelityOptions.createDefault());
		factory.setCodingMode(CodingMode.BIT_PACKED);
		FidelityOptions fo = factory.getFidelityOptions();
		fo.setFidelity(FidelityOptions.FEATURE_SC, true);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		QName foo = new QName("", "foo");
		// QName foo2 = new QName("", "foo2");
		Value s = new StringValue("text");

		QName[] scElements = new QName[1];
		scElements[0] = foo;
		factory.setSelfContainedElements(scElements);

		int offsetSC1, offsetSC2;

		// encoder
		{
			EXIBodyEncoder encoder = factory.createEXIBodyEncoder();
			encoder.setOutputStream(baos);
			String pfx = null;
			encoder.encodeStartDocument();
			encoder.encodeStartElement(foo.getNamespaceURI(),
					foo.getLocalPart(), pfx);
			offsetSC1 = baos.toByteArray().length;
			{
				encoder.encodeStartElement(foo.getNamespaceURI(),
						foo.getLocalPart(), pfx);

				offsetSC2 = baos.toByteArray().length;
				// System.out.println("SC_1: " + offsetSC1);

				encoder.encodeCharacters(s);
				encoder.encodeEndElement();
			}
			encoder.encodeEndElement(); // foo
			encoder.encodeEndDocument();
			encoder.flush();
		}

		// System.out.println("offsetSC1 = " + offsetSC1);
		// System.out.println("offsetSC2 = " + offsetSC2);

		// decoder ALL
		{
			EXIBodyDecoder decoder = factory.createEXIBodyDecoder();
			decoder.setInputStream(new ByteArrayInputStream(baos.toByteArray()));

			assertTrue(decoder.next() == EventType.START_DOCUMENT);
			decoder.decodeStartDocument();

			// <sc> #1
			assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC);
			// assertTrue(decoder.decodeStartElementGeneric().equals(foo));
			assertTrue(decoder.decodeStartElement().getQName().equals(foo));

			assertTrue(decoder.next() == EventType.SELF_CONTAINED);
			decoder.decodeStartSelfContainedFragment();

			assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC_UNDECLARED);
			// <sc> #2
			// decoder.decodeStartElementGenericUndeclared();
			decoder.decodeStartElement();
			{
				assertTrue(decoder.next() == EventType.SELF_CONTAINED);
				decoder.decodeStartSelfContainedFragment();

				assertTrue(decoder.next() == EventType.CHARACTERS_GENERIC_UNDECLARED);
				// assertTrue(s.equals(decoder.decodeCharactersGenericUndeclared().toString()));
				assertTrue(s.equals(decoder.decodeCharacters().toString()));

				assertTrue(decoder.next() == EventType.END_ELEMENT);
				decoder.decodeEndElement();
			}

			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.END_DOCUMENT);
			decoder.decodeEndDocument();
		}

		EXIFactory scEXIFactory = factory.clone();
		scEXIFactory.setFragment(true);
		// scEXIFactory.setEXIBodyOnly(true);

		int MINUS_BYTE_OFFSET = 4; // TODO why 4

		// decoder SC #1
		{
			EXIBodyDecoder decoder = scEXIFactory.createEXIBodyDecoder();
			InputStream is = new ByteArrayInputStream(baos.toByteArray());
			// is.skip(offsetSC1-MINUS_BYTE_OFFSET);
			int toSkip = offsetSC1 - MINUS_BYTE_OFFSET;
			while (toSkip != 0) {
				toSkip -= is.skip(toSkip);
			}
			decoder.setInputStream(is);

			assertTrue(decoder.next() == EventType.START_DOCUMENT);
			decoder.decodeStartDocument();

			assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC);
			// decoder.decodeStartElementGeneric();
			decoder.decodeStartElement();

			{
				assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC_UNDECLARED);
				// decoder.decodeStartElementGenericUndeclared();
				decoder.decodeStartElement();

				assertTrue(decoder.next() == EventType.SELF_CONTAINED);
				decoder.decodeStartSelfContainedFragment();

				assertTrue(decoder.next() == EventType.CHARACTERS_GENERIC_UNDECLARED);
				// assertTrue(s.equals(decoder.decodeCharactersGenericUndeclared().toString()));
				assertTrue(s.equals(decoder.decodeCharacters().toString()));

				assertTrue(decoder.next() == EventType.END_ELEMENT);
				decoder.decodeEndElement();
			}

			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.END_DOCUMENT);
			decoder.decodeEndDocument();
		}

		// decoder SC #2
		{
			EXIBodyDecoder decoder = scEXIFactory.createEXIBodyDecoder();
			InputStream is = new ByteArrayInputStream(baos.toByteArray());
			// is.skip(offsetSC2-MINUS_BYTE_OFFSET);
			int toSkip = offsetSC2 - MINUS_BYTE_OFFSET;
			while (toSkip != 0) {
				toSkip -= is.skip(toSkip);
			}
			decoder.setInputStream(is);

			assertTrue(decoder.next() == EventType.START_DOCUMENT);
			decoder.decodeStartDocument();

			assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC);
			// decoder.decodeStartElementGeneric();
			decoder.decodeStartElement();

			assertTrue(decoder.next() == EventType.CHARACTERS_GENERIC_UNDECLARED);
			// assertTrue(s.equals(decoder.decodeCharactersGenericUndeclared().toString()));
			assertTrue(s.equals(decoder.decodeCharacters().toString()));

			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.END_DOCUMENT);
			decoder.decodeEndDocument();
		}

	}

}