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

package com.siemens.ct.exi.core.sax;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.transform.sax.SAXResult;

import org.custommonkey.xmlunit.XMLTestCase;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import com.siemens.ct.exi.CodingMode;
import com.siemens.ct.exi.Constants;
import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.FidelityOptions;
import com.siemens.ct.exi.api.sax.EXIResult;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.helpers.DefaultEXIFactory;

public class SAXDecoderTestCase extends XMLTestCase {

	/* does EXI make sense otherwise ?*/
	static final boolean namespaces = true;
	
	String xml = "<foo:root xmlns:foo='urn:foo' xmlns:fooX='urn:fooX' at1='x'>"
			+ "   <bla:el xmlns:bla='urn:bla' buu:at2='y' fooX:at3='z' xmlns:buu='urn:buu'>w</bla:el>"
			+ "</foo:root>";

	class TestContentHandler extends DefaultHandler {
		final boolean namespacePrefixes;
		final boolean preservePrefixes;
		int openElements = 0;
		List<String>[] pfxMappings;
		@SuppressWarnings("unchecked")
		public TestContentHandler(boolean namespacePrefixes, boolean preservePrefixes) {
			this.namespacePrefixes = namespacePrefixes;
			this.preservePrefixes = preservePrefixes;
			pfxMappings = new ArrayList[2];
			pfxMappings[0] = new ArrayList<String>();
			pfxMappings[1] = new ArrayList<String>();
		}
		public void startElement(String uri, String localName, String qName,
				Attributes atts) throws SAXException {
			if (namespaces) {
				if (openElements == 0) {
					// [xml] & urn:foo (+urn:fooX)
					assertTrue(pfxMappings[openElements].size() >= 1);
					assertTrue(pfxMappings[openElements].contains("urn:foo"));
					// assertTrue(pfxMappings[openElements].contains("urn:fooX"));
				} else if (openElements == 1) {
					// urn:bla & urn:buu (+urn:fooX)
					assertTrue(pfxMappings[openElements].size() >= 2);
					assertTrue(pfxMappings[openElements].contains("urn:bla"));
					assertTrue(pfxMappings[openElements].contains("urn:buu"));
					// assertTrue(pfxMappings[openElements].contains("urn:fooX"));	
				}
			}
			
			openElements++;
			if (openElements == 1)  {
				// element
				assertTrue("urn:foo".equals(uri));
				assertTrue("root".equals(localName));
				if (namespacePrefixes) {
					assertTrue(!qName.equals(Constants.EMPTY_STRING));
					if(preservePrefixes) {
						assertTrue(qName.equals("foo:root"));
					}
				} else {
					assertTrue(qName.equals(Constants.EMPTY_STRING));
				}
				// attributes, at1='x'
				assertTrue(atts.getLength() == 1);
				assertTrue("".equals(atts.getURI(0)));
				assertTrue("at1".equals(atts.getLocalName(0)));
				assertTrue("x".equals(atts.getValue(0)));
				if (namespacePrefixes) {
					if(preservePrefixes) {
						assertTrue(atts.getQName(0).equals("at1"));
					}
				} else {
					assertTrue(atts.getQName(0).equals(Constants.EMPTY_STRING));
				}
			} else if(openElements == 2) {
				// element
				assertTrue("urn:bla".equals(uri));
				assertTrue("el".equals(localName));
				if (namespacePrefixes) {
					assertTrue(!qName.equals(Constants.EMPTY_STRING));
					if(preservePrefixes) {
						assertTrue(qName.equals("bla:el"));
					}
				} else {
					assertTrue(qName.equals(Constants.EMPTY_STRING));
				}
				// attributes,  buu:at2='y' fooX:at3='z'
				assertTrue(atts.getLength() == 2);
				assertTrue("urn:buu".equals(atts.getURI(0)));
				assertTrue("at2".equals(atts.getLocalName(0)));
				assertTrue("y".equals(atts.getValue(0)));
				assertTrue("urn:fooX".equals(atts.getURI(1)));
				assertTrue("at3".equals(atts.getLocalName(1)));
				assertTrue("z".equals(atts.getValue(1)));
				if (namespacePrefixes) {
					assertTrue(!atts.getQName(0).equals(Constants.EMPTY_STRING));
					assertTrue(!atts.getQName(1).equals(Constants.EMPTY_STRING));
					if(preservePrefixes) {
						assertTrue(atts.getQName(0).equals("buu:at2"));
						assertTrue(atts.getQName(1).equals("fooX:at3"));
					}
				} else {
					assertTrue(atts.getQName(0).equals(Constants.EMPTY_STRING));
				}
			}
		}
		public void endElement(String uri, String localName, String qName)
				throws SAXException {
			if (openElements == 1)  {
				assertTrue("urn:foo".equals(uri));
				assertTrue("root".equals(localName));
				if (namespacePrefixes) {
					assertTrue(!qName.equals(Constants.EMPTY_STRING));
					if(preservePrefixes) {
						assertTrue(qName.equals("foo:root"));
					}
				} else {
					assertTrue(qName.equals(Constants.EMPTY_STRING));
				}
			} else if(openElements == 2) {
				assertTrue("urn:bla".equals(uri));
				assertTrue("el".equals(localName));
				if (namespacePrefixes) {
					assertTrue(!qName.equals(Constants.EMPTY_STRING));
					if(preservePrefixes) {
						assertTrue(qName.equals("bla:el"));
					}
				} else {
					assertTrue(qName.equals(Constants.EMPTY_STRING));
				}
			}
			openElements--;
		}
		public void startPrefixMapping(String prefix, String uri)
				throws SAXException {
			if (openElements == 0)  {
				pfxMappings[0].add(uri);
				// xmlns:foo='urn:foo' 
				if (uri.equals("urn:foo")) {
					if(preservePrefixes) {
						assertTrue(prefix.equals("foo"));
					} else {
						assertTrue(prefix.length() > 0);
					}
				// xmlns:fooX='urn:fooX'
				} else if(uri.equals("urn:fooX")) {
					if(preservePrefixes) {
						assertTrue(prefix.equals("fooX"));
					} else {
						assertTrue(prefix.length() > 0);
					}
				} else if (uri.equals("http://www.w3.org/XML/1998/namespace")) {
					assertTrue(prefix.equals("xml"));
				} else if (uri.equals(XMLConstants.NULL_NS_URI)) {
					assertTrue(prefix.equals(XMLConstants.DEFAULT_NS_PREFIX));
				} else {
					fail("No exptected URI: " + uri);
				}
				// System.out.println("0 " + prefix + " --> " + uri);
			} else if (openElements == 1)  {
				pfxMappings[1].add(uri);
				// xmlns:bla='urn:bla' 
				if (uri.equals("urn:bla")) {
					if(preservePrefixes) {
						assertTrue(prefix.equals("bla"));
					} else {
						assertTrue(prefix.length() > 0);
					}
				// xmlns:buu='urn:buu'
				} else if(uri.equals("urn:buu")) {
					if(preservePrefixes) {
						assertTrue(prefix.equals("buu"));
					} else {
						assertTrue(prefix.length() > 0);
					}
				} else if(!preservePrefixes && uri.equals("urn:fooX")) {
					assertTrue(prefix.length() > 0);
				} else {
					fail("No exptected URI: " + uri);
				}
				// System.out.println("1 " + prefix + " --> " + uri);
			}
		}
		public void endPrefixMapping(String prefix) throws SAXException {
			if (openElements == 0)  {
				if (preservePrefixes) {
					// assertTrue(prefix.equals("foo") || prefix.equals("fooX"));
				}
			} else if (openElements == 1)  {
				
			}
		}
	}

