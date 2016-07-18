/*
 * Copyright (c) 2007-2015 Siemens AG
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
package com.siemens.ct.exi.api.stream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import com.siemens.ct.exi.CodingMode;
import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.FidelityOptions;
import com.siemens.ct.exi.GrammarFactory;
import com.siemens.ct.exi.TestDOMEncoder;
import com.siemens.ct.exi.TestStAXDecoder;
import com.siemens.ct.exi.TestStAXEncoder;
import com.siemens.ct.exi.data.AbstractTestCase;
import com.siemens.ct.exi.grammars.Grammars;
import com.siemens.ct.exi.helpers.DefaultEXIFactory;
import com.siemens.ct.exi.util.FragmentUtilities;
import junit.framework.AssertionFailedError;
import org.w3c.dom.Document;

public class StAXCoderTestCase extends AbstractTestCase {

	public StAXCoderTestCase(String s) {
		super(s);
	}

	public void testNotebook() throws AssertionFailedError, Exception {
		String xmlInput = "./data/W3C/PrimerNotebook/notebook.xml";
		String exiOutput = "./out/W3C/PrimerNotebook/notebook.xml.exi";
		String xmlOutput = "./out/W3C/PrimerNotebook/notebook.xml.exi.xml";

		EXIFactory ef1 = DefaultEXIFactory.newInstance();
		this._test(ef1, xmlInput, exiOutput, xmlOutput, false);

		EXIFactory ef2 = DefaultEXIFactory.newInstance();
		ef2.setFidelityOptions(FidelityOptions.createAll());
		this._test(ef2, xmlInput, exiOutput, xmlOutput, true);
	}
	
	public static void testEXIbyExample() throws AssertionFailedError, Exception {
		String xmlInput = "./data/schema/example_model.xml";
		String exiOutput = "./out/example_model.xml.exi";
		String xmlOutput = "./out/example_model.xml.exi.xml";
		String xsdInput = "./data/schema/example_model.xsd";

		EXIFactory ef1 = DefaultEXIFactory.newInstance();
		ef1.setGrammars(getGrammarFromSchemaAsString(xsdInput));
		ef1.setCodingMode(CodingMode.COMPRESSION);
		_test(ef1, xmlInput, exiOutput, xmlOutput, false);

		EXIFactory ef2 = DefaultEXIFactory.newInstance();
		ef2.setFidelityOptions(FidelityOptions.createAll());
		ef2.setGrammars(getGrammarFromSchemaAsString(xsdInput));
		ef2.setCodingMode(CodingMode.COMPRESSION);
		_test(ef2, xmlInput, exiOutput, xmlOutput, true);
	}

	private static Grammars getGrammarFromSchemaAsString(String xsdInput)
			throws Exception {
		InputStream xsd = new FileInputStream(xsdInput);

		byte[] buff = new byte[8000];

		int bytesRead = 0;
		ByteArrayOutputStream bao = new ByteArrayOutputStream();

		while((bytesRead = xsd.read(buff)) != -1) {
			bao.write(buff, 0, bytesRead);
		}

		byte[] data = bao.toByteArray();

		ByteArrayInputStream bin = new ByteArrayInputStream(data);

		GrammarFactory grammarFactory = GrammarFactory.newInstance();
		Grammars grammar = grammarFactory.createGrammars(bin);

		return grammar;
	}

	public void testXsiType() throws AssertionFailedError, Exception {

		String xmlInput = "./data/schema/xsi-type.xml";
		String exiOutput = "./out/xsi-type.xml.exi";
		String xmlOutput = "./out/xsi-type.xml.exi.xml";

		EXIFactory ef1 = DefaultEXIFactory.newInstance();
		this._test(ef1, xmlInput, exiOutput, xmlOutput, false);

		EXIFactory ef2 = DefaultEXIFactory.newInstance();
		ef2.setFidelityOptions(FidelityOptions.createAll());
		this._test(ef2, xmlInput, exiOutput, xmlOutput, true);
	}

	protected static void _test(EXIFactory exiFactory, String xmlInput,
			String exiOutput, String xmlOutput, boolean xmlEqual)
			throws AssertionFailedError, Exception {

		
		for(int i=0; i<2; i++) {
			TestStAXEncoder tse = new TestStAXEncoder(exiFactory);
			if(i == 0) {
				 tse = new TestStAXEncoder(exiFactory);
			} else {
				 tse = new TestStAXEncoder(exiFactory, true);
			}
			
			// encode
			File fOut = new File(exiOutput);
			fOut.getParentFile().mkdirs();
			OutputStream exiOut = new FileOutputStream(fOut);
			InputStream xmlIn = new FileInputStream(xmlInput);
			tse.encodeTo(xmlIn, exiOut);
			exiOut.close();
			xmlIn.close();

			// decode
			InputStream exiIn = new FileInputStream(exiOutput);
			TestStAXDecoder tsd = new TestStAXDecoder(exiFactory);
			OutputStream xmlOut = new FileOutputStream(xmlOutput);
			tsd.decodeTo(exiIn, xmlOut);
			xmlOut.close();

			// check equality
			// @SuppressWarnings("unused")
			// InputStream control = new FileInputStream(xmlInput);
			InputStream testXML = new FileInputStream(xmlOutput);
			checkXML(exiFactory, testXML);
			if (xmlEqual) {
				// this.checkXMLEquality(exiFactory, control, testXML);
			}
		}
		


	}

	public static void checkXML(EXIFactory ef, InputStream testXML)
			throws Exception {
		if (ef.isFragment()) {
			// surround with root element for equality check
			testXML = FragmentUtilities.getSurroundingRootInputStream(testXML);
		}

		// try to read stream and create DOM
		try {
			// @SuppressWarnings("unused")
			Document docTest = TestDOMEncoder.getDocument(testXML);
			assertTrue(docTest != null);
		} catch (Exception e) {
			String msg = e.getMessage();
			if (msg.contains("The entity \"ent\" was referenced, but not declared")) {
				// known issue? --> entityReference2 for StAX
				return;
			}
			throw new Exception("Not able to create DOM. " + ef.getCodingMode()
					+ ", schema=" + ef.getGrammars().isSchemaInformed() + " "
					+ ef.getFidelityOptions().toString(), e);
		}
		// assertXMLValid(new InputSource(test));
	}

	// protected void encode(EXIFactory exiFactory, String xmlInput, String
	// exiOutput) throws XMLStreamException, EXIException, IOException {
	// XMLInputFactory xmlFactory = XMLInputFactory.newInstance();
	// InputStream is = new FileInputStream(xmlInput);
	//
	// // XMLStreamReader xmlReader = xmlFactory.createXMLStreamReader(is);
	// XMLEventReader xmlReader = xmlFactory.createXMLEventReader(is);
	//
	//
	// OutputStream os = new FileOutputStream(exiOutput);
	// StAXEncoder exiWriter = new StAXEncoder(exiFactory, os);
	//
	// exiWriter.encode(xmlReader);
	// }

	// protected void decode(EXIFactory exiFactory, String exiInput, String
	// xmlOutput) throws EXIException, IOException, TransformerException,
	// XMLStreamException {
	// InputStream is = new FileInputStream(exiInput);
	// OutputStream os = new FileOutputStream(xmlOutput);
	// XMLStreamReader exiReader = new StAXStreamReader(exiFactory, is);
	// // XMLStreamReader xmlStream = null;
	// // exiReader.setXMLStreamReader(xmlStream);
	//
	// // System.err.println("TEST");
	// // FileInputStream fileInputStream = new
	// FileInputStream("./data/schema/xsi-type.xml");
	// // exiReader =
	// XMLInputFactory.newInstance().createXMLStreamReader(fileInputStream);
	//
	//
	//
	// System.out.println("----");
	//
	// XMLOutputFactory xof = XMLOutputFactory.newInstance();
	// XMLStreamWriter xmlWriter = xof.createXMLStreamWriter(os);
	//
	// xmlWriter.writeStartDocument();
	//
	// while(exiReader.hasNext()) {
	// int event = exiReader.next();
	// switch(event) {
	// case XMLStreamConstants.START_DOCUMENT:
	// // should have happen beforehand
	// throw new EXIException("Unexpected START_DOCUMENT event");
	// case XMLStreamConstants.END_DOCUMENT:
	// xmlWriter.writeEndDocument();
	// break;
	// case XMLStreamConstants.START_ELEMENT:
	// QName qn = exiReader.getName();
	// String pfx = exiReader.getPrefix();
	// System.out.println("> SE " + qn);
	// xmlWriter.writeStartElement(pfx, qn.getLocalPart(),
	// qn.getNamespaceURI());
	//
	// //
	// // xmlWriter.setPrefix(pfx, qn.getNamespaceURI());
	// xmlWriter.writeNamespace(pfx, qn.getNamespaceURI());
	//
	// int atts = exiReader.getAttributeCount();
	// for(int i=0; i<atts; i++) {
	// QName atQname = exiReader.getAttributeName(i);
	// String atPfx = exiReader.getAttributePrefix(i);
	// String atVal = exiReader.getAttributeValue(i);
	// System.out.println("  AT " + atQname + " = " + atVal);
	// xmlWriter.writeAttribute(atPfx, atQname.getNamespaceURI(),
	// atQname.getLocalPart(), atVal);
	// }
	// break;
	// case XMLStreamConstants.END_ELEMENT:
	// System.out.println("< EE ");
	// xmlWriter.writeEndElement();
	// break;
	// case XMLStreamConstants.NAMESPACE:
	// String prefix = null;
	// String namespaceURI = null;
	// xmlWriter.writeNamespace(prefix, namespaceURI);
	// break;
	// case XMLStreamConstants.CHARACTERS:
	// String ch = exiReader.getText();
	// System.out.println("> ch " + ch);
	// xmlWriter.writeCharacters(ch);
	// break;
	// case XMLStreamConstants.SPACE:
	// String ignorableSpace = exiReader.getText();
	// break;
	// case XMLStreamConstants.ATTRIBUTE:
	// int attsX = exiReader.getAttributeCount();
	// // exiWriter.writeCharacters(ch);
	// break;
	// default:
	// System.out.println("Event '" + event +"' not supported!");
	// }
	// }
	//
	//
	//
	//
	//
	//
	//
	// xmlWriter.writeEndDocument();
	//
	// xmlWriter.flush();
	// xmlWriter.close();
	// }
	//

	public static void main(String[] args) throws Exception {

		StAXCoderTestCase st = new StAXCoderTestCase("StAX");
		// st.testNotebook();
		st.testXsiType();

	}

}
