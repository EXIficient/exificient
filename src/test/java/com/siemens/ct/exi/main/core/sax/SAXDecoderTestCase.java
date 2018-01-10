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

package com.siemens.ct.exi.main.core.sax;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.custommonkey.xmlunit.XMLTestCase;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import com.siemens.ct.exi.core.CodingMode;
import com.siemens.ct.exi.core.Constants;
import com.siemens.ct.exi.core.EXIFactory;
import com.siemens.ct.exi.core.FidelityOptions;
import com.siemens.ct.exi.grammars.GrammarFactory;
import com.siemens.ct.exi.main.api.sax.EXIResult;
import com.siemens.ct.exi.main.api.sax.SAXFactory;
import com.siemens.ct.exi.core.exceptions.EXIException;
import com.siemens.ct.exi.core.grammars.Grammars;
import com.siemens.ct.exi.core.helpers.DefaultEXIFactory;

public class SAXDecoderTestCase extends XMLTestCase {

	/* does EXI make sense otherwise ? */
	static final boolean namespaces = true;

	String xmlA = "<foo:root xmlns:foo='urn:foo' xmlns:fooX='urn:fooX' at1='x'>"
			+ "   <bla:el xmlns:bla='urn:bla' buu:at2='y' fooX:at3='z' xmlns:buu='urn:buu'>w</bla:el>"
			+ "</foo:root>";

	// Bug-ID 3033335
	String xmlB = "<FpML xmlns='http://www.fpml.org/2005/FpML-4-2' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' "
			+ " xmlns:ibml='http://ibml.jel.com/2005' xsi:type='ibml:ContractAmended' >"
			+ "</FpML>";

	String xsdBLocation = "./data/bugs/ID3033335/ibml.xsd";

	abstract class TestContentHandler extends DefaultHandler {
		final boolean namespacePrefixes;
		final boolean preservePrefixes;
		int openElements = 0;
		List<String>[] pfxMappings;

		@SuppressWarnings("unchecked")
		public TestContentHandler(boolean namespacePrefixes,
				boolean preservePrefixes) {
			this.namespacePrefixes = namespacePrefixes;
			this.preservePrefixes = preservePrefixes;
			pfxMappings = new ArrayList[2];
			pfxMappings[0] = new ArrayList<String>();
			pfxMappings[1] = new ArrayList<String>();
		}

		public void startElement(String uri, String localName, String qName,
				Attributes atts) throws SAXException {
			openElements++;
		}

		public void endElement(String uri, String localName, String qName)
				throws SAXException {
			openElements--;
		}
	}

	class TestContentHandlerA extends TestContentHandler {

		public TestContentHandlerA(boolean namespacePrefixes,
				boolean preservePrefixes) {
			super(namespacePrefixes, preservePrefixes);
		}

