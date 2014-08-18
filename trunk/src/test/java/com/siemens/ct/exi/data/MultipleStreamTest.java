/*
 * Copyright (C) 2007-2009 Siemens AG
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

package com.siemens.ct.exi.data;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;

import junit.framework.AssertionFailedError;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.siemens.ct.exi.CodingMode;
import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.EncodingOptions;
import com.siemens.ct.exi.FidelityOptions;
import com.siemens.ct.exi.QuickTestConfiguration;
import com.siemens.ct.exi.api.sax.EXIResult;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.helpers.DefaultEXIFactory;

public class MultipleStreamTest extends AbstractTestCase
{

	public static String XML_NOTEBOOK = "<notebook date=\"2007-09-12\"><note date=\"2007-07-23\" category=\"EXI\"><subject>EXI</subject><body>Do not forget it!</body></note><note date=\"2007-09-12\"><subject>shopping list</subject><body>milk, honey</body></note></notebook>";
	
	public MultipleStreamTest(String s) {
		super(s);
	}
	
	
	protected void _test(EXIFactory exiFactory, byte[] isBytes, int numberOfEXIDocuments) throws AssertionFailedError, Exception {		
		// output stream
		ByteArrayOutputStream osEXI = new ByteArrayOutputStream();
		
		// encode exi document multiple times
		for(int i=0; i<numberOfEXIDocuments; i++) {
			EXIResult exiResult = new EXIResult( exiFactory );
			exiResult.setOutputStream( osEXI );
			XMLReader xmlReader = XMLReaderFactory.createXMLReader();
			xmlReader.setContentHandler( exiResult.getHandler() );
			// set LexicalHandler
			xmlReader.setProperty("http://xml.org/sax/properties/lexical-handler",
					exiResult.getLexicalHandler());
			
			xmlReader.parse(new InputSource(new ByteArrayInputStream(isBytes)));
		}
		
		// System.out.println("OutputSize: " + os.size());
		
		// decode EXI
		TransformerFactory  tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		
		InputStream isEXI = new ByteArrayInputStream(osEXI.toByteArray());
		isEXI = new BufferedInputStream(isEXI);
		
		for(int i=0; i<numberOfEXIDocuments; i++) {
			// InputSource is = new InputSource(isEXI);
			InputSource is = new InputSource(isEXI);
			SAXSource exiSource = new SAXSource(is);
			XMLReader exiReader = exiFactory.createEXIReader();
			exiSource.setXMLReader(exiReader);
			ByteArrayOutputStream xmlOutput = new ByteArrayOutputStream();
			Result result = new StreamResult(xmlOutput);
			transformer.transform(exiSource, result);
			byte[] xmlDec = xmlOutput.toByteArray();
			// System.out.println("Decode #" + (i+1) + new String(xmlDec));
			
			
			ByteArrayInputStream baisControl = new ByteArrayInputStream(isBytes); // testing only
			checkXMLEquality(exiFactory, baisControl, new ByteArrayInputStream(xmlDec));
			// checkXMLValidity(exiFactory, new ByteArrayInputStream(xmlDec));
		}
	}

	
	public void testXMLNotebook4() throws Exception {
		// exi factory
		EXIFactory exiFactory = DefaultEXIFactory.newInstance();
		exiFactory.setCodingMode(CodingMode.COMPRESSION);
		EncodingOptions encOption = EncodingOptions.createDefault();
		encOption.setOption(EncodingOptions.INCLUDE_COOKIE);
		encOption.setOption(EncodingOptions.INCLUDE_OPTIONS);
		exiFactory.setEncodingOptions(encOption);
		
		_test(exiFactory, XML_NOTEBOOK.getBytes(), 4);
	}
	
	public void testXMLNotebook6Block3() throws Exception {
		// exi factory
		EXIFactory exiFactory = DefaultEXIFactory.newInstance();
		exiFactory.setBlockSize(3);
		exiFactory.setCodingMode(CodingMode.COMPRESSION);
		exiFactory.setFidelityOptions(FidelityOptions.createAll());
		
		_test(exiFactory, XML_NOTEBOOK.getBytes(), 6);
	}
	
	public void testXMLRandj3() throws Exception {
		// exi factory
		EXIFactory exiFactory = DefaultEXIFactory.newInstance();
		exiFactory.setCodingMode(CodingMode.COMPRESSION);
		exiFactory.setFidelityOptions(FidelityOptions.createAll());
		EncodingOptions encOption = EncodingOptions.createDefault();
		encOption.setOption(EncodingOptions.INCLUDE_COOKIE);
		encOption.setOption(EncodingOptions.INCLUDE_XSI_SCHEMALOCATION);
		encOption.setOption(EncodingOptions.INCLUDE_INSIGNIFICANT_XSI_NIL);
		exiFactory.setEncodingOptions(encOption);
		
		
		// 
		String sXML = "./data/general/randj.xml";
		InputStream is = new FileInputStream(sXML);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int b;
		while((b = is.read()) != -1) {
			baos.write(b);
		}
		is.close();
		
		_test(exiFactory, baos.toByteArray(), 3);
	}

}
