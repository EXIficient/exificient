/*
 * Copyright (C) 2007-2015 Siemens AG
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

import junit.framework.TestCase;

import com.siemens.ct.exi.GrammarFactory;
import com.siemens.ct.exi.grammars.event.Attribute;
import com.siemens.ct.exi.grammars.event.EventType;
import com.siemens.ct.exi.grammars.event.StartElement;
import com.siemens.ct.exi.grammars.grammar.Grammar;
import com.siemens.ct.exi.grammars.production.Production;
import com.siemens.ct.exi.types.BuiltInType;

public class ElementFragmentGrammarTest extends TestCase {
	String schema;

	public static Grammars getGrammarFromSchemaAsString(String schemaAsString)
			throws Exception {
		ByteArrayInputStream bais = new ByteArrayInputStream(
				schemaAsString.getBytes());
		GrammarFactory grammarFactory = GrammarFactory.newInstance();
		Grammars grammar = grammarFactory.createGrammars(bais);

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
				+ "  </xs:complexType>" + " </xs:element>"
				+ " <xs:attribute name='c' type='xs:int'/>" + "</xs:schema>";

		Grammars g = getGrammarFromSchemaAsString(schema);
		Grammar r = g.getFragmentGrammar();

		Production ei = r.getProduction(EventType.START_DOCUMENT);
		Production ei2 = ei.getNextGrammar().getStartElementProduction("",
				"root");
		assertTrue(ei2.getEvent().isEventType(EventType.START_ELEMENT));
		StartElement seRoot = (StartElement) ei2.getEvent();
		Grammar rRoot = seRoot.getGrammar();

		Production eiAtC = rRoot.getAttributeProduction("", "c");
		assertTrue(eiAtC.getEvent().isEventType(EventType.ATTRIBUTE));
		Attribute atC = (Attribute) eiAtC.getEvent();
		assertTrue(atC.getDatatype().getBuiltInType() == BuiltInType.INTEGER);

		Production eiAtD = rRoot.getAttributeProduction("", "d");
		assertTrue(eiAtD.getEvent().isEventType(EventType.ATTRIBUTE));
		Attribute atD = (Attribute) eiAtD.getEvent();
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
				+ "  </xs:complexType>" + " </xs:element>"
				+ " <xs:attribute name='c' type='xs:date'/>" + "</xs:schema>";

		Grammars g = getGrammarFromSchemaAsString(schema);
		Grammar r = g.getFragmentGrammar();

		Production ei = r.getProduction(EventType.START_DOCUMENT);
		Production ei2 = ei.getNextGrammar().getStartElementProduction("",
				"root");
		assertTrue(ei2.getEvent().isEventType(EventType.START_ELEMENT));
		StartElement seRoot = (StartElement) ei2.getEvent();
		Grammar rRoot = seRoot.getGrammar();

		Production eiAtC = rRoot.getAttributeProduction("", "c");
		assertTrue(eiAtC.getEvent().isEventType(EventType.ATTRIBUTE));
		Attribute atC = (Attribute) eiAtC.getEvent();
		assertTrue(atC.getDatatype().getBuiltInType() == BuiltInType.STRING);

		Production eiAtD = rRoot.getAttributeProduction("", "d");
		assertTrue(eiAtD.getEvent().isEventType(EventType.ATTRIBUTE));
		Attribute atD = (Attribute) eiAtD.getEvent();
		assertTrue(atD.getDatatype().getBuiltInType() == BuiltInType.INTEGER);
	}

}
