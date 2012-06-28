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

package com.siemens.ct.exi.grammar;

import java.io.ByteArrayInputStream;

import javax.xml.namespace.QName;

import junit.framework.TestCase;

import org.custommonkey.xmlunit.XMLConstants;

import com.siemens.ct.exi.GrammarFactory;
import com.siemens.ct.exi.context.GrammarContext;
import com.siemens.ct.exi.context.QNameContext;
import com.siemens.ct.exi.grammar.event.Attribute;
import com.siemens.ct.exi.grammar.event.EventType;
import com.siemens.ct.exi.grammar.event.StartElement;
import com.siemens.ct.exi.grammar.rule.Rule;
import com.siemens.ct.exi.grammar.rule.SchemaLessStartTag;

public class GrammarTest extends TestCase {
	String schema;

	public static Grammar getGrammarFromSchemaAsString(String schemaAsString)
			throws Exception {
		ByteArrayInputStream bais = new ByteArrayInputStream(
				schemaAsString.getBytes());
		GrammarFactory grammarFactory = GrammarFactory.newInstance();
		Grammar grammar = grammarFactory.createGrammar(bais);

		return grammar;
	}

	public void testSequence1() throws Exception {
		schema = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ " <xs:element name='root'>" + "  <xs:complexType>"
				+ "   <xs:sequence >"
				+ "    <xs:element name='a' type='xs:string' /> "
				+ "    <xs:element name='b' type='xs:string' /> "
				+ "   </xs:sequence>" + "  </xs:complexType>"
				+ " </xs:element>" + "</xs:schema>";

		Grammar g = getGrammarFromSchemaAsString(schema);
		GrammarContext gc = g.getGrammarContext();
		// GrammarURIEntry[] gues = g.getGrammarEntries();

		// Rule root = g.getGlobalElement(new QName("", "root")).getRule();
		Rule root = gc.getGrammarUriContext("").getQNameContext("root").getGlobalStartElement().getRule();
		// Rule root =
		// g.getGlobalElement(XSDGrammarBuilder.getEfficientQName(gues, "",
		// "root")).getRule();

		// SE(a)
		assertTrue(root.getNumberOfEvents() == 1);
		EventInformation er0 = root.lookFor(0);
		assertTrue(er0.event.isEventType(EventType.START_ELEMENT));
		assertTrue(((StartElement) er0.event).getQName().getLocalPart()
				.equals("a"));

		Rule a = er0.next;
		// SE(b)
		assertTrue(a.getNumberOfEvents() == 1);
		EventInformation er1 = a.lookFor(0);
		assertTrue(er1.event.isEventType(EventType.START_ELEMENT));
		assertTrue(((StartElement) er1.event).getQName().getLocalPart()
				.equals("b"));

		Rule b = er1.next;
		// SE(b)
		assertTrue(b.getNumberOfEvents() == 1);
		EventInformation er2 = b.lookFor(0);
		assertTrue(er2.event.isEventType(EventType.END_ELEMENT));
	}

