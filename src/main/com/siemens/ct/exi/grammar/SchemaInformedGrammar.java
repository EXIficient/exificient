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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import com.siemens.ct.exi.Constants;
import com.siemens.ct.exi.grammar.event.Attribute;
import com.siemens.ct.exi.grammar.event.StartDocument;
import com.siemens.ct.exi.grammar.event.StartElement;
import com.siemens.ct.exi.grammar.rule.DocEnd;
import com.siemens.ct.exi.grammar.rule.Document;
import com.siemens.ct.exi.grammar.rule.Fragment;
import com.siemens.ct.exi.grammar.rule.Rule;
import com.siemens.ct.exi.grammar.rule.SchemaInformedDocContent;
import com.siemens.ct.exi.grammar.rule.SchemaInformedFragmentContent;
import com.siemens.ct.exi.grammar.rule.SchemaInformedRule;

/**
 * 
 * @author Daniel.Peintner.EXT@siemens.com
 * @author Joerg.Heuer@siemens.com
 * 
 * @version 0.5
 */

public class SchemaInformedGrammar extends AbstractGrammar {

	protected List<StartElement> sortedGlobalElements; // subset of entire set of
													// element

	protected Map<QName, StartElement> globalElements;

	protected Map<QName, Attribute> globalAttributes;
	
	protected Map<QName, SchemaInformedRule> grammarTypes;
	
	/* (direct) simple sub-types for given qname */
	protected Map<QName, List<QName>> subtypes;

	protected SchemaInformedRule builtInFragmentGrammar;
	
	protected boolean builtInXMLSchemaTypesOnly = false;
	
	protected String schemaId;

	protected SchemaInformedGrammar(GrammarURIEntry[] grammarEntries,
			List<StartElement> fragmentElements, List<StartElement> sortedGlobalElements) {
		super(true);
		
		// uri & local-name & prefix entries
		this.grammarEntries = grammarEntries;
		
		//	elements
		this.sortedGlobalElements = sortedGlobalElements;

//		//	initialze grammar entries
//		boolean hasEmptyURIEntries = containsEmptyURI(additionalSchemaEntries);
//		int uriSize = hasEmptyURIEntries ? 4+additionalSchemaEntries.length-1 : 4+additionalSchemaEntries.length;
//		grammarEntries = new GrammarURIEntry[uriSize];
//
//		// "", empty string
//		if (hasEmptyURIEntries) {
//			assert(additionalSchemaEntries[0].uri.equals(Constants.EMPTY_STRING));
//			grammarEntries[0] = additionalSchemaEntries[0];
//		} else {
//			grammarEntries[0] = getURIEntryForEmpty();
//		}
//
//		// "http://www.w3.org/XML/1998/namespace"
//		grammarEntries[1] = getURIEntryForXML();
//
//		// "http://www.w3.org/2001/XMLSchema-instance", xsi
//		grammarEntries[2] = getURIEntryForXSI();
//		
//		// "http://www.w3.org/2001/XMLSchema", xsd
//		grammarEntries[3] = getURIEntryForXSD();
//		
//		//	*additional* URIs
//		int diff = hasEmptyURIEntries ? 3 : 4;
//		
//		for(int index=4; (index-diff)<additionalSchemaEntries.length; index++) {
//			grammarEntries[index] = additionalSchemaEntries[index-diff];
//		}
		
		// init document & fragment grammar
		initDocumentGrammar();
		initFragmentGrammar(fragmentElements);

		// initialize map of global element
		this.globalElements = new HashMap<QName, StartElement>();
		for (StartElement globalElement : sortedGlobalElements) {
			globalElements.put(globalElement.getQName(), globalElement);
		}
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
	
	protected static boolean containsEmptyURI(GrammarURIEntry[] entries) {
		for(int i=0;i<entries.length; i++) {
			if(entries[i].uri.equals(Constants.EMPTY_STRING)) {
				return true;
			}
		}
		return false;
	}
	
	public StartElement getGlobalElement(QName qname) {
		//	TODO build hash-map
		for (StartElement globalElement : sortedGlobalElements) {
			if(globalElement.getQName().equals(qname)) {
				return globalElement;
			}
		}

		return null;
	}
	
	protected void setGlobalAttributes(Map<QName, Attribute> globalAttributes) {
		assert (globalAttributes != null);
		this.globalAttributes = globalAttributes;
	}

	public Attribute getGlobalAttribute(QName qname) {
		return globalAttributes.get(qname);
	}
	
	protected void setTypeGrammars(Map<QName, SchemaInformedRule> grammarTypes) {
		assert (grammarTypes != null);
		this.grammarTypes = grammarTypes;
	}

	public SchemaInformedRule getTypeGrammar(QName qname) {
		return grammarTypes.get(qname);
	}
	
	
	protected void setSimpleTypeSubtypes(Map<QName, List<QName>> subtypes) {
		this.subtypes = subtypes;
	}
	
	public List<QName> getSimpleTypeSubtypes(QName type) {
		return subtypes.get(type);
	}

	protected void initDocumentGrammar() {
		// Note: Schema-informed document grammar does NOT change over time!
		/*
		 * Global elements declared in the schema. G 0, G 1, ... G n-1 represent
		 * all the qnames of global elements sorted lexicographically, first by
		 * localName, then by uri.
		 * http://www.w3.org/TR/exi/#informedDocGrammars
		 */
		// DocEnd rule
		Rule builtInDocEndGrammar = new DocEnd("DocEnd");
		// DocContent rule
		SchemaInformedRule builtInDocContentGrammar = new SchemaInformedDocContent(
				builtInDocEndGrammar, "DocContent");
		// DocContent rule & add global elements (sorted)
		for (StartElement globalElement : sortedGlobalElements) {
			builtInDocContentGrammar.addRule(globalElement,
					builtInDocEndGrammar);
		}
		// Document rule
		builtInDocumentGrammar = new Document(builtInDocContentGrammar,
				"Document");
	}

	protected void initFragmentGrammar(List<StartElement> namedElements) {
		// Note: Schema-informed fragment grammar does NOT change over time!
		/*
		 * FragmentContent grammar represents the number of unique element
		 * qnames declared in the schema sorted lexicographically, first by
		 * localName, then by uri.
		 * http://www.w3.org/TR/exi/#informedElementFragGrammar
		 */
		/*
		 * Fragment Content
		 */
		SchemaInformedRule builtInFragmentContentGrammar = new SchemaInformedFragmentContent(
				"FragmentContent");
		for (StartElement namedElement : namedElements) {
			builtInFragmentContentGrammar.addRule(namedElement,
					builtInFragmentContentGrammar);
		}

		/*
		 * Fragment
		 */
		builtInFragmentGrammar = new Fragment(builtInFragmentContentGrammar,
				"Fragment");
		builtInFragmentGrammar.addRule(new StartDocument(),
				builtInFragmentContentGrammar);
	}

	public Rule getBuiltInFragmentGrammar() {
		return builtInFragmentGrammar;
	}

}
