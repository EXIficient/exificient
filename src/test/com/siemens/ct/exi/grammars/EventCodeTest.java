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

import junit.framework.TestCase;

import com.siemens.ct.exi.FidelityOptions;
import com.siemens.ct.exi.GrammarFactory;
import com.siemens.ct.exi.context.GrammarContext;
import com.siemens.ct.exi.grammars.event.Attribute;
import com.siemens.ct.exi.grammars.event.EventType;
import com.siemens.ct.exi.grammars.event.StartElement;
import com.siemens.ct.exi.grammars.grammar.Grammar;
import com.siemens.ct.exi.grammars.grammar.SchemaInformedElement;
import com.siemens.ct.exi.grammars.grammar.SchemaInformedFirstStartTag;
import com.siemens.ct.exi.grammars.grammar.SchemaInformedStartTag;
import com.siemens.ct.exi.grammars.production.Production;

public class EventCodeTest extends TestCase {
	String schema;

	private Grammars getGrammarFromSchemaAsString(String schemaAsString)
			throws Exception {
		ByteArrayInputStream bais = new ByteArrayInputStream(schemaAsString
				.getBytes());
		GrammarFactory grammarFactory = GrammarFactory.newInstance();
		Grammars grammar = grammarFactory.createGrammars(bais);

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

		Grammars g = getGrammarFromSchemaAsString(schema);
		// GrammarURIEntry[] gues = g.getGrammarEntries();
		GrammarContext gc = g.getGrammarContext();
		
		// Rule r = g.getNamedElement("", "optional").getUniqueRule();
		// Rule rRoot = g.getGlobalElement(new QName("", "root")).getRule();
		Grammar rRoot = gc.getGrammarUriContext(0).getQNameContext("root").getGlobalStartElement().getGrammar();
		// Rule rRoot = g.getGlobalElement(XSDGrammarBuilder.getEfficientQName(gues, "", "root")).getRule();
		StartElement seOptional = (StartElement) rRoot.getStartElementProduction("", "optional").getEvent();
		Grammar rOptional = seOptional.getGrammar();

		// Sequence: atA, atB, SE(f), SE(e), SE(d), SE(c), SE(b), EE
		// Note: SE(a) missing
		assertTrue(rOptional.getNumberOfEvents() == 8);

		int eventCode = 0;

		// AT( atA )
		assertTrue(rOptional.getAttributeProduction("", "atA").getEventCode() == eventCode++);
		// AT( atB )
		assertTrue(rOptional.getAttributeProduction("", "atB").getEventCode() == eventCode++);
		// SE( f )
		assertTrue(rOptional.getStartElementProduction("", "f").getEventCode() == eventCode++);
		// SE( e )
		assertTrue(rOptional.getStartElementProduction("", "e").getEventCode() == eventCode++);
		// SE( d )
		assertTrue(rOptional.getStartElementProduction("", "d").getEventCode() == eventCode++);
		// SE( c )
		assertTrue(rOptional.getStartElementProduction("", "c").getEventCode() == eventCode++);
		// SE( b )
		assertTrue(rOptional.getStartElementProduction("", "b").getEventCode() == eventCode++);
		// EE
		assertTrue(rOptional.getProduction(EventType.END_ELEMENT).getEventCode() == eventCode++);
		// Unknown
		assertTrue(rOptional.getStartElementProduction("", "unknown") == null);
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

		Grammars g = getGrammarFromSchemaAsString(schema);
		GrammarContext gc = g.getGrammarContext();
		
		// Rule Use_color_0 = g.getGlobalElement(new QName("", "product")).getRule();
		Grammar Use_color_0 = gc.getGrammarUriContext(0).getQNameContext("product").getGlobalStartElement().getGrammar();
		// Rule Use_color_0 = g.getGlobalElement(XSDGrammarBuilder.getEfficientQName(gues, "", "product")).getRule();
		
		// assertTrue(g.isGlobalElement("", "product"));
		// assertTrue(g.getGlobalElement(new QName("", "product")) != null);
		assertTrue(gc.getGrammarUriContext(0).getQNameContext("product").getGlobalStartElement() != null);
		// assertTrue(g.getGlobalElement(XSDGrammarBuilder.getEfficientQName(gues, "", "product")) != null);
		
		// default fidelity options
		FidelityOptions fo = FidelityOptions.createDefault();

		// ### Use_color_0 ###
		// 1st level
		assertTrue(Use_color_0.getNumberOfEvents() == 2);
		// AT( color )
		assertTrue(Use_color_0.getAttributeProduction("", "color").getEventCode() == 0);
		// AT( sku )
		assertTrue(Use_color_0.getAttributeProduction("", "sku").getEventCode() == 1);
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
		Grammar Use_color_1 = Use_color_0.getProduction(0).getNextGrammar();
		// 1st level
		assertTrue(Use_color_1.getNumberOfEvents() == 1);
		// AT( sku )
		assertTrue(Use_color_1.getAttributeProduction("", "sku").getEventCode() == 0);

		// ### Use_sku_1 ###
		Grammar Use_sku_1 = Use_color_1.getProduction(0).getNextGrammar();
		// 1st level
		assertTrue(Use_sku_1.getNumberOfEvents() == 2);
		// SE( description )
		assertTrue(Use_sku_1.getStartElementProduction("", "description").getEventCode() == 0);
		// SE( quantity )
		assertTrue(Use_sku_1.getStartElementProduction("", "quantity").getEventCode() == 1);

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
	
		Grammars g = getGrammarFromSchemaAsString(schema);
		GrammarContext gc = g.getGrammarContext();
		
		// GrammarURIEntry[] gues = g.getGrammarEntries();
		// Rule G_00 = g.getGlobalElement(new QName("", "root")).getRule();
		Grammar G_00 = gc.getGrammarUriContext(0).getQNameContext("root").getGlobalStartElement().getGrammar();
		// Rule G_00 = g.getGlobalElement(XSDGrammarBuilder.getEfficientQName(gues, "", "root")).getRule();
		
		
		
		/*
		 *  G_00:                        
		 *        AT(a) G_10                   
		 *        AT(*) G_00
		 */
		assertTrue(G_00.getNumberOfEvents() == 2);
		assertTrue(G_00.getProduction(0).getEvent().isEventType(EventType.ATTRIBUTE));
		Attribute atA = (Attribute) G_00.getProduction(0).getEvent();
		assertTrue(atA.getQName().getLocalPart().equals("a"));
		assertTrue(G_00.getProduction(1).getEvent().isEventType(EventType.ATTRIBUTE_GENERIC));
		assertTrue(G_00.getProduction(1).getNextGrammar() == G_00);
		Grammar G_10 = G_00.getProduction(0).getNextGrammar();
		
		/*
		 * 	G_10:                       
		 *       AT(b) G_20               
		 *       AT(*) G_10
		 */
		assertTrue(G_10.getNumberOfEvents() == 2);
		assertTrue(G_10.getProduction(0).getEvent().isEventType(EventType.ATTRIBUTE));
		Attribute atB = (Attribute) G_10.getProduction(0).getEvent();
		assertTrue(atB.getQName().getLocalPart().equals("b"));
		assertTrue(G_10.getProduction(1).getEvent().isEventType(EventType.ATTRIBUTE_GENERIC));
		assertTrue(G_10.getProduction(1).getNextGrammar() == G_10);
		Grammar G_20 = G_10.getProduction(0).getNextGrammar();
		
		/*
		 * G_20:
		 *       AT(*) G_20
		 *       SE(X) G_31
		 */
		assertTrue(G_20.getNumberOfEvents() == 2);
		assertTrue(G_20.getProduction(0).getEvent().isEventType(EventType.ATTRIBUTE_GENERIC));
		assertTrue(G_20.getProduction(0).getNextGrammar() == G_20);
		assertTrue(G_20.getProduction(1).getEvent().isEventType(EventType.START_ELEMENT));
		StartElement seX = (StartElement) G_20.getProduction(1).getEvent();
		assertTrue(seX.getQName().getLocalPart().equals("X"));
		Grammar G_31 = G_20.getProduction(1).getNextGrammar();
		
		/*
		 * G_31:
		 *      EE
		 */
		assertTrue(G_31.getNumberOfEvents() == 1);
		assertTrue(G_31.getProduction(0).getEvent().isEventType(EventType.END_ELEMENT));
	}
	
	
	public void testBuiltInDocumentGrammar() {
		Grammars g = GrammarFactory.newInstance().createSchemaLessGrammars();
		
		Grammar document = g.getDocumentGrammar();
		/*
		 * Document :
		 * SD DocContent	0
		 */
		assertTrue(document.getNumberOfEvents() == 1);
		Production eiSD =  document.getProduction(0);
		assertTrue(eiSD.getEvent().isEventType(EventType.START_DOCUMENT));
		
		Grammar docContent = eiSD.getNextGrammar();
		/*
		 * DocContent :
		 * SE (*) DocEnd	0
		 * DT DocContent	1.0
		 * CM DocContent	1.1.0
		 * PI DocContent	1.1.1
		 */
		assertTrue(docContent.getNumberOfEvents() == 1);
		Production eiSE =  docContent.getProduction(0);
		assertTrue(eiSE.getEvent().isEventType(EventType.START_ELEMENT_GENERIC));

		Grammar docEnd = eiSE.getNextGrammar();
		/*
		 * DocEnd :
		 * ED	0
		 * CM DocEnd	1.0
		 * PI DocEnd	1.1
		 */
		assertTrue(docEnd.getNumberOfEvents() == 1);
		Production ei =  docEnd.getProduction(0);
		assertTrue(ei.getEvent().isEventType(EventType.END_DOCUMENT));
	}

