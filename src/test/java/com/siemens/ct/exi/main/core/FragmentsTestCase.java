/*
 * Copyright (c) 2007-2018 Siemens AG
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

package com.siemens.ct.exi.main.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.xml.namespace.QName;

import junit.framework.TestCase;

import org.junit.Test;
import org.xml.sax.SAXException;

import com.siemens.ct.exi.core.CodingMode;
import com.siemens.ct.exi.core.EXIBodyDecoder;
import com.siemens.ct.exi.core.EXIBodyEncoder;
import com.siemens.ct.exi.core.EXIFactory;
import com.siemens.ct.exi.core.FidelityOptions;
import com.siemens.ct.exi.grammars.GrammarFactory;
import com.siemens.ct.exi.core.exceptions.EXIException;
import com.siemens.ct.exi.core.grammars.event.EventType;
import com.siemens.ct.exi.core.helpers.DefaultEXIFactory;
import com.siemens.ct.exi.core.values.StringValue;
import com.siemens.ct.exi.core.values.Value;

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
		factory.setGrammars(grammarFactory.createSchemaLessGrammars());
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
			decoder.setInputStream(new ByteArrayInputStream(baos.toByteArray()));

			assertTrue(decoder.next() == EventType.START_DOCUMENT);
			decoder.decodeStartDocument();

			assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC);
			// assertTrue(decoder.decodeStartElementGeneric().equals(s1));
			assertTrue(decoder.decodeStartElement().getQName().equals(s1));

			assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC_UNDECLARED);
			// assertTrue(decoder.decodeStartElementGenericUndeclared().equals(s2));
			assertTrue(decoder.decodeStartElement().getQName().equals(s2));

			assertTrue(decoder.next() == EventType.ATTRIBUTE_GENERIC_UNDECLARED);
			// assertTrue(decoder.decodeAttributeGenericUndeclared().equals(at1));
			assertTrue(decoder.decodeAttribute().getQName().equals(at1));
			assertTrue(decoder.getAttributeValue().equals(atCh1));

			assertTrue(decoder.next() == EventType.END_ELEMENT_UNDECLARED);
			// decoder.decodeEndElementUndeclared();
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.START_ELEMENT);
			assertTrue(decoder.decodeStartElement().getQName().equals(s1));

			assertTrue(decoder.next() == EventType.START_ELEMENT);
			assertTrue(decoder.decodeStartElement().getQName().equals(s2));

			assertTrue(decoder.next() == EventType.ATTRIBUTE);
			assertTrue(decoder.decodeAttribute().getQName().equals(at1));
			assertTrue(decoder.getAttributeValue().equals(atCh2));

			assertTrue(decoder.next() == EventType.ATTRIBUTE_GENERIC_UNDECLARED);
			// assertTrue(decoder.decodeAttributeGenericUndeclared().equals(at2));
			assertTrue(decoder.decodeAttribute().getQName().equals(at2));
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
