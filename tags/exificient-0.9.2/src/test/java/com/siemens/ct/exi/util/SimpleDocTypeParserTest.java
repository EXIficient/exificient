package com.siemens.ct.exi.util;

import java.io.IOException;

import junit.framework.TestCase;

import org.xml.sax.SAXException;

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
		// "<!ENTITY eacute \"יי\"> <!ENTITY Pub-Status \"This is a pre-release of the specification.\">";
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
