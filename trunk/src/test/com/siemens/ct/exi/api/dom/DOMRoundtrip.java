package com.siemens.ct.exi.api.dom;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.custommonkey.xmlunit.XMLTestCase;
import org.custommonkey.xmlunit.XMLUnit;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.helpers.DefaultEXIFactory;

public class DOMRoundtrip extends XMLTestCase {

	public void testNotebook() throws ParserConfigurationException,
			SAXException, IOException, EXIException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(new File(
				"./data/W3C/PrimerNotebook/notebook.xml"));

		// exi factory
		EXIFactory exiFactory = DefaultEXIFactory.newInstance();
		//exiFactory.setEXIBodyOnly(true);

		// encode DOM to EXI
		DOMWriter domEncoder = new DOMWriter(exiFactory);
		ByteArrayOutputStream osEXI = new ByteArrayOutputStream();
		// File f = File.createTempFile("prefix", "suffix");
		// OutputStream osEXI = new FileOutputStream(f);
		domEncoder.setOutput(osEXI);
		domEncoder.encode(document);
		osEXI.flush();

		// decode EXI to DOM
		InputStream is = new ByteArrayInputStream(osEXI.toByteArray());
		// InputStream is = new FileInputStream(f);
		DOMBuilder db = new DOMBuilder(exiFactory);
		Document exiDocument = db.parse(is);

		// equal ?
		isXMLEqual(document, exiDocument);
	}

	protected void isXMLEqual(Document control, Document test)
			throws SAXException, IOException {
		XMLUnit.setIgnoreWhitespace(true);
		XMLUnit.setIgnoreAttributeOrder(true);
		// XMLUnit.setNormalize(true);

		// Diff diff = compareXML (control, test);

		assertXMLEqual(control, test);
	}
}
