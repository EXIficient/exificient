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

package com.siemens.ct.exi;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.grammar.Grammar;
import com.siemens.ct.exi.helpers.DefaultEXIFactory;

public abstract class AbstractTestCoder {
	protected static GrammarFactory grammarFactory;

	static {
		grammarFactory = GrammarFactory.newInstance();
	}

	public AbstractTestCoder() {
	}

	protected EXIFactory getQuickTestEXIactory() throws Exception {
		if (QuickTestConfiguration.USE_SCHEMA) {
			return getFactorySchema();
		} else {
			return getFactoryNoSchema();
		}
	}

	protected EXIFactory getFactory() {
		return DefaultEXIFactory.newInstance();
	}

	protected EXIFactory getFactoryNoSchema() {
		EXIFactory ef = getFactory();
		ef.setCodingMode(QuickTestConfiguration.CODING_MODE);
		ef.setFidelityOptions(QuickTestConfiguration.fidelityOptions);
		ef.setFragment(QuickTestConfiguration.FRAGMENTS);
		ef.setSelfContainedElements(QuickTestConfiguration.selfContainedElements);

		return ef;
	}

	protected EXIFactory getFactorySchema() throws EXIException {
		EXIFactory ef = getFactoryNoSchema();
		ef.setGrammar(getGrammar(QuickTestConfiguration.getXsdLocation()));

		return ef;
	}

	public static Grammar getGrammar(String xsdLocation) throws EXIException {
		return grammarFactory.createGrammar(xsdLocation);
	}

	protected static OutputStream getOutputStream(String exiLocation)
			throws FileNotFoundException {
		File fileEXI = new File(exiLocation);

		File path = fileEXI.getParentFile();
		if (!path.exists()) {
			path.mkdirs();
		}

		return new BufferedOutputStream(new FileOutputStream(fileEXI));
	}
}
