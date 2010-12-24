/*
 * Copyright (C) 2007-2010 Siemens AG
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

package com.siemens.ct.exi.grammar;

import java.io.Serializable;
import java.util.List;

import javax.xml.namespace.QName;

import com.siemens.ct.exi.grammar.event.Attribute;
import com.siemens.ct.exi.grammar.event.StartElement;
import com.siemens.ct.exi.grammar.rule.Rule;
import com.siemens.ct.exi.grammar.rule.SchemaInformedRule;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.6
 */

public interface Grammar extends Serializable {

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
	 * The built-in XML schema types are available.
	 * 
	 * <p>
	 * Note: the grammar is schema-informed also (see isSchemaInformed())
	 * </p>
	 * 
	 * @return boolean value indicating whether the grammar uses built-in types only
	 */
	public boolean isBuiltInXMLSchemaTypesOnly();

	public Rule getDocumentGrammar();

	public Rule getFragmentGrammar();

	public GrammarURIEntry[] getGrammarEntries();

	public StartElement getGlobalElement(QName qname);

	public Attribute getGlobalAttribute(QName qname);

	public SchemaInformedRule getTypeGrammar(QName qname);

	/**
	 * Returns (direct) simple types in type hierarchy
	 * 
	 * @param type
	 * @return list of named sub-types or null
	 */
	public List<QName> getSimpleTypeSubtypes(QName type);
}