	public void testSequence1a() throws Exception {
		schema = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ " <xs:element name='root'>"
				+ "  <xs:complexType>"
				+ "   <xs:sequence >"
				+ "    <xs:element name='a' type='xs:string' minOccurs='2' maxOccurs='2' /> "
				+ "    <xs:element name='b' type='xs:string' /> "
				+ "   </xs:sequence>" + "  </xs:complexType>"
				+ " </xs:element>" + "</xs:schema>";

		Grammar g = getGrammarFromSchemaAsString(schema);
		GrammarContext gc = g.getGrammarContext();
		// GrammarURIEntry[] gues = g.getGrammarEntries();

		// Rule root = g.getGlobalElement(new QName("", "root")).getRule();
		Rule root = gc.getGrammarUriContext("").getQNameContext("root").getGlobalStartElement().getRule();
		// Rule root =
		// g.getGlobalElement(XSDGrammarBuilder.getEfficientQName(gues, "",
		// "root")).getRule();

		// SE(a)
		assertTrue(root.getNumberOfEvents() == 1);
		EventInformation er0 = root.lookFor(0);
		assertTrue(er0.event.isEventType(EventType.START_ELEMENT));
		assertTrue(((StartElement) er0.event).getQName().getLocalPart()
				.equals("a"));

		Rule a_1 = er0.next;
		// SE(a)
		assertTrue(a_1.getNumberOfEvents() == 1);
		EventInformation er1 = a_1.lookFor(0);
		assertTrue(er1.event.isEventType(EventType.START_ELEMENT));
		assertTrue(((StartElement) er1.event).getQName().getLocalPart()
				.equals("a"));

		Rule a_2 = er1.next;
		// SE(b)
		assertTrue(a_2.getNumberOfEvents() == 1);
		EventInformation er2 = a_2.lookFor(0);
		assertTrue(er2.event.isEventType(EventType.START_ELEMENT));
		assertTrue(((StartElement) er2.event).getQName().getLocalPart()
				.equals("b"));

		Rule b = er2.next;
		// SE(b)
		assertTrue(b.getNumberOfEvents() == 1);
		EventInformation er3 = b.lookFor(0);
		assertTrue(er3.event.isEventType(EventType.END_ELEMENT));
	}

	public void testSequence2() throws Exception {
		schema = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ " <xs:element name='root'>" + "  <xs:complexType>"
				+ "   <xs:sequence >"
				+ "    <xs:element name='a' type='xs:string' minOccurs='0' /> "
				+ "    <xs:element name='b' type='xs:string' /> "
				+ "   </xs:sequence>" + "  </xs:complexType>"
				+ " </xs:element>" + "</xs:schema>";

		Grammar g = getGrammarFromSchemaAsString(schema);
		GrammarContext gc = g.getGrammarContext();
		// GrammarURIEntry[] gues = g.getGrammarEntries();

		//Rule root = g.getGlobalElement(new QName("", "root")).getRule();
		Rule root = gc.getGrammarUriContext("").getQNameContext("root").getGlobalStartElement().getRule();
		// Rule root =
		// g.getGlobalElement(XSDGrammarBuilder.getEfficientQName(gues, "",
		// "root")).getRule();

		// SE(a, b)
		assertTrue(root.getNumberOfEvents() == 2);
		EventInformation er0 = root.lookFor(0);
		assertTrue(er0.event.isEventType(EventType.START_ELEMENT));
		assertTrue(((StartElement) er0.event).getQName().getLocalPart()
				.equals("a"));
		EventInformation er1 = root.lookFor(1);
		assertTrue(er1.event.isEventType(EventType.START_ELEMENT));
		assertTrue(((StartElement) er1.event).getQName().getLocalPart()
				.equals("b"));

		Rule a = er0.next;
		// SE(A)
		assertTrue(a.getNumberOfEvents() == 1);
		EventInformation era1 = a.lookFor(0);
		assertTrue(era1.event.isEventType(EventType.START_ELEMENT));
		assertTrue(((StartElement) era1.event).getQName().getLocalPart()
				.equals("b"));

		Rule b1 = er1.next;
		// SE(B)
		assertTrue(b1.getNumberOfEvents() == 1);
		EventInformation erb1 = b1.lookFor(0);
		assertTrue(erb1.event.isEventType(EventType.END_ELEMENT));

		Rule b2 = era1.next;
		// SE(B)
		assertTrue(b2.getNumberOfEvents() == 1);
		EventInformation erbb1 = b2.lookFor(0);
		assertTrue(erbb1.event.isEventType(EventType.END_ELEMENT));
	}

