/*
 * Copyright (C) 2007-2014 Siemens AG
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
import java.io.IOException;
import java.io.InputStream;

import javax.xml.namespace.QName;

import junit.framework.TestCase;

import org.apache.xerces.xni.XMLResourceIdentifier;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.custommonkey.xmlunit.XMLConstants;

import com.siemens.ct.exi.FidelityOptions;
import com.siemens.ct.exi.GrammarFactory;
import com.siemens.ct.exi.context.GrammarContext;
import com.siemens.ct.exi.context.QNameContext;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.grammars.event.Attribute;
import com.siemens.ct.exi.grammars.event.Event;
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
		Grammar root = gc.getGrammarUriContext("").getQNameContext("root")
				.getGlobalStartElement().getGrammar();
		// Rule root =
		// g.getGlobalElement(XSDGrammarBuilder.getEfficientQName(gues, "",
		// "root")).getRule();

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
		Grammar root = gc.getGrammarUriContext("").getQNameContext("root")
				.getGlobalStartElement().getGrammar();
		// Rule root =
		// g.getGlobalElement(XSDGrammarBuilder.getEfficientQName(gues, "",
		// "root")).getRule();

		// SE(a)
		assertTrue(root.getNumberOfEvents() == 1);
		Production er0 = root.getProduction(0);
		assertTrue(er0.getEvent().isEventType(EventType.START_ELEMENT));
		assertTrue(((StartElement) er0.getEvent()).getQName().getLocalPart()
				.equals("a"));

		Grammar a_1 = er0.getNextGrammar();
		// SE(a)
		assertTrue(a_1.getNumberOfEvents() == 1);
		Production er1 = a_1.getProduction(0);
		assertTrue(er1.getEvent().isEventType(EventType.START_ELEMENT));
		assertTrue(((StartElement) er1.getEvent()).getQName().getLocalPart()
				.equals("a"));

		Grammar a_2 = er1.getNextGrammar();
		// SE(b)
		assertTrue(a_2.getNumberOfEvents() == 1);
		Production er2 = a_2.getProduction(0);
		assertTrue(er2.getEvent().isEventType(EventType.START_ELEMENT));
		assertTrue(((StartElement) er2.getEvent()).getQName().getLocalPart()
				.equals("b"));

		Grammar b = er2.getNextGrammar();
		// SE(b)
		assertTrue(b.getNumberOfEvents() == 1);
		Production er3 = b.getProduction(0);
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

		// Rule root = g.getGlobalElement(new QName("", "root")).getRule();
		Grammar root = gc.getGrammarUriContext("").getQNameContext("root")
				.getGlobalStartElement().getGrammar();
		// Rule root =
		// g.getGlobalElement(XSDGrammarBuilder.getEfficientQName(gues, "",
		// "root")).getRule();

		// SE(a, b)
		assertTrue(root.getNumberOfEvents() == 2);
		Production er0 = root.getProduction(0);
		assertTrue(er0.getEvent().isEventType(EventType.START_ELEMENT));
		assertTrue(((StartElement) er0.getEvent()).getQName().getLocalPart()
				.equals("a"));
		Production er1 = root.getProduction(1);
		assertTrue(er1.getEvent().isEventType(EventType.START_ELEMENT));
		assertTrue(((StartElement) er1.getEvent()).getQName().getLocalPart()
				.equals("b"));

		Grammar a = er0.getNextGrammar();
		// SE(A)
		assertTrue(a.getNumberOfEvents() == 1);
		Production era1 = a.getProduction(0);
		assertTrue(era1.getEvent().isEventType(EventType.START_ELEMENT));
		assertTrue(((StartElement) era1.getEvent()).getQName().getLocalPart()
				.equals("b"));

		Grammar b1 = er1.getNextGrammar();
		// SE(B)
		assertTrue(b1.getNumberOfEvents() == 1);
		Production erb1 = b1.getProduction(0);
		assertTrue(erb1.getEvent().isEventType(EventType.END_ELEMENT));

		Grammar b2 = era1.getNextGrammar();
		// SE(B)
		assertTrue(b2.getNumberOfEvents() == 1);
		Production erbb1 = b2.getProduction(0);
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
		Grammar root = gc.getGrammarUriContext("").getQNameContext("root")
				.getGlobalStartElement().getGrammar();
		// Rule root =
		// g.getGlobalElement(XSDGrammarBuilder.getEfficientQName(gues, "",
		// "root")).getRule();

		// SE(a, b)
		assertTrue(root.getNumberOfEvents() == 2);
		Production er0 = root.getProduction(0);
		assertTrue(er0.getEvent().isEventType(EventType.START_ELEMENT));
		assertTrue(((StartElement) er0.getEvent()).getQName().getLocalPart()
				.equals("a"));
		Production er1 = root.getProduction(1);
		assertTrue(er1.getEvent().isEventType(EventType.START_ELEMENT));
		assertTrue(((StartElement) er1.getEvent()).getQName().getLocalPart()
				.equals("b"));

		Grammar a = er0.getNextGrammar();
		// SE(A)
		assertTrue(a.getNumberOfEvents() == 2);
		Production era1 = a.getProduction(0);
		assertTrue(era1.getEvent().isEventType(EventType.START_ELEMENT));
		assertTrue(((StartElement) era1.getEvent()).getQName().getLocalPart()
				.equals("a"));
		Production era2 = a.getProduction(1);
		assertTrue(era2.getEvent().isEventType(EventType.START_ELEMENT));
		assertTrue(((StartElement) era2.getEvent()).getQName().getLocalPart()
				.equals("b"));

		Grammar b1 = er1.getNextGrammar();
		// SE(B)
		assertTrue(b1.getNumberOfEvents() == 1);
		Production erb1 = b1.getProduction(0);
		assertTrue(erb1.getEvent().isEventType(EventType.END_ELEMENT));

		// multiple "a"'s followed by b
		Grammar x = root.getProduction(0).getNextGrammar();
		for (int i = 0; i < 10; i++) {
			assertTrue(x.getNumberOfEvents() == 2);
			Production x0 = x.getProduction(0);
			assertTrue(x0.getEvent().isEventType(EventType.START_ELEMENT));
			assertTrue(((StartElement) x0.getEvent()).getQName().getLocalPart()
					.equals("a"));

			Production x1 = x.getProduction(1);
			assertTrue(x1.getEvent().isEventType(EventType.START_ELEMENT));
			assertTrue(((StartElement) x1.getEvent()).getQName().getLocalPart()
					.equals("b"));

			x = er0.getNextGrammar();
		}

		Grammar b2 = x.getProduction(1).getNextGrammar();
		// SE(B)
		assertTrue(b2.getNumberOfEvents() == 1);
		Production erbb1 = b2.getProduction(0);
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
		Grammar c = gc.getGrammarUriContext("").getQNameContext("root")
				.getGlobalStartElement().getGrammar();
		// Rule c = g.getGlobalElement(XSDGrammarBuilder.getEfficientQName(gues,
		// "", "root")).getRule();

		// SE(c), EE
		// maxOccurs = "0" --> same rule over and over again
		for (int i = 0; i < 10; i++) {
			assertTrue(c.getNumberOfEvents() == 2);
			Production er0 = c.getProduction(0);
			assertTrue(er0.getEvent().isEventType(EventType.START_ELEMENT));
			assertTrue(((StartElement) er0.getEvent()).getQName()
					.getLocalPart().equals("c"));
			Production er1 = c.getProduction(1);
			assertTrue(er1.getEvent().isEventType(EventType.END_ELEMENT));

			c = er0.getNextGrammar();
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
		Grammar rule = gc.getGrammarUriContext("").getQNameContext("root")
				.getGlobalStartElement().getGrammar();
		// Rule rule =
		// g.getGlobalElement(XSDGrammarBuilder.getEfficientQName(gues, "",
		// "root")).getRule();

		// SE(a), SE(c), EE
		{
			assertTrue(rule.getNumberOfEvents() == 3);
			Production er0 = rule.getProduction(0);
			assertTrue(er0.getEvent().isEventType(EventType.START_ELEMENT));
			assertTrue(((StartElement) er0.getEvent()).getQName()
					.getLocalPart().equals("a"));
			Production er1 = rule.getProduction(1);
			assertTrue(er1.getEvent().isEventType(EventType.START_ELEMENT));
			assertTrue(((StartElement) er1.getEvent()).getQName()
					.getLocalPart().equals("c"));
			Production er2 = rule.getProduction(2);
			assertTrue(er2.getEvent().isEventType(EventType.END_ELEMENT));
		}

		// SE(a), SE(c), EE
		// "a" over and over again
		{
			Grammar a = rule.getProduction(0).getNextGrammar();
			for (int i = 0; i < 10; i++) {
				assertTrue(a.getNumberOfEvents() == 3);
				Production er0 = a.getProduction(0);
				assertTrue(er0.getEvent().isEventType(EventType.START_ELEMENT));
				assertTrue(((StartElement) er0.getEvent()).getQName()
						.getLocalPart().equals("a"));
				Production er1 = a.getProduction(1);
				assertTrue(er1.getEvent().isEventType(EventType.START_ELEMENT));
				assertTrue(((StartElement) er1.getEvent()).getQName()
						.getLocalPart().equals("c"));
				Production er2 = a.getProduction(2);
				assertTrue(er2.getEvent().isEventType(EventType.END_ELEMENT));

				a = er0.getNextGrammar();
			}
		}

		// SE(a), SE(c), EE
		// "c" over and over again
		{
			Grammar c = rule.getProduction(0).getNextGrammar();
			for (int i = 0; i < 10; i++) {
				// System.out.println(i + ": " + c);
				assertTrue(c.getNumberOfEvents() == 3);
				Production er0 = c.getProduction(0);
				assertTrue(er0.getEvent().isEventType(EventType.START_ELEMENT));
				assertTrue(((StartElement) er0.getEvent()).getQName()
						.getLocalPart().equals("a"));
				Production er1 = c.getProduction(1);
				assertTrue(er1.getEvent().isEventType(EventType.START_ELEMENT));
				assertTrue(((StartElement) er1.getEvent()).getQName()
						.getLocalPart().equals("c"));
				Production er2 = c.getProduction(2);
				assertTrue(er2.getEvent().isEventType(EventType.END_ELEMENT));

				c = er1.getNextGrammar();
			}
		}

		// SE(a), SE(c), EE
		// alternately "a" and "c" over and over again
		{
			Grammar ac = rule.getProduction(0).getNextGrammar();
			for (int i = 0; i < 10; i++) {
				if (i % 2 == 0) {

				} else {
					assertTrue(ac.getNumberOfEvents() == 3);
					Production er0 = ac.getProduction(0);
					assertTrue(er0.getEvent().isEventType(
							EventType.START_ELEMENT));
					assertTrue(((StartElement) er0.getEvent()).getQName()
							.getLocalPart().equals("a"));
					Production er1 = ac.getProduction(1);
					assertTrue(er1.getEvent().isEventType(
							EventType.START_ELEMENT));
					assertTrue(((StartElement) er1.getEvent()).getQName()
							.getLocalPart().equals("c"));
					Production er2 = ac.getProduction(2);
					assertTrue(er2.getEvent()
							.isEventType(EventType.END_ELEMENT));

					ac = er0.getNextGrammar();
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
		Grammar rule = gc.getGrammarUriContext("").getQNameContext("root")
				.getGlobalStartElement().getGrammar();
		// Rule rule =
		// g.getGlobalElement(XSDGrammarBuilder.getEfficientQName(gues, "",
		// "root")).getRule();

		{
			assertTrue(rule.getNumberOfEvents() == 4);

			Production er0 = rule.getProduction(0);
			assertTrue(er0.getEvent().isEventType(EventType.START_ELEMENT));
			assertTrue(((StartElement) er0.getEvent()).getQName()
					.getLocalPart().equals("a"));

			Production er1 = rule.getProduction(1);
			assertTrue(er1.getEvent().isEventType(EventType.START_ELEMENT));
			assertTrue(((StartElement) er1.getEvent()).getQName()
					.getLocalPart().equals("b"));

			Production er2 = rule.getProduction(2);
			assertTrue(er2.getEvent().isEventType(EventType.START_ELEMENT));
			assertTrue(((StartElement) er2.getEvent()).getQName()
					.getLocalPart().equals("c"));

			Production er3 = rule.getProduction(3);
			assertTrue(er3.getEvent().isEventType(EventType.END_ELEMENT));
		}
	}

	// http://sourceforge.net/projects/exificient/forums/forum/856596/topic/5459806
	public void testSequenceSourceForgeForum1() throws Exception {
		schema = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"

				+ "<xs:complexType name='SubscribableResource'>"
				+ "  <xs:complexContent>"
				+ "    <xs:extension base='Resource'>"
				+ "      <xs:attribute name='subscribable' use='optional' type='xs:string' />"
				+ "    </xs:extension>"
				+ "  </xs:complexContent>"
				+ "</xs:complexType>"

				+ "<xs:complexType name='Resource'>"
				+ "  <xs:attribute name='href' use='optional' type='xs:anyURI' />"
				+ "</xs:complexType>"

				+ " <xs:element name='root'>"
				+ " <xs:complexType >"
				// + " <xs:complexType name='AbstractDevice'>"
				+ "    <xs:complexContent>"
				+ "      <xs:extension base='SubscribableResource'>" //
				+ "        <xs:sequence>"
				+ "          <xs:element name='ConfigurationLink'  minOccurs='0' maxOccurs='1' />" // type='ConfigurationLink'
				+ "          <xs:element name='DERLink'  minOccurs='0' maxOccurs='1' />" // type='DERLink'
				+ "          <xs:element name='DeviceInformationLink'  minOccurs='0' maxOccurs='1' />" // type='DeviceInformationLink'
				+ "          <xs:element name='DeviceStatusLink'  minOccurs='0' maxOccurs='1' />" // type='DeviceStatusLink'
				+ "          <xs:element name='FileStatusLink'  minOccurs='0' maxOccurs='1' />" // type='FileStatusLink'
				+ "          <xs:element name='IPInterfaceListLink' minOccurs='0' maxOccurs='1' />" // type='IPInterfaceListLink'
				+ "          <xs:element name='LoadShedAvailabilityLink'  minOccurs='0' maxOccurs='1' />" // type='LoadShedAvailabilityLink'
				+ "          <xs:element name='loadShedDeviceCategory'  minOccurs='0' maxOccurs='1' />" // type='DeviceCategoryType'
				+ "          <xs:element name='LogEventListLin' minOccurs='0' maxOccurs='1' />" // type='LogEventListLink'
				+ "          <xs:element name='PowerStatusLink' minOccurs='0' maxOccurs='1' />" // type='PowerStatusLink'
				+ "          <xs:element name='sFDI'   minOccurs='1' maxOccurs='1' />" // type='SFDIType'
				+ "        </xs:sequence>" + "      </xs:extension>"
				+ "    </xs:complexContent>" + "  </xs:complexType>"
				+ " </xs:element>" + "</xs:schema>";

		Grammars g = getGrammarFromSchemaAsString(schema);
		GrammarContext gc = g.getGrammarContext();

		Grammar rule = gc.getGrammarUriContext("").getQNameContext("root")
				.getGlobalStartElement().getGrammar();

		{
			// SE(ConfigurationLink), SE(DERLink), SE(DeviceInformationLink),
			// SE(DeviceStatusLink),
			// SE(FileStatusLink), SE(IPInterfaceListLink),
			// SE(LoadShedAvailabilityLink),
			// SE(loadShedDeviceCategory), SE(LogEventListLin),
			// SE(PowerStatusLink), SE(sFDI)
			assertTrue(rule.getNumberOfEvents() == 13);

			// FirstStartTag[ATTRIBUTE[STRING](href),
			// ATTRIBUTE[STRING](subscribable),
			// START_ELEMENT(ConfigurationLink), START_ELEMENT(DERLink),
			// START_ELEMENT(DeviceInformationLink),
			// START_ELEMENT(DeviceStatusLink),
			// START_ELEMENT(FileStatusLink),
			// START_ELEMENT(IPInterfaceListLink),
			// START_ELEMENT(LoadShedAvailabilityLink),
			// START_ELEMENT(loadShedDeviceCategory),
			// START_ELEMENT(LogEventListLin), START_ELEMENT(PowerStatusLink),
			// START_ELEMENT(sFDI)]

			Production er0 = rule.getProduction(0);
			assertTrue(er0.getEvent().isEventType(EventType.ATTRIBUTE));
			assertTrue(((Attribute) er0.getEvent()).getQName().getLocalPart()
					.equals("href"));

			Production er1 = rule.getProduction(1);
			assertTrue(er1.getEvent().isEventType(EventType.ATTRIBUTE));
			assertTrue(((Attribute) er1.getEvent()).getQName().getLocalPart()
					.equals("subscribable"));

			Production er2 = rule.getProduction(2);
			assertTrue(er2.getEvent().isEventType(EventType.START_ELEMENT));
			assertTrue(((StartElement) er2.getEvent()).getQName()
					.getLocalPart().equals("ConfigurationLink"));

			Production er3 = rule.getProduction(3);
			assertTrue(er3.getEvent().isEventType(EventType.START_ELEMENT));
			assertTrue(((StartElement) er3.getEvent()).getQName()
					.getLocalPart().equals("DERLink"));

			Production er4 = rule.getProduction(4);
			assertTrue(er4.getEvent().isEventType(EventType.START_ELEMENT));
			assertTrue(((StartElement) er4.getEvent()).getQName()
					.getLocalPart().equals("DeviceInformationLink"));

			Production er5 = rule.getProduction(5);
			assertTrue(er5.getEvent().isEventType(EventType.START_ELEMENT));
			assertTrue(((StartElement) er5.getEvent()).getQName()
					.getLocalPart().equals("DeviceStatusLink"));

			Production er6 = rule.getProduction(6);
			assertTrue(er6.getEvent().isEventType(EventType.START_ELEMENT));
			assertTrue(((StartElement) er6.getEvent()).getQName()
					.getLocalPart().equals("FileStatusLink"));

			Production er7 = rule.getProduction(7);
			assertTrue(er7.getEvent().isEventType(EventType.START_ELEMENT));
			assertTrue(((StartElement) er7.getEvent()).getQName()
					.getLocalPart().equals("IPInterfaceListLink"));

			Production er8 = rule.getProduction(8);
			assertTrue(er8.getEvent().isEventType(EventType.START_ELEMENT));
			assertTrue(((StartElement) er8.getEvent()).getQName()
					.getLocalPart().equals("LoadShedAvailabilityLink"));

			Production er9 = rule.getProduction(9);
			assertTrue(er9.getEvent().isEventType(EventType.START_ELEMENT));
			assertTrue(((StartElement) er9.getEvent()).getQName()
					.getLocalPart().equals("loadShedDeviceCategory"));

			Production er10 = rule.getProduction(10);
			assertTrue(er10.getEvent().isEventType(EventType.START_ELEMENT));
			assertTrue(((StartElement) er10.getEvent()).getQName()
					.getLocalPart().equals("LogEventListLin"));

			Production er11 = rule.getProduction(11);
			assertTrue(er11.getEvent().isEventType(EventType.START_ELEMENT));
			assertTrue(((StartElement) er11.getEvent()).getQName()
					.getLocalPart().equals("PowerStatusLink"));

			Production er12 = rule.getProduction(12);
			assertTrue(er12.getEvent().isEventType(EventType.START_ELEMENT));
			assertTrue(((StartElement) er12.getEvent()).getQName()
					.getLocalPart().equals("sFDI"));

		}
	}

	// public void testSequenceSourceForgeForum1_() throws Exception {
	// String schema = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
	// +
	// "<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns=\"http://zigbee.org/sep\" targetNamespace=\"http://zigbee.org/sep\" elementFormDefault=\"qualified\" attributeFormDefault=\"unqualified\" version=\"2.0.47\">\n"
	// + "  <xs:complexType name=\"AbstractDevice\">\n"
	// + "    <xs:annotation>\n"
	// +
	// "      <xs:documentation>The EndDevice providing the resources available within the DeviceCapabilities.</xs:documentation>\n"
	// + "    </xs:annotation>\n"
	// + "    <xs:complexContent>\n"
	// + "      <xs:extension base=\"SubscribableResource\">\n"
	// + "        <xs:sequence>\n"
	// +
	// "          <xs:element name=\"ConfigurationLink\" minOccurs=\"0\" maxOccurs=\"1\" />\n"
	// +
	// "          <xs:element name=\"DERLink\" minOccurs=\"0\" maxOccurs=\"1\" />\n"
	// +
	// "          <xs:element name=\"DeviceInformationLink\" minOccurs=\"0\" maxOccurs=\"1\" />\n"
	// +
	// "          <xs:element name=\"DeviceStatusLink\" minOccurs=\"0\" maxOccurs=\"1\" />\n"
	// +
	// "          <xs:element name=\"FileStatusLink\" minOccurs=\"0\" maxOccurs=\"1\" />\n"
	// +
	// "          <xs:element name=\"IPInterfaceListLink\" minOccurs=\"0\" maxOccurs=\"1\" />\n"
	// +
	// "          <xs:element name=\"LoadShedAvailabilityLink\" minOccurs=\"0\" maxOccurs=\"1\" />\n"
	// +
	// "          <xs:element name=\"loadShedDeviceCategory\" minOccurs=\"0\" maxOccurs=\"1\" >\n"
	// + "            <xs:annotation>\n"
	// +
	// "              <xs:documentation>This field is for use in devices that can shed load.  If you are a device that does not respond to EndDeviceControls (for instance, an ESI), this field should not have any bits set.</xs:documentation>\n"
	// + "            </xs:annotation>\n"
	// + "          </xs:element>\n"
	// +
	// "          <xs:element name=\"LogEventListLink\" minOccurs=\"0\" maxOccurs=\"1\" />\n"
	// +
	// "          <xs:element name=\"PowerStatusLink\" minOccurs=\"0\" maxOccurs=\"1\" />\n"
	// +
	// "          <xs:element name=\"sFDI\" minOccurs=\"1\" maxOccurs=\"1\" >\n"
	// + "            <xs:annotation>\n"
	// +
	// "              <xs:documentation>Short form of device identifier. See the Security section for additional details.</xs:documentation>\n"
	// + "            </xs:annotation>\n"
	// + "          </xs:element>\n"
	// + "        </xs:sequence>\n"
	// + "      </xs:extension>\n"
	// + "    </xs:complexContent>\n"
	// + "  </xs:complexType>\n"
	// + "  <xs:complexType name=\"SubscribableResource\">\n"
	// + "    <xs:annotation>\n"
	// +
	// "      <xs:documentation>A Resource to which a Subscription can be requested.</xs:documentation>\n"
	// + "    </xs:annotation>\n"
	// + "    <xs:complexContent>\n"
	// + "      <xs:extension base=\"Resource\">\n"
	// + "        <xs:attribute name=\"subscribable\" use=\"optional\" />\n"
	// + "      </xs:extension>\n"
	// + "    </xs:complexContent>\n"
	// + "  </xs:complexType>\n"
	// + "  <xs:complexType name=\"Resource\">\n"
	// + "    <xs:annotation>\n"
	// +
	// "      <xs:documentation>A resource is an addressable unit of information, either a collection (List) or instance of an object (identifiedObject, or simply, Resource)</xs:documentation>\n"
	// + "    </xs:annotation>\n"
	// +
	// "    <xs:attribute name=\"href\" use=\"optional\" type=\"xs:anyURI\" />\n"
	// + "  </xs:complexType>\n"
	// + "  <xs:element name=\"AbstractDevice\" type=\"AbstractDevice\" />\n"
	// + "</xs:schema>\n";
	//
	//
	// //Create new grammar from file
	// GrammarFactory gf = GrammarFactory.newInstance();
	// ByteArrayInputStream bais = new ByteArrayInputStream(schema
	// .getBytes());
	// GrammarFactory grammarFactory = GrammarFactory.newInstance();
	// Grammars g = grammarFactory.createGrammars(bais);
	//
	// //Start at document grammar
	// Grammar document = g.getDocumentGrammar();
	// //Get DocContent
	// Grammar doccontent =
	// document.lookForEvent(EventType.START_DOCUMENT).getNextGrammar();
	// //Get AbstractDevice
	// Grammar abstractdevice =
	// ((StartElement)doccontent.lookFor(0).getEvent()).getGrammar();
	//
	//
	// //Initialize variables
	// Grammar currentGrammar = abstractdevice;
	//
	// //Add all next rules to queue
	// Production p;
	// int numEvents = currentGrammar.getNumberOfEvents();
	// for (int i = 0; i < numEvents; i++)
	// {
	// p = currentGrammar.lookFor(i);
	// System.out.println(p);
	// }
	// }

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
		Grammar rule = gc.getGrammarUriContext("").getQNameContext("root")
				.getGlobalStartElement().getGrammar();
		// Rule rule =
		// g.getGlobalElement(XSDGrammarBuilder.getEfficientQName(gues, "",
		// "root")).getRule();

		assertTrue(rule.getNumberOfEvents() == 4);

		// schema-informed grammars should not expand
		// EvolvingUriContext ruc = new RuntimeEvolvingUriContext(0, "");
		int namespaceUriID = 0;
		QNameContext qncAt = new QNameContext(namespaceUriID, 0,
				new QName("a"), 0);
		rule.learnAttribute(new Attribute(qncAt, null));
		QNameContext qncSE = new QNameContext(namespaceUriID, 1,
				new QName("s"), 1);
		rule.learnStartElement(new StartElement(qncSE));
		rule.learnEndElement();
		rule.learnCharacters();

		assertTrue(rule.getNumberOfEvents() == 4);
	}

	public void testLearning2() throws Exception {
		// schema-less grammars
		Grammar startTag = new BuiltInStartTag();
		Grammar content = startTag.getElementContentGrammar();

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
		QNameContext qncSE = new QNameContext(namespaceUriID, 1,
				new QName("s"), 1);
		StartElement s = new StartElement(qncSE);
		startTag.learnStartElement(s);
		startTag.learnStartElement(s);
		assertTrue(startTag.getNumberOfEvents() == 4);
		content.learnStartElement(s);
		content.learnStartElement(s);
		assertTrue(content.getNumberOfEvents() == 4);

		// learn AT, can have multiple events even if similar
		QNameContext qncAt = new QNameContext(namespaceUriID, 0,
				new QName("a"), 0);
		Attribute a = new Attribute(qncAt, null);
		startTag.learnAttribute(a);
		startTag.learnAttribute(a);
		assertTrue(startTag.getNumberOfEvents() == 6);
		// Note: element cannot learn AT

		// learn multiple AT(xsi:type) --> at most one AT(xsi:type)
		QNameContext qncAtxsiType = new QNameContext(2, 1, new QName(
				XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "type"), 0);
		startTag.learnAttribute(new Attribute(qncAtxsiType, null));
		startTag.learnAttribute(new Attribute(qncAtxsiType, null));
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
		Grammar Document2 = gc2
				.getGrammarUriContext(qnDocument.getNamespaceURI())
				.getQNameContext(qnDocument.getLocalPart())
				.getGlobalStartElement().getGrammar();
		{
			// 1
			assertTrue(Document2.getNumberOfEvents() == 1);
			Production ei1 = Document2.getProduction(0);
			assertTrue(ei1.getEvent().isEventType(EventType.START_ELEMENT));
			StartElement se1 = (StartElement) ei1.getEvent();
			assertTrue(se1.getQName().equals(qnSurname));
			// 2
			assertTrue(ei1.getNextGrammar().getNumberOfEvents() == 1);
			Production ei2 = ei1.getNextGrammar().getProduction(0);
			assertTrue(ei2.getEvent().isEventType(EventType.START_ELEMENT));
			StartElement se2 = (StartElement) ei2.getEvent();
			assertTrue(se2.getQName().equals(qnSurname));
			// 3
			assertTrue(ei2.getNextGrammar().getNumberOfEvents() == 2);
			assertTrue(ei2.getNextGrammar().getProduction(1).getEvent()
					.isEventType(EventType.END_ELEMENT));
			Production ei3 = ei2.getNextGrammar().getProduction(0);
			assertTrue(ei3.getEvent().isEventType(EventType.START_ELEMENT));
			StartElement se3 = (StartElement) ei3.getEvent();
			assertTrue(se3.getQName().equals(qnSurname));
			// loop
			assertTrue(ei3.getNextGrammar() == ei3.getNextGrammar()
					.getProduction(0).getNextGrammar());
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
		Grammar Document3 = gc3
				.getGrammarUriContext(qnDocument.getNamespaceURI())
				.getQNameContext(qnDocument.getLocalPart())
				.getGlobalStartElement().getGrammar();

		{
			// 1
			assertTrue(Document3.getNumberOfEvents() == 1);
			Production ei1 = Document3.getProduction(0);
			assertTrue(ei1.getEvent().isEventType(EventType.START_ELEMENT));
			StartElement se1 = (StartElement) ei1.getEvent();
			assertTrue(se1.getQName().equals(qnSurname));
			// 2
			assertTrue(ei1.getNextGrammar().getNumberOfEvents() == 1);
			Production ei2 = ei1.getNextGrammar().getProduction(0);
			assertTrue(ei2.getEvent().isEventType(EventType.START_ELEMENT));
			StartElement se2 = (StartElement) ei2.getEvent();
			assertTrue(se2.getQName().equals(qnSurname));
			// 3
			assertTrue(ei2.getNextGrammar().getNumberOfEvents() == 1);
			Production ei3 = ei2.getNextGrammar().getProduction(0);
			assertTrue(ei3.getEvent().isEventType(EventType.START_ELEMENT));
			StartElement se3 = (StartElement) ei3.getEvent();
			assertTrue(se3.getQName().equals(qnSurname));
			// 4
			assertTrue(ei3.getNextGrammar().getNumberOfEvents() == 2);
			assertTrue(ei3.getNextGrammar().getProduction(1).getEvent()
					.isEventType(EventType.END_ELEMENT));
			Production ei4 = ei3.getNextGrammar().getProduction(0);
			assertTrue(ei4.getEvent().isEventType(EventType.START_ELEMENT));
			StartElement se4 = (StartElement) ei4.getEvent();
			assertTrue(se4.getQName().equals(qnSurname));
			// loop
			assertTrue(ei4.getNextGrammar() == ei4.getNextGrammar()
					.getProduction(0).getNextGrammar());
		}
	}

	public void testBugEc1PreCalculation() throws Exception {
		GrammarFactory gf = GrammarFactory.newInstance();
		Grammars grs = gf.createSchemaLessGrammars();
		FidelityOptions fidelityOptions = FidelityOptions.createDefault();

		// docContent: SE(*)
		Grammar doc = grs.getDocumentGrammar()
				.getProduction(EventType.START_DOCUMENT).getNextGrammar();
		assertTrue(doc.get1stLevelEventCodeLength(fidelityOptions) == 0);

		// fragmentContent SE(*), ED
		Grammar frag = grs.getFragmentGrammar()
				.getProduction(EventType.START_DOCUMENT).getNextGrammar();
		assertTrue(frag.get1stLevelEventCodeLength(fidelityOptions) == 1);
		// fragmentContent SE(dd), SE(*), ED
		QName qn = new QName("dd");
		frag.learnStartElement(new StartElement(new QNameContext(0, 0, qn, 0)));
		assertTrue(frag.get1stLevelEventCodeLength(fidelityOptions) == 2);
		Event l = frag.getProduction(0).getEvent();
		assertTrue(l.getEventType() == EventType.START_ELEMENT);
		StartElement se = (StartElement) l;
		assertTrue(se.getQName().equals(qn));
	}

	// // Xerces warning: the value of 'maxOccurs' is '65553', it will be
	// // considered 'unbounded' for validation because large values for
	// maxOccurs
	// // are not supported by Xerces.
	// public void testHugeMaxOccurrence() throws Exception {
	// QName qnDocument = new QName("", "Document");
	// QName qnSurname = new QName("", "surname");
	//
	// // StackOverflow
	// // 65553
	//
	// schema = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
	// + " <xs:element name='Document'>"
	// + "  <xs:complexType>"
	// + "   <xs:sequence >"
	// + "    <xs:element name='surname' type='xs:string' maxOccurs='1553'/> "
	// + "   </xs:sequence>" + "  </xs:complexType>"
	// + " </xs:element>" + "</xs:schema>";
	//
	//
	// Grammars g2 = getGrammarFromSchemaAsString(schema);
	// GrammarContext gc2 = g2.getGrammarContext();
	// }

	
	public void testXsdResolverTest() throws XNIException, IOException, EXIException {
		// Note: resolve 2 schema files without "actual" files

		InputStream isMain = new ByteArrayInputStream(sMain.getBytes());
		
		TestXSDResolver entityResolver = new TestXSDResolver();
		Grammars grs = GrammarFactory.newInstance().createGrammars(isMain, entityResolver);
		assertTrue(grs != null);
	}

	// main.xsd
	// <xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema">
	// <xs:include schemaLocation="types.xsd"></xs:include>
	// <xs:element name="root" type="myInt"></xs:element>
	// </xs:schema>
	String sMain = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\">\n    <xs:include schemaLocation=\"types.xsd\"></xs:include>\n    <xs:element name=\"root\" type=\"myInt\"></xs:element>\n</xs:schema>\n";
	
	// types.xsd
	// <xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema">
	// <xs:simpleType name="myInt">
	// <xs:restriction base="xs:int"></xs:restriction>
	// </xs:simpleType>
	// </xs:schema>
	String sTypes = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\">\n    <xs:simpleType name=\"myInt\">\n        <xs:restriction base=\"xs:int\"></xs:restriction>\n    </xs:simpleType>\n</xs:schema>\n";
	
	
	class TestXSDResolver implements
			org.apache.xerces.xni.parser.XMLEntityResolver {

		public TestXSDResolver() {
		}


		public XMLInputSource resolveEntity(
				XMLResourceIdentifier resourceIdentifier) throws XNIException,
				IOException {
			// String publicId = resourceIdentifier.getPublicId();
			// String baseSystemId = resourceIdentifier.getBaseSystemId();
			// String expandedSystemId =
			// resourceIdentifier.getExpandedSystemId();
			String literalSystemId = resourceIdentifier.getLiteralSystemId(); // e.g., "types.xsd"
			// String namespace = resourceIdentifier.getNamespace();

			if (literalSystemId.equals("types.xsd")) {
				InputStream isTypes = new ByteArrayInputStream(sTypes.getBytes());
				
				String publicId = null;
				String systemId = null;
				String baseSystemId = null;
				String encoding = null;
				XMLInputSource xsdSourceTypes = new XMLInputSource(publicId, systemId,
						baseSystemId, isTypes, encoding);
				return xsdSourceTypes;
			} else {
				// Note: if the entity cannot be resolved, this method should return
				// null.
				return null;				
			}

		}

	}

}
