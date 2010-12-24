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

package com.siemens.ct.exi.grammar;

import java.io.ByteArrayInputStream;

import javax.xml.namespace.QName;

import junit.framework.TestCase;

import com.siemens.ct.exi.GrammarFactory;
import com.siemens.ct.exi.grammar.event.EventType;
import com.siemens.ct.exi.grammar.event.StartElement;
import com.siemens.ct.exi.grammar.rule.Rule;

public class GrammarTest extends TestCase {
	String schema;

	public static Grammar getGrammarFromSchemaAsString(String schemaAsString)
			throws Exception {
		ByteArrayInputStream bais = new ByteArrayInputStream(schemaAsString
				.getBytes());
		GrammarFactory grammarFactory = GrammarFactory.newInstance();
		Grammar grammar = grammarFactory.createGrammar(bais);

		return grammar;
	}
	
	public void testSequence1() throws Exception {
		schema = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ " <xs:element name='root'>"
				+ "  <xs:complexType>"
				+ "   <xs:sequence >"
				+ "    <xs:element name='a' type='xs:string' /> "
				+ "    <xs:element name='b' type='xs:string' /> "
				+ "   </xs:sequence>" + "  </xs:complexType>"
				+ " </xs:element>" + "</xs:schema>";

		Grammar g = getGrammarFromSchemaAsString(schema);

		Rule root = g.getGlobalElement(new QName("", "root")).getRule();
		
		//	SE(a)
		assertTrue(root.getNumberOfEvents() == 1);
		EventInformation er0 = root.lookFor(0);
		assertTrue(er0.event.isEventType(EventType.START_ELEMENT));
		assertTrue(((StartElement) er0.event).getQName().getLocalPart().equals("a"));

		Rule a = er0.next;
		// SE(b)
		assertTrue(a.getNumberOfEvents() == 1);
		EventInformation er1 = a.lookFor(0);
		assertTrue(er1.event.isEventType(EventType.START_ELEMENT));
		assertTrue(((StartElement) er1.event).getQName().getLocalPart().equals("b"));
		
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

		Rule root = g.getGlobalElement(new QName("", "root")).getRule();
		
		//	SE(a)
		assertTrue(root.getNumberOfEvents() == 1);
		EventInformation er0 = root.lookFor(0);
		assertTrue(er0.event.isEventType(EventType.START_ELEMENT));
		assertTrue(((StartElement) er0.event).getQName().getLocalPart().equals("a"));

		Rule a_1 = er0.next;
		// SE(a)
		assertTrue(a_1.getNumberOfEvents() == 1);
		EventInformation er1 = a_1.lookFor(0);
		assertTrue(er1.event.isEventType(EventType.START_ELEMENT));
		assertTrue(((StartElement) er1.event).getQName().getLocalPart().equals("a"));
		
		Rule a_2 = er1.next;
		// SE(b)
		assertTrue(a_2.getNumberOfEvents() == 1);
		EventInformation er2 = a_2.lookFor(0);
		assertTrue(er2.event.isEventType(EventType.START_ELEMENT));
		assertTrue(((StartElement) er2.event).getQName().getLocalPart().equals("b"));
		
		Rule b = er2.next;
		// SE(b)
		assertTrue(b.getNumberOfEvents() == 1);
		EventInformation er3 = b.lookFor(0);
		assertTrue(er3.event.isEventType(EventType.END_ELEMENT));
	}
	
