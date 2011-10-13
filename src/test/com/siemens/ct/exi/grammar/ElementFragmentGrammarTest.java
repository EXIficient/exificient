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

import junit.framework.TestCase;

import com.siemens.ct.exi.GrammarFactory;
import com.siemens.ct.exi.grammar.event.Attribute;
import com.siemens.ct.exi.grammar.event.EventType;
import com.siemens.ct.exi.grammar.event.StartElement;
import com.siemens.ct.exi.grammar.rule.Rule;
import com.siemens.ct.exi.types.BuiltInType;

public class ElementFragmentGrammarTest extends TestCase {
	String schema;

	public static Grammar getGrammarFromSchemaAsString(String schemaAsString)
			throws Exception {
		ByteArrayInputStream bais = new ByteArrayInputStream(schemaAsString
				.getBytes());
		GrammarFactory grammarFactory = GrammarFactory.newInstance();
		Grammar grammar = grammarFactory.createGrammar(bais);

		return grammar;
	}

	// attribute c twice but same type
	public void testAttributes1() throws Exception {
		schema = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ " <xs:element name='root'>"
				+ "  <xs:complexType>"
				+ "   <xs:sequence >"
				+ "    <xs:element name='a' type='xs:string' minOccurs='0' /> "
				+ "    <xs:element name='b' type='xs:string' minOccurs='0' /> "
				+ "    <xs:element name='root' type='xs:string' minOccurs='0' /> "
				+ "   </xs:sequence>"
				+ "   <xs:attribute name='c' type='xs:int'/>"
				+ "   <xs:attribute name='d' type='xs:int'/>"
				+ "  </xs:complexType>"
				+ " </xs:element>"
				+ " <xs:attribute name='c' type='xs:int'/>"
				+ "</xs:schema>";

		Grammar g = getGrammarFromSchemaAsString(schema);
		Rule r = g.getFragmentGrammar();

		EventInformation ei = r.lookForEvent(EventType.START_DOCUMENT);
		EventInformation ei2 =  ei.next.lookForStartElement("", "root");
		assertTrue(ei2.event.isEventType(EventType.START_ELEMENT));
		StartElement seRoot = (StartElement) ei2.event;
		Rule rRoot = seRoot.getRule();
		
		EventInformation eiAtC = rRoot.lookForAttribute("", "c");
		assertTrue(eiAtC.event.isEventType(EventType.ATTRIBUTE));
		Attribute atC = (Attribute) eiAtC.event;
		assertTrue(atC.getDatatype().getBuiltInType() == BuiltInType.INTEGER);
		
		EventInformation eiAtD = rRoot.lookForAttribute("", "d");
		assertTrue(eiAtD.event.isEventType(EventType.ATTRIBUTE));
		Attribute atD = (Attribute) eiAtD.event;
		assertTrue(atD.getDatatype().getBuiltInType() == BuiltInType.INTEGER);
	}
	
	
	// attribute c has to different definitions-> typed a string
	public void testAttributes2() throws Exception {
		schema = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ " <xs:element name='root'>"
				+ "  <xs:complexType>"
				+ "   <xs:sequence >"
				+ "    <xs:element name='a' type='xs:string' minOccurs='0' /> "
				+ "    <xs:element name='b' type='xs:string' minOccurs='0' /> "
				+ "    <xs:element name='root' type='xs:string' minOccurs='0' /> "
				+ "   </xs:sequence>"
				+ "   <xs:attribute name='c' type='xs:int'/>"
				+ "   <xs:attribute name='d' type='xs:int'/>"
				+ "  </xs:complexType>"
				+ " </xs:element>"
				+ " <xs:attribute name='c' type='xs:date'/>"
				+ "</xs:schema>";

		Grammar g = getGrammarFromSchemaAsString(schema);
		Rule r = g.getFragmentGrammar();

		EventInformation ei = r.lookForEvent(EventType.START_DOCUMENT);
		EventInformation ei2 =  ei.next.lookForStartElement("", "root");
		assertTrue(ei2.event.isEventType(EventType.START_ELEMENT));
		StartElement seRoot = (StartElement) ei2.event;
		Rule rRoot = seRoot.getRule();
		
		EventInformation eiAtC = rRoot.lookForAttribute("", "c");
		assertTrue(eiAtC.event.isEventType(EventType.ATTRIBUTE));
		Attribute atC = (Attribute) eiAtC.event;
		assertTrue(atC.getDatatype().getBuiltInType() == BuiltInType.STRING);
		
		EventInformation eiAtD = rRoot.lookForAttribute("", "d");
		assertTrue(eiAtD.event.isEventType(EventType.ATTRIBUTE));
		Attribute atD = (Attribute) eiAtD.event;
		assertTrue(atD.getDatatype().getBuiltInType() == BuiltInType.INTEGER);
	}

}
