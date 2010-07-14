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
 * @version 0.5
 */

public interface Grammar {
	
	public boolean isSchemaInformed();

	public Rule getBuiltInDocumentGrammar();

	public Rule getBuiltInFragmentGrammar();

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