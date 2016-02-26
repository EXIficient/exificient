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

package com.siemens.ct.exi.grammars;

import com.siemens.ct.exi.context.GrammarContext;
import com.siemens.ct.exi.exceptions.UnsupportedOption;
import com.siemens.ct.exi.grammars.grammar.Grammar;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9.6-SNAPSHOT
 */

public interface Grammars {

	/**
	 * Schema information is used to create grammar.
	 * 
	 * @return boolean value indicating whether the grammar is schema-informed
	 */
	public boolean isSchemaInformed();

	/**
	 * The schemaID option may be used to identify the schema information used
	 * for processing the EXI body. When the "schemaID" is null no schema
	 * information is used for processing the EXI body. When the value of the
	 * "schemaID" is an empty string, no user defined schema information is used
	 * for processing the EXI body; however, the built-in XML schema types are
	 * available for use in the EXI body.
	 * 
	 * <p>
	 * An example schemaID scheme is the use of URI that is apt for globally
	 * identifying schema resources on the Web.
	 * </p>
	 * 
	 * @return schema identifier
	 */
	public String getSchemaId();

	/**
	 * Sets the schemaId passed to EXI decoder.
	 * 
	 * <p>
	 * Note: Schema-less or XML-types grammars have a schema id associated with
	 * them (null and empty string).
	 * </p>
	 * 
	 * @param schemaId schemaId
	 * @throws UnsupportedOption
	 *             if schemaId does not respect EXI restrictions (e.g.,
	 *             schema-less grammars: schemaId == null)
	 */
	public void setSchemaId(String schemaId) throws UnsupportedOption;

	/**
	 * The built-in XML schema types are available.
	 * 
	 * <p>
	 * Note: the grammar is schema-informed also (see isSchemaInformed())
	 * </p>
	 * 
	 * @return boolean value indicating whether the grammar uses built-in types
	 *         only
	 */
	public boolean isBuiltInXMLSchemaTypesOnly();

	public Grammar getDocumentGrammar();

	public Grammar getFragmentGrammar();

//	public Grammar getUrTypeGrammar();

	public GrammarContext getGrammarContext();
}