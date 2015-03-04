/*
 * Copyright (C) 2007-2015 Siemens AG
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

package com.siemens.ct.exi.api.sax;

import javax.xml.transform.sax.SAXSource;

import org.xml.sax.XMLReader;

import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.helpers.DefaultEXIFactory;

/**
 * Acts as an holder for SAX-style Source.
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9.4-SNAPSHOT
 */

public class EXISource extends SAXSource {
	EXIFactory exiFactory;

	public EXISource() throws EXIException {
		// use default exi-factory
		this(DefaultEXIFactory.newInstance());
	}

	public EXISource(EXIFactory exiFactory) throws EXIException {
		this.exiFactory = exiFactory;

		init();
	}

	protected void init() throws EXIException {
		// create sax decoder
		XMLReader xmlReader = exiFactory.createEXIReader();

		// set internal state
		this.setXMLReader(xmlReader);
	}

}
