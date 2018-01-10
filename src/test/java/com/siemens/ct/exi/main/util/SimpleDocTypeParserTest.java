/*
 * Copyright (c) 2007-2018 Siemens AG
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

package com.siemens.ct.exi.main.util;

import java.io.IOException;

import junit.framework.TestCase;

import org.xml.sax.SAXException;

import com.siemens.ct.exi.main.util.SimpleDocTypeParser;

public class SimpleDocTypeParserTest extends TestCase {

	SimpleDocTypeParser dtdParser;

	protected SimpleDocTypeParser getDtdParser() throws SAXException {
		if (dtdParser == null) {
			dtdParser = new SimpleDocTypeParser();
		}
		return dtdParser;
	}

	public void test3() throws SAXException, IOException {
		SimpleDocTypeParser dtdParser = getDtdParser();
		String dtd = "<!DOCTYPE foo [<!ENTITY ent SYSTEM \"entityReference2-er.xml\">]>";
		dtdParser.parse(dtd);

		assertTrue("foo".equals(dtdParser.name));
		assertTrue("".equals(dtdParser.publicID));
		assertTrue("".equals(dtdParser.systemID));
		// System.out.println(dtdParser.text);
		// System.out.println("<!ENTITY ent SYSTEM \"entityReference2-er.xml\">");
		// assertTrue("<!ENTITY ent SYSTEM \"entityReference2-er.xml\">".equals(dtdParser.text));
		assertTrue(dtdParser.text.contains("ENTITY"));
		assertTrue(dtdParser.text.contains("ent"));
		assertTrue(dtdParser.text.contains("SYSTEM"));
		assertTrue(dtdParser.text.contains("\"entityReference2-er.xml\""));

	}

	public void test1() throws SAXException, IOException {
		SimpleDocTypeParser dtdParser = getDtdParser();
		String dtd = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">";
		dtdParser.parse(dtd);

		assertTrue("html".equals(dtdParser.name));
		assertTrue("-//W3C//DTD XHTML 1.0 Strict//EN"
				.equals(dtdParser.publicID));
		assertTrue("http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"
				.equals(dtdParser.systemID));
		assertTrue("".equals(dtdParser.text));

	}

	public void test2() throws SAXException, IOException {
		SimpleDocTypeParser dtdParser = getDtdParser();
		String dtd = "<!DOCTYPE pPurchaseOrder [<!ENTITY eacute \"&#xE9;&#xE9;\"><!ENTITY Pub-Status \"This is a pre-release of the specification.\">]>";
		dtdParser.parse(dtd);

		assertTrue("pPurchaseOrder".equals(dtdParser.name));
		assertTrue("".equals(dtdParser.publicID));
		assertTrue("".equals(dtdParser.systemID));
		// String text =
		// "<!ENTITY eacute \"��\"> <!ENTITY Pub-Status \"This is a pre-release of the specification.\">";
		// System.out.println(text);
		// System.out.println(dtdParser.text);
		// assertTrue(text.equals(dtdParser.text));
		char c = 0xE9;
		String s1 = String.valueOf(c);
		assertTrue(dtdParser.text.contains(s1));
		assertTrue(dtdParser.text.contains("ENTITY eacute \"" + s1 + s1 + "\""));
		assertTrue(dtdParser.text
				.contains("\"This is a pre-release of the specification.\""));
	}

}
