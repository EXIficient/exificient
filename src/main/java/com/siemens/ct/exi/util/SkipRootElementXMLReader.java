/*
 * Copyright (C) 2007-2012 Siemens AG
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

package com.siemens.ct.exi.util;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLFilterImpl;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9.2
 */

public class SkipRootElementXMLReader extends XMLFilterImpl {
	private int openElement = 0;

	public SkipRootElementXMLReader(XMLReader parent) {
		super(parent);

		this.setContentHandler(parent.getContentHandler());
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes atts) throws SAXException {
		if (openElement > 0) {
			super.startElement(uri, localName, qName, atts);
		}

		openElement++;
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		openElement--;

		if (openElement > 0) {
			super.endElement(uri, localName, qName);
		}
	}

}