	public void testBuiltInFragmentGrammar() {
		Grammars g = GrammarFactory.newInstance().createSchemaLessGrammars();
		
		Grammar fragment = g.getFragmentGrammar();
		/*
		 * Fragment :
		 * SD FragmentContent	0
		 */
		assertTrue(fragment.getNumberOfEvents() == 1);
		Production eiSD =  fragment.getProduction(0);
		assertTrue(eiSD.getEvent().isEventType(EventType.START_DOCUMENT));
		
		Grammar fragmentContent = eiSD.getNextGrammar();
		/*
		 * FragmentContent :
		 * SE (*) FragmentContent	0
		 * ED	1
		 * CM FragmentContent	2.0
		 * PI FragmentContent	2.1
		 */
		assertTrue(fragmentContent.getNumberOfEvents() == 2);
		Production eiSE =  fragmentContent.getProduction(0);
		assertTrue(eiSE.getEvent().isEventType(EventType.START_ELEMENT_GENERIC));
		assertTrue(eiSE.getNextGrammar() == fragmentContent);
		Production eiED =  fragmentContent.getProduction(1);
		assertTrue(eiED.getEvent().isEventType(EventType.END_DOCUMENT));
	}

	public void testSchemaInformedDocumentGrammar() throws Exception {
		schema = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
			+ " <xs:element name='root'>"
			+ "  <xs:complexType>"
			+ "   <xs:sequence>"
			+ "    <xs:element name='optional' minOccurs='0' maxOccurs='unbounded'/> "
			+ "   </xs:sequence>" + "  </xs:complexType>"
			+ " </xs:element>"
			+ "</xs:schema>";

		Grammars g = getGrammarFromSchemaAsString(schema);
	
		Grammar document = g.getDocumentGrammar();
		/*
		 * Document :
		 * SD DocContent	0
		 */
		assertTrue(document.getNumberOfEvents() == 1);
		Production eiSD =  document.getProduction(0);
		assertTrue(eiSD.getEvent().isEventType(EventType.START_DOCUMENT));
		
		Grammar docContent = eiSD.getNextGrammar();
		/*
		 * DocContent :
		 * SE (G 0) DocEnd	0
		 * SE (G 1) DocEnd	1
		 * . . .
		 * SE (G n-1) DocEnd	n-1
		 * SE (*) DocEnd	n
		 * DT DocContent	 (n+1).0
		 * CM DocContent	 (n+1).1.0
		 * PI DocContent	 (n+1).1.1
		 */
		assertTrue(docContent.getNumberOfEvents() == 2);
		Production eiSE_root =  docContent.getProduction(0);
		assertTrue(eiSE_root.getEvent().isEventType(EventType.START_ELEMENT));
		assertTrue(((StartElement)eiSE_root.getEvent()).getQName().getLocalPart().equals("root"));
		Production eiSEG =  docContent.getProduction(1);
		assertTrue(eiSEG.getEvent().isEventType(EventType.START_ELEMENT_GENERIC));
		assertTrue(eiSE_root.getNextGrammar() == eiSEG.getNextGrammar());
		
		Grammar docEnd = eiSEG.getNextGrammar();
		/*
		 * DocEnd :
		 * ED	0
		 * CM DocEnd	1.0
		 * PI DocEnd	1.1
		 */
		assertTrue(docEnd.getNumberOfEvents() == 1);
		Production ei =  docEnd.getProduction(0);
		assertTrue(ei.getEvent().isEventType(EventType.END_DOCUMENT));
	}

