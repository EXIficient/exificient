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

package com.siemens.ct.exi.grammars;

import java.io.Serializable;

import com.siemens.ct.exi.context.GrammarContext;
import com.siemens.ct.exi.exceptions.UnsupportedOption;
import com.siemens.ct.exi.grammars.grammar.Grammar;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.9
 */

public interface Grammars extends Serializable {

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
	 * @param schemaId
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

	public Grammar getUrTypeGrammar();

	public GrammarContext getGrammarContext();
}