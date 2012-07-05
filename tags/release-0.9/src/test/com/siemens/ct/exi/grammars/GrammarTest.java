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

package com.siemens.ct.exi.grammars;

import java.io.ByteArrayInputStream;

import javax.xml.namespace.QName;

import junit.framework.TestCase;

import org.custommonkey.xmlunit.XMLConstants;

import com.siemens.ct.exi.GrammarFactory;
import com.siemens.ct.exi.context.GrammarContext;
import com.siemens.ct.exi.context.QNameContext;
import com.siemens.ct.exi.grammars.event.Attribute;
import com.siemens.ct.exi.grammars.event.EventType;
import com.siemens.ct.exi.grammars.event.StartElement;
import com.siemens.ct.exi.grammars.grammar.BuiltInStartTag;
import com.siemens.ct.exi.grammars.grammar.Grammar;
import com.siemens.ct.exi.grammars.production.Production;

public class GrammarTest extends TestCase {
	String schema;

	public static Grammars getGrammarFromSchemaAsString(String schemaAsString)
			throws Exception {
		ByteArrayInputStream bais = new ByteArrayInputStream(
				schemaAsString.getBytes());
		GrammarFactory grammarFactory = GrammarFactory.newInstance();
		Grammars grammar = grammarFactory.createGrammars(bais);

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

		Grammars g = getGrammarFromSchemaAsString(schema);
		GrammarContext gc = g.getGrammarContext();
		// GrammarURIEntry[] gues = g.getGrammarEntries();

		// Rule root = g.getGlobalElement(new QName("", "root")).getRule();
		Grammar root = gc.getGrammarUriContext("").getQNameContext("root").getGlobalStartElement().getRule();
		// Rule root =
		// g.getGlobalElement(XSDGrammarBuilder.getEfficientQName(gues, "",
		// "root")).getRule();

		// SE(a)
		assertTrue(root.getNumberOfEvents() == 1);
		Production er0 = root.lookFor(0);
		assertTrue(er0.getEvent().isEventType(EventType.START_ELEMENT));
		assertTrue(((StartElement) er0.getEvent()).getQName().getLocalPart()
				.equals("a"));

		Grammar a = er0.getNextRule();
		// SE(b)
		assertTrue(a.getNumberOfEvents() == 1);
		Production er1 = a.lookFor(0);
		assertTrue(er1.getEvent().isEventType(EventType.START_ELEMENT));
		assertTrue(((StartElement) er1.getEvent()).getQName().getLocalPart()
				.equals("b"));

		Grammar b = er1.getNextRule();
		// SE(b)
		assertTrue(b.getNumberOfEvents() == 1);
		Production er2 = b.lookFor(0);
		assertTrue(er2.getEvent().isEventType(EventType.END_ELEMENT));
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

		Grammars g = getGrammarFromSchemaAsString(schema);
		GrammarContext gc = g.getGrammarContext();
		// GrammarURIEntry[] gues = g.getGrammarEntries();

		// Rule root = g.getGlobalElement(new QName("", "root")).getRule();
		Grammar root = gc.getGrammarUriContext("").getQNameContext("root").getGlobalStartElement().getRule();
		// Rule root =
		// g.getGlobalElement(XSDGrammarBuilder.getEfficientQName(gues, "",
		// "root")).getRule();

		// SE(a)
		assertTrue(root.getNumberOfEvents() == 1);
		Production er0 = root.lookFor(0);
		assertTrue(er0.getEvent().isEventType(EventType.START_ELEMENT));
		assertTrue(((StartElement) er0.getEvent()).getQName().getLocalPart()
				.equals("a"));

		Grammar a_1 = er0.getNextRule();
		// SE(a)
		assertTrue(a_1.getNumberOfEvents() == 1);
		Production er1 = a_1.lookFor(0);
		assertTrue(er1.getEvent().isEventType(EventType.START_ELEMENT));
		assertTrue(((StartElement) er1.getEvent()).getQName().getLocalPart()
				.equals("a"));

		Grammar a_2 = er1.getNextRule();
		// SE(b)
		assertTrue(a_2.getNumberOfEvents() == 1);
		Production er2 = a_2.lookFor(0);
		assertTrue(er2.getEvent().isEventType(EventType.START_ELEMENT));
		assertTrue(((StartElement) er2.getEvent()).getQName().getLocalPart()
				.equals("b"));

		Grammar b = er2.getNextRule();
		// SE(b)
		assertTrue(b.getNumberOfEvents() == 1);
		Production er3 = b.lookFor(0);
		assertTrue(er3.getEvent().isEventType(EventType.END_ELEMENT));
	}

