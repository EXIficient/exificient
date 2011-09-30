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

import org.junit.Test;
import org.xml.sax.SAXException;

import com.siemens.ct.exi.CodingMode;
import com.siemens.ct.exi.EXIBodyDecoder;
import com.siemens.ct.exi.EXIBodyEncoder;
import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.FidelityOptions;
import com.siemens.ct.exi.GrammarFactory;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.grammar.event.EventType;
import com.siemens.ct.exi.helpers.DefaultEXIFactory;
import com.siemens.ct.exi.values.StringValue;
import com.siemens.ct.exi.values.Value;

public class FragmentsTestCase extends TestCase {
	protected GrammarFactory grammarFactory = GrammarFactory.newInstance();

	public FragmentsTestCase() {
		super("XML Test Cases");
	}

	@Test
	public void testFragment0() throws IOException, SAXException, EXIException {
		EXIFactory factory = DefaultEXIFactory.newInstance();

		factory.setFidelityOptions(FidelityOptions.createDefault());
		factory.setCodingMode(CodingMode.BIT_PACKED);
		factory.setGrammar(grammarFactory.createSchemaLessGrammar());
		factory.setFragment(true);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		QName s1 = new QName("", "el1");
		QName s2 = new QName("", "el2");

		QName at1 = new QName("", "at1");
		QName at2 = new QName("", "at2");

		Value atCh1 = new StringValue("dasdas");
		Value atCh2 = new StringValue("312312");
		Value atCh3 = new StringValue("002k");

		// encoder
		{
			EXIBodyEncoder encoder = factory.createEXIBodyEncoder();
			encoder.setOutputStream(baos);

			String pfx = null; // unset according fidelity-options

			encoder.encodeStartDocument();

			encoder.encodeStartElement(s1.getNamespaceURI(), s1.getLocalPart(),
					pfx);
			encoder.encodeStartElement(s2.getNamespaceURI(), s2.getLocalPart(),
					pfx);
			encoder.encodeAttribute(at1.getNamespaceURI(), at1.getLocalPart(),
					pfx, atCh1);
			encoder.encodeEndElement();
			encoder.encodeEndElement();
			encoder.encodeStartElement(s1.getNamespaceURI(), s1.getLocalPart(),
					pfx);
			encoder.encodeStartElement(s2.getNamespaceURI(), s2.getLocalPart(),
					pfx);
			encoder.encodeAttribute(at1.getNamespaceURI(), at1.getLocalPart(),
					pfx, atCh2);
			encoder.encodeAttribute(at2.getNamespaceURI(), at2.getLocalPart(),
					pfx, atCh3);
			encoder.encodeEndElement();
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
			// assertTrue(decoder.decodeStartElementGeneric().equals(s1));
			assertTrue(decoder.decodeStartElement().equals(s1));

			assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC_UNDECLARED);
			// assertTrue(decoder.decodeStartElementGenericUndeclared().equals(s2));
			assertTrue(decoder.decodeStartElement().equals(s2));

			assertTrue(decoder.next() == EventType.ATTRIBUTE_GENERIC_UNDECLARED);
			// assertTrue(decoder.decodeAttributeGenericUndeclared().equals(at1));
			assertTrue(decoder.decodeAttribute().equals(at1));
			assertTrue(decoder.getAttributeValue().equals(atCh1));

			assertTrue(decoder.next() == EventType.END_ELEMENT_UNDECLARED);
			// decoder.decodeEndElementUndeclared();
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.START_ELEMENT);
			assertTrue(decoder.decodeStartElement().equals(s1));
			
			assertTrue(decoder.next() == EventType.START_ELEMENT);
			assertTrue(decoder.decodeStartElement().equals(s2));

			assertTrue(decoder.next() == EventType.ATTRIBUTE);
			assertTrue(decoder.decodeAttribute().equals(at1));
			assertTrue(decoder.getAttributeValue().equals(atCh2));

			assertTrue(decoder.next() == EventType.ATTRIBUTE_GENERIC_UNDECLARED);
			// assertTrue(decoder.decodeAttributeGenericUndeclared().equals(at2));
			assertTrue(decoder.decodeAttribute().equals(at2));
			assertTrue(decoder.getAttributeValue().equals(atCh3));

			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.END_DOCUMENT);
			decoder.decodeEndDocument();
		}
	}

}
