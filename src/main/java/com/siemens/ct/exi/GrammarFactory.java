/*
 * Copyright (c) 2007-2015 Siemens AG
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
 * @version 0.9.6-SNAPSHOT
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
	 * @throws EXIException EXI exception
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
	 * @throws EXIException EXI exception
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
	 * @throws EXIException EXI exception
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
	 * @throws EXIException EXI exception
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
	 * @throws EXIException EXI exception
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
