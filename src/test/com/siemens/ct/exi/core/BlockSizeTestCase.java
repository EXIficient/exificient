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

package com.siemens.ct.exi.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

import javax.xml.transform.sax.SAXResult;

import org.custommonkey.xmlunit.XMLTestCase;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.siemens.ct.exi.CodingMode;
import com.siemens.ct.exi.EXIDecoder;
import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.api.sax.EXIResult;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.grammar.event.EventType;
import com.siemens.ct.exi.helpers.DefaultEXIFactory;

public class BlockSizeTestCase extends XMLTestCase {

	String xml = "<root atA='a' atB='b' atC='c' atD='d' atE='e'>"
			+ "... TEXT ..."
			+ "</root>";

	
	protected void _test(CodingMode codingMode, int blockSize) throws SAXException, IOException, EXIException {
		try {
			EXIFactory factory = DefaultEXIFactory.newInstance();
			factory.setCodingMode(codingMode);
			factory.setBlockSize(blockSize);
			
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
			EXIDecoder exiDecoder = factory.createEXIDecoder();
			exiDecoder.setInputStream(is, factory.isEXIBodyOnly());

			assertTrue(exiDecoder.next() == EventType.START_DOCUMENT);
			exiDecoder.decodeStartDocument();
			
			assertTrue(exiDecoder.next() == EventType.START_ELEMENT_GENERIC);
			assertTrue(exiDecoder.decodeStartElementGeneric().getLocalPart().equals("root"));
			
			assertTrue(exiDecoder.next() == EventType.ATTRIBUTE_GENERIC_UNDECLARED);	
			assertTrue(exiDecoder.decodeAttributeGenericUndeclared().getLocalPart().equals("atA"));
			assertTrue(exiDecoder.getAttributeValue().equals("a"));

			assertTrue(exiDecoder.next() == EventType.ATTRIBUTE_GENERIC_UNDECLARED);	
			assertTrue(exiDecoder.decodeAttributeGenericUndeclared().getLocalPart().equals("atB"));
			assertTrue(exiDecoder.getAttributeValue().equals("b"));

			assertTrue(exiDecoder.next() == EventType.ATTRIBUTE_GENERIC_UNDECLARED);
			assertTrue(exiDecoder.decodeAttributeGenericUndeclared().getLocalPart().equals("atC"));
			assertTrue(exiDecoder.getAttributeValue().equals("c"));
			
			assertTrue(exiDecoder.next() == EventType.ATTRIBUTE_GENERIC_UNDECLARED);	
			assertTrue(exiDecoder.decodeAttributeGenericUndeclared().getLocalPart().equals("atD"));
			assertTrue(exiDecoder.getAttributeValue().equals("d"));
			
			assertTrue(exiDecoder.next() == EventType.ATTRIBUTE_GENERIC_UNDECLARED);	
			assertTrue(exiDecoder.decodeAttributeGenericUndeclared().getLocalPart().equals("atE"));
			assertTrue(exiDecoder.getAttributeValue().equals("e"));
			
			assertTrue(exiDecoder.next() == EventType.CHARACTERS_GENERIC_UNDECLARED);	
			assertTrue(exiDecoder.decodeCharactersGenericUndeclared().toString().equals("... TEXT ..."));

			assertTrue(exiDecoder.next() == EventType.END_ELEMENT);	
			assertTrue(exiDecoder.decodeEndElement().getLocalPart().equals("root"));
			
			assertTrue(exiDecoder.next() == EventType.END_DOCUMENT);
			exiDecoder.decodeEndDocument();
			
			
		} catch (Exception e) {
			throw new RuntimeException("codingMode="+codingMode + ", blockSize=" + blockSize
					+ ", codingMode="+codingMode, e);
		}
	}
	
	public void testPreCompression2() throws SAXException, IOException, EXIException {
		_test(CodingMode.PRE_COMPRESSION, 2);
	}

	public void testCompression3() throws SAXException, IOException, EXIException {
		_test(CodingMode.COMPRESSION, 3);
	}

	public void testCompression4() throws SAXException, IOException, EXIException {
		_test(CodingMode.COMPRESSION, 4);
	}
	

}
