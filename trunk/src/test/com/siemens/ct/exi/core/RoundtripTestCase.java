package com.siemens.ct.exi.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import javax.xml.namespace.QName;

import junit.framework.Assert;
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
import com.siemens.ct.exi.context.QNameContext;
import com.siemens.ct.exi.core.container.NamespaceDeclaration;
import com.siemens.ct.exi.grammars.GrammarTest;
import com.siemens.ct.exi.grammars.Grammars;
import com.siemens.ct.exi.grammars.event.EventType;
import com.siemens.ct.exi.helpers.DefaultEXIFactory;
import com.siemens.ct.exi.values.IntegerValue;
import com.siemens.ct.exi.values.StringValue;
import com.siemens.ct.exi.values.Value;
import com.siemens.ct.exi.values.ValueType;

public class RoundtripTestCase extends TestCase {
	
	// Type-aware roundtrip
	public void test1() throws Exception {
		String schema = "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'>"
			+ " <xs:element name='root' type='xs:int' nillable='true' >"
			+ " </xs:element>" + "</xs:schema>";

		Grammars grammar = GrammarTest.getGrammarFromSchemaAsString(schema);
		
		EXIFactory factory = DefaultEXIFactory.newInstance();
		factory.setGrammars(grammar);
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
		QName qnameSE = dec.decodeStartElement().getQName();
		enc2.encodeStartElement(qnameSE);
		assertTrue(qnameSE.equals(qnameRoot));
		assertTrue(dec.next() == EventType.CHARACTERS);
		Value val = dec.decodeCharacters();
		enc2.encodeCharacters(val);
		assertTrue(val.getValueType() == ValueType.INTEGER_INT);
		assertTrue(dec.next() == EventType.END_ELEMENT);
		QName qnameEE = dec.decodeEndElement().getQName();
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

		Grammars grammar = GrammarTest.getGrammarFromSchemaAsString(schema);
		
		EXIFactory factory = DefaultEXIFactory.newInstance();
		factory.setGrammars(grammar);
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
		QName qnameSE = dec.decodeStartElement().getQName();
		enc2.encodeStartElement(qnameSE);
		assertTrue(qnameSE.equals(qnameRoot));
		assertTrue(dec.next() == EventType.CHARACTERS);
		Value val = dec.decodeCharacters();
		enc2.encodeCharacters(val);
		// assertTrue(val.getValueType() == ValueType.INT_INTEGER);
		assertTrue(val.getValueType() == ValueType.STRING);
		assertTrue(dec.next() == EventType.END_ELEMENT);
		QName qnameEE = dec.decodeEndElement().getQName();
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
		Grammars grammar = gf.createGrammars(xsd);
	
		EXIFactory factory = DefaultEXIFactory.newInstance();
		factory.setCodingMode(CodingMode.BYTE_PACKED);
		factory.setGrammars(grammar);
		factory.setFidelityOptions(FidelityOptions.createStrict());
		
		/*
		 * Encode to EXI
		 */
		EXIBodyEncoder enc1 = factory.createEXIBodyEncoder();
		ByteArrayOutputStream os1 = new ByteArrayOutputStream();
		enc1.setOutputStream(os1);
		XMLReader xmlReader = XMLReaderFactory.createXMLReader();
		EXIResult exiResult = new EXIResult(factory);
		exiResult.setOutputStream(os1);
		xmlReader.setContentHandler(exiResult.getHandler());
		xmlReader.parse(new InputSource(xml));
		
		/*
		 * Decode EXI and Encode EXI again
		 */
		EXIStreamDecoder streamDecoder = new EXIStreamDecoder(factory);
		EXIBodyDecoder dec = streamDecoder.decodeHeader(new ByteArrayInputStream(os1.toByteArray()));
		
		EXIStreamEncoder streamEncoder = new EXIStreamEncoder(factory);
		ByteArrayOutputStream os2 = new ByteArrayOutputStream();
		EXIBodyEncoder enc2 = streamEncoder.encodeHeader(os2);
		
		EventType event;
		while( (event = dec.next()) != null) {
			switch(event) {
			case START_DOCUMENT:
				dec.decodeStartDocument();
				enc2.encodeStartDocument();
				break;
			case START_ELEMENT:
				QNameContext se = dec.decodeStartElement();
				enc2.encodeStartElement(se.getQName());
				break;
			case ATTRIBUTE:
				QName at = dec.decodeAttribute().getQName();
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
		EXIResult exiResult = new EXIResult(factory);
		exiResult.setOutputStream(os1);
		xmlReader.setContentHandler(exiResult.getHandler());
		xmlReader.parse(new InputSource(xml));
		
		/*
		 * Decode EXI and Encode EXI again
		 */
		EXIStreamDecoder streamDecoder = new EXIStreamDecoder(factory);
		EXIBodyDecoder dec = streamDecoder.decodeHeader(new ByteArrayInputStream(os1.toByteArray()));
		
		EXIStreamEncoder streamEncoder = new EXIStreamEncoder(factory);
		ByteArrayOutputStream os2 = new ByteArrayOutputStream();
		EXIBodyEncoder enc2 = streamEncoder.encodeHeader(os2);
		
		EventType event;
		while( (event = dec.next()) != null) {
			switch(event) {
			case START_DOCUMENT:
				dec.decodeStartDocument();
				enc2.encodeStartDocument();
				break;
			case START_ELEMENT_GENERIC:
				// QName se = dec.decodeStartElementGeneric();
				QName se = dec.decodeStartElement().getQName();
				assertTrue(se.getLocalPart().equals("a"));
				enc2.encodeStartElement(se);
				break;
			case CHARACTERS_GENERIC_UNDECLARED:
				// Value chv = dec.decodeCharactersGenericUndeclared();
				Value chv = dec.decodeCharacters();
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
		EXIResult exiResult = new EXIResult(factory);
		exiResult.setOutputStream(os1);
		xmlReader.setContentHandler(exiResult.getHandler());
		xmlReader.parse(new InputSource(xml));
		
		/*
		 * Decode EXI and Encode EXI again
		 */
		EXIStreamDecoder streamDecoder = new EXIStreamDecoder(factory);
		EXIBodyDecoder dec = streamDecoder.decodeHeader(new ByteArrayInputStream(os1.toByteArray()));
		
		EXIStreamEncoder streamEncoder = new EXIStreamEncoder(factory);
		ByteArrayOutputStream os2 = new ByteArrayOutputStream();
		EXIBodyEncoder enc2 = streamEncoder.encodeHeader(os2);
		
		EventType event;
		while( (event = dec.next()) != null) {
			switch(event) {
			case START_DOCUMENT:
				dec.decodeStartDocument();
				enc2.encodeStartDocument();
				break;
			case START_ELEMENT_GENERIC:
				// QName se = dec.decodeStartElementGeneric();
				QName se = dec.decodeStartElement().getQName();
				assertTrue(se.getLocalPart().equals("a"));
				enc2.encodeStartElement(se);
				break;
			case CHARACTERS_GENERIC_UNDECLARED:
				// Value chv = dec.decodeCharactersGenericUndeclared();
				Value chv = dec.decodeCharacters();
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
	
	public void testBugID3420173() throws Exception {
		EXIFactory factory = DefaultEXIFactory.newInstance();
		factory.setFidelityOptions(FidelityOptions.createAll());

		QName name = new QName("http://foo", "alice", "foo");
		
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		EXIBodyEncoder encoder = new EXIStreamEncoder(factory).encodeHeader(output);
		encoder.encodeStartDocument();
		encoder.encodeStartElement(name);
		encoder.encodeNamespaceDeclaration(name.getNamespaceURI(), name.getPrefix());
		encoder.encodeCharacters(new StringValue("bob"));
		encoder.encodeEndElement();
		encoder.encodeEndDocument();
		encoder.flush();
		output.close();

		ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
		EXIBodyDecoder decoder = new EXIStreamDecoder(factory).decodeHeader(input);
		Assert.assertEquals(EventType.START_DOCUMENT, decoder.next());
		decoder.decodeStartDocument();
		Assert.assertEquals(EventType.START_ELEMENT_GENERIC, decoder.next());
		QName actual = decoder.decodeStartElement().getQName();
		// prefix not known yet
		// Assert.assertEquals(name.getPrefix(), actual.getPrefix()); // bang!
		Assert.assertEquals(name, actual);
		Assert.assertEquals(EventType.NAMESPACE_DECLARATION, decoder.next());
		NamespaceDeclaration nsdecl = decoder.decodeNamespaceDeclaration();
		Assert.assertEquals(name.getNamespaceURI(), nsdecl.namespaceURI);
		Assert.assertEquals(name.getPrefix(), nsdecl.prefix);
		Assert.assertEquals(EventType.CHARACTERS_GENERIC_UNDECLARED, decoder.next());
		Assert.assertEquals("bob", decoder.decodeCharacters().toString());
		Assert.assertEquals(EventType.END_ELEMENT, decoder.next());
		// prefix is known as long as EndElement wasn't called
		Assert.assertEquals(name.getPrefix(), decoder.getElementPrefix());
		actual = decoder.decodeEndElement().getQName();
		Assert.assertEquals(name, actual);
		// Assert.assertEquals(name.getPrefix(), actual.getPrefix()); // bang!
		Assert.assertEquals(EventType.END_DOCUMENT, decoder.next());
		decoder.decodeEndDocument();
		Assert.assertNull(decoder.next());
		
	}
}
