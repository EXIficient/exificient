/*
 * Copyright (c) 2007-2016 Siemens AG
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

package com.siemens.ct.exi.api.sax;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.StringReader;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class SchemaLessProperties extends AbstractProperties {

	private void encodeSchemaLessToEXI(OutputStream osEXI) throws Exception {
		// start encoding process
		XMLReader xmlReader = XMLReaderFactory.createXMLReader();

		EXIResult exiResult = new EXIResult(factory);
		exiResult.setOutputStream(osEXI);
		xmlReader.setContentHandler(exiResult.getHandler());

		xmlReader.parse(new InputSource(new StringReader(xml)));
	}

	private void startTest() throws Exception {
		// encode
		ByteArrayOutputStream osEXI = new ByteArrayOutputStream();
		encodeSchemaLessToEXI(osEXI);

		// reverse streams
		ByteArrayInputStream isEXI = new ByteArrayInputStream(
				osEXI.toByteArray());

		// decode
		String sXMLDecoded = decodeEXIToXML(isEXI);
		// System.out.println(sXMLDecoded);

		// equal ?
		isXMLEqual(sXMLDecoded);
	}

	public void testXsiType() throws Exception {
		xml = XSI_TYPE_XML;

		startTest();
	}

	public void testSimple1() throws Exception {
		xml = SIMPLE_XML;

		startTest();
	}

	public void testUnexpectedRoot() throws Exception {
		xml = UNEXPECTED_ROOT_XML;

		startTest();
	}

}