	public void testDecoderPrefixesAndPreservePrefixes() throws Exception {
		boolean namespacePrefixes = true;
		boolean preservePrefixes = true;
		
		// bit-packed
		_test(namespacePrefixes, preservePrefixes, CodingMode.BIT_PACKED);
		// byte-packed
		_test(namespacePrefixes, preservePrefixes, CodingMode.BYTE_PACKED);
		// pre-compression
		_test(namespacePrefixes, preservePrefixes, CodingMode.PRE_COMPRESSION);
		// compression
		_test(namespacePrefixes, preservePrefixes, CodingMode.COMPRESSION);
	}
	
	public void testDecoderPrefixes() throws Exception {
		boolean namespacePrefixes = true;
		boolean preservePrefixes = false;
		
		// bit-packed
		_test(namespacePrefixes, preservePrefixes, CodingMode.BIT_PACKED);
		// byte-packed
		_test(namespacePrefixes, preservePrefixes, CodingMode.BYTE_PACKED);
		// pre-compression
		_test(namespacePrefixes, preservePrefixes, CodingMode.PRE_COMPRESSION);
		// compression
		_test(namespacePrefixes, preservePrefixes, CodingMode.COMPRESSION);
	}
	
	public void testDecoderPreservePrefixes() throws Exception {
		boolean namespacePrefixes = false;
		boolean preservePrefixes = true;
		
		// bit-packed
		_test(namespacePrefixes, preservePrefixes, CodingMode.BIT_PACKED);
		// byte-packed
		_test(namespacePrefixes, preservePrefixes, CodingMode.BYTE_PACKED);
		// pre-compression
		_test(namespacePrefixes, preservePrefixes, CodingMode.PRE_COMPRESSION);
		// compression
		_test(namespacePrefixes, preservePrefixes, CodingMode.COMPRESSION);
	}
	