	public void testSequence3() throws Exception {
		schema = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ " <xs:element name='root'>"
				+ "  <xs:complexType>"
				+ "   <xs:sequence >"
				+ "    <xs:element name='a' type='xs:string' minOccurs='0'  maxOccurs='unbounded' /> "
				+ "    <xs:element name='b' type='xs:string' /> "
				+ "   </xs:sequence>" + "  </xs:complexType>"
				+ " </xs:element>" + "</xs:schema>";

		Grammar g = getGrammarFromSchemaAsString(schema);
		GrammarContext gc = g.getGrammarContext();
		// GrammarURIEntry[] gues = g.getGrammarEntries();

		// Rule root = g.getGlobalElement(new QName("", "root")).getRule();
		Rule root = gc.getGrammarUriContext("").getQNameContext("root").getGlobalStartElement().getRule();
		// Rule root =
		// g.getGlobalElement(XSDGrammarBuilder.getEfficientQName(gues, "",
		// "root")).getRule();

		// SE(a, b)
		assertTrue(root.getNumberOfEvents() == 2);
		EventInformation er0 = root.lookFor(0);
		assertTrue(er0.event.isEventType(EventType.START_ELEMENT));
		assertTrue(((StartElement) er0.event).getQName().getLocalPart()
				.equals("a"));
		EventInformation er1 = root.lookFor(1);
		assertTrue(er1.event.isEventType(EventType.START_ELEMENT));
		assertTrue(((StartElement) er1.event).getQName().getLocalPart()
				.equals("b"));

		Rule a = er0.next;
		// SE(A)
		assertTrue(a.getNumberOfEvents() == 2);
		EventInformation era1 = a.lookFor(0);
		assertTrue(era1.event.isEventType(EventType.START_ELEMENT));
		assertTrue(((StartElement) era1.event).getQName().getLocalPart()
				.equals("a"));
		EventInformation era2 = a.lookFor(1);
		assertTrue(era2.event.isEventType(EventType.START_ELEMENT));
		assertTrue(((StartElement) era2.event).getQName().getLocalPart()
				.equals("b"));

		Rule b1 = er1.next;
		// SE(B)
		assertTrue(b1.getNumberOfEvents() == 1);
		EventInformation erb1 = b1.lookFor(0);
		assertTrue(erb1.event.isEventType(EventType.END_ELEMENT));

		// multiple "a"'s followed by b
		Rule x = root.lookFor(0).next;
		for (int i = 0; i < 10; i++) {
			assertTrue(x.getNumberOfEvents() == 2);
			EventInformation x0 = x.lookFor(0);
			assertTrue(x0.event.isEventType(EventType.START_ELEMENT));
			assertTrue(((StartElement) x0.event).getQName().getLocalPart()
					.equals("a"));

			EventInformation x1 = x.lookFor(1);
			assertTrue(x1.event.isEventType(EventType.START_ELEMENT));
			assertTrue(((StartElement) x1.event).getQName().getLocalPart()
					.equals("b"));

			x = er0.next;
		}

		Rule b2 = x.lookFor(1).next;
		// SE(B)
		assertTrue(b2.getNumberOfEvents() == 1);
		EventInformation erbb1 = b2.lookFor(0);
		assertTrue(erbb1.event.isEventType(EventType.END_ELEMENT));
	}

	public void testSequence4() throws Exception {
		schema = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ " <xs:element name='root'>"
				+ "  <xs:complexType>"
				+ "   <xs:sequence maxOccurs='unbounded'>"
				+ "    <xs:element name='c' type='xs:string' minOccurs='0' maxOccurs='unbounded'/> "
				+ "   </xs:sequence>" + "  </xs:complexType>"
				+ " </xs:element>" + "</xs:schema>";

		Grammar g = getGrammarFromSchemaAsString(schema);
		GrammarContext gc = g.getGrammarContext();
		// GrammarURIEntry[] gues = g.getGrammarEntries();

		// Rule c = g.getGlobalElement(new QName("", "root")).getRule();
		Rule c = gc.getGrammarUriContext("").getQNameContext("root").getGlobalStartElement().getRule();
		// Rule c = g.getGlobalElement(XSDGrammarBuilder.getEfficientQName(gues,
		// "", "root")).getRule();

		// SE(c), EE
		// maxOccurs = "0" --> same rule over and over again
		for (int i = 0; i < 10; i++) {
			assertTrue(c.getNumberOfEvents() == 2);
			EventInformation er0 = c.lookFor(0);
			assertTrue(er0.event.isEventType(EventType.START_ELEMENT));
			assertTrue(((StartElement) er0.event).getQName().getLocalPart()
					.equals("c"));
			EventInformation er1 = c.lookFor(1);
			assertTrue(er1.event.isEventType(EventType.END_ELEMENT));

			c = er0.next;
		}
	}

