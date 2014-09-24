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