	public void testSequence2() throws Exception {
		schema = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ " <xs:element name='root'>"
				+ "  <xs:complexType>"
				+ "   <xs:sequence >"
				+ "    <xs:element name='a' type='xs:string' minOccurs='0' /> "
				+ "    <xs:element name='b' type='xs:string' /> "
				+ "   </xs:sequence>" + "  </xs:complexType>"
				+ " </xs:element>" + "</xs:schema>";

		Grammar g = getGrammarFromSchemaAsString(schema);

		Rule root = g.getGlobalElement(new QName("", "root")).getRule();
		
		//	SE(a, b)
		assertTrue(root.getNumberOfEvents() == 2);
		EventInformation er0 = root.lookFor(0);
		assertTrue(er0.event.isEventType(EventType.START_ELEMENT));
		assertTrue(((StartElement) er0.event).getQName().getLocalPart().equals("a"));
		EventInformation er1 = root.lookFor(1);
		assertTrue(er1.event.isEventType(EventType.START_ELEMENT));
		assertTrue(((StartElement) er1.event).getQName().getLocalPart().equals("b"));

		Rule a = er0.next;
		// SE(A)
		assertTrue(a.getNumberOfEvents() == 1);
		EventInformation era1 = a.lookFor(0);
		assertTrue(era1.event.isEventType(EventType.START_ELEMENT));
		assertTrue(((StartElement) era1.event).getQName().getLocalPart().equals("b"));
		
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

		Rule root = g.getGlobalElement(new QName("", "root")).getRule();
		
		//	SE(a, b)
		assertTrue(root.getNumberOfEvents() == 2);
		EventInformation er0 = root.lookFor(0);
		assertTrue(er0.event.isEventType(EventType.START_ELEMENT));
		assertTrue(((StartElement) er0.event).getQName().getLocalPart().equals("a"));
		EventInformation er1 = root.lookFor(1);
		assertTrue(er1.event.isEventType(EventType.START_ELEMENT));
		assertTrue(((StartElement) er1.event).getQName().getLocalPart().equals("b"));

		
		Rule a = er0.next;
		// SE(A)
		assertTrue(a.getNumberOfEvents() == 2);
		EventInformation era1 = a.lookFor(0);
		assertTrue(era1.event.isEventType(EventType.START_ELEMENT));
		assertTrue(((StartElement) era1.event).getQName().getLocalPart().equals("a"));
		EventInformation era2 = a.lookFor(1);
		assertTrue(era2.event.isEventType(EventType.START_ELEMENT));
		assertTrue(((StartElement) era2.event).getQName().getLocalPart().equals("b"));
		
		Rule b1 = er1.next;
		// SE(B)
		assertTrue(b1.getNumberOfEvents() == 1);
		EventInformation erb1 = b1.lookFor(0);
		assertTrue(erb1.event.isEventType(EventType.END_ELEMENT));
		
		//	multiple "a"'s followed by b
		Rule x = root.lookFor(0).next;
		for (int i = 0; i < 10; i++) {
			assertTrue(x.getNumberOfEvents() == 2);
			EventInformation x0 = x.lookFor(0);
			assertTrue(x0.event.isEventType(EventType.START_ELEMENT));
			assertTrue(((StartElement) x0.event).getQName().getLocalPart().equals("a"));
			
			EventInformation x1 = x.lookFor(1);
			assertTrue(x1.event.isEventType(EventType.START_ELEMENT));
			assertTrue(((StartElement) x1.event).getQName().getLocalPart().equals("b"));

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

		Rule c = g.getGlobalElement(new QName("", "root")).getRule();

		// SE(c), EE
		// maxOccurs = "0" --> same rule over and over again
		for (int i = 0; i < 10; i++) {
			assertTrue(c.getNumberOfEvents() == 2);
			EventInformation er0 = c.lookFor(0);
			assertTrue(er0.event.isEventType(EventType.START_ELEMENT));
			assertTrue(((StartElement) er0.event).getQName().getLocalPart().equals("c"));
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

		Rule rule = g.getGlobalElement(new QName("", "root")).getRule();

		// SE(a), SE(c), EE
		{
			assertTrue(rule.getNumberOfEvents() == 3);
			EventInformation er0 = rule.lookFor(0);
			assertTrue(er0.event.isEventType(EventType.START_ELEMENT));
			assertTrue(((StartElement) er0.event).getQName().getLocalPart().equals("a"));
			EventInformation er1 = rule.lookFor(1);
			assertTrue(er1.event.isEventType(EventType.START_ELEMENT));
			assertTrue(((StartElement) er1.event).getQName().getLocalPart().equals("c"));
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
					assertTrue(((StartElement) er0.event).getQName().getLocalPart()
							.equals("a"));
					EventInformation er1 = ac.lookFor(1);
					assertTrue(er1.event.isEventType(EventType.START_ELEMENT));
					assertTrue(((StartElement) er1.event).getQName().getLocalPart()
							.equals("c"));
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

		Rule rule = g.getGlobalElement(new QName("", "root")).getRule();

		{
			assertTrue(rule.getNumberOfEvents() == 4);
			
			EventInformation er0 = rule.lookFor(0);
			assertTrue(er0.event.isEventType(EventType.START_ELEMENT));
			assertTrue(((StartElement) er0.event).getQName().getLocalPart().equals("a"));

			EventInformation er1 = rule.lookFor(1);
			assertTrue(er1.event.isEventType(EventType.START_ELEMENT));
			assertTrue(((StartElement) er1.event).getQName().getLocalPart().equals("b"));
			
			EventInformation er2 = rule.lookFor(2);
			assertTrue(er2.event.isEventType(EventType.START_ELEMENT));
			assertTrue(((StartElement) er2.event).getQName().getLocalPart().equals("c"));
			
			EventInformation er3 = rule.lookFor(3);
			assertTrue(er3.event.isEventType(EventType.END_ELEMENT));
		}
	}

}