		public void startElement(String uri, String localName, String qName,
				Attributes atts) throws SAXException {
			super.startElement(uri, localName, qName, atts);

			if (namespaces) {
				if (openElements == 1) {
					// [xml] & urn:foo (+urn:fooX)
					assertTrue(pfxMappings[openElements - 1].size() >= 1);
					assertTrue(pfxMappings[openElements - 1]
							.contains("urn:foo"));
					// assertTrue(pfxMappings[openElements].contains("urn:fooX"));
				} else if (openElements == 2) {
					// urn:bla & urn:buu (+urn:fooX)
					assertTrue(pfxMappings[openElements - 1].size() >= 2);
					assertTrue(pfxMappings[openElements - 1]
							.contains("urn:bla"));
					assertTrue(pfxMappings[openElements - 1]
							.contains("urn:buu"));
					// assertTrue(pfxMappings[openElements].contains("urn:fooX"));
				}
			}

			if (openElements == 1) {
				// element
				assertTrue("urn:foo".equals(uri));
				assertTrue("root".equals(localName));
				// https://sourceforge.net/projects/exificient/forums/forum/856596/topic/5839494
				// if (namespacePrefixes) {
				assertTrue(!qName.equals(Constants.EMPTY_STRING));
				if (preservePrefixes) {
					assertTrue(qName.equals("foo:root"));
				}
				// } else {
				// assertTrue(qName.equals(Constants.EMPTY_STRING));
				// }
				// attributes, at1='x'
				assertTrue(atts.getLength() == 1);
				assertTrue("".equals(atts.getURI(0)));
				assertTrue("at1".equals(atts.getLocalName(0)));
				assertTrue("x".equals(atts.getValue(0)));
				// https://sourceforge.net/projects/exificient/forums/forum/856596/topic/5839494
				// if (namespacePrefixes) {
				if (preservePrefixes) {
					assertTrue(atts.getQName(0).equals("at1"));
				}
				// } else {
				// assertTrue(atts.getQName(0).equals(Constants.EMPTY_STRING));
				// }
			} else if (openElements == 2) {
				// element
				assertTrue("urn:bla".equals(uri));
				assertTrue("el".equals(localName));
				// https://sourceforge.net/projects/exificient/forums/forum/856596/topic/5839494
				// if (namespacePrefixes) {
				assertTrue(!qName.equals(Constants.EMPTY_STRING));
				if (preservePrefixes) {
					assertTrue(qName.equals("bla:el"));
				}
				// } else {
				// assertTrue(qName.equals(Constants.EMPTY_STRING));
				// }
				// attributes, buu:at2='y' fooX:at3='z'
				assertTrue(atts.getLength() == 2);
				assertTrue("urn:buu".equals(atts.getURI(0)));
				assertTrue("at2".equals(atts.getLocalName(0)));
				assertTrue("y".equals(atts.getValue(0)));
				assertTrue("urn:fooX".equals(atts.getURI(1)));
				assertTrue("at3".equals(atts.getLocalName(1)));
				assertTrue("z".equals(atts.getValue(1)));
				// https://sourceforge.net/projects/exificient/forums/forum/856596/topic/5839494
				// if (namespacePrefixes) {
				assertTrue(!atts.getQName(0).equals(Constants.EMPTY_STRING));
				assertTrue(!atts.getQName(1).equals(Constants.EMPTY_STRING));
				if (preservePrefixes) {
					assertTrue(atts.getQName(0).equals("buu:at2"));
					assertTrue(atts.getQName(1).equals("fooX:at3"));
				}
				// } else {
				// assertTrue(atts.getQName(0).equals(Constants.EMPTY_STRING));
				// }
			}
		}

		public void endElement(String uri, String localName, String qName)
				throws SAXException {

			if (openElements == 1) {
				assertTrue("urn:foo".equals(uri));
				assertTrue("root".equals(localName));
				// https://sourceforge.net/projects/exificient/forums/forum/856596/topic/5839494
				// if (namespacePrefixes) {
				assertTrue(!qName.equals(Constants.EMPTY_STRING));
				if (preservePrefixes) {
					assertTrue(qName.equals("foo:root"));
				}
				// } else {
				// assertTrue(qName.equals(Constants.EMPTY_STRING));
				// }
			} else if (openElements == 2) {
				assertTrue("urn:bla".equals(uri));
				assertTrue("el".equals(localName));
				// https://sourceforge.net/projects/exificient/forums/forum/856596/topic/5839494
				// if (namespacePrefixes) {
				assertTrue(!qName.equals(Constants.EMPTY_STRING));
				if (preservePrefixes) {
					assertTrue(qName.equals("bla:el"));
				}
				// } else {
				// assertTrue(qName.equals(Constants.EMPTY_STRING));
				// }
			}

			super.endElement(uri, localName, qName);
		}

