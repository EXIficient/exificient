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

package com.siemens.ct.exi.attributes;

import javax.xml.XMLConstants;

import org.custommonkey.xmlunit.XMLTestCase;

import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.GrammarFactory;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.helpers.DefaultEXIFactory;

public class TestAttributeList extends XMLTestCase {

	protected static AttributeFactory af = AttributeFactory.newInstance();

	protected static final String ATTRIBUTE_TYPE = "CDATA";

	public void testAttributes1() {
		// schema-less
		EXIFactory ef = DefaultEXIFactory.newInstance();

		AttributeList al = af.createAttributeListInstance(ef);

		al.addAttribute("", "c", "c", "bla");
		al.addAttribute("", "b", "b", "bla");
		al.addAttribute("", "a", "a", "bla");

		assertTrue(al.getNumberOfAttributes() == 3);
		assertTrue(al.hasXsiNil() == false);
		assertTrue(al.hasXsiType() == false);
	}

	public void testAttributes2() throws EXIException {
		// schema-informed
		EXIFactory ef = DefaultEXIFactory.newInstance();
		ef.setGrammar(GrammarFactory.newInstance().createXSDTypesOnlyGrammar());

		AttributeList al = af.createAttributeListInstance(ef);

		al.addAttribute("", "c", "c", "bla");
		al.addAttribute("", "b", "b", "bla");
		al.addAttribute("", "a", "a", "bla");

		assertTrue(al.getNumberOfAttributes() == 3);
		assertTrue(al.hasXsiNil() == false);
		assertTrue(al.hasXsiType() == false);

		assertTrue(al.getAttributeLocalName(0).equals("a"));
		assertTrue(al.getAttributeLocalName(1).equals("b"));
		assertTrue(al.getAttributeLocalName(2).equals("c"));
	}

	public void testAttributes3() throws EXIException {
		// schema-informed + xsi:type
		EXIFactory ef = DefaultEXIFactory.newInstance();
		ef.setGrammar(GrammarFactory.newInstance().createXSDTypesOnlyGrammar());

		AttributeList al = af.createAttributeListInstance(ef);

		al.addAttribute("", "x", "x", "bla");
		al.addAttribute("", "y", "y", "bla");
		al.addAttribute(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "type",
				"xsi", "uri:type");

		assertTrue(al.getNumberOfAttributes() == 2);
		assertTrue(al.hasXsiNil() == false);
		assertTrue(al.hasXsiType() == true);

		assertTrue(al.getAttributeLocalName(0).equals("x"));
		assertTrue(al.getAttributeLocalName(1).equals("y"));

		assertTrue(al.getXsiTypeRaw().equals("uri:type"));
		assertTrue(al.getXsiTypePrefix().equals("xsi"));
	}

	public void testAttributes4() throws EXIException {
		// schema-less + xsi:nil
		EXIFactory ef = DefaultEXIFactory.newInstance();

		AttributeList al = af.createAttributeListInstance(ef);

		al.addAttribute("", "y", "y", "bla");
		al.addAttribute("", "x", "x", "bla");
		al.addAttribute(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "nil",
				"xsi", "false ");

		assertTrue(al.getNumberOfAttributes() == 2);
		assertTrue(al.hasXsiNil() == true);
		assertTrue(al.hasXsiType() == false);

		// assertTrue(al.getAttributeLocalName(0).equals("y"));
		// assertTrue(al.getAttributeLocalName(1).equals("x"));

		assertTrue(al.getXsiNil().equals("false "));
		assertTrue(al.getXsiNilPrefix().equals("xsi"));
	}

	public void testAttributes5() throws EXIException {
		// schema-informed + diverse uris
		EXIFactory ef = DefaultEXIFactory.newInstance();
		ef.setGrammar(GrammarFactory.newInstance().createXSDTypesOnlyGrammar());

		AttributeList al = af.createAttributeListInstance(ef);

		// cif:aomPath="-43/1" desc="LLN0" lnType="foo" lnClass="LLN0" inst=""
		// xsi:type="cif:tLN0"
		al.addAttribute("www.cif.de", "aomPath", "cif:aomPath", "-43/1");
		al.addAttribute("", "desc", "desc", "LLN0");
		al.addAttribute("", "lnType", "lnType", "foo");
		al.addAttribute("", "lnClass", "lnClass", "LLN0");
		al.addAttribute("", "inst", "inst", "");
		al.addAttribute(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "type",
				"xsi", "cif:tLN0");

		assertTrue(al.getNumberOfAttributes() == 5);
		assertTrue(al.hasXsiNil() == false);
		assertTrue(al.hasXsiType() == true);

		assertTrue(al.getAttributeLocalName(0).equals("aomPath"));
		assertTrue(al.getAttributeLocalName(1).equals("desc"));
		assertTrue(al.getAttributeLocalName(2).equals("inst"));
		assertTrue(al.getAttributeLocalName(3).equals("lnClass"));
		assertTrue(al.getAttributeLocalName(4).equals("lnType"));

		assertTrue(al.getXsiTypeRaw().equals("cif:tLN0"));
		assertTrue(al.getXsiTypePrefix().equals("xsi"));
	}

}
