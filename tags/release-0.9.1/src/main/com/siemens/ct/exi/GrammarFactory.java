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

package com.siemens.ct.exi;

import java.io.InputStream;

import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.grammars.Grammars;
import com.siemens.ct.exi.grammars.SchemaInformedGrammars;
import com.siemens.ct.exi.grammars.SchemaLessGrammars;
import com.siemens.ct.exi.grammars.XSDGrammarsBuilder;

/**
 * Class allows creating EXI <code>Grammars</code>s from different sources.
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9.1
 */

public class GrammarFactory {

	protected XSDGrammarsBuilder grammarBuilder;

	protected GrammarFactory() {
		grammarBuilder = XSDGrammarsBuilder.newInstance();
	}

	public static GrammarFactory newInstance() {
		return new GrammarFactory();
	}

	/* schema file as location */
	public Grammars createGrammars(String xsdLocation) throws EXIException {
		if (xsdLocation == null || xsdLocation.equals("")) {
			throw new EXIException("SchemaLocation not specified correctly!");
		} else {
			// System.out.println("Grammar for: " + xsdLocation);
			grammarBuilder.loadGrammars(xsdLocation);
			SchemaInformedGrammars g = grammarBuilder.toGrammars();
			g.setSchemaId(xsdLocation);
			return g;
		}
	}

	/* schema file as input stream */
	public Grammars createGrammars(InputStream is) throws EXIException {
		grammarBuilder.loadGrammars(is);
		SchemaInformedGrammars g = grammarBuilder.toGrammars();
		g.setSchemaId("No-Schema-ID-Set");
		return g;
	}

	/* built-in XSD types only are available */
	public Grammars createXSDTypesOnlyGrammars() throws EXIException {
		grammarBuilder.loadXSDTypesOnlyGrammars();
		SchemaInformedGrammars g = grammarBuilder.toGrammars();
		g.setBuiltInXMLSchemaTypesOnly(true); // builtInXMLSchemaTypesOnly
		return g;
	}

	/* no schema information at all */
	public Grammars createSchemaLessGrammars() {
		return new SchemaLessGrammars();
	}
}
