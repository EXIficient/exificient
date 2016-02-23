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
import com.siemens.ct.exi.context.GrammarContext;
import com.siemens.ct.exi.grammars.event.EventType;
import com.siemens.ct.exi.grammars.event.StartElement;
import com.siemens.ct.exi.grammars.grammar.Fragment;
import com.siemens.ct.exi.grammars.grammar.Grammar;
import com.siemens.ct.exi.grammars.grammar.SchemaInformedFirstStartTagGrammar;
import com.siemens.ct.exi.grammars.production.Production;

public class TypeEmptyTest extends TestCase {
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
		
		// TODO fragment grammar
		Grammar fragGr = g.getFragmentGrammar();
		assertTrue(fragGr instanceof Fragment);
		@SuppressWarnings("unused")
		Fragment frag = (Fragment) fragGr;
		g.getDocumentGrammar();
		
		// complexType root
		GrammarContext gc = g.getGrammarContext();
		Grammar root = gc.getGrammarUriContext("").getQNameContext("root")
				.getGlobalStartElement().getGrammar();
		
		assertTrue(root instanceof SchemaInformedFirstStartTagGrammar);
		{
			SchemaInformedFirstStartTagGrammar sifstgRoot = (SchemaInformedFirstStartTagGrammar) root;
			SchemaInformedFirstStartTagGrammar sifstgRootEmpty = sifstgRoot.getTypeEmpty();
			assertTrue(sifstgRootEmpty instanceof SchemaInformedFirstStartTagGrammar);
			assertTrue(sifstgRootEmpty.getNumberOfEvents() == 1);
			Production er0 = sifstgRootEmpty.getProduction(0);
			assertTrue(er0.getEvent().isEventType(EventType.END_ELEMENT));
		}

		
		// simpleType a
		Grammar a = ((StartElement)root.getProduction(0).getEvent()).getGrammar();
		{
			assertTrue(a instanceof SchemaInformedFirstStartTagGrammar);
			SchemaInformedFirstStartTagGrammar sifstgA = (SchemaInformedFirstStartTagGrammar) a;
			SchemaInformedFirstStartTagGrammar sifstgAEmpty = sifstgA.getTypeEmpty();
			assertTrue(sifstgAEmpty instanceof SchemaInformedFirstStartTagGrammar);
			// System.out.println(sifstgAEmpty.getGrammarType());
			assertTrue(sifstgAEmpty.getNumberOfEvents() == 1);
			Production er0 = sifstgAEmpty.getProduction(0);
			assertTrue(er0.getEvent().isEventType(EventType.END_ELEMENT));
		}
	}

	
	public void testSequence2() throws Exception {
		schema = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ " <xs:element name='root'>" + "  <xs:complexType>"
				+ "   <xs:sequence >"
				+ "    <xs:element name='a' type='xs:string' /> "
				+ "    <xs:element name='b' type='xs:string' /> "
				+ "   </xs:sequence>"
				+ "   <xs:attribute name='atX'/>"
				+ "   <xs:attribute name='atY'/>"
				+ "  </xs:complexType>"
				+ " </xs:element>" + "</xs:schema>";

		Grammars g = getGrammarFromSchemaAsString(schema);
		
		// TODO fragment grammar
		Grammar fragGr = g.getFragmentGrammar();
		assertTrue(fragGr instanceof Fragment);
		@SuppressWarnings("unused")
		Fragment frag = (Fragment) fragGr;
		g.getDocumentGrammar();
		
		// complexType root
		GrammarContext gc = g.getGrammarContext();
		Grammar root = gc.getGrammarUriContext("").getQNameContext("root")
				.getGlobalStartElement().getGrammar();
		
		assertTrue(root instanceof SchemaInformedFirstStartTagGrammar);
		{
			SchemaInformedFirstStartTagGrammar sifstgRoot = (SchemaInformedFirstStartTagGrammar) root;
			SchemaInformedFirstStartTagGrammar sifstgRootEmpty = sifstgRoot.getTypeEmpty();
			assertTrue(sifstgRootEmpty instanceof SchemaInformedFirstStartTagGrammar);
			assertTrue(sifstgRootEmpty.getNumberOfEvents() == 3);
			assertTrue(sifstgRootEmpty.getProduction(0).getEvent().isEventType(EventType.ATTRIBUTE));
			{
				Grammar g1 = sifstgRootEmpty.getProduction(0).getNextGrammar();
				assertTrue(g1.getNumberOfEvents() == 2);
				assertTrue(g1.getProduction(0).getEvent().isEventType(EventType.ATTRIBUTE));
				{
					Grammar g11 = g1.getProduction(0).getNextGrammar();
					assertTrue(g11.getNumberOfEvents() == 1);
					assertTrue(g11.getProduction(0).getEvent().isEventType(EventType.END_ELEMENT));
				}
				assertTrue(g1.getProduction(1).getEvent().isEventType(EventType.END_ELEMENT));
				
			}
			assertTrue(sifstgRootEmpty.getProduction(1).getEvent().isEventType(EventType.ATTRIBUTE));
			assertTrue(sifstgRootEmpty.getProduction(2).getEvent().isEventType(EventType.END_ELEMENT));
		}

		
		// simpleType a
		Grammar a = ((StartElement)root.getProduction(1).getNextGrammar().getProduction(0).getEvent()).getGrammar();
		{
			assertTrue(a instanceof SchemaInformedFirstStartTagGrammar);
			SchemaInformedFirstStartTagGrammar sifstgA = (SchemaInformedFirstStartTagGrammar) a;
			SchemaInformedFirstStartTagGrammar sifstgAEmpty = sifstgA.getTypeEmpty();
			assertTrue(sifstgAEmpty instanceof SchemaInformedFirstStartTagGrammar);
			assertTrue(sifstgAEmpty.getNumberOfEvents() == 1);
			Production er0 = sifstgAEmpty.getProduction(0);
			assertTrue(er0.getEvent().isEventType(EventType.END_ELEMENT));
		}
	}



}
