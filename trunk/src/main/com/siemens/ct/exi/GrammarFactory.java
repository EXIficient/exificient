/*
 * Copyright (C) 2007, 2008 Siemens AG
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

import java.io.InputStream;

import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.grammar.Grammar;
import com.siemens.ct.exi.grammar.SchemaLessGrammar;
import com.siemens.ct.exi.grammar.XSDGrammarBuilder;

/**
 * Class allows creating EXI <code>Grammar</code>s from different sources.
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.2.20080718
 */

public class GrammarFactory {
	private static XSDGrammarBuilder grammarBuilder = XSDGrammarBuilder
			.newInstance();

	protected GrammarFactory() {
	}

	public static GrammarFactory newInstance() {
		return new GrammarFactory();
	}

	public Grammar createGrammar(String xsdLocation) throws EXIException {
		if (xsdLocation == null || xsdLocation.equals("")) {
			throw new EXIException("SchemaLocation not specified correctly!");
		} else {
			return grammarBuilder.build(xsdLocation);
		}
	}

	public Grammar createGrammar(InputStream is) throws EXIException {
		return grammarBuilder.build(is);
	}

	public Grammar createSchemaLessGrammar() {
		return new SchemaLessGrammar();
	}
}