	public void testSequence5() throws Exception {
		schema = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ " <xs:element name='root'>"
				+ "  <xs:complexType>"
				+ "   <xs:sequence maxOccurs='unbounded'>"
				+ "    <xs:element name='a' type='xs:string' minOccurs='0' maxOccurs='unbounded'/> "
				+ "    <xs:element name='c' type='xs:string' minOccurs='0' maxOccurs='unbounded'/> "
				+ "   </xs:sequence>" + "  </xs:complexType>"
				+ " </xs:element>" + "</xs:schema>";

		Grammar g = getGrammarFromSchemaAsString(schema);
		GrammarContext gc = g.getGrammarContext();
		// GrammarURIEntry[] gues = g.getGrammarEntries();

		// Rule rule = g.getGlobalElement(new QName("", "root")).getRule();
		Rule rule = gc.getGrammarUriContext("").getQNameContext("root").getGlobalStartElement().getRule();
		// Rule rule =
		// g.getGlobalElement(XSDGrammarBuilder.getEfficientQName(gues, "",
		// "root")).getRule();

		// SE(a), SE(c), EE
		{
			assertTrue(rule.getNumberOfEvents() == 3);
			EventInformation er0 = rule.lookFor(0);
			assertTrue(er0.event.isEventType(EventType.START_ELEMENT));
			assertTrue(((StartElement) er0.event).getQName().getLocalPart()
					.equals("a"));
			EventInformation er1 = rule.lookFor(1);
			assertTrue(er1.event.isEventType(EventType.START_ELEMENT));
			assertTrue(((StartElement) er1.event).getQName().getLocalPart()
					.equals("c"));
			EventInformation er2 = rule.lookFor(2);
			assertTrue(er2.event.isEventType(EventType.END_ELEMENT));
		}

		// SE(a), SE(c), EE
		// "a" over and over again
		{
			Rule a = rule.lookFor(0).next;
			for (int i = 0; i < 10; i++) {
				assertTrue(a.getNumberOfEvents() == 3);
				EventInformation er0 = a.lookFor(0);
				assertTrue(er0.event.isEventType(EventType.START_ELEMENT));
				assertTrue(((StartElement) er0.event).getQName().getLocalPart()
						.equals("a"));
				EventInformation er1 = a.lookFor(1);
				assertTrue(er1.event.isEventType(EventType.START_ELEMENT));
				assertTrue(((StartElement) er1.event).getQName().getLocalPart()
						.equals("c"));
				EventInformation er2 = a.lookFor(2);
				assertTrue(er2.event.isEventType(EventType.END_ELEMENT));

				a = er0.next;
			}
		}

		// SE(a), SE(c), EE
		// "c" over and over again
		{
			Rule c = rule.lookFor(0).next;
			for (int i = 0; i < 10; i++) {
				// System.out.println(i + ": " + c);
				assertTrue(c.getNumberOfEvents() == 3);
				EventInformation er0 = c.lookFor(0);
				assertTrue(er0.event.isEventType(EventType.START_ELEMENT));
				assertTrue(((StartElement) er0.event).getQName().getLocalPart()
						.equals("a"));
				EventInformation er1 = c.lookFor(1);
				assertTrue(er1.event.isEventType(EventType.START_ELEMENT));
				assertTrue(((StartElement) er1.event).getQName().getLocalPart()
						.equals("c"));
				EventInformation er2 = c.lookFor(2);
				assertTrue(er2.event.isEventType(EventType.END_ELEMENT));

				c = er1.next;
			}
		}

		// SE(a), SE(c), EE
		// alternately "a" and "c" over and over again
		{
			Rule ac = rule.lookFor(0).next;
			for (int i = 0; i < 10; i++) {
				if (i % 2 == 0) {

				} else {
					assertTrue(ac.getNumberOfEvents() == 3);
					EventInformation er0 = ac.lookFor(0);
					assertTrue(er0.event.isEventType(EventType.START_ELEMENT));
					assertTrue(((StartElement) er0.event).getQName()
							.getLocalPart().equals("a"));
					EventInformation er1 = ac.lookFor(1);
					assertTrue(er1.event.isEventType(EventType.START_ELEMENT));
					assertTrue(((StartElement) er1.event).getQName()
							.getLocalPart().equals("c"));
					EventInformation er2 = ac.lookFor(2);
					assertTrue(er2.event.isEventType(EventType.END_ELEMENT));

					ac = er0.next;
				}
			}
		}
	}

