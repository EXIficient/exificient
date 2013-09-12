/*
 * Copyright (C) 2007-2011 Siemens AG
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;

import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.GrammarFactory;
import com.siemens.ct.exi.api.sax.EXIResult;
import com.siemens.ct.exi.api.sax.EXISource;
import com.siemens.ct.exi.grammars.Grammars;
import com.siemens.ct.exi.helpers.DefaultEXIFactory;

public final class EXIficientDemo {

	static final String OUTPUT_FOLDER = "./sample-out/";
	static final String EXI_EXTENSION = "_exi";
	static final String EXI_SCHEMA_EXTENSION = "_exi_schema";
	static final String XML_EXTENSION = ".xml";
	static int NUMBER_OF_RUNS = 1;

	// XML
	String xmlLocation;
	String xmlName;
	// XML Schema
	String xsdLocation;

	private void parseAndProofFileLocations(String[] args) throws Exception {
		if (args.length >= 2) {
			// xml
			xmlLocation = args[0];
			File xmlFile = new File(xmlLocation);
			xmlName = xmlFile.getName();
			// xsd
			xsdLocation = args[1];
			File xsdFile = new File(xsdLocation);

			// number of runs
			if (args.length == 3) {
				NUMBER_OF_RUNS = Integer.parseInt(args[2]);
			}

			if (xmlFile.exists() && xsdFile.exists()) {
				// output path
				File outputDir = new File(OUTPUT_FOLDER);
				outputDir.mkdirs();

				return;
			}
		}

		throw new IllegalArgumentException("Input files not valid!");
	}

	private String getEXILocation(boolean schemaLess) {
		if (schemaLess) {
			return OUTPUT_FOLDER + xmlName + EXI_EXTENSION;
		} else {
			return OUTPUT_FOLDER + xmlName + EXI_SCHEMA_EXTENSION;
		}
	}

	protected void codeSchemaLess() throws Exception {
		String exiLocation = getEXILocation(true);		

		// encode
		OutputStream exiOS = new FileOutputStream(exiLocation);
		EXIResult exiResult = new EXIResult();
		exiResult.setOutputStream(exiOS);
		encode(exiResult.getHandler());
		exiOS.close();

		// decode
		SAXSource exiSource = new EXISource();
		XMLReader exiReader = exiSource.getXMLReader();
		decode(exiReader, exiLocation);
	}

	protected void codeSchemaInformed() throws Exception {
		String exiLocation = getEXILocation(false);

		// create default factory and EXI grammar for schema
		EXIFactory exiFactory = DefaultEXIFactory.newInstance();
		GrammarFactory grammarFactory = GrammarFactory.newInstance();
		Grammars g = grammarFactory.createGrammars(xsdLocation);
		exiFactory.setGrammars(g);
		
		// encode
		OutputStream exiOS = new FileOutputStream(exiLocation);
		EXIResult exiResult = new EXIResult(exiFactory);
		exiResult.setOutputStream(exiOS);
		encode(exiResult.getHandler());
		exiOS.close();

		// decode
		EXISource saxSource = new EXISource(exiFactory);
		XMLReader xmlReader = saxSource.getXMLReader();
		decode(xmlReader, exiLocation);
	}

	protected void encode(ContentHandler ch) throws SAXException, IOException {
		XMLReader xmlReader = XMLReaderFactory.createXMLReader();
		xmlReader.setContentHandler(ch);

		// parse xml file
		xmlReader.parse(new InputSource(xmlLocation));
	}

	protected void decode(XMLReader exiReader, String exiLocation)
			throws SAXException, IOException, TransformerException {

		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();

		InputStream exiIS = new FileInputStream(exiLocation);
		SAXSource exiSource = new SAXSource(new InputSource(exiIS));
		exiSource.setXMLReader(exiReader);

		OutputStream os = new FileOutputStream(exiLocation + XML_EXTENSION);
		transformer.transform(exiSource, new StreamResult(os));
		os.close();
	}

	public static void main(String[] args) throws Exception {
		/*
		 * Note: we are using default coding options and SAX
		 */
		if (args.length > 0) {

			EXIficientDemo sample = new EXIficientDemo();
			sample.parseAndProofFileLocations(args);

			// schema-less
			for (int i = 0; i < NUMBER_OF_RUNS; i++) {
				sample.codeSchemaLess();
				System.out.print(". ");
			}
			System.out.println();
			System.out.println("# SchemaLess - " + NUMBER_OF_RUNS + " run(s)");
			System.out.println("\t" + " --> " + sample.getEXILocation(true));
			System.out.println("\t" + " <-- " + sample.getEXILocation(true)
					+ XML_EXTENSION);

			// schema-informed
			for (int i = 0; i < NUMBER_OF_RUNS; i++) {
				sample.codeSchemaInformed();
				System.out.print(". ");
			}
			System.out.println();
			System.out.println("# SchemaInformed - " + NUMBER_OF_RUNS
					+ " run(s)");
			System.out.println("\t" + " --> " + sample.getEXILocation(false));
			System.out.println("\t" + " <-- " + sample.getEXILocation(false)
					+ XML_EXTENSION);

		} else {
			System.out.println("# EXIficient Sample, no input files specified");
			System.out.println("Usage: " + EXIficientDemo.class.getName()
					+ " xmlFile xsdFile [NumberOfRuns]");
		}
	}

}
