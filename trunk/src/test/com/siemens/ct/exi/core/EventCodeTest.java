/*
 * Copyright (C) 2007, 2008 Siemens AG
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

package com.siemens.ct.exi.core;

import java.io.ByteArrayInputStream;

import javax.xml.XMLConstants;

import junit.framework.TestCase;

import com.siemens.ct.exi.Constants;
import com.siemens.ct.exi.FidelityOptions;
import com.siemens.ct.exi.GrammarFactory;
import com.siemens.ct.exi.grammar.ElementKey;
import com.siemens.ct.exi.grammar.Grammar;
import com.siemens.ct.exi.grammar.event.Attribute;
import com.siemens.ct.exi.grammar.event.EndElement;
import com.siemens.ct.exi.grammar.event.Event;
import com.siemens.ct.exi.grammar.event.EventType;
import com.siemens.ct.exi.grammar.event.StartElement;
import com.siemens.ct.exi.grammar.rule.Rule;
import com.siemens.ct.exi.util.ExpandedName;

public class EventCodeTest extends TestCase {
	String schema;

	private Grammar getGrammarFromSchemaAsString(String schemaAsString)
			throws Exception {
		ByteArrayInputStream bais = new ByteArrayInputStream(schemaAsString
				.getBytes());
		GrammarFactory grammarFactory = GrammarFactory.newInstance();
		Grammar grammar = grammarFactory.createGrammar(bais);

		return grammar;
	}

	// Sort all productions with G i, j on the left hand side in the following
	// order:
	// 
	// 1. All productions with AT(qname) on the right hand side sorted lexically
	// by qname localName, then by qname uri, followed by
	// 2. any production with AT(*) on the right hand side, followed by
	// 3. all productions with SE(qname) on the right hand side sorted in schema
	// order, followed by
	// 4. any production with EE on the right hand side, followed by
	// 5. any production with CH on the right hand side.
	public void testEventCodeOrder() throws Exception {
		schema = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ " <xs:element name='root'>"
				+ "  <xs:complexType>"
				+ "   <xs:sequence>"
				+ "    <xs:element name='optional' type='Optional' minOccurs='0' maxOccurs='unbounded'/> "
				+ "   </xs:sequence>" + "  </xs:complexType>"
				+ " </xs:element>" + "" + " <xs:complexType name='Optional'>"
				+ "  <xs:sequence> "
				+ "   <xs:element name='f' minOccurs='0' />"
				+ "   <xs:element name='e' minOccurs='0' maxOccurs='3' />"
				+ "   <xs:choice minOccurs='0'>"
				+ "    <xs:element name='d' />" + "    <xs:element name='c' />"
				+ "    <xs:sequence minOccurs='0' > "
				+ "     <xs:element name='b' /> "
				+ "     <xs:element name='a' />" + "    </xs:sequence>"
				+ "   </xs:choice>" + "  </xs:sequence>"
				+ "  <xs:attribute name='atB'/>"
				+ "  <xs:attribute name='atA'/> " + "  </xs:complexType>"
				+ "</xs:schema>";

		Grammar g = getGrammarFromSchemaAsString(schema);

		ElementKey elKey = new ElementKey(new ExpandedName("", "optional"));
		Rule r = g.getRule(elKey);

		// Sequence: atA, atB, SE(f), SE(e), SE(d), SE(c), SE(b), EE
		// Note: SE(a) missing
		assertTrue(r.getNumberOfEvents() == 8);

		Event ev;
		int eventCode = 0;

		// AT( atA )
		ev = new Attribute(XMLConstants.NULL_NS_URI, "atA");
		assertTrue(r.get1stLevelEventCode(ev) == eventCode++);

		// AT( atB )
		ev = new Attribute(XMLConstants.NULL_NS_URI, "atB");
		assertTrue(r.get1stLevelEventCode(ev) == eventCode++);

		// SE( f )
		ev = new StartElement(XMLConstants.NULL_NS_URI, "f");
		assertTrue(r.get1stLevelEventCode(ev) == eventCode++);

		// SE( e )
		ev = new StartElement(XMLConstants.NULL_NS_URI, "e");
		assertTrue(r.get1stLevelEventCode(ev) == eventCode++);

		// SE( d )
		ev = new StartElement(XMLConstants.NULL_NS_URI, "d");
		assertTrue(r.get1stLevelEventCode(ev) == eventCode++);

		// SE( c )
		ev = new StartElement(XMLConstants.NULL_NS_URI, "c");
		assertTrue(r.get1stLevelEventCode(ev) == eventCode++);

		// SE( b )
		ev = new StartElement(XMLConstants.NULL_NS_URI, "b");
		assertTrue(r.get1stLevelEventCode(ev) == eventCode++);

		// EE
		ev = new EndElement();
		assertTrue(r.get1stLevelEventCode(ev) == eventCode++);

		// Unknown
		ev = new StartElement(XMLConstants.NULL_NS_URI, "unknown");
		assertTrue(r.get1stLevelEventCode(ev) == Constants.NOT_FOUND);

	}

	public void testEventCodeEXISpecExample() throws Exception {
		// http://www.w3.org/XML/Group/EXI/docs/format/exi.html#example

		schema = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ " <xs:element name='product'>"
				+ "  <xs:complexType>"
				+ "   <xs:sequence maxOccurs='2'>"
				+ "    <xs:element name='description' type='xs:string' minOccurs='0'/> "
				+ "    <xs:element name='quantity' type='xs:integer' /> "
				+ "    <xs:element name='price' type='xs:float' />  "
				+ "   </xs:sequence>"
				+ "   <xs:attribute name='sku' type='xs:string' use='required' />   "
				+ "   <xs:attribute name='color' type='xs:string' use='optional' />   "
				+ "  </xs:complexType>" + " </xs:element>" + ""
				+ " <xs:element name='order'>" + "  <xs:complexType> "
				+ "   <xs:sequence> "
				+ "     <xs:element ref='product' maxOccurs='unbounded' /> "
				+ "   </xs:sequence>" + "  </xs:complexType>"
				+ " </xs:element>" + "</xs:schema>";

		Grammar g = getGrammarFromSchemaAsString(schema);

		ElementKey elKey = new ElementKey(new ExpandedName("", "product"));
		Rule Use_color_0 = g.getRule(elKey);

		// default fidelity options
		FidelityOptions fo = FidelityOptions.createDefault();

		Event ev;

		// ### Use_color_0 ###
		// 1st level
		assertTrue(Use_color_0.getNumberOfEvents() == 2);
		// AT( color )
		ev = new Attribute(XMLConstants.NULL_NS_URI, "color");
		assertTrue(Use_color_0.get1stLevelEventCode(ev) == 0);
		// AT( sku )
		ev = new Attribute(XMLConstants.NULL_NS_URI, "sku");
		assertTrue(Use_color_0.get1stLevelEventCode(ev) == 1);
		// 2nd level
		assertTrue(Use_color_0.get2ndLevelCharacteristics(fo) == 7);
		// EE 2.0
		assertTrue(Use_color_0.get2ndLevelEventCode(
				EventType.END_ELEMENT_UNDECLARED, fo) == 0);
		// AT(xsi:type) Use_color 0 2.1
		assertTrue(Use_color_0.get2ndLevelEventCode(
				EventType.ATTRIBUTE_XSI_TYPE, fo) == 1);
		// AT(xsi:nil) Use_color 0 2.2
		assertTrue(Use_color_0.get2ndLevelEventCode(
				EventType.ATTRIBUTE_XSI_NIL, fo) == 2);
		// AT(*) Use_color 0 2.3
		assertTrue(Use_color_0.get2ndLevelEventCode(
				EventType.ATTRIBUTE_GENERIC_UNDECLARED, fo) == 3);
		// TODO schema invalid value
		// AT [schema-invalid value] Use_color 0 2.4.x
		// assertTrue( Use_color_0.get2ndLevelEventCode (
		// EventType.ATTRIBUTE_INVALID_VALUE, fo ) == 4 );
		// AT("color") [schema-invalid value] Use_color 0 2.4.0
		// AT("sku") [schema-invalid value] Use_color 0 2.4.1
		// AT(*) [schema-invalid value] Use_color 0 2.4.2
		// SE(*) Use_sku 1 2.5
		assertTrue(Use_color_0.get2ndLevelEventCode(
				EventType.START_ELEMENT_GENERIC_UNDECLARED, fo) == 5);
		// CH [schema-invalid value] Use_sku 1 2.6
		assertTrue(Use_color_0.get2ndLevelEventCode(
				EventType.CHARACTERS_GENERIC_UNDECLARED, fo) == 6);

		// ### Use_color_1 ###
		Rule Use_color_1 = Use_color_0.get1stLevelRule(0);
		// 1st level
		assertTrue(Use_color_1.getNumberOfEvents() == 1);
		// AT( sku )
		ev = new Attribute(XMLConstants.NULL_NS_URI, "sku");
		assertTrue(Use_color_1.get1stLevelEventCode(ev) == 0);

		// ### Use_sku_1 ###
		Rule Use_sku_1 = Use_color_1.get1stLevelRule(0);
		// 1st level
		assertTrue(Use_sku_1.getNumberOfEvents() == 2);
		// SE( description )
		ev = new StartElement(XMLConstants.NULL_NS_URI, "description");
		assertTrue(Use_sku_1.get1stLevelEventCode(ev) == 0);
		// SE( quantity )
		ev = new StartElement(XMLConstants.NULL_NS_URI, "quantity");
		assertTrue(Use_sku_1.get1stLevelEventCode(ev) == 1);

		// ### Term_description0_1 ###

		// ### Term_quantity0_1 ###

		// ### Term_price0_1 ###

		// ### Term_description1_1 ###

		// ### Term_quantity1_1 ###

		// ### Term_price1_1 ###

		// ### Term_product0_0 ###

		// ### Term_product1_0 ###
	}

}
