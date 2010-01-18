/*
 * Copyright (C) 2007-2010 Siemens AG
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
import com.siemens.ct.exi.EXIDecoder;
import com.siemens.ct.exi.EXIEncoder;
import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.FidelityOptions;
import com.siemens.ct.exi.GrammarFactory;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.grammar.event.EventType;
import com.siemens.ct.exi.helpers.DefaultEXIFactory;

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

		String atCh1 = "dasdas";
		String atCh2 = "312312";
		String atCh3 = "002k";

		// encoder
		{
			EXIEncoder encoder = factory.createEXIEncoder();
			encoder.setOutput(baos, factory.isEXIBodyOnly());
			
			String pfx = null;	//	unset according fidelity-options

			encoder.encodeStartDocument();

			encoder.encodeStartElement(s1.getNamespaceURI(), s1.getLocalPart(), pfx);
			encoder.encodeStartElement(s2.getNamespaceURI(), s2.getLocalPart(), pfx);
			encoder.encodeAttribute(at1.getNamespaceURI(), at1.getLocalPart(), pfx,
					atCh1);
			encoder.encodeEndElement();
			encoder.encodeEndElement();
			encoder.encodeStartElement(s1.getNamespaceURI(), s1.getLocalPart(), pfx);
			encoder.encodeStartElement(s2.getNamespaceURI(), s2.getLocalPart(), pfx);
			encoder.encodeAttribute(at1.getNamespaceURI(), at1.getLocalPart(), pfx,
					atCh2);
			encoder.encodeAttribute(at2.getNamespaceURI(), at2.getLocalPart(), pfx,
					atCh3);
			encoder.encodeEndElement();
			encoder.encodeEndElement();

			encoder.encodeEndDocument();
		}

		// decoder
		{
			EXIDecoder decoder = factory.createEXIDecoder();
			decoder.setInputStream(
					new ByteArrayInputStream(baos.toByteArray()), factory
							.isEXIBodyOnly());

			assertTrue(decoder.next() == EventType.START_DOCUMENT);
			decoder.decodeStartDocument();

			//decoder.inspectStream();
			decoder.hasNext();
			assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC);
			decoder.decodeStartElementGeneric();
			assertTrue(decoder.getElementURI().equals(s1.getNamespaceURI()));
			assertTrue(decoder.getElementLocalName().equals(s1.getLocalPart()));

			// decoder.inspectStream();
			decoder.hasNext();
			assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC_UNDECLARED);
			decoder.decodeStartElementGenericUndeclared();
			assertTrue(decoder.getElementURI().equals(s2.getNamespaceURI()));
			assertTrue(decoder.getElementLocalName().equals(s2.getLocalPart()));

			// decoder.inspectStream();
			decoder.hasNext();
			assertTrue(decoder.next() == EventType.ATTRIBUTE_GENERIC_UNDECLARED);
			decoder.decodeAttributeGenericUndeclared();
			assertTrue(decoder.getAttributeURI().equals(at1.getNamespaceURI()));
			assertTrue(decoder.getAttributeLocalName().equals(
					at1.getLocalPart()));
			assertTrue(decoder.getAttributeValue().equals(atCh1));

			// decoder.inspectStream();
			decoder.hasNext();
			assertTrue(decoder.next() == EventType.END_ELEMENT_UNDECLARED);
			decoder.decodeEndElementUndeclared();

			// decoder.inspectStream();
			decoder.hasNext();
			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();

			// decoder.inspectStream();
			decoder.hasNext();
			assertTrue(decoder.next() == EventType.START_ELEMENT);
			decoder.decodeStartElement();
			assertTrue(decoder.getElementURI().equals(s1.getNamespaceURI()));
			assertTrue(decoder.getElementLocalName().equals(s1.getLocalPart()));

			// decoder.inspectStream();
			decoder.hasNext();
			assertTrue(decoder.next() == EventType.START_ELEMENT);
			decoder.decodeStartElement();
			assertTrue(decoder.getElementURI().equals(s2.getNamespaceURI()));
			assertTrue(decoder.getElementLocalName().equals(s2.getLocalPart()));

			// decoder.inspectStream();
			decoder.hasNext();
			assertTrue(decoder.next() == EventType.ATTRIBUTE);
			decoder.decodeAttribute();
			assertTrue(decoder.getAttributeURI().equals(at1.getNamespaceURI()));
			assertTrue(decoder.getAttributeLocalName().equals(
					at1.getLocalPart()));
			assertTrue(decoder.getAttributeValue().equals(atCh2));

			// decoder.inspectStream();
			decoder.hasNext();
			assertTrue(decoder.next() == EventType.ATTRIBUTE_GENERIC_UNDECLARED);
			decoder.decodeAttributeGenericUndeclared();
			assertTrue(decoder.getAttributeURI().equals(at2.getNamespaceURI()));
			assertTrue(decoder.getAttributeLocalName().equals(
					at2.getLocalPart()));
			assertTrue(decoder.getAttributeValue().equals(atCh3));

			// decoder.inspectStream();
			decoder.hasNext();
			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();

			// decoder.inspectStream();
			decoder.hasNext();
			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();

			// decoder.inspectStream();
			decoder.hasNext();
			assertTrue(decoder.next() == EventType.END_DOCUMENT);
			decoder.decodeEndDocument();
		}
	}

}