	public void testSequence2() throws Exception {
		schema = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ " <xs:element name='root'>" + "  <xs:complexType>"
				+ "   <xs:sequence >"
				+ "    <xs:element name='a' type='xs:string' minOccurs='0' /> "
				+ "    <xs:element name='b' type='xs:string' /> "
				+ "   </xs:sequence>" + "  </xs:complexType>"
				+ " </xs:element>" + "</xs:schema>";

		Grammars g = getGrammarFromSchemaAsString(schema);
		GrammarContext gc = g.getGrammarContext();
		// GrammarURIEntry[] gues = g.getGrammarEntries();

		//Rule root = g.getGlobalElement(new QName("", "root")).getRule();
		Grammar root = gc.getGrammarUriContext("").getQNameContext("root").getGlobalStartElement().getRule();
		// Rule root =
		// g.getGlobalElement(XSDGrammarBuilder.getEfficientQName(gues, "",
		// "root")).getRule();

		// SE(a, b)
		assertTrue(root.getNumberOfEvents() == 2);
		Production er0 = root.lookFor(0);
		assertTrue(er0.getEvent().isEventType(EventType.START_ELEMENT));
		assertTrue(((StartElement) er0.getEvent()).getQName().getLocalPart()
				.equals("a"));
		Production er1 = root.lookFor(1);
		assertTrue(er1.getEvent().isEventType(EventType.START_ELEMENT));
		assertTrue(((StartElement) er1.getEvent()).getQName().getLocalPart()
				.equals("b"));

		Grammar a = er0.getNextRule();
		// SE(A)
		assertTrue(a.getNumberOfEvents() == 1);
		Production era1 = a.lookFor(0);
		assertTrue(era1.getEvent().isEventType(EventType.START_ELEMENT));
		assertTrue(((StartElement) era1.getEvent()).getQName().getLocalPart()
				.equals("b"));

		Grammar b1 = er1.getNextRule();
		// SE(B)
		assertTrue(b1.getNumberOfEvents() == 1);
		Production erb1 = b1.lookFor(0);
		assertTrue(erb1.getEvent().isEventType(EventType.END_ELEMENT));

		Grammar b2 = era1.getNextRule();
		// SE(B)
		assertTrue(b2.getNumberOfEvents() == 1);
		Production erbb1 = b2.lookFor(0);
		assertTrue(erbb1.getEvent().isEventType(EventType.END_ELEMENT));
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

		Grammars g = getGrammarFromSchemaAsString(schema);
		GrammarContext gc = g.getGrammarContext();
		// GrammarURIEntry[] gues = g.getGrammarEntries();

		// Rule root = g.getGlobalElement(new QName("", "root")).getRule();
		Grammar root = gc.getGrammarUriContext("").getQNameContext("root").getGlobalStartElement().getRule();
		// Rule root =
		// g.getGlobalElement(XSDGrammarBuilder.getEfficientQName(gues, "",
		// "root")).getRule();

		// SE(a, b)
		assertTrue(root.getNumberOfEvents() == 2);
		Production er0 = root.lookFor(0);
		assertTrue(er0.getEvent().isEventType(EventType.START_ELEMENT));
		assertTrue(((StartElement) er0.getEvent()).getQName().getLocalPart()
				.equals("a"));
		Production er1 = root.lookFor(1);
		assertTrue(er1.getEvent().isEventType(EventType.START_ELEMENT));
		assertTrue(((StartElement) er1.getEvent()).getQName().getLocalPart()
				.equals("b"));

		Grammar a = er0.getNextRule();
		// SE(A)
		assertTrue(a.getNumberOfEvents() == 2);
		Production era1 = a.lookFor(0);
		assertTrue(era1.getEvent().isEventType(EventType.START_ELEMENT));
		assertTrue(((StartElement) era1.getEvent()).getQName().getLocalPart()
				.equals("a"));
		Production era2 = a.lookFor(1);
		assertTrue(era2.getEvent().isEventType(EventType.START_ELEMENT));
		assertTrue(((StartElement) era2.getEvent()).getQName().getLocalPart()
				.equals("b"));

		Grammar b1 = er1.getNextRule();
		// SE(B)
		assertTrue(b1.getNumberOfEvents() == 1);
		Production erb1 = b1.lookFor(0);
		assertTrue(erb1.getEvent().isEventType(EventType.END_ELEMENT));

		// multiple "a"'s followed by b
		Grammar x = root.lookFor(0).getNextRule();
		for (int i = 0; i < 10; i++) {
			assertTrue(x.getNumberOfEvents() == 2);
			Production x0 = x.lookFor(0);
			assertTrue(x0.getEvent().isEventType(EventType.START_ELEMENT));
			assertTrue(((StartElement) x0.getEvent()).getQName().getLocalPart()
					.equals("a"));

			Production x1 = x.lookFor(1);
			assertTrue(x1.getEvent().isEventType(EventType.START_ELEMENT));
			assertTrue(((StartElement) x1.getEvent()).getQName().getLocalPart()
					.equals("b"));

			x = er0.getNextRule();
		}

		Grammar b2 = x.lookFor(1).getNextRule();
		// SE(B)
		assertTrue(b2.getNumberOfEvents() == 1);
		Production erbb1 = b2.lookFor(0);
		assertTrue(erbb1.getEvent().isEventType(EventType.END_ELEMENT));
	}

