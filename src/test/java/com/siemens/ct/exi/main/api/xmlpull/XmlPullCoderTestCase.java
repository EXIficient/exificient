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
package com.siemens.ct.exi.main.api.xmlpull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import junit.framework.AssertionFailedError;

import com.siemens.ct.exi.core.EXIFactory;
import com.siemens.ct.exi.core.FidelityOptions;
import com.siemens.ct.exi.core.helpers.DefaultEXIFactory;
import com.siemens.ct.exi.main.TestXmlPullDecoder;
import com.siemens.ct.exi.main.TestXmlPullEncoder;
import com.siemens.ct.exi.main.data.AbstractTestCase;

public class XmlPullCoderTestCase extends AbstractTestCase {

	public XmlPullCoderTestCase(String s) {
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
	
	public void testEXIbyExample() throws AssertionFailedError, Exception {
		String xmlInput = "./data/W3C/EXIbyExample/XMLSample.xml";
		String exiOutput = "./out/W3C/EXIbyExample/XMLSample.xml.exi";
		String xmlOutput = "./out/W3C/EXIbyExample/XMLSample.xml.exi.xml";

		EXIFactory ef1 = DefaultEXIFactory.newInstance();
		this._test(ef1, xmlInput, exiOutput, xmlOutput, false);

		EXIFactory ef2 = DefaultEXIFactory.newInstance();
		ef2.setFidelityOptions(FidelityOptions.createAll());
		this._test(ef2, xmlInput, exiOutput, xmlOutput, true);
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

	protected void _test(EXIFactory exiFactory, String xmlInput,
			String exiOutput, String xmlOutput, boolean xmlEqual)
			throws AssertionFailedError, Exception {

		
		for(int i=0; i<2; i++) {
			TestXmlPullEncoder tse = new TestXmlPullEncoder(exiFactory);
			if(i == 0) {
				 tse = new TestXmlPullEncoder(exiFactory);
			} else {
				 tse = new TestXmlPullEncoder(exiFactory, true);
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
			TestXmlPullDecoder tsd = new TestXmlPullDecoder(exiFactory);
			OutputStream xmlOut = new FileOutputStream(xmlOutput);
			tsd.decodeTo(exiIn, xmlOut);
			xmlOut.close();

			// check equality
			// @SuppressWarnings("unused")
			// InputStream control = new FileInputStream(xmlInput);
			InputStream testXML = new FileInputStream(xmlOutput);
			this.checkXMLValidity(exiFactory, testXML);
			if (xmlEqual) {
				// this.checkXMLEquality(exiFactory, control, testXML);
			}
		}
		


	}

	public static void main(String[] args) throws Exception {

		XmlPullCoderTestCase st = new XmlPullCoderTestCase("XmlPull");
		// st.testNotebook();
		st.testXsiType();

	}

}
