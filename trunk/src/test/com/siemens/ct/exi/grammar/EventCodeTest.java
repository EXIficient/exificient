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

import com.siemens.ct.exi.FidelityOptions;
import com.siemens.ct.exi.GrammarFactory;
import com.siemens.ct.exi.grammar.event.Attribute;
import com.siemens.ct.exi.grammar.event.EventType;
import com.siemens.ct.exi.grammar.event.StartElement;
import com.siemens.ct.exi.grammar.rule.Rule;

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
		
		// Rule r = g.getNamedElement("", "optional").getUniqueRule();
		Rule rRoot = g.getGlobalElement(new QName("", "root")).getRule();
		StartElement seOptional = (StartElement) rRoot.lookForStartElement("", "optional").event;
		Rule rOptional = seOptional.getRule();

		// Sequence: atA, atB, SE(f), SE(e), SE(d), SE(c), SE(b), EE
		// Note: SE(a) missing
		assertTrue(rOptional.getNumberOfEvents() == 8);

		int eventCode = 0;

		// AT( atA )
		assertTrue(rOptional.lookForAttribute("", "atA").getEventCode() == eventCode++);
		// AT( atB )
		assertTrue(rOptional.lookForAttribute("", "atB").getEventCode() == eventCode++);
		// SE( f )
		assertTrue(rOptional.lookForStartElement("", "f").getEventCode() == eventCode++);
		// SE( e )
		assertTrue(rOptional.lookForStartElement("", "e").getEventCode() == eventCode++);
		// SE( d )
		assertTrue(rOptional.lookForStartElement("", "d").getEventCode() == eventCode++);
		// SE( c )
		assertTrue(rOptional.lookForStartElement("", "c").getEventCode() == eventCode++);
		// SE( b )
		assertTrue(rOptional.lookForStartElement("", "b").getEventCode() == eventCode++);
		// EE
		assertTrue(rOptional.lookForEvent(EventType.END_ELEMENT).getEventCode() == eventCode++);
		// Unknown
		assertTrue(rOptional.lookForStartElement("", "unknown") == null);
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
		Rule Use_color_0 = g.getGlobalElement(new QName("", "product")).getRule();
		// assertTrue(g.isGlobalElement("", "product"));
		assertTrue(g.getGlobalElement(new QName("", "product")) != null);
		
		// default fidelity options
		FidelityOptions fo = FidelityOptions.createDefault();

		// ### Use_color_0 ###
		// 1st level
		assertTrue(Use_color_0.getNumberOfEvents() == 2);
		// AT( color )
		assertTrue(Use_color_0.lookForAttribute("", "color").getEventCode() == 0);
		// AT( sku )
		assertTrue(Use_color_0.lookForAttribute("", "sku").getEventCode() == 1);
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
		Rule Use_color_1 = Use_color_0.lookFor(0).next;
		// 1st level
		assertTrue(Use_color_1.getNumberOfEvents() == 1);
		// AT( sku )
		assertTrue(Use_color_1.lookForAttribute("", "sku").getEventCode() == 0);

		// ### Use_sku_1 ###
		Rule Use_sku_1 = Use_color_1.lookFor(0).next;
		// 1st level
		assertTrue(Use_sku_1.getNumberOfEvents() == 2);
		// SE( description )
		assertTrue(Use_sku_1.lookForStartElement("", "description").getEventCode() == 0);
		// SE( quantity )
		assertTrue(Use_sku_1.lookForStartElement("", "quantity").getEventCode() == 1);

		// ### Term_description0_1 ###

		// ### Term_quantity0_1 ###

		// ### Term_price0_1 ###

		// ### Term_description1_1 ###

		// ### Term_quantity1_1 ###

		// ### Term_price1_1 ###

		// ### Term_product0_0 ###

		// ### Term_product1_0 ###
	}

	public void testAttributeWildcard() throws Exception {
		
		/*
		 * AT/SE sequence having required items AT(a), AT(b), SE(X) PLUS
		 * an attribute wildcard.
		 * 
		 * 
		 * 
		 * G_00:                        G_01:
		 *       AT(a) G_01                   EE
		 *       AT(*) G_00
		 *       
		 * G_10:                        G_11:
		 *       AT(b) G_11                   EE
		 *       AT(*) G_10
		 *       
		 * G_20:
		 * 	    AT(*) G_20
		 *      EE
		 *      
		 * G_30:                        G_31:
		 *      SE(X) G_31                   EE
		 *      
		 *      
		 * Then, when we compute G_0 + G_1 + G_2 + G_3, the concatenation operator
		 * replaces all the EE non-terminals of productions in the first 3 grammars
		 * with references to the "next" grammar as follows:
		 * 
		 * G_00:                        G_01:
		 *       AT(a) G_01                   G_10
		 *       AT(*) G_00
		 *       
		 *  G_10:                        G_11:
		 *        AT(b) G_11                   G_20
		 *        AT(*) G_10
		 *        
		 * G_20:
		 *        AT(*) G_20
		 *        G_30
		 *        
		 * G_30:                        G_31
		 *        SE(X) G_31                   EE
		 *  
		 *  
		 *  Normalization replaces all the productions that do not have a terminal
		 *  symbol, yielding this:
		 *  
		 *  G_00:                        
		 *        AT(a) G_10                   
		 *        AT(*) G_00
		 *        
		 * G_10:                       
		 *       AT(b) G_20               
		 *       AT(*) G_10
		 * 
		 * G_20:
		 *       AT(*) G_20
		 *       SE(X) G_31
		 * 
		 * G_30:                        
		 *      SE(X) G_31 
		 * 
		 * G_31:
		 *      EE
		 */
	
		schema = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
				+ " <xs:element name='root'>"
				+ "  <xs:complexType>"
				+ "   <xs:sequence >"
				+ "    <xs:element name='X' type='xs:string'  /> "
				+ "   </xs:sequence>"
				+ "   <xs:attribute name='b' type='xs:string' use='required' />   "
				+ "   <xs:attribute name='a' type='xs:string' use='required' />   "
				+ "   <xs:anyAttribute processContents='lax' namespace='##any'/>"
				+ "  </xs:complexType>" + " </xs:element>" + ""
				+ "</xs:schema>";
	
		Grammar g = getGrammarFromSchemaAsString(schema);
		Rule G_00 = g.getGlobalElement(new QName("", "root")).getRule();
		
		/*
		 *  G_00:                        
		 *        AT(a) G_10                   
		 *        AT(*) G_00
		 */
		assertTrue(G_00.getNumberOfEvents() == 2);
		assertTrue(G_00.lookFor(0).event.isEventType(EventType.ATTRIBUTE));
		Attribute atA = (Attribute) G_00.lookFor(0).event;
		assertTrue(atA.getQName().getLocalPart().equals("a"));
		assertTrue(G_00.lookFor(1).event.isEventType(EventType.ATTRIBUTE_GENERIC));
		assertTrue(G_00.lookFor(1).next == G_00);
		Rule G_10 = G_00.lookFor(0).next;
		
		/*
		 * 	G_10:                       
		 *       AT(b) G_20               
		 *       AT(*) G_10
		 */
		assertTrue(G_10.getNumberOfEvents() == 2);
		assertTrue(G_10.lookFor(0).event.isEventType(EventType.ATTRIBUTE));
		Attribute atB = (Attribute) G_10.lookFor(0).event;
		assertTrue(atB.getQName().getLocalPart().equals("b"));
		assertTrue(G_10.lookFor(1).event.isEventType(EventType.ATTRIBUTE_GENERIC));
		assertTrue(G_10.lookFor(1).next == G_10);
		Rule G_20 = G_10.lookFor(0).next;
		
		/*
		 * G_20:
		 *       AT(*) G_20
		 *       SE(X) G_31
		 */
		assertTrue(G_20.getNumberOfEvents() == 2);
		assertTrue(G_20.lookFor(0).event.isEventType(EventType.ATTRIBUTE_GENERIC));
		assertTrue(G_20.lookFor(0).next == G_20);
		assertTrue(G_20.lookFor(1).event.isEventType(EventType.START_ELEMENT));
		StartElement seX = (StartElement) G_20.lookFor(1).event;
		assertTrue(seX.getQName().getLocalPart().equals("X"));
		Rule G_31 = G_20.lookFor(1).next;
		
		/*
		 * G_31:
		 *      EE
		 */
		assertTrue(G_31.getNumberOfEvents() == 1);
		assertTrue(G_31.lookFor(0).event.isEventType(EventType.END_ELEMENT));
	}

}
