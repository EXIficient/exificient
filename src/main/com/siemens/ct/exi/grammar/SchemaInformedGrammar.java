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
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import com.siemens.ct.exi.Constants;
import com.siemens.ct.exi.grammar.event.Attribute;
import com.siemens.ct.exi.grammar.event.StartElement;
import com.siemens.ct.exi.grammar.rule.Document;
import com.siemens.ct.exi.grammar.rule.Fragment;
import com.siemens.ct.exi.grammar.rule.Rule;
import com.siemens.ct.exi.grammar.rule.SchemaInformedFirstStartTagRule;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.6
 */

public class SchemaInformedGrammar extends AbstractGrammar implements Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 7647530843802602241L;
	
	protected Map<QName, StartElement> globalElements;

	protected Map<QName, Attribute> globalAttributes;
	
	protected Map<QName, SchemaInformedFirstStartTagRule> grammarTypes;
	
	// protected Collection<StartElement> elements;
	
	/* (direct) simple sub-types for given qname */
	protected Map<QName, List<QName>> subtypes;
	
	protected boolean builtInXMLSchemaTypesOnly = false;
	
	protected String schemaId;

	
	// public @version 0.6(GrammarURIEntry[] grammarEntries, Document document, Fragment fragment, Collection<StartElement> elements) {
	public SchemaInformedGrammar(GrammarURIEntry[] grammarEntries, Document document, Fragment fragment) {
		super(true);
		
		// uri & local-name & prefix entries
		this.grammarEntries = grammarEntries;
		
		// set document & fragment grammar
		documentGrammar = document;
		fragmentGrammar= fragment;

		// this.elements = elements;
	}
	
	
	public void setBuiltInXMLSchemaTypesOnly(boolean builtInXMLSchemaTypesOnly) {
		this.builtInXMLSchemaTypesOnly = builtInXMLSchemaTypesOnly;
		this.schemaId = Constants.EMPTY_STRING;
	}
	
	public final String getSchemaId() {
		return schemaId;
	}
	
	public void setSchemaId(String schemaId) {
		this.schemaId = schemaId;
	}
		
	public boolean isBuiltInXMLSchemaTypesOnly() {
		return builtInXMLSchemaTypesOnly;
	}

//	public Collection<StartElement> getElements() {
//		return this.elements;
//	}
	
	public void setGlobalElements(Map<QName, StartElement> globalElements) {
		assert (globalElements != null);
		this.globalElements = globalElements;
	}
	
	public StartElement getGlobalElement(QName qname) {
		return globalElements.get(qname);
	}
	
	public void setGlobalAttributes(Map<QName, Attribute> globalAttributes) {
		assert (globalAttributes != null);
		this.globalAttributes = globalAttributes;
	}

	public Attribute getGlobalAttribute(QName qname) {
		return globalAttributes.get(qname);
	}
	
	public void setTypeGrammars(Map<QName, SchemaInformedFirstStartTagRule> grammarTypes) {
		assert (grammarTypes != null);
		this.grammarTypes = grammarTypes;
	}

	public SchemaInformedFirstStartTagRule getTypeGrammar(QName qname) {
		return grammarTypes.get(qname);
	}

	public Set<QName> getTypeGrammars() {
		return grammarTypes.keySet();
	}
	
	public void setSimpleTypeSubtypes(Map<QName, List<QName>> subtypes) {
		this.subtypes = subtypes;
	}
	
	public List<QName> getSimpleTypeSubtypes(QName type) {
		return subtypes.get(type);
	}

	public Rule getFragmentGrammar() {
		return fragmentGrammar;
	}

}
