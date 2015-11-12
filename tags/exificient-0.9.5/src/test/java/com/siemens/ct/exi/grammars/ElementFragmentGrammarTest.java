/*
 * Copyright (c) 2007-2015 Siemens AG
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
