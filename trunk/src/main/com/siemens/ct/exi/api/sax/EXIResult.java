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
 * @version 0.2.20080718
 */

public class EXIResult extends SAXResult {
	protected OutputStream os;

	protected EXIFactory exiFactory;

	public EXIResult(OutputStream os) throws IOException {
		// use default exi-factory
		this(os, DefaultEXIFactory.newInstance());
	}

	public EXIResult(OutputStream os, EXIFactory exiFactory) throws IOException {
		this.os = os;
		this.exiFactory = exiFactory;

		init();
	}

	protected void init() throws IOException {
		// create new sax encoder
		EXIWriter saxEncoder = exiFactory.createEXIWriter();
		try {
			saxEncoder.setOutput(os, exiFactory.isEXIBodyOnly());
		} catch (EXIException e) {
			throw new IOException(e.getMessage());
			// TODO Java 1.5 does not support Throwable as parameter
			// throw new IOException(e);
		}

		// set internal states
		setHandler(saxEncoder);
		setLexicalHandler(saxEncoder);
	}

	public OutputStream getOutputStream() {
		return os;
	}

	public void setOutputStream(OutputStream os) throws IOException {
		this.os = os;

		init();
	}

}
