/*
 * Copyright (C) 2007-2014 Siemens AG
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

import org.apache.xerces.xni.parser.XMLEntityResolver;

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
 * @version 0.9.4-SNAPSHOT
 */

public class GrammarFactory {

	protected XSDGrammarsBuilder grammarBuilder;

	protected GrammarFactory() {
		grammarBuilder = XSDGrammarsBuilder.newInstance();
	}

	/**
	 * Create grammar factory instance.
	 * 
	 * @return GrammarFactory
	 */
	public static GrammarFactory newInstance() {
		return new GrammarFactory();
	}

	/**
	 * Schema information is generated for processing the EXI body.
	 * 
	 * @param xsdLocation file location
	 * @return schema-informed EXI grammars
	 * @throws EXIException
	 */
	public Grammars createGrammars(String xsdLocation) throws EXIException {
		return this.createGrammars(xsdLocation, null);
	}

	/**
	 * Schema information is generated for processing the EXI body.
	 * 
	 * @param xsdLocation file location
	 * @param entityResolver application can register XSD resolver
	 * @return schema-informed EXI grammars
	 * @throws EXIException
	 */
	public Grammars createGrammars(String xsdLocation,
			XMLEntityResolver entityResolver) throws EXIException {
		if (xsdLocation == null || xsdLocation.equals("")) {
			throw new EXIException("SchemaLocation not specified correctly!");
		} else {
			// System.out.println("Grammar for: " + xsdLocation);
			grammarBuilder.loadGrammars(xsdLocation, entityResolver);
			SchemaInformedGrammars g = grammarBuilder.toGrammars();
			g.setSchemaId(xsdLocation);
			return g;
		}
	}

	/**
	 * Schema information is generated for processing the EXI body.
	 * 
	 * @param is input stream
	 * @return schema-informed EXI grammars
	 * @throws EXIException
	 */
	public Grammars createGrammars(InputStream is) throws EXIException {
		return this.createGrammars(is, null);
	}

	/**
	 * Schema information is generated for processing the EXI body.
	 * 
	 * @param is input stream
	 * @param entityResolver application can register XSD resolver
	 * @return schema-informed EXI grammars
	 * @throws EXIException
	 */
	public Grammars createGrammars(InputStream is,
			XMLEntityResolver entityResolver) throws EXIException {
		grammarBuilder.loadGrammars(is, entityResolver);
		SchemaInformedGrammars g = grammarBuilder.toGrammars();
		g.setSchemaId("No-Schema-ID-Set");
		return g;
	}

	/**
	 * No user defined schema information is generated for processing the EXI body;
	 * however, the built-in XML schema types are available for use in the EXI
	 * body.
	 * 
	 * @return built-in XSD EXI grammars
	 * @throws EXIException
	 */
	public Grammars createXSDTypesOnlyGrammars() throws EXIException {
		grammarBuilder.loadXSDTypesOnlyGrammars();
		SchemaInformedGrammars g = grammarBuilder.toGrammars();
		g.setBuiltInXMLSchemaTypesOnly(true); // builtInXMLSchemaTypesOnly
		return g;
	}

	/**
	 * No schema information is used for processing the EXI body (i.e. a
	 * schema-less EXI stream)
	 * 
	 * @return schema-less EXI grammars
	 */
	public Grammars createSchemaLessGrammars() {
		return new SchemaLessGrammars();
	}
}
