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

package com.siemens.ct.exi.attributes;

import org.custommonkey.xmlunit.XMLTestCase;

import com.siemens.ct.exi.Constants;
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
		ef.setGrammars(GrammarFactory.newInstance()
				.createXSDTypesOnlyGrammars());

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
		ef.setGrammars(GrammarFactory.newInstance()
				.createXSDTypesOnlyGrammars());

		AttributeList al = af.createAttributeListInstance(ef);

		al.addAttribute("", "x", "x", "bla");
		al.addAttribute("", "y", "y", "bla");
		al.addAttribute(Constants.XML_SCHEMA_INSTANCE_NS_URI, "type",
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
		al.addAttribute(Constants.XML_SCHEMA_INSTANCE_NS_URI, "nil",
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
		ef.setGrammars(GrammarFactory.newInstance()
				.createXSDTypesOnlyGrammars());

		AttributeList al = af.createAttributeListInstance(ef);

		// cif:aomPath="-43/1" desc="LLN0" lnType="foo" lnClass="LLN0" inst=""
		// xsi:type="cif:tLN0"
		al.addAttribute("www.cif.de", "aomPath", "cif:aomPath", "-43/1");
		al.addAttribute("", "desc", "desc", "LLN0");
		al.addAttribute("", "lnType", "lnType", "foo");
		al.addAttribute("", "lnClass", "lnClass", "LLN0");
		al.addAttribute("", "inst", "inst", "");
		al.addAttribute(Constants.XML_SCHEMA_INSTANCE_NS_URI, "type",
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