	public void testSchemaInformedFragmentGrammar() throws Exception {
		schema = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
			+ " <xs:element name='root'>"
			+ "  <xs:complexType>"
			+ "   <xs:sequence>"
			+ "    <xs:element name='optional' minOccurs='0' maxOccurs='unbounded'/> "
			+ "   </xs:sequence>" + "  </xs:complexType>"
			+ " </xs:element>"
			+ "</xs:schema>";

		Grammars g = getGrammarFromSchemaAsString(schema);
		
		Grammar fragment = g.getFragmentGrammar();
		/*
		 * Fragment :
		 * SD FragmentContent	0
		 */
		assertTrue(fragment.getNumberOfEvents() == 1);
		Production eiSD =  fragment.getProduction(0);
		assertTrue(eiSD.getEvent().isEventType(EventType.START_DOCUMENT));
		
		Grammar fragmentContent = eiSD.getNextGrammar();
		/*
		 * FragmentContent :
		 * SE (F 0) FragmentContent	0
		 * SE (F 1) FragmentContent	1
		 * . . . 
		 * SE (F n-1) FragmentContent	n-1
		 * SE (*) FragmentContent	n
		 * ED	n+1
		 * CM FragmentContent	(n+2).0
		 * PI FragmentContent	(n+2).1
		 */
		assertTrue(fragmentContent.getNumberOfEvents() == 4);
		
		Production eiSE_optional =  fragmentContent.getProduction(0);
		assertTrue(eiSE_optional.getEvent().isEventType(EventType.START_ELEMENT));
		assertTrue(eiSE_optional.getNextGrammar() == fragmentContent);
		assertTrue(((StartElement)eiSE_optional.getEvent()).getQName().getLocalPart().equals("optional"));
		
		Production eiSE_root =  fragmentContent.getProduction(1);
		assertTrue(eiSE_root.getEvent().isEventType(EventType.START_ELEMENT));
		assertTrue(eiSE_root.getNextGrammar() == fragmentContent);
		assertTrue(((StartElement)eiSE_root.getEvent()).getQName().getLocalPart().equals("root"));
		
		Production eiSE =  fragmentContent.getProduction(2);
		assertTrue(eiSE.getEvent().isEventType(EventType.START_ELEMENT_GENERIC));
		assertTrue(eiSE.getNextGrammar() == fragmentContent);
		
		Production eiED =  fragmentContent.getProduction(3);
		assertTrue(eiED.getEvent().isEventType(EventType.END_DOCUMENT));
	}

