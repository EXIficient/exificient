/*
 * Copyright (C) 2007-2010 Siemens AG
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

import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.exceptions.EXIException;

/**
 * TODO Description
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.3.20090324
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
			// create empty document fragment
			Document document = domImplementation.createDocument(null, null,
					null);
			DocumentFragment docFragment = document.createDocumentFragment();

			// create SAX to DOM Handlers
			SaxToDomHandler s2dHandler = new SaxToDomHandler(document,
					docFragment);

			XMLReader reader = factory.createEXIReader();
			reader.setContentHandler(s2dHandler);

			reader.parse(new InputSource(is));
			return docFragment;
		} catch (Exception e) {
			throw new EXIException(e);
		}
	}

	public Document parse(InputStream is) throws EXIException {
		try {
			// create empty document
			Document document = domImplementation.createDocument(null, null,
					null);

			// create SAX to DOM Handlers
			SaxToDomHandler s2dHandler = new SaxToDomHandler(document);

			XMLReader reader = factory.createEXIReader();
			reader.setContentHandler(s2dHandler);
			reader.setDTDHandler(s2dHandler);

			reader.parse(new InputSource(is));
			return document;
		} catch (Exception e) {
			throw new EXIException(e);
		}
	}
}
