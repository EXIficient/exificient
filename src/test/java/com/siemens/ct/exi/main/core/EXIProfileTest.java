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

import javax.xml.namespace.QName;

import junit.framework.TestCase;

import com.siemens.ct.exi.core.CodingMode;
import com.siemens.ct.exi.core.Constants;
import com.siemens.ct.exi.core.EXIBodyDecoder;
import com.siemens.ct.exi.core.EXIBodyEncoder;
import com.siemens.ct.exi.core.EXIFactory;
import com.siemens.ct.exi.core.FidelityOptions;
import com.siemens.ct.exi.grammars.GrammarFactory;
import com.siemens.ct.exi.core.grammars.Grammars;
import com.siemens.ct.exi.core.grammars.event.EventType;
import com.siemens.ct.exi.core.helpers.DefaultEXIFactory;
import com.siemens.ct.exi.core.values.QNameValue;
import com.siemens.ct.exi.core.values.StringValue;

public class EXIProfileTest extends TestCase {

	public EXIProfileTest(String testName) {
		super(testName);
	}

	public void testP1_a() throws Exception {
		GrammarFactory gf = GrammarFactory.newInstance();
		Grammars g = gf.createXSDTypesOnlyGrammars();

		EXIFactory factory = DefaultEXIFactory.newInstance();

		factory.setFidelityOptions(FidelityOptions.createStrict());
		factory.setCodingMode(CodingMode.BIT_PACKED);
		factory.setGrammars(g);

		// factory.setMaximumNumberOfBuiltInElementGrammars(1);
		factory.setMaximumNumberOfBuiltInProductions(1);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		QName qnRoot = new QName("", "root");
		QName qnEl1 = new QName("", "el1");
		QName qnAt1 = new QName("", "at1");

		QName qnXsiType = new QName(
				Constants.XML_SCHEMA_INSTANCE_NS_URI, "type");

		String sAtValue = "atValue";

		// encoder
		{
			EXIBodyEncoder encoder = factory.createEXIBodyEncoder();
			encoder.setOutputStream(baos);
			String pfx = null; // unset according fidelity-options
			encoder.encodeStartDocument();
			encoder.encodeStartElement(qnRoot.getNamespaceURI(),
					qnRoot.getLocalPart(), pfx);
			{
				encoder.encodeStartElement(qnEl1.getNamespaceURI(),
						qnEl1.getLocalPart(), pfx);
				encoder.encodeAttribute(qnAt1, new StringValue(sAtValue));
				encoder.encodeEndElement();
			}
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
			assertTrue(decoder.decodeStartElement().getQName().equals(qnRoot));

			{
				assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC_UNDECLARED);
				assertTrue(decoder.decodeStartElement().getQName()
						.equals(qnEl1));

				assertTrue(decoder.next() == EventType.ATTRIBUTE_GENERIC_UNDECLARED);
				assertTrue(decoder.decodeAttribute().getQName()
						.equals(qnXsiType));

				assertTrue(decoder.next() == EventType.ATTRIBUTE_GENERIC);
				assertTrue(decoder.decodeAttribute().getQName().equals(qnAt1));
				assertTrue(decoder.getAttributeValue().toString()
						.equals(sAtValue));

				assertTrue(decoder.next() == EventType.END_ELEMENT);
				decoder.decodeEndElement();
			}

			// assertTrue(decoder.next() == EventType.CHARACTERS);
			// assertTrue(decoder.decodeCharacters().equals(""));

			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.END_DOCUMENT);
			decoder.decodeEndDocument();
		}
	}

	public void testP2_a() throws Exception {
		GrammarFactory gf = GrammarFactory.newInstance();
		Grammars g = gf.createXSDTypesOnlyGrammars();

		EXIFactory factory = DefaultEXIFactory.newInstance();

		factory.setFidelityOptions(FidelityOptions.createStrict());
		factory.setCodingMode(CodingMode.BIT_PACKED);
		factory.setGrammars(g);

		// factory.setMaximumNumberOfBuiltInElementGrammars(1);
		factory.setMaximumNumberOfBuiltInProductions(2);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		QName qnRoot = new QName("", "root");
		QName qnEl1 = new QName("", "el1");

		// encoder
		{
			EXIBodyEncoder encoder = factory.createEXIBodyEncoder();
			encoder.setOutputStream(baos);
			String pfx = null; // unset according fidelity-options
			encoder.encodeStartDocument();
			encoder.encodeStartElement(qnRoot.getNamespaceURI(),
					qnRoot.getLocalPart(), pfx);
			{
				encoder.encodeStartElement(qnEl1.getNamespaceURI(),
						qnEl1.getLocalPart(), pfx);
				encoder.encodeEndElement();
			}
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
			assertTrue(decoder.decodeStartElement().getQName().equals(qnRoot));

			{
				assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC_UNDECLARED);
				assertTrue(decoder.decodeStartElement().getQName()
						.equals(qnEl1));

				assertTrue(decoder.next() == EventType.END_ELEMENT_UNDECLARED);
				decoder.decodeEndElement();
			}

			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.END_DOCUMENT);
			decoder.decodeEndDocument();
		}
	}

	public void testP2_b() throws Exception {
		GrammarFactory gf = GrammarFactory.newInstance();
		Grammars g = gf.createXSDTypesOnlyGrammars();

		EXIFactory factory = DefaultEXIFactory.newInstance();

		factory.setFidelityOptions(FidelityOptions.createStrict());
		factory.setCodingMode(CodingMode.BIT_PACKED);
		factory.setGrammars(g);

		// factory.setMaximumNumberOfBuiltInElementGrammars(1);
		factory.setMaximumNumberOfBuiltInProductions(2);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		QName qnRoot = new QName("", "root");
		QName qnEl1 = new QName("", "el1");
		QName qnAt1 = new QName("", "at1");
		QName qnAt2 = new QName("", "at2");

		// QName qnXsiType = new
		// QName(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "type");

		String sAtValue = "atValue";

		// encoder
		{
			EXIBodyEncoder encoder = factory.createEXIBodyEncoder();
			encoder.setOutputStream(baos);
			String pfx = null; // unset according fidelity-options
			encoder.encodeStartDocument();
			encoder.encodeStartElement(qnRoot.getNamespaceURI(),
					qnRoot.getLocalPart(), pfx);
			{
				encoder.encodeStartElement(qnEl1.getNamespaceURI(),
						qnEl1.getLocalPart(), pfx);
				encoder.encodeAttribute(qnAt1, new StringValue(sAtValue));
				encoder.encodeAttribute(qnAt2, new StringValue(sAtValue));
				encoder.encodeEndElement();
			}
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
			assertTrue(decoder.decodeStartElement().getQName().equals(qnRoot));

			{
				assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC_UNDECLARED);
				assertTrue(decoder.decodeStartElement().getQName()
						.equals(qnEl1));

				// assertTrue(decoder.next() ==
				// EventType.ATTRIBUTE_GENERIC_UNDECLARED);
				// assertTrue(decoder.decodeAttribute().getQName().equals(qnXsiType));

				assertTrue(decoder.next() == EventType.ATTRIBUTE_GENERIC_UNDECLARED);
				assertTrue(decoder.decodeAttribute().getQName().equals(qnAt1));
				assertTrue(decoder.getAttributeValue().toString()
						.equals(sAtValue));

				// Profile ghost node on 2nd level
				assertTrue(decoder.next() == EventType.ATTRIBUTE_GENERIC_UNDECLARED);
				assertTrue(decoder.decodeAttribute().getQName().equals(qnAt2));
				assertTrue(decoder.getAttributeValue().toString()
						.equals(sAtValue));

				// Profile ghost node on 2nd level
				assertTrue(decoder.next() == EventType.END_ELEMENT_UNDECLARED);
				decoder.decodeEndElement();
			}

			// assertTrue(decoder.next() == EventType.CHARACTERS);
			// assertTrue(decoder.decodeCharacters().equals(""));

			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.END_DOCUMENT);
			decoder.decodeEndDocument();
		}
	}

	public void testP2_c() throws Exception {
		GrammarFactory gf = GrammarFactory.newInstance();
		Grammars g = gf.createXSDTypesOnlyGrammars();

		EXIFactory factory = DefaultEXIFactory.newInstance();

		factory.setFidelityOptions(FidelityOptions.createStrict());
		factory.setCodingMode(CodingMode.BIT_PACKED);
		factory.setGrammars(g);

		// factory.setMaximumNumberOfBuiltInElementGrammars(1);
		factory.setMaximumNumberOfBuiltInProductions(2);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		QName qnRoot = new QName("", "root");
		QName qnEl1 = new QName("", "el1");
		QName qnAt1 = new QName("", "at1");
		QName qnAt2 = new QName("", "at2");

		// QName qnXsiType = new
		// QName(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "type");

		String sAtValue = "atValue";

		// encoder
		{
			EXIBodyEncoder encoder = factory.createEXIBodyEncoder();
			encoder.setOutputStream(baos);
			String pfx = null; // unset according fidelity-options
			encoder.encodeStartDocument();
			encoder.encodeStartElement(qnRoot.getNamespaceURI(),
					qnRoot.getLocalPart(), pfx);
			{
				encoder.encodeStartElement(qnEl1.getNamespaceURI(),
						qnEl1.getLocalPart(), pfx);
				encoder.encodeAttribute(qnAt1, new StringValue(sAtValue));
				encoder.encodeAttribute(qnAt2, new StringValue(sAtValue));
				encoder.encodeEndElement();
			}
			{
				encoder.encodeStartElement(qnEl1.getNamespaceURI(),
						qnEl1.getLocalPart(), pfx);
				encoder.encodeAttribute(qnAt1, new StringValue(sAtValue));
				// Try to re-use ghost nodes
				encoder.encodeAttribute(qnAt2, new StringValue(sAtValue));
				encoder.encodeEndElement();
			}
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
			assertTrue(decoder.decodeStartElement().getQName().equals(qnRoot));

			{
				assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC_UNDECLARED);
				assertTrue(decoder.decodeStartElement().getQName()
						.equals(qnEl1));

				// assertTrue(decoder.next() ==
				// EventType.ATTRIBUTE_GENERIC_UNDECLARED);
				// assertTrue(decoder.decodeAttribute().getQName().equals(qnXsiType));

				assertTrue(decoder.next() == EventType.ATTRIBUTE_GENERIC_UNDECLARED);
				assertTrue(decoder.decodeAttribute().getQName().equals(qnAt1));
				assertTrue(decoder.getAttributeValue().toString()
						.equals(sAtValue));

				// Profile ghost node on 2nd level
				assertTrue(decoder.next() == EventType.ATTRIBUTE_GENERIC_UNDECLARED);
				assertTrue(decoder.decodeAttribute().getQName().equals(qnAt2));
				assertTrue(decoder.getAttributeValue().toString()
						.equals(sAtValue));

				// Profile ghost node on 2nd level
				assertTrue(decoder.next() == EventType.END_ELEMENT_UNDECLARED);
				decoder.decodeEndElement();
			}

			{
				assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC_UNDECLARED);
				assertTrue(decoder.decodeStartElement().getQName()
						.equals(qnEl1));

				// re-use learned attribute
				assertTrue(decoder.next() == EventType.ATTRIBUTE);
				assertTrue(decoder.decodeAttribute().getQName().equals(qnAt1));
				assertTrue(decoder.getAttributeValue().toString()
						.equals(sAtValue));

				// AGAIN Profile ghost node on 2nd level
				assertTrue(decoder.next() == EventType.ATTRIBUTE_GENERIC_UNDECLARED);
				assertTrue(decoder.decodeAttribute().getQName().equals(qnAt2));
				assertTrue(decoder.getAttributeValue().toString()
						.equals(sAtValue));

				// AGAIN Profile ghost node on 2nd level
				assertTrue(decoder.next() == EventType.END_ELEMENT_UNDECLARED);
				decoder.decodeEndElement();
			}

			// assertTrue(decoder.next() == EventType.CHARACTERS);
			// assertTrue(decoder.decodeCharacters().equals(""));

			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.END_DOCUMENT);
			decoder.decodeEndDocument();
		}
	}

	public void testG1_a() throws Exception {
		GrammarFactory gf = GrammarFactory.newInstance();
		Grammars g = gf.createXSDTypesOnlyGrammars();

		EXIFactory factory = DefaultEXIFactory.newInstance();

		factory.setFidelityOptions(FidelityOptions.createStrict());
		factory.setCodingMode(CodingMode.BIT_PACKED);
		factory.setGrammars(g);

		factory.setMaximumNumberOfBuiltInElementGrammars(1);
		// factory.setMaximumNumberOfBuiltInProductions(2);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		QName qnRoot = new QName("", "root");
		QName qnEl1 = new QName("", "el1");
		QName qnAt1 = new QName("", "at1");
		QName qnAt2 = new QName("", "at2");

		QName qnXsiType = new QName(
				Constants.XML_SCHEMA_INSTANCE_NS_URI, "type");

		String sAtValue = "atValue";

		// encoder
		{
			EXIBodyEncoder encoder = factory.createEXIBodyEncoder();
			encoder.setOutputStream(baos);
			String pfx = null; // unset according fidelity-options
			encoder.encodeStartDocument();
			encoder.encodeStartElement(qnRoot.getNamespaceURI(),
					qnRoot.getLocalPart(), pfx);
			{
				encoder.encodeStartElement(qnEl1.getNamespaceURI(),
						qnEl1.getLocalPart(), pfx);
				encoder.encodeAttribute(qnAt1, new StringValue(sAtValue));
				encoder.encodeAttribute(qnAt2, new StringValue(sAtValue));
				encoder.encodeEndElement();
			}
			{
				encoder.encodeStartElement(qnEl1.getNamespaceURI(),
						qnEl1.getLocalPart(), pfx);
				encoder.encodeAttribute(qnAt1, new StringValue(sAtValue));
				// Try to re-use ghost nodes
				encoder.encodeAttribute(qnAt2, new StringValue(sAtValue));
				encoder.encodeEndElement();
			}
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
			assertTrue(decoder.decodeStartElement().getQName().equals(qnRoot));

			{
				assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC_UNDECLARED);
				assertTrue(decoder.decodeStartElement().getQName()
						.equals(qnEl1));

				// no more grammar, hence type-cast
				assertTrue(decoder.next() == EventType.ATTRIBUTE_GENERIC_UNDECLARED);
				assertTrue(decoder.decodeAttribute().getQName()
						.equals(qnXsiType));

				assertTrue(decoder.next() == EventType.ATTRIBUTE_GENERIC);
				assertTrue(decoder.decodeAttribute().getQName().equals(qnAt1));
				assertTrue(decoder.getAttributeValue().toString()
						.equals(sAtValue));

				assertTrue(decoder.next() == EventType.ATTRIBUTE_GENERIC);
				assertTrue(decoder.decodeAttribute().getQName().equals(qnAt2));
				assertTrue(decoder.getAttributeValue().toString()
						.equals(sAtValue));

				assertTrue(decoder.next() == EventType.END_ELEMENT);
				decoder.decodeEndElement();
			}

			{
				assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC_UNDECLARED);
				assertTrue(decoder.decodeStartElement().getQName()
						.equals(qnEl1));

				// no more grammar, type-cast for previous element
				// no more grammar, hence type-cast
				assertTrue(decoder.next() == EventType.ATTRIBUTE_GENERIC_UNDECLARED);
				assertTrue(decoder.decodeAttribute().getQName()
						.equals(qnXsiType));

				assertTrue(decoder.next() == EventType.ATTRIBUTE_GENERIC);
				assertTrue(decoder.decodeAttribute().getQName().equals(qnAt1));
				assertTrue(decoder.getAttributeValue().toString()
						.equals(sAtValue));

				assertTrue(decoder.next() == EventType.ATTRIBUTE_GENERIC);
				assertTrue(decoder.decodeAttribute().getQName().equals(qnAt2));
				assertTrue(decoder.getAttributeValue().toString()
						.equals(sAtValue));

				assertTrue(decoder.next() == EventType.END_ELEMENT);
				decoder.decodeEndElement();
			}

			// assertTrue(decoder.next() == EventType.CHARACTERS);
			// assertTrue(decoder.decodeCharacters().equals(""));

			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.END_DOCUMENT);
			decoder.decodeEndDocument();
		}
	}

	public void testG1_b() throws Exception {
		GrammarFactory gf = GrammarFactory.newInstance();
		Grammars g = gf.createXSDTypesOnlyGrammars();

		EXIFactory factory = DefaultEXIFactory.newInstance();

		factory.setFidelityOptions(FidelityOptions.createStrict());
		factory.setCodingMode(CodingMode.BIT_PACKED);
		factory.setGrammars(g);

		factory.setMaximumNumberOfBuiltInElementGrammars(1);
		// factory.setMaximumNumberOfBuiltInProductions(2);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		QName qnRoot = new QName("", "root");
		QName qnEl1 = new QName("", "el1");
		QName qnEl2 = new QName("", "el2");

		QName qnXsiType = new QName(
				Constants.XML_SCHEMA_INSTANCE_NS_URI, "type");

		// String sAtValue = "atValue";
		String sChValue = "chValue";
		String sChValueTyped = "123";

		// encoder
		{
			EXIBodyEncoder encoder = factory.createEXIBodyEncoder();
			encoder.setOutputStream(baos);
			String pfx = null; // unset according fidelity-options
			encoder.encodeStartDocument();
			encoder.encodeStartElement(qnRoot.getNamespaceURI(),
					qnRoot.getLocalPart(), pfx);
			{
				encoder.encodeStartElement(qnEl1.getNamespaceURI(),
						qnEl1.getLocalPart(), pfx);
				encoder.encodeCharacters(new StringValue(sChValue));
				encoder.encodeEndElement();
			}
			{
				encoder.encodeStartElement(qnEl2.getNamespaceURI(),
						qnEl2.getLocalPart(), pfx);
				QNameValue qnv = new QNameValue(
						Constants.XML_SCHEMA_NS_URI, "int",
						qnXsiType.getPrefix());
				encoder.encodeAttributeXsiType(qnv, null);
				encoder.encodeCharacters(new StringValue(sChValueTyped));
				encoder.encodeEndElement();
			}
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
			assertTrue(decoder.decodeStartElement().getQName().equals(qnRoot));

			{
				assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC_UNDECLARED);
				assertTrue(decoder.decodeStartElement().getQName()
						.equals(qnEl1));

				// no more grammar, hence type-cast
				assertTrue(decoder.next() == EventType.ATTRIBUTE_GENERIC_UNDECLARED);
				assertTrue(decoder.decodeAttribute().getQName()
						.equals(qnXsiType));

				assertTrue(decoder.next() == EventType.CHARACTERS_GENERIC);
				assertTrue(decoder.decodeCharacters().toString()
						.equals(sChValue));

				assertTrue(decoder.next() == EventType.END_ELEMENT);
				decoder.decodeEndElement();
			}

			{
				assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC_UNDECLARED);
				assertTrue(decoder.decodeStartElement().getQName()
						.equals(qnEl2));

				// no more grammar, type-cast present in stream --> no need for
				// extra type-cast
				assertTrue(decoder.next() == EventType.ATTRIBUTE_GENERIC_UNDECLARED);
				assertTrue(decoder.decodeAttribute().getQName()
						.equals(qnXsiType));
				assertTrue(decoder.getAttributeValue() instanceof QNameValue);
				QNameValue tcVal = (QNameValue) decoder.getAttributeValue();
				assertTrue(tcVal.getNamespaceUri().equals(
						Constants.XML_SCHEMA_NS_URI));
				assertTrue(tcVal.getLocalName().equals("int"));

				assertTrue(decoder.next() == EventType.CHARACTERS);
				assertTrue(decoder.decodeCharacters().toString()
						.equals(sChValueTyped));

				assertTrue(decoder.next() == EventType.END_ELEMENT);
				decoder.decodeEndElement();
			}

			// assertTrue(decoder.next() == EventType.CHARACTERS);
			// assertTrue(decoder.decodeCharacters().equals(""));

			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.END_DOCUMENT);
			decoder.decodeEndDocument();
		}
	}

	public void testG2_P2_a() throws Exception {
		GrammarFactory gf = GrammarFactory.newInstance();
		Grammars g = gf.createXSDTypesOnlyGrammars();

		EXIFactory factory = DefaultEXIFactory.newInstance();

		factory.setFidelityOptions(FidelityOptions.createStrict());
		factory.setCodingMode(CodingMode.BIT_PACKED);
		factory.setGrammars(g);

		factory.setMaximumNumberOfBuiltInElementGrammars(2);
		factory.setMaximumNumberOfBuiltInProductions(2);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		QName qnRoot = new QName("", "root");
		QName qnEl1 = new QName("", "el1");
		QName qnEl2 = new QName("", "el2");
		QName qnAt1 = new QName("", "at1");
		QName qnAt2 = new QName("", "at2");
		QName qnAt3 = new QName("", "at3");

		QName qnXsiType = new QName(
				Constants.XML_SCHEMA_INSTANCE_NS_URI, "type");

		String sAtValue1 = "atValue1";
		String sAtValue2 = "atValue2";

		// encoder
		{
			EXIBodyEncoder encoder = factory.createEXIBodyEncoder();
			encoder.setOutputStream(baos);
			String pfx = null; // unset according fidelity-options
			encoder.encodeStartDocument();
			encoder.encodeStartElement(qnRoot.getNamespaceURI(),
					qnRoot.getLocalPart(), pfx);
			{
				encoder.encodeStartElement(qnEl1.getNamespaceURI(),
						qnEl1.getLocalPart(), pfx);
				encoder.encodeAttribute(qnAt1, new StringValue(sAtValue1));
				encoder.encodeAttribute(qnAt2, new StringValue(sAtValue1));
				encoder.encodeEndElement();
			}
			{
				encoder.encodeStartElement(qnEl1.getNamespaceURI(),
						qnEl1.getLocalPart(), pfx);
				encoder.encodeAttribute(qnAt1, new StringValue(sAtValue1));
				encoder.encodeAttribute(qnAt2, new StringValue(sAtValue1));
				encoder.encodeAttribute(qnAt3, new StringValue(sAtValue1));
				encoder.encodeEndElement();
			}
			{
				encoder.encodeStartElement(qnEl1.getNamespaceURI(),
						qnEl1.getLocalPart(), pfx);
				encoder.encodeAttribute(qnAt1, new StringValue(sAtValue1));
				encoder.encodeAttribute(qnAt2, new StringValue(sAtValue1));
				encoder.encodeEndElement();
			}
			{
				encoder.encodeStartElement(qnEl2.getNamespaceURI(),
						qnEl2.getLocalPart(), pfx);
				encoder.encodeAttribute(qnAt1, new StringValue(sAtValue2));
				encoder.encodeEndElement();
			}
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
			assertTrue(decoder.decodeStartElement().getQName().equals(qnRoot));

			{
				assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC_UNDECLARED);
				assertTrue(decoder.decodeStartElement().getQName()
						.equals(qnEl1));

				// // no more grammar, hence type-cast
				// assertTrue(decoder.next() ==
				// EventType.ATTRIBUTE_GENERIC_UNDECLARED);
				// assertTrue(decoder.decodeAttribute().getQName().equals(qnXsiType));

				assertTrue(decoder.next() == EventType.ATTRIBUTE_GENERIC_UNDECLARED);
				assertTrue(decoder.decodeAttribute().getQName().equals(qnAt1));
				assertTrue(decoder.getAttributeValue().toString()
						.equals(sAtValue1));

				// Profile ghost node on 2nd level
				assertTrue(decoder.next() == EventType.ATTRIBUTE_GENERIC_UNDECLARED);
				assertTrue(decoder.decodeAttribute().getQName().equals(qnAt2));
				assertTrue(decoder.getAttributeValue().toString()
						.equals(sAtValue1));

				// Profile ghost node on 2nd level
				assertTrue(decoder.next() == EventType.END_ELEMENT_UNDECLARED);
				decoder.decodeEndElement();
			}

			{
				assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC_UNDECLARED);
				assertTrue(decoder.decodeStartElement().getQName()
						.equals(qnEl1));

				// // no more grammar, type-cast for previous element
				// // no more grammar, hence type-cast
				// assertTrue(decoder.next() ==
				// EventType.ATTRIBUTE_GENERIC_UNDECLARED);
				// assertTrue(decoder.decodeAttribute().getQName().equals(qnXsiType));

				assertTrue(decoder.next() == EventType.ATTRIBUTE);
				assertTrue(decoder.decodeAttribute().getQName().equals(qnAt1));
				assertTrue(decoder.getAttributeValue().toString()
						.equals(sAtValue1));

				assertTrue(decoder.next() == EventType.ATTRIBUTE_GENERIC_UNDECLARED);
				// assertTrue(decoder.next() == EventType.ATTRIBUTE);
				assertTrue(decoder.decodeAttribute().getQName().equals(qnAt2));
				assertTrue(decoder.getAttributeValue().toString()
						.equals(sAtValue1));

				assertTrue(decoder.next() == EventType.ATTRIBUTE_GENERIC_UNDECLARED);
				// assertTrue(decoder.next() == EventType.ATTRIBUTE);
				assertTrue(decoder.decodeAttribute().getQName().equals(qnAt3));
				assertTrue(decoder.getAttributeValue().toString()
						.equals(sAtValue1));

				assertTrue(decoder.next() == EventType.END_ELEMENT_UNDECLARED);
				decoder.decodeEndElement();
			}

			{
				assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC_UNDECLARED);
				assertTrue(decoder.decodeStartElement().getQName()
						.equals(qnEl1));

				// // no more grammar, hence type-cast
				// assertTrue(decoder.next() ==
				// EventType.ATTRIBUTE_GENERIC_UNDECLARED);
				// assertTrue(decoder.decodeAttribute().getQName().equals(qnXsiType));

				assertTrue(decoder.next() == EventType.ATTRIBUTE);
				assertTrue(decoder.decodeAttribute().getQName().equals(qnAt1));
				assertTrue(decoder.getAttributeValue().toString()
						.equals(sAtValue1));

				assertTrue(decoder.next() == EventType.ATTRIBUTE_GENERIC_UNDECLARED);
				assertTrue(decoder.decodeAttribute().getQName().equals(qnAt2));
				assertTrue(decoder.getAttributeValue().toString()
						.equals(sAtValue1));

				assertTrue(decoder.next() == EventType.END_ELEMENT_UNDECLARED);
				decoder.decodeEndElement();
			}

			{
				assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC_UNDECLARED);
				assertTrue(decoder.decodeStartElement().getQName()
						.equals(qnEl2));

				// no more grammar, hence type-cast
				assertTrue(decoder.next() == EventType.ATTRIBUTE_GENERIC_UNDECLARED);
				assertTrue(decoder.decodeAttribute().getQName()
						.equals(qnXsiType));

				assertTrue(decoder.next() == EventType.ATTRIBUTE_GENERIC);
				assertTrue(decoder.decodeAttribute().getQName().equals(qnAt1));
				assertTrue(decoder.getAttributeValue().toString()
						.equals(sAtValue2));

				assertTrue(decoder.next() == EventType.END_ELEMENT);
				decoder.decodeEndElement();
			}

			// assertTrue(decoder.next() == EventType.CHARACTERS);
			// assertTrue(decoder.decodeCharacters().equals(""));

			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.END_DOCUMENT);
			decoder.decodeEndDocument();
		}
	}

	public void testG0_P0() throws Exception {
		GrammarFactory gf = GrammarFactory.newInstance();
		Grammars g = gf.createXSDTypesOnlyGrammars();

		EXIFactory factory = DefaultEXIFactory.newInstance();

		factory.setFidelityOptions(FidelityOptions.createStrict());
		factory.setCodingMode(CodingMode.BIT_PACKED);
		factory.setGrammars(g);

		factory.setMaximumNumberOfBuiltInElementGrammars(0);
		factory.setMaximumNumberOfBuiltInProductions(0);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		QName qnRoot = new QName("", "root");
		QName qnA = new QName("", "a");

		QName qnXsiType = new QName(
				Constants.XML_SCHEMA_INSTANCE_NS_URI, "type");

		QNameValue typeInt = new QNameValue(Constants.XML_SCHEMA_NS_URI,
				"int", null);
		QNameValue typeDecimal = new QNameValue(
				Constants.XML_SCHEMA_NS_URI, "decimal", null);

		String sValue1 = "12345";
		String sValue2 = "12345.67";

		// encoder
		{
			EXIBodyEncoder encoder = factory.createEXIBodyEncoder();
			encoder.setOutputStream(baos);
			String pfx = null; // unset according fidelity-options
			encoder.encodeStartDocument();
			encoder.encodeStartElement(qnRoot.getNamespaceURI(),
					qnRoot.getLocalPart(), pfx);
			{
				encoder.encodeStartElement(qnA.getNamespaceURI(),
						qnA.getLocalPart(), pfx);
				encoder.encodeAttributeXsiType(typeInt, pfx);
				encoder.encodeCharacters(new StringValue(sValue1));
				encoder.encodeEndElement();
			}
			{
				encoder.encodeStartElement(qnA.getNamespaceURI(),
						qnA.getLocalPart(), pfx);
				encoder.encodeAttributeXsiType(typeDecimal, pfx);
				encoder.encodeCharacters(new StringValue(sValue2));
				encoder.encodeEndElement();
			}
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
			assertTrue(decoder.decodeStartElement().getQName().equals(qnRoot));

			assertTrue(decoder.next() == EventType.ATTRIBUTE_GENERIC_UNDECLARED);
			assertTrue(decoder.decodeAttribute().getQName().equals(qnXsiType));
			assertTrue(decoder.getAttributeValue().toString()
					.endsWith(":anyType"));

			{
				assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC);
				assertTrue(decoder.decodeStartElement().getQName().equals(qnA));

				assertTrue(decoder.next() == EventType.ATTRIBUTE_GENERIC_UNDECLARED);
				assertTrue(decoder.decodeAttribute().getQName()
						.equals(qnXsiType));
				assertTrue(decoder.getAttributeValue().toString()
						.endsWith(":int"));

				assertTrue(decoder.next() == EventType.CHARACTERS);
				assertTrue(decoder.decodeCharacters().toString()
						.equals(sValue1));

				// Profile ghost node on 2nd level
				assertTrue(decoder.next() == EventType.END_ELEMENT);
				decoder.decodeEndElement();
			}
			{
				assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC);
				assertTrue(decoder.decodeStartElement().getQName().equals(qnA));

				assertTrue(decoder.next() == EventType.ATTRIBUTE_GENERIC_UNDECLARED);
				assertTrue(decoder.decodeAttribute().getQName()
						.equals(qnXsiType));
				assertTrue(decoder.getAttributeValue().toString()
						.endsWith(":decimal"));

				assertTrue(decoder.next() == EventType.CHARACTERS);
				assertTrue(decoder.decodeCharacters().toString()
						.equals(sValue2));

				// Profile ghost node on 2nd level
				assertTrue(decoder.next() == EventType.END_ELEMENT);
				decoder.decodeEndElement();
			}

			// assertTrue(decoder.next() == EventType.CHARACTERS);
			// assertTrue(decoder.decodeCharacters().equals(""));

			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.END_DOCUMENT);
			decoder.decodeEndDocument();
		}
	}

	// > 3.2 Grammar Learning Disabling Parameters
	// >
	// > - In particular, the AT(xsi:type) productions that would be inserted in
	// > grammars that would be instantiated after the
	// maximumNumberOfBuiltInElementGrammars
	// > threshold are not counted.
	//
	// It encodes the following XML.
	//
	// <A><B/><B/><B/></A>
	//
	// with the following settings.
	//
	// <param name="org.w3c.exi.ttf.useProfile" value="true"/>
	// <param name="org.w3c.exi.ttf.useSchemas" value="true"/>
	// <param name="org.w3c.exi.ttf.schemaLocation" value=""/>
	// <param name="org.w3c.exi.ttf.schemaDeviations" value="false"/>
	// <param name="org.w3c.exi.ttf.maxBuiltinGr" value="1"/>
	// <param name="org.w3c.exi.ttf.maxBuiltinProd" value="2"/>
	//
	// The expectation is that xsi:type productions for <B/> never gets counted
	// because by the time first <B/> appears, the limit for built-in grammars
	// (it is 1 in this case) has already been reached. This makes for a room
	// for the second <B> to be learned by A's ElementContent grammar.

	public void testG1_P2() throws Exception {
		GrammarFactory gf = GrammarFactory.newInstance();
		Grammars g = gf.createXSDTypesOnlyGrammars();

		EXIFactory factory = DefaultEXIFactory.newInstance();

		factory.setFidelityOptions(FidelityOptions.createStrict());
		factory.setCodingMode(CodingMode.BIT_PACKED);
		factory.setGrammars(g);

		factory.setMaximumNumberOfBuiltInElementGrammars(1);
		factory.setMaximumNumberOfBuiltInProductions(2);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		QName qnA = new QName("", "A");
		QName qnB = new QName("", "B");

		QName qnXsiType = new QName(
				Constants.XML_SCHEMA_INSTANCE_NS_URI, "type");

		// encoder
		{
			EXIBodyEncoder encoder = factory.createEXIBodyEncoder();
			encoder.setOutputStream(baos);
			String pfx = null; // unset according fidelity-options
			encoder.encodeStartDocument();
			encoder.encodeStartElement(qnA.getNamespaceURI(),
					qnA.getLocalPart(), pfx);
			{
				encoder.encodeStartElement(qnB.getNamespaceURI(),
						qnB.getLocalPart(), pfx);
				encoder.encodeEndElement();
			}
			{
				encoder.encodeStartElement(qnB.getNamespaceURI(),
						qnB.getLocalPart(), pfx);
				encoder.encodeEndElement();
			}
			{
				encoder.encodeStartElement(qnB.getNamespaceURI(),
						qnB.getLocalPart(), pfx);
				encoder.encodeEndElement();
			}
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
			assertTrue(decoder.decodeStartElement().getQName().equals(qnA));

			{
				assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC_UNDECLARED);
				assertTrue(decoder.decodeStartElement().getQName().equals(qnB));

				assertTrue(decoder.next() == EventType.ATTRIBUTE_GENERIC_UNDECLARED);
				assertTrue(decoder.decodeAttribute().getQName()
						.equals(qnXsiType));
				assertTrue(decoder.getAttributeValue().toString()
						.endsWith(":anyType"));

				// Profile ghost node on 2nd level
				assertTrue(decoder.next() == EventType.END_ELEMENT);
				decoder.decodeEndElement();
			}
			{
				// learn "B"
				assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC_UNDECLARED);
				assertTrue(decoder.decodeStartElement().getQName().equals(qnB));

				assertTrue(decoder.next() == EventType.ATTRIBUTE_GENERIC_UNDECLARED);
				assertTrue(decoder.decodeAttribute().getQName()
						.equals(qnXsiType));
				assertTrue(decoder.getAttributeValue().toString()
						.endsWith(":anyType"));

				// Profile ghost node on 2nd level
				assertTrue(decoder.next() == EventType.END_ELEMENT);
				decoder.decodeEndElement();
			}
			{
				// "re-use" B
				assertTrue(decoder.next() == EventType.START_ELEMENT);
				assertTrue(decoder.decodeStartElement().getQName().equals(qnB));

				assertTrue(decoder.next() == EventType.ATTRIBUTE_GENERIC_UNDECLARED);
				assertTrue(decoder.decodeAttribute().getQName()
						.equals(qnXsiType));
				assertTrue(decoder.getAttributeValue().toString()
						.endsWith(":anyType"));

				// Profile ghost node on 2nd level
				assertTrue(decoder.next() == EventType.END_ELEMENT);
				decoder.decodeEndElement();
			}

			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.END_DOCUMENT);
			decoder.decodeEndDocument();
		}
	}

}