	public void testSequence6() throws Exception {
		schema = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ " <xs:element name='root'>"
				+ "  <xs:complexType>"
				+ "   <xs:sequence minOccurs='1' maxOccurs='unbounded'>"
				+ "    <xs:element name='a' type='xs:string' minOccurs='0' maxOccurs='unbounded'/> "
				+ "    <xs:element name='b' type='xs:string' minOccurs='0' maxOccurs='unbounded'/> "
				+ "    <xs:element name='c' type='xs:string' minOccurs='0' maxOccurs='unbounded'/> "
				+ "   </xs:sequence>" + "  </xs:complexType>"
				+ " </xs:element>" + "</xs:schema>";

		Grammar g = getGrammarFromSchemaAsString(schema);
		GrammarContext gc = g.getGrammarContext();
		// GrammarURIEntry[] gues = g.getGrammarEntries();

		// Rule rule = g.getGlobalElement(new QName("", "root")).getRule();
		Rule rule = gc.getGrammarUriContext("").getQNameContext("root").getGlobalStartElement().getRule();
		// Rule rule =
		// g.getGlobalElement(XSDGrammarBuilder.getEfficientQName(gues, "",
		// "root")).getRule();

		{
			assertTrue(rule.getNumberOfEvents() == 4);

			EventInformation er0 = rule.lookFor(0);
			assertTrue(er0.event.isEventType(EventType.START_ELEMENT));
			assertTrue(((StartElement) er0.event).getQName().getLocalPart()
					.equals("a"));

			EventInformation er1 = rule.lookFor(1);
			assertTrue(er1.event.isEventType(EventType.START_ELEMENT));
			assertTrue(((StartElement) er1.event).getQName().getLocalPart()
					.equals("b"));

			EventInformation er2 = rule.lookFor(2);
			assertTrue(er2.event.isEventType(EventType.START_ELEMENT));
			assertTrue(((StartElement) er2.event).getQName().getLocalPart()
					.equals("c"));

			EventInformation er3 = rule.lookFor(3);
			assertTrue(er3.event.isEventType(EventType.END_ELEMENT));
		}
	}

