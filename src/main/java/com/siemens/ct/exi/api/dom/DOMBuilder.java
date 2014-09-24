/*
 * Copyright (C) 2007-2014 Siemens AG
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

package com.siemens.ct.exi.api.dom;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import com.siemens.ct.exi.Constants;
import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.exceptions.EXIException;

/**
 * Builds a <code>Document</code> for a given EXI stream.
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9.3
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

			XMLReader reader = factory.createEXIReader();
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

			XMLReader reader = factory.createEXIReader();
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