		public void startPrefixMapping(String prefix, String uri)
				throws SAXException {
			if (openElements == 0) {
				pfxMappings[0].add(uri);
				// xmlns:foo='urn:foo'
				if (uri.equals("urn:foo")) {
					if (preservePrefixes) {
						assertTrue(prefix.equals("foo"));
					} else {
						assertTrue(prefix.length() > 0);
					}
					// xmlns:fooX='urn:fooX'
				} else if (uri.equals("urn:fooX")) {
					if (preservePrefixes) {
						assertTrue(prefix.equals("fooX"));
					} else {
						assertTrue(prefix.length() > 0);
					}
				} else if (uri.equals("http://www.w3.org/XML/1998/namespace")) {
					assertTrue(prefix.equals("xml"));
				} else if (uri.equals(Constants.XML_NULL_NS_URI)) {
					assertTrue(prefix.equals(Constants.XML_DEFAULT_NS_PREFIX));
				} else if (uri
						.equals(Constants.XML_SCHEMA_INSTANCE_NS_URI)) {
					assertTrue(prefix.equals("xsi"));
				} else {
					fail("No exptected URI: " + uri);
				}
				// System.out.println("0 " + prefix + " --> " + uri);
			} else if (openElements == 1) {
				pfxMappings[1].add(uri);
				// xmlns:bla='urn:bla'
				if (uri.equals("urn:bla")) {
					if (preservePrefixes) {
						assertTrue(prefix.equals("bla"));
					} else {
						assertTrue(prefix.length() > 0);
					}
					// xmlns:buu='urn:buu'
				} else if (uri.equals("urn:buu")) {
					if (preservePrefixes) {
						assertTrue(prefix.equals("buu"));
					} else {
						assertTrue(prefix.length() > 0);
					}
				} else if (!preservePrefixes && uri.equals("urn:fooX")) {
					assertTrue(prefix.length() > 0);
				} else {
					fail("No exptected URI: " + uri);
				}
				// System.out.println("1 " + prefix + " --> " + uri);
			}
		}

		public void endPrefixMapping(String prefix) throws SAXException {
			if (openElements == 0) {
				if (preservePrefixes) {
					// assertTrue(prefix.equals("foo") ||
					// prefix.equals("fooX"));
				}
			} else if (openElements == 1) {

			}
		}
	}

	class TestContentHandlerB extends TestContentHandler {

		public TestContentHandlerB(boolean namespacePrefixes,
				boolean preservePrefixes) {
			super(namespacePrefixes, preservePrefixes);
		}

		public void startElement(String uri, String localName, String qName,
				Attributes atts) throws SAXException {
			super.startElement(uri, localName, qName, atts);

			if (namespaces) {
				if (openElements == 1) {
					// [xml] & urn:foo (+urn:fooX)
					assertTrue(pfxMappings[openElements - 1].size() >= 2);
					assertTrue(pfxMappings[openElements - 1]
							.contains("http://www.w3.org/2001/XMLSchema-instance"));
					assertTrue(pfxMappings[openElements - 1]
							.contains("http://www.fpml.org/2005/FpML-4-2"));
					assertTrue(pfxMappings[openElements - 1]
							.contains("http://ibml.jel.com/2005"));
				}
			}
		}

		public void endElement(String uri, String localName, String qName)
				throws SAXException {

			if (openElements == 1) {

			}

			super.endElement(uri, localName, qName);
		}