	public void testLearning1() throws Exception {
		schema = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ " <xs:element name='root'>"
				+ "  <xs:complexType>"
				+ "   <xs:sequence minOccurs='1' maxOccurs='unbounded'>"
				+ "    <xs:element name='a' type='xs:string' minOccurs='0' maxOccurs='unbounded'/> "
				+ "    <xs:element name='b' type='xs:string' minOccurs='0' maxOccurs='unbounded'/> "
				+ "    <xs:element name='c' type='xs:string' minOccurs='0' maxOccurs='unbounded'/> "
				+ "   </xs:sequence>" + "  </xs:complexType>"
				+ " </xs:element>" + "</xs:schema>";

		Grammar g = getGrammarFromSchemaAsString(schema);
		GrammarContext gc = g.getGrammarContext();
		// GrammarURIEntry[] gues = g.getGrammarEntries();

		// Rule rule = g.getGlobalElement(new QName("", "root")).getRule();
		Rule rule = gc.getGrammarUriContext("").getQNameContext("root").getGlobalStartElement().getRule();
		// Rule rule =
		// g.getGlobalElement(XSDGrammarBuilder.getEfficientQName(gues, "",
		// "root")).getRule();

		assertTrue(rule.getNumberOfEvents() == 4);

		// schema-informed grammars should not expand
		// EvolvingUriContext ruc = new RuntimeEvolvingUriContext(0, "");
		int namespaceUriID = 0;
		QNameContext qncAt = new QNameContext(namespaceUriID, 0, new QName("a"), 0);
		rule.learnAttribute(new Attribute(qncAt, null, null));
		QNameContext qncSE = new QNameContext(namespaceUriID, 1, new QName("s"), 1);
		rule.learnStartElement(new StartElement(qncSE));
		rule.learnEndElement();
		rule.learnCharacters();

		assertTrue(rule.getNumberOfEvents() == 4);
	}

	public void testLearning2() throws Exception {
		// schema-less grammars
		Rule startTag = new SchemaLessStartTag();
		Rule content = startTag.getElementContentRule();

		assertTrue(startTag.getNumberOfEvents() == 0);
		assertTrue(content.getNumberOfEvents() == 1); // EE

		// learn multiple EE --> at most one EE
		startTag.learnEndElement();
		startTag.learnEndElement();
		assertTrue(startTag.getNumberOfEvents() == 1);
		content.learnEndElement();
		content.learnEndElement();
		assertTrue(content.getNumberOfEvents() == 1);

		// learn multiple CH --> at most one CH
		startTag.learnCharacters();
		startTag.learnCharacters();
		assertTrue(startTag.getNumberOfEvents() == 2);
		content.learnCharacters();
		content.learnCharacters();
		assertTrue(content.getNumberOfEvents() == 2);

		// EvolvingUriContext ruc = new RuntimeEvolvingUriContext(0, "");
		int namespaceUriID = 0;
		
		// learn SE, can have multiple events even if similar
		QNameContext qncSE = new QNameContext(namespaceUriID, 1, new QName("s"), 1);
		StartElement s = new StartElement(qncSE);
		startTag.learnStartElement(s);
		startTag.learnStartElement(s);
		assertTrue(startTag.getNumberOfEvents() == 4);
		content.learnStartElement(s);
		content.learnStartElement(s);
		assertTrue(content.getNumberOfEvents() == 4);

		// learn AT, can have multiple events even if similar
		QNameContext qncAt = new QNameContext(namespaceUriID, 0, new QName("a"), 0);
		Attribute a = new Attribute(qncAt, null, null);
		startTag.learnAttribute(a);
		startTag.learnAttribute(a);
		assertTrue(startTag.getNumberOfEvents() == 6);
		// Note: element cannot learn AT
		
		
		// learn multiple AT(xsi:type) --> at most one AT(xsi:type)
		QNameContext qncAtxsiType = new QNameContext(2, 1, new QName(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "type"), 0);
		startTag.learnAttribute(new Attribute(qncAtxsiType, null, null));
		startTag.learnAttribute(new Attribute(qncAtxsiType, null, null));
		assertTrue(startTag.getNumberOfEvents() == 7);


	}

