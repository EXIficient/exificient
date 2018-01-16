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

package com.siemens.ct.exi.main.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;

import junit.framework.TestCase;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.siemens.ct.exi.core.CodingMode;
import com.siemens.ct.exi.core.Constants;
import com.siemens.ct.exi.core.EXIBodyDecoder;
import com.siemens.ct.exi.core.EXIBodyEncoder;
import com.siemens.ct.exi.core.EXIFactory;
import com.siemens.ct.exi.core.FidelityOptions;
import com.siemens.ct.exi.core.SelfContainedHandler;
import com.siemens.ct.exi.core.exceptions.EXIException;
import com.siemens.ct.exi.core.grammars.event.EventType;
import com.siemens.ct.exi.core.helpers.DefaultEXIFactory;
import com.siemens.ct.exi.core.io.channel.EncoderChannel;
import com.siemens.ct.exi.core.values.StringValue;
import com.siemens.ct.exi.core.values.Value;
import com.siemens.ct.exi.grammars.GrammarFactory;
import com.siemens.ct.exi.main.api.sax.EXIResult;
import com.siemens.ct.exi.main.api.sax.SAXFactory;

public class SelfContainedTestCase extends TestCase {

	public SelfContainedTestCase(String testName) {
		super(testName);
	}

	/*
	 * <root> text <sc>text</sc> <sc>text</sc> </root>
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
		Value s = new StringValue("text");

		QName[] scElements = new QName[1];
		scElements[0] = sc;
		factory.setSelfContainedElements(scElements);

		int offsetSC1, offsetSC2;

		// encoder
		{
			EXIBodyEncoder encoder = factory.createEXIBodyEncoder();
			encoder.setOutputStream(baos);
			String pfx = null;
			encoder.encodeStartDocument();
			encoder.encodeStartElement(root.getNamespaceURI(),
					root.getLocalPart(), pfx);
			encoder.encodeCharacters(s);
			{
				encoder.encodeStartElement(sc.getNamespaceURI(),
						sc.getLocalPart(), pfx);

				offsetSC1 = baos.toByteArray().length;
				// System.out.println("SC_1: " + offsetSC1);

				encoder.encodeCharacters(s);
				encoder.encodeEndElement();
			}
			{
				encoder.encodeStartElement(sc.getNamespaceURI(),
						sc.getLocalPart(), pfx);

				offsetSC2 = baos.toByteArray().length;
				// System.out.println("SC_2: " + offsetSC2);

				encoder.encodeCharacters(s);
				encoder.encodeEndElement();
			}
			encoder.encodeEndElement(); // root
			encoder.encodeEndDocument();
			encoder.flush();
		}

		// decoder ALL
		{
			EXIBodyDecoder decoder = factory.createEXIBodyDecoder();
			decoder.setInputStream(new ByteArrayInputStream(baos.toByteArray()));

			assertTrue(decoder.next() == EventType.START_DOCUMENT);
			decoder.decodeStartDocument();

			assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC);
			// assertTrue(decoder.decodeStartElementGeneric().equals(root));
			assertTrue(decoder.decodeStartElement().getQName().equals(root));

			assertTrue(decoder.next() == EventType.CHARACTERS_GENERIC_UNDECLARED);
			// assertTrue(s.equals(decoder.decodeCharactersGenericUndeclared().toString()));
			assertTrue(s.equals(decoder.decodeCharacters().toString()));

			assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC_UNDECLARED);
			// <sc> #1
			// decoder.decodeStartElementGenericUndeclared();
			decoder.decodeStartElement();
			{
				assertTrue(decoder.next() == EventType.SELF_CONTAINED);
				decoder.decodeStartSelfContainedFragment();

				assertTrue(decoder.next() == EventType.CHARACTERS_GENERIC_UNDECLARED);
				// assertTrue(s.equals(decoder.decodeCharactersGenericUndeclared().toString()));
				assertTrue(s.equals(decoder.decodeCharacters().toString()));

				assertTrue(decoder.next() == EventType.END_ELEMENT);
				decoder.decodeEndElement();
			}

			assertTrue(decoder.next() == EventType.START_ELEMENT);
			// <sc> #2
			decoder.decodeStartElement();
			{
				assertTrue(decoder.next() == EventType.SELF_CONTAINED);
				decoder.decodeStartSelfContainedFragment();

				assertTrue(decoder.next() == EventType.CHARACTERS_GENERIC_UNDECLARED);
				// assertTrue(s.equals(decoder.decodeCharactersGenericUndeclared().toString()));
				assertTrue(s.equals(decoder.decodeCharacters().toString()));

				assertTrue(decoder.next() == EventType.END_ELEMENT);
				decoder.decodeEndElement();
			}

			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.END_DOCUMENT);
			decoder.decodeEndDocument();
		}

		EXIFactory scEXIFactory = factory.clone();
		scEXIFactory.setFragment(true);
		// scEXIFactory.setEXIBodyOnly(true);

		int MINUS_BYTE_OFFSET = 3; // TODO why 3

		// decoder SC #1
		{
			EXIBodyDecoder decoder = scEXIFactory.createEXIBodyDecoder();
			InputStream is = new ByteArrayInputStream(baos.toByteArray());
			// is.skip(offsetSC1-MINUS_BYTE_OFFSET);
			int toSkip = offsetSC1 - MINUS_BYTE_OFFSET;
			while (toSkip != 0) {
				toSkip -= is.skip(toSkip);
			}
			decoder.setInputStream(is);

			assertTrue(decoder.next() == EventType.START_DOCUMENT);
			decoder.decodeStartDocument();

			assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC);
			// decoder.decodeStartElementGeneric();
			decoder.decodeStartElement();

			assertTrue(decoder.next() == EventType.CHARACTERS_GENERIC_UNDECLARED);
			// assertTrue(s.equals(decoder.decodeCharactersGenericUndeclared().toString()));
			assertTrue(s.equals(decoder.decodeCharacters().toString()));

			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.END_DOCUMENT);
			decoder.decodeEndDocument();
		}

		// decoder SC #2
		{
			EXIBodyDecoder decoder = scEXIFactory.createEXIBodyDecoder();
			InputStream is = new ByteArrayInputStream(baos.toByteArray());
			// is.skip(offsetSC2-MINUS_BYTE_OFFSET);
			int toSkip = offsetSC2 - MINUS_BYTE_OFFSET;
			while (toSkip != 0) {
				toSkip -= is.skip(toSkip);
			}
			decoder.setInputStream(is);

			assertTrue(decoder.next() == EventType.START_DOCUMENT);
			decoder.decodeStartDocument();

			assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC);
			// decoder.decodeStartElementGeneric();
			decoder.decodeStartElement();

			assertTrue(decoder.next() == EventType.CHARACTERS_GENERIC_UNDECLARED);
			// assertTrue(s.equals(decoder.decodeCharactersGenericUndeclared().toString()));
			assertTrue(s.equals(decoder.decodeCharacters().toString()));

			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.END_DOCUMENT);
			decoder.decodeEndDocument();
		}

	}

	/*
	 * <foo> <foo>text</foo> </foo>
	 */
	public void testSelfContained1() throws IOException, SAXException,
			EXIException {
		// String xmlAsString = "<foo><foo>text</foo></foo>";
		// XMLReader xmlReader = XMLReaderFactory.createXMLReader();

		EXIFactory factory = DefaultEXIFactory.newInstance();

		factory.setFidelityOptions(FidelityOptions.createDefault());
		factory.setCodingMode(CodingMode.BIT_PACKED);
		FidelityOptions fo = factory.getFidelityOptions();
		fo.setFidelity(FidelityOptions.FEATURE_SC, true);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		QName foo = new QName("", "foo");
		// QName foo2 = new QName("", "foo2");
		Value s = new StringValue("text");

		QName[] scElements = new QName[1];
		scElements[0] = foo;
		factory.setSelfContainedElements(scElements);

		int offsetSC1, offsetSC2;

		// encoder
		{
			EXIBodyEncoder encoder = factory.createEXIBodyEncoder();
			encoder.setOutputStream(baos);
			String pfx = null;
			encoder.encodeStartDocument();
			encoder.encodeStartElement(foo.getNamespaceURI(),
					foo.getLocalPart(), pfx);
			offsetSC1 = baos.toByteArray().length;
			{
				encoder.encodeStartElement(foo.getNamespaceURI(),
						foo.getLocalPart(), pfx);

				offsetSC2 = baos.toByteArray().length;
				// System.out.println("SC_1: " + offsetSC1);

				encoder.encodeCharacters(s);
				encoder.encodeEndElement();
			}
			encoder.encodeEndElement(); // foo
			encoder.encodeEndDocument();
			encoder.flush();
		}

		// System.out.println("offsetSC1 = " + offsetSC1);
		// System.out.println("offsetSC2 = " + offsetSC2);

		// decoder ALL
		{
			EXIBodyDecoder decoder = factory.createEXIBodyDecoder();
			decoder.setInputStream(new ByteArrayInputStream(baos.toByteArray()));

			assertTrue(decoder.next() == EventType.START_DOCUMENT);
			decoder.decodeStartDocument();

			// <sc> #1
			assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC);
			// assertTrue(decoder.decodeStartElementGeneric().equals(foo));
			assertTrue(decoder.decodeStartElement().getQName().equals(foo));

			assertTrue(decoder.next() == EventType.SELF_CONTAINED);
			decoder.decodeStartSelfContainedFragment();

			assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC_UNDECLARED);
			// <sc> #2
			// decoder.decodeStartElementGenericUndeclared();
			decoder.decodeStartElement();
			{
				assertTrue(decoder.next() == EventType.SELF_CONTAINED);
				decoder.decodeStartSelfContainedFragment();

				assertTrue(decoder.next() == EventType.CHARACTERS_GENERIC_UNDECLARED);
				// assertTrue(s.equals(decoder.decodeCharactersGenericUndeclared().toString()));
				assertTrue(s.equals(decoder.decodeCharacters().toString()));

				assertTrue(decoder.next() == EventType.END_ELEMENT);
				decoder.decodeEndElement();
			}

			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.END_DOCUMENT);
			decoder.decodeEndDocument();
		}

		EXIFactory scEXIFactory = factory.clone();
		scEXIFactory.setFragment(true);
		// scEXIFactory.setEXIBodyOnly(true);

		int MINUS_BYTE_OFFSET = 4; // TODO why 4

		// decoder SC #1
		{
			EXIBodyDecoder decoder = scEXIFactory.createEXIBodyDecoder();
			InputStream is = new ByteArrayInputStream(baos.toByteArray());
			// is.skip(offsetSC1-MINUS_BYTE_OFFSET);
			int toSkip = offsetSC1 - MINUS_BYTE_OFFSET;
			while (toSkip != 0) {
				toSkip -= is.skip(toSkip);
			}
			decoder.setInputStream(is);

			assertTrue(decoder.next() == EventType.START_DOCUMENT);
			decoder.decodeStartDocument();

			assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC);
			// decoder.decodeStartElementGeneric();
			decoder.decodeStartElement();

			{
				assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC_UNDECLARED);
				// decoder.decodeStartElementGenericUndeclared();
				decoder.decodeStartElement();

				assertTrue(decoder.next() == EventType.SELF_CONTAINED);
				decoder.decodeStartSelfContainedFragment();

				assertTrue(decoder.next() == EventType.CHARACTERS_GENERIC_UNDECLARED);
				// assertTrue(s.equals(decoder.decodeCharactersGenericUndeclared().toString()));
				assertTrue(s.equals(decoder.decodeCharacters().toString()));

				assertTrue(decoder.next() == EventType.END_ELEMENT);
				decoder.decodeEndElement();
			}

			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.END_DOCUMENT);
			decoder.decodeEndDocument();
		}

		// decoder SC #2
		{
			EXIBodyDecoder decoder = scEXIFactory.createEXIBodyDecoder();
			InputStream is = new ByteArrayInputStream(baos.toByteArray());
			// is.skip(offsetSC2-MINUS_BYTE_OFFSET);
			int toSkip = offsetSC2 - MINUS_BYTE_OFFSET;
			while (toSkip != 0) {
				toSkip -= is.skip(toSkip);
			}
			decoder.setInputStream(is);

			assertTrue(decoder.next() == EventType.START_DOCUMENT);
			decoder.decodeStartDocument();

			assertTrue(decoder.next() == EventType.START_ELEMENT_GENERIC);
			// decoder.decodeStartElementGeneric();
			decoder.decodeStartElement();

			assertTrue(decoder.next() == EventType.CHARACTERS_GENERIC_UNDECLARED);
			// assertTrue(s.equals(decoder.decodeCharactersGenericUndeclared().toString()));
			assertTrue(s.equals(decoder.decodeCharacters().toString()));

			assertTrue(decoder.next() == EventType.END_ELEMENT);
			decoder.decodeEndElement();

			assertTrue(decoder.next() == EventType.END_DOCUMENT);
			decoder.decodeEndDocument();
		}

	}

	static class SelfContainedHandlerTracker implements SelfContainedHandler {

		List<Integer> scIndices;
		List<QName> scQNames;

		public SelfContainedHandlerTracker() {
			scIndices = new ArrayList<Integer>();
			scQNames = new ArrayList<QName>();
		}

		public List<Integer> getSCIndices() {
			return scIndices;
		}
		
		public List<QName> getSCQNames() {
			return scQNames;
		}

		public void scElement(String uri, String localName,
				EncoderChannel channel) throws EXIException {
			// System.out.println("SC element: {" + uri + "}" + localName);
			// System.out.println(channel.getLength() + " --> " +
			// channel.getOutputStream());
			scIndices.add(channel.getLength());
			scQNames.add(new QName(uri, localName));
		}

	}

	protected void _testSelfContained2(boolean bytePacked) throws Exception {

		String xmlAsString = "<foo><foo>text</foo><bla>btext</bla></foo>";

		// boolean t1 = xmlAsString.matches("*");
		// boolean t2 = xmlAsString.matches(".*");

		EXIFactory factory = DefaultEXIFactory.newInstance();

		factory.setFidelityOptions(FidelityOptions.createDefault());
		// factory.setCodingMode(CodingMode.BIT_PACKED);
		if (bytePacked) {
			factory.setCodingMode(CodingMode.BYTE_PACKED);
		}
		FidelityOptions fo = factory.getFidelityOptions();
		fo.setFidelity(FidelityOptions.FEATURE_SC, true);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		// QName sc = new QName("", "bla");
		QName sc = new QName(".*", ".*"); // any

		QName[] scElements = new QName[1];
		scElements[0] = sc;
		SelfContainedHandlerTracker scHandler = new SelfContainedHandlerTracker();
		factory.setSelfContainedElements(scElements, scHandler);

		EXIResult exiResult = new EXIResult(factory);
		exiResult.setOutputStream(baos);
		XMLReader xmlReader = XMLReaderFactory.createXMLReader();
		xmlReader.setContentHandler(exiResult.getHandler());
		xmlReader.parse(new InputSource(new StringReader(xmlAsString)));

		// decode overall document
		{
			TransformerFactory tf = TransformerFactory.newInstance();
			XMLReader exiReader = new SAXFactory(factory).createEXIReader();
			Transformer transformer = tf.newTransformer();

			ByteArrayOutputStream baosSC = new ByteArrayOutputStream();
			Result result = new StreamResult(baosSC);
			ByteArrayInputStream bais = new ByteArrayInputStream(
					baos.toByteArray());
			InputSource is = new InputSource(bais);
			SAXSource exiSource = new SAXSource(is);
			exiSource.setXMLReader(exiReader);
			transformer.transform(exiSource, result);

			// System.out.println(new String(baosSC.toByteArray()));
		}

		// decode each SC elements
		assertTrue("SC number issue", scHandler.getSCIndices().size() == 3);
		// Note: 2 important aspects
		// a) SC element is EXI Body only
		// b) SC element root starts with fragment grammar

		factory.setFragment(true); // see b)

		for (int i = 0; i < scHandler.getSCIndices().size(); i++) {
			TransformerFactory tf = TransformerFactory.newInstance();
			XMLReader exiReader = new SAXFactory(factory).createEXIReader();
			exiReader.setFeature(Constants.W3C_EXI_FEATURE_BODY_ONLY, true); // see
																				// a)
			Transformer transformer = tf.newTransformer();

			ByteArrayOutputStream baosSC = new ByteArrayOutputStream();
			Result result = new StreamResult(baosSC);
			ByteArrayInputStream bais = new ByteArrayInputStream(
					baos.toByteArray());
			int scSkip = scHandler.getSCIndices().get(i);
			if (bytePacked) {
				scSkip += 1; // Header, TODO how can this situation be improved
			}
			long skip = bais.skip(scSkip);
			assertTrue(skip == scSkip);
			InputSource is = new InputSource(bais);
			SAXSource exiSource = new SAXSource(is);
			exiSource.setXMLReader(exiReader);
			transformer.transform(exiSource, result);

			// System.out.println(new String(baosSC.toByteArray()));
		}
	}

	public void testSelfContained2BitPacked() throws Exception {
		_testSelfContained2(false);
	}

	public void testSelfContained2BytePacked() throws Exception {
		_testSelfContained2(true);
	}
	
	public void testSelfContainedNotebook() throws Exception {
		String xsd = "./data/W3C/PrimerNotebook/notebook.xsd";
		String xml = "./data/W3C/PrimerNotebook/notebook.xml";
		
		EXIFactory factory = DefaultEXIFactory.newInstance();
		factory.setGrammars(GrammarFactory.newInstance().createGrammars(xsd));
		factory.setFidelityOptions(FidelityOptions.createDefault());
		// factory.setCodingMode(CodingMode.BIT_PACKED);
		FidelityOptions fo = factory.getFidelityOptions();
		fo.setFidelity(FidelityOptions.FEATURE_SC, true);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		// QName sc = new QName("", "bla");
		QName sc = new QName(".*", ".*"); // any

		QName[] scElements = new QName[1];
		scElements[0] = sc;
		SelfContainedHandlerTracker scHandler = new SelfContainedHandlerTracker();
		factory.setSelfContainedElements(scElements, scHandler);

		EXIResult exiResult = new EXIResult(factory);
		exiResult.setOutputStream(baos);
		XMLReader xmlReader = XMLReaderFactory.createXMLReader();
		xmlReader.setContentHandler(exiResult.getHandler());
		xmlReader.parse(new InputSource(new FileInputStream(xml)));
		
		assertTrue("Number of Indices", scHandler.getSCIndices().size() == 7);
		assertTrue("Number of QNnames", scHandler.getSCQNames().size() == 7);
		
//		System.out.println(scHandler.getSCIndices());
//		System.out.println(scHandler.getSCQNames());
//		System.out.println("Size Stream: " + baos.size());
		
	}
	
	public static void main(String[] args) throws EXIException, IOException, SAXException {
		String xml = "./data/W3C/PrimerNotebook/notebook.xml";
		//String xsd = "./data/W3C/PrimerNotebook/notebook.xsd";
		
		EXIFactory factory = DefaultEXIFactory.newInstance();
		// factory.setGrammars(GrammarFactory.newInstance().createGrammars(xsd));
		factory.setFidelityOptions(FidelityOptions.createDefault());
		// factory.setCodingMode(CodingMode.BIT_PACKED);
		FidelityOptions fo = factory.getFidelityOptions();
		fo.setFidelity(FidelityOptions.FEATURE_SC, true);
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		QName sc = new QName("", "note");
		// QName sc = new QName(".*", ".*"); // any
		
		QName[] scElements = new QName[1];
		scElements[0] = sc;
		SelfContainedHandlerTracker scHandler = new SelfContainedHandlerTracker();
		factory.setSelfContainedElements(scElements, scHandler);

		EXIResult exiResult = new EXIResult(factory);
		exiResult.setOutputStream(baos);
		XMLReader xmlReader = XMLReaderFactory.createXMLReader();
		xmlReader.setContentHandler(exiResult.getHandler());
		xmlReader.parse(new InputSource(new FileInputStream(xml)));
		
		for(int i=0; i<scHandler.getSCIndices().size(); i++) {
			System.out.println(scHandler.getSCQNames().get(i) + ": " + scHandler.getSCIndices().get(i) + " bytes");
		}
		
//		OutputStream os = new FileOutputStream("./out/notebook.xml.exi-sc");
//		os.write(baos.toByteArray());
//		os.close();
	}

}