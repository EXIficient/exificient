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

package com.siemens.ct.exi.api.sax;

import java.io.IOException;
import java.io.OutputStream;

import javax.xml.transform.sax.SAXResult;

import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.helpers.DefaultEXIFactory;

/**
 * Acts as an holder for a transformation Result.
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9.1
 */

public class EXIResult extends SAXResult {

	protected SAXEncoder handler;
	
	public EXIResult() throws IOException, EXIException {
		// use default exi-factory
		this(DefaultEXIFactory.newInstance());
	}

	public EXIResult(EXIFactory exiFactory) throws EXIException {
		// DefaultHandler2 handler = exiFactory.createEXIWriter(os);
		handler = exiFactory.createEXIWriter();
		// set internal states
		setHandler(handler);
		setLexicalHandler(handler);
	}
	
	public void setOutputStream(OutputStream os) throws EXIException, IOException {
		handler.setOutputStream(os);
	}

}
