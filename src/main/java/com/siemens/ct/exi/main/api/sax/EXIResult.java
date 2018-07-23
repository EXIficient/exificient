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

package com.siemens.ct.exi.main.api.sax;

import java.io.IOException;
import java.io.OutputStream;

import javax.xml.transform.sax.SAXResult;

import com.siemens.ct.exi.core.EXIFactory;
import com.siemens.ct.exi.core.exceptions.EXIException;
import com.siemens.ct.exi.core.helpers.DefaultEXIFactory;

/**
 * Acts as an holder for a transformation Result.
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Richard.Kuntschke@siemens.com
 * 
 */

public class EXIResult extends SAXResult {

	protected SAXEncoder handler;

	public EXIResult() throws IOException, EXIException {
		// use default exi-factory
		this(DefaultEXIFactory.newInstance());
	}

	public EXIResult(EXIFactory exiFactory) throws EXIException {
		// DefaultHandler2 handler = exiFactory.createEXIWriter(os);
		handler = new SAXFactory(exiFactory).createEXIWriter();
		// set internal states
		setHandler(handler);
		setLexicalHandler(handler);
	}

	public void setOutputStream(OutputStream os) throws EXIException,
			IOException {
		handler.setOutputStream(os);
	}

}
