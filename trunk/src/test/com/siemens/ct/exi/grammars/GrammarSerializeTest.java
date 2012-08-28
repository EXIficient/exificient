/*
 * Copyright (C) 2007-2012 Siemens AG
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
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import junit.framework.TestCase;

import com.siemens.ct.exi.GrammarFactory;
import com.siemens.ct.exi.context.GrammarContext;
import com.siemens.ct.exi.grammars.event.EventType;
import com.siemens.ct.exi.grammars.event.StartElement;
import com.siemens.ct.exi.grammars.grammar.Grammar;
import com.siemens.ct.exi.grammars.production.Production;

public class GrammarSerializeTest extends TestCase {
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
		assertTrue(g.isSchemaInformed());
		
		SchemaInformedGrammars sig1 = (SchemaInformedGrammars) g;
		
		// Serialize 
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(sig1);
		oos.flush();
		oos.close();
		
		// Deserialize
		ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
		Object o = ois.readObject();
		ois.close();
		assertTrue(o instanceof SchemaInformedGrammars);
		SchemaInformedGrammars sig2 = (SchemaInformedGrammars)o;
				
		GrammarContext gc = sig2.getGrammarContext();

		Grammar root = gc.getGrammarUriContext("").getQNameContext("root").getGlobalStartElement().getGrammar();

		// SE(a)
		assertTrue(root.getNumberOfEvents() == 1);
		Production er0 = root.getProduction(0);
		assertTrue(er0.getEvent().isEventType(EventType.START_ELEMENT));
		assertTrue(((StartElement) er0.getEvent()).getQName().getLocalPart()
				.equals("a"));

		Grammar a = er0.getNextGrammar();
		// SE(b)
		assertTrue(a.getNumberOfEvents() == 1);
		Production er1 = a.getProduction(0);
		assertTrue(er1.getEvent().isEventType(EventType.START_ELEMENT));
		assertTrue(((StartElement) er1.getEvent()).getQName().getLocalPart()
				.equals("b"));

		Grammar b = er1.getNextGrammar();
		// SE(b)
		assertTrue(b.getNumberOfEvents() == 1);
		Production er2 = b.getProduction(0);
		assertTrue(er2.getEvent().isEventType(EventType.END_ELEMENT));
	}

	
	
}
