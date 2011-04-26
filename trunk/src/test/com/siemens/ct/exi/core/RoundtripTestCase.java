package com.siemens.ct.exi.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import javax.xml.namespace.QName;
import javax.xml.transform.sax.SAXResult;

import junit.framework.TestCase;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.siemens.ct.exi.CodingMode;
import com.siemens.ct.exi.EXIBodyDecoder;
import com.siemens.ct.exi.EXIBodyEncoder;
import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.EXIStreamDecoder;
import com.siemens.ct.exi.EXIStreamEncoder;
import com.siemens.ct.exi.EncodingOptions;
import com.siemens.ct.exi.FidelityOptions;
import com.siemens.ct.exi.GrammarFactory;
import com.siemens.ct.exi.api.sax.EXIResult;
import com.siemens.ct.exi.grammar.Grammar;
import com.siemens.ct.exi.grammar.GrammarTest;
import com.siemens.ct.exi.grammar.event.EventType;
import com.siemens.ct.exi.helpers.DefaultEXIFactory;
import com.siemens.ct.exi.values.IntegerValue;
import com.siemens.ct.exi.values.Value;
import com.siemens.ct.exi.values.ValueType;

public class RoundtripTestCase extends TestCase {
	
	// Type-aware roundtrip
	public void test1() throws Exception {
		String schema = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
			+ " <xs:element name='root' type='xs:int' nillable='true' >"
			+ " </xs:element>" + "</xs:schema>";

		Grammar grammar = GrammarTest.getGrammarFromSchemaAsString(schema);
		
		EXIFactory factory = DefaultEXIFactory.newInstance();
		factory.setGrammar(grammar);
		factory.setFidelityOptions(FidelityOptions.createStrict());
		
		QName qnameRoot = new QName("", "root");
		Value vint = IntegerValue.valueOf(654);
		
		/*
		 * Encoder
		 */
		EXIBodyEncoder enc1 = factory.createEXIBodyEncoder();
		ByteArrayOutputStream os1 = new ByteArrayOutputStream();
		enc1.setOutputStream(os1);
		
		enc1.encodeStartDocument();
		enc1.encodeStartElement(qnameRoot);
		enc1.encodeCharacters(vint);
		enc1.encodeEndElement();
		enc1.encodeEndDocument();
		enc1.flush();
		
		/*
		 * Decode and Encode again
		 */
		EXIBodyEncoder enc2 = factory.createEXIBodyEncoder();
		ByteArrayOutputStream os2 = new ByteArrayOutputStream();
		enc2.setOutputStream(os2);
		
		EXIBodyDecoder dec = factory.createEXIBodyDecoder();
		dec.setInputStream(new ByteArrayInputStream(os1.toByteArray()));
		
		assertTrue(dec.next() == EventType.START_DOCUMENT);
		dec.decodeStartDocument();
		enc2.encodeStartDocument();
		assertTrue(dec.next() == EventType.START_ELEMENT);
		QName qnameSE = dec.decodeStartElement();
		enc2.encodeStartElement(qnameSE);
		assertTrue(qnameSE.equals(qnameRoot));
		assertTrue(dec.next() == EventType.CHARACTERS);
		Value val = dec.decodeCharacters();
		enc2.encodeCharacters(val);
		assertTrue(val.getValueType() == ValueType.INTEGER_INT);
		assertTrue(dec.next() == EventType.END_ELEMENT);
		QName qnameEE = dec.decodeEndElement();
		assertTrue(qnameEE.equals(qnameRoot));
		enc2.encodeEndElement();
		assertTrue(dec.next() == EventType.END_DOCUMENT);
		dec.decodeEndDocument();
		enc2.encodeEndDocument();
		enc2.flush();
		
		/*
		 * Check equality of streams
		 */
		assertTrue(os1.size() == os2.size());
		assertTrue(Arrays.equals(os1.toByteArray(),os2.toByteArray()));
	}
	