	/*
	 * <xsd:complexType name="B">
	 * <xsd:sequence>
	 * <xsd:element name="AB"/>
	 * <xsd:element name="AC" minOccurs="0" maxOccurs="2"/>
	 * <xsd:element name="AD" minOccurs="0"/>
	 * </xsd:sequence>
	 * </xsd:complexType>
	 */
	public void testSchemaInformedGrammarSequence1() throws Exception {
		schema = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
			+ " <xs:element name='root'>"
			+ "  <xs:complexType>"
			+ "   <xs:sequence>"
			+ "    <xs:element name='AB'/> "
			+ "    <xs:element name='AC' minOccurs='0' maxOccurs='2'/> "
			+ "    <xs:element name='AD' minOccurs='0'/> "
			+ "   </xs:sequence>" + "  </xs:complexType>"
			+ " </xs:element>"
			+ "</xs:schema>";
	
		Grammars g = getGrammarFromSchemaAsString(schema);
		Grammar document = g.getDocumentGrammar();
		/*
		 * Document :
		 * SD DocContent	0
		 */
		assertTrue(document.getNumberOfEvents() == 1);
		Production eiSD =  document.getProduction(0);
		assertTrue(eiSD.getEvent().isEventType(EventType.START_DOCUMENT));
		
		Grammar docContent = eiSD.getNextGrammar();
		/*
		 * DocContent :
		 * SE (G 0) DocEnd	0
		 * SE (G 1) DocEnd	1
		 * . . .
		 * SE (G n-1) DocEnd	n-1
		 * SE (*) DocEnd	n
		 * DT DocContent	 (n+1).0
		 * CM DocContent	 (n+1).1.0
		 * PI DocContent	 (n+1).1.1
		 */
		assertTrue(docContent.getNumberOfEvents() == 2);
		Production eiSE_root =  docContent.getProduction(0);
		assertTrue(eiSE_root.getEvent().isEventType(EventType.START_ELEMENT));
		assertTrue(((StartElement)eiSE_root.getEvent()).getQName().getLocalPart().equals("root"));
		Production eiSEG =  docContent.getProduction(1);
		assertTrue(eiSEG.getEvent().isEventType(EventType.START_ELEMENT_GENERIC));
		assertTrue(eiSE_root.getNextGrammar() == eiSEG.getNextGrammar());
		
		Grammar docEnd = eiSEG.getNextGrammar();
		/*
		 * DocEnd :
		 * ED	0
		 * CM DocEnd	1.0
		 * PI DocEnd	1.1
		 */
		assertTrue(docEnd.getNumberOfEvents() == 1);
		Production ei =  docEnd.getProduction(0);
		assertTrue(ei.getEvent().isEventType(EventType.END_DOCUMENT));
		
		/*
		 * 1. SE(AB) 
		 */
		Grammar root1 = ((StartElement)eiSE_root.getEvent()).getGrammar();
		assertTrue(root1.getNumberOfEvents() == 1);
		assertTrue(root1.getProduction(0).getEvent().isEventType(EventType.START_ELEMENT));
		// System.out.println(root1.getNumberOfEvents());
		
		/*
		 * 2. SE(AC), SE(AD), EE
		 */
		Grammar root2 = root1.getProduction(0).getNextGrammar();
		assertTrue(root2.getNumberOfEvents() == 3);
		assertTrue(root2.getProduction(0).getEvent().isEventType(EventType.START_ELEMENT));
		assertTrue(root2.getProduction(1).getEvent().isEventType(EventType.START_ELEMENT));
		assertTrue(root2.getProduction(2).getEvent().isEventType(EventType.END_ELEMENT));
		// after SE(AD) is end
		assertTrue(root2.getProduction(1).getNextGrammar().getNumberOfEvents() == 1);
		assertTrue(root2.getProduction(1).getNextGrammar().getProduction(0).getEvent().isEventType(EventType.END_ELEMENT));
		
		/*
		 * following 1st time SE(AC)
		 * SE(AC), SE(AD), EE
		 */
		Grammar root3 = root2.getProduction(0).getNextGrammar();
		assertTrue(root3.getNumberOfEvents() == 3);
		assertTrue(root3.getProduction(0).getEvent().isEventType(EventType.START_ELEMENT));
		assertTrue(root3.getProduction(1).getEvent().isEventType(EventType.START_ELEMENT));
		assertTrue(root3.getProduction(2).getEvent().isEventType(EventType.END_ELEMENT));
		
		/*
		 * following 2nd time SE(AC)
		 * SE(AD), EE
		 */
		Grammar root4 = root3.getProduction(0).getNextGrammar();
		assertTrue(root4.getNumberOfEvents() == 2);
		
	}

