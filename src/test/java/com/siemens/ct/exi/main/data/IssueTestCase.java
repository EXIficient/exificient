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

package com.siemens.ct.exi.main.data;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;

import com.siemens.ct.exi.core.FidelityOptions;
import com.siemens.ct.exi.main.util.NoEntityResolver;
import org.xml.sax.*;
import org.xml.sax.helpers.XMLReaderFactory;

import com.siemens.ct.exi.core.EXIFactory;
import com.siemens.ct.exi.core.helpers.DefaultEXIFactory;
import com.siemens.ct.exi.main.api.sax.EXIResult;
import com.siemens.ct.exi.main.api.sax.EXISource;

public class IssueTestCase extends AbstractTestCase {

	public IssueTestCase() {
		super("Issues Test Cases");
	}
	
	// https://github.com/EXIficient/exificient/issues/20
	public void testIssue20() throws Exception {
		EXIFactory exiFactory = DefaultEXIFactory.newInstance();
		exiFactory.setValueMaxLength(16);
		// exiFactory.setCodingMode(CodingMode.COMPRESSION);

		String sxmlIssue20 = "./data/issues/issue20/treebank_e.xml";
		
		File fEXI = File.createTempFile("testIssue20", ".exi");
		fEXI.deleteOnExit();
		
		
		// encode
		{
			long startEncode = System.currentTimeMillis();
			OutputStream osEXI = new FileOutputStream(fEXI); // EXI output
			EXIResult exiResult = new EXIResult(exiFactory);
			exiResult.setOutputStream(osEXI);
			XMLReader xmlReader = XMLReaderFactory.createXMLReader();
			xmlReader.setContentHandler( exiResult.getHandler() );
			xmlReader.parse(sxmlIssue20); // parse XML input
			osEXI.close();
			long endEncode = System.currentTimeMillis();
			System.out.println("Encode time for " + sxmlIssue20 + " is " + (endEncode-startEncode) + "ms to " + fEXI.length() +"Bytes, " + System.getProperty("java.version"));
		}
		
		// decode
		{
			long startDecode = System.currentTimeMillis();
			Result result = new StreamResult(File.createTempFile("testIssue20", ".xml"));
			InputSource is = new InputSource(new FileInputStream(fEXI));
			SAXSource exiSource = new EXISource(exiFactory);
			exiSource.setInputSource(is);
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.transform(exiSource, result);	
			long endDecode = System.currentTimeMillis();
			System.out.println("Decode time for " + sxmlIssue20 + " is " + (endDecode-startDecode) + "ms");
		}
	}

	// https://github.com/EXIficient/exificient/issues/46
	public void testIssue46() throws Exception {
		EXIFactory exiFactory = DefaultEXIFactory.newInstance();
		exiFactory.getFidelityOptions().setFidelity(FidelityOptions.FEATURE_DTD, true);
		// exiFactory.getEncodingOptions().setOption(EncodingOptions.RETAIN_ENTITY_REFERENCE);

		String sxmlIssue20 = "./data/issues/issue46/test.xml";

		File fEXI = File.createTempFile("testIssue46", ".exi");
		fEXI.deleteOnExit();


		// encode
		{
			long startEncode = System.currentTimeMillis();
			OutputStream osEXI = new FileOutputStream(fEXI); // EXI output
			EXIResult exiResult = new EXIResult(exiFactory);
			exiResult.setOutputStream(osEXI);
			XMLReader xmlReader = XMLReaderFactory.createXMLReader();
			xmlReader.setContentHandler( exiResult.getHandler() );
			xmlReader.setProperty("http://xml.org/sax/properties/lexical-handler", exiResult.getLexicalHandler());
			xmlReader.setProperty("http://xml.org/sax/properties/declaration-handler", exiResult.getLexicalHandler());

			xmlReader.setDTDHandler((DTDHandler) exiResult.getHandler());
			xmlReader.setEntityResolver(new NoEntityResolver());
			xmlReader.parse(sxmlIssue20); // parse XML input
			osEXI.close();
			long endEncode = System.currentTimeMillis();
			System.out.println("Encode time for " + sxmlIssue20 + " is " + (endEncode-startEncode) + "ms to " + fEXI.length() +"Bytes, " + System.getProperty("java.version"));
		}

		// decode
		{
			long startDecode = System.currentTimeMillis();
			File fDec = File.createTempFile("testIssue20", ".xml");
			Result result = new StreamResult(fDec);
			InputSource is = new InputSource(new FileInputStream(fEXI));
			SAXSource exiSource = new EXISource(exiFactory);
			exiSource.setInputSource(is);
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.transform(exiSource, result);
			long endDecode = System.currentTimeMillis();
			System.out.println("Decode time for " + sxmlIssue20 + " is " + (endDecode-startDecode) + "ms");

			String content = new String(Files.readAllBytes(Paths.get(fDec.getPath())));
			System.out.println(content);
			assertTrue(content.contains("fragment.xml"));
		}
	}
	
}
