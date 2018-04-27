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

package com.siemens.ct.exi.main.api.dom;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import com.siemens.ct.exi.core.Constants;
import com.siemens.ct.exi.core.EXIFactory;
import com.siemens.ct.exi.core.exceptions.EXIException;
import com.siemens.ct.exi.main.api.sax.SAXFactory;

/**
 * Builds a <code>Document</code> for a given EXI stream.
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 1.0.1-SNAPSHOT
 */

public class DOMBuilder {
	protected EXIFactory factory;

	protected DOMImplementation domImplementation;

	public DOMBuilder(EXIFactory factory) throws ParserConfigurationException {
		this.factory = factory;

		// setup document builder etc.
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		dbFactory.setNamespaceAware(true);
		DocumentBuilder builder = dbFactory.newDocumentBuilder();
		domImplementation = builder.getDOMImplementation();
	}

	public DocumentFragment parseFragment(InputStream is) throws EXIException {
		try {
			// // create empty document fragment
			// Document document = domImplementation.createDocument(null, null,
			// null);
			// DocumentFragment docFragment = document.createDocumentFragment();

			// create SAX to DOM Handlers
			SaxToDomHandler s2dHandler = new SaxToDomHandler(domImplementation,
					true);

			XMLReader reader = new SAXFactory(factory).createEXIReader();
			reader.setFeature("http://xml.org/sax/features/namespace-prefixes",
					true);
			reader.setContentHandler(s2dHandler);

			reader.parse(new InputSource(is));
			// return docFragment;
			return s2dHandler.getDocumentFragment();
		} catch (Exception e) {
			throw new EXIException(e);
		}
	}

	public Document parse(InputStream is) throws EXIException {
		return parse(is, false);
	}

	public Document parse(InputStream is, boolean exiBodyOnly)
			throws EXIException {
		try {
			// create SAX to DOM Handlers
			SaxToDomHandler s2dHandler = new SaxToDomHandler(domImplementation,
					false);

			XMLReader reader = new SAXFactory(factory).createEXIReader();
			// EXI Features
			reader.setFeature(Constants.W3C_EXI_FEATURE_BODY_ONLY, exiBodyOnly);
			// SAX Features
			reader.setFeature("http://xml.org/sax/features/namespace-prefixes",
					true);
			reader.setProperty("http://xml.org/sax/properties/lexical-handler",
					s2dHandler);
			reader.setProperty(
					"http://xml.org/sax/properties/declaration-handler",
					s2dHandler);
			reader.setContentHandler(s2dHandler);
			reader.setDTDHandler(s2dHandler);

			reader.parse(new InputSource(is));

			// return document;
			return s2dHandler.getDocument();
		} catch (Exception e) {
			throw new EXIException(e);
		}
	}
}