	/*
	 * <xsd:complexType name="B">
	 * <xsd:sequence>
	 * <xsd:element name="AB"/>
	 * <xsd:element name="AC" minOccurs="2" maxOccurs="unbounded"/>
	 * <xsd:element name="AD" minOccurs="0"/>
	 * </xsd:sequence>
	 * </xsd:complexType>
	 */
	public void testSchemaInformedGrammarSequence2() throws Exception {
		schema = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
			+ " <xs:element name='root'>"
			+ "  <xs:complexType>"
			+ "   <xs:sequence>"
			+ "    <xs:element name='AB'/> "
			+ "    <xs:element name='AC' minOccurs='2' maxOccurs='unbounded'/> "
			+ "    <xs:element name='AD' minOccurs='0'/> "
			+ "   </xs:sequence>" + "  </xs:complexType>"
			+ " </xs:element>"
			+ "</xs:schema>";
	
		Grammars g = getGrammarFromSchemaAsString(schema);
		Grammar document = g.getDocumentGrammar();
		/*
		 * Document :
		 * SD DocContent	0
		 */
		assertTrue(document.getNumberOfEvents() == 1);
		Production eiSD =  document.getProduction(0);
		assertTrue(eiSD.getEvent().isEventType(EventType.START_DOCUMENT));
		
		Grammar docContent = eiSD.getNextGrammar();
		/*
		 * DocContent :
		 * SE (G 0) DocEnd	0
		 * SE (G 1) DocEnd	1
		 * . . .
		 * SE (G n-1) DocEnd	n-1
		 * SE (*) DocEnd	n
		 * DT DocContent	 (n+1).0
		 * CM DocContent	 (n+1).1.0
		 * PI DocContent	 (n+1).1.1
		 */
		assertTrue(docContent.getNumberOfEvents() == 2);
		Production eiSE_root =  docContent.getProduction(0);
		assertTrue(eiSE_root.getEvent().isEventType(EventType.START_ELEMENT));
		assertTrue(((StartElement)eiSE_root.getEvent()).getQName().getLocalPart().equals("root"));
		Production eiSEG =  docContent.getProduction(1);
		assertTrue(eiSEG.getEvent().isEventType(EventType.START_ELEMENT_GENERIC));
		assertTrue(eiSE_root.getNextGrammar() == eiSEG.getNextGrammar());
		
		Grammar docEnd = eiSEG.getNextGrammar();
		/*
		 * DocEnd :
		 * ED	0
		 * CM DocEnd	1.0
		 * PI DocEnd	1.1
		 */
		assertTrue(docEnd.getNumberOfEvents() == 1);
		Production ei =  docEnd.getProduction(0);
		assertTrue(ei.getEvent().isEventType(EventType.END_DOCUMENT));
		
		/*
		 * 1. SE(AB) 
		 */
		Grammar root1 = ((StartElement)eiSE_root.getEvent()).getGrammar();
		assertTrue(root1.getNumberOfEvents() == 1);
		assertTrue(root1.getProduction(0).getEvent().isEventType(EventType.START_ELEMENT));
		// System.out.println(root1.getNumberOfEvents());
		
		/*
		 * 2. SE(AC)
		 */
		Grammar root2 = root1.getProduction(0).getNextGrammar();
		assertTrue(root2.getNumberOfEvents() == 1);
		assertTrue(root2.getProduction(0).getEvent().isEventType(EventType.START_ELEMENT));
		
		/*
		 * 2. SE(AC)
		 */
		Grammar root3 = root2.getProduction(0).getNextGrammar();
		assertTrue(root3.getNumberOfEvents() == 1);
		assertTrue(root3.getProduction(0).getEvent().isEventType(EventType.START_ELEMENT));

		/*
		 * 3. SE(AC), SE(AD), EE
		 */
		Grammar root4 = root3.getProduction(0).getNextGrammar();
		assertTrue(root4.getNumberOfEvents() == 3);
		assertTrue(root4.getProduction(0).getEvent().isEventType(EventType.START_ELEMENT));
		assertTrue(root4.getProduction(1).getEvent().isEventType(EventType.START_ELEMENT));
		assertTrue(root4.getProduction(2).getEvent().isEventType(EventType.END_ELEMENT));
		
		// SE(AC) should point to same node
		assertTrue(root4.getProduction(0).getNextGrammar() == root4);
		
		// after SE(AD) is end
		assertTrue(root4.getProduction(1).getNextGrammar().getNumberOfEvents() == 1);
		assertTrue(root4.getProduction(1).getNextGrammar().getProduction(0).getEvent().isEventType(EventType.END_ELEMENT));
		
	}
	
	

