/*
 * Copyright (c) 2007-2016 Siemens AG
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

package com.siemens.ct.exi.main.api.sax;

import javax.xml.transform.sax.SAXSource;

import org.xml.sax.XMLReader;

import com.siemens.ct.exi.core.EXIFactory;
import com.siemens.ct.exi.core.exceptions.EXIException;
import com.siemens.ct.exi.core.helpers.DefaultEXIFactory;

/**
 * Acts as an holder for SAX-style Source.
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9.7-SNAPSHOT
 */

public class EXISource extends SAXSource {
	
	SAXFactory saxFactory;

	public EXISource() throws EXIException {
		// use default exi-factory
		this(DefaultEXIFactory.newInstance());
	}

	public EXISource(EXIFactory exiFactory) throws EXIException {
		this.saxFactory = new SAXFactory(exiFactory);

		init();
	}

	protected void init() throws EXIException {
		// create sax decoder
		XMLReader xmlReader = saxFactory.createEXIReader();

		// set internal state
		this.setXMLReader(xmlReader);
	}

}