	public void testSequence4() throws Exception {
		schema = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ " <xs:element name='root'>"
				+ "  <xs:complexType>"
				+ "   <xs:sequence maxOccurs='unbounded'>"
				+ "    <xs:element name='c' type='xs:string' minOccurs='0' maxOccurs='unbounded'/> "
				+ "   </xs:sequence>" + "  </xs:complexType>"
				+ " </xs:element>" + "</xs:schema>";

		Grammars g = getGrammarFromSchemaAsString(schema);
		GrammarContext gc = g.getGrammarContext();
		// GrammarURIEntry[] gues = g.getGrammarEntries();

		// Rule c = g.getGlobalElement(new QName("", "root")).getRule();
		Grammar c = gc.getGrammarUriContext("").getQNameContext("root").getGlobalStartElement().getRule();
		// Rule c = g.getGlobalElement(XSDGrammarBuilder.getEfficientQName(gues,
		// "", "root")).getRule();

		// SE(c), EE
		// maxOccurs = "0" --> same rule over and over again
		for (int i = 0; i < 10; i++) {
			assertTrue(c.getNumberOfEvents() == 2);
			Production er0 = c.lookFor(0);
			assertTrue(er0.getEvent().isEventType(EventType.START_ELEMENT));
			assertTrue(((StartElement) er0.getEvent()).getQName().getLocalPart()
					.equals("c"));
			Production er1 = c.lookFor(1);
			assertTrue(er1.getEvent().isEventType(EventType.END_ELEMENT));

			c = er0.getNextRule();
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

		Grammars g = getGrammarFromSchemaAsString(schema);
		GrammarContext gc = g.getGrammarContext();
		// GrammarURIEntry[] gues = g.getGrammarEntries();

		// Rule rule = g.getGlobalElement(new QName("", "root")).getRule();
		Grammar rule = gc.getGrammarUriContext("").getQNameContext("root").getGlobalStartElement().getRule();
		// Rule rule =
		// g.getGlobalElement(XSDGrammarBuilder.getEfficientQName(gues, "",
		// "root")).getRule();

		// SE(a), SE(c), EE
		{
			assertTrue(rule.getNumberOfEvents() == 3);
			Production er0 = rule.lookFor(0);
			assertTrue(er0.getEvent().isEventType(EventType.START_ELEMENT));
			assertTrue(((StartElement) er0.getEvent()).getQName().getLocalPart()
					.equals("a"));
			Production er1 = rule.lookFor(1);
			assertTrue(er1.getEvent().isEventType(EventType.START_ELEMENT));
			assertTrue(((StartElement) er1.getEvent()).getQName().getLocalPart()
					.equals("c"));
			Production er2 = rule.lookFor(2);
			assertTrue(er2.getEvent().isEventType(EventType.END_ELEMENT));
		}

		// SE(a), SE(c), EE
		// "a" over and over again
		{
			Grammar a = rule.lookFor(0).getNextRule();
			for (int i = 0; i < 10; i++) {
				assertTrue(a.getNumberOfEvents() == 3);
				Production er0 = a.lookFor(0);
				assertTrue(er0.getEvent().isEventType(EventType.START_ELEMENT));
				assertTrue(((StartElement) er0.getEvent()).getQName().getLocalPart()
						.equals("a"));
				Production er1 = a.lookFor(1);
				assertTrue(er1.getEvent().isEventType(EventType.START_ELEMENT));
				assertTrue(((StartElement) er1.getEvent()).getQName().getLocalPart()
						.equals("c"));
				Production er2 = a.lookFor(2);
				assertTrue(er2.getEvent().isEventType(EventType.END_ELEMENT));

				a = er0.getNextRule();
			}
		}

		// SE(a), SE(c), EE
		// "c" over and over again
		{
			Grammar c = rule.lookFor(0).getNextRule();
			for (int i = 0; i < 10; i++) {
				// System.out.println(i + ": " + c);
				assertTrue(c.getNumberOfEvents() == 3);
				Production er0 = c.lookFor(0);
				assertTrue(er0.getEvent().isEventType(EventType.START_ELEMENT));
				assertTrue(((StartElement) er0.getEvent()).getQName().getLocalPart()
						.equals("a"));
				Production er1 = c.lookFor(1);
				assertTrue(er1.getEvent().isEventType(EventType.START_ELEMENT));
				assertTrue(((StartElement) er1.getEvent()).getQName().getLocalPart()
						.equals("c"));
				Production er2 = c.lookFor(2);
				assertTrue(er2.getEvent().isEventType(EventType.END_ELEMENT));

				c = er1.getNextRule();
			}
		}

		// SE(a), SE(c), EE
		// alternately "a" and "c" over and over again
		{
			Grammar ac = rule.lookFor(0).getNextRule();
			for (int i = 0; i < 10; i++) {
				if (i % 2 == 0) {

				} else {
					assertTrue(ac.getNumberOfEvents() == 3);
					Production er0 = ac.lookFor(0);
					assertTrue(er0.getEvent().isEventType(EventType.START_ELEMENT));
					assertTrue(((StartElement) er0.getEvent()).getQName()
							.getLocalPart().equals("a"));
					Production er1 = ac.lookFor(1);
					assertTrue(er1.getEvent().isEventType(EventType.START_ELEMENT));
					assertTrue(((StartElement) er1.getEvent()).getQName()
							.getLocalPart().equals("c"));
					Production er2 = ac.lookFor(2);
					assertTrue(er2.getEvent().isEventType(EventType.END_ELEMENT));

					ac = er0.getNextRule();
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

		Grammars g = getGrammarFromSchemaAsString(schema);
		GrammarContext gc = g.getGrammarContext();
		// GrammarURIEntry[] gues = g.getGrammarEntries();

		// Rule rule = g.getGlobalElement(new QName("", "root")).getRule();
		Grammar rule = gc.getGrammarUriContext("").getQNameContext("root").getGlobalStartElement().getRule();
		// Rule rule =
		// g.getGlobalElement(XSDGrammarBuilder.getEfficientQName(gues, "",
		// "root")).getRule();

		{
			assertTrue(rule.getNumberOfEvents() == 4);

			Production er0 = rule.lookFor(0);
			assertTrue(er0.getEvent().isEventType(EventType.START_ELEMENT));
			assertTrue(((StartElement) er0.getEvent()).getQName().getLocalPart()
					.equals("a"));

			Production er1 = rule.lookFor(1);
			assertTrue(er1.getEvent().isEventType(EventType.START_ELEMENT));
			assertTrue(((StartElement) er1.getEvent()).getQName().getLocalPart()
					.equals("b"));

			Production er2 = rule.lookFor(2);
			assertTrue(er2.getEvent().isEventType(EventType.START_ELEMENT));
			assertTrue(((StartElement) er2.getEvent()).getQName().getLocalPart()
					.equals("c"));

			Production er3 = rule.lookFor(3);
			assertTrue(er3.getEvent().isEventType(EventType.END_ELEMENT));
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

		Grammars g = getGrammarFromSchemaAsString(schema);
		GrammarContext gc = g.getGrammarContext();
		// GrammarURIEntry[] gues = g.getGrammarEntries();

		// Rule rule = g.getGlobalElement(new QName("", "root")).getRule();
		Grammar rule = gc.getGrammarUriContext("").getQNameContext("root").getGlobalStartElement().getRule();
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
		Grammar startTag = new BuiltInStartTag();
		Grammar content = startTag.getElementContent();

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

		Grammars g2 = getGrammarFromSchemaAsString(schema);
		GrammarContext gc2 = g2.getGrammarContext();
		
		// Rule Document2 = g2.getGlobalElement(qnDocument).getRule();
		Grammar Document2 = gc2.getGrammarUriContext(qnDocument.getNamespaceURI()).getQNameContext(qnDocument.getLocalPart()).getGlobalStartElement().getRule();
		{
			// 1
			assertTrue(Document2.getNumberOfEvents() == 1);
			Production ei1 = Document2.lookFor(0);
			assertTrue(ei1.getEvent().isEventType(EventType.START_ELEMENT));
			StartElement se1 = (StartElement) ei1.getEvent();
			assertTrue(se1.getQName().equals(qnSurname));
			// 2
			assertTrue(ei1.getNextRule().getNumberOfEvents() == 1);
			Production ei2 = ei1.getNextRule().lookFor(0);
			assertTrue(ei2.getEvent().isEventType(EventType.START_ELEMENT));
			StartElement se2 = (StartElement) ei2.getEvent();
			assertTrue(se2.getQName().equals(qnSurname));
			// 3
			assertTrue(ei2.getNextRule().getNumberOfEvents() == 2);
			assertTrue(ei2.getNextRule().lookFor(1).getEvent().isEventType(EventType.END_ELEMENT));
			Production ei3 = ei2.getNextRule().lookFor(0);
			assertTrue(ei3.getEvent().isEventType(EventType.START_ELEMENT));
			StartElement se3 = (StartElement) ei3.getEvent();
			assertTrue(se3.getQName().equals(qnSurname));
			// loop
			assertTrue(ei3.getNextRule() == ei3.getNextRule().lookFor(0).getNextRule());
		}


		schema = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ " <xs:element name='Document'>"
				+ "  <xs:complexType>"
				+ "   <xs:sequence >"
				+ "    <xs:element name='surname' type='xs:string' minOccurs='3' maxOccurs='unbounded'/> "
				+ "   </xs:sequence>" + "  </xs:complexType>"
				+ " </xs:element>" + "</xs:schema>";
		Grammars g3 = getGrammarFromSchemaAsString(schema);
		GrammarContext gc3 = g3.getGrammarContext();
		// Rule Document3 = g3.getGlobalElement(qnDocument).getRule();
		Grammar Document3 = gc3.getGrammarUriContext(qnDocument.getNamespaceURI()).getQNameContext(qnDocument.getLocalPart()).getGlobalStartElement().getRule();
		
		{
			// 1
			assertTrue(Document3.getNumberOfEvents() == 1);
			Production ei1 = Document3.lookFor(0);
			assertTrue(ei1.getEvent().isEventType(EventType.START_ELEMENT));
			StartElement se1 = (StartElement) ei1.getEvent();
			assertTrue(se1.getQName().equals(qnSurname));
			// 2
			assertTrue(ei1.getNextRule().getNumberOfEvents() == 1);
			Production ei2 = ei1.getNextRule().lookFor(0);
			assertTrue(ei2.getEvent().isEventType(EventType.START_ELEMENT));
			StartElement se2 = (StartElement) ei2.getEvent();
			assertTrue(se2.getQName().equals(qnSurname));
			// 3
			assertTrue(ei2.getNextRule().getNumberOfEvents() == 1);
			Production ei3 = ei2.getNextRule().lookFor(0);
			assertTrue(ei3.getEvent().isEventType(EventType.START_ELEMENT));
			StartElement se3 = (StartElement) ei3.getEvent();
			assertTrue(se3.getQName().equals(qnSurname));
			// 4
			assertTrue(ei3.getNextRule().getNumberOfEvents() == 2);
			assertTrue(ei3.getNextRule().lookFor(1).getEvent().isEventType(EventType.END_ELEMENT));
			Production ei4 = ei3.getNextRule().lookFor(0);
			assertTrue(ei4.getEvent().isEventType(EventType.START_ELEMENT));
			StartElement se4 = (StartElement) ei4.getEvent();
			assertTrue(se4.getQName().equals(qnSurname));	
			// loop
			assertTrue(ei4.getNextRule() == ei4.getNextRule().lookFor(0).getNextRule());
		}
	}

}