	/*
	 * <xsd:complexType name="B">
	 * <xsd:sequence>
	 * <xsd:element name="AB" minOccurs='0' />
	 * <xsd:element name="AC" minOccurs="1" maxOccurs="3"/>
	 * <xsd:element name="AD" minOccurs="0"/>
	 * </xsd:sequence>
	 * </xsd:complexType>
	 */
	public void testSchemaInformedGrammarSequence3() throws Exception {
		schema = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
			+ " <xs:element name='root'>"
			+ "  <xs:complexType>"
			+ "   <xs:sequence>"
			+ "    <xs:element name='AB' minOccurs='0' /> "
			+ "    <xs:element name='AC' minOccurs='1' maxOccurs='3'/> "
			+ "    <xs:element name='AD' minOccurs='0'/> "
			+ "   </xs:sequence>" + "  </xs:complexType>"
			+ " </xs:element>"
			+ "</xs:schema>";
	
		Grammars g = getGrammarFromSchemaAsString(schema);
		Grammar document = g.getDocumentGrammar();
		/*
		 * Document :
		 * SD DocContent	0
		 */
		assertTrue(document.getNumberOfEvents() == 1);
		Production eiSD =  document.getProduction(0);
		assertTrue(eiSD.getEvent().isEventType(EventType.START_DOCUMENT));
		
		Grammar docContent = eiSD.getNextGrammar();
		/*
		 * DocContent :
		 * SE (G 0) DocEnd	0
		 * SE (G 1) DocEnd	1
		 * . . .
		 * SE (G n-1) DocEnd	n-1
		 * SE (*) DocEnd	n
		 * DT DocContent	 (n+1).0
		 * CM DocContent	 (n+1).1.0
		 * PI DocContent	 (n+1).1.1
		 */
		assertTrue(docContent.getNumberOfEvents() == 2);
		Production eiSE_root =  docContent.getProduction(0);
		assertTrue(eiSE_root.getEvent().isEventType(EventType.START_ELEMENT));
		assertTrue(((StartElement)eiSE_root.getEvent()).getQName().getLocalPart().equals("root"));
		Production eiSEG =  docContent.getProduction(1);
		assertTrue(eiSEG.getEvent().isEventType(EventType.START_ELEMENT_GENERIC));
		assertTrue(eiSE_root.getNextGrammar() == eiSEG.getNextGrammar());
		
		Grammar docEnd = eiSEG.getNextGrammar();
		/*
		 * DocEnd :
		 * ED	0
		 * CM DocEnd	1.0
		 * PI DocEnd	1.1
		 */
		assertTrue(docEnd.getNumberOfEvents() == 1);
		Production ei =  docEnd.getProduction(0);
		assertTrue(ei.getEvent().isEventType(EventType.END_DOCUMENT));
		
		/*
		 * 1. SE(AB), SE(AC)
		 */
		Grammar root1 = ((StartElement)eiSE_root.getEvent()).getGrammar();
		assertTrue(root1.getNumberOfEvents() == 2);
		assertTrue(root1.getProduction(0).getEvent().isEventType(EventType.START_ELEMENT));
		assertTrue(root1.getProduction(1).getEvent().isEventType(EventType.START_ELEMENT));
		
		/*
		 * 2. SE(AC)
		 */
		Grammar root2 = root1.getProduction(0).getNextGrammar();
		assertTrue(root2.getNumberOfEvents() == 1);
		assertTrue(root2.getProduction(0).getEvent().isEventType(EventType.START_ELEMENT));
		
		/*
		 * 3. SE(AC), SE(AD), EE
		 */
		Grammar root3 = root2.getProduction(0).getNextGrammar();
		assertTrue(root3.getNumberOfEvents() == 3);
		assertTrue(root3.getProduction(0).getEvent().isEventType(EventType.START_ELEMENT));
		assertTrue(root3.getProduction(1).getEvent().isEventType(EventType.START_ELEMENT));
		assertTrue(root3.getProduction(2).getEvent().isEventType(EventType.END_ELEMENT));
	
		/*
		 * 4. SE(AC), SE(AD), EE
		 */
		Grammar root4 = root3.getProduction(0).getNextGrammar();
		assertTrue(root4.getNumberOfEvents() == 3);
		assertTrue(root4.getProduction(0).getEvent().isEventType(EventType.START_ELEMENT));
		assertTrue(root4.getProduction(1).getEvent().isEventType(EventType.START_ELEMENT));
		assertTrue(root4.getProduction(2).getEvent().isEventType(EventType.END_ELEMENT));
		
		/*
		 * 5. SE(AD), EE
		 */
		Grammar root5 = root4.getProduction(0).getNextGrammar();
		assertTrue(root5.getNumberOfEvents() == 2);
		assertTrue(root5.getProduction(0).getEvent().isEventType(EventType.START_ELEMENT));
		assertTrue(root5.getProduction(1).getEvent().isEventType(EventType.END_ELEMENT));
		
	}
	
