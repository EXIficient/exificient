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

import javax.xml.namespace.QName;

import junit.framework.TestCase;

import org.xml.sax.SAXException;

import com.siemens.ct.exi.CodingMode;
import com.siemens.ct.exi.EXIDecoder;
import com.siemens.ct.exi.EXIEncoder;
import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.FidelityOptions;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.grammar.event.EventType;
import com.siemens.ct.exi.helpers.DefaultEXIFactory;

public class SelfContainedTestCase extends TestCase {

	public SelfContainedTestCase(String testName) {
		super(testName);
	}

	/*
	 * <root>
	 *   text
	 *   <sc>text</sc>
	 *   <sc>text</sc>
	 * </root>
	 */
	public void testSelfContained0() throws IOException, SAXException,
			EXIException {
		EXIFactory factory = DefaultEXIFactory.newInstance();

		factory.setFidelityOptions(FidelityOptions.createDefault());
		factory.setCodingMode(CodingMode.BIT_PACKED);
		FidelityOptions fo = factory.getFidelityOptions();
		fo.setFidelity(FidelityOptions.FEATURE_SC, true);
		
		

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		QName root = new QName("", "root");
		QName sc = new QName("", "sc");
		String s = "text";
		
		QName[] scElements = new QName[1];
		scElements[0] = sc;
		factory.setSelfContainedElements(scElements);
		
		int offsetSC1, offsetSC2;
		
		// encoder
		{
			EXIEncoder encoder = factory.createEXIEncoder();
			encoder.setOutput(baos, factory.isEXIBodyOnly());
			String pfx = null;
			encoder.encodeStartDocument();
			encoder.encodeStartElement(root.getNamespaceURI(), root.getLocalPart(),
					pfx);
			encoder.encodeCharacters(s);
			{
				encoder.encodeStartElement(sc.getNamespaceURI(), sc.getLocalPart(),
						pfx);
				
				offsetSC1 = baos.toByteArray().length;
				// System.out.println("SC_1: " + offsetSC1);
				
				encoder.encodeCharacters(s);
				encoder.encodeEndElement();
			}
			{
				encoder.encodeStartElement(sc.getNamespaceURI(), sc.getLocalPart(),
						pfx);
				
				offsetSC2 = baos.toByteArray().length;
				// System.out.println("SC_2: " + offsetSC2);
				
				encoder.encodeCharacters(s);
				encoder.encodeEndElement();
			}
			encoder.encodeEndElement();	//	root
			encoder.encodeEndDocument();
			encoder.flush();
		}
		
		

		// decoder ALL
		{
			EXIDecoder decoder = factory.createEXIDecoder();
			decoder.setInputStream(
					new ByteArrayInputStream(baos.toByteArray()), factory
							.isEXIBodyOnly());

			assertTrue(decoder.next() == EventType.START_DOCUMENT);
			decoder.decodeStartDocument();

			decoder.hasNext();
			assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC);
			decoder.decodeStartElementGeneric();
			assertTrue(decoder.getElementQName().equals(root));
			
			decoder.hasNext();
			assertTrue(decoder.next() == EventType.CHARACTERS_GENERIC_UNDECLARED);
			decoder.decodeCharactersGenericUndeclared();
			assertTrue(s.equals(decoder.getCharactersValue().toString()));

			decoder.hasNext();
			assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC_UNDECLARED);
			//	<sc> #1
			decoder.decodeStartElementGenericUndeclared();
			{
				decoder.hasNext();
				assertTrue(decoder.next() == EventType.SELF_CONTAINED);
				decoder.decodeStartFragmentSelfContained();
				
				decoder.hasNext();
				assertTrue(decoder.next() == EventType.CHARACTERS_GENERIC_UNDECLARED);
				decoder.decodeCharactersGenericUndeclared();
				assertTrue(s.equals(decoder.getCharactersValue().toString()));
				
				decoder.hasNext();
				assertTrue(decoder.next() == EventType.END_ELEMENT);
				decoder.decodeEndElement();
			}
			
			decoder.hasNext();
			assertTrue(decoder.next() == EventType.START_ELEMENT);
			//	<sc> #2
			decoder.decodeStartElement();
			{ 
				decoder.hasNext();
				assertTrue(decoder.next() == EventType.SELF_CONTAINED);
				decoder.decodeStartFragmentSelfContained();
				
				decoder.hasNext();
				assertTrue(decoder.next() == EventType.CHARACTERS_GENERIC_UNDECLARED);
				decoder.decodeCharactersGenericUndeclared();
				assertTrue(s.equals(decoder.getCharactersValue().toString()));
				
				decoder.hasNext();
				assertTrue(decoder.next() == EventType.END_ELEMENT);
				decoder.decodeEndElement();
			}
						
			decoder.hasNext();
			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();

			decoder.hasNext();
			assertTrue(decoder.next() == EventType.END_DOCUMENT);
			decoder.decodeEndDocument();
		}
		
		EXIFactory scEXIFactory = factory.clone();
		scEXIFactory.setFragment(true);
		scEXIFactory.setEXIBodyOnly(true);
		
		int MINUS_BYTE_OFFSET = 3;	// TODO why 3
		
		// decoder SC #1 
		{
			EXIDecoder decoder = scEXIFactory.createEXIDecoder();
			InputStream is = new ByteArrayInputStream(baos.toByteArray());
			is.skip(offsetSC1-MINUS_BYTE_OFFSET);
			decoder.setInputStream(is, scEXIFactory.isEXIBodyOnly());
			
			assertTrue(decoder.next() == EventType.START_DOCUMENT);
			decoder.decodeStartDocument();
			
			decoder.hasNext();
			assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC);
			decoder.decodeStartElementGeneric();
			
			decoder.hasNext();
			assertTrue(decoder.next() == EventType.CHARACTERS_GENERIC_UNDECLARED);
			decoder.decodeCharactersGenericUndeclared();
			assertTrue(s.equals(decoder.getCharactersValue().toString()));
			
			decoder.hasNext();
			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();
			
			decoder.hasNext();
			assertTrue(decoder.next() == EventType.END_DOCUMENT);
			decoder.decodeEndDocument();	
		}
		
		// decoder SC #2
		{
			EXIDecoder decoder = scEXIFactory.createEXIDecoder();
			InputStream is = new ByteArrayInputStream(baos.toByteArray());
			is.skip(offsetSC2-MINUS_BYTE_OFFSET);
			decoder.setInputStream(is, scEXIFactory.isEXIBodyOnly());
			
			assertTrue(decoder.next() == EventType.START_DOCUMENT);
			decoder.decodeStartDocument();
			
			decoder.hasNext();
			assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC);
			decoder.decodeStartElementGeneric();
			
			decoder.hasNext();
			assertTrue(decoder.next() == EventType.CHARACTERS_GENERIC_UNDECLARED);
			decoder.decodeCharactersGenericUndeclared();
			assertTrue(s.equals(decoder.getCharactersValue().toString()));
			
			decoder.hasNext();
			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();
			
			decoder.hasNext();
			assertTrue(decoder.next() == EventType.END_DOCUMENT);
			decoder.decodeEndDocument();	
		}
		
	}

}