	// lexical type-aware roundtrip
	public void testLex1() throws Exception {
		String schema = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
			+ " <xs:element name='root' type='xs:int' nillable='true' >"
			+ " </xs:element>" + "</xs:schema>";

		Grammar grammar = GrammarTest.getGrammarFromSchemaAsString(schema);
		
		EXIFactory factory = DefaultEXIFactory.newInstance();
		factory.setGrammar(grammar);
		FidelityOptions fo = factory.getFidelityOptions();
		fo.setFidelity(FidelityOptions.FEATURE_LEXICAL_VALUE, true);
		
		QName qnameRoot = new QName("", "root");
		Value vint = IntegerValue.valueOf(654);
		
		/*
		 * Encoder
		 */
		EXIBodyEncoder enc1 = factory.createEXIBodyEncoder();
		ByteArrayOutputStream os1 = new ByteArrayOutputStream();
		enc1.setOutputStream(os1);
		
		enc1.encodeStartDocument();
		enc1.encodeStartElement(qnameRoot);
		enc1.encodeCharacters(vint);
		enc1.encodeEndElement();
		enc1.encodeEndDocument();
		enc1.flush();
		
		/*
		 * Decode and Encode again
		 */
		EXIBodyEncoder enc2 = factory.createEXIBodyEncoder();
		ByteArrayOutputStream os2 = new ByteArrayOutputStream();
		enc2.setOutputStream(os2);
		
		EXIBodyDecoder dec = factory.createEXIBodyDecoder();
		dec.setInputStream(new ByteArrayInputStream(os1.toByteArray()));
		
		assertTrue(dec.next() == EventType.START_DOCUMENT);
		dec.decodeStartDocument();
		enc2.encodeStartDocument();
		assertTrue(dec.next() == EventType.START_ELEMENT);
		QName qnameSE = dec.decodeStartElement();
		enc2.encodeStartElement(qnameSE);
		assertTrue(qnameSE.equals(qnameRoot));
		assertTrue(dec.next() == EventType.CHARACTERS);
		Value val = dec.decodeCharacters();
		enc2.encodeCharacters(val);
		// assertTrue(val.getValueType() == ValueType.INT_INTEGER);
		assertTrue(val.getValueType() == ValueType.STRING);
		assertTrue(dec.next() == EventType.END_ELEMENT);
		QName qnameEE = dec.decodeEndElement();
		assertTrue(qnameEE.equals(qnameRoot));
		enc2.encodeEndElement();
		assertTrue(dec.next() == EventType.END_DOCUMENT);
		dec.decodeEndDocument();
		enc2.encodeEndDocument();
		enc2.flush();
		
		/*
		 * Check equality of streams
		 */
		assertTrue(os1.size() == os2.size());
		assertTrue(Arrays.equals(os1.toByteArray(),os2.toByteArray()));
	}

	// type-aware roundtrip of notebook example
	public void testNotebook() throws Exception {
		String xsd = "./data/W3C/PrimerNotebook/notebook.xsd";
		String xml = "./data/W3C/PrimerNotebook/notebook.xml";
		
		GrammarFactory gf = GrammarFactory.newInstance();
		Grammar grammar = gf.createGrammar(xsd);
	
		EXIFactory factory = DefaultEXIFactory.newInstance();
		factory.setCodingMode(CodingMode.BYTE_PACKED);
		factory.setGrammar(grammar);
		factory.setFidelityOptions(FidelityOptions.createStrict());
		
		/*
		 * Encode to EXI
		 */
		EXIBodyEncoder enc1 = factory.createEXIBodyEncoder();
		ByteArrayOutputStream os1 = new ByteArrayOutputStream();
		enc1.setOutputStream(os1);
		XMLReader xmlReader = XMLReaderFactory.createXMLReader();
		SAXResult saxResult = new EXIResult(os1, factory);
		xmlReader.setContentHandler(saxResult.getHandler());
		xmlReader.parse(new InputSource(xml));
		
		/*
		 * Decode EXI and Encode EXI again
		 */
		EXIStreamDecoder streamDecoder = new EXIStreamDecoder();
		EXIBodyDecoder dec = streamDecoder.decodeHeader(factory, new ByteArrayInputStream(os1.toByteArray()));
		
		EXIStreamEncoder streamEncoder = new EXIStreamEncoder();
		ByteArrayOutputStream os2 = new ByteArrayOutputStream();
		EXIBodyEncoder enc2 = streamEncoder.encodeHeader(factory, os2);
		
		EventType event;
		while( (event = dec.next()) != null) {
			switch(event) {
			case START_DOCUMENT:
				dec.decodeStartDocument();
				enc2.encodeStartDocument();
				break;
			case START_ELEMENT:
				QName se = dec.decodeStartElement();
				enc2.encodeStartElement(se);
				break;
			case ATTRIBUTE:
				QName at = dec.decodeAttribute();
				Value atv = dec.getAttributeValue();
				enc2.encodeAttribute(at, atv);
				break;
			case CHARACTERS:
				Value chv = dec.decodeCharacters();
				enc2.encodeCharacters(chv);
				break;
			case END_ELEMENT:
				dec.decodeEndElement();
				enc2.encodeEndElement();
				break;
			case END_DOCUMENT:
				dec.decodeEndDocument();
				enc2.encodeEndDocument();
				break;
			default:
				throw new RuntimeException("Unexpected event: " + event);
			}
		}
		
		/*
		 * Check equality of streams
		 */
		assertTrue(os1.size() == os2.size());
		assertTrue(Arrays.equals(os1.toByteArray(),os2.toByteArray()));
	}

	
	// CDATA, BYTE_PACKED, INCLUDE_COOKIE, INCLUDE_OPTIONS 
	public void testBugID3290090_a() throws Exception {
		String xml = "./data/bugs/ID3290090/test.xml";
	
		EXIFactory factory = DefaultEXIFactory.newInstance();
		factory.setCodingMode(CodingMode.BYTE_PACKED);
		EncodingOptions eo = factory.getEncodingOptions();
		eo.setOption(EncodingOptions.INCLUDE_COOKIE);
		eo.setOption(EncodingOptions.INCLUDE_OPTIONS);
		
		/*
		 * Encode to EXI
		 */
		EXIBodyEncoder enc1 = factory.createEXIBodyEncoder();
		ByteArrayOutputStream os1 = new ByteArrayOutputStream();
		enc1.setOutputStream(os1);
		XMLReader xmlReader = XMLReaderFactory.createXMLReader();
		SAXResult saxResult = new EXIResult(os1, factory);
		xmlReader.setContentHandler(saxResult.getHandler());
		xmlReader.parse(new InputSource(xml));
		
		/*
		 * Decode EXI and Encode EXI again
		 */
		EXIStreamDecoder streamDecoder = new EXIStreamDecoder();
		EXIBodyDecoder dec = streamDecoder.decodeHeader(factory, new ByteArrayInputStream(os1.toByteArray()));
		
		EXIStreamEncoder streamEncoder = new EXIStreamEncoder();
		ByteArrayOutputStream os2 = new ByteArrayOutputStream();
		EXIBodyEncoder enc2 = streamEncoder.encodeHeader(factory, os2);
		
		EventType event;
		while( (event = dec.next()) != null) {
			switch(event) {
			case START_DOCUMENT:
				dec.decodeStartDocument();
				enc2.encodeStartDocument();
				break;
			case START_ELEMENT_GENERIC:
				QName se = dec.decodeStartElementGeneric();
				assertTrue(se.getLocalPart().equals("a"));
				enc2.encodeStartElement(se);
				break;
			case CHARACTERS_GENERIC_UNDECLARED:
				Value chv = dec.decodeCharactersGenericUndeclared();
				assertTrue(chv.toString().equals("x < 0"));
				enc2.encodeCharacters(chv);
				break;
			case END_ELEMENT:
				dec.decodeEndElement();
				enc2.encodeEndElement();
				break;
			case END_DOCUMENT:
				dec.decodeEndDocument();
				enc2.encodeEndDocument();
				break;
			default:
				throw new RuntimeException("Unexpected event: " + event);
			}
		}
		
		/*
		 * Check equality of streams
		 */
		assertTrue(os1.size() == os2.size());
		assertTrue(Arrays.equals(os1.toByteArray(),os2.toByteArray()));
	}