	public void testBugID3427971() throws Exception {
		QName qnDocument = new QName("", "Document");
		QName qnSurname = new QName("", "surname");
		
		
		schema = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ " <xs:element name='Document'>"
				+ "  <xs:complexType>"
				+ "   <xs:sequence >"
				+ "    <xs:element name='surname' type='xs:string' minOccurs='2' maxOccurs='unbounded'/> "
				+ "   </xs:sequence>" + "  </xs:complexType>"
				+ " </xs:element>" + "</xs:schema>";

		Grammar g2 = getGrammarFromSchemaAsString(schema);
		GrammarContext gc2 = g2.getGrammarContext();
		
		// Rule Document2 = g2.getGlobalElement(qnDocument).getRule();
		Rule Document2 = gc2.getGrammarUriContext(qnDocument.getNamespaceURI()).getQNameContext(qnDocument.getLocalPart()).getGlobalStartElement().getRule();
		{
			// 1
			assertTrue(Document2.getNumberOfEvents() == 1);
			EventInformation ei1 = Document2.lookFor(0);
			assertTrue(ei1.event.isEventType(EventType.START_ELEMENT));
			StartElement se1 = (StartElement) ei1.event;
			assertTrue(se1.getQName().equals(qnSurname));
			// 2
			assertTrue(ei1.next.getNumberOfEvents() == 1);
			EventInformation ei2 = ei1.next.lookFor(0);
			assertTrue(ei2.event.isEventType(EventType.START_ELEMENT));
			StartElement se2 = (StartElement) ei2.event;
			assertTrue(se2.getQName().equals(qnSurname));
			// 3
			assertTrue(ei2.next.getNumberOfEvents() == 2);
			assertTrue(ei2.next.lookFor(1).event.isEventType(EventType.END_ELEMENT));
			EventInformation ei3 = ei2.next.lookFor(0);
			assertTrue(ei3.event.isEventType(EventType.START_ELEMENT));
			StartElement se3 = (StartElement) ei3.event;
			assertTrue(se3.getQName().equals(qnSurname));
			// loop
			assertTrue(ei3.next == ei3.next.lookFor(0).next);
		}


		schema = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ " <xs:element name='Document'>"
				+ "  <xs:complexType>"
				+ "   <xs:sequence >"
				+ "    <xs:element name='surname' type='xs:string' minOccurs='3' maxOccurs='unbounded'/> "
				+ "   </xs:sequence>" + "  </xs:complexType>"
				+ " </xs:element>" + "</xs:schema>";
		Grammar g3 = getGrammarFromSchemaAsString(schema);
		GrammarContext gc3 = g3.getGrammarContext();
		// Rule Document3 = g3.getGlobalElement(qnDocument).getRule();
		Rule Document3 = gc3.getGrammarUriContext(qnDocument.getNamespaceURI()).getQNameContext(qnDocument.getLocalPart()).getGlobalStartElement().getRule();
		
		{
			// 1
			assertTrue(Document3.getNumberOfEvents() == 1);
			EventInformation ei1 = Document3.lookFor(0);
			assertTrue(ei1.event.isEventType(EventType.START_ELEMENT));
			StartElement se1 = (StartElement) ei1.event;
			assertTrue(se1.getQName().equals(qnSurname));
			// 2
			assertTrue(ei1.next.getNumberOfEvents() == 1);
			EventInformation ei2 = ei1.next.lookFor(0);
			assertTrue(ei2.event.isEventType(EventType.START_ELEMENT));
			StartElement se2 = (StartElement) ei2.event;
			assertTrue(se2.getQName().equals(qnSurname));
			// 3
			assertTrue(ei2.next.getNumberOfEvents() == 1);
			EventInformation ei3 = ei2.next.lookFor(0);
			assertTrue(ei3.event.isEventType(EventType.START_ELEMENT));
			StartElement se3 = (StartElement) ei3.event;
			assertTrue(se3.getQName().equals(qnSurname));
			// 4
			assertTrue(ei3.next.getNumberOfEvents() == 2);
			assertTrue(ei3.next.lookFor(1).event.isEventType(EventType.END_ELEMENT));
			EventInformation ei4 = ei3.next.lookFor(0);
			assertTrue(ei4.event.isEventType(EventType.START_ELEMENT));
			StartElement se4 = (StartElement) ei4.event;
			assertTrue(se4.getQName().equals(qnSurname));	
			// loop
			assertTrue(ei4.next == ei4.next.lookFor(0).next);
		}
	}

}