	public void testDecoderNone() throws Exception {
		boolean namespacePrefixes = false;
		boolean preservePrefixes = false;
		
		// bit-packed
		_test(namespacePrefixes, preservePrefixes, CodingMode.BIT_PACKED);
		// byte-packed
		_test(namespacePrefixes, preservePrefixes, CodingMode.BYTE_PACKED);
		// pre-compression
		_test(namespacePrefixes, preservePrefixes, CodingMode.PRE_COMPRESSION);
		// compression
		_test(namespacePrefixes, preservePrefixes, CodingMode.COMPRESSION);
	}
	
	protected void _test(boolean namespacePrefixes, boolean preservePrefixes, CodingMode codingMode) throws SAXException, IOException, EXIException {
		try {
			EXIFactory factory = DefaultEXIFactory.newInstance();
			factory.setCodingMode(codingMode);
			FidelityOptions fo = factory.getFidelityOptions();
			fo.setFidelity(FidelityOptions.FEATURE_PREFIX, preservePrefixes);
			
			ByteArrayOutputStream os = new ByteArrayOutputStream();

			// write EXI stream
			{
				XMLReader xmlReader = XMLReaderFactory.createXMLReader();

				SAXResult saxResult = new EXIResult(os, factory);
				xmlReader.setContentHandler(saxResult.getHandler());

				xmlReader.parse(new InputSource(new StringReader(xml)));
			}

			// read EXI stream
			os.flush();
			InputStream is = new ByteArrayInputStream(os.toByteArray());
			XMLReader exiReader = factory.createEXIReader();
			exiReader.setFeature("http://xml.org/sax/features/namespaces", namespaces);
			exiReader.setFeature("http://xml.org/sax/features/namespace-prefixes",
					namespacePrefixes);
			exiReader.setContentHandler(new TestContentHandler(namespacePrefixes, preservePrefixes));

			exiReader.parse(new InputSource(is));
		} catch (Exception e) {
			throw new RuntimeException("namespacePrefixes="+namespacePrefixes + ", preservePrefixes=" + preservePrefixes
					+ ", codingMode="+codingMode, e);
		}
	}
	

}