		public void startPrefixMapping(String prefix, String uri)
				throws SAXException {
			if (openElements == 0) {
				pfxMappings[0].add(uri);
				// xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'
				if (uri.equals("http://www.w3.org/2001/XMLSchema-instance")) {
					if (preservePrefixes) {
						assertTrue(prefix.equals("xsi"));
					} else {
						assertTrue(prefix.length() > 0);
					}
					// xmlns='http://www.fpml.org/2005/FpML-4-2'
				} else if (uri.equals("http://www.fpml.org/2005/FpML-4-2")) {
					if (preservePrefixes) {
						assertTrue(prefix.equals(""));
					} else {
						assertTrue(prefix.length() > 0);
					}
					// xmlns:ibml='http://ibml.jel.com/2005'
				} else if (uri.equals("http://ibml.jel.com/2005")) {
					if (preservePrefixes) {
						assertTrue(prefix.equals("ibml"));
					} else {
						assertTrue(prefix.length() > 0);
					}
				} else if (uri.equals("http://www.w3.org/XML/1998/namespace")) {
					assertTrue(prefix.equals("xml"));
				} else if (uri.equals(Constants.XML_NULL_NS_URI)) {
					assertTrue(prefix.equals(Constants.XML_DEFAULT_NS_PREFIX));
				} else if (uri.equals(Constants.XML_SCHEMA_NS_URI)) {
					assertTrue(prefix.equals("ns3"));
				} else if (uri.equals("http://www.w3.org/2000/09/xmldsig#")) {
					assertTrue(prefix.equals("ns6"));
				} else {
					fail("No exptected URI: " + uri);
				}
				// System.out.println("0 " + prefix + " --> " + uri);
			}
		}

		public void endPrefixMapping(String prefix) throws SAXException {
			if (openElements == 0) {
			}
		}
	}

	public void testDecoderBNone() throws Exception {
		boolean namespacePrefixes = false;
		boolean preservePrefixes = false;

		TestContentHandler tch = new TestContentHandlerB(namespacePrefixes,
				preservePrefixes);

		// bit-packed
		_test(xmlB, null, tch, namespacePrefixes, preservePrefixes,
				CodingMode.BIT_PACKED);
		// byte-packed
		_test(xmlB, null, tch, namespacePrefixes, preservePrefixes,
				CodingMode.BYTE_PACKED);
		// pre-compression
		_test(xmlB, null, tch, namespacePrefixes, preservePrefixes,
				CodingMode.PRE_COMPRESSION);
		// compression
		_test(xmlB, null, tch, namespacePrefixes, preservePrefixes,
				CodingMode.COMPRESSION);
	}

	public void testDecoderBNoneSchema() throws Exception {
		boolean namespacePrefixes = false;
		boolean preservePrefixes = false;

		TestContentHandler tch = new TestContentHandlerB(namespacePrefixes,
				preservePrefixes);

		// bit-packed
		_test(xmlB, xsdBLocation, tch, namespacePrefixes, preservePrefixes,
				CodingMode.BIT_PACKED);
		// byte-packed
		_test(xmlB, xsdBLocation, tch, namespacePrefixes, preservePrefixes,
				CodingMode.BYTE_PACKED);
		// pre-compression
		_test(xmlB, xsdBLocation, tch, namespacePrefixes, preservePrefixes,
				CodingMode.PRE_COMPRESSION);
		// compression
		_test(xmlB, xsdBLocation, tch, namespacePrefixes, preservePrefixes,
				CodingMode.COMPRESSION);
	}

	public void testDecoderAPrefixesAndPreservePrefixes() throws Exception {
		boolean namespacePrefixes = true;
		boolean preservePrefixes = true;

		TestContentHandler tch = new TestContentHandlerA(namespacePrefixes,
				preservePrefixes);

		// bit-packed
		_test(xmlA, null, tch, namespacePrefixes, preservePrefixes,
				CodingMode.BIT_PACKED);
		// byte-packed
		_test(xmlA, null, tch, namespacePrefixes, preservePrefixes,
				CodingMode.BYTE_PACKED);
		// pre-compression
		_test(xmlA, null, tch, namespacePrefixes, preservePrefixes,
				CodingMode.PRE_COMPRESSION);
		// compression
		_test(xmlA, null, tch, namespacePrefixes, preservePrefixes,
				CodingMode.COMPRESSION);
	}