	public void testEventCode2ndLevel() throws Exception {
		schema = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
			+ " <xs:element name='root'>"
			+ "  <xs:complexType>"
			+ "   <xs:sequence>"
			+ "    <xs:element name='A' minOccurs='0' /> "
			+ "   </xs:sequence>"
			+ "   <xs:attribute name='atB'/>"
			+ "  </xs:complexType>"
			+ " </xs:element>"
			+ "</xs:schema>";
	
		Grammars g = getGrammarFromSchemaAsString(schema);
		Grammar document = g.getDocumentGrammar();
		Production eiSD =  document.getProduction(0);
		Grammar docContent = eiSD.getNextGrammar();
		assertTrue(docContent.getNumberOfEvents() == 2);
		Production eiSE_root =  docContent.getProduction(0);
		
		
		/*
		 * 1. SE(A), AT(atB) EE
		 */
		Grammar root1 = ((StartElement)eiSE_root.getEvent()).getGrammar(); // FirstStartTag
		assertTrue(root1 instanceof SchemaInformedFirstStartTag);
		assertTrue(root1.getNumberOfEvents() == 3);
		assertTrue(root1.getProduction(0).getEvent().isEventType(EventType.ATTRIBUTE));
		assertTrue(root1.getProduction(1).getEvent().isEventType(EventType.START_ELEMENT));
		assertTrue(root1.getProduction(2).getEvent().isEventType(EventType.END_ELEMENT));
		
		FidelityOptions foStrict = FidelityOptions.createStrict();
		FidelityOptions foDef = FidelityOptions.createDefault();
		FidelityOptions foAll = FidelityOptions.createAll();
		
		{
			// strict
			assertTrue(root1.get2ndLevelCharacteristics(foStrict) == 0);
			
			// default
			assertTrue(root1.get2ndLevelCharacteristics(foDef) == 6);
			assertTrue(root1.get2ndLevelEventType(0, foDef) == EventType.ATTRIBUTE_XSI_TYPE);
			assertTrue(root1.get2ndLevelEventType(1, foDef) == EventType.ATTRIBUTE_XSI_NIL);
			assertTrue(root1.get2ndLevelEventType(2, foDef) == EventType.ATTRIBUTE_GENERIC_UNDECLARED);
			assertTrue(root1.get2ndLevelEventType(3, foDef) == EventType.ATTRIBUTE_INVALID_VALUE);
			assertTrue(root1.get2ndLevelEventType(4, foDef) == EventType.START_ELEMENT_GENERIC_UNDECLARED);
			assertTrue(root1.get2ndLevelEventType(5, foDef) == EventType.CHARACTERS_GENERIC_UNDECLARED);
			assertTrue(root1.get2ndLevelEventCode(EventType.ATTRIBUTE_XSI_TYPE, foDef) == 0);
			assertTrue(root1.get2ndLevelEventCode(EventType.ATTRIBUTE_XSI_NIL, foDef) == 1);
			assertTrue(root1.get2ndLevelEventCode(EventType.ATTRIBUTE_GENERIC_UNDECLARED, foDef) == 2);
			assertTrue(root1.get2ndLevelEventCode(EventType.ATTRIBUTE_INVALID_VALUE, foDef) == 3);
			assertTrue(root1.get2ndLevelEventCode(EventType.START_ELEMENT_GENERIC_UNDECLARED, foDef) == 4);
			assertTrue(root1.get2ndLevelEventCode(EventType.CHARACTERS_GENERIC_UNDECLARED, foDef) == 5);
			
			// all
			assertTrue(root1.get2ndLevelCharacteristics(foAll) == (8 + 1));
			assertTrue(root1.get2ndLevelEventType(0, foAll) == EventType.ATTRIBUTE_XSI_TYPE);
			assertTrue(root1.get2ndLevelEventType(1, foAll) == EventType.ATTRIBUTE_XSI_NIL);
			assertTrue(root1.get2ndLevelEventType(2, foAll) == EventType.ATTRIBUTE_GENERIC_UNDECLARED);
			assertTrue(root1.get2ndLevelEventType(3, foAll) == EventType.ATTRIBUTE_INVALID_VALUE);
			assertTrue(root1.get2ndLevelEventType(4, foAll) == EventType.NAMESPACE_DECLARATION);
			assertTrue(root1.get2ndLevelEventType(5, foAll) == EventType.START_ELEMENT_GENERIC_UNDECLARED);
			assertTrue(root1.get2ndLevelEventType(6, foAll) == EventType.CHARACTERS_GENERIC_UNDECLARED);
			assertTrue(root1.get2ndLevelEventType(7, foAll) == EventType.ENTITY_REFERENCE);
			assertTrue(root1.get2ndLevelEventCode(EventType.ATTRIBUTE_XSI_TYPE, foAll) == 0);
			assertTrue(root1.get2ndLevelEventCode(EventType.ATTRIBUTE_XSI_NIL, foAll) == 1);
			assertTrue(root1.get2ndLevelEventCode(EventType.ATTRIBUTE_GENERIC_UNDECLARED, foAll) == 2);
			assertTrue(root1.get2ndLevelEventCode(EventType.ATTRIBUTE_INVALID_VALUE, foAll) == 3);
			assertTrue(root1.get2ndLevelEventCode(EventType.NAMESPACE_DECLARATION, foAll) == 4);
			assertTrue(root1.get2ndLevelEventCode(EventType.START_ELEMENT_GENERIC_UNDECLARED, foAll) == 5);
			assertTrue(root1.get2ndLevelEventCode(EventType.CHARACTERS_GENERIC_UNDECLARED, foAll) == 6);
			assertTrue(root1.get2ndLevelEventCode(EventType.ENTITY_REFERENCE, foAll) == 7);
		}

		
		/*
		 * 2.SE(A), EE
		 */
		Grammar root2 = root1.getProduction(0).getNextGrammar();
		assertTrue(root2 instanceof SchemaInformedStartTag);
		assertTrue(root2.getNumberOfEvents() == 2);
		assertTrue(root2.getProduction(0).getEvent().isEventType(EventType.START_ELEMENT));
		assertTrue(root2.getProduction(1).getEvent().isEventType(EventType.END_ELEMENT));
		
		{
			// strict
			assertTrue(root2.get2ndLevelCharacteristics(foStrict) == 0);
			
			// default
			assertTrue(root2.get2ndLevelCharacteristics(foDef) == 4);
			assertTrue(root2.get2ndLevelEventType(0, foDef) == EventType.ATTRIBUTE_GENERIC_UNDECLARED);
			assertTrue(root2.get2ndLevelEventType(1, foDef) == EventType.ATTRIBUTE_INVALID_VALUE);
			assertTrue(root2.get2ndLevelEventType(2, foDef) == EventType.START_ELEMENT_GENERIC_UNDECLARED);
			assertTrue(root2.get2ndLevelEventType(3, foDef) == EventType.CHARACTERS_GENERIC_UNDECLARED);
			assertTrue(root2.get2ndLevelEventCode(EventType.ATTRIBUTE_GENERIC_UNDECLARED, foDef) == 0);
			assertTrue(root2.get2ndLevelEventCode(EventType.ATTRIBUTE_INVALID_VALUE, foDef) == 1);
			assertTrue(root2.get2ndLevelEventCode(EventType.START_ELEMENT_GENERIC_UNDECLARED, foDef) == 2);
			assertTrue(root2.get2ndLevelEventCode(EventType.CHARACTERS_GENERIC_UNDECLARED, foDef) == 3);
			
			// all
			assertTrue(root2.get2ndLevelCharacteristics(foAll) == (5 + 1));
			assertTrue(root2.get2ndLevelEventType(0, foAll) == EventType.ATTRIBUTE_GENERIC_UNDECLARED);
			assertTrue(root2.get2ndLevelEventType(1, foAll) == EventType.ATTRIBUTE_INVALID_VALUE);
			assertTrue(root2.get2ndLevelEventType(2, foAll) == EventType.START_ELEMENT_GENERIC_UNDECLARED);
			assertTrue(root2.get2ndLevelEventType(3, foAll) == EventType.CHARACTERS_GENERIC_UNDECLARED);
			assertTrue(root2.get2ndLevelEventType(4, foAll) == EventType.ENTITY_REFERENCE);
			assertTrue(root2.get2ndLevelEventCode(EventType.ATTRIBUTE_GENERIC_UNDECLARED, foAll) == 0);
			assertTrue(root2.get2ndLevelEventCode(EventType.ATTRIBUTE_INVALID_VALUE, foAll) == 1);
			assertTrue(root2.get2ndLevelEventCode(EventType.START_ELEMENT_GENERIC_UNDECLARED, foAll) == 2);
			assertTrue(root2.get2ndLevelEventCode(EventType.CHARACTERS_GENERIC_UNDECLARED, foAll) == 3);
			assertTrue(root2.get2ndLevelEventCode(EventType.ENTITY_REFERENCE, foAll) == 4);
		}
		
		/*
		 * 3. 
		 */
		Grammar root3 = root1.getProduction(1).getNextGrammar();
		assertTrue(root3 instanceof SchemaInformedElement);
		
		{
			// strict
			assertTrue(root3.get2ndLevelCharacteristics(foStrict) == 0);
			
			// default
			assertTrue(root3.get2ndLevelCharacteristics(foDef) == 2);
			assertTrue(root3.get2ndLevelEventType(0, foDef) == EventType.START_ELEMENT_GENERIC_UNDECLARED);
			assertTrue(root3.get2ndLevelEventType(1, foDef) == EventType.CHARACTERS_GENERIC_UNDECLARED);
			assertTrue(root3.get2ndLevelEventCode(EventType.START_ELEMENT_GENERIC_UNDECLARED, foDef) == 0);
			assertTrue(root3.get2ndLevelEventCode(EventType.CHARACTERS_GENERIC_UNDECLARED, foDef) == 1);
			
			// all
			assertTrue(root3.get2ndLevelCharacteristics(foAll) == (3 + 1));
			assertTrue(root3.get2ndLevelEventType(0, foAll) == EventType.START_ELEMENT_GENERIC_UNDECLARED);
			assertTrue(root3.get2ndLevelEventType(1, foAll) == EventType.CHARACTERS_GENERIC_UNDECLARED);
			assertTrue(root3.get2ndLevelEventType(2, foAll) == EventType.ENTITY_REFERENCE);
			assertTrue(root3.get2ndLevelEventCode(EventType.START_ELEMENT_GENERIC_UNDECLARED, foAll) == 0);
			assertTrue(root3.get2ndLevelEventCode(EventType.CHARACTERS_GENERIC_UNDECLARED, foAll) == 1);
			assertTrue(root3.get2ndLevelEventCode(EventType.ENTITY_REFERENCE, foAll) == 2);
		}
		
		
		
	}


}