	// CDATA BYTE_PACKED, INCLUDE_COOKIE, INCLUDE_OPTIONS, PRESERVE_COMMENTS
	public void testBugID3290090_b() throws Exception {
		String xml = "./data/bugs/ID3290090/test.xml";
	
		EXIFactory factory = DefaultEXIFactory.newInstance();
		factory.setCodingMode(CodingMode.BYTE_PACKED);
		EncodingOptions eo = factory.getEncodingOptions();
		eo.setOption(EncodingOptions.INCLUDE_COOKIE);
		eo.setOption(EncodingOptions.INCLUDE_OPTIONS);
		
		/* with comments */
		FidelityOptions fo = factory.getFidelityOptions();
		fo.setFidelity(FidelityOptions.FEATURE_COMMENT, true);
		
		
		/*
		 * Encode to EXI
		 */
		EXIBodyEncoder enc1 = factory.createEXIBodyEncoder();
		ByteArrayOutputStream os1 = new ByteArrayOutputStream();
		enc1.setOutputStream(os1);
		XMLReader xmlReader = XMLReaderFactory.createXMLReader();
		SAXResult saxResult = new EXIResult(os1, factory);
		xmlReader.setContentHandler(saxResult.getHandler());
		xmlReader.parse(new InputSource(xml));
		
		/*
		 * Decode EXI and Encode EXI again
		 */
		EXIStreamDecoder streamDecoder = new EXIStreamDecoder();
		EXIBodyDecoder dec = streamDecoder.decodeHeader(factory, new ByteArrayInputStream(os1.toByteArray()));
		
		EXIStreamEncoder streamEncoder = new EXIStreamEncoder();
		ByteArrayOutputStream os2 = new ByteArrayOutputStream();
		EXIBodyEncoder enc2 = streamEncoder.encodeHeader(factory, os2);
		
		EventType event;
		while( (event = dec.next()) != null) {
			switch(event) {
			case START_DOCUMENT:
				dec.decodeStartDocument();
				enc2.encodeStartDocument();
				break;
			case START_ELEMENT_GENERIC:
				QName se = dec.decodeStartElementGeneric();
				assertTrue(se.getLocalPart().equals("a"));
				enc2.encodeStartElement(se);
				break;
			case CHARACTERS_GENERIC_UNDECLARED:
				Value chv = dec.decodeCharactersGenericUndeclared();
				assertTrue(chv.toString().equals("x < 0"));
				enc2.encodeCharacters(chv);
				break;
			case END_ELEMENT:
				dec.decodeEndElement();
				enc2.encodeEndElement();
				break;
			case END_DOCUMENT:
				dec.decodeEndDocument();
				enc2.encodeEndDocument();
				break;
			default:
				throw new RuntimeException("Unexpected event: " + event);
			}
		}
		
		/*
		 * Check equality of streams
		 */
		assertTrue(os1.size() == os2.size());
		assertTrue(Arrays.equals(os1.toByteArray(),os2.toByteArray()));
	}
}
