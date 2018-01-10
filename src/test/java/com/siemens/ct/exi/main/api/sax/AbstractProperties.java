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

package com.siemens.ct.exi.main.api.sax;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;

import org.custommonkey.xmlunit.XMLTestCase;
import org.custommonkey.xmlunit.XMLUnit;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.siemens.ct.exi.core.EXIFactory;
import com.siemens.ct.exi.core.exceptions.EXIException;
import com.siemens.ct.exi.core.helpers.DefaultEXIFactory;
import com.siemens.ct.exi.main.api.sax.SAXFactory;

public abstract class AbstractProperties extends XMLTestCase {
	String xml;

	EXIFactory factory;

	@Override
	protected void setUp() {
		factory = DefaultEXIFactory.newInstance();
	}

	protected String decodeEXIToXML(InputStream isEXI) throws IOException,
			SAXException, TransformerException, EXIException {
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();

		SAXSource exiSource = new SAXSource(new InputSource(isEXI));
		exiSource.setXMLReader(new SAXFactory(factory).createEXIReader());

		ByteArrayOutputStream xmlDecoded = new ByteArrayOutputStream();
		transformer.transform(exiSource, new StreamResult(xmlDecoded));

		return xmlDecoded.toString();
	}

	protected void isXMLEqual(String sXMLDecoded) throws SAXException,
			IOException {
		StringReader control = new StringReader(xml);
		StringReader test = new StringReader(sXMLDecoded);

		XMLUnit.setIgnoreWhitespace(true);

		// Diff diff = compareXML ( control, test );
		// XMLUnit.setNormalize ( true );

		XMLUnit.setIgnoreAttributeOrder(true);

		assertXMLEqual(control, test);
	}

	static final String SIMPLE_XSD = "<schema xmlns='http://www.w3.org/2001/XMLSchema'>"
			+ " <element name='root'>"
			+ "  <complexType>"
			+ "   <sequence maxOccurs='unbounded'>"
			+ "    <element name='strings' type='string' />"
			+ "   </sequence>"
			+ "  </complexType>" + " </element>" + "</schema>";

	static final String SIMPLE_XML = "<root>" + " <strings>a</strings>"
			+ " <strings>b</strings>" + " <strings>c</strings>"
			+ " <strings>a</strings>" + "</root>";

	static final String UNEXPECTED_ROOT_XSD = "<schema xmlns='http://www.w3.org/2001/XMLSchema'>"
			+ " <element name='root'>"
			+ "  <complexType>"
			+ "   <sequence maxOccurs='unbounded'>"
			+ "    <element name='strings' type='string' />"
			+ "   </sequence>"
			+ "  </complexType>" + " </element>" + "</schema>";

	static final String UNEXPECTED_ROOT_XML = "<unknown>" + "?!?!"
			+ "</unknown>";

	static final String XSI_TYPE_XSD = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
			+ " <xs:element name='values'>"
			+ "  <xs:complexType>"
			+ "   <xs:sequence maxOccurs='unbounded'>"
			+ "    <xs:element ref='value' />"
			+ "   </xs:sequence>"
			+ "  </xs:complexType>"
			+ " </xs:element>"
			+ ""
			+ " <xs:element name='value' type='tValue' />"
			+ ""
			+ " <xs:complexType name='tValue' />"
			+ ""
			+ " <xs:complexType name='tDouble'>"
			+ "  <xs:complexContent>"
			+ "   <xs:extension base='tValue'>"
			+ "    <xs:sequence>"
			+ "     <xs:element name='val' type='xs:double' />"
			+ "    </xs:sequence>"
			+ "   </xs:extension>"
			+ "  </xs:complexContent>"
			+ " </xs:complexType>"
			+ ""
			+ " <xs:complexType name='tInteger'>"
			+ "  <xs:complexContent>"
			+ "   <xs:extension base='tValue'>"
			+ "    <xs:sequence>"
			+ "     <xs:element name='val' type='xs:int' />"
			+ "    </xs:sequence>"
			+ "   </xs:extension>"
			+ "  </xs:complexContent>"
			+ " </xs:complexType>"
			+ ""
			+ "</xs:schema>";

	static final String XSI_TYPE_XML = "<values xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' >"
			+ " <value />"
			+ " <value xsi:type='tDouble' >"
			+ "   <val>12.00</val>"
			+ " </value>"
			+ " <value xsi:type='tInteger' >"
			+ "   <val>12</val>"
			+ " </value>"
			+ " <value xsi:type='tDouble' >"
			+ "   <val>1.23</val>" + " </value>" + " <value />" + "</values>";
}