	public void testDecoderAPrefixes() throws Exception {
		boolean namespacePrefixes = true;
		boolean preservePrefixes = false;

		TestContentHandler tch = new TestContentHandlerA(namespacePrefixes,
				preservePrefixes);

		// bit-packed
		_test(xmlA, null, tch, namespacePrefixes, preservePrefixes,
				CodingMode.BIT_PACKED);
		// byte-packed
		_test(xmlA, null, tch, namespacePrefixes, preservePrefixes,
				CodingMode.BYTE_PACKED);
		// pre-compression
		_test(xmlA, null, tch, namespacePrefixes, preservePrefixes,
				CodingMode.PRE_COMPRESSION);
		// compression
		_test(xmlA, null, tch, namespacePrefixes, preservePrefixes,
				CodingMode.COMPRESSION);
	}

	public void testDecoderAPreservePrefixes() throws Exception {
		boolean namespacePrefixes = false;
		boolean preservePrefixes = true;

		TestContentHandler tch = new TestContentHandlerA(namespacePrefixes,
				preservePrefixes);

		// bit-packed
		_test(xmlA, null, tch, namespacePrefixes, preservePrefixes,
				CodingMode.BIT_PACKED);
		// byte-packed
		_test(xmlA, null, tch, namespacePrefixes, preservePrefixes,
				CodingMode.BYTE_PACKED);
		// pre-compression
		_test(xmlA, null, tch, namespacePrefixes, preservePrefixes,
				CodingMode.PRE_COMPRESSION);
		// compression
		_test(xmlA, null, tch, namespacePrefixes, preservePrefixes,
				CodingMode.COMPRESSION);
	}

	public void testDecoderANone() throws Exception {
		boolean namespacePrefixes = false;
		boolean preservePrefixes = false;

		TestContentHandler tch = new TestContentHandlerA(namespacePrefixes,
				preservePrefixes);

		// bit-packed
		_test(xmlA, null, tch, namespacePrefixes, preservePrefixes,
				CodingMode.BIT_PACKED);
		// byte-packed
		_test(xmlA, null, tch, namespacePrefixes, preservePrefixes,
				CodingMode.BYTE_PACKED);
		// pre-compression
		_test(xmlA, null, tch, namespacePrefixes, preservePrefixes,
				CodingMode.PRE_COMPRESSION);
		// compression
		_test(xmlA, null, tch, namespacePrefixes, preservePrefixes,
				CodingMode.COMPRESSION);
	}

	protected void _test(String xml, String xsdLoc, TestContentHandler tch,
			boolean namespacePrefixes, boolean preservePrefixes,
			CodingMode codingMode) throws SAXException, IOException,
			EXIException {
		try {
			EXIFactory factory = DefaultEXIFactory.newInstance();

			// schema?
			if (xsdLoc != null) {
				GrammarFactory gf = GrammarFactory.newInstance();
				Grammars g = gf.createGrammars(xsdLoc);
				factory.setGrammars(g);
			}

			factory.setCodingMode(codingMode);
			FidelityOptions fo = factory.getFidelityOptions();
			fo.setFidelity(FidelityOptions.FEATURE_PREFIX, preservePrefixes);

			ByteArrayOutputStream os = new ByteArrayOutputStream();

			// write EXI stream
			{
				XMLReader xmlReader = XMLReaderFactory.createXMLReader();

				EXIResult exiResult = new EXIResult(factory);
				exiResult.setOutputStream(os);
				xmlReader.setContentHandler(exiResult.getHandler());

				xmlReader.parse(new InputSource(new StringReader(xml)));
			}

			// read EXI stream
			os.flush();
			InputStream is = new ByteArrayInputStream(os.toByteArray());
			XMLReader exiReader = new SAXFactory(factory).createEXIReader();
			exiReader.setFeature("http://xml.org/sax/features/namespaces",
					namespaces);
			exiReader.setFeature(
					"http://xml.org/sax/features/namespace-prefixes",
					namespacePrefixes);
			exiReader.setContentHandler(tch);

			exiReader.parse(new InputSource(is));
		} catch (Exception e) {
			throw new RuntimeException("namespacePrefixes=" + namespacePrefixes
					+ ", preservePrefixes=" + preservePrefixes
					+ ", codingMode=" + codingMode, e);
		}
	}

}
