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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;

import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.siemens.ct.exi.CodingMode;
import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.EncodingOptions;
import com.siemens.ct.exi.api.sax.EXIResult;
import com.siemens.ct.exi.helpers.DefaultEXIFactory;

public class MultipleStreamTest extends AbstractTestCase
{

	public static String XML_NOTEBOOK = "<notebook date=\"2007-09-12\"><note date=\"2007-07-23\" category=\"EXI\"><subject>EXI</subject><body>Do not forget it!</body></note><note date=\"2007-09-12\"><subject>shopping list</subject><body>milk, honey</body></note></notebook>";
	public static final int NUMBER_OF_EXI_DOCUMENTS = 4;

	public MultipleStreamTest(String s) {
		super(s);
	}

	
	public void testXMLNotebook() throws Exception {
		// exi factory
		EXIFactory exiFactory = DefaultEXIFactory.newInstance();
		exiFactory.setCodingMode(CodingMode.COMPRESSION);
		EncodingOptions encOption = EncodingOptions.createDefault();
		encOption.setOption(EncodingOptions.INCLUDE_COOKIE);
		encOption.setOption(EncodingOptions.INCLUDE_OPTIONS);
		exiFactory.setEncodingOptions(encOption);
		
		// output stream
		ByteArrayOutputStream osEXI = new ByteArrayOutputStream();
		
		// encode exi document multiple times
		for(int i=0; i<NUMBER_OF_EXI_DOCUMENTS; i++) {
			EXIResult exiResult = new EXIResult( exiFactory );
			exiResult.setOutputStream( osEXI );
			XMLReader xmlReader = XMLReaderFactory.createXMLReader();
			xmlReader.setContentHandler( exiResult.getHandler() );
			xmlReader.parse(new InputSource(new StringReader(XML_NOTEBOOK)));
		}
		
		// System.out.println("OutputSize: " + os.size());
		
		// decode EXI
		TransformerFactory  tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		
		ByteArrayInputStream isEXI = new ByteArrayInputStream(osEXI.toByteArray());
		
		ByteArrayInputStream baisControl = new ByteArrayInputStream(XML_NOTEBOOK.getBytes()); // testing only
		
		for(int i=0; i<NUMBER_OF_EXI_DOCUMENTS; i++) {
			InputSource is = new InputSource(isEXI) ;
			SAXSource exiSource = new SAXSource(is);
			XMLReader exiReader = exiFactory.createEXIReader();
			exiSource.setXMLReader(exiReader);
			ByteArrayOutputStream xmlOutput = new ByteArrayOutputStream();
			Result result = new StreamResult(xmlOutput);
			transformer.transform(exiSource, result);
			byte[] xmlDec = xmlOutput.toByteArray();
			// System.out.println("Decode #" + (i+1) + new String(xmlDec));
			
			baisControl.reset();
			checkXMLEquality(exiFactory, baisControl, new ByteArrayInputStream(xmlDec));
		}
		
	}